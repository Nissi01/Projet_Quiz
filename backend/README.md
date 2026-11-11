# StudyQuiz Backend — Java / Spring Boot / PostgreSQL

API REST de StudyQuiz : comptes utilisateurs avec authentification JWT, quiz (import CSV, sélection de questions, correction côté serveur, historique des notes), création de questions et forum.

## Prérequis
- **Java 25** (JDK)
- **Gradle 9.1+** (installe Gradle ou génère-le avec `gradle wrapper`)
- **Docker Desktop** (pour PostgreSQL)

## Configuration des secrets
Deux variables sont **obligatoires** et n'ont **aucune valeur par défaut** : l'application refuse de démarrer sans elles.

| Variable | Règle | Rôle |
|---|---|---|
| `JWT_SECRET` | au moins 32 caractères | clé de signature des tokens JWT |
| `ADMIN_PASSWORD` | au moins 12 caractères | mot de passe du compte admin créé au premier démarrage |

En développement, elles sont lues dans `backend/.env` (fichier ignoré par git) :
```bash
cp .env.example .env
# puis remplacer les valeurs, par exemple :
openssl rand -base64 48   # pour JWT_SECRET
```
En production, définis-les directement comme variables d'environnement (elles sont prioritaires sur `.env`).

Variables optionnelles : `ADMIN_USERNAME` (`admin`), `ADMIN_EMAIL` (`admin@studyquiz.local`), `JWT_EXPIRATION` (`PT24H`), `DB_URL`, `DB_USER`, `DB_PASSWORD`, `CORS_ORIGIN` (`http://localhost:5173`), `IMPORT_SEED` (`true`).

## Lancer
```bash
cd backend
docker compose up -d
gradle bootRun
```
L'API écoute sur `http://localhost:8080`. Au premier démarrage, Flyway crée le schéma, les questions de `questions.csv` sont importées et le compte admin est créé.

## Authentification
1. Créer un compte : `POST /api/users`
2. Se connecter : `POST /api/auth/login` → renvoie un `token`
3. Envoyer le token sur chaque requête : `Authorization: Bearer <token>`

Le token expire après `JWT_EXPIRATION` (24 h par défaut). Il est aussi **révoqué côté serveur** :
- à la déconnexion (`POST /api/auth/logout`) : tous les tokens de l'utilisateur deviennent invalides, sur tous ses appareils ;
- quand un admin change le rôle d'un utilisateur : l'utilisateur doit se reconnecter.

Les erreurs sont renvoyées en JSON `{"error":"..."}` : `401` non connecté / token invalide, `403` accès refusé, `404` introuvable, `409` conflit (doublon).

## API utilisateurs
### `POST /api/users` — créer un compte (public)
```json
{"username":"alice","email":"alice@mail.fr","password":"motdepasse"}
```
Mot de passe : 8 à 72 caractères. Réponse `201` :
```json
{"id":2,"username":"alice","role":"user","isPublic":true,"createdAt":"..."}
```

### `POST /api/auth/login` — se connecter (public)
`login` accepte le nom d'utilisateur **ou** l'email.
```json
{"login":"alice","password":"motdepasse"}
```
Réponse :
```json
{"token":"eyJ...","expiresAt":"...","user":{"id":2,"username":"alice","role":"user","isPublic":true,"createdAt":"..."}}
```

### `POST /api/auth/logout` — se déconnecter (connecté)
Réponse `204`. Révoque tous les tokens de l'utilisateur.

### `GET /api/users/me` — mon profil (connecté)
### `GET /api/users/{id}` — profil d'un utilisateur (public)
Un profil privé renvoie `403`, sauf pour son propriétaire ou un admin.
```json
{
  "user":{"id":2,"username":"alice","role":"user","isPublic":true,"createdAt":"..."},
  "createdQuestions":[{"id":12,"subject":"SQL","difficulty":"facile","prompt":"..."}],
  "quizResults":[{"sessionId":"<uuid>","score":8,"total":10,"completedAt":"..."}]
}
```

### `PATCH /api/users/me/visibility` — profil public / privé (connecté)
```json
{"isPublic":false}
```

### `PATCH /api/users/{id}/role` — changer un rôle (admin)
```json
{"role":"admin"}
```

## API quiz
### `GET /api/subjects` (public)
```json
[{"id":1,"name":"Histoire de l'informatique","filiere":"Général"}]
```

