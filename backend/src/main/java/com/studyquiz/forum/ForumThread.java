package com.studyquiz.forum;
import com.studyquiz.user.User;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="forum_threads")
public class ForumThread {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false) @JoinColumn(name="user_id") public User user;
    @Column(nullable=false,length=200) public String title;
    @Column(nullable=false,columnDefinition="text") public String body;
    @Column(name="author_name",nullable=false,length=80) public String authorName;
    @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
    protected ForumThread() {}
    public ForumThread(User user,String title,String body){this.user=user;this.title=title;this.body=body;this.authorName=user.username;}
}
