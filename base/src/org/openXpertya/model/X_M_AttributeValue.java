/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AttributeValue
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:37.046 */
public class X_M_AttributeValue extends PO
{
/** Constructor estándar */
public X_M_AttributeValue (Properties ctx, int M_AttributeValue_ID, String trxName)
{
super (ctx, M_AttributeValue_ID, trxName);
/** if (M_AttributeValue_ID == 0)
{
setM_AttributeValue_ID (0);
setM_Attribute_ID (0);
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_M_AttributeValue (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=558 */
public static final int Table_ID=558;

/** TableName=M_AttributeValue */
public static final String Table_Name="M_AttributeValue";

protected static KeyNamePair Model = new KeyNamePair(558,"M_AttributeValue");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_AttributeValue[").append(getID()).append("]");
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
/** Set Attribute Value.
Product Attribute Value */
public void setM_AttributeValue_ID (int M_AttributeValue_ID)
{
set_ValueNoCheck ("M_AttributeValue_ID", new Integer(M_AttributeValue_ID));
}
/** Get Attribute Value.
Product Attribute Value */
public int getM_AttributeValue_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeValue_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute.
Product Attribute */
public void setM_Attribute_ID (int M_Attribute_ID)
{
set_ValueNoCheck ("M_Attribute_ID", new Integer(M_Attribute_ID));
}
/** Get Attribute.
Product Attribute */
public int getM_Attribute_ID() 
{
Integer ii = (Integer)get_Value("M_Attribute_ID");
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
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,39);
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
