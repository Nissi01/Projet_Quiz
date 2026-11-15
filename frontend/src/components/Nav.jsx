import { useEffect, useState } from "react";
import { Link, useNavigate, useLocation } from "react-router";

function themeInitial() {
  const sauve = localStorage.getItem("theme");
  if (sauve) return sauve;
  return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
}

function Soleil() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
         strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <circle cx="12" cy="12" r="4" />
      <path d="M12 2v2M12 20v2M4.93 4.93l1.41 1.41M17.66 17.66l1.41 1.41M2 12h2M20 12h2M4.93 19.07l1.41-1.41M17.66 6.34l1.41-1.41" />
    </svg>
  );
}

function Lune() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor"
         strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
      <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
    </svg>
  );
}

export default function Nav() {
  const navigate = useNavigate();
  useLocation(); // force le re-render quand on change de page
  const connecte = !!localStorage.getItem("token");
  const [theme, setTheme] = useState(themeInitial);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    localStorage.setItem("theme", theme);
  }, [theme]);

  function deconnexion() {
    localStorage.removeItem("token");
    navigate("/");
  }

  const sombre = theme === "dark";

  return (
    <nav className="nav">
      <Link to="/">Accueil</Link>
      <Link to="/quiz">Quiz</Link>
      {connecte && <Link to="/forum">Forum</Link>}
      <span className="espace" />
      <button
        className="theme"
        onClick={() => setTheme(sombre ? "light" : "dark")}
        aria-label={sombre ? "Passer au thème clair" : "Passer au thème sombre"}
        title={sombre ? "Thème clair" : "Thème sombre"}
      >
        {sombre ? <Soleil /> : <Lune />}
      </button>
      {connecte ? (
        <button onClick={deconnexion}>Se déconnecter</button>
      ) : (
        <>
          <Link to="/connexion">Connexion</Link>
          <Link to="/inscription">Inscription</Link>
        </>
      )}
    </nav>
  );
}