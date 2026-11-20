package com.studyquiz.quiz;
import com.studyquiz.user.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import static com.studyquiz.quiz.QuizCreationDtos.*;
@RestController @RequestMapping("/api")
public class QuizCreationController {
    private final QuizCreationService service;
    private final CurrentUser current;
    public QuizCreationController(QuizCreationService service,CurrentUser current){this.service=service;this.current=current;}
    @PostMapping("/quizzes/custom") public ResponseEntity<CreatedQuizView> create(@Valid @RequestBody CreateQuizRequest req,@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req,current.require(jwt)));
    }
}
