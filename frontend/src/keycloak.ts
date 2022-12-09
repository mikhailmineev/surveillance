import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
    url: '/auth',
    realm: 'surveillance',
    clientId: 'surveillance',

});

export default keycloak;
