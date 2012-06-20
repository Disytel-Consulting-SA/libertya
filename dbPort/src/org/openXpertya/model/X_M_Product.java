/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Product
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-12-02 16:44:45.978 */
public class X_M_Product extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Product (Properties ctx, int M_Product_ID, String trxName)
{
super (ctx, M_Product_ID, trxName);
/** if (M_Product_ID == 0)
{
setAmortizationPerc (Env.ZERO);
setCheckoutPlace (null);	// B
setC_TaxCategory_ID (0);
setC_UOM_ID (0);
setIsBOM (false);	// N
setIsDropShip (false);
setIsInvoicePrintDetails (false);
setIsPickListPrintDetails (false);
setIsPurchased (true);	// Y
setIsSelfService (true);	// Y
setIsSold (true);	// Y
setIsStocked (true);	// Y
setIsSummary (false);
setIsVerified (false);	// N
setIsWebStoreFeatured (false);
setM_AttributeSetInstance_ID (0);
setM_Product_Category_ID (0);
setM_Product_ID (0);
setName (null);
setProductType (null);	// I
setValue (null);
setYearLife (0);
}
 */
}
/** Load Constructor */
public X_M_Product (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Product");

/** TableName=M_Product */
public static final String Table_Name="M_Product";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Product");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Product[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
/** Set Amortization Percentage.
Anual Amortization Percentage */
public void setAmortizationPerc (BigDecimal AmortizationPerc)
{
if (AmortizationPerc == null) throw new IllegalArgumentException ("AmortizationPerc is mandatory");
set_Value ("AmortizationPerc", AmortizationPerc);
}
/** Get Amortization Percentage.
Anual Amortization Percentage */
public BigDecimal getAmortizationPerc() 
{
BigDecimal bd = (BigDecimal)get_Value("AmortizationPerc");
if (bd == null) return Env.ZERO;
return bd;
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
if (CheckoutPlace.equals("W") || CheckoutPlace.equals("P") || CheckoutPlace.equals("B"));
 else throw new IllegalArgumentException ("CheckoutPlace Invalid value - Reference = CHECKOUTPLACE_AD_Reference_ID - W - P - B");
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
/** Set common_ref */
public void setcommon_ref (BigDecimal common_ref)
{
set_Value ("common_ref", common_ref);
}
/** Get common_ref */
public BigDecimal getcommon_ref() 
{
BigDecimal bd = (BigDecimal)get_Value("common_ref");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Revenue Recognition.
Method for recording revenue */
public void setC_RevenueRecognition_ID (int C_RevenueRecognition_ID)
{
if (C_RevenueRecognition_ID <= 0) set_Value ("C_RevenueRecognition_ID", null);
 else 
set_Value ("C_RevenueRecognition_ID", new Integer(C_RevenueRecognition_ID));
}
/** Get Revenue Recognition.
Method for recording revenue */
public int getC_RevenueRecognition_ID() 
{
Integer ii = (Integer)get_Value("C_RevenueRecognition_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Subscription Type.
Type of subscription */
public void setC_SubscriptionType_ID (int C_SubscriptionType_ID)
{
if (C_SubscriptionType_ID <= 0) set_Value ("C_SubscriptionType_ID", null);
 else 
set_Value ("C_SubscriptionType_ID", new Integer(C_SubscriptionType_ID));
}
/** Get Subscription Type.
Type of subscription */
public int getC_SubscriptionType_ID() 
{
Integer ii = (Integer)get_Value("C_SubscriptionType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax Category.
Tax Category */
public void setC_TaxCategory_ID (int C_TaxCategory_ID)
{
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
/** Set Download URL.
URL of the Download files */
public void setDownloadURL (String DownloadURL)
{
if (DownloadURL != null && DownloadURL.length() > 120)
{
log.warning("Length > 120 - truncated");
DownloadURL = DownloadURL.substring(0,120);
}
set_Value ("DownloadURL", DownloadURL);
}
/** Get Download URL.
URL of the Download files */
public String getDownloadURL() 
{
return (String)get_Value("DownloadURL");
}
/** Set Guarantee Days.
Number of days the product is guaranteed or available */
public void setGuaranteeDays (int GuaranteeDays)
{
set_Value ("GuaranteeDays", new Integer(GuaranteeDays));
}
/** Get Guarantee Days.
Number of days the product is guaranteed or available */
public int getGuaranteeDays() 
{
Integer ii = (Integer)get_Value("GuaranteeDays");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Min Guarantee Days.
Minumum number of guarantee days */
public void setGuaranteeDaysMin (int GuaranteeDaysMin)
{
set_Value ("GuaranteeDaysMin", new Integer(GuaranteeDaysMin));
}
/** Get Min Guarantee Days.
Minumum number of guarantee days */
public int getGuaranteeDaysMin() 
{
Integer ii = (Integer)get_Value("GuaranteeDaysMin");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Drop Shipment.
Drop Shipments are sent from the Vendor directly to the Customer */
public void setIsDropShip (boolean IsDropShip)
{
set_Value ("IsDropShip", new Boolean(IsDropShip));
}
/** Get Drop Shipment.
Drop Shipments are sent from the Vendor directly to the Customer */
public boolean isDropShip() 
{
Object oo = get_Value("IsDropShip");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsHelp */
public void setIsHelp (boolean IsHelp)
{
set_Value ("IsHelp", new Boolean(IsHelp));
}
/** Get IsHelp */
public boolean isHelp() 
{
Object oo = get_Value("IsHelp");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Print detail records on invoice .
Print detail BOM elements on the invoice */
public void setIsInvoicePrintDetails (boolean IsInvoicePrintDetails)
{
set_Value ("IsInvoicePrintDetails", new Boolean(IsInvoicePrintDetails));
}
/** Get Print detail records on invoice .
Print detail BOM elements on the invoice */
public boolean isInvoicePrintDetails() 
{
Object oo = get_Value("IsInvoicePrintDetails");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Print detail records on pick list.
Print detail BOM elements on the pick list */
public void setIsPickListPrintDetails (boolean IsPickListPrintDetails)
{
set_Value ("IsPickListPrintDetails", new Boolean(IsPickListPrintDetails));
}
/** Get Print detail records on pick list.
Print detail BOM elements on the pick list */
public boolean isPickListPrintDetails() 
{
Object oo = get_Value("IsPickListPrintDetails");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public void setIsSelfService (boolean IsSelfService)
{
set_Value ("IsSelfService", new Boolean(IsSelfService));
}
/** Get Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public boolean isSelfService() 
{
Object oo = get_Value("IsSelfService");
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
/** Set Stocked.
Organization stocks this product */
public void setIsStocked (boolean IsStocked)
{
set_Value ("IsStocked", new Boolean(IsStocked));
}
/** Get Stocked.
Organization stocks this product */
public boolean isStocked() 
{
Object oo = get_Value("IsStocked");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Summary Level.
This is a summary entity */
public void setIsSummary (boolean IsSummary)
{
set_Value ("IsSummary", new Boolean(IsSummary));
}
/** Get Summary Level.
This is a summary entity */
public boolean isSummary() 
{
Object oo = get_Value("IsSummary");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsToFormule */
public void setIsToFormule (boolean IsToFormule)
{
set_Value ("IsToFormule", new Boolean(IsToFormule));
}
/** Get IsToFormule */
public boolean isToFormule() 
{
Object oo = get_Value("IsToFormule");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Verified.
The BOM configuration has been verified */
public void setIsVerified (boolean IsVerified)
{
set_ValueNoCheck ("IsVerified", new Boolean(IsVerified));
}
/** Get Verified.
The BOM configuration has been verified */
public boolean isVerified() 
{
Object oo = get_Value("IsVerified");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Featured in Web Store.
If selected, the product is displayed in the inital or any empy search */
public void setIsWebStoreFeatured (boolean IsWebStoreFeatured)
{
set_Value ("IsWebStoreFeatured", new Boolean(IsWebStoreFeatured));
}
/** Get Featured in Web Store.
If selected, the product is displayed in the inital or any empy search */
public boolean isWebStoreFeatured() 
{
Object oo = get_Value("IsWebStoreFeatured");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Low Level */
public void setLowLevel (int LowLevel)
{
set_ValueNoCheck ("LowLevel", new Integer(LowLevel));
}
/** Get Low Level */
public int getLowLevel() 
{
Integer ii = (Integer)get_Value("LowLevel");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute Set.
Product Attribute Set */
public void setM_AttributeSet_ID (int M_AttributeSet_ID)
{
if (M_AttributeSet_ID <= 0) set_Value ("M_AttributeSet_ID", null);
 else 
set_Value ("M_AttributeSet_ID", new Integer(M_AttributeSet_ID));
}
/** Get Attribute Set.
Product Attribute Set */
public int getM_AttributeSet_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSet_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute Set Instance.
Product Attribute Set Instance */
public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
{
set_Value ("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
}
/** Get Attribute Set Instance.
Product Attribute Set Instance */
public int getM_AttributeSetInstance_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSetInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Freight Category.
Category of the Freight */
public void setM_FreightCategory_ID (int M_FreightCategory_ID)
{
if (M_FreightCategory_ID <= 0) set_Value ("M_FreightCategory_ID", null);
 else 
set_Value ("M_FreightCategory_ID", new Integer(M_FreightCategory_ID));
}
/** Get Freight Category.
Category of the Freight */
public int getM_FreightCategory_ID() 
{
Integer ii = (Integer)get_Value("M_FreightCategory_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set m_intrastatcode_id */
public void setm_intrastatcode_id (BigDecimal m_intrastatcode_id)
{
set_Value ("m_intrastatcode_id", m_intrastatcode_id);
}
/** Get m_intrastatcode_id */
public BigDecimal getm_intrastatcode_id() 
{
BigDecimal bd = (BigDecimal)get_Value("m_intrastatcode_id");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Locator.
Warehouse Locator */
public void setM_Locator_ID (int M_Locator_ID)
{
if (M_Locator_ID <= 0) set_Value ("M_Locator_ID", null);
 else 
set_Value ("M_Locator_ID", new Integer(M_Locator_ID));
}
/** Get Locator.
Warehouse Locator */
public int getM_Locator_ID() 
{
Integer ii = (Integer)get_Value("M_Locator_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCT_CATEGORY_ID_AD_Reference_ID = MReference.getReferenceID("M_Product Category ");
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
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
public static final int M_PRODUCT_FAMILY_ID_AD_Reference_ID = MReference.getReferenceID("m_product_family");
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
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
if (ProductType.equals("I") || ProductType.equals("S") || ProductType.equals("E") || ProductType.equals("O") || ProductType.equals("R") || ProductType.equals("A"));
 else throw new IllegalArgumentException ("ProductType Invalid value - Reference = PRODUCTTYPE_AD_Reference_ID - I - S - E - O - R - A");
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
/** Set Mail Template.
Text templates for mailings */
public void setR_MailText_ID (int R_MailText_ID)
{
if (R_MailText_ID <= 0) set_Value ("R_MailText_ID", null);
 else 
set_Value ("R_MailText_ID", new Integer(R_MailText_ID));
}
/** Get Mail Template.
Text templates for mailings */
public int getR_MailText_ID() 
{
Integer ii = (Integer)get_Value("R_MailText_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SALESREP_ID_AD_Reference_ID = MReference.getReferenceID("AD_User - SalesRep");
/** Set Sales Representative.
Sales Representative or Company Agent */
public void setSalesRep_ID (int SalesRep_ID)
{
if (SalesRep_ID <= 0) set_Value ("SalesRep_ID", null);
 else 
set_Value ("SalesRep_ID", new Integer(SalesRep_ID));
}
/** Get Sales Representative.
Sales Representative or Company Agent */
public int getSalesRep_ID() 
{
Integer ii = (Integer)get_Value("SalesRep_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Expense Type.
Expense report type */
public void setS_ExpenseType_ID (int S_ExpenseType_ID)
{
if (S_ExpenseType_ID <= 0) set_ValueNoCheck ("S_ExpenseType_ID", null);
 else 
set_ValueNoCheck ("S_ExpenseType_ID", new Integer(S_ExpenseType_ID));
}
/** Get Expense Type.
Expense report type */
public int getS_ExpenseType_ID() 
{
Integer ii = (Integer)get_Value("S_ExpenseType_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Resource.
Resource */
public void setS_Resource_ID (int S_Resource_ID)
{
if (S_Resource_ID <= 0) set_ValueNoCheck ("S_Resource_ID", null);
 else 
set_ValueNoCheck ("S_Resource_ID", new Integer(S_Resource_ID));
}
/** Get Resource.
Resource */
public int getS_Resource_ID() 
{
Integer ii = (Integer)get_Value("S_Resource_ID");
if (ii == null) return 0;
return ii.intValue();
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getValue());
}
/** Set Version No.
Version Number */
public void setVersionNo (String VersionNo)
{
if (VersionNo != null && VersionNo.length() > 20)
{
log.warning("Length > 20 - truncated");
VersionNo = VersionNo.substring(0,20);
}
set_Value ("VersionNo", VersionNo);
}
/** Get Version No.
Version Number */
public String getVersionNo() 
{
return (String)get_Value("VersionNo");
}
/** Set Volume.
Volume of a product */
public void setVolume (BigDecimal Volume)
{
set_Value ("Volume", Volume);
}
/** Get Volume.
Volume of a product */
public BigDecimal getVolume() 
{
BigDecimal bd = (BigDecimal)get_Value("Volume");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Weight.
Weight of a product */
public void setWeight (BigDecimal Weight)
{
set_Value ("Weight", Weight);
}
/** Get Weight.
Weight of a product */
public BigDecimal getWeight() 
{
BigDecimal bd = (BigDecimal)get_Value("Weight");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Year Life */
public void setYearLife (int YearLife)
{
set_Value ("YearLife", new Integer(YearLife));
}
/** Get Year Life */
public int getYearLife() 
{
Integer ii = (Integer)get_Value("YearLife");
if (ii == null) return 0;
return ii.intValue();
}
}
