import { useState } from "react";
import { createQuiz } from "../services/quizService";

const CODES = ["A", "B", "C", "D"];
const NIVEAUX = [
  ["bebe", "Débutant"],
  ["facile", "Facile"],
  ["normal", "Normal"],
  ["difficile", "Difficile"],
];
const NOUVELLE = "nouvelle";

let compteur = 0;
function questionVide() {
  compteur += 1;
  return { cle: compteur, prompt: "", options: ["", "", "", ""], correctCode: "A", explanation: "" };
}

export default function CreerQuiz({ subjects, onRetour, onCree }) {
  const [matiere, setMatiere] = useState(subjects[0] ? String(subjects[0].id) : NOUVELLE);
  const [nomMatiere, setNomMatiere] = useState("");
  const [filiere, setFiliere] = useState("");
  const [difficulty, setDifficulty] = useState("normal");
  const [questions, setQuestions] = useState(() => [questionVide()]);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const filieres = [...new Set(subjects.map((s) => s.filiere))];

  function modifier(cle, champ, valeur) {
    setQuestions(questions.map((q) => (q.cle === cle ? { ...q, [champ]: valeur } : q)));
  }

  function modifierOption(cle, i, valeur) {
    setQuestions(
      questions.map((q) =>
        q.cle === cle ? { ...q, options: q.options.map((o, j) => (j === i ? valeur : o)) } : q
      )
    );
  }

  function ajouter() {
    setQuestions([...questions, questionVide()]);
  }

  function retirer(cle) {
    setQuestions(questions.filter((q) => q.cle !== cle));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setMessage("");
    setLoading(true);
    const nouvelle = matiere === NOUVELLE;
    try {
      const r = await createQuiz({
        subjectId: nouvelle ? null : Number(matiere),
        subjectName: nouvelle ? nomMatiere : null,
        filiere: nouvelle ? filiere : null,
        difficulty,
        questions: questions.map(({ prompt, explanation, options, correctCode }) => ({
          prompt,
          explanation,
          options,
          correctCode,
        })),
      });
      setQuestions([questionVide()]);
      if (nouvelle) {
        setNomMatiere("");
        setFiliere("");
        setMatiere(String(r.subjectId));
      }
      setMessage(`Quiz publié : ${r.created} question${r.created > 1 ? "s" : ""} ajoutée${r.created > 1 ? "s" : ""} à « ${r.subject} ».`);
      onCree();
    } catch (err) {
      setError(
        err.message.includes("already exists")
          ? "Une de tes questions existe déjà pour cette matière (ou elle est en double dans le quiz)."
          : "Publication impossible : " + err.message
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <form className="carte" onSubmit={handleSubmit}>
      <h1>Créer un quiz</h1>

      <select value={matiere} onChange={(e) => setMatiere(e.target.value)}>
        {subjects.map((s) => (
          <option key={s.id} value={s.id}>{s.name} ({s.filiere})</option>
        ))}
        <option value={NOUVELLE}>+ Nouvelle matière</option>
      </select>

      {matiere === NOUVELLE && (
        <>
          <input placeholder="Nom de la matière" value={nomMatiere}
                 onChange={(e) => setNomMatiere(e.target.value)} required />
          <input placeholder="Filière (ex : Informatique)" list="filieres" value={filiere}
                 onChange={(e) => setFiliere(e.target.value)} />
          <datalist id="filieres">
            {filieres.map((f) => <option key={f} value={f} />)}
          </datalist>
        </>
      )}

      <select value={difficulty} onChange={(e) => setDifficulty(e.target.value)}>
        {NIVEAUX.map(([valeur, nom]) => (
          <option key={valeur} value={valeur}>{nom}</option>
        ))}
      </select>

      {questions.map((q, n) => (
        <div key={q.cle} className="carte">
          <strong>Question {n + 1}</strong>
          <textarea placeholder="Ta question" value={q.prompt}
                    onChange={(e) => modifier(q.cle, "prompt", e.target.value)} required />
          <small>Écris 4 réponses et coche la bonne.</small>
          {CODES.map((code, i) => (
            <label key={code} className="option-creation">
              <input type="radio" name={`bonne-${q.cle}`} checked={q.correctCode === code}
                     onChange={() => modifier(q.cle, "correctCode", code)} />
              <input placeholder={`Réponse ${i + 1}`} value={q.options[i]}
                     onChange={(e) => modifierOption(q.cle, i, e.target.value)} required />
            </label>
          ))}
          <textarea placeholder="Explication de la bonne réponse" value={q.explanation}
                    onChange={(e) => modifier(q.cle, "explanation", e.target.value)} required />
          {questions.length > 1 && (
            <button type="button" className="secondaire" onClick={() => retirer(q.cle)}>
              Retirer cette question
            </button>
          )}
        </div>
      ))}

      <button type="button" className="secondaire" onClick={ajouter} disabled={questions.length >= 50}>
        + Ajouter une question
      </button>

      {message && <p className="ok">{message}</p>}
      {error && <p className="erreur">{error}</p>}

      <div className="liste">
        <button type="submit" disabled={loading}>
          {loading ? "Publication..." : `Publier le quiz (${questions.length})`}
        </button>
        <button type="button" className="secondaire" onClick={onRetour}>Retour aux quiz</button>
      </div>
    </form>
  );
}
