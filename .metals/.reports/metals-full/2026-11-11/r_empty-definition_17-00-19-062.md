error id: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/forum/ForumController.java:com/studyquiz/user/User#
file://<WORKSPACE>/backend/src/main/java/com/studyquiz/forum/ForumController.java
empty definition using pc, found symbol in pc: com/studyquiz/user/User#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 223
uri: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/forum/ForumController.java
text:
```scala
package com.studyquiz.forum;
import com.studyquiz.user.CurrentUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import com.studyquiz.user.@@User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
@RestController @RequestMapping("/api/forum/threads")
public class ForumController {
    public record CreateThread(@NotBlank @Size(max=200) String title,@NotBlank String body) {}
    public record CreatePost(@NotBlank String body) {}
    public record ThreadView(Long id,String title,String body,Long userId,String authorName,Instant createdAt) {}
    public record PostView(Long id,Long threadId,String body,Long userId,String authorName,Instant createdAt) {}
    private final ForumThreadRepository threads;
    private final ForumPostRepository posts;
    private final CurrentUser current;
    public ForumController(ForumThreadRepository threads,ForumPostRepository posts,CurrentUser current){this.threads=threads;this.posts=posts;this.current=current;}
    @GetMapping public Page<ThreadView> threads(@RequestParam(defaultValue="0") int page){
        return threads.findAll(PageRequest.of(Math.max(page,0),20,Sort.by(Sort.Direction.DESC,"createdAt"))).map(this::view);
    }
    @PostMapping public ResponseEntity<ThreadView> create(@Valid @RequestBody CreateThread req,@AuthenticationPrincipal Jwt jwt){
        return ResponseEntity.status(HttpStatus.CREATED).body(view(threads.save(new ForumThread(current.require(jwt),req.title(),req.body()))));
    }
    @GetMapping("/{id}") public ThreadView thread(@PathVariable Long id){return view(findThread(id));}
    @PutMapping("/{id}") public ThreadView update(@PathVariable Long id,@Valid @RequestBody CreateThread req,@AuthenticationPrincipal Jwt jwt){
        ForumThread t=ownedThread(id,current.require(jwt));
        t.title=req.title();t.body=req.body();
        return view(threads.save(t));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){
        threads.delete(ownedThread(id,current.require(jwt)));
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}/posts") public Page<PostView> posts(@PathVariable Long id,@RequestParam(defaultValue="0") int page){
        findThread(id);
        return posts.findByThreadId(id,PageRequest.of(Math.max(page,0),20,Sort.by("createdAt"))).map(p->view(p,id));
    }
    @PostMapping("/{id}/posts") public ResponseEntity<PostView> reply(@PathVariable Long id,@Valid @RequestBody CreatePost req,@AuthenticationPrincipal Jwt jwt){
        User user=current.require(jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(view(posts.save(new ForumPost(findThread(id),user,req.body())),id));
    }
    private ForumThread findThread(Long id){return threads.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Thread not found"));}
    /** Seul l'auteur du thread ou un admin peut le modifier ou le supprimer. */
    private ForumThread ownedThread(Long id,User user){
        ForumThread t=findThread(id);
        if(!t.user.id.equals(user.id) && !user.isAdmin()) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not the author of this thread");
        return t;
    }
    private ThreadView view(ForumThread t){return new ThreadView(t.id,t.title,t.body,t.user.id,t.user.username,t.createdAt);}
    private PostView view(ForumPost p,Long threadId){return new PostView(p.id,threadId,p.body,p.user.id,p.user.username,p.createdAt);}
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: com/studyquiz/user/User#