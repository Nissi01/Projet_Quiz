# Couizz — Quiz, l'appli de la réussite

Application web pour aider les étudiants à optimiser leur temps : transformer les moments du quotidien (transports, pauses, files d'attente) en courtes sessions de révision de ~10 minutes.

- **Backend** : Java 25, Spring Boot 3.5, PostgreSQL 17, Flyway, authentification JWT
- **Frontend** : React 19, Vite, react-router
- **Questions** : fichiers CSV du dossier `Quiz/`, importés au démarrage

## Fonctionnalités

- Plusieurs filières et matières, avec plusieurs quiz par matière
- Correction côté serveur avec explication de chaque réponse
- Comptes utilisateurs, historique des notes, profil public ou privé
- Création de questions personnalisées
- Forum collaboratif
- Mode invité (3 quiz maximum sans compte)

## Lancement rapide avec Docker (recommandé)

Prérequis : **Docker Desktop** uniquement.

```bash
# à la racine du projet
docker compose up --build
```

Puis ouvrir **http://localhost:5173**.

| Service | URL | Rôle |
|---|---|---|
| Frontend | http://localhost:5173 | site (nginx) |
| Backend | http://localhost:8080/api | API REST |
| PostgreSQL | interne à Docker | non exposé |

Au premier démarrage : Flyway crée le schéma, les questions du dossier `Quiz/` sont importées et le compte admin est créé.

- **Compte admin** : `admin` / `admin-dev-a-changer`
- **Arrêter** : `Ctrl+C`, ou `docker compose down`
- **Repartir de zéro** (efface la base) : `docker compose down -v`
- **Après modification du code** : relancer avec `--build`
- **Après modification des CSV** : `docker compose restart backend` (le dossier `Quiz/` est monté en lecture seule dans le conteneur, pas besoin de rebuild)

> Les secrets (`JWT_SECRET`, `ADMIN_PASSWORD`) du `compose.yaml` sont des valeurs de **développement**. À changer pour tout autre usage qu'un TP.

## Lancement en local (sans Docker pour l'appli)

Prérequis : Java 25, Gradle 9.1+, Node.js, Docker Desktop (pour PostgreSQL seulement).

**1. Backend** (premier terminal)
```bash
cd backend
cp .env.example .env      # puis renseigner JWT_SECRET (≥ 32 car.) et ADMIN_PASSWORD (≥ 12 car.)
docker compose up -d      # PostgreSQL seul
gradle bootRun
```
L'API écoute sur http://localhost:8080. Le message « 80 % EXECUTING » affiché par Gradle est normal : l'application tourne.

**2. Frontend** (second terminal)
```bash
cd frontend
npm install
npm run dev
```
Le site est servi sur http://localhost:5173.


## Configuration

Variables d'environnement du backend :

| Variable | Obligatoire | Défaut | Rôle |
|---|---|---|---|
| `JWT_SECRET` | oui | — | clé de signature des tokens (≥ 32 caractères) |
| `ADMIN_PASSWORD` | oui | — | mot de passe admin créé au 1er démarrage (≥ 12 caractères) |
| `DB_URL` | non | `jdbc:postgresql://localhost:5432/studyquiz` | connexion PostgreSQL |
| `QUIZ_DIR` | non | `../Quiz` | dossier des CSV de questions |
| `CORS_ORIGIN` | non | `http://localhost:5173` | origine autorisée pour le frontend |
| `IMPORT_SEED` | non | `true` | `false` désactive l'import CSV au démarrage |

Autres variables optionnelles : `ADMIN_USERNAME`, `ADMIN_EMAIL`, `JWT_EXPIRATION`, `DB_USER`, `DB_PASSWORD`. Avec Docker, elles se règlent dans la section `environment` du service `backend` de `compose.yaml`.

L'URL de l'API côté frontend est écrite en dur dans `frontend/src/services/api.js` (`http://localhost:8080/api`).

## Ajouter des questions (CSV)

Déposer un fichier `.csv` dans `Quiz/`, puis redémarrer le backend.

- **Colonnes** : `Matiere,Question,Option_A,Option_B,Option_C,Option_D,Reponse_Correcte,Explication`
- **Encodage** : UTF-8 (BOM accepté). Séparateur `,` ou `;` détecté automatiquement.
- **Filière** : nom du premier sous-dossier (`Quiz/Informatique/Code/...` → « Informatique »). À la racine de `Quiz/` → « Général ».
- **Difficulté** : suffixe du nom de fichier — `_f1`, `_f2`… → facile · `_d1`… → difficile · `_n1` → normal.
- Une question déjà présente (même matière + même énoncé) n'est pas mise à jour.

Exemple de nom : `Quiz/Maths/qcm_maths_d1.csv`.



## Structure du projet

```text
Projet_Quiz/
├── compose.yaml            # lance toute l'appli (Docker)
├── Quiz/                   # fichiers CSV de questions, par filière
├── backend/
│   ├── Dockerfile
│   ├── compose.yaml        # PostgreSQL seul (dev local)
│   ├── build.gradle
│   └── src/main/
│       ├── java/com/studyquiz/   # config, user, quiz, forum
│       └── resources/            # application.yml, migrations Flyway
└── frontend/
    ├── Dockerfile
    ├── nginx.conf
    └── src/
        ├── pages/          # Acceuil, Connexion, Inscription, QuizPage, ForumPage
        ├── components/     # Nav
        └── services/       # api, quiz, forum, user, invité
```

## Documentation de l'API

Toutes les routes (utilisateurs, authentification, quiz, questions, forum), avec exemples de requêtes et réponses, sont décrites dans [`backend/README.md`](backend/README.md).

