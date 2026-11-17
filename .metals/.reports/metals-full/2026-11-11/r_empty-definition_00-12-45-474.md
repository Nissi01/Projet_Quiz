error id: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizSessionQuestionId.java:java/io/Serializable#
file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizSessionQuestionId.java
empty definition using pc, found symbol in pc: java/io/Serializable#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 43
uri: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizSessionQuestionId.java
text:
```scala
package com.studyquiz.quiz;
import java.io.@@Serializable;
import java.util.*;
public class QuizSessionQuestionId implements Serializable {
    public UUID session;
    public Long question;
    public QuizSessionQuestionId() {}
    @Override public boolean equals(Object x){return x instanceof QuizSessionQuestionId other && Objects.equals(session,other.session) && Objects.equals(question,other.question);}
    @Override public int hashCode(){return Objects.hash(session,question);}
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/io/Serializable#