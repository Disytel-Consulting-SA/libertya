/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_Product
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-07-25 01:25:46.053 */
public class X_T_Product extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_Product (Properties ctx, int T_Product_ID, String trxName)
{
super (ctx, T_Product_ID, trxName);
/** if (T_Product_ID == 0)
{
setAD_PInstance_ID (0);
setCheckoutPlace (null);
setIsPurchased (false);
setIsSold (false);
setM_Product_ID (0);
setName (null);
setProductType (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_T_Product (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_Product");

/** TableName=T_Product */
public static final String Table_Name="T_Product";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_Product");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_Product[").append(getID()).append("]");
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
/** Set Business Partner Name */
public void setBPartner_Name (String BPartner_Name)
{
if (BPartner_Name != null && BPartner_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
BPartner_Name = BPartner_Name.substring(0,60);
}
set_Value ("BPartner_Name", BPartner_Name);
}
/** Get Business Partner Name */
public String getBPartner_Name() 
{
return (String)get_Value("BPartner_Name");
}
/** Set Business Partner Key.
The Key of the Business Partner */
public void setBPartner_Value (String BPartner_Value)
{
if (BPartner_Value != null && BPartner_Value.length() > 40)
{
log.warning("Length > 40 - truncated");
BPartner_Value = BPartner_Value.substring(0,40);
}
set_Value ("BPartner_Value", BPartner_Value);
}
/** Get Business Partner Key.
The Key of the Business Partner */
public String getBPartner_Value() 
{
return (String)get_Value("BPartner_Value");
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
/** Set Checkout Place.
Product Checkout Place */
public void setCheckoutPlace (String CheckoutPlace)
{
if (CheckoutPlace == null) throw new IllegalArgumentException ("CheckoutPlace is mandatory");
if (CheckoutPlace.length() > 1)
{
log.warning("Length > 1 - truncated");
CheckoutPlace = CheckoutPlace.substring(0,1);
}
set_Value ("CheckoutPlace", CheckoutPlace);
}
/** Get Checkout Place.
Product Checkout Place */
public String getCheckoutPlace() 
{
return (String)get_Value("CheckoutPlace");
}
/** Set Tax Category.
Tax Category */
public void setC_TaxCategory_ID (int C_TaxCategory_ID)
{
if (C_TaxCategory_ID <= 0) set_Value ("C_TaxCategory_ID", null);
 else 
set_Value ("C_TaxCategory_ID", new Integer(C_TaxCategory_ID));
}
/** Get Tax Category.
Tax Category */
public int getC_TaxCategory_ID() 
{
Integer ii = (Integer)get_Value("C_TaxCategory_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax Category Name.
Tax Category Name */
public void setC_TaxCategory_Name (String C_TaxCategory_Name)
{
if (C_TaxCategory_Name != null && C_TaxCategory_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
C_TaxCategory_Name = C_TaxCategory_Name.substring(0,60);
}
set_Value ("C_TaxCategory_Name", C_TaxCategory_Name);
}
/** Get Tax Category Name.
Tax Category Name */
public String getC_TaxCategory_Name() 
{
return (String)get_Value("C_TaxCategory_Name");
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
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Purchased.
Organization purchases this product */
public void setIsPurchased (boolean IsPurchased)
{
set_Value ("IsPurchased", new Boolean(IsPurchased));
}
/** Get Purchased.
Organization purchases this product */
public boolean isPurchased() 
{
Object oo = get_Value("IsPurchased");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sold.
Organization sells this product */
public void setIsSold (boolean IsSold)
{
set_Value ("IsSold", new Boolean(IsSold));
}
/** Get Sold.
Organization sells this product */
public boolean isSold() 
{
Object oo = get_Value("IsSold");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set M_Product_Family_ID */
public void setM_Product_Family_ID (int M_Product_Family_ID)
{
if (M_Product_Family_ID <= 0) set_Value ("M_Product_Family_ID", null);
 else 
set_Value ("M_Product_Family_ID", new Integer(M_Product_Family_ID));
}
/** Get M_Product_Family_ID */
public int getM_Product_Family_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Family_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_Product_Gamas_ID */
public void setM_Product_Gamas_ID (int M_Product_Gamas_ID)
{
if (M_Product_Gamas_ID <= 0) set_Value ("M_Product_Gamas_ID", null);
 else 
set_Value ("M_Product_Gamas_ID", new Integer(M_Product_Gamas_ID));
}
/** Get M_Product_Gamas_ID */
public int getM_Product_Gamas_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Gamas_ID");
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
/** Set M_Product_Lines_ID */
public void setM_Product_Lines_ID (int M_Product_Lines_ID)
{
if (M_Product_Lines_ID <= 0) set_Value ("M_Product_Lines_ID", null);
 else 
set_Value ("M_Product_Lines_ID", new Integer(M_Product_Lines_ID));
}
/** Get M_Product_Lines_ID */
public int getM_Product_Lines_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Lines_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 255)
{
log.warning("Length > 255 - truncated");
Name = Name.substring(0,255);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Organization Name.
Name of the Organization */
public void setOrgName (String OrgName)
{
if (OrgName != null && OrgName.length() > 60)
{
log.warning("Length > 60 - truncated");
OrgName = OrgName.substring(0,60);
}
set_Value ("OrgName", OrgName);
}
/** Get Organization Name.
Name of the Organization */
public String getOrgName() 
{
return (String)get_Value("OrgName");
}
/** Set Org Key.
Key of the Organization */
public void setOrgValue (String OrgValue)
{
if (OrgValue != null && OrgValue.length() > 40)
{
log.warning("Length > 40 - truncated");
OrgValue = OrgValue.substring(0,40);
}
set_Value ("OrgValue", OrgValue);
}
/** Get Org Key.
Key of the Organization */
public String getOrgValue() 
{
return (String)get_Value("OrgValue");
}
/** Set Product Category Name */
public void setProductCategory_Name (String ProductCategory_Name)
{
if (ProductCategory_Name != null && ProductCategory_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
ProductCategory_Name = ProductCategory_Name.substring(0,60);
}
set_Value ("ProductCategory_Name", ProductCategory_Name);
}
/** Get Product Category Name */
public String getProductCategory_Name() 
{
return (String)get_Value("ProductCategory_Name");
}
/** Set Product Category Key */
public void setProductCategory_Value (String ProductCategory_Value)
{
if (ProductCategory_Value != null && ProductCategory_Value.length() > 40)
{
log.warning("Length > 40 - truncated");
ProductCategory_Value = ProductCategory_Value.substring(0,40);
}
set_Value ("ProductCategory_Value", ProductCategory_Value);
}
/** Get Product Category Key */
public String getProductCategory_Value() 
{
return (String)get_Value("ProductCategory_Value");
}
/** Set Product Family Name */
public void setProductFamily_Name (String ProductFamily_Name)
{
if (ProductFamily_Name != null && ProductFamily_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
ProductFamily_Name = ProductFamily_Name.substring(0,60);
}
set_Value ("ProductFamily_Name", ProductFamily_Name);
}
/** Get Product Family Name */
public String getProductFamily_Name() 
{
return (String)get_Value("ProductFamily_Name");
}
/** Set Product Family Value */
public void setProductFamily_Value (String ProductFamily_Value)
{
if (ProductFamily_Value != null && ProductFamily_Value.length() > 60)
{
log.warning("Length > 60 - truncated");
ProductFamily_Value = ProductFamily_Value.substring(0,60);
}
set_Value ("ProductFamily_Value", ProductFamily_Value);
}
/** Get Product Family Value */
public String getProductFamily_Value() 
{
return (String)get_Value("ProductFamily_Value");
}
/** Set Product Gamas Name */
public void setProductGamas_Name (String ProductGamas_Name)
{
if (ProductGamas_Name != null && ProductGamas_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
ProductGamas_Name = ProductGamas_Name.substring(0,60);
}
set_Value ("ProductGamas_Name", ProductGamas_Name);
}
/** Get Product Gamas Name */
public String getProductGamas_Name() 
{
return (String)get_Value("ProductGamas_Name");
}
/** Set Product Gamas Value */
public void setProductGamas_Value (String ProductGamas_Value)
{
if (ProductGamas_Value != null && ProductGamas_Value.length() > 100)
{
log.warning("Length > 100 - truncated");
ProductGamas_Value = ProductGamas_Value.substring(0,100);
}
set_Value ("ProductGamas_Value", ProductGamas_Value);
}
/** Get Product Gamas Value */
public String getProductGamas_Value() 
{
return (String)get_Value("ProductGamas_Value");
}
/** Set Product Lines Name */
public void setProductLines_Name (String ProductLines_Name)
{
if (ProductLines_Name != null && ProductLines_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
ProductLines_Name = ProductLines_Name.substring(0,60);
}
set_Value ("ProductLines_Name", ProductLines_Name);
}
/** Get Product Lines Name */
public String getProductLines_Name() 
{
return (String)get_Value("ProductLines_Name");
}
/** Set Product Lines Value */
public void setProductLines_Value (String ProductLines_Value)
{
if (ProductLines_Value != null && ProductLines_Value.length() > 40)
{
log.warning("Length > 40 - truncated");
ProductLines_Value = ProductLines_Value.substring(0,40);
}
set_Value ("ProductLines_Value", ProductLines_Value);
}
/** Get Product Lines Value */
public String getProductLines_Value() 
{
return (String)get_Value("ProductLines_Value");
}
/** Set Product Type.
Type of product */
public void setProductType (String ProductType)
{
if (ProductType == null) throw new IllegalArgumentException ("ProductType is mandatory");
if (ProductType.length() > 1)
{
log.warning("Length > 1 - truncated");
ProductType = ProductType.substring(0,1);
}
set_Value ("ProductType", ProductType);
}
/** Get Product Type.
Type of product */
public String getProductType() 
{
return (String)get_Value("ProductType");
}
/** Set Symbol.
Symbol for a Unit of Measure */
public void setUOMSymbol (String UOMSymbol)
{
if (UOMSymbol != null && UOMSymbol.length() > 10)
{
log.warning("Length > 10 - truncated");
UOMSymbol = UOMSymbol.substring(0,10);
}
set_Value ("UOMSymbol", UOMSymbol);
}
/** Get Symbol.
Symbol for a Unit of Measure */
public String getUOMSymbol() 
{
return (String)get_Value("UOMSymbol");
}
/** Set UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public void setUPC (String UPC)
{
if (UPC != null && UPC.length() > 30)
{
log.warning("Length > 30 - truncated");
UPC = UPC.substring(0,30);
}
set_Value ("UPC", UPC);
}
/** Get UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public String getUPC() 
{
return (String)get_Value("UPC");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
/** Set UOM Code.
UOM EDI X12 Code */
public void setX12DE355 (String X12DE355)
{
if (X12DE355 != null && X12DE355.length() > 4)
{
log.warning("Length > 4 - truncated");
X12DE355 = X12DE355.substring(0,4);
}
set_Value ("X12DE355", X12DE355);
}
/** Get UOM Code.
UOM EDI X12 Code */
public String getX12DE355() 
{
return (String)get_Value("X12DE355");
}
}
