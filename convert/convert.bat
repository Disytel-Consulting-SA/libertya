
@echo off

set OPENXPERTYA_HOME=".."

set CLASSPATH=..\tools\lib\local
set CLASSPATH=%CLASSPATH%;..\interfaces\classes
set CLASSPATH=%CLASSPATH%;..\tools\lib\postgresql.jar
set CLASSPATH=%CLASSPATH%;..\lib\activation.jar;..\lib\ant.jar;..\lib\batik-awt-util.jar;..\lib\batik-dom.jar;..\lib\batik-svggen.jar;..\lib\batik-util.jar;..\lib\batik-xml.jar;..\lib\bsh-2.0b4.jar;..\lib\CMPCS.jar;..\lib\CSTools.jar;..\lib\j2ee.jar;..\lib\javax.servlet.jar;..\lib\jboss.jar;..\lib\jbossall-client.jar;..\lib\jconn3.jar;..\lib\jdom.jar;..\lib\jnp-client.jar;..\lib\jnpserver.jar;..\lib\jsp-api-2.0.jar;..\lib\jstl.jar;..\lib\jtds-1.0.jar;..\lib\jTDS3.jar;..\lib\junit.jar;..\lib\log4j.jar;..\lib\mail.jar;..\lib\ModulesServerApp.jar;..\lib\ojdbc14.jar;..\lib\oracle.jar;..\lib\orai18n.jar;..\lib\OXP.jar;..\lib\OXPApps.jar;..\lib\OXPRoot.jar;..\lib\pager-taglib-2.0.jar;..\lib\pdfviewer.jar;..\lib\postgresql.jar;..\lib\sqlServer.jar;..\lib\standard.jar;..\lib\swing-layout-1.0.jar;..\lib\sybase.jar;..\lib\Verisign.jar;..\lib\xdoclet.jar;..\lib\XOXPTools.jar
set CLASSPATH=%CLASSPATH%;Convert.jar;lib\xstream-1.2.1.jar;lib\xpp3_min-1.1.3.4.O.jar

%JAVA_HOME%\bin\java -Xms32m -Xmx512m -DOPENXPERTYA_HOME=%OPENXPERTYA_HOME% -classpath %CLASSPATH% es.indeos.transform.Convert %1 %2 %3

