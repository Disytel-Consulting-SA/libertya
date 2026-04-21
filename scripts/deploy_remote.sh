#!/usr/bin/env bash
set -Eeuo pipefail

ZIP_PATH="${1:-}"
if [[ -z "${ZIP_PATH}" ]]; then
    echo "Uso: deploy_remote.sh /tmp/ServidorOXP_V25.0.zip"
    exit 1
fi

OXP_HOME="${OXP_HOME:-/ServidorOXP}"
SERVICE_NAME="${SERVICE_NAME:-libertyad}"
APP_USER="${APP_USER:-libertya}"
APP_GROUP="${APP_GROUP:-libertya}"
BACKUP_RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-3}"

BASE_DIR="$(dirname "${OXP_HOME}")"
APP_DIR="$(basename "${OXP_HOME}")"
TS="$(date +%Y%m%d_%H%M%S)"
BACKUP_DIR="${BASE_DIR}/${APP_DIR}_backup_${TS}"
STAGE_DIR="$(mktemp -d "/tmp/${APP_DIR}_stage_${TS}_XXXX")"
DEPLOY_STARTED=0

log() {
    printf '[%s] %s\n' "$(date '+%F %T')" "$*"
}

die() {
    echo "ERROR: $*" >&2
    exit 1
}

read_env_property() {
    local key="$1"
    local default_value="${2:-}"
    local env_file="${OXP_HOME}/LibertyaEnv.properties"
    local raw_value=""

    if [[ ! -f "${env_file}" ]]; then
        printf '%s' "${default_value}"
        return
    fi

    raw_value="$(grep -m1 "^${key}=" "${env_file}" | cut -d'=' -f2- || true)"
    if [[ -z "${raw_value}" ]]; then
        printf '%s' "${default_value}"
        return
    fi

    # Compatibilidad mínima con propiedades Java escapadas.
    raw_value="${raw_value//\\:/:}"
    raw_value="${raw_value//\\\\/\\}"
    printf '%s' "${raw_value}"
}

