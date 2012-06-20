/*
 * @(#)SetupRes_ja.java   11.jun 2007  Versión 2.2
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
public class SetupRes_ja extends ListResourceBundle {

    /** Descripción de Campo */
    static final Object[][]	contents	= new String[][] {

        { "InstalarServidorOXP", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec \u30b5\u30fc\u30d0 \u8a2d\u5b9a" }, { "Ok", "Ok" }, { "File", "\u30d5\u30a1\u30a4\u30eb" }, { "Exit", "\u7d42\u4e86" }, { "Help", "\u30d8\u30eb\u30d7" }, { "PleaseCheck", "\u78ba\u304b\u3081\u3066\u4e0b\u3055\u3044" }, { "UnableToConnect", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u30db\u30fc\u30e0\u30da\u30fc\u30b8\u306b\u63a5\u7d9a\u304c\u3067\u304d\u306a\u3044" },

        //
        { "OXPHomeInfo", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u30db\u30fc\u30e0\u30d5\u30a9\u30eb\u30c0" }, { "OXPHome", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u30db\u30fc\u30e0" }, { "WebPortInfo", "\u30a6\u30a8\u30d6 (HTML) \u30dd\u30fc\u30c8" }, { "WebPort", "\u30a6\u30a8\u30d6 \u30dd\u30fc\u30c8" }, { "AppsServerInfo", "\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u30fb\u30b5\u30fc\u30d0\u540d" }, { "AppsServer", "\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u30fb\u30b5\u30fc\u30d0" }, { "DatabaseTypeInfo", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9" }, { "DatabaseType", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9" }, { "DatabaseNameInfo", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u540d" }, { "DatabaseName", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u540d (SID)" }, { "DatabasePortInfo", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9 \u30ea\u30bb\u30ca\u30fc \u30dd\u30fc\u30c8" }, { "DatabasePort", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9 \u30dd\u30fc\u30c8" }, { "DatabaseUserInfo", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30e6\u30fc\u30b6\u540d" }, { "DatabaseUser", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30e6\u30fc\u30b6\u540d" }, { "DatabasePasswordInfo", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "DatabasePassword", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "TNSNameInfo", "\u30b0\u30ed\u30fc\u30d0\u30eb\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u540d (TNS)" }, { "TNSName", "TNS" }, { "SystemPasswordInfo", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306e\u30b7\u30b9\u30c6\u30e0\u30e6\u30fc\u30b6\u306e\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "SystemPassword", "\u30b7\u30b9\u30c6\u30e0\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "MailServerInfo", "\u30e1\u30fc\u30eb\u30fb\u30b5\u30fc\u30d0" }, { "MailServer", "\u30e1\u30fc\u30eb\u30fb\u30b5\u30fc\u30d0" }, { "AdminEMailInfo", "\u30a2\u30c9\u30df\u30cb\u30b9\u30c8\u30ec\u30fc\u30c8\u306e\u30e1\u30fc\u30eb\u30a2\u30c9\u30ec\u30b9" }, { "AdminEMail", "\u30e1\u30fc\u30eb" }, { "DatabaseServerInfo", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30fb\u30b5\u30fc\u30d0\u540d" }, { "DatabaseServer", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30fb\u30b5\u30fc\u30d0\u540d" }, { "JavaHomeInfo", "Java\u306e\u30db\u30fc\u30e0\u30d5\u30a9\u30eb\u30c0" }, { "JavaHome", "Java\u306e\u30db\u30fc\u30e0" }, { "JNPPortInfo", "\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u30fb\u30b5\u30fc\u30d0\u306eJNP \u30dd\u30fc\u30c8" }, { "JNPPort", "JNP \u30dd\u30fc\u30c8" }, { "MailUserInfo", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u30e1\u30fc\u30eb\u30e6\u30fc\u30b6\u540d" }, { "MailUser", "\u30e1\u30fc\u30eb\u30e6\u30fc\u30b6" }, { "MailPasswordInfo", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u30e1\u30fc\u30eb\u30d1\u30b9\u30ef\u30fc\u30c9" }, { "MailPassword", "\u30e1\u30fc\u30eb\u30d1\u30b9\u30ef\u30fc\u30c9" },

        //
        { "JavaType", "Java VM" }, { "JavaTypeInfo", "Java VM Vendor" }, { "AppsType", "Server Type" }, { "AppsTypeInfo", "J2EE Application Server Type" }, { "DeployDir", "Deployment" }, { "DeployDirInfo", "J2EE Deployment Directory" }, { "ErrorDeployDir", "Error Deployment Directory" },

        //
        { "TestInfo", "\u8a2d\u5b9a\u306e\u30c6\u30b9\u30c8" }, { "Test", "\u30c6\u30b9\u30c8" }, { "SaveInfo", "\u8a2d\u5b9a\u306e\u4fdd\u5b58" }, { "Save", "\u4fdd\u5b58" }, { "HelpInfo", "\u30d8\u30eb\u30d7" },

        //
        { "ServerError", "\u30b5\u30fc\u30d0\u8a2d\u5b9a\u30a8\u30e9\u30fc" }, { "ErrorJavaHome", "Java\u306e\u30db\u30fc\u30e0\u30a8\u30e9\u30fc" }, { "ErrorOXPHome", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u30db\u30fc\u30e0\u30a8\u30e9\u30fc" }, { "ErrorAppsServer", "\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u30fb\u30b5\u30fc\u30d0\u306e\u30a8\u30e9\u30fc\uff1alocalhost" }, { "ErrorWebPort", "\u30a6\u30a8\u30d6\u30dd\u30fc\u30c8\u306e\u30a8\u30e9\u30fc" }, { "ErrorJNPPort", "JNP\u30dd\u30fc\u30c8\u306e\u30a8\u30e9\u30fc" }, { "ErrorDatabaseServer", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30fb\u30b5\u30fc\u30d0\u306e\u30a8\u30e9\u30fc\uff1alocalhost" }, { "ErrorDatabasePort", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30dd\u30fc\u30c8\u306e\u30a8\u30e9\u30fc" }, { "ErrorJDBC", "JDBC\u63a5\u7d9a\u306e\u30a8\u30e9\u30fc" }, { "ErrorTNS", "TNS\u63a5\u7d9a\u306e\u30a8\u30e9\u30fc" }, { "ErrorMailServer", "\u30e1\u30fc\u30eb\u30fb\u30b5\u30fc\u30d0\u306e\u30a8\u30e9\u30fc\uff1alocalhost" }, { "ErrorMail", "\u30e1\u30fc\u30eb\u306e\u30a8\u30e9\u30fc" }, { "ErrorSave", "\u4fdd\u5b58\u306e\u30a8\u30e9\u30fc" }, { "EnvironmentSaved", "\u8a2d\u5b9a\u3092\u4fdd\u5b58\u3057\u307e\u3057\u305f\u3002\n\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u30fb\u30b5\u30fc\u30d0\u3092\u958b\u3044\u3066\u4e0b\u3055\u3044\u3002" }
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
 * @(#)SetupRes_ja.java   11.jun 2007
 * 
 *  Fin del fichero SetupRes_ja.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
