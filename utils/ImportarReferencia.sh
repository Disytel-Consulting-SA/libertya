#!/bin/sh
#
# $Id: ImportarReferencia.sh,v 1.0 $

if [ $OXP_HOME ]; then
  cd $OXP_HOME/utils
fi
. ./Variables.sh Server
echo Importar OpenXpertya - $OXP_HOME \($NOMBRE_BD_OXP\)


echo ReCrear Usuario e Importar $OXP_HOME/data/openxpv2.dmp - \($NOMBRE_BD_OXP\)
echo == La importacion mostrar avisos, es normal ==
ls -lsa $OXP_HOME/data/openxpv2.dmp
echo Presione Entrar para Continuar ...
read in

# Parameter: <systemAccount> <USUARIO_BD_OXP> <PASSWD_BD_OXP>
sh $RUTA_BD_OXP/ImportarOXP.sh system/$SYSTEM_BD_OXP reference reference
