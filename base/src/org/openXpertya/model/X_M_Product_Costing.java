/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Product_Costing
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:39.218 */
public class X_M_Product_Costing extends PO
{
/** Constructor estándar */
public X_M_Product_Costing (Properties ctx, int M_Product_Costing_ID, String trxName)
{
super (ctx, M_Product_Costing_ID, trxName);
/** if (M_Product_Costing_ID == 0)
{
setC_AcctSchema_ID (0);
setCostAverage (Env.ZERO);
setCostAverageCumAmt (Env.ZERO);
setCostAverageCumQty (Env.ZERO);
setCostStandard (Env.ZERO);
setCostStandardCumAmt (Env.ZERO);
setCostStandardCumQty (Env.ZERO);
setCostStandardPOAmt (Env.ZERO);
setCostStandardPOQty (Env.ZERO);
setCurrentCostPrice (Env.ZERO);
setFutureCostPrice (Env.ZERO);
setM_Product_ID (0);
setPriceLastInv (Env.ZERO);
setPriceLastPO (Env.ZERO);
setTotalInvAmt (Env.ZERO);
setTotalInvQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_Product_Costing (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=327 */
public static final int Table_ID=327;

/** TableName=M_Product_Costing */
public static final String Table_Name="M_Product_Costing";

protected static KeyNamePair Model = new KeyNamePair(327,"M_Product_Costing");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Product_Costing[").append(getID()).append("]");
return sb.toString();
}
/** Set Accounting Schema.
Rules for accounting */
public void setC_AcctSchema_ID (int C_AcctSchema_ID)
{
set_ValueNoCheck ("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
}
/** Get Accounting Schema.
Rules for accounting */
public int getC_AcctSchema_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Average Cost.
Weighted average costs */
public void setCostAverage (BigDecimal CostAverage)
{
if (CostAverage == null) throw new IllegalArgumentException ("CostAverage is mandatory");
set_ValueNoCheck ("CostAverage", CostAverage);
}
/** Get Average Cost.
Weighted average costs */
public BigDecimal getCostAverage() 
{
BigDecimal bd = (BigDecimal)get_Value("CostAverage");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Average Cost Amount Sum.
Cumulative average cost amounts (internal) */
public void setCostAverageCumAmt (BigDecimal CostAverageCumAmt)
{
if (CostAverageCumAmt == null) throw new IllegalArgumentException ("CostAverageCumAmt is mandatory");
set_ValueNoCheck ("CostAverageCumAmt", CostAverageCumAmt);
}
/** Get Average Cost Amount Sum.
Cumulative average cost amounts (internal) */
public BigDecimal getCostAverageCumAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CostAverageCumAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Average Cost Quantity Sum.
Cumulative average cost quantities (internal) */
public void setCostAverageCumQty (BigDecimal CostAverageCumQty)
{
if (CostAverageCumQty == null) throw new IllegalArgumentException ("CostAverageCumQty is mandatory");
set_ValueNoCheck ("CostAverageCumQty", CostAverageCumQty);
}
/** Get Average Cost Quantity Sum.
Cumulative average cost quantities (internal) */
public BigDecimal getCostAverageCumQty() 
{
BigDecimal bd = (BigDecimal)get_Value("CostAverageCumQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Standard Cost.
Standard Costs */
public void setCostStandard (BigDecimal CostStandard)
{
if (CostStandard == null) throw new IllegalArgumentException ("CostStandard is mandatory");
set_ValueNoCheck ("CostStandard", CostStandard);
}
/** Get Standard Cost.
Standard Costs */
public BigDecimal getCostStandard() 
{
BigDecimal bd = (BigDecimal)get_Value("CostStandard");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Std Cost Amount Sum.
Standard Cost Invoice Amount Sum (internal) */
public void setCostStandardCumAmt (BigDecimal CostStandardCumAmt)
{
if (CostStandardCumAmt == null) throw new IllegalArgumentException ("CostStandardCumAmt is mandatory");
set_ValueNoCheck ("CostStandardCumAmt", CostStandardCumAmt);
}
/** Get Std Cost Amount Sum.
Standard Cost Invoice Amount Sum (internal) */
public BigDecimal getCostStandardCumAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CostStandardCumAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Std Cost Quantity Sum.
Standard Cost Invoice Quantity Sum (internal) */
public void setCostStandardCumQty (BigDecimal CostStandardCumQty)
{
if (CostStandardCumQty == null) throw new IllegalArgumentException ("CostStandardCumQty is mandatory");
set_ValueNoCheck ("CostStandardCumQty", CostStandardCumQty);
}
/** Get Std Cost Quantity Sum.
Standard Cost Invoice Quantity Sum (internal) */
public BigDecimal getCostStandardCumQty() 
{
BigDecimal bd = (BigDecimal)get_Value("CostStandardCumQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Std PO Cost Amount Sum.
Standard Cost Purchase Order Amount Sum (internal) */
public void setCostStandardPOAmt (BigDecimal CostStandardPOAmt)
{
if (CostStandardPOAmt == null) throw new IllegalArgumentException ("CostStandardPOAmt is mandatory");
set_ValueNoCheck ("CostStandardPOAmt", CostStandardPOAmt);
}
/** Get Std PO Cost Amount Sum.
Standard Cost Purchase Order Amount Sum (internal) */
public BigDecimal getCostStandardPOAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CostStandardPOAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Std PO Cost Quantity Sum.
Standard Cost Purchase Order Quantity Sum (internal) */
public void setCostStandardPOQty (BigDecimal CostStandardPOQty)
{
if (CostStandardPOQty == null) throw new IllegalArgumentException ("CostStandardPOQty is mandatory");
set_ValueNoCheck ("CostStandardPOQty", CostStandardPOQty);
}
/** Get Std PO Cost Quantity Sum.
Standard Cost Purchase Order Quantity Sum (internal) */
public BigDecimal getCostStandardPOQty() 
{
BigDecimal bd = (BigDecimal)get_Value("CostStandardPOQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Current Cost Price.
The currently used cost price */
public void setCurrentCostPrice (BigDecimal CurrentCostPrice)
{
if (CurrentCostPrice == null) throw new IllegalArgumentException ("CurrentCostPrice is mandatory");
set_Value ("CurrentCostPrice", CurrentCostPrice);
}
/** Get Current Cost Price.
The currently used cost price */
public BigDecimal getCurrentCostPrice() 
{
BigDecimal bd = (BigDecimal)get_Value("CurrentCostPrice");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Future Cost Price */
public void setFutureCostPrice (BigDecimal FutureCostPrice)
{
if (FutureCostPrice == null) throw new IllegalArgumentException ("FutureCostPrice is mandatory");
set_Value ("FutureCostPrice", FutureCostPrice);
}
/** Get Future Cost Price */
public BigDecimal getFutureCostPrice() 
{
BigDecimal bd = (BigDecimal)get_Value("FutureCostPrice");
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
/** Set Last Invoice Price.
Price of the last invoice for the product */
public void setPriceLastInv (BigDecimal PriceLastInv)
{
if (PriceLastInv == null) throw new IllegalArgumentException ("PriceLastInv is mandatory");
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
if (PriceLastPO == null) throw new IllegalArgumentException ("PriceLastPO is mandatory");
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
/** Set Total Invoice Amount.
Cumulative total lifetime invoice amount */
public void setTotalInvAmt (BigDecimal TotalInvAmt)
{
if (TotalInvAmt == null) throw new IllegalArgumentException ("TotalInvAmt is mandatory");
set_ValueNoCheck ("TotalInvAmt", TotalInvAmt);
}
/** Get Total Invoice Amount.
Cumulative total lifetime invoice amount */
public BigDecimal getTotalInvAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalInvAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Total Invoice Quantity.
Cumulative total lifetime invoice quantity */
public void setTotalInvQty (BigDecimal TotalInvQty)
{
if (TotalInvQty == null) throw new IllegalArgumentException ("TotalInvQty is mandatory");
set_ValueNoCheck ("TotalInvQty", TotalInvQty);
}
/** Get Total Invoice Quantity.
Cumulative total lifetime invoice quantity */
public BigDecimal getTotalInvQty() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalInvQty");
if (bd == null) return Env.ZERO;
return bd;
}
}
