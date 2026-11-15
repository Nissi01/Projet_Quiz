import { useState } from "react";
import { useNavigate, Link } from "react-router";
import { login } from "../services/userService";

function Connexion() {
  const [identifiant, setIdentifiant] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await login(identifiant, password);
      navigate("/");
    } catch {
      setError("Identifiant ou mot de passe incorrect.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <form className="carte" onSubmit={handleSubmit}>
      <h1>Connexion</h1>
      <input placeholder="Pseudo ou email" value={identifiant}
             onChange={(e) => setIdentifiant(e.target.value)} required />
      <input type="password" placeholder="Mot de passe" value={password}
             onChange={(e) => setPassword(e.target.value)} required />
      {error && <p className="erreur">{error}</p>}
      <button type="submit" disabled={loading}>
        {loading ? "Connexion..." : "Se connecter"}
      </button>
      <p>Pas de compte ? <Link to="/inscription">Créer un compte</Link></p>
    </form>
  );
}

export default Connexion;