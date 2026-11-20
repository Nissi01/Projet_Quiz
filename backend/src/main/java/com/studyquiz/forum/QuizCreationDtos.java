package com.studyquiz.quiz;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public final class QuizCreationDtos {
    private QuizCreationDtos() {}
    public record QuestionInput(@NotBlank @Size(max=2000) String prompt,@NotBlank @Size(max=2000) String explanation,
                                @NotNull @Size(min=4,max=4) List<@NotBlank @Size(max=500) String> options,
                                @NotBlank @Pattern(regexp="[ABCD]") String correctCode) {}
    public record CreateQuizRequest(Long subjectId,@Size(max=150) String subjectName,@Size(max=150) String filiere,Difficulty difficulty,
                                    @NotNull @Size(min=1,max=50) List<@Valid QuestionInput> questions) {}
    public record CreatedQuizView(Long subjectId,String subject,String filiere,int created) {}
}
