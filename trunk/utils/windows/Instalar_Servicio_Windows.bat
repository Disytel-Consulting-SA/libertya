@echo off

REM $Id: Instalar_Servicio_Windows.bat,v 2.0 $

if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)

%OXP_HOME%\utils\windows\JavaService.exe -install openXpertya %JAVA_HOME%\jre\bin\server\jvm.dll -Xmx256M -Djava.class.path=%JAVA_HOME%\lib\tools.jar;%OXP_HOME%\jboss\bin\run.jar -server %OPCIONES_JAVA_OXP% -Djetty.port=%PUERTO_WEB_OXP% -Djetty.ssl=%PUERTO_SSL_OXP% -Djetty.keystore=%KEYSTORE_OXP% -Djetty.password=%KEYSTORE_OXP_PASSWORD% -start org.jboss.Main -params -c openXpertya -stop org.jboss.Main -method systemExit -out %OXP_HOME%\log\salida-JBoss.log -err %OXP_HOME%\log\errores-JBoss.log -current %OXP_HOME%\jboss\bin
