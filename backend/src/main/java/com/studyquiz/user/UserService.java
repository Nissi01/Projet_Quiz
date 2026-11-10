package com.studyquiz.user;
import com.studyquiz.quiz.QuestionRepository;
import com.studyquiz.quiz.QuizSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Locale;
import static com.studyquiz.user.UserDtos.*;
@Service
public class UserService {
    private final UserRepository users;
    private final QuestionRepository questions;
    private final QuizSessionRepository sessions;
    private final PasswordEncoder encoder=new BCryptPasswordEncoder();
    public UserService(UserRepository users,QuestionRepository questions,QuizSessionRepository sessions){
        this.users=users;this.questions=questions;this.sessions=sessions;
    }
    @Transactional
    public UserView create(CreateUserRequest request){
        String username=request.username().trim(),email=request.email().trim().toLowerCase(Locale.ROOT);
        if(users.existsByUsernameIgnoreCase(username)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Username already taken");
        if(users.existsByEmailIgnoreCase(email)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already used");
        return view(users.save(new User(username,email,encoder.encode(request.password()))));
    }
    @Transactional(readOnly=true)
    public UserProfile profile(Long id,User viewer){
        User u=find(id);
        boolean allowed=u.isPublic || viewer!=null && (viewer.id.equals(u.id) || viewer.isAdmin());
        if(!allowed) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"This profile is private");
        return new UserProfile(view(u),
            questions.findByCreatedByIdOrderByIdDesc(id).stream().map(q->new CreatedQuestionView(q.id,q.subject.name,q.difficulty,q.prompt)).toList(),
            sessions.findByUserIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(id).stream().map(s->new QuizResultView(s.id,s.score,s.total,s.completedAt)).toList());
    }
    @Transactional
    public UserView setVisibility(User me,boolean isPublic){
        User u=find(me.id);u.isPublic=isPublic;return view(u);
    }
    @Transactional
    public UserView setRole(User admin,Long id,UserRole role){
        if(!admin.isAdmin()) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Admin only");
        User u=find(id);u.role=role;return view(u);
    }
    private User find(Long id){return users.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found"));}
    static UserView view(User u){return new UserView(u.id,u.username,u.role,u.isPublic,u.createdAt);}
}
