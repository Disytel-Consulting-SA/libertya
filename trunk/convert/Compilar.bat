@Title Construye openXpertya Root
@Rem $Header: Compilar.bat,v 2.1 02/07/2007 $

@CALL ..\utils_dev\VariablesCompilacion.bat
@IF NOT %ENV_OXP%==Y GOTO NOBUILD

@echo Building ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main

@Echo Done ...

:NOBUILD
@Echo Comprobar VariablesCompilacion.bat (configurar a partir de PlantillaVariablesCompilacion.bat)
@Pause
