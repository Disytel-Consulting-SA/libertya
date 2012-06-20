@Rem $Id: ImportarReferencia.bat,v 1.0 $

@if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title Importar Referencia - %OXP_HOME% (%NOMBRE_BD_OXP%)


@echo Recrear Referencia de Usuario e Importacion %OXP_HOME%\data\OXP.dmp - (%NOMBRE_BD_OXP%)
@dir %OXP_HOME%\data\OXP.dmp
@echo == La Importacion mostrara Avisos. Eso es correcto ==
@pause

@Rem Parameter: <systemAccount> <OXPID> <OXPClave>
@call %RUTA_BD_OXP%\ImportarOXP system/%SYSTEM_BD_OXP% reference reference

@pause
