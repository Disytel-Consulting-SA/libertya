@Rem $Id: Restaurar_BD,v 2.0 $

@Title Restaurar Base de Datos - %OXP_HOME% (%NOMBRE_BD_OXP%)

@Echo Restaurando openXpertya DB desde %OXP_HOME%\data\openxpdb_bk.sql

@Call cd "C:\Archivos de programa\PostgreSQL\8.2\bin"
@Rem retocar la siguiente línea en función de lo que se quiera restaurar
@call pg_restore.exe -i -h localhost -p 5432 -d openxp -U openxp  -v "C:\ServidorOXP\data\openxpdb_bk.sql"


