/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Cash
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-28 17:03:08.431 */
public class X_C_Cash extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Cash (Properties ctx, int C_Cash_ID, String trxName)
{
super (ctx, C_Cash_ID, trxName);
/** if (C_Cash_ID == 0)
{
setBeginningBalance (Env.ZERO);
setC_CashBook_ID (0);
setC_Cash_ID (0);
setDateAcct (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDocAction (null);	// CO
setDocStatus (null);	// DR
setEndingBalance (Env.ZERO);
setIsApproved (false);
setName (null);	// @#Date@
setPosted (false);	// N
setProcessed (false);
setStatementDate (new Timestamp(System.currentTimeMillis()));	// @#Date@
setValidatePOSJournal (false);
}
 */
}
/** Load Constructor */
public X_C_Cash (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Cash");

/** TableName=C_Cash */
public static final String Table_Name="C_Cash";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Cash");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Cash[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_ORGTRX_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (Trx)");
/** Set Trx Organization.
Performing or initiating organization */
public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
{
if (AD_OrgTrx_ID <= 0) set_Value ("AD_OrgTrx_ID", null);
 else 
set_Value ("AD_OrgTrx_ID", new Integer(AD_OrgTrx_ID));
}
/** Get Trx Organization.
Performing or initiating organization */
public int getAD_OrgTrx_ID() 
{
Integer ii = (Integer)get_Value("AD_OrgTrx_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Beginning Balance.
Balance prior to any transactions */
public void setBeginningBalance (BigDecimal BeginningBalance)
{
if (BeginningBalance == null) throw new IllegalArgumentException ("BeginningBalance is mandatory");
set_Value ("BeginningBalance", BeginningBalance);
}
/** Get Beginning Balance.
Balance prior to any transactions */
public BigDecimal getBeginningBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("BeginningBalance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Activity.
Business Activity */
public void setC_Activity_ID (int C_Activity_ID)
{
if (C_Activity_ID <= 0) set_Value ("C_Activity_ID", null);
 else 
set_Value ("C_Activity_ID", new Integer(C_Activity_ID));
}
/** Get Activity.
Business Activity */
public int getC_Activity_ID() 
{
Integer ii = (Integer)get_Value("C_Activity_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CASHBOOKTYPE_AD_Reference_ID = MReference.getReferenceID("C_CashBook Type");
/** General Cash Book = G */
public static final String CASHBOOKTYPE_GeneralCashBook = "G";
/** Journal Cash Book = J */
public static final String CASHBOOKTYPE_JournalCashBook = "J";
/** Set Cash Book Type */
public void setCashBookType (String CashBookType)
{
if (CashBookType == null || CashBookType.equals("G") || CashBookType.equals("J"));
 else throw new IllegalArgumentException ("CashBookType Invalid value - Reference = CASHBOOKTYPE_AD_Reference_ID - G - J");
throw new IllegalArgumentException ("CashBookType is virtual column");
}
/** Get Cash Book Type */
public String getCashBookType() 
{
return (String)get_Value("CashBookType");
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
/** Set Cash Book.
Cash Book for recording petty cash transactions */
public void setC_CashBook_ID (int C_CashBook_ID)
{
set_ValueNoCheck ("C_CashBook_ID", new Integer(C_CashBook_ID));
}
/** Get Cash Book.
Cash Book for recording petty cash transactions */
public int getC_CashBook_ID() 
{
Integer ii = (Integer)get_Value("C_CashBook_ID");
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
/** Set Ending balance.
Ending  or closing balance */
public void setEndingBalance (BigDecimal EndingBalance)
{
if (EndingBalance == null) throw new IllegalArgumentException ("EndingBalance is mandatory");
set_Value ("EndingBalance", EndingBalance);
}
/** Get Ending balance.
Ending  or closing balance */
public BigDecimal getEndingBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("EndingBalance");
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
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
/** Set Statement date.
Date of the statement */
public void setStatementDate (Timestamp StatementDate)
{
if (StatementDate == null) throw new IllegalArgumentException ("StatementDate is mandatory");
set_Value ("StatementDate", StatementDate);
}
/** Get Statement date.
Date of the statement */
public Timestamp getStatementDate() 
{
return (Timestamp)get_Value("StatementDate");
}
/** Set Statement difference.
Difference between statement ending balance and actual ending balance */
public void setStatementDifference (BigDecimal StatementDifference)
{
set_Value ("StatementDifference", StatementDifference);
}
/** Get Statement difference.
Difference between statement ending balance and actual ending balance */
public BigDecimal getStatementDifference() 
{
BigDecimal bd = (BigDecimal)get_Value("StatementDifference");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int USER1_ID_AD_Reference_ID = MReference.getReferenceID("Account_ID - User1");
/** Set User1.
User defined element #1 */
public void setUser1_ID (int User1_ID)
{
if (User1_ID <= 0) set_Value ("User1_ID", null);
 else 
set_Value ("User1_ID", new Integer(User1_ID));
}
/** Get User1.
User defined element #1 */
public int getUser1_ID() 
{
Integer ii = (Integer)get_Value("User1_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int USER2_ID_AD_Reference_ID = MReference.getReferenceID("Account_ID - User2");
/** Set User2.
User defined element #2 */
public void setUser2_ID (int User2_ID)
{
if (User2_ID <= 0) set_Value ("User2_ID", null);
 else 
set_Value ("User2_ID", new Integer(User2_ID));
}
/** Get User2.
User defined element #2 */
public int getUser2_ID() 
{
Integer ii = (Integer)get_Value("User2_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Validate POS Journal */
public void setValidatePOSJournal (boolean ValidatePOSJournal)
{
set_Value ("ValidatePOSJournal", new Boolean(ValidatePOSJournal));
}
/** Get Validate POS Journal */
public boolean isValidatePOSJournal() 
{
Object oo = get_Value("ValidatePOSJournal");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
