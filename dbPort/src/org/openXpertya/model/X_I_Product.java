/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_Product
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-08-07 15:46:50.124 */
public class X_I_Product extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_Product (Properties ctx, int I_Product_ID, String trxName)
{
super (ctx, I_Product_ID, trxName);
/** if (I_Product_ID == 0)
{
setI_IsImported (false);
setI_Product_ID (0);
setIsBOM (false);
setIsPurchased (false);
setIsSold (false);
}
 */
}
/** Load Constructor */
public X_I_Product (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_Product");

/** TableName=I_Product */
public static final String Table_Name="I_Product";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_Product");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_Product[").append(getID()).append("]");
return sb.toString();
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
public static final int CHECKOUTPLACE_AD_Reference_ID = MReference.getReferenceID("M_Product Checkout Place");
/** Warehouse = W */
public static final String CHECKOUTPLACE_Warehouse = "W";
/** Point Of Sale = P */
public static final String CHECKOUTPLACE_PointOfSale = "P";
/** Warehouse & POS = B */
public static final String CHECKOUTPLACE_WarehousePOS = "B";
/** Set Checkout Place.
Product Checkout Place */
public void setCheckoutPlace (String CheckoutPlace)
{
if (CheckoutPlace == null || CheckoutPlace.equals("W") || CheckoutPlace.equals("P") || CheckoutPlace.equals("B"));
 else throw new IllegalArgumentException ("CheckoutPlace Invalid value - Reference = CHECKOUTPLACE_AD_Reference_ID - W - P - B");
if (CheckoutPlace != null && CheckoutPlace.length() > 1)
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
/** Set Classification.
Classification for grouping */
public void setClassification (String Classification)
{
if (Classification != null && Classification.length() > 1)
{
log.warning("Length > 1 - truncated");
Classification = Classification.substring(0,1);
}
set_Value ("Classification", Classification);
}
/** Get Classification.
Classification for grouping */
public String getClassification() 
{
return (String)get_Value("Classification");
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
/** Set Description URL.
URL for the description */
public void setDescriptionURL (String DescriptionURL)
{
if (DescriptionURL != null && DescriptionURL.length() > 120)
{
log.warning("Length > 120 - truncated");
DescriptionURL = DescriptionURL.substring(0,120);
}
set_Value ("DescriptionURL", DescriptionURL);
}
/** Get Description URL.
URL for the description */
public String getDescriptionURL() 
{
return (String)get_Value("DescriptionURL");
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
/** Set Document Note.
Additional information for a Document */
public void setDocumentNote (String DocumentNote)
{
if (DocumentNote != null && DocumentNote.length() > 2000)
{
log.warning("Length > 2000 - truncated");
DocumentNote = DocumentNote.substring(0,2000);
}
set_Value ("DocumentNote", DocumentNote);
}
/** Get Document Note.
Additional information for a Document */
public String getDocumentNote() 
{
return (String)get_Value("DocumentNote");
}
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,2000);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Import Error Message.
Messages generated from import process */
public void setI_ErrorMsg (String I_ErrorMsg)
{
if (I_ErrorMsg != null && I_ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
I_ErrorMsg = I_ErrorMsg.substring(0,2000);
}
set_Value ("I_ErrorMsg", I_ErrorMsg);
}
/** Get Import Error Message.
Messages generated from import process */
public String getI_ErrorMsg() 
{
return (String)get_Value("I_ErrorMsg");
}
/** Set Imported.
Has this import been processed */
public void setI_IsImported (boolean I_IsImported)
{
set_Value ("I_IsImported", new Boolean(I_IsImported));
}
/** Get Imported.
Has this import been processed */
public boolean isI_IsImported() 
{
Object oo = get_Value("I_IsImported");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Image URL.
URL of  image */
public void setImageURL (String ImageURL)
{
if (ImageURL != null && ImageURL.length() > 120)
{
log.warning("Length > 120 - truncated");
ImageURL = ImageURL.substring(0,120);
}
set_Value ("ImageURL", ImageURL);
}
/** Get Image URL.
URL of  image */
public String getImageURL() 
{
return (String)get_Value("ImageURL");
}
/** Set Import Product.
Import Item or Service */
public void setI_Product_ID (int I_Product_ID)
{
set_ValueNoCheck ("I_Product_ID", new Integer(I_Product_ID));
}
/** Get Import Product.
Import Item or Service */
public int getI_Product_ID() 
{
Integer ii = (Integer)get_Value("I_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bill of Materials.
Bill of Materials */
public void setIsBOM (boolean IsBOM)
{
set_Value ("IsBOM", new Boolean(IsBOM));
}
/** Get Bill of Materials.
Bill of Materials */
public boolean isBOM() 
{
Object oo = get_Value("IsBOM");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set ISO Currency Code.
Three letter ISO 4217 Code of the Currency */
public void setISO_Code (String ISO_Code)
{
if (ISO_Code != null && ISO_Code.length() > 3)
{
log.warning("Length > 3 - truncated");
ISO_Code = ISO_Code.substring(0,3);
}
set_Value ("ISO_Code", ISO_Code);
}
/** Get ISO Currency Code.
Three letter ISO 4217 Code of the Currency */
public String getISO_Code() 
{
return (String)get_Value("ISO_Code");
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
/** Set Manufacturer.
Manufacturer of the Product */
public void setManufacturer (String Manufacturer)
{
if (Manufacturer != null && Manufacturer.length() > 30)
{
log.warning("Length > 30 - truncated");
Manufacturer = Manufacturer.substring(0,30);
}
set_Value ("Manufacturer", Manufacturer);
}
/** Get Manufacturer.
Manufacturer of the Product */
public String getManufacturer() 
{
return (String)get_Value("Manufacturer");
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
/** Set Minimum Order Qty.
Minimum order quantity in UOM */
public void setOrder_Min (int Order_Min)
{
set_Value ("Order_Min", new Integer(Order_Min));
}
/** Get Minimum Order Qty.
Minimum order quantity in UOM */
public int getOrder_Min() 
{
Integer ii = (Integer)get_Value("Order_Min");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order Pack Qty.
Package order size in UOM (e.g. order set of 5 units) */
public void setOrder_Pack (int Order_Pack)
{
set_Value ("Order_Pack", new Integer(Order_Pack));
}
/** Get Order Pack Qty.
Package order size in UOM (e.g. order set of 5 units) */
public int getOrder_Pack() 
{
Integer ii = (Integer)get_Value("Order_Pack");
if (ii == null) return 0;
return ii.intValue();
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
public static final int PRODUCTTYPE_AD_Reference_ID = MReference.getReferenceID("M_Product_ProductType");
/** Item = I */
public static final String PRODUCTTYPE_Item = "I";
/** Service = S */
public static final String PRODUCTTYPE_Service = "S";
/** Expense type = E */
public static final String PRODUCTTYPE_ExpenseType = "E";
/** Online = O */
public static final String PRODUCTTYPE_Online = "O";
/** Resource = R */
public static final String PRODUCTTYPE_Resource = "R";
/** Assets = A */
public static final String PRODUCTTYPE_Assets = "A";
/** Set Product Type.
Type of product */
public void setProductType (String ProductType)
{
if (ProductType == null || ProductType.equals("I") || ProductType.equals("S") || ProductType.equals("E") || ProductType.equals("O") || ProductType.equals("R") || ProductType.equals("A"));
 else throw new IllegalArgumentException ("ProductType Invalid value - Reference = PRODUCTTYPE_AD_Reference_ID - I - S - E - O - R - A");
if (ProductType != null && ProductType.length() > 1)
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
/** Set Shelf Depth.
Shelf depth required */
public void setShelfDepth (int ShelfDepth)
{
set_Value ("ShelfDepth", new Integer(ShelfDepth));
}
/** Get Shelf Depth.
Shelf depth required */
public int getShelfDepth() 
{
Integer ii = (Integer)get_Value("ShelfDepth");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shelf Height.
Shelf height required */
public void setShelfHeight (int ShelfHeight)
{
set_Value ("ShelfHeight", new Integer(ShelfHeight));
}
/** Get Shelf Height.
Shelf height required */
public int getShelfHeight() 
{
Integer ii = (Integer)get_Value("ShelfHeight");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shelf Width.
Shelf width required */
public void setShelfWidth (int ShelfWidth)
{
set_Value ("ShelfWidth", new Integer(ShelfWidth));
}
/** Get Shelf Width.
Shelf width required */
public int getShelfWidth() 
{
Integer ii = (Integer)get_Value("ShelfWidth");
if (ii == null) return 0;
return ii.intValue();
}
/** Set SKU.
Stock Keeping Unit */
public void setSKU (String SKU)
{
if (SKU != null && SKU.length() > 30)
{
log.warning("Length > 30 - truncated");
SKU = SKU.substring(0,30);
}
set_Value ("SKU", SKU);
}
/** Get SKU.
Stock Keeping Unit */
public String getSKU() 
{
return (String)get_Value("SKU");
}
/** Set Units Per Pallet.
Units Per Pallet */
public void setUnitsPerPallet (int UnitsPerPallet)
{
set_Value ("UnitsPerPallet", new Integer(UnitsPerPallet));
}
/** Get Units Per Pallet.
Units Per Pallet */
public int getUnitsPerPallet() 
{
Integer ii = (Integer)get_Value("UnitsPerPallet");
if (ii == null) return 0;
return ii.intValue();
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
if (Value != null && Value.length() > 40)
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getValue());
}
/** Set Partner Category.
Product Category of the Business Partner */
public void setVendorCategory (String VendorCategory)
{
if (VendorCategory != null && VendorCategory.length() > 30)
{
log.warning("Length > 30 - truncated");
VendorCategory = VendorCategory.substring(0,30);
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
VendorProductNo = VendorProductNo.substring(0,30);
}
set_Value ("VendorProductNo", VendorProductNo);
}
/** Get Partner Product Key.
Product Key of the Business Partner */
public String getVendorProductNo() 
{
return (String)get_Value("VendorProductNo");
}
/** Set Volume.
Volume of a product */
public void setVolume (int Volume)
{
set_Value ("Volume", new Integer(Volume));
}
/** Get Volume.
Volume of a product */
public int getVolume() 
{
Integer ii = (Integer)get_Value("Volume");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Weight.
Weight of a product */
public void setWeight (int Weight)
{
set_Value ("Weight", new Integer(Weight));
}
/** Get Weight.
Weight of a product */
public int getWeight() 
{
Integer ii = (Integer)get_Value("Weight");
if (ii == null) return 0;
return ii.intValue();
}
/** Set UOM Code.
UOM EDI X12 Code */
public void setX12DE355 (String X12DE355)
{
if (X12DE355 != null && X12DE355.length() > 2)
{
log.warning("Length > 2 - truncated");
X12DE355 = X12DE355.substring(0,2);
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
