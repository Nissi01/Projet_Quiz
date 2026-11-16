import { useNavigate } from "react-router";
import { getRestants, UtiliserUnquizz } from "../services/invite.js";
import "./accueil.css";

// Fonction séparée : elle reçoit "naviguer" en paramètre
function essayez(naviguer) {
    UtiliserUnquizz();   // le compteur baisse
    naviguer("/quiz");   // on va sur la page quiz
}

// Le bouton d'essai, ou le message quand il n'y a plus d'essais
function Essai() {
    const naviguer = useNavigate();
    const restants = getRestants();

    if (restants > 0) {
        return (
            <div className="carte accueil-essai">
                <button onClick={() => essayez(naviguer)}>
                    Essayez tant que vous pouvez ({restants} restants)
                </button>
            </div>
        );
    }

    return (
        <div className="carte accueil-essai">
            <p>Vous avez deja essayez 3 fois le quizz il faut maintenant s'inscrire!</p>
        </div>
    );
}

// La page d'accueil
function Accueil() {
    const naviguer = useNavigate();

    return (
        <main className="accueil">
            <h1>Bienvenue dans question pour un Quizz</h1>

            <div className="accueil-boutons">
                <button onClick={() => naviguer("/inscription")}>inscription</button>
                <button onClick={() => naviguer("/connexion")}>connexion</button>
            </div>

            <Essai />
        </main>
    );
}

export default Accueil;