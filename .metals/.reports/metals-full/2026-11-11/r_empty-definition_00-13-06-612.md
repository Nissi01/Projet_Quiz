error id: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/SubjectRepository.java:java/util/Optional#
file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/SubjectRepository.java
empty definition using pc, found symbol in pc: java/util/Optional#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 107
uri: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/SubjectRepository.java
text:
```scala
package com.studyquiz.quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.@@Optional;
public interface SubjectRepository extends JpaRepository<Subject,Long> { Optional<Subject> findByName(String name); }

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/Optional#