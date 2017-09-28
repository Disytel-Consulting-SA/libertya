/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Project
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-09-26 10:29:15.307 */
public class X_C_Project extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Project (Properties ctx, int C_Project_ID, String trxName)
{
super (ctx, C_Project_ID, trxName);
/** if (C_Project_ID == 0)
{
setC_Currency_ID (0);
setCommittedAmt (Env.ZERO);
setCommittedQty (Env.ZERO);
setC_Project_ID (0);
setInvoicedAmt (Env.ZERO);
setInvoicedQty (Env.ZERO);
setIsCommitCeiling (false);
setIsCommitment (false);
setIsSummary (false);
setName (null);
setPlannedAmt (Env.ZERO);
setPlannedMarginAmt (Env.ZERO);
setPlannedQty (Env.ZERO);
setProcessed (false);
setProjectBalanceAmt (Env.ZERO);
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_Project (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Project");

/** TableName=C_Project */
public static final String Table_Name="C_Project";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Project");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Project[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_Value ("AD_User_ID", null);
 else 
set_Value ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Partner Location.
Identifies the (ship to) address for this Business Partner */
public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
{
if (C_BPartner_Location_ID <= 0) set_Value ("C_BPartner_Location_ID", null);
 else 
set_Value ("C_BPartner_Location_ID", new Integer(C_BPartner_Location_ID));
}
/** Get Partner Location.
Identifies the (ship to) address for this Business Partner */
public int getC_BPartner_Location_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Campaign.
Marketing Campaign */
public void setC_Campaign_ID (int C_Campaign_ID)
{
if (C_Campaign_ID <= 0) set_Value ("C_Campaign_ID", null);
 else 
set_Value ("C_Campaign_ID", new Integer(C_Campaign_ID));
}
/** Get Campaign.
Marketing Campaign */
public int getC_Campaign_ID() 
{
Integer ii = (Integer)get_Value("C_Campaign_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
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
/** Set Committed Quantity.
The (legal) commitment Quantity */
public void setCommittedQty (BigDecimal CommittedQty)
{
if (CommittedQty == null) throw new IllegalArgumentException ("CommittedQty is mandatory");
set_Value ("CommittedQty", CommittedQty);
}
/** Get Committed Quantity.
The (legal) commitment Quantity */
public BigDecimal getCommittedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("CommittedQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Copy From.
Copy From Record */
public void setCopyFrom (String CopyFrom)
{
if (CopyFrom != null && CopyFrom.length() > 1)
{
log.warning("Length > 1 - truncated");
CopyFrom = CopyFrom.substring(0,1);
}
set_Value ("CopyFrom", CopyFrom);
}
/** Get Copy From.
Copy From Record */
public String getCopyFrom() 
{
return (String)get_Value("CopyFrom");
}
/** Set Payment Term.
The terms for Payment of this transaction */
public void setC_PaymentTerm_ID (int C_PaymentTerm_ID)
{
if (C_PaymentTerm_ID <= 0) set_Value ("C_PaymentTerm_ID", null);
 else 
set_Value ("C_PaymentTerm_ID", new Integer(C_PaymentTerm_ID));
}
/** Get Payment Term.
The terms for Payment of this transaction */
public int getC_PaymentTerm_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentTerm_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Standard Phase.
Standard Phase of the Project Type */
public void setC_Phase_ID (int C_Phase_ID)
{
if (C_Phase_ID <= 0) set_Value ("C_Phase_ID", null);
 else 
set_Value ("C_Phase_ID", new Integer(C_Phase_ID));
}
/** Get Standard Phase.
Standard Phase of the Project Type */
public int getC_Phase_ID() 
{
Integer ii = (Integer)get_Value("C_Phase_ID");
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
/** Set Project Type.
Type of the project */
public void setC_ProjectType_ID (String C_ProjectType_ID)
{
if (C_ProjectType_ID != null && C_ProjectType_ID.length() > 22)
{
log.warning("Length > 22 - truncated");
C_ProjectType_ID = C_ProjectType_ID.substring(0,22);
}
set_Value ("C_ProjectType_ID", C_ProjectType_ID);
}
/** Get Project Type.
Type of the project */
public String getC_ProjectType_ID() 
{
return (String)get_Value("C_ProjectType_ID");
}
public static final int CREATEDOCUMENT_AD_Reference_ID = MReference.getReferenceID("C_DocType DocBaseType");
/** Material Production = MMP */
public static final String CREATEDOCUMENT_MaterialProduction = "MMP";
/** Match Invoice = MXI */
public static final String CREATEDOCUMENT_MatchInvoice = "MXI";
/** Match PO = MXP */
public static final String CREATEDOCUMENT_MatchPO = "MXP";
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
/** Bank Statement = CMB */
public static final String CREATEDOCUMENT_BankStatement = "CMB";
/** Cash Journal = CMC */
public static final String CREATEDOCUMENT_CashJournal = "CMC";
/** Payment Allocation = CMA */
public static final String CREATEDOCUMENT_PaymentAllocation = "CMA";
/** AP Credit Memo = APC */
public static final String CREATEDOCUMENT_APCreditMemo = "APC";
/** AR Credit Memo = ARC */
public static final String CREATEDOCUMENT_ARCreditMemo = "ARC";
/** Project Issue = PJI */
public static final String CREATEDOCUMENT_ProjectIssue = "PJI";
/** Amortization = AMO */
public static final String CREATEDOCUMENT_Amortization = "AMO";
/** Bank List = BLB */
public static final String CREATEDOCUMENT_BankList = "BLB";
/** Check Printing = CHP */
public static final String CREATEDOCUMENT_CheckPrinting = "CHP";
/** AP Payment Batch = APB */
public static final String CREATEDOCUMENT_APPaymentBatch = "APB";
/** Credit Card Settlement = CCS */
public static final String CREATEDOCUMENT_CreditCardSettlement = "CCS";
/** Set CreateDocument */
public void setCreateDocument (String CreateDocument)
{
if (CreateDocument == null || CreateDocument.equals("MMP") || CreateDocument.equals("MXI") || CreateDocument.equals("MXP") || CreateDocument.equals("ARF") || CreateDocument.equals("MMS") || CreateDocument.equals("MMR") || CreateDocument.equals("MMM") || CreateDocument.equals("POO") || CreateDocument.equals("POR") || CreateDocument.equals("MMI") || CreateDocument.equals("MOR") || CreateDocument.equals("MOU") || CreateDocument.equals("MOM") || CreateDocument.equals("MOV") || CreateDocument.equals("MOP") || CreateDocument.equals("MOF") || CreateDocument.equals("GLJ") || CreateDocument.equals("GLD") || CreateDocument.equals("API") || CreateDocument.equals("APP") || CreateDocument.equals("ARI") || CreateDocument.equals("ARR") || CreateDocument.equals("SOO") || CreateDocument.equals("MOI") || CreateDocument.equals("CMB") || CreateDocument.equals("CMC") || CreateDocument.equals("CMA") || CreateDocument.equals("APC") || CreateDocument.equals("ARC") || CreateDocument.equals("PJI") || CreateDocument.equals("AMO") || CreateDocument.equals("BLB") || CreateDocument.equals("CHP") || CreateDocument.equals("APB") || CreateDocument.equals("CCS") || ( refContainsValue("CORE-AD_Reference-183", CreateDocument) ) );
 else throw new IllegalArgumentException ("CreateDocument Invalid value: " + CreateDocument + ".  Valid: " +  refValidOptions("CORE-AD_Reference-183") );
if (CreateDocument != null && CreateDocument.length() > 1)
{
log.warning("Length > 1 - truncated");
CreateDocument = CreateDocument.substring(0,1);
}
set_Value ("CreateDocument", CreateDocument);
}
/** Get CreateDocument */
public String getCreateDocument() 
{
return (String)get_Value("CreateDocument");
}
/** Set Contract Date.
The (planned) effective date of this document. */
public void setDateContract (Timestamp DateContract)
{
set_Value ("DateContract", DateContract);
}
/** Get Contract Date.
The (planned) effective date of this document. */
public Timestamp getDateContract() 
{
return (Timestamp)get_Value("DateContract");
}
/** Set Finish Date.
Finish or (planned) completion date */
public void setDateFinish (Timestamp DateFinish)
{
set_Value ("DateFinish", DateFinish);
}
/** Get Finish Date.
Finish or (planned) completion date */
public Timestamp getDateFinish() 
{
return (Timestamp)get_Value("DateFinish");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Generate To.
Generate To */
public void setGenerateTo (String GenerateTo)
{
if (GenerateTo != null && GenerateTo.length() > 1)
{
log.warning("Length > 1 - truncated");
GenerateTo = GenerateTo.substring(0,1);
}
set_Value ("GenerateTo", GenerateTo);
}
/** Get Generate To.
Generate To */
public String getGenerateTo() 
{
return (String)get_Value("GenerateTo");
}
/** Set Invoiced Amount.
The amount invoiced */
public void setInvoicedAmt (BigDecimal InvoicedAmt)
{
if (InvoicedAmt == null) throw new IllegalArgumentException ("InvoicedAmt is mandatory");
set_ValueNoCheck ("InvoicedAmt", InvoicedAmt);
}
/** Get Invoiced Amount.
The amount invoiced */
public BigDecimal getInvoicedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("InvoicedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Invoiced Quantity.
The Quantity Invoiced */
public void setInvoicedQty (BigDecimal InvoicedQty)
{
if (InvoicedQty == null) throw new IllegalArgumentException ("InvoicedQty is mandatory");
set_ValueNoCheck ("InvoicedQty", InvoicedQty);
}
/** Get Invoiced Quantity.
The Quantity Invoiced */
public BigDecimal getInvoicedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("InvoicedQty");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Commitment.
Is this document a (legal) commitment? */
public void setIsCommitment (boolean IsCommitment)
{
set_Value ("IsCommitment", new Boolean(IsCommitment));
}
/** Get Commitment.
Is this document a (legal) commitment? */
public boolean isCommitment() 
{
Object oo = get_Value("IsCommitment");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Summary Level.
This is a summary entity */
public void setIsSummary (boolean IsSummary)
{
set_Value ("IsSummary", new Boolean(IsSummary));
}
/** Get Summary Level.
This is a summary entity */
public boolean isSummary() 
{
Object oo = get_Value("IsSummary");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Price List Version.
Identifies a unique instance of a Price List */
public void setM_PriceList_Version_ID (int M_PriceList_Version_ID)
{
if (M_PriceList_Version_ID <= 0) set_Value ("M_PriceList_Version_ID", null);
 else 
set_Value ("M_PriceList_Version_ID", new Integer(M_PriceList_Version_ID));
}
/** Get Price List Version.
Identifies a unique instance of a Price List */
public int getM_PriceList_Version_ID() 
{
Integer ii = (Integer)get_Value("M_PriceList_Version_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
if (M_Warehouse_ID <= 0) set_Value ("M_Warehouse_ID", null);
 else 
set_Value ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
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
Name = Name.substring(0,60);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
/** Set Note.
Optional additional user defined information */
public void setNote (String Note)
{
if (Note != null && Note.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Note = Note.substring(0,2000);
}
set_Value ("Note", Note);
}
/** Get Note.
Optional additional user defined information */
public String getNote() 
{
return (String)get_Value("Note");
}
/** Set Planned Amount.
Planned amount for this project */
public void setPlannedAmt (BigDecimal PlannedAmt)
{
if (PlannedAmt == null) throw new IllegalArgumentException ("PlannedAmt is mandatory");
set_Value ("PlannedAmt", PlannedAmt);
}
/** Get Planned Amount.
Planned amount for this project */
public BigDecimal getPlannedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PlannedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Planned Margin.
Project's planned margin amount */
public void setPlannedMarginAmt (BigDecimal PlannedMarginAmt)
{
if (PlannedMarginAmt == null) throw new IllegalArgumentException ("PlannedMarginAmt is mandatory");
set_Value ("PlannedMarginAmt", PlannedMarginAmt);
}
/** Get Planned Margin.
Project's planned margin amount */
public BigDecimal getPlannedMarginAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PlannedMarginAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Planned Quantity.
Planned quantity for this project */
public void setPlannedQty (BigDecimal PlannedQty)
{
if (PlannedQty == null) throw new IllegalArgumentException ("PlannedQty is mandatory");
set_Value ("PlannedQty", PlannedQty);
}
/** Get Planned Quantity.
Planned quantity for this project */
public BigDecimal getPlannedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("PlannedQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Order Reference.
Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner */
public void setPOReference (String POReference)
{
if (POReference != null && POReference.length() > 20)
{
log.warning("Length > 20 - truncated");
POReference = POReference.substring(0,20);
}
set_Value ("POReference", POReference);
}
/** Get Order Reference.
Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner */
public String getPOReference() 
{
return (String)get_Value("POReference");
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Project Balance.
Total Project Balance */
public void setProjectBalanceAmt (BigDecimal ProjectBalanceAmt)
{
if (ProjectBalanceAmt == null) throw new IllegalArgumentException ("ProjectBalanceAmt is mandatory");
set_ValueNoCheck ("ProjectBalanceAmt", ProjectBalanceAmt);
}
/** Get Project Balance.
Total Project Balance */
public BigDecimal getProjectBalanceAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ProjectBalanceAmt");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int PROJECTCATEGORY_AD_Reference_ID = MReference.getReferenceID("C_ProjectType Category");
/** General = N */
public static final String PROJECTCATEGORY_General = "N";
/** Asset Project = A */
public static final String PROJECTCATEGORY_AssetProject = "A";
/** Work Order (Job) = W */
public static final String PROJECTCATEGORY_WorkOrderJob = "W";
/** Service (Charge) Project = S */
public static final String PROJECTCATEGORY_ServiceChargeProject = "S";
/** Set Project Category.
Project Category */
public void setProjectCategory (String ProjectCategory)
{
if (ProjectCategory == null || ProjectCategory.equals("N") || ProjectCategory.equals("A") || ProjectCategory.equals("W") || ProjectCategory.equals("S") || ( refContainsValue("CORE-AD_Reference-288", ProjectCategory) ) );
 else throw new IllegalArgumentException ("ProjectCategory Invalid value: " + ProjectCategory + ".  Valid: " +  refValidOptions("CORE-AD_Reference-288") );
if (ProjectCategory != null && ProjectCategory.length() > 1)
{
log.warning("Length > 1 - truncated");
ProjectCategory = ProjectCategory.substring(0,1);
}
set_Value ("ProjectCategory", ProjectCategory);
}
/** Get Project Category.
Project Category */
public String getProjectCategory() 
{
return (String)get_Value("ProjectCategory");
}
public static final int SALESREP_ID_AD_Reference_ID = MReference.getReferenceID("AD_User - SalesRep");
/** Set Sales Representative.
Sales Representative or Company Agent */
public void setSalesRep_ID (int SalesRep_ID)
{
if (SalesRep_ID <= 0) set_Value ("SalesRep_ID", null);
 else 
set_Value ("SalesRep_ID", new Integer(SalesRep_ID));
}
/** Get Sales Representative.
Sales Representative or Company Agent */
public int getSalesRep_ID() 
{
Integer ii = (Integer)get_Value("SalesRep_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getValue());
}
}
