/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_POSKeyLayout
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:31.25 */
public class X_C_POSKeyLayout extends PO
{
/** Constructor estándar */
public X_C_POSKeyLayout (Properties ctx, int C_POSKeyLayout_ID, String trxName)
{
super (ctx, C_POSKeyLayout_ID, trxName);
/** if (C_POSKeyLayout_ID == 0)
{
setC_POSKeyLayout_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_POSKeyLayout (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=749 */
public static final int Table_ID=749;

/** TableName=C_POSKeyLayout */
public static final String Table_Name="C_POSKeyLayout";

protected static KeyNamePair Model = new KeyNamePair(749,"C_POSKeyLayout");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_POSKeyLayout[").append(getID()).append("]");
return sb.toString();
}
/** Set POS Key Layout.
POS Function Key Layout */
public void setC_POSKeyLayout_ID (int C_POSKeyLayout_ID)
{
set_ValueNoCheck ("C_POSKeyLayout_ID", new Integer(C_POSKeyLayout_ID));
}
/** Get POS Key Layout.
POS Function Key Layout */
public int getC_POSKeyLayout_ID() 
{
Integer ii = (Integer)get_Value("C_POSKeyLayout_ID");
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
