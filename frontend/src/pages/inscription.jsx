import { useState } from "react";
import { useNavigate, Link } from "react-router";
import { register } from "../services/userService";

function Inscription() {
  // A ADAPTER avec UserDtos.CreateUserRequest (noms des champs)
  const [form, setForm] = useState({ username: "", email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const change = (champ) => (e) => setForm({ ...form, [champ]: e.target.value });

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await register(form);
      navigate("/");
    } catch (err) {
      setError("Inscription impossible : " + err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <form className="carte" onSubmit={handleSubmit}>
      <h1>Inscription</h1>
      <input placeholder="Pseudo" value={form.username} onChange={change("username")} required />
      <input type="email" placeholder="Email" value={form.email} onChange={change("email")} required />
      <input type="password" placeholder="Mot de passe" value={form.password}
             onChange={change("password")} minLength={6} required />
      {error && <p className="erreur">{error}</p>}
      <button type="submit" disabled={loading}>
        {loading ? "Création..." : "Créer mon compte"}
      </button>
      <p>Déjà un compte ? <Link to="/connexion">Se connecter</Link></p>
    </form>
  );
}

export default Inscription;