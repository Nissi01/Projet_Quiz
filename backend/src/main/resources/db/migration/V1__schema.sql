CREATE TABLE subjects (
  id BIGSERIAL PRIMARY KEY,
  name VARCHAR(160) NOT NULL UNIQUE
);
CREATE TABLE questions (
  id BIGSERIAL PRIMARY KEY,
  subject_id BIGINT NOT NULL REFERENCES subjects(id),
  prompt TEXT NOT NULL,
  explanation TEXT NOT NULL,
  CONSTRAINT uq_question_subject UNIQUE(subject_id, prompt)
);
CREATE TABLE question_options (
  id BIGSERIAL PRIMARY KEY,
  question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
  code VARCHAR(1) NOT NULL CHECK (code IN ('A','B','C','D')),
  label TEXT NOT NULL,
  correct BOOLEAN NOT NULL,
  CONSTRAINT uq_option_code UNIQUE(question_id, code)
);
CREATE UNIQUE INDEX ux_one_correct_per_question ON question_options(question_id) WHERE correct;
CREATE INDEX idx_questions_subject ON questions(subject_id);
CREATE TABLE quiz_sessions (
  id UUID PRIMARY KEY,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at TIMESTAMPTZ,
  score INTEGER,
  total INTEGER NOT NULL
);
CREATE TABLE quiz_session_questions (
  session_id UUID NOT NULL REFERENCES quiz_sessions(id) ON DELETE CASCADE,
  question_id BIGINT NOT NULL REFERENCES questions(id),
  position INTEGER NOT NULL,
  selected_code VARCHAR(1) CHECK (selected_code IN ('A','B','C','D')),
  PRIMARY KEY (session_id, question_id),
  UNIQUE(session_id, position)
);
CREATE TABLE forum_threads (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  body TEXT NOT NULL,
  author_name VARCHAR(80) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE forum_posts (
  id BIGSERIAL PRIMARY KEY,
  thread_id BIGINT NOT NULL REFERENCES forum_threads(id) ON DELETE CASCADE,
  body TEXT NOT NULL,
  author_name VARCHAR(80) NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_forum_posts_thread ON forum_posts(thread_id, created_at);
