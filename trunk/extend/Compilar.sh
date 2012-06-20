# Script de Compilacion
# Migrado desde el Script de Windows


SAVED_DIR=`pwd`			#Guardar el Directorio Actual
cd `dirname $0`/../utils_dev	#Cambiar el Directorio donde reside el Script
UTILS_DEV=`pwd`			#Esta es la Carpeta de Fuentes de openXpertya
cd $SAVED_DIR			#Vuelta al Directorio 

.  $UTILS_DEV/VariablesCompilacion.sh	#Llamada a las Variables
echo done
if [ ! $ENV_OXP==Y ] ; then
    echo "No se pueden establecer las Variables de compilacion - Testear VariablesCompilacion.sh"
    exit 1
fi

echo running Ant
$JAVA_HOME/bin/java -Dant.home="." $ANT_PROPERTIES org.apache.tools.ant.Main
