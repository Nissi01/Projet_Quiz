package com.studyquiz.quiz;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
@Entity @Table(name="quiz_sessions")
public class QuizSession {
    @Id public UUID id=UUID.randomUUID();
    @Column(name="created_at",nullable=false) public Instant createdAt=Instant.now();
    @Column(name="completed_at") public Instant completedAt;
    public Integer score;
    @Column(nullable=false) public int total;
    @OneToMany(mappedBy="session",cascade=CascadeType.ALL,orphanRemoval=true)
    @OrderBy("position ASC") public List<QuizSessionQuestion> questions=new ArrayList<>();
    protected QuizSession() {}
    public QuizSession(List<Question> selected) {
        total=selected.size();int n=0;
        for (Question q:selected) questions.add(new QuizSessionQuestion(this,q,n++));
    }
}
