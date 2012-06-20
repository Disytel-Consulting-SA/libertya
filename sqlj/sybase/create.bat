@Title Crear SQLJ SYBASE
@Rem	
@Rem	$Id: create.bat,v 2.0 Exp $
@Rem
@Rem	Currently user/etc. name hard coded
@Rem

@Echo Load Sybase Database ...
instjava -f "C:\ServidorOXP\lib\sqlj.jar" -update -U openxp -P openxp -D openxp

@Echo Create Sybase Functions ...
isql -U openxp -P openxp -D openxp -i createSQLJ.sql

@pause

