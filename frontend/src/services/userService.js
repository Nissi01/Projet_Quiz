import { request } from "./api";

export async function login(email, password) {
  const data = await request("/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
  localStorage.setItem("token", data.token); // à adapter selon la réponse
  return data;
}

export const register = (user) =>
  request("/users", { method: "POST", body: JSON.stringify(user) });

export const getMe = () => request("/users/me");