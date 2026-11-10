package com.studyquiz.quiz;
import java.io.Serializable;
import java.util.*;
public class QuizSessionQuestionId implements Serializable {
    public UUID session;
    public Long question;
    public QuizSessionQuestionId() {}
    @Override public boolean equals(Object x){return x instanceof QuizSessionQuestionId other && Objects.equals(session,other.session) && Objects.equals(question,other.question);}
    @Override public int hashCode(){return Objects.hash(session,question);}
}
