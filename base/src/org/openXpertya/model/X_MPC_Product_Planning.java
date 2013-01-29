/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Product_Planning
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.453 */
public class X_MPC_Product_Planning extends PO
{
/** Constructor estándar */
public X_MPC_Product_Planning (Properties ctx, int MPC_Product_Planning_ID, String trxName)
{
super (ctx, MPC_Product_Planning_ID, trxName);
/** if (MPC_Product_Planning_ID == 0)
{
setIsCreatePlan (false);
setIsIssue (true);	// Y
setIsPhantom (false);
setIsRequiredMRP (false);
setMPC_Product_Planning_ID (0);
setM_Product_ID (0);
setM_Warehouse_ID (0);
setS_Resource_ID (0);
}
 */
}
/** Load Constructor */
public X_MPC_Product_Planning (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000014 */
public static final int Table_ID=1000014;

/** TableName=MPC_Product_Planning */
public static final String Table_Name="MPC_Product_Planning";

protected static KeyNamePair Model = new KeyNamePair(1000014,"MPC_Product_Planning");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Product_Planning[").append(getID()).append("]");
return sb.toString();
}
/** Set Workflow.
Workflow or combination of tasks */
public void setAD_Workflow_ID (int AD_Workflow_ID)
{
if (AD_Workflow_ID <= 0) set_Value ("AD_Workflow_ID", null);
 else 
set_Value ("AD_Workflow_ID", new Integer(AD_Workflow_ID));
}
/** Get Workflow.
Workflow or combination of tasks */
public int getAD_Workflow_ID() 
{
Integer ii = (Integer)get_Value("AD_Workflow_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Promised Delivery Time.
Promised days between order and delivery */
public void setDeliveryTime_Promised (BigDecimal DeliveryTime_Promised)
{
set_Value ("DeliveryTime_Promised", DeliveryTime_Promised);
}
/** Get Promised Delivery Time.
Promised days between order and delivery */
public BigDecimal getDeliveryTime_Promised() 
{
BigDecimal bd = (BigDecimal)get_Value("DeliveryTime_Promised");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Is Create Plan */
public void setIsCreatePlan (boolean IsCreatePlan)
{
set_Value ("IsCreatePlan", new Boolean(IsCreatePlan));
}
/** Get Is Create Plan */
public boolean isCreatePlan() 
{
Object oo = get_Value("IsCreatePlan");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Demand */
public void setIsDemand (boolean IsDemand)
{
set_Value ("IsDemand", new Boolean(IsDemand));
}
/** Get Is Demand */
public boolean isDemand() 
{
Object oo = get_Value("IsDemand");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Issue */
public void setIsIssue (boolean IsIssue)
{
set_Value ("IsIssue", new Boolean(IsIssue));
}
/** Get Is Issue */
public boolean isIssue() 
{
Object oo = get_Value("IsIssue");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is  Master Production Schedule */
public void setIsMPS (boolean IsMPS)
{
set_Value ("IsMPS", new Boolean(IsMPS));
}
/** Get Is  Master Production Schedule */
public boolean isMPS() 
{
Object oo = get_Value("IsMPS");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsPhantom */
public void setIsPhantom (boolean IsPhantom)
{
set_Value ("IsPhantom", new Boolean(IsPhantom));
}
/** Get IsPhantom */
public boolean isPhantom() 
{
Object oo = get_Value("IsPhantom");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Required MRP */
public void setIsRequiredMRP (boolean IsRequiredMRP)
{
set_ValueNoCheck ("IsRequiredMRP", new Boolean(IsRequiredMRP));
}
/** Get Is Required MRP */
public boolean isRequiredMRP() 
{
Object oo = get_Value("IsRequiredMRP");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Supply */
public void setIsSupply (boolean IsSupply)
{
set_Value ("IsSupply", new Boolean(IsSupply));
}
/** Get Is Supply */
public boolean isSupply() 
{
Object oo = get_Value("IsSupply");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set BOM & Formula */
public void setMPC_Product_BOM_ID (int MPC_Product_BOM_ID)
{
if (MPC_Product_BOM_ID <= 0) set_Value ("MPC_Product_BOM_ID", null);
 else 
set_Value ("MPC_Product_BOM_ID", new Integer(MPC_Product_BOM_ID));
}
/** Get BOM & Formula */
public int getMPC_Product_BOM_ID() 
{
Integer ii = (Integer)get_Value("MPC_Product_BOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set MPC_Product_Planning_ID */
public void setMPC_Product_Planning_ID (int MPC_Product_Planning_ID)
{
set_ValueNoCheck ("MPC_Product_Planning_ID", new Integer(MPC_Product_Planning_ID));
}
/** Get MPC_Product_Planning_ID */
public int getMPC_Product_Planning_ID() 
{
Integer ii = (Integer)get_Value("MPC_Product_Planning_ID");
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
/** Set Order Max */
public void setOrder_Max (BigDecimal Order_Max)
{
set_Value ("Order_Max", Order_Max);
}
/** Get Order Max */
public BigDecimal getOrder_Max() 
{
BigDecimal bd = (BigDecimal)get_Value("Order_Max");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set OrderPeriod */
public void setOrder_Period (BigDecimal Order_Period)
{
set_Value ("Order_Period", Order_Period);
}
/** Get OrderPeriod */
public BigDecimal getOrder_Period() 
{
BigDecimal bd = (BigDecimal)get_Value("Order_Period");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int ORDER_POLICY_AD_Reference_ID=1000015;
/** Period Order Quantity = POQ */
public static final String ORDER_POLICY_PeriodOrderQuantity = "POQ";
/** Lote For Lote = LFL */
public static final String ORDER_POLICY_LoteForLote = "LFL";
/** Order Fixed Quantity = FQ */
public static final String ORDER_POLICY_OrderFixedQuantity = "FQ";
/** Set Order Policy */
public void setOrder_Policy (String Order_Policy)
{
if (Order_Policy == null || Order_Policy.equals("POQ") || Order_Policy.equals("LFL") || Order_Policy.equals("FQ"));
 else throw new IllegalArgumentException ("Order_Policy Invalid value - Reference_ID=1000015 - POQ - LFL - FQ");
if (Order_Policy != null && Order_Policy.length() > 3)
{
log.warning("Length > 3 - truncated");
Order_Policy = Order_Policy.substring(0,2);
}
set_Value ("Order_Policy", Order_Policy);
}
/** Get Order Policy */
public String getOrder_Policy() 
{
return (String)get_Value("Order_Policy");
}
/** Set Order Qty */
public void setOrder_Qty (BigDecimal Order_Qty)
{
set_Value ("Order_Qty", Order_Qty);
}
/** Get Order Qty */
public BigDecimal getOrder_Qty() 
{
BigDecimal bd = (BigDecimal)get_Value("Order_Qty");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int PLANNER_ID_AD_Reference_ID=110;
/** Set Planner .
ID of the person responsible of planning the product. */
public void setPlanner_ID (int Planner_ID)
{
if (Planner_ID <= 0) set_Value ("Planner_ID", null);
 else 
set_Value ("Planner_ID", new Integer(Planner_ID));
}
/** Get Planner .
ID of the person responsible of planning the product. */
public int getPlanner_ID() 
{
Integer ii = (Integer)get_Value("Planner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Resource.
Resource */
public void setS_Resource_ID (int S_Resource_ID)
{
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
/** Set TimeFence */
public void setTimeFence (BigDecimal TimeFence)
{
set_Value ("TimeFence", TimeFence);
}
/** Get TimeFence */
public BigDecimal getTimeFence() 
{
BigDecimal bd = (BigDecimal)get_Value("TimeFence");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set TransfertTime */
public void setTransfertTime (BigDecimal TransfertTime)
{
set_Value ("TransfertTime", TransfertTime);
}
/** Get TransfertTime */
public BigDecimal getTransfertTime() 
{
BigDecimal bd = (BigDecimal)get_Value("TransfertTime");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Working Time.
Workflow Simulation Execution Time */
public void setWorkingTime (BigDecimal WorkingTime)
{
set_Value ("WorkingTime", WorkingTime);
}
/** Get Working Time.
Workflow Simulation Execution Time */
public BigDecimal getWorkingTime() 
{
BigDecimal bd = (BigDecimal)get_Value("WorkingTime");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Yield */
public void setYield (int Yield)
{
set_Value ("Yield", new Integer(Yield));
}
/** Get Yield */
public int getYield() 
{
Integer ii = (Integer)get_Value("Yield");
if (ii == null) return 0;
return ii.intValue();
}
}
