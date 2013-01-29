/*
 * @(#)X_M_CostDetail.java   12.oct 2007  Versión 2.2
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

/** Modelo Generado para M_CostDetail
  *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Comunidad de Desarrollo OpenXpertya (generado)
        *Basado en Diverso Codigo Original Modificado, Revisado y Optimizado de:
        *Copyright ï¿½ 1999-2001 Jorg Janke, Copyright ï¿½ ComPiere, Inc. *  @version Version 1.2 18:23:19.759 */
public class X_M_CostDetail extends PO {

/** AD_Table_ID=808 */
    public static final int	Table_ID	= 808;

/** TableName=M_CostDetail */
    public static final String	Table_Name	= "M_CostDetail";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(808, "M_CostDetail");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Constructor Standard
 *
 * @param ctx
 * @param M_CostDetail_ID
 * @param trxName
 */
    public X_M_CostDetail(Properties ctx, int M_CostDetail_ID, String trxName) {

        super(ctx, M_CostDetail_ID, trxName);

/** if (M_CostDetail_ID == 0)
{
setAmt (Env.ZERO);
setC_AcctSchema_ID (0);
setM_AttributeSetInstance_ID (0);
setM_CostDetail_ID (0);
setM_Product_ID (0);
setProcessed (false);
setQty (Env.ZERO);
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
    public X_M_CostDetail(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_M_CostDetail[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/** Get Amount.
 *
 * @return
Amount */
    public BigDecimal getAmt() {

        BigDecimal	bd	= (BigDecimal) get_Value("Amt");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get Accounting Schema.
 *
 * @return
Rules for accounting */
    public int getC_AcctSchema_ID() {

        Integer	ii	= (Integer) get_Value("C_AcctSchema_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Invoice Line.
 *
 * @return
Invoice Detail Line */
    public int getC_InvoiceLine_ID() {

        Integer	ii	= (Integer) get_Value("C_InvoiceLine_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Sales Order Line.
 *
 * @return
Sales Order Line */
    public int getC_OrderLine_ID() {

        Integer	ii	= (Integer) get_Value("C_OrderLine_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Description.
 *
 * @return
Optional short description of the record */
    public String getDescription() {
        return (String) get_Value("Description");
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

/** Get Cost Detail.
 *
 * @return
Cost Detail Information */
    public int getM_CostDetail_ID() {

        Integer	ii	= (Integer) get_Value("M_CostDetail_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Cost Element.
 *
 * @return
Product Cost Element */
    public int getM_CostElement_ID() {

        Integer	ii	= (Integer) get_Value("M_CostElement_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Shipment/Receipt Line.
 *
 * @return
Line on Shipment or Receipt document */
    public int getM_InOutLine_ID() {

        Integer	ii	= (Integer) get_Value("M_InOutLine_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Product.
 *
 * @return
Product, Service, Item */
    public int getM_Product_ID() {

        Integer	ii	= (Integer) get_Value("M_Product_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Quantity.
 *
 * @return
Quantity */
    public BigDecimal getQty() {

        BigDecimal	bd	= (BigDecimal) get_Value("Qty");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get Processed.
 *
 * @return
The document has been processed */
    public boolean isProcessed() {

        Object	oo	= get_Value("Processed");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

    //~--- set methods --------------------------------------------------------

/** Set Amount.
 *
 * @param Amt
Amount */
    public void setAmt(BigDecimal Amt) {

        if (Amt == null) {
            throw new IllegalArgumentException("Amt is mandatory");
        }

        set_Value("Amt", Amt);
    }

/** Set Accounting Schema.
 *
 * @param C_AcctSchema_ID
Rules for accounting */
    public void setC_AcctSchema_ID(int C_AcctSchema_ID) {
        set_ValueNoCheck("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
    }

/** Set Invoice Line.
 *
 * @param C_InvoiceLine_ID
Invoice Detail Line */
    public void setC_InvoiceLine_ID(int C_InvoiceLine_ID) {

        if (C_InvoiceLine_ID <= 0) {
            set_ValueNoCheck("C_InvoiceLine_ID", null);
        } else {
            set_ValueNoCheck("C_InvoiceLine_ID", new Integer(C_InvoiceLine_ID));
        }
    }

/** Set Sales Order Line.
 *
 * @param C_OrderLine_ID
Sales Order Line */
    public void setC_OrderLine_ID(int C_OrderLine_ID) {

        if (C_OrderLine_ID <= 0) {
            set_ValueNoCheck("C_OrderLine_ID", null);
        } else {
            set_ValueNoCheck("C_OrderLine_ID", new Integer(C_OrderLine_ID));
        }
    }

/** Set Description.
 *
 * @param Description
Optional short description of the record */
    public void setDescription(String Description) {

        if ((Description != null) && (Description.length() > 255)) {

            log.warning("Length > 255 - truncated");
            Description	= Description.substring(0, 254);
        }

        set_Value("Description", Description);
    }

/** Set Attribute Set Instance.
 *
 * @param M_AttributeSetInstance_ID
Product Attribute Set Instance */
    public void setM_AttributeSetInstance_ID(int M_AttributeSetInstance_ID) {
        set_ValueNoCheck("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
    }

/** Set Cost Detail.
 *
 * @param M_CostDetail_ID
Cost Detail Information */
    public void setM_CostDetail_ID(int M_CostDetail_ID) {
        set_ValueNoCheck("M_CostDetail_ID", new Integer(M_CostDetail_ID));
    }

/** Set Cost Element.
 *
 * @param M_CostElement_ID
Product Cost Element */
    public void setM_CostElement_ID(int M_CostElement_ID) {

        if (M_CostElement_ID <= 0) {
            set_ValueNoCheck("M_CostElement_ID", null);
        } else {
            set_ValueNoCheck("M_CostElement_ID", new Integer(M_CostElement_ID));
        }
    }

/** Set Shipment/Receipt Line.
 *
 * @param M_InOutLine_ID
Line on Shipment or Receipt document */
    public void setM_InOutLine_ID(int M_InOutLine_ID) {

        if (M_InOutLine_ID <= 0) {
            set_ValueNoCheck("M_InOutLine_ID", null);
        } else {
            set_ValueNoCheck("M_InOutLine_ID", new Integer(M_InOutLine_ID));
        }
    }

/** Set Product.
 *
 * @param M_Product_ID
Product, Service, Item */
    public void setM_Product_ID(int M_Product_ID) {
        set_ValueNoCheck("M_Product_ID", new Integer(M_Product_ID));
    }

/** Set Processed.
 *
 * @param Processed
The document has been processed */
    public void setProcessed(boolean Processed) {
        set_Value("Processed", new Boolean(Processed));
    }

/** Set Quantity.
 *
 * @param Qty
Quantity */
    public void setQty(BigDecimal Qty) {

        if (Qty == null) {
            throw new IllegalArgumentException("Qty is mandatory");
        }

        set_Value("Qty", Qty);
    }
}



/*
 * @(#)X_M_CostDetail.java   02.jul 2007
 * 
 *  Fin del fichero X_M_CostDetail.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
