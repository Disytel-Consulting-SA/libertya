@Rem $Id: Importa_DB_Postgresql.bat,v 2.0 $

@Title Importar Base de Datos - %OXP_HOME% (%NOMBRE_BD_OXP%)

@Call cd "C:\Archivos de programa\PostgreSQL\8.2\bin"
@Call Psql -d openxp -U openxp -f "C:\ServidorOXP\data\openxpdb.sql"