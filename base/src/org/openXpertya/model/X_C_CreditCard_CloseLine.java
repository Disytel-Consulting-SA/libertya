/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CreditCard_CloseLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-06-08 12:50:50.364 */
public class X_C_CreditCard_CloseLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CreditCard_CloseLine (Properties ctx, int C_CreditCard_CloseLine_ID, String trxName)
{
super (ctx, C_CreditCard_CloseLine_ID, trxName);
/** if (C_CreditCard_CloseLine_ID == 0)
{
setC_Creditcard_Closeline_ID (0);
setC_Payment_ID (0);
setDateTrx (new Timestamp(System.currentTimeMillis()));
setDocumentNo (null);
setPayAmt (Env.ZERO);	// 0
}
 */
}
/** Load Constructor */
public X_C_CreditCard_CloseLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CreditCard_CloseLine");

/** TableName=C_CreditCard_CloseLine */
public static final String Table_Name="C_CreditCard_CloseLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CreditCard_CloseLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CreditCard_CloseLine[").append(getID()).append("]");
return sb.toString();
}
/** Set C_CreditCard_Close_ID */
public void setC_CreditCard_Close_ID (int C_CreditCard_Close_ID)
{
if (C_CreditCard_Close_ID <= 0) set_Value ("C_CreditCard_Close_ID", null);
 else 
set_Value ("C_CreditCard_Close_ID", new Integer(C_CreditCard_Close_ID));
}
/** Get C_CreditCard_Close_ID */
public int getC_CreditCard_Close_ID() 
{
Integer ii = (Integer)get_Value("C_CreditCard_Close_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Creditcard_Closeline_ID */
public void setC_Creditcard_Closeline_ID (int C_Creditcard_Closeline_ID)
{
set_ValueNoCheck ("C_Creditcard_Closeline_ID", new Integer(C_Creditcard_Closeline_ID));
}
/** Get C_Creditcard_Closeline_ID */
public int getC_Creditcard_Closeline_ID() 
{
Integer ii = (Integer)get_Value("C_Creditcard_Closeline_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Creditcard_Closeline_ID()));
}
/** Set Coupon Batch Number */
public void setCouponBatchNumber (String CouponBatchNumber)
{
if (CouponBatchNumber != null && CouponBatchNumber.length() > 30)
{
log.warning("Length > 30 - truncated");
CouponBatchNumber = CouponBatchNumber.substring(0,30);
}
set_Value ("CouponBatchNumber", CouponBatchNumber);
}
/** Get Coupon Batch Number */
public String getCouponBatchNumber() 
{
return (String)get_Value("CouponBatchNumber");
}
/** Set Coupon Number.
Credit Card Payment Coupon Number */
public void setCouponNumber (String CouponNumber)
{
if (CouponNumber != null && CouponNumber.length() > 30)
{
log.warning("Length > 30 - truncated");
CouponNumber = CouponNumber.substring(0,30);
}
set_Value ("CouponNumber", CouponNumber);
}
/** Get Coupon Number.
Credit Card Payment Coupon Number */
public String getCouponNumber() 
{
return (String)get_Value("CouponNumber");
}
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
set_Value ("C_Payment_ID", new Integer(C_Payment_ID));
}
/** Get Payment.
Payment identifier */
public int getC_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Payment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set POS Journal.
POS Journal */
public void setC_POSJournal_ID (int C_POSJournal_ID)
{
if (C_POSJournal_ID <= 0) set_Value ("C_POSJournal_ID", null);
 else 
set_Value ("C_POSJournal_ID", new Integer(C_POSJournal_ID));
}
/** Get POS Journal.
POS Journal */
public int getC_POSJournal_ID() 
{
Integer ii = (Integer)get_Value("C_POSJournal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Number.
Credit Card Number  */
public void setCreditCardNumber (String CreditCardNumber)
{
if (CreditCardNumber != null && CreditCardNumber.length() > 20)
{
log.warning("Length > 20 - truncated");
CreditCardNumber = CreditCardNumber.substring(0,20);
}
set_Value ("CreditCardNumber", CreditCardNumber);
}
/** Get Number.
Credit Card Number  */
public String getCreditCardNumber() 
{
return (String)get_Value("CreditCardNumber");
}
/** Set Transaction Date.
Transaction Date */
public void setDateTrx (Timestamp DateTrx)
{
if (DateTrx == null) throw new IllegalArgumentException ("DateTrx is mandatory");
set_Value ("DateTrx", DateTrx);
}
/** Get Transaction Date.
Transaction Date */
public Timestamp getDateTrx() 
{
return (Timestamp)get_Value("DateTrx");
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
/** Set Plan de Entidad Financiera.
Plan de Entidad Financiera */
public void setM_EntidadFinancieraPlan_ID (int M_EntidadFinancieraPlan_ID)
{
if (M_EntidadFinancieraPlan_ID <= 0) set_Value ("M_EntidadFinancieraPlan_ID", null);
 else 
set_Value ("M_EntidadFinancieraPlan_ID", new Integer(M_EntidadFinancieraPlan_ID));
}
/** Get Plan de Entidad Financiera.
Plan de Entidad Financiera */
public int getM_EntidadFinancieraPlan_ID() 
{
Integer ii = (Integer)get_Value("M_EntidadFinancieraPlan_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment amount.
Amount being paid */
public void setPayAmt (BigDecimal PayAmt)
{
if (PayAmt == null) throw new IllegalArgumentException ("PayAmt is mandatory");
set_Value ("PayAmt", PayAmt);
}
/** Get Payment amount.
Amount being paid */
public BigDecimal getPayAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("PayAmt");
if (bd == null) return Env.ZERO;
return bd;
}
}
