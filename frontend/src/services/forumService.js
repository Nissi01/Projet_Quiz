import { request } from "./api";

export const getThreads = (page = 0) => request(`/forum/threads?page=${page}`);
export const getThread = (id) => request(`/forum/threads/${id}`);

export const createThread = (title, body) =>
  request("/forum/threads", {
    method: "POST",
    body: JSON.stringify({ title, body }),
  });

export const updateThread = (id, title, body) =>
  request(`/forum/threads/${id}`, {
    method: "PUT",
    body: JSON.stringify({ title, body }),
  });

export const deleteThread = (id) =>
  request(`/forum/threads/${id}`, { method: "DELETE" });

export const getPosts = (id, page = 0) =>
  request(`/forum/threads/${id}/posts?page=${page}`);

export const createPost = (id, body) =>
  request(`/forum/threads/${id}/posts`, {
    method: "POST",
    body: JSON.stringify({ body }),
  });