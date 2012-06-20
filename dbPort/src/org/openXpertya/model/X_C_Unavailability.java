/*
 * @(#)X_C_Unavailability.java   12.oct 2007  Versión 2.2
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

/** Generated Model for C_Unavailability
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-08-09 10:47:14.937 */
public class X_C_Unavailability extends PO {

/** AD_Table_ID=1000103 */
    public static final int	Table_ID	= 1000103;

/** TableName=C_Unavailability */
    public static final String	Table_Name	= "C_Unavailability";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000103, "C_Unavailability");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

    /** Descripción de Campo */
    public static final int	AD_TABLE_ID_AD_Reference_ID	= 1000070;

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_Unavailability_ID
 * @param trxName
 */
    public X_C_Unavailability(Properties ctx, int C_Unavailability_ID, String trxName) {

        super(ctx, C_Unavailability_ID, trxName);

/** if (C_Unavailability_ID == 0)
{
setAD_Table_ID (0);
setC_Unavailability_ID (0);
setC_Unavailability_Type_ID (0);
setDateFrom (new Timestamp(System.currentTimeMillis()));
setDateTo (new Timestamp(System.currentTimeMillis()));
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
    public X_C_Unavailability(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_Unavailability[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/** Get Table.
 *
 * @return
Table for the Fields */
    public int getAD_Table_ID() {

        Integer	ii	= (Integer) get_Value("AD_Table_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Calendar.
 *
 * @return
Accounting Calendar Name */
    public int getC_Calendar_ID() {

        Integer	ii	= (Integer) get_Value("C_Calendar_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Period.
 *
 * @return
Period of the Calendar */
    public int getC_Period_ID() {

        Integer	ii	= (Integer) get_Value("C_Period_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get C_Unavailability_ID
 *
 * @return
 */
    public int getC_Unavailability_ID() {

        Integer	ii	= (Integer) get_Value("C_Unavailability_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

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

/** Get Year.
 *
 * @return
Calendar Year */
    public int getC_Year_ID() {

        Integer	ii	= (Integer) get_Value("C_Year_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get TIMESTAMP From.
 *
 * @return
Starting TIMESTAMP for a range */
    public Timestamp getDateFrom() {
        return (Timestamp) get_Value("DateFrom");
    }

/** Get TIMESTAMP To.
 *
 * @return
End TIMESTAMP of a TIMESTAMP range */
    public Timestamp getDateTo() {
        return (Timestamp) get_Value("DateTo");
    }

/** Get Description.
 *
 * @return
Optional short description of the record */
    public String getDescription() {
        return (String) get_Value("Description");
    }

/**
 * Get GoParent
 *
 * @return
 */
    public String getGoParent() {
        return (String) get_Value("GoParent");
    }

/** Get Record ID.
 *
 * @return
Direct internal record ID */
    public int getRecord_ID() {

        Integer	ii	= (Integer) get_Value("Record_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

    //~--- set methods --------------------------------------------------------

/** Set Table.
 *
 * @param AD_Table_ID
Table for the Fields */
    public void setAD_Table_ID(int AD_Table_ID) {
        set_Value("AD_Table_ID", new Integer(AD_Table_ID));
    }

/** Set Calendar.
 *
 * @param C_Calendar_ID
Accounting Calendar Name */
    public void setC_Calendar_ID(int C_Calendar_ID) {

        if (C_Calendar_ID <= 0) {
            set_Value("C_Calendar_ID", null);
        } else {
            set_Value("C_Calendar_ID", new Integer(C_Calendar_ID));
        }
    }

/** Set Period.
 *
 * @param C_Period_ID
Period of the Calendar */
    public void setC_Period_ID(int C_Period_ID) {

        if (C_Period_ID <= 0) {
            set_Value("C_Period_ID", null);
        } else {
            set_Value("C_Period_ID", new Integer(C_Period_ID));
        }
    }

/**
 * Set C_Unavailability_ID
 *
 * @param C_Unavailability_ID
 */
    public void setC_Unavailability_ID(int C_Unavailability_ID) {
        set_ValueNoCheck("C_Unavailability_ID", new Integer(C_Unavailability_ID));
    }

/**
 * Set C_Unavailability_Type_ID
 *
 * @param C_Unavailability_Type_ID
 */
    public void setC_Unavailability_Type_ID(int C_Unavailability_Type_ID) {
        set_Value("C_Unavailability_Type_ID", new Integer(C_Unavailability_Type_ID));
    }

/** Set Year.
 *
 * @param C_Year_ID
Calendar Year */
    public void setC_Year_ID(int C_Year_ID) {

        if (C_Year_ID <= 0) {
            set_Value("C_Year_ID", null);
        } else {
            set_Value("C_Year_ID", new Integer(C_Year_ID));
        }
    }

/** Set TIMESTAMP From.
 *
 * @param DateFrom
Starting TIMESTAMP for a range */
    public void setDateFrom(Timestamp DateFrom) {

        if (DateFrom == null) {
            throw new IllegalArgumentException("DateFrom is mandatory");
        }

        set_Value("DateFrom", DateFrom);
    }

/** Set TIMESTAMP To.
 *
 * @param DateTo
End TIMESTAMP of a TIMESTAMP range */
    public void setDateTo(Timestamp DateTo) {

        if (DateTo == null) {
            throw new IllegalArgumentException("DateTo is mandatory");
        }

        set_Value("DateTo", DateTo);
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

/**
 * Set GoParent
 *
 * @param GoParent
 */
    public void setGoParent(String GoParent) {

        if ((GoParent != null) && (GoParent.length() > 1)) {

            log.warning("Length > 1 - truncated");
            GoParent	= GoParent.substring(0, 0);
        }

        set_Value("GoParent", GoParent);
    }

/** Set Record ID.
 *
 * @param Record_ID
Direct internal record ID */
    public void setRecord_ID(int Record_ID) {

        if (Record_ID <= 0) {
            set_Value("Record_ID", null);
        } else {
            set_Value("Record_ID", new Integer(Record_ID));
        }
    }
}



/*
 * @(#)X_C_Unavailability.java   02.jul 2007
 * 
 *  Fin del fichero X_C_Unavailability.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
