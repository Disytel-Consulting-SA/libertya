/*
 * @(#)X_C_Action_Type.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.KeyNamePair;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.BigDecimal;

import java.sql.ResultSet;

import java.util.Properties;

/** Generated Model for C_Action_Type
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-05-30 09:52:57.578 */
public class X_C_Action_Type extends PO {

/** AD_Table_ID=1000756 */
    public static final int	Table_ID	= 1000756;

/** TableName=C_Action_Type */
    public static final String	Table_Name	= "C_Action_Type";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000756, "C_Action_Type");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_Action_Type_ID
 * @param trxName
 */
    public X_C_Action_Type(Properties ctx, int C_Action_Type_ID, String trxName) {

        super(ctx, C_Action_Type_ID, trxName);

/** if (C_Action_Type_ID == 0)
{
setC_Action_Type_ID (0);
setM_Product_ID (0);
setName (null);
setValue (null);
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
    public X_C_Action_Type(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_Action_Type[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

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
     * Descripción de Método
     *
     *
     * @return
     */
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(getID(), getName());
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

/** Get Name.
 *
 * @return
Alphanumeric identifier of the entity */
    public String getName() {
        return (String) get_Value("Name");
    }

/** Get Search Key.
 *
 * @return
Search key for the record in the format required - must be unique */
    public String getValue() {
        return (String) get_Value("Value");
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
 * Set C_Action_Type_ID
 *
 * @param C_Action_Type_ID
 */
    public void setC_Action_Type_ID(int C_Action_Type_ID) {
        set_ValueNoCheck("C_Action_Type_ID", new Integer(C_Action_Type_ID));
    }

/**
 * Set IsSales
 *
 * @param IsSales
 */
    public void setIsSales(boolean IsSales) {
        set_Value("IsSales", new Boolean(IsSales));
    }

/**
 * Set M_Product_ID
 *
 * @param M_Product_ID
 */
    public void setM_Product_ID(int M_Product_ID) {
        set_Value("M_Product_ID", new Integer(M_Product_ID));
    }

/** Set Name.
 *
 * @param Name
Alphanumeric identifier of the entity */
    public void setName(String Name) {

        if (Name == null) {
            throw new IllegalArgumentException("Name is mandatory");
        }

        if (Name.length() > 60) {

            log.warning("Length > 60 - truncated");
            Name	= Name.substring(0, 59);
        }

        set_Value("Name", Name);
    }

/** Set Search Key.
 *
 * @param Value
Search key for the record in the format required - must be unique */
    public void setValue(String Value) {

        if (Value == null) {
            throw new IllegalArgumentException("Value is mandatory");
        }

        if (Value.length() > 60) {

            log.warning("Length > 60 - truncated");
            Value	= Value.substring(0, 59);
        }

        set_Value("Value", Value);
    }
}



/*
 * @(#)X_C_Action_Type.java   02.jul 2007
 * 
 *  Fin del fichero X_C_Action_Type.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
