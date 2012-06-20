/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_LandedCostAllocation
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:30.468 */
public class X_C_LandedCostAllocation extends PO
{
/** Constructor estándar */
public X_C_LandedCostAllocation (Properties ctx, int C_LandedCostAllocation_ID, String trxName)
{
super (ctx, C_LandedCostAllocation_ID, trxName);
/** if (C_LandedCostAllocation_ID == 0)
{
setAmt (Env.ZERO);
setC_LandedCost_ID (0);
setM_Product_ID (0);
setQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_LandedCostAllocation (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=760 */
public static final int Table_ID=760;

/** TableName=C_LandedCostAllocation */
public static final String Table_Name="C_LandedCostAllocation";

protected static KeyNamePair Model = new KeyNamePair(760,"C_LandedCostAllocation");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_LandedCostAllocation[").append(getID()).append("]");
return sb.toString();
}
/** Set Amount.
Amount */
public void setAmt (BigDecimal Amt)
{
if (Amt == null) throw new IllegalArgumentException ("Amt is mandatory");
set_Value ("Amt", Amt);
}
/** Get Amount.
Amount */
public BigDecimal getAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("Amt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Landed Cost.
Landed cost to be allocated to material receipts */
public void setC_LandedCost_ID (int C_LandedCost_ID)
{
set_ValueNoCheck ("C_LandedCost_ID", new Integer(C_LandedCost_ID));
}
/** Get Landed Cost.
Landed cost to be allocated to material receipts */
public int getC_LandedCost_ID() 
{
Integer ii = (Integer)get_Value("C_LandedCost_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_LandedCost_ID()));
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
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
if (Qty == null) throw new IllegalArgumentException ("Qty is mandatory");
set_Value ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
}
}
