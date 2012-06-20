# $Id: DetenerBD.sh,v 1.4  $
if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi
. ./Variables.sh Server
echo Detener Servicio de Base de Datos - $OXP_HOME \($NOMBRE_BD_OXP\)


sh $RUTA_BD_OXP/Detener.sh

