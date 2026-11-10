# StudyQuiz Backend — Java / Spring Boot / PostgreSQL

Premier MVP : import CSV, sélection de questions, sessions de quiz et correction sécurisée côté serveur, plus forum minimal.

## Prérequis
- **Java 21** (JDK)
- **Gradle 8.14+** ou une version compatible avec Spring Boot 3.5.x (Gradle 8.14 recommandé)
- **Docker Desktop** (pour PostgreSQL)

## Lancer
```bash
docker compose up -d
gradle bootRun
```
L'API écoute sur `http://localhost:8080` ; le front React en développement est supposé sur `http://localhost:5173` (modifiable avec `CORS_ORIGIN`).

Gradle n'est pas fourni sous forme de wrapper dans cette archive : installe Gradle localement (ou génère le wrapper via `gradle wrapper --gradle-version 8.14.3`).

## Ajouter les questions
Modifier `src/main/resources/questions.csv`, encodage UTF-8, séparateur `;`, puis redémarrer. Le fichier est lu au lancement et chaque question absente est insérée en base. Une question déjà importée (même matière + même libellé) n'est pas mise à jour : pour éditer un contenu, il faudra prévoir un espace d'administration et un import avec mise à jour.

En production : `IMPORT_SEED=false` pour ne pas effectuer d'import au démarrage.

## API quiz
### `GET /api/subjects`
Retourne `[{"id":1,"name":"Histoire de l'informatique"}]`.

### `POST /api/quizzes`
Corps (quiz ciblé) :
```json
{"subjectId":1,"count":2}
```
Corps (mix toutes matières) :
```json
{"subjectId":null,"count":2}
```
Réponse `201` :
```json
{"sessionId":"<uuid>","questions":[{"id":1,"subject":"Histoire de l'informatique","prompt":"...","options":[{"code":"A","label":"Ada Lovelace"}]}]}
```
Le **code correct et l'explication ne sont pas envoyés** à cette étape.

### `POST /api/quizzes/{sessionId}/submit`
```json
{"answers":[{"questionId":1,"optionCode":"A"},{"questionId":2,"optionCode":"C"}]}
```
Réponse : `{"sessionId":"...","score":2,"total":2,"corrections":[{"questionId":1,"selectedCode":"A","correctCode":"A","correct":true,"explanation":"..."}]}`
Une session ne peut être soumise qu'une fois. Il faut répondre à toutes les questions exactement une fois.

## API forum (prototype sans comptes)
- `GET /api/forum/threads?page=0` — sujets (pagination)
- `POST /api/forum/threads` — `{"title":"Question SQL","body":"...","authorName":"Alice"}`
- `GET /api/forum/threads/{id}` — détail
- `GET /api/forum/threads/{id}/posts?page=0` — réponses
- `POST /api/forum/threads/{id}/posts` — `{"body":"...","authorName":"Bob"}`

## Structure
```text
src/main/java/com/studyquiz/
├── common/          # erreurs HTTP
├── config/          # CORS React
├── quiz/            # import CSV, questions, sessions, correction
└── forum/           # sujets et réponses
src/main/resources/
├── application.yml # configuration
├── questions.csv   # données initiales
└── db/migration/    # Flyway : schéma SQL versionné
```

## Pourquoi ces choix ?
- **Spring Web** : routes HTTP JSON pour React.
- **JPA + PostgreSQL** : persistance structurée des questions, sessions et messages.
- **Flyway** : changements de schéma reproductibles entre développeurs.
- **Apache Commons CSV** : lecture robuste du `;` et gestion des champs entre guillemets.
- **DTO Java records** : réponse HTTP contrôlée, sans divulguer les corrigés.
- **Monolithe modulaire** : frontières `quiz`/`forum` claires sans complexité microservices.

## Limites volontaires du MVP
- Aucune authentification : le lien de session UUID agit comme un secret temporaire ; les auteurs du forum sont des noms librement saisis. **Ne pas déployer publiquement tel quel.**
- Pas encore de comptes, droits, modération, anti-spam, limite de fréquence, expiration des sessions, édition admin ou suivi individuel.
- L'import automatique au démarrage est destiné au développement ; il n'est pas conçu pour des démarrages concurrents de plusieurs instances.
- Sélection aléatoire en mémoire, appropriée pour un premier catalogue mais à optimiser pour des millions de questions.
- Relecture pédagogique des questions obligatoire ; la chronologie de l'ENIAC du CSV initial a été reformulée pour éviter une formulation historiquement ambiguë.

## Tests
```bash
gradle test
```
Tests unitaires fournis pour quelques refus de requêtes. À compléter par tests d'intégration PostgreSQL/Testcontainers, tests de bout en bout et sécurité avant mise en production.
