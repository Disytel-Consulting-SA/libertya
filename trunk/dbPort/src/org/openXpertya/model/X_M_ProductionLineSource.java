/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_ProductionLineSource
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-06-18 14:08:51.718 */
public class X_M_ProductionLineSource extends PO
{
/** Constructor estándar */
public X_M_ProductionLineSource (Properties ctx, int M_ProductionLineSource_ID, String trxName)
{
super (ctx, M_ProductionLineSource_ID, trxName);
/** if (M_ProductionLineSource_ID == 0)
{
setC_Production_Order_ID (0);
setC_Production_Orderline_ID (0);
setC_UOM_ID (0);
setDateOrdered (new Timestamp(System.currentTimeMillis()));
setM_ProductionLineSource_ID (0);
setM_Warehouse_ID (0);
setName (null);
setQtyDelivered (Env.ZERO);
setQtyOrdered (Env.ZERO);
setQtyReserved (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_ProductionLineSource (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000158 */
public static final int Table_ID=1000158;

/** TableName=M_ProductionLineSource */
public static final String Table_Name="M_ProductionLineSource";

protected static KeyNamePair Model = new KeyNamePair(1000158,"M_ProductionLineSource");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_ProductionLineSource[").append(getID()).append("]");
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
/** Set C_Production_Orderline_ID */
public void setC_Production_Orderline_ID (int C_Production_Orderline_ID)
{
set_Value ("C_Production_Orderline_ID", new Integer(C_Production_Orderline_ID));
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
/** Set Date Delivered.
Date when the product was delivered */
public void setDateDelivered (Timestamp DateDelivered)
{
set_Value ("DateDelivered", DateDelivered);
}
/** Get Date Delivered.
Date when the product was delivered */
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
/** Set Production Line Source */
public void setM_ProductionLineSource_ID (int M_ProductionLineSource_ID)
{
set_ValueNoCheck ("M_ProductionLineSource_ID", new Integer(M_ProductionLineSource_ID));
}
/** Get Production Line Source */
public int getM_ProductionLineSource_ID() 
{
Integer ii = (Integer)get_Value("M_ProductionLineSource_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipper.
Method or manner of product delivery */
public void setM_Shipper_ID (int M_Shipper_ID)
{
if (M_Shipper_ID <= 0) set_Value ("M_Shipper_ID", null);
 else 
set_Value ("M_Shipper_ID", new Integer(M_Shipper_ID));
}
/** Get Shipper.
Method or manner of product delivery */
public int getM_Shipper_ID() 
{
Integer ii = (Integer)get_Value("M_Shipper_ID");
if (ii == null) return 0;
return ii.intValue();
}
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
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
/** Set Delivered Quantity.
Delivered Quantity */
public void setQtyDelivered (BigDecimal QtyDelivered)
{
if (QtyDelivered == null) throw new IllegalArgumentException ("QtyDelivered is mandatory");
set_Value ("QtyDelivered", QtyDelivered);
}
/** Get Delivered Quantity.
Delivered Quantity */
public BigDecimal getQtyDelivered() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyDelivered");
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
/** Set Reserved Quantity.
Reserved Quantity */
public void setQtyReserved (BigDecimal QtyReserved)
{
if (QtyReserved == null) throw new IllegalArgumentException ("QtyReserved is mandatory");
set_Value ("QtyReserved", QtyReserved);
}
/** Get Reserved Quantity.
Reserved Quantity */
public BigDecimal getQtyReserved() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyReserved");
if (bd == null) return Env.ZERO;
return bd;
}
}
