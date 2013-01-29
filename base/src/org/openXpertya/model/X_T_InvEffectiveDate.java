/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_InvEffectiveDate
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.937 */
public class X_T_InvEffectiveDate extends PO
{
/** Constructor estándar */
public X_T_InvEffectiveDate (Properties ctx, int T_InvEffectiveDate_ID, String trxName)
{
super (ctx, T_InvEffectiveDate_ID, trxName);
/** if (T_InvEffectiveDate_ID == 0)
{
setC_AcctSchema_ID (0);
setMovementDate (new Timestamp(System.currentTimeMillis()));
setS_Resource_ID (0);
setT_InvEffectiveDate_ID (0);
}
 */
}
/** Load Constructor */
public X_T_InvEffectiveDate (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000069 */
public static final int Table_ID=1000069;

/** TableName=T_InvEffectiveDate */
public static final String Table_Name="T_InvEffectiveDate";

protected static KeyNamePair Model = new KeyNamePair(1000069,"T_InvEffectiveDate");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_InvEffectiveDate[").append(getID()).append("]");
return sb.toString();
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
set_Value ("CostCumAmt", CostCumAmt);
}
/** Get Cum Amt Cost Element CMPCS.
Cumulative amount for calculating the element cost */
public BigDecimal getCostCumAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CostCumAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cost Element CMPCS.
ID of the cost element(an element of a cost type) */
public void setMPC_Cost_Element_ID (int MPC_Cost_Element_ID)
{
if (MPC_Cost_Element_ID <= 0) set_Value ("MPC_Cost_Element_ID", null);
 else 
set_Value ("MPC_Cost_Element_ID", new Integer(MPC_Cost_Element_ID));
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
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
if (M_Warehouse_ID <= 0) set_Value ("M_Warehouse_ID", null);
 else 
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
/** Set Movement Date.
Date a product was moved in or out of inventory */
public void setMovementDate (Timestamp MovementDate)
{
if (MovementDate == null) throw new IllegalArgumentException ("MovementDate is mandatory");
set_Value ("MovementDate", MovementDate);
}
/** Get Movement Date.
Date a product was moved in or out of inventory */
public Timestamp getMovementDate() 
{
return (Timestamp)get_Value("MovementDate");
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
/** Set Resource.
Resource */
public void setS_Resource_ID (int S_Resource_ID)
{
set_Value ("S_Resource_ID", new Integer(S_Resource_ID));
}
/** Get Resource.
Resource */
public int getS_Resource_ID() 
{
Integer ii = (Integer)get_Value("S_Resource_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set T_InvEffectiveDate_ID */
public void setT_InvEffectiveDate_ID (int T_InvEffectiveDate_ID)
{
set_ValueNoCheck ("T_InvEffectiveDate_ID", new Integer(T_InvEffectiveDate_ID));
}
/** Get T_InvEffectiveDate_ID */
public int getT_InvEffectiveDate_ID() 
{
Integer ii = (Integer)get_Value("T_InvEffectiveDate_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
