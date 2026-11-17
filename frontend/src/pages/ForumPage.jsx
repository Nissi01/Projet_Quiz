import { useEffect, useState } from "react";
import { Link } from "react-router";
import { getThreads } from "../services/forumService";
import { liste, auteur, dateFr, extrait } from "./forumHelpers";

function ForumPage() {
  const [threads, setThreads] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [error, setError] = useState("");

  useEffect(() => {
    getThreads(page)
      .then((data) => {
        setThreads(liste(data));
        setTotalPages(data?.totalPages ?? 1);
      })
      .catch((e) => setError(e.message));
  }, [page]);

  return (
    <div>
      <div className="forum-entete">
        <h1>Forum</h1>
        <Link to="/forum/nouveau" className="bouton">Poser une question</Link>
      </div>

      {error && <p className="erreur">{error}</p>}
      {!error && threads.length === 0 && <p>Aucune discussion pour le moment. Lance la première.</p>}

      {threads.map((t) => (
        <Link key={t.id} to={`/forum/${t.id}`} className="sujet">
          <div className="sujet-texte">
            <h2>{t.title}</h2>
            <p>{extrait(t.body)}</p>
            <small>{auteur(t)} {t.createdAt && `· ${dateFr(t.createdAt)}`}</small>
          </div>
        </Link>
      ))}

      {totalPages > 1 && (
        <div className="suite">
          <button className="secondaire" disabled={page === 0} onClick={() => setPage(page - 1)}>Précédent</button>
          <span className="pagination">Page {page + 1} / {totalPages}</span>
          <button className="secondaire" disabled={page + 1 >= totalPages} onClick={() => setPage(page + 1)}>Suivant</button>
        </div>
      )}
    </div>
  );
}

export default ForumPage;