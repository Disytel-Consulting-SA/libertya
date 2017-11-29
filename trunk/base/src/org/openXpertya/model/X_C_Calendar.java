/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Calendar
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-11-29 09:11:19.139 */
public class X_C_Calendar extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Calendar (Properties ctx, int C_Calendar_ID, String trxName)
{
super (ctx, C_Calendar_ID, trxName);
/** if (C_Calendar_ID == 0)
{
setCacheEnabled (false);
setC_Calendar_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_Calendar (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Calendar");

/** TableName=C_Calendar */
public static final String Table_Name="C_Calendar";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Calendar");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Calendar[").append(getID()).append("]");
return sb.toString();
}
/** Set Cache Enabled.
Enable cache allows to access more quickly to the information but changes are available after reboot the application.  */
public void setCacheEnabled (boolean CacheEnabled)
{
set_Value ("CacheEnabled", new Boolean(CacheEnabled));
}
/** Get Cache Enabled.
Enable cache allows to access more quickly to the information but changes are available after reboot the application.  */
public boolean isCacheEnabled() 
{
Object oo = get_Value("CacheEnabled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Calendar.
Accounting Calendar Name */
public void setC_Calendar_ID (int C_Calendar_ID)
{
set_ValueNoCheck ("C_Calendar_ID", new Integer(C_Calendar_ID));
}
/** Get Calendar.
Accounting Calendar Name */
public int getC_Calendar_ID() 
{
Integer ii = (Integer)get_Value("C_Calendar_ID");
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
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
}
