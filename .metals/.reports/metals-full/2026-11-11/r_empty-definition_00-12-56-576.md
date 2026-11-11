error id: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizSessionRepository.java:org/springframework/data/repository/query/Param#
file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizSessionRepository.java
empty definition using pc, found symbol in pc: org/springframework/data/repository/query/Param#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 127
uri: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuizSessionRepository.java
text:
```scala
package com.studyquiz.quiz;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.@@Param;
import java.util.*;
public interface QuizSessionRepository extends JpaRepository<QuizSession,UUID> {
    @Query("select distinct s from QuizSession s left join fetch s.questions where s.id = :id")
    Optional<QuizSession> findWithQuestions(@Param("id") UUID id);
    List<QuizSession> findByUserIdAndCompletedAtIsNotNullOrderByCompletedAtDesc(Long userId);
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/springframework/data/repository/query/Param#