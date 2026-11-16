import { useState } from "react";
import { useNavigate, Link } from "react-router";
import { createThread } from "../services/forumService";

function ForumNouveau() {
  const [titre, setTitre] = useState("");
  const [texte, setTexte] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const cree = await createThread(titre, texte);
      navigate(cree?.id ? `/forum/${cree.id}` : "/forum");
    } catch (err) {
      setError("Publication impossible : " + err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <form className="carte" onSubmit={handleSubmit}>
      <h1>Poser une question</h1>
      <input placeholder="Titre : résume ta question en une phrase" value={titre}
             onChange={(e) => setTitre(e.target.value)} maxLength={200} required />
      <textarea className="grand" placeholder="Détaille ta question" value={texte}
                onChange={(e) => setTexte(e.target.value)} required />
      {error && <p className="erreur">{error}</p>}
      <div className="liste">
        <button type="submit" disabled={loading}>{loading ? "Publication..." : "Publier"}</button>
        <Link to="/forum" className="bouton secondaire">Annuler</Link>
      </div>
    </form>
  );
}

export default ForumNouveau;