/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AttributeSearch
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.953 */
public class X_M_AttributeSearch extends PO
{
/** Constructor estándar */
public X_M_AttributeSearch (Properties ctx, int M_AttributeSearch_ID, String trxName)
{
super (ctx, M_AttributeSearch_ID, trxName);
/** if (M_AttributeSearch_ID == 0)
{
setM_AttributeSearch_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_M_AttributeSearch (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=564 */
public static final int Table_ID=564;

/** TableName=M_AttributeSearch */
public static final String Table_Name="M_AttributeSearch";

protected static KeyNamePair Model = new KeyNamePair(564,"M_AttributeSearch");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_AttributeSearch[").append(getID()).append("]");
return sb.toString();
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
/** Set Attribute Search.
Common Search Attribute  */
public void setM_AttributeSearch_ID (int M_AttributeSearch_ID)
{
set_ValueNoCheck ("M_AttributeSearch_ID", new Integer(M_AttributeSearch_ID));
}
/** Get Attribute Search.
Common Search Attribute  */
public int getM_AttributeSearch_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSearch_ID");
if (ii == null) return 0;
return ii.intValue();
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
