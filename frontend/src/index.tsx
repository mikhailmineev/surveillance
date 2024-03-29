import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import reportWebVitals from './reportWebVitals';
import keycloak from "./keycloak";
import { ReactKeycloakProvider } from '@react-keycloak/web'
import keycloakOptions, {setTokens} from "./keycloakOptions";

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
    <ReactKeycloakProvider authClient={keycloak} onTokens={setTokens} initOptions={keycloakOptions} >
        <React.StrictMode>
            <App />
        </React.StrictMode>
    </ReactKeycloakProvider>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
