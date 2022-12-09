import React from 'react';
import './App.css';
import Header from "./components/Header";
import 'bootstrap/dist/css/bootstrap.min.css';
import AppRouter from "./components/AppRouter";

function App() {
  return (
    <div className="App">
        <Header />
        <AppRouter />
    </div>
  );
}

export default App;
