import { request } from "./api";

export async function login(identifiant, password) {
  localStorage.removeItem("token");
  localStorage.removeItem("user");
  const data = await request("/auth/login", {
    method: "POST",
    body: JSON.stringify({ login: identifiant, password }),
  });
  localStorage.setItem("token", data.token);
  localStorage.setItem("user", JSON.stringify(data.user));
  return data;
}
export const register = (user) =>
  request("/users", { method: "POST", body: JSON.stringify(user) });

export const getMe = () => request("/users/me");

// Révoque le token côté serveur, puis l'oublie localement même si l'appel échoue
export async function logout() {
  try {
    await request("/auth/logout", { method: "POST" });
  } finally {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
  }
}