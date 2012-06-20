/*
 * @(#)SetupRes_es.java   21.abr 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.install;

import java.util.ListResourceBundle;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 21.04.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class SetupRes extends ListResourceBundle {

    /** Descripción de Campo */
    static final Object[][]	contents	= new String[][] {

        { "InstalarServidorOXP", "Configuraci" + "\u00f3" + "n del servidor Libertya" },
        { "Ok", " De acuerdo" },
        { "File", "Archivo" },
        { "Exit", "Salir" },
        { "Help", "Ayuda" },
        { "PleaseCheck", "Por favor, compru" + "\u00e9" + "belo" },
        { "UnableToConnect", "No se ha podido obtener ayuda de la p" + "\u00e1" + "gina de Libertya" },
        { "OXPHomeInfo", "Introduzca el directorio base de ubicaci" + "\u00f3" + "n del servidor Libertya" },
        { "OXPHome", "Directorio base de Libertya" },
        { "WebPortInfo", "Puerto a utilizar por el servidor Web (http) de Libertya" },
        { "WebPort", "Puerto Web" },
        { "SSLInfo", "Puerto a utilizar por el servidor Web seguro (https) de Libertya" },
        { "SSL", "Puerto SSL" },
        { "AppsServerInfo", "Introduzca el nombre de la m" + "\u00e1" + "quina donde se ejecuta el servidor de aplicaciones" },
        { "AppsServer", " Servidor de aplicaciones" },
        { "DatabaseTypeInfo", "Tipo de base de datos a utilizar por el servidor (Oracle, PostgreSQL, etc...)" },
        { "DatabaseType", "Tipo de base de datos" },
        { "DatabaseNameInfo", "Introducir el nombre de base de datos que contendr" + "\u00e1" + " los datos de Libertya" },
        { "DatabaseName", "Nombre de la base de datos" },
        { "DatabasePortInfo", "Introducir el puerto a utilizar por el servidor para conectarse a la base de datos" },
        { "DatabasePort", "Puerto de la base de datos" },
        { "DatabaseUserInfo", "Introduzca el usuario que se utilizar" + "\u00e1" + " para conectarse a la base de datos" },
        { "DatabaseUser", "Usuario de la base de datos" },
        { "DatabasePasswordInfo", "Introduzca la contrase" + "\u00f1" + "a del usuario de conexi" + "\u00f3" + "n a la Base de Datos" },
        { "DatabasePassword", "Contrase" + "\u00f1" + "a de la base de datos" },
        { "TNSNameInfo", "TNS o Nombre Global de la base de datos ORACLE" },
        { "TNSName", "Nombre TNS" },
        { "SystemPasswordInfo", "Introduzca la contrase" + "\u00f1" + "a del superusuario de la base de datos" },
        { "SystemPassword", "Contrase" + "\u00f1" + "a de superusuario" },
        { "MailServerInfo", "Introduzca el servidor de correo electr" + "\u00f3" + "nico a utilizar por Libertya" },
        { "MailServer", " Servidor de correo" },
        { "AdminEMailInfo", "Direcci" + "\u00f3" + "n de correo electr" + "\u00f3" + "nico del administrador de Libertya" },
        { "AdminEMail", "Correo del administrador" },
        { "DatabaseServerInfo", "Introduzca el nombre del servidor del sistema de base de datos" },
        { "DatabaseServer", " Servidor base de datos" },
        { "JavaHomeInfo", "Introduzca el directorio base de ubicaci" + "\u00f3" + "n del Java JDK" },
        { "JavaHome", "Directorio base de Java JDK" },
        { "JNPPortInfo", "Puerto de escucha RMI a utilizar por el servidor de aplicaciones" },
        { "JNPPort", "Puerto JNP" },
        { "MailUserInfo", "Nombre de la cuenta de correo del servidor Libertya" },
        { "MailUser", "Usuario de correo" },
        { "MailPasswordInfo", "Contrase" + "\u00f1" + "a de la cuenta de correo del servidor Libertya" },
        { "MailPassword", "Contrase" + "\u00f1" + "a de correo" },
        { "KeyStorePassword", "Contrase" + "\u00f1" + "a de keyStore" },
        { "KeyStorePasswordInfo", "Contrase" + "\u00f1" + "a a utilizar para la base de datos de claves privadas y p"+"\u00fa"+"blicas (keystore)" },
        //
        { "JavaType", "Tipo de Java" },
        { "JavaTypeInfo", "Seleccione el tipo de m" + "\u00e1" + "quina virtual Java a utilizar con Libertya" },
        { "AppsType", "Tipo de servidor" },
        { "AppsTypeInfo", "Seleccione el tipo de servidor de Aplicaciones J2EE a utilizar con Libertya" },
        { "DeployDir", "Directorio de despliegue" },
        { "DeployDirInfo", "Directorio de despliegue del servidor Libertya (J2EE)" },
        { "ErrorDeployDir", " Error en el directorio de despliegue" },
        //
        { "TestInfo", "Comprobar la configuraci" + "\u00f3" + "n introducida" },
        { "Test", "Comprobar" },
        { "SaveInfo", "Guardar la configuraci" + "\u00f3" + "n introducida" },
        { "Save", "Guardar" },
        { "HelpInfo", "Obtener ayuda adicional desde la p" + "\u00e1" + "gina de Libertya" },
        { "ServerError", " Error de configuraci" + "\u00f3" + "n del servidor" },
        { "ErrorJavaHome", " Error en el directorio base del Java JDK" },
        { "ErrorOXPHome", " Error en el directorio base de Libertya" },
        { "ErrorAppsServer", " Error de conexi" + "\u00f3" + "n al servidor de aplicaciones (no utilizar " + "\u00ab" + "localhost" + "\u00bb" + ")" },
        { "ErrorWebPort", " Error en el puerto web (" + "\u00bf" + "est" + "\u00e1" + " libre?)" },
        { "ErrorJNPPort", " Error en el puerto JNP (" + "\u00bf" + "est" + "\u00e1" + " libre?)" },
        { "ErrorDatabaseServer", "Error de conexi" + "\u00f3" + "n al servidor de base de datos (no utilizar " + "\u00ab" + "localhost" + "\u00bb" + ")" },
        { "ErrorDatabasePort", " Error en el puerto de la base de datos (" + "\u00bf" + "est" + "\u00e1" + " libre?)" },
        { "ErrorJDBC", " Error de conexi" + "\u00f3" + "n JDBC" },
        { "ErrorTNS", " Error de conexi" + "\u00f3" + "n a la base de datos" },
        { "ErrorMailServer", " Error en el servidor correo electr" + "\u00f3" + "nico (no utilizar " + "\u00ab" + "localhost" + "\u00bb" + ")" },
        { "ErrorMail", " Error de correo electr" + "\u00f3" + "nico" },
        { "ErrorSave", " Error guardando la configuraci" + "\u00f3" + "n" },
        { "EnvironmentSaved", " Configuraci" + "\u00f3" + "n guardada, ahora debe iniciar el servidor." },
        { "RMIoverHTTP", "Entunelamiento a trav" + "\u00e9" + "s de http" },
        { "RMIoverHTTPInfo", "Entunelar RMI sobre el protocolo HTTP permite acceder al servidor de aplicaciones a trav" + "\u00e9" + "s de firewalls" }
    };

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public Object[][] getContents() {
        return contents;
    }		// getContents
}	// SerupRes



/*
 * @(#)SetupRes_es.java   21.abr 2007
 * 
 *  Fin del fichero SetupRes_es.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 21.abr 2007