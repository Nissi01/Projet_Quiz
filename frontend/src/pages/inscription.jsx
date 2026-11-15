import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { register } from "../services/userService.js";


const MESSAGES = {
    "Username already taken": "Ce pseudo est déjà utilisé.",
    "Email already used": "Cet email est déjà utilisé.",
    "Invalid request body": "Un des champs n'est pas valide.",
};

async function envoyer(evenement, pseudo, email, motDePasse, setErreur, naviguer) {
    evenement.preventDefault();
    setErreur("");
    try {
        await register({ username: pseudo, email: email, password: motDePasse });
        naviguer("/connexion");
    } catch (e) {
        if (e.message === "Failed to fetch") {
            setErreur("Impossible de joindre le serveur.");
        } else {
            setErreur(MESSAGES[e.message] || "Inscription impossible (" + e.message + ").");
        }
    }
}

function Inscription() {
    const naviguer = useNavigate();
    const [pseudo, setPseudo] = useState("");
    const [email, setEmail] = useState("");
    const [motDePasse, setMotDePasse] = useState("");
    const [erreur, setErreur] = useState("");

    return (
        <div>
            <h1>Inscription</h1>

            <form onSubmit={(e) => envoyer(e, pseudo, email, motDePasse, setErreur, naviguer)}>
                <input placeholder="Pseudo" value={pseudo}
                    onChange={(e) => setPseudo(e.target.value)}
                    required minLength={3} maxLength={80} />
                <br />
                <input type="email" placeholder="Email" value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required />
                <br />
                <input type="password" placeholder="Mot de passe (8 caractères minimum)" value={motDePasse}
                    onChange={(e) => setMotDePasse(e.target.value)}
                    required minLength={8} maxLength={72} />
                <br />
                <button type="submit">Créer mon compte</button>
            </form>
            {erreur && <p style={{ color: "red" }}>{erreur}</p>}

            <p>Déjà un compte ? <Link to="/connexion">Se connecter</Link></p>
            <p><Link to="/">Retour à l'accueil</Link></p>
        </div>
    );
}

export default Inscription;