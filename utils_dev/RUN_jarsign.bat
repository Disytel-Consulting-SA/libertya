@Title	Firmar JARS en %OXP_HOME%\lib\
@Rem
@Rem	Firmar Todos los JAR con tu certificado
@Rem	Keystore Localizado en c:\oxp2_2\keystore\myKeystore
@Rem	Contraseña de KeyStore %KEY_PASSWORD% (por defecto openxp)
@Rem	Los Archivos JAR estan en el directorio Deployment de %OXP_HOME%\lib
@Rem
@Rem	Despues de este script, Iniciar Configurar 
@Rem	Generar/Copiar jnlp y/o webstart

jarsigner -keystore c:\oxp2_2\myKeystore -storepass %KEY_PASSWORD% -keypass %KEY_PASSWORD% %OXP_HOME%\lib\CClient.jar openxp
jarsigner -keystore c:\oxp2_2\keystore\myKeystore -storepass %KEY_PASSWORD% -keypass %KEY_PASSWORD% %OXP_HOME%\lib\OXPTools.jar openxp
jarsigner -keystore c:\oxp2_2\keystore\myKeystore -storepass %KEY_PASSWORD% -keypass %KEY_PASSWORD% %OXP_HOME%\lib\oracle.jar openxp

@Echo	Despues iniciar Configurar.exe o Configurar.sh
@pause