import { useEffect, useState } from "react";
import { getSubjects } from "./services/quizService";

function App() {
  const [subjects, setSubjects] = useState([]);

  useEffect(() => {
    getSubjects().then(setSubjects).catch(console.error);
  }, []);

  return <pre>{JSON.stringify(subjects, null, 2)}</pre>;
}

export default App;