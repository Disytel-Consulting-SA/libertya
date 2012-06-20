/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_DemandLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:37.453 */
public class X_M_DemandLine extends PO
{
/** Constructor estándar */
public X_M_DemandLine (Properties ctx, int M_DemandLine_ID, String trxName)
{
super (ctx, M_DemandLine_ID, trxName);
/** if (M_DemandLine_ID == 0)
{
setC_Period_ID (0);
setM_DemandLine_ID (0);
setM_Demand_ID (0);
setM_Product_ID (0);
setQty (Env.ZERO);
setQtyCalculated (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_DemandLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=719 */
public static final int Table_ID=719;

/** TableName=M_DemandLine */
public static final String Table_Name="M_DemandLine";

protected static KeyNamePair Model = new KeyNamePair(719,"M_DemandLine");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_DemandLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Period.
Period of the Calendar */
public void setC_Period_ID (int C_Period_ID)
{
set_ValueNoCheck ("C_Period_ID", new Integer(C_Period_ID));
}
/** Get Period.
Period of the Calendar */
public int getC_Period_ID() 
{
Integer ii = (Integer)get_Value("C_Period_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Period_ID()));
}
/** Set Demand Line.
Material Demand Line */
public void setM_DemandLine_ID (int M_DemandLine_ID)
{
set_ValueNoCheck ("M_DemandLine_ID", new Integer(M_DemandLine_ID));
}
/** Get Demand Line.
Material Demand Line */
public int getM_DemandLine_ID() 
{
Integer ii = (Integer)get_Value("M_DemandLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Demand.
Material Demand */
public void setM_Demand_ID (int M_Demand_ID)
{
set_ValueNoCheck ("M_Demand_ID", new Integer(M_Demand_ID));
}
/** Get Demand.
Material Demand */
public int getM_Demand_ID() 
{
Integer ii = (Integer)get_Value("M_Demand_ID");
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
/** Set Calculated Quantity.
Calculated Quantity */
public void setQtyCalculated (BigDecimal QtyCalculated)
{
if (QtyCalculated == null) throw new IllegalArgumentException ("QtyCalculated is mandatory");
set_Value ("QtyCalculated", QtyCalculated);
}
/** Get Calculated Quantity.
Calculated Quantity */
public BigDecimal getQtyCalculated() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyCalculated");
if (bd == null) return Env.ZERO;
return bd;
}
}
