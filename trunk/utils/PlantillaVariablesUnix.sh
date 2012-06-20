#!/bin/sh
#
echo Fija las variables de entorno en Unix
# $Id: PlantillaVariablesUnix.sh,v 2.3 $

echo ===================================
echo Configura el entorno del cliente
echo ===================================

echo Por favor, incluya OXP_HOME y JAVA_HOME en su entorno

JAVA_HOME=@JAVA_HOME@
export JAVA_HOME
OXP_HOME=@OXP_HOME@
export OXP_HOME

echo Puede ser necesario fijar LD_LIBRARY_PATH si su sistema lo necesita

# Las siguientes variables de entorno, manuales, son necesarias para utilizar Oracle
# ORACLE_HOME=/var/oracle/OraHome
# export ORACLE_HOME

# Compruebe la documentación de instalación de Oracle para mayores detalles
# LD_LIBRARY_PATH=$ORACLE_HOME/lib
# export LD_LIBRARY_PATH
