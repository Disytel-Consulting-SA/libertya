#!/bin/bash
set -e

# ==============================================================================
# Entrypoint de la imagen Libertya App
#
# Genera LibertyaEnv.properties a partir de variables de entorno, ejecuta
# ConfigurarAuto.sh (que regenera Variables.sh y la configuración de JBoss)
# y arranca el servidor.
#
# Variables de entorno soportadas (con sus defaults para docker-compose):
#   OXP_HOME              /ServidorOXP
#   JAVA_HOME             (autodetectado)
#   SERVIDOR_BD_OXP       db
#   PUERTO_BD_OXP         5432
#   NOMBRE_BD_OXP         libertya
#   USUARIO_BD_OXP        libertya
#   PASSWD_BD_OXP         libertya
#   SYSTEM_BD_OXP         libertya   (contraseña superusuario postgres, usada internamente)
#   SERVIDOR_APPS_OXP     localhost
#   PUERTO_WEB_OXP        8080
#   PUERTO_SSL_OXP        8443
#   PUERTO_JNP_OXP        1099
#   KEYSTOREPASS_OXP      libertya
#   OPCIONES_JAVA_OXP     -Xms512M -Xmx1024M -Dfile.encoding=UTF-8
#   SERVIDOR_MAIL_OXP     localhost
#   ADMIN_MAIL_OXP        admin@localhost
#   WAIT_FOR_DB           true   (espera que postgres acepte conexiones antes de arrancar)
#   WAIT_FOR_DB_TIMEOUT   60     (segundos máximos de espera)
#   DB_INTERNAL_HOST      db     (host interno Docker para el wait; por defecto igual a SERVIDOR_BD_OXP)
#   DB_INTERNAL_PORT      5432   (puerto interno Docker para el wait; por defecto igual a PUERTO_BD_OXP)
# ==============================================================================

OXP_HOME="${OXP_HOME:-/ServidorOXP}"
JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-8-openjdk-amd64}"

SERVIDOR_BD_OXP="${SERVIDOR_BD_OXP:-db}"
PUERTO_BD_OXP="${PUERTO_BD_OXP:-5432}"
NOMBRE_BD_OXP="${NOMBRE_BD_OXP:-libertya}"
USUARIO_BD_OXP="${USUARIO_BD_OXP:-libertya}"
PASSWD_BD_OXP="${PASSWD_BD_OXP:-libertya}"
SYSTEM_BD_OXP="${SYSTEM_BD_OXP:-libertya}"

SERVIDOR_APPS_OXP="${SERVIDOR_APPS_OXP:-localhost}"
PUERTO_WEB_OXP="${PUERTO_WEB_OXP:-8080}"
PUERTO_SSL_OXP="${PUERTO_SSL_OXP:-8443}"
PUERTO_JNP_OXP="${PUERTO_JNP_OXP:-1099}"

KEYSTOREPASS_OXP="${KEYSTOREPASS_OXP:-libertya}"
KEYSTORE_OXP="${KEYSTORE_OXP:-${OXP_HOME}/keystore/myKeystore}"
ALIASWEBKEYSTORE_OXP="${ALIASWEBKEYSTORE_OXP:-libertya}"
CODIGOALIASKEYSTORE_OXP="${CODIGOALIASKEYSTORE_OXP:-libertya}"

OPCIONES_JAVA_OXP="${OPCIONES_JAVA_OXP:--Xms512M -Xmx1024M -XX:MaxPermSize=512M -Dfile.encoding=UTF-8}"

SERVIDOR_MAIL_OXP="${SERVIDOR_MAIL_OXP:-localhost}"
ADMIN_MAIL_OXP="${ADMIN_MAIL_OXP:-admin@localhost}"
USUARIO_MAIL_OXP="${USUARIO_MAIL_OXP:-}"
PASSWORD_MAIL_OXP="${PASSWORD_MAIL_OXP:-}"

WAIT_FOR_DB="${WAIT_FOR_DB:-true}"
WAIT_FOR_DB_TIMEOUT="${WAIT_FOR_DB_TIMEOUT:-60}"
# Host/puerto internos de Docker para el healthcheck del wait.
# Cuando SERVIDOR_BD_OXP apunta al host externo (ej. localhost) para que el cliente
# Java/Swing pueda conectar, estos valores permiten que el wait use el nombre
# interno del contenedor (ej. "db") en lugar del externo.
DB_INTERNAL_HOST="${DB_INTERNAL_HOST:-${SERVIDOR_BD_OXP}}"
DB_INTERNAL_PORT="${DB_INTERNAL_PORT:-${PUERTO_BD_OXP}}"

URL_BD_OXP="jdbc:postgresql://${SERVIDOR_BD_OXP}:${PUERTO_BD_OXP}/${NOMBRE_BD_OXP}"

