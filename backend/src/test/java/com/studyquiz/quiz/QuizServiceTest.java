package com.studyquiz.quiz;

import com.studyquiz.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.LongStream;

import static com.studyquiz.quiz.QuizDtos.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class QuizServiceTest {
    private SubjectRepository subjects;
    private QuestionRepository questions;
    private QuizSessionRepository sessions;
    private QuizService service;

    @BeforeEach void setup() {
        subjects = mock(SubjectRepository.class);
        questions = mock(QuestionRepository.class);
        sessions = mock(QuizSessionRepository.class);
        service = new QuizService(subjects, questions, sessions);
        when(sessions.save(any(QuizSession.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Question question(long id, String correct) {
        Filiere filiere = new Filiere("Informatique");
        Subject subject = new Subject(filiere, "Algorithmique");
        Question q = new Question(subject, "Question " + id, "Explication " + id);
        q.id = id;
        for (String code : List.of("A", "B", "C", "D")) q.addOption(code, "Proposition " + code, code.equals(correct));
        return q;
    }

    @Test void tenQuestionsAreDistinctAndSolutionsAreNotExposed() {
        List<Question> bank = LongStream.rangeClosed(1, 15).mapToObj(id -> question(id, "C")).toList();
        when(subjects.existsById(1L)).thenReturn(true);
        when(questions.findIds(1L)).thenReturn(bank.stream().map(q -> q.id).toList());
        when(questions.withOptions(anyCollection())).thenAnswer(inv -> {
            Collection<Long> ids = inv.getArgument(0);
            return bank.stream().filter(q -> ids.contains(q.id)).toList();
        });

        QuizView quiz = service.start(new StartQuizRequest(1L, 10), null);
        assertEquals(10, quiz.questions().size());
        assertEquals(10, quiz.questions().stream().map(QuestionView::id).distinct().count());
        assertTrue(quiz.questions().stream().allMatch(q -> q.options().size() == 4));
        // QuestionView / OptionView do not have correctCode, correct or explanation fields.
        verify(sessions).save(argThat(s -> s.total == 10 && s.questions.size() == 10));
    }

    @Test void tooManyQuestionsAreRejectedWith400() {
        when(subjects.existsById(1L)).thenReturn(true);
        when(questions.findIds(1L)).thenReturn(List.of(1L));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.start(new StartQuizRequest(1L, 2), null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(sessions, never()).save(any());
    }

    @Test void incorrectAnswerDoesNotIncrementScore() {
        Question q = question(1L, "C");
        QuizSession session = new QuizSession(List.of(q), null);
        when(sessions.findWithQuestions(session.id)).thenReturn(Optional.of(session));
        when(questions.withOptions(anyCollection())).thenReturn(List.of(q));

        QuizResult result = service.submit(session.id,
                new SubmitRequest(List.of(new AnswerInput(1L, "A"))), null);

        assertEquals(0, result.score());
        assertEquals(1, result.total());
        assertFalse(result.corrections().getFirst().correct());
        assertEquals("C", result.corrections().getFirst().correctCode());
        assertEquals("A", session.questions.getFirst().selectedCode);
        assertNotNull(session.completedAt);
    }

    @Test void correctAnswerIncrementsScore() {
        Question q = question(1L, "C");
        QuizSession session = new QuizSession(List.of(q), null);
        when(sessions.findWithQuestions(session.id)).thenReturn(Optional.of(session));
        when(questions.withOptions(anyCollection())).thenReturn(List.of(q));
        QuizResult result = service.submit(session.id,
                new SubmitRequest(List.of(new AnswerInput(1L, "C"))), null);
        assertEquals(1, result.score());
        assertEquals(1, session.score);
    }

    @Test void questionOutsideSessionIsRejectedWith400() {
        QuizSession session = new QuizSession(List.of(question(1L, "C")), null);
        when(sessions.findWithQuestions(session.id)).thenReturn(Optional.of(session));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.submit(session.id, new SubmitRequest(List.of(new AnswerInput(2L, "A"))), null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertNull(session.completedAt);
    }

    @Test void duplicateAnswersAreRejectedWith400() {
        QuizSession session = new QuizSession(List.of(question(1L, "C")), null);
        when(sessions.findWithQuestions(session.id)).thenReturn(Optional.of(session));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.submit(session.id, new SubmitRequest(List.of(new AnswerInput(1L, "A"), new AnswerInput(1L, "B"))), null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test void secondSubmissionIsRejectedWith409() {
        Question q = question(1L, "C");
        QuizSession session = new QuizSession(List.of(q), null);
        when(sessions.findWithQuestions(session.id)).thenReturn(Optional.of(session));
        when(questions.withOptions(anyCollection())).thenReturn(List.of(q));
        SubmitRequest answer = new SubmitRequest(List.of(new AnswerInput(1L, "C")));
        service.submit(session.id, answer, null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.submit(session.id, answer, null));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test void quizOwnedByAnotherUserIsForbidden() {
        User owner = new User("owner", "owner@test.fr", "hash"); owner.id = 10L;
        User stranger = new User("other", "other@test.fr", "hash"); stranger.id = 11L;
        QuizSession session = new QuizSession(List.of(question(1L, "C")), owner);
        when(sessions.findWithQuestions(session.id)).thenReturn(Optional.of(session));
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.submit(session.id, new SubmitRequest(List.of(new AnswerInput(1L, "C"))), stranger));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
