/*
 * @(#)SetupRes_zh.java   11.jun 2007  Versión 2.2
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
public class SetupRes_zh extends ListResourceBundle {

    /** Descripción de Campo */
    static final Object[][]	contents	= new String[][] {

        { "InstalarServidorOXP", "OpenXpertya \u4f3a\u670d\u5668\u8a2d\u5b9a" }, { "Ok", "\u78ba\u5b9a" }, { "File", "\u6a94\u6848" }, { "Exit", "\u96e2\u958b" }, { "Help", "\u8aaa\u660e" }, { "PleaseCheck", "\u8acb\u6aa2\u67e5" }, { "UnableToConnect", "\u7121\u6cd5\u81ea OpenXpertya \u7db2\u7ad9\u5f97\u5230\u8aaa\u660e" }, { "OXPHomeInfo", "OpenXpertya \u4e3b\u76ee\u9304" }, { "OXPHome", "OpenXpertya Home" }, { "WebPortInfo", "\u7db2\u9801\u4f3a\u670d\u5668\u9023\u63a5\u57e0" }, { "WebPort", "Web Port" }, { "AppsServerInfo", "\u61c9\u7528\u4f3a\u670d\u5668\u540d\u7a31" }, { "AppsServer", "Apps Server" }, { "DatabaseTypeInfo", "\u8cc7\u6599\u5eab\u7a2e\u985e" }, { "DatabaseType", "Database Type" }, { "DatabaseNameInfo", "\u8cc7\u6599\u5eab\u540d\u7a31 " }, { "DatabaseName", "Database Name (SID)" }, { "DatabasePortInfo", "\u8cc7\u6599\u5eab\u9023\u63a5\u57e0" }, { "DatabasePort", "Database Port" }, { "DatabaseUserInfo", "OpenXpertya \u4f7f\u7528\u8cc7\u6599\u5eab\u7684\u5e33\u865f" }, { "DatabaseUser", "Database User" }, { "DatabasePasswordInfo", "OpenXpertya \u4f7f\u7528\u8cc7\u6599\u5eab\u7684\u5bc6\u78bc" }, { "DatabasePassword", "Database Password" }, { "TNSNameInfo", "TNS or Global Database Name" }, { "TNSName", "TNS Name" }, { "SystemPasswordInfo", "\u7cfb\u7d71\u5bc6\u78bc" }, { "SystemPassword", "System Password" }, { "MailServerInfo", "\u90f5\u4ef6\u4f3a\u670d\u5668" }, { "MailServer", "Mail Server" }, { "AdminEMailInfo", "OpenXpertya \u7ba1\u7406\u8005 EMail" }, { "AdminEMail", "Admin EMail" }, { "DatabaseServerInfo", "\u8cc7\u6599\u5eab\u540d\u7a31" }, { "DatabaseServer", "Database Server" }, { "JavaHomeInfo", "Java \u4e3b\u76ee\u9304" }, { "JavaHome", "Java Home" }, { "JNPPortInfo", "\u61c9\u7528\u4f3a\u670d\u5668\u7684 JNP \u9023\u63a5\u57e0" }, { "JNPPort", "JNP Port" }, { "MailUserInfo", "OpenXpertya Mail \u5e33\u865f" }, { "MailUser", "Mail User" }, { "MailPasswordInfo", "OpenXpertya Mail \u5e33\u865f\u7684\u5bc6\u78bc" }, { "MailPassword", "Mail Password" }, { "KeyStorePassword", "Key Store Password" }, { "KeyStorePasswordInfo", "Password for SSL Key Store" },

        //
        { "JavaType", "Java VM" }, { "JavaTypeInfo", "Java VM Vendor" }, { "AppsType", "Server Type" }, { "AppsTypeInfo", "J2EE Application Server Type" }, { "DeployDir", "Deployment" }, { "DeployDirInfo", "J2EE Deployment Directory" }, { "ErrorDeployDir", "Error Deployment Directory" },

        //
        { "TestInfo", "\u8a2d\u5b9a\u6e2c\u8a66" }, { "Test", "Test" }, { "SaveInfo", "\u5132\u5b58\u8a2d\u5b9a" }, { "Save", "Save" }, { "HelpInfo", "\u53d6\u5f97\u8aaa\u660e" }, { "ServerError", "\u4f3a\u670d\u5668\u8a2d\u5b9a\u932f\u8aa4" }, { "ErrorJavaHome", "Java \u4e3b\u76ee\u9304\u932f\u8aa4" }, { "ErrorOXPHome", "OpenXpertya \u4e3b\u76ee\u9304\u932f\u8aa4" }, { "ErrorAppsServer", "\u61c9\u7528\u4f3a\u670d\u5668\u932f\u8aa4 (do not use localhost)" }, { "ErrorWebPort", "\u7db2\u9801\u4f3a\u670d\u5668\u9023\u63a5\u57e0\u932f\u8aa4" }, { "ErrorJNPPort", "JNP \u9023\u63a5\u57e0\u932f\u8aa4" }, { "ErrorDatabaseServer", "\u8cc7\u6599\u5eab\u932f\u8aa4 (do not use localhost)" }, { "ErrorDatabasePort", "\u8cc7\u6599\u5eab Port \u932f\u8aa4" }, { "ErrorJDBC", "JDBC \u9023\u63a5\u932f\u8aa4" }, { "ErrorTNS", "TNS \u9023\u63a5\u932f\u8aa4" }, { "ErrorMailServer", "\u90f5\u4ef6\u4f3a\u670d\u5668\u932f\u8aa4 (do not use localhost)" }, { "ErrorMail", "\u90f5\u4ef6\u932f\u8aa4" }, { "ErrorSave", "\u5b58\u6a94\u932f\u8aa4" }, { "EnvironmentSaved", "\u74b0\u5883\u8a2d\u5b9a\u5132\u5b58\u6210\u529f\n\u8acb\u5c07\u4f3a\u670d\u5668\u91cd\u65b0\u555f\u52d5." }, { "RMIoverHTTP", "Tunnel Objects via HTTP" }, { "RMIoverHTTPInfo", "RMI over HTTP allows to go through firewalls" }
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
 * @(#)SetupRes_zh.java   11.jun 2007
 * 
 *  Fin del fichero SetupRes_zh.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
