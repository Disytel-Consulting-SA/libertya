@Title Construir
@Rem   $Header: /oxp2_2/sqlj/Compilar.bat,v 1.0 $
@Rem
@Rem	El archivo de compilacion SQLJ no es parte de un archivo normal de compilacion
@Rem	You need to build the sqlj.jar file either with this script
@Rem	or with the 'compile' script for older Java versions
@Rem	You then deploy it with the database dependent 'create' script

@CALL ..\utils_dev\VariablesCompilacion.bat
@IF NOT %ENV_OXP%==Y GOTO NOBUILD

@echo Cleanup ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main clean

@echo Building ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main sqljDist

@Echo Done ...
@sleep 60
@exit

:NOBUILD
@Echo Check VariablesCompilacion.bat (copy from PlantillaVariablesCompilacion.bat)
@Pause
