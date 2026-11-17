error id: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizController.java:com/studyquiz/quiz/QuizDtos#
file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizController.java
empty definition using pc, found symbol in pc: com/studyquiz/quiz/QuizDtos#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 238
uri: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizController.java
text:
```scala
package com.studyquiz.quiz;
import com.studyquiz.user.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static com.studyquiz.quiz.@@QuizDtos.*;
@RestController @RequestMapping("/api")
public class QuizController {
    private final QuizService service;
    private final CurrentUser current;
    public QuizController(QuizService service,CurrentUser current){this.service=service;this.current=current;}
    @GetMapping("/subjects") public List<SubjectView> subjects(){return service.subjects();}
    @PostMapping("/questions") public ResponseEntity<QuestionView> createQuestion(@Valid @RequestBody CreateQuestionRequest req,@RequestHeader(name=CurrentUser.HEADER,required=false) Long userId){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createQuestion(req,current.require(userId)));
    }
    @PostMapping("/quizzes") public ResponseEntity<QuizView> start(@Valid @RequestBody StartQuizRequest req,@RequestHeader(name=CurrentUser.HEADER,required=false) Long userId){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.start(req,current.optional(userId)));
    }
    @PostMapping("/quizzes/{id}/submit") public QuizResult submit(@PathVariable UUID id,@Valid @RequestBody SubmitRequest req,@RequestHeader(name=CurrentUser.HEADER,required=false) Long userId){
        return service.submit(id,req,current.optional(userId));
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: com/studyquiz/quiz/QuizDtos#