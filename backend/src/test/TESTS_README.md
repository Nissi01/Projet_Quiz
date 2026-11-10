# Tests ajoutés à votre backend

## Exécution

Depuis `backend/` sur votre Mac (Java 25 et Gradle installés) :

```bash
gradle test
```

Les tests fournis sont **unitaires** (Mockito), ainsi que des tests HTTP **standalone MockMvc**. Ils ne nécessitent pas de PostgreSQL ou de Docker : les dépendances de persistance sont simulées.

## Fichiers

- `src/test/java/com/studyquiz/quiz/QuizServiceTest.java` : génération de 10 questions distinctes, réponses correctes/incorrectes, questions inconnues, doublons, deuxième soumission, utilisateur différent.
- `src/test/java/com/studyquiz/quiz/QuizControllerHttpTest.java` : validation des tailles de quiz et traduction en codes HTTP 400/409.
- `src/test/java/com/studyquiz/forum/ForumControllerTest.java` : contrôles d'auteur/admin pour modification/suppression, création sans identité.
- `src/test/java/com/studyquiz/user/UserAuthorizationTest.java` : permissions métier pour rôle admin et absence d'utilisateur courant.
- `src/test/java/com/studyquiz/quiz/ConcurrencyGapTest.java` : **désactivé intentionnellement** ; spécification du futur test avec deux transactions PostgreSQL.

## Limites de ces tests

1. Le `CurrentUser` actuel fait confiance à un simple `X-User-Id` envoyé par le client. Les tests de rôle peuvent passer **alors que l'API est usurpable**. Corriger par Spring Security et ajouter des tests HTTP sans / avec JWT ou session valide.
2. Le test de concurrence réel n'est **pas fait**. Une vérification valide requiert 2 connexions/transactions indépendantes sur PostgreSQL et un verrouillage ou `@Version` sur la session. `@Transactional` à lui seul ne protège pas une double soumission concurrente.
3. Il faudra ensuite ajouter des tests d'intégration JPA/Flyway/Testcontainers pour vérifier requêtes SQL, cascade et intégrité des contraintes.
4. Les tests ont été écrits à partir du ZIP fourni ; **je n'ai pas pu les exécuter ici** (Gradle et JDK 25 non disponibles dans cet environnement). Exécuter `gradle test` sur ton Mac et partager toute erreur éventuelle.

## Ajouter uniquement les tests à votre dépôt Git

Copiez le contenu de `src/test/java/com/studyquiz/` fourni dans votre projet, sans copier `build/` ni `.gradle/`. Le ZIP complet livré a été nettoyé de ces caches et fichiers compilés.

## Pour la suite

- Construire un test PostgreSQL/Testcontainers de soumission concurrente (2 transactions réelles) et faire échouer le second appel de manière déterministe.
- Installer Spring Security : authentification réelle et contrôles sur les endpoints du forum et des rôles.
- Renforcer la validation des entrées de soumission (par exemple `@NotEmpty` sur les réponses et validation de la taille du quiz côté service, indépendamment du controller).
