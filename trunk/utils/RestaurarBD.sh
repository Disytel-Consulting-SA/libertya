# $Id: RestaurarBD.sh,
if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi
. ./VariablesCompilacion.sh Server
echo Restaurar Base de Datos openXpertya Desde la Exportacion- $OXP_HOME \($NOMBRE_BD_OXP\)


echo Recrear Usuario e Importacion desde $OXP_HOME/data/ExpDat.dmp
echo == La importacion mostrara avisos. Correcto ==
ls -lsa $OXP_HOME/data/ExpDat.dmp
echo Presione enter para continuar ...
read in

sh $RUTA_BD_OXP/Restaurar_BD.sh system/$SYSTEM_BD_OXP $USUARIO_BD_OXP $PASSWD_BD_OXP
