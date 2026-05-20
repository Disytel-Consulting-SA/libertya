# Imagen de base de datos Libertya
#
# Prerequisito: el dump SQL debe estar disponible en el contexto de build.
# Ejemplo de build:
#   docker build --build-arg DUMP_FILE=dump_libertya_core_v26.05.sql -t disytel/libertya-db:26.05 .
#
# El dump debe ser generado con:
#   pg_dump -d <nombre_bd> -f dump_libertya_core_v26.05.sql
#
# Variables de entorno en runtime (se pasan desde docker-compose):
#   POSTGRES_USER       postgres
#   POSTGRES_PASSWORD   postgres
#   LIBERTYA_DB_NAME    libertya       (nombre de la base de datos a crear)
#   LIBERTYA_DB_USER    libertya       (usuario propietario)
#   LIBERTYA_DB_PASS    libertya       (contraseña del usuario)

FROM postgres:16-bookworm

ARG DUMP_FILE=dump_libertya_core_v26.05.sql

# dos2unix para normalizar los CRLF que puede traer el dump
RUN apt-get update && apt-get install -y --no-install-recommends dos2unix \
    && rm -rf /var/lib/apt/lists/*

# Copiar el dump fuera de initdb.d para que no lo ejecute la imagen base automáticamente.
# El script 00-init lo ejecuta él mismo contra la BD correcta.
COPY ${DUMP_FILE} /docker-libertya/libertya_release.sql
RUN dos2unix /docker-libertya/libertya_release.sql

# Script de inicialización: crea usuario, crea BD y ejecuta el dump
COPY init-libertya-db.sh /docker-entrypoint-initdb.d/00-init-libertya-db.sh
RUN chmod +x /docker-entrypoint-initdb.d/00-init-libertya-db.sh

EXPOSE 5432
