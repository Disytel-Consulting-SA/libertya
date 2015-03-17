#!/bin/sh
#
#	Mis Variables de Compilacion
#
#	Este fichero de proceso por lotes define las Variables para la compilacion de todo Libertya bajo Linux y derivados	
#	
# 	$Header: /oxp2_2/utils_dev/VariablesCompilacion.sh,#
#
#  Comprueba los siguientes parametros:
#  -------------------------------
# export JAVA_HOME=/usr/local/java/
export FUENTES_OXP=..

if [ $JAVA_HOME ]; then
  export PATH=$JAVA_HOME/bin:$PATH
  echo "-> JAVA HOME configurado"	
else
  echo "JAVA_HOME no configurado"
  echo "Posiblemente no se pueda compilar Libertya"
  echo "Configura JAVA_HOME al directorio de tu JDK local."
fi

#Establecer la Carpeta oxp2_2 - Por defecto uno mas arriba de donde reside el script

SAVED_DIR=`pwd`			# Guarda el Directorio actual
cd `dirname $0`/..		# Cambia el directorio a uno mas arriba
# export FUENTES_OXP=`pwd`	# Estas son las fuentes de Libertya (si no se establece anteriormente)
cd $SAVED_DIR			# Vuelta al Directorio inicial
echo "-> FUENTES_OXP es $FUENTES_OXP"

if [ ! -d $FUENTES_OXP/client ] ; then
	echo "** FUENTES_OXP no encontradas **"
    else
        echo "-> FUENTES_OXP correctamente localizadas"
fi  

#	Contraseñas para la KeyStore
export KEYTOOL_PASS=$JJ_PASSWORD
if [ ! $KEYTOOL_PASS ] ; then
	export KEYTOOL_PASS=openxp
fi
echo "-> La contraseña para la KeyStore es $KEYTOOL_PASS"

#	Contraseña para FTP Y KEYSTORE
export ANT_PROPERTIES=-Dpassword=$KEYTOOL_PASS
#	Enviar un mail mediante Ant - Cambiar o Borrar
export ANT_PROPERTIES="$ANT_PROPERTIES -DMailLogger.mailhost=xxx -DMailLogger.from=xxxx -DMailLogger.failure.to=xxxx -DMailLogger.success.to=xxxx"
#	Instalacion automatica, donde se descomprime Libertya
export ROOT_OXP=/
#	Instalacion Automatica, donde se genera la carpeta final Libertya
export OXP_HOME=/ServidorOXP
echo "-> OXP_HOME es $OXP_HOME"
#	Instalacion Automatica
INSTALACION_OXP=/install
export INSTALACION_OXP
if [ ! -d $INSTALACION_OXP ] ; then
    mkdir -p $INSTALACION_OXP
    echo "-> Creamos el directorio de instalación $INSTALACION_OXP"
   else
    echo "-> Ya existe el directorio de instalación $INSTALACION_OXP"
fi  

#  ---------------------------------------------------------------
#  En condiciones generales no tiene porque tocar nada por debajo
#  de esta linea,
#  ---------------------------------------------------------------

export CURRENTDIR=`pwd`
#  Set Version
export VERSION_OXP=Libertya
export VERSION_OXP_FILE=V15.03
export CONSULTORA_OXP=SERVICIOS_DIGITALES

#	ClassPath
if  [ ! -f $JAVA_HOME/lib/tools.jar ] ; then
	echo "** Es necesario JAVA SDK **"
    else
        echo "-> ClassPath correctamente configurado"
fi
export CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/tools.jar

# obsoleto si hay commons-net if  [ ! -f $FUENTES_OXP/jboss/server/openXpertya/lib/ NetComponents-1.3.8.jar ] ;then
#	echo "** NetComponents NO encontrado **"
# fi
# export CLASSPATH=$CLASSPATH:$FUENTES_OXP/jboss/server/openXpertya/lib/NetComponents-1.3.8.jar

if  [ ! -f $FUENTES_OXP/tools/lib/ant.jar ] ;then
	echo "** Ant.jar NO encontrado **"
fi
export CLASSPATH=$CLASSPATH:$FUENTES_OXP/tools/lib/ant.jar:$CLASSPATH:$FUENTES_OXP/tools/lib/ant-launcher.jar:$FUENTES_OXP/tools/lib/ant-swing.jar:$FUENTES_OXP/tools/lib/ant-commons-net.jar:$FUENTES_OXP/tools/lib/commons-net.jar:$FUENTES_OXP/lib/jdom.jar:$FUENTES_OXP/jboss/lib/endorsed/xml-apis.jar


#	Configura el entorno de XDoclet 
export XDOCLET_HOME=$FUENTES_OXP/tools
#	.
#	Esta es la keystore para firmar el código.
#	Reemplazar con el certificado oficial.
#	 - Éste no es el certificado SSL -
#	.

if [ ! -d $FUENTES_OXP/keystore ] ; then
    mkdir $FUENTES_OXP/keystore			# crea el directorio
    echo "-> Creamos el directorio $FUENTES_OXP/keystore"
  else
    echo "-> El directorio $FUENTES_OXP/keystore ya existe"
fi    

# comprobar 	
if  [ ! -f $FUENTES_OXP/keystore/myKeystore ] || [ ! "keytool -list -alias openxp -keyStore $FUENTES_OXP/keystore/myKeystore -storepass $KEYTOOL_PASS" ] ; then		     
     # 	Esta es una keystore de prueba para acceso SSL al servidor		     
     #	reemplazar con el certificado SSL oficial (de utilizarlo).		     
     #	El certificado SSL es diferente del certificado para fimar el código 		     
     #	El certificado SSL no precisa un alias de openxp y debería de haber		     
     #	tan sólo un certificado en la keystore.

    HOSTNAME=`hostname`	
    echo "** No se encuentra la Keystore, creando una para $HOSTNAME ..."
    KEYTOOL_DNAME="CN=$HOSTNAME, OU=SERVICIOS_DIGITALES, O=SERVICIOS_DIGITALES, L=CF, ST=BSAS, C=AR"
    keytool -genkey -keyalg rsa -alias openxp -dname "$KEYTOOL_DNAME" -keypass $KEYTOOL_PASS -validity 365 -keystore $FUENTES_OXP/keystore/myKeystore -storepass $KEYTOOL_PASS
    keytool -selfcert -alias openxp -dname "$KEYTOOL_DNAME" -keypass $KEYTOOL_PASS -validity 180 -keystore $FUENTES_OXP/keystore/myKeystore -storepass $KEYTOOL_PASS
fi

# Set ENV_OXP for all other scripts.
export ENV_OXP=Y
echo "Finalizando, configuramos ENV_OXP a $ENV_OXP para otros guiones"


