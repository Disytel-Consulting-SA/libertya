#!/bin/sh
#
if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi
. ./VariablesCompilacion.sh Server
echo Importar openXpertya - $OXP_HOME \($NOMBRE_BD_OXP\)


echo Recrear openXpertya $OXP_HOME/data/openxpv2.dmp - \($NOMBRE_BD_OXP\)
echo == La importacion mostrara avisos. CORRECTO ==
ls -lsa $OXP_HOME/data/openxpv2.dmp
echo Presione Entrar para continuar ...
read in


sh $RUTA_BD_OXP/ImportarOXP.sh system/$SYSTEM_BD_OXP $USUARIO_BD_OXP $PASSWD_BD_OXP
