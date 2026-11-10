package com.studyquiz.quiz;
import jakarta.persistence.*;
import java.util.*;
@Entity @Table(name="questions")
public class Question {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="subject_id") public Subject subject;
    @Column(name="prompt",nullable=false,columnDefinition="text") public String prompt;
    @Column(nullable=false,columnDefinition="text") public String explanation;
    @OneToMany(mappedBy="question",cascade=CascadeType.ALL,orphanRemoval=true)
    @OrderBy("code ASC") public List<QuestionOption> options=new ArrayList<>();
    protected Question() {}
    public Question(Subject subject,String prompt,String explanation) { this.subject=subject;this.prompt=prompt;this.explanation=explanation; }
    public void addOption(String code,String label,boolean correct){ options.add(new QuestionOption(this,code,label,correct)); }
}
