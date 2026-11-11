//la page  disant main qui control les pages a affiche
import { useEffect, useState } from "react";
import { getQuestions } from "./services/quizService";
import Quiz from "./pages/quiz";
import forum from "./pages/forum";
import acceuil from "./pages/acceuil";
function App() {
  return (
    <div>
      <h1>Mon Quiz</h1>
    </div>
  );
}

export default App;