/*
 * @(#)MConversionType.java   12.oct 2007  Versión 2.2
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



package org.openXpertya.model;

import org.openXpertya.util.CCache;
import org.openXpertya.util.DB;

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.ResultSet;

import java.util.Properties;

/**
 *      Currency Conversion Type Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MConversionType.java,v 1.6 2005/03/11 20:28:32 jjanke Exp $
 */
public class MConversionType extends X_C_ConversionType {

    /** Cache */
    private static CCache	s_cache	= new CCache("C_ConversionType", 4);

    /**
     *      Standard Constructor
     *      @param ctx context
     *      @param C_ConversionType_ID id
     * @param trxName
     */
    public MConversionType(Properties ctx, int C_ConversionType_ID, String trxName) {
        super(ctx, C_ConversionType_ID, trxName);
    }		// MConversionType

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MConversionType(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MConversionType

    //~--- get methods --------------------------------------------------------

    /**
     *      Get Default Conversion Rate for Client/Org
     *      @param AD_Client_ID client
     *      @return C_ConversionType_ID or 0 if not found
     */
    public static int getDefault(int AD_Client_ID) {

        // Try Cache
        Integer	key	= new Integer(AD_Client_ID);
        Integer	ii	= (Integer) s_cache.get(key);

        if (ii != null) {
            return ii.intValue();
        }

        // Get from DB
        int	C_ConversionType_ID	= 0;
        String	sql			= "SELECT C_ConversionType_ID " + "FROM C_ConversionType " + "WHERE IsActive='Y'" + " AND AD_Client_ID IN (0,?) "	// #1
                                          + "ORDER BY IsDefault DESC, AD_Client_ID DESC";

        C_ConversionType_ID	= DB.getSQLValue(null, sql, AD_Client_ID);

        // Return
        s_cache.put(key, new Integer(C_ConversionType_ID));

        return C_ConversionType_ID;
    }		// getDefault
}	// MConversionType



/*
 * @(#)MConversionType.java   02.jul 2007
 * 
 *  Fin del fichero MConversionType.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
