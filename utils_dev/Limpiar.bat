@Title Limpiar OpenXpertya

@CALL VariablesCompilacion.bat
@IF NOT %ENV_OXP%==Y GOTO NOCONSTRUIDO

@echo Limpiando ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main clean

@exit
:NOCONSTRUIDO
@Echo Comprueba el fichero VariablesCompilacion.bat (personalizado a partir de PlantillaVariablesCompilacion.bat)
@Pause