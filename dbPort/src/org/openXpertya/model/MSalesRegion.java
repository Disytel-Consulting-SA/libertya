/*
 * @(#)MSalesRegion.java   12.oct 2007  Versión 2.2
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

//~--- Importaciones JDK ------------------------------------------------------

import java.sql.ResultSet;

import java.util.Properties;

/**
 *      Sales Region Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MSalesRegion.java,v 1.6 2005/03/11 20:28:36 jjanke Exp $
 */
public class MSalesRegion extends X_C_SalesRegion {

    /** Cache */
    private static CCache	s_cache	= new CCache("C_SalesRegion", 10);

    /**
     *      Default Constructor
     *      @param ctx context
     *      @param C_SalesRegion_ID id
     * @param trxName
     */
    public MSalesRegion(Properties ctx, int C_SalesRegion_ID, String trxName) {
        super(ctx, C_SalesRegion_ID, trxName);
    }		// MSalesRegion

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MSalesRegion(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MSalesRegion

    /**
     *      After Delete
     *      @param success
     *      @return deleted
     */
    protected boolean afterDelete(boolean success) {

        if (success) {
            delete_Tree(MTree_Base.TREETYPE_SalesRegion);
        }

        return true;

    }		// afterDelete

    /**
     *      After Save.
     *      Insert
     *      - create tree
     *      @param newRecord insert
     *      @param success save success
     *
     * @return
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        if (!success) {
            return success;
        }

        if (newRecord) {
            insert_Tree(MTree_Base.TREETYPE_SalesRegion);
        }

        // Value/Name change
        if (!newRecord && (is_ValueChanged("Value") || is_ValueChanged("Name"))) {
            MAccount.updateValueDescription(getCtx(), "C_SalesRegion_ID=" + getC_SalesRegion_ID(), get_TrxName());
        }

        return true;

    }		// afterSave

    //~--- get methods --------------------------------------------------------

    /**
     *      Get SalesRegion from Cache
     *      @param ctx context
     *      @param C_SalesRegion_ID id
     *      @return MSalesRegion
     */
    public static MSalesRegion get(Properties ctx, int C_SalesRegion_ID) {

        Integer		key		= new Integer(C_SalesRegion_ID);
        MSalesRegion	retValue	= (MSalesRegion) s_cache.get(key);

        if (retValue != null) {
            return retValue;
        }

        retValue	= new MSalesRegion(ctx, C_SalesRegion_ID, null);

        if (retValue.getID() != 0) {
            s_cache.put(key, retValue);
        }

        return retValue;

    }		// get
}	// MSalesRegion



/*
 * @(#)MSalesRegion.java   02.jul 2007
 * 
 *  Fin del fichero MSalesRegion.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
