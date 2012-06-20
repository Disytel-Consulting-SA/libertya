/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_SaleByVendor
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-06-18 14:08:53.515 */
public class X_T_SaleByVendor extends PO
{
/** Constructor estándar */
public X_T_SaleByVendor (Properties ctx, int T_SaleByVendor_ID, String trxName)
{
super (ctx, T_SaleByVendor_ID, trxName);
/** if (T_SaleByVendor_ID == 0)
{
setAD_PInstance_ID (0);
setC_BPartner_ID (0);
setentered_units (Env.ZERO);
setM_Product_ID (0);
setsold_units (Env.ZERO);
setstored_units (Env.ZERO);
setTotalAmt (Env.ZERO);
settotalneto (Env.ZERO);
setT_SaleByVendor_ID (0);
}
 */
}
/** Load Constructor */
public X_T_SaleByVendor (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000154 */
public static final int Table_ID=1000154;

/** TableName=T_SaleByVendor */
public static final String Table_Name="T_SaleByVendor";

protected static KeyNamePair Model = new KeyNamePair(1000154,"T_SaleByVendor");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_SaleByVendor[").append(getID()).append("]");
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
/** Set Entered Units */
public void setentered_units (BigDecimal entered_units)
{
if (entered_units == null) throw new IllegalArgumentException ("entered_units is mandatory");
set_Value ("entered_units", entered_units);
}
/** Get Entered Units */
public BigDecimal getentered_units() 
{
BigDecimal bd = (BigDecimal)get_Value("entered_units");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
if (M_Product_Category_ID <= 0) set_Value ("M_Product_Category_ID", null);
 else 
set_Value ("M_Product_Category_ID", new Integer(M_Product_Category_ID));
}
/** Get Product Category.
Category of a Product */
public int getM_Product_Category_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Category_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
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
/** Set Register TIMESTAMP */
public void setregister_date (Timestamp register_date)
{
set_Value ("register_date", register_date);
}
/** Get Register TIMESTAMP */
public Timestamp getregister_date() 
{
return (Timestamp)get_Value("register_date");
}
/** Set Sold Units */
public void setsold_units (BigDecimal sold_units)
{
if (sold_units == null) throw new IllegalArgumentException ("sold_units is mandatory");
set_Value ("sold_units", sold_units);
}
/** Get Sold Units */
public BigDecimal getsold_units() 
{
BigDecimal bd = (BigDecimal)get_Value("sold_units");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Stored Units */
public void setstored_units (BigDecimal stored_units)
{
if (stored_units == null) throw new IllegalArgumentException ("stored_units is mandatory");
set_Value ("stored_units", stored_units);
}
/** Get Stored Units */
public BigDecimal getstored_units() 
{
BigDecimal bd = (BigDecimal)get_Value("stored_units");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Total Amount.
Total Amount */
public void setTotalAmt (BigDecimal TotalAmt)
{
if (TotalAmt == null) throw new IllegalArgumentException ("TotalAmt is mandatory");
set_Value ("TotalAmt", TotalAmt);
}
/** Get Total Amount.
Total Amount */
public BigDecimal getTotalAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Neto */
public void settotalneto (BigDecimal totalneto)
{
if (totalneto == null) throw new IllegalArgumentException ("totalneto is mandatory");
set_Value ("totalneto", totalneto);
}
/** Get Neto */
public BigDecimal gettotalneto() 
{
BigDecimal bd = (BigDecimal)get_Value("totalneto");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Sale by vendor */
public void setT_SaleByVendor_ID (int T_SaleByVendor_ID)
{
set_ValueNoCheck ("T_SaleByVendor_ID", new Integer(T_SaleByVendor_ID));
}
/** Get Sale by vendor */
public int getT_SaleByVendor_ID() 
{
Integer ii = (Integer)get_Value("T_SaleByVendor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value != null && Value.length() > 20)
{
log.warning("Length > 20 - truncated");
Value = Value.substring(0,20);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
