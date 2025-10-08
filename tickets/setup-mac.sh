#!/bin/bash
set -e

# Crear keycloak-cert.pem
cp nginx/certs/local.crt keycloak-cert.pem

# Levantar contenedores con tu start-dev.sh
./start-dev.sh

# Importar certificado en backend
docker cp keycloak-cert.pem tickets-backend-1:/tmp/keycloak-cert.pem
docker exec -it tickets-backend-1 bash -c "\
keytool -importcert -noprompt -trustcacerts \
-alias keycloak-local \
-file /tmp/keycloak-cert.pem \
-keystore \$JAVA_HOME/lib/security/cacerts \
-storepass changeit"