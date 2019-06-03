/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Inflation_Index
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-06-03 13:38:17.16 */
public class X_C_Inflation_Index extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Inflation_Index (Properties ctx, int C_Inflation_Index_ID, String trxName)
{
super (ctx, C_Inflation_Index_ID, trxName);
/** if (C_Inflation_Index_ID == 0)
{
setC_Inflation_Index_ID (0);
setC_Period_ID (0);
setInflationIndex (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_Inflation_Index (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Inflation_Index");

/** TableName=C_Inflation_Index */
public static final String Table_Name="C_Inflation_Index";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Inflation_Index");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Inflation_Index[").append(getID()).append("]");
return sb.toString();
}
/** Set C_Inflation_Index_ID */
public void setC_Inflation_Index_ID (int C_Inflation_Index_ID)
{
set_ValueNoCheck ("C_Inflation_Index_ID", new Integer(C_Inflation_Index_ID));
}
/** Get C_Inflation_Index_ID */
public int getC_Inflation_Index_ID() 
{
Integer ii = (Integer)get_Value("C_Inflation_Index_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Period.
Period of the Calendar */
public void setC_Period_ID (int C_Period_ID)
{
set_Value ("C_Period_ID", new Integer(C_Period_ID));
}
/** Get Period.
Period of the Calendar */
public int getC_Period_ID() 
{
Integer ii = (Integer)get_Value("C_Period_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Inflation Index */
public void setInflationIndex (BigDecimal InflationIndex)
{
if (InflationIndex == null) throw new IllegalArgumentException ("InflationIndex is mandatory");
set_Value ("InflationIndex", InflationIndex);
}
/** Get Inflation Index */
public BigDecimal getInflationIndex() 
{
BigDecimal bd = (BigDecimal)get_Value("InflationIndex");
if (bd == null) return Env.ZERO;
return bd;
}
}
