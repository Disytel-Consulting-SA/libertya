/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por D_Filetype
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.812 */
public class X_D_Filetype extends PO
{
/** Constructor estándar */
public X_D_Filetype (Properties ctx, int D_Filetype_ID, String trxName)
{
super (ctx, D_Filetype_ID, trxName);
/** if (D_Filetype_ID == 0)
{
setD_Filetype_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_D_Filetype (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000105 */
public static final int Table_ID=1000105;

/** TableName=D_Filetype */
public static final String Table_Name="D_Filetype";

protected static KeyNamePair Model = new KeyNamePair(1000105,"D_Filetype");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_D_Filetype[").append(getID()).append("]");
return sb.toString();
}
/** Set D_Filetype_ID */
public void setD_Filetype_ID (int D_Filetype_ID)
{
set_ValueNoCheck ("D_Filetype_ID", new Integer(D_Filetype_ID));
}
/** Get D_Filetype_ID */
public int getD_Filetype_ID() 
{
Integer ii = (Integer)get_Value("D_Filetype_ID");
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
/** Set nameprint */
public void setnameprint (String nameprint)
{
if (nameprint != null && nameprint.length() > 60)
{
log.warning("Length > 60 - truncated");
nameprint = nameprint.substring(0,59);
}
set_Value ("nameprint", nameprint);
}
/** Get nameprint */
public String getnameprint() 
{
return (String)get_Value("nameprint");
}
}
