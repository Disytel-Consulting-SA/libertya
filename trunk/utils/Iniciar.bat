@if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title Arranca openXpertya - %OXP_HOME% (%NOMBRE_BD_OXP%)

@Rem $Id: Iniciar.bat,v 2.1 $

@Echo Iniciando Base de Datos
@CALL %RUTA_BD_OXP%\iniciar.bat

@START %OXP_HOME%\utils\IniciarServidor.bat 
