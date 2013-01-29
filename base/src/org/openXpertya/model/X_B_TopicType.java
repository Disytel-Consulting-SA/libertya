/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por B_TopicType
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.14 */
public class X_B_TopicType extends PO
{
/** Constructor estándar */
public X_B_TopicType (Properties ctx, int B_TopicType_ID, String trxName)
{
super (ctx, B_TopicType_ID, trxName);
/** if (B_TopicType_ID == 0)
{
setAuctionType (null);
setB_TopicType_ID (0);
setM_PriceList_ID (0);
setM_ProductMember_ID (0);
setM_Product_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_B_TopicType (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=690 */
public static final int Table_ID=690;

/** TableName=B_TopicType */
public static final String Table_Name="B_TopicType";

protected static KeyNamePair Model = new KeyNamePair(690,"B_TopicType");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_B_TopicType[").append(getID()).append("]");
return sb.toString();
}
/** Set Auction Type */
public void setAuctionType (String AuctionType)
{
if (AuctionType == null) throw new IllegalArgumentException ("AuctionType is mandatory");
if (AuctionType.length() > 1)
{
log.warning("Length > 1 - truncated");
AuctionType = AuctionType.substring(0,0);
}
set_Value ("AuctionType", AuctionType);
}
/** Get Auction Type */
public String getAuctionType() 
{
return (String)get_Value("AuctionType");
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
/** Set Price List.
Unique identifier of a Price List */
public void setM_PriceList_ID (int M_PriceList_ID)
{
set_Value ("M_PriceList_ID", new Integer(M_PriceList_ID));
}
/** Get Price List.
Unique identifier of a Price List */
public int getM_PriceList_ID() 
{
Integer ii = (Integer)get_Value("M_PriceList_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCTMEMBER_ID_AD_Reference_ID=162;
/** Set Membership.
Product used to deternine the price of the membership for the topic type */
public void setM_ProductMember_ID (int M_ProductMember_ID)
{
set_Value ("M_ProductMember_ID", new Integer(M_ProductMember_ID));
}
/** Get Membership.
Product used to deternine the price of the membership for the topic type */
public int getM_ProductMember_ID() 
{
Integer ii = (Integer)get_Value("M_ProductMember_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
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
