/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_Color
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.125 */
public class X_PA_Color extends PO
{
/** Constructor estándar */
public X_PA_Color (Properties ctx, int PA_Color_ID, String trxName)
{
super (ctx, PA_Color_ID, trxName);
/** if (PA_Color_ID == 0)
{
setJavaColorClass (null);
setName (null);
setPA_Color_ID (0);
setUpToPercent (0);
}
 */
}
/** Load Constructor */
public X_PA_Color (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=439 */
public static final int Table_ID=439;

/** TableName=PA_Color */
public static final String Table_Name="PA_Color";

protected static KeyNamePair Model = new KeyNamePair(439,"PA_Color");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_Color[").append(getID()).append("]");
return sb.toString();
}
/** Set Java Color Class.
Full qualified Java Class inheriting from java.awt.Color */
public void setJavaColorClass (String JavaColorClass)
{
if (JavaColorClass == null) throw new IllegalArgumentException ("JavaColorClass is mandatory");
if (JavaColorClass.length() > 60)
{
log.warning("Length > 60 - truncated");
JavaColorClass = JavaColorClass.substring(0,59);
}
set_Value ("JavaColorClass", JavaColorClass);
}
/** Get Java Color Class.
Full qualified Java Class inheriting from java.awt.Color */
public String getJavaColorClass() 
{
return (String)get_Value("JavaColorClass");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Color.
Color for Performance Analysis */
public void setPA_Color_ID (int PA_Color_ID)
{
set_ValueNoCheck ("PA_Color_ID", new Integer(PA_Color_ID));
}
/** Get Color.
Color for Performance Analysis */
public int getPA_Color_ID() 
{
Integer ii = (Integer)get_Value("PA_Color_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Up to Percent.
Maximum value for this color;
 If this is the highest value, it is used also for higher values. */
public void setUpToPercent (int UpToPercent)
{
set_Value ("UpToPercent", new Integer(UpToPercent));
}
/** Get Up to Percent.
Maximum value for this color;
 If this is the highest value, it is used also for higher values. */
public int getUpToPercent() 
{
Integer ii = (Integer)get_Value("UpToPercent");
if (ii == null) return 0;
return ii.intValue();
}
}
