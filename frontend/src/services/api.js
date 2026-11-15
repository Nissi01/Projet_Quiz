export const API_URL = "http://localhost:8080/api";

export async function request(path, options = {}) {
  const token = localStorage.getItem("token");

  const res = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
  });

  if (!res.ok) {
    // token expiré : on le supprime pour forcer une reconnexion
    if (res.status === 401 && token) localStorage.removeItem("token");
    const msg = await res.text();
    throw new Error(msg || `Erreur ${res.status}`);
  }

  // évite le crash quand la réponse est vide (DELETE, 204...)
  const text = await res.text();
  return text ? JSON.parse(text) : null;
}