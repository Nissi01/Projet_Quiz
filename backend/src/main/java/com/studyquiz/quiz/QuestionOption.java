package com.studyquiz.quiz;
import jakarta.persistence.*;
@Entity @Table(name="question_options")
public class QuestionOption {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="question_id") public Question question;
    @Column(nullable=false,length=1) public String code;
    @Column(nullable=false,columnDefinition="text") public String label;
    @Column(nullable=false,name="correct") public boolean correct;
    protected QuestionOption() {}
    QuestionOption(Question question,String code,String label,boolean correct){this.question=question;this.code=code;this.label=label;this.correct=correct;}
}
