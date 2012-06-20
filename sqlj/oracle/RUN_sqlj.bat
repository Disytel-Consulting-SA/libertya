@if (%OXP_HOME%) == () (CALL ..\VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)
@Title Create Oracle SQLJ - %OXP_HOME% (%NOMBRE_BD_OXP%)
@Rem	
@Rem	Author + Copyright 1999-2005 Jorg Janke
@Rem	$Id: RUN_sqlj.bat,v 1.1 2005/04/27 05:21:46 jjanke Exp $
@Rem

call create %USUARIO_BD_OXP%/%PASSWD_BD_OXP%

@pause
