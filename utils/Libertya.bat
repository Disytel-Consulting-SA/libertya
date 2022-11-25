@Title	Cliente Libertya %OXP_HOME%   %1%
@Rem $Id: Libertya.bat,v 2.0 $
@Echo off

@Rem Check OXP
@Rem Si %OXP_HOME% esta seteado dejarlo, sino setearlo en el directorio actual
@if not %OXP_HOME% == "" goto OXP_HOME_OK
@Set OXP_HOME=%~dp0
@Echo OXP_HOME establecido en %OXP_HOME%

:OXP_HOME_OK
@Rem Set CLASSPATH=%OXP_HOME%\lib\OXP.jar;%OXP_HOME%\lib\OXPXLib.jar;%CLASSPATH%
@Set CLASSPATH=%OXP_HOME%\lib\OXP.jar;%OXP_HOME%\lib\OXPXLib.jar;%OXP_HOME%\lib\XOXPTools.jar;%OXP_HOME%\lib\OXPApps.jar;%OXP_HOME%\lib\CMPCS.jar;%OXP_HOME%\lib\JasperReports.jar;%CLASSPATH%

:CHECK_JAVA
@if not "%JAVA_HOME%" == "" goto JAVA_HOME_OK
@Set JAVA=java
@Echo JAVA_HOME no esta establecido.  
@Echo No es posible iniciar Libertya
@Echo Establecer JAVA_HOME al directorio de su java JDK.

:JAVA_HOME_OK
@Set JAVA=%JAVA_HOME%\bin\java
@Rem Set CLASSPATH=lib\OXP.jar;lib\OXPXLib.jar;%CLASSPATH%
@Rem @Set CLASSPATH=lib\OXP.jar;lib\OXPXLib.jar;lib\CMPCS.jar;lib\JasperReports.jar;%CLASSPATH%
@goto START

:START
Rem lectura de proxy.preferences
set preferences=
setlocal enabledelayedexpansion
for /f "tokens=*" %%a in (proxy.preferences) do (
    set preferences=!preferences! %%a
)
@Echo proxy preferences %preferences%

@Rem inicio de Libertya
@"%JAVA%" -Xms64m -Xmx512m %preferences% -Dfile.encoding=UTF-8 -DOXP_HOME=%OXP_HOME% -classpath %CLASSPATH% org.openXpertya.OpenXpertya 

