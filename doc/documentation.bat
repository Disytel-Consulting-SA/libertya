@Rem Llamar con Parametros
@Rem	1 - PATH Fuentes
@Rem	2 - Destino
@Rem	3 - Parametros
@Rem Asume que hay un fichero packages.txt en cada directorio

@Rem $Id: documentation.bat,v 2.1 01/07/2007 $
@CALL ..\utils_dev\VariablesCompilacion.bat

@Set CLASSPATH=..\lib\CClient.jar;..\lib\OXPTools.jar;..\lib\oracle.jar;..\lib\postgresql.jar
@Set CLASSPATH=%CLASSPATH%;..\lib\CServer.jar;..\lib\CSTools.jar;..\tools\lib\junit.jar
@Set CLASSPATH=%CLASSPATH%;..\jboss\client\jboss-client.jar;..\jboss\client\jboss-common-client.jar
@Set CLASSPATH=%CLASSPATH%;..\tools\lib\servlet.jar;..\jboss\lib\jboss-system.jar;..\jboss\lib\jboss-jmx.jar
@Set CLASSPATH=%CLASSPATH%;..\tools\lib\local

javadoc -sourcepath %1 -d %2 -use -author -breakiterator -version -link http://java.sun.com/j2se/1.5.0/docs/api -link http://java.sun.com/j2ee/1.4/docs/api -splitindex -windowtitle "openXpertya %VERSION_OXP% API Documentation" -doctitle "openXpertya<sup>TM</sup> Documentación API" -header "<b>openXpertya %VERSION_OXP%</b>" -bottom "Copyright (c) 2005-2007 FUNDESLE" -overview doc\overview.html %3 -J-Xmx180m @packages.txt


