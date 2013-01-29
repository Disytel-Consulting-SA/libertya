/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Preference
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:23.515 */
public class X_AD_Preference extends PO
{
/** Constructor estándar */
public X_AD_Preference (Properties ctx, int AD_Preference_ID, String trxName)
{
super (ctx, AD_Preference_ID, trxName);
/** if (AD_Preference_ID == 0)
{
setAD_Preference_ID (0);
setAttribute (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_AD_Preference (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=195 */
public static final int Table_ID=195;

/** TableName=AD_Preference */
public static final String Table_Name="AD_Preference";

protected static KeyNamePair Model = new KeyNamePair(195,"AD_Preference");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Preference[").append(getID()).append("]");
return sb.toString();
}
/** Set Preference.
Personal Value Preference */
public void setAD_Preference_ID (int AD_Preference_ID)
{
set_ValueNoCheck ("AD_Preference_ID", new Integer(AD_Preference_ID));
}
/** Get Preference.
Personal Value Preference */
public int getAD_Preference_ID() 
{
Integer ii = (Integer)get_Value("AD_Preference_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_Value ("AD_User_ID", null);
 else 
set_Value ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Window.
Data entry or display window */
public void setAD_Window_ID (int AD_Window_ID)
{
if (AD_Window_ID <= 0) set_Value ("AD_Window_ID", null);
 else 
set_Value ("AD_Window_ID", new Integer(AD_Window_ID));
}
/** Get Window.
Data entry or display window */
public int getAD_Window_ID() 
{
Integer ii = (Integer)get_Value("AD_Window_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute */
public void setAttribute (String Attribute)
{
if (Attribute == null) throw new IllegalArgumentException ("Attribute is mandatory");
if (Attribute.length() > 60)
{
log.warning("Length > 60 - truncated");
Attribute = Attribute.substring(0,59);
}
set_Value ("Attribute", Attribute);
}
/** Get Attribute */
public String getAttribute() 
{
return (String)get_Value("Attribute");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getAttribute());
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 60)
{
log.warning("Length > 60 - truncated");
Value = Value.substring(0,59);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
