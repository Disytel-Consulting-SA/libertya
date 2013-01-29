/*
 * @(#)DBRes_fa.java   12.oct 2007  Versión 2.2
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
 *         *     .
 *  @version    $Id: DBRes_fa.java,v 1.4 2005/03/11 20:29:00 jjanke Exp $
 */
public class DBRes_fa extends ListResourceBundle {

    /** Data */
    static final Object[][]	contents	= new String[][] {
        { "CConnectionDialog", "\u0627\u062a\u0635\u0627\u0644 \u0628\u0647 \u06a9\u0627\u0645\u067e\u064a\u0631\u0647" }, { "Name", "\u0646\u0627\u0645" }, { "AppsHost", "\u0633\u064a\u0633\u062a\u0645 \u0645\u064a\u0632\u0628\u0627\u0646 \u06a9\u0627\u0631\u0628\u0631\u062f" }, { "AppsPort", "\u062f\u0631\u06af\u0627\u0647 \u06a9\u0627\u0631\u0628\u0631\u062f" }, { "TestApps", "\u0633\u0631\u0648\u0631 \u06a9\u0627\u0631\u0628\u0631\u062f \u0622\u0632\u0645\u0627\u064a\u0634\u06cc" }, { "DBHost", "\u0645\u064a\u0632\u0628\u0627\u0646 \u0628\u0627\u0646\u06a9 \u0627\u0637\u0644\u0627\u0639\u0627\u062a\u06cc" }, { "DBPort", "\u062f\u0631\u06af\u0627\u0647 \u0628\u0627\u0646\u06a9 \u0627\u0637\u0644\u0627\u0639\u0627\u062a\u06cc" }, { "DBName", "\u0646\u0627\u0645 \u0628\u0627\u0646\u06a9 \u0627\u0637\u0644\u0627\u0639\u0627\u062a" }, { "DBUidPwd", "\u0645\u0634\u062e\u0635\u0647 \u0627\u0633\u062a\u0641\u0627\u062f\u0647 \u06a9\u0646\u0646\u062f\u0647 \u0648 \u06a9\u0644\u0645\u0647 \u0639\u0628\u0648\u0631" }, { "ViaFirewall", "\u0627\u0632 \u0637\u0631\u0650\u064a\u0642 \u0641\u0627\u064a\u0631\u0648\u0627\u0644" }, { "FWHost", "\u0645\u064a\u0632\u0628\u0627\u0646 \u0641\u0627\u064a\u0631\u0648\u0627\u0644" }, { "FWPort", "\u062f\u0631\u06af\u0627\u0647 \u0641\u0627\u064a\u0631\u0648\u0627\u0644" }, { "TestConnection", "\u0628\u0627\u0646\u06a9 \u0627\u0637\u0644\u0627\u0639\u0627\u062a \u0622\u0632\u0645\u0627\u064a\u0634" }, { "Type", "\u0646\u0648\u0639 \u0628\u0627\u0646\u06a9 \u0627\u0637\u0644\u0627\u0639\u0627\u062a" }, { "BequeathConnection", "\u0627\u062a\u0635\u0627\u0644 \u062a\u062e\u0635\u064a\u0635 \u062f\u0627\u062f\u0647 \u0634\u062f\u0647" }, { "Overwrite", "\u0628\u0627\u0632\u0646\u0648\u064a\u0633\u06cc" }, { "RMIoverHTTP", "\u0627\u0634\u064a\u0627\u0621 \u0631\u0627 \u0627\u0632 \u0637\u0631\u0650\u0642 \u0627\u0686 \u062a\u06cc \u062a\u06cc \u067e\u06cc \u062a\u0648\u0646\u0644 \u06a9\u0646" }, { "ConnectionError", "\u062e\u0637\u0627 \u062f\u0631 \u0627\u062a\u0635\u0627\u0644" }, { "ServerNotActive", "\u0633\u0631\u0648\u0631 \u0641\u0639\u0627\u0644 \u0646\u064a\u0633\u062a" }
    };

    //~--- get methods --------------------------------------------------------

    /**
     * Get Contsnts
     * @return contents
     */
    public Object[][] getContents() {
        return contents;
    }		// getContent
}	// Res



/*
 * @(#)DBRes_fa.java   02.jul 2007
 * 
 *  Fin del fichero DBRes_fa.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
