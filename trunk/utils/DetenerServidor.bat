@if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title Detener Servidor openXpertya - %OXP_HOME%

@Rem $Id: DetenerServidor.bat,v 1.0 $

@IF '%TIPO_APPS_OXP%' == 'jboss' GOTO JBOSS
@GOTO UNSUPPORTED

:JBOSS
@Set NOPAUSE=Yes
@Set JBOSS_LIB=%JBOSS_HOME%\lib
@Set JBOSS_SERVERLIB=%JBOSS_HOME%\server\openXpertya\lib
@Set JBOSS_CLASSPATH=%JBOSS_LIB%\jboss-system.jar;%JBOSS_SERVERLIB%\jnpserver.jar;%JBOSS_LIB%\jboss-common.jar;%JBOSS_SERVERLIB%\jmx-adaptor-plugin.jar;%JBOSS_SERVERLIB%\jboss.jar;%JBOSS_SERVERLIB%\jboss-transaction.jar;%JBOSS_SERVERLIB%\jboss-j2ee.jar

@CD %JBOSS_HOME%\bin
Call shutdown --server=jnp://%SERVIDOR_APPS_OXP%:%PUERTO_JNP_OXP% --shutdown

@Echo Deteniendo Servidor de Aplicaciones openXpertya %OXP_HOME% (%NOMBRE_BD_OXP%)
@GOTO END

:UNSUPPORTED
@Echo Servidor de Aplicaciones %TIPO_APPS_OXP%

:END
@sleep 30
@Exit
