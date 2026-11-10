package com.studyquiz.quiz;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.studyquiz.quiz.QuizDtos.*;
class QuizServiceTest {
    @Test void rejectsRequestWhenThereAreTooFewQuestions(){
        SubjectRepository subjects=mock(SubjectRepository.class);
        QuestionRepository questions=mock(QuestionRepository.class);
        QuizSessionRepository sessions=mock(QuizSessionRepository.class);
        when(subjects.existsById(1L)).thenReturn(true);
        when(questions.findIds(1L)).thenReturn(List.of(1L));
        QuizService service=new QuizService(subjects,questions,sessions);
        assertThrows(ResponseStatusException.class,()->service.start(new StartQuizRequest(1L,2)));
    }
    @Test void rejectsDuplicateAnswers(){
        SubjectRepository subjects=mock(SubjectRepository.class);
        QuestionRepository questions=mock(QuestionRepository.class);
        QuizSessionRepository sessions=mock(QuizSessionRepository.class);
        Question q=new Question(new Subject("Algo"),"Question","Explanation");q.id=1L;
        QuizSession session=new QuizSession(List.of(q));
        when(sessions.findWithQuestions(session.id)).thenReturn(Optional.of(session));
        QuizService service=new QuizService(subjects,questions,sessions);
        assertThrows(ResponseStatusException.class,()->service.submit(session.id,new SubmitRequest(List.of(new AnswerInput(1L,"A"),new AnswerInput(1L,"B")))));
    }
}
