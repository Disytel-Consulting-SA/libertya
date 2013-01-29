/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_RelatedProduct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:39.562 */
public class X_M_RelatedProduct extends PO
{
/** Constructor estándar */
public X_M_RelatedProduct (Properties ctx, int M_RelatedProduct_ID, String trxName)
{
super (ctx, M_RelatedProduct_ID, trxName);
/** if (M_RelatedProduct_ID == 0)
{
setM_Product_ID (0);
setName (null);
setRelatedProductType (null);
setRelatedProduct_ID (0);
}
 */
}
/** Load Constructor */
public X_M_RelatedProduct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=662 */
public static final int Table_ID=662;

/** TableName=M_RelatedProduct */
public static final String Table_Name="M_RelatedProduct";

protected static KeyNamePair Model = new KeyNamePair(662,"M_RelatedProduct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_RelatedProduct[").append(getID()).append("]");
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
public static final int RELATEDPRODUCTTYPE_AD_Reference_ID=313;
/** Web Promotion = P */
public static final String RELATEDPRODUCTTYPE_WebPromotion = "P";
/** Alternative = A */
public static final String RELATEDPRODUCTTYPE_Alternative = "A";
/** Supplemental = S */
public static final String RELATEDPRODUCTTYPE_Supplemental = "S";
/** Set Related Product Type */
public void setRelatedProductType (String RelatedProductType)
{
if (RelatedProductType.equals("P") || RelatedProductType.equals("A") || RelatedProductType.equals("S"));
 else throw new IllegalArgumentException ("RelatedProductType Invalid value - Reference_ID=313 - P - A - S");
if (RelatedProductType == null) throw new IllegalArgumentException ("RelatedProductType is mandatory");
if (RelatedProductType.length() > 1)
{
log.warning("Length > 1 - truncated");
RelatedProductType = RelatedProductType.substring(0,0);
}
set_Value ("RelatedProductType", RelatedProductType);
}
/** Get Related Product Type */
public String getRelatedProductType() 
{
return (String)get_Value("RelatedProductType");
}
public static final int RELATEDPRODUCT_ID_AD_Reference_ID=162;
/** Set Related Product.
Related Product */
public void setRelatedProduct_ID (int RelatedProduct_ID)
{
set_ValueNoCheck ("RelatedProduct_ID", new Integer(RelatedProduct_ID));
}
/** Get Related Product.
Related Product */
public int getRelatedProduct_ID() 
{
Integer ii = (Integer)get_Value("RelatedProduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
