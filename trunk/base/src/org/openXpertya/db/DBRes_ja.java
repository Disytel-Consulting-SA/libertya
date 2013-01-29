/*
 * @(#)DBRes_ja.java   12.oct 2007  Versión 2.2
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
 *         *     Stefan Christians
 *  @version    $Id: DBRes_ja.java,v 1.3 2005/03/11 20:29:01 jjanke Exp $
 */
public class DBRes_ja extends ListResourceBundle {

    /** Data */
    static final Object[][]	contents	= new String[][] {
        { "CConnectionDialog", "\u30b3\u30f3\u30d4\u30a8\u30fc\u30ec\u306e\u63a5\u7d9a" }, { "Name", "\u540d\u524d" }, { "AppsHost", "\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u30fb\u30b5\u30fc\u30d0" }, { "AppsPort", "\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u30fb\u30dd\u30fc\u30c8" }, { "TestApps", "\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u30fb\u30b5\u30fc\u30d0\u306e\u30c6\u30b9\u30c8" }, { "DBHost", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30fb\u30b5\u30fc\u30d0" }, { "DBPort", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30fb\u30dd\u30fc\u30c8" }, { "DBName", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u306e\u540d\u524d" }, { "DBUidPwd", "\u30e6\u30fc\u30b6 / \u30d1\u30b9\u30ef\u30fc\u30c9" }, { "ViaFirewall", "\u30d5\u30a1\u30a4\u30a2\u30a6\u30a9\u30fc\u30eb" }, { "FWHost", "\u30d5\u30a1\u30a4\u30a2\u30a6\u30a9\u30fc\u30eb\u30fb\u30b5\u30fc\u30d0" }, { "FWPort", "\u30d5\u30a1\u30a4\u30a2\u30a6\u30a9\u30fc\u30eb\u30fb\u30dd\u30fc\u30c8" }, { "TestConnection", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9\u30fb\u30b5\u30fc\u30d0\u306e\u30c6\u30b9\u30c8" }, { "Type", "\u30c7\u30fc\u30bf\u30d9\u30fc\u30b9" }, { "BequeathConnection", "\u53e4\u63a5\u7d9a" }, { "Overwrite", "\u30aa\u30fc\u30f4\u30a1\u30e9\u30a4\u30c8" }, { "RMIoverHTTP", "HTTP\u30c8\u30cd\u30eb" }, { "ConnectionError", "\u63a5\u7d9a\u306e\u30a8\u30e9\u30fc" }, { "ServerNotActive", "\u30b5\u30fc\u30d0\u3092\u898b\u4ed8\u3051\u308c\u306a\u3044" }
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
 * @(#)DBRes_ja.java   02.jul 2007
 * 
 *  Fin del fichero DBRes_ja.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
