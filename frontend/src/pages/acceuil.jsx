import { useState, useEffect } from "react";
import { useNavigate } from "react-router";
import { getSubjects, startQuiz, submitQuiz } from "../services/quizService.js";
import { getRestants, UtiliserUnquizz } from "../services/invite.js";
import "./accueil.css";

const MIN_QUESTIONS = 5;
const MAX_QUESTIONS = 15;

const MESSAGES = {
  "Not enough questions for this selection": "Pas assez de questions dans cette matière.",
  "Subject not found": "Cette matière n'existe pas.",
};

function Accueil() {
  const naviguer = useNavigate();
  const connecte = localStorage.getItem("token");

  const [matieres, setMatieres] = useState([]);
  const [matiere, setMatiere] = useState("");
  const [nbQuestions, setNbQuestions] = useState(MIN_QUESTIONS);
  const [quiz, setQuiz] = useState(null);
  const [index, setIndex] = useState(0);
  const [reponses, setReponses] = useState({});
  const [resultat, setResultat] = useState(null);
  const [chargement, setChargement] = useState(false);
  const [erreur, setErreur] = useState("");

  // Au premier affichage de la page : on charge la liste des matières
  useEffect(() => {
    getSubjects()
      .then(setMatieres)
      .catch(() => setErreur("Impossible de charger les matières. Recharge la page."));
  }, []);

  const question = quiz ? quiz.questions[index] : null;

  // Démarre un quiz : l'API tire les questions au hasard
  async function demarrer() {
    if (!connecte && getRestants() <= 0) return;
    setErreur("");
    setChargement(true);
    try {
      const nouveau = await startQuiz({
        subjectId: matiere ? Number(matiere) : null,
        count: nbQuestions,
      });
      if (!connecte) UtiliserUnquizz();
      setQuiz(nouveau);
      setIndex(0);
      setReponses({});
      setResultat(null);
    } catch (e) {
      setErreur(MESSAGES[e.message] || "Impossible de lancer le quiz (" + e.message + ").");
    } finally {
      setChargement(false);
    }
  }

  function choisir(code) {
    if (reponses[question.id]) return;
    setReponses({ ...reponses, [question.id]: code });
  }

  async function suivante() {
    if (index < quiz.questions.length - 1) {
      setIndex(index + 1);
      return;
    }
    setErreur("");
    setChargement(true);
    try {
      const answers = quiz.questions.map((q) => ({ questionId: q.id, optionCode: reponses[q.id] }));
      const res = await submitQuiz(quiz.sessionId, { answers });
      setResultat(res);
    } catch (e) {
      setErreur("Impossible d'envoyer les réponses (" + e.message + ").");
    } finally {
      setChargement(false);
    }
  }

  if (!quiz) {
    const restants = getRestants();
    const peutJouer = connecte || restants > 0;

    return (
      <div className="qcm centre">
        <h2>Bienvenue dans Qouizz</h2>

        {connecte && <p className="progression">Autant de quiz que tu veux !</p>}
        {!connecte && restants > 0 && (
          <p className="progression">Sans compte : {restants} quiz restants</p>
        )}

        {peutJouer ? (
          <>
            <select className="choix-matiere" value={matiere}
              onChange={(e) => setMatiere(e.target.value)}>
              <option value="">Toutes les matières</option>
              {matieres.map((m) => (
                <option key={m.id} value={m.id}>{m.name}</option>
              ))}
            </select>

            <div className="nombre-questions">
              <label htmlFor="nb-accueil">Nombre de questions : <strong>{nbQuestions}</strong></label>
              <input id="nb-accueil" type="range"
                min={MIN_QUESTIONS} max={MAX_QUESTIONS}
                value={nbQuestions}
                onChange={(e) => setNbQuestions(Number(e.target.value))} />
            </div>

            <div className="suite">
              <button onClick={demarrer} disabled={chargement}>
                {chargement ? "Chargement..." : "Commencer le quiz"}
              </button>
            </div>
          </>
        ) : (
          <>
            <p>Vous avez deja essayez 3 fois le quizz il faut maintenant s'inscrire!</p>
            <div className="suite">
              <button onClick={() => naviguer("/inscription")}>Créer un compte</button>
            </div>
          </>
        )}

        {erreur && <p className="erreur">{erreur}</p>}
      </div>
    );
  }

  if (resultat) {
    return (
      <div className="qcm">
        <h2>Ton score</h2>
        <p className="score centre">{resultat.score} / {resultat.total}</p>

        {quiz.questions.map((q) => {
          const correction = resultat.corrections.find((c) => c.questionId === q.id);
          return (
            <div key={q.id} className="carte">
              <h2>{q.prompt}</h2>
              <div className="reponses">
                {q.options.map((o) => {
                  let classe = "reponse";
                  if (o.code === correction.correctCode) classe += " bonne";
                  else if (o.code === correction.selectedCode) classe += " mauvaise";
                  else classe += " estompee";
                  return (
                    <button key={o.code} className={classe}>
                      <span className="numero">{o.code}</span>
                      {o.label}
                    </button>
                  );
                })}
              </div>
              <p className="progression">{correction.explanation}</p>
            </div>
          );
        })}

        <div className="suite">
          <button onClick={() => setQuiz(null)}>Retour</button>
        </div>
      </div>
    );
  }

  const choisi = reponses[question.id];
  const derniere = index === quiz.questions.length - 1;

  return (
    <div className="qcm">
      <p className="progression">Question {index + 1} / {quiz.questions.length} · {question.subject}</p>
      <h2>{question.prompt}</h2>

      <div className="reponses">
        {question.options.map((o) => {
          let classe = "reponse";
          if (choisi === o.code) classe += " choisie";
          else if (choisi) classe += " estompee";
          return (
            <button key={o.code} className={classe} onClick={() => choisir(o.code)}>
              <span className="numero">{o.code}</span>
              {o.label}
            </button>
          );
        })}
      </div>

      {choisi && (
        <div className="suite">
          <button onClick={suivante} disabled={chargement}>
            {chargement ? "Envoi..." : derniere ? "Voir mon score" : "Question suivante"}
          </button>
        </div>
      )}

      {erreur && <p className="erreur centre">{erreur}</p>}
    </div>
  );
}

export default Accueil;