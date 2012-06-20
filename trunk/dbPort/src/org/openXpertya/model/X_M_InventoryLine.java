/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_InventoryLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:38.312 */
public class X_M_InventoryLine extends PO
{
/** Constructor estándar */
public X_M_InventoryLine (Properties ctx, int M_InventoryLine_ID, String trxName)
{
super (ctx, M_InventoryLine_ID, trxName);
/** if (M_InventoryLine_ID == 0)
{
setInventoryType (null);	// D
setM_AttributeSetInstance_ID (0);
setM_InventoryLine_ID (0);
setM_Inventory_ID (0);
setM_Locator_ID (0);	// @M_Locator_ID@
setM_Product_ID (0);
setProcessed (false);
setQtyBook (Env.ZERO);
setQtyCount (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_InventoryLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=322 */
public static final int Table_ID=322;

/** TableName=M_InventoryLine */
public static final String Table_Name="M_InventoryLine";

protected static KeyNamePair Model = new KeyNamePair(322,"M_InventoryLine");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_InventoryLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Charge.
Additional document charges */
public void setC_Charge_ID (int C_Charge_ID)
{
if (C_Charge_ID <= 0) set_Value ("C_Charge_ID", null);
 else 
set_Value ("C_Charge_ID", new Integer(C_Charge_ID));
}
/** Get Charge.
Additional document charges */
public int getC_Charge_ID() 
{
Integer ii = (Integer)get_Value("C_Charge_ID");
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
public static final int INVENTORYTYPE_AD_Reference_ID=292;
/** Inventory Difference = D */
public static final String INVENTORYTYPE_InventoryDifference = "D";
/** Charge Account = C */
public static final String INVENTORYTYPE_ChargeAccount = "C";
/** Set Inventory Type.
Type of inventory difference */
public void setInventoryType (String InventoryType)
{
if (InventoryType.equals("D") || InventoryType.equals("C"));
 else throw new IllegalArgumentException ("InventoryType Invalid value - Reference_ID=292 - D - C");
if (InventoryType == null) throw new IllegalArgumentException ("InventoryType is mandatory");
if (InventoryType.length() > 1)
{
log.warning("Length > 1 - truncated");
InventoryType = InventoryType.substring(0,0);
}
set_Value ("InventoryType", InventoryType);
}
/** Get Inventory Type.
Type of inventory difference */
public String getInventoryType() 
{
return (String)get_Value("InventoryType");
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
/** Set Attribute Set Instance.
Product Attribute Set Instance */
public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
{
set_Value ("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
}
/** Get Attribute Set Instance.
Product Attribute Set Instance */
public int getM_AttributeSetInstance_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSetInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Phys.Inventory Line.
Unique line in an Inventory document */
public void setM_InventoryLine_ID (int M_InventoryLine_ID)
{
set_ValueNoCheck ("M_InventoryLine_ID", new Integer(M_InventoryLine_ID));
}
/** Get Phys.Inventory Line.
Unique line in an Inventory document */
public int getM_InventoryLine_ID() 
{
Integer ii = (Integer)get_Value("M_InventoryLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Phys.Inventory.
Parameters for a Physical Inventory */
public void setM_Inventory_ID (int M_Inventory_ID)
{
set_ValueNoCheck ("M_Inventory_ID", new Integer(M_Inventory_ID));
}
/** Get Phys.Inventory.
Parameters for a Physical Inventory */
public int getM_Inventory_ID() 
{
Integer ii = (Integer)get_Value("M_Inventory_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int M_PRODUCT_ID_AD_Reference_ID=171;
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
/** Set Quantity book.
Book Quantity */
public void setQtyBook (BigDecimal QtyBook)
{
if (QtyBook == null) throw new IllegalArgumentException ("QtyBook is mandatory");
set_ValueNoCheck ("QtyBook", QtyBook);
}
/** Get Quantity book.
Book Quantity */
public BigDecimal getQtyBook() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyBook");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Quantity count.
Counted Quantity */
public void setQtyCount (BigDecimal QtyCount)
{
if (QtyCount == null) throw new IllegalArgumentException ("QtyCount is mandatory");
set_Value ("QtyCount", QtyCount);
}
/** Get Quantity count.
Counted Quantity */
public BigDecimal getQtyCount() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyCount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Internal Use Qty.
Internal Use Quantity removed from Inventory */
public void setQtyInternalUse (BigDecimal QtyInternalUse)
{
set_Value ("QtyInternalUse", QtyInternalUse);
}
/** Get Internal Use Qty.
Internal Use Quantity removed from Inventory */
public BigDecimal getQtyInternalUse() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyInternalUse");
if (bd == null) return Env.ZERO;
return bd;
}
}
