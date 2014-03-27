/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Transaction
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-03-27 15:06:29.14 */
public class X_M_Transaction extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Transaction (Properties ctx, int M_Transaction_ID, String trxName)
{
super (ctx, M_Transaction_ID, trxName);
/** if (M_Transaction_ID == 0)
{
setM_AttributeSetInstance_ID (0);
setM_Locator_ID (0);
setMovementDate (new Timestamp(System.currentTimeMillis()));
setMovementQty (Env.ZERO);
setMovementType (null);
setM_Product_ID (0);
setM_Transaction_ID (0);
}
 */
}
/** Load Constructor */
public X_M_Transaction (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Transaction");

/** TableName=M_Transaction */
public static final String Table_Name="M_Transaction";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Transaction");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Transaction[").append(getID()).append("]");
return sb.toString();
}
/** Set Project Issue.
Project Issues (Material, Labor) */
public void setC_ProjectIssue_ID (int C_ProjectIssue_ID)
{
if (C_ProjectIssue_ID <= 0) set_ValueNoCheck ("C_ProjectIssue_ID", null);
 else 
set_ValueNoCheck ("C_ProjectIssue_ID", new Integer(C_ProjectIssue_ID));
}
/** Get Project Issue.
Project Issues (Material, Labor) */
public int getC_ProjectIssue_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectIssue_ID");
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
set_ValueNoCheck ("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
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
if (M_InOutLine_ID <= 0) set_ValueNoCheck ("M_InOutLine_ID", null);
 else 
set_ValueNoCheck ("M_InOutLine_ID", new Integer(M_InOutLine_ID));
}
/** Get Shipment/Receipt Line.
Line on Shipment or Receipt document */
public int getM_InOutLine_ID() 
{
Integer ii = (Integer)get_Value("M_InOutLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Phys.Inventory Line.
Unique line in an Inventory document */
public void setM_InventoryLine_ID (int M_InventoryLine_ID)
{
if (M_InventoryLine_ID <= 0) set_ValueNoCheck ("M_InventoryLine_ID", null);
 else 
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
/** Set Locator.
Warehouse Locator */
public void setM_Locator_ID (int M_Locator_ID)
{
set_ValueNoCheck ("M_Locator_ID", new Integer(M_Locator_ID));
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
if (M_MovementLine_ID <= 0) set_ValueNoCheck ("M_MovementLine_ID", null);
 else 
set_ValueNoCheck ("M_MovementLine_ID", new Integer(M_MovementLine_ID));
}
/** Get Move Line.
Inventory Move document Line */
public int getM_MovementLine_ID() 
{
Integer ii = (Integer)get_Value("M_MovementLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Movement Date.
Date a product was moved in or out of inventory */
public void setMovementDate (Timestamp MovementDate)
{
if (MovementDate == null) throw new IllegalArgumentException ("MovementDate is mandatory");
set_ValueNoCheck ("MovementDate", MovementDate);
}
/** Get Movement Date.
Date a product was moved in or out of inventory */
public Timestamp getMovementDate() 
{
return (Timestamp)get_Value("MovementDate");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getMovementDate()));
}
/** Set Movement Quantity.
Quantity of a product moved. */
public void setMovementQty (BigDecimal MovementQty)
{
if (MovementQty == null) throw new IllegalArgumentException ("MovementQty is mandatory");
set_ValueNoCheck ("MovementQty", MovementQty);
}
/** Get Movement Quantity.
Quantity of a product moved. */
public BigDecimal getMovementQty() 
{
BigDecimal bd = (BigDecimal)get_Value("MovementQty");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int MOVEMENTTYPE_AD_Reference_ID = MReference.getReferenceID("M_Transaction Movement Type");
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
/** Customer Shipment = C- */
public static final String MOVEMENTTYPE_CustomerShipment = "C-";
/** Customer Returns = C+ */
public static final String MOVEMENTTYPE_CustomerReturns = "C+";
/** Work Order + = W+ */
public static final String MOVEMENTTYPE_WorkOrderPlus = "W+";
/** Work Order - = W- */
public static final String MOVEMENTTYPE_WorkOrder_ = "W-";
/** Production + = P+ */
public static final String MOVEMENTTYPE_ProductionPlus = "P+";
/** Production - = P- */
public static final String MOVEMENTTYPE_Production_ = "P-";
/** Set Movement Type.
Method of moving the inventory */
public void setMovementType (String MovementType)
{
if (MovementType.equals("V+") || MovementType.equals("V-") || MovementType.equals("I-") || MovementType.equals("I+") || MovementType.equals("M-") || MovementType.equals("M+") || MovementType.equals("C-") || MovementType.equals("C+") || MovementType.equals("W+") || MovementType.equals("W-") || MovementType.equals("P+") || MovementType.equals("P-"));
 else throw new IllegalArgumentException ("MovementType Invalid value - Reference = MOVEMENTTYPE_AD_Reference_ID - V+ - V- - I- - I+ - M- - M+ - C- - C+ - W+ - W- - P+ - P-");
if (MovementType == null) throw new IllegalArgumentException ("MovementType is mandatory");
if (MovementType.length() > 2)
{
log.warning("Length > 2 - truncated");
MovementType = MovementType.substring(0,2);
}
set_ValueNoCheck ("MovementType", MovementType);
}
/** Get Movement Type.
Method of moving the inventory */
public String getMovementType() 
{
return (String)get_Value("MovementType");
}
/** Set Order BOM Line ID */
public void setMPC_Order_BOMLine_ID (int MPC_Order_BOMLine_ID)
{
if (MPC_Order_BOMLine_ID <= 0) set_Value ("MPC_Order_BOMLine_ID", null);
 else 
set_Value ("MPC_Order_BOMLine_ID", new Integer(MPC_Order_BOMLine_ID));
}
/** Get Order BOM Line ID */
public int getMPC_Order_BOMLine_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_BOMLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Manufacturing Order.
Manufacturing Order */
public void setMPC_Order_ID (int MPC_Order_ID)
{
if (MPC_Order_ID <= 0) set_Value ("MPC_Order_ID", null);
 else 
set_Value ("MPC_Order_ID", new Integer(MPC_Order_ID));
}
/** Get Manufacturing Order.
Manufacturing Order */
public int getMPC_Order_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Production Line.
Document Line representing a production */
public void setM_ProductionLine_ID (int M_ProductionLine_ID)
{
if (M_ProductionLine_ID <= 0) set_ValueNoCheck ("M_ProductionLine_ID", null);
 else 
set_ValueNoCheck ("M_ProductionLine_ID", new Integer(M_ProductionLine_ID));
}
/** Get Production Line.
Document Line representing a production */
public int getM_ProductionLine_ID() 
{
Integer ii = (Integer)get_Value("M_ProductionLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Inventory Transaction */
public void setM_Transaction_ID (int M_Transaction_ID)
{
set_ValueNoCheck ("M_Transaction_ID", new Integer(M_Transaction_ID));
}
/** Get Inventory Transaction */
public int getM_Transaction_ID() 
{
Integer ii = (Integer)get_Value("M_Transaction_ID");
if (ii == null) return 0;
return ii.intValue();
}

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO M_Transaction(AD_Client_ID,AD_Org_ID,C_ProjectIssue_ID,Created,CreatedBy,Description,IsActive,M_AttributeSetInstance_ID,M_InOutLine_ID,M_InventoryLine_ID,M_Locator_ID,M_MovementLine_ID,MovementDate,MovementQty,MovementType,MPC_Order_BOMLine_ID,MPC_Order_ID,M_Product_ID,M_ProductionLine_ID,M_Transaction_ID,Updated,UpdatedBy) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		 if (getAD_Client_ID() == 0) sql = sql.replaceFirst("AD_Client_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_Org_ID() == 0) sql = sql.replaceFirst("AD_Org_ID,","").replaceFirst("\\?,", "");
 		 if (getC_ProjectIssue_ID() == 0) sql = sql.replaceFirst("C_ProjectIssue_ID,","").replaceFirst("\\?,", "");
 		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getCreatedBy() == 0) sql = sql.replaceFirst("CreatedBy,","").replaceFirst("\\?,", "");
 		 if (getDescription() == null) sql = sql.replaceFirst("Description,","").replaceFirst("\\?,", "");
 		 if (getM_AttributeSetInstance_ID() == 0) sql = sql.replaceFirst("M_AttributeSetInstance_ID,","").replaceFirst("\\?,", "");
 		 if (getM_InOutLine_ID() == 0) sql = sql.replaceFirst("M_InOutLine_ID,","").replaceFirst("\\?,", "");
 		 if (getM_InventoryLine_ID() == 0) sql = sql.replaceFirst("M_InventoryLine_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Locator_ID() == 0) sql = sql.replaceFirst("M_Locator_ID,","").replaceFirst("\\?,", "");
 		 if (getM_MovementLine_ID() == 0) sql = sql.replaceFirst("M_MovementLine_ID,","").replaceFirst("\\?,", "");
 		 if (getMovementDate() == null) sql = sql.replaceFirst("MovementDate,","").replaceFirst("\\?,", "");
 		 if (getMovementQty() == null) sql = sql.replaceFirst("MovementQty,","").replaceFirst("\\?,", "");
 		 if (getMovementType() == null) sql = sql.replaceFirst("MovementType,","").replaceFirst("\\?,", "");
 		 if (getMPC_Order_BOMLine_ID() == 0) sql = sql.replaceFirst("MPC_Order_BOMLine_ID,","").replaceFirst("\\?,", "");
 		 if (getMPC_Order_ID() == 0) sql = sql.replaceFirst("MPC_Order_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Product_ID() == 0) sql = sql.replaceFirst("M_Product_ID,","").replaceFirst("\\?,", "");
 		 if (getM_ProductionLine_ID() == 0) sql = sql.replaceFirst("M_ProductionLine_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Transaction_ID() == 0) sql = sql.replaceFirst("M_Transaction_ID,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getUpdatedBy() == 0) sql = sql.replaceFirst("UpdatedBy,","").replaceFirst("\\?,", "");
 
 		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 if (getAD_Client_ID() != 0) pstmt.setInt(col++, getAD_Client_ID());
		 if (getAD_Org_ID() != 0) pstmt.setInt(col++, getAD_Org_ID());
		 if (getC_ProjectIssue_ID() != 0) pstmt.setInt(col++, getC_ProjectIssue_ID());
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 if (getCreatedBy() != 0) pstmt.setInt(col++, getCreatedBy());
		 if (getDescription() != null) pstmt.setString(col++, getDescription());
		 pstmt.setString(col++, isActive()?"Y":"N");
		 if (getM_AttributeSetInstance_ID() != 0) pstmt.setInt(col++, getM_AttributeSetInstance_ID());
		 if (getM_InOutLine_ID() != 0) pstmt.setInt(col++, getM_InOutLine_ID());
		 if (getM_InventoryLine_ID() != 0) pstmt.setInt(col++, getM_InventoryLine_ID());
		 if (getM_Locator_ID() != 0) pstmt.setInt(col++, getM_Locator_ID());
		 if (getM_MovementLine_ID() != 0) pstmt.setInt(col++, getM_MovementLine_ID());
		 if (getMovementDate() != null) pstmt.setTimestamp(col++, getMovementDate());
		 if (getMovementQty() != null) pstmt.setBigDecimal(col++, getMovementQty());
		 if (getMovementType() != null) pstmt.setString(col++, getMovementType());
		 if (getMPC_Order_BOMLine_ID() != 0) pstmt.setInt(col++, getMPC_Order_BOMLine_ID());
		 if (getMPC_Order_ID() != 0) pstmt.setInt(col++, getMPC_Order_ID());
		 if (getM_Product_ID() != 0) pstmt.setInt(col++, getM_Product_ID());
		 if (getM_ProductionLine_ID() != 0) pstmt.setInt(col++, getM_ProductionLine_ID());
		 if (getM_Transaction_ID() != 0) pstmt.setInt(col++, getM_Transaction_ID());
		 if (getUpdated() != null) pstmt.setTimestamp(col++, getUpdated());
		 if (getUpdatedBy() != 0) pstmt.setInt(col++, getUpdatedBy());

		pstmt.executeUpdate();

		return true;

	}
catch (SQLException e) 
{
	log.log(Level.SEVERE, "insertDirect", e);
	log.saveError("Error", DB.getErrorMsg(e) + " - " + e);
	return false;
	}
catch (Exception e2) 
{
	log.log(Level.SEVERE, "insertDirect", e2);
	return false;
}

}

}
