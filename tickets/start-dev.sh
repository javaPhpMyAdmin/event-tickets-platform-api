# mv .env .env.bak  # Renombra el .env para que Docker Compose no lo tome
# export ENV_FILE=$(pwd)/.env.dev
# DOCKER_FILE=Dockerfile.dev docker compose --env-file "$ENV_FILE" -f docker-compose.yml build --no-cache && \
# DOCKER_FILE=Dockerfile.dev docker compose --env-file "$ENV_FILE" -f docker-compose.yml up -d --force-recreate

# mv .env.bak .env  # Restaura el .env original si lo necesitas después

#!/bin/bash
set -e

# Backup .env
if [ -f .env ]; then
  mv .env .env.bak
fi

# Variables de entorno para docker compose
export ENV_FILE=$(pwd)/.env.dev
export DOCKER_FILE=Dockerfile.dev

# Build y levantar contenedores
docker compose --env-file "$ENV_FILE" -f docker-compose.yml build --no-cache
docker compose --env-file "$ENV_FILE" -f docker-compose.yml up -d --force-recreate

# Restaurar .env original
if [ -f .env.bak ]; then
  mv .env.bak .env
fi