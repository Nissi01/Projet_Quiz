package com.studyquiz.quiz;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.*;
public final class QuizDtos {
    private QuizDtos() {}
    public record SubjectView(Long id,String name,String filiere) {}
    public record StartQuizRequest(Long subjectId,@Min(1) @Max(50) int count) {}
    public record OptionView(String code,String label) {}
    public record QuestionView(Long id,String subject,Difficulty difficulty,String prompt,List<OptionView> options) {}
    public record QuizView(UUID sessionId,List<QuestionView> questions) {}
    public record AnswerInput(@NotNull Long questionId,@NotBlank String optionCode) {}
    public record SubmitRequest(@NotNull List<@Valid AnswerInput> answers) {}
    public record Correction(Long questionId,String selectedCode,String correctCode,boolean correct,String explanation) {}
    public record QuizResult(UUID sessionId,int score,int total,List<Correction> corrections) {}
    /** options = libellés des réponses A, B, C, D dans cet ordre. */
    public record CreateQuestionRequest(@NotNull Long subjectId,Difficulty difficulty,@NotBlank String prompt,@NotBlank String explanation,
                                        @NotNull @Size(min=4,max=4) List<@NotBlank String> options,@NotBlank @Pattern(regexp="[ABCD]") String correctCode) {}
}
