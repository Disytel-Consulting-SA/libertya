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
OXP_HOME=@OXP_HOME@
export OXP_HOME
JAVA_HOME=@JAVA_HOME@
export JAVA_HOME

#	Base de datos ...
USUARIO_BD_OXP=@USUARIO_BD_OXP@
export USUARIO_BD_OXP
PASSWD_BD_OXP=@PASSWD_BD_OXP@
export PASSWD_BD_OXP
URL_BD_OXP=@URL_BD_OXP@
export URL_BD_OXP

#	Valores de Oracle ...
RUTA_BD_OXP=oracle
export RUTA_BD_OXP
NOMBRE_BD_OXP=@NOMBRE_BD_OXP@
export NOMBRE_BD_OXP
SYSTEM_BD_OXP=@SYSTEM_BD_OXP@
export SYSTEM_BD_OXP

#	Directorios base adicionales ...
HOME_BD_OXP=$OXP_HOME/utils/$RUTA_BD_OXP
export HOME_BD_OXP
JBOSS_HOME=$OXP_HOME/jboss
export JBOSS_HOME

#	Servidor de aplicaciones ...
SERVIDOR_APPS_OXP=@SERVIDOR_APPS_OXP@
export SERVIDOR_APPS_OXP
PUERTO_WEB_OXP=@PUERTO_WEB_OXP@
export PUERTO_WEB_OXP
PUERTO_JNP_OXP=@PUERTO_JNP_OXP@
export PUERTO_JNP_OXP
#       Configuraciones SSL - ver jboss/server/openXpertya/deploy/jbossweb-tomcat55.sar/META-INF/jboss-service.xml
PUERTO_SSL_OXP=@PUERTO_SSL_OXP@
export PUERTO_SSL_OXP
KEYSTORE_OXP=@KEYSTORE_OXP@
export KEYSTORE_OXP
KEYSTOREPASS_OXP=@KEYSTOREPASS_OXP@
export KEYSTOREPASS_OXP

#	etc.
SERVIDOR_FTP_OXP=@SERVIDOR_FTP_OXP@
export SERVIDOR_FTP_OXP

#	Java
COMPIERE_JAVA=$JAVA_HOME/bin/java
export COMPIERE_JAVA
OPCIONES_JAVA_OXP="@OPCIONES_JAVA_OXP@ -DOXP_HOME=$OXP_HOME"
export OPCIONES_JAVA_OXP
CLASSPATH="$OXP_HOME/lib/OXP.jar:$OXP_HOME/lib/OXPXLib.jar"
export CLASSPATH

if [ $DOLLAR$# -eq 0 ] 
  then
    cp Variables.sh Variables.sav
fi
