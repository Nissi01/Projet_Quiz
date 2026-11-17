import { BrowserRouter, Routes, Route, Navigate } from "react-router";
import Accueil from "./pages/acceuil.jsx";
import Inscription from "./pages/inscription.jsx";
import Connexion from "./pages/connexion.jsx";
import QuizPage from "./pages/QuizPage.jsx";
import ForumPage from "./pages/ForumPage.jsx";
import Nav from "./components/Nav.jsx";
import ForumNouveau from "./pages/ForumNouveau.jsx";
import ForumThread from "./pages/ForumThread.jsx";


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
          <Route path="/forum" element={<Protege><ForumPage /></Protege>} />
          <Route path="/forum/nouveau" element={<Protege><ForumNouveau /></Protege>} />
          <Route path="/forum/:id" element={<Protege><ForumThread /></Protege>} />
          <Route path="*" element={<Navigate to="/" />} />
        </Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;