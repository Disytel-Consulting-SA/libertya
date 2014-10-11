/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_AllocationLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-10-10 23:51:10.369 */
public class X_C_AllocationLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_AllocationLine (Properties ctx, int C_AllocationLine_ID, String trxName)
{
super (ctx, C_AllocationLine_ID, trxName);
/** if (C_AllocationLine_ID == 0)
{
setAmount (Env.ZERO);
setC_AllocationHdr_ID (0);
setC_AllocationLine_ID (0);
setDiscountAmt (Env.ZERO);
setWriteOffAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_AllocationLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_AllocationLine");

/** TableName=C_AllocationLine */
public static final String Table_Name="C_AllocationLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_AllocationLine");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_AllocationLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Allocation No.
Allocation Number */
public void setAllocationNo (int AllocationNo)
{
set_ValueNoCheck ("AllocationNo", new Integer(AllocationNo));
}
/** Get Allocation No.
Allocation Number */
public int getAllocationNo() 
{
Integer ii = (Integer)get_Value("AllocationNo");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Amount.
Amount in a defined currency */
public void setAmount (BigDecimal Amount)
{
if (Amount == null) throw new IllegalArgumentException ("Amount is mandatory");
set_ValueNoCheck ("Amount", Amount);
}
/** Get Amount.
Amount in a defined currency */
public BigDecimal getAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("Amount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Allocation.
Payment allocation */
public void setC_AllocationHdr_ID (int C_AllocationHdr_ID)
{
set_ValueNoCheck ("C_AllocationHdr_ID", new Integer(C_AllocationHdr_ID));
}
/** Get Allocation.
Payment allocation */
public int getC_AllocationHdr_ID() 
{
Integer ii = (Integer)get_Value("C_AllocationHdr_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Allocation Line.
Allocation Line */
public void setC_AllocationLine_ID (int C_AllocationLine_ID)
{
set_ValueNoCheck ("C_AllocationLine_ID", new Integer(C_AllocationLine_ID));
}
/** Get Allocation Line.
Allocation Line */
public int getC_AllocationLine_ID() 
{
Integer ii = (Integer)get_Value("C_AllocationLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_ValueNoCheck ("C_BPartner_ID", null);
 else 
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
/** Set Cash Journal Line.
Cash Journal Line */
public void setC_CashLine_ID (int C_CashLine_ID)
{
if (C_CashLine_ID <= 0) set_ValueNoCheck ("C_CashLine_ID", null);
 else 
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
/** Set Change Amount */
public void setChangeAmt (BigDecimal ChangeAmt)
{
set_Value ("ChangeAmt", ChangeAmt);
}
/** Get Change Amount */
public BigDecimal getChangeAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ChangeAmt");
if (bd == null) return Env.ZERO;
return bd;
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Invoice_ID()));
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
if (C_Order_ID <= 0) set_ValueNoCheck ("C_Order_ID", null);
 else 
set_ValueNoCheck ("C_Order_ID", new Integer(C_Order_ID));
}
/** Get Order.
Order */
public int getC_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
if (C_Payment_ID <= 0) set_ValueNoCheck ("C_Payment_ID", null);
 else 
set_ValueNoCheck ("C_Payment_ID", new Integer(C_Payment_ID));
}
/** Get Payment.
Payment identifier */
public int getC_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Payment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Transaction Date.
Transaction Date */
public void setDateTrx (Timestamp DateTrx)
{
set_ValueNoCheck ("DateTrx", DateTrx);
}
/** Get Transaction Date.
Transaction Date */
public Timestamp getDateTrx() 
{
return (Timestamp)get_Value("DateTrx");
}
/** Set Discount Amount.
Calculated amount of discount */
public void setDiscountAmt (BigDecimal DiscountAmt)
{
if (DiscountAmt == null) throw new IllegalArgumentException ("DiscountAmt is mandatory");
set_ValueNoCheck ("DiscountAmt", DiscountAmt);
}
/** Get Discount Amount.
Calculated amount of discount */
public BigDecimal getDiscountAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("DiscountAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Manual.
This is a manual process */
public void setIsManual (boolean IsManual)
{
set_ValueNoCheck ("IsManual", new Boolean(IsManual));
}
/** Get Manual.
This is a manual process */
public boolean isManual() 
{
Object oo = get_Value("IsManual");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Line description */
public void setLine_Description (String Line_Description)
{
if (Line_Description != null && Line_Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Line_Description = Line_Description.substring(0,255);
}
set_Value ("Line_Description", Line_Description);
}
/** Get Line description */
public String getLine_Description() 
{
return (String)get_Value("Line_Description");
}
/** Set Over/Under Payment.
Over-Payment (unallocated) or Under-Payment (partial payment) Amount */
public void setOverUnderAmt (BigDecimal OverUnderAmt)
{
set_Value ("OverUnderAmt", OverUnderAmt);
}
/** Get Over/Under Payment.
Over-Payment (unallocated) or Under-Payment (partial payment) Amount */
public BigDecimal getOverUnderAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("OverUnderAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Posted.
Posting status */
public void setPosted (boolean Posted)
{
set_ValueNoCheck ("Posted", new Boolean(Posted));
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
/** Set Write-off Amount.
Amount to write-off */
public void setWriteOffAmt (BigDecimal WriteOffAmt)
{
if (WriteOffAmt == null) throw new IllegalArgumentException ("WriteOffAmt is mandatory");
set_ValueNoCheck ("WriteOffAmt", WriteOffAmt);
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
