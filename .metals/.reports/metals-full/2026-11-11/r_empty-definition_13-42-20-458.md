error id: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizController.java:org/springframework/security/core/annotation/AuthenticationPrincipal#
file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizController.java
empty definition using pc, found symbol in pc: org/springframework/security/core/annotation/AuthenticationPrincipal#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 119
uri: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizController.java
text:
```scala
package com.studyquiz.quiz;
import com.studyquiz.user.CurrentUser;
import org.springframework.security.core.annotation.@@AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static com.studyquiz.quiz.QuizDtos.*;
@RestController @RequestMapping("/api")
public class QuizController {
    private final QuizService service;
    private final CurrentUser current;
    public QuizController(QuizService service,CurrentUser current){this.service=service;this.current=current;}
    @GetMapping("/subjects") public List<SubjectView> subjects(){return service.subjects();}
    @PostMapping("/questions") public ResponseEntity<QuestionView> createQuestion(@Valid @RequestBody CreateQuestionRequest req,@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createQuestion(req,current.require(jwt)));
    }
    @PostMapping("/quizzes") public ResponseEntity<QuizView> start(@Valid @RequestBody StartQuizRequest req,@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.start(req,current.optional(jwt)));
    }
    @PostMapping("/quizzes/{id}/submit") public QuizResult submit(@PathVariable UUID id,@Valid @RequestBody SubmitRequest req,@AuthenticationPrincipal Jwt jwt){
        return service.submit(id,req,current.optional(jwt));
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/springframework/security/core/annotation/AuthenticationPrincipal#