#!/bin/sh
#
# $Id: Openxp.sh,v $
echo Cliente openXpertya $OXP_HOME

#	Se reemplazara
OXP_HOME=/ServidorOXP
#JAVA_HOME=/usr/lib/java

##	Check Java Home
if [ $JAVA_HOME ]; then
  JAVA=$JAVA_HOME/bin/java
else
  JAVA=java
  echo JAVA_HOME No se ha Establecido
  echo   No es posible Iniciar openXpertya
  echo   Establezca JAVA_HOME al directorio donde se encuentre local JDK.
fi

## Testear OXP_HOME
if [ $OXP_HOME ]; then
  CLASSPATH=$OXP_HOME/lib/OXP.jar:$OXP_HOME/lib/OXPXLib.jar:/app/postgresql/postgresql-8.0-310.jdbc3.jar:$CLASSPATH
else
  CLASSPATH=lib/OXP.jar:lib/OXPXLib.jar:/app/postgresql/postgresql-8.0-310.jdbc3.jar:lib/oracle.jar:$CLASSPATH
  echo OXP_HOME no se ha Establecido
 echo   No es posible Iniciar openXpertya
  echo   Establezca OXP_HOME al directorio ServidorOXP
fi


# Para Cambiar entre multiples instalaciones mire el archivo.properties

PROP=
#PROP=-DPropertyFile=test.properties

$JAVA -Xms32m -Xmx512m -DOXP_HOME=$OXP_HOME $PROP -classpath $CLASSPATH org.openXpertya.db.CrearOXP
