package com.studyquiz.quiz;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static com.studyquiz.quiz.QuizDtos.*;
@RestController @RequestMapping("/api")
public class QuizController {
    private final QuizService service;
    public QuizController(QuizService service){this.service=service;}
    @GetMapping("/subjects") public List<SubjectView> subjects(){return service.subjects();}
    @PostMapping("/quizzes") public ResponseEntity<QuizView> start(@Valid @RequestBody StartQuizRequest req){return ResponseEntity.status(HttpStatus.CREATED).body(service.start(req));}
    @PostMapping("/quizzes/{id}/submit") public QuizResult submit(@PathVariable UUID id,@Valid @RequestBody SubmitRequest req){return service.submit(id,req);}
}
