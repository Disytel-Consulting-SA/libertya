@Title Compilar el sistema de comercio electrónico
@Rem $Header: Compilar.bat,v 2.0 Exp $

@CALL ..\utils_dev\VariablesCompilacion.bat

@IF %ENV_OXP%==N GOTO NOBUILD
@echo Limpiando ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.launch.Launcher clean
@echo Compilando ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.launch.Launcher

@pause
:NOBUILD