@Title Para la base de datos de PostgreSQL

@Rem $Id: Detener.bat,v 2.0  $
@Rem Usar la siguiente si no est� instalado como servicio
@Rem ser� necesario definir la variable de entorno PGDATA
@Rem pg_ctl stop
@Rem Usar la siguiente si PostgreSQL est� correctamente instalado como servicio
net stop pgsql-8.2

