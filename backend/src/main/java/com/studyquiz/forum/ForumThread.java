package com.studyquiz.forum;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="forum_threads")
public class ForumThread {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false,length=200) public String title;
    @Column(nullable=false,columnDefinition="text") public String body;
    @Column(name="author_name",nullable=false,length=80) public String authorName;
    @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
    protected ForumThread() {}
    public ForumThread(String title,String body,String authorName){this.title=title;this.body=body;this.authorName=authorName;}
}
