/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PeriodControl
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:31.734 */
public class X_C_PeriodControl extends PO
{
/** Constructor estándar */
public X_C_PeriodControl (Properties ctx, int C_PeriodControl_ID, String trxName)
{
super (ctx, C_PeriodControl_ID, trxName);
/** if (C_PeriodControl_ID == 0)
{
setC_PeriodControl_ID (0);
setC_Period_ID (0);
setDocBaseType (null);
setPeriodAction (null);	// N
}
 */
}
/** Load Constructor */
public X_C_PeriodControl (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=229 */
public static final int Table_ID=229;

/** TableName=C_PeriodControl */
public static final String Table_Name="C_PeriodControl";

protected static KeyNamePair Model = new KeyNamePair(229,"C_PeriodControl");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PeriodControl[").append(getID()).append("]");
return sb.toString();
}
/** Set Period Control */
public void setC_PeriodControl_ID (int C_PeriodControl_ID)
{
set_ValueNoCheck ("C_PeriodControl_ID", new Integer(C_PeriodControl_ID));
}
/** Get Period Control */
public int getC_PeriodControl_ID() 
{
Integer ii = (Integer)get_Value("C_PeriodControl_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_PeriodControl_ID()));
}
/** Set Period.
Period of the Calendar */
public void setC_Period_ID (int C_Period_ID)
{
set_ValueNoCheck ("C_Period_ID", new Integer(C_Period_ID));
}
/** Get Period.
Period of the Calendar */
public int getC_Period_ID() 
{
Integer ii = (Integer)get_Value("C_Period_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int DOCBASETYPE_AD_Reference_ID=183;
/** Project Issue = PJI */
public static final String DOCBASETYPE_ProjectIssue = "PJI";
/** AP Credit Memo = APC */
public static final String DOCBASETYPE_APCreditMemo = "APC";
/** AR Credit Memo = ARC */
public static final String DOCBASETYPE_ARCreditMemo = "ARC";
/** Bank Statement = CMB */
public static final String DOCBASETYPE_BankStatement = "CMB";
/** Cash Journal = CMC */
public static final String DOCBASETYPE_CashJournal = "CMC";
/** Payment Allocation = CMA */
public static final String DOCBASETYPE_PaymentAllocation = "CMA";
/** GL Journal = GLJ */
public static final String DOCBASETYPE_GLJournal = "GLJ";
/** GL Document = GLD */
public static final String DOCBASETYPE_GLDocument = "GLD";
/** AP Invoice = API */
public static final String DOCBASETYPE_APInvoice = "API";
/** AP Payment = APP */
public static final String DOCBASETYPE_APPayment = "APP";
/** AR Invoice = ARI */
public static final String DOCBASETYPE_ARInvoice = "ARI";
/** AR Receipt = ARR */
public static final String DOCBASETYPE_ARReceipt = "ARR";
/** Sales Order = SOO */
public static final String DOCBASETYPE_SalesOrder = "SOO";
/** Manufacturing Order Issue = MOI */
public static final String DOCBASETYPE_ManufacturingOrderIssue = "MOI";
/** Manufacturing Order Receipt = MOR */
public static final String DOCBASETYPE_ManufacturingOrderReceipt = "MOR";
/** Manufacturing Order Use Variation  = MOU */
public static final String DOCBASETYPE_ManufacturingOrderUseVariation = "MOU";
/** Manufacturing Order Method Variation  = MOM */
public static final String DOCBASETYPE_ManufacturingOrderMethodVariation = "MOM";
/** Manufacturing Order Rate Variation  = MOV */
public static final String DOCBASETYPE_ManufacturingOrderRateVariation = "MOV";
/** Manufacturing Order = MOP */
public static final String DOCBASETYPE_ManufacturingOrder = "MOP";
/** Maintenance Order = MOF */
public static final String DOCBASETYPE_MaintenanceOrder = "MOF";
/** AR Pro Forma Invoice = ARF */
public static final String DOCBASETYPE_ARProFormaInvoice = "ARF";
/** Material Delivery = MMS */
public static final String DOCBASETYPE_MaterialDelivery = "MMS";
/** Material Receipt = MMR */
public static final String DOCBASETYPE_MaterialReceipt = "MMR";
/** Material Movement = MMM */
public static final String DOCBASETYPE_MaterialMovement = "MMM";
/** Purchase Order = POO */
public static final String DOCBASETYPE_PurchaseOrder = "POO";
/** Purchase Requisition = POR */
public static final String DOCBASETYPE_PurchaseRequisition = "POR";
/** Material Physical Inventory = MMI */
public static final String DOCBASETYPE_MaterialPhysicalInventory = "MMI";
/** Material Production = MMP */
public static final String DOCBASETYPE_MaterialProduction = "MMP";
/** Match Invoice = MXI */
public static final String DOCBASETYPE_MatchInvoice = "MXI";
/** Match PO = MXP */
public static final String DOCBASETYPE_MatchPO = "MXP";
/** Set Document BaseType.
Logical type of document */
public void setDocBaseType (String DocBaseType)
{
if (DocBaseType.equals("PJI") || DocBaseType.equals("APC") || DocBaseType.equals("ARC") || DocBaseType.equals("CMB") || DocBaseType.equals("CMC") || DocBaseType.equals("CMA") || DocBaseType.equals("GLJ") || DocBaseType.equals("GLD") || DocBaseType.equals("API") || DocBaseType.equals("APP") || DocBaseType.equals("ARI") || DocBaseType.equals("ARR") || DocBaseType.equals("SOO") || DocBaseType.equals("MOI") || DocBaseType.equals("MOR") || DocBaseType.equals("MOU") || DocBaseType.equals("MOM") || DocBaseType.equals("MOV") || DocBaseType.equals("MOP") || DocBaseType.equals("MOF") || DocBaseType.equals("ARF") || DocBaseType.equals("MMS") || DocBaseType.equals("MMR") || DocBaseType.equals("MMM") || DocBaseType.equals("POO") || DocBaseType.equals("POR") || DocBaseType.equals("MMI") || DocBaseType.equals("MMP") || DocBaseType.equals("MXI") || DocBaseType.equals("MXP"));
 else throw new IllegalArgumentException ("DocBaseType Invalid value - Reference_ID=183 - PJI - APC - ARC - CMB - CMC - CMA - GLJ - GLD - API - APP - ARI - ARR - SOO - MOI - MOR - MOU - MOM - MOV - MOP - MOF - ARF - MMS - MMR - MMM - POO - POR - MMI - MMP - MXI - MXP");
if (DocBaseType == null) throw new IllegalArgumentException ("DocBaseType is mandatory");
if (DocBaseType.length() > 3)
{
log.warning("Length > 3 - truncated");
DocBaseType = DocBaseType.substring(0,2);
}
set_ValueNoCheck ("DocBaseType", DocBaseType);
}
/** Get Document BaseType.
Logical type of document */
public String getDocBaseType() 
{
return (String)get_Value("DocBaseType");
}
public static final int PERIODACTION_AD_Reference_ID=176;
/** Close Period = C */
public static final String PERIODACTION_ClosePeriod = "C";
/** Permanently Close Period = P */
public static final String PERIODACTION_PermanentlyClosePeriod = "P";
/** <No Action> = N */
public static final String PERIODACTION_NoAction = "N";
/** Open Period = O */
public static final String PERIODACTION_OpenPeriod = "O";
/** Set Period Action.
Action taken for this period */
public void setPeriodAction (String PeriodAction)
{
if (PeriodAction.equals("C") || PeriodAction.equals("P") || PeriodAction.equals("N") || PeriodAction.equals("O"));
 else throw new IllegalArgumentException ("PeriodAction Invalid value - Reference_ID=176 - C - P - N - O");
if (PeriodAction == null) throw new IllegalArgumentException ("PeriodAction is mandatory");
if (PeriodAction.length() > 1)
{
log.warning("Length > 1 - truncated");
PeriodAction = PeriodAction.substring(0,0);
}
set_Value ("PeriodAction", PeriodAction);
}
/** Get Period Action.
Action taken for this period */
public String getPeriodAction() 
{
return (String)get_Value("PeriodAction");
}
public static final int PERIODSTATUS_AD_Reference_ID=177;
/** Open = O */
public static final String PERIODSTATUS_Open = "O";
/** Closed = C */
public static final String PERIODSTATUS_Closed = "C";
/** Permanently closed = P */
public static final String PERIODSTATUS_PermanentlyClosed = "P";
/** Never opened = N */
public static final String PERIODSTATUS_NeverOpened = "N";
/** Set Period Status.
Current state of this period */
public void setPeriodStatus (String PeriodStatus)
{
if (PeriodStatus == null || PeriodStatus.equals("O") || PeriodStatus.equals("C") || PeriodStatus.equals("P") || PeriodStatus.equals("N"));
 else throw new IllegalArgumentException ("PeriodStatus Invalid value - Reference_ID=177 - O - C - P - N");
if (PeriodStatus != null && PeriodStatus.length() > 1)
{
log.warning("Length > 1 - truncated");
PeriodStatus = PeriodStatus.substring(0,0);
}
set_ValueNoCheck ("PeriodStatus", PeriodStatus);
}
/** Get Period Status.
Current state of this period */
public String getPeriodStatus() 
{
return (String)get_Value("PeriodStatus");
}
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
