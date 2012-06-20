#	This script compile Compiere 
#	Ported from Windows script Marek Mosiewicz<marek.mosiewicz@jotel.com.pl>


SAVED_DIR=`pwd`			#save current dir
cd `dirname $0`/../utils_dev	#change dir to place where script resides - doesn not work with sym links
UTILS_DEV=`pwd`			#this is compiere source
cd $SAVED_DIR			#back to the saved directory

.  $UTILS_DEV/VariablesCompilacion.sh	#call environment

if [ ! $ENV_OXP==Y ] ; then
    echo "Can't set developemeent environemnt - check VariablesCompilacion.sh"
    exit 1
fi

echo "Stop Apps Server (waiting)"
$OXP_HOME/utils/DetenerServidor.sh
sleep 5

echo Building ...
$JAVA_HOME/bin/java -Dant.home="." $ANT_PROPERTIES org.apache.tools.ant.Main complete

ls $INSTALACION_OXP

echo	Cleaning up ...
rm -r -f $OXP_HOME/jboss/server/compiere/tmp

echo	Starting Apps Server ...
$OXP_HOME/utils/IniciarServidor.sh

exit 0
