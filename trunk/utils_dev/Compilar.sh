#!/bin/sh
#	Este guión compila y construye openXpertya



SAVED_DIR=`pwd`			# Guarda el Directorio Actual
cd `dirname $0`			# Cambia al directorio donde reside el Script
UTILS_DEV=`pwd`			# Esta es la ubicación del directorio donde residen los guiones base para compilar
cd $SAVED_DIR			# Vuelta al directorio Base

.  $UTILS_DEV/VariablesCompilacion.sh	# Establece las Variables de entorno necesarias


if [ ! $ENV_OXP==Y ] ; then
    echo "No puedo fijar las variables de entorno necesarias, compruebe el fichero VariablesCompilacion.sh"
    exit 1
fi

echo Limpiando ...
cd $UTILS_DEV
$JAVA_HOME/bin/java -Dant.home="." $ANT_PROPERTIES org.apache.tools.ant.Main clean

echo Construyendo ...
$JAVA_HOME/bin/java -Dant.home="." $ANT_PROPERTIES org.apache.tools.ant.Main -logger org.apache.tools.ant.listener.MailLogger complete

ls -la $INSTALACION_OXP

echo Todo hecho ...

exit 0

