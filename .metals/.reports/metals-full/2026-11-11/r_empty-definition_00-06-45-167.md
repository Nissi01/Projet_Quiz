error id: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/FiliereRepository.java:java/util/Optional#
file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/FiliereRepository.java
empty definition using pc, found symbol in pc: java/util/Optional#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 107
uri: file://<WORKSPACE>/backend/src/main/java/com/studyquiz/quiz/FiliereRepository.java
text:
```scala
package com.studyquiz.quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.@@Optional;
public interface FiliereRepository extends JpaRepository<Filiere,Long> { Optional<Filiere> findByName(String name); }

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/Optional#