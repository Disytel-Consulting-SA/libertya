@if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title	Exporta Base de Datos openXpertya - %OXP_HOME% (%NOMBRE_BD_OXP%)
@Rem 
@Rem $Id: ExportarBD.bat,v 1.0 $
@Rem 
@Rem Parameter: <UsuarioBDOXP>/<CLAVEBDOXP>

@call %RUTA_BD_OXP%\Exportar_BD %USUARIO_BD_OXP% %PASSWD_BD_OXP%
@Rem call %RUTA_BD_OXP%\Exportar_TODO_BD system %SYSTEM_BD_OXP%

@Echo Si la siguiente setencia falla, revise sus variables
IF (%OXP_HOME%) == () (CALL CopiarBD.bat) else (CALL %OXP_HOME%\utils\CopiarBD.bat)

@Echo Sleeping ... (Borrar el comando si estamos en XP)
@sleep 60
