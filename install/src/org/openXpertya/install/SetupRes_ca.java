/*
 * @(#)SetupRes_ca.java   11.jun 2007  Versión 2.2
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
public class SetupRes_ca extends ListResourceBundle {

    /** Descripción de Campo */
    static final Object[][]	contents	= new String[][] {

        { "InstalarServidorOXP", "ConfiguraciÃ¯Â¿Â½ Servidor OpenXpertya" }, { "Ok", "D'Acord" }, { "File", "Fitxer" }, { "Exit", "Sortir" }, { "Help", "Ajuda" }, { "PleaseCheck", "Sisplau Comproveu" }, { "UnableToConnect", "No s'ha pogut obtenir l'ajuda de la web del OpenXpertya" }, { "OXPHomeInfo", "OpenXpertya Home Ã¯Â¿Â½s la Carpeta Principal" }, { "OXPHome", "OpenXpertya Home" }, { "WebPortInfo", "Web (HTML) Port" }, { "WebPort", "Web Port" }, { "AppsServerInfo", "Nom Servidor AplicaciÃ¯Â¿Â½" }, { "AppsServer", "Servidor AplicaciÃ¯Â¿Â½" }, { "DatabaseTypeInfo", "Tipus Base de Dades" }, { "DatabaseType", "Tipus Base de Dades" }, { "DatabaseNameInfo", "Nom Base de Dades" }, { "DatabaseName", "Nom Base de Dades (SID)" }, { "DatabasePortInfo", "Port Listener Base de Dades" }, { "DatabasePort", "Port Base de Dades" }, { "DatabaseUserInfo", "ID Usuari OpenXpertya Base de Dades" }, { "DatabaseUser", "Usuari Base de Dades" }, { "DatabasePasswordInfo", "Contrasenya Usuari OpenXpertya Base de Dades" }, { "DatabasePassword", "Contrasenya Base de Dades" }, { "TNSNameInfo", "TNS o Nom Global Base de Dades" }, { "TNSName", "Nom TNS" }, { "SystemPasswordInfo", "Contrasenya Usuari System" }, { "SystemPassword", "Contrasenya System" }, { "MailServerInfo", "Servidor Correu" }, { "MailServer", "Servidor Correu" }, { "AdminEMailInfo", "Email Administrador OpenXpertya" }, { "AdminEMail", "Email Admin" }, { "DatabaseServerInfo", "Nom Servidor Base de Dades" }, { "DatabaseServer", "Servidor Base de Dades" }, { "JavaHomeInfo", "Carpeta Java Home" }, { "JavaHome", "Java Home" }, { "JNPPortInfo", "Port JNP Servidor AplicaciÃ¯Â¿Â½" }, { "JNPPort", "Port JNP" }, { "MailUserInfo", "Usuari Correu OpenXpertya" }, { "MailUser", "Usuari Correu" }, { "MailPasswordInfo", "Contrasenya Usuari Correu OpenXpertya" }, { "MailPassword", "Contrasenya Correu" }, { "KeyStorePassword", "Key Store Password" }, { "KeyStorePasswordInfo", "Password for SSL Key Store" },

        //
        { "JavaType", "Java VM" }, { "JavaTypeInfo", "Java VM Vendor" }, { "AppsType", "Server Type" }, { "AppsTypeInfo", "J2EE Application Server Type" }, { "DeployDir", "Deployment" }, { "DeployDirInfo", "J2EE Deployment Directory" }, { "ErrorDeployDir", "Error Deployment Directory" },

        //
        { "TestInfo", "Provar ConfiguraciÃ¯Â¿Â½" }, { "Test", "Provar" }, { "SaveInfo", "Guardar ConfiguraciÃ¯Â¿Â½" }, { "Save", "Guardar" }, { "HelpInfo", "Obtenir Ajuda" }, { "ServerError", "Error ConfiguraciÃ¯Â¿Â½ Servidor" }, { "ErrorJavaHome", "Error Java Home" }, { "ErrorOXPHome", "Error OpenXpertya Home" }, { "ErrorAppsServer", "Error Servidor AplicaciÃ¯Â¿Â½ (no emprar localhost)" }, { "ErrorWebPort", "Error Port Web" }, { "ErrorJNPPort", "Error Port JNP" }, { "ErrorDatabaseServer", "Error Servidor Base de Dades (no emprar localhost)" }, { "ErrorDatabasePort", "Error Port Base de Dades" }, { "ErrorJDBC", "Error ConnexiÃ¯Â¿Â½ JDBC" }, { "ErrorTNS", "Error ConnexiÃ¯Â¿Â½ TNS" }, { "ErrorMailServer", "Error Servidor Correu (no emprar localhost)" }, { "ErrorMail", "Error Correu" }, { "ErrorSave", "Error Guardant Fitxer" }, { "EnvironmentSaved", "Entorn Guardat\nCal reiniciar el servidor." }, { "RMIoverHTTP", "Tunnel Objects via HTTP" }, { "RMIoverHTTPInfo", "RMI over HTTP allows to go through firewalls" }
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
 * @(#)SetupRes_ca.java   11.jun 2007
 * 
 *  Fin del fichero SetupRes_ca.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
