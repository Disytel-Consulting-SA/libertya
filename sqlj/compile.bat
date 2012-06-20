@Title Compila + Jar SQLJ
@Rem    @version $Id: compile.bat,v 2.0 $
@Rem
@Rem    Algunas bases de datos requieren una versi¢n antigua de Java SDK
@Rem    y que el Zip no sea comprimido
@Rem
@Rem    Oracle: 1.4.2 - (puedes usar Compilar.bat)
@Rem	Sybase: 1.2.2 - 
@Rem
@Rem    Retocar las siguientes variables si es necesario
@Rem
@SET PATH=C:\Java\jdk1.5.0_06\bin;%PATH%
@SET JAVA_HOME=C:\Java\jdk1.5.0_06
@java -version

javac -sourcepath src -d lib src/org/openXpertya/sqlj/OpenXpertya.java src/org/openXpertya/sqlj/Product.java src/org/openXpertya/sqlj/Currency.java src/org/openXpertya/sqlj/BPartner.java src/org/openXpertya/sqlj/Invoice.java src/org/openXpertya/sqlj/Payment.java src/org/openXpertya/sqlj/PaymentTerm.java src/org/openXpertya/sqlj/Account.java

jar cf0 sqlj.jar -C lib org/openXpertya/sqlj

pause
