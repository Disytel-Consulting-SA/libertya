/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CommissionDetail
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.234 */
public class X_C_CommissionDetail extends PO
{
/** Constructor estándar */
public X_C_CommissionDetail (Properties ctx, int C_CommissionDetail_ID, String trxName)
{
super (ctx, C_CommissionDetail_ID, trxName);
/** if (C_CommissionDetail_ID == 0)
{
setActualAmt (Env.ZERO);
setActualQty (Env.ZERO);
setC_CommissionAmt_ID (0);
setC_CommissionDetail_ID (0);
setC_Currency_ID (0);
setConvertedAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_CommissionDetail (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=437 */
public static final int Table_ID=437;

/** TableName=C_CommissionDetail */
public static final String Table_Name="C_CommissionDetail";

protected static KeyNamePair Model = new KeyNamePair(437,"C_CommissionDetail");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CommissionDetail[").append(getID()).append("]");
return sb.toString();
}
/** Set Actual Amount.
The actual amount */
public void setActualAmt (BigDecimal ActualAmt)
{
if (ActualAmt == null) throw new IllegalArgumentException ("ActualAmt is mandatory");
set_Value ("ActualAmt", ActualAmt);
}
/** Get Actual Amount.
The actual amount */
public BigDecimal getActualAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ActualAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Actual Quantity.
The actual quantity */
public void setActualQty (BigDecimal ActualQty)
{
if (ActualQty == null) throw new IllegalArgumentException ("ActualQty is mandatory");
set_Value ("ActualQty", ActualQty);
}
/** Get Actual Quantity.
The actual quantity */
public BigDecimal getActualQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ActualQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Commission Amount.
Generated Commission Amount  */
public void setC_CommissionAmt_ID (int C_CommissionAmt_ID)
{
set_ValueNoCheck ("C_CommissionAmt_ID", new Integer(C_CommissionAmt_ID));
}
/** Get Commission Amount.
Generated Commission Amount  */
public int getC_CommissionAmt_ID() 
{
Integer ii = (Integer)get_Value("C_CommissionAmt_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Commission Detail.
Supporting information for Commission Amounts */
public void setC_CommissionDetail_ID (int C_CommissionDetail_ID)
{
set_ValueNoCheck ("C_CommissionDetail_ID", new Integer(C_CommissionDetail_ID));
}
/** Get Commission Detail.
Supporting information for Commission Amounts */
public int getC_CommissionDetail_ID() 
{
Integer ii = (Integer)get_Value("C_CommissionDetail_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
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
/** Set Converted Amount.
Converted Amount */
public void setConvertedAmt (BigDecimal ConvertedAmt)
{
if (ConvertedAmt == null) throw new IllegalArgumentException ("ConvertedAmt is mandatory");
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
/** Set Info.
Information */
public void setInfo (String Info)
{
if (Info != null && Info.length() > 60)
{
log.warning("Length > 60 - truncated");
Info = Info.substring(0,59);
}
set_Value ("Info", Info);
}
/** Get Info.
Information */
public String getInfo() 
{
return (String)get_Value("Info");
}
/** Set Reference.
Reference for this record */
public void setReference (String Reference)
{
if (Reference != null && Reference.length() > 60)
{
log.warning("Length > 60 - truncated");
Reference = Reference.substring(0,59);
}
set_Value ("Reference", Reference);
}
/** Get Reference.
Reference for this record */
public String getReference() 
{
return (String)get_Value("Reference");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getReference());
}
}
