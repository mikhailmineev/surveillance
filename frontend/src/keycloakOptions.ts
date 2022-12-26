import { AuthClientTokens } from "@react-keycloak/core";

export const setTokens = (tokens: AuthClientTokens) => {
    localStorage.setItem('token', tokens.token ?? '');
    localStorage.setItem('refreshToken', tokens.refreshToken ?? '');
    localStorage.setItem('idToken', tokens.idToken ?? '');
}

const keycloakOptions = {
    onLoad: null,
    token: localStorage.getItem('token'),
    refreshToken: localStorage.getItem('refreshToken'),
    idToken: localStorage.getItem('idToken'),
    enableLogging: true,
    timeSkew: 0
}

export default keycloakOptions;
