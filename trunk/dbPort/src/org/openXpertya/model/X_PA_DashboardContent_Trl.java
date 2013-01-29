/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_DashboardContent_Trl
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-01-16 17:33:21.516 */
public class X_PA_DashboardContent_Trl extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_PA_DashboardContent_Trl (Properties ctx, int PA_DashboardContent_Trl_ID, String trxName)
{
super (ctx, PA_DashboardContent_Trl_ID, trxName);
/** if (PA_DashboardContent_Trl_ID == 0)
{
setAD_Language (null);
setIsTranslated (false);
setName (null);
setPA_DashboardContent_ID (0);
}
 */
}
/** Load Constructor */
public X_PA_DashboardContent_Trl (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("PA_DashboardContent_Trl");

/** TableName=PA_DashboardContent_Trl */
public static final String Table_Name="PA_DashboardContent_Trl";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"PA_DashboardContent_Trl");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_DashboardContent_Trl[").append(getID()).append("]");
return sb.toString();
}
/** Set Language.
Language for this entity */
public void setAD_Language (String AD_Language)
{
if (AD_Language == null) throw new IllegalArgumentException ("AD_Language is mandatory");
if (AD_Language.length() > 6)
{
log.warning("Length > 6 - truncated");
AD_Language = AD_Language.substring(0,6);
}
set_Value ("AD_Language", AD_Language);
}
/** Get Language.
Language for this entity */
public String getAD_Language() 
{
return (String)get_Value("AD_Language");
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
/** Set Translated.
This column is translated */
public void setIsTranslated (boolean IsTranslated)
{
set_Value ("IsTranslated", new Boolean(IsTranslated));
}
/** Get Translated.
This column is translated */
public boolean isTranslated() 
{
Object oo = get_Value("IsTranslated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set PA_DashboardContent_ID */
public void setPA_DashboardContent_ID (int PA_DashboardContent_ID)
{
set_Value ("PA_DashboardContent_ID", new Integer(PA_DashboardContent_ID));
}
/** Get PA_DashboardContent_ID */
public int getPA_DashboardContent_ID() 
{
Integer ii = (Integer)get_Value("PA_DashboardContent_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
