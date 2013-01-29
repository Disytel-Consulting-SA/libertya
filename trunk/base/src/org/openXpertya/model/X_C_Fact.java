/*
 * @(#)X_C_Fact.java   12.oct 2007  Versión 2.2
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

/** Modelo Generado para C_Fact
  *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Comunidad de Desarrollo OpenXpertya (generado)
        *Basado en Diverso Codigo Original Modificado, Revisado y Optimizado de:
        *Copyright ï¿½ 1999-2001 Jorg Janke, Copyright ï¿½ ComPiere, Inc. *  @version Version 1.2 19:41:03.82 */
public class X_C_Fact extends PO {

/** AD_Table_ID=1000001 */
    public static final int	Table_ID	= 1000001;

/** TableName=C_Fact */
    public static final String	Table_Name	= "C_Fact";

    /** Descripción de Campo */
    public static final int	REF_ORDER_ID_AD_Reference_ID	= 290;

    /** Descripción de Campo */
    protected static KeyNamePair	Model	= new KeyNamePair(1000001, "C_Fact");

/** Waiting Payment = WP */
    public static final String	DOCSTATUS_WaitingPayment	= "WP";

/** Waiting Confirmation = WC */
    public static final String	DOCSTATUS_WaitingConfirmation	= "WC";

/** Voided = VO */
    public static final String	DOCSTATUS_Voided	= "VO";

/** Unknown = ?? */
    public static final String	DOCSTATUS_Unknown	= "??";

/** Reversed = RE */
    public static final String	DOCSTATUS_Reversed	= "RE";

/** Not Approved = NA */
    public static final String	DOCSTATUS_NotApproved	= "NA";

/** Invalid = IN */
    public static final String	DOCSTATUS_Invalid	= "IN";

/** In Progress = IP */
    public static final String	DOCSTATUS_InProgress	= "IP";

/** Drafted = DR */
    public static final String	DOCSTATUS_Drafted	= "DR";

/** Completed = CO */
    public static final String	DOCSTATUS_Completed	= "CO";

/** Closed = CL */
    public static final String	DOCSTATUS_Closed	= "CL";

/** Approved = AP */
    public static final String	DOCSTATUS_Approved	= "AP";

    /** Descripción de Campo */
    public static final int	DOCSTATUS_AD_Reference_ID	= 131;

    /** Descripción de Campo */
    protected static BigDecimal	AccessLevel	= new BigDecimal(7);

