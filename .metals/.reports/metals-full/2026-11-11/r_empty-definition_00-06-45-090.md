error id: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/FiliereRepository.java:org/springframework/data/jpa/repository/JpaRepository#
file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/FiliereRepository.java
empty definition using pc, found symbol in pc: org/springframework/data/jpa/repository/JpaRepository#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 75
uri: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/FiliereRepository.java
text:
```scala
package com.studyquiz.quiz;
import org.springframework.data.jpa.repository.@@JpaRepository;
import java.util.Optional;
public interface FiliereRepository extends JpaRepository<Filiere,Long> { Optional<Filiere> findByName(String name); }

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/springframework/data/jpa/repository/JpaRepository#