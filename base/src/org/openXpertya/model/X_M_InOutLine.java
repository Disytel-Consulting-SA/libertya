/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_InOutLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-12-30 19:21:30.944 */
public class X_M_InOutLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_InOutLine (Properties ctx, int M_InOutLine_ID, String trxName)
{
super (ctx, M_InOutLine_ID, trxName);
/** if (M_InOutLine_ID == 0)
{
setC_UOM_ID (0);	// @#C_UOM_ID@
setIsDescription (false);	// N
setIsInvoiced (false);
setIsSelected (false);
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_InOutLine WHERE M_InOut_ID=@M_InOut_ID@
setM_AttributeSetInstance_ID (0);
setM_InOut_ID (0);
setM_InOutLine_ID (0);
setM_Locator_ID (0);	// @SQL=SELECT m_locator_id FROM m_locator where m_warehouse_id = @M_Warehouse_ID@ order by isdefault desc limit 1
setMovementQty (Env.ZERO);	// 1
setM_Product_ID (0);
setProcessed (false);
setQtyEntered (Env.ZERO);	// 1
}
 */
}
/** Load Constructor */
public X_M_InOutLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_InOutLine");

/** TableName=M_InOutLine */
public static final String Table_Name="M_InOutLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_InOutLine");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_InOutLine[").append(getID()).append("]");
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
/** Set Country.
Country  */
public void setC_Country_ID (int C_Country_ID)
{
if (C_Country_ID <= 0) set_Value ("C_Country_ID", null);
 else 
set_Value ("C_Country_ID", new Integer(C_Country_ID));
}
/** Get Country.
Country  */
public int getC_Country_ID() 
{
Integer ii = (Integer)get_Value("C_Country_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_INVOICELINE_ID_AD_Reference_ID = MReference.getReferenceID("C_InvoiceLine");
/** Set Invoice Line.
Invoice Detail Line */
public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
{
if (C_InvoiceLine_ID <= 0) set_Value ("C_InvoiceLine_ID", null);
 else 
set_Value ("C_InvoiceLine_ID", new Integer(C_InvoiceLine_ID));
}
/** Get Invoice Line.
Invoice Detail Line */
public int getC_InvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Confirmed Quantity.
Confirmation of a received quantity */
public void setConfirmedQty (BigDecimal ConfirmedQty)
{
set_Value ("ConfirmedQty", ConfirmedQty);
}
/** Get Confirmed Quantity.
Confirmation of a received quantity */
public BigDecimal getConfirmedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ConfirmedQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Sales Order Line.
Sales Order Line */
public void setC_OrderLine_ID (int C_OrderLine_ID)
{
if (C_OrderLine_ID <= 0) set_ValueNoCheck ("C_OrderLine_ID", null);
 else 
set_ValueNoCheck ("C_OrderLine_ID", new Integer(C_OrderLine_ID));
}
/** Get Sales Order Line.
Sales Order Line */
public int getC_OrderLine_ID() 
{
Integer ii = (Integer)get_Value("C_OrderLine_ID");
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
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
set_ValueNoCheck ("C_UOM_ID", new Integer(C_UOM_ID));
}
/** Get UOM.
Unit of Measure */
public int getC_UOM_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set declarationno */
public void setdeclarationno (String declarationno)
{
if (declarationno != null && declarationno.length() > 60)
{
log.warning("Length > 60 - truncated");
declarationno = declarationno.substring(0,60);
}
set_Value ("declarationno", declarationno);
}
/** Get declarationno */
public String getdeclarationno() 
{
return (String)get_Value("declarationno");
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
/** Set Invoiced.
Is this invoiced? */
public void setIsInvoiced (boolean IsInvoiced)
{
set_Value ("IsInvoiced", new Boolean(IsInvoiced));
}
/** Get Invoiced.
Is this invoiced? */
public boolean isInvoiced() 
{
Object oo = get_Value("IsInvoiced");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Selected */
public void setIsSelected (boolean IsSelected)
{
set_Value ("IsSelected", new Boolean(IsSelected));
}
/** Get Selected */
public boolean isSelected() 
{
Object oo = get_Value("IsSelected");
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
/** Set Shipment/Receipt.
Material Shipment Document */
public void setM_InOut_ID (int M_InOut_ID)
{
set_ValueNoCheck ("M_InOut_ID", new Integer(M_InOut_ID));
}
/** Get Shipment/Receipt.
Material Shipment Document */
public int getM_InOut_ID() 
{
Integer ii = (Integer)get_Value("M_InOut_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipment/Receipt Line.
Line on Shipment or Receipt document */
public void setM_InOutLine_ID (int M_InOutLine_ID)
{
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
/** Set Picked Quantity */
public void setPickedQty (BigDecimal PickedQty)
{
set_Value ("PickedQty", PickedQty);
}
/** Get Picked Quantity */
public BigDecimal getPickedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("PickedQty");
if (bd == null) return Env.ZERO;
return bd;
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
public static final int REF_INOUTLINE_ID_AD_Reference_ID = MReference.getReferenceID("M_InOutLine");
/** Set Referenced Shipment Line */
public void setRef_InOutLine_ID (int Ref_InOutLine_ID)
{
if (Ref_InOutLine_ID <= 0) set_Value ("Ref_InOutLine_ID", null);
 else 
set_Value ("Ref_InOutLine_ID", new Integer(Ref_InOutLine_ID));
}
/** Get Referenced Shipment Line */
public int getRef_InOutLine_ID() 
{
Integer ii = (Integer)get_Value("Ref_InOutLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Scrapped Quantity.
The Quantity scrapped due to QA issues */
public void setScrappedQty (BigDecimal ScrappedQty)
{
set_Value ("ScrappedQty", ScrappedQty);
}
/** Get Scrapped Quantity.
The Quantity scrapped due to QA issues */
public BigDecimal getScrappedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ScrappedQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Target Quantity.
Target Movement Quantity */
public void setTargetQty (BigDecimal TargetQty)
{
set_Value ("TargetQty", TargetQty);
}
/** Get Target Quantity.
Target Movement Quantity */
public BigDecimal getTargetQty() 
{
BigDecimal bd = (BigDecimal)get_Value("TargetQty");
if (bd == null) return Env.ZERO;
return bd;
}

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO M_InOutLine(AD_Client_ID,AD_Org_ID,C_Charge_ID,C_Country_ID,C_InvoiceLine_ID,ConfirmedQty,C_OrderLine_ID,C_Project_ID,Created,CreatedBy,C_UOM_ID,declarationno,Description,IsActive,IsDescription,IsInvoiced,IsSelected,Line,M_AttributeSetInstance_ID,M_InOut_ID,M_InOutLine_ID,M_Locator_ID,MovementQty,M_Product_ID,PickedQty,Processed,QtyEntered,Ref_InOutLine_ID,ScrappedQty,TargetQty,Updated,UpdatedBy) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		 if (getAD_Client_ID() == 0) sql = sql.replaceFirst("AD_Client_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_Org_ID() == 0) sql = sql.replaceFirst("AD_Org_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Charge_ID() == 0) sql = sql.replaceFirst("C_Charge_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Country_ID() == 0) sql = sql.replaceFirst("C_Country_ID,","").replaceFirst("\\?,", "");
 		 if (getC_InvoiceLine_ID() == 0) sql = sql.replaceFirst("C_InvoiceLine_ID,","").replaceFirst("\\?,", "");
 		 if (getConfirmedQty() == null) sql = sql.replaceFirst("ConfirmedQty,","").replaceFirst("\\?,", "");
 		 if (getC_OrderLine_ID() == 0) sql = sql.replaceFirst("C_OrderLine_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Project_ID() == 0) sql = sql.replaceFirst("C_Project_ID,","").replaceFirst("\\?,", "");
 		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getCreatedBy() == 0) sql = sql.replaceFirst("CreatedBy,","").replaceFirst("\\?,", "");
 		 if (getC_UOM_ID() == 0) sql = sql.replaceFirst("C_UOM_ID,","").replaceFirst("\\?,", "");
 		 if (getdeclarationno() == null) sql = sql.replaceFirst("declarationno,","").replaceFirst("\\?,", "");
 		 if (getDescription() == null) sql = sql.replaceFirst("Description,","").replaceFirst("\\?,", "");
 		 if (getLine() == 0) sql = sql.replaceFirst("Line,","").replaceFirst("\\?,", "");
 		 if (getM_AttributeSetInstance_ID() == 0) sql = sql.replaceFirst("M_AttributeSetInstance_ID,","").replaceFirst("\\?,", "");
 		 if (getM_InOut_ID() == 0) sql = sql.replaceFirst("M_InOut_ID,","").replaceFirst("\\?,", "");
 		 if (getM_InOutLine_ID() == 0) sql = sql.replaceFirst("M_InOutLine_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Locator_ID() == 0) sql = sql.replaceFirst("M_Locator_ID,","").replaceFirst("\\?,", "");
 		 if (getMovementQty() == null) sql = sql.replaceFirst("MovementQty,","").replaceFirst("\\?,", "");
 		 if (getM_Product_ID() == 0) sql = sql.replaceFirst("M_Product_ID,","").replaceFirst("\\?,", "");
 		 if (getPickedQty() == null) sql = sql.replaceFirst("PickedQty,","").replaceFirst("\\?,", "");
 		 if (getQtyEntered() == null) sql = sql.replaceFirst("QtyEntered,","").replaceFirst("\\?,", "");
 		 if (getRef_InOutLine_ID() == 0) sql = sql.replaceFirst("Ref_InOutLine_ID,","").replaceFirst("\\?,", "");
 		 if (getScrappedQty() == null) sql = sql.replaceFirst("ScrappedQty,","").replaceFirst("\\?,", "");
 		 if (getTargetQty() == null) sql = sql.replaceFirst("TargetQty,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getUpdatedBy() == 0) sql = sql.replaceFirst("UpdatedBy,","").replaceFirst("\\?,", "");
 
 		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 if (getAD_Client_ID() != 0) pstmt.setInt(col++, getAD_Client_ID());
		 if (getAD_Org_ID() != 0) pstmt.setInt(col++, getAD_Org_ID());
		 if (getC_Charge_ID() != 0) pstmt.setInt(col++, getC_Charge_ID());
		 if (getC_Country_ID() != 0) pstmt.setInt(col++, getC_Country_ID());
		 if (getC_InvoiceLine_ID() != 0) pstmt.setInt(col++, getC_InvoiceLine_ID());
		 if (getConfirmedQty() != null) pstmt.setBigDecimal(col++, getConfirmedQty());
		 if (getC_OrderLine_ID() != 0) pstmt.setInt(col++, getC_OrderLine_ID());
		 if (getC_Project_ID() != 0) pstmt.setInt(col++, getC_Project_ID());
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 if (getCreatedBy() != 0) pstmt.setInt(col++, getCreatedBy());
		 if (getC_UOM_ID() != 0) pstmt.setInt(col++, getC_UOM_ID());
		 if (getdeclarationno() != null) pstmt.setString(col++, getdeclarationno());
		 if (getDescription() != null) pstmt.setString(col++, getDescription());
		 pstmt.setString(col++, isActive()?"Y":"N");
		 pstmt.setString(col++, isDescription()?"Y":"N");
		 pstmt.setString(col++, isInvoiced()?"Y":"N");
		 pstmt.setString(col++, isSelected()?"Y":"N");
		 if (getLine() != 0) pstmt.setInt(col++, getLine());
		 if (getM_AttributeSetInstance_ID() != 0) pstmt.setInt(col++, getM_AttributeSetInstance_ID());
		 if (getM_InOut_ID() != 0) pstmt.setInt(col++, getM_InOut_ID());
		 if (getM_InOutLine_ID() != 0) pstmt.setInt(col++, getM_InOutLine_ID());
		 if (getM_Locator_ID() != 0) pstmt.setInt(col++, getM_Locator_ID());
		 if (getMovementQty() != null) pstmt.setBigDecimal(col++, getMovementQty());
		 if (getM_Product_ID() != 0) pstmt.setInt(col++, getM_Product_ID());
		 if (getPickedQty() != null) pstmt.setBigDecimal(col++, getPickedQty());
		 pstmt.setString(col++, isProcessed()?"Y":"N");
		 if (getQtyEntered() != null) pstmt.setBigDecimal(col++, getQtyEntered());
		 if (getRef_InOutLine_ID() != 0) pstmt.setInt(col++, getRef_InOutLine_ID());
		 if (getScrappedQty() != null) pstmt.setBigDecimal(col++, getScrappedQty());
		 if (getTargetQty() != null) pstmt.setBigDecimal(col++, getTargetQty());
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
