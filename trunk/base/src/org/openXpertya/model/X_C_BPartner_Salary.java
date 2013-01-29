/*
 * @(#)X_C_BPartner_Salary.java   12.oct 2007  Versión 2.2
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

/** Generated Model for C_BPartner_Salary
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-07-31 09:04:44.718 */
public class X_C_BPartner_Salary extends PO {

/** AD_Table_ID=1000099 */
    public static final int	Table_ID	= 1000099;

/** TableName=C_BPartner_Salary */
    public static final String	Table_Name	= "C_BPartner_Salary";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000099, "C_BPartner_Salary");

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_BPartner_Salary_ID
 * @param trxName
 */
    public X_C_BPartner_Salary(Properties ctx, int C_BPartner_Salary_ID, String trxName) {

        super(ctx, C_BPartner_Salary_ID, trxName);

/** if (C_BPartner_Salary_ID == 0)
{
setC_BPartner_ID (0);
setC_BPartner_Salary_ID (0);
setC_Calendar_ID (0);   // @#C_Calendar_ID@
setC_Currency_ID (0);   // #@AD_Currency_id#
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
    public X_C_BPartner_Salary(Properties ctx, ResultSet rs, String trxName) {
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
 * Set isBlocked
 *
 * @param isBlocked
 */
    public void setisBlocked(boolean isBlocked) {
        set_Value("isBlocked", new Boolean(isBlocked));
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("X_C_BPartner_Salary[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/** Get Business Partner .
 *
 * @return
Identifies a Business Partner */
    public int getC_BPartner_ID() {

        Integer	ii	= (Integer) get_Value("C_BPartner_ID");

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

/** Get Currency.
 *
 * @return
The Currency for this record */
    public int getC_Currency_ID() {

        Integer	ii	= (Integer) get_Value("C_Currency_ID");

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
 * Get Generate Pay
 *
 * @return
 */
    public String getGeneratePay() {
        return (String) get_Value("GeneratePay");
    }

/**
 * Get Generate Raise
 *
 * @return
 */
    public String getGenerateRaise() {
        return (String) get_Value("GenerateRaise");
    }

/**
 * Get NetSalary
 *
 * @return
 */
    public BigDecimal getNetSalary() {

        BigDecimal	bd	= (BigDecimal) get_Value("NetSalary");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/**
 * Get Raise
 *
 * @return
 */
    public int getRaise() {

        Integer	ii	= (Integer) get_Value("Raise");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get RaiseAppDate
 *
 * @return
 */
    public Timestamp getRaiseAppDate() {
        return (Timestamp) get_Value("RaiseAppDate");
    }

/** Get Salary.
 *
 * @return
Salary */
    public BigDecimal getSalary() {

        BigDecimal	bd	= (BigDecimal) get_Value("Salary");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/**
 * Get TIMESTAMP
 *
 * @return
 */
    public Timestamp getTIMESTAMP() {
        return (Timestamp) get_Value("TIMESTAMP");
    }

/**
 * Get isBlocked
 *
 * @return
 */
    public boolean isBlocked() {

        Object	oo	= get_Value("isBlocked");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

    //~--- set methods --------------------------------------------------------

/** Set Business Partner .
 *
 * @param C_BPartner_ID
Identifies a Business Partner */
    public void setC_BPartner_ID(int C_BPartner_ID) {
        set_Value("C_BPartner_ID", new Integer(C_BPartner_ID));
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

/** Set Currency.
 *
 * @param C_Currency_ID
The Currency for this record */
    public void setC_Currency_ID(int C_Currency_ID) {
        set_Value("C_Currency_ID", new Integer(C_Currency_ID));
    }

/** Set Year.
 *
 * @param C_Year_ID
Calendar Year */
    public void setC_Year_ID(int C_Year_ID) {
        set_Value("C_Year_ID", new Integer(C_Year_ID));
    }

/**
 * Set Generate Pay
 *
 * @param GeneratePay
 */
    public void setGeneratePay(String GeneratePay) {

        if ((GeneratePay != null) && (GeneratePay.length() > 30)) {

            log.warning("Length > 30 - truncated");
            GeneratePay	= GeneratePay.substring(0, 29);
        }

        set_Value("GeneratePay", GeneratePay);
    }

/**
 * Set Generate Raise
 *
 * @param GenerateRaise
 */
    public void setGenerateRaise(String GenerateRaise) {

        if ((GenerateRaise != null) && (GenerateRaise.length() > 30)) {

            log.warning("Length > 30 - truncated");
            GenerateRaise	= GenerateRaise.substring(0, 29);
        }

        set_Value("GenerateRaise", GenerateRaise);
    }

/**
 * Set NetSalary
 *
 * @param NetSalary
 */
    public void setNetSalary(BigDecimal NetSalary) {
        set_Value("NetSalary", NetSalary);
    }

/**
 * Set Raise
 *
 * @param Raise
 */
    public void setRaise(int Raise) {
        set_Value("Raise", new Integer(Raise));
    }

/**
 * Set RaiseAppDate
 *
 * @param RaiseAppDate
 */
    public void setRaiseAppDate(Timestamp RaiseAppDate) {
        set_Value("RaiseAppDate", RaiseAppDate);
    }

/** Set Salary.
 *
 * @param Salary
Salary */
    public void setSalary(BigDecimal Salary) {
        set_Value("Salary", Salary);
    }

/**
 * Set TIMESTAMP
 *
 * @param TIMESTAMP
 */
    public void setTIMESTAMP(Timestamp TIMESTAMP) {
        set_Value("TIMESTAMP", TIMESTAMP);
    }
}



/*
 * @(#)X_C_BPartner_Salary.java   02.jul 2007
 * 
 *  Fin del fichero X_C_BPartner_Salary.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
