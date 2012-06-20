/*
 * @(#)X_Xper_M_Product_Packing.java   12.oct 2007  Versión 2.2
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

/** Modelo Generado para Xper_M_Product_Packing
  *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Comunidad de Desarrollo OpenXpertya (generado)
        *Basado en Diverso Codigo Original Modificado, Revisado y Optimizado de:
        *Copyright ï¿½ 1999-2001 Jorg Janke, Copyright ï¿½ ComPiere, Inc. *  @version Version 1.2 19:41:04.449 */
public class X_Xper_M_Product_Packing extends PO {

/** AD_Table_ID=1000016 */
    public static final int	Table_ID	= 1000016;

/** TableName=Xper_M_Product_Packing */
    public static final String	Table_Name	= "Xper_M_Product_Packing";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000016, "Xper_M_Product_Packing");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Constructor Standard
 *
 * @param ctx
 * @param Xper_M_Product_Packing_ID
 * @param trxName
 */
    public X_Xper_M_Product_Packing(Properties ctx, int Xper_M_Product_Packing_ID, String trxName) {

        super(ctx, Xper_M_Product_Packing_ID, trxName);

/** if (Xper_M_Product_Packing_ID == 0)
{
setC_UOMPACKING_ID (0);
setC_UOM_ID (0);
setIsGreenPointManaged (false);
setLine (0);
setM_ProductPacking_ID (0);
setM_Product_ID (0);
setPackingQty (0);
setXper_M_Product_Packing_ID (0);
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
    public X_Xper_M_Product_Packing(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_Xper_M_Product_Packing[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/**
 * Get C_UOMPACKING_ID
 *
 * @return
 */
    public int getC_UOMPACKING_ID() {

        Integer	ii	= (Integer) get_Value("C_UOMPACKING_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get C_UOM_ID
 *
 * @return
 */
    public int getC_UOM_ID() {

        Integer	ii	= (Integer) get_Value("C_UOM_ID");

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

/**
 * Get Line
 *
 * @return
 */
    public int getLine() {

        Integer	ii	= (Integer) get_Value("Line");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get M_ProductPacking_ID
 *
 * @return
 */
    public int getM_ProductPacking_ID() {

        Integer	ii	= (Integer) get_Value("M_ProductPacking_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get M_Product_ID
 *
 * @return
 */
    public int getM_Product_ID() {

        Integer	ii	= (Integer) get_Value("M_Product_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get PackingQty
 *
 * @return
 */
    public int getPackingQty() {

        Integer	ii	= (Integer) get_Value("PackingQty");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Packing_Cost
 *
 * @return
 */
    public BigDecimal getPacking_Cost() {

        BigDecimal	bd	= (BigDecimal) get_Value("Packing_Cost");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/**
 * Get Xper_M_Product_Packing_ID
 *
 * @return
 */
    public int getXper_M_Product_Packing_ID() {

        Integer	ii	= (Integer) get_Value("Xper_M_Product_Packing_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get IsGreenPointManaged
 *
 * @return
 */
    public boolean isGreenPointManaged() {

        Object	oo	= get_Value("IsGreenPointManaged");

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
 * Set C_UOMPACKING_ID
 *
 * @param C_UOMPACKING_ID
 */
    public void setC_UOMPACKING_ID(int C_UOMPACKING_ID) {
        set_Value("C_UOMPACKING_ID", new Integer(C_UOMPACKING_ID));
    }

/**
 * Set C_UOM_ID
 *
 * @param C_UOM_ID
 */
    public void setC_UOM_ID(int C_UOM_ID) {
        set_Value("C_UOM_ID", new Integer(C_UOM_ID));
    }

/**
 * Set Description
 *
 * @param Description
 */
    public void setDescription(String Description) {

        if ((Description != null) && (Description.length() > 510)) {

            log.warning("Length > 510 - truncated");
            Description	= Description.substring(0, 509);
        }

        set_Value("Description", Description);
    }

/**
 * Set IsGreenPointManaged
 *
 * @param IsGreenPointManaged
 */
    public void setIsGreenPointManaged(boolean IsGreenPointManaged) {
        set_Value("IsGreenPointManaged", new Boolean(IsGreenPointManaged));
    }

/**
 * Set Line
 *
 * @param Line
 */
    public void setLine(int Line) {
        set_Value("Line", new Integer(Line));
    }

/**
 * Set M_ProductPacking_ID
 *
 * @param M_ProductPacking_ID
 */
    public void setM_ProductPacking_ID(int M_ProductPacking_ID) {
        set_Value("M_ProductPacking_ID", new Integer(M_ProductPacking_ID));
    }

/**
 * Set M_Product_ID
 *
 * @param M_Product_ID
 */
    public void setM_Product_ID(int M_Product_ID) {
        set_ValueNoCheck("M_Product_ID", new Integer(M_Product_ID));
    }

/**
 * Set PackingQty
 *
 * @param PackingQty
 */
    public void setPackingQty(int PackingQty) {
        set_Value("PackingQty", new Integer(PackingQty));
    }

/**
 * Set Packing_Cost
 *
 * @param Packing_Cost
 */
    public void setPacking_Cost(BigDecimal Packing_Cost) {
        set_Value("Packing_Cost", Packing_Cost);
    }

/**
 * Set Xper_M_Product_Packing_ID
 *
 * @param Xper_M_Product_Packing_ID
 */
    public void setXper_M_Product_Packing_ID(int Xper_M_Product_Packing_ID) {
        set_Value("Xper_M_Product_Packing_ID", new Integer(Xper_M_Product_Packing_ID));
    }
}



/*
 * @(#)X_Xper_M_Product_Packing.java   02.jul 2007
 * 
 *  Fin del fichero X_Xper_M_Product_Packing.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
