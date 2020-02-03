@echo off
set FECHA_ACTUAL=%DATE%
set HORA_ACTUAL=%TIME%
set OXP_HOME=C:\ServidorOXP
set JAVA_HOME=C:\jdk6
set LOGFILE=log.txt

cd %OXP_HOME%\utils\replicacion
echo %DATE% %TIME%

%JAVA_HOME%\bin\java -Dfile.encoding=UTF-8 -cp lib\repClient.jar;lib\lyws.jar;../../lib/OXP.jar;../../lib/OXPSLib.jar;../../lib/mail.jar;../../lib/AxisJar.jar org.libertya.ws.client.ReplicationClientProcess "%1" "%2" "%3" "%4" "%5" "%6" "%7" "%8"

echo %DATE% %TIME% 
echo ----------