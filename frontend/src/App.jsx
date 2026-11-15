import { BrowserRouter, Routes, Route, Navigate } from "react-router";
import Accueil from "./pages/Acceuil.jsx";
import Inscription from "./pages/Inscription.jsx";
import Connexion from "./pages/Connexion.jsx";
import QuizPage from "./pages/QuizPage.jsx";
import ForumPage from "./pages/ForumPage.jsx";
import Nav from "./components/Nav.jsx";

// Le forum n'est accessible que si on est connecté
function Protege({ children }) {
  return localStorage.getItem("token") ? children : <Navigate to="/connexion" />;
}

function App() {
  return (
    <BrowserRouter>
      <Nav />
      <main>
        <Routes>
          <Route path="/" element={<Accueil />} />
          <Route path="/inscription" element={<Inscription />} />
          <Route path="/connexion" element={<Connexion />} />
          <Route path="/quiz" element={<Protege><QuizPage /></Protege>} />
          <Route path="/forum" element={<Protege><ForumPage /></Protege>} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;