# #!/bin/bash
# set -e

# # Backup .env
# if [ -f .env ]; then
#   mv .env .env.bak
# fi

# # Variables de entorno para docker compose
# export ENV_FILE=$(pwd)/.env.dev
# export DOCKER_FILE=Dockerfile.dev

# # Build y levantar contenedores
# docker compose --env-file "$ENV_FILE" -f docker-compose.yml build --no-cache
# docker compose --env-file "$ENV_FILE" -f docker-compose.yml up -d --force-recreate

# # Restaurar .env original
# if [ -f .env.bak ]; then
#   mv .env.bak .env
# fi



#!/bin/bash
set -e

echo "🚀 Levantando entorno de desarrollo con Traefik..."

# Backup .env si existe
if [ -f .env ]; then
  mv .env .env.bak
fi

# Variables de entorno para docker compose
export ENV_FILE=$(pwd)/.env.dev
export DOCKER_FILE=Dockerfile.dev

# Crear carpeta de certificados si no existe
mkdir -p ./certs

# Generar certificados autofirmados si no existen
if [ ! -f ./certs/local.crt ] || [ ! -f ./certs/local.key ]; then
  echo "🔑 Generando certificado autofirmado para api.localtest.me y auth.localtest.me..."
  openssl req -x509 -nodes -days 365 \
    -newkey rsa:2048 \
    -keyout ./certs/localtest.me.key \
    -out ./certs/localtest.me.crt \
    -subj "/CN=localtest.me" \
    -addext "subjectAltName=DNS:localtest.me,DNS:api.localtest.me,DNS:auth.localtest.me"
fi

# Build y levantar contenedores con Traefik
docker compose --env-file "$ENV_FILE" -f docker-compose.yml build --no-cache
docker compose --env-file "$ENV_FILE" -f docker-compose.yml up -d --force-recreate

# Restaurar .env original
if [ -f .env.bak ]; then
  mv .env.bak .env
fi

echo "✅ Entorno dev levantado:"
echo "   Backend: https://api.localtest.me"
echo "   Keycloak: https://auth.localtest.me"
echo "   Dashboard Traefik: http://localhost:8088"