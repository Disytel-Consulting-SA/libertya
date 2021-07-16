@echo off
IF "%JAVA_HOME%"=="" (
  ECHO JAVA_HOME NO DEFINIDA
) else (
  java -Dfile.encoding=UTF-8 -classpath ../lib/OXP.jar;../lib/OXPXLib.jar;../lib/OXPSLib.jar;../lib/JasperReports.jar %*
)