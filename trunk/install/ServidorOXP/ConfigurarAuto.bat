@Title Configurar Libertya desde Template
@Echo off

@if not "%JAVA_HOME%" == "" goto JAVA_HOME_OK
@Set JAVA=java
@Echo JAVA_HOME no está asignado.  
@Echo Es posible que no la configuración no pueda realizarse correctamente!!
@Echo Se debe asignar JAVA_HOME al directorio en donde se encuentra el JDK 1.6.
@rem Echo Se se prensentan problemas, ejecute utils/WinEnv.js
@rem Echo Ejemplo: cscript utils\WinEnv.js C:\Libertya "C:\Program Files\Java\jdk1.5.0_04"
goto START

:JAVA_HOME_OK
@Set JAVA=%JAVA_HOME%\bin\java


:START
@Echo =======================================
@Echo Comenzando Configuración ...
@Echo =======================================
@rem @SET CP=lib\CInstall.jar;lib\Adempiere.jar;lib\CCTools.jar;lib\oracle.jar;lib\fyracle.jar;lib\derby.jar;lib\jboss.jar;lib\postgresql.jar;
@SET CP=lib\OXPInstall.jar;lib\OXP.jar;lib\CCTools.jar;lib\oracle.jar;lib\jboss.jar;lib\postgresql.jar;lib\log4j.jar;lib\mail.jar

@Rem Parámetro de configuración de log, e.g. SET ARGS=ALL
@SET ARGS=CONFIG

@Rem To test the OCI driver, add -DTestOCI=Y to the command - example:
@Rem %JAVA% -classpath %CP% -DADEMPIERE_HOME=%ADEMPIERE_HOME% -DTestOCI=Y org.compiere.install.Setup %ARGS%

@"%JAVA%" -classpath %CP% -DOXP_HOME="%OXP_HOME%" org.openXpertya.install.SilentSetup %ARGS%
@Rem Echo ErrorLevel = %ERRORLEVEL%
@IF NOT ERRORLEVEL 1 GOTO NEXT
@Echo ***************************************
@Echo Verifique el mensaje de error previo
@Echo ***************************************
@Echo Asegúrese de que el entorno está asignado correctamente!
@Echo Asigne la variable de entorno JAVA_HOME manualmente
@Echo o utilice WinEnv.js que se encuentra en el directorio util
@Echo ***************************************
@Rem Esperar 5 segundos
@Rem PING 1.1.1.1 -n 1 -w 5000 > NUL
@Rem Retorna error
@Exit 1


:NEXT

@Rem cd utils

@Rem ===================================
@Rem Instalación del Entorno de Libertya
@Rem ===================================
@rem Call RUN_WinEnv.bat
@Rem Esperar 3 segundos
@Rem PING 1.1.1.1 -n 1 -w 3000 > NUL