@Title	Cliente Libertya %OXP_HOME%   %1%
@Rem $Id: Libertya.bat,v 2.0 $
@Echo off

@Rem Establecer/sobreescribir OXP_HOME/JAVA_HOME 
@Rem Para versiones Diferentes, etc. por ejemplo
@Rem
@Rem SET OXP_HOME=C:\ServidorOXP
@Rem SET JAVA_HOME=c:\java\jdk1.5.0_06

:CHECK_JAVA:
@if not "%JAVA_HOME%" == "" goto JAVA_HOME_OK
@Set JAVA=java
@Echo JAVA_HOME no esta establecido.  
@Echo   No es posible iniciar Libertya
@Echo   Establecer JAVA_HOME al directorio de su java JDK.
@Echo   Puede establcerlo a traves de WinEnv.js por ejemplo:
@Echo   cscript WinEnv.js C:\ServidorOXP c:\java\jdk1.5.0_06
@goto CHECK_OXP
:JAVA_HOME_OK
@Set JAVA=%JAVA_HOME%\bin\java

:CHECK_OXP
@if "%OXP_HOME%" == "" set OXP_HOME=C:\ServidorOXP
@goto OXP_HOME_OK
@Rem Set CLASSPATH=lib\OXP.jar;lib\OXPXLib.jar;%CLASSPATH%
@Set CLASSPATH=lib\OXP.jar;lib\OXPXLib.jar;lib\CMPCS.jar;lib\JasperReports.jar;%CLASSPATH%
@Set OXP_HOME=%~dp0..
@Echo OXP_HOME no esta establecido.  
@Echo   No es posible iniciar Libertya
@Echo   Establecer OXP_HOME a donde tenga la carpeta ServidorOXP
@Echo   Deberia establcer las variables via WinEnv.js por ejemplo:
@Echo     cscript WinEnv.js C:\ServidorOXP c:\java\jdk1.5.0_06
@goto START

:OXP_HOME_OK
@Rem Set CLASSPATH=%OXP_HOME%\lib\OXP.jar;%OXP_HOME%\lib\OXPXLib.jar;%CLASSPATH%
@Set CLASSPATH=%OXP_HOME%\lib\OXP.jar;%OXP_HOME%\lib\OXPXLib.jar;%OXP_HOME%\lib\XOXPTools.jar;%OXP_HOME%\lib\OXPApps.jar;%OXP_HOME%\lib\CMPCS.jar;%OXP_HOME%\lib\JasperReports.jar;%CLASSPATH%
@REM Para cambiar entre distintas instalaciones, Copiar el archivo OXP.properties
@REM Seleccione la configuracion de la variable PROP de entre las siguientes
@SET PROP=
@Rem SET PROP=-DPropertyFile=C:\test.properties
@REM Use los parametros alternativamente
@if "%1" == "" goto START
@SET PROP=-DPropertyFile=%1

:START
@"%JAVA%" -Xms64m -Xmx512m -Dfile.encoding=UTF-8 -DOXP_HOME=%OXP_HOME% %PROP% -classpath %CLASSPATH% org.openXpertya.OpenXpertya 

@sleep 15