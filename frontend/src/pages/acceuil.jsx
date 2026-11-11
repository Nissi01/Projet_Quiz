import { useNavigate } from "react-router";
import { getRestants, UtiliserUnquizz } from "../services/invite.js";

// Fonction séparée : elle reçoit "naviguer" en paramètre
function essayez(naviguer) {
    UtiliserUnquizz();   // le compteur baisse
    naviguer("/quiz");  // on va sur la page quiz
}

// Le bouton d'essai, ou le message quand il n'y a plus d'essais
function Essai() {
    const naviguer = useNavigate();
    const restants = getRestants();

    if (restants > 0) {
        return (
            <div>
                <button onClick={() => essayez(naviguer)}>
                    Essayez tant que vous pouvez ({restants} restants)
                </button>
            </div>
        );
    }

    return (
        <div>
            <p>Vous avez deja essayez 3 fois le quizz il faut maintenant s'inscrire!</p>
        </div>
    );
}

// La page d'accueil
function Accueil() {
    const naviguer = useNavigate();

    return (
        <div>
            <h1>Bienvenue a question pour un Quizz</h1>
            <button onClick={() => naviguer("/inscription")}>inscription</button>
            <button onClick={() => naviguer("/connexion")}>connexion</button>
            <Essai />
        </div>
    );
}

export default Accueil;