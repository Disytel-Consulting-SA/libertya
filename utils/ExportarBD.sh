# $Id: ExportarBD.sh,v 1.9 2005/04/27 17:45:02 jjanke Exp $
if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi
. ./Variables.sh Server
echo 	Exportar Base de Datos openXpertya - $OXP_HOME \($NOMBRE_BD_OXP\)


# Parameter: <USUARIO_BD_OXP>/<PASSWD_BD_OXP>
sh $RUTA_BD_OXP/Exportar_BD.sh $USUARIO_BD_OXP $PASSWD_BD_OXP

# sh $RUTA_BD_OXP/Exportar_TODO_BD system $SYSTEM_BD_OXP

if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi
sh CopiarBD.sh

