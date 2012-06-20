echo Importar_DB_Postgresql.bat,v 2.0

echo Importar Base de Datos - $OXP_HOME ($NOMBRE_BD_OXP)

Psql -d openxp -U openxp -f "$OXP_HOME/data/openxpdb.sql"
