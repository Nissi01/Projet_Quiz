package com.studyquiz.user;

import com.studyquiz.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidationException;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {
    private SecurityConfig config;
    private UserRepository users;
    private JwtService jwtService;
    private User admin;

    @BeforeEach void setup() {
        config = new SecurityConfig("test-secret-test-secret-test-secret-0123");
        users = mock(UserRepository.class);
        jwtService = new JwtService(config.jwtEncoder(), Duration.ofHours(1));
        admin = new User("admin", "admin@test.fr", "hash"); admin.id = 7L; admin.role = UserRole.admin;
    }

    @Test void issuedTokenIsDecodedWithUserIdAndRole() {
        when(users.findById(7L)).thenReturn(Optional.of(admin));
        String token = jwtService.issue(admin).token();
        Jwt jwt = config.jwtDecoder(users).decode(token);
        assertEquals("7", jwt.getSubject());
        assertEquals("admin", jwt.getClaimAsString("role"));
    }

    @Test void revokedTokenIsRejected() {
        when(users.findById(7L)).thenReturn(Optional.of(admin));
        String token = jwtService.issue(admin).token();
        admin.revokeTokens();
        assertThrows(JwtValidationException.class, () -> config.jwtDecoder(users).decode(token));
    }

    @Test void tokenOfDeletedUserIsRejected() {
        when(users.findById(7L)).thenReturn(Optional.empty());
        String token = jwtService.issue(admin).token();
        assertThrows(JwtValidationException.class, () -> config.jwtDecoder(users).decode(token));
    }

    @Test void tooShortSecretIsRejected() {
        assertThrows(IllegalStateException.class, () -> new SecurityConfig("short"));
    }
}
