#!/bin/sh

mkdir "keys"
cd "keys"
if [ ! -f "keystore.jks" ]; then
    echo "Creating new keystore.jks and setting password in config file"
    PASSWORD="P@ssw0rd"
    keytool \
        -dname "CN=, OU=, O=, L=, S=, C=" \
        -genkeypair \
        -alias "selfsigned" \
        -keyalg "RSA" \
        -validity 365 \
        -dname "CN=localhost, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown" \
        -ext "SAN=DNS:localhost" \
        --keystore "keystore.jks" \
        -storepass "$PASSWORD" \
        -noprompt
    keytool \
        -importkeystore \
        -srckeystore "keystore.jks" \
        -srcstorepass "$PASSWORD" \
        -destkeystore "keystore.p12" \
        -deststorepass "$PASSWORD" \
        -deststoretype "PKCS12" \
        -noprompt
    openssl pkcs12 -in keystore.p12 -passin "pass:$PASSWORD" -nokeys -clcerts -out tls.crt
    openssl pkcs12 -in keystore.p12 -passin "pass:$PASSWORD" -nocerts -nodes -out tls.key
    cd ".."

    echo "POSTGRES_PASSWORD=$PASSWORD" >> ".env"
    echo "KC_DB_PASSWORD=$PASSWORD" >> ".env"
    echo "KC_HTTPS_KEY_STORE_PASSWORD=$PASSWORD" >> ".env"
    echo "KEYCLOAK_ADMIN_PASSWORD=$PASSWORD" >> ".env"

    echo "server.ssl.key-store-password=$PASSWORD" >> "application.properties"
    echo "client.ssl.trust-store-password=$PASSWORD" >> "application.properties"

    echo "Use credential admin/$PASSWORD to login in the application"
fi
