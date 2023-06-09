/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_OrderTax
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2021-12-14 10:23:00.226 */
public class X_C_OrderTax extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_OrderTax (Properties ctx, int C_OrderTax_ID, String trxName)
{
super (ctx, C_OrderTax_ID, trxName);
/** if (C_OrderTax_ID == 0)
{
setC_Order_ID (0);
setC_Tax_ID (0);
setIsTaxIncluded (false);
setProcessed (false);
setTaxAmt (Env.ZERO);
setTaxBaseAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_OrderTax (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_OrderTax");

/** TableName=C_OrderTax */
public static final String Table_Name="C_OrderTax";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_OrderTax");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_OrderTax[").append(getID()).append("]");
return sb.toString();
}
/** Set Arciba Norm Code */
public void setArcibaNormCode (String ArcibaNormCode)
{
if (ArcibaNormCode != null && ArcibaNormCode.length() > 10)
{
log.warning("Length > 10 - truncated");
ArcibaNormCode = ArcibaNormCode.substring(0,10);
}
set_Value ("ArcibaNormCode", ArcibaNormCode);
}
/** Get Arciba Norm Code */
public String getArcibaNormCode() 
{
return (String)get_Value("ArcibaNormCode");
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
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
/** Set Tax.
Tax identifier */
public void setC_Tax_ID (int C_Tax_ID)
{
set_ValueNoCheck ("C_Tax_ID", new Integer(C_Tax_ID));
}
/** Get Tax.
Tax identifier */
public int getC_Tax_ID() 
{
Integer ii = (Integer)get_Value("C_Tax_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Price includes Tax.
Tax is included in the price  */
public void setIsTaxIncluded (boolean IsTaxIncluded)
{
set_Value ("IsTaxIncluded", new Boolean(IsTaxIncluded));
}
/** Get Price includes Tax.
Tax is included in the price  */
public boolean isTaxIncluded() 
{
Object oo = get_Value("IsTaxIncluded");
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
/** Set Rate.
Rate or Tax or Exchange */
public void setRate (BigDecimal Rate)
{
set_Value ("Rate", Rate);
}
/** Get Rate.
Rate or Tax or Exchange */
public BigDecimal getRate() 
{
BigDecimal bd = (BigDecimal)get_Value("Rate");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Tax Amount.
Tax Amount for a document */
public void setTaxAmt (BigDecimal TaxAmt)
{
if (TaxAmt == null) throw new IllegalArgumentException ("TaxAmt is mandatory");
set_ValueNoCheck ("TaxAmt", TaxAmt);
}
/** Get Tax Amount.
Tax Amount for a document */
public BigDecimal getTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Tax base Amount.
Base for calculating the tax amount */
public void setTaxBaseAmt (BigDecimal TaxBaseAmt)
{
if (TaxBaseAmt == null) throw new IllegalArgumentException ("TaxBaseAmt is mandatory");
set_ValueNoCheck ("TaxBaseAmt", TaxBaseAmt);
}
/** Get Tax base Amount.
Base for calculating the tax amount */
public BigDecimal getTaxBaseAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TaxBaseAmt");
if (bd == null) return Env.ZERO;
return bd;
}
}
