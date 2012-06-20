@Title Construir Instalador openXpertya
@Rem  $Header: /oxp2_2/install/Compilar.bat,v 1.0 $
@Echo off

@CALL ..\utils_dev\VariablesCompilacion.bat
@IF NOT %ENV_OXP%==Y GOTO NOBUILD

@echo Limpiando ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main clean

@echo Construyendo ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main 

@Echo Terminado ...
@sleep 60
@exit

:NOBUILD
@Echo Testear VariablesCompilacion.bat (Copiar Desde PlantillaVariablesCompilacion.bat)
@Pause
