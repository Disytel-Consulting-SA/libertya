#	Este Script instala OPENXPERTYA



SAVED_DIR=`pwd`			#Guardar Directorio Actual
cd `dirname $0`/../utils_dev	#Cambiar a donde reside el Script
UTILS_DEV=`pwd`			#Esta es la carpeta de fuentes openXpertya
cd $SAVED_DIR			#Vuelta al Directorio

.  $UTILS_DEV/VariablesCompilacion.sh	#Llamar a las Variables


if [ ! $ENV_OXP==Y ] ; then
    echo "No encuentra el fichero de variables VariablesCompilacion.sh"
    exit 1
fi

echo Instalando ...
$JAVA_HOME/bin/java -Dant.home="." $ANT_PROPERTIES org.apache.tools.ant.Main install

echo Done ...

exit 0
