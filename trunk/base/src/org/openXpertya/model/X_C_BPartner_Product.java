/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_Product
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:28.281 */
public class X_C_BPartner_Product extends PO
{
/** Constructor estándar */
public X_C_BPartner_Product (Properties ctx, int C_BPartner_Product_ID, String trxName)
{
super (ctx, C_BPartner_Product_ID, trxName);
/** if (C_BPartner_Product_ID == 0)
{
setC_BPartner_ID (0);
setM_Product_ID (0);
setShelfLifeMinDays (0);
setShelfLifeMinPct (0);
}
 */
}
/** Load Constructor */
public X_C_BPartner_Product (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=632 */
public static final int Table_ID=632;

/** TableName=C_BPartner_Product */
public static final String Table_Name="C_BPartner_Product";

protected static KeyNamePair Model = new KeyNamePair(632,"C_BPartner_Product");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_Product[").append(getID()).append("]");
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
/** Set Quality Rating.
Method for rating vendors */
public void setQualityRating (BigDecimal QualityRating)
{
set_Value ("QualityRating", QualityRating);
}
/** Get Quality Rating.
Method for rating vendors */
public BigDecimal getQualityRating() 
{
BigDecimal bd = (BigDecimal)get_Value("QualityRating");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Min Shelf Life Days.
Minimum Shelf Life in days based on Product Instance Guarantee Date */
public void setShelfLifeMinDays (int ShelfLifeMinDays)
{
set_Value ("ShelfLifeMinDays", new Integer(ShelfLifeMinDays));
}
/** Get Min Shelf Life Days.
Minimum Shelf Life in days based on Product Instance Guarantee Date */
public int getShelfLifeMinDays() 
{
Integer ii = (Integer)get_Value("ShelfLifeMinDays");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Min Shelf Life %.
Minimum Shelf Life in percent based on Product Instance Guarantee Date */
public void setShelfLifeMinPct (int ShelfLifeMinPct)
{
set_Value ("ShelfLifeMinPct", new Integer(ShelfLifeMinPct));
}
/** Get Min Shelf Life %.
Minimum Shelf Life in percent based on Product Instance Guarantee Date */
public int getShelfLifeMinPct() 
{
Integer ii = (Integer)get_Value("ShelfLifeMinPct");
if (ii == null) return 0;
return ii.intValue();
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
if (VendorProductNo != null && VendorProductNo.length() > 30)
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
