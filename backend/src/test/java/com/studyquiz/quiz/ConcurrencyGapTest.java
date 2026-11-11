package com.studyquiz.quiz;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test à rendre exécutable APRÈS l'ajout du verrouillage transactionnel.
 * Un mock de repository partageant la même instance JPA ne reproduit PAS deux transactions concurrentes.
 * Utiliser @SpringBootTest + PostgreSQL Testcontainers, deux threads et deux transactions distinctes.
 */
class ConcurrencyGapTest {
    @Test
    @Disabled("À implémenter sur PostgreSQL avec 2 transactions après ajout d'un verrou pessimiste/optimiste sur QuizSession")
    void onlyOneConcurrentSubmissionCanCommit() {
        // Critère d'acceptation : sur deux soumissions simultanées d'une même session,
        // une seule réussit; la seconde doit échouer et aucune correction ne doit être écrasée.
    }
}
