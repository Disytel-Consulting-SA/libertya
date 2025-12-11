@echo off
REM ==========================================================
REM  Iniciar Servidor Libertya - Tomcat
REM ==========================================================

setlocal

REM Ruta base por defecto si no viene OXP_HOME
set "DEFAULT_PROP_BASE=C:\ServidorOXP"

if not defined OXP_HOME (
    set "PROP_BASE=%DEFAULT_PROP_BASE%"
) else (
    set "PROP_BASE=%OXP_HOME%"
)

echo Iniciando Servidor Libertya - %PROP_BASE%

REM Directorio donde está este .bat (se asume que está en tomcat\bin)
set "SCRIPT_DIR=%~dp0"

REM Cargar entorno común (TomcatEnv.bat)
call "%SCRIPT_DIR%TomcatEnv.bat"
if errorlevel 1 (
    echo Error cargando entorno Tomcat/Libertya
    endlocal & exit /b 1
)

REM Pasar BASE_CATALINA_OPTS a CATALINA_OPTS
set "CATALINA_OPTS=%BASE_CATALINA_OPTS%"

REM Ejecutar startup de Tomcat
call "%SCRIPT_DIR%startup.bat"

endlocal
