#!/bin/sh
#
echo Instalar Servidor openXpertya
# $Header: /openxp/install/ServidorOXP/Configurar.sh,v 1.0 $

if [ $JAVA_HOME ]; then
  JAVA=$JAVA_HOME/bin/java
  KEYTOOL=$JAVA_HOME/bin/keytool
else
  JAVA=java
  KEYTOOL=keytool
  echo JAVA_HOME is not set.
  echo You may not be able to start the Setup
  echo Set JAVA_HOME to the directory of your local JDK.
fi


echo ===================================
echo Setup Dialog
echo ===================================
CP=lib/OXPInstall.jar:lib/OXP.jar:lib/XOXPTools.jar:lib/oracle.jar:lib/sybase.jar:lib/jboss.jar:lib/postgresql.jar:

# Trace Level Parameter, e.g. ARGS=ALL
ARGS=CONFIG

# To test the OCI driver, add -DTestOCI=Y to the command - example:

$JAVA -classpath $CP -DOXP_HOME=$OXP_HOME org.openXpertya.install.Setup $ARGS


#echo ===================================
#echo Instalar Variables openXpertya
#echo ===================================
#$JAVA -classpath $CP -DCOMPIERE_HOME=$OXP_HOME -Dant.home="." org.apache.tools.ant.launch.Launcher setup


echo ===================================
echo Hacer .sh Ejecutables & set Env
echo ===================================
chmod -R a+x *.sh
find . -name '*.sh' -exec chmod a+x '{}' \;

. utils/VariablesUnix.sh

#echo ================================
#echo	Test local Connection
#echo ================================
#%JAVA% -classpath lib/OXP.jar:lib/OXPLib.jar org.openXpertya.install.ConnectTest localhost

echo .
echo For problems, check log file in base directory