restore_or_create_keystore() {
    local keystore_file
    local keystore_pass
    local keystore_alias
    local java_home_from_env
    local keytool_cmd
    local host_name

    if [[ -d "${BACKUP_DIR}/keystore" ]]; then
        log "Restaurando keystore"
        sudo mkdir -p "${OXP_HOME}/keystore"
        sudo cp -a "${BACKUP_DIR}/keystore/." "${OXP_HOME}/keystore/"
    fi

    keystore_file="$(read_env_property "KEYSTORE_OXP" "${OXP_HOME}/keystore/myKeystore")"
    keystore_pass="$(read_env_property "KEYSTOREPASS_OXP" "libertya")"
    keystore_alias="$(read_env_property "CODIGOALIASKEYSTORE_OXP" "libertya")"
    java_home_from_env="$(read_env_property "JAVA_HOME" "")"

    if [[ -z "${keystore_pass}" ]]; then
        keystore_pass="libertya"
    fi
    if [[ -z "${keystore_alias}" ]]; then
        keystore_alias="libertya"
    fi

    if [[ "${keystore_file}" != /* ]]; then
        log "KEYSTORE_OXP inválido (${keystore_file}), usando ${OXP_HOME}/keystore/myKeystore"
        keystore_file="${OXP_HOME}/keystore/myKeystore"
    fi

    if sudo test -f "${keystore_file}"; then
        return
    fi

    log "Keystore no encontrado en ${keystore_file}, generando uno nuevo"
    sudo mkdir -p "$(dirname "${keystore_file}")"

    keytool_cmd="keytool"
    if [[ -n "${java_home_from_env}" && -x "${java_home_from_env}/bin/keytool" ]]; then
        keytool_cmd="${java_home_from_env}/bin/keytool"
    elif ! command -v keytool >/dev/null 2>&1; then
        die "No se encontró keytool para generar ${keystore_file}"
    fi

    host_name="$(hostname -f 2>/dev/null || hostname)"
    sudo "${keytool_cmd}" \
        -genkeypair \
        -keyalg RSA \
        -alias "${keystore_alias}" \
        -dname "CN=${host_name}, OU=Libertya, O=Libertya, L=Buenos Aires, ST=Buenos Aires, C=AR" \
        -keypass "${keystore_pass}" \
        -storepass "${keystore_pass}" \
        -validity 3650 \
        -keystore "${keystore_file}" \
        >/dev/null
}

validate_oxp_home() {
    local p="$1"

    [[ -n "${p}" ]] || die "OXP_HOME vacío"
    [[ "${p}" = /* ]] || die "OXP_HOME debe ser una ruta absoluta: ${p}"

    case "${p}" in
        "/"|"/bin"|"/boot"|"/dev"|"/etc"|"/home"|"/lib"|"/lib64"|"/media"|"/mnt"|"/opt"|"/proc"|"/root"|"/run"|"/sbin"|"/srv"|"/sys"|"/tmp"|"/usr"|"/var")
            die "OXP_HOME apunta a una ruta crítica no permitida: ${p}"
            ;;
    esac

    [[ "${p}" != *".."* ]] || die "OXP_HOME no puede contener '..': ${p}"
}

safe_rm_rf() {
    local target="$1"
    local must_be_under="${2:-}"

    [[ -n "${target}" ]] || die "Intento de rm -rf con ruta vacía"
    [[ "${target}" = /* ]] || die "Intento de rm -rf con ruta no absoluta: ${target}"
    [[ "${target}" != "/" ]] || die "Intento de rm -rf sobre / bloqueado"

    if [[ -n "${must_be_under}" ]]; then
        if [[ "${must_be_under}" == "/" ]]; then
            [[ "${target}" == /* ]] || die "Ruta fuera de scope para rm -rf: ${target}"
        else
            [[ "${target}" == "${must_be_under}"/* ]] || die "Ruta fuera de scope para rm -rf: ${target}"
        fi
    fi

    sudo rm -rf -- "${target}"
}

cleanup_old_backups() {
    local keep_days="$1"
    local cutoff_arg

    [[ "${keep_days}" =~ ^[0-9]+$ ]] || die "BACKUP_RETENTION_DAYS inválido: ${keep_days}"
    cutoff_arg="+${keep_days}"

    log "Limpieza de backups viejos (> ${keep_days} días) en ${BASE_DIR}"

    while IFS= read -r old_backup; do
        [[ -n "${old_backup}" ]] || continue
        [[ "${old_backup}" == "${BACKUP_DIR}" ]] && continue
        log "Eliminando backup viejo: ${old_backup}"
        safe_rm_rf "${old_backup}" "${BASE_DIR}"
    done < <(find "${BASE_DIR}" -maxdepth 1 -type d -name "${APP_DIR}_backup_*" -mtime "${cutoff_arg}" 2>/dev/null || true)
}

stop_service() {
    if command -v systemctl >/dev/null 2>&1; then
        sudo systemctl stop "${SERVICE_NAME}" || sudo service "${SERVICE_NAME}" stop
    else
        sudo service "${SERVICE_NAME}" stop
    fi
}

start_service() {
    if command -v systemctl >/dev/null 2>&1; then
        sudo systemctl start "${SERVICE_NAME}" || sudo service "${SERVICE_NAME}" start
    else
        sudo service "${SERVICE_NAME}" start
    fi
}

rollback() {
    set +e

    if [[ "${DEPLOY_STARTED}" -eq 1 && -d "${BACKUP_DIR}" ]]; then
        log "Fallo detectado, iniciando rollback..."
        stop_service
        safe_rm_rf "${OXP_HOME}" "${BASE_DIR}"
        sudo mv "${BACKUP_DIR}" "${OXP_HOME}"
        sudo chown -R "${APP_USER}:${APP_GROUP}" "${OXP_HOME}"
        start_service
        log "Rollback completado."
    fi

    safe_rm_rf "${STAGE_DIR}" "/tmp"
}

trap 'rc=$?; if [[ $rc -ne 0 ]]; then rollback; else safe_rm_rf "${STAGE_DIR}" "/tmp"; fi; exit $rc' EXIT

validate_oxp_home "${OXP_HOME}"

if [[ ! -f "${ZIP_PATH}" ]]; then
    echo "No existe el ZIP a desplegar: ${ZIP_PATH}"
    exit 1
fi

if [[ ! -d "${OXP_HOME}" ]]; then
    echo "No existe instalación actual en ${OXP_HOME}"
    exit 1
fi

log "Descomprimiendo ${ZIP_PATH} en ${STAGE_DIR}"
unzip -q "${ZIP_PATH}" -d "${STAGE_DIR}"

NEW_HOME=""
if [[ -d "${STAGE_DIR}/${APP_DIR}" ]]; then
    NEW_HOME="${STAGE_DIR}/${APP_DIR}"
else
    shopt -s nullglob
    dirs=("${STAGE_DIR}"/*)
    shopt -u nullglob
    if [[ ${#dirs[@]} -eq 1 && -d "${dirs[0]}" ]]; then
        NEW_HOME="${dirs[0]}"
    else
        echo "No se pudo detectar el directorio raíz de la nueva instalación."
        exit 1
    fi
fi

log "Deteniendo servicio ${SERVICE_NAME}"
stop_service

log "Creando backup: ${BACKUP_DIR}"
sudo mv "${OXP_HOME}" "${BACKUP_DIR}"
DEPLOY_STARTED=1

log "Moviendo nueva versión a ${OXP_HOME}"
sudo mv "${NEW_HOME}" "${OXP_HOME}"

if [[ -f "${BACKUP_DIR}/LibertyaEnv.properties" ]]; then
    log "Restaurando LibertyaEnv.properties"
    sudo cp -f "${BACKUP_DIR}/LibertyaEnv.properties" "${OXP_HOME}/LibertyaEnv.properties"
fi

if [[ -d "${BACKUP_DIR}/lib/plugins" ]]; then
    log "Restaurando plugins custom"
    sudo mkdir -p "${OXP_HOME}/lib/plugins"
    find "${BACKUP_DIR}/lib/plugins" -maxdepth 1 -type f -name '*.jar' -exec sudo cp -f {} "${OXP_HOME}/lib/plugins/" \;
fi

restore_or_create_keystore

log "Aplicando permisos iniciales"
sudo chown -R "${APP_USER}:${APP_GROUP}" "${OXP_HOME}"
sudo find "${OXP_HOME}" -type f -name '*.sh' -exec chmod +x {} \;

log "Ejecutando ConfigurarAuto.sh"
JAVA_HOME_RUNTIME="$(read_env_property "JAVA_HOME" "")"
if [[ -n "${JAVA_HOME_RUNTIME}" && -x "${JAVA_HOME_RUNTIME}/bin/java" ]]; then
    log "Usando JAVA_HOME=${JAVA_HOME_RUNTIME} para ConfigurarAuto.sh"
    sudo env "JAVA_HOME=${JAVA_HOME_RUNTIME}" "PATH=${JAVA_HOME_RUNTIME}/bin:${PATH}" \
        bash -c "cd '${OXP_HOME}' && ./ConfigurarAuto.sh"
else
    sudo bash -c "cd '${OXP_HOME}' && ./ConfigurarAuto.sh"
fi

log "Aplicando permisos finales"
sudo chown -R "${APP_USER}:${APP_GROUP}" "${OXP_HOME}"

log "Iniciando servicio ${SERVICE_NAME}"
start_service

DEPLOY_STARTED=0
cleanup_old_backups "${BACKUP_RETENTION_DAYS}"
log "Deploy finalizado correctamente. Backup en ${BACKUP_DIR}"
