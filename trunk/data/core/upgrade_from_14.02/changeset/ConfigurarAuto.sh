#!/bin/bash
#export JAVA=${JAVA_HOME}/bin/java
if [ "$JAVA_HOME" = "" ]; then
export JAVA=java
echo JAVA_HOME no est� asignado.  
echo Es posible que la configuraci�n no pueda realizarse correctamente!!
echo Se debe asignar JAVA_HOME al directorio en donde se encuentra el JDK 1.6.
else
export JAVA=${JAVA_HOME}/bin/java	
fi

echo =======================================
echo Comenzando Configuraci�n ...
echo =======================================

# Classpath
export CP=lib/OXPInstall.jar:lib/OXP.jar:lib/CCTools.jar:lib/oracle.jar:lib/jboss.jar:lib/postgresql.jar:lib/log4j.jar:lib/mail.jar

# Par�metro de configuraci�n de log, e.g. export ARGS=ALL
export ARGS=CONFIG


$JAVA -classpath $CP -DOXP_HOME=$OXP_HOME -Djava.awt.headless=true org.openXpertya.install.SilentSetup $ARGS

