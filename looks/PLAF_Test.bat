@Title Testear PLAF

@Rem $Id: PLAF_Test.bat,v 1.0 $
@Rem Put the swing.properties file in the \rje\lib directory
@set JAVA_HOME=c:\Java\jdk1.5.0_06

@Rem 
%JAVA_HOME%\jre\bin\java -cp c:\oxp2_2\looks\CLooks.jar;%JAVA_HOME%\demo\jfc\SwingSet2\SwingSet2.jar org.compiere.plaf.CompierePLAF SwingSet2

@Pause