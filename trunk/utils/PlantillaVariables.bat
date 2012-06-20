@Rem	Este fichero define las variables usadas por openXpertya
@Rem	No editar directamente - usar Configurar
@Rem	
@Rem	$Id: PlantillaVariables.bat,v 2.1 02/07/2007 $

@Echo Setting myEnvironment ....
@Rem	Clients only needs
@Rem		OXP_HOME
@Rem		JAVA_HOME 
@Rem	Server install needs to check
@Rem		NOMBRE_BD_OXP	(for Oracle)
@Rem		passwords

@Rem 	Homes ...
@SET OXP_HOME=@OXP_HOME@
@SET JAVA_HOME=@JAVA_HOME@


@Rem	Database ...
@SET USUARIO_BD_OXP=@USUARIO_BD_OXP@
@SET PASSWD_BD_OXP=@PASSWD_BD_OXP@
@SET URL_BD_OXP=@URL_BD_OXP@

@Rem	Oracle specifics
@SET RUTA_BD_OXP=@TIPO_BD_OXP@
@SET NOMBRE_BD_OXP=@NOMBRE_BD_OXP@
@SET SYSTEM_BD_OXP=@SYSTEM_BD_OXP@

@Rem	Homes(2)
@SET HOME_BD_OXP=@OXP_HOME@\utils\@TIPO_BD_OXP@
@SET JBOSS_HOME=@OXP_HOME@\jboss

@Rem	Apps Server
@SET TIPO_APPS_OXP=@TIPO_APPS_OXP@
@SET SERVIDOR_APPS_OXP=@SERVIDOR_APPS_OXP@
@SET PUERTO_JNP_OXP=@PUERTO_JNP_OXP@
@SET PUERTO_WEB_OXP=@PUERTO_WEB_OXP@
@SET DEPLOY_APPS_OXP=@TIPO_APPS_OXP@
@Rem	SSL Settings
@SET PUERTO_SSL_OXP=@PUERTO_SSL_OXP@
@SET KEYSTORE_OXP=@KEYSTORE_OXP@
@SET ALIASWEBKEYSTORE_OXP=@ALIASWEBKEYSTORE_OXP@
@SET KEYSTOREPASS_OXP=@KEYSTOREPASS_OXP@

@Rem	etc.
@SET SERVIDOR_FTP_OXP=@SERVIDOR_FTP_OXP@
@SET USUARIO_FTP_OXP=@USUARIO_FTP_OXP@

@Rem	Java
@SET OXP_JAVA=@JAVA_HOME@\bin\java
@SET OPCIONES_JAVA_OXP=@OPCIONES_JAVA_OXP@ -DOXP_HOME=@OXP_HOME@
@SET CLASSPATH="@OXP_HOME@\lib\OXP.jar;@OXP_HOME@\lib\OXPXLib.jar;"

@Rem Save Environment file
@if (%1) == () copy VariablesOXP.bat myEnvironment_%RANDOM%.bat /Y 

