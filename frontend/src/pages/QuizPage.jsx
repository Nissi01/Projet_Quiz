import { useEffect, useState } from "react";
import { getSubjects } from "../services/quizService";
import { demarrerQuizMulti, terminerQuizMulti } from "../services/quizMulti";
import CreerQuestion from "../components/CreerQuestion";
import "./QuizPage.css";

const MIN_QUESTIONS = 5;
const MAX_QUESTIONS = 15;
function QuizPage() {
  const [subjects, setSubjects] = useState([]);
  const [choisis, setChoisis] = useState([]);  
  const [quiz, setQuiz] = useState(null);   
  const [current, setCurrent] = useState(0);
  const [answers, setAnswers] = useState({});  
  const [result, setResult] = useState(null);
  const [finished, setFinished] = useState(false);
  const [error, setError] = useState("");
  const [creation, setCreation] = useState(false);
  const [chargement, setChargement] = useState(false);
  const [nbQuestions, setNbQuestions] = useState(MIN_QUESTIONS);

  useEffect(() => {
    getSubjects().then(setSubjects).catch((e) => setError(e.message));
  }, []);

 
  function basculer(id) {
    if (choisis.includes(id)) {
      setChoisis(choisis.filter((x) => x !== id));
    } else {
      setChoisis([...choisis, id]);
    }
  }

  async function commencer() {
    setError("");
    setChargement(true);
    try {
      const q = await demarrerQuizMulti(choisis, nbQuestions);
      setQuiz(q);
      setCurrent(0);
      setAnswers({});
      setResult(null);
      setFinished(false);
    } catch (e) {
      setError(
        e.message.includes("Not enough questions")
          ? "Pas assez de questions dans un des sujets choisis. Retires-en un ou choisis-en un autre."
          : "Impossible de démarrer le quiz : " + e.message
      );
    } finally {
      setChargement(false);
    }
  }

  function choisir(questionId, code) {
    setAnswers({ ...answers, [questionId]: code });
  }

  async function terminer() {
    setError("");
    setChargement(true);
    try {
      const r = await terminerQuizMulti(quiz, answers);
      setResult(r);
      setFinished(true);
    } catch (e) {
      setError("Envoi impossible : " + e.message);
    } finally {
      setChargement(false);
    }
  }

  function recommencer() {
    setQuiz(null);
    setResult(null);
    setFinished(false);
  }

  if (creation) {
    return <CreerQuestion subjects={subjects} onRetour={() => setCreation(false)} />;
  }

  // 1) Choix des sujets
  if (!quiz) {
    let resume;
    if (choisis.length === 0) resume = "tous les sujets";
    else if (choisis.length === 1) resume = "1 sujet";
    else resume = choisis.length + " sujets";

    return (
      <div className="qcm">
        <h2>Choisis un ou plusieurs sujets</h2>
        {error && <p className="erreur centre">{error}</p>}

        <div className="liste bulles">
          <button className={"bulle" + (choisis.length === 0 ? " choisie" : "")}
            onClick={() => setChoisis([])}>
            Tous les sujets
          </button>
          {subjects.map((s) => (
            <button key={s.id}
              className={"bulle" + (choisis.includes(s.id) ? " choisie" : "")}
              onClick={() => basculer(s.id)}>
              {s.name}{s.filiere !== s.name && ` (${s.filiere})`}
            </button>
          ))}
        </div>

        <div className="nombre-questions">
          <label htmlFor="nb">Nombre de questions : <strong>{nbQuestions}</strong></label>
          <input id="nb" type="range"
            min={MIN_QUESTIONS} max={MAX_QUESTIONS}
            value={nbQuestions}
            onChange={(e) => setNbQuestions(Number(e.target.value))} />
        </div>

        <div className="suite">
          <button onClick={commencer} disabled={chargement}>
            {chargement ? "Chargement..." : `Valider (${resume}, ${nbQuestions} questions)`}
          </button>
        </div>
      </div>
    );
  }

  // 3) Résultat
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
              <small>{q.subject}</small>
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

  // 2) Question en cours
  const question = quiz.questions[current];
  const choisie = answers[question.id];
  const derniere = current === quiz.questions.length - 1;
  const toutesRepondues = quiz.questions.every((q) => answers[q.id]);

  return (
    <div className="qcm">
      <p className="progression">
        Question {current + 1} / {quiz.questions.length} · {question.subject}
      </p>
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
          <button disabled={!toutesRepondues || chargement} onClick={terminer}>
            {chargement ? "Envoi..." : "Terminer"}
          </button>
        ) : (
          <button disabled={!choisie} onClick={() => setCurrent(current + 1)}>Suivant</button>
        )}
      </div>
    </div>
  );
}

export default QuizPage;