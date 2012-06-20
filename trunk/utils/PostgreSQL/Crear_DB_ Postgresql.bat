@Rem $Id: Crear_DB_Postgresql.bat,v 2.0 $

@Title Crear Base de Datos - %OXP_HOME% (%NOMBRE_BD_OXP%)

@Call cd "C:\Archivos de programa\PostgreSQL\8.2\bin"
@REM Creamos el rol de openxp como superusuario
@Call Psql -U postgres -c "CREATE ROLE openxp LOGIN ENCRYPTED PASSWORD 'md52b2861c4d594f29f9ce9107c4560f3ae' SUPERUSER CREATEDB CREATEROLE VALID UNTIL 'infinity' IN ROLE postgres;UPDATE pg_authid SET rolcatupdate=true WHERE rolname='openxp';"
@REM Creamos la base de datos openxp con propietario el rol openxp y codificación UTF-8
@Call Psql -U postgres -c "CREATE DATABASE openxp WITH ENCODING='UTF8' OWNER=openxp;"