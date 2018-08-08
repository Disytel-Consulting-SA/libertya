@Rem   Variables necesarias para el entorno de desarrollo.
@Rem
@Rem   Este script fija las variables para compilar Libertya desde sus fuentes.
@Rem   Versi�n estandard 2.1 por Fundesle.	
@Rem	
@Rem  Es necesario comprobar y ajustar los siguientes par�metros de acuerdo con su entorno de desarrollo:
@Rem  -------------------------------

@Rem	Fija Java Home
@IF NOT EXIST %JAVA_HOME% ECHO ** VARIABLE JAVA_HOME NO ESTABLECIDA, la asignamos **
@IF NOT EXIST %JAVA_HOME% SET JAVA_HOME=C:\Java\jdk1.6.0_14
@IF NOT EXIST %JAVA_HOME%\bin ECHO "** JAVA_HOME NO encontrado"
@SET PATH=%JAVA_HOME%\bin;%PATH%

@Rem	Fija Directorio de fuentes de Libertya
@SET FUENTES_OXP=..
@IF NOT EXIST %FUENTES_OXP%\utils_dev ECHO "** FUENTES_OXP NO encontradas"

@Rem	Contrase�a para la keystore
@SET KEYTOOL_PASS=%KEY_PASSWORD%
@IF "%KEYTOOL_PASS%"=="" SET KEYTOOL_PASS=openxp

@Rem	Contrase�a para la Keystore y FTP
@SET ANT_PROPERTIES=-Dpassword=%KEYTOOL_PASS% -DftpPassword=%FTP_PASSWORD%

@Rem	Configura Ant para enviar un correo tras completar - cambiar o borrar - No es imprescindible
@SET ANT_PROPERTIES=%ANT_PROPERTIES% -DMailLogger.mailhost=xxx -DMailLogger.from=xxxx -DMailLogger.failure.to=xxxx -DMailLogger.success.to=xxxx

@Rem	Instalacion Automatica - Donde se descomprime ServidorOXP
@SET ROOT_OXP=C:\
@Rem	Instalaci�n Autom�tica - Directorio base resultante
@SET OXP_HOME=%ROOT_OXP%ServidorOXP
@Rem	Instalaci�n Autom�tica - Compartido para instaladores finales
@SET INSTALACION_OXP=C:\Install
@IF NOT EXIST %INSTALACION_OXP% Mkdir %INSTALACION_OXP%


@Rem  ---------------------------------------------------------------
@Rem  No es habitual cambiar nada de lo existente debajo de estas l�nea
@Rem  Si es necesario definir algo manualmente, hay que hacerlo en las
@Rem  variables debajo situadas. Deber�a funcionar, dado que todas las
@Rem  variables son comprobadas antes de asignarlas.
@Rem  ---------------------------------------------------------------

@SET CURRENTDIR=%CD%

@Rem  Fija datos de la Versi�n
@SET VERSION_OXP=Libertya
@SET VERSION_OXP_FILE=V18.06
@SET CONSULTORA_OXP=SERVICIOS_DIGITALES

@Rem  Rutas a las librer�as de clases.
@IF NOT EXIST %JAVA_HOME%\lib\tools.jar ECHO "** Es necesario utilizar el SDK de Java completo**"
@SET CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar

@IF NOT EXIST %FUENTES_OXP%\tools\lib\ant.jar ECHO "** Ant.jar NO encontrado **"
@SET CLASSPATH=%CLASSPATH%;%FUENTES_OXP%\tools\lib\ant.jar;%FUENTES_OXP%\tools\lib\ant-launcher.jar;%FUENTES_OXP%\tools\lib\ant-swing.jar;%FUENTES_OXP%\tools\lib\ant-commons-net.jar;%FUENTES_OXP%\tools\lib\commons-net.jar;%FUENTES_OXP%\lib\jdom.jar
@Rem SET CLASSPATH=%CLASSPATH%;%FUENTES_OXP%\jboss\lib\endorsed\xml-apis.jar


@Rem	Fija el entorno necesario para XDoclet 1.1.2 
@SET XDOCLET_HOME=%FUENTES_OXP%\tools

@Rem	La Keystore Java, necesaria para firmar los ficheros jar resultantes
@IF NOT EXIST %FUENTES_OXP%\keystore MKDIR %FUENTES_OXP%\keystore
@IF EXIST %FUENTES_OXP%\keystore\myKeystore GOTO CHEQUEAVALORDELACLAVE

:CREARCLAVEOXP
@Echo     No hay Keystore Java, la creamos ...
@Rem	.
@Rem	Esta es la clave para firmar el c�digo.
@Rem	Reemplazala con el certificado oficial, si est� disponible.
@Rem	�STE NO ES EL CERTIFICADO SSL.
@Rem	.

SET KEYTOOL_DNAME="CN=LIBERTYA, OU=SERVICIOS_DIGITALES, O=SERVICIOS_DIGITALES, L=CF, ST=BSAS, C=AR"

%JAVA_HOME%\bin\keytool -genkey -keyalg rsa -alias openxp -dname %KEYTOOL_DNAME% -keypass %KEYTOOL_PASS% -validity 365 -keystore %FUENTES_OXP%\keystore\myKeystore -storepass %KEYTOOL_PASS%

%JAVA_HOME%\bin\keytool -selfcert -alias openxp -dname %KEYTOOL_DNAME% -keypass %KEYTOOL_PASS% -validity 180 -keystore %FUENTES_OXP%\keystore\myKeystore -storepass %KEYTOOL_PASS%

:CHEQUEAVALORDELACLAVE
@%JAVA_HOME%\bin\keytool -list -alias openxp -keyStore %FUENTES_OXP%\keystore\myKeystore -storepass %KEYTOOL_PASS%
@IF ERRORLEVEL 1 GOTO :CREARCLAVEOXP

@Rem Fija ENV_OXP para todos los otros scripts.
@SET ENV_OXP=Y
