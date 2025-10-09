# #!/bin/bash
# set -e
# export MSYS_NO_PATHCONV=1  # Evita conversiones de ruta automáticas en Git Bash

# echo "🚀 Levantando entorno de desarrollo con Traefik..."

# # Backup .env si existe
# if [ -f .env ]; then
#   mv .env .env.bak
# fi

# # Detectar sistema operativo y construir rutas correctas
# if [[ "$OS" == "Windows_NT" ]]; then
#   # En Git Bash (Windows)
#   PROJECT_PATH=$(pwd -W)
# else
#   # En macOS / Linux
#   PROJECT_PATH=$(pwd)
# fi

# export ENV_FILE="$PROJECT_PATH/.env.dev"
# export DOCKER_FILE="Dockerfile.dev"

# # Verificar que el archivo .env.dev exista
# if [ ! -f "$ENV_FILE" ]; then
#   echo "❌ No se encontró el archivo .env.dev en $ENV_FILE"
#   exit 1
# fi

# # Crear carpeta de certificados si no existe
# mkdir -p "$PROJECT_PATH/certs"

# # Generar certificados autofirmados si no existen
# if [ ! -f "$PROJECT_PATH/certs/localtest.me.crt" ] || [ ! -f "$PROJECT_PATH/certs/localtest.me.key" ]; then
#   echo "🔑 Generando certificado autofirmado para api.localtest.me y auth.localtest.me..."
#   openssl req -x509 -nodes -days 365 \
#     -newkey rsa:2048 \
#     -keyout "$PROJECT_PATH/certs/localtest.me.key" \
#     -out "$PROJECT_PATH/certs/localtest.me.crt" \
#     -subj "//CN=localtest.me" \
#     -addext "subjectAltName=DNS:localtest.me,DNS:api.localtest.me,DNS:auth.localtest.me"
# else
#   echo "🔒 Certificados ya existen, no se regeneran."
# fi

# # Build y levantar contenedores con Traefik
# docker compose --env-file "$ENV_FILE" -f "$PROJECT_PATH/docker-compose.yml" build --no-cache
# docker compose --env-file "$ENV_FILE" -f "$PROJECT_PATH/docker-compose.yml" up -d --force-recreate

# # Restaurar .env original si había uno
# if [ -f .env.bak ]; then
#   mv .env.bak .env
# fi

# echo ""
# echo "✅ Entorno dev levantado:"
# echo "   Backend: https://api.localtest.me"
# echo "   Keycloak: https://auth.localtest.me"
# echo "   Dashboard Traefik: http://localhost:8088"

#!/bin/bash
set -e
export MSYS_NO_PATHCONV=1  # Evita conversiones de ruta automáticas en Git Bash

echo "🚀 Levantando entorno de desarrollo con Traefik..."

# Backup .env si existe
if [ -f .env ]; then
  mv .env .env.bak
fi

# Detectar sistema operativo y construir rutas correctas
if [[ "$OS" == "Windows_NT" ]]; then
  # En Git Bash (Windows)
  PROJECT_PATH=$(pwd -W)
  IS_WINDOWS=true
else
  # En macOS / Linux
  PROJECT_PATH=$(pwd)
  IS_WINDOWS=false
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

# --------------------------------------------------------------------
# 🧩 GENERAR CERTIFICADOS SEGÚN SISTEMA OPERATIVO
# --------------------------------------------------------------------
if [ "$IS_WINDOWS" = true ]; then
  echo "🔐 Detectado Windows: usando mkcert..."

  # Verificar que mkcert esté instalado
  if ! command -v mkcert &> /dev/null; then
    echo "❌ mkcert no está instalado. Descárgalo desde: https://github.com/FiloSottile/mkcert"
    exit 1
  fi

  # Instalar CA local si no existe
  mkcert -install

  # Generar certificado si no existe
  if [ ! -f "$PROJECT_PATH/certs/api.localtest.me+auth.localtest.me+localtest.me.pem" ]; then
    echo "🧾 Generando certificados con mkcert..."
    cd "$PROJECT_PATH/certs"
    mkcert api.localtest.me auth.localtest.me localtest.me
    cd "$PROJECT_PATH"
  else
    echo "🔒 Certificados mkcert ya existen, no se regeneran."
  fi

  # Crear traefik-dynamic.yml para Windows
  cat > "$PROJECT_PATH/traefik-dynamic.yml" <<EOF
tls:
  certificates:
    - certFile: /certs/api.localtest.me+auth.localtest.me+localtest.me.pem
      keyFile: /certs/api.localtest.me+auth.localtest.me+localtest.me+key.pem
EOF

else
  echo "🧰 Detectado macOS/Linux: usando OpenSSL..."

  # Generar certificados autofirmados si no existen
  if [ ! -f "$PROJECT_PATH/certs/localtest.me.crt" ] || [ ! -f "$PROJECT_PATH/certs/localtest.me.key" ]; then
    echo "🔑 Generando certificado autofirmado para api.localtest.me y auth.localtest.me..."
    openssl req -x509 -nodes -days 365 \
      -newkey rsa:2048 \
      -keyout "$PROJECT_PATH/certs/localtest.me.key" \
      -out "$PROJECT_PATH/certs/localtest.me.crt" \
      -subj "//CN=localtest.me" \
      -addext "subjectAltName=DNS:localtest.me,DNS:api.localtest.me,DNS:auth.localtest.me"
  else
    echo "🔒 Certificados OpenSSL ya existen, no se regeneran."
  fi

  # Crear traefik-dynamic.yml para macOS/Linux
  cat > "$PROJECT_PATH/traefik-dynamic.yml" <<EOF
tls:
  certificates:
    - certFile: /certs/localtest.me.crt
      keyFile: /certs/localtest.me.key
EOF
fi

# --------------------------------------------------------------------
# 🐳 Levantar contenedores
# --------------------------------------------------------------------
docker compose --env-file "$ENV_FILE" -f "$PROJECT_PATH/docker-compose.yml" build --no-cache
docker compose --env-file "$ENV_FILE" -f "$PROJECT_PATH/docker-compose.yml" up -d --force-recreate

# Restaurar .env original si había uno
if [ -f .env.bak ]; then
  mv .env.bak .env
fi

echo ""
echo "✅ Entorno dev levantado:"
echo "   Backend: https://api.localtest.me"
echo "   Keycloak: https://auth.localtest.me"
echo "   Dashboard Traefik: http://localhost:8088"