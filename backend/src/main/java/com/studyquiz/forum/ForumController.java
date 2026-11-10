package com.studyquiz.forum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
@RestController @RequestMapping("/api/forum/threads")
public class ForumController {
    public record CreateThread(@NotBlank @Size(max=200) String title,@NotBlank String body,@NotBlank @Size(max=80) String authorName) {}
    public record CreatePost(@NotBlank String body,@NotBlank @Size(max=80) String authorName) {}
    public record ThreadView(Long id,String title,String body,String authorName,Instant createdAt) {}
    public record PostView(Long id,Long threadId,String body,String authorName,Instant createdAt) {}
    private final ForumThreadRepository threads;
    private final ForumPostRepository posts;
    public ForumController(ForumThreadRepository threads,ForumPostRepository posts){this.threads=threads;this.posts=posts;}
    @GetMapping public Page<ThreadView> threads(@RequestParam(defaultValue="0") int page){
        return threads.findAll(PageRequest.of(Math.max(page,0),20,Sort.by(Sort.Direction.DESC,"createdAt"))).map(this::view);
    }
    @PostMapping public ResponseEntity<ThreadView> create(@Valid @RequestBody CreateThread req){
        return ResponseEntity.status(HttpStatus.CREATED).body(view(threads.save(new ForumThread(req.title(),req.body(),req.authorName()))));
    }
    @GetMapping("/{id}") public ThreadView thread(@PathVariable Long id){return view(findThread(id));}
    @GetMapping("/{id}/posts") public Page<PostView> posts(@PathVariable Long id,@RequestParam(defaultValue="0") int page){
        findThread(id);
        return posts.findByThreadId(id,PageRequest.of(Math.max(page,0),20,Sort.by("createdAt"))).map(this::view);
    }
    @PostMapping("/{id}/posts") public ResponseEntity<PostView> reply(@PathVariable Long id,@Valid @RequestBody CreatePost req){
        return ResponseEntity.status(HttpStatus.CREATED).body(view(posts.save(new ForumPost(findThread(id),req.body(),req.authorName()))));
    }
    private ForumThread findThread(Long id){return threads.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Thread not found"));}
    private ThreadView view(ForumThread t){return new ThreadView(t.id,t.title,t.body,t.authorName,t.createdAt);}
    private PostView view(ForumPost p){return new PostView(p.id,p.thread.id,p.body,p.authorName,p.createdAt);}
}
