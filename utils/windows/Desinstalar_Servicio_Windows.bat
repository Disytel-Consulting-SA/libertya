@echo off

REM $Id: Desinstalar_Servicio-Windowsl.bat,v 2.0 $

if (%OXP_HOME%) == () (CALL VariablesOXP.bat Server) else (CALL %OXP_HOME%\utils\VariablesOXP.bat Server)

NET STOP openXpertya
%OXP_HOME%\utils\windows\JavaService.exe -uninstall openXpertya