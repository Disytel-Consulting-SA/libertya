@if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title Inicio del Servicio de Base de Datos  - %OXP_HOME% (%NOMBRE_BD_OXP%)

@Rem $Id: IniciarBD.bat,v 1.0 $

@CALL %RUTA_BD_OXP%\Start.bat
@Echo Iniciando Base de Datos %OXP_HOME% (%NOMBRE_BD_OXP%)

@sleep 60
