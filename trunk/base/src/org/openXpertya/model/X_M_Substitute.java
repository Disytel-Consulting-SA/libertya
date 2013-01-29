/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Substitute
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:39.859 */
public class X_M_Substitute extends PO
{
/** Constructor estándar */
public X_M_Substitute (Properties ctx, int M_Substitute_ID, String trxName)
{
super (ctx, M_Substitute_ID, trxName);
/** if (M_Substitute_ID == 0)
{
setM_Product_ID (0);
setName (null);
setSubstitute_ID (0);
}
 */
}
/** Load Constructor */
public X_M_Substitute (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=213 */
public static final int Table_ID=213;

/** TableName=M_Substitute */
public static final String Table_Name="M_Substitute";

protected static KeyNamePair Model = new KeyNamePair(213,"M_Substitute");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Substitute[").append(getID()).append("]");
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
public static final int M_PRODUCT_ID_AD_Reference_ID=162;
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
set_ValueNoCheck ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
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
public static final int SUBSTITUTE_ID_AD_Reference_ID=162;
/** Set Substitute.
Entity which can be used in place of this entity */
public void setSubstitute_ID (int Substitute_ID)
{
set_ValueNoCheck ("Substitute_ID", new Integer(Substitute_ID));
}
/** Get Substitute.
Entity which can be used in place of this entity */
public int getSubstitute_ID() 
{
Integer ii = (Integer)get_Value("Substitute_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
