/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_InventoryLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-09-11 18:54:16.926 */
public class X_M_InventoryLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_InventoryLine (Properties ctx, int M_InventoryLine_ID, String trxName)
{
super (ctx, M_InventoryLine_ID, trxName);
/** if (M_InventoryLine_ID == 0)
{
setCost (Env.ZERO);
setInventoryType (null);	// D
setM_AttributeSetInstance_ID (0);
setM_Inventory_ID (0);
setM_InventoryLine_ID (0);
setM_Locator_ID (0);	// @SQL=SELECT m_locator_id FROM m_locator where m_warehouse_id = @M_Warehouse_ID@ order by isdefault desc limit 1
setM_Product_ID (0);
setProcessed (false);
setQtyBook (Env.ZERO);
setQtyCount (Env.ZERO);
setQtyCountWithoutChargeSign (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_InventoryLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_InventoryLine");

/** TableName=M_InventoryLine */
public static final String Table_Name="M_InventoryLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_InventoryLine");
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
/** Set Cost.
Cost information */
public void setCost (BigDecimal Cost)
{
if (Cost == null) throw new IllegalArgumentException ("Cost is mandatory");
set_Value ("Cost", Cost);
}
/** Get Cost.
Cost information */
public BigDecimal getCost() 
{
BigDecimal bd = (BigDecimal)get_Value("Cost");
if (bd == null) return Env.ZERO;
return bd;
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
public static final int INVENTORYTYPE_AD_Reference_ID = MReference.getReferenceID("M_Inventory Type");
/** Inventory Difference = D */
public static final String INVENTORYTYPE_InventoryDifference = "D";
/** Charge Account = C */
public static final String INVENTORYTYPE_ChargeAccount = "C";
/** Overwrite Inventory = O */
public static final String INVENTORYTYPE_OverwriteInventory = "O";
/** Set Inventory Type.
Type of inventory difference */
public void setInventoryType (String InventoryType)
{
if (InventoryType.equals("D") || InventoryType.equals("C") || InventoryType.equals("O"));
 else throw new IllegalArgumentException ("InventoryType Invalid value - Reference = INVENTORYTYPE_AD_Reference_ID - D - C - O");
if (InventoryType == null) throw new IllegalArgumentException ("InventoryType is mandatory");
if (InventoryType.length() > 1)
{
log.warning("Length > 1 - truncated");
InventoryType = InventoryType.substring(0,1);
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
public static final int M_PRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("M_Product (stocked)");
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
/** Set Qty Count Without Charge Sign */
public void setQtyCountWithoutChargeSign (BigDecimal QtyCountWithoutChargeSign)
{
if (QtyCountWithoutChargeSign == null) throw new IllegalArgumentException ("QtyCountWithoutChargeSign is mandatory");
set_Value ("QtyCountWithoutChargeSign", QtyCountWithoutChargeSign);
}
/** Get Qty Count Without Charge Sign */
public BigDecimal getQtyCountWithoutChargeSign() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyCountWithoutChargeSign");
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

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO M_InventoryLine(AD_Client_ID,AD_Org_ID,C_Charge_ID,Cost,Created,CreatedBy,Description,InventoryType,IsActive,Line,M_AttributeSetInstance_ID,M_Inventory_ID,M_InventoryLine_ID,M_Locator_ID,M_Product_ID,Processed,QtyBook,QtyCount,QtyCountWithoutChargeSign,QtyInternalUse,Updated,UpdatedBy) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		 if (getAD_Client_ID() == 0) sql = sql.replaceFirst("AD_Client_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_Org_ID() == 0) sql = sql.replaceFirst("AD_Org_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Charge_ID() == 0) sql = sql.replaceFirst("C_Charge_ID,","").replaceFirst("\\?,", "");
 		 if (getCost() == null) sql = sql.replaceFirst("Cost,","").replaceFirst("\\?,", "");
 		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getCreatedBy() == 0) sql = sql.replaceFirst("CreatedBy,","").replaceFirst("\\?,", "");
 		 if (getDescription() == null) sql = sql.replaceFirst("Description,","").replaceFirst("\\?,", "");
 		 if (getInventoryType() == null) sql = sql.replaceFirst("InventoryType,","").replaceFirst("\\?,", "");
 		 if (getLine() == 0) sql = sql.replaceFirst("Line,","").replaceFirst("\\?,", "");
 		 if (getM_AttributeSetInstance_ID() == 0) sql = sql.replaceFirst("M_AttributeSetInstance_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Inventory_ID() == 0) sql = sql.replaceFirst("M_Inventory_ID,","").replaceFirst("\\?,", "");
 		 if (getM_InventoryLine_ID() == 0) sql = sql.replaceFirst("M_InventoryLine_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Locator_ID() == 0) sql = sql.replaceFirst("M_Locator_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Product_ID() == 0) sql = sql.replaceFirst("M_Product_ID,","").replaceFirst("\\?,", "");
 		 if (getQtyBook() == null) sql = sql.replaceFirst("QtyBook,","").replaceFirst("\\?,", "");
 		 if (getQtyCount() == null) sql = sql.replaceFirst("QtyCount,","").replaceFirst("\\?,", "");
 		 if (getQtyCountWithoutChargeSign() == null) sql = sql.replaceFirst("QtyCountWithoutChargeSign,","").replaceFirst("\\?,", "");
 		 if (getQtyInternalUse() == null) sql = sql.replaceFirst("QtyInternalUse,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getUpdatedBy() == 0) sql = sql.replaceFirst("UpdatedBy,","").replaceFirst("\\?,", "");
 
 		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 if (getAD_Client_ID() != 0) pstmt.setInt(col++, getAD_Client_ID());
		 if (getAD_Org_ID() != 0) pstmt.setInt(col++, getAD_Org_ID());
		 if (getC_Charge_ID() != 0) pstmt.setInt(col++, getC_Charge_ID());
		 if (getCost() != null) pstmt.setBigDecimal(col++, getCost());
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 if (getCreatedBy() != 0) pstmt.setInt(col++, getCreatedBy());
		 if (getDescription() != null) pstmt.setString(col++, getDescription());
		 if (getInventoryType() != null) pstmt.setString(col++, getInventoryType());
		 pstmt.setString(col++, isActive()?"Y":"N");
		 if (getLine() != 0) pstmt.setInt(col++, getLine());
		 if (getM_AttributeSetInstance_ID() != 0) pstmt.setInt(col++, getM_AttributeSetInstance_ID());
		 if (getM_Inventory_ID() != 0) pstmt.setInt(col++, getM_Inventory_ID());
		 if (getM_InventoryLine_ID() != 0) pstmt.setInt(col++, getM_InventoryLine_ID());
		 if (getM_Locator_ID() != 0) pstmt.setInt(col++, getM_Locator_ID());
		 if (getM_Product_ID() != 0) pstmt.setInt(col++, getM_Product_ID());
		 pstmt.setString(col++, isProcessed()?"Y":"N");
		 if (getQtyBook() != null) pstmt.setBigDecimal(col++, getQtyBook());
		 if (getQtyCount() != null) pstmt.setBigDecimal(col++, getQtyCount());
		 if (getQtyCountWithoutChargeSign() != null) pstmt.setBigDecimal(col++, getQtyCountWithoutChargeSign());
		 if (getQtyInternalUse() != null) pstmt.setBigDecimal(col++, getQtyInternalUse());
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
