@Rem Crear Oracle SQLJ
@Rem
@Rem Parameter: <openxpertyaDBuser>/<openxpertyaDBpassword>
@Rem

@Echo .
@Echo Cargando SQLJ ORACLE ...
@SET CLASSPATH=
@call loadjava -user %1@%NOMBRE_BD_OXP% -verbose -force -resolve %OXP_HOME%\lib\sqlj.jar

@Echo .

@Echo Creando las funciones de Oracle ...
@sqlplus %1@%NOMBRE_BD_OXP% @%OXP_HOME%\utils\oracle\createSQLJ.sql
