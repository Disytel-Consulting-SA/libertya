/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por B_TopicCategory
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.109 */
public class X_B_TopicCategory extends PO
{
/** Constructor estándar */
public X_B_TopicCategory (Properties ctx, int B_TopicCategory_ID, String trxName)
{
super (ctx, B_TopicCategory_ID, trxName);
/** if (B_TopicCategory_ID == 0)
{
setB_TopicCategory_ID (0);
setB_TopicType_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_B_TopicCategory (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=691 */
public static final int Table_ID=691;

/** TableName=B_TopicCategory */
public static final String Table_Name="B_TopicCategory";

protected static KeyNamePair Model = new KeyNamePair(691,"B_TopicCategory");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_B_TopicCategory[").append(getID()).append("]");
return sb.toString();
}
/** Set Topic Category.
Auction Topic Category */
public void setB_TopicCategory_ID (int B_TopicCategory_ID)
{
set_ValueNoCheck ("B_TopicCategory_ID", new Integer(B_TopicCategory_ID));
}
/** Get Topic Category.
Auction Topic Category */
public int getB_TopicCategory_ID() 
{
Integer ii = (Integer)get_Value("B_TopicCategory_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Topic Type.
Auction Topic Type */
public void setB_TopicType_ID (int B_TopicType_ID)
{
set_ValueNoCheck ("B_TopicType_ID", new Integer(B_TopicType_ID));
}
/** Get Topic Type.
Auction Topic Type */
public int getB_TopicType_ID() 
{
Integer ii = (Integer)get_Value("B_TopicType_ID");
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
}
