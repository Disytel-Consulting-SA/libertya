/*
 * @(#)SetupRes_sl.java   11.jun 2007  Versión 2.2
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
public class SetupRes_sl extends ListResourceBundle {

    /** Descripción de Campo */
    static final Object[][]	contents	= new String[][] {

        { "InstalarServidorOXP", "Namestavitve OpenXpertya streÃÂ¾nika" }, { "Ok", "V redu" }, { "File", "Datoteka" }, { "Exit", "Izhod" }, { "Help", "PomoÃ¯Â¿Â½?" }, { "PleaseCheck", "Prosim preverite" }, { "UnableToConnect", "Napaka pri povezavi na OpenXpertya web pomoÃ¯Â¿Â½?" },

        //
        { "OXPHomeInfo", "OpenXpertya Home je glavni imenik" }, { "OXPHome", "OpenXpertya Home" }, { "WebPortInfo", "Web (HTML) vrata" }, { "WebPort", "Web vrata" }, { "AppsServerInfo", "Ime programskega streÃÂ¾nika" }, { "AppsServer", "Programski streÃÂ¾nik" }, { "DatabaseTypeInfo", "Tip baze podatkov" }, { "DatabaseType", "Tip baze podatakov" }, { "DatabaseNameInfo", "Ime baze podatkov " }, { "DatabaseName", "Ime baze (SID)" }, { "DatabasePortInfo", "Vrata Listener programa" }, { "DatabasePort", "Vrata baze podatkov" }, { "DatabaseUserInfo", "UporabniÃÂ¡ko ime OpenXpertya baze podatkov" }, { "DatabaseUser", "Uporabnik baze podatkov" }, { "DatabasePasswordInfo", "Geslo uporabnika baze podatkov" }, { "DatabasePassword", "Geslo baze podatkov" }, { "TNSNameInfo", "TNS ali globalno ime baze podatkov" }, { "TNSName", "TNS Ime" }, { "SystemPasswordInfo", "Geslo uporabnika System" }, { "SystemPassword", "System geslo" }, { "MailServerInfo", "StreÃÂ¾nik elektronske poÃÂ¡te" }, { "MailServer", "StreÃÂ¾nik elektronske poÃÂ¡te" }, { "AdminEMailInfo", "Elektronski naslov OpenXpertya Skrbnika" }, { "AdminEMail", "Elektronski naslov Skrbnika" }, { "DatabaseServerInfo", "Ime streÃÂ¾nika baze podatkov" }, { "DatabaseServer", "StreÃÂ¾nik baze podatkov" }, { "JavaHomeInfo", "DomaÃ¯Â¿Â½? imenik Jave" }, { "JavaHome", "Java imenik" }, { "JNPPortInfo", "JNP vrata programskega streÃÂ¾nika" }, { "JNPPort", "JNP vrata" }, { "MailUserInfo", "Uporabnik elektronske poÃÂ¡te za OpenXpertya" }, { "MailUser", "Uporabnik elektronske poÃÂ¡te" }, { "MailPasswordInfo", "Geslo uporabnika elektronske poÃÂ¡te OpenXpertya" }, { "MailPassword", "Geslo uporabnika elektronske poÃÂ¡te" }, { "KeyStorePassword", "Geslo shrambe kljuÃ¯Â¿Â½?ev" }, { "KeyStorePasswordInfo", "Geslo za shrambo SSL kljuÃ¯Â¿Â½?ev" },

        //
        { "JavaType", "Java VM" }, { "JavaTypeInfo", "Java VM Vendor" }, { "AppsType", "Server Type" }, { "AppsTypeInfo", "J2EE Application Server Type" }, { "DeployDir", "Deployment" }, { "DeployDirInfo", "J2EE Deployment Directory" }, { "ErrorDeployDir", "Error Deployment Directory" },

        //
        { "TestInfo", "Test informacije" }, { "Test", "Test" }, { "SaveInfo", "Shrani informacije" }, { "Save", "Shrani" }, { "HelpInfo", "PomoÃ¯Â¿Â½?" },

        //
        { "ServerError", "Napaka v nastavitvah programskega streÃÂ¾nika" }, { "ErrorJavaHome", "Error napaÃ¯Â¿Â½?en domaÃ¯Â¿Â½? imenik Java" }, { "ErrorOXPHome", "Error napaÃ¯Â¿Â½?en OpenXpertya Home imenik" }, { "ErrorAppsServer", "Error programski streÃÂ¾nik (ne uporabljaj imena localhost)" }, { "ErrorWebPort", "Error napaÃ¯Â¿Â½?na Web vrata" }, { "ErrorJNPPort", "Error napaÃ¯Â¿Â½?na JNP vrata" }, { "ErrorDatabaseServer", "Error streÃÂ¾nik baze podatkov (ne uporabljaj imena localhost)" }, { "ErrorDatabasePort", "Error napaÃ¯Â¿Â½?na vrata baze podatkov" }, { "ErrorJDBC", "Error napaka v JDBC povezavi" }, { "ErrorTNS", "Error napaka v TNS povezavi" }, { "ErrorMailServer", "Error streÃÂ¾nik elektronske poÃÂ¡te (ne uporabljaj imena localhost)" }, { "ErrorMail", "Error napaka elektronska poÃÂ¡ta" }, { "ErrorSave", "Error napaka pri shranjevanju datoteke" }, { "EnvironmentSaved", "Nastavitve so shranjene\nSedaj lahko poÃÂ¾enete programski streÃÂ¾nik." }
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
 * @(#)SetupRes_sl.java   11.jun 2007
 * 
 *  Fin del fichero SetupRes_sl.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
