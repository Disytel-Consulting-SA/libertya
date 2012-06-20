@Rem $Id: Instalar_DB_Postgresql.bat,v 2.0 $

@Title Crear Base de Datos - %OXP_HOME% (%NOMBRE_BD_OXP%)

@REM Crea el rol de openxp y la base de datsos
@Call %OXP_HOME%\utils\PostgreSQL\Crear_DB_Postgresql.bat
@REM Importa la base de datos inicial
@Call %OXP_HOME%\utils\PostgreSQL\Importar_DB_Postgresql.bat