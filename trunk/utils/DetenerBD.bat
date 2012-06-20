@if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title Detener Servicio Base de Datos openXpertya  - %OXP_HOME% (%NOMBRE_BD_OXP%)

@Rem $Id: DetenerBD.bat,v 1.0 $

@CALL %RUTA_BD_OXP%\Detener.bat
@Echo Parando Base de Datos openXpertya %OXP_HOME% (%NOMBRE_BD_OXP%)

@sleep 60
