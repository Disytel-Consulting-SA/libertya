#!/bin/sh
# Iniciar Servidor openXpertya
#
# $Id: IniciarServidorDebug.sh,v 2.0 $

if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi

. ./Variables.sh Server
echo Iniciando Servidor openXpertya - $OXP_HOME \($NOMBRE_BD_OXP\)

OPCIONES_JAVA_OXP="$OPCIONES_JAVA_OXP -Djava.rmi.server.useLocalHostname=false -Djava.rmi.server.hostname=$SERVIDOR_APPS_OXP"
export OPCIONES_JAVA_OXP

# headless Esta opcion es por si no estan instaladas las X
JAVA_OPTS="-server $OPCIONES_JAVA_OXP -Djava.awt.headless=true"

export JAVA_OPTS

$JBOSS_HOME/bin/rundebug.sh -c openXpertya
