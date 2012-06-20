/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por A_RegistrationProduct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.89 */
public class X_A_RegistrationProduct extends PO
{
/** Constructor estándar */
public X_A_RegistrationProduct (Properties ctx, int A_RegistrationProduct_ID, String trxName)
{
super (ctx, A_RegistrationProduct_ID, trxName);
/** if (A_RegistrationProduct_ID == 0)
{
setA_RegistrationAttribute_ID (0);
setM_Product_ID (0);
}
 */
}
/** Load Constructor */
public X_A_RegistrationProduct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=715 */
public static final int Table_ID=715;

/** TableName=A_RegistrationProduct */
public static final String Table_Name="A_RegistrationProduct";

protected static KeyNamePair Model = new KeyNamePair(715,"A_RegistrationProduct");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_A_RegistrationProduct[").append(getID()).append("]");
return sb.toString();
}
/** Set Registration Attribute.
Asset Registration Attribute */
public void setA_RegistrationAttribute_ID (int A_RegistrationAttribute_ID)
{
set_ValueNoCheck ("A_RegistrationAttribute_ID", new Integer(A_RegistrationAttribute_ID));
}
/** Get Registration Attribute.
Asset Registration Attribute */
public int getA_RegistrationAttribute_ID() 
{
Integer ii = (Integer)get_Value("A_RegistrationAttribute_ID");
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
}
