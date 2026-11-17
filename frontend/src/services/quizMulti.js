import { startQuiz, submitQuiz } from "./quizService";


function repartir(nb, nbSujets) {
    const base = Math.floor(nb / nbSujets);
    const reste = nb % nbSujets;
    const parts = [];
    for (let i = 0; i < nbSujets; i++) {
        parts.push(Math.max(1, base + (i < reste ? 1 : 0)));
    }
    return parts;
}

function melanger(liste) {
    const copie = [...liste];
    for (let i = copie.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [copie[i], copie[j]] = [copie[j], copie[i]];
    }
    return copie;
}

export async function demarrerQuizMulti(ids, nb) {
    let sessions;
    if (ids.length === 0) {
        sessions = [await startQuiz({ subjectId: null, count: nb })];
    } else {
        const parts = repartir(nb, ids.length);
        sessions = await Promise.all(
            ids.map((id, i) => startQuiz({ subjectId: id, count: parts[i] }))
        );
    }
    const questions = sessions.flatMap((s) =>
        s.questions.map((q) => ({ ...q, sessionId: s.sessionId }))
    );
    return { sessions: sessions.map((s) => s.sessionId), questions: melanger(questions) };
}

export async function terminerQuizMulti(quiz, reponses) {
    const resultats = await Promise.all(
        quiz.sessions.map((sid) =>
            submitQuiz(sid, {
                answers: quiz.questions
                    .filter((q) => q.sessionId === sid)
                    .map((q) => ({ questionId: q.id, optionCode: reponses[q.id] })),
            })
        )
    );

    let score = 0;
    let total = 0;
    let corrections = [];
    for (const r of resultats) {
        score += r.score;
        total += r.total;
        corrections = corrections.concat(r.corrections);
    }
    return { score, total, corrections };
}