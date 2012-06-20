/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_Allocation_Report
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-06-27 09:43:21.03 */
public class X_T_Allocation_Report extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_Allocation_Report (Properties ctx, int T_Allocation_Report_ID, String trxName)
{
super (ctx, T_Allocation_Report_ID, trxName);
/** if (T_Allocation_Report_ID == 0)
{
setAccountType (null);
setAD_OrgTrx_ID (0);
setAD_PInstance_ID (0);
setC_BPartner_ID (0);
setC_DocType_ID (0);
setDateTrx (new Timestamp(System.currentTimeMillis()));
setDocAllocationCategory (null);
setT_Allocation_Report_ID (0);
}
 */
}
/** Load Constructor */
public X_T_Allocation_Report (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_Allocation_Report");

/** TableName=T_Allocation_Report */
public static final String Table_Name="T_Allocation_Report";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_Allocation_Report");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_Allocation_Report[").append(getID()).append("]");
return sb.toString();
}
public static final int ACCOUNTTYPE_AD_Reference_ID = MReference.getReferenceID("Account type");
/** Customer = C */
public static final String ACCOUNTTYPE_Customer = "C";
/** Vendor = V */
public static final String ACCOUNTTYPE_Vendor = "V";
/** Set Account Type.
Indicates the type of account */
public void setAccountType (String AccountType)
{
if (AccountType.equals("C") || AccountType.equals("V"));
 else throw new IllegalArgumentException ("AccountType Invalid value - Reference = ACCOUNTTYPE_AD_Reference_ID - C - V");
if (AccountType == null) throw new IllegalArgumentException ("AccountType is mandatory");
if (AccountType.length() > 1)
{
log.warning("Length > 1 - truncated");
AccountType = AccountType.substring(0,1);
}
set_Value ("AccountType", AccountType);
}
/** Get Account Type.
Indicates the type of account */
public String getAccountType() 
{
return (String)get_Value("AccountType");
}
public static final int AD_ORGTRX_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (all)");
/** Set Trx Organization.
Performing or initiating organization */
public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
{
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
/** Set Cash Journal Line.
Cash Journal Line */
public void setC_CashLine_ID (int C_CashLine_ID)
{
if (C_CashLine_ID <= 0) set_Value ("C_CashLine_ID", null);
 else 
set_Value ("C_CashLine_ID", new Integer(C_CashLine_ID));
}
/** Get Cash Journal Line.
Cash Journal Line */
public int getC_CashLine_ID() 
{
Integer ii = (Integer)get_Value("C_CashLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
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
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_Value ("C_Invoice_ID", null);
 else 
set_Value ("C_Invoice_ID", new Integer(C_Invoice_ID));
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
/** Set Credit Document */
public void setCreditDocument (String CreditDocument)
{
if (CreditDocument != null && CreditDocument.length() > 255)
{
log.warning("Length > 255 - truncated");
CreditDocument = CreditDocument.substring(0,255);
}
set_Value ("CreditDocument", CreditDocument);
}
/** Get Credit Document */
public String getCreditDocument() 
{
return (String)get_Value("CreditDocument");
}
/** Set Credit Document No */
public void setCreditDocumentNo (String CreditDocumentNo)
{
if (CreditDocumentNo != null && CreditDocumentNo.length() > 255)
{
log.warning("Length > 255 - truncated");
CreditDocumentNo = CreditDocumentNo.substring(0,255);
}
set_Value ("CreditDocumentNo", CreditDocumentNo);
}
/** Get Credit Document No */
public String getCreditDocumentNo() 
{
return (String)get_Value("CreditDocumentNo");
}
/** Set CurrentOpenAmt */
public void setCurrentOpenAmt (BigDecimal CurrentOpenAmt)
{
set_Value ("CurrentOpenAmt", CurrentOpenAmt);
}
/** Get CurrentOpenAmt */
public BigDecimal getCurrentOpenAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CurrentOpenAmt");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Debit Document */
public void setDebitDocument (String DebitDocument)
{
if (DebitDocument != null && DebitDocument.length() > 255)
{
log.warning("Length > 255 - truncated");
DebitDocument = DebitDocument.substring(0,255);
}
set_Value ("DebitDocument", DebitDocument);
}
/** Get Debit Document */
public String getDebitDocument() 
{
return (String)get_Value("DebitDocument");
}
/** Set Debit Document No */
public void setDebitDocumentNo (String DebitDocumentNo)
{
if (DebitDocumentNo != null && DebitDocumentNo.length() > 255)
{
log.warning("Length > 255 - truncated");
DebitDocumentNo = DebitDocumentNo.substring(0,255);
}
set_Value ("DebitDocumentNo", DebitDocumentNo);
}
/** Get Debit Document No */
public String getDebitDocumentNo() 
{
return (String)get_Value("DebitDocumentNo");
}
public static final int DOCALLOCATIONCATEGORY_AD_Reference_ID = MReference.getReferenceID("Allocation Document Category");
/** Total Paid Debits = T */
public static final String DOCALLOCATIONCATEGORY_TotalPaidDebits = "T";
/** Partial Paid Debits = P */
public static final String DOCALLOCATIONCATEGORY_PartialPaidDebits = "P";
/** Unpaid Debits = W */
public static final String DOCALLOCATIONCATEGORY_UnpaidDebits = "W";
/** Not Allocated Credits = C */
public static final String DOCALLOCATIONCATEGORY_NotAllocatedCredits = "C";
/** Set Document Allocation Category */
public void setDocAllocationCategory (String DocAllocationCategory)
{
if (DocAllocationCategory.equals("T") || DocAllocationCategory.equals("P") || DocAllocationCategory.equals("W") || DocAllocationCategory.equals("C"));
 else throw new IllegalArgumentException ("DocAllocationCategory Invalid value - Reference = DOCALLOCATIONCATEGORY_AD_Reference_ID - T - P - W - C");
if (DocAllocationCategory == null) throw new IllegalArgumentException ("DocAllocationCategory is mandatory");
if (DocAllocationCategory.length() > 1)
{
log.warning("Length > 1 - truncated");
DocAllocationCategory = DocAllocationCategory.substring(0,1);
}
set_Value ("DocAllocationCategory", DocAllocationCategory);
}
/** Get Document Allocation Category */
public String getDocAllocationCategory() 
{
return (String)get_Value("DocAllocationCategory");
}
/** Set Allocation Report */
public void setT_Allocation_Report_ID (int T_Allocation_Report_ID)
{
set_ValueNoCheck ("T_Allocation_Report_ID", new Integer(T_Allocation_Report_ID));
}
/** Get Allocation Report */
public int getT_Allocation_Report_ID() 
{
Integer ii = (Integer)get_Value("T_Allocation_Report_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
