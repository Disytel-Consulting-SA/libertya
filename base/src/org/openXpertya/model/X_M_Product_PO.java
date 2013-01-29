/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Product_PO
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:39.312 */
public class X_M_Product_PO extends PO
{
/** Constructor estándar */
public X_M_Product_PO (Properties ctx, int M_Product_PO_ID, String trxName)
{
super (ctx, M_Product_PO_ID, trxName);
/** if (M_Product_PO_ID == 0)
{
setC_BPartner_ID (0);	// 0
setIsCurrentVendor (true);	// Y
setM_Product_ID (0);	// @M_Product_ID@
setVendorProductNo (null);	// @Value@
}
 */
}
/** Load Constructor */
public X_M_Product_PO (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=210 */
public static final int Table_ID=210;

/** TableName=M_Product_PO */
public static final String Table_Name="M_Product_PO";

protected static KeyNamePair Model = new KeyNamePair(210,"M_Product_PO");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Product_PO[").append(getID()).append("]");
return sb.toString();
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
/** Set Cost per Order.
Fixed Cost Per Order */
public void setCostPerOrder (BigDecimal CostPerOrder)
{
set_Value ("CostPerOrder", CostPerOrder);
}
/** Get Cost per Order.
Fixed Cost Per Order */
public BigDecimal getCostPerOrder() 
{
BigDecimal bd = (BigDecimal)get_Value("CostPerOrder");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Actual Delivery Time.
Actual days between order and delivery */
public void setDeliveryTime_Actual (int DeliveryTime_Actual)
{
set_Value ("DeliveryTime_Actual", new Integer(DeliveryTime_Actual));
}
/** Get Actual Delivery Time.
Actual days between order and delivery */
public int getDeliveryTime_Actual() 
{
Integer ii = (Integer)get_Value("DeliveryTime_Actual");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Promised Delivery Time.
Promised days between order and delivery */
public void setDeliveryTime_Promised (int DeliveryTime_Promised)
{
set_Value ("DeliveryTime_Promised", new Integer(DeliveryTime_Promised));
}
/** Get Promised Delivery Time.
Promised days between order and delivery */
public int getDeliveryTime_Promised() 
{
Integer ii = (Integer)get_Value("DeliveryTime_Promised");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Discontinued.
This product is no longer available */
public void setDiscontinued (boolean Discontinued)
{
set_Value ("Discontinued", new Boolean(Discontinued));
}
/** Get Discontinued.
This product is no longer available */
public boolean isDiscontinued() 
{
Object oo = get_Value("Discontinued");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Discontinued by.
Discontinued By */
public void setDiscontinuedBy (Timestamp DiscontinuedBy)
{
set_Value ("DiscontinuedBy", DiscontinuedBy);
}
/** Get Discontinued by.
Discontinued By */
public Timestamp getDiscontinuedBy() 
{
return (Timestamp)get_Value("DiscontinuedBy");
}
/** Set Current vendor.
Use this Vendor for pricing and stock replenishment */
public void setIsCurrentVendor (boolean IsCurrentVendor)
{
set_Value ("IsCurrentVendor", new Boolean(IsCurrentVendor));
}
/** Get Current vendor.
Use this Vendor for pricing and stock replenishment */
public boolean isCurrentVendor() 
{
Object oo = get_Value("IsCurrentVendor");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
set_ValueNoCheck ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Manufacturer.
Manufacturer of the Product */
public void setManufacturer (String Manufacturer)
{
if (Manufacturer != null && Manufacturer.length() > 30)
{
log.warning("Length > 30 - truncated");
Manufacturer = Manufacturer.substring(0,29);
}
set_Value ("Manufacturer", Manufacturer);
}
/** Get Manufacturer.
Manufacturer of the Product */
public String getManufacturer() 
{
return (String)get_Value("Manufacturer");
}
/** Set Minimum Order Qty.
Minimum order quantity in UOM */
public void setOrder_Min (BigDecimal Order_Min)
{
set_Value ("Order_Min", Order_Min);
}
/** Get Minimum Order Qty.
Minimum order quantity in UOM */
public BigDecimal getOrder_Min() 
{
BigDecimal bd = (BigDecimal)get_Value("Order_Min");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Order Pack Qty.
Package order size in UOM (e.g. order set of 5 units) */
public void setOrder_Pack (BigDecimal Order_Pack)
{
set_Value ("Order_Pack", Order_Pack);
}
/** Get Order Pack Qty.
Package order size in UOM (e.g. order set of 5 units) */
public BigDecimal getOrder_Pack() 
{
BigDecimal bd = (BigDecimal)get_Value("Order_Pack");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Price effective.
Effective Date of Price */
public void setPriceEffective (Timestamp PriceEffective)
{
set_Value ("PriceEffective", PriceEffective);
}
/** Get Price effective.
Effective Date of Price */
public Timestamp getPriceEffective() 
{
return (Timestamp)get_Value("PriceEffective");
}
/** Set Last Invoice Price.
Price of the last invoice for the product */
public void setPriceLastInv (BigDecimal PriceLastInv)
{
set_ValueNoCheck ("PriceLastInv", PriceLastInv);
}
/** Get Last Invoice Price.
Price of the last invoice for the product */
public BigDecimal getPriceLastInv() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceLastInv");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Last PO Price.
Price of the last purchase order for the product */
public void setPriceLastPO (BigDecimal PriceLastPO)
{
set_ValueNoCheck ("PriceLastPO", PriceLastPO);
}
/** Get Last PO Price.
Price of the last purchase order for the product */
public BigDecimal getPriceLastPO() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceLastPO");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set List Price.
List Price */
public void setPriceList (BigDecimal PriceList)
{
set_Value ("PriceList", PriceList);
}
/** Get List Price.
List Price */
public BigDecimal getPriceList() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceList");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PO Price.
Price based on a purchase order */
public void setPricePO (BigDecimal PricePO)
{
set_Value ("PricePO", PricePO);
}
/** Get PO Price.
Price based on a purchase order */
public BigDecimal getPricePO() 
{
BigDecimal bd = (BigDecimal)get_Value("PricePO");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Quality Rating.
Method for rating vendors */
public void setQualityRating (int QualityRating)
{
set_Value ("QualityRating", new Integer(QualityRating));
}
/** Get Quality Rating.
Method for rating vendors */
public int getQualityRating() 
{
Integer ii = (Integer)get_Value("QualityRating");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Royalty Amount.
(Included) Amount for copyright, etc. */
public void setRoyaltyAmt (BigDecimal RoyaltyAmt)
{
set_Value ("RoyaltyAmt", RoyaltyAmt);
}
/** Get Royalty Amount.
(Included) Amount for copyright, etc. */
public BigDecimal getRoyaltyAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("RoyaltyAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public void setUPC (String UPC)
{
if (UPC != null && UPC.length() > 20)
{
log.warning("Length > 20 - truncated");
UPC = UPC.substring(0,19);
}
set_Value ("UPC", UPC);
}
/** Get UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public String getUPC() 
{
return (String)get_Value("UPC");
}
/** Set Partner Category.
Product Category of the Business Partner */
public void setVendorCategory (String VendorCategory)
{
if (VendorCategory != null && VendorCategory.length() > 30)
{
log.warning("Length > 30 - truncated");
VendorCategory = VendorCategory.substring(0,29);
}
set_Value ("VendorCategory", VendorCategory);
}
/** Get Partner Category.
Product Category of the Business Partner */
public String getVendorCategory() 
{
return (String)get_Value("VendorCategory");
}
/** Set Partner Product Key.
Product Key of the Business Partner */
public void setVendorProductNo (String VendorProductNo)
{
if (VendorProductNo == null) throw new IllegalArgumentException ("VendorProductNo is mandatory");
if (VendorProductNo.length() > 30)
{
log.warning("Length > 30 - truncated");
VendorProductNo = VendorProductNo.substring(0,29);
}
set_Value ("VendorProductNo", VendorProductNo);
}
/** Get Partner Product Key.
Product Key of the Business Partner */
public String getVendorProductNo() 
{
return (String)get_Value("VendorProductNo");
}
}
