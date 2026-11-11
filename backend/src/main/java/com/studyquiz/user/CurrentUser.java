package com.studyquiz.user;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
/** Charge l'utilisateur correspondant au JWT (claim sub = id). */
@Component
public class CurrentUser {
    private final UserRepository users;
    public CurrentUser(UserRepository users){this.users=users;}
    public User require(Jwt jwt){
        if(jwt==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required");
        return users.findById(Long.valueOf(jwt.getSubject())).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unknown user"));
    }
    public User optional(Jwt jwt){return jwt==null?null:require(jwt);}
}
