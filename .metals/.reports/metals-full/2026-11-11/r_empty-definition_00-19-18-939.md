error id: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuestionRepository.java:org/springframework/data/repository/query/Param#
file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuestionRepository.java
empty definition using pc, found symbol in pc: org/springframework/data/repository/query/Param#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 127
uri: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/QuestionRepository.java
text:
```scala
package com.studyquiz.quiz;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.@@Param;
import java.util.*;
public interface QuestionRepository extends JpaRepository<Question,Long> {
    boolean existsBySubjectIdAndPrompt(Long id,String prompt);
    @Query("select distinct q from Question q join fetch q.subject left join fetch q.options where q.id in :ids")
    List<Question> withOptions(@Param("ids") Collection<Long> ids);
    @Query("select q.id from Question q where (:subjectId is null or q.subject.id = :subjectId)")
    List<Long> findIds(@Param("subjectId") Long subjectId);
    @EntityGraph(attributePaths="subject")
    List<Question> findByCreatedByIdOrderByIdDesc(Long userId);
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/springframework/data/repository/query/Param#