#!/bin/sh
#
# Define las variables utilizadas por openXpertya
# No editar directamente. Utilizar -Configurar.sh-
#
# $Id: Variables.sh,v 2.3$

echo Configurando el entorno ....
#	El cliente necesita
#		OXP_HOME
#		JAVA_HOME 
#	El servidor adicionalmente necesita
#               NOMBRE_BD_OXP   (para Oracle)
#               palabras de paso
#

# 	Directorios base de las instalaciones ...
OXP_HOME=/ServidorOXP
export OXP_HOME
JAVA_HOME=/usr/java/jdk1.5.0_14
export JAVA_HOME

#	Base de datos ...
USUARIO_BD_OXP=openxp
export USUARIO_BD_OXP
PASSWD_BD_OXP=openxp
export PASSWD_BD_OXP
URL_BD_OXP=jdbc:postgresql://demo.openxpertya.org:5432/
export URL_BD_OXP

#	Valores de Oracle ...
RUTA_BD_OXP=oracle
export RUTA_BD_OXP
NOMBRE_BD_OXP=
export NOMBRE_BD_OXP
SYSTEM_BD_OXP=openxp
export SYSTEM_BD_OXP

#	Directorios base adicionales ...
HOME_BD_OXP=$OXP_HOME/utils/$RUTA_BD_OXP
export HOME_BD_OXP
JBOSS_HOME=$OXP_HOME/jboss
export JBOSS_HOME

#	Servidor de aplicaciones ...
SERVIDOR_APPS_OXP=demo.openxpertya.org
export SERVIDOR_APPS_OXP
PUERTO_WEB_OXP=80
export PUERTO_WEB_OXP
PUERTO_JNP_OXP=1099
export PUERTO_JNP_OXP
#       Configuraciones SSL - ver jboss/server/openXpertya/deploy/jbossweb-tomcat55.sar/META-INF/jboss-service.xml
PUERTO_SSL_OXP=443
export PUERTO_SSL_OXP
KEYSTORE_OXP=/ServidorOXP/keystore/myKeystore
export KEYSTORE_OXP
KEYSTOREPASS_OXP=openxp
export KEYSTOREPASS_OXP

#	etc.
SERVIDOR_FTP_OXP=demo.openxpertya.org
export SERVIDOR_FTP_OXP

#	Java
JAVA_OXP=$JAVA_HOME/bin/java
export JAVA_OXP
OPCIONES_JAVA_OXP="-Xms64M -Xmx512M -DOXP_HOME=$OXP_HOME"
export OPCIONES_JAVA_OXP
CLASSPATH="$OXP_HOME/lib/OXP.jar:$OXP_HOME/lib/OXPXLib.jar"
export CLASSPATH

if [ $DOLLAR$# -eq 0 ] 
  then
    cp Variables.sh Variables.sav
fi
