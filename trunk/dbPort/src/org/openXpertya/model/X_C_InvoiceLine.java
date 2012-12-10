/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_InvoiceLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-12-09 21:11:50.3 */
public class X_C_InvoiceLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_InvoiceLine (Properties ctx, int C_InvoiceLine_ID, String trxName)
{
super (ctx, C_InvoiceLine_ID, trxName);
/** if (C_InvoiceLine_ID == 0)
{
setC_Invoice_ID (0);
setC_InvoiceLine_ID (0);
setCostPrice (Env.ZERO);	// 0
setC_Tax_ID (0);
setDocumentDiscountAmt (Env.ZERO);
setIsDescription (false);	// N
setIsPrinted (true);	// Y
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM C_InvoiceLine WHERE C_Invoice_ID=@C_Invoice_ID@
setLineBonusAmt (Env.ZERO);
setLineDiscountAmt (Env.ZERO);
setLineNetAmount (Env.ZERO);
setLineNetAmt (Env.ZERO);
setM_AttributeSetInstance_ID (0);
setPriceActual (Env.ZERO);
setPriceEntered (Env.ZERO);
setPriceLimit (Env.ZERO);
setPriceList (Env.ZERO);
setProcessed (false);
setQtyEntered (Env.ZERO);	// 1
setQtyInvoiced (Env.ZERO);	// 1
}
 */
}
/** Load Constructor */
public X_C_InvoiceLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_InvoiceLine");

