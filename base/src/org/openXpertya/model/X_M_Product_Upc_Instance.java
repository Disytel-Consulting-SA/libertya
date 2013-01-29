/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Product_Upc_Instance
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-06-18 14:08:51.812 */
public class X_M_Product_Upc_Instance extends PO
{
/** Constructor estándar */
public X_M_Product_Upc_Instance (Properties ctx, int M_Product_Upc_Instance_ID, String trxName)
{
super (ctx, M_Product_Upc_Instance_ID, trxName);
/** if (M_Product_Upc_Instance_ID == 0)
{
setM_AttributeSetInstance_ID (0);
setM_Product_ID (0);
setM_Product_Upc_Instance_ID (0);
setUPC (null);
}
 */
}
/** Load Constructor */
public X_M_Product_Upc_Instance (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000162 */
public static final int Table_ID=1000162;

/** TableName=M_Product_Upc_Instance */
public static final String Table_Name="M_Product_Upc_Instance";

protected static KeyNamePair Model = new KeyNamePair(1000162,"M_Product_Upc_Instance");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Product_Upc_Instance[").append(getID()).append("]");
return sb.toString();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Attribute Set Instance.
Product Attribute Set Instance */
public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
{
set_ValueNoCheck ("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
}
/** Get Attribute Set Instance.
Product Attribute Set Instance */
public int getM_AttributeSetInstance_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSetInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
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
/** Set Product UPC by Instance */
public void setM_Product_Upc_Instance_ID (int M_Product_Upc_Instance_ID)
{
set_ValueNoCheck ("M_Product_Upc_Instance_ID", new Integer(M_Product_Upc_Instance_ID));
}
/** Get Product UPC by Instance */
public int getM_Product_Upc_Instance_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Upc_Instance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 255)
{
log.warning("Length > 255 - truncated");
Name = Name.substring(0,255);
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
/** Set UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public void setUPC (String UPC)
{
if (UPC == null) throw new IllegalArgumentException ("UPC is mandatory");
if (UPC.length() > 30)
{
log.warning("Length > 30 - truncated");
UPC = UPC.substring(0,30);
}
set_ValueNoCheck ("UPC", UPC);
}
/** Get UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public String getUPC() 
{
return (String)get_Value("UPC");
}
}
