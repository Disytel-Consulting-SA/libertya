#	Mis Variables de Compilacion
#
#	Este Script define las Variables para la compilacion de todo openXpertya
#	
#	
# 	$Header: /oxp2_2/utils_dev/PlantillaVariablesCompilacion.sh,#
#  Testea los siguientes parametros:
#  -------------------------------
JAVA_HOME=/app/java/jdk1.5.0_03
#JAVA_HOME=/app/java/j2sdk1.4.2_07
export FUENTES_OXP=/oxp2_2/
#export JAVA_HOME;
#JAVA_HOME=/usr/lib/java
export JAVA_HOME;
if [ $JAVA_HOME ]; then
  export PATH=$JAVA_HOME/bin:$PATH	
else
  echo JAVA_HOME is not set.
  echo You may not be able to build Compiere
  echo Set JAVA_HOME to the directory of your local JDK.
fi


#Establecer la Carpeta oxp2_2 - Por defecto uno mas arriba de donde reside el script

SAVED_DIR=`pwd`			#Guardar el Directorio actual
cd `dirname $0`/..		#Cambiar el directorio a uno mas arriba
#export FUENTES_OXP=`pwd`	#Estas son las fuentes de openXpertya
cd $SAVED_DIR			#Vuelta al Directorio

echo FUENTES_OXP is $FUENTES_OXP

if [ ! -d $FUENTES_OXP/client ] ; then
	echo "** FUENTES_OXP NO ENCONTRADAS"
fi  

#	Contraseñas para la KeyStore
export KEYTOOL_PASS=$JJ_PASSWORD
if [ ! $KEYTOOL_PASS ] ; then
	export KEYTOOL_PASS=openxp
fi

#	Contraseña para FTP Y KEYSTORE
export ANT_PROPERTIES=-Dpassword=$KEYTOOL_PASS

#	Enviar un mail mediante Ant - Cambiar o Borrar
export ANT_PROPERTIES="$ANT_PROPERTIES -DMailLogger.mailhost=xxx -DMailLogger.from=xxxx -DMailLogger.failure.to=xxxx -DMailLogger.success.to=xxxx"

#	Instalacion automatica, donde sera descompreso openXpertya
export ROOT_OXP=/Instalacion_OXP

#	Instalacion Automatica, donde se generara la carpeta final openXpertya
export OXP_HOME=$ROOT_OXP/ServidorOXP

#	Instalacion Automatica
export INSTALACION_OXP=/Instalacion_OXP
if [ ! -d $INSTALACION_OXP ] ; then
    mkdir -p $INSTALACION_OXP
fi  

#  ---------------------------------------------------------------
#  En condiciones generales no tiene porque tocar nada por debajo
#  de esta linea,
#  ---------------------------------------------------------------

export CURRENTDIR=`pwd`

#  Set Version
export VERSION_OXP=openXpertya
export VERSION_OXP_FILE=v2
export CONSULTORA_OXP=FUNDESLE

#	ClassPath
if  [ ! -f $JAVA_HOME/lib/tools.jar ] ; then
	echo "** Es necesario JAVA SDK **"
fi
export CLASSPATH=$CLASSPATH:$JAVA_HOME/lib/tools.jar
if  [ ! -f $FUENTES_OXP/jboss/server/openXpertya/lib/NetComponents-1.3.8.jar ] ;then
	echo "** NetComponents NOT found **"
fi
export CLASSPATH=$CLASSPATH:$FUENTES_OXP/jboss/server/openXpertya/lib/NetComponents-1.3.8.jar

if  [ ! -f $FUENTES_OXP/tools/lib/ant.jar ] ;then
	echo "** Ant.jar NOT found **"
fi
export CLASSPATH=$CLASSPATH:$FUENTES_OXP/tools/lib/ant.jar:$CLASSPATH:$FUENTES_OXP/tools/lib/ant-launcher.jar:$FUENTES_OXP/tools/optional.jar:$FUENTES_OXP/jboss/lib/xml-apis.jar


#	Set XDoclet 1.1.2 Environment
export XDOCLET_HOME=$FUENTES_OXP/tools

#	.
#	This is the keystore for code signing.
#	Replace it with the official certificate.
#	Note that this is not the SSL certificate.
#	.

if [ ! -d $FUENTES_OXP/keystore ] ; then
    mkdir $FUENTES_OXP/keystore			#create dir
fi    
	    
# check 	
if  [ ! -f $FUENTES_OXP/keystore/myKeystore ] || [ ! "keytool -list -alias openxp -keyStore $FUENTES_OXP/keystore/myKeystore -storepass $KEYTOOL_PASS" ] ; then		     
     # 	This is a dummy keystore for localhost SSL		     
     #	replace it with your SSL certifificate.		     
     #	Please note that a SSL certificate is 		     
     #	different from the code signing certificate.
     #	The SSL does not require an alias of openxp and		     
     #	there should only be one certificate in the keystore
		     
    HOSTNAME=`hostname`
			
			
    echo No Keystore found, creating for $HOSTNAME ...
			    
    KEYTOOL_DNAME="CN=$HOSTNAME, OU=myName, O=UsuarioOXP, L=myTown, ST=myState, C=US"

    keytool -genkey -keyalg rsa -alias openxp -dname "$KEYTOOL_DNAME" -keypass $KEYTOOL_PASS -validity 365 -keystore $FUENTES_OXP/keystore/myKeystore -storepass $KEYTOOL_PASS
    keytool -selfcert -alias openxp -dname "$KEYTOOL_DNAME" -keypass $KEYTOOL_PASS -validity 180 -keystore $FUENTES_OXP/keystore/myKeystore -storepass $KEYTOOL_PASS
fi

# Set ENV_OXP for all other scripts.
export ENV_OXP=Y
