import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { login } from "../services/userService.js";

async function envoyer(evenement, identifiant, motDePasse, setErreur, naviguer) {
    evenement.preventDefault();

    try {
        await login(identifiant, motDePasse);
        naviguer("/quiz");
    } catch (e) {
        if (e.message === "Failed to fetch") {
            setErreur("Impossible de joindre le serveur.");
        } else if (identifiant.includes("@")) {
            setErreur("Email ou mot de passe incorrect.");
        } else {
            setErreur("Pseudo ou mot de passe incorrect.");
        }
    }
}

function Connexion() {
    const naviguer = useNavigate();
    const [identifiant, setIdentifiant] = useState("");
    const [motDePasse, setMotDePasse] = useState("");
    const [erreur, setErreur] = useState("");

    return (
        <main>
            <div className="carte">
                <h1>Connexion</h1>

                <form className="carte" onSubmit={(e) => envoyer(e, identifiant, motDePasse, setErreur, naviguer)}>
                    <input type="text" placeholder="Email ou pseudo" value={identifiant}
                        onChange={(e) => setIdentifiant(e.target.value)}
                        required />
                    <input type="password" placeholder="Mot de passe" value={motDePasse}
                        onChange={(e) => setMotDePasse(e.target.value)}
                        required />
                    <button type="submit">Se connecter</button>
                </form>

                {erreur && <p className="erreur">{erreur}</p>}

                <p>Pas encore de compte ? <Link to="/inscription">Créer un compte</Link></p>
                <p><Link to="/">Retour à l'accueil</Link></p>
            </div>
        </main>
    );
}

export default Connexion;