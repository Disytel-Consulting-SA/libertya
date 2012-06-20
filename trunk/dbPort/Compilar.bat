@Title Construir Dbport
@Rem   $Header: /openxp2_0/dbPort/Compilar.bat,v 1.0 $

@CALL ..\utils_dev\VariablesCompilacion.bat
@IF NOT %ENV_OXP%==Y GOTO NOBUILD

@echo Limpiando ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main clean

@echo Compilando ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main dist

@Echo Done ...
@sleep 60
@exit

:NOBUILD
@Echo Testear VariablesCompilacion.bat (Copiado de PlantillaVariablesCompilacion.bat)
@Pause