package com.studyquiz.user;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import java.time.*;
@Service
public class JwtService {
    public record IssuedToken(String token,Instant expiresAt) {}
    private final JwtEncoder encoder;
    private final Duration expiration;
    public JwtService(JwtEncoder encoder,@Value("${app.jwt.expiration}") Duration expiration){this.encoder=encoder;this.expiration=expiration;}
    /** sub = id de l'utilisateur ; role et username servent au front et aux règles d'accès. */
    public IssuedToken issue(User user){
        Instant now=Instant.now(),expiresAt=now.plus(expiration);
        JwtClaimsSet claims=JwtClaimsSet.builder()
            .issuer("studyquiz").issuedAt(now).expiresAt(expiresAt)
            .subject(String.valueOf(user.id))
            .claim("username",user.username)
            .claim("role",user.role.name())
            .claim(TokenVersionValidator.CLAIM,user.tokenVersion)
            .build();
        String token=encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
        return new IssuedToken(token,expiresAt);
    }
}
