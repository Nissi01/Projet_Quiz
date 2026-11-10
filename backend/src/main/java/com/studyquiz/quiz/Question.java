package com.studyquiz.quiz;
import com.studyquiz.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import java.util.*;
@Entity @Table(name="questions")
public class Question {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="subject_id") public Subject subject;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") public User createdBy;
    @Enumerated(EnumType.STRING) @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(nullable=false,columnDefinition="quiz_difficulty") public Difficulty difficulty=Difficulty.bebe;
    @Column(name="prompt",nullable=false,columnDefinition="text") public String prompt;
    @Column(nullable=false,columnDefinition="text") public String explanation;
    @OneToMany(mappedBy="question",cascade=CascadeType.ALL,orphanRemoval=true)
    @OrderBy("code ASC") public List<QuestionOption> options=new ArrayList<>();
    protected Question() {}
    public Question(Subject subject,String prompt,String explanation) { this.subject=subject;this.prompt=prompt;this.explanation=explanation; }
    public Question(Subject subject,Difficulty difficulty,String prompt,String explanation,User createdBy) {
        this(subject,prompt,explanation);this.difficulty=difficulty;this.createdBy=createdBy;
    }
    public void addOption(String code,String label,boolean correct){ options.add(new QuestionOption(this,code,label,correct)); }
}
