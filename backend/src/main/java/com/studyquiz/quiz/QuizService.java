package com.studyquiz.quiz;
import com.studyquiz.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static com.studyquiz.quiz.QuizDtos.*;
@Service
public class QuizService {
    private final SubjectRepository subjects;
    private final QuestionRepository questions;
    private final QuizSessionRepository sessions;
    public QuizService(SubjectRepository subjects,QuestionRepository questions,QuizSessionRepository sessions){
        this.subjects=subjects;this.questions=questions;this.sessions=sessions;
    }
    @Transactional(readOnly=true)
    public List<SubjectView> subjects(){return subjects.findAll().stream().map(s->new SubjectView(s.id,s.name,s.filiere.name)).toList();}
    @Transactional
    public QuestionView createQuestion(CreateQuestionRequest request,User author){
        Subject subject=subjects.findById(request.subjectId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Subject not found"));
        String prompt=request.prompt().trim();
        if(questions.existsBySubjectIdAndPrompt(subject.id,prompt)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Question already exists");
        Difficulty difficulty=request.difficulty()==null?Difficulty.bebe:request.difficulty();
        Question q=new Question(subject,difficulty,prompt,request.explanation().trim(),author);
        List<String> codes=List.of("A","B","C","D");
        for(int i=0;i<codes.size();i++) q.addOption(codes.get(i),request.options().get(i).trim(),codes.get(i).equals(request.correctCode()));
        return publicQuestion(questions.save(q));
    }
    @Transactional
    public QuizView start(StartQuizRequest request,User user){
        if(request.subjectId()!=null && !subjects.existsById(request.subjectId())) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Subject not found");
        List<Long> ids=new ArrayList<>(questions.findIds(request.subjectId()));
        if(ids.size()<request.count()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not enough questions for this selection");
        Collections.shuffle(ids);
        List<Long> chosen=ids.subList(0,request.count());
        Map<Long,Question> map=questions.withOptions(chosen).stream().collect(Collectors.toMap(q->q.id, Function.identity()));
        List<Question> ordered=chosen.stream().map(map::get).toList();
        QuizSession s=sessions.save(new QuizSession(ordered,user));
        return new QuizView(s.id,ordered.stream().map(this::publicQuestion).toList());
    }
    private QuestionView publicQuestion(Question q){
        return new QuestionView(q.id,q.subject.name,q.difficulty,q.prompt,q.options.stream().map(o->new OptionView(o.code,o.label)).toList());
    }
    @Transactional
    public QuizResult submit(UUID id,SubmitRequest request,User user){
        QuizSession s=sessions.findWithQuestions(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Quiz not found"));
        if(s.user!=null && (user==null || !s.user.id.equals(user.id))) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This quiz belongs to another user");
        if(s.completedAt!=null) throw new ResponseStatusException(HttpStatus.CONFLICT,"Quiz already submitted");
        Map<Long,String> answers=new HashMap<>();
        for(AnswerInput a:request.answers()){
            if(!Set.of("A","B","C","D").contains(a.optionCode())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Option must be A, B, C or D");
            if(answers.putIfAbsent(a.questionId(),a.optionCode())!=null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Duplicate answer");
        }
        Set<Long> expected=s.questions.stream().map(sq->sq.question.id).collect(Collectors.toSet());
        if(!expected.equals(answers.keySet())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Answer exactly the session questions");
        Map<Long,Question> map=questions.withOptions(expected).stream().collect(Collectors.toMap(q->q.id,Function.identity()));
        List<Correction> corrections=new ArrayList<>();int score=0;
        for(QuizSessionQuestion sq:s.questions){
            Question q=map.get(sq.question.id);
            String correct=q.options.stream().filter(o->o.correct).findFirst().orElseThrow().code;
            String selected=answers.get(q.id);
            boolean ok=correct.equals(selected);
            if(ok)score++;
            sq.selectedCode=selected;
            corrections.add(new Correction(q.id,selected,correct,ok,q.explanation));
        }
        s.score=score;s.completedAt=Instant.now();
        return new QuizResult(s.id,score,s.total,corrections);
    }
}
