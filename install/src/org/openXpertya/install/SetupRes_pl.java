/*
 * @(#)SetupRes_pl.java   11.jun 2007  Versión 2.2
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
public class SetupRes_pl extends ListResourceBundle {

    /** Descripción de Campo */
    static final Object[][]	contents	= new String[][] {

        { "InstalarServidorOXP", "Konfiguracja serwera OpenXpertya" }, { "Ok", "Ok" }, { "File", "Plik" }, { "Exit", "Wyj\u015bcie" }, { "Help", "Pomoc" }, { "PleaseCheck", "Prosz\u0119 sprawdzi\u0107" }, { "UnableToConnect", "Nie mo\u017cna po\u0142\u0105czy\u0107 si\u0119 ze stron\u0105 OpenXpertya w celu uzyskania pomocy" }, { "OXPHomeInfo", "Folder OpenXpertya jest folderem g\u0142\u00f3wnym" }, { "OXPHome", "Folder OpenXpertya" }, { "WebPortInfo", "Web (HTML) Port" }, { "WebPort", "Web Port" }, { "AppsServerInfo", "Nazwa serwera aplikacji" }, { "AppsServer", "Serwer bazy danych" }, { "DatabaseTypeInfo", "Typ bazy danych" }, { "DatabaseType", "Typ bazy danych" }, { "DatabaseNameInfo", "Nazwa bazy danych " }, { "DatabaseName", "Nazwa bazy danych (SID)" }, { "DatabasePortInfo", "Port listenera bazy danych" }, { "DatabasePort", "Port bazy danych" }, { "DatabaseUserInfo", "U\u017cytkownik OpenXpertya w bazie danych" }, { "DatabaseUser", "U\u017cytkownik bazy" }, { "DatabasePasswordInfo", "Has\u0142o u\u017cytkownika OpenXpertya" }, { "DatabasePassword", "Has\u0142o u\u017cytkownika" }, { "TNSNameInfo", "TNS lub Globalna Nazwa Bazy (dla Oracle)" }, { "TNSName", "Nazwa TNS" }, { "SystemPasswordInfo", "Has\u0142o dla u\u017cytkownika System w bazie danych" }, { "SystemPassword", "Has\u0142o System" }, { "MailServerInfo", "Serwer pocztowy" }, { "MailServer", "Serwer pocztowy" }, { "AdminEMailInfo", "Adres email administartora OpenXpertya" }, { "AdminEMail", "EMail administ." }, { "DatabaseServerInfo", "Nazwa serwera bazy danych" }, { "DatabaseServer", "Serwer bazy danych" }, { "JavaHomeInfo", "Folder Javy" }, { "JavaHome", "Folder Javy" }, { "JNPPortInfo", "Application Server JNP Port" }, { "JNPPort", "JNP Port" }, { "MailUserInfo", "U\u017cytkownik poczty dla cel\u00f3w administracyjnych OpenXpertya" }, { "MailUser", "U\u017cytkownik poczty" }, { "MailPasswordInfo", "Has\u0142o dla konta pocztowego OpenXpertya" }, { "MailPassword", "Has\u0142o poczty" }, { "KeyStorePassword", "Key Store Password" }, { "KeyStorePasswordInfo", "Password for SSL Key Store" },

        //
        { "JavaType", "Java VM" }, { "JavaTypeInfo", "Java VM Vendor" }, { "AppsType", "Server Type" }, { "AppsTypeInfo", "J2EE Application Server Type" }, { "DeployDir", "Deployment" }, { "DeployDirInfo", "J2EE Deployment Directory" }, { "ErrorDeployDir", "Error Deployment Directory" },

        //
        { "TestInfo", "Sprawd\u017a ustawienia" }, { "Test", "Testuj" }, { "SaveInfo", "Zapisz ustawienia" }, { "Save", "Zapisz" }, { "HelpInfo", "Pomoc" }, { "ServerError", "B\u0142\u0119dne ustawienia" }, { "ErrorJavaHome", "Niepoprawny folder Javy" }, { "ErrorOXPHome", "Nie stwierdzono zainstalowanego systemu OpenXpertya w miescu wskazanym jako Folder OpenXpertya" }, { "ErrorAppsServer", "Niepoprawny serwer aplikacji (nie mo\u017ce by\u0107 localhost)" }, { "ErrorWebPort", "Niepoprawny port WWW (by\u0107 mo\u017ce inna aplikacja u\u017cywa ju\u017c tego portu)" }, { "ErrorJNPPort", "Niepoprawny port JNP (by\u0107 mo\u017ce inna aplikacja u\u017cywa ju\u017c tego portu)" }, { "ErrorDatabaseServer", "Niepoprawny serwer bazy (nie mo\u017ce by\u0107 localhost)" }, { "ErrorDatabasePort", "Niepoprawny port serwer bazy" }, { "ErrorJDBC", "Wyst\u0105pi\u0142 b\u0142\u0105d przy pr\u00f3bie po\u0142\u0105cznia si\u0119 z baz\u0105 danych" }, { "ErrorTNS", "Wyst\u0105pi\u0142 b\u0142\u0105d przy pr\u00f3bie po\u0142\u0105cznia si\u0119 z baz\u0105 danych poprzez TNS" }, { "ErrorMailServer", "Niepoprawny serwer pocztowy (nie mo\u017ce by\u0107 localhost)" }, { "ErrorMail", "B\u0142\u0105d poczty" }, { "ErrorSave", "B\u0142\u0105d przy zapisywaniu konfiguracji" }, { "EnvironmentSaved", "Ustawienia zapisany\nMusisz ponownie uruchomi\u0107 serwer." }, { "RMIoverHTTP", "Tunelowanie RMI over HTTP" }, { "RMIoverHTTPInfo", "Tunelowanie RMI over HTTP pozwala u\u017cywa\u0107 OpenXpertya przez firewall" }
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
 * @(#)SetupRes_pl.java   11.jun 2007
 * 
 *  Fin del fichero SetupRes_pl.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
