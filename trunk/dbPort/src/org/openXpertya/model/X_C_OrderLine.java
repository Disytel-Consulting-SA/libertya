/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_OrderLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-09-09 10:51:28.652 */
public class X_C_OrderLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_OrderLine (Properties ctx, int C_OrderLine_ID, String trxName)
{
super (ctx, C_OrderLine_ID, trxName);
/** if (C_OrderLine_ID == 0)
{
setC_BPartner_Location_ID (0);	// @C_BPartner_Location_ID@
setC_Currency_ID (0);	// @C_Currency_ID@
setC_Order_ID (0);
setC_OrderLine_ID (0);
setC_Tax_ID (0);
setC_UOM_ID (0);	// @#C_UOM_ID@
setDateOrdered (new Timestamp(System.currentTimeMillis()));	// @DateOrdered@
setDocumentDiscountAmt (Env.ZERO);
setFreightAmt (Env.ZERO);
setIsDescription (false);	// N
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM C_OrderLine WHERE C_Order_ID=@C_Order_ID@
setLineBonusAmt (Env.ZERO);
setLineDiscountAmt (Env.ZERO);
setLineNetAmt (Env.ZERO);
setLineTotalAmt (Env.ZERO);
setM_AttributeSetInstance_ID (0);
setM_Warehouse_ID (0);	// @M_Warehouse_ID@
setOpenMatrix (null);
setPriceActual (Env.ZERO);
setPriceEntered (Env.ZERO);
setPriceLimit (Env.ZERO);
setPriceList (Env.ZERO);
setProcessed (false);
setQtyDelivered (Env.ZERO);
setQtyEntered (Env.ZERO);	// 1
setQtyInvoiced (Env.ZERO);
setQtyOrdered (Env.ZERO);	// 1
setQtyReserved (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_OrderLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_OrderLine");

/** TableName=C_OrderLine */
public static final String Table_Name="C_OrderLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_OrderLine");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_OrderLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_ValueNoCheck ("C_BPartner_ID", null);
 else 
