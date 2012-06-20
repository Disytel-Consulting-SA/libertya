/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RfQLineQty
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.984 */
public class X_C_RfQLineQty extends PO
{
/** Constructor estándar */
public X_C_RfQLineQty (Properties ctx, int C_RfQLineQty_ID, String trxName)
{
super (ctx, C_RfQLineQty_ID, trxName);
/** if (C_RfQLineQty_ID == 0)
{
setBenchmarkPrice (Env.ZERO);
setC_RfQLineQty_ID (0);
setC_RfQLine_ID (0);
setC_UOM_ID (0);
setIsOfferQty (false);
setIsPurchaseQty (false);
setIsRfQQty (true);	// Y
setQty (Env.ZERO);	// 1
}
 */
}
/** Load Constructor */
public X_C_RfQLineQty (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=675 */
public static final int Table_ID=675;

/** TableName=C_RfQLineQty */
public static final String Table_Name="C_RfQLineQty";

protected static KeyNamePair Model = new KeyNamePair(675,"C_RfQLineQty");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RfQLineQty[").append(getID()).append("]");
return sb.toString();
}
/** Set Benchmark Price.
Price to compare responses to */
public void setBenchmarkPrice (BigDecimal BenchmarkPrice)
{
if (BenchmarkPrice == null) throw new IllegalArgumentException ("BenchmarkPrice is mandatory");
set_Value ("BenchmarkPrice", BenchmarkPrice);
}
/** Get Benchmark Price.
Price to compare responses to */
public BigDecimal getBenchmarkPrice() 
{
BigDecimal bd = (BigDecimal)get_Value("BenchmarkPrice");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Best Response Amount.
Best Response Amount */
public void setBestResponseAmt (BigDecimal BestResponseAmt)
{
set_Value ("BestResponseAmt", BestResponseAmt);
}
/** Get Best Response Amount.
Best Response Amount */
public BigDecimal getBestResponseAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("BestResponseAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set RfQ Line Quantity.
Request for Quotation Line Quantity */
public void setC_RfQLineQty_ID (int C_RfQLineQty_ID)
{
set_ValueNoCheck ("C_RfQLineQty_ID", new Integer(C_RfQLineQty_ID));
}
/** Get RfQ Line Quantity.
Request for Quotation Line Quantity */
public int getC_RfQLineQty_ID() 
{
Integer ii = (Integer)get_Value("C_RfQLineQty_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set RfQ Line.
Request for Quotation Line */
public void setC_RfQLine_ID (int C_RfQLine_ID)
{
set_ValueNoCheck ("C_RfQLine_ID", new Integer(C_RfQLine_ID));
}
/** Get RfQ Line.
Request for Quotation Line */
public int getC_RfQLine_ID() 
{
Integer ii = (Integer)get_Value("C_RfQLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_UOM_ID()));
}
/** Set Offer Quantity.
This quantity is used in the Offer to the Customer */
public void setIsOfferQty (boolean IsOfferQty)
{
set_Value ("IsOfferQty", new Boolean(IsOfferQty));
}
/** Get Offer Quantity.
This quantity is used in the Offer to the Customer */
public boolean isOfferQty() 
{
Object oo = get_Value("IsOfferQty");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Purchase Quantity.
This quantity is used in the Purchase Order to the Supplier */
public void setIsPurchaseQty (boolean IsPurchaseQty)
{
set_Value ("IsPurchaseQty", new Boolean(IsPurchaseQty));
}
/** Get Purchase Quantity.
This quantity is used in the Purchase Order to the Supplier */
public boolean isPurchaseQty() 
{
Object oo = get_Value("IsPurchaseQty");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set RfQ Quantity.
The quantity is used when generating RfQ Responses */
public void setIsRfQQty (boolean IsRfQQty)
{
set_Value ("IsRfQQty", new Boolean(IsRfQQty));
}
/** Get RfQ Quantity.
The quantity is used when generating RfQ Responses */
public boolean isRfQQty() 
{
Object oo = get_Value("IsRfQQty");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Margin %.
Margin for a product as a percentage */
public void setMargin (BigDecimal Margin)
{
set_Value ("Margin", Margin);
}
/** Get Margin %.
Margin for a product as a percentage */
public BigDecimal getMargin() 
{
BigDecimal bd = (BigDecimal)get_Value("Margin");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Offer Amount.
Amount of the Offer */
public void setOfferAmt (BigDecimal OfferAmt)
{
set_Value ("OfferAmt", OfferAmt);
}
/** Get Offer Amount.
Amount of the Offer */
public BigDecimal getOfferAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("OfferAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
if (Qty == null) throw new IllegalArgumentException ("Qty is mandatory");
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
}
