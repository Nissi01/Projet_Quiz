import { BrowserRouter, Routes, Route } from "react-router";
import Accueil from "./pages/acceuil.jsx";
import Inscription from "./pages/inscription.jsx";
import Connexion from "./pages/connexion.jsx";
import Quiz from "./pages/quiz.jsx";

// La page principale : elle choisit quelle page afficher selon l'adresse
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Accueil />} />
        <Route path="/inscription" element={<Inscription />} />
        <Route path="/connexion" element={<Connexion />} />
        <Route path="/quiz" element={<Quiz />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;