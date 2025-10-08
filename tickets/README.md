🚀 Proyecto Event Ticket Platform
✅ Checklist de instalación y configuración

📦 Requisitos previos

 Instalar Docker + Docker Compose

 Instalar Git

 (Windows) Usar Git Bash o WSL


 🔑 Paso 1: Clonar el proyecto
git clone <tu-repo.git>
cd <nombre-del-proyecto>

⚙️ Paso 2: Configuración de variables de entorno

 Copiar el archivo de ejemplo:

cp .env.example .env.dev


 Editar .env.dev y completar credenciales (DB, Cloudinary, etc.)


 🔒 Paso 3: Generar certificados HTTPS
3.1 Crear certificados locales (si no existen)
mkdir -p nginx/certs
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout nginx/certs/local.key \
  -out nginx/certs/local.crt \
  -subj "/CN=auth.localtest.me"

  3.2 Crear certificado para el backend (Java)
cp nginx/certs/local.crt keycloak-cert.pem

🐘 Paso 4: Base de datos

 Al levantar Postgres, se ejecuta automáticamente init-multiple-dbs.sh para crear:

event_ticket_platform

keycloak_db

⚠️ Si ya levantaste Postgres antes de añadir el script, elimina los volúmenes para recrear las DB:

docker compose down -v
./start-dev.sh

🐳 Paso 5: Levantar contenedores
./start-dev.sh

Esto hará:

Construcción de imágenes (backend, frontend, keycloak, postgres, nginx)

Creación de bases de datos

Inicialización de Keycloak y Nginx con SSL local

☕ Paso 6: Importar certificado en backend (Java confíe en Keycloak)
docker cp keycloak-cert.pem tickets-backend-1:/tmp/keycloak-cert.pem
docker exec -it tickets-backend-1 bash
keytool -importcert -noprompt -trustcacerts \
  -alias keycloak-local \
  -file /tmp/keycloak-cert.pem \
  -keystore $JAVA_HOME/lib/security/cacerts \
  -storepass changeit
keytool -list -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit | grep keycloak-local
exit

🌐 Paso 7: Acceso al sistema

Backend → https://localhost:8081/api/v1/

Frontend → http://localhost:5173 (antes clonar el otro repositorio de Frontend_ticket_platform)

Keycloak Admin → https://auth.localtest.me/admin

🤖 Paso 8 (opcional): Scripts de setup automático

Si querés que todo se configure con un solo comando:

Mac/Linux
./setup-mac.sh

Windows (Git Bash / WSL)
./setup-windows.sh


Estos scripts harán:

Copiar certificado para Keycloak

Levantar contenedores

Importar certificado en el backend automáticamente