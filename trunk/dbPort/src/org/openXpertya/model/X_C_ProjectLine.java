/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_ProjectLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.031 */
public class X_C_ProjectLine extends PO
{
/** Constructor estándar */
public X_C_ProjectLine (Properties ctx, int C_ProjectLine_ID, String trxName)
{
super (ctx, C_ProjectLine_ID, trxName);
/** if (C_ProjectLine_ID == 0)
{
setC_ProjectLine_ID (0);
setC_Project_ID (0);
setInvoicedAmt (Env.ZERO);
setInvoicedQty (Env.ZERO);	// 0
setIsPrinted (true);	// Y
setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM C_ProjectLine WHERE C_Project_ID=@C_Project_ID@
setPlannedAmt (Env.ZERO);
setPlannedMarginAmt (Env.ZERO);
setPlannedPrice (Env.ZERO);
setPlannedQty (Env.ZERO);	// 1
setProcessed (false);	// N
}
 */
}
/** Load Constructor */
public X_C_ProjectLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=434 */
public static final int Table_ID=434;

/** TableName=C_ProjectLine */
public static final String Table_Name="C_ProjectLine";

protected static KeyNamePair Model = new KeyNamePair(434,"C_ProjectLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ProjectLine[").append(getID()).append("]");
return sb.toString();
}
public static final int C_ORDERPO_ID_AD_Reference_ID=290;
/** Set Purchase Order.
Purchase Order */
public void setC_OrderPO_ID (int C_OrderPO_ID)
{
if (C_OrderPO_ID <= 0) set_ValueNoCheck ("C_OrderPO_ID", null);
 else 
set_ValueNoCheck ("C_OrderPO_ID", new Integer(C_OrderPO_ID));
}
/** Get Purchase Order.
Purchase Order */
public int getC_OrderPO_ID() 
{
Integer ii = (Integer)get_Value("C_OrderPO_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
if (C_Order_ID <= 0) set_ValueNoCheck ("C_Order_ID", null);
 else 
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
/** Set Project Line.
Task or step in a project */
public void setC_ProjectLine_ID (int C_ProjectLine_ID)
{
set_ValueNoCheck ("C_ProjectLine_ID", new Integer(C_ProjectLine_ID));
}
/** Get Project Line.
Task or step in a project */
public int getC_ProjectLine_ID() 
{
Integer ii = (Integer)get_Value("C_ProjectLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
set_ValueNoCheck ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Committed Amount.
The (legal) commitment amount */
public void setCommittedAmt (BigDecimal CommittedAmt)
{
set_Value ("CommittedAmt", CommittedAmt);
}
/** Get Committed Amount.
The (legal) commitment amount */
public BigDecimal getCommittedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CommittedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Committed Quantity.
The (legal) commitment Quantity */
public void setCommittedQty (BigDecimal CommittedQty)
{
set_Value ("CommittedQty", CommittedQty);
}
/** Get Committed Quantity.
The (legal) commitment Quantity */
public BigDecimal getCommittedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("CommittedQty");
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
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Pricing */
public void setDoPricing (String DoPricing)
{
if (DoPricing != null && DoPricing.length() > 1)
{
log.warning("Length > 1 - truncated");
DoPricing = DoPricing.substring(0,0);
}
set_Value ("DoPricing", DoPricing);
}
/** Get Pricing */
public String getDoPricing() 
{
return (String)get_Value("DoPricing");
}
/** Set Invoiced Amount.
The amount invoiced */
public void setInvoicedAmt (BigDecimal InvoicedAmt)
{
if (InvoicedAmt == null) throw new IllegalArgumentException ("InvoicedAmt is mandatory");
set_Value ("InvoicedAmt", InvoicedAmt);
}
/** Get Invoiced Amount.
The amount invoiced */
public BigDecimal getInvoicedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("InvoicedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Invoiced Quantity.
The Quantity Invoiced */
public void setInvoicedQty (BigDecimal InvoicedQty)
{
if (InvoicedQty == null) throw new IllegalArgumentException ("InvoicedQty is mandatory");
set_Value ("InvoicedQty", InvoicedQty);
}
/** Get Invoiced Quantity.
The Quantity Invoiced */
public BigDecimal getInvoicedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("InvoicedQty");
if (bd == null) return Env.ZERO;
return bd;
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getLine()));
}
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
if (M_Product_Category_ID <= 0) set_Value ("M_Product_Category_ID", null);
 else 
set_Value ("M_Product_Category_ID", new Integer(M_Product_Category_ID));
}
/** Get Product Category.
Category of a Product */
public int getM_Product_Category_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Category_ID");
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
/** Set Planned Amount.
Planned amount for this project */
public void setPlannedAmt (BigDecimal PlannedAmt)
{
if (PlannedAmt == null) throw new IllegalArgumentException ("PlannedAmt is mandatory");
set_Value ("PlannedAmt", PlannedAmt);
}
/** Get Planned Amount.
Planned amount for this project */
public BigDecimal getPlannedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PlannedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Planned Margin.
Project's planned margin amount */
public void setPlannedMarginAmt (BigDecimal PlannedMarginAmt)
{
if (PlannedMarginAmt == null) throw new IllegalArgumentException ("PlannedMarginAmt is mandatory");
set_Value ("PlannedMarginAmt", PlannedMarginAmt);
}
/** Get Planned Margin.
Project's planned margin amount */
public BigDecimal getPlannedMarginAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PlannedMarginAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Planned Price.
Planned price for this project line */
public void setPlannedPrice (BigDecimal PlannedPrice)
{
if (PlannedPrice == null) throw new IllegalArgumentException ("PlannedPrice is mandatory");
set_Value ("PlannedPrice", PlannedPrice);
}
/** Get Planned Price.
Planned price for this project line */
public BigDecimal getPlannedPrice() 
{
BigDecimal bd = (BigDecimal)get_Value("PlannedPrice");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Planned Quantity.
Planned quantity for this project */
public void setPlannedQty (BigDecimal PlannedQty)
{
if (PlannedQty == null) throw new IllegalArgumentException ("PlannedQty is mandatory");
set_Value ("PlannedQty", PlannedQty);
}
/** Get Planned Quantity.
Planned quantity for this project */
public BigDecimal getPlannedQty() 
{
BigDecimal bd = (BigDecimal)get_Value("PlannedQty");
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
}
