/*
 * @(#)X_C_BPartner_Issues.java   12.oct 2007  Versión 2.2
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
import java.sql.Timestamp;

import java.util.Properties;

/** Generated Model for C_BPartner_Issues
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke (generated)
 *  @version FUNDESLE - 2006-06-27 16:56:33.593 */
public class X_C_BPartner_Issues extends PO {

/** AD_Table_ID=1000088 */
    public static final int	Table_ID	= 1000088;

/** TableName=C_BPartner_Issues */
    public static final String	Table_Name	= "C_BPartner_Issues";

    /** Descripción de Campo */
    public static final int	REGISTEREDBY_AD_Reference_ID	= 110;

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000088, "C_BPartner_Issues");

    /** Descripción de Campo */
    public static final int	C_SEVERITY_ID_AD_Reference_ID	= 1000051;

    /** Descripción de Campo */
    public static final int	C_ORDER_ID_AD_Reference_ID	= 290;

    /** Descripción de Campo */
    public static final int	C_ISSUE_RESOLUTION_ID_AD_Reference_ID	= 1000050;

    /** Descripción de Campo */
    public static final int	C_BPARTNER_ID_AD_Reference_ID	= 192;

    /** Descripción de Campo */
    public static final int	CLOSEDBY_AD_Reference_ID	= 286;

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(3);

/**
 * Standard Constructor
 *
 * @param ctx
 * @param C_BPartner_Issues_ID
 * @param trxName
 */
    public X_C_BPartner_Issues(Properties ctx, int C_BPartner_Issues_ID, String trxName) {

        super(ctx, C_BPartner_Issues_ID, trxName);

/** if (C_BPartner_Issues_ID == 0)
{
setC_BPartner_ID (0);
setC_BPartner_Issues_ID (0);
setC_Severity_ID (0);
setDescription (null);
setDetected_Date (new Timestamp(System.currentTimeMillis()));   // @#Date@

setIssueDate (new Timestamp(System.currentTimeMillis()));       // @#Date@
setRegisteredBy (0);
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
    public X_C_BPartner_Issues(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_BPartner_Issues[").append(getID()).append("]");

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
 * Get C_BPartner_Issues_ID
 *
 * @return
 */
    public int getC_BPartner_Issues_ID() {

        Integer	ii	= (Integer) get_Value("C_BPartner_Issues_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

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

/** Get Order.
 *
 * @return
Order */
    public int getC_Order_ID() {

        Integer	ii	= (Integer) get_Value("C_Order_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get C_Order_Supplier
 *
 * @return
 */
    public String getC_Order_Supplier() {
        return (String) get_Value("C_Order_Supplier");
    }

/**
 * Get C_Severity_ID
 *
 * @return
 */
    public int getC_Severity_ID() {

        Integer	ii	= (Integer) get_Value("C_Severity_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get ClosedBy
 *
 * @return
 */
    public int getClosedBy() {

        Integer	ii	= (Integer) get_Value("ClosedBy");

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
 * Get Detected_Date
 *
 * @return
 */
    public Timestamp getDetected_Date() {
        return (Timestamp) get_Value("Detected_Date");
    }

/**
 * Get HasAttachment
 *
 * @return
 */
    public String getHasAttachment() {
        return (String) get_Value("HasAttachment");
    }

/**
 * Get IssueDate
 *
 * @return
 */
    public Timestamp getIssueDate() {
        return (Timestamp) get_Value("IssueDate");
    }

/**
 * Get RegisteredBy
 *
 * @return
 */
    public int getRegisteredBy() {

        Integer	ii	= (Integer) get_Value("RegisteredBy");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get Solved_Date
 *
 * @return
 */
    public Timestamp getSolved_Date() {
        return (Timestamp) get_Value("Solved_Date");
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
 * Set C_BPartner_Issues_ID
 *
 * @param C_BPartner_Issues_ID
 */
    public void setC_BPartner_Issues_ID(int C_BPartner_Issues_ID) {
        set_ValueNoCheck("C_BPartner_Issues_ID", new Integer(C_BPartner_Issues_ID));
    }

/**
 * Set C_Issue_Resolution_ID
 *
 * @param C_Issue_Resolution_ID
 */
    public void setC_Issue_Resolution_ID(int C_Issue_Resolution_ID) {

        if (C_Issue_Resolution_ID <= 0) {
            set_Value("C_Issue_Resolution_ID", null);
        } else {
            set_Value("C_Issue_Resolution_ID", new Integer(C_Issue_Resolution_ID));
        }
    }

/** Set Order.
 *
 * @param C_Order_ID
Order */
    public void setC_Order_ID(int C_Order_ID) {

        if (C_Order_ID <= 0) {
            set_Value("C_Order_ID", null);
        } else {
            set_Value("C_Order_ID", new Integer(C_Order_ID));
        }
    }

/**
 * Set C_Order_Supplier
 *
 * @param C_Order_Supplier
 */
    public void setC_Order_Supplier(String C_Order_Supplier) {

        if ((C_Order_Supplier != null) && (C_Order_Supplier.length() > 40)) {

            log.warning("Length > 40 - truncated");
            C_Order_Supplier	= C_Order_Supplier.substring(0, 39);
        }

        set_Value("C_Order_Supplier", C_Order_Supplier);
    }

/**
 * Set C_Severity_ID
 *
 * @param C_Severity_ID
 */
    public void setC_Severity_ID(int C_Severity_ID) {
        set_Value("C_Severity_ID", new Integer(C_Severity_ID));
    }

/**
 * Set ClosedBy
 *
 * @param ClosedBy
 */
    public void setClosedBy(int ClosedBy) {
        set_Value("ClosedBy", new Integer(ClosedBy));
    }

/** Set Description.
 *
 * @param Description
Optional short description of the record */
    public void setDescription(String Description) {

        if (Description == null) {
            throw new IllegalArgumentException("Description is mandatory");
        }

        if (Description.length() > 255) {

            log.warning("Length > 255 - truncated");
            Description	= Description.substring(0, 254);
        }

        set_Value("Description", Description);
    }

/**
 * Set Detected_Date
 *
 * @param Detected_Date
 */
    public void setDetected_Date(Timestamp Detected_Date) {

        if (Detected_Date == null) {
            throw new IllegalArgumentException("Detected_Date is mandatory");
        }

        set_Value("Detected_Date", Detected_Date);
    }

/**
 * Set HasAttachment
 *
 * @param HasAttachment
 */
    public void setHasAttachment(String HasAttachment) {

        if ((HasAttachment != null) && (HasAttachment.length() > 1)) {

            log.warning("Length > 1 - truncated");
            HasAttachment	= HasAttachment.substring(0, 0);
        }

        set_Value("HasAttachment", HasAttachment);
    }

/**
 * Set IssueDate
 *
 * @param IssueDate
 */
    public void setIssueDate(Timestamp IssueDate) {

        if (IssueDate == null) {
            throw new IllegalArgumentException("IssueDate is mandatory");
        }

        set_Value("IssueDate", IssueDate);
    }

/**
 * Set RegisteredBy
 *
 * @param RegisteredBy
 */
    public void setRegisteredBy(int RegisteredBy) {
        set_Value("RegisteredBy", new Integer(RegisteredBy));
    }

/**
 * Set Solved_Date
 *
 * @param Solved_Date
 */
    public void setSolved_Date(Timestamp Solved_Date) {
        set_Value("Solved_Date", Solved_Date);
    }
}



/*
 * @(#)X_C_BPartner_Issues.java   02.jul 2007
 * 
 *  Fin del fichero X_C_BPartner_Issues.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
