@Title Construir Codigo openXpertya

@CALL VariablesCompilacion.bat
@IF NOT %ENV_OXP%==Y GOTO NOCONSTRUIDO

@echo Limpiando ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main clean

@echo Construyendo ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main -logger org.apache.tools.ant.listener.MailLogger complete
@IF ERRORLEVEL 1 goto ERROR

dir %INSTALACION_OXP%

@Echo Echo ...
@sleep 60
@exit

:NOCONSTRUIDO
@Echo Comprueba el fichero VariablesCompilacion.bat (personalizado a partir de PlantillaVariablesCompilacion.bat)

:ERROR
@Color fc

@Pause