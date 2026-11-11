package com.studyquiz.user;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;
/** Crée le compte admin au démarrage s'il n'existe pas (configuré par app.admin.*). */
@Component
public class AdminInitializer implements ApplicationRunner {
    static final int MIN_PASSWORD_LENGTH=12;
    private static final Logger log=LoggerFactory.getLogger(AdminInitializer.class);
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final String username,email,password;
    public AdminInitializer(UserRepository users,PasswordEncoder encoder,@Value("${app.admin.username}") String username,
                            @Value("${app.admin.email}") String email,@Value("${app.admin.password}") String password){
        if(password==null || password.length()<MIN_PASSWORD_LENGTH)
            throw new IllegalStateException("ADMIN_PASSWORD must be at least "+MIN_PASSWORD_LENGTH+" characters");
        this.users=users;this.encoder=encoder;this.username=username.trim();this.email=email.trim().toLowerCase(Locale.ROOT);this.password=password;
    }
    @Override @Transactional public void run(ApplicationArguments args){
        if(users.existsByUsernameIgnoreCase(username) || users.existsByEmailIgnoreCase(email)) return;
        User admin=new User(username,email,encoder.encode(password));
        admin.role=UserRole.admin;
        users.save(admin);
        log.info("Admin account '{}' created",username);
    }
}
