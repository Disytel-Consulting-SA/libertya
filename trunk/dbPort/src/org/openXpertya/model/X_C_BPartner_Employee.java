/*
 * @(#)X_C_BPartner_Employee.java   12.oct 2007  Versión 2.2
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

/** Generated Model for C_BPartner_Employee
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-07-27 11:11:53.093 */
public class X_C_BPartner_Employee extends PO {

/** AD_Table_ID=1000118 */
    public static final int	Table_ID	= 1000118;

/** TableName=C_BPartner_Employee */
    public static final String	Table_Name	= "C_BPartner_Employee";

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000118, "C_BPartner_Employee");

/** Single, widow, divorced... in charge of younger or elder = 10000019 */
    public static final String	FAMILYSTATUS_SingleWidowDivorcedInChargeOfYoungerOrElder	= "10000019";

/** Married (couple with income < 1500ï¿½) = 10000020 */
    public static final String	FAMILYSTATUS_MarriedCoupleWithIncomeLe1500	= "10000020";

/** Any other situation = 10000021 */
    public static final String	FAMILYSTATUS_AnyOtherSituation	= "10000021";

    /** Descripción de Campo */
    public static final int	FAMILYSTATUS_AD_Reference_ID	= 1000075;

/** Substitution contract = 10000017 */
    public static final String	CONTRACTTYPE_ID_SubstitutionContract	= "10000017";

/** Service Contract = 10000016 */
    public static final String	CONTRACTTYPE_ID_ServiceContract	= "10000016";

/** Practice = 10000018 */
    public static final String	CONTRACTTYPE_ID_Practice	= "10000018";

/** Part time long term = 10000015 */
    public static final String	CONTRACTTYPE_ID_PartTimeLongTerm	= "10000015";

