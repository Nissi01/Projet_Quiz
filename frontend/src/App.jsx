<<<<<<< HEAD
import { useEffect, useState } from "react";
import { getSubjects } from "./services/quizService";

function App() {
  const [subjects, setSubjects] = useState([]);

  useEffect(() => {
    getSubjects().then(setSubjects).catch(console.error);
  }, []);

  return <pre>{JSON.stringify(subjects, null, 2)}</pre>;
=======
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
>>>>>>> da710d206712bdff7bded68ca6673a8f63e034d2
}

export default App;