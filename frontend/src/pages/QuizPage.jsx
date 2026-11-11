function QuizPage() {
    //n°question,func update n° question
    const [current, setCurrent] = useState(0); 
    // score,func update score
    const [score, setScore] = useState(0);
    // bool fini , fun update bool fini
    const [finished, setFinished] = useState(false);

  // ce qui s'affiche à l'écran
  return (
    <div>
      <h1>Quiz</h1>;
    </div>
  );
}

export default QuizPage;