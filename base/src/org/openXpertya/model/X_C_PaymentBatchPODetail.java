/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PaymentBatchPODetail
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-24 12:48:12.399 */
public class X_C_PaymentBatchPODetail extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_PaymentBatchPODetail (Properties ctx, int C_PaymentBatchPODetail_ID, String trxName)
{
super (ctx, C_PaymentBatchPODetail_ID, trxName);
/** if (C_PaymentBatchPODetail_ID == 0)
{
setC_BPartner_ID (0);
setC_PaymentBatchpoDetail_ID (0);
setC_PaymentBatchPO_ID (0);
setPaymentDate (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_C_PaymentBatchPODetail (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_PaymentBatchPODetail");

/** TableName=C_PaymentBatchPODetail */
public static final String Table_Name="C_PaymentBatchPODetail";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_PaymentBatchPODetail");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PaymentBatchPODetail[").append(getID()).append("]");
return sb.toString();
}
public static final int BATCH_PAYMENT_RULE_AD_Reference_ID = MReference.getReferenceID("Batch Payment Rules");
/** Check = C */
public static final String BATCH_PAYMENT_RULE_Check = "C";
/** Electronic Check = E */
public static final String BATCH_PAYMENT_RULE_ElectronicCheck = "E";
/** Set Batch Payment Rule */
public void setBatch_Payment_Rule (String Batch_Payment_Rule)
{
if (Batch_Payment_Rule == null || Batch_Payment_Rule.equals("C") || Batch_Payment_Rule.equals("E"));
 else throw new IllegalArgumentException ("Batch_Payment_Rule Invalid value - Reference = BATCH_PAYMENT_RULE_AD_Reference_ID - C - E");
if (Batch_Payment_Rule != null && Batch_Payment_Rule.length() > 1)
{
log.warning("Length > 1 - truncated");
Batch_Payment_Rule = Batch_Payment_Rule.substring(0,1);
}
set_Value ("Batch_Payment_Rule", Batch_Payment_Rule);
}
/** Get Batch Payment Rule */
public String getBatch_Payment_Rule() 
{
return (String)get_Value("Batch_Payment_Rule");
}
/** Set Allocation.
Payment allocation */
public void setC_AllocationHdr_ID (int C_AllocationHdr_ID)
{
if (C_AllocationHdr_ID <= 0) set_Value ("C_AllocationHdr_ID", null);
 else 
set_Value ("C_AllocationHdr_ID", new Integer(C_AllocationHdr_ID));
}
/** Get Allocation.
Payment allocation */
public int getC_AllocationHdr_ID() 
{
Integer ii = (Integer)get_Value("C_AllocationHdr_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Account.
Account at the Bank */
public void setC_BankAccount_ID (int C_BankAccount_ID)
{
if (C_BankAccount_ID <= 0) set_Value ("C_BankAccount_ID", null);
 else 
set_Value ("C_BankAccount_ID", new Integer(C_BankAccount_ID));
}
/** Get Bank Account.
Account at the Bank */
public int getC_BankAccount_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccount_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank.
Bank */
public void setC_Bank_ID (int C_Bank_ID)
{
if (C_Bank_ID <= 0) set_Value ("C_Bank_ID", null);
 else 
set_Value ("C_Bank_ID", new Integer(C_Bank_ID));
}
/** Get Bank.
Bank */
public int getC_Bank_ID() 
{
Integer ii = (Integer)get_Value("C_Bank_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_BPartner_ID()));
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
/** Set C_PaymentBatchpoDetail_ID */
public void setC_PaymentBatchpoDetail_ID (int C_PaymentBatchpoDetail_ID)
{
set_ValueNoCheck ("C_PaymentBatchpoDetail_ID", new Integer(C_PaymentBatchpoDetail_ID));
}
/** Get C_PaymentBatchpoDetail_ID */
public int getC_PaymentBatchpoDetail_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentBatchpoDetail_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_PaymentBatchPO_ID */
public void setC_PaymentBatchPO_ID (int C_PaymentBatchPO_ID)
{
set_Value ("C_PaymentBatchPO_ID", new Integer(C_PaymentBatchPO_ID));
}
/** Get C_PaymentBatchPO_ID */
public int getC_PaymentBatchPO_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentBatchPO_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set FirstDueDate */
public void setFirstDueDate (Timestamp FirstDueDate)
{
set_Value ("FirstDueDate", FirstDueDate);
}
/** Get FirstDueDate */
public Timestamp getFirstDueDate() 
{
return (Timestamp)get_Value("FirstDueDate");
}
/** Set LastDueDate */
public void setLastDueDate (Timestamp LastDueDate)
{
set_Value ("LastDueDate", LastDueDate);
}
/** Get LastDueDate */
public Timestamp getLastDueDate() 
{
return (Timestamp)get_Value("LastDueDate");
}
/** Set PaymentAmount */
public void setPaymentAmount (BigDecimal PaymentAmount)
{
set_Value ("PaymentAmount", PaymentAmount);
}
/** Get PaymentAmount */
public BigDecimal getPaymentAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("PaymentAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PaymentDate */
public void setPaymentDate (Timestamp PaymentDate)
{
if (PaymentDate == null) throw new IllegalArgumentException ("PaymentDate is mandatory");
set_Value ("PaymentDate", PaymentDate);
}
/** Get PaymentDate */
public Timestamp getPaymentDate() 
{
return (Timestamp)get_Value("PaymentDate");
}
}
