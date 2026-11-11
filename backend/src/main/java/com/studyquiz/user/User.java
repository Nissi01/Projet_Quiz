package com.studyquiz.user;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import java.time.Instant;
@Entity @Table(name="users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false,unique=true,length=80) public String username;
    @Column(nullable=false,unique=true,length=255) public String email;
    @Column(name="password_hash",nullable=false,length=255) public String passwordHash;
    @Enumerated(EnumType.STRING) @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable=false,columnDefinition="user_role") public UserRole role=UserRole.user;
    @Column(name="is_public",nullable=false) public boolean isPublic=true;
    /** Incrémenté pour révoquer tous les JWT déjà émis (déconnexion, changement de rôle). */
    @Column(name="token_version",nullable=false) public int tokenVersion=0;
    @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
    protected User() {}
    public User(String username,String email,String passwordHash){this.username=username;this.email=email;this.passwordHash=passwordHash;}
    public boolean isAdmin(){return role==UserRole.admin;}
    public void revokeTokens(){tokenVersion++;}
}
