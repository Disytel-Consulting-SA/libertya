@Title Construir Cliente
@Rem   $Header: /oxp2_2/client/Compilar.bat,v 1.0 $

@CALL ..\utils_dev\VariablesCompilacion.bat
@IF NOT %ENV_OXP%==Y GOTO NOBUILD

@echo Limpiando ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main clean

@echo Compilando ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main dist

@Echo Hecho ...
@sleep 60
@exit

:NOBUILD
@Echo Comprobando VariablesCompilacion.bat (Copiado de PlantillaVariablesCompilacion.bat)
@Pause