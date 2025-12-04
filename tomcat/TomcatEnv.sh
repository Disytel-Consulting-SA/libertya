#!/bin/bash

# Carga entorno Libertya + Tomcat a partir de LibertyaEnv.properties

# Si no viene OXP_HOME del entorno, uso el del properties por default
PROP_BASE="${OXP_HOME:-/ServidorOXP}"
PROP_FILE="$PROP_BASE/LibertyaEnv.properties"

if [ ! -r "$PROP_FILE" ]; then
  echo "ERROR: No se puede leer $PROP_FILE"
  return 1 2>/dev/null || exit 1
fi

# OPCIONES_JAVA_OXP
OPCIONES_JAVA_OXP=$(grep '^OPCIONES_JAVA_OXP=' "$PROP_FILE" | tail -n 1 | cut -d= -f2- | tr -d '\r')

# JAVA_HOME
JAVA_HOME_PROP=$(grep '^JAVA_HOME=' "$PROP_FILE" | tail -n 1 | cut -d= -f2- | tr -d '\r')

# Si no vino nada en OPCIONES_JAVA_OXP, pongo un default
if [ -z "$OPCIONES_JAVA_OXP" ]; then
  OPCIONES_JAVA_OXP="-Xms512M -Xmx1024M"
fi

# Exporto JAVA_HOME / JRE_HOME si están definidos
if [ -n "$JAVA_HOME_PROP" ]; then
  export JAVA_HOME="$JAVA_HOME_PROP"
  # Para simplificar, igualo JRE_HOME a JAVA_HOME
  export JRE_HOME="$JAVA_HOME_PROP"
fi

# Armo un CATALINA_OPTS base
BASE_CATALINA_OPTS="$OPCIONES_JAVA_OXP -Djava.awt.headless=true -DOXP_HOME=$PROP_BASE -Dfile.encoding=UTF-8"
export BASE_CATALINA_OPTS

