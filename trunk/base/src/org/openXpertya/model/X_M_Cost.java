/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Cost
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:37.296 */
public class X_M_Cost extends PO
{
/** Constructor estándar */
public X_M_Cost (Properties ctx, int M_Cost_ID, String trxName)
{
super (ctx, M_Cost_ID, trxName);
/** if (M_Cost_ID == 0)
{
setC_AcctSchema_ID (0);
setCurrentCostPrice (Env.ZERO);
setFutureCostPrice (Env.ZERO);
setM_CostElement_ID (0);
setM_CostType_ID (0);
setM_Product_ID (0);
}
 */
}
/** Load Constructor */
public X_M_Cost (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=771 */
public static final int Table_ID=771;

/** TableName=M_Cost */
public static final String Table_Name="M_Cost";

protected static KeyNamePair Model = new KeyNamePair(771,"M_Cost");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Cost[").append(getID()).append("]");
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
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
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
/** Set Calculated.
The value is calculated by the system */
public void setIsCalculated (boolean IsCalculated)
{
throw new IllegalArgumentException ("IsCalculated is virtual column");
}
/** Get Calculated.
The value is calculated by the system */
public boolean isCalculated() 
{
Object oo = get_Value("IsCalculated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Cost Element.
Product Cost Element */
public void setM_CostElement_ID (int M_CostElement_ID)
{
set_Value ("M_CostElement_ID", new Integer(M_CostElement_ID));
}
/** Get Cost Element.
Product Cost Element */
public int getM_CostElement_ID() 
{
Integer ii = (Integer)get_Value("M_CostElement_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cost Type.
Type of Cost (e.g. Current, Plan, Future) */
public void setM_CostType_ID (int M_CostType_ID)
{
set_Value ("M_CostType_ID", new Integer(M_CostType_ID));
}
/** Get Cost Type.
Type of Cost (e.g. Current, Plan, Future) */
public int getM_CostType_ID() 
{
Integer ii = (Integer)get_Value("M_CostType_ID");
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
}
