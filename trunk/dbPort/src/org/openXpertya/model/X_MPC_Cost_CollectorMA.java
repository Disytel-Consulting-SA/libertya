/*
 * @(#)X_MPC_Cost_CollectorMA.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.BigDecimal;

import java.sql.ResultSet;

import java.util.Properties;

/** Generated Model for M_InOutLineMA
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke (generated)
 *  @version Release 2.5.2d - 2005-05-29 00:55:54.808 */
public class X_MPC_Cost_CollectorMA extends PO {

/** AD_Table_ID=762 */
    public static final int	Table_ID	= 762;

/** TableName=M_InOutLineMA */
    public static final String	Table_Name	= "MPC_Cost_CollectorMA";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(762, "MPC_Cost_CollectorMA");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(1);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param MPC_Cos_tCollectorMA_ID
 * @param trxName
 */
    public X_MPC_Cost_CollectorMA(Properties ctx, int MPC_Cos_tCollectorMA_ID, String trxName) {

        super(ctx, MPC_Cos_tCollectorMA_ID, trxName);

/** if (M_InOutLineMA_ID == 0)
{
setM_AttributeSetInstance_ID (0);
setM_InOutLine_ID (0);
setMovementQty (Env.ZERO);
}
 */
    }

/**
 * Load Constructor
 *
 * @param ctx
 * @param rs
 * @param trxName
 */
    public X_MPC_Cost_CollectorMA(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

/**
 * Load Meta Data
 *
 * @param ctx
 *
 * @return
 */
    protected POInfo initPO(Properties ctx) {

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

        StringBuffer	sb	= new StringBuffer("X_M_InOutLineMA[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(getID(), String.valueOf(getMPC_Cost_Collector_ID()));
    }

/** Get Shipment/Receipt Line.
 *
 * @return
Line on Shipment or Receipt document */
    public int getMPC_Cost_Collector_ID() {

        Integer	ii	= (Integer) get_Value("M_InOutLine_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Attribute Set Instance.
 *
 * @return
Product Attribute Set Instance */
    public int getM_AttributeSetInstance_ID() {

        Integer	ii	= (Integer) get_Value("M_AttributeSetInstance_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Movement Quantity.
 *
 * @return
Quantity of a product moved. */
    public BigDecimal getMovementQty() {

        BigDecimal	bd	= (BigDecimal) get_Value("MovementQty");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

    //~--- set methods --------------------------------------------------------

/** Set Shipment/Receipt Line.
 *
 * @param M_InOutLine_ID
Line on Shipment or Receipt document */
    public void setMPC_Cost_Collector_ID(int M_InOutLine_ID) {
        set_ValueNoCheck("M_InOutLine_ID", new Integer(M_InOutLine_ID));
    }

/** Set Attribute Set Instance.
 *
 * @param M_AttributeSetInstance_ID
Product Attribute Set Instance */
    public void setM_AttributeSetInstance_ID(int M_AttributeSetInstance_ID) {
        set_ValueNoCheck("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
    }

/** Set Movement Quantity.
 *
 * @param MovementQty
Quantity of a product moved. */
    public void setMovementQty(BigDecimal MovementQty) {

        if (MovementQty == null) {
            throw new IllegalArgumentException("MovementQty is mandatory");
        }

        set_Value("MovementQty", MovementQty);
    }
}



/*
 * @(#)X_MPC_Cost_CollectorMA.java   02.jul 2007
 * 
 *  Fin del fichero X_MPC_Cost_CollectorMA.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
