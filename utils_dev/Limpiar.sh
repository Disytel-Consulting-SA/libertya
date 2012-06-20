#!/bin/sh
#       Este guión limpia los ficheros temporales generados al compilar

SAVED_DIR=`pwd`                 # Guarda el directorio actual
cd `dirname $0`/../utils_dev    # Cambia al Directorio del Script
UTILS_DEV=`pwd`                 # Este es el directorio base de las utilidades para compilar
cd $SAVED_DIR                   # Vuelta al directorio original

. $UTILS_DEV/VariablesCompilacion.sh    # Establece las variables de entorno necesarias para compilar

if [ ! $ENV_OXP==Y ] ; then
    echo "No se puede encontrar las variables de compilacion - Compruebe el fichero VariablesCompilacion.sh"
    exit 1
fi

echo "->-> Limpiando ..."
cd $UTILS_DEV
$JAVA_HOME/bin/java -Dant.home="." $ANT_PROPERTIES org.apache.tools.ant.Main clean

echo "->->-> Todo realizado ..."

exit 0

