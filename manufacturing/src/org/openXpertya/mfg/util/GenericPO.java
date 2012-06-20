/*
 * @(#)GenericPO.java   14.jun 2007  Versión 2.2
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



//----------------------------------------------------------------------
//Generic PO.
//Used to insert/update data from a data.xml file.


//package org.openXpertya.model;
package org.openXpertya.mfg.util;

//import for GenericPO
import java.util.*;

import java.sql.*;

import java.math.*;

import org.openXpertya.model.*;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 14.jun 2007
 * @autor     Fundesle    
 */
public class GenericPO extends PO {

    // private Logger  log = Logger.getCLogger(getClass());

    /**
     * Standard Constructor 
     *
     * @param ctx
     * @param ID
     * @param trxName
     */
    public GenericPO(Properties ctx, int ID, String trxName) {
        super(ctx, ID, trxName);
    }

    /**
     * Load Constructor 
     *
     * @param ctx
     * @param rs
     * @param trxName
     */
    public GenericPO(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

    /** Descripción de Campo */
    private int	Table_ID	= 0;

    /**
     * Load Meta Data 
     *
     * @param ctx
     *
     * @return
     */
    protected POInfo initPO(Properties ctx) {

        Table_ID	= Integer.valueOf(ctx.getProperty("compieredataTable_ID")).intValue();

        // log.info("Table_ID: "+Table_ID);
        POInfo	poi	= POInfo.getPOInfo(ctx, Table_ID);

        return poi;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("GenericPO[Table=").append("" + Table_ID + ",ID=").append(getID()).append("]");

        return sb.toString();
    }

    /** Descripción de Campo */
    public static final int	AD_ORGTRX_ID_AD_Reference_ID	= 130;

    /**
     * Set Trx Organization.
     *   Performing or initiating organization 
     *
     * @param AD_OrgTrx_ID
     */
    public void setAD_OrgTrx_ID(int AD_OrgTrx_ID) {

        if (AD_OrgTrx_ID == 0)
            set_Value("AD_OrgTrx_ID", null);
        else
            set_Value("AD_OrgTrx_ID", new Integer(AD_OrgTrx_ID));
    }

    /**
     * Get Trx Organization.
     *   Performing or initiating organization 
     *
     * @return
     */
    public int getAD_OrgTrx_ID() {

        Integer	ii	= (Integer) get_Value("AD_OrgTrx_ID");

        if (ii == null)
            return 0;

        return ii.intValue();
    }

    // setValue

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     * @param value
     */
    public void setValue(String columnName, Object value) {
        set_Value(columnName, value);
    }

    // setValueNoCheck

    /**
     * Descripción de Método
     *
     *
     * @param columnName
     * @param value
     */
    public void setValueNoCheck(String columnName, Object value) {
        set_ValueNoCheck(columnName, value);
    }

    // setValue

    /**
     * Descripción de Método
     *
     *
     * @param index
     * @param value
     */
    public void setValue(int index, Object value) {
        set_Value(index, value);
    }
}	// GenericPO



/*
 * @(#)GenericPO.java   14.jun 2007
 * 
 *  Fin del fichero GenericPO.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
