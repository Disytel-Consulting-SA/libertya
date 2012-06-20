# Crear ORACLE SQLJ
# $Id: create.sh,v  $
#


# unset CLASSPATH=

echo .
echo Cargar SQLJ  Oracle...
loadjava -user $1@$NOMBRE_BD_OXP -verbose -resolve $OXP_HOME/lib/sqlj.jar

echo .
echo Crear Funciones Oracle ...
sqlplus $1@$NOMBRE_BD_OXP @$OXP_HOME/utils/oracle/createSQLJ.sql
