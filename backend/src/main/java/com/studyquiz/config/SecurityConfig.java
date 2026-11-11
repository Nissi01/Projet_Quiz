package com.studyquiz.config;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.studyquiz.user.TokenVersionValidator;
import com.studyquiz.user.UserRepository;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
@Configuration
public class SecurityConfig {
    private final SecretKey key;
    public SecurityConfig(@Value("${app.jwt.secret}") String secret){
        byte[] bytes=secret.getBytes(StandardCharsets.UTF_8);
        if(bytes.length<32) throw new IllegalStateException("app.jwt.secret must be at least 32 bytes");
        this.key=new SecretKeySpec(bytes,"HmacSHA256");
    }
    @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(c->c.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a->a
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.POST,"/api/auth/login","/api/users").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/subjects","/api/forum/**").permitAll()
                .requestMatchers(HttpMethod.GET,"/api/users/me").authenticated()
                .requestMatchers(HttpMethod.GET,"/api/users/*").permitAll()
                // Quiz possible sans compte : la session est alors anonyme.
                .requestMatchers(HttpMethod.POST,"/api/quizzes","/api/quizzes/*/submit").permitAll()
                .requestMatchers(HttpMethod.PATCH,"/api/users/*/role").hasRole("admin")
                .anyRequest().authenticated())
            .oauth2ResourceServer(o->o.jwt(j->j.jwtAuthenticationConverter(jwtAuthenticationConverter())))
            .build();
    }
    /** Le claim "role" (user/admin) devient l'autorité ROLE_user / ROLE_admin. */
    private JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter authorities=new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("role");
        authorities.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter=new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
    @Bean public JwtEncoder jwtEncoder(){return new NimbusJwtEncoder(new ImmutableSecret<>(key));}
    /** Vérifie signature + expiration (validateurs par défaut) + version du token en base. */
    @Bean public JwtDecoder jwtDecoder(UserRepository users){
        NimbusJwtDecoder decoder=NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefault(),new TokenVersionValidator(users)));
        return decoder;
    }
    @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
}