# ------------------------------------------------------------------------------
# 1. Esperar que la base de datos esté disponible
# ------------------------------------------------------------------------------
if [ "${WAIT_FOR_DB}" = "true" ]; then
    echo "[entrypoint] Esperando que PostgreSQL esté disponible en ${DB_INTERNAL_HOST}:${DB_INTERNAL_PORT}..."
    waited=0
    until nc -z "${DB_INTERNAL_HOST}" "${DB_INTERNAL_PORT}" 2>/dev/null; do
        if [ "${waited}" -ge "${WAIT_FOR_DB_TIMEOUT}" ]; then
            echo "[entrypoint] ERROR: Timeout esperando PostgreSQL en ${DB_INTERNAL_HOST}:${DB_INTERNAL_PORT} (${WAIT_FOR_DB_TIMEOUT}s)"
            exit 1
        fi
        sleep 2
        waited=$((waited + 2))
    done
    echo "[entrypoint] PostgreSQL disponible."
fi

# ------------------------------------------------------------------------------
# 2. Generar LibertyaEnv.properties desde variables de entorno
#    ConfigurarAuto.sh (SilentSetup) lo lee para generar Variables.sh y configs JBoss
# ------------------------------------------------------------------------------
echo "[entrypoint] Generando LibertyaEnv.properties..."

cat > "${OXP_HOME}/LibertyaEnv.properties" << EOF
#LibertyaEnv.properties - generado por docker-entrypoint.sh

OXP_HOME=${OXP_HOME}
JAVA_HOME=${JAVA_HOME}
TIPO_JAVA_OXP=sun
OPCIONES_JAVA_OXP=${OPCIONES_JAVA_OXP}

TIPO_BD_OXP=PostgreSQL
SERVIDOR_BD_OXP=${SERVIDOR_BD_OXP}
PUERTO_BD_OXP=${PUERTO_BD_OXP}
NOMBRE_BD_OXP=${NOMBRE_BD_OXP}
SYSTEM_BD_OXP=${SYSTEM_BD_OXP}
USUARIO_BD_OXP=${USUARIO_BD_OXP}
PASSWD_BD_OXP=${PASSWD_BD_OXP}
URL_BD_OXP=jdbc\:postgresql\://${SERVIDOR_BD_OXP}\:${PUERTO_BD_OXP}/${NOMBRE_BD_OXP}

TIPO_APPS_OXP=jboss
SERVIDOR_APPS_OXP=${SERVIDOR_APPS_OXP}
PUERTO_JNP_OXP=${PUERTO_JNP_OXP}
PUERTO_WEB_OXP=${PUERTO_WEB_OXP}
PUERTO_SSL_OXP=${PUERTO_SSL_OXP}
DEPLOY_APPS_OXP=${OXP_HOME}/jboss/server/openXpertya/deploy

KEYSTORE_OXP=${KEYSTORE_OXP}
ALIASWEBKEYSTORE_OXP=${ALIASWEBKEYSTORE_OXP}
CODIGOALIASKEYSTORE_OXP=${CODIGOALIASKEYSTORE_OXP}
KEYSTOREPASS_OXP=${KEYSTOREPASS_OXP}

SERVIDOR_MAIL_OXP=${SERVIDOR_MAIL_OXP}
ADMIN_MAIL_OXP=${ADMIN_MAIL_OXP}
USUARIO_MAIL_OXP=${USUARIO_MAIL_OXP}
PASSWORD_MAIL_OXP=${PASSWORD_MAIL_OXP}

SERVIDOR_FTP_OXP=localhost
PREFIJO_FTP_OXP=my
USUARIO_FTP_OXP=anonymous
PASSWD_FTP_OXP=user@host.com
EOF

# ------------------------------------------------------------------------------
# 3. Generar keystore si no existe
# ------------------------------------------------------------------------------
if [ ! -f "${KEYSTORE_OXP}" ]; then
    echo "[entrypoint] Generando keystore en ${KEYSTORE_OXP}..."
    mkdir -p "$(dirname "${KEYSTORE_OXP}")"
    keytool -genkeypair \
        -keyalg RSA \
        -alias "${CODIGOALIASKEYSTORE_OXP}" \
        -dname "CN=libertya, OU=Libertya, O=Libertya, L=Buenos Aires, ST=Buenos Aires, C=AR" \
        -keypass "${KEYSTOREPASS_OXP}" \
        -storepass "${KEYSTOREPASS_OXP}" \
        -validity 3650 \
        -keystore "${KEYSTORE_OXP}" \
        > /dev/null 2>&1
    echo "[entrypoint] Keystore generado."
fi

# ------------------------------------------------------------------------------
# 4. Ejecutar ConfigurarAuto.sh (genera Variables.sh y configuración de JBoss)
# ------------------------------------------------------------------------------
echo "[entrypoint] Ejecutando ConfigurarAuto.sh..."
cd "${OXP_HOME}"
./ConfigurarAuto.sh

# ------------------------------------------------------------------------------
# 5. Arrancar el servidor Libertya (JBoss)
# ------------------------------------------------------------------------------
echo "[entrypoint] Iniciando servidor Libertya..."
cd "${OXP_HOME}/utils"

# IniciarServidorJBoss.sh carga Variables.sh y lanza JBoss con JAVA_OPTS apropiado
exec ./IniciarServidorJBoss.sh
