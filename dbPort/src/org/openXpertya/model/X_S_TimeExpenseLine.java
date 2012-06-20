/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por S_TimeExpenseLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.609 */
public class X_S_TimeExpenseLine extends PO
{
/** Constructor estándar */
public X_S_TimeExpenseLine (Properties ctx, int S_TimeExpenseLine_ID, String trxName)
{
super (ctx, S_TimeExpenseLine_ID, trxName);
/** if (S_TimeExpenseLine_ID == 0)
{
setDateExpense (new Timestamp(System.currentTimeMillis()));	// @DateExpense@;
@DateReport@
setIsInvoiced (false);
setIsTimeReport (false);
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM S_TimeExpenseLine WHERE S_TimeExpense_ID=@S_TimeExpense_ID@
setProcessed (false);
setS_TimeExpenseLine_ID (0);
setS_TimeExpense_ID (0);
}
 */
}
/** Load Constructor */
public X_S_TimeExpenseLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=488 */
public static final int Table_ID=488;

/** TableName=S_TimeExpenseLine */
public static final String Table_Name="S_TimeExpenseLine";

protected static KeyNamePair Model = new KeyNamePair(488,"S_TimeExpenseLine");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_S_TimeExpenseLine[").append(getID()).append("]");
return sb.toString();
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
/** Set Invoice Line.
Invoice Detail Line */
public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
{
if (C_InvoiceLine_ID <= 0) set_ValueNoCheck ("C_InvoiceLine_ID", null);
 else 
set_ValueNoCheck ("C_InvoiceLine_ID", new Integer(C_InvoiceLine_ID));
}
/** Get Invoice Line.
Invoice Detail Line */
public int getC_InvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sales Order Line.
Sales Order Line */
public void setC_OrderLine_ID (int C_OrderLine_ID)
{
if (C_OrderLine_ID <= 0) set_ValueNoCheck ("C_OrderLine_ID", null);
 else 
set_ValueNoCheck ("C_OrderLine_ID", new Integer(C_OrderLine_ID));
}
/** Get Sales Order Line.
Sales Order Line */
public int getC_OrderLine_ID() 
{
Integer ii = (Integer)get_Value("C_OrderLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project Phase.
Phase of a Project */
public void setC_ProjectPhase_ID (int C_ProjectPhase_ID)
{
if (C_ProjectPhase_ID <= 0) set_Value ("C_ProjectPhase_ID", null);
 else 
set_Value ("C_ProjectPhase_ID", new Integer(C_ProjectPhase_ID));
}
/** Get Project Phase.
Phase of a Project */
public int getC_ProjectPhase_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectPhase_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project Task.
Actual Project Task in a Phase */
public void setC_ProjectTask_ID (int C_ProjectTask_ID)
{
if (C_ProjectTask_ID <= 0) set_Value ("C_ProjectTask_ID", null);
 else 
set_Value ("C_ProjectTask_ID", new Integer(C_ProjectTask_ID));
}
/** Get Project Task.
Actual Project Task in a Phase */
public int getC_ProjectTask_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectTask_ID");
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
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
if (C_UOM_ID <= 0) set_Value ("C_UOM_ID", null);
 else 
set_Value ("C_UOM_ID", new Integer(C_UOM_ID));
}
/** Get UOM.
Unit of Measure */
public int getC_UOM_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Converted Amount.
Converted Amount */
public void setConvertedAmt (BigDecimal ConvertedAmt)
{
set_Value ("ConvertedAmt", ConvertedAmt);
}
/** Get Converted Amount.
Converted Amount */
public BigDecimal getConvertedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ConvertedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Expense Date.
Date of expense */
public void setDateExpense (Timestamp DateExpense)
{
if (DateExpense == null) throw new IllegalArgumentException ("DateExpense is mandatory");
set_Value ("DateExpense", DateExpense);
}
/** Get Expense Date.
Date of expense */
public Timestamp getDateExpense() 
{
return (Timestamp)get_Value("DateExpense");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Expense Amount.
Amount for this expense */
public void setExpenseAmt (BigDecimal ExpenseAmt)
{
set_Value ("ExpenseAmt", ExpenseAmt);
}
/** Get Expense Amount.
Amount for this expense */
public BigDecimal getExpenseAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ExpenseAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Invoice Price.
Unit price to be invoiced or 0 for default price */
public void setInvoicePrice (BigDecimal InvoicePrice)
{
set_Value ("InvoicePrice", InvoicePrice);
}
/** Get Invoice Price.
Unit price to be invoiced or 0 for default price */
public BigDecimal getInvoicePrice() 
{
BigDecimal bd = (BigDecimal)get_Value("InvoicePrice");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Invoiced.
Is this invoiced? */
public void setIsInvoiced (boolean IsInvoiced)
{
set_Value ("IsInvoiced", new Boolean(IsInvoiced));
}
/** Get Invoiced.
Is this invoiced? */
public boolean isInvoiced() 
{
Object oo = get_Value("IsInvoiced");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Time Report.
Line is a time report only (no expense) */
public void setIsTimeReport (boolean IsTimeReport)
{
set_Value ("IsTimeReport", new Boolean(IsTimeReport));
}
/** Get Time Report.
Line is a time report only (no expense) */
public boolean isTimeReport() 
{
Object oo = get_Value("IsTimeReport");
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getLine()));
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Note.
Optional additional user defined information */
public void setNote (String Note)
{
if (Note != null && Note.length() > 255)
{
log.warning("Length > 255 - truncated");
Note = Note.substring(0,254);
}
set_Value ("Note", Note);
}
/** Get Note.
Optional additional user defined information */
public String getNote() 
{
return (String)get_Value("Note");
}
/** Set Price Invoiced.
The priced invoiced to the customer (in the currency of the customer's AR price list) - 0 for default price */
public void setPriceInvoiced (BigDecimal PriceInvoiced)
{
set_Value ("PriceInvoiced", PriceInvoiced);
}
/** Get Price Invoiced.
The priced invoiced to the customer (in the currency of the customer's AR price list) - 0 for default price */
public BigDecimal getPriceInvoiced() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceInvoiced");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Price Reimbursed.
The reimbursed price (in currency of the employee's AP price list) */
public void setPriceReimbursed (BigDecimal PriceReimbursed)
{
set_Value ("PriceReimbursed", PriceReimbursed);
}
/** Get Price Reimbursed.
The reimbursed price (in currency of the employee's AP price list) */
public BigDecimal getPriceReimbursed() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceReimbursed");
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
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
set_Value ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Invoiced Quantity.
Invoiced Quantity */
public void setQtyInvoiced (BigDecimal QtyInvoiced)
{
set_Value ("QtyInvoiced", QtyInvoiced);
}
/** Get Invoiced Quantity.
Invoiced Quantity */
public BigDecimal getQtyInvoiced() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyInvoiced");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Quantity Reimbursed.
The reimbursed quantity */
public void setQtyReimbursed (BigDecimal QtyReimbursed)
{
set_Value ("QtyReimbursed", QtyReimbursed);
}
/** Get Quantity Reimbursed.
The reimbursed quantity */
public BigDecimal getQtyReimbursed() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyReimbursed");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Resource Assignment.
Resource Assignment */
public void setS_ResourceAssignment_ID (int S_ResourceAssignment_ID)
{
if (S_ResourceAssignment_ID <= 0) set_Value ("S_ResourceAssignment_ID", null);
 else 
set_Value ("S_ResourceAssignment_ID", new Integer(S_ResourceAssignment_ID));
}
/** Get Resource Assignment.
Resource Assignment */
public int getS_ResourceAssignment_ID() 
{
Integer ii = (Integer)get_Value("S_ResourceAssignment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Expense Line.
Time and Expense Report Line */
public void setS_TimeExpenseLine_ID (int S_TimeExpenseLine_ID)
{
set_ValueNoCheck ("S_TimeExpenseLine_ID", new Integer(S_TimeExpenseLine_ID));
}
/** Get Expense Line.
Time and Expense Report Line */
public int getS_TimeExpenseLine_ID() 
{
Integer ii = (Integer)get_Value("S_TimeExpenseLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Expense Report.
Time and Expense Report */
public void setS_TimeExpense_ID (int S_TimeExpense_ID)
{
set_ValueNoCheck ("S_TimeExpense_ID", new Integer(S_TimeExpense_ID));
}
/** Get Expense Report.
Time and Expense Report */
public int getS_TimeExpense_ID() 
{
Integer ii = (Integer)get_Value("S_TimeExpense_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Time Type.
Type of time recorded */
public void setS_TimeType_ID (int S_TimeType_ID)
{
if (S_TimeType_ID <= 0) set_Value ("S_TimeType_ID", null);
 else 
set_Value ("S_TimeType_ID", new Integer(S_TimeType_ID));
}
/** Get Time Type.
Type of time recorded */
public int getS_TimeType_ID() 
{
Integer ii = (Integer)get_Value("S_TimeType_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
