/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_DebitCreditAllocation
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2020-04-23 15:35:10.085 */
public class X_C_DebitCreditAllocation extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_DebitCreditAllocation (Properties ctx, int C_DebitCreditAllocation_ID, String trxName)
{
super (ctx, C_DebitCreditAllocation_ID, trxName);
/** if (C_DebitCreditAllocation_ID == 0)
{
setAmount (Env.ZERO);
setC_BPartner_ID (0);
setC_Currency_ID (0);
setC_DebitCreditAllocation_ID (0);
setC_Invoice_Debit_ID (0);
setDocAction (null);	// CO
setDocStatus (null);	// DR
setDocumentNo (null);
setImputationDate (new Timestamp(System.currentTimeMillis()));	// @#Date@
setPendingDebitAmount (Env.ZERO);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_C_DebitCreditAllocation (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_DebitCreditAllocation");

/** TableName=C_DebitCreditAllocation */
public static final String Table_Name="C_DebitCreditAllocation";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_DebitCreditAllocation");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_DebitCreditAllocation[").append(getID()).append("]");
return sb.toString();
}
/** Set Amount.
Amount in a defined currency */
public void setAmount (BigDecimal Amount)
{
if (Amount == null) throw new IllegalArgumentException ("Amount is mandatory");
set_Value ("Amount", Amount);
}
/** Get Amount.
Amount in a defined currency */
public BigDecimal getAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("Amount");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int C_ALLOCATIONHDR_ANTICIPATED_ID_AD_Reference_ID = MReference.getReferenceID("C_Allocation");
/** Set Anticipated Allocation */
public void setC_AllocationHdr_Anticipated_ID (int C_AllocationHdr_Anticipated_ID)
{
if (C_AllocationHdr_Anticipated_ID <= 0) set_Value ("C_AllocationHdr_Anticipated_ID", null);
 else 
set_Value ("C_AllocationHdr_Anticipated_ID", new Integer(C_AllocationHdr_Anticipated_ID));
}
/** Get Anticipated Allocation */
public int getC_AllocationHdr_Anticipated_ID() 
{
Integer ii = (Integer)get_Value("C_AllocationHdr_Anticipated_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Allocation.
Payment allocation */
public void setC_AllocationHdr_ID (int C_AllocationHdr_ID)
{
if (C_AllocationHdr_ID <= 0) set_Value ("C_AllocationHdr_ID", null);
 else 
set_Value ("C_AllocationHdr_ID", new Integer(C_AllocationHdr_ID));
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
set_ValueNoCheck ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_DebitCreditAllocation_ID */
public void setC_DebitCreditAllocation_ID (int C_DebitCreditAllocation_ID)
{
set_ValueNoCheck ("C_DebitCreditAllocation_ID", new Integer(C_DebitCreditAllocation_ID));
}
/** Get C_DebitCreditAllocation_ID */
public int getC_DebitCreditAllocation_ID() 
{
Integer ii = (Integer)get_Value("C_DebitCreditAllocation_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_INVOICE_CREDIT_ID_AD_Reference_ID = MReference.getReferenceID("C_Invoice");
/** Set Credit Invoice */
public void setC_Invoice_Credit_ID (int C_Invoice_Credit_ID)
{
if (C_Invoice_Credit_ID <= 0) set_Value ("C_Invoice_Credit_ID", null);
 else 
set_Value ("C_Invoice_Credit_ID", new Integer(C_Invoice_Credit_ID));
}
/** Get Credit Invoice */
public int getC_Invoice_Credit_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_Credit_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_INVOICE_DEBIT_ID_AD_Reference_ID = MReference.getReferenceID("C_Invoice");
/** Set Debit Invoice */
public void setC_Invoice_Debit_ID (int C_Invoice_Debit_ID)
{
set_Value ("C_Invoice_Debit_ID", new Integer(C_Invoice_Debit_ID));
}
/** Get Debit Invoice */
public int getC_Invoice_Debit_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_Debit_ID");
if (ii == null) return 0;
return ii.intValue();
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
if (DocAction.equals("AP") || DocAction.equals("CL") || DocAction.equals("PR") || DocAction.equals("IN") || DocAction.equals("CO") || DocAction.equals("--") || DocAction.equals("RC") || DocAction.equals("RJ") || DocAction.equals("RA") || DocAction.equals("WC") || DocAction.equals("XL") || DocAction.equals("RE") || DocAction.equals("PO") || DocAction.equals("VO") || ( refContainsValue("CORE-AD_Reference-135", DocAction) ) );
 else throw new IllegalArgumentException ("DocAction Invalid value: " + DocAction + ".  Valid: " +  refValidOptions("CORE-AD_Reference-135") );
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
if (DocStatus.equals("VO") || DocStatus.equals("NA") || DocStatus.equals("IP") || DocStatus.equals("CO") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("WC") || DocStatus.equals("WP") || DocStatus.equals("??") || DocStatus.equals("DR") || DocStatus.equals("IN") || DocStatus.equals("RE") || ( refContainsValue("CORE-AD_Reference-131", DocStatus) ) );
 else throw new IllegalArgumentException ("DocStatus Invalid value: " + DocStatus + ".  Valid: " +  refValidOptions("CORE-AD_Reference-131") );
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
Document sequence NUMERIC of the document */
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
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDocumentNo());
}
/** Set Imputation Date */
public void setImputationDate (Timestamp ImputationDate)
{
if (ImputationDate == null) throw new IllegalArgumentException ("ImputationDate is mandatory");
set_Value ("ImputationDate", ImputationDate);
}
/** Get Imputation Date */
public Timestamp getImputationDate() 
{
return (Timestamp)get_Value("ImputationDate");
}
/** Set Pending Debit Amount */
public void setPendingDebitAmount (BigDecimal PendingDebitAmount)
{
if (PendingDebitAmount == null) throw new IllegalArgumentException ("PendingDebitAmount is mandatory");
set_Value ("PendingDebitAmount", PendingDebitAmount);
}
/** Get Pending Debit Amount */
public BigDecimal getPendingDebitAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("PendingDebitAmount");
if (bd == null) return Env.ZERO;
return bd;
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
}
