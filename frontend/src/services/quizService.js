import { request } from "./api";

export const getSubjects = () => request("/subjects");

export const startQuiz = (body) =>
  request("/quizzes", { method: "POST", body: JSON.stringify(body) });

export const submitQuiz = (id, body) =>
  request(`/quizzes/${id}/submit`, { method: "POST", body: JSON.stringify(body) });