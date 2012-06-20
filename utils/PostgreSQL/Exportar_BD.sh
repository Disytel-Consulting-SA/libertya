echo	Exportación de la base de datos PostgreSQL 	$Revision: 2.0 $


echo Guardando la base de datos $1@$NOMBRE_BD_OXP en $OXP_HOME/data/openxpdb_bk.sql

pg_dump -F c -f $OXP_HOME/data/openxpdb_bk.sql openxp 

