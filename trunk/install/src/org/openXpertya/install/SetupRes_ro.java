/*
 * @(#)SetupRes_ro.java   11.jun 2007  Versión 2.2
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
public class SetupRes_ro extends ListResourceBundle {

    /** Descripción de Campo */
    static final Object[][]	contents	= new String[][] {

        { "InstalarServidorOXP", "Configurarea serverului OpenXpertya" }, { "Ok", "OK" }, { "File", "Aplica\u0163ie" }, { "Exit", "Ie\u015fire" }, { "Help", "Ajutor" }, { "PleaseCheck", "Consulta\u0163i" }, { "UnableToConnect", "Nu s-a putut ob\u0163ine ajutor de pe site-ul web al OpenXpertya" },

        //
        { "OXPHomeInfo", "Loca\u0163ia OpenXpertya reprezint\u0103 directorul s\u0103u de instalare" }, { "OXPHome", "Loca\u0163ie OpenXpertya" }, { "WebPortInfo", "Portul de web (HTML)" }, { "WebPort", "Port de web" }, { "AppsServerInfo", "Numele serverului de aplica\u0163ie" }, { "AppsServer", "Server de aplica\u0163ie" }, { "DatabaseTypeInfo", "Tipul bazei de date" }, { "DatabaseType", "Tip de baz\u0103 de date" }, { "DatabaseNameInfo", "Numele (serviciului) bazei de date" }, { "DatabaseName", "Nume de baz\u0103 de date" }, { "DatabasePortInfo", "Portul rezevat serviciului bazei de date" }, { "DatabasePort", "Port de baz\u0103 de date" }, { "DatabaseUserInfo", "Utilizatorul OpenXpertya pentru baza de date" }, { "DatabaseUser", "Utilizator de baz\u0103 de date" }, { "DatabasePasswordInfo", "Parola utilizatorului OpenXpertya pentru baza de date" }, { "DatabasePassword", "Parola pentru baza de date" }, { "TNSNameInfo", "Baze de date g\u0103site" }, { "TNSName", "C\u0103utare de baze de date" }, { "SystemPasswordInfo", "Parola utilizatorului System" }, { "SystemPassword", "Parol\u0103 pentru System" }, { "MailServerInfo", "Server de po\u015ft\u0103 electronic\u0103" }, { "MailServer", "Server de po\u015ft\u0103 electronic\u0103" }, { "AdminEMailInfo", "Adresa de po\u015ft\u0103 electronic\u0103 a administratorului OpenXpertya" }, { "AdminEMail", "Adres\u0103 de e-mail a administratorului" }, { "DatabaseServerInfo", "Numele serverului de baz\u0103 de date" }, { "DatabaseServer", "Server de baz\u0103 de date" }, { "JavaHomeInfo", "Loca\u0163ia de instalare a Java" }, { "JavaHome", "Loca\u0163ie Java" }, { "JNPPortInfo", "Portul JNP al serverului de aplica\u0163ie" }, { "JNPPort", "Port JNP" }, { "MailUserInfo", "Utilizatorul OpenXpertya pentru po\u015fta electronic\u0103" }, { "MailUser", "Utilizator de po\u015ft\u0103 electronic\u0103" }, { "MailPasswordInfo", "Parola utilizatorului OpenXpertya pentru po\u015fta electronic\u0103" }, { "MailPassword", "Parol\u0103 de po\u015ft\u0103 electronic\u0103" }, { "KeyStorePassword", "Parol\u0103 de keystore" }, { "KeyStorePasswordInfo", "Parola de pentru arhiva de chei SSL" },

        //
        { "JavaType", "Ma\u015fina virtual\u0103 Java" }, { "JavaTypeInfo", "Furnizorul ma\u015finii virtuale Java" }, { "AppsType", "Tip de server" }, { "AppsTypeInfo", "Tipul serverului de aplica\u0163ie J2EE" }, { "DeployDir", "Director de instalare" }, { "DeployDirInfo", "Directorul J2EE de instalare" }, { "ErrorDeployDir", "Director de instalare incorect" },

        //
        { "TestInfo", "Testarea configur\u0103rii" }, { "Test", "Testare" }, { "SaveInfo", "Salvarea configur\u0103rii" }, { "Save", "Salvare" }, { "HelpInfo", "Ob\u0163inere de ajutor" },

        //
        { "ServerError", "Eroare de configurare a serverului" }, { "ErrorJavaHome", "Eroare de loca\u0163ie Java" }, { "ErrorOXPHome", "Eroare de loca\u0163ie OpenXpertya" }, { "ErrorAppsServer", "Eroare de server de aplica\u0163ie (nu folosi\u0163i 'localhost')" }, { "ErrorWebPort", "Eroare de port de web" }, { "ErrorJNPPort", "Eroare de port JNP" }, { "ErrorDatabaseServer", "Eroare de server de baz\u0103 de date (nu folosi\u0163i 'localhost')" }, { "ErrorDatabasePort", "Eroare de port de baz\u0103 de date" }, { "ErrorJDBC", "Eroare de conexiune JDBC" }, { "ErrorTNS", "Eroare de conexiune TNS" }, { "ErrorMailServer", "Eroare de server de po\u015ft\u0103 electronic\u0103 (nu folosi\u0163i 'localhost')" }, { "ErrorMail", "Eroare de po\u015ft\u0103 electronic\u0103" }, { "ErrorSave", "Eroare la salvarea fi\u015fierului" }, { "EnvironmentSaved", "Configurarea a fost salvat\u0103... se \u00eencepe instalarea.\n" + "Pute\u0163i (re)porni serverul de aplica\u0163ie dup\u0103 terminarea programului curent.\n" + "Verifica\u0163i apoi dac\u0103 apar erori \u00een jurnal." }
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
}	// SetupRes



/*
 * @(#)SetupRes_ro.java   11.jun 2007
 * 
 *  Fin del fichero SetupRes_ro.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
