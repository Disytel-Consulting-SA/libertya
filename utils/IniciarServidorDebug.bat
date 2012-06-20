@if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title Iniciar Servido openXpertya - %OXP_HOME% (%TIPO_APPS_OXP%)

@Rem $Id: IniciarServidor.bat,v 1.0 $

@IF '%TIPO_APPS_OXP%' == 'jboss' GOTO JBOSS
@GOTO UNSUPPORTED

:JBOSS
@Set NOPAUSE=Yes
@Set JAVA_OPTS=-server %OPCIONES_JAVA_OXP%

@rem abrir el servidor en modo debug. No iniciarlo en modo debug en produccion!!
@rem set JAVA_OPTS= -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n %JAVA_OPTS%

@Call %JBOSS_HOME%\bin\rundebug -c openXpertya
@Echo Iniciado Servidor de Aplicaciones openXpertya %OXP_HOME% (%NOMBRE_BD_OXP%)
@GOTO END

:UNSUPPORTED
@Echo Iniciar Servidor de Aplicaciones openXpertya %TIPO_APPS_OXP%

:END
@Sleep 60
@Exit