/** Full time long term = 10000014 */
    public static final String	CONTRACTTYPE_ID_FullTimeLongTerm	= "10000014";

    /** Descripción de Campo */
    public static final int	CONTRACTTYPE_ID_AD_Reference_ID	= 1000074;

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(1);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_BPartner_Employee_ID
 * @param trxName
 */
    public X_C_BPartner_Employee(Properties ctx, int C_BPartner_Employee_ID, String trxName) {

        super(ctx, C_BPartner_Employee_ID, trxName);

/** if (C_BPartner_Employee_ID == 0)
{
setc_bpartner_employee_id (0);
setC_BPartner_ID (0);
setcontracttype_id (null);
setIsEmployee (false);
setIsSalesRep (false);
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
    public X_C_BPartner_Employee(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }

/**
 * Get c_bpartner_employee_id
 *
 * @return
 */
    public int getc_bpartner_employee_id() {

        Integer	ii	= (Integer) get_Value("c_bpartner_employee_id");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get contracttype_id.
 *
 * @return
Defines the kind of contract the employee has */
    public String getcontracttype_id() {
        return (String) get_Value("contracttype_id");
    }

/**
 * Get dateofcontract
 *
 * @return
 */
    public Timestamp getdateofcontract() {
        return (Timestamp) get_Value("dateofcontract");
    }

/**
 * Get elder
 *
 * @return
 */
    public String getelder() {
        return (String) get_Value("elder");
    }

/**
 * Get familystatus
 *
 * @return
 */
    public String getfamilystatus() {
        return (String) get_Value("familystatus");
    }

/**
 * Get impaired
 *
 * @return
 */
    public String getimpaired() {
        return (String) get_Value("impaired");
    }

/**
 * Get offspring
 *
 * @return
 */
    public String getoffspring() {
        return (String) get_Value("offspring");
    }

/**
 * Get taxgroup
 *
 * @return
 */
    public String gettaxgroup() {
        return (String) get_Value("taxgroup");
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
 * Set c_bpartner_employee_id
 *
 * @param c_bpartner_employee_id
 */
    public void setc_bpartner_employee_id(int c_bpartner_employee_id) {
        set_ValueNoCheck("c_bpartner_employee_id", new Integer(c_bpartner_employee_id));
    }

/** Set contracttype_id.
 *
 * @param contracttype_id
Defines the kind of contract the employee has */
    public void setcontracttype_id(String contracttype_id) {

        if (contracttype_id.equals("10000014") || contracttype_id.equals("10000015") || contracttype_id.equals("10000016") || contracttype_id.equals("10000017") || contracttype_id.equals("10000018")) {
            ;
        } else {
            throw new IllegalArgumentException("contracttype_id Invalid value - Reference_ID=1000074 - 10000014 - 10000015 - 10000016 - 10000017 - 10000018");
        }

        if (contracttype_id == null) {
            throw new IllegalArgumentException("contracttype_id is mandatory");
        }

        if (contracttype_id.length() > 1) {

            log.warning("Length > 1 - truncated");
            contracttype_id	= contracttype_id.substring(0, 0);
        }

        set_Value("contracttype_id", contracttype_id);
    }

/**
 * Set dateofcontract
 *
 * @param dateofcontract
 */
    public void setdateofcontract(Timestamp dateofcontract) {
        set_Value("dateofcontract", dateofcontract);
    }

/**
 * Set elder
 *
 * @param elder
 */
    public void setelder(String elder) {

        if ((elder != null) && (elder.length() > 30)) {

            log.warning("Length > 30 - truncated");
            elder	= elder.substring(0, 29);
        }

        set_Value("elder", elder);
    }

/**
 * Set familystatus
 *
 * @param familystatus
 */
    public void setfamilystatus(String familystatus) {

        if ((familystatus == null) || familystatus.equals("10000019") || familystatus.equals("10000020") || familystatus.equals("10000021")) {
            ;
        } else {
            throw new IllegalArgumentException("familystatus Invalid value - Reference_ID=1000075 - 10000019 - 10000020 - 10000021");
        }

        if ((familystatus != null) && (familystatus.length() > 1)) {

            log.warning("Length > 1 - truncated");
            familystatus	= familystatus.substring(0, 0);
        }

        set_Value("familystatus", familystatus);
    }

/**
 * Set impaired
 *
 * @param impaired
 */
    public void setimpaired(String impaired) {

        if ((impaired != null) && (impaired.length() > 40)) {

            log.warning("Length > 40 - truncated");
            impaired	= impaired.substring(0, 39);
        }

        set_Value("impaired", impaired);
    }

/**
 * Set offspring
 *
 * @param offspring
 */
    public void setoffspring(String offspring) {

        if ((offspring != null) && (offspring.length() > 30)) {

            log.warning("Length > 30 - truncated");
            offspring	= offspring.substring(0, 29);
        }

        set_Value("offspring", offspring);
    }

/**
 * Set taxgroup
 *
 * @param taxgroup
 */
    public void settaxgroup(String taxgroup) {

        if ((taxgroup != null) && (taxgroup.length() > 30)) {

            log.warning("Length > 30 - truncated");
            taxgroup	= taxgroup.substring(0, 29);
        }

        set_Value("taxgroup", taxgroup);
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("X_C_BPartner_Employee[").append(getID()).append("]");

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

/** Get Employee.
 *
 * @return
Indicates if this Business Partner is an employee */
    public boolean isEmployee() {

        Object	oo	= get_Value("IsEmployee");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

/** Get Sales Representative.
 *
 * @return
Indicates if the business partner is a sales representative or company agent */
    public boolean isSalesRep() {

        Object	oo	= get_Value("IsSalesRep");

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

/** Set Employee.
 *
 * @param IsEmployee
Indicates if this Business Partner is an employee */
    public void setIsEmployee(boolean IsEmployee) {
        set_Value("IsEmployee", new Boolean(IsEmployee));
    }

/** Set Sales Representative.
 *
 * @param IsSalesRep
Indicates if the business partner is a sales representative or company agent */
    public void setIsSalesRep(boolean IsSalesRep) {
        set_Value("IsSalesRep", new Boolean(IsSalesRep));
    }
}



/*
 * @(#)X_C_BPartner_Employee.java   02.jul 2007
 * 
 *  Fin del fichero X_C_BPartner_Employee.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
