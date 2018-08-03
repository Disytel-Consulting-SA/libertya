/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_OrderLine_Pending
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2018-08-03 00:28:05.584 */
public class X_T_OrderLine_Pending extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_OrderLine_Pending (Properties ctx, int T_OrderLine_Pending_ID, String trxName)
{
super (ctx, T_OrderLine_Pending_ID, trxName);
/** if (T_OrderLine_Pending_ID == 0)
{
setAD_PInstance_ID (0);
setT_OrderLine_Pending_ID (0);
}
 */
}
/** Load Constructor */
public X_T_OrderLine_Pending (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_OrderLine_Pending");

/** TableName=T_OrderLine_Pending */
public static final String Table_Name="T_OrderLine_Pending";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_OrderLine_Pending");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_OrderLine_Pending[").append(getID()).append("]");
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
/** Set Pending Deliver */
public void setPendingDeliver (BigDecimal PendingDeliver)
{
set_Value ("PendingDeliver", PendingDeliver);
}
/** Get Pending Deliver */
public BigDecimal getPendingDeliver() 
{
BigDecimal bd = (BigDecimal)get_Value("PendingDeliver");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Pending Invoice */
public void setPendingInvoice (BigDecimal PendingInvoice)
{
set_Value ("PendingInvoice", PendingInvoice);
}
/** Get Pending Invoice */
public BigDecimal getPendingInvoice() 
{
BigDecimal bd = (BigDecimal)get_Value("PendingInvoice");
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
/** Set Qty Returned */
public void setQtyReturned (BigDecimal QtyReturned)
{
set_Value ("QtyReturned", QtyReturned);
}
/** Get Qty Returned */
public BigDecimal getQtyReturned() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyReturned");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Qty Transferred */
public void setQtyTransferred (BigDecimal QtyTransferred)
{
set_Value ("QtyTransferred", QtyTransferred);
}
/** Get Qty Transferred */
public BigDecimal getQtyTransferred() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyTransferred");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int STATUS_AD_Reference_ID = MReference.getReferenceID("Tipo de Pendiente");
/** Deliver Pending = D */
public static final String STATUS_DeliverPending = "D";
/** Invoice Pending = I */
public static final String STATUS_InvoicePending = "I";
/** Invoice and Delivery Pending = ID */
public static final String STATUS_InvoiceAndDeliveryPending = "ID";
/** Set Status */
public void setStatus (String Status)
{
if (Status == null || Status.equals("D") || Status.equals("I") || Status.equals("ID") || ( refContainsValue("CORE-AD_Reference-1000091", Status) ) );
 else throw new IllegalArgumentException ("Status Invalid value: " + Status + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1000091") );
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
/** Set T_OrderLine_Pending_ID */
public void setT_OrderLine_Pending_ID (int T_OrderLine_Pending_ID)
{
set_ValueNoCheck ("T_OrderLine_Pending_ID", new Integer(T_OrderLine_Pending_ID));
}
/** Get T_OrderLine_Pending_ID */
public int getT_OrderLine_Pending_ID() 
{
Integer ii = (Integer)get_Value("T_OrderLine_Pending_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
