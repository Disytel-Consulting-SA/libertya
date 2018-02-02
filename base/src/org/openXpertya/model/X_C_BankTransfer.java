/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BankTransfer
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2018-01-30 10:24:40.977 */
public class X_C_BankTransfer extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BankTransfer (Properties ctx, int C_BankTransfer_ID, String trxName)
{
super (ctx, C_BankTransfer_ID, trxName);
/** if (C_BankTransfer_ID == 0)
{
setammount_from (Env.ZERO);
setammount_to (Env.ZERO);
setC_bankaccount_from_ID (0);
setC_bankaccount_to_ID (0);
setC_banktransfer_ID (0);
setC_BPartner_ID (0);
setC_currency_from_ID (0);
setC_currency_to_ID (0);
setDateTrx (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDocAction (null);	// CO
setDocStatus (null);	// DR
setPosted (false);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_C_BankTransfer (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BankTransfer");

/** TableName=C_BankTransfer */
public static final String Table_Name="C_BankTransfer";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BankTransfer");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BankTransfer[").append(getID()).append("]");
return sb.toString();
}
public static final int ACCOUNTING_C_CHARGE_ID_AD_Reference_ID = MReference.getReferenceID("C_Charge");
/** Set ACCOUNTING_C_Charge_ID */
public void setACCOUNTING_C_Charge_ID (int ACCOUNTING_C_Charge_ID)
{
if (ACCOUNTING_C_Charge_ID <= 0) set_Value ("ACCOUNTING_C_Charge_ID", null);
 else 
set_Value ("ACCOUNTING_C_Charge_ID", new Integer(ACCOUNTING_C_Charge_ID));
}
/** Get ACCOUNTING_C_Charge_ID */
public int getACCOUNTING_C_Charge_ID() 
{
Integer ii = (Integer)get_Value("ACCOUNTING_C_Charge_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set ammount_from */
public void setammount_from (BigDecimal ammount_from)
{
if (ammount_from == null) throw new IllegalArgumentException ("ammount_from is mandatory");
set_Value ("ammount_from", ammount_from);
}
/** Get ammount_from */
public BigDecimal getammount_from() 
{
BigDecimal bd = (BigDecimal)get_Value("ammount_from");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set ammount_to */
public void setammount_to (BigDecimal ammount_to)
{
if (ammount_to == null) throw new IllegalArgumentException ("ammount_to is mandatory");
set_Value ("ammount_to", ammount_to);
}
/** Get ammount_to */
public BigDecimal getammount_to() 
{
BigDecimal bd = (BigDecimal)get_Value("ammount_to");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int C_BANKACCOUNT_FROM_ID_AD_Reference_ID = MReference.getReferenceID("C_BankAccount");
/** Set Source Bank Account  */
public void setC_bankaccount_from_ID (int C_bankaccount_from_ID)
{
set_Value ("C_bankaccount_from_ID", new Integer(C_bankaccount_from_ID));
}
/** Get Source Bank Account  */
public int getC_bankaccount_from_ID() 
{
Integer ii = (Integer)get_Value("C_bankaccount_from_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_BANKACCOUNT_TO_ID_AD_Reference_ID = MReference.getReferenceID("C_BankAccount");
/** Set Target Bank Account */
public void setC_bankaccount_to_ID (int C_bankaccount_to_ID)
{
set_Value ("C_bankaccount_to_ID", new Integer(C_bankaccount_to_ID));
}
/** Get Target Bank Account */
public int getC_bankaccount_to_ID() 
{
Integer ii = (Integer)get_Value("C_bankaccount_to_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Transfer */
public void setC_banktransfer_ID (int C_banktransfer_ID)
{
set_ValueNoCheck ("C_banktransfer_ID", new Integer(C_banktransfer_ID));
}
/** Get Bank Transfer */
public int getC_banktransfer_ID() 
{
Integer ii = (Integer)get_Value("C_banktransfer_ID");
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
public static final int C_CURRENCY_FROM_ID_AD_Reference_ID = MReference.getReferenceID("C_Currencies");
/** Set Source Bank Account Currency */
public void setC_currency_from_ID (int C_currency_from_ID)
{
set_Value ("C_currency_from_ID", new Integer(C_currency_from_ID));
}
/** Get Source Bank Account Currency */
public int getC_currency_from_ID() 
{
Integer ii = (Integer)get_Value("C_currency_from_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_CURRENCY_TO_ID_AD_Reference_ID = MReference.getReferenceID("C_Currencies");
/** Set Target Bank Account Currency */
public void setC_currency_to_ID (int C_currency_to_ID)
{
set_Value ("C_currency_to_ID", new Integer(C_currency_to_ID));
}
/** Get Target Bank Account Currency */
public int getC_currency_to_ID() 
{
Integer ii = (Integer)get_Value("C_currency_to_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
if (C_DocType_ID <= 0) set_Value ("C_DocType_ID", null);
 else 
set_Value ("C_DocType_ID", new Integer(C_DocType_ID));
}
/** Get Document Type.
Document type or rules */
public int getC_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set charge_amt_from */
public void setcharge_amt_from (BigDecimal charge_amt_from)
{
set_Value ("charge_amt_from", charge_amt_from);
}
/** Get charge_amt_from */
public BigDecimal getcharge_amt_from() 
{
BigDecimal bd = (BigDecimal)get_Value("charge_amt_from");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int CHARGE_FROM_ID_AD_Reference_ID = MReference.getReferenceID("C_Charge");
/** Set charge_from_id */
public void setcharge_from_ID (int charge_from_ID)
{
if (charge_from_ID <= 0) set_Value ("charge_from_ID", null);
 else 
set_Value ("charge_from_ID", new Integer(charge_from_ID));
}
/** Get charge_from_id */
public int getcharge_from_ID() 
{
Integer ii = (Integer)get_Value("charge_from_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CHARGE_TO_ID_AD_Reference_ID = MReference.getReferenceID("C_Charge");
/** Set charge_to_id */
public void setcharge_to_ID (int charge_to_ID)
{
if (charge_to_ID <= 0) set_Value ("charge_to_ID", null);
 else 
set_Value ("charge_to_ID", new Integer(charge_to_ID));
}
/** Get charge_to_id */
public int getcharge_to_ID() 
{
Integer ii = (Integer)get_Value("charge_to_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_PAYMENT_FROM_ID_AD_Reference_ID = MReference.getReferenceID("C_Payment");
/** Set c_payment_from_id */
public void setC_payment_from_ID (int C_payment_from_ID)
{
if (C_payment_from_ID <= 0) set_Value ("C_payment_from_ID", null);
 else 
set_Value ("C_payment_from_ID", new Integer(C_payment_from_ID));
}
/** Get c_payment_from_id */
public int getC_payment_from_ID() 
{
Integer ii = (Integer)get_Value("C_payment_from_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_PAYMENT_TO_ID_AD_Reference_ID = MReference.getReferenceID("C_Payment");
/** Set c_payment_to_id */
public void setC_payment_to_ID (int C_payment_to_ID)
{
if (C_payment_to_ID <= 0) set_Value ("C_payment_to_ID", null);
 else 
set_Value ("C_payment_to_ID", new Integer(C_payment_to_ID));
}
/** Get c_payment_to_id */
public int getC_payment_to_ID() 
{
Integer ii = (Integer)get_Value("C_payment_to_ID");
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
/** Set Transaction TIMESTAMP.
Transaction TIMESTAMP */
public void setDateTrx (Timestamp DateTrx)
{
if (DateTrx == null) throw new IllegalArgumentException ("DateTrx is mandatory");
set_Value ("DateTrx", DateTrx);
}
/** Get Transaction TIMESTAMP.
Transaction TIMESTAMP */
public Timestamp getDateTrx() 
{
return (Timestamp)get_Value("DateTrx");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 60)
{
log.warning("Length > 60 - truncated");
Description = Description.substring(0,60);
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
/** Set Reference No.
Your customer or vendor NUMERIC at the Business Partner's site */
public void setReferenceNo (BigDecimal ReferenceNo)
{
set_Value ("ReferenceNo", ReferenceNo);
}
/** Get Reference No.
Your customer or vendor NUMERIC at the Business Partner's site */
public BigDecimal getReferenceNo() 
{
BigDecimal bd = (BigDecimal)get_Value("ReferenceNo");
if (bd == null) return Env.ZERO;
return bd;
}
}
