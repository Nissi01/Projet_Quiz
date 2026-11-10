package com.studyquiz.quiz;

import com.studyquiz.common.ApiExceptionHandler;
import com.studyquiz.user.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class QuizControllerHttpTest {
    private QuizService service;
    private MockMvc mvc;

    @BeforeEach void setup() {
        service = mock(QuizService.class);
        CurrentUser current = mock(CurrentUser.class);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new QuizController(service, current))
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test void invalidQuizSizeOver50Returns400() throws Exception {
        mvc.perform(post("/api/quizzes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"subjectId\":1,\"count\":51}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void zeroQuizSizeReturns400() throws Exception {
        mvc.perform(post("/api/quizzes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"subjectId\":1,\"count\":0}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void insufficientQuestionsBecomeHttp400() throws Exception {
        when(service.start(any(), isNull())).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,"Not enough questions"));
        mvc.perform(post("/api/quizzes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"subjectId\":1,\"count\":10}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Not enough questions"));
    }

    @Test void duplicateSubmissionBecomesHttp409() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.submit(eq(id), any(), isNull())).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT,"Quiz already submitted"));
        mvc.perform(post("/api/quizzes/{id}/submit", id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"answers\":[{\"questionId\":1,\"optionCode\":\"A\"}]}"))
                .andExpect(status().isConflict());
    }
}
