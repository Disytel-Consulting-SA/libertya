/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Repair_Order_Product
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.671 */
public class X_C_Repair_Order_Product extends PO
{
/** Constructor estándar */
public X_C_Repair_Order_Product (Properties ctx, int C_Repair_Order_Product_ID, String trxName)
{
super (ctx, C_Repair_Order_Product_ID, trxName);
/** if (C_Repair_Order_Product_ID == 0)
{
setC_Repair_Order_ID (0);	// @C_Repair_Order_ID@
setC_Repair_Order_Product_ID (0);
setC_UOM_ID (0);	// @#C_UOM_ID@
setM_AttributeSetInstance_ID (0);
setQtyEntered (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_Repair_Order_Product (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000114 */
public static final int Table_ID=1000114;

/** TableName=C_Repair_Order_Product */
public static final String Table_Name="C_Repair_Order_Product";

protected static KeyNamePair Model = new KeyNamePair(1000114,"C_Repair_Order_Product");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Repair_Order_Product[").append(getID()).append("]");
return sb.toString();
}
/** Set C_Repair_Order_ID */
public void setC_Repair_Order_ID (int C_Repair_Order_ID)
{
set_ValueNoCheck ("C_Repair_Order_ID", new Integer(C_Repair_Order_ID));
}
/** Get C_Repair_Order_ID */
public int getC_Repair_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Repair_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Repair_Order_Product_ID */
public void setC_Repair_Order_Product_ID (int C_Repair_Order_Product_ID)
{
set_ValueNoCheck ("C_Repair_Order_Product_ID", new Integer(C_Repair_Order_Product_ID));
}
/** Get C_Repair_Order_Product_ID */
public int getC_Repair_Order_Product_ID() 
{
Integer ii = (Integer)get_Value("C_Repair_Order_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
set_Value ("C_UOM_ID", new Integer(C_UOM_ID));
}
/** Get UOM.
Unit of Measure */
public int getC_UOM_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 255)
{
log.warning("Length > 255 - truncated");
Name = Name.substring(0,254);
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
return new KeyNamePair(getID(), String.valueOf(getName()));
}
/** Set Quantity.
The Quantity Entered is based on the selected UoM */
public void setQtyEntered (BigDecimal QtyEntered)
{
if (QtyEntered == null) throw new IllegalArgumentException ("QtyEntered is mandatory");
set_Value ("QtyEntered", QtyEntered);
}
/** Get Quantity.
The Quantity Entered is based on the selected UoM */
public BigDecimal getQtyEntered() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyEntered");
if (bd == null) return Env.ZERO;
return bd;
}
}