set_ValueNoCheck ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Partner Location.
Identifies the (ship to) address for this Business Partner */
public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
{
set_Value ("C_BPartner_Location_ID", new Integer(C_BPartner_Location_ID));
}
/** Get Partner Location.
Identifies the (ship to) address for this Business Partner */
public int getC_BPartner_Location_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Location_ID");
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
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
set_ValueNoCheck ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
set_ValueNoCheck ("C_Order_ID", new Integer(C_Order_ID));
}
/** Get Order.
Order */
public int getC_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Order_ID()));
}
/** Set Sales Order Line.
Sales Order Line */
public void setC_OrderLine_ID (int C_OrderLine_ID)
{
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
/** Set Date Delivered.
Date when the product was delivered */
public void setDateDelivered (Timestamp DateDelivered)
{
set_ValueNoCheck ("DateDelivered", DateDelivered);
}
/** Get Date Delivered.
Date when the product was delivered */
public Timestamp getDateDelivered() 
{
return (Timestamp)get_Value("DateDelivered");
}
/** Set Date Invoiced.
Date printed on Invoice */
public void setDateInvoiced (Timestamp DateInvoiced)
{
set_ValueNoCheck ("DateInvoiced", DateInvoiced);
}
/** Get Date Invoiced.
Date printed on Invoice */
public Timestamp getDateInvoiced() 
{
return (Timestamp)get_Value("DateInvoiced");
}
/** Set Date Ordered.
Date of Order */
public void setDateOrdered (Timestamp DateOrdered)
{
if (DateOrdered == null) throw new IllegalArgumentException ("DateOrdered is mandatory");
set_Value ("DateOrdered", DateOrdered);
}
/** Get Date Ordered.
Date of Order */
public Timestamp getDateOrdered() 
{
return (Timestamp)get_Value("DateOrdered");
}
/** Set Date Promised.
Date Order was promised */
public void setDatePromised (Timestamp DatePromised)
{
set_Value ("DatePromised", DatePromised);
}
/** Get Date Promised.
Date Order was promised */
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
/** Set Discount %.
Discount in percent */
public void setDiscount (BigDecimal Discount)
{
set_Value ("Discount", Discount);
}
/** Get Discount %.
Discount in percent */
public BigDecimal getDiscount() 
{
BigDecimal bd = (BigDecimal)get_Value("Discount");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Freight Amount.
Freight Amount  */
public void setFreightAmt (BigDecimal FreightAmt)
{
if (FreightAmt == null) throw new IllegalArgumentException ("FreightAmt is mandatory");
set_Value ("FreightAmt", FreightAmt);
}
/** Get Freight Amount.
Freight Amount  */
public BigDecimal getFreightAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FreightAmt");
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
if (LineTotalAmt == null) throw new IllegalArgumentException ("LineTotalAmt is mandatory");
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
public static final int M_WAREHOUSE_ID_AD_Reference_ID = MReference.getReferenceID("M_Warehouse of Client");
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
/** Set Open attributes matrix */
public void setOpenMatrix (String OpenMatrix)
{
if (OpenMatrix == null) throw new IllegalArgumentException ("OpenMatrix is mandatory");
if (OpenMatrix.length() > 1)
{
log.warning("Length > 1 - truncated");
OpenMatrix = OpenMatrix.substring(0,1);
}
set_Value ("OpenMatrix", OpenMatrix);
}
/** Get Open attributes matrix */
public String getOpenMatrix() 
{
return (String)get_Value("OpenMatrix");
}
/** Set Unit Price.
Actual Price  */
public void setPriceActual (BigDecimal PriceActual)
{
if (PriceActual == null) throw new IllegalArgumentException ("PriceActual is mandatory");
set_Value ("PriceActual", PriceActual);
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
/** Set Delivered Quantity.
Delivered Quantity */
public void setQtyDelivered (BigDecimal QtyDelivered)
{
if (QtyDelivered == null) throw new IllegalArgumentException ("QtyDelivered is mandatory");
set_ValueNoCheck ("QtyDelivered", QtyDelivered);
}
/** Get Delivered Quantity.
Delivered Quantity */
public BigDecimal getQtyDelivered() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyDelivered");
if (bd == null) return Env.ZERO;
return bd;
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
set_ValueNoCheck ("QtyInvoiced", QtyInvoiced);
}
/** Get Invoiced Quantity.
Invoiced Quantity */
public BigDecimal getQtyInvoiced() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyInvoiced");
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
set_ValueNoCheck ("QtyReserved", QtyReserved);
}
/** Get Reserved Quantity.
Reserved Quantity */
public BigDecimal getQtyReserved() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyReserved");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int REF_ORDERLINE_ID_AD_Reference_ID = MReference.getReferenceID("C_OrderLine");
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

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO C_OrderLine(AD_Client_ID,AD_Org_ID,C_BPartner_ID,C_BPartner_Location_ID,C_Charge_ID,C_Currency_ID,C_Order_ID,C_OrderLine_ID,C_Project_ID,Created,CreatedBy,C_Tax_ID,C_UOM_ID,DateDelivered,DateInvoiced,DateOrdered,DatePromised,Description,Discount,DocumentDiscountAmt,FreightAmt,IsActive,IsDescription,Line,LineBonusAmt,LineDiscountAmt,LineNetAmt,LineTotalAmt,M_AttributeSetInstance_ID,M_Product_ID,M_Shipper_ID,M_Warehouse_ID,OpenMatrix,PriceActual,PriceEntered,PriceLimit,PriceList,Processed,QtyDelivered,QtyEntered,QtyInvoiced,QtyOrdered,QtyReserved,Ref_OrderLine_ID,S_ResourceAssignment_ID,Updated,UpdatedBy) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		 if (getAD_Client_ID() == 0) sql = sql.replaceFirst("AD_Client_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_Org_ID() == 0) sql = sql.replaceFirst("AD_Org_ID,","").replaceFirst("\\?,", "");
 		 if (getC_BPartner_ID() == 0) sql = sql.replaceFirst("C_BPartner_ID,","").replaceFirst("\\?,", "");
 		 if (getC_BPartner_Location_ID() == 0) sql = sql.replaceFirst("C_BPartner_Location_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Charge_ID() == 0) sql = sql.replaceFirst("C_Charge_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Currency_ID() == 0) sql = sql.replaceFirst("C_Currency_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Order_ID() == 0) sql = sql.replaceFirst("C_Order_ID,","").replaceFirst("\\?,", "");
 		 if (getC_OrderLine_ID() == 0) sql = sql.replaceFirst("C_OrderLine_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Project_ID() == 0) sql = sql.replaceFirst("C_Project_ID,","").replaceFirst("\\?,", "");
 		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getCreatedBy() == 0) sql = sql.replaceFirst("CreatedBy,","").replaceFirst("\\?,", "");
 		 if (getC_Tax_ID() == 0) sql = sql.replaceFirst("C_Tax_ID,","").replaceFirst("\\?,", "");
 		 if (getC_UOM_ID() == 0) sql = sql.replaceFirst("C_UOM_ID,","").replaceFirst("\\?,", "");
 		 if (getDateDelivered() == null) sql = sql.replaceFirst("DateDelivered,","").replaceFirst("\\?,", "");
 		 if (getDateInvoiced() == null) sql = sql.replaceFirst("DateInvoiced,","").replaceFirst("\\?,", "");
 		 if (getDateOrdered() == null) sql = sql.replaceFirst("DateOrdered,","").replaceFirst("\\?,", "");
 		 if (getDatePromised() == null) sql = sql.replaceFirst("DatePromised,","").replaceFirst("\\?,", "");
 		 if (getDescription() == null) sql = sql.replaceFirst("Description,","").replaceFirst("\\?,", "");
 		 if (getDiscount() == null) sql = sql.replaceFirst("Discount,","").replaceFirst("\\?,", "");
 		 if (getDocumentDiscountAmt() == null) sql = sql.replaceFirst("DocumentDiscountAmt,","").replaceFirst("\\?,", "");
 		 if (getFreightAmt() == null) sql = sql.replaceFirst("FreightAmt,","").replaceFirst("\\?,", "");
 		 if (getLine() == 0) sql = sql.replaceFirst("Line,","").replaceFirst("\\?,", "");
 		 if (getLineBonusAmt() == null) sql = sql.replaceFirst("LineBonusAmt,","").replaceFirst("\\?,", "");
 		 if (getLineDiscountAmt() == null) sql = sql.replaceFirst("LineDiscountAmt,","").replaceFirst("\\?,", "");
 		 if (getLineNetAmt() == null) sql = sql.replaceFirst("LineNetAmt,","").replaceFirst("\\?,", "");
 		 if (getLineTotalAmt() == null) sql = sql.replaceFirst("LineTotalAmt,","").replaceFirst("\\?,", "");
 		 if (getM_AttributeSetInstance_ID() == 0) sql = sql.replaceFirst("M_AttributeSetInstance_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Product_ID() == 0) sql = sql.replaceFirst("M_Product_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Shipper_ID() == 0) sql = sql.replaceFirst("M_Shipper_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Warehouse_ID() == 0) sql = sql.replaceFirst("M_Warehouse_ID,","").replaceFirst("\\?,", "");
 		 if (getOpenMatrix() == null) sql = sql.replaceFirst("OpenMatrix,","").replaceFirst("\\?,", "");
 		 if (getPriceActual() == null) sql = sql.replaceFirst("PriceActual,","").replaceFirst("\\?,", "");
 		 if (getPriceEntered() == null) sql = sql.replaceFirst("PriceEntered,","").replaceFirst("\\?,", "");
 		 if (getPriceLimit() == null) sql = sql.replaceFirst("PriceLimit,","").replaceFirst("\\?,", "");
 		 if (getPriceList() == null) sql = sql.replaceFirst("PriceList,","").replaceFirst("\\?,", "");
 		 if (getQtyDelivered() == null) sql = sql.replaceFirst("QtyDelivered,","").replaceFirst("\\?,", "");
 		 if (getQtyEntered() == null) sql = sql.replaceFirst("QtyEntered,","").replaceFirst("\\?,", "");
 		 if (getQtyInvoiced() == null) sql = sql.replaceFirst("QtyInvoiced,","").replaceFirst("\\?,", "");
 		 if (getQtyOrdered() == null) sql = sql.replaceFirst("QtyOrdered,","").replaceFirst("\\?,", "");
 		 if (getQtyReserved() == null) sql = sql.replaceFirst("QtyReserved,","").replaceFirst("\\?,", "");
 		 if (getRef_OrderLine_ID() == 0) sql = sql.replaceFirst("Ref_OrderLine_ID,","").replaceFirst("\\?,", "");
 		 if (getS_ResourceAssignment_ID() == 0) sql = sql.replaceFirst("S_ResourceAssignment_ID,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getUpdatedBy() == 0) sql = sql.replaceFirst("UpdatedBy,","").replaceFirst("\\?,", "");
 
 		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 if (getAD_Client_ID() != 0) pstmt.setInt(col++, getAD_Client_ID());
		 if (getAD_Org_ID() != 0) pstmt.setInt(col++, getAD_Org_ID());
		 if (getC_BPartner_ID() != 0) pstmt.setInt(col++, getC_BPartner_ID());
		 if (getC_BPartner_Location_ID() != 0) pstmt.setInt(col++, getC_BPartner_Location_ID());
		 if (getC_Charge_ID() != 0) pstmt.setInt(col++, getC_Charge_ID());
		 if (getC_Currency_ID() != 0) pstmt.setInt(col++, getC_Currency_ID());
		 if (getC_Order_ID() != 0) pstmt.setInt(col++, getC_Order_ID());
		 if (getC_OrderLine_ID() != 0) pstmt.setInt(col++, getC_OrderLine_ID());
		 if (getC_Project_ID() != 0) pstmt.setInt(col++, getC_Project_ID());
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 if (getCreatedBy() != 0) pstmt.setInt(col++, getCreatedBy());
		 if (getC_Tax_ID() != 0) pstmt.setInt(col++, getC_Tax_ID());
		 if (getC_UOM_ID() != 0) pstmt.setInt(col++, getC_UOM_ID());
		 if (getDateDelivered() != null) pstmt.setTimestamp(col++, getDateDelivered());
		 if (getDateInvoiced() != null) pstmt.setTimestamp(col++, getDateInvoiced());
		 if (getDateOrdered() != null) pstmt.setTimestamp(col++, getDateOrdered());
		 if (getDatePromised() != null) pstmt.setTimestamp(col++, getDatePromised());
		 if (getDescription() != null) pstmt.setString(col++, getDescription());
		 if (getDiscount() != null) pstmt.setBigDecimal(col++, getDiscount());
		 if (getDocumentDiscountAmt() != null) pstmt.setBigDecimal(col++, getDocumentDiscountAmt());
		 if (getFreightAmt() != null) pstmt.setBigDecimal(col++, getFreightAmt());
		 pstmt.setString(col++, isActive()?"Y":"N");
		 pstmt.setString(col++, isDescription()?"Y":"N");
		 if (getLine() != 0) pstmt.setInt(col++, getLine());
		 if (getLineBonusAmt() != null) pstmt.setBigDecimal(col++, getLineBonusAmt());
		 if (getLineDiscountAmt() != null) pstmt.setBigDecimal(col++, getLineDiscountAmt());
		 if (getLineNetAmt() != null) pstmt.setBigDecimal(col++, getLineNetAmt());
		 if (getLineTotalAmt() != null) pstmt.setBigDecimal(col++, getLineTotalAmt());
		 if (getM_AttributeSetInstance_ID() != 0) pstmt.setInt(col++, getM_AttributeSetInstance_ID());
		 if (getM_Product_ID() != 0) pstmt.setInt(col++, getM_Product_ID());
		 if (getM_Shipper_ID() != 0) pstmt.setInt(col++, getM_Shipper_ID());
		 if (getM_Warehouse_ID() != 0) pstmt.setInt(col++, getM_Warehouse_ID());
		 if (getOpenMatrix() != null) pstmt.setString(col++, getOpenMatrix());
		 if (getPriceActual() != null) pstmt.setBigDecimal(col++, getPriceActual());
		 if (getPriceEntered() != null) pstmt.setBigDecimal(col++, getPriceEntered());
		 if (getPriceLimit() != null) pstmt.setBigDecimal(col++, getPriceLimit());
		 if (getPriceList() != null) pstmt.setBigDecimal(col++, getPriceList());
		 pstmt.setString(col++, isProcessed()?"Y":"N");
		 if (getQtyDelivered() != null) pstmt.setBigDecimal(col++, getQtyDelivered());
		 if (getQtyEntered() != null) pstmt.setBigDecimal(col++, getQtyEntered());
		 if (getQtyInvoiced() != null) pstmt.setBigDecimal(col++, getQtyInvoiced());
		 if (getQtyOrdered() != null) pstmt.setBigDecimal(col++, getQtyOrdered());
		 if (getQtyReserved() != null) pstmt.setBigDecimal(col++, getQtyReserved());
		 if (getRef_OrderLine_ID() != 0) pstmt.setInt(col++, getRef_OrderLine_ID());
		 if (getS_ResourceAssignment_ID() != 0) pstmt.setInt(col++, getS_ResourceAssignment_ID());
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
