package com.studyquiz.user;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
/**
 * Résout l'utilisateur courant à partir de l'en-tête X-User-Id.
 * Solution provisoire : à remplacer par une vraie authentification (session ou JWT).
 */
@Component
public class CurrentUser {
    public static final String HEADER="X-User-Id";
    private final UserRepository users;
    public CurrentUser(UserRepository users){this.users=users;}
    public User require(Long id){
        if(id==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Authentication required");
        return users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Unknown user"));
    }
    public User optional(Long id){return id==null?null:require(id);}
}
