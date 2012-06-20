# Author + Copyright 1999-2005 Jorg Janke
# $Id: RUN_sqlj.sh,v 1.1 2005/05/31 07:28:21 jjanke Exp $
if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi
. ./myEnvironment.sh Server
echo 	Create Oracle SQLJ - $OXP_HOME \($NOMBRE_BD_OXP\)

sh $RUTA_BD_OXP/create.sh $USUARIO_BD_OXP/$PASSWD_BD_OXP

