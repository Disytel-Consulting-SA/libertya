/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CashLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-08-24 20:18:53.634 */
public class X_C_CashLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CashLine (Properties ctx, int C_CashLine_ID, String trxName)
{
super (ctx, C_CashLine_ID, trxName);
/** if (C_CashLine_ID == 0)
{
setAmount (Env.ZERO);
setCashAmount (Env.ZERO);	// 0
setCashType (null);	// E
setC_Cash_ID (0);
setC_CashLine_ID (0);
setDocAction (null);	// CO
setDocStatus (null);	// DR
setIsAllocated (false);
setLine (0);	// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM C_CashLine WHERE C_Cash_ID=@C_Cash_ID@
setProcessed (false);
setUpdateBPBalance (true);	// Y
}
 */
}
/** Load Constructor */
public X_C_CashLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CashLine");

/** TableName=C_CashLine */
public static final String Table_Name="C_CashLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CashLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CashLine[").append(getID()).append("]");
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
/** Set Authorization Code */
public void setAuthCode (String AuthCode)
{
if (AuthCode != null && AuthCode.length() > 255)
{
log.warning("Length > 255 - truncated");
AuthCode = AuthCode.substring(0,255);
}
set_Value ("AuthCode", AuthCode);
}
/** Get Authorization Code */
public String getAuthCode() 
{
return (String)get_Value("AuthCode");
}
/** Set Authorization Matching */
public void setAuthMatch (boolean AuthMatch)
{
set_Value ("AuthMatch", new Boolean(AuthMatch));
}
/** Get Authorization Matching */
public boolean isAuthMatch() 
{
Object oo = get_Value("AuthMatch");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Cash Amount.
Cash Amount */
public void setCashAmount (BigDecimal CashAmount)
{
if (CashAmount == null) throw new IllegalArgumentException ("CashAmount is mandatory");
set_Value ("CashAmount", CashAmount);
}
/** Get Cash Amount.
Cash Amount */
public BigDecimal getCashAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("CashAmount");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int CASHTYPE_AD_Reference_ID = MReference.getReferenceID("C_Cash Trx Type");
/** Payment on account = P */
public static final String CASHTYPE_PaymentOnAccount = "P";
/** Bank Account Transfer = T */
public static final String CASHTYPE_BankAccountTransfer = "T";
/** Invoice = I */
public static final String CASHTYPE_Invoice = "I";
/** General Expense = E */
public static final String CASHTYPE_GeneralExpense = "E";
/** General Receipts = R */
public static final String CASHTYPE_GeneralReceipts = "R";
/** Charge = C */
public static final String CASHTYPE_Charge = "C";
/** Difference = D */
public static final String CASHTYPE_Difference = "D";
/** Cash Transfer = X */
public static final String CASHTYPE_CashTransfer = "X";
/** Set Cash Type.
Source of Cash */
public void setCashType (String CashType)
{
if (CashType.equals("P") || CashType.equals("T") || CashType.equals("I") || CashType.equals("E") || CashType.equals("R") || CashType.equals("C") || CashType.equals("D") || CashType.equals("X"));
 else throw new IllegalArgumentException ("CashType Invalid value - Reference = CASHTYPE_AD_Reference_ID - P - T - I - E - R - C - D - X");
if (CashType == null) throw new IllegalArgumentException ("CashType is mandatory");
if (CashType.length() > 1)
{
log.warning("Length > 1 - truncated");
CashType = CashType.substring(0,1);
}
set_ValueNoCheck ("CashType", CashType);
}
/** Get Cash Type.
Source of Cash */
public String getCashType() 
{
return (String)get_Value("CashType");
}
/** Set Bank Account.
Account at the Bank */
public void setC_BankAccount_ID (int C_BankAccount_ID)
{
if (C_BankAccount_ID <= 0) set_Value ("C_BankAccount_ID", null);
 else 
set_Value ("C_BankAccount_ID", new Integer(C_BankAccount_ID));
}
/** Get Bank Account.
Account at the Bank */
public int getC_BankAccount_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccount_ID");
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
public static final int C_CASHCURRENCY_ID_AD_Reference_ID = MReference.getReferenceID("C_Currency");
/** Set Cash Currency.
Cash Currency */
public void setC_CashCurrency_ID (int C_CashCurrency_ID)
{
if (C_CashCurrency_ID <= 0) set_Value ("C_CashCurrency_ID", null);
 else 
set_Value ("C_CashCurrency_ID", new Integer(C_CashCurrency_ID));
}
/** Get Cash Currency.
Cash Currency */
public int getC_CashCurrency_ID() 
{
Integer ii = (Integer)get_Value("C_CashCurrency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cash Journal.
Cash Journal */
public void setC_Cash_ID (int C_Cash_ID)
{
set_ValueNoCheck ("C_Cash_ID", new Integer(C_Cash_ID));
}
/** Get Cash Journal.
Cash Journal */
public int getC_Cash_ID() 
{
Integer ii = (Integer)get_Value("C_Cash_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Cash_ID()));
}
/** Set Cash Journal Line.
Cash Journal Line */
public void setC_CashLine_ID (int C_CashLine_ID)
{
set_ValueNoCheck ("C_CashLine_ID", new Integer(C_CashLine_ID));
}
/** Get Cash Journal Line.
Cash Journal Line */
public int getC_CashLine_ID() 
{
Integer ii = (Integer)get_Value("C_CashLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Charge.
Additional document charges */
public void setC_Charge_ID (int C_Charge_ID)
{
if (C_Charge_ID <= 0) set_Value ("C_Charge_ID", null);
 else 
set_Value ("C_Charge_ID", new Integer(C_Charge_ID));
}
/** Get Charge.
Additional document charges */
public int getC_Charge_ID() 
{
Integer ii = (Integer)get_Value("C_Charge_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
if (C_Currency_ID <= 0) set_ValueNoCheck ("C_Currency_ID", null);
 else 
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
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_ValueNoCheck ("C_Invoice_ID", null);
 else 
set_ValueNoCheck ("C_Invoice_ID", new Integer(C_Invoice_ID));
}
/** Get Invoice.
Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
if (C_Payment_ID <= 0) set_Value ("C_Payment_ID", null);
 else 
set_Value ("C_Payment_ID", new Integer(C_Payment_ID));
}
/** Get Payment.
Payment identifier */
public int getC_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Payment_ID");
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
/** Set POS Payment Medium.
POS Terminal Payment Medium */
public void setC_POSPaymentMedium_ID (int C_POSPaymentMedium_ID)
{
if (C_POSPaymentMedium_ID <= 0) set_Value ("C_POSPaymentMedium_ID", null);
 else 
set_Value ("C_POSPaymentMedium_ID", new Integer(C_POSPaymentMedium_ID));
}
/** Get POS Payment Medium.
POS Terminal Payment Medium */
public int getC_POSPaymentMedium_ID() 
{
Integer ii = (Integer)get_Value("C_POSPaymentMedium_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
if (C_Project_ID <= 0) set_Value ("C_Project_ID", null);
 else 
set_Value ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
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
/** Set Discount Amount.
Calculated amount of discount */
public void setDiscountAmt (BigDecimal DiscountAmt)
{
set_Value ("DiscountAmt", DiscountAmt);
}
/** Get Discount Amount.
Calculated amount of discount */
public BigDecimal getDiscountAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("DiscountAmt");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Allocated.
Indicates if the payment has been allocated */
public void setIsAllocated (boolean IsAllocated)
{
set_Value ("IsAllocated", new Boolean(IsAllocated));
}
/** Get Allocated.
Indicates if the payment has been allocated */
public boolean isAllocated() 
{
Object oo = get_Value("IsAllocated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Generated.
This Line is generated */
public void setIsGenerated (boolean IsGenerated)
{
set_ValueNoCheck ("IsGenerated", new Boolean(IsGenerated));
}
/** Get Generated.
This Line is generated */
public boolean isGenerated() 
{
Object oo = get_Value("IsGenerated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Line No.
Unique line for this document */
public void setLine (int Line)
{
set_Value ("Line", new Integer(Line));
}
/** Get Line No.
Unique line for this document */
public int getLine() 
{
Integer ii = (Integer)get_Value("Line");
if (ii == null) return 0;
return ii.intValue();
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
public static final int TRANSFERCASH_ID_AD_Reference_ID = MReference.getReferenceID("C_Cash");
/** Set Transfer Cash.
Transfer Target Cash */
public void setTransferCash_ID (int TransferCash_ID)
{
if (TransferCash_ID <= 0) set_Value ("TransferCash_ID", null);
 else 
set_Value ("TransferCash_ID", new Integer(TransferCash_ID));
}
/** Get Transfer Cash.
Transfer Target Cash */
public int getTransferCash_ID() 
{
Integer ii = (Integer)get_Value("TransferCash_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int TRANSFERCASHLINE_ID_AD_Reference_ID = MReference.getReferenceID("C_CashLine");
/** Set Transfer CashLine.
Transfer CashLine */
public void setTransferCashLine_ID (int TransferCashLine_ID)
{
if (TransferCashLine_ID <= 0) set_Value ("TransferCashLine_ID", null);
 else 
set_Value ("TransferCashLine_ID", new Integer(TransferCashLine_ID));
}
/** Get Transfer CashLine.
Transfer CashLine */
public int getTransferCashLine_ID() 
{
Integer ii = (Integer)get_Value("TransferCashLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Update Business Partner Balance */
public void setUpdateBPBalance (boolean UpdateBPBalance)
{
set_Value ("UpdateBPBalance", new Boolean(UpdateBPBalance));
}
/** Get Update Business Partner Balance */
public boolean isUpdateBPBalance() 
{
Object oo = get_Value("UpdateBPBalance");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set whiteoffamt */
public void setwhiteoffamt (BigDecimal whiteoffamt)
{
set_Value ("whiteoffamt", whiteoffamt);
}
/** Get whiteoffamt */
public BigDecimal getwhiteoffamt() 
{
BigDecimal bd = (BigDecimal)get_Value("whiteoffamt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Write-off Amount.
Amount to write-off */
public void setWriteOffAmt (BigDecimal WriteOffAmt)
{
set_Value ("WriteOffAmt", WriteOffAmt);
}
/** Get Write-off Amount.
Amount to write-off */
public BigDecimal getWriteOffAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("WriteOffAmt");
if (bd == null) return Env.ZERO;
return bd;
}
}
