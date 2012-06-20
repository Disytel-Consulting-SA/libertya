/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_BankBalances
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-09-01 12:51:58.928 */
public class X_T_BankBalances extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_BankBalances (Properties ctx, int T_BankBalances_ID, String trxName)
{
super (ctx, T_BankBalances_ID, trxName);
/** if (T_BankBalances_ID == 0)
{
setAD_PInstance_ID (0);
setC_BankAccount_ID (0);
setDateTrx (new Timestamp(System.currentTimeMillis()));
setDocStatus (null);
setDueDate (new Timestamp(System.currentTimeMillis()));
setT_BankBalances_ID (0);
}
 */
}
/** Load Constructor */
public X_T_BankBalances (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_BankBalances");

/** TableName=T_BankBalances */
public static final String Table_Name="T_BankBalances";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_BankBalances");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_BankBalances[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_Value ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Balance */
public void setBalance (BigDecimal Balance)
{
set_Value ("Balance", Balance);
}
/** Get Balance */
public BigDecimal getBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("Balance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Bank Account.
Account at the Bank */
public void setC_BankAccount_ID (int C_BankAccount_ID)
{
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
/** Set Common Date */
public void setCommonDate (Timestamp CommonDate)
{
set_Value ("CommonDate", CommonDate);
}
/** Get Common Date */
public Timestamp getCommonDate() 
{
return (Timestamp)get_Value("CommonDate");
}
/** Set Credit */
public void setCredit (BigDecimal Credit)
{
set_Value ("Credit", Credit);
}
/** Get Credit */
public BigDecimal getCredit() 
{
BigDecimal bd = (BigDecimal)get_Value("Credit");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int DATEORDER_AD_Reference_ID = MReference.getReferenceID("Date Order");
/** Due = D */
public static final String DATEORDER_Due = "D";
/** Transaction = T */
public static final String DATEORDER_Transaction = "T";
/** Set Date Order */
public void setDateOrder (String DateOrder)
{
if (DateOrder == null || DateOrder.equals("D") || DateOrder.equals("T"));
 else throw new IllegalArgumentException ("DateOrder Invalid value - Reference = DATEORDER_AD_Reference_ID - D - T");
if (DateOrder != null && DateOrder.length() > 1)
{
log.warning("Length > 1 - truncated");
DateOrder = DateOrder.substring(0,1);
}
set_Value ("DateOrder", DateOrder);
}
/** Get Date Order */
public String getDateOrder() 
{
return (String)get_Value("DateOrder");
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
/** Set Debit */
public void setDebit (BigDecimal Debit)
{
set_Value ("Debit", Debit);
}
/** Get Debit */
public BigDecimal getDebit() 
{
BigDecimal bd = (BigDecimal)get_Value("Debit");
if (bd == null) return Env.ZERO;
return bd;
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
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 255)
{
log.warning("Length > 255 - truncated");
DocumentNo = DocumentNo.substring(0,255);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set Document Type.
Document Type */
public void setDocumentType (String DocumentType)
{
if (DocumentType != null && DocumentType.length() > 255)
{
log.warning("Length > 255 - truncated");
DocumentType = DocumentType.substring(0,255);
}
set_Value ("DocumentType", DocumentType);
}
/** Get Document Type.
Document Type */
public String getDocumentType() 
{
return (String)get_Value("DocumentType");
}
/** Set Due Date.
Date when the payment is due */
public void setDueDate (Timestamp DueDate)
{
if (DueDate == null) throw new IllegalArgumentException ("DueDate is mandatory");
set_Value ("DueDate", DueDate);
}
/** Get Due Date.
Date when the payment is due */
public Timestamp getDueDate() 
{
return (Timestamp)get_Value("DueDate");
}
/** Set Reconciled.
Payment is reconciled with bank statement */
public void setIsReconciled (boolean IsReconciled)
{
set_Value ("IsReconciled", new Boolean(IsReconciled));
}
/** Get Reconciled.
Payment is reconciled with bank statement */
public boolean isReconciled() 
{
Object oo = get_Value("IsReconciled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Bank Balances */
public void setT_BankBalances_ID (int T_BankBalances_ID)
{
set_ValueNoCheck ("T_BankBalances_ID", new Integer(T_BankBalances_ID));
}
/** Get Bank Balances */
public int getT_BankBalances_ID() 
{
Integer ii = (Integer)get_Value("T_BankBalances_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
