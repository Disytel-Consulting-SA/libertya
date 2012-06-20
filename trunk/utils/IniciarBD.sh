# $Id: IniciarBD.sh,v 1.4 2005/01/22 21:59:15 jjanke Exp $
if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi
. ./myEnvironment.sh Server
echo Start DataBase Service - $OXP_HOME \($NOMBRE_BD_OXP\)


sh $RUTA_BD_OXP/Start.sh

