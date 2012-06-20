/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_ProjectPhase
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.062 */
public class X_C_ProjectPhase extends PO
{
/** Constructor estándar */
public X_C_ProjectPhase (Properties ctx, int C_ProjectPhase_ID, String trxName)
{
super (ctx, C_ProjectPhase_ID, trxName);
/** if (C_ProjectPhase_ID == 0)
{
setC_ProjectPhase_ID (0);
setC_Project_ID (0);
setCommittedAmt (Env.ZERO);
setIsCommitCeiling (false);
setIsComplete (false);
setName (null);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM C_ProjectPhase WHERE C_Project_ID=@C_Project_ID@
}
 */
}
/** Load Constructor */
public X_C_ProjectPhase (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=576 */
public static final int Table_ID=576;

/** TableName=C_ProjectPhase */
public static final String Table_Name="C_ProjectPhase";

protected static KeyNamePair Model = new KeyNamePair(576,"C_ProjectPhase");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ProjectPhase[").append(getID()).append("]");
return sb.toString();
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
if (C_Order_ID <= 0) set_ValueNoCheck ("C_Order_ID", null);
 else 
set_ValueNoCheck ("C_Order_ID", new Integer(C_Order_ID));
}
/** Get Order.
Order */
public int getC_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Standard Phase.
Standard Phase of the Project Type */
public void setC_Phase_ID (int C_Phase_ID)
{
if (C_Phase_ID <= 0) set_ValueNoCheck ("C_Phase_ID", null);
 else 
set_ValueNoCheck ("C_Phase_ID", new Integer(C_Phase_ID));
}
/** Get Standard Phase.
Standard Phase of the Project Type */
public int getC_Phase_ID() 
{
Integer ii = (Integer)get_Value("C_Phase_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project Phase.
Phase of a Project */
public void setC_ProjectPhase_ID (int C_ProjectPhase_ID)
{
set_ValueNoCheck ("C_ProjectPhase_ID", new Integer(C_ProjectPhase_ID));
}
/** Get Project Phase.
Phase of a Project */
public int getC_ProjectPhase_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectPhase_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
set_ValueNoCheck ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Committed Amount.
The (legal) commitment amount */
public void setCommittedAmt (BigDecimal CommittedAmt)
{
if (CommittedAmt == null) throw new IllegalArgumentException ("CommittedAmt is mandatory");
set_Value ("CommittedAmt", CommittedAmt);
}
/** Get Committed Amount.
The (legal) commitment amount */
public BigDecimal getCommittedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CommittedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int CREATEDOCUMENT_AD_Reference_ID=183;
/** Project Issue = PJI */
public static final String CREATEDOCUMENT_ProjectIssue = "PJI";
/** AP Credit Memo = APC */
public static final String CREATEDOCUMENT_APCreditMemo = "APC";
/** AR Credit Memo = ARC */
public static final String CREATEDOCUMENT_ARCreditMemo = "ARC";
/** Bank Statement = CMB */
public static final String CREATEDOCUMENT_BankStatement = "CMB";
/** Cash Journal = CMC */
public static final String CREATEDOCUMENT_CashJournal = "CMC";
/** Payment Allocation = CMA */
public static final String CREATEDOCUMENT_PaymentAllocation = "CMA";
/** GL Journal = GLJ */
public static final String CREATEDOCUMENT_GLJournal = "GLJ";
/** GL Document = GLD */
public static final String CREATEDOCUMENT_GLDocument = "GLD";
/** AP Invoice = API */
public static final String CREATEDOCUMENT_APInvoice = "API";
/** AP Payment = APP */
public static final String CREATEDOCUMENT_APPayment = "APP";
/** AR Invoice = ARI */
public static final String CREATEDOCUMENT_ARInvoice = "ARI";
/** AR Receipt = ARR */
public static final String CREATEDOCUMENT_ARReceipt = "ARR";
/** Sales Order = SOO */
public static final String CREATEDOCUMENT_SalesOrder = "SOO";
/** Manufacturing Order Issue = MOI */
public static final String CREATEDOCUMENT_ManufacturingOrderIssue = "MOI";
/** Manufacturing Order Receipt = MOR */
public static final String CREATEDOCUMENT_ManufacturingOrderReceipt = "MOR";
/** Manufacturing Order Use Variation  = MOU */
public static final String CREATEDOCUMENT_ManufacturingOrderUseVariation = "MOU";
/** Manufacturing Order Method Variation  = MOM */
public static final String CREATEDOCUMENT_ManufacturingOrderMethodVariation = "MOM";
/** Manufacturing Order Rate Variation  = MOV */
public static final String CREATEDOCUMENT_ManufacturingOrderRateVariation = "MOV";
/** Manufacturing Order = MOP */
public static final String CREATEDOCUMENT_ManufacturingOrder = "MOP";
/** Maintenance Order = MOF */
public static final String CREATEDOCUMENT_MaintenanceOrder = "MOF";
/** AR Pro Forma Invoice = ARF */
public static final String CREATEDOCUMENT_ARProFormaInvoice = "ARF";
/** Material Delivery = MMS */
public static final String CREATEDOCUMENT_MaterialDelivery = "MMS";
/** Material Receipt = MMR */
public static final String CREATEDOCUMENT_MaterialReceipt = "MMR";
/** Material Movement = MMM */
public static final String CREATEDOCUMENT_MaterialMovement = "MMM";
/** Purchase Order = POO */
public static final String CREATEDOCUMENT_PurchaseOrder = "POO";
/** Purchase Requisition = POR */
public static final String CREATEDOCUMENT_PurchaseRequisition = "POR";
/** Material Physical Inventory = MMI */
public static final String CREATEDOCUMENT_MaterialPhysicalInventory = "MMI";
/** Material Production = MMP */
public static final String CREATEDOCUMENT_MaterialProduction = "MMP";
/** Match Invoice = MXI */
public static final String CREATEDOCUMENT_MatchInvoice = "MXI";
/** Match PO = MXP */
public static final String CREATEDOCUMENT_MatchPO = "MXP";
/** Set CreateDocument */
public void setCreateDocument (String CreateDocument)
{
if (CreateDocument == null || CreateDocument.equals("PJI") || CreateDocument.equals("APC") || CreateDocument.equals("ARC") || CreateDocument.equals("CMB") || CreateDocument.equals("CMC") || CreateDocument.equals("CMA") || CreateDocument.equals("GLJ") || CreateDocument.equals("GLD") || CreateDocument.equals("API") || CreateDocument.equals("APP") || CreateDocument.equals("ARI") || CreateDocument.equals("ARR") || CreateDocument.equals("SOO") || CreateDocument.equals("MOI") || CreateDocument.equals("MOR") || CreateDocument.equals("MOU") || CreateDocument.equals("MOM") || CreateDocument.equals("MOV") || CreateDocument.equals("MOP") || CreateDocument.equals("MOF") || CreateDocument.equals("ARF") || CreateDocument.equals("MMS") || CreateDocument.equals("MMR") || CreateDocument.equals("MMM") || CreateDocument.equals("POO") || CreateDocument.equals("POR") || CreateDocument.equals("MMI") || CreateDocument.equals("MMP") || CreateDocument.equals("MXI") || CreateDocument.equals("MXP"));
 else throw new IllegalArgumentException ("CreateDocument Invalid value - Reference_ID=183 - PJI - APC - ARC - CMB - CMC - CMA - GLJ - GLD - API - APP - ARI - ARR - SOO - MOI - MOR - MOU - MOM - MOV - MOP - MOF - ARF - MMS - MMR - MMM - POO - POR - MMI - MMP - MXI - MXP");
if (CreateDocument != null && CreateDocument.length() > 1)
{
log.warning("Length > 1 - truncated");
CreateDocument = CreateDocument.substring(0,0);
}
set_Value ("CreateDocument", CreateDocument);
}
/** Get CreateDocument */
public String getCreateDocument() 
{
return (String)get_Value("CreateDocument");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set End Date.
Last effective date (inclusive) */
public void setEndDate (Timestamp EndDate)
{
set_Value ("EndDate", EndDate);
}
/** Get End Date.
Last effective date (inclusive) */
public Timestamp getEndDate() 
{
return (Timestamp)get_Value("EndDate");
}
/** Set Generate Order.
Generate Order */
public void setGenerateOrder (String GenerateOrder)
{
if (GenerateOrder != null && GenerateOrder.length() > 1)
{
log.warning("Length > 1 - truncated");
GenerateOrder = GenerateOrder.substring(0,0);
}
set_Value ("GenerateOrder", GenerateOrder);
}
/** Get Generate Order.
Generate Order */
public String getGenerateOrder() 
{
return (String)get_Value("GenerateOrder");
}
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Commitment is Ceiling.
The commitment amount/quantity is the chargeable ceiling  */
public void setIsCommitCeiling (boolean IsCommitCeiling)
{
set_Value ("IsCommitCeiling", new Boolean(IsCommitCeiling));
}
/** Get Commitment is Ceiling.
The commitment amount/quantity is the chargeable ceiling  */
public boolean isCommitCeiling() 
{
Object oo = get_Value("IsCommitCeiling");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Complete.
It is complete */
public void setIsComplete (boolean IsComplete)
{
set_Value ("IsComplete", new Boolean(IsComplete));
}
/** Get Complete.
It is complete */
public boolean isComplete() 
{
Object oo = get_Value("IsComplete");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
/** Set Unit Price.
Actual Price  */
public void setPriceActual (BigDecimal PriceActual)
{
set_Value ("PriceActual", PriceActual);
}
/** Get Unit Price.
Actual Price  */
public BigDecimal getPriceActual() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceActual");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
set_Value ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getSeqNo()));
}
/** Set Start Date.
First effective day (inclusive) */
public void setStartDate (Timestamp StartDate)
{
set_Value ("StartDate", StartDate);
}
/** Get Start Date.
First effective day (inclusive) */
public Timestamp getStartDate() 
{
return (Timestamp)get_Value("StartDate");
}
}
