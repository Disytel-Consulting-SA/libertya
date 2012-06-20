/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_InvoicePaySchedule
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:30.234 */
public class X_C_InvoicePaySchedule extends PO
{
/** Constructor estándar */
public X_C_InvoicePaySchedule (Properties ctx, int C_InvoicePaySchedule_ID, String trxName)
{
super (ctx, C_InvoicePaySchedule_ID, trxName);
/** if (C_InvoicePaySchedule_ID == 0)
{
setC_InvoicePaySchedule_ID (0);
setC_Invoice_ID (0);
setDiscountAmt (Env.ZERO);
setDiscountDate (new Timestamp(System.currentTimeMillis()));
setDueAmt (Env.ZERO);
setDueDate (new Timestamp(System.currentTimeMillis()));
setIsValid (false);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_C_InvoicePaySchedule (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=551 */
public static final int Table_ID=551;

/** TableName=C_InvoicePaySchedule */
public static final String Table_Name="C_InvoicePaySchedule";

protected static KeyNamePair Model = new KeyNamePair(551,"C_InvoicePaySchedule");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_InvoicePaySchedule[").append(getID()).append("]");
return sb.toString();
}
/** Set Invoice Payment Schedule.
Invoice Payment Schedule */
public void setC_InvoicePaySchedule_ID (int C_InvoicePaySchedule_ID)
{
set_ValueNoCheck ("C_InvoicePaySchedule_ID", new Integer(C_InvoicePaySchedule_ID));
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
/** Set Payment Schedule.
Payment Schedule Template */
public void setC_PaySchedule_ID (int C_PaySchedule_ID)
{
if (C_PaySchedule_ID <= 0) set_ValueNoCheck ("C_PaySchedule_ID", null);
 else 
set_ValueNoCheck ("C_PaySchedule_ID", new Integer(C_PaySchedule_ID));
}
/** Get Payment Schedule.
Payment Schedule Template */
public int getC_PaySchedule_ID() 
{
Integer ii = (Integer)get_Value("C_PaySchedule_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Remesa_ID */
public void setC_Remesa_ID (int C_Remesa_ID)
{
if (C_Remesa_ID <= 0) set_Value ("C_Remesa_ID", null);
 else 
set_Value ("C_Remesa_ID", new Integer(C_Remesa_ID));
}
/** Get C_Remesa_ID */
public int getC_Remesa_ID() 
{
Integer ii = (Integer)get_Value("C_Remesa_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Discount Amount.
Calculated amount of discount */
public void setDiscountAmt (BigDecimal DiscountAmt)
{
if (DiscountAmt == null) throw new IllegalArgumentException ("DiscountAmt is mandatory");
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
if (DiscountDate == null) throw new IllegalArgumentException ("DiscountDate is mandatory");
set_Value ("DiscountDate", DiscountDate);
}
/** Get Discount Date.
Last Date for payments with discount */
public Timestamp getDiscountDate() 
{
return (Timestamp)get_Value("DiscountDate");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getDiscountDate()));
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
/** Set Valid.
Element is valid */
public void setIsValid (boolean IsValid)
{
set_Value ("IsValid", new Boolean(IsValid));
}
/** Get Valid.
Element is valid */
public boolean isValid() 
{
Object oo = get_Value("IsValid");
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
}
