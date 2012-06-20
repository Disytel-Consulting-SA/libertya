@Rem $Id: RestaurarBD.bat,v 1.0 $

@if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title Restaurar Base de Datos openxpertya para Exportar - %OXP_HOME% (%NOMBRE_BD_OXP%)


@echo Recrear usuario openxp e importar %OXP_HOME%\data\ExpDat.dmp
@dir %OXP_HOME%\data\ExpDat.dmp
@echo == La importacion generara avisos, es correcto ==
@pause

@Rem Parameter: <systemAccount> <OXPID> <OXPClave>
@call %RUTA_BD_OXP%\Restaurar_BD system/%SYSTEM_BD_OXP% %USUARIO_BD_OXP% %PASSWD_BD_OXP%

@pause
