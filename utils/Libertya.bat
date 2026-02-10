@Rem $Id: Libertya.bat,v 2.0 $
@Echo off
setlocal EnableExtensions EnableDelayedExpansion

@Rem Logs desactivados por defecto.
@Rem Habilitar con flag (--logs, -logs, /LOGS) o variable LIBERTYA_ENABLE_LOGS=1/true.
set "ENABLE_LOGS=0"
if /I "%LIBERTYA_ENABLE_LOGS%"=="1" set "ENABLE_LOGS=1"
if /I "%LIBERTYA_ENABLE_LOGS%"=="true" set "ENABLE_LOGS=1"
call :PARSE_FLAGS %*

if "%ENABLE_LOGS%"=="1" (
    set "LOGDIR=%~dp0logs"
    if not exist "%LOGDIR%" mkdir "%LOGDIR%"
    for /f %%I in ('powershell -NoProfile -Command "(Get-Date -Format \"yyyy-MM-dd\")"') do set "TODAY=%%I"
    set "LOGFILE=%LOGDIR%\Libertya_!TODAY!.log"
    call :MAIN %* >> "!LOGFILE!" 2>&1
) else (
    call :MAIN %*
)
exit /b %ERRORLEVEL%

:PARSE_FLAGS
if "%~1"=="" goto :eof
if /I "%~1"=="--logs" set "ENABLE_LOGS=1"
if /I "%~1"=="-logs" set "ENABLE_LOGS=1"
if /I "%~1"=="/LOGS" set "ENABLE_LOGS=1"
shift
goto :PARSE_FLAGS

:MAIN
@Title	Cliente Libertya %OXP_HOME%   %1%

@Rem Check OXP
@Rem Si %OXP_HOME% esta seteado dejarlo, sino setearlo en el directorio actual
@if not "%OXP_HOME%" == "" goto OXP_HOME_OK
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
for /f "tokens=*" %%a in (proxy.preferences) do (
    set preferences=!preferences! %%a
)
@Echo proxy preferences %preferences%

@Rem inicio de Libertya
@"%JAVA%" -Xms64m -Xmx512m %preferences% -Dfile.encoding=UTF-8 -DOXP_HOME=%OXP_HOME% -classpath %CLASSPATH% org.openXpertya.OpenXpertya
exit /b %ERRORLEVEL%
