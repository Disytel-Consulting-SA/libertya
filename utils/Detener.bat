@if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title Detener openXpertya  - %OXP_HOME% (%NOMBRE_BD_OXP%)

@Rem $Id: Detener.bat,v 1.0 $

@CALL %OXP_HOME%\utils\DetenerServidor.bat

@CALL %RUTA_BD_OXP%\Detener.bat

