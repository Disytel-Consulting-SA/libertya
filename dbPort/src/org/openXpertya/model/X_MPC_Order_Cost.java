/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Order_Cost
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.015 */
public class X_MPC_Order_Cost extends PO
{
/** Constructor estándar */
public X_MPC_Order_Cost (Properties ctx, int MPC_Order_Cost_ID, String trxName)
{
super (ctx, MPC_Order_Cost_ID, trxName);
/** if (MPC_Order_Cost_ID == 0)
{
setC_AcctSchema_ID (0);
setMPC_Cost_Element_ID (0);
setMPC_Order_Cost_ID (0);
setMPC_Order_ID (0);
setM_Product_ID (0);
}
 */
}
/** Load Constructor */
public X_MPC_Order_Cost (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000021 */
public static final int Table_ID=1000021;

/** TableName=MPC_Order_Cost */
public static final String Table_Name="MPC_Order_Cost";

protected static KeyNamePair Model = new KeyNamePair(1000021,"MPC_Order_Cost");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Order_Cost[").append(getID()).append("]");
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
/** Set Accounting Schema.
Rules for accounting */
public void setC_AcctSchema_ID (int C_AcctSchema_ID)
{
set_Value ("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
}
/** Get Accounting Schema.
Rules for accounting */
public int getC_AcctSchema_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cum Amt Cost Element CMPCS.
Cumulative amount for calculating the element cost */
public void setCostCumAmt (BigDecimal CostCumAmt)
{
set_ValueNoCheck ("CostCumAmt", CostCumAmt);
}
/** Get Cum Amt Cost Element CMPCS.
Cumulative amount for calculating the element cost */
public BigDecimal getCostCumAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CostCumAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cum Amt Cost Element Post CMPCS.
Cumulative amount for calculating the element cost */
public void setCostCumAmtPost (BigDecimal CostCumAmtPost)
{
set_ValueNoCheck ("CostCumAmtPost", CostCumAmtPost);
}
/** Get Cum Amt Cost Element Post CMPCS.
Cumulative amount for calculating the element cost */
public BigDecimal getCostCumAmtPost() 
{
BigDecimal bd = (BigDecimal)get_Value("CostCumAmtPost");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cum Qty Cost Element CMPCS.
Cumulative quantity for calculating the cost element */
public void setCostCumQty (BigDecimal CostCumQty)
{
set_ValueNoCheck ("CostCumQty", CostCumQty);
}
/** Get Cum Qty Cost Element CMPCS.
Cumulative quantity for calculating the cost element */
public BigDecimal getCostCumQty() 
{
BigDecimal bd = (BigDecimal)get_Value("CostCumQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cost Element Cum Qty Post CMPCS */
public void setCostCumQtyPost (BigDecimal CostCumQtyPost)
{
set_ValueNoCheck ("CostCumQtyPost", CostCumQtyPost);
}
/** Get Cost Element Cum Qty Post CMPCS */
public BigDecimal getCostCumQtyPost() 
{
BigDecimal bd = (BigDecimal)get_Value("CostCumQtyPost");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Lower Level amount for Cost Element CMPCS.
Lower levels amount for calculating the cost element   */
public void setCostLLAmt (BigDecimal CostLLAmt)
{
set_ValueNoCheck ("CostLLAmt", CostLLAmt);
}
/** Get Lower Level amount for Cost Element CMPCS.
Lower levels amount for calculating the cost element   */
public BigDecimal getCostLLAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CostLLAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set This Level amount for Cost Element CMPCS */
public void setCostTLAmt (BigDecimal CostTLAmt)
{
set_ValueNoCheck ("CostTLAmt", CostTLAmt);
}
/** Get This Level amount for Cost Element CMPCS */
public BigDecimal getCostTLAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CostTLAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cost Element CMPCS.
ID of the cost element(an element of a cost type) */
public void setMPC_Cost_Element_ID (int MPC_Cost_Element_ID)
{
set_ValueNoCheck ("MPC_Cost_Element_ID", new Integer(MPC_Cost_Element_ID));
}
/** Get Cost Element CMPCS.
ID of the cost element(an element of a cost type) */
public int getMPC_Cost_Element_ID() 
{
Integer ii = (Integer)get_Value("MPC_Cost_Element_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cost Group CMPCS */
public void setMPC_Cost_Group_ID (int MPC_Cost_Group_ID)
{
if (MPC_Cost_Group_ID <= 0) set_Value ("MPC_Cost_Group_ID", null);
 else 
set_Value ("MPC_Cost_Group_ID", new Integer(MPC_Cost_Group_ID));
}
/** Get Cost Group CMPCS */
public int getMPC_Cost_Group_ID() 
{
Integer ii = (Integer)get_Value("MPC_Cost_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set ID of Cost Order */
public void setMPC_Order_Cost_ID (int MPC_Order_Cost_ID)
{
set_ValueNoCheck ("MPC_Order_Cost_ID", new Integer(MPC_Order_Cost_ID));
}
/** Get ID of Cost Order */
public int getMPC_Order_Cost_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_Cost_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Manufacturing Order.
Manufacturing Order */
public void setMPC_Order_ID (int MPC_Order_ID)
{
set_ValueNoCheck ("MPC_Order_ID", new Integer(MPC_Order_ID));
}
/** Get Manufacturing Order.
Manufacturing Order */
public int getMPC_Order_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_ID");
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
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
if (M_Warehouse_ID <= 0) set_ValueNoCheck ("M_Warehouse_ID", null);
 else 
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
}
