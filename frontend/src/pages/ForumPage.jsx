import { useEffect, useState } from "react";
import {
  getThreads, getThread, createThread, deleteThread, getPosts, createPost,
} from "../services/forumService";

// Spring renvoie souvent une Page : { content: [...] }
const liste = (d) => d?.content ?? d ?? [];
// A ADAPTER avec ForumController (ThreadView / PostView)
const auteur = (x) => x.authorName ?? x.author?.username ?? x.author ?? "";

function ForumPage() {
  const [threads, setThreads] = useState([]);
  const [selected, setSelected] = useState(null);
  const [posts, setPosts] = useState([]);
  const [titre, setTitre] = useState("");
  const [texte, setTexte] = useState("");
  const [reponse, setReponse] = useState("");
  const [error, setError] = useState("");

  async function chargerThreads() {
    try { setThreads(liste(await getThreads(0))); }
    catch (e) { setError(e.message); }
  }

  useEffect(() => { chargerThreads(); }, []);

  async function ouvrir(id) {
    try {
      setSelected(await getThread(id));
      setPosts(liste(await getPosts(id, 0)));
    } catch (e) { setError(e.message); }
  }

  async function nouveauThread(e) {
    e.preventDefault();
    try {
      await createThread(titre, texte);
      setTitre(""); setTexte("");
      chargerThreads();
    } catch (err) { setError(err.message); }
  }

  async function repondre(e) {
    e.preventDefault();
    try {
      await createPost(selected.id, reponse);
      setReponse("");
      setPosts(liste(await getPosts(selected.id, 0)));
    } catch (err) { setError(err.message); }
  }

  async function supprimer(id) {
    try {
      await deleteThread(id);
      setSelected(null);
      chargerThreads();
    } catch (err) { setError("Suppression refusée : " + err.message); }
  }

  if (selected) {
    return (
      <div>
        <button onClick={() => setSelected(null)}>← Retour au forum</button>
        <h1>{selected.title}</h1>
        <p>{selected.body}</p>
        <small>{auteur(selected)}</small>
        <button onClick={() => supprimer(selected.id)}>Supprimer</button>

        <h2>Réponses</h2>
        {posts.length === 0 && <p>Aucune réponse pour le moment.</p>}
        {posts.map((p) => (
          <div key={p.id} className="carte">
            <p>{p.body}</p>
            <small>{auteur(p)}</small>
          </div>
        ))}

        <form className="carte" onSubmit={repondre}>
          <textarea placeholder="Ta réponse" value={reponse}
                    onChange={(e) => setReponse(e.target.value)} required />
          <button type="submit">Répondre</button>
        </form>
        {error && <p className="erreur">{error}</p>}
      </div>
    );
  }

  return (
    <div>
      <h1>Forum</h1>
      {error && <p className="erreur">{error}</p>}

      <form className="carte" onSubmit={nouveauThread}>
        <input placeholder="Titre" value={titre}
               onChange={(e) => setTitre(e.target.value)} required />
        <textarea placeholder="Ta question ou ton message" value={texte}
                  onChange={(e) => setTexte(e.target.value)} required />
        <button type="submit">Publier</button>
      </form>

      {threads.length === 0 && <p>Aucune discussion. Lance la première.</p>}
      {threads.map((t) => (
        <div key={t.id} className="carte clic" onClick={() => ouvrir(t.id)}>
          <strong>{t.title}</strong>
          <small>{auteur(t)}</small>
        </div>
      ))}
    </div>
  );
}

export default ForumPage;