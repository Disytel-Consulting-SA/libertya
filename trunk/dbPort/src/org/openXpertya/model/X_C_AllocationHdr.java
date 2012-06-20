/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_AllocationHdr
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-01-24 17:44:43.59 */
public class X_C_AllocationHdr extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_AllocationHdr (Properties ctx, int C_AllocationHdr_ID, String trxName)
{
super (ctx, C_AllocationHdr_ID, trxName);
/** if (C_AllocationHdr_ID == 0)
{
setAllocationType (null);
setApprovalAmt (Env.ZERO);
setC_AllocationHdr_ID (0);
setC_Currency_ID (0);
setDateAcct (new Timestamp(System.currentTimeMillis()));
setDateTrx (new Timestamp(System.currentTimeMillis()));
setDocAction (null);	// CO
setDocStatus (null);	// DR
setDocumentNo (null);
setGrandTotal (Env.ZERO);
setIsApproved (false);
setIsManual (false);
setPosted (false);
setProcessed (false);
setRetencion_Amt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_AllocationHdr (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_AllocationHdr");

/** TableName=C_AllocationHdr */
public static final String Table_Name="C_AllocationHdr";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_AllocationHdr");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_AllocationHdr[").append(getID()).append("]");
return sb.toString();
}
/** Set Action Detail */
public void setActionDetail (String ActionDetail)
{
if (ActionDetail != null && ActionDetail.length() > 2000)
{
log.warning("Length > 2000 - truncated");
ActionDetail = ActionDetail.substring(0,2000);
}
set_ValueNoCheck ("ActionDetail", ActionDetail);
}
/** Get Action Detail */
public String getActionDetail() 
{
return (String)get_Value("ActionDetail");
}
public static final int ALLOCATIONACTION_AD_Reference_ID = MReference.getReferenceID("Allocation_DocumentAction ");
/** Revert Allocation = RX */
public static final String ALLOCATIONACTION_RevertAllocation = "RX";
/** Void Payments = VP */
public static final String ALLOCATIONACTION_VoidPayments = "VP";
/** Void Payments & Retentions = VR */
public static final String ALLOCATIONACTION_VoidPaymentsRetentions = "VR";
/** Set Allocation Specific Action */
public void setAllocationAction (String AllocationAction)
{
if (AllocationAction == null || AllocationAction.equals("RX") || AllocationAction.equals("VP") || AllocationAction.equals("VR"));
 else throw new IllegalArgumentException ("AllocationAction Invalid value - Reference = ALLOCATIONACTION_AD_Reference_ID - RX - VP - VR");
if (AllocationAction != null && AllocationAction.length() > 2)
{
log.warning("Length > 2 - truncated");
AllocationAction = AllocationAction.substring(0,2);
}
set_Value ("AllocationAction", AllocationAction);
}
/** Get Allocation Specific Action */
public String getAllocationAction() 
{
return (String)get_Value("AllocationAction");
}
public static final int ALLOCATIONTYPE_AD_Reference_ID = MReference.getReferenceID("AllocationType");
/** Customer Receipt = RC */
public static final String ALLOCATIONTYPE_CustomerReceipt = "RC";
/** Advanced Customer Receipt = RCA */
public static final String ALLOCATIONTYPE_AdvancedCustomerReceipt = "RCA";
/** Payment Order = OP */
public static final String ALLOCATIONTYPE_PaymentOrder = "OP";
/** Payment from Invoice = CPI */
public static final String ALLOCATIONTYPE_PaymentFromInvoice = "CPI";
/** Advanced Payment Order = OPA */
public static final String ALLOCATIONTYPE_AdvancedPaymentOrder = "OPA";
/** Sales Transaction = STX */
public static final String ALLOCATIONTYPE_SalesTransaction = "STX";
/** Manual = MAN */
public static final String ALLOCATIONTYPE_Manual = "MAN";
/** Set AllocationType */
public void setAllocationType (String AllocationType)
{
if (AllocationType.equals("RC") || AllocationType.equals("RCA") || AllocationType.equals("OP") || AllocationType.equals("CPI") || AllocationType.equals("OPA") || AllocationType.equals("STX") || AllocationType.equals("MAN"));
 else throw new IllegalArgumentException ("AllocationType Invalid value - Reference = ALLOCATIONTYPE_AD_Reference_ID - RC - RCA - OP - CPI - OPA - STX - MAN");
if (AllocationType == null) throw new IllegalArgumentException ("AllocationType is mandatory");
if (AllocationType.length() > 50)
{
log.warning("Length > 50 - truncated");
AllocationType = AllocationType.substring(0,50);
}
set_Value ("AllocationType", AllocationType);
}
/** Get AllocationType */
public String getAllocationType() 
{
return (String)get_Value("AllocationType");
}
/** Set Approval Amount.
Document Approval Amount */
public void setApprovalAmt (BigDecimal ApprovalAmt)
{
if (ApprovalAmt == null) throw new IllegalArgumentException ("ApprovalAmt is mandatory");
set_Value ("ApprovalAmt", ApprovalAmt);
}
/** Get Approval Amount.
Document Approval Amount */
public BigDecimal getApprovalAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ApprovalAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Allocation.
Payment allocation */
public void setC_AllocationHdr_ID (int C_AllocationHdr_ID)
{
set_ValueNoCheck ("C_AllocationHdr_ID", new Integer(C_AllocationHdr_ID));
}
/** Get Allocation.
Payment allocation */
public int getC_AllocationHdr_ID() 
{
Integer ii = (Integer)get_Value("C_AllocationHdr_ID");
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
/** Set POS Journal.
POS Journal */
public void setC_POSJournal_ID (int C_POSJournal_ID)
{
if (C_POSJournal_ID <= 0) set_Value ("C_POSJournal_ID", null);
 else 
set_Value ("C_POSJournal_ID", new Integer(C_POSJournal_ID));
}
/** Get POS Journal.
POS Journal */
public int getC_POSJournal_ID() 
{
Integer ii = (Integer)get_Value("C_POSJournal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Account Date.
Accounting Date */
public void setDateAcct (Timestamp DateAcct)
{
if (DateAcct == null) throw new IllegalArgumentException ("DateAcct is mandatory");
set_Value ("DateAcct", DateAcct);
}
/** Get Account Date.
Accounting Date */
public Timestamp getDateAcct() 
{
return (Timestamp)get_Value("DateAcct");
}
/** Set Transaction Date.
Transaction Date */
public void setDateTrx (Timestamp DateTrx)
{
if (DateTrx == null) throw new IllegalArgumentException ("DateTrx is mandatory");
set_Value ("DateTrx", DateTrx);
}
/** Get Transaction Date.
Transaction Date */
public Timestamp getDateTrx() 
{
return (Timestamp)get_Value("DateTrx");
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
public static final int DOCACTION_AD_Reference_ID = MReference.getReferenceID("_Document Action");
/** Approve = AP */
public static final String DOCACTION_Approve = "AP";
/** Close = CL */
public static final String DOCACTION_Close = "CL";
/** Prepare = PR */
public static final String DOCACTION_Prepare = "PR";
/** Invalidate = IN */
public static final String DOCACTION_Invalidate = "IN";
/** Complete = CO */
public static final String DOCACTION_Complete = "CO";
/** <None> = -- */
public static final String DOCACTION_None = "--";
/** Reverse - Correct = RC */
public static final String DOCACTION_Reverse_Correct = "RC";
/** Reject = RJ */
public static final String DOCACTION_Reject = "RJ";
/** Reverse - Accrual = RA */
public static final String DOCACTION_Reverse_Accrual = "RA";
/** Wait Complete = WC */
public static final String DOCACTION_WaitComplete = "WC";
/** Unlock = XL */
public static final String DOCACTION_Unlock = "XL";
/** Re-activate = RE */
public static final String DOCACTION_Re_Activate = "RE";
/** Post = PO */
public static final String DOCACTION_Post = "PO";
/** Void = VO */
public static final String DOCACTION_Void = "VO";
/** Set Document Action.
The targeted status of the document */
public void setDocAction (String DocAction)
{
if (DocAction.equals("AP") || DocAction.equals("CL") || DocAction.equals("PR") || DocAction.equals("IN") || DocAction.equals("CO") || DocAction.equals("--") || DocAction.equals("RC") || DocAction.equals("RJ") || DocAction.equals("RA") || DocAction.equals("WC") || DocAction.equals("XL") || DocAction.equals("RE") || DocAction.equals("PO") || DocAction.equals("VO"));
 else throw new IllegalArgumentException ("DocAction Invalid value - Reference = DOCACTION_AD_Reference_ID - AP - CL - PR - IN - CO - -- - RC - RJ - RA - WC - XL - RE - PO - VO");
if (DocAction == null) throw new IllegalArgumentException ("DocAction is mandatory");
if (DocAction.length() > 2)
{
log.warning("Length > 2 - truncated");
DocAction = DocAction.substring(0,2);
}
set_Value ("DocAction", DocAction);
}
/** Get Document Action.
The targeted status of the document */
public String getDocAction() 
{
return (String)get_Value("DocAction");
}
public static final int DOCSTATUS_AD_Reference_ID = MReference.getReferenceID("_Document Status");
/** Voided = VO */
public static final String DOCSTATUS_Voided = "VO";
/** Not Approved = NA */
public static final String DOCSTATUS_NotApproved = "NA";
/** In Progress = IP */
public static final String DOCSTATUS_InProgress = "IP";
/** Completed = CO */
public static final String DOCSTATUS_Completed = "CO";
/** Approved = AP */
public static final String DOCSTATUS_Approved = "AP";
/** Closed = CL */
public static final String DOCSTATUS_Closed = "CL";
/** Waiting Confirmation = WC */
public static final String DOCSTATUS_WaitingConfirmation = "WC";
/** Waiting Payment = WP */
public static final String DOCSTATUS_WaitingPayment = "WP";
/** Unknown = ?? */
public static final String DOCSTATUS_Unknown = "??";
/** Drafted = DR */
public static final String DOCSTATUS_Drafted = "DR";
/** Invalid = IN */
public static final String DOCSTATUS_Invalid = "IN";
/** Reversed = RE */
public static final String DOCSTATUS_Reversed = "RE";
/** Set Document Status.
The current status of the document */
public void setDocStatus (String DocStatus)
{
if (DocStatus.equals("VO") || DocStatus.equals("NA") || DocStatus.equals("IP") || DocStatus.equals("CO") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("WC") || DocStatus.equals("WP") || DocStatus.equals("??") || DocStatus.equals("DR") || DocStatus.equals("IN") || DocStatus.equals("RE"));
 else throw new IllegalArgumentException ("DocStatus Invalid value - Reference = DOCSTATUS_AD_Reference_ID - VO - NA - IP - CO - AP - CL - WC - WP - ?? - DR - IN - RE");
if (DocStatus == null) throw new IllegalArgumentException ("DocStatus is mandatory");
if (DocStatus.length() > 2)
{
log.warning("Length > 2 - truncated");
DocStatus = DocStatus.substring(0,2);
}
set_Value ("DocStatus", DocStatus);
}
/** Get Document Status.
The current status of the document */
public String getDocStatus() 
{
return (String)get_Value("DocStatus");
}
/** Set Document No.
Document sequence number of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo == null) throw new IllegalArgumentException ("DocumentNo is mandatory");
if (DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,30);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence number of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDocumentNo());
}
/** Set Grand Total.
Total amount of document */
public void setGrandTotal (BigDecimal GrandTotal)
{
if (GrandTotal == null) throw new IllegalArgumentException ("GrandTotal is mandatory");
set_Value ("GrandTotal", GrandTotal);
}
/** Get Grand Total.
Total amount of document */
public BigDecimal getGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("GrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Approved.
Indicates if this document requires approval */
public void setIsApproved (boolean IsApproved)
{
set_Value ("IsApproved", new Boolean(IsApproved));
}
/** Get Approved.
Indicates if this document requires approval */
public boolean isApproved() 
{
Object oo = get_Value("IsApproved");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Manual.
This is a manual process */
public void setIsManual (boolean IsManual)
{
set_Value ("IsManual", new Boolean(IsManual));
}
/** Get Manual.
This is a manual process */
public boolean isManual() 
{
Object oo = get_Value("IsManual");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Posted.
Posting status */
public void setPosted (boolean Posted)
{
set_Value ("Posted", new Boolean(Posted));
}
/** Get Posted.
Posting status */
public boolean isPosted() 
{
Object oo = get_Value("Posted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Retencion_Amt */
public void setRetencion_Amt (BigDecimal Retencion_Amt)
{
if (Retencion_Amt == null) throw new IllegalArgumentException ("Retencion_Amt is mandatory");
set_Value ("Retencion_Amt", Retencion_Amt);
}
/** Get Retencion_Amt */
public BigDecimal getRetencion_Amt() 
{
BigDecimal bd = (BigDecimal)get_Value("Retencion_Amt");
if (bd == null) return Env.ZERO;
return bd;
}
}
