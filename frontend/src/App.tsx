import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import AppRouter from "./components/AppRouter";
import {WebSocketProvider} from "./contexts/WebSocketContext";

function App() {
  return (
    <div className="App">
        <WebSocketProvider>
            <AppRouter />
        </WebSocketProvider>
    </div>
  );
}

export default App;
