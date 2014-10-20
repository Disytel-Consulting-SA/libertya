/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por RV_Orderline_pending
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-10-20 10:31:49.604 */
public class X_RV_Orderline_pending extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_RV_Orderline_pending (Properties ctx, int RV_Orderline_pending_ID, String trxName)
{
super (ctx, RV_Orderline_pending_ID, trxName);
/** if (RV_Orderline_pending_ID == 0)
{
}
 */
}
/** Load Constructor */
public X_RV_Orderline_pending (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("RV_Orderline_pending");

/** TableName=RV_Orderline_pending */
public static final String Table_Name="RV_Orderline_pending";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"RV_Orderline_pending");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_RV_Orderline_pending[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
if (C_Order_ID <= 0) set_Value ("C_Order_ID", null);
 else 
set_Value ("C_Order_ID", new Integer(C_Order_ID));
}
/** Get Order.
Order */
public int getC_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sales Order Line.
Sales Order Line */
public void setC_OrderLine_ID (int C_OrderLine_ID)
{
if (C_OrderLine_ID <= 0) set_Value ("C_OrderLine_ID", null);
 else 
set_Value ("C_OrderLine_ID", new Integer(C_OrderLine_ID));
}
/** Get Sales Order Line.
Sales Order Line */
public int getC_OrderLine_ID() 
{
Integer ii = (Integer)get_Value("C_OrderLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set TIMESTAMP Ordered.
TIMESTAMP of Order */
public void setDateOrdered (Timestamp DateOrdered)
{
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
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,30);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set Sales Transaction.
This is a Sales Transaction */
public void setIsSOTrx (boolean IsSOTrx)
{
set_Value ("IsSOTrx", new Boolean(IsSOTrx));
}
/** Get Sales Transaction.
This is a Sales Transaction */
public boolean isSOTrx() 
{
Object oo = get_Value("IsSOTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set pendingdeliver */
public void setpendingdeliver (BigDecimal pendingdeliver)
{
set_Value ("pendingdeliver", pendingdeliver);
}
/** Get pendingdeliver */
public BigDecimal getpendingdeliver() 
{
BigDecimal bd = (BigDecimal)get_Value("pendingdeliver");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set pendinginvoice */
public void setpendinginvoice (BigDecimal pendinginvoice)
{
set_Value ("pendinginvoice", pendinginvoice);
}
/** Get pendinginvoice */
public BigDecimal getpendinginvoice() 
{
BigDecimal bd = (BigDecimal)get_Value("pendinginvoice");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Delivered Quantity.
Delivered Quantity */
public void setQtyDelivered (BigDecimal QtyDelivered)
{
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
/** Set Invoiced Quantity.
Invoiced Quantity */
public void setQtyInvoiced (BigDecimal QtyInvoiced)
{
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
/** Set Ordered Quantity.
Ordered Quantity */
public void setQtyOrdered (BigDecimal QtyOrdered)
{
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
/** Set Status */
public void setStatus (String Status)
{
if (Status != null && Status.length() > 1)
{
log.warning("Length > 1 - truncated");
Status = Status.substring(0,1);
}
set_Value ("Status", Status);
}
/** Get Status */
public String getStatus() 
{
return (String)get_Value("Status");
}
}
