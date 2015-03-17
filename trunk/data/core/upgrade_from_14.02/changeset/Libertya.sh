#!/bin/sh
#
# $Id: Libertya.sh,v 2.0 $
echo Cliente Libertya v15.03 $OXP_HOME

#	Establecer Directamente para Sobreescribir
#OXP_HOME=/ServidorOXP
#JAVA_HOME=/usr/lib/jvm/java

## Comprueba JAVA_HOME
if [ $JAVA_HOME ]; then
  JAVA=$JAVA_HOME/bin/java
else
  JAVA=java
  echo JAVA_HOME no esta establecido
  echo   Debe establecer la variable JAVA_HOME
  echo   Establecer JAVA_HOME al directorio base del JDK de java.
fi

## Comprueba OXP_HOME
if [ $OXP_HOME ]; then
  CLASSPATH=$OXP_HOME/lib/OXP.jar:$OXP_HOME/lib/OXPXLib.jar:$OXP_HOME/lib/CMPCS.jar:$OXP_HOME/lib/JasperReports.jar:$CLASSPATH
else
  CLASSPATH=lib/OXP.jar:lib/OXPXLib.jar:lib/CMPCS.jar:lib/JasperReports.jar:$CLASSPATH
  echo  La variable OXP_HOME no aparece establecida
  echo   Deebe establecer la variable OXP_HOME 
  echo   al directorio base del servidor de Libertya.
fi


# Para intercambiar entre diversas instalaciones, copia los ficheros libertya.properties 
# Selecciona la Variable PROP
PROP=
#PROP=-DPropertyFile=test.properties

$JAVA -Xms32m -Xmx512m -Dfile.encoding=UTF-8 -DOXP_HOME=$OXP_HOME $PROP -classpath $CLASSPATH org.openXpertya.OpenXpertya
