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


🧩 Entorno de desarrollo con Traefik y certificados locales

Este proyecto usa Traefik como reverse proxy para exponer los servicios del backend (api.localtest.me) y de Keycloak (auth.localtest.me) bajo HTTPS, incluso en entorno local de desarrollo.

El script start-dev.sh
 automatiza todo el proceso de forma cross-platform, soportando macOS, Linux y Windows.

⚙️ Cómo funciona

El script:

Detecta el sistema operativo.

Genera automáticamente certificados SSL locales (con OpenSSL en macOS/Linux o mkcert en Windows).

Crea un archivo traefik-dynamic.yml con la configuración TLS adecuada para el entorno.

Levanta los contenedores del proyecto (backend, Keycloak, Postgres, Traefik, etc.) usando Docker Compose.

🧱 Certificados locales
Sistema	Herramienta usada	Archivos generados	Notas
macOS / Linux	openssl	certs/localtest.me.crt y certs/localtest.me.key	Se crean automáticamente al ejecutar ./start-dev.sh.
Windows	mkcert	certs/api.localtest.me+auth.localtest.me+localtest.me.pem y .key.pem	Necesitás instalar mkcert manualmente la primera vez.
🪟 Instalación de mkcert (solo Windows)

Descargá mkcert desde
👉 https://github.com/FiloSottile/mkcert/releases

Copiá el binario mkcert.exe en alguna ruta incluida en tu PATH (por ejemplo, C:\Windows\System32).

Ejecutá una vez en Git Bash o PowerShell:

mkcert -install


Esto instalará una autoridad certificadora local (CA) que tus navegadores confiarán.

Luego simplemente corré:

bash start-dev.sh


El script generará los certificados válidos automáticamente en certs/.

🧩 Archivo TLS dinámico (traefik-dynamic.yml)

El script genera este archivo de forma automática, con contenido diferente según el sistema operativo:

En macOS / Linux:
tls:
  certificates:
    - certFile: /certs/localtest.me.crt
      keyFile: /certs/localtest.me.key

En Windows:
tls:
  certificates:
    - certFile: /certs/api.localtest.me+auth.localtest.me+localtest.me.pem
      keyFile: /certs/api.localtest.me+auth.localtest.me+localtest.me+key.pem

🚀 Levantar el entorno

Desde la raíz del proyecto:

bash start-dev.sh


Esto:

Construye las imágenes (Dockerfile.dev)

Crea y levanta los contenedores

Configura certificados HTTPS válidos para el entorno local

🌐 Accesos locales
Servicio	URL
Backend API	https://api.localtest.me/api/v1/events

Keycloak	https://auth.localtest.me

Dashboard Traefik	http://localhost:8088
🔒 Nota sobre navegadores y certificados

Si el navegador aún muestra un error de seguridad:

Asegurate de que la CA esté instalada en el sistema (mkcert -install en Windows).

2️⃣ Windows

Certificados: mkcert

Instalá mkcert y ejecutá:

mkcert -install


Esto crea la CA local en:

C:\Users\<usuario>\AppData\Local\mkcert


Importá la CA al sistema si es necesario:

Abrí certmgr.msc

Seleccioná Trusted Root Certification Authorities → Certificates

Importá rootCA.pem generado por mkcert.

Reiniciá el navegador y verificá que confíe en api.localtest.me y auth.localtest.me.

Nota: Solo agregarlo al Current User funciona en la mayoría de casos; si falla, hay que hacerlo a Local Machine (requiere permisos de administrador).

🍏 macOS: agregar certificado al llavero

Abrí Acceso a Llaveros (Keychain Access).

En el panel izquierdo, seleccioná Sistema (no “inicio de sesión”).

Arrastrá el archivo del certificado raíz que generaste:

Con OpenSSL:

/ruta/a/tu/proyecto/certs/localtest.me.crt


Hacé doble clic sobre él → desplegá Confiar → seleccioná Confiar siempre en este certificado.

Cerrá y reabrí tu navegador.