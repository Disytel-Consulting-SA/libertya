/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_InventoryValue
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-07-27 19:01:03.362 */
public class X_T_InventoryValue extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_InventoryValue (Properties ctx, int T_InventoryValue_ID, String trxName)
{
super (ctx, T_InventoryValue_ID, trxName);
/** if (T_InventoryValue_ID == 0)
{
setAD_PInstance_ID (0);
setM_Product_ID (0);
setM_Warehouse_ID (0);
}
 */
}
/** Load Constructor */
public X_T_InventoryValue (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_InventoryValue");

/** TableName=T_InventoryValue */
public static final String Table_Name="T_InventoryValue";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_InventoryValue");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_InventoryValue[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_ValueNoCheck ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
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
/** Set Standard Cost.
Standard Costs */
public void setCostStandard (BigDecimal CostStandard)
{
set_Value ("CostStandard", CostStandard);
}
/** Get Standard Cost.
Standard Costs */
public BigDecimal getCostStandard() 
{
BigDecimal bd = (BigDecimal)get_Value("CostStandard");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Standard Cost Value.
Value in Standard Costs */
public void setCostStandardAmt (BigDecimal CostStandardAmt)
{
set_Value ("CostStandardAmt", CostStandardAmt);
}
/** Get Standard Cost Value.
Value in Standard Costs */
public BigDecimal getCostStandardAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CostStandardAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Valuation Date.
Date of valuation */
public void setDateValue (Timestamp DateValue)
{
set_Value ("DateValue", DateValue);
}
/** Get Valuation Date.
Date of valuation */
public Timestamp getDateValue() 
{
return (Timestamp)get_Value("DateValue");
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
/** Set M_Product_Category_ID */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
if (M_Product_Category_ID <= 0) set_Value ("M_Product_Category_ID", null);
 else 
set_Value ("M_Product_Category_ID", new Integer(M_Product_Category_ID));
}
/** Get M_Product_Category_ID */
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
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_Value ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Limit Price.
Lowest price for a product */
public void setPriceLimit (BigDecimal PriceLimit)
{
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
/** Set Limit price Value.
Value with limit price */
public void setPriceLimitAmt (BigDecimal PriceLimitAmt)
{
set_Value ("PriceLimitAmt", PriceLimitAmt);
}
/** Get Limit price Value.
Value with limit price */
public BigDecimal getPriceLimitAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceLimitAmt");
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
/** Set List price Value.
Valuation with List Price */
public void setPriceListAmt (BigDecimal PriceListAmt)
{
set_Value ("PriceListAmt", PriceListAmt);
}
/** Get List price Value.
Valuation with List Price */
public BigDecimal getPriceListAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceListAmt");
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
/** Set PO Price Value.
Valuation with PO Price */
public void setPricePOAmt (BigDecimal PricePOAmt)
{
set_Value ("PricePOAmt", PricePOAmt);
}
/** Get PO Price Value.
Valuation with PO Price */
public BigDecimal getPricePOAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PricePOAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Standard Price.
Standard Price */
public void setPriceStd (BigDecimal PriceStd)
{
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
/** Set Std Price Value.
Valuation with standard price */
public void setPriceStdAmt (BigDecimal PriceStdAmt)
{
set_Value ("PriceStdAmt", PriceStdAmt);
}
/** Get Std Price Value.
Valuation with standard price */
public BigDecimal getPriceStdAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceStdAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set On Hand Quantity.
On Hand Quantity */
public void setQtyOnHand (BigDecimal QtyOnHand)
{
set_Value ("QtyOnHand", QtyOnHand);
}
/** Get On Hand Quantity.
On Hand Quantity */
public BigDecimal getQtyOnHand() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyOnHand");
if (bd == null) return Env.ZERO;
return bd;
}
}
