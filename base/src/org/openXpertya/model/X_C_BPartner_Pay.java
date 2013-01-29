/*
 * @(#)X_C_BPartner_Pay.java   12.oct 2007  Versión 2.2
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

/** Generated Model for C_BPartner_Pay
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-07-03 10:38:47.343 */
public class X_C_BPartner_Pay extends PO {

/** AD_Table_ID=1000100 */
    public static final int	Table_ID	= 1000100;

/** TableName=C_BPartner_Pay */
    public static final String	Table_Name	= "C_BPartner_Pay";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000100, "C_BPartner_Pay");

/** Otros = 05 */
    public static final String	CONCEPT_Otros	= "05";

/** Mensualidad Ordinaria = 01 */
    public static final String	CONCEPT_MensualidadOrdinaria	= "01";

/** Extra de Verano = 02 */
    public static final String	CONCEPT_ExtraDeVerano	= "02";

/** Extra de Navidad = 03 */
    public static final String	CONCEPT_ExtraDeNavidad	= "03";

/** Bonifications = 04 */
    public static final String	CONCEPT_Bonifications	= "04";

    /** Descripción de Campo */
    public static final int	CONCEPT_AD_Reference_ID	= 1000057;

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_BPartner_Pay_ID
 * @param trxName
 */
    public X_C_BPartner_Pay(Properties ctx, int C_BPartner_Pay_ID, String trxName) {

        super(ctx, C_BPartner_Pay_ID, trxName);

/** if (C_BPartner_Pay_ID == 0)
{
setC_BPartner_Pay_ID (0);
setC_BPartner_Salary_ID (0);
setC_Calendar_ID (0);   // @#c_calendar_id@
setConcept (null);      // -1
setC_Period_ID (0);
setC_Year_ID (0);
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
    public X_C_BPartner_Pay(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_BPartner_Pay[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/**
 * Get C_BPartner_Pay_ID
 *
 * @return
 */
    public int getC_BPartner_Pay_ID() {

        Integer	ii	= (Integer) get_Value("C_BPartner_Pay_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get C_BPartner_Salary_ID
 *
 * @return
 */
    public int getC_BPartner_Salary_ID() {

        Integer	ii	= (Integer) get_Value("C_BPartner_Salary_ID");

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

/**
 * Get Concept
 *
 * @return
 */
    public String getConcept() {
        return (String) get_Value("Concept");
    }

/**
 * Get ExecutePaid
 *
 * @return
 */
    public String getExecutePaid() {
        return (String) get_Value("ExecutePaid");
    }

/**
 * Get GrossPay
 *
 * @return
 */
    public BigDecimal getGrossPay() {

        BigDecimal	bd	= (BigDecimal) get_Value("GrossPay");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/**
 * Get NetPay
 *
 * @return
 */
    public BigDecimal getNetPay() {

        BigDecimal	bd	= (BigDecimal) get_Value("NetPay");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/**
 * Get Paid
 *
 * @return
 */
    public boolean isPaid() {

        Object	oo	= get_Value("Paid");

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
 * Set C_BPartner_Pay_ID
 *
 * @param C_BPartner_Pay_ID
 */
    public void setC_BPartner_Pay_ID(int C_BPartner_Pay_ID) {
        set_ValueNoCheck("C_BPartner_Pay_ID", new Integer(C_BPartner_Pay_ID));
    }

/**
 * Set C_BPartner_Salary_ID
 *
 * @param C_BPartner_Salary_ID
 */
    public void setC_BPartner_Salary_ID(int C_BPartner_Salary_ID) {
        set_Value("C_BPartner_Salary_ID", new Integer(C_BPartner_Salary_ID));
    }

/** Set Calendar.
 *
 * @param C_Calendar_ID
Accounting Calendar Name */
    public void setC_Calendar_ID(int C_Calendar_ID) {
        set_Value("C_Calendar_ID", new Integer(C_Calendar_ID));
    }

/** Set Period.
 *
 * @param C_Period_ID
Period of the Calendar */
    public void setC_Period_ID(int C_Period_ID) {
        set_Value("C_Period_ID", new Integer(C_Period_ID));
    }

/** Set Year.
 *
 * @param C_Year_ID
Calendar Year */
    public void setC_Year_ID(int C_Year_ID) {
        set_Value("C_Year_ID", new Integer(C_Year_ID));
    }

/**
 * Set Concept
 *
 * @param Concept
 */
    public void setConcept(String Concept) {

        if (Concept.equals("01") || Concept.equals("02") || Concept.equals("03") || Concept.equals("05") || Concept.equals("04")) {
            ;
        } else {
            throw new IllegalArgumentException("Concept Invalid value - Reference_ID=1000057 - 01 - 02 - 03 - 05 - 04");
        }

        if (Concept == null) {
            throw new IllegalArgumentException("Concept is mandatory");
        }

        if (Concept.length() > 30) {

            log.warning("Length > 30 - truncated");
            Concept	= Concept.substring(0, 29);
        }

        set_Value("Concept", Concept);
    }

/**
 * Set ExecutePaid
 *
 * @param ExecutePaid
 */
    public void setExecutePaid(String ExecutePaid) {

        if ((ExecutePaid != null) && (ExecutePaid.length() > 60)) {

            log.warning("Length > 60 - truncated");
            ExecutePaid	= ExecutePaid.substring(0, 59);
        }

        set_Value("ExecutePaid", ExecutePaid);
    }

/**
 * Set GrossPay
 *
 * @param GrossPay
 */
    public void setGrossPay(BigDecimal GrossPay) {
        set_Value("GrossPay", GrossPay);
    }

/**
 * Set NetPay
 *
 * @param NetPay
 */
    public void setNetPay(BigDecimal NetPay) {
        set_Value("NetPay", NetPay);
    }

/**
 * Set Paid
 *
 * @param Paid
 */
    public void setPaid(boolean Paid) {
        set_Value("Paid", new Boolean(Paid));
    }
}



/*
 * @(#)X_C_BPartner_Pay.java   02.jul 2007
 * 
 *  Fin del fichero X_C_BPartner_Pay.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
