package com.studyquiz.user;
import com.studyquiz.quiz.Difficulty;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;
public final class UserDtos {
    private UserDtos() {}
    public record CreateUserRequest(@NotBlank @Size(min=3,max=80) String username,@NotBlank @Email @Size(max=255) String email,@NotBlank @Size(min=8,max=72) String password) {}
    /** login = nom d'utilisateur ou email. */
    public record LoginRequest(@NotBlank String login,@NotBlank String password) {}
    public record LoginResponse(String token,Instant expiresAt,UserView user) {}
    public record VisibilityRequest(@NotNull Boolean isPublic) {}
    public record RoleRequest(@NotNull UserRole role) {}
    public record UserView(Long id,String username,UserRole role,boolean isPublic,Instant createdAt) {}
    public record CreatedQuestionView(Long id,String subject,Difficulty difficulty,String prompt) {}
    public record QuizResultView(UUID sessionId,Integer score,int total,Instant completedAt) {}
    public record UserProfile(UserView user,List<CreatedQuestionView> createdQuestions,List<QuizResultView> quizResults) {}
}
