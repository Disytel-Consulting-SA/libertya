@Rem $Id: Exportar_BD,v 2.0 $

@Title Exportar Base de Datos - %OXP_HOME% (%NOMBRE_BD_OXP%)

@Call cd "C:\Archivos de programa\PostgreSQL\8.2\bin"
@Rem retocar la siguiente línea en función de lo que se quiera exportar
@call pg_dump.exe -i -h localhost -p 5432 -U openxp -F p -v -f "C:\ServidorOXP\data\openxpdb_bk.sql" -n openxp