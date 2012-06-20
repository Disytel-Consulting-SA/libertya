/*
 * @(#)SetupRes_fi.java   11.jun 2007  Versión 2.2
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
public class SetupRes_fi extends ListResourceBundle {

    /** Descripción de Campo */
    static final Object[][]	contents	= new String[][] {

        { "InstalarServidorOXP", "OpenXpertya-palvelimen Asetukset" }, { "Ok", "HyvÃ¯Â¿Â½ksy" }, { "File", "Tiedosto" }, { "Exit", "Poistu" }, { "Help", "Help" }, { "PleaseCheck", "Ole hyvÃ¯Â¿Â½ ja valitse" }, { "UnableToConnect", "Yhteydenotto Compieren Web-Help:in ei onnistu" },

        //
        { "OXPHomeInfo", "OpenXpertya Home on pÃ¯Â¿Â½Ã¯Â¿Â½kansio" }, { "OXPHome", "OpenXpertya Home" }, { "WebPortInfo", "Web (HTML) Portti" }, { "WebPort", "Web Portti" }, { "AppsServerInfo", "Sovelluspalvelimen Nimi" }, { "AppsServer", "Sovelluspalvelin" }, { "DatabaseTypeInfo", "Tietokantatyyppi" }, { "DatabaseType", "Tietokantatyyppi" }, { "DatabaseNameInfo", "Tietokannan Nimi" }, { "DatabaseName", "Tietokannan Nimi (SID)" }, { "DatabasePortInfo", "Tietokannan kuuntelijaportti" }, { "DatabasePort", "Tietokantaportti" }, { "DatabaseUserInfo", "Tietokannan OpenXpertya-kÃ¯Â¿Â½yttÃ¯Â¿Â½jÃ¯Â¿Â½tunnus" }, { "DatabaseUser", "Tietokannan kÃ¯Â¿Â½yttÃ¯Â¿Â½jÃ¯Â¿Â½tunnus" }, { "DatabasePasswordInfo", "Tietokannan OpenXpertya-salasana" }, { "DatabasePassword", "Tietokannan salasana" }, { "TNSNameInfo", "TNS tai Globaali Tietokannan Nimi" }, { "TNSName", "TNS Nimi" }, { "SystemPasswordInfo", "JÃ¯Â¿Â½rjestelmÃ¯Â¿Â½salasana" }, { "SystemPassword", "JÃ¯Â¿Â½rjestelmÃ¯Â¿Â½salasana" }, { "MailServerInfo", "SÃ¯Â¿Â½hkÃ¯Â¿Â½postipalvelin" }, { "MailServer", "SÃ¯Â¿Â½hkÃ¯Â¿Â½postipalvelin" }, { "AdminEMailInfo", "OpenXpertya-yllÃ¯Â¿Â½pitÃ¯Â¿Â½jÃ¯Â¿Â½n SÃ¯Â¿Â½hkÃ¯Â¿Â½posti" }, { "AdminEMail", "YllÃ¯Â¿Â½pitÃ¯Â¿Â½jÃ¯Â¿Â½n SÃ¯Â¿Â½hkÃ¯Â¿Â½posti" }, { "DatabaseServerInfo", "Tietokantapalvelimen Nimi" }, { "DatabaseServer", "Tietokantapalvelin" }, { "JavaHomeInfo", "Java-kotihakemisto" }, { "JavaHome", "Java-koti" }, { "JNPPortInfo", "Sovelluspalvelimen JNP-portti" }, { "JNPPort", "JNP-portti" }, { "MailUserInfo", "OpenXpertya-sÃ¯Â¿Â½hkÃ¯Â¿Â½postikÃ¯Â¿Â½yttÃ¯Â¿Â½jÃ¯Â¿Â½" }, { "MailUser", "SÃ¯Â¿Â½hkÃ¯Â¿Â½postikÃ¯Â¿Â½yttÃ¯Â¿Â½jÃ¯Â¿Â½" }, { "MailPasswordInfo", "OpenXpertya-sÃ¯Â¿Â½hkÃ¯Â¿Â½postisalasana" }, { "MailPassword", "SÃ¯Â¿Â½hkÃ¯Â¿Â½postisalasana" }, { "KeyStorePassword", "Key Store Password" }, { "KeyStorePasswordInfo", "Password for SSL Key Store" },

        //
        { "JavaType", "Java VM" }, { "JavaTypeInfo", "Java VM Vendor" }, { "AppsType", "Server Type" }, { "AppsTypeInfo", "J2EE Application Server Type" }, { "DeployDir", "Deployment" }, { "DeployDirInfo", "J2EE Deployment Directory" }, { "ErrorDeployDir", "Error Deployment Directory" },

        //
        { "TestInfo", "Testaa Asetukset" }, { "Test", "Testaa" }, { "SaveInfo", "Tallenna Asetukset" }, { "Save", "Tallenna" }, { "HelpInfo", "Hae Apua" },

        //
        { "ServerError", "Palvelimen Asetusvirhe" }, { "ErrorJavaHome", "Java-kotivirhe" }, { "ErrorOXPHome", "OpenXpertya-kotivirhe" }, { "ErrorAppsServer", "Sovelluspalvelinvirhe (Ã¯Â¿Â½lÃ¯Â¿Â½ kÃ¯Â¿Â½ytÃ¯Â¿Â½ paikallisverkkoasemaa)" }, { "ErrorWebPort", "Web-porttivirhe" }, { "ErrorJNPPort", "JNP-porttivirhe" }, { "ErrorDatabaseServer", "Tietokantapalvelinvirhe (Ã¯Â¿Â½lÃ¯Â¿Â½ kÃ¯Â¿Â½ytÃ¯Â¿Â½ paikallisverkkoasemaa)" }, { "ErrorDatabasePort", "Tietokantaporttivirhe" }, { "ErrorJDBC", "JDBC-yhteysvirhe" }, { "ErrorTNS", "TNS-yhteysvirhe" }, { "ErrorMailServer", "SÃ¯Â¿Â½hkÃ¯Â¿Â½postipalvelinvirhe (Ã¯Â¿Â½lÃ¯Â¿Â½ kÃ¯Â¿Â½ytÃ¯Â¿Â½ paikallisverkkoasemaa)" }, { "ErrorMail", "SÃ¯Â¿Â½hkÃ¯Â¿Â½postivirhe" }, { "ErrorSave", "Tiedostontallennusvirhe" }, { "EnvironmentSaved", "YmpÃ¯Â¿Â½ristÃ¯Â¿Â½ tallennettu/Palvelin tÃ¯Â¿Â½ytyy kÃ¯Â¿Â½ynnistÃ¯Â¿Â½Ã¯Â¿Â½ uudelleen." }, { "RMIoverHTTP", "Tunneloi objektit HTTP kautta" }, { "RMIoverHTTPInfo", "RMI HTTP:n yli mahdollistaa palomuurien lÃ¯Â¿Â½pÃ¯Â¿Â½isyn" }
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
 * @(#)SetupRes_fi.java   11.jun 2007
 * 
 *  Fin del fichero SetupRes_fi.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 11.jun 2007
