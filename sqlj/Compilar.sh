#!/bin/sh
# Script de Compilacion migrado desde el Script de Windows

SAVED_DIR=`pwd`			#Guardar el Directorio Actual
# cd `dirname $0`/../utils_dev	#Cambiar el Directorio donde reside el Script
# UTILS_DEV=`pwd`			#Esta es la Carpeta de Fuentes de openXpertya
cd $SAVED_DIR			#Vuelta al Directorio 

. ../utils_dev/VariablesCompilacion.sh	#Llamada a las Variables
echo "--> Hecho"
echo "Ejecutando ANT"
$JAVA_HOME/bin/java -Dant.home="." $ANT_PROPERTIES org.apache.tools.ant.Main clean
$JAVA_HOME/bin/java -Dant.home="." $ANT_PROPERTIES org.apache.tools.ant.Main sqljDist

if [ ! $ENV_OXP==Y ] ; then
   echo "No se pueden establecer las Variables de compilacion - Testear VariablesCompilacion.sh"
   exit 1   
fi
echo "--> Todo hecho"



