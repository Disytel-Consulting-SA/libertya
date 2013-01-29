/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_UserDef_Win
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:25.671 */
public class X_AD_UserDef_Win extends PO
{
/** Constructor estándar */
public X_AD_UserDef_Win (Properties ctx, int AD_UserDef_Win_ID, String trxName)
{
super (ctx, AD_UserDef_Win_ID, trxName);
/** if (AD_UserDef_Win_ID == 0)
{
setAD_Language (null);
setAD_UserDef_Win_ID (0);
setAD_Window_ID (0);
setIsDefault (false);
setIsReadOnly (false);
setIsUserUpdateable (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_UserDef_Win (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=467 */
public static final int Table_ID=467;

/** TableName=AD_UserDef_Win */
public static final String Table_Name="AD_UserDef_Win";

protected static KeyNamePair Model = new KeyNamePair(467,"AD_UserDef_Win");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_UserDef_Win[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_LANGUAGE_AD_Reference_ID=106;
/** Set Language.
Language for this entity */
public void setAD_Language (String AD_Language)
{
if (AD_Language == null) throw new IllegalArgumentException ("AD_Language is mandatory");
if (AD_Language.length() > 6)
{
log.warning("Length > 6 - truncated");
AD_Language = AD_Language.substring(0,5);
}
set_Value ("AD_Language", AD_Language);
}
/** Get Language.
Language for this entity */
public String getAD_Language() 
{
return (String)get_Value("AD_Language");
}
/** Set Role.
Responsibility Role */
public void setAD_Role_ID (int AD_Role_ID)
{
if (AD_Role_ID <= 0) set_Value ("AD_Role_ID", null);
 else 
set_Value ("AD_Role_ID", new Integer(AD_Role_ID));
}
/** Get Role.
Responsibility Role */
public int getAD_Role_ID() 
{
Integer ii = (Integer)get_Value("AD_Role_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set User defined Window */
public void setAD_UserDef_Win_ID (int AD_UserDef_Win_ID)
{
set_ValueNoCheck ("AD_UserDef_Win_ID", new Integer(AD_UserDef_Win_ID));
}
/** Get User defined Window */
public int getAD_UserDef_Win_ID() 
{
Integer ii = (Integer)get_Value("AD_UserDef_Win_ID");
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Read Only.
Field is read only */
public void setIsReadOnly (boolean IsReadOnly)
{
set_Value ("IsReadOnly", new Boolean(IsReadOnly));
}
/** Get Read Only.
Field is read only */
public boolean isReadOnly() 
{
Object oo = get_Value("IsReadOnly");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set User updateable.
The field can be updated by the user */
public void setIsUserUpdateable (boolean IsUserUpdateable)
{
set_Value ("IsUserUpdateable", new Boolean(IsUserUpdateable));
}
/** Get User updateable.
The field can be updated by the user */
public boolean isUserUpdateable() 
{
Object oo = get_Value("IsUserUpdateable");
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
}
