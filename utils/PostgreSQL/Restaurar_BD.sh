echo	Restaurar la base de datos de openXpertya $Revision: 2.0 $


echo	Restaurando la base de datos de openXpertya desde $OXP_HOME/data/openxpdb.sql

#       retocar la siguiente línea en función de lo que se quiera restaurar

pg_restore.exe -i -h localhost -p 5432 -d openxp -U openxp  -v "$OXP_HOME/data/openxpdb_bk.sql"

