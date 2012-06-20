/*
 * @(#)X_C_Unavailability_Type.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.util.*;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.*;

import java.sql.*;

import java.util.*;

/** Generated Model for C_Unavailability_Type
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-07-24 11:44:31.906 */
public class X_C_Unavailability_Type extends PO {

/** AD_Table_ID=1000101 */
    public static final int	Table_ID	= 1000101;

/** TableName=C_Unavailability_Type */
    public static final String	Table_Name	= "C_Unavailability_Type";

    /** Descripción de Campo */
    public static final int	TABLE_ID_AD_Reference_ID	= 1000070;

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000101, "C_Unavailability_Type");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_Unavailability_Type_ID
 * @param trxName
 */
    public X_C_Unavailability_Type(Properties ctx, int C_Unavailability_Type_ID, String trxName) {

        super(ctx, C_Unavailability_Type_ID, trxName);

/** if (C_Unavailability_Type_ID == 0)
{
setC_Unavailability_Type_ID (0);
setName (null);
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
    public X_C_Unavailability_Type(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_Unavailability_Type[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/**
 * Get C_Unavailability_Type_ID
 *
 * @return
 */
    public int getC_Unavailability_Type_ID() {

        Integer	ii	= (Integer) get_Value("C_Unavailability_Type_ID");

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

/** Get Help.
 *
 * @return
Comment/Help */
    public String getHelp() {
        return (String) get_Value("Help");
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

/** Get Table ID.
 *
 * @return
Table for the Fields */
    public int getTable_ID() {

        Integer	ii	= (Integer) get_Value("Table_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

    //~--- set methods --------------------------------------------------------

/**
 * Set C_Unavailability_Type_ID
 *
 * @param C_Unavailability_Type_ID
 */
    public void setC_Unavailability_Type_ID(int C_Unavailability_Type_ID) {
        set_ValueNoCheck("C_Unavailability_Type_ID", new Integer(C_Unavailability_Type_ID));
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

/** Set Help.
 *
 * @param Help
Comment/Help */
    public void setHelp(String Help) {

        if ((Help != null) && (Help.length() > 255)) {

            log.warning("Length > 255 - truncated");
            Help	= Help.substring(0, 254);
        }

        set_Value("Help", Help);
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

/** Set Table ID.
 *
 * @param Table_ID
Table for the Fields */
    public void setTable_ID(int Table_ID) {

        if (Table_ID <= 0) {
            set_Value("Table_ID", null);
        } else {
            set_Value("Table_ID", new Integer(Table_ID));
        }
    }
}



/*
 * @(#)X_C_Unavailability_Type.java   02.jul 2007
 * 
 *  Fin del fichero X_C_Unavailability_Type.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