### `POST /api/quizzes` — démarrer un quiz (public)
```json
{"subjectId":1,"count":2}
```
`subjectId: null` mélange toutes les matières. `count` : 1 à 50. Si un token est envoyé, la session est liée à l'utilisateur et sa note apparaîtra dans son profil ; sinon le quiz est anonyme.

Réponse `201` :
```json
{"sessionId":"<uuid>","questions":[{"id":1,"subject":"Histoire de l'informatique","difficulty":"bebe","prompt":"...","options":[{"code":"A","label":"Ada Lovelace"}]}]}
```
La **bonne réponse et l'explication ne sont pas envoyées** à cette étape.

### `POST /api/quizzes/{sessionId}/submit` — soumettre les réponses (public)
```json
{"answers":[{"questionId":1,"optionCode":"A"},{"questionId":2,"optionCode":"C"}]}
```
Réponse :
```json
{"sessionId":"...","score":2,"total":2,"corrections":[{"questionId":1,"selectedCode":"A","correctCode":"A","correct":true,"explanation":"..."}]}
```
Une session ne peut être soumise qu'une fois, avec une réponse pour chaque question. Une session liée à un compte ne peut être soumise que par ce compte.

### `POST /api/questions` — créer une question (connecté)
```json
{"subjectId":1,"difficulty":"normal","prompt":"...","explanation":"...","options":["Rép. A","Rép. B","Rép. C","Rép. D"],"correctCode":"B"}
```
`options` contient les 4 réponses dans l'ordre A, B, C, D. `difficulty` : `bebe` (défaut), `facile`, `normal`, `difficile`.

## API forum
| Méthode | Route | Accès | Corps |
|---|---|---|---|
| `GET` | `/api/forum/threads?page=0` | public | |
| `GET` | `/api/forum/threads/{id}` | public | |
| `GET` | `/api/forum/threads/{id}/posts?page=0` | public | |
| `POST` | `/api/forum/threads` | connecté | `{"title":"...","body":"..."}` |
| `PUT` | `/api/forum/threads/{id}` | auteur ou admin | `{"title":"...","body":"..."}` |
| `DELETE` | `/api/forum/threads/{id}` | auteur ou admin | |
| `POST` | `/api/forum/threads/{id}/posts` | connecté | `{"body":"..."}` |

L'auteur est déduit du token : les réponses contiennent `userId` et `authorName`.

## Ajouter des questions par CSV
Modifier `src/main/resources/questions.csv` (UTF-8, séparateur `;`), puis redémarrer. Colonnes : `Matiere;Question;Option_A;Option_B;Option_C;Option_D;Reponse_Correcte;Explication`, plus une colonne `Filiere` facultative (sinon la matière est rangée dans la filière « Général »). Une question déjà présente (même matière + même énoncé) n'est pas mise à jour.

En production : `IMPORT_SEED=false` pour désactiver l'import au démarrage.

## Structure
```text
backend/
├── .env.example          # modèle des variables d'environnement
├── build.gradle          # dépendances
├── compose.yaml          # PostgreSQL pour le développement
└── src/main/
    ├── java/com/studyquiz/
    │   ├── common/       # format des erreurs HTTP
    │   ├── config/       # sécurité (JWT), CORS
    │   ├── user/         # comptes, login, admin, profils
    │   ├── quiz/         # filières, matières, questions, sessions, correction, import CSV
    │   └── forum/        # discussions et réponses
    └── resources/
        ├── application.yml
        ├── questions.csv
        └── db/migration/V1__schema.sql
```

## Tests
```bash
gradle test
```
Tests unitaires : refus de requêtes de quiz invalides, émission / vérification / révocation des JWT. Restent à ajouter : tests d'intégration avec PostgreSQL (Testcontainers) et tests de bout en bout.

## Limites connues
- Pas de limite de tentatives de connexion (anti brute-force) ni de vérification d'email.
- La déconnexion révoque les tokens de **tous** les appareils de l'utilisateur.
- Pas de modération du forum au-delà de la suppression par l'auteur ou un admin ; pas de modification ou suppression de posts.
- L'import CSV au démarrage n'est pas prévu pour plusieurs instances lancées en même temps.
- La sélection aléatoire des questions se fait en mémoire : suffisant pour un catalogue modeste.
