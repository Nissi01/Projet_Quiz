import { startQuiz, submitQuiz } from "./quizService";


function repartir(nb, stocks) {
    const parts = stocks.map(() => 0);
    let reste = nb;
    let progres = true;
    while (reste > 0 && progres) {
        progres = false;
        for (let i = 0; i < stocks.length && reste > 0; i++) {
            if (parts[i] < stocks[i]) {
                parts[i]++;
                reste--;
                progres = true;
            }
        }
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

export async function demarrerQuizMulti(ids, nb, stocks = {}) {
    let sessions;
    if (ids.length === 0) {
        sessions = [await startQuiz({ subjectId: null, count: nb })];
    } else {
        const disponibles = melanger(ids).filter((id) => (stocks[id] ?? 0) > 0).slice(0, nb);
        const parts = repartir(nb, disponibles.map((id) => stocks[id]));
        sessions = await Promise.all(
            disponibles.map((id, i) => startQuiz({ subjectId: id, count: parts[i] }))
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