/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_Inventory
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-11-29 20:45:19.829 */
public class X_I_Inventory extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_Inventory (Properties ctx, int I_Inventory_ID, String trxName)
{
super (ctx, I_Inventory_ID, trxName);
/** if (I_Inventory_ID == 0)
{
setI_Inventory_ID (0);
setI_IsImported (false);
setQtyCount (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_I_Inventory (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_Inventory");

/** TableName=I_Inventory */
public static final String Table_Name="I_Inventory";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_Inventory");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_Inventory[").append(getID()).append("]");
return sb.toString();
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
/** Set Import Error Message.
Messages generated from import process */
public void setI_ErrorMsg (String I_ErrorMsg)
{
if (I_ErrorMsg != null && I_ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
I_ErrorMsg = I_ErrorMsg.substring(0,2000);
}
set_Value ("I_ErrorMsg", I_ErrorMsg);
}
/** Get Import Error Message.
Messages generated from import process */
public String getI_ErrorMsg() 
{
return (String)get_Value("I_ErrorMsg");
}
/** Set Import Inventory.
Import Inventory Transactions */
public void setI_Inventory_ID (int I_Inventory_ID)
{
set_ValueNoCheck ("I_Inventory_ID", new Integer(I_Inventory_ID));
}
/** Get Import Inventory.
Import Inventory Transactions */
public int getI_Inventory_ID() 
{
Integer ii = (Integer)get_Value("I_Inventory_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getI_Inventory_ID()));
}
/** Set Imported.
Has this import been processed */
public void setI_IsImported (boolean I_IsImported)
{
set_Value ("I_IsImported", new Boolean(I_IsImported));
}
/** Get Imported.
Has this import been processed */
public boolean isI_IsImported() 
{
Object oo = get_Value("I_IsImported");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set instance_description */
public void setinstance_description (String instance_description)
{
if (instance_description != null && instance_description.length() > 255)
{
log.warning("Length > 255 - truncated");
instance_description = instance_description.substring(0,255);
}
set_Value ("instance_description", instance_description);
}
/** Get instance_description */
public String getinstance_description() 
{
return (String)get_Value("instance_description");
}
/** Set Inventory DocumentNo */
public void setInventory_DocumentNo (String Inventory_DocumentNo)
{
if (Inventory_DocumentNo != null && Inventory_DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
Inventory_DocumentNo = Inventory_DocumentNo.substring(0,30);
}
set_Value ("Inventory_DocumentNo", Inventory_DocumentNo);
}
/** Get Inventory DocumentNo */
public String getInventory_DocumentNo() 
{
return (String)get_Value("Inventory_DocumentNo");
}
/** Set Locator Key.
Key of the Warehouse Locator */
public void setLocatorValue (String LocatorValue)
{
if (LocatorValue != null && LocatorValue.length() > 40)
{
log.warning("Length > 40 - truncated");
LocatorValue = LocatorValue.substring(0,40);
}
set_Value ("LocatorValue", LocatorValue);
}
/** Get Locator Key.
Key of the Warehouse Locator */
public String getLocatorValue() 
{
return (String)get_Value("LocatorValue");
}
/** Set Lot No.
Lot number (alphanumeric) */
public void setLot (String Lot)
{
if (Lot != null && Lot.length() > 20)
{
log.warning("Length > 20 - truncated");
Lot = Lot.substring(0,20);
}
set_Value ("Lot", Lot);
}
/** Get Lot No.
Lot number (alphanumeric) */
public String getLot() 
{
return (String)get_Value("Lot");
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
/** Set Locator.
Warehouse Locator */
public void setM_Locator_ID (int M_Locator_ID)
{
if (M_Locator_ID <= 0) set_Value ("M_Locator_ID", null);
 else 
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
/** Set Movement Date.
Date a product was moved in or out of inventory */
public void setMovementDate (Timestamp MovementDate)
{
set_Value ("MovementDate", MovementDate);
}
/** Get Movement Date.
Date a product was moved in or out of inventory */
public Timestamp getMovementDate() 
{
return (Timestamp)get_Value("MovementDate");
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
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
if (M_Warehouse_ID <= 0) set_Value ("M_Warehouse_ID", null);
 else 
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
/** Set Org Key.
Key of the Organization */
public void setOrgValue (String OrgValue)
{
if (OrgValue != null && OrgValue.length() > 40)
{
log.warning("Length > 40 - truncated");
OrgValue = OrgValue.substring(0,40);
}
set_Value ("OrgValue", OrgValue);
}
/** Get Org Key.
Key of the Organization */
public String getOrgValue() 
{
return (String)get_Value("OrgValue");
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
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
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
set_Value ("QtyBook", QtyBook);
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
/** Set Serial No.
Product Serial Number  */
public void setSerNo (String SerNo)
{
if (SerNo != null && SerNo.length() > 20)
{
log.warning("Length > 20 - truncated");
SerNo = SerNo.substring(0,20);
}
set_Value ("SerNo", SerNo);
}
/** Get Serial No.
Product Serial Number  */
public String getSerNo() 
{
return (String)get_Value("SerNo");
}
/** Set UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public void setUPC (String UPC)
{
if (UPC != null && UPC.length() > 30)
{
log.warning("Length > 30 - truncated");
UPC = UPC.substring(0,30);
}
set_Value ("UPC", UPC);
}
/** Get UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public String getUPC() 
{
return (String)get_Value("UPC");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value != null && Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
/** Set Warehouse Key.
Key of the Warehouse */
public void setWarehouseValue (String WarehouseValue)
{
if (WarehouseValue != null && WarehouseValue.length() > 40)
{
log.warning("Length > 40 - truncated");
WarehouseValue = WarehouseValue.substring(0,40);
}
set_Value ("WarehouseValue", WarehouseValue);
}
/** Get Warehouse Key.
Key of the Warehouse */
public String getWarehouseValue() 
{
return (String)get_Value("WarehouseValue");
}
/** Set Aisle (X).
X dimension, e.g., Aisle */
public void setX (String X)
{
if (X != null && X.length() > 60)
{
log.warning("Length > 60 - truncated");
X = X.substring(0,60);
}
set_Value ("X", X);
}
/** Get Aisle (X).
X dimension, e.g., Aisle */
public String getX() 
{
return (String)get_Value("X");
}
/** Set Bin (Y).
Y dimension, e.g., Bin */
public void setY (String Y)
{
if (Y != null && Y.length() > 60)
{
log.warning("Length > 60 - truncated");
Y = Y.substring(0,60);
}
set_Value ("Y", Y);
}
/** Get Bin (Y).
Y dimension, e.g., Bin */
public String getY() 
{
return (String)get_Value("Y");
}
/** Set Level (Z).
Z dimension, e.g., Level */
public void setZ (String Z)
{
if (Z != null && Z.length() > 60)
{
log.warning("Length > 60 - truncated");
Z = Z.substring(0,60);
}
set_Value ("Z", Z);
}
/** Get Level (Z).
Z dimension, e.g., Level */
public String getZ() 
{
return (String)get_Value("Z");
}

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO I_Inventory(I_IsImported,I_Inventory_ID,MovementDate,IsActive,Y,QtyBook,X,M_Product_ID,SerNo,M_Locator_ID,CreatedBy,Value,Description,UPC,M_InventoryLine_ID,UpdatedBy,Updated,Processed,Z,Lot,Processing,M_Warehouse_ID,Created,AD_Client_ID,QtyCount,AD_Org_ID,I_ErrorMsg,WarehouseValue,LocatorValue,M_Inventory_ID,instance_description,M_AttributeSetInstance_ID,OrgValue,Inventory_DocumentNo," + getAdditionalParamNames() + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getAdditionalParamMarks() + ") ";

		 if (getMovementDate() == null) sql = sql.replaceFirst("MovementDate,","").replaceFirst("\\?,", "");
 		 if (getY() == null) sql = sql.replaceFirst("Y,","").replaceFirst("\\?,", "");
 		 if (getQtyBook() == null) sql = sql.replaceFirst("QtyBook,","").replaceFirst("\\?,", "");
 		 if (getX() == null) sql = sql.replaceFirst("X,","").replaceFirst("\\?,", "");
 		 if (getM_Product_ID() == 0) sql = sql.replaceFirst("M_Product_ID,","").replaceFirst("\\?,", "");
 		 if (getSerNo() == null) sql = sql.replaceFirst("SerNo,","").replaceFirst("\\?,", "");
 		 if (getM_Locator_ID() == 0) sql = sql.replaceFirst("M_Locator_ID,","").replaceFirst("\\?,", "");
 		 if (getCreatedBy() == 0) sql = sql.replaceFirst("CreatedBy,","").replaceFirst("\\?,", "");
 		 if (getValue() == null) sql = sql.replaceFirst("Value,","").replaceFirst("\\?,", "");
 		 if (getDescription() == null) sql = sql.replaceFirst("Description,","").replaceFirst("\\?,", "");
 		 if (getUPC() == null) sql = sql.replaceFirst("UPC,","").replaceFirst("\\?,", "");
 		 if (getM_InventoryLine_ID() == 0) sql = sql.replaceFirst("M_InventoryLine_ID,","").replaceFirst("\\?,", "");
 		 if (getUpdatedBy() == 0) sql = sql.replaceFirst("UpdatedBy,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getZ() == null) sql = sql.replaceFirst("Z,","").replaceFirst("\\?,", "");
 		 if (getLot() == null) sql = sql.replaceFirst("Lot,","").replaceFirst("\\?,", "");
 		 if (getM_Warehouse_ID() == 0) sql = sql.replaceFirst("M_Warehouse_ID,","").replaceFirst("\\?,", "");
 		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getQtyCount() == null) sql = sql.replaceFirst("QtyCount,","").replaceFirst("\\?,", "");
 		 if (getI_ErrorMsg() == null) sql = sql.replaceFirst("I_ErrorMsg,","").replaceFirst("\\?,", "");
 		 if (getWarehouseValue() == null) sql = sql.replaceFirst("WarehouseValue,","").replaceFirst("\\?,", "");
 		 if (getLocatorValue() == null) sql = sql.replaceFirst("LocatorValue,","").replaceFirst("\\?,", "");
 		 if (getM_Inventory_ID() == 0) sql = sql.replaceFirst("M_Inventory_ID,","").replaceFirst("\\?,", "");
 		 if (getinstance_description() == null) sql = sql.replaceFirst("instance_description,","").replaceFirst("\\?,", "");
 		 if (getM_AttributeSetInstance_ID() == 0) sql = sql.replaceFirst("M_AttributeSetInstance_ID,","").replaceFirst("\\?,", "");
 		 if (getOrgValue() == null) sql = sql.replaceFirst("OrgValue,","").replaceFirst("\\?,", "");
 		 if (getInventory_DocumentNo() == null) sql = sql.replaceFirst("Inventory_DocumentNo,","").replaceFirst("\\?,", "");
 		 skipAdditionalNullValues(sql);
 

 		 sql = sql.replace(",)", ")");
 
		 sql = sql.replace(",,)", ",");
 
		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 pstmt.setString(col++, isI_IsImported()?"Y":"N");
		 pstmt.setInt(col++, getI_Inventory_ID());
		 if (getMovementDate() != null) pstmt.setTimestamp(col++, getMovementDate());
		 pstmt.setString(col++, isActive()?"Y":"N");
		 if (getY() != null) pstmt.setString(col++, getY());
		 if (getQtyBook() != null) pstmt.setBigDecimal(col++, getQtyBook());
		 if (getX() != null) pstmt.setString(col++, getX());
		 if (getM_Product_ID() != 0) pstmt.setInt(col++, getM_Product_ID());
		 if (getSerNo() != null) pstmt.setString(col++, getSerNo());
		 if (getM_Locator_ID() != 0) pstmt.setInt(col++, getM_Locator_ID());
		 if (getCreatedBy() != 0) pstmt.setInt(col++, getCreatedBy());
		 if (getValue() != null) pstmt.setString(col++, getValue());
		 if (getDescription() != null) pstmt.setString(col++, getDescription());
		 if (getUPC() != null) pstmt.setString(col++, getUPC());
		 if (getM_InventoryLine_ID() != 0) pstmt.setInt(col++, getM_InventoryLine_ID());
		 if (getUpdatedBy() != 0) pstmt.setInt(col++, getUpdatedBy());
		 if (getUpdated() != null) pstmt.setTimestamp(col++, getUpdated());
		 pstmt.setString(col++, isProcessed()?"Y":"N");
		 if (getZ() != null) pstmt.setString(col++, getZ());
		 if (getLot() != null) pstmt.setString(col++, getLot());
		 pstmt.setString(col++, isProcessing()?"Y":"N");
		 if (getM_Warehouse_ID() != 0) pstmt.setInt(col++, getM_Warehouse_ID());
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 pstmt.setInt(col++, getAD_Client_ID());
		 if (getQtyCount() != null) pstmt.setBigDecimal(col++, getQtyCount());
		 pstmt.setInt(col++, getAD_Org_ID());
		 if (getI_ErrorMsg() != null) pstmt.setString(col++, getI_ErrorMsg());
		 if (getWarehouseValue() != null) pstmt.setString(col++, getWarehouseValue());
		 if (getLocatorValue() != null) pstmt.setString(col++, getLocatorValue());
		 if (getM_Inventory_ID() != 0) pstmt.setInt(col++, getM_Inventory_ID());
		 if (getinstance_description() != null) pstmt.setString(col++, getinstance_description());
		 if (getM_AttributeSetInstance_ID() != 0) pstmt.setInt(col++, getM_AttributeSetInstance_ID());
		 if (getOrgValue() != null) pstmt.setString(col++, getOrgValue());
		 if (getInventory_DocumentNo() != null) pstmt.setString(col++, getInventory_DocumentNo());
		 col = setAdditionalInsertValues(col, pstmt);
 

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

protected String getAdditionalParamNames() 
{
 return "";
 }
 
protected String getAdditionalParamMarks() 
{
 return "";
 }
 
protected void skipAdditionalNullValues(String sql) 
{
  }
 
protected int setAdditionalInsertValues(int col, PreparedStatement pstmt) throws Exception 
{
 return col;
 }
 
}
