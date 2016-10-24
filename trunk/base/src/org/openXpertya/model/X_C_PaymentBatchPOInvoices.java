/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PaymentBatchPOInvoices
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-24 12:48:12.71 */
public class X_C_PaymentBatchPOInvoices extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_PaymentBatchPOInvoices (Properties ctx, int C_PaymentBatchPOInvoices_ID, String trxName)
{
super (ctx, C_PaymentBatchPOInvoices_ID, trxName);
/** if (C_PaymentBatchPOInvoices_ID == 0)
{
setC_Invoice_ID (0);
setC_InvoicePaySchedule_ID (0);
setC_PaymentBatchpoDetail_ID (0);
setC_PaymentBatchPOInvoices_ID (0);
setDateInvoiced (new Timestamp(System.currentTimeMillis()));
setDocumentNo (null);
setDueDate (new Timestamp(System.currentTimeMillis()));
setInvoiceAmount (Env.ZERO);
setOpenAmount (Env.ZERO);
setPaymentAmount (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_PaymentBatchPOInvoices (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_PaymentBatchPOInvoices");

/** TableName=C_PaymentBatchPOInvoices */
public static final String Table_Name="C_PaymentBatchPOInvoices";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_PaymentBatchPOInvoices");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PaymentBatchPOInvoices[").append(getID()).append("]");
return sb.toString();
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
/** Set Invoice Payment Schedule.
Invoice Payment Schedule */
public void setC_InvoicePaySchedule_ID (int C_InvoicePaySchedule_ID)
{
set_Value ("C_InvoicePaySchedule_ID", new Integer(C_InvoicePaySchedule_ID));
}
/** Get Invoice Payment Schedule.
Invoice Payment Schedule */
public int getC_InvoicePaySchedule_ID() 
{
Integer ii = (Integer)get_Value("C_InvoicePaySchedule_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_PAYMENTBATCHPODETAIL_ID_AD_Reference_ID = MReference.getReferenceID("C_PaymentBatchPODetail");
/** Set C_PaymentBatchpoDetail_ID */
public void setC_PaymentBatchpoDetail_ID (int C_PaymentBatchpoDetail_ID)
{
set_Value ("C_PaymentBatchpoDetail_ID", new Integer(C_PaymentBatchpoDetail_ID));
}
/** Get C_PaymentBatchpoDetail_ID */
public int getC_PaymentBatchpoDetail_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentBatchpoDetail_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_PaymentBatchPOInvoices_ID */
public void setC_PaymentBatchPOInvoices_ID (int C_PaymentBatchPOInvoices_ID)
{
set_ValueNoCheck ("C_PaymentBatchPOInvoices_ID", new Integer(C_PaymentBatchPOInvoices_ID));
}
/** Get C_PaymentBatchPOInvoices_ID */
public int getC_PaymentBatchPOInvoices_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentBatchPOInvoices_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Date Invoiced.
Date printed on Invoice */
public void setDateInvoiced (Timestamp DateInvoiced)
{
if (DateInvoiced == null) throw new IllegalArgumentException ("DateInvoiced is mandatory");
set_Value ("DateInvoiced", DateInvoiced);
}
/** Get Date Invoiced.
Date printed on Invoice */
public Timestamp getDateInvoiced() 
{
return (Timestamp)get_Value("DateInvoiced");
}
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo == null) throw new IllegalArgumentException ("DocumentNo is mandatory");
if (DocumentNo.length() > 30)
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
/** Set Due Date.
Date when the payment is due */
public void setDueDate (Timestamp DueDate)
{
if (DueDate == null) throw new IllegalArgumentException ("DueDate is mandatory");
set_Value ("DueDate", DueDate);
}
/** Get Due Date.
Date when the payment is due */
public Timestamp getDueDate() 
{
return (Timestamp)get_Value("DueDate");
}
/** Set InvoiceAmount */
public void setInvoiceAmount (BigDecimal InvoiceAmount)
{
if (InvoiceAmount == null) throw new IllegalArgumentException ("InvoiceAmount is mandatory");
set_Value ("InvoiceAmount", InvoiceAmount);
}
/** Get InvoiceAmount */
public BigDecimal getInvoiceAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("InvoiceAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set OpenAmount */
public void setOpenAmount (BigDecimal OpenAmount)
{
if (OpenAmount == null) throw new IllegalArgumentException ("OpenAmount is mandatory");
set_Value ("OpenAmount", OpenAmount);
}
/** Get OpenAmount */
public BigDecimal getOpenAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("OpenAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PaymentAmount */
public void setPaymentAmount (BigDecimal PaymentAmount)
{
if (PaymentAmount == null) throw new IllegalArgumentException ("PaymentAmount is mandatory");
set_Value ("PaymentAmount", PaymentAmount);
}
/** Get PaymentAmount */
public BigDecimal getPaymentAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("PaymentAmount");
if (bd == null) return Env.ZERO;
return bd;
}
}
