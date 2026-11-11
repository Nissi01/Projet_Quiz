package com.studyquiz.user;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.Jwt;
/**
 * Refuse un JWT dont la version ("ver") ne correspond plus à users.token_version,
 * ou dont l'utilisateur n'existe plus. C'est ce qui permet la déconnexion côté serveur.
 */
public class TokenVersionValidator implements OAuth2TokenValidator<Jwt> {
    public static final String CLAIM="ver";
    private static final OAuth2Error REVOKED=new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN,"Token revoked",null);
    private final UserRepository users;
    public TokenVersionValidator(UserRepository users){this.users=users;}
    @Override public OAuth2TokenValidatorResult validate(Jwt jwt){
        Object ver=jwt.getClaims().get(CLAIM);
        if(!(ver instanceof Number version)) return OAuth2TokenValidatorResult.failure(REVOKED);
        long id;
        try{id=Long.parseLong(jwt.getSubject());}catch(NumberFormatException e){return OAuth2TokenValidatorResult.failure(REVOKED);}
        boolean valid=users.findById(id).map(u->u.tokenVersion==version.intValue()).orElse(false);
        return valid?OAuth2TokenValidatorResult.success():OAuth2TokenValidatorResult.failure(REVOKED);
    }
}
