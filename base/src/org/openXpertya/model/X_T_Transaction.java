/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_Transaction
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.218 */
public class X_T_Transaction extends PO
{
/** Constructor estándar */
public X_T_Transaction (Properties ctx, int T_Transaction_ID, String trxName)
{
super (ctx, T_Transaction_ID, trxName);
/** if (T_Transaction_ID == 0)
{
setAD_PInstance_ID (0);
setM_AttributeSetInstance_ID (0);
setM_Locator_ID (0);
setM_Product_ID (0);
setM_Transaction_ID (0);
setMovementDate (new Timestamp(System.currentTimeMillis()));
setMovementQty (Env.ZERO);
setMovementType (null);
}
 */
}
/** Load Constructor */
public X_T_Transaction (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=758 */
public static final int Table_ID=758;

/** TableName=T_Transaction */
public static final String Table_Name="T_Transaction";

protected static KeyNamePair Model = new KeyNamePair(758,"T_Transaction");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_Transaction[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_Value ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project Issue.
Project Issues (Material, Labor) */
public void setC_ProjectIssue_ID (int C_ProjectIssue_ID)
{
if (C_ProjectIssue_ID <= 0) set_Value ("C_ProjectIssue_ID", null);
 else 
set_Value ("C_ProjectIssue_ID", new Integer(C_ProjectIssue_ID));
}
/** Get Project Issue.
Project Issues (Material, Labor) */
public int getC_ProjectIssue_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectIssue_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
if (C_Project_ID <= 0) set_Value ("C_Project_ID", null);
 else 
set_Value ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
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
/** Set Shipment/Receipt Line.
Line on Shipment or Receipt document */
public void setM_InOutLine_ID (int M_InOutLine_ID)
{
if (M_InOutLine_ID <= 0) set_Value ("M_InOutLine_ID", null);
 else 
set_Value ("M_InOutLine_ID", new Integer(M_InOutLine_ID));
}
/** Get Shipment/Receipt Line.
Line on Shipment or Receipt document */
public int getM_InOutLine_ID() 
{
Integer ii = (Integer)get_Value("M_InOutLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipment/Receipt.
Material Shipment Document */
public void setM_InOut_ID (int M_InOut_ID)
{
if (M_InOut_ID <= 0) set_Value ("M_InOut_ID", null);
 else 
set_Value ("M_InOut_ID", new Integer(M_InOut_ID));
}
/** Get Shipment/Receipt.
Material Shipment Document */
public int getM_InOut_ID() 
{
Integer ii = (Integer)get_Value("M_InOut_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Phys.Inventory Line.
Unique line in an Inventory document */
public void setM_InventoryLine_ID (int M_InventoryLine_ID)
{
if (M_InventoryLine_ID <= 0) set_Value ("M_InventoryLine_ID", null);
 else 
set_Value ("M_InventoryLine_ID", new Integer(M_InventoryLine_ID));
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
if (M_Inventory_ID <= 0) set_Value ("M_Inventory_ID", null);
 else 
set_Value ("M_Inventory_ID", new Integer(M_Inventory_ID));
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
/** Set Move Line.
Inventory Move document Line */
public void setM_MovementLine_ID (int M_MovementLine_ID)
{
if (M_MovementLine_ID <= 0) set_Value ("M_MovementLine_ID", null);
 else 
set_Value ("M_MovementLine_ID", new Integer(M_MovementLine_ID));
}
/** Get Move Line.
Inventory Move document Line */
public int getM_MovementLine_ID() 
{
Integer ii = (Integer)get_Value("M_MovementLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Inventory Move.
Movement of Inventory */
public void setM_Movement_ID (int M_Movement_ID)
{
if (M_Movement_ID <= 0) set_Value ("M_Movement_ID", null);
 else 
set_Value ("M_Movement_ID", new Integer(M_Movement_ID));
}
/** Get Inventory Move.
Movement of Inventory */
public int getM_Movement_ID() 
{
Integer ii = (Integer)get_Value("M_Movement_ID");
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
/** Set Production Line.
Document Line representing a production */
public void setM_ProductionLine_ID (int M_ProductionLine_ID)
{
if (M_ProductionLine_ID <= 0) set_Value ("M_ProductionLine_ID", null);
 else 
set_Value ("M_ProductionLine_ID", new Integer(M_ProductionLine_ID));
}
/** Get Production Line.
Document Line representing a production */
public int getM_ProductionLine_ID() 
{
Integer ii = (Integer)get_Value("M_ProductionLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Production.
Plan for producing a product */
public void setM_Production_ID (int M_Production_ID)
{
if (M_Production_ID <= 0) set_Value ("M_Production_ID", null);
 else 
set_Value ("M_Production_ID", new Integer(M_Production_ID));
}
/** Get Production.
Plan for producing a product */
public int getM_Production_ID() 
{
Integer ii = (Integer)get_Value("M_Production_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Inventory Transaction */
public void setM_Transaction_ID (int M_Transaction_ID)
{
set_Value ("M_Transaction_ID", new Integer(M_Transaction_ID));
}
/** Get Inventory Transaction */
public int getM_Transaction_ID() 
{
Integer ii = (Integer)get_Value("M_Transaction_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Movement Date.
Date a product was moved in or out of inventory */
public void setMovementDate (Timestamp MovementDate)
{
if (MovementDate == null) throw new IllegalArgumentException ("MovementDate is mandatory");
set_Value ("MovementDate", MovementDate);
}
/** Get Movement Date.
Date a product was moved in or out of inventory */
public Timestamp getMovementDate() 
{
return (Timestamp)get_Value("MovementDate");
}
/** Set Movement Quantity.
Quantity of a product moved. */
public void setMovementQty (BigDecimal MovementQty)
{
if (MovementQty == null) throw new IllegalArgumentException ("MovementQty is mandatory");
set_Value ("MovementQty", MovementQty);
}
/** Get Movement Quantity.
Quantity of a product moved. */
public BigDecimal getMovementQty() 
{
BigDecimal bd = (BigDecimal)get_Value("MovementQty");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int MOVEMENTTYPE_AD_Reference_ID=189;
/** Production + = P+ */
public static final String MOVEMENTTYPE_ProductionPlus = "P+";
/** Production - = P- */
public static final String MOVEMENTTYPE_Production_ = "P-";
/** Customer Shipment = C- */
public static final String MOVEMENTTYPE_CustomerShipment = "C-";
/** Customer Returns = C+ */
public static final String MOVEMENTTYPE_CustomerReturns = "C+";
/** Work Order + = W+ */
public static final String MOVEMENTTYPE_WorkOrderPlus = "W+";
/** Work Order - = W- */
public static final String MOVEMENTTYPE_WorkOrder_ = "W-";
/** Vendor Receipts = V+ */
public static final String MOVEMENTTYPE_VendorReceipts = "V+";
/** Vendor Returns = V- */
public static final String MOVEMENTTYPE_VendorReturns = "V-";
/** Inventory Out = I- */
public static final String MOVEMENTTYPE_InventoryOut = "I-";
/** Inventory In = I+ */
public static final String MOVEMENTTYPE_InventoryIn = "I+";
/** Movement From = M- */
public static final String MOVEMENTTYPE_MovementFrom = "M-";
/** Movement To = M+ */
public static final String MOVEMENTTYPE_MovementTo = "M+";
/** Set Movement Type.
Method of moving the inventory */
public void setMovementType (String MovementType)
{
if (MovementType.equals("P+") || MovementType.equals("P-") || MovementType.equals("C-") || MovementType.equals("C+") || MovementType.equals("W+") || MovementType.equals("W-") || MovementType.equals("V+") || MovementType.equals("V-") || MovementType.equals("I-") || MovementType.equals("I+") || MovementType.equals("M-") || MovementType.equals("M+"));
 else throw new IllegalArgumentException ("MovementType Invalid value - Reference_ID=189 - P+ - P- - C- - C+ - W+ - W- - V+ - V- - I- - I+ - M- - M+");
if (MovementType == null) throw new IllegalArgumentException ("MovementType is mandatory");
if (MovementType.length() > 2)
{
log.warning("Length > 2 - truncated");
MovementType = MovementType.substring(0,1);
}
set_Value ("MovementType", MovementType);
}
/** Get Movement Type.
Method of moving the inventory */
public String getMovementType() 
{
return (String)get_Value("MovementType");
}
public static final int SEARCH_INOUT_ID_AD_Reference_ID=295;
/** Set Search Shipment/Receipt.
Material Shipment Document */
public void setSearch_InOut_ID (int Search_InOut_ID)
{
if (Search_InOut_ID <= 0) set_Value ("Search_InOut_ID", null);
 else 
set_Value ("Search_InOut_ID", new Integer(Search_InOut_ID));
}
/** Get Search Shipment/Receipt.
Material Shipment Document */
public int getSearch_InOut_ID() 
{
Integer ii = (Integer)get_Value("Search_InOut_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SEARCH_INVOICE_ID_AD_Reference_ID=336;
/** Set Search Invoice.
Search Invoice Identifier */
public void setSearch_Invoice_ID (int Search_Invoice_ID)
{
if (Search_Invoice_ID <= 0) set_Value ("Search_Invoice_ID", null);
 else 
set_Value ("Search_Invoice_ID", new Integer(Search_Invoice_ID));
}
/** Get Search Invoice.
Search Invoice Identifier */
public int getSearch_Invoice_ID() 
{
Integer ii = (Integer)get_Value("Search_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SEARCH_ORDER_ID_AD_Reference_ID=290;
/** Set Search Order.
Order Identifier */
public void setSearch_Order_ID (int Search_Order_ID)
{
if (Search_Order_ID <= 0) set_Value ("Search_Order_ID", null);
 else 
set_Value ("Search_Order_ID", new Integer(Search_Order_ID));
}
/** Get Search Order.
Order Identifier */
public int getSearch_Order_ID() 
{
Integer ii = (Integer)get_Value("Search_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
