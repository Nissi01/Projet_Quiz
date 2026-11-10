package com.studyquiz.quiz;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface QuizSessionRepository extends JpaRepository<QuizSession,UUID> {
    @Query("select distinct s from QuizSession s left join fetch s.questions where s.id = :id")
    Optional<QuizSession> findWithQuestions(@Param("id") UUID id);
}
