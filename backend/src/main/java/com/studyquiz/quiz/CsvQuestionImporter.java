package com.studyquiz.quiz;
import org.apache.commons.csv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Stream;
/**
 * Importe au démarrage toutes les questions des fichiers CSV du dossier Quiz.
 * <ul>
 *   <li>Filière = premier sous-dossier sous la racine (ex. {@code Quiz/Informatique/Code/x.csv} → « Informatique »).</li>
 *   <li>Difficulté = suffixe du nom de fichier : {@code _d1} → difficile, {@code _f2} → facile, {@code _n1} → normal, {@code _b1} → bébé.</li>
 *   <li>En-tête : {@code Matiere,Question,Option_A..D,Reponse_Correcte,Explication} ; un commentaire entre parenthèses
 *       est toléré ({@code Matiere(Auteur)}). Séparateur {@code ,} ou {@code ;}, détecté par fichier (l'en-tête et les lignes
 *       peuvent même utiliser des séparateurs différents).</li>
 * </ul>
 */
@Component
public class CsvQuestionImporter implements ApplicationRunner {
    private static final Logger log=LoggerFactory.getLogger(CsvQuestionImporter.class);
    /** Filière utilisée pour un CSV placé directement à la racine, sans colonne "Filiere". */
    static final String DEFAULT_FILIERE="Général";
    static final List<String> REQUIRED_COLUMNS=List.of("Matiere","Question","Option_A","Option_B","Option_C","Option_D","Reponse_Correcte","Explication");
    private static final Pattern DIFFICULTY_SUFFIX=Pattern.compile("_([bfnd])\\d*\\.csv$",Pattern.CASE_INSENSITIVE);
    private static final Pattern HEADER_COMMENT=Pattern.compile("\\s*\\(.*\\)\\s*$");
    private final boolean enabled;
    private final Path root;
    private final FiliereRepository filieres;
    private final SubjectRepository subjects;
    private final QuestionRepository questions;
    private final TransactionTemplate transactions;
    public CsvQuestionImporter(@Value("${app.import.enabled}") boolean enabled,@Value("${app.import.location}") String location,
                               FiliereRepository filieres,SubjectRepository subjects,QuestionRepository questions,TransactionTemplate transactions){
        this.enabled=enabled;this.root=Path.of(location).toAbsolutePath().normalize();
        this.filieres=filieres;this.subjects=subjects;this.questions=questions;this.transactions=transactions;
    }
    @Override public void run(ApplicationArguments args) throws IOException {
        if(!enabled)return;
        if(!Files.isDirectory(root)){log.warn("Dossier de quiz introuvable : {} (import ignoré)",root);return;}
        List<Path> files;
        try(Stream<Path> walk=Files.walk(root)){
            files=walk.filter(Files::isRegularFile).filter(p->p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".csv")).sorted().toList();
        }
        int imported=0;
        for(Path file:files)imported+=importFile(file);
        log.info("Import des quiz : {} nouvelle(s) question(s) depuis {} fichier(s) de {}",imported,files.size(),root);
    }
    /** Une ligne CSV déjà découpée, avec les colonnes indexées par leur nom normalisé. */
    record Row(int line,Map<String,String> values) {
        String get(String key){
            String v=values.get(key);
            if(v==null||v.isBlank())throw new IllegalArgumentException("Empty column: "+key);
            return v.trim();
        }
        Optional<String> optional(String key){ return Optional.ofNullable(values.get(key)).map(String::trim).filter(s->!s.isEmpty()); }
    }
    private int importFile(Path file) throws IOException {
        String filiereName=filiereOf(file);
        Difficulty difficulty=difficultyOf(file.getFileName().toString());
        List<Row> rows;
        try{ rows=parse(Files.readString(file,StandardCharsets.UTF_8)); }
        catch(IllegalArgumentException e){ throw new IllegalArgumentException("Invalid CSV "+root.relativize(file)+": "+e.getMessage(),e); }
        int[] count={0};
        for(Row row:rows){
            transactions.executeWithoutResult(status->{
                try{ if(importRow(row,filiereName,difficulty))count[0]++; }
                catch(RuntimeException e){ throw new IllegalArgumentException("Invalid CSV "+root.relativize(file)+" row "+row.line()+": "+e.getMessage(),e); }
            });
        }
        log.debug("{} : {} nouvelle(s) question(s) ({}, {})",root.relativize(file),count[0],filiereName,difficulty);
        return count[0];
    }
    /** Découpe le contenu d'un fichier : BOM retiré, séparateur détecté séparément pour l'en-tête et pour les données. */
    static List<Row> parse(String content) throws IOException {
        if(content.startsWith("﻿"))content=content.substring(1);
        int eol=content.indexOf('\n');
        String headerLine=(eol<0?content:content.substring(0,eol)).strip();
        String body=eol<0?"":content.substring(eol+1);
        if(headerLine.isEmpty())throw new IllegalArgumentException("Missing header");
        List<String> header=new ArrayList<>();
        for(String h:parseRecords(headerLine,headerLine.contains(";")?';':',').get(0))header.add(normalizeHeader(h));
        for(String c:REQUIRED_COLUMNS)if(!header.contains(c))throw new IllegalArgumentException("Missing column: "+c);
        List<CSVRecord> records=null;
        for(char delimiter:new char[]{';',','}){
            List<CSVRecord> candidate=parseRecords(body,delimiter);
            if(candidate.stream().allMatch(r->r.size()==header.size())){records=candidate;break;}
        }
        if(records==null)throw new IllegalArgumentException("Cannot detect delimiter (expected "+header.size()+" columns per row)");
        List<Row> rows=new ArrayList<>();
        for(CSVRecord r:records){
            Map<String,String> values=new HashMap<>();
            for(int i=0;i<header.size();i++)values.put(header.get(i),r.get(i));
            rows.add(new Row(lineOf(body,r)+1,values));
        }
        return rows;
    }
    private static List<CSVRecord> parseRecords(String text,char delimiter) throws IOException {
        try(CSVParser parser=CSVFormat.DEFAULT.builder().setDelimiter(delimiter).setIgnoreEmptyLines(true).setTrim(true).get().parse(new StringReader(text))){
            return parser.getRecords();
        }catch(UncheckedIOException|IllegalStateException e){ return List.of(); }
    }
    /** Numéro de ligne (1 = en-tête) d'un enregistrement, pour les messages d'erreur. */
    private static int lineOf(String body,CSVRecord r){
        int line=1;long pos=r.getCharacterPosition();
        for(int i=0;i<pos&&i<body.length();i++)if(body.charAt(i)=='\n')line++;
        return line;
    }
    /** "Matiere(Manon Landelle)" → "Matiere". */
    static String normalizeHeader(String h){ return HEADER_COMMENT.matcher(h.replace("﻿","")).replaceFirst("").trim(); }
    String filiereOf(Path file){
        Path rel=root.relativize(file);
        return rel.getNameCount()>1?rel.getName(0).toString():DEFAULT_FILIERE;
    }
    static Difficulty difficultyOf(String fileName){
        Matcher m=DIFFICULTY_SUFFIX.matcher(fileName);
        if(!m.find())return Difficulty.bebe;
        return switch(Character.toLowerCase(m.group(1).charAt(0))){
            case 'd'->Difficulty.difficile;
            case 'n'->Difficulty.normal;
            case 'f'->Difficulty.facile;
            default->Difficulty.bebe;
        };
    }
    private boolean importRow(Row r,String folderFiliere,Difficulty difficulty){
        String subjectName=r.get("Matiere"),prompt=r.get("Question"),explanation=r.get("Explication");
        String correct=r.get("Reponse_Correcte");
        if(!Set.of("Option_A","Option_B","Option_C","Option_D").contains(correct)) throw new IllegalArgumentException("Invalid correct option");
        String filiereName=r.optional("Filiere").orElse(folderFiliere);
        Subject subject=subjects.findByName(subjectName).orElseGet(()->{
            Filiere filiere=filieres.findByName(filiereName).orElseGet(()->filieres.save(new Filiere(filiereName)));
            return subjects.save(new Subject(filiere,subjectName));
        });
        if(questions.existsBySubjectIdAndPrompt(subject.id,prompt))return false;
        Question q=new Question(subject,difficulty,prompt,explanation,null);
        for(String code:List.of("A","B","C","D"))q.addOption(code,r.get("Option_"+code),correct.equals("Option_"+code));
        questions.save(q);
        return true;
    }
}
