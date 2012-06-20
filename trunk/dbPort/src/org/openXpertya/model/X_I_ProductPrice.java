/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_ProductPrice
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-03-29 12:55:47.756 */
public class X_I_ProductPrice extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_ProductPrice (Properties ctx, int I_ProductPrice_ID, String trxName)
{
super (ctx, I_ProductPrice_ID, trxName);
/** if (I_ProductPrice_ID == 0)
{
setI_IsImported (false);
setI_ProductPrice_ID (0);
setM_DiscountSchemaLine_ID (0);
setPriceLimit (Env.ZERO);
setPriceList (Env.ZERO);
setPriceStd (Env.ZERO);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_I_ProductPrice (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_ProductPrice");

/** TableName=I_ProductPrice */
public static final String Table_Name="I_ProductPrice";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_ProductPrice");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_ProductPrice[").append(getID()).append("]");
return sb.toString();
}
/** Set Attribute1Value */
public void setAttribute1Value (String Attribute1Value)
{
if (Attribute1Value != null && Attribute1Value.length() > 30)
{
log.warning("Length > 30 - truncated");
Attribute1Value = Attribute1Value.substring(0,30);
}
set_Value ("Attribute1Value", Attribute1Value);
}
/** Get Attribute1Value */
public String getAttribute1Value() 
{
return (String)get_Value("Attribute1Value");
}
/** Set Attribute2Value */
public void setAttribute2Value (String Attribute2Value)
{
if (Attribute2Value != null && Attribute2Value.length() > 30)
{
log.warning("Length > 30 - truncated");
Attribute2Value = Attribute2Value.substring(0,30);
}
set_Value ("Attribute2Value", Attribute2Value);
}
/** Get Attribute2Value */
public String getAttribute2Value() 
{
return (String)get_Value("Attribute2Value");
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
/** Set InstanceUPC */
public void setInstanceUPC (String InstanceUPC)
{
if (InstanceUPC != null && InstanceUPC.length() > 30)
{
log.warning("Length > 30 - truncated");
InstanceUPC = InstanceUPC.substring(0,30);
}
set_Value ("InstanceUPC", InstanceUPC);
}
/** Get InstanceUPC */
public String getInstanceUPC() 
{
return (String)get_Value("InstanceUPC");
}
/** Set Imported Product Price */
public void setI_ProductPrice_ID (int I_ProductPrice_ID)
{
set_ValueNoCheck ("I_ProductPrice_ID", new Integer(I_ProductPrice_ID));
}
/** Get Imported Product Price */
public int getI_ProductPrice_ID() 
{
Integer ii = (Integer)get_Value("I_ProductPrice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute Set Instance.
Product Attribute Set Instance */
public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
{
if (M_AttributeSetInstance_ID <= 0) set_Value ("M_AttributeSetInstance_ID", null);
 else 
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
/** Set Discount Pricelist.
Line of the pricelist trade discount schema */
public void setM_DiscountSchemaLine_ID (int M_DiscountSchemaLine_ID)
{
set_Value ("M_DiscountSchemaLine_ID", new Integer(M_DiscountSchemaLine_ID));
}
/** Get Discount Pricelist.
Line of the pricelist trade discount schema */
public int getM_DiscountSchemaLine_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountSchemaLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Price List Version.
Identifies a unique instance of a Price List */
public void setM_PriceList_Version_ID (int M_PriceList_Version_ID)
{
if (M_PriceList_Version_ID <= 0) set_Value ("M_PriceList_Version_ID", null);
 else 
set_Value ("M_PriceList_Version_ID", new Integer(M_PriceList_Version_ID));
}
/** Get Price List Version.
Identifies a unique instance of a Price List */
public int getM_PriceList_Version_ID() 
{
Integer ii = (Integer)get_Value("M_PriceList_Version_ID");
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
/** Set Product Value */
public void setM_Product_Value (String M_Product_Value)
{
if (M_Product_Value != null && M_Product_Value.length() > 40)
{
log.warning("Length > 40 - truncated");
M_Product_Value = M_Product_Value.substring(0,40);
}
set_Value ("M_Product_Value", M_Product_Value);
}
/** Get Product Value */
public String getM_Product_Value() 
{
return (String)get_Value("M_Product_Value");
}
/** Set PreviousPriceLimit */
public void setPreviousPriceLimit (BigDecimal PreviousPriceLimit)
{
set_Value ("PreviousPriceLimit", PreviousPriceLimit);
}
/** Get PreviousPriceLimit */
public BigDecimal getPreviousPriceLimit() 
{
BigDecimal bd = (BigDecimal)get_Value("PreviousPriceLimit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PreviousPriceList */
public void setPreviousPriceList (BigDecimal PreviousPriceList)
{
set_Value ("PreviousPriceList", PreviousPriceList);
}
/** Get PreviousPriceList */
public BigDecimal getPreviousPriceList() 
{
BigDecimal bd = (BigDecimal)get_Value("PreviousPriceList");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PreviousPriceStd */
public void setPreviousPriceStd (BigDecimal PreviousPriceStd)
{
set_Value ("PreviousPriceStd", PreviousPriceStd);
}
/** Get PreviousPriceStd */
public BigDecimal getPreviousPriceStd() 
{
BigDecimal bd = (BigDecimal)get_Value("PreviousPriceStd");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Limit Price.
Lowest price for a product */
public void setPriceLimit (BigDecimal PriceLimit)
{
if (PriceLimit == null) throw new IllegalArgumentException ("PriceLimit is mandatory");
set_Value ("PriceLimit", PriceLimit);
}
/** Get Limit Price.
Lowest price for a product */
public BigDecimal getPriceLimit() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceLimit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set List Price.
List Price */
public void setPriceList (BigDecimal PriceList)
{
if (PriceList == null) throw new IllegalArgumentException ("PriceList is mandatory");
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
/** Set Price List Name */
public void setPriceList_Name (String PriceList_Name)
{
if (PriceList_Name != null && PriceList_Name.length() > 50)
{
log.warning("Length > 50 - truncated");
PriceList_Name = PriceList_Name.substring(0,50);
}
set_Value ("PriceList_Name", PriceList_Name);
}
/** Get Price List Name */
public String getPriceList_Name() 
{
return (String)get_Value("PriceList_Name");
}
/** Set Standard Price.
Standard Price */
public void setPriceStd (BigDecimal PriceStd)
{
if (PriceStd == null) throw new IllegalArgumentException ("PriceStd is mandatory");
set_Value ("PriceStd", PriceStd);
}
/** Get Standard Price.
Standard Price */
public BigDecimal getPriceStd() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceStd");
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
/** Set ProductUPC */
public void setProductUPC (String ProductUPC)
{
if (ProductUPC != null && ProductUPC.length() > 30)
{
log.warning("Length > 30 - truncated");
ProductUPC = ProductUPC.substring(0,30);
}
set_Value ("ProductUPC", ProductUPC);
}
/** Get ProductUPC */
public String getProductUPC() 
{
return (String)get_Value("ProductUPC");
}
/** Set Stock */
public void setStock (BigDecimal Stock)
{
set_Value ("Stock", Stock);
}
/** Get Stock */
public BigDecimal getStock() 
{
BigDecimal bd = (BigDecimal)get_Value("Stock");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VariationPriceLimit */
public void setVariationPriceLimit (BigDecimal VariationPriceLimit)
{
set_Value ("VariationPriceLimit", VariationPriceLimit);
}
/** Get VariationPriceLimit */
public BigDecimal getVariationPriceLimit() 
{
BigDecimal bd = (BigDecimal)get_Value("VariationPriceLimit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VariationPriceList */
public void setVariationPriceList (BigDecimal VariationPriceList)
{
set_Value ("VariationPriceList", VariationPriceList);
}
/** Get VariationPriceList */
public BigDecimal getVariationPriceList() 
{
BigDecimal bd = (BigDecimal)get_Value("VariationPriceList");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VariationPriceStd */
public void setVariationPriceStd (BigDecimal VariationPriceStd)
{
set_Value ("VariationPriceStd", VariationPriceStd);
}
/** Get VariationPriceStd */
public BigDecimal getVariationPriceStd() 
{
BigDecimal bd = (BigDecimal)get_Value("VariationPriceStd");
if (bd == null) return Env.ZERO;
return bd;
}
}
