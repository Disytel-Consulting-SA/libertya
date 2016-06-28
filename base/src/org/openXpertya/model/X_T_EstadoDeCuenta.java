/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_EstadoDeCuenta
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-06-28 15:44:16.609 */
public class X_T_EstadoDeCuenta extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_EstadoDeCuenta (Properties ctx, int T_EstadoDeCuenta_ID, String trxName)
{
super (ctx, T_EstadoDeCuenta_ID, trxName);
/** if (T_EstadoDeCuenta_ID == 0)
{
setAD_PInstance_ID (0);
setT_EstadoDeCuenta_ID (0);
}
 */
}
/** Load Constructor */
public X_T_EstadoDeCuenta (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_EstadoDeCuenta");

/** TableName=T_EstadoDeCuenta */
public static final String Table_Name="T_EstadoDeCuenta";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_EstadoDeCuenta");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_EstadoDeCuenta[").append(getID()).append("]");
return sb.toString();
}
/** Set Account Type.
Indicates the type of account */
public void setAccountType (String AccountType)
{
if (AccountType != null && AccountType.length() > 1)
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
/** Set bpartner */
public void setbpartner (String bpartner)
{
if (bpartner != null && bpartner.length() > 60)
{
log.warning("Length > 60 - truncated");
bpartner = bpartner.substring(0,60);
}
set_Value ("bpartner", bpartner);
}
/** Get bpartner */
public String getbpartner() 
{
return (String)get_Value("bpartner");
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
/** Set Currency Type.
Currency Conversion Rate Type */
public void setC_ConversionType_ID (int C_ConversionType_ID)
{
if (C_ConversionType_ID <= 0) set_Value ("C_ConversionType_ID", null);
 else 
set_Value ("C_ConversionType_ID", new Integer(C_ConversionType_ID));
}
/** Get Currency Type.
Currency Conversion Rate Type */
public int getC_ConversionType_ID() 
{
Integer ii = (Integer)get_Value("C_ConversionType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
if (C_Currency_ID <= 0) set_Value ("C_Currency_ID", null);
 else 
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
/** Set Invoice Payment Schedule.
Invoice Payment Schedule */
public void setC_InvoicePaySchedule_ID (int C_InvoicePaySchedule_ID)
{
if (C_InvoicePaySchedule_ID <= 0) set_Value ("C_InvoicePaySchedule_ID", null);
 else 
set_Value ("C_InvoicePaySchedule_ID", new Integer(C_InvoicePaySchedule_ID));
}
/** Get Invoice Payment Schedule.
Invoice Payment Schedule */
public int getC_InvoicePaySchedule_ID() 
{
Integer ii = (Integer)get_Value("C_InvoicePaySchedule_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CONDITION_AD_Reference_ID = MReference.getReferenceID("Document Condition");
/** Cash = B */
public static final String CONDITION_Cash = "B";
/** On Credit = P */
public static final String CONDITION_OnCredit = "P";
/** All = A */
public static final String CONDITION_All = "A";
/** Set Condition */
public void setCondition (String Condition)
{
if (Condition == null || Condition.equals("B") || Condition.equals("P") || Condition.equals("A"));
 else throw new IllegalArgumentException ("Condition Invalid value - Reference = CONDITION_AD_Reference_ID - B - P - A");
if (Condition != null && Condition.length() > 1)
{
log.warning("Length > 1 - truncated");
Condition = Condition.substring(0,1);
}
set_Value ("Condition", Condition);
}
/** Get Condition */
public String getCondition() 
{
return (String)get_Value("Condition");
}
/** Set Conversion Rate */
public void setConversionRate (BigDecimal ConversionRate)
{
set_Value ("ConversionRate", ConversionRate);
}
/** Get Conversion Rate */
public BigDecimal getConversionRate() 
{
BigDecimal bd = (BigDecimal)get_Value("ConversionRate");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
if (C_Order_ID <= 0) set_Value ("C_Order_ID", null);
 else 
set_Value ("C_Order_ID", new Integer(C_Order_ID));
}
/** Get Order.
Order */
public int getC_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Order_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Account Date.
Accounting Date */
public void setDateAcct (Timestamp DateAcct)
{
set_Value ("DateAcct", DateAcct);
}
/** Get Account Date.
Accounting Date */
public Timestamp getDateAcct() 
{
return (Timestamp)get_Value("DateAcct");
}
/** Set Document Date.
Date of the Document */
public void setDateDoc (Timestamp DateDoc)
{
set_Value ("DateDoc", DateDoc);
}
/** Get Document Date.
Date of the Document */
public Timestamp getDateDoc() 
{
return (Timestamp)get_Value("DateDoc");
}
/** Set Date to Days */
public void setDateToDays (Timestamp DateToDays)
{
set_Value ("DateToDays", DateToDays);
}
/** Get Date to Days */
public Timestamp getDateToDays() 
{
return (Timestamp)get_Value("DateToDays");
}
/** Set Days due.
Number of days due (negative: due in number of days) */
public void setDaysDue (int DaysDue)
{
set_Value ("DaysDue", new Integer(DaysDue));
}
/** Get Days due.
Number of days due (negative: due in number of days) */
public int getDaysDue() 
{
Integer ii = (Integer)get_Value("DaysDue");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Discount Date.
Last Date for payments with discount */
public void setDiscountDate (Timestamp DiscountDate)
{
set_Value ("DiscountDate", DiscountDate);
}
/** Get Discount Date.
Last Date for payments with discount */
public Timestamp getDiscountDate() 
{
return (Timestamp)get_Value("DiscountDate");
}
/** Set doc_id */
public void setdoc_id (int doc_id)
{
set_Value ("doc_id", new Integer(doc_id));
}
/** Get doc_id */
public int getdoc_id() 
{
Integer ii = (Integer)get_Value("doc_id");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 30)
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
/** Set Grand Total.
Total amount of document */
public void setGrandTotal (BigDecimal GrandTotal)
{
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
/** Set Grand Total Multicurrency */
public void setGrandTotalMulticurrency (BigDecimal GrandTotalMulticurrency)
{
set_Value ("GrandTotalMulticurrency", GrandTotalMulticurrency);
}
/** Get Grand Total Multicurrency */
public BigDecimal getGrandTotalMulticurrency() 
{
BigDecimal bd = (BigDecimal)get_Value("GrandTotalMulticurrency");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Pay Schedule valid.
Is the Payment Schedule is valid */
public void setIsPayScheduleValid (boolean IsPayScheduleValid)
{
set_Value ("IsPayScheduleValid", new Boolean(IsPayScheduleValid));
}
/** Get Pay Schedule valid.
Is the Payment Schedule is valid */
public boolean isPayScheduleValid() 
{
Object oo = get_Value("IsPayScheduleValid");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sales Transaction.
This is a Sales Transaction */
public void setIsSOTrx (boolean IsSOTrx)
{
set_Value ("IsSOTrx", new Boolean(IsSOTrx));
}
/** Get Sales Transaction.
This is a Sales Transaction */
public boolean isSOTrx() 
{
Object oo = get_Value("IsSOTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Net Days.
Net Days in which payment is due */
public void setNetDays (BigDecimal NetDays)
{
set_Value ("NetDays", NetDays);
}
/** Get Net Days.
Net Days in which payment is due */
public BigDecimal getNetDays() 
{
BigDecimal bd = (BigDecimal)get_Value("NetDays");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Open Amount.
Open item amount */
public void setOpenAmt (BigDecimal OpenAmt)
{
set_Value ("OpenAmt", OpenAmt);
}
/** Get Open Amount.
Open item amount */
public BigDecimal getOpenAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("OpenAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Open Amt Multicurrency */
public void setOpenAmtMulticurrency (BigDecimal OpenAmtMulticurrency)
{
set_Value ("OpenAmtMulticurrency", OpenAmtMulticurrency);
}
/** Get Open Amt Multicurrency */
public BigDecimal getOpenAmtMulticurrency() 
{
BigDecimal bd = (BigDecimal)get_Value("OpenAmtMulticurrency");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Paid Amount */
public void setPaidAmt (BigDecimal PaidAmt)
{
set_Value ("PaidAmt", PaidAmt);
}
/** Get Paid Amount */
public BigDecimal getPaidAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PaidAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Paid Amt Multicurrency */
public void setPaidAmtMulticurrency (BigDecimal PaidAmtMulticurrency)
{
set_Value ("PaidAmtMulticurrency", PaidAmtMulticurrency);
}
/** Get Paid Amt Multicurrency */
public BigDecimal getPaidAmtMulticurrency() 
{
BigDecimal bd = (BigDecimal)get_Value("PaidAmtMulticurrency");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Show Documents.
Show Documents */
public void setShowDocuments (String ShowDocuments)
{
if (ShowDocuments != null && ShowDocuments.length() > 1)
{
log.warning("Length > 1 - truncated");
ShowDocuments = ShowDocuments.substring(0,1);
}
set_Value ("ShowDocuments", ShowDocuments);
}
/** Get Show Documents.
Show Documents */
public String getShowDocuments() 
{
return (String)get_Value("ShowDocuments");
}
/** Set Signo IsSOTrx */
public void setsigno_issotrx (int signo_issotrx)
{
set_Value ("signo_issotrx", new Integer(signo_issotrx));
}
/** Get Signo IsSOTrx */
public int getsigno_issotrx() 
{
Integer ii = (Integer)get_Value("signo_issotrx");
if (ii == null) return 0;
return ii.intValue();
}
/** Set T_EstadoDeCuenta_ID */
public void setT_EstadoDeCuenta_ID (int T_EstadoDeCuenta_ID)
{
set_ValueNoCheck ("T_EstadoDeCuenta_ID", new Integer(T_EstadoDeCuenta_ID));
}
/** Get T_EstadoDeCuenta_ID */
public int getT_EstadoDeCuenta_ID() 
{
Integer ii = (Integer)get_Value("T_EstadoDeCuenta_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set tipodoc */
public void settipodoc (String tipodoc)
{
if (tipodoc != null && tipodoc.length() > 60)
{
log.warning("Length > 60 - truncated");
tipodoc = tipodoc.substring(0,60);
}
set_Value ("tipodoc", tipodoc);
}
/** Get tipodoc */
public String gettipodoc() 
{
return (String)get_Value("tipodoc");
}
}
