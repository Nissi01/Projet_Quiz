package com.studyquiz.quiz;
import com.studyquiz.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
@Entity @Table(name="quiz_sessions")
public class QuizSession {
    @Id public UUID id=UUID.randomUUID();
    @ManyToOne @JoinColumn(name="user_id") public User user;
    public Integer score;
    @Column(nullable=false) public int total;
    @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
    @Column(name="completed_at") public Instant completedAt;
    @OneToMany(mappedBy="session",cascade=CascadeType.ALL,orphanRemoval=true)
    @OrderBy("position ASC") public List<QuizSessionQuestion> questions=new ArrayList<>();
    protected QuizSession() {}
    public QuizSession(List<Question> selected,User user) {
        this.user=user;total=selected.size();int n=0;
        for (Question q:selected) questions.add(new QuizSessionQuestion(this,q,n++));
    }
}
