/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_ForecastLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:37.843 */
public class X_M_ForecastLine extends PO
{
/** Constructor estándar */
public X_M_ForecastLine (Properties ctx, int M_ForecastLine_ID, String trxName)
{
super (ctx, M_ForecastLine_ID, trxName);
/** if (M_ForecastLine_ID == 0)
{
setC_Period_ID (0);
setM_ForecastLine_ID (0);
setM_Forecast_ID (0);
setM_Product_ID (0);
setQty (Env.ZERO);
setQtyCalculated (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_ForecastLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=722 */
public static final int Table_ID=722;

/** TableName=M_ForecastLine */
public static final String Table_Name="M_ForecastLine";

protected static KeyNamePair Model = new KeyNamePair(722,"M_ForecastLine");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_ForecastLine[").append(getID()).append("]");
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
/** Set Forecast Line.
Forecast Line */
public void setM_ForecastLine_ID (int M_ForecastLine_ID)
{
set_ValueNoCheck ("M_ForecastLine_ID", new Integer(M_ForecastLine_ID));
}
/** Get Forecast Line.
Forecast Line */
public int getM_ForecastLine_ID() 
{
Integer ii = (Integer)get_Value("M_ForecastLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Forecast.
Material Forecast */
public void setM_Forecast_ID (int M_Forecast_ID)
{
set_ValueNoCheck ("M_Forecast_ID", new Integer(M_Forecast_ID));
}
/** Get Forecast.
Material Forecast */
public int getM_Forecast_ID() 
{
Integer ii = (Integer)get_Value("M_Forecast_ID");
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
