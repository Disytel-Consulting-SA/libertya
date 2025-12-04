#!/bin/bash

echo "Iniciando Servidor Libertya - $OXP_HOME"

# Cargar entorno común
PROP_BASE="${OXP_HOME:-/ServidorOXP}"
. "$PROP_BASE/tomcat/bin/TomcatEnv.sh" || exit 1

export CATALINA_OPTS="$BASE_CATALINA_OPTS"

./startup.sh
