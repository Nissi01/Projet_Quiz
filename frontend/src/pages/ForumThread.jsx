import { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router";
import { getThread, getPosts, createPost, deleteThread } from "../services/forumService";
import { getMe } from "../services/userService";
import { liste, auteur, dateFr } from "./forumHelpers";

function ForumThread() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [thread, setThread] = useState(null);
  const [posts, setPosts] = useState([]);
  const [me, setMe] = useState(null);
  const [reponse, setReponse] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    Promise.all([getThread(id), getPosts(id, 0), getMe().catch(() => null)])
      .then(([t, p, moi]) => { setThread(t); setPosts(liste(p)); setMe(moi); })
      .catch((e) => setError(e.message));
  }, [id]);

  
  const estAdmin = String(me?.role ?? "").toLowerCase().includes("admin");
  const peutSupprimer = me && thread && (me.id === thread.userId || estAdmin);

  async function repondre(e) {
    e.preventDefault();
    setError("");
    try {
      await createPost(id, reponse);
      setReponse("");
      setPosts(liste(await getPosts(id, 0)));
    } catch (err) { setError(err.message); }
  }

  async function supprimer() {
    if (!window.confirm("Supprimer cette discussion ?")) return;
    try {
      await deleteThread(id);
      navigate("/forum");
    } catch (err) { setError("Suppression refusée : " + err.message); }
  }

  if (error && !thread) return <p className="erreur">{error}</p>;
  if (!thread) return <p>Chargement...</p>;

  return (
    <div>
      <Link to="/forum">← Retour au forum</Link>
      <h1>{thread.title}</h1>
      <div className="carte">
        <p>{thread.body}</p>
        <small>{auteur(thread)} · {dateFr(thread.createdAt)}</small>
        {peutSupprimer && (
          <div><button className="secondaire" onClick={supprimer}>Supprimer</button></div>
        )}
      </div>

      <h2>{posts.length} réponse{posts.length > 1 ? "s" : ""}</h2>
      {posts.map((p) => (
        <div key={p.id} className="carte">
          <p>{p.body}</p>
          <small>{auteur(p)} · {dateFr(p.createdAt)}</small>
        </div>
      ))}

      <form className="carte" onSubmit={repondre}>
        <h2>Ta réponse</h2>
        <textarea value={reponse} onChange={(e) => setReponse(e.target.value)} required />
        {error && <p className="erreur">{error}</p>}
        <div><button type="submit">Publier ma réponse</button></div>
      </form>
    </div>
  );
}

export default ForumThread;