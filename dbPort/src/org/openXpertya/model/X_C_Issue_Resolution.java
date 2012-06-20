/*
 * @(#)X_C_Issue_Resolution.java   12.oct 2007  Versión 2.2
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

/** Generated Model for C_Issue_Resolution
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-06-27 11:37:05.968 */
public class X_C_Issue_Resolution extends PO {

/** AD_Table_ID=1000087 */
    public static final int	Table_ID	= 1000087;

/** TableName=C_Issue_Resolution */
    public static final String	Table_Name	= "C_Issue_Resolution";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000087, "C_Issue_Resolution");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

    /** Descripción de Campo */
    public static final int	VALUE_AD_Reference_ID	= 1000043;

/** 9 = 09 */
    public static final String	VALUE_9	= "09";

/** 8 = 08 */
    public static final String	VALUE_8	= "08";

/** 7 = 07 */
    public static final String	VALUE_7	= "07";

/** 6 = 06 */
    public static final String	VALUE_6	= "06";

/** 5 = 05 */
    public static final String	VALUE_5	= "05";

/** 4 = 04 */
    public static final String	VALUE_4	= "04";

/** 3 = 03 */
    public static final String	VALUE_3	= "03";

/** 2 = 02 */
    public static final String	VALUE_2	= "02";

/** 10 = 10 */
    public static final String	VALUE_10	= "10";

/** 1 = 01 */
    public static final String	VALUE_1	= "01";

/** 0 = 00 */
    public static final String	VALUE_0	= "00";

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_Issue_Resolution_ID
 * @param trxName
 */
    public X_C_Issue_Resolution(Properties ctx, int C_Issue_Resolution_ID, String trxName) {

        super(ctx, C_Issue_Resolution_ID, trxName);

/** if (C_Issue_Resolution_ID == 0)
{
setC_Issue_Resolution_ID (0);
setName (null);
setNamePrint (null);
setValue (null);        // 1
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
    public X_C_Issue_Resolution(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_Issue_Resolution[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/**
 * Get C_Issue_Resolution_ID
 *
 * @return
 */
    public int getC_Issue_Resolution_ID() {

        Integer	ii	= (Integer) get_Value("C_Issue_Resolution_ID");

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

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public KeyNamePair getKeyNamePair() {
        return new KeyNamePair(getID(), getName());
    }

/** Get Name.
 *
 * @return
Alphanumeric identifier of the entity */
    public String getName() {
        return (String) get_Value("Name");
    }

/**
 * Get NamePrint
 *
 * @return
 */
    public String getNamePrint() {
        return (String) get_Value("NamePrint");
    }

/**
 * Get NumericValue
 *
 * @return
 */
    public String getValue() {
        return (String) get_Value("Value");
    }

    //~--- set methods --------------------------------------------------------

/**
 * Set C_Issue_Resolution_ID
 *
 * @param C_Issue_Resolution_ID
 */
    public void setC_Issue_Resolution_ID(int C_Issue_Resolution_ID) {
        set_ValueNoCheck("C_Issue_Resolution_ID", new Integer(C_Issue_Resolution_ID));
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

/**
 * Set NamePrint
 *
 * @param NamePrint
 */
    public void setNamePrint(String NamePrint) {

        if (NamePrint == null) {
            throw new IllegalArgumentException("NamePrint is mandatory");
        }

        if (NamePrint.length() > 60) {

            log.warning("Length > 60 - truncated");
            NamePrint	= NamePrint.substring(0, 59);
        }

        set_Value("NamePrint", NamePrint);
    }

/**
 * Set NumericValue
 *
 * @param Value
 */
    public void setValue(String Value) {

        if (Value.equals("10") || Value.equals("01") || Value.equals("02") || Value.equals("03") || Value.equals("04") || Value.equals("05") || Value.equals("06") || Value.equals("07") || Value.equals("08") || Value.equals("09") || Value.equals("00")) {
            ;
        } else {
            throw new IllegalArgumentException("Value Invalid value - Reference_ID=1000043 - 10 - 01 - 02 - 03 - 04 - 05 - 06 - 07 - 08 - 09 - 00");
        }

        if (Value == null) {
            throw new IllegalArgumentException("Value is mandatory");
        }

        if (Value.length() > 2) {

            log.warning("Length > 2 - truncated");
            Value	= Value.substring(0, 1);
        }

        set_Value("Value", Value);
    }
}



/*
 * @(#)X_C_Issue_Resolution.java   02.jul 2007
 * 
 *  Fin del fichero X_C_Issue_Resolution.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
