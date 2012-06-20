@Rem $Id: ImportarOXP.bat,v 1.0 $

@if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title Importar openXpertya - %OXP_HOME% (%NOMBRE_BD_OXP%)


@echo Recrear usuario openxp e importar %OXP_HOME%\data\OXP.dmp - (%NOMBRE_BD_OXP%)
@dir %OXP_HOME%\data\OXP.dmp
@echo == La importacion mostrara avisos. Es Correcto ==
@pause

@Rem Parameter: <systemAccount> <OXPID> <OXPClave>
@call %RUTA_BD_OXP%\ImportarOXP system/%SYSTEM_BD_OXP% %USUARIO_BD_OXP% %PASSWD_BD_OXP%

@pause
