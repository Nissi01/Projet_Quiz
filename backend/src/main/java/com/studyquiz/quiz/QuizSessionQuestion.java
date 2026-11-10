package com.studyquiz.quiz;
import jakarta.persistence.*;
@Entity @Table(name="quiz_session_questions")
@IdClass(QuizSessionQuestionId.class)
public class QuizSessionQuestion {
    @Id @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="session_id") public QuizSession session;
    @Id @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="question_id") public Question question;
    @Column(nullable=false) public int position;
    @Column(name="selected_code",length=1) public String selectedCode;
    protected QuizSessionQuestion() {}
    QuizSessionQuestion(QuizSession session,Question question,int position){this.session=session;this.question=question;this.position=position;}
}
