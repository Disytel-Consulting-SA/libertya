@Title Iniciar Servicio de Base de datos de PostgreSQL 

@Rem $Id: Iniciar.bat,v 2.0 Exp $

@Rem Puede hacer falta IPC demon si se utiliza cygwin
@Rem ipc-daemon&
@Rem Para usar la siguiente línea hay que instalar correctamente las
@Rem variables de entorno de PostgreSQL (PGDATA, etc). 
@Rem pg_ctl -o "-i" -l $PGLOG start
@Rem Usar la siguiente línea si esta correctamente instalado como servicio
net start pgsql-8.2
@pause
