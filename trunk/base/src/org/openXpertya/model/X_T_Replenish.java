/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_Replenish
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.093 */
public class X_T_Replenish extends PO
{
/** Constructor estándar */
public X_T_Replenish (Properties ctx, int T_Replenish_ID, String trxName)
{
super (ctx, T_Replenish_ID, trxName);
/** if (T_Replenish_ID == 0)
{
setAD_PInstance_ID (0);
setC_BPartner_ID (0);
setLevel_Max (Env.ZERO);
setLevel_Min (Env.ZERO);
setM_Product_ID (0);
setM_Warehouse_ID (0);
setReplenishType (null);
}
 */
}
/** Load Constructor */
public X_T_Replenish (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=364 */
public static final int Table_ID=364;

/** TableName=T_Replenish */
public static final String Table_Name="T_Replenish";

protected static KeyNamePair Model = new KeyNamePair(364,"T_Replenish");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_Replenish[").append(getID()).append("]");
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
/** Set Maximum Level.
Maximum Inventory level for this product */
public void setLevel_Max (BigDecimal Level_Max)
{
if (Level_Max == null) throw new IllegalArgumentException ("Level_Max is mandatory");
set_Value ("Level_Max", Level_Max);
}
/** Get Maximum Level.
Maximum Inventory level for this product */
public BigDecimal getLevel_Max() 
{
BigDecimal bd = (BigDecimal)get_Value("Level_Max");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Minimum Level.
Minimum Inventory level for this product */
public void setLevel_Min (BigDecimal Level_Min)
{
if (Level_Min == null) throw new IllegalArgumentException ("Level_Min is mandatory");
set_Value ("Level_Min", Level_Min);
}
/** Get Minimum Level.
Minimum Inventory level for this product */
public BigDecimal getLevel_Min() 
{
BigDecimal bd = (BigDecimal)get_Value("Level_Min");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_ValueNoCheck ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Ordered Quantity.
Ordered Quantity */
public void setQtyOrdered (BigDecimal QtyOrdered)
{
set_Value ("QtyOrdered", QtyOrdered);
}
/** Get Ordered Quantity.
Ordered Quantity */
public BigDecimal getQtyOrdered() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyOrdered");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Reserved Quantity.
Reserved Quantity */
public void setQtyReserved (BigDecimal QtyReserved)
{
set_Value ("QtyReserved", QtyReserved);
}
/** Get Reserved Quantity.
Reserved Quantity */
public BigDecimal getQtyReserved() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyReserved");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Quantity to Order */
public void setQtyToOrder (BigDecimal QtyToOrder)
{
set_Value ("QtyToOrder", QtyToOrder);
}
/** Get Quantity to Order */
public BigDecimal getQtyToOrder() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyToOrder");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int REPLENISHTYPE_AD_Reference_ID=164;
/** Maintain Maximum Level = 2 */
public static final String REPLENISHTYPE_MaintainMaximumLevel = "2";
/** Manual = 0 */
public static final String REPLENISHTYPE_Manual = "0";
/** Reorder below Minimum Level = 1 */
public static final String REPLENISHTYPE_ReorderBelowMinimumLevel = "1";
/** Automatic Replenish = 3 */
public static final String REPLENISHTYPE_AutomaticReplenish = "3";
/** Automatic Replenish(all) = 4 */
public static final String REPLENISHTYPE_AutomaticReplenishAll = "4";
/** Set Replenish Type.
Method for re-ordering a product */
public void setReplenishType (String ReplenishType)
{
if (ReplenishType.equals("2") || ReplenishType.equals("0") || ReplenishType.equals("1") || ReplenishType.equals("3") || ReplenishType.equals("4"));
 else throw new IllegalArgumentException ("ReplenishType Invalid value - Reference_ID=164 - 2 - 0 - 1 - 3 - 4");
if (ReplenishType == null) throw new IllegalArgumentException ("ReplenishType is mandatory");
if (ReplenishType.length() > 1)
{
log.warning("Length > 1 - truncated");
ReplenishType = ReplenishType.substring(0,0);
}
set_Value ("ReplenishType", ReplenishType);
}
/** Get Replenish Type.
Method for re-ordering a product */
public String getReplenishType() 
{
return (String)get_Value("ReplenishType");
}
public static final int REPLENISHMENTCREATE_AD_Reference_ID=329;
/** Purchase Order = P */
public static final String REPLENISHMENTCREATE_PurchaseOrder = "P";
/** Requisition = R */
public static final String REPLENISHMENTCREATE_Requisition = "R";
/** Set Create.
Create from Replenishment */
public void setReplenishmentCreate (String ReplenishmentCreate)
{
if (ReplenishmentCreate == null || ReplenishmentCreate.equals("P") || ReplenishmentCreate.equals("R"));
 else throw new IllegalArgumentException ("ReplenishmentCreate Invalid value - Reference_ID=329 - P - R");
if (ReplenishmentCreate != null && ReplenishmentCreate.length() > 1)
{
log.warning("Length > 1 - truncated");
ReplenishmentCreate = ReplenishmentCreate.substring(0,0);
}
set_Value ("ReplenishmentCreate", ReplenishmentCreate);
}
/** Get Create.
Create from Replenishment */
public String getReplenishmentCreate() 
{
return (String)get_Value("ReplenishmentCreate");
}
}
