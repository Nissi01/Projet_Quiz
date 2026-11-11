-- =============================================================================
-- Types
-- =============================================================================
CREATE TYPE user_role AS ENUM ('user', 'admin');
CREATE TYPE quiz_difficulty AS ENUM ('bebe', 'facile', 'normal', 'difficile');

-- =============================================================================
-- Utilisateurs
-- =============================================================================
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(80) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role user_role NOT NULL DEFAULT 'user',
  is_public BOOLEAN NOT NULL DEFAULT TRUE,
  token_version INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- =============================================================================
-- Quiz
-- =============================================================================
CREATE TABLE filieres (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(160) NOT NULL UNIQUE
);

CREATE TABLE subjects (
  id BIGSERIAL PRIMARY KEY,
  filiere_id BIGINT NOT NULL REFERENCES filieres(id),
  name VARCHAR(160) NOT NULL UNIQUE
);

CREATE TABLE questions (
  id BIGSERIAL PRIMARY KEY,
  subject_id BIGINT NOT NULL REFERENCES subjects(id),
  created_by BIGINT REFERENCES users(id) ON DELETE SET NULL,
  difficulty quiz_difficulty NOT NULL DEFAULT 'bebe',
  prompt TEXT NOT NULL,
  explanation TEXT NOT NULL,
  CONSTRAINT uq_question_subject_prompt UNIQUE (subject_id, prompt)
);

CREATE TABLE question_options (
  id BIGSERIAL PRIMARY KEY,
  question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
  code VARCHAR(1) NOT NULL CHECK (code IN ('A', 'B', 'C', 'D')),
  label TEXT NOT NULL,
  correct BOOLEAN NOT NULL,
  CONSTRAINT uq_option_question_code UNIQUE (question_id, code)
);

CREATE TABLE quiz_sessions (
  id UUID PRIMARY KEY,
  user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
  score INTEGER,
  total INTEGER NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at TIMESTAMPTZ
);

CREATE TABLE quiz_session_questions (
  session_id UUID NOT NULL REFERENCES quiz_sessions(id) ON DELETE CASCADE,
  question_id BIGINT NOT NULL REFERENCES questions(id),
  position INTEGER NOT NULL,
  selected_code VARCHAR(1) CHECK (selected_code IN ('A', 'B', 'C', 'D')),
  PRIMARY KEY (session_id, question_id),
  CONSTRAINT uq_session_position UNIQUE (session_id, position)
);

CREATE UNIQUE INDEX ux_one_correct_per_question ON question_options(question_id) WHERE correct;
CREATE INDEX idx_subjects_filiere ON subjects(filiere_id);
CREATE INDEX idx_questions_subject ON questions(subject_id);
CREATE INDEX idx_questions_created_by ON questions(created_by);
CREATE INDEX idx_quiz_sessions_user ON quiz_sessions(user_id);

-- =============================================================================
-- Forum
-- =============================================================================
CREATE TABLE forum_threads (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  title VARCHAR(200) NOT NULL,
  body TEXT NOT NULL,
  author_name VARCHAR(80) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE forum_posts (
  id BIGSERIAL PRIMARY KEY,
  thread_id BIGINT NOT NULL REFERENCES forum_threads(id) ON DELETE CASCADE,
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  body TEXT NOT NULL,
  author_name VARCHAR(80) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_forum_threads_user ON forum_threads(user_id);
CREATE INDEX idx_forum_posts_thread ON forum_posts(thread_id, created_at);
CREATE INDEX idx_forum_posts_user ON forum_posts(user_id);
