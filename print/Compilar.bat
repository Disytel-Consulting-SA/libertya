@Title Construir Print
@Rem   $Header: /oxp2_2/print/Compilar.bat,v 1.0 $

@CALL ..\utils_dev\VariablesCompilacion.bat

@IF %ENV_OXP%==N GOTO NOBUILD
@IF NOT %ENV_OXP%==Y GOTO NOBUILD

@echo Cleanup ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main clean

@echo Building ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main dist

@Echo Done ...
@sleep 60
@exit

:NOBUILD
@Echo Check VariablesCompilacion.bat (copy from PlantillaVariablesCompilacion.bat)
@Pause