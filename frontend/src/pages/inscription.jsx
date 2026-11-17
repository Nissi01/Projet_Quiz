import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { register } from "../services/userService.js";

// Traduction des messages d'erreur de l'API
const MESSAGES = {
  "Username already taken": "Ce pseudo est déjà utilisé.",
  "Email already used": "Cet email est déjà utilisé.",
  "Invalid request body": "Un des champs n'est pas valide.",
};

// Envoie le formulaire à l'API
async function envoyer(evenement, pseudo, email, motDePasse, setErreur, setChargement, naviguer) {
  evenement.preventDefault();
  setErreur("");
  setChargement(true);

  try {
    await register({ username: pseudo, email: email, password: motDePasse });
    naviguer("/connexion");
  } catch (e) {
    if (e.message === "Failed to fetch") {
      setErreur("Impossible de joindre le serveur.");
    } else {
      setErreur(MESSAGES[e.message] || "Inscription impossible (" + e.message + ").");
    }
  } finally {
    setChargement(false);
  }
}

function Inscription() {
  const naviguer = useNavigate();
  const [pseudo, setPseudo] = useState("");
  const [email, setEmail] = useState("");
  const [motDePasse, setMotDePasse] = useState("");
  const [erreur, setErreur] = useState("");
  const [chargement, setChargement] = useState(false);

  return (
    <form className="carte"
      onSubmit={(e) => envoyer(e, pseudo, email, motDePasse, setErreur, setChargement, naviguer)}>
      <h1>Inscription</h1>

      <input placeholder="Pseudo" value={pseudo}
        onChange={(e) => setPseudo(e.target.value)}
        required minLength={3} maxLength={80} />
      <input type="email" placeholder="Email" value={email}
        onChange={(e) => setEmail(e.target.value)}
        required />
      <input type="password" placeholder="Mot de passe (8 caractères minimum)" value={motDePasse}
        onChange={(e) => setMotDePasse(e.target.value)}
        required minLength={8} maxLength={72} />

      {erreur && <p className="erreur">{erreur}</p>}

      <button type="submit" disabled={chargement}>
        {chargement ? "Création..." : "Créer mon compte"}
      </button>

      <p>Déjà un compte ? <Link to="/connexion">Se connecter</Link></p>
    </form>
  );
}

export default Inscription;