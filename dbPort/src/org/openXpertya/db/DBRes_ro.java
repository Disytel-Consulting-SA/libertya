/*
 * @(#)DBRes_ro.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.db;

import java.util.ListResourceBundle;

/**
 *  Connection Resource Strings
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: DBRes_ro.java,v 1.6 2005/03/11 20:29:01 jjanke Exp $
 */
public class DBRes_ro extends ListResourceBundle {

    /** Data */
    static final Object[][]	contents	= new String[][] {
        { "CConnectionDialog", "Conexiune" }, { "Name", "Nume" }, { "AppsHost", "Server de aplica\u0163ie" }, { "AppsPort", "Port de aplica\u0163ie" }, { "TestApps", "Testare a serverului de aplica\u0163ie" }, { "DBHost", "Server de baz\u0103 de date" }, { "DBPort", "Port de baz\u0103 de date" }, { "DBName", "Numele bazei de date" }, { "DBUidPwd", "Utilizator / parol\u0103" }, { "ViaFirewall", "Prin firewall" }, { "FWHost", "Gazd\u0103 de firewall" }, { "FWPort", "Port de firewall" }, { "TestConnection", "Testare a bazei de date" }, { "Type", "Tip al bazei de date" }, { "BequeathConnection", "Cedare de conexiune" }, { "Overwrite", "Suprascriere" }, { "RMIoverHTTP", "Traficare a obiectelor prin HTTP" }, { "ConnectionError", "Eroare de conexiune" }, { "ServerNotActive", "Serverul este inactiv" }
    };

    //~--- get methods --------------------------------------------------------

    /**
     * Get Contents
     * @return contents
     */
    public Object[][] getContents() {
        return contents;
    }		// getContent
}	// Res



/*
 * @(#)DBRes_ro.java   02.jul 2007
 * 
 *  Fin del fichero DBRes_ro.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
