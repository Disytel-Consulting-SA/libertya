/*
 * @(#)X_C_Dataware.java   12.oct 2007  Versión 2.2
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

/** Generated Model for C_Dataware
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-05-23 10:07:51.796 */
public class X_C_Dataware extends PO {

/** AD_Table_ID=1000085 */
    public static final int	Table_ID	= 1000085;

/** TableName=C_Dataware */
    public static final String	Table_Name	= "C_Dataware";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000085, "C_Dataware");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_Dataware_ID
 * @param trxName
 */
    public X_C_Dataware(Properties ctx, int C_Dataware_ID, String trxName) {

        super(ctx, C_Dataware_ID, trxName);

/** if (C_Dataware_ID == 0)
{
setC_Dataware_ID (0);
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
    public X_C_Dataware(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_Dataware[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/**
 * Get C_Dataware_ID
 *
 * @return
 */
    public int getC_Dataware_ID() {

        Integer	ii	= (Integer) get_Value("C_Dataware_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get DatwareDescription
 *
 * @return
 */
    public String getDatwareDescription() {
        return (String) get_Value("DatwareDescription");
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

/** Get Search Key.
 *
 * @return
Search key for the record in the format required - must be unique */
    public String getValue() {
        return (String) get_Value("Value");
    }

    //~--- set methods --------------------------------------------------------

/**
 * Set C_Dataware_ID
 *
 * @param C_Dataware_ID
 */
    public void setC_Dataware_ID(int C_Dataware_ID) {
        set_ValueNoCheck("C_Dataware_ID", new Integer(C_Dataware_ID));
    }

/**
 * Set DatwareDescription
 *
 * @param DatwareDescription
 */
    public void setDatwareDescription(String DatwareDescription) {

        if ((DatwareDescription != null) && (DatwareDescription.length() > 125)) {

            log.warning("Length > 125 - truncated");
            DatwareDescription	= DatwareDescription.substring(0, 124);
        }

        set_Value("DatwareDescription", DatwareDescription);
    }

/** Set Name.
 *
 * @param Name
Alphanumeric identifier of the entity */
    public void setName(String Name) {

        if ((Name != null) && (Name.length() > 128)) {

            log.warning("Length > 128 - truncated");
            Name	= Name.substring(0, 127);
        }

        set_Value("Name", Name);
    }

/** Set Search Key.
 *
 * @param Value
Search key for the record in the format required - must be unique */
    public void setValue(String Value) {

        if ((Value != null) && (Value.length() > 32)) {

            log.warning("Length > 32 - truncated");
            Value	= Value.substring(0, 31);
        }

        set_Value("Value", Value);
    }
}



/*
 * @(#)X_C_Dataware.java   02.jul 2007
 * 
 *  Fin del fichero X_C_Dataware.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
