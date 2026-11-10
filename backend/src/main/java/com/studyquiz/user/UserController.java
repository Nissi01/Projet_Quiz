package com.studyquiz.user;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import static com.studyquiz.user.UserDtos.*;
@RestController @RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final CurrentUser current;
    public UserController(UserService service,CurrentUser current){this.service=service;this.current=current;}
    @PostMapping public ResponseEntity<UserView> create(@Valid @RequestBody CreateUserRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }
    @GetMapping("/me") public UserProfile me(@RequestHeader(name=CurrentUser.HEADER,required=false) Long userId){
        User me=current.require(userId);
        return service.profile(me.id,me);
    }
    @GetMapping("/{id}") public UserProfile profile(@PathVariable Long id,@RequestHeader(name=CurrentUser.HEADER,required=false) Long userId){
        return service.profile(id,current.optional(userId));
    }
    @PatchMapping("/me/visibility") public UserView visibility(@Valid @RequestBody VisibilityRequest req,@RequestHeader(name=CurrentUser.HEADER,required=false) Long userId){
        return service.setVisibility(current.require(userId),req.isPublic());
    }
    @PatchMapping("/{id}/role") public UserView role(@PathVariable Long id,@Valid @RequestBody RoleRequest req,@RequestHeader(name=CurrentUser.HEADER,required=false) Long userId){
        return service.setRole(current.require(userId),id,req.role());
    }
}
