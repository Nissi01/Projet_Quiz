import { request } from "./api";

export async function login(identifiant, password) {
  const data = await request("/auth/login", {
    method: "POST",
    body: JSON.stringify({ login: identifiant, password }),
  });
  localStorage.setItem("token", data.token);
  return data;
}

export const register = (user) =>
  request("/users", { method: "POST", body: JSON.stringify(user) });

export const getMe = () => request("/users/me");