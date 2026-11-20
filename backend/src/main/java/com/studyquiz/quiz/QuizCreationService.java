package com.studyquiz.quiz;
import com.studyquiz.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static com.studyquiz.quiz.QuizCreationDtos.*;
@Service
public class QuizCreationService {
    private static final List<String> CODES=List.of("A","B","C","D");
    private final SubjectRepository subjects;
    private final FiliereRepository filieres;
    private final QuestionRepository questions;
    public QuizCreationService(SubjectRepository subjects,FiliereRepository filieres,QuestionRepository questions){
        this.subjects=subjects;this.filieres=filieres;this.questions=questions;
    }
    @Transactional
    public CreatedQuizView create(CreateQuizRequest request,User author){
        Subject subject=resolveSubject(request);
        Difficulty difficulty=request.difficulty()==null?Difficulty.bebe:request.difficulty();
        Set<String> seen=new HashSet<>();
        for(QuestionInput input:request.questions()){
            String prompt=input.prompt().trim();
            if(!seen.add(prompt)||questions.existsBySubjectIdAndPrompt(subject.id,prompt))
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Question already exists: "+prompt);
            Question q=new Question(subject,difficulty,prompt,input.explanation().trim(),author);
            for(int i=0;i<CODES.size();i++) q.addOption(CODES.get(i),input.options().get(i).trim(),CODES.get(i).equals(input.correctCode()));
            questions.save(q);
        }
        return new CreatedQuizView(subject.id,subject.name,subject.filiere.name,request.questions().size());
    }
    private Subject resolveSubject(CreateQuizRequest request){
        if(request.subjectId()!=null)
            return subjects.findById(request.subjectId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Subject not found"));
        String name=request.subjectName()==null?"":request.subjectName().trim();
        if(name.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Subject required");
        return subjects.findByName(name).orElseGet(()->{
            String filiereName=request.filiere()==null||request.filiere().isBlank()?"Général":request.filiere().trim();
            Filiere filiere=filieres.findByName(filiereName).orElseGet(()->filieres.save(new Filiere(filiereName)));
            return subjects.save(new Subject(filiere,name));
        });
    }
}
