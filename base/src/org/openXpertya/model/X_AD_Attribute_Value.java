/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Attribute_Value
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:22.281 */
public class X_AD_Attribute_Value extends PO
{
/** Constructor estándar */
public X_AD_Attribute_Value (Properties ctx, int AD_Attribute_Value_ID, String trxName)
{
super (ctx, AD_Attribute_Value_ID, trxName);
/** if (AD_Attribute_Value_ID == 0)
{
setAD_Attribute_ID (0);
setRecord_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_Attribute_Value (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=406 */
public static final int Table_ID=406;

/** TableName=AD_Attribute_Value */
public static final String Table_Name="AD_Attribute_Value";

protected static KeyNamePair Model = new KeyNamePair(406,"AD_Attribute_Value");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Attribute_Value[").append(getID()).append("]");
return sb.toString();
}
/** Set System Attribute */
public void setAD_Attribute_ID (int AD_Attribute_ID)
{
set_ValueNoCheck ("AD_Attribute_ID", new Integer(AD_Attribute_ID));
}
/** Get System Attribute */
public int getAD_Attribute_ID() 
{
Integer ii = (Integer)get_Value("AD_Attribute_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Record ID.
Direct internal record ID */
public void setRecord_ID (int Record_ID)
{
set_ValueNoCheck ("Record_ID", new Integer(Record_ID));
}
/** Get Record ID.
Direct internal record ID */
public int getRecord_ID() 
{
Integer ii = (Integer)get_Value("Record_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set V_Date */
public void setV_Date (Timestamp V_Date)
{
set_Value ("V_Date", V_Date);
}
/** Get V_Date */
public Timestamp getV_Date() 
{
return (Timestamp)get_Value("V_Date");
}
/** Set V_Number */
public void setV_Number (String V_Number)
{
if (V_Number != null && V_Number.length() > 22)
{
log.warning("Length > 22 - truncated");
V_Number = V_Number.substring(0,21);
}
set_Value ("V_Number", V_Number);
}
/** Get V_Number */
public String getV_Number() 
{
return (String)get_Value("V_Number");
}
/** Set V_String */
public void setV_String (String V_String)
{
if (V_String != null && V_String.length() > 2000)
{
log.warning("Length > 2000 - truncated");
V_String = V_String.substring(0,1999);
}
set_Value ("V_String", V_String);
}
/** Get V_String */
public String getV_String() 
{
return (String)get_Value("V_String");
}
}
