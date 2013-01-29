/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Production_Orderline
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-03-05 08:29:30.937 */
public class X_C_Production_Orderline extends PO
{
/** Constructor estándar */
public X_C_Production_Orderline (Properties ctx, int C_Production_Orderline_ID, String trxName)
{
super (ctx, C_Production_Orderline_ID, trxName);
/** if (C_Production_Orderline_ID == 0)
{
setC_Production_Order_ID (0);
setC_Production_Orderline_ID (0);
setC_UOM_ID (0);	// @#C_UOM_ID@
setDateOrdered (new Timestamp(System.currentTimeMillis()));	// @DateOrdered@
setIsDescription (false);
setLine (0);	// @SQL=SELECT COALESCE(MAX(Line),0)+10 AS DefaultValue FROM C_Production_OrderLine WHERE C_Production_Order_ID=@C_Production_Order_ID@
setM_Locator_ID (0);	// @M_Locator_ID@
setM_Product_ID (0);
setM_Warehouse_ID (0);	// @M_Warehouse_ID@
setProcessed (false);
setQtyEntered (Env.ZERO);
setQtyOrdered (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_Production_Orderline (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000127 */
public static final int Table_ID=1000127;

/** TableName=C_Production_Orderline */
public static final String Table_Name="C_Production_Orderline";

protected static KeyNamePair Model = new KeyNamePair(1000127,"C_Production_Orderline");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Production_Orderline[").append(getID()).append("]");
return sb.toString();
}
/** Set C_Production_Order_ID */
public void setC_Production_Order_ID (int C_Production_Order_ID)
{
set_Value ("C_Production_Order_ID", new Integer(C_Production_Order_ID));
}
/** Get C_Production_Order_ID */
public int getC_Production_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Production_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Production_Order_ID()));
}
/** Set C_Production_Orderline_ID */
public void setC_Production_Orderline_ID (int C_Production_Orderline_ID)
{
set_ValueNoCheck ("C_Production_Orderline_ID", new Integer(C_Production_Orderline_ID));
}
/** Get C_Production_Orderline_ID */
public int getC_Production_Orderline_ID() 
{
Integer ii = (Integer)get_Value("C_Production_Orderline_ID");
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
/** Set TIMESTAMP Delivered.
TIMESTAMP when the product was delivered */
public void setDateDelivered (Timestamp DateDelivered)
{
set_Value ("DateDelivered", DateDelivered);
}
/** Get TIMESTAMP Delivered.
TIMESTAMP when the product was delivered */
public Timestamp getDateDelivered() 
{
return (Timestamp)get_Value("DateDelivered");
}
/** Set TIMESTAMP Ordered.
TIMESTAMP of Order */
public void setDateOrdered (Timestamp DateOrdered)
{
if (DateOrdered == null) throw new IllegalArgumentException ("DateOrdered is mandatory");
set_Value ("DateOrdered", DateOrdered);
}
/** Get TIMESTAMP Ordered.
TIMESTAMP of Order */
public Timestamp getDateOrdered() 
{
return (Timestamp)get_Value("DateOrdered");
}
/** Set TIMESTAMP Promised.
TIMESTAMP Order was promised */
public void setDatePromised (Timestamp DatePromised)
{
set_Value ("DatePromised", DatePromised);
}
/** Get TIMESTAMP Promised.
TIMESTAMP Order was promised */
public Timestamp getDatePromised() 
{
return (Timestamp)get_Value("DatePromised");
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
/** Set Description Only.
if true, the line is just description and no transaction */
public void setIsDescription (boolean IsDescription)
{
set_Value ("IsDescription", new Boolean(IsDescription));
}
/** Get Description Only.
if true, the line is just description and no transaction */
public boolean isDescription() 
{
Object oo = get_Value("IsDescription");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Attribute Set Instance.
Product Attribute Set Instance */
public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
{
if (M_AttributeSetInstance_ID <= 0) set_Value ("M_AttributeSetInstance_ID", null);
 else 
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
public static final int M_WAREHOUSE_ID_AD_Reference_ID=197;
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_Value ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
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
/** Set Ordered Quantity.
Ordered Quantity */
public void setQtyOrdered (BigDecimal QtyOrdered)
{
if (QtyOrdered == null) throw new IllegalArgumentException ("QtyOrdered is mandatory");
set_Value ("QtyOrdered", QtyOrdered);
}
/** Get Ordered Quantity.
Ordered Quantity */
public BigDecimal getQtyOrdered() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyOrdered");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int REF_ORDERLINE_ID_AD_Reference_ID=1000063;
/** Set Referenced Order Line.
Reference to corresponding Sales/Purchase Order */
public void setRef_OrderLine_ID (int Ref_OrderLine_ID)
{
if (Ref_OrderLine_ID <= 0) set_Value ("Ref_OrderLine_ID", null);
 else 
set_Value ("Ref_OrderLine_ID", new Integer(Ref_OrderLine_ID));
}
/** Get Referenced Order Line.
Reference to corresponding Sales/Purchase Order */
public int getRef_OrderLine_ID() 
{
Integer ii = (Integer)get_Value("Ref_OrderLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Resource Assignment.
Resource Assignment */
public void setS_ResourceAssignment_ID (int S_ResourceAssignment_ID)
{
if (S_ResourceAssignment_ID <= 0) set_Value ("S_ResourceAssignment_ID", null);
 else 
set_Value ("S_ResourceAssignment_ID", new Integer(S_ResourceAssignment_ID));
}
/** Get Resource Assignment.
Resource Assignment */
public int getS_ResourceAssignment_ID() 
{
Integer ii = (Integer)get_Value("S_ResourceAssignment_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
