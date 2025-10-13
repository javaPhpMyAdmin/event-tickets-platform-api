#!/bin/bash
set -e

export MSYS_NO_PATHCONV=1  # Evita conversiones automáticas en Git Bash

echo "🚀 Levantando entorno de desarrollo con Traefik..."

# Backup del .env si existe
if [ -f .env ]; then
  mv .env .env.bak
fi

# Detectar sistema operativo y construir rutas correctas
if [[ "$OS" == "Windows_NT" ]]; then
  PROJECT_PATH=$(pwd -W)  # Windows (Git Bash)
  export TRAEFIK_HTTP_PORT=8081
  export TRAEFIK_HTTPS_PORT=8443
  export TRAEFIK_DASHBOARD_PORT=8088
else
  PROJECT_PATH=$(pwd)     # macOS / Linux
  export TRAEFIK_HTTP_PORT=80
  export TRAEFIK_HTTPS_PORT=443
  export TRAEFIK_DASHBOARD_PORT=8088
fi

export ENV_FILE="$PROJECT_PATH/.env.dev"
export DOCKER_FILE="Dockerfile.dev"

# Verificar que el archivo .env.dev exista
if [ ! -f "$ENV_FILE" ]; then
  echo "❌ No se encontró el archivo .env.dev en $ENV_FILE"
  exit 1
fi

# Crear carpeta de certificados si no existe
mkdir -p "$PROJECT_PATH/certs"

# Generar certificados si no existen
if [ ! -f "$PROJECT_PATH/certs/localtest.me.crt" ] || [ ! -f "$PROJECT_PATH/certs/localtest.me.key" ]; then
  echo "🔑 Generando certificados SSL para localtest.me ..."

  if command -v mkcert &> /dev/null; then
    echo "🟢 mkcert detectado — generando certificados confiables..."
    mkcert -install
    mkcert -cert-file "$PROJECT_PATH/certs/localtest.me.crt" \
           -key-file "$PROJECT_PATH/certs/localtest.me.key" \
           "localtest.me" "api.localtest.me" "auth.localtest.me"
  else
    echo "🟡 mkcert no está instalado — usando certificado autofirmado con OpenSSL..."
    openssl req -x509 -nodes -days 365 \
      -newkey rsa:2048 \
      -keyout "$PROJECT_PATH/certs/localtest.me.key" \
      -out "$PROJECT_PATH/certs/localtest.me.crt" \
      -subj "/CN=localtest.me" \
      -addext "subjectAltName=DNS:localtest.me,DNS:api.localtest.me,DNS:auth.localtest.me"
  fi
else
  echo "🔒 Certificados ya existen, no se regeneran."
fi

cat > "$PROJECT_PATH/traefik-dynamic.yml" <<EOL
tls:
  certificates:
    - certFile: /certs/localtest.me.crt
      keyFile: /certs/localtest.me.key
EOL

echo "📝 traefik-dynamic.yml actualizado con los certificados recién generados."

# Build y levantar contenedores con Traefik
docker compose --env-file "$ENV_FILE" -f "$PROJECT_PATH/docker-compose.yml" build --no-cache
docker compose --env-file "$ENV_FILE" -f "$PROJECT_PATH/docker-compose.yml" up -d --force-recreate

# Restaurar .env original si había uno
# Restaurar .env original si había uno
if [ -f .env.bak ]; then
  mv .env.bak .env
fi

echo ""
echo "✅ Entorno de desarrollo levantado correctamente:"
echo " Backend: https://api.localtest.me"
echo " Keycloak: https://auth.localtest.me"
echo " Dashboard Traefik: http://localhost:8088"