    /** Descripción de Campo */
    public static final int	USER1_ID_AD_Reference_ID	= 110;

/**
 * Constructor Standard
 *
 * @param ctx
 * @param C_Fact_ID
 * @param trxName
 */
    public X_C_Fact(Properties ctx, int C_Fact_ID, String trxName) {

        super(ctx, C_Fact_ID, trxName);

/** if (C_Fact_ID == 0)
{
setC_BPartner_ID (0);
setC_Fact_ID (0);
setC_Quarter_ID (0);
setDocAction (null);
setDocStatus (null);    // DR
setGrandTotal (Env.ZERO);
setM_PriceList_ID (0);
setM_Product_Category_ID (0);
setM_Product_Gamas_ID (0);
setM_Warehouse_ID (0);  // @M_Warehouse_ID@
setPriceEntered (Env.ZERO);
setPriceList (Env.ZERO);
setProcessed (false);   // N
setQtyEntered (Env.ZERO);       // 1
setTotalLines (Env.ZERO);
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
    public X_C_Fact(Properties ctx, ResultSet rs, String trxName) {
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

        StringBuffer	sb	= new StringBuffer("X_C_Fact[").append(getID()).append("]");

        return sb.toString();
    }

    //~--- get methods --------------------------------------------------------

/** Get AD_OrgTrx_ID.
 *
 * @return
AD_OrgTrx_ID */
    public int getAD_OrgTrx_ID() {

        Integer	ii	= (Integer) get_Value("AD_OrgTrx_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get AD_User_ID.
 *
 * @return
AD_User_ID */
    public int getAD_User_ID() {

        Integer	ii	= (Integer) get_Value("AD_User_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get C_BPartner_ID.
 *
 * @return
C_BPartner_ID */
    public int getC_BPartner_ID() {

        Integer	ii	= (Integer) get_Value("C_BPartner_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get C_Fact_ID.
 *
 * @return
C_Fact_ID */
    public int getC_Fact_ID() {

        Integer	ii	= (Integer) get_Value("C_Fact_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get C_Quarter_ID.
 *
 * @return
C_Quarter_ID */
    public int getC_Quarter_ID() {

        Integer	ii	= (Integer) get_Value("C_Quarter_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/**
 * Get C_Vendor_ID
 *
 * @return
 */
    public int getC_Vendor_ID() {

        Integer	ii	= (Integer) get_Value("C_Vendor_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Copy From.
 *
 * @return
Copy From */
    public String getCopyFrom() {
        return (String) get_Value("CopyFrom");
    }

/** Get Description.
 *
 * @return
Description */
    public String getDescription() {
        return (String) get_Value("Description");
    }

/** Get DocAction.
 *
 * @return
DocAction */
    public String getDocAction() {
        return (String) get_Value("DocAction");
    }

/** Get DocStatus.
 *
 * @return
DocStatus */
    public String getDocStatus() {
        return (String) get_Value("DocStatus");
    }

/** Get Grand Total.
 *
 * @return
GrandTotal */
    public BigDecimal getGrandTotal() {

        BigDecimal	bd	= (BigDecimal) get_Value("GrandTotal");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get M_PriceList_ID.
 *
 * @return
M_PriceList_ID */
    public int getM_PriceList_ID() {

        Integer	ii	= (Integer) get_Value("M_PriceList_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get M_Product_Category_ID.
 *
 * @return
M_Product_Category_ID */
    public int getM_Product_Category_ID() {

        Integer	ii	= (Integer) get_Value("M_Product_Category_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get M_Product_Gamas_ID.
 *
 * @return
M_Product_Gamas_ID */
    public int getM_Product_Gamas_ID() {

        Integer	ii	= (Integer) get_Value("M_Product_Gamas_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get M_Product_ID.
 *
 * @return
M_Product_ID */
    public int getM_Product_ID() {

        Integer	ii	= (Integer) get_Value("M_Product_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get M_Warehouse_ID.
 *
 * @return
M_Warehouse_ID */
    public int getM_Warehouse_ID() {

        Integer	ii	= (Integer) get_Value("M_Warehouse_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get POReference.
 *
 * @return
POReference */
    public String getPOReference() {
        return (String) get_Value("POReference");
    }

/** Get Param1.
 *
 * @return
Param1 */
    public BigDecimal getParam1() {

        BigDecimal	bd	= (BigDecimal) get_Value("Param1");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get Param2.
 *
 * @return
Param2 */
    public BigDecimal getParam2() {

        BigDecimal	bd	= (BigDecimal) get_Value("Param2");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get Param3.
 *
 * @return
Param3 */
    public int getParam3() {

        Integer	ii	= (Integer) get_Value("Param3");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Param4.
 *
 * @return
Param4 */
    public BigDecimal getParam4() {

        BigDecimal	bd	= (BigDecimal) get_Value("Param4");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/**
 * Get PriceEntered
 *
 * @return
 */
    public BigDecimal getPriceEntered() {

        BigDecimal	bd	= (BigDecimal) get_Value("PriceEntered");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get PriceList.
 *
 * @return
PriceList */
    public BigDecimal getPriceList() {

        BigDecimal	bd	= (BigDecimal) get_Value("PriceList");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get QtyAvailable.
 *
 * @return
QtyAvailable */
    public int getQtyAvailable() {

        Integer	ii	= (Integer) get_Value("QtyAvailable");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get QtyEntered.
 *
 * @return
QtyEntered */
    public BigDecimal getQtyEntered() {

        BigDecimal	bd	= (BigDecimal) get_Value("QtyEntered");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get QtyOnHand.
 *
 * @return
QtyOnHand */
    public int getQtyOnHand() {

        Integer	ii	= (Integer) get_Value("QtyOnHand");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Ref_Order_ID.
 *
 * @return
Ref_Order_ID */
    public int getRef_Order_ID() {

        Integer	ii	= (Integer) get_Value("Ref_Order_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get TotalLines.
 *
 * @return
TotalLines */
    public BigDecimal getTotalLines() {

        BigDecimal	bd	= (BigDecimal) get_Value("TotalLines");

        if (bd == null) {
            return Env.ZERO;
        }

        return bd;
    }

/** Get User1_ID.
 *
 * @return
User1_ID */
    public int getUser1_ID() {

        Integer	ii	= (Integer) get_Value("User1_ID");

        if (ii == null) {
            return 0;
        }

        return ii.intValue();
    }

/** Get Processed.
 *
 * @return
Processed */
    public boolean isProcessed() {

        Object	oo	= get_Value("Processed");

        if (oo != null) {

            if (oo instanceof Boolean) {
                return ((Boolean) oo).booleanValue();
            }

            return "Y".equals(oo);
        }

        return false;
    }

    //~--- set methods --------------------------------------------------------

/** Set AD_OrgTrx_ID.
 *
 * @param AD_OrgTrx_ID
AD_OrgTrx_ID */
    public void setAD_OrgTrx_ID(int AD_OrgTrx_ID) {

        if (AD_OrgTrx_ID <= 0) {
            set_Value("AD_OrgTrx_ID", null);
        } else {
            set_Value("AD_OrgTrx_ID", new Integer(AD_OrgTrx_ID));
        }
    }

/** Set AD_User_ID.
 *
 * @param AD_User_ID
AD_User_ID */
    public void setAD_User_ID(int AD_User_ID) {

        if (AD_User_ID <= 0) {
            set_Value("AD_User_ID", null);
        } else {
            set_Value("AD_User_ID", new Integer(AD_User_ID));
        }
    }

/** Set C_BPartner_ID.
 *
 * @param C_BPartner_ID
C_BPartner_ID */
    public void setC_BPartner_ID(int C_BPartner_ID) {
        set_Value("C_BPartner_ID", new Integer(C_BPartner_ID));
    }

/** Set C_Fact_ID.
 *
 * @param C_Fact_ID
C_Fact_ID */
    public void setC_Fact_ID(int C_Fact_ID) {
        set_Value("C_Fact_ID", new Integer(C_Fact_ID));
    }

/** Set C_Quarter_ID.
 *
 * @param C_Quarter_ID
C_Quarter_ID */
    public void setC_Quarter_ID(int C_Quarter_ID) {
        set_Value("C_Quarter_ID", new Integer(C_Quarter_ID));
    }

/**
 * Set C_Vendor_ID
 *
 * @param C_Vendor_ID
 */
    public void setC_Vendor_ID(int C_Vendor_ID) {

        if (C_Vendor_ID <= 0) {
            set_Value("C_Vendor_ID", null);
        } else {
            set_Value("C_Vendor_ID", new Integer(C_Vendor_ID));
        }
    }

/** Set Copy From.
 *
 * @param CopyFrom
Copy From */
    public void setCopyFrom(String CopyFrom) {

        if ((CopyFrom != null) && (CopyFrom.length() > 1)) {

            log.warning("Length > 1 - truncated");
            CopyFrom	= CopyFrom.substring(0, 0);
        }

        set_Value("CopyFrom", CopyFrom);
    }

/** Set Description.
 *
 * @param Description
Description */
    public void setDescription(String Description) {

        if ((Description != null) && (Description.length() > 1020)) {

            log.warning("Length > 1020 - truncated");
            Description	= Description.substring(0, 1019);
        }

        set_Value("Description", Description);
    }

/** Set DocAction.
 *
 * @param DocAction
DocAction */
    public void setDocAction(String DocAction) {

        if (DocAction == null) {
            throw new IllegalArgumentException("DocAction is mandatory");
        }

        if (DocAction.length() > 2) {

            log.warning("Length > 2 - truncated");
            DocAction	= DocAction.substring(0, 1);
        }

        set_Value("DocAction", DocAction);
    }

/** Set DocStatus.
 *
 * @param DocStatus
DocStatus */
    public void setDocStatus(String DocStatus) {

        if (DocStatus.equals("??") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("CO") || DocStatus.equals("DR") || DocStatus.equals("IN") || DocStatus.equals("IP") || DocStatus.equals("NA") || DocStatus.equals("RE") || DocStatus.equals("VO") || DocStatus.equals("WC") || DocStatus.equals("WP")) {
            ;
        } else {
            throw new IllegalArgumentException("DocStatus Invalid value - Reference_ID=131 - ?? - AP - CL - CO - DR - IN - IP - NA - RE - VO - WC - WP");
        }

        if (DocStatus == null) {
            throw new IllegalArgumentException("DocStatus is mandatory");
        }

        if (DocStatus.length() > 2) {

            log.warning("Length > 2 - truncated");
            DocStatus	= DocStatus.substring(0, 1);
        }

        set_Value("DocStatus", DocStatus);
    }

/** Set Grand Total.
 *
 * @param GrandTotal
GrandTotal */
    public void setGrandTotal(BigDecimal GrandTotal) {

        if (GrandTotal == null) {
            throw new IllegalArgumentException("GrandTotal is mandatory");
        }

        set_Value("GrandTotal", GrandTotal);
    }

/** Set M_PriceList_ID.
 *
 * @param M_PriceList_ID
M_PriceList_ID */
    public void setM_PriceList_ID(int M_PriceList_ID) {
        set_Value("M_PriceList_ID", new Integer(M_PriceList_ID));
    }

/** Set M_Product_Category_ID.
 *
 * @param M_Product_Category_ID
M_Product_Category_ID */
    public void setM_Product_Category_ID(int M_Product_Category_ID) {
        set_Value("M_Product_Category_ID", new Integer(M_Product_Category_ID));
    }

/** Set M_Product_Gamas_ID.
 *
 * @param M_Product_Gamas_ID
M_Product_Gamas_ID */
    public void setM_Product_Gamas_ID(int M_Product_Gamas_ID) {
        set_Value("M_Product_Gamas_ID", new Integer(M_Product_Gamas_ID));
    }

/** Set M_Product_ID.
 *
 * @param M_Product_ID
M_Product_ID */
    public void setM_Product_ID(int M_Product_ID) {

        if (M_Product_ID <= 0) {
            set_Value("M_Product_ID", null);
        } else {
            set_Value("M_Product_ID", new Integer(M_Product_ID));
        }
    }

/** Set M_Warehouse_ID.
 *
 * @param M_Warehouse_ID
M_Warehouse_ID */
    public void setM_Warehouse_ID(int M_Warehouse_ID) {
        set_Value("M_Warehouse_ID", new Integer(M_Warehouse_ID));
    }

/** Set POReference.
 *
 * @param POReference
POReference */
    public void setPOReference(String POReference) {

        if ((POReference != null) && (POReference.length() > 80)) {

            log.warning("Length > 80 - truncated");
            POReference	= POReference.substring(0, 79);
        }

        set_Value("POReference", POReference);
    }

/** Set Param1.
 *
 * @param Param1
Param1 */
    public void setParam1(BigDecimal Param1) {
        set_Value("Param1", Param1);
    }

/** Set Param2.
 *
 * @param Param2
Param2 */
    public void setParam2(BigDecimal Param2) {
        set_Value("Param2", Param2);
    }

/** Set Param3.
 *
 * @param Param3
Param3 */
    public void setParam3(int Param3) {
        set_Value("Param3", new Integer(Param3));
    }

/** Set Param4.
 *
 * @param Param4
Param4 */
    public void setParam4(BigDecimal Param4) {
        set_Value("Param4", Param4);
    }

/**
 * Set PriceEntered
 *
 * @param PriceEntered
 */
    public void setPriceEntered(BigDecimal PriceEntered) {

        if (PriceEntered == null) {
            throw new IllegalArgumentException("PriceEntered is mandatory");
        }

        set_Value("PriceEntered", PriceEntered);
    }

/** Set PriceList.
 *
 * @param PriceList
PriceList */
    public void setPriceList(BigDecimal PriceList) {

        if (PriceList == null) {
            throw new IllegalArgumentException("PriceList is mandatory");
        }

        set_Value("PriceList", PriceList);
    }

/** Set Processed.
 *
 * @param Processed
Processed */
    public void setProcessed(boolean Processed) {
        set_Value("Processed", new Boolean(Processed));
    }

/** Set QtyAvailable.
 *
 * @param QtyAvailable
QtyAvailable */
    public void setQtyAvailable(int QtyAvailable) {
        set_Value("QtyAvailable", new Integer(QtyAvailable));
    }

/** Set QtyEntered.
 *
 * @param QtyEntered
QtyEntered */
    public void setQtyEntered(BigDecimal QtyEntered) {

        if (QtyEntered == null) {
            throw new IllegalArgumentException("QtyEntered is mandatory");
        }

        set_Value("QtyEntered", QtyEntered);
    }

/** Set QtyOnHand.
 *
 * @param QtyOnHand
QtyOnHand */
    public void setQtyOnHand(int QtyOnHand) {
        set_Value("QtyOnHand", new Integer(QtyOnHand));
    }

/** Set Ref_Order_ID.
 *
 * @param Ref_Order_ID
Ref_Order_ID */
    public void setRef_Order_ID(int Ref_Order_ID) {

        if (Ref_Order_ID <= 0) {
            set_Value("Ref_Order_ID", null);
        } else {
            set_Value("Ref_Order_ID", new Integer(Ref_Order_ID));
        }
    }

/** Set TotalLines.
 *
 * @param TotalLines
TotalLines */
    public void setTotalLines(BigDecimal TotalLines) {

        if (TotalLines == null) {
            throw new IllegalArgumentException("TotalLines is mandatory");
        }

        set_Value("TotalLines", TotalLines);
    }

/** Set User1_ID.
 *
 * @param User1_ID
User1_ID */
    public void setUser1_ID(int User1_ID) {

        if (User1_ID <= 0) {
            set_Value("User1_ID", null);
        } else {
            set_Value("User1_ID", new Integer(User1_ID));
        }
    }
}



/*
 * @(#)X_C_Fact.java   02.jul 2007
 * 
 *  Fin del fichero X_C_Fact.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
