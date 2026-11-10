package com.studyquiz.quiz;
import org.apache.commons.csv.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Component
public class CsvQuestionImporter implements ApplicationRunner {
    private final boolean enabled;
    private final Resource file;
    private final SubjectRepository subjects;
    private final QuestionRepository questions;
    private final TransactionTemplate transactions;
    public CsvQuestionImporter(@Value("${app.import.enabled}") boolean enabled,@Value("${app.import.location}") Resource file,
                               SubjectRepository subjects,QuestionRepository questions,TransactionTemplate transactions){
        this.enabled=enabled;this.file=file;this.subjects=subjects;this.questions=questions;this.transactions=transactions;
    }
    @Override public void run(ApplicationArguments args) throws IOException {
        if(!enabled)return;
        try(Reader reader=new InputStreamReader(file.getInputStream(),StandardCharsets.UTF_8);
            CSVParser parser=CSVFormat.DEFAULT.builder().setDelimiter(';').setHeader().setSkipHeaderRecord(true).setIgnoreEmptyLines(true).get().parse(reader)){
            int row=1;
            for(CSVRecord record:parser){row++;final int line=row;
                transactions.executeWithoutResult(status->importRecord(record,line));
            }
        }
    }
    private void importRecord(CSVRecord r,int line){
        try {
            String subjectName=value(r,"Matiere"),prompt=value(r,"Question"),explanation=value(r,"Explication");
            String correct=value(r,"Reponse_Correcte");
            if(!Set.of("Option_A","Option_B","Option_C","Option_D").contains(correct)) throw new IllegalArgumentException("Invalid correct option");
            Subject subject=subjects.findByName(subjectName).orElseGet(()->subjects.save(new Subject(subjectName)));
            if(questions.existsBySubjectIdAndPrompt(subject.id,prompt))return;
            Question q=new Question(subject,prompt,explanation);
            for(String code:List.of("A","B","C","D"))q.addOption(code,value(r,"Option_"+code),correct.equals("Option_"+code));
            questions.save(q);
        }catch(RuntimeException e){throw new IllegalArgumentException("Invalid CSV row " + line + ": " + e.getMessage(),e);}
    }
    private String value(CSVRecord r,String key){
        String v=r.get(key).trim();
        if(v.isBlank())throw new IllegalArgumentException("Empty column: "+key);
        return v;
    }
}