/** TableName=C_InvoiceLine */
public static final String Table_Name="C_InvoiceLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_InvoiceLine");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_InvoiceLine[").append(getID()).append("]");
return sb.toString();
}
public static final int A_ASSET_ID_AD_Reference_ID = MReference.getReferenceID("A_Asset");
/** Set Asset.
Asset used internally or by customers */
public void setA_Asset_ID (int A_Asset_ID)
{
if (A_Asset_ID <= 0) set_Value ("A_Asset_ID", null);
 else 
set_Value ("A_Asset_ID", new Integer(A_Asset_ID));
}
/** Get Asset.
Asset used internally or by customers */
public int getA_Asset_ID() 
{
Integer ii = (Integer)get_Value("A_Asset_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_BPARTNER_VENDOR_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner Vendors");
/** Set C_BPartner_Vendor_ID.
Vendor */
public void setC_BPartner_Vendor_ID (int C_BPartner_Vendor_ID)
{
if (C_BPartner_Vendor_ID <= 0) set_Value ("C_BPartner_Vendor_ID", null);
 else 
set_Value ("C_BPartner_Vendor_ID", new Integer(C_BPartner_Vendor_ID));
}
/** Get C_BPartner_Vendor_ID.
Vendor */
public int getC_BPartner_Vendor_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Vendor_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
set_ValueNoCheck ("C_Invoice_ID", new Integer(C_Invoice_ID));
}
/** Get Invoice.
Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Invoice_ID()));
}
/** Set Invoice Line.
Invoice Detail Line */
public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
{
set_ValueNoCheck ("C_InvoiceLine_ID", new Integer(C_InvoiceLine_ID));
}
/** Get Invoice Line.
Invoice Detail Line */
public int getC_InvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceLine_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Cost Price */
public void setCostPrice (BigDecimal CostPrice)
{
if (CostPrice == null) throw new IllegalArgumentException ("CostPrice is mandatory");
set_Value ("CostPrice", CostPrice);
}
/** Get Cost Price */
public BigDecimal getCostPrice() 
{
BigDecimal bd = (BigDecimal)get_Value("CostPrice");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Tax.
Tax identifier */
public void setC_Tax_ID (int C_Tax_ID)
{
set_Value ("C_Tax_ID", new Integer(C_Tax_ID));
}
/** Get Tax.
Tax identifier */
public int getC_Tax_ID() 
{
Integer ii = (Integer)get_Value("C_Tax_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
if (C_UOM_ID <= 0) set_ValueNoCheck ("C_UOM_ID", null);
 else 
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
/** Set Document Discount Amount.
Line Document Discount Amount */
public void setDocumentDiscountAmt (BigDecimal DocumentDiscountAmt)
{
if (DocumentDiscountAmt == null) throw new IllegalArgumentException ("DocumentDiscountAmt is mandatory");
set_Value ("DocumentDiscountAmt", DocumentDiscountAmt);
}
/** Get Document Discount Amount.
Line Document Discount Amount */
public BigDecimal getDocumentDiscountAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("DocumentDiscountAmt");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Printed.
Indicates if this document / line is printed */
public void setIsPrinted (boolean IsPrinted)
{
set_Value ("IsPrinted", new Boolean(IsPrinted));
}
/** Get Printed.
Indicates if this document / line is printed */
public boolean isPrinted() 
{
Object oo = get_Value("IsPrinted");
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
/** Set Line Bonus Amount.
Line Bonus Amount */
public void setLineBonusAmt (BigDecimal LineBonusAmt)
{
if (LineBonusAmt == null) throw new IllegalArgumentException ("LineBonusAmt is mandatory");
set_Value ("LineBonusAmt", LineBonusAmt);
}
/** Get Line Bonus Amount.
Line Bonus Amount */
public BigDecimal getLineBonusAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineBonusAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line Discount.
Line Discount Amount */
public void setLineDiscountAmt (BigDecimal LineDiscountAmt)
{
if (LineDiscountAmt == null) throw new IllegalArgumentException ("LineDiscountAmt is mandatory");
set_Value ("LineDiscountAmt", LineDiscountAmt);
}
/** Get Line Discount.
Line Discount Amount */
public BigDecimal getLineDiscountAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineDiscountAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line Net Amount */
public void setLineNetAmount (BigDecimal LineNetAmount)
{
if (LineNetAmount == null) throw new IllegalArgumentException ("LineNetAmount is mandatory");
set_ValueNoCheck ("LineNetAmount", LineNetAmount);
}
/** Get Line Net Amount */
public BigDecimal getLineNetAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("LineNetAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line Amount.
Line Extended Amount (Quantity * Actual Price) without Freight and Charges */
public void setLineNetAmt (BigDecimal LineNetAmt)
{
if (LineNetAmt == null) throw new IllegalArgumentException ("LineNetAmt is mandatory");
set_ValueNoCheck ("LineNetAmt", LineNetAmt);
}
/** Get Line Amount.
Line Extended Amount (Quantity * Actual Price) without Freight and Charges */
public BigDecimal getLineNetAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineNetAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line Total.
Total line amount incl. Tax */
public void setLineTotalAmt (BigDecimal LineTotalAmt)
{
set_Value ("LineTotalAmt", LineTotalAmt);
}
/** Get Line Total.
Total line amount incl. Tax */
public BigDecimal getLineTotalAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineTotalAmt");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Unit Price.
Actual Price  */
public void setPriceActual (BigDecimal PriceActual)
{
if (PriceActual == null) throw new IllegalArgumentException ("PriceActual is mandatory");
set_ValueNoCheck ("PriceActual", PriceActual);
}
/** Get Unit Price.
Actual Price  */
public BigDecimal getPriceActual() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceActual");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Price.
Price Entered - the price based on the selected/base UoM */
public void setPriceEntered (BigDecimal PriceEntered)
{
if (PriceEntered == null) throw new IllegalArgumentException ("PriceEntered is mandatory");
set_Value ("PriceEntered", PriceEntered);
}
/** Get Price.
Price Entered - the price based on the selected/base UoM */
public BigDecimal getPriceEntered() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceEntered");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Limit Price.
Lowest price for a product */
public void setPriceLimit (BigDecimal PriceLimit)
{
if (PriceLimit == null) throw new IllegalArgumentException ("PriceLimit is mandatory");
set_Value ("PriceLimit", PriceLimit);
}
/** Get Limit Price.
Lowest price for a product */
public BigDecimal getPriceLimit() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceLimit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set List Price.
List Price */
public void setPriceList (BigDecimal PriceList)
{
if (PriceList == null) throw new IllegalArgumentException ("PriceList is mandatory");
set_Value ("PriceList", PriceList);
}
/** Get List Price.
List Price */
public BigDecimal getPriceList() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceList");
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
/** Set Invoiced Quantity.
Invoiced Quantity */
public void setQtyInvoiced (BigDecimal QtyInvoiced)
{
if (QtyInvoiced == null) throw new IllegalArgumentException ("QtyInvoiced is mandatory");
set_Value ("QtyInvoiced", QtyInvoiced);
}
/** Get Invoiced Quantity.
Invoiced Quantity */
public BigDecimal getQtyInvoiced() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyInvoiced");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int REF_INVOICELINE_ID_AD_Reference_ID = MReference.getReferenceID("C_InvoiceLine");
/** Set Referenced Invoice Line */
public void setRef_InvoiceLine_ID (int Ref_InvoiceLine_ID)
{
if (Ref_InvoiceLine_ID <= 0) set_Value ("Ref_InvoiceLine_ID", null);
 else 
set_Value ("Ref_InvoiceLine_ID", new Integer(Ref_InvoiceLine_ID));
}
/** Get Referenced Invoice Line */
public int getRef_InvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("Ref_InvoiceLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Resource Assignment.
Resource Assignment */
public void setS_ResourceAssignment_ID (int S_ResourceAssignment_ID)
{
if (S_ResourceAssignment_ID <= 0) set_ValueNoCheck ("S_ResourceAssignment_ID", null);
 else 
set_ValueNoCheck ("S_ResourceAssignment_ID", new Integer(S_ResourceAssignment_ID));
}
/** Get Resource Assignment.
Resource Assignment */
public int getS_ResourceAssignment_ID() 
{
Integer ii = (Integer)get_Value("S_ResourceAssignment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax Amount.
Tax Amount for a document */
public void setTaxAmt (BigDecimal TaxAmt)
{
set_Value ("TaxAmt", TaxAmt);
}
/** Get Tax Amount.
Tax Amount for a document */
public BigDecimal getTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO C_InvoiceLine(A_Asset_ID,AD_Client_ID,AD_Org_ID,C_BPartner_Vendor_ID,C_Charge_ID,C_Invoice_ID,C_InvoiceLine_ID,C_OrderLine_ID,CostPrice,C_Project_ID,Created,CreatedBy,C_Tax_ID,C_UOM_ID,Description,DocumentDiscountAmt,IsActive,IsDescription,IsPrinted,Line,LineBonusAmt,LineDiscountAmt,LineNetAmount,LineNetAmt,LineTotalAmt,M_AttributeSetInstance_ID,M_InOutLine_ID,M_Product_ID,PriceActual,PriceEntered,PriceLimit,PriceList,Processed,QtyEntered,QtyInvoiced,Ref_InvoiceLine_ID,S_ResourceAssignment_ID,TaxAmt,Updated,UpdatedBy) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		 if (getA_Asset_ID() == 0) sql = sql.replaceFirst("A_Asset_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_Client_ID() == 0) sql = sql.replaceFirst("AD_Client_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_Org_ID() == 0) sql = sql.replaceFirst("AD_Org_ID,","").replaceFirst("\\?,", "");
 		 if (getC_BPartner_Vendor_ID() == 0) sql = sql.replaceFirst("C_BPartner_Vendor_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Charge_ID() == 0) sql = sql.replaceFirst("C_Charge_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Invoice_ID() == 0) sql = sql.replaceFirst("C_Invoice_ID,","").replaceFirst("\\?,", "");
 		 if (getC_InvoiceLine_ID() == 0) sql = sql.replaceFirst("C_InvoiceLine_ID,","").replaceFirst("\\?,", "");
 		 if (getC_OrderLine_ID() == 0) sql = sql.replaceFirst("C_OrderLine_ID,","").replaceFirst("\\?,", "");
 		 if (getCostPrice() == null) sql = sql.replaceFirst("CostPrice,","").replaceFirst("\\?,", "");
 		 if (getC_Project_ID() == 0) sql = sql.replaceFirst("C_Project_ID,","").replaceFirst("\\?,", "");
 		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getCreatedBy() == 0) sql = sql.replaceFirst("CreatedBy,","").replaceFirst("\\?,", "");
 		 if (getC_Tax_ID() == 0) sql = sql.replaceFirst("C_Tax_ID,","").replaceFirst("\\?,", "");
 		 if (getC_UOM_ID() == 0) sql = sql.replaceFirst("C_UOM_ID,","").replaceFirst("\\?,", "");
 		 if (getDescription() == null) sql = sql.replaceFirst("Description,","").replaceFirst("\\?,", "");
 		 if (getDocumentDiscountAmt() == null) sql = sql.replaceFirst("DocumentDiscountAmt,","").replaceFirst("\\?,", "");
 		 if (getLine() == 0) sql = sql.replaceFirst("Line,","").replaceFirst("\\?,", "");
 		 if (getLineBonusAmt() == null) sql = sql.replaceFirst("LineBonusAmt,","").replaceFirst("\\?,", "");
 		 if (getLineDiscountAmt() == null) sql = sql.replaceFirst("LineDiscountAmt,","").replaceFirst("\\?,", "");
 		 if (getLineNetAmount() == null) sql = sql.replaceFirst("LineNetAmount,","").replaceFirst("\\?,", "");
 		 if (getLineNetAmt() == null) sql = sql.replaceFirst("LineNetAmt,","").replaceFirst("\\?,", "");
 		 if (getLineTotalAmt() == null) sql = sql.replaceFirst("LineTotalAmt,","").replaceFirst("\\?,", "");
 		 if (getM_AttributeSetInstance_ID() == 0) sql = sql.replaceFirst("M_AttributeSetInstance_ID,","").replaceFirst("\\?,", "");
 		 if (getM_InOutLine_ID() == 0) sql = sql.replaceFirst("M_InOutLine_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Product_ID() == 0) sql = sql.replaceFirst("M_Product_ID,","").replaceFirst("\\?,", "");
 		 if (getPriceActual() == null) sql = sql.replaceFirst("PriceActual,","").replaceFirst("\\?,", "");
 		 if (getPriceEntered() == null) sql = sql.replaceFirst("PriceEntered,","").replaceFirst("\\?,", "");
 		 if (getPriceLimit() == null) sql = sql.replaceFirst("PriceLimit,","").replaceFirst("\\?,", "");
 		 if (getPriceList() == null) sql = sql.replaceFirst("PriceList,","").replaceFirst("\\?,", "");
 		 if (getQtyEntered() == null) sql = sql.replaceFirst("QtyEntered,","").replaceFirst("\\?,", "");
 		 if (getQtyInvoiced() == null) sql = sql.replaceFirst("QtyInvoiced,","").replaceFirst("\\?,", "");
 		 if (getRef_InvoiceLine_ID() == 0) sql = sql.replaceFirst("Ref_InvoiceLine_ID,","").replaceFirst("\\?,", "");
 		 if (getS_ResourceAssignment_ID() == 0) sql = sql.replaceFirst("S_ResourceAssignment_ID,","").replaceFirst("\\?,", "");
 		 if (getTaxAmt() == null) sql = sql.replaceFirst("TaxAmt,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getUpdatedBy() == 0) sql = sql.replaceFirst("UpdatedBy,","").replaceFirst("\\?,", "");
 
 		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 if (getA_Asset_ID() != 0) pstmt.setInt(col++, getA_Asset_ID());
		 if (getAD_Client_ID() != 0) pstmt.setInt(col++, getAD_Client_ID());
		 if (getAD_Org_ID() != 0) pstmt.setInt(col++, getAD_Org_ID());
		 if (getC_BPartner_Vendor_ID() != 0) pstmt.setInt(col++, getC_BPartner_Vendor_ID());
		 if (getC_Charge_ID() != 0) pstmt.setInt(col++, getC_Charge_ID());
		 if (getC_Invoice_ID() != 0) pstmt.setInt(col++, getC_Invoice_ID());
		 if (getC_InvoiceLine_ID() != 0) pstmt.setInt(col++, getC_InvoiceLine_ID());
		 if (getC_OrderLine_ID() != 0) pstmt.setInt(col++, getC_OrderLine_ID());
		 if (getCostPrice() != null) pstmt.setBigDecimal(col++, getCostPrice());
		 if (getC_Project_ID() != 0) pstmt.setInt(col++, getC_Project_ID());
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 if (getCreatedBy() != 0) pstmt.setInt(col++, getCreatedBy());
		 if (getC_Tax_ID() != 0) pstmt.setInt(col++, getC_Tax_ID());
		 if (getC_UOM_ID() != 0) pstmt.setInt(col++, getC_UOM_ID());
		 if (getDescription() != null) pstmt.setString(col++, getDescription());
		 if (getDocumentDiscountAmt() != null) pstmt.setBigDecimal(col++, getDocumentDiscountAmt());
		 pstmt.setString(col++, isActive()?"Y":"N");
		 pstmt.setString(col++, isDescription()?"Y":"N");
		 pstmt.setString(col++, isPrinted()?"Y":"N");
		 if (getLine() != 0) pstmt.setInt(col++, getLine());
		 if (getLineBonusAmt() != null) pstmt.setBigDecimal(col++, getLineBonusAmt());
		 if (getLineDiscountAmt() != null) pstmt.setBigDecimal(col++, getLineDiscountAmt());
		 if (getLineNetAmount() != null) pstmt.setBigDecimal(col++, getLineNetAmount());
		 if (getLineNetAmt() != null) pstmt.setBigDecimal(col++, getLineNetAmt());
		 if (getLineTotalAmt() != null) pstmt.setBigDecimal(col++, getLineTotalAmt());
		 if (getM_AttributeSetInstance_ID() != 0) pstmt.setInt(col++, getM_AttributeSetInstance_ID());
		 if (getM_InOutLine_ID() != 0) pstmt.setInt(col++, getM_InOutLine_ID());
		 if (getM_Product_ID() != 0) pstmt.setInt(col++, getM_Product_ID());
		 if (getPriceActual() != null) pstmt.setBigDecimal(col++, getPriceActual());
		 if (getPriceEntered() != null) pstmt.setBigDecimal(col++, getPriceEntered());
		 if (getPriceLimit() != null) pstmt.setBigDecimal(col++, getPriceLimit());
		 if (getPriceList() != null) pstmt.setBigDecimal(col++, getPriceList());
		 pstmt.setString(col++, isProcessed()?"Y":"N");
		 if (getQtyEntered() != null) pstmt.setBigDecimal(col++, getQtyEntered());
		 if (getQtyInvoiced() != null) pstmt.setBigDecimal(col++, getQtyInvoiced());
		 if (getRef_InvoiceLine_ID() != 0) pstmt.setInt(col++, getRef_InvoiceLine_ID());
		 if (getS_ResourceAssignment_ID() != 0) pstmt.setInt(col++, getS_ResourceAssignment_ID());
		 if (getTaxAmt() != null) pstmt.setBigDecimal(col++, getTaxAmt());
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
