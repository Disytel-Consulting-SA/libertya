/*
 * @(#)X_C_Action.java   12.oct 2007  Versión 2.2
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

/** Generated Model for C_Action
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-05-30 09:52:57.531 */
public class X_C_Action extends PO {

/** AD_Table_ID=1000760 */
    public static final int	Table_ID	= 1000760;

/** TableName=C_Action */
    public static final String	Table_Name	= "C_Action";

    /** Descripción de Campo */
    public static final int	SALES_USER_ID_AD_Reference_ID	= 286;

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000760, "C_Action");

    /** Descripción de Campo */
    public static final int	C_BPARTNER_ID_AD_Reference_ID	= 1000040;

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_Action_ID
 * @param trxName
 */
    public X_C_Action(Properties ctx, int C_Action_ID, String trxName) {

        super(ctx, C_Action_ID, trxName);

/** if (C_Action_ID == 0)
{
setAD_User_ID (0);      // -1
setC_Action_ID (0);
setC_Action_Type_ID (0);
setC_BPartner_ID (0);
setQtyEntered (Env.ZERO);
setSales_User_ID (0);   // @#AD_User_ID@
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
    public X_C_Action(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_Action[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/**
 * Get AD_User_ID
 *
 * @return
 */
    public int getAD_User_ID() {

        Integer	ii	= (Integer) get_Value("AD_User_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get C_Action_ID
 *
 * @return
 */
    public int getC_Action_ID() {

        Integer	ii	= (Integer) get_Value("C_Action_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get C_Action_Type_ID
 *
 * @return
 */
    public int getC_Action_Type_ID() {

        Integer	ii	= (Integer) get_Value("C_Action_Type_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get C_BPartner_ID
 *
 * @return
 */
    public int getC_BPartner_ID() {

        Integer	ii	= (Integer) get_Value("C_BPartner_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Description
 *
 * @return
 */
    public String getDescription() {
        return (String) get_Value("Description");
    }

/** Get Price List.
 *
 * @return
Unique identifier of a Price List */
    public int getM_PriceList_ID() {

        Integer	ii	= (Integer) get_Value("M_PriceList_ID");

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

/**
 * Get M_Warehouse_ID
 *
 * @return
 */
    public int getM_Warehouse_ID() {

        Integer	ii	= (Integer) get_Value("M_Warehouse_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Process
 *
 * @return
 */
    public String getProcess() {
        return (String) get_Value("Process");
    }

/** Get Quantity.
 *
 * @return
The Quantity Entered is based on the selected UoM */
    public BigDecimal getQtyEntered() {

        BigDecimal	bd	= (BigDecimal) get_Value("QtyEntered");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/**
 * Get Sales_User_ID
 *
 * @return
 */
    public int getSales_User_ID() {

        Integer	ii	= (Integer) get_Value("Sales_User_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Processed
 *
 * @return
 */
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

/**
 * Get IsSales
 *
 * @return
 */
    public boolean isSales() {

        Object	oo	= get_Value("IsSales");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

    //~--- set methods --------------------------------------------------------

/**
 * Set AD_User_ID
 *
 * @param AD_User_ID
 */
    public void setAD_User_ID(int AD_User_ID) {
        set_Value("AD_User_ID", new Integer(AD_User_ID));
    }

/**
 * Set C_Action_ID
 *
 * @param C_Action_ID
 */
    public void setC_Action_ID(int C_Action_ID) {
        set_Value("C_Action_ID", new Integer(C_Action_ID));
    }

/**
 * Set C_Action_Type_ID
 *
 * @param C_Action_Type_ID
 */
    public void setC_Action_Type_ID(int C_Action_Type_ID) {
        set_Value("C_Action_Type_ID", new Integer(C_Action_Type_ID));
    }

/**
 * Set C_BPartner_ID
 *
 * @param C_BPartner_ID
 */
    public void setC_BPartner_ID(int C_BPartner_ID) {
        set_Value("C_BPartner_ID", new Integer(C_BPartner_ID));
    }

/**
 * Set Description
 *
 * @param Description
 */
    public void setDescription(String Description) {

        if ((Description != null) && (Description.length() > 255)) {

            log.warning("Length > 255 - truncated");
            Description	= Description.substring(0, 254);
        }

        set_Value("Description", Description);
    }

/**
 * Set IsSales
 *
 * @param IsSales
 */
    public void setIsSales(boolean IsSales) {
        set_Value("IsSales", new Boolean(IsSales));
    }

/** Set Price List.
 *
 * @param M_PriceList_ID
Unique identifier of a Price List */
    public void setM_PriceList_ID(int M_PriceList_ID) {

        if (M_PriceList_ID <= 0) {
            set_Value("M_PriceList_ID", null);
        } else {
            set_Value("M_PriceList_ID", new Integer(M_PriceList_ID));
        }
    }

/** Set Product.
 *
 * @param M_Product_ID
Product, Service, Item */
    public void setM_Product_ID(int M_Product_ID) {

        if (M_Product_ID <= 0) {
            set_Value("M_Product_ID", null);
        } else {
            set_Value("M_Product_ID", new Integer(M_Product_ID));
        }
    }

/**
 * Set M_Warehouse_ID
 *
 * @param M_Warehouse_ID
 */
    public void setM_Warehouse_ID(int M_Warehouse_ID) {

        if (M_Warehouse_ID <= 0) {
            set_Value("M_Warehouse_ID", null);
        } else {
            set_Value("M_Warehouse_ID", new Integer(M_Warehouse_ID));
        }
    }

/**
 * Set Process
 *
 * @param Process
 */
    public void setProcess(String Process) {

        if ((Process != null) && (Process.length() > 1)) {

            log.warning("Length > 1 - truncated");
            Process	= Process.substring(0, 0);
        }

        set_Value("Process", Process);
    }

/**
 * Set Processed
 *
 * @param Processed
 */
    public void setProcessed(boolean Processed) {
        set_Value("Processed", new Boolean(Processed));
    }

/** Set Quantity.
 *
 * @param QtyEntered
The Quantity Entered is based on the selected UoM */
    public void setQtyEntered(BigDecimal QtyEntered) {

        if (QtyEntered == null) {
            throw new IllegalArgumentException("QtyEntered is mandatory");
        }

        set_Value("QtyEntered", QtyEntered);
    }

/**
 * Set Sales_User_ID
 *
 * @param Sales_User_ID
 */
    public void setSales_User_ID(int Sales_User_ID) {
        set_Value("Sales_User_ID", new Integer(Sales_User_ID));
    }
}



/*
 * @(#)X_C_Action.java   02.jul 2007
 * 
 *  Fin del fichero X_C_Action.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
