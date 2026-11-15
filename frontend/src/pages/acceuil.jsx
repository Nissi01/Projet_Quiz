import { useState } from "react";

// Questions de démonstration : "bonne" = indice de la bonne réponse (0 à 3)
const QUESTIONS = [
  {
    texte: "Qui est considérée comme la première programmeuse de l'histoire ?",
    options: ["Grace Hopper", "Ada Lovelace", "Margaret Hamilton", "Hedy Lamarr"],
    bonne: 1,
  },
  {
    texte: "Quel était le nom du réseau précurseur d'Internet ?",
    options: ["ARPANET", "Minitel", "Ethernet", "USENET"],
    bonne: 0,
  },
  {
    texte: "Quel langage a été créé par Dennis Ritchie ?",
    options: ["Java", "C", "Pascal", "Python"],
    bonne: 1,
  },
  {
    texte: "En quelle année Tim Berners-Lee a-t-il proposé le World Wide Web ?",
    options: ["1969", "1983", "1989", "1995"],
    bonne: 2,
  },
  {
    texte: "Qui a créé le noyau Linux en 1991 ?",
    options: ["Richard Stallman", "Linus Torvalds", "Bill Gates", "Steve Jobs"],
    bonne: 1,
  },
];

function Accueil() {
  const [index, setIndex] = useState(0);      // n° de la question
  const [choix, setChoix] = useState(null);    // réponse cliquée (null = pas encore répondu)
  const [score, setScore] = useState(0);
  const [fini, setFini] = useState(false);

  const question = QUESTIONS[index];
  const aRepondu = choix !== null;

  function repondre(i) {
    if (aRepondu) return; // on ne peut répondre qu'une fois
    setChoix(i);
    if (i === question.bonne) setScore(score + 1);
  }

  function suivante() {
    if (index === QUESTIONS.length - 1) {
      setFini(true);
    } else {
      setIndex(index + 1);
      setChoix(null);
    }
  }

  function recommencer() {
    setIndex(0);
    setChoix(null);
    setScore(0);
    setFini(false);
  }

  if (fini) {
    return (
      <div className="qcm">
        <h2>Ton score</h2>
        <p className="score centre">{score} / {QUESTIONS.length}</p>
        <div className="suite">
          <button onClick={recommencer}>Recommencer</button>
        </div>
      </div>
    );
  }

  return (
    <div className="qcm">
      <p className="progression">Question {index + 1} / {QUESTIONS.length}</p>
      <h2>{question.texte}</h2>

      <div className="reponses">
        {question.options.map((option, i) => {
          let classe = "reponse";
          if (aRepondu && i === question.bonne) classe += " bonne";
          else if (aRepondu && i === choix) classe += " mauvaise";
          else if (aRepondu) classe += " estompee";

          return (
            <button key={i} className={classe} onClick={() => repondre(i)}>
              <span className="numero">{i + 1}</span>
              {option}
            </button>
          );
        })}
      </div>

      {aRepondu && (
        <div className="suite">
          <button onClick={suivante}>
            {index === QUESTIONS.length - 1 ? "Voir mon score" : "Question suivante"}
          </button>
        </div>
      )}
    </div>
  );
}

export default Accueil;