@Title Instalación de openXpertya

@CALL VariablesCompilacion.bat
@IF NOT %ENV_OXP%==Y GOTO NOCONSTRUIDO

@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main install

@Pause
@exit
:NOCONSTRUIDO
@Echo Comprueba el fichero VariablesCompilacion.bat (personalizado a partir de PlantillaVariablesCompilacion.bat)
@Pause
