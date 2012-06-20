#!/bin/sh
# Detener Servidor openXpertya
#
# $Id: DetenerServidor.sh,v 1.0 $

if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi

. ./Variables.sh Server
echo Detener Servidor openXpertya - $OXP_HOME \($NOMBRE_BD_OXP\)

JBOSS_LIB=$JBOSS_HOME/lib
export JBOSS_LIB
JBOSS_SERVERLIB=$JBOSS_HOME/server/openXpertya/lib
export JBOSS_SERVERLIB
JBOSS_CLASSPATH=$JBOSS_LIB/jboss-system.jar:$JBOSS_SERVERLIB/jnpserver.jar:$JBOSS_LIB/jboss-common.jar:$JBOSS_SERVERLIB/jmx-adaptor-plugin.jar:$JBOSS_SERVERLIB/jboss.jar:$JBOSS_SERVERLIB/jboss-transaction.jar:$JBOSS_SERVERLIB/jboss-j2ee.jar
export JBOSS_CLASSPATH

echo shutdown.sh --server=jnp://$SERVIDOR_APPS_OXP:$PUERTO_JNP_OXP
. $JBOSS_HOME/bin/shutdown.sh --server=jnp://$SERVIDOR_APPS_OXP:$PUERTO_JNP_OXP --shutdown
