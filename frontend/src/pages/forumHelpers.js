export const liste = (d) => d?.content ?? d ?? [];
export const auteur = (x) => x.authorName ?? "";

export const dateFr = (iso) =>
  iso ? new Date(iso).toLocaleDateString("fr-FR", { day: "numeric", month: "long", year: "numeric" }) : "";

export const extrait = (texte = "", n = 140) =>
  texte.length > n ? texte.slice(0, n).trimEnd() + "…" : texte;