@Rem   Plantilla de variables necesarias para el entorno de desarrollo.
@Rem   A modificar de acuerdo con las necesidades de cada instalación.
@Rem   Este script fija las variables para compilar openXpertya desde sus fuentes.
@Rem   Versión estandard 2.0 por Fundesle.	
@Rem	
@Rem  Es necesario comprobar y ajustar los siguientes parámetros de acuerdo con su entorno de desarrollo:
@Rem  -------------------------------

@Rem	Fija Java Home
@SET JAVA_HOME=C:\Java\jdk1.5.0_06
@IF NOT EXIST %JAVA_HOME%\bin ECHO "** JAVA_HOME NO encontrado"
@SET PATH=%JAVA_HOME%\bin;%PATH%

@Rem	Fija Directorio de fuentes de openXpertya
@SET FUENTES_OXP=C:\oxp2_2
@IF NOT EXIST %JAVA_HOME%\bin ECHO "** FUENTES_OXP NO encontradas"

@Rem	Contraseña para la keystore
@SET KEYTOOL_PASS=%KEY_PASSWORD%
@IF "%KEYTOOL_PASS%"=="" SET KEYTOOL_PASS=openxp

@Rem	Contraseña para la Keystore y FTP
@SET ANT_PROPERTIES=-Dpassword=%KEYTOOL_PASS% -DftpPassword=%FTP_PASSWORD%

@Rem	Configura Ant para enviar un correo tras completar - cambiar o borrar - No es imprescindible
@SET ANT_PROPERTIES=%ANT_PROPERTIES% -DMailLogger.mailhost=xxx -DMailLogger.from=xxxx -DMailLogger.failure.to=xxxx -DMailLogger.success.to=xxxx

@Rem	Instalación Automática - Dónde openXpertya será descomprimido
@SET ROOT_OXP=C:\
@Rem	Instalación Automática - Directorio base resultante
@SET OXP_HOME=%ROOT_OXP%ServidorOXP
@Rem	Instalación Automática - Compartido para instaladores finales
@SET INSTALACION_OXP=C:\InstalaOXP
@IF NOT EXIST %INSTALACION_OXP% Mkdir %INSTALACION_OXP%


@Rem  ---------------------------------------------------------------
@Rem  No es habitual cambiar nada de lo existente debajo de estas línea
@Rem  Si es necesario definir algo manualmente, hay que hacerlo en las
@Rem  variables debajo situadas. Debería funcionar, dado que todas las
@Rem  variables son comprobadas antes de asignarlas.
@Rem  ---------------------------------------------------------------

@SET CURRENTDIR=%CD%

@Rem  Fija datos de la Versión
@SET VERSION_OXP=OPENXP
@SET VERSION_OXP_FILE=V2_0
@SET CONSULTORA_OXP=FUNDESLE

@Rem  Rutas a las librerías de clases.
@IF NOT EXIST %JAVA_HOME%\lib\tools.jar ECHO "** Es necesario utilizar el SDK de Java completo**"
@SET CLASSPATH=%CLASSPATH%;%JAVA_HOME%\lib\tools.jar

@IF NOT EXIST %FUENTES_OXP%\tools\lib\ant.jar ECHO "** Ant.jar NO encontrado **"
@SET CLASSPATH=%CLASSPATH%;%FUENTES_OXP%\tools\lib\ant.jar;%FUENTES_OXP%\tools\lib\ant-launcher.jar;%FUENTES_OXP%\tools\lib\ant-swing.jar;%FUENTES_OXP%\tools\lib\ant-commons-net.jar;%FUENTES_OXP%\tools\lib\commons-net.jar
@Rem SET CLASSPATH=%CLASSPATH%;%FUENTES_OXP%\jboss\lib\xml-apis.jar


@Rem	Fija el entorno necesario para XDoclet 1.1.2 
@SET XDOCLET_HOME=%FUENTES_OXP%\tools

@Rem	La Keystore Java, necesaria para firmar los ficheros jar resultantes
@IF NOT EXIST %FUENTES_OXP%\keystore MKDIR %FUENTES_OXP%\keystore
@IF EXIST %FUENTES_OXP%\keystore\myKeystore GOTO CHEQUEAVALORDELACLAVE

:CREARCLAVEOXP
@Echo   No hay Keystore Java, la creamos ...
@Rem	.
@Rem	Esta es la clave para firmar el código.
@Rem	Reemplazarla con el certificado oficial, si está disponible.
@Rem	ÉSTE NO ES EL CERTIFICADO SSL.
@Rem	.

SET KEYTOOL_DNAME="CN=NOMBRE, OU=UNIDAD_ORGANIZATIVA, O=ORGANIZACION, L=CIUDAD, ST=PROVINCIA, C=PAIS"

%JAVA_HOME%\bin\keytool -genkey -keyalg rsa -alias openxp -dname %KEYTOOL_DNAME% -keypass %KEYTOOL_PASS% -validity 365 -keystore %FUENTES_OXP%\keystore\myKeystore -storepass %KEYTOOL_PASS%

%JAVA_HOME%\bin\keytool -selfcert -alias openxp -dname %KEYTOOL_DNAME% -keypass %KEYTOOL_PASS% -validity 180 -keystore %FUENTES_OXP%\keystore\myKeystore -storepass %KEYTOOL_PASS%

:CHEQUEAVALORDELACLAVE
@%JAVA_HOME%\bin\keytool -list -alias openxp -keyStore %FUENTES_OXP%\keystore\myKeystore -storepass %KEYTOOL_PASS%
@IF ERRORLEVEL 1 GOTO :CREARCLAVEOXP

@Rem Fija ENV_OXP para todos los otros scripts.
@SET ENV_OXP=Y
