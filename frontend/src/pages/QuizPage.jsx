import { useEffect, useState } from "react";
import { getSubjects, startQuiz, submitQuiz } from "../services/quizService";
import CreerQuestion from "../components/CreerQuestion";

const NB_QUESTIONS = 5;

function QuizPage() {
  const [subjects, setSubjects] = useState([]);
  const [quiz, setQuiz] = useState(null);      // { sessionId, questions }
  const [current, setCurrent] = useState(0);
  const [answers, setAnswers] = useState({});  // { [questionId]: "A" | "B" | "C" | "D" }
  const [result, setResult] = useState(null);
  const [finished, setFinished] = useState(false);
  const [error, setError] = useState("");
  const [creation, setCreation] = useState(false);

  useEffect(() => {
    getSubjects().then(setSubjects).catch((e) => setError(e.message));
  }, []);

  async function commencer(subjectId) {
    setError("");
    try {
      const q = await startQuiz({ subjectId, count: NB_QUESTIONS });
      setQuiz(q);
      setCurrent(0);
      setAnswers({});
      setResult(null);
      setFinished(false);
    } catch (e) {
      setError(
        e.message.includes("Not enough questions")
          ? "Pas assez de questions pour ce sujet. Choisis-en un autre."
          : "Impossible de démarrer le quiz : " + e.message
      );
    }
  }

  function choisir(questionId, code) {
    setAnswers({ ...answers, [questionId]: code });
  }

  async function terminer() {
    setError("");
    try {
      const r = await submitQuiz(quiz.sessionId, {
        answers: quiz.questions.map((q) => ({ questionId: q.id, optionCode: answers[q.id] })),
      });
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

  // 1. Choix du sujet
  if (creation) {
    return <CreerQuestion subjects={subjects} onRetour={() => setCreation(false)} />;
  }
  if (!quiz) {
    return (
      <div className="qcm">
        <h2>Choisis un sujet</h2>
        {error && <p className="erreur centre">{error}</p>}
        <div className="liste" style={{ justifyContent: "center" }}>
        <div className="suite">
          <button className="secondaire" onClick={() => setCreation(true)}>
            Créer mon quiz
          </button>
        </div>
          <button onClick={() => commencer(null)}>Tous les sujets</button>
          {subjects.map((s) => (
            <button key={s.id} onClick={() => commencer(s.id)}>
              {s.name} ({s.filiere})
            </button>
          ))}
        </div>
      </div>
    );
  }

  // 3. Résultat
  if (finished) {
    const parId = Object.fromEntries(quiz.questions.map((q) => [q.id, q]));
    return (
      <div className="qcm">
        <h2>Ton score</h2>
        <p className="score centre">{result.score} / {result.total}</p>
        {result.corrections.map((c) => {
          const q = parId[c.questionId];
          const label = (code) => q.options.find((o) => o.code === code)?.label;
          return (
            <div key={c.questionId} className="carte">
              <strong>{q.prompt}</strong>
              {c.correct ? (
                <p className="ok">Bonne réponse : {label(c.selectedCode)}</p>
              ) : (
                <>
                  <p className="erreur">Ta réponse : {label(c.selectedCode)}</p>
                  <p className="ok">Bonne réponse : {label(c.correctCode)}</p>
                </>
              )}
              <small>{c.explanation}</small>
            </div>
          );
        })}
        <div className="suite">
          <button onClick={recommencer}>Refaire un quiz</button>
        </div>
      </div>
    );
  }

  // 2. Question en cours
  const question = quiz.questions[current];
  const choisie = answers[question.id];
  const derniere = current === quiz.questions.length - 1;
  const toutesRepondues = quiz.questions.every((q) => answers[q.id]);

  return (
    <div className="qcm">
      <p className="progression">Question {current + 1} / {quiz.questions.length}</p>
      <h2>{question.prompt}</h2>

      <div className="reponses">
        {question.options.map((o, i) => (
          <button key={o.code}
                  className={"reponse" + (choisie === o.code ? " choisie" : "")}
                  onClick={() => choisir(question.id, o.code)}>
            <span className="numero">{i + 1}</span>
            {o.label}
          </button>
        ))}
      </div>

      {error && <p className="erreur centre">{error}</p>}

      <div className="suite">
        <button disabled={current === 0} onClick={() => setCurrent(current - 1)}>Précédent</button>
        {derniere ? (
          <button disabled={!toutesRepondues} onClick={terminer}>Terminer</button>
        ) : (
          <button disabled={!choisie} onClick={() => setCurrent(current + 1)}>Suivant</button>
        )}
      </div>
    </div>
  );
}

export default QuizPage;