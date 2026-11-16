import { useState } from "react";
import { createQuestion } from "../services/quizService";

const CODES = ["A", "B", "C", "D"];
const NIVEAUX = [
  ["bebe", "Débutant"],
  ["facile", "Facile"],
  ["normal", "Normal"],
  ["difficile", "Difficile"],
];

export default function CreerQuestion({ subjects, onRetour }) {
  const [subjectId, setSubjectId] = useState(subjects[0]?.id ?? "");
  const [difficulty, setDifficulty] = useState("normal");
  const [prompt, setPrompt] = useState("");
  const [options, setOptions] = useState(["", "", "", ""]);
  const [correctCode, setCorrectCode] = useState("A");
  const [explanation, setExplanation] = useState("");
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function changerOption(i, valeur) {
    setOptions(options.map((o, j) => (j === i ? valeur : o)));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setMessage("");
    setLoading(true);
    try {
      await createQuestion({
        subjectId: Number(subjectId),
        difficulty,
        prompt,
        explanation,
        options,
        correctCode,
      });
      // on vide le formulaire pour pouvoir en ajouter une autre
      setPrompt("");
      setOptions(["", "", "", ""]);
      setCorrectCode("A");
      setExplanation("");
      setMessage("Question publiée. Les autres utilisateurs pourront la rencontrer dans les quiz.");
    } catch (err) {
      setError(
        err.message.includes("already exists")
          ? "Cette question existe déjà pour ce sujet."
          : "Publication impossible : " + err.message
      );
    } finally {
      setLoading(false);
    }
  }

  if (subjects.length === 0) {
    return (
      <div className="qcm">
        <p>Aucun sujet disponible pour le moment.</p>
        <button onClick={onRetour}>Retour</button>
      </div>
    );
  }

  return (
    <form className="carte" onSubmit={handleSubmit}>
      <h1>Ajouter une question</h1>

      <select value={subjectId} onChange={(e) => setSubjectId(e.target.value)}>
        {subjects.map((s) => (
          <option key={s.id} value={s.id}>{s.name} ({s.filiere})</option>
        ))}
      </select>

      <select value={difficulty} onChange={(e) => setDifficulty(e.target.value)}>
        {NIVEAUX.map(([valeur, nom]) => (
          <option key={valeur} value={valeur}>{nom}</option>
        ))}
      </select>

      <textarea placeholder="Ta question" value={prompt}
                onChange={(e) => setPrompt(e.target.value)} required />

      <small>Écris 4 réponses et coche la bonne.</small>
      {CODES.map((code, i) => (
        <label key={code} className="option-creation">
          <input type="radio" name="bonne" checked={correctCode === code}
                 onChange={() => setCorrectCode(code)} />
          <input placeholder={`Réponse ${i + 1}`} value={options[i]}
                 onChange={(e) => changerOption(i, e.target.value)} required />
        </label>
      ))}

      <textarea placeholder="Explication de la bonne réponse" value={explanation}
                onChange={(e) => setExplanation(e.target.value)} required />

      {message && <p className="ok">{message}</p>}
      {error && <p className="erreur">{error}</p>}

      <div className="liste">
        <button type="submit" disabled={loading}>
          {loading ? "Publication..." : "Publier la question"}
        </button>
        <button type="button" className="secondaire" onClick={onRetour}>Retour aux quiz</button>
      </div>
    </form>
  );
}