/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PaySelectionCheck
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:31.343 */
public class X_C_PaySelectionCheck extends PO
{
/** Constructor estándar */
public X_C_PaySelectionCheck (Properties ctx, int C_PaySelectionCheck_ID, String trxName)
{
super (ctx, C_PaySelectionCheck_ID, trxName);
/** if (C_PaySelectionCheck_ID == 0)
{
setC_BPartner_ID (0);
setC_PaySelectionCheck_ID (0);
setC_PaySelection_ID (0);
setDiscountAmt (Env.ZERO);
setIsPrinted (false);
setIsReceipt (false);
setPayAmt (Env.ZERO);
setPaymentRule (null);
setProcessed (false);	// N
setQty (0);
}
 */
}
/** Load Constructor */
public X_C_PaySelectionCheck (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=525 */
public static final int Table_ID=525;

/** TableName=C_PaySelectionCheck */
public static final String Table_Name="C_PaySelectionCheck";

protected static KeyNamePair Model = new KeyNamePair(525,"C_PaySelectionCheck");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PaySelectionCheck[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
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
/** Set Pay Selection Check.
Payment Selection Check */
public void setC_PaySelectionCheck_ID (int C_PaySelectionCheck_ID)
{
set_ValueNoCheck ("C_PaySelectionCheck_ID", new Integer(C_PaySelectionCheck_ID));
}
/** Get Pay Selection Check.
Payment Selection Check */
public int getC_PaySelectionCheck_ID() 
{
Integer ii = (Integer)get_Value("C_PaySelectionCheck_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Selection.
Payment Selection */
public void setC_PaySelection_ID (int C_PaySelection_ID)
{
set_ValueNoCheck ("C_PaySelection_ID", new Integer(C_PaySelection_ID));
}
/** Get Payment Selection.
Payment Selection */
public int getC_PaySelection_ID() 
{
Integer ii = (Integer)get_Value("C_PaySelection_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
if (C_Payment_ID <= 0) set_Value ("C_Payment_ID", null);
 else 
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
/** Set CurrencyInWord */
public void setCurrencyInWord (String CurrencyInWord)
{
if (CurrencyInWord != null && CurrencyInWord.length() > 22)
{
log.warning("Length > 22 - truncated");
CurrencyInWord = CurrencyInWord.substring(0,21);
}
set_Value ("CurrencyInWord", CurrencyInWord);
}
/** Get CurrencyInWord */
public String getCurrencyInWord() 
{
return (String)get_Value("CurrencyInWord");
}
/** Set Discount Amount.
Calculated amount of discount */
public void setDiscountAmt (BigDecimal DiscountAmt)
{
if (DiscountAmt == null) throw new IllegalArgumentException ("DiscountAmt is mandatory");
set_Value ("DiscountAmt", DiscountAmt);
}
/** Get Discount Amount.
Calculated amount of discount */
public BigDecimal getDiscountAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("DiscountAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Document No.
Document sequence number of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,29);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence number of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
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
/** Set Receipt.
This is a sales transaction (receipt) */
public void setIsReceipt (boolean IsReceipt)
{
set_Value ("IsReceipt", new Boolean(IsReceipt));
}
/** Get Receipt.
This is a sales transaction (receipt) */
public boolean isReceipt() 
{
Object oo = get_Value("IsReceipt");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
public static final int PAYMENTRULE_AD_Reference_ID=195;
/** Direct Deposit = T */
public static final String PAYMENTRULE_DirectDeposit = "T";
/** Transfer = Tr */
public static final String PAYMENTRULE_Transfer = "Tr";
/** Direct Debit = D */
public static final String PAYMENTRULE_DirectDebit = "D";
/** Credit Card = K */
public static final String PAYMENTRULE_CreditCard = "K";
/** Payment Check = PC */
public static final String PAYMENTRULE_PaymentCheck = "PC";
/** Cash = B */
public static final String PAYMENTRULE_Cash = "B";
/** Confirming = Cf */
public static final String PAYMENTRULE_Confirming = "Cf";
/** On Credit = P */
public static final String PAYMENTRULE_OnCredit = "P";
/** Check = S */
public static final String PAYMENTRULE_Check = "S";
/** Set Payment Rule.
How you pay the invoice */
public void setPaymentRule (String PaymentRule)
{
if (PaymentRule.equals("T") || PaymentRule.equals("Tr") || PaymentRule.equals("D") || PaymentRule.equals("K") || PaymentRule.equals("PC") || PaymentRule.equals("B") || PaymentRule.equals("Cf") || PaymentRule.equals("P") || PaymentRule.equals("S"));
 else throw new IllegalArgumentException ("PaymentRule Invalid value - Reference_ID=195 - T - Tr - D - K - PC - B - Cf - P - S");
if (PaymentRule == null) throw new IllegalArgumentException ("PaymentRule is mandatory");
if (PaymentRule.length() > 1)
{
log.warning("Length > 1 - truncated");
PaymentRule = PaymentRule.substring(0,0);
}
set_Value ("PaymentRule", PaymentRule);
}
/** Get Payment Rule.
How you pay the invoice */
public String getPaymentRule() 
{
return (String)get_Value("PaymentRule");
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
Quantity */
public void setQty (int Qty)
{
set_Value ("Qty", new Integer(Qty));
}
/** Get Quantity.
Quantity */
public int getQty() 
{
Integer ii = (Integer)get_Value("Qty");
if (ii == null) return 0;
return ii.intValue();
}
}
