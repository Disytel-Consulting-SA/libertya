@Title Compilado openXpertya Incremental + Instalación
@Rem $Header: CompilarIncremental.bat,v 2.0 Exp $

@CALL VariablesCompilacion.bat
@IF NOT %ENV_OXP%==Y GOTO NOBUILD

@Echo	Parando servidor de aplicaciones (esperando...)
@START %OXP_HOME%\utils\DetenerServidor.bat
@Sleep 5

@echo Construyendo ...
@%JAVA_HOME%\bin\java -Dant.home="." %ANT_PROPERTIES% org.apache.tools.ant.Main complete
@Echo ErrorLevel = %ERRORLEVEL%

@IF NOT ERRORLEVEL 0 GOTO BUILDOK
@Pause
@Exit

:BUILDOK
dir %INSTALACION_OXP%

@Echo	Limpiando ...
@erase /q /s %TMP%

@Echo	Arrancando el servidor de aplicaciones ...
@Start %OXP_HOME%\utils\IniciarServidor.bat

@Sleep 10
@Exit

:NOBUILD
@Echo Comprueba VariablesCompilacion.bat (copia desde PlantillaVariablesCompilacion.bat)
@Pause