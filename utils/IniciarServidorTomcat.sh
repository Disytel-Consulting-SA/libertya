#!/bin/sh
# Iniciar Servidor Libertya
#
# $Id: IniciarServidor.sh,v 2.0 $

if [ $OXP_HOME ]; then
  cd $OXP_HOME/tomcat/bin
  $OXP_HOME/tomcat/bin/IniciarServidor.sh
else
  echo "Variable OXP_HOME no seteada"
  exit 1;
fi
