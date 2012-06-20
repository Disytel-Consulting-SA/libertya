@Title Testeo de Variables openXpertya

@Rem $Id: EstablecerVariables.bat,v 2.1 $

@if (%OXP_HOME%) == () (CALL VariablesOXP.bat) else (CALL %OXP_HOME%\utils\VariablesOXP.bat)

@Echo General ...
@Echo PATH      = %PATH%
@Echo CLASSPTH  = %CLASSPATH%

@Echo .
@Echo Inicio ...
@Echo OXP_HOME        = %OXP_HOME%
@Echo JAVA_HOME            = %JAVA_HOME%
@Echo URL_BD_OXP      = %URL_BD_OXP%

@Echo .
@Echo Base de Datos ...
@Echo USUARIO_BD_OXP     = %USUARIO_BD_OXP%
@Echo PASSWD_BD_OXP = %PASSWD_BD_OXP%
@Echo RUTA_BD_OXP     = %RUTA_BD_OXP%

@Echo .. Oracle
@Echo NOMBRE_BD_OXP      = %NOMBRE_BD_OXP%
@Echo SYSTEM_BD_OXP   = %SYSTEM_BD_OXP%

%JAVA_HOME%\bin\java -version

@Echo .
@Echo La version java deberia ser al menos "1.5"
@Echo ---------------------------------------------------------------
@Pause

@Echo .
@Echo ---------------------------------------------------------------
@Echo Testear Conexion de Base de Datos (1) ... %NOMBRE_BD_OXP%
@Echo Si esto fallase, verificar la variable NOMBRE_BD_OXP con el Oracle Net Manager
@Echo Deberia de ver un CORRECTO al finalizar
@Pause
tnsping %NOMBRE_BD_OXP%

@Echo .
@Echo ---------------------------------------------------------------
@Echo Testear Conexion de Base de Datos (3) ... system/%SYSTEM_BD_OXP% in %HOME_BD_OXP%
@Echo Si este testeo falla, verifica la contraseña de system en SYSTEM_BD_OXP
@Pause
sqlplus system/%SYSTEM_BD_OXP%@%NOMBRE_BD_OXP% @%HOME_BD_OXP%\Test.sql

@Echo .
@Echo ---------------------------------------------------------------
@Echo Testear Tamaño de la base de datos
@Pause
sqlplus system/%SYSTEM_BD_OXP%@%NOMBRE_BD_OXP% @%HOME_BD_OXP%\CheckDB.sql %USUARIO_BD_OXP%

@Echo .
@Echo ---------------------------------------------------------------
@Echo Testeo de la Conexion de la Base de Datos (4) ... %USUARIO_BD_OXP%/%PASSWD_BD_OXP%
@Echo Si esto falla, la base de datos openXpertya no esta importada - escriba exit y arranque el script de importacion
@Pause
sqlplus %USUARIO_BD_OXP%/%PASSWD_BD_OXP%@%NOMBRE_BD_OXP% @%HOME_BD_OXP%\Test.sql

@Echo .
@Echo Hecho
@pause
