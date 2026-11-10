package com.studyquiz.forum;
import com.studyquiz.user.User;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="forum_posts")
public class ForumPost {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="thread_id") public ForumThread thread;
    @ManyToOne(optional=false) @JoinColumn(name="user_id") public User user;
    @Column(nullable=false,columnDefinition="text") public String body;
    @Column(name="author_name",nullable=false,length=80) public String authorName;
    @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
    protected ForumPost() {}
    public ForumPost(ForumThread thread,User user,String body){this.thread=thread;this.user=user;this.body=body;this.authorName=user.username;}
}
