/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_Aging
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.765 */
public class X_T_Aging extends PO
{
/** Constructor estándar */
public X_T_Aging (Properties ctx, int T_Aging_ID, String trxName)
{
super (ctx, T_Aging_ID, trxName);
/** if (T_Aging_ID == 0)
{
setAD_PInstance_ID (0);
setC_BP_Group_ID (0);
setC_BPartner_ID (0);
setC_Currency_ID (0);
setC_InvoicePaySchedule_ID (0);	// 0
setC_Invoice_ID (0);
setDue0 (Env.ZERO);
setDue0_30 (Env.ZERO);
setDue0_7 (Env.ZERO);
setDue1_7 (Env.ZERO);
setDue31_60 (Env.ZERO);
setDue31_Plus (Env.ZERO);
setDue61_90 (Env.ZERO);
setDue61_Plus (Env.ZERO);
setDue8_30 (Env.ZERO);
setDue91_Plus (Env.ZERO);
setDueAmt (Env.ZERO);
setDueDate (new Timestamp(System.currentTimeMillis()));
setInvoicedAmt (Env.ZERO);
setIsListInvoices (false);
setIsSOTrx (false);
setOpenAmt (Env.ZERO);
setPastDue1_30 (Env.ZERO);
setPastDue1_7 (Env.ZERO);
setPastDue31_60 (Env.ZERO);
setPastDue31_Plus (Env.ZERO);
setPastDue61_90 (Env.ZERO);
setPastDue61_Plus (Env.ZERO);
setPastDue8_30 (Env.ZERO);
setPastDue91_Plus (Env.ZERO);
setPastDueAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_T_Aging (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=631 */
public static final int Table_ID=631;

/** TableName=T_Aging */
public static final String Table_Name="T_Aging";

protected static KeyNamePair Model = new KeyNamePair(631,"T_Aging");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_Aging[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_ValueNoCheck ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner Group.
Business Partner Group */
public void setC_BP_Group_ID (int C_BP_Group_ID)
{
set_Value ("C_BP_Group_ID", new Integer(C_BP_Group_ID));
}
/** Get Business Partner Group.
Business Partner Group */
public int getC_BP_Group_ID() 
{
Integer ii = (Integer)get_Value("C_BP_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_ValueNoCheck ("C_BPartner_ID", new Integer(C_BPartner_ID));
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
/** Set Invoice Payment Schedule.
Invoice Payment Schedule */
public void setC_InvoicePaySchedule_ID (int C_InvoicePaySchedule_ID)
{
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
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
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
/** Set Due Today */
public void setDue0 (BigDecimal Due0)
{
if (Due0 == null) throw new IllegalArgumentException ("Due0 is mandatory");
set_Value ("Due0", Due0);
}
/** Get Due Today */
public BigDecimal getDue0() 
{
BigDecimal bd = (BigDecimal)get_Value("Due0");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due Today-30 */
public void setDue0_30 (BigDecimal Due0_30)
{
if (Due0_30 == null) throw new IllegalArgumentException ("Due0_30 is mandatory");
set_Value ("Due0_30", Due0_30);
}
/** Get Due Today-30 */
public BigDecimal getDue0_30() 
{
BigDecimal bd = (BigDecimal)get_Value("Due0_30");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due Today-7 */
public void setDue0_7 (BigDecimal Due0_7)
{
if (Due0_7 == null) throw new IllegalArgumentException ("Due0_7 is mandatory");
set_Value ("Due0_7", Due0_7);
}
/** Get Due Today-7 */
public BigDecimal getDue0_7() 
{
BigDecimal bd = (BigDecimal)get_Value("Due0_7");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due 1-7 */
public void setDue1_7 (BigDecimal Due1_7)
{
if (Due1_7 == null) throw new IllegalArgumentException ("Due1_7 is mandatory");
set_Value ("Due1_7", Due1_7);
}
/** Get Due 1-7 */
public BigDecimal getDue1_7() 
{
BigDecimal bd = (BigDecimal)get_Value("Due1_7");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due 31-60 */
public void setDue31_60 (BigDecimal Due31_60)
{
if (Due31_60 == null) throw new IllegalArgumentException ("Due31_60 is mandatory");
set_Value ("Due31_60", Due31_60);
}
/** Get Due 31-60 */
public BigDecimal getDue31_60() 
{
BigDecimal bd = (BigDecimal)get_Value("Due31_60");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due > 31 */
public void setDue31_Plus (BigDecimal Due31_Plus)
{
if (Due31_Plus == null) throw new IllegalArgumentException ("Due31_Plus is mandatory");
set_Value ("Due31_Plus", Due31_Plus);
}
/** Get Due > 31 */
public BigDecimal getDue31_Plus() 
{
BigDecimal bd = (BigDecimal)get_Value("Due31_Plus");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due 61-90 */
public void setDue61_90 (BigDecimal Due61_90)
{
if (Due61_90 == null) throw new IllegalArgumentException ("Due61_90 is mandatory");
set_Value ("Due61_90", Due61_90);
}
/** Get Due 61-90 */
public BigDecimal getDue61_90() 
{
BigDecimal bd = (BigDecimal)get_Value("Due61_90");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due > 61 */
public void setDue61_Plus (BigDecimal Due61_Plus)
{
if (Due61_Plus == null) throw new IllegalArgumentException ("Due61_Plus is mandatory");
set_Value ("Due61_Plus", Due61_Plus);
}
/** Get Due > 61 */
public BigDecimal getDue61_Plus() 
{
BigDecimal bd = (BigDecimal)get_Value("Due61_Plus");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due 8-30 */
public void setDue8_30 (BigDecimal Due8_30)
{
if (Due8_30 == null) throw new IllegalArgumentException ("Due8_30 is mandatory");
set_Value ("Due8_30", Due8_30);
}
/** Get Due 8-30 */
public BigDecimal getDue8_30() 
{
BigDecimal bd = (BigDecimal)get_Value("Due8_30");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due > 91 */
public void setDue91_Plus (BigDecimal Due91_Plus)
{
if (Due91_Plus == null) throw new IllegalArgumentException ("Due91_Plus is mandatory");
set_Value ("Due91_Plus", Due91_Plus);
}
/** Get Due > 91 */
public BigDecimal getDue91_Plus() 
{
BigDecimal bd = (BigDecimal)get_Value("Due91_Plus");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Amount due.
Amount of the payment due */
public void setDueAmt (BigDecimal DueAmt)
{
if (DueAmt == null) throw new IllegalArgumentException ("DueAmt is mandatory");
set_Value ("DueAmt", DueAmt);
}
/** Get Amount due.
Amount of the payment due */
public BigDecimal getDueAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("DueAmt");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Invoiced Amount.
The amount invoiced */
public void setInvoicedAmt (BigDecimal InvoicedAmt)
{
if (InvoicedAmt == null) throw new IllegalArgumentException ("InvoicedAmt is mandatory");
set_Value ("InvoicedAmt", InvoicedAmt);
}
/** Get Invoiced Amount.
The amount invoiced */
public BigDecimal getInvoicedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("InvoicedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set List Invoices.
Include List of Invoices */
public void setIsListInvoices (boolean IsListInvoices)
{
set_Value ("IsListInvoices", new Boolean(IsListInvoices));
}
/** Get List Invoices.
Include List of Invoices */
public boolean isListInvoices() 
{
Object oo = get_Value("IsListInvoices");
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
/** Set Open Amount.
Open item amount */
public void setOpenAmt (BigDecimal OpenAmt)
{
if (OpenAmt == null) throw new IllegalArgumentException ("OpenAmt is mandatory");
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
/** Set Past Due 1-30 */
public void setPastDue1_30 (BigDecimal PastDue1_30)
{
if (PastDue1_30 == null) throw new IllegalArgumentException ("PastDue1_30 is mandatory");
set_Value ("PastDue1_30", PastDue1_30);
}
/** Get Past Due 1-30 */
public BigDecimal getPastDue1_30() 
{
BigDecimal bd = (BigDecimal)get_Value("PastDue1_30");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Past Due 1-7 */
public void setPastDue1_7 (BigDecimal PastDue1_7)
{
if (PastDue1_7 == null) throw new IllegalArgumentException ("PastDue1_7 is mandatory");
set_Value ("PastDue1_7", PastDue1_7);
}
/** Get Past Due 1-7 */
public BigDecimal getPastDue1_7() 
{
BigDecimal bd = (BigDecimal)get_Value("PastDue1_7");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Past Due 31-60 */
public void setPastDue31_60 (BigDecimal PastDue31_60)
{
if (PastDue31_60 == null) throw new IllegalArgumentException ("PastDue31_60 is mandatory");
set_Value ("PastDue31_60", PastDue31_60);
}
/** Get Past Due 31-60 */
public BigDecimal getPastDue31_60() 
{
BigDecimal bd = (BigDecimal)get_Value("PastDue31_60");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Past Due > 31 */
public void setPastDue31_Plus (BigDecimal PastDue31_Plus)
{
if (PastDue31_Plus == null) throw new IllegalArgumentException ("PastDue31_Plus is mandatory");
set_Value ("PastDue31_Plus", PastDue31_Plus);
}
/** Get Past Due > 31 */
public BigDecimal getPastDue31_Plus() 
{
BigDecimal bd = (BigDecimal)get_Value("PastDue31_Plus");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Past Due 61-90 */
public void setPastDue61_90 (BigDecimal PastDue61_90)
{
if (PastDue61_90 == null) throw new IllegalArgumentException ("PastDue61_90 is mandatory");
set_Value ("PastDue61_90", PastDue61_90);
}
/** Get Past Due 61-90 */
public BigDecimal getPastDue61_90() 
{
BigDecimal bd = (BigDecimal)get_Value("PastDue61_90");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Past Due > 61 */
public void setPastDue61_Plus (BigDecimal PastDue61_Plus)
{
if (PastDue61_Plus == null) throw new IllegalArgumentException ("PastDue61_Plus is mandatory");
set_Value ("PastDue61_Plus", PastDue61_Plus);
}
/** Get Past Due > 61 */
public BigDecimal getPastDue61_Plus() 
{
BigDecimal bd = (BigDecimal)get_Value("PastDue61_Plus");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Past Due 8-30 */
public void setPastDue8_30 (BigDecimal PastDue8_30)
{
if (PastDue8_30 == null) throw new IllegalArgumentException ("PastDue8_30 is mandatory");
set_Value ("PastDue8_30", PastDue8_30);
}
/** Get Past Due 8-30 */
public BigDecimal getPastDue8_30() 
{
BigDecimal bd = (BigDecimal)get_Value("PastDue8_30");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Past Due > 91 */
public void setPastDue91_Plus (BigDecimal PastDue91_Plus)
{
if (PastDue91_Plus == null) throw new IllegalArgumentException ("PastDue91_Plus is mandatory");
set_Value ("PastDue91_Plus", PastDue91_Plus);
}
/** Get Past Due > 91 */
public BigDecimal getPastDue91_Plus() 
{
BigDecimal bd = (BigDecimal)get_Value("PastDue91_Plus");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Past Due */
public void setPastDueAmt (BigDecimal PastDueAmt)
{
if (PastDueAmt == null) throw new IllegalArgumentException ("PastDueAmt is mandatory");
set_Value ("PastDueAmt", PastDueAmt);
}
/** Get Past Due */
public BigDecimal getPastDueAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PastDueAmt");
if (bd == null) return Env.ZERO;
return bd;
}
}
