/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_UserDef_Tab
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:25.656 */
public class X_AD_UserDef_Tab extends PO
{
/** Constructor estándar */
public X_AD_UserDef_Tab (Properties ctx, int AD_UserDef_Tab_ID, String trxName)
{
super (ctx, AD_UserDef_Tab_ID, trxName);
/** if (AD_UserDef_Tab_ID == 0)
{
setAD_Tab_ID (0);
setAD_UserDef_Tab_ID (0);
setAD_UserDef_Win_ID (0);
setIsMultiRowOnly (false);
setIsReadOnly (false);
setIsSingleRow (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_UserDef_Tab (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=466 */
public static final int Table_ID=466;

/** TableName=AD_UserDef_Tab */
public static final String Table_Name="AD_UserDef_Tab";

protected static KeyNamePair Model = new KeyNamePair(466,"AD_UserDef_Tab");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_UserDef_Tab[").append(getID()).append("]");
return sb.toString();
}
/** Set Tab.
Tab within a Window */
public void setAD_Tab_ID (int AD_Tab_ID)
{
set_Value ("AD_Tab_ID", new Integer(AD_Tab_ID));
}
/** Get Tab.
Tab within a Window */
public int getAD_Tab_ID() 
{
Integer ii = (Integer)get_Value("AD_Tab_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set User defined Tab */
public void setAD_UserDef_Tab_ID (int AD_UserDef_Tab_ID)
{
set_ValueNoCheck ("AD_UserDef_Tab_ID", new Integer(AD_UserDef_Tab_ID));
}
/** Get User defined Tab */
public int getAD_UserDef_Tab_ID() 
{
Integer ii = (Integer)get_Value("AD_UserDef_Tab_ID");
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
/** Set Multi Row Only.
This applies to Multi-Row view only */
public void setIsMultiRowOnly (boolean IsMultiRowOnly)
{
set_Value ("IsMultiRowOnly", new Boolean(IsMultiRowOnly));
}
/** Get Multi Row Only.
This applies to Multi-Row view only */
public boolean isMultiRowOnly() 
{
Object oo = get_Value("IsMultiRowOnly");
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
/** Set Single Row Layout.
Default for toggle between Single- and Multi-Row (Grid) Layout */
public void setIsSingleRow (boolean IsSingleRow)
{
set_Value ("IsSingleRow", new Boolean(IsSingleRow));
}
/** Get Single Row Layout.
Default for toggle between Single- and Multi-Row (Grid) Layout */
public boolean isSingleRow() 
{
Object oo = get_Value("IsSingleRow");
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
