@echo off

if defined OXP_HOME (
    cd /d "%OXP_HOME%\tomcat\bin"
    call "%OXP_HOME%\tomcat\bin\IniciarServidor.bat"
) else (
    echo Variable OXP_HOME no seteada
    exit /b 1
)