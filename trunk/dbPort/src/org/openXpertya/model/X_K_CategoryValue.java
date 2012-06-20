/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por K_CategoryValue
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.406 */
public class X_K_CategoryValue extends PO
{
/** Constructor estándar */
public X_K_CategoryValue (Properties ctx, int K_CategoryValue_ID, String trxName)
{
super (ctx, K_CategoryValue_ID, trxName);
/** if (K_CategoryValue_ID == 0)
{
setK_CategoryValue_ID (0);
setK_Category_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_K_CategoryValue (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=614 */
public static final int Table_ID=614;

/** TableName=K_CategoryValue */
public static final String Table_Name="K_CategoryValue";

protected static KeyNamePair Model = new KeyNamePair(614,"K_CategoryValue");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_K_CategoryValue[").append(getID()).append("]");
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
/** Set Category Value.
The value of the category */
public void setK_CategoryValue_ID (int K_CategoryValue_ID)
{
set_ValueNoCheck ("K_CategoryValue_ID", new Integer(K_CategoryValue_ID));
}
/** Get Category Value.
The value of the category */
public int getK_CategoryValue_ID() 
{
Integer ii = (Integer)get_Value("K_CategoryValue_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Knowledge Category.
Knowledge Category */
public void setK_Category_ID (int K_Category_ID)
{
set_ValueNoCheck ("K_Category_ID", new Integer(K_Category_ID));
}
/** Get Knowledge Category.
Knowledge Category */
public int getK_Category_ID() 
{
Integer ii = (Integer)get_Value("K_Category_ID");
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
