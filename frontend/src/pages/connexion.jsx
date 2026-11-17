import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { login } from "../services/userService.js";

// Envoie le formulaire à l'API
async function envoyer(evenement, identifiant, motDePasse, setErreur, setChargement, naviguer) {
  evenement.preventDefault();
  setErreur("");
  setChargement(true);

  try {
    await login(identifiant, motDePasse);
    naviguer("/");
  } catch (e) {
    if (e.message === "Failed to fetch") {
      setErreur("Impossible de joindre le serveur.");
    } else if (identifiant.includes("@")) {
      setErreur("Email ou mot de passe incorrect.");
    } else {
      setErreur("Pseudo ou mot de passe incorrect.");
    }
  } finally {
    setChargement(false);
  }
}

function Connexion() {
  const naviguer = useNavigate();
  const [identifiant, setIdentifiant] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [erreur, setErreur] = useState("");
  const [chargement, setChargement] = useState(false);

  return (
    <form className="carte"
      onSubmit={(e) => envoyer(e, identifiant, motDePasse, setErreur, setChargement, naviguer)}>
      <h1>Connexion</h1>

      <input type="text" placeholder="Pseudo ou email" value={identifiant}
        onChange={(e) => setIdentifiant(e.target.value)}
        required />
      <input type="password" placeholder="Mot de passe" value={motDePasse}
        onChange={(e) => setMotDePasse(e.target.value)}
        required />

      {erreur && <p className="erreur">{erreur}</p>}

      <button type="submit" disabled={chargement}>
        {chargement ? "Connexion..." : "Se connecter"}
      </button>

      <p>Pas encore de compte ? <Link to="/inscription">Créer un compte</Link></p>
    </form>
  );
}

export default Connexion;