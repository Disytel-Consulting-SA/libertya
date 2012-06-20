/*
 * @(#)SetupRes_ml.java   11.jun 2007  Versión 2.2
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
 * DescripciÃ¯Â¿Â½n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */
public class SetupRes_ml extends ListResourceBundle {

    /** Descripción de Campo */
    static final Object[][]	contents	= new String[][] {

        { "InstalarServidorOXP", "OpenXpertya Server Setup" }, { "Ok", "Ok" }, { "File", "File" }, { "Exit", "Exit" }, { "Help", "Help" }, { "PleaseCheck", "Please Check" }, { "UnableToConnect", "Unable get help from OpenXpertya Web Site" }, { "OXPHomeInfo", "OpenXpertya Home is the main Folder" }, { "OXPHome", "OpenXpertya Home" }, { "WebPortInfo", "Web (HTML) Port" }, { "WebPort", "Web Port" }, { "AppsServerInfo", "Application Server Name" }, { "AppsServer", "Apps Server" }, { "DatabaseTypeInfo", "Database Type" }, { "DatabaseType", "Database Type" }, { "DatabaseNameInfo", "Database Name " }, { "DatabaseName", "Database Name (SID)" }, { "DatabasePortInfo", "Database Listener Port" }, { "DatabasePort", "Database Port" }, { "DatabaseUserInfo", "Database OpenXpertya User ID" }, { "DatabaseUser", "Database User" }, { "DatabasePasswordInfo", "Database OpenXpertya User Password" }, { "DatabasePassword", "Database Password" }, { "TNSNameInfo", "TNS or Global Database Name" }, { "TNSName", "TNS Name" }, { "SystemPasswordInfo", "System User Password" }, { "SystemPassword", "System Password" }, { "MailServerInfo", "Mail Server" }, { "MailServer", "Mail Server" }, { "AdminEMailInfo", "OpenXpertya Administrator EMail" }, { "AdminEMail", "Admin EMail" }, { "DatabaseServerInfo", "Database Server Name" }, { "DatabaseServer", "Database Server" }, { "JavaHomeInfo", "Java Home Folder" }, { "JavaHome", "Java Home" }, { "JNPPortInfo", "Application Server JNP Port" }, { "JNPPort", "JNP Port" }, { "MailUserInfo", "OpenXpertya Mail User" }, { "MailUser", "Mail User" }, { "MailPasswordInfo", "OpenXpertya Mail User Password" }, { "MailPassword", "Mail Password" }, { "KeyStorePassword", "Key Store Password" }, { "KeyStorePasswordInfo", "Password for SSL Key Store" },

        //
        { "JavaType", "Java VM" }, { "JavaTypeInfo", "Java VM Vendor" }, { "AppsType", "Server Type" }, { "AppsTypeInfo", "J2EE Application Server Type" }, { "DeployDir", "Deployment" }, { "DeployDirInfo", "J2EE Deployment Directory" }, { "ErrorDeployDir", "Error Deployment Directory" },

        //
        { "TestInfo", "Test the Setup" }, { "Test", "Test" }, { "SaveInfo", "Save the Setup" }, { "Save", "Save" }, { "HelpInfo", "Get Help" }, { "ServerError", "Server Setup Error" }, { "ErrorJavaHome", "Error Java Home" }, { "ErrorOXPHome", "Error OpenXpertya Home" }, { "ErrorAppsServer", "Error Apps Server (do not use localhost)" }, { "ErrorWebPort", "Error Web Port" }, { "ErrorJNPPort", "Error JNP Port" }, { "ErrorDatabaseServer", "Error Database Server (do not use localhost)" }, { "ErrorDatabasePort", "Error Database Port" }, { "ErrorJDBC", "Error JDBC Connection" }, { "ErrorTNS", "Error TNS Connection" }, { "ErrorMailServer", "Error Mail Server (do not use localhost)" }, { "ErrorMail", "Error Mail" }, { "ErrorSave", "Error Sving File" }, { "EnvironmentSaved", "Environment saved\nYou need to re-start the server." }
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
 * @(#)SetupRes_ml.java   11.jun 2007
 * 
 *  Fin del fichero SetupRes_ml.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
