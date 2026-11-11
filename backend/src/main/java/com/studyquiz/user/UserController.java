package com.studyquiz.user;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import static com.studyquiz.user.UserDtos.*;
@RestController @RequestMapping("/api")
public class UserController {
    private final UserService service;
    private final CurrentUser current;
    public UserController(UserService service,CurrentUser current){this.service=service;this.current=current;}
    @PostMapping("/auth/login") public LoginResponse login(@Valid @RequestBody LoginRequest req){return service.login(req);}
    @PostMapping("/auth/logout") public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt){
        service.logout(current.require(jwt));
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/users") public ResponseEntity<UserView> create(@Valid @RequestBody CreateUserRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }
    @GetMapping("/users/me") public UserProfile me(@AuthenticationPrincipal Jwt jwt){
        User me=current.require(jwt);
        return service.profile(me.id,me);
    }
    @GetMapping("/users/{id}") public UserProfile profile(@PathVariable Long id,@AuthenticationPrincipal Jwt jwt){
        return service.profile(id,current.optional(jwt));
    }
    @PatchMapping("/users/me/visibility") public UserView visibility(@Valid @RequestBody VisibilityRequest req,@AuthenticationPrincipal Jwt jwt){
        return service.setVisibility(current.require(jwt),req.isPublic());
    }
    @PatchMapping("/users/{id}/role") public UserView role(@PathVariable Long id,@Valid @RequestBody RoleRequest req,@AuthenticationPrincipal Jwt jwt){
        return service.setRole(current.require(jwt),id,req.role());
    }
}
