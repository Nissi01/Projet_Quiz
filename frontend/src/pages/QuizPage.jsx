import { useEffect, useState } from "react";
import { getSubjects, startQuiz, submitQuiz } from "../services/quizService";

/* ===== A ADAPTER avec QuizDtos.java (tout est regroupé ici) ===== */
const subjectLabel = (s) => s.name ?? s.title ?? s.label;
const questionText = (q) => q.text ?? q.statement ?? q.label;
const optionText = (o) => o.text ?? o.label;
const buildStartBody = (subjectId) => ({ subjectId, count: 5 });
const buildSubmitBody = (questions, answers) => ({
  answers: questions.map((q, i) => ({
    questionId: q.id,
    optionIds: answers[i] ?? [],
  })),
});
/* ================================================================ */

function QuizPage() {
  const [subjects, setSubjects] = useState([]);
  const [quiz, setQuiz] = useState(null);       // le quiz en cours
  const [current, setCurrent] = useState(0);    // n° de la question
  const [answers, setAnswers] = useState([]);   // réponses choisies par question
  const [result, setResult] = useState(null);   // résultat renvoyé par le backend
  const [finished, setFinished] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    getSubjects().then(setSubjects).catch((e) => setError(e.message));
  }, []);

  async function commencer(subjectId) {
    setError("");
    try {
      const q = await startQuiz(buildStartBody(subjectId));
      setQuiz(q);
      setCurrent(0);
      setAnswers([]);
      setResult(null);
      setFinished(false);
    } catch (e) {
      setError("Impossible de démarrer le quiz : " + e.message);
    }
  }

  function toggle(optionId) {
    setAnswers((prev) => {
      const copie = [...prev];
      const deja = copie[current] ?? [];
      copie[current] = deja.includes(optionId)
        ? deja.filter((id) => id !== optionId)
        : [...deja, optionId];
      return copie;
    });
  }

  async function terminer() {
    try {
      const r = await submitQuiz(quiz.id, buildSubmitBody(quiz.questions, answers));
      setResult(r);
      setFinished(true);
    } catch (e) {
      setError("Envoi impossible : " + e.message);
    }
  }

  function recommencer() {
    setQuiz(null);
    setResult(null);
    setFinished(false);
  }

  if (error && !quiz) {
    return <p className="erreur">{error}</p>;
  }

  // 1. Choix du sujet
  if (!quiz) {
    return (
      <div>
        <h1>Choisis un sujet</h1>
        {subjects.length === 0 && <p>Aucun sujet disponible.</p>}
        <div className="liste">
          {subjects.map((s) => (
            <button key={s.id} onClick={() => commencer(s.id)}>{subjectLabel(s)}</button>
          ))}
        </div>
      </div>
    );
  }

  // 3. Résultat
  if (finished) {
    const total = result?.total ?? quiz.questions.length;
    return (
      <div className="carte">
        <h1>Résultat</h1>
        <p className="score">{result?.score} / {total}</p>
        {result?.corrections && (
          <details>
            <summary>Voir les corrections</summary>
            <pre>{JSON.stringify(result.corrections, null, 2)}</pre>
          </details>
        )}
        <button onClick={recommencer}>Refaire un quiz</button>
      </div>
    );
  }

  // 2. Question en cours
  const question = quiz.questions[current];
  const choisies = answers[current] ?? [];
  const derniere = current === quiz.questions.length - 1;

  return (
    <div className="carte">
      <p>Question {current + 1} / {quiz.questions.length}</p>
      <h2>{questionText(question)}</h2>
      <div className="liste vertical">
        {question.options.map((o) => (
          <label key={o.id} className="option">
            <input type="checkbox" checked={choisies.includes(o.id)}
                   onChange={() => toggle(o.id)} />
            {optionText(o)}
          </label>
        ))}
      </div>
      {error && <p className="erreur">{error}</p>}
      <div className="liste">
        <button disabled={current === 0} onClick={() => setCurrent(current - 1)}>Précédent</button>
        {derniere
          ? <button onClick={terminer}>Terminer</button>
          : <button onClick={() => setCurrent(current + 1)}>Suivant</button>}
      </div>
    </div>
  );
}

export default QuizPage;