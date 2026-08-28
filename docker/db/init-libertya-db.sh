#!/bin/bash
# Script de inicialización de la base de datos Libertya.
# Se ejecuta una sola vez, cuando el volumen de datos está vacío.
# La imagen oficial de postgres lo invoca como el usuario postgres.
set -e

LIBERTYA_DB_NAME="${LIBERTYA_DB_NAME:-libertya}"
LIBERTYA_DB_USER="${LIBERTYA_DB_USER:-libertya}"
LIBERTYA_DB_PASS="${LIBERTYA_DB_PASS:-libertya}"
DUMP_FILE="/docker-libertya/libertya_release.sql"

echo "[init-db] Creando usuario ${LIBERTYA_DB_USER}..."
psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" --dbname postgres <<-SQL
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = '${LIBERTYA_DB_USER}') THEN
            CREATE USER "${LIBERTYA_DB_USER}" WITH PASSWORD '${LIBERTYA_DB_PASS}';
        END IF;
    END
    \$\$;
SQL

echo "[init-db] Creando base de datos ${LIBERTYA_DB_NAME}..."
psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" --dbname postgres <<-SQL
    CREATE DATABASE "${LIBERTYA_DB_NAME}" OWNER "${LIBERTYA_DB_USER}";
SQL

echo "[init-db] Restaurando dump en ${LIBERTYA_DB_NAME} (esto puede tardar varios minutos)..."
psql -v ON_ERROR_STOP=1 \
    --username "${POSTGRES_USER}" \
    --dbname "${LIBERTYA_DB_NAME}" \
    --file "${DUMP_FILE}"

echo "[init-db] Asignando permisos al usuario ${LIBERTYA_DB_USER}..."
psql -v ON_ERROR_STOP=1 --username "${POSTGRES_USER}" --dbname "${LIBERTYA_DB_NAME}" <<-SQL
    GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO "${LIBERTYA_DB_USER}";
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO "${LIBERTYA_DB_USER}";
    GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO "${LIBERTYA_DB_USER}";
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO "${LIBERTYA_DB_USER}";
    ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO "${LIBERTYA_DB_USER}";
SQL

echo "[init-db] Base de datos Libertya inicializada correctamente."
