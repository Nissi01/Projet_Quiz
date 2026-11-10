package com.studyquiz.user;

import com.studyquiz.quiz.QuestionRepository;
import com.studyquiz.quiz.QuizSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthorizationTest {
    private UserRepository repository;
    private UserService service;
    private User regular, target, admin;

    @BeforeEach void setup() {
        repository = mock(UserRepository.class);
        service = new UserService(repository, mock(QuestionRepository.class), mock(QuizSessionRepository.class),
                mock(PasswordEncoder.class), mock(JwtService.class));
        regular = new User("regular", "regular@test.fr", "hash"); regular.id = 1L;
        target = new User("target", "target@test.fr", "hash"); target.id = 2L;
        admin = new User("admin", "admin@test.fr", "hash"); admin.id = 3L; admin.role = UserRole.admin;
        when(repository.findById(2L)).thenReturn(Optional.of(target));
    }

    @Test void regularUserCannotGrantAdminRole() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.setRole(regular, 2L, UserRole.admin));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertEquals(UserRole.user, target.role);
        assertEquals(0, target.tokenVersion);
    }

    @Test void adminCanGrantAdminRole() {
        service.setRole(admin, 2L, UserRole.admin);
        assertEquals(UserRole.admin, target.role);
        // Les JWT déjà émis portent l'ancien rôle : ils doivent être révoqués.
        assertEquals(1, target.tokenVersion);
    }

    @Test void unchangedRoleDoesNotRevokeTokens() {
        service.setRole(admin, 2L, UserRole.user);
        assertEquals(0, target.tokenVersion);
    }

    @Test void missingUserIdentityReturns401() {
        CurrentUser current = new CurrentUser(repository);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> current.require(null));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test void unknownIdentityReturns401() {
        CurrentUser current = new CurrentUser(repository);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> current.require(Jwt.withTokenValue("t").header("alg", "HS256").subject("999").build()));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
