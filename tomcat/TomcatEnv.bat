@echo off
REM ==========================================================
REM  Carga entorno Libertya + Tomcat a partir de LibertyaEnv.properties
REM ==========================================================

REM Ruta base por defecto si no viene OXP_HOME del entorno
set "DEFAULT_PROP_BASE=C:\ServidorOXP"

REM Si ya viene PROP_BASE definida, la respeto. Si no, la armo.
if not defined PROP_BASE (
    if not defined OXP_HOME (
        set "PROP_BASE=%DEFAULT_PROP_BASE%"
    ) else (
        set "PROP_BASE=%OXP_HOME%"
    )
)

set "PROP_FILE=%PROP_BASE%\LibertyaEnv.properties"

if not exist "%PROP_FILE%" (
    echo ERROR: No se puede leer "%PROP_FILE%"
    exit /b 1
)

REM -----------------------------------------------------------------
REM Leer OPCIONES_JAVA_OXP
REM -----------------------------------------------------------------
set "OPCIONES_JAVA_OXP="

for /f "usebackq tokens=1* delims==" %%A in ("%PROP_FILE%") do (
    if /I "%%A"=="OPCIONES_JAVA_OXP" (
        set "OPCIONES_JAVA_OXP=%%B"
    )
)

REM Si no vino nada en OPCIONES_JAVA_OXP, pongo un default
if "%OPCIONES_JAVA_OXP%"=="" (
    set "OPCIONES_JAVA_OXP=-Xms512M -Xmx1024M"
)

REM -----------------------------------------------------------------
REM Leer JAVA_HOME
REM -----------------------------------------------------------------
set "JAVA_HOME_PROP="

for /f "usebackq tokens=1* delims==" %%A in ("%PROP_FILE%") do (
    if /I "%%A"=="JAVA_HOME" (
        set "JAVA_HOME_PROP=%%B"
    )
)

REM Exporto JAVA_HOME / JRE_HOME si están definidos
if not "%JAVA_HOME_PROP%"=="" (
    set "JAVA_HOME=%JAVA_HOME_PROP%"
    set "JRE_HOME=%JAVA_HOME_PROP%"
)

REM -----------------------------------------------------------------
REM Armar CATALINA_OPTS base
REM -----------------------------------------------------------------
set "BASE_CATALINA_OPTS=%OPCIONES_JAVA_OXP% -Djava.awt.headless=true -DOXP_HOME=%PROP_BASE% -Dfile.encoding=UTF-8"

REM No hacer exit /b aquí para no matar al caller.
