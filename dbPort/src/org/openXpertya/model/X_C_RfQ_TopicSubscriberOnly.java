/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RfQ_TopicSubscriberOnly
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.171 */
public class X_C_RfQ_TopicSubscriberOnly extends PO
{
/** Constructor estándar */
public X_C_RfQ_TopicSubscriberOnly (Properties ctx, int C_RfQ_TopicSubscriberOnly_ID, String trxName)
{
super (ctx, C_RfQ_TopicSubscriberOnly_ID, trxName);
/** if (C_RfQ_TopicSubscriberOnly_ID == 0)
{
setC_RfQ_TopicSubscriberOnly_ID (0);
setC_RfQ_TopicSubscriber_ID (0);
}
 */
}
/** Load Constructor */
public X_C_RfQ_TopicSubscriberOnly (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=747 */
public static final int Table_ID=747;

/** TableName=C_RfQ_TopicSubscriberOnly */
public static final String Table_Name="C_RfQ_TopicSubscriberOnly";

protected static KeyNamePair Model = new KeyNamePair(747,"C_RfQ_TopicSubscriberOnly");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RfQ_TopicSubscriberOnly[").append(getID()).append("]");
return sb.toString();
}
/** Set RfQ Topic Subscriber Restriction.
Include Subscriber only for certain products or product categories */
public void setC_RfQ_TopicSubscriberOnly_ID (int C_RfQ_TopicSubscriberOnly_ID)
{
set_ValueNoCheck ("C_RfQ_TopicSubscriberOnly_ID", new Integer(C_RfQ_TopicSubscriberOnly_ID));
}
/** Get RfQ Topic Subscriber Restriction.
Include Subscriber only for certain products or product categories */
public int getC_RfQ_TopicSubscriberOnly_ID() 
{
Integer ii = (Integer)get_Value("C_RfQ_TopicSubscriberOnly_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set RfQ Subscriber.
Request for Quotation Topic Subscriber */
public void setC_RfQ_TopicSubscriber_ID (int C_RfQ_TopicSubscriber_ID)
{
set_ValueNoCheck ("C_RfQ_TopicSubscriber_ID", new Integer(C_RfQ_TopicSubscriber_ID));
}
/** Get RfQ Subscriber.
Request for Quotation Topic Subscriber */
public int getC_RfQ_TopicSubscriber_ID() 
{
Integer ii = (Integer)get_Value("C_RfQ_TopicSubscriber_ID");
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
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
if (M_Product_Category_ID <= 0) set_Value ("M_Product_Category_ID", null);
 else 
set_Value ("M_Product_Category_ID", new Integer(M_Product_Category_ID));
}
/** Get Product Category.
Category of a Product */
public int getM_Product_Category_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Category_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getM_Product_Category_ID()));
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
set_Value ("M_Product_ID", new Integer(M_Product_ID));
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
