#!/bin/sh
#
echo Instalar Servidor Libertya
# $Header: /ServidorOXP/Configurar.sh, v 2.0 $

if [ $JAVA_HOME ]; then
  JAVA=$JAVA_HOME/bin/java
  KEYTOOL=$JAVA_HOME/bin/keytool
else
  JAVA=java
  KEYTOOL=keytool
  echo la variable JAVA_HOME no ha sido establecida.
  echo No es posible iniciar configurar openXpertya
  echo fijar JAVA_HOME al directorio of su JDK local.
fi


echo ===================================
echo Dialogo de Configuración
echo ===================================
CP=lib/OXPInstall.jar:lib/OXP.jar:lib/XOXPTools.jar:lib/oracle.jar:lib/sybase.jar:lib/jboss.jar:lib/postgresql.jar:

# Trace Level Parameter, e.g. ARGS=ALL
ARGS=CONFIG

# To test the OCI driver, add -DTestOCI=Y to the command - example:

$JAVA -classpath $CP -DOXP_HOME=$OXP_HOME org.openXpertya.install.Setup $ARGS


#echo ===================================
#echo Instalar Variables Libertya
#echo ===================================
#$JAVA -classpath $CP -DCOMPIERE_HOME=$OXP_HOME -Dant.home="." org.apache.tools.ant.launch.Launcher setup


echo ===================================
echo Hacer .sh Ejecutables & set Env
echo ===================================
chmod -R a+x *.sh
find . -name '*.sh' -exec chmod a+x '{}' \;

. utils/VariablesUnix.sh

#echo ================================
#echo	Probando la conexión local
#echo ================================
#%JAVA% -classpath lib/OXP.jar:lib/OXPLib.jar org.openXpertya.install.ConnectTest localhost

echo .
echo "En caso de problemas, verifique el archivo de log en el directorio base de Libertya"
