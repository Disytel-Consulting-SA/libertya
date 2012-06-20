/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_ProductionPlan
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:39.453 */
public class X_M_ProductionPlan extends PO
{
/** Constructor estándar */
public X_M_ProductionPlan (Properties ctx, int M_ProductionPlan_ID, String trxName)
{
super (ctx, M_ProductionPlan_ID, trxName);
/** if (M_ProductionPlan_ID == 0)
{
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_ProductionPlan WHERE M_Production_ID=@M_Production_ID@
setM_Locator_ID (0);	// @M_Locator_ID@
setM_Product_ID (0);
setM_ProductionPlan_ID (0);
setM_Production_ID (0);
setProcessed (false);
setProductionQty (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_ProductionPlan (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=385 */
public static final int Table_ID=385;

/** TableName=M_ProductionPlan */
public static final String Table_Name="M_ProductionPlan";

protected static KeyNamePair Model = new KeyNamePair(385,"M_ProductionPlan");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_ProductionPlan[").append(getID()).append("]");
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
/** Set Line No.
Unique line for this document */
public void setLine (int Line)
{
set_Value ("Line", new Integer(Line));
}
/** Get Line No.
Unique line for this document */
public int getLine() 
{
Integer ii = (Integer)get_Value("Line");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getLine()));
}
/** Set Locator.
Warehouse Locator */
public void setM_Locator_ID (int M_Locator_ID)
{
set_Value ("M_Locator_ID", new Integer(M_Locator_ID));
}
/** Get Locator.
Warehouse Locator */
public int getM_Locator_ID() 
{
Integer ii = (Integer)get_Value("M_Locator_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCT_ID_AD_Reference_ID=211;
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
/** Set Production Plan.
Plan for how a product is produced */
public void setM_ProductionPlan_ID (int M_ProductionPlan_ID)
{
set_ValueNoCheck ("M_ProductionPlan_ID", new Integer(M_ProductionPlan_ID));
}
/** Get Production Plan.
Plan for how a product is produced */
public int getM_ProductionPlan_ID() 
{
Integer ii = (Integer)get_Value("M_ProductionPlan_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Production.
Plan for producing a product */
public void setM_Production_ID (int M_Production_ID)
{
set_ValueNoCheck ("M_Production_ID", new Integer(M_Production_ID));
}
/** Get Production.
Plan for producing a product */
public int getM_Production_ID() 
{
Integer ii = (Integer)get_Value("M_Production_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Production Quantity.
Quantity of products to produce */
public void setProductionQty (BigDecimal ProductionQty)
{
if (ProductionQty == null) throw new IllegalArgumentException ("ProductionQty is mandatory");
set_Value ("ProductionQty", ProductionQty);
}
/** Get Production Quantity.
Quantity of products to produce */
public BigDecimal getProductionQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ProductionQty");
if (bd == null) return Env.ZERO;
return bd;
}
}
