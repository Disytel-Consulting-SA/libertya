/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CreditCardSettlement
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-02-09 12:25:48.771 */
public class X_C_CreditCardSettlement extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_CreditCardSettlement (Properties ctx, int C_CreditCardSettlement_ID, String trxName)
{
super (ctx, C_CreditCardSettlement_ID, trxName);
/** if (C_CreditCardSettlement_ID == 0)
{
setC_BPartner_ID (0);
setC_CreditCardSettlement_ID (0);
setDocAction (null);	// CO
setDocStatus (null);	// DR
setIsApproved (false);
setIsReconciled (false);
setPaymentDate (new Timestamp(System.currentTimeMillis()));
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_C_CreditCardSettlement (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_CreditCardSettlement");

/** TableName=C_CreditCardSettlement */
public static final String Table_Name="C_CreditCardSettlement";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_CreditCardSettlement");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CreditCardSettlement[").append(getID()).append("]");
return sb.toString();
}
/** Set Amount.
Amount in a defined currency */
public void setAmount (BigDecimal Amount)
{
set_Value ("Amount", Amount);
}
/** Get Amount.
Amount in a defined currency */
public BigDecimal getAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("Amount");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Credit Card Settlement ID */
public void setC_CreditCardSettlement_ID (int C_CreditCardSettlement_ID)
{
set_ValueNoCheck ("C_CreditCardSettlement_ID", new Integer(C_CreditCardSettlement_ID));
}
/** Get Credit Card Settlement ID */
public int getC_CreditCardSettlement_ID() 
{
Integer ii = (Integer)get_Value("C_CreditCardSettlement_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
if (C_Currency_ID <= 0) set_Value ("C_Currency_ID", null);
 else 
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Commission Amount */
public void setCommissionAmount (BigDecimal CommissionAmount)
{
set_Value ("CommissionAmount", CommissionAmount);
}
/** Get Commission Amount */
public BigDecimal getCommissionAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("CommissionAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Coupons total amount */
public void setCouponsTotalAmount (BigDecimal CouponsTotalAmount)
{
set_Value ("CouponsTotalAmount", CouponsTotalAmount);
}
/** Get Coupons total amount */
public BigDecimal getCouponsTotalAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("CouponsTotalAmount");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int C_PAYMENT_ID_AD_Reference_ID = MReference.getReferenceID("C_Payment");
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
public static final int CREDITCARDTYPE_AD_Reference_ID = MReference.getReferenceID("CreditCardTypes");
/** AMEX = AM */
public static final String CREDITCARDTYPE_AMEX = "AM";
/** VISA = VI */
public static final String CREDITCARDTYPE_VISA = "VI";
/** NARANJA = NA */
public static final String CREDITCARDTYPE_NARANJA = "NA";
/** FIRSTDATA = FD */
public static final String CREDITCARDTYPE_FIRSTDATA = "FD";
/** Set Credit Card.
Credit Card (Visa, MC, AmEx) */
public void setCreditCardType (String CreditCardType)
{
if (CreditCardType == null || CreditCardType.equals("AM") || CreditCardType.equals("VI") || CreditCardType.equals("NA") || CreditCardType.equals("FD"));
 else throw new IllegalArgumentException ("CreditCardType Invalid value - " + CreditCardType + " - Reference = CREDITCARDTYPE_AD_Reference_ID - AM - VI - NA - FD");
if (CreditCardType != null && CreditCardType.length() > 2)
{
log.warning("Length > 2 - truncated");
CreditCardType = CreditCardType.substring(0,2);
}
set_Value ("CreditCardType", CreditCardType);
}
/** Get Credit Card.
Credit Card (Visa, MC, AmEx) */
public String getCreditCardType() 
{
return (String)get_Value("CreditCardType");
}
public static final int DOCACTION_AD_Reference_ID = MReference.getReferenceID("_Document Action");
/** Approve = AP */
public static final String DOCACTION_Approve = "AP";
/** Close = CL */
public static final String DOCACTION_Close = "CL";
/** Prepare = PR */
public static final String DOCACTION_Prepare = "PR";
/** Invalidate = IN */
public static final String DOCACTION_Invalidate = "IN";
/** Complete = CO */
public static final String DOCACTION_Complete = "CO";
/** <None> = -- */
public static final String DOCACTION_None = "--";
/** Reverse - Correct = RC */
public static final String DOCACTION_Reverse_Correct = "RC";
/** Reject = RJ */
public static final String DOCACTION_Reject = "RJ";
/** Reverse - Accrual = RA */
public static final String DOCACTION_Reverse_Accrual = "RA";
/** Wait Complete = WC */
public static final String DOCACTION_WaitComplete = "WC";
/** Unlock = XL */
public static final String DOCACTION_Unlock = "XL";
/** Re-activate = RE */
public static final String DOCACTION_Re_Activate = "RE";
/** Post = PO */
public static final String DOCACTION_Post = "PO";
/** Void = VO */
public static final String DOCACTION_Void = "VO";
/** Set Document Action.
The targeted status of the document */
public void setDocAction (String DocAction)
{
if (DocAction.equals("AP") || DocAction.equals("CL") || DocAction.equals("PR") || DocAction.equals("IN") || DocAction.equals("CO") || DocAction.equals("--") || DocAction.equals("RC") || DocAction.equals("RJ") || DocAction.equals("RA") || DocAction.equals("WC") || DocAction.equals("XL") || DocAction.equals("RE") || DocAction.equals("PO") || DocAction.equals("VO"));
 else throw new IllegalArgumentException ("DocAction Invalid value - " + DocAction + " - Reference = DOCACTION_AD_Reference_ID - AP - CL - PR - IN - CO - -- - RC - RJ - RA - WC - XL - RE - PO - VO");
if (DocAction == null) throw new IllegalArgumentException ("DocAction is mandatory");
if (DocAction.length() > 2)
{
log.warning("Length > 2 - truncated");
DocAction = DocAction.substring(0,2);
}
set_Value ("DocAction", DocAction);
}
/** Get Document Action.
The targeted status of the document */
public String getDocAction() 
{
return (String)get_Value("DocAction");
}
public static final int DOCSTATUS_AD_Reference_ID = MReference.getReferenceID("_Document Status");
/** Voided = VO */
public static final String DOCSTATUS_Voided = "VO";
/** Not Approved = NA */
public static final String DOCSTATUS_NotApproved = "NA";
/** In Progress = IP */
public static final String DOCSTATUS_InProgress = "IP";
/** Completed = CO */
public static final String DOCSTATUS_Completed = "CO";
/** Approved = AP */
public static final String DOCSTATUS_Approved = "AP";
/** Closed = CL */
public static final String DOCSTATUS_Closed = "CL";
/** Waiting Confirmation = WC */
public static final String DOCSTATUS_WaitingConfirmation = "WC";
/** Waiting Payment = WP */
public static final String DOCSTATUS_WaitingPayment = "WP";
/** Unknown = ?? */
public static final String DOCSTATUS_Unknown = "??";
/** Drafted = DR */
public static final String DOCSTATUS_Drafted = "DR";
/** Invalid = IN */
public static final String DOCSTATUS_Invalid = "IN";
/** Reversed = RE */
public static final String DOCSTATUS_Reversed = "RE";
/** Set Document Status.
The current status of the document */
public void setDocStatus (String DocStatus)
{
if (DocStatus.equals("VO") || DocStatus.equals("NA") || DocStatus.equals("IP") || DocStatus.equals("CO") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("WC") || DocStatus.equals("WP") || DocStatus.equals("??") || DocStatus.equals("DR") || DocStatus.equals("IN") || DocStatus.equals("RE"));
 else throw new IllegalArgumentException ("DocStatus Invalid value - " + DocStatus + " - Reference = DOCSTATUS_AD_Reference_ID - VO - NA - IP - CO - AP - CL - WC - WP - ?? - DR - IN - RE");
if (DocStatus == null) throw new IllegalArgumentException ("DocStatus is mandatory");
if (DocStatus.length() > 2)
{
log.warning("Length > 2 - truncated");
DocStatus = DocStatus.substring(0,2);
}
set_Value ("DocStatus", DocStatus);
}
/** Get Document Status.
The current status of the document */
public String getDocStatus() 
{
return (String)get_Value("DocStatus");
}
/** Set Expenses */
public void setExpenses (BigDecimal Expenses)
{
set_Value ("Expenses", Expenses);
}
/** Get Expenses */
public BigDecimal getExpenses() 
{
BigDecimal bd = (BigDecimal)get_Value("Expenses");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Approved.
Indicates if this document requires approval */
public void setIsApproved (boolean IsApproved)
{
set_Value ("IsApproved", new Boolean(IsApproved));
}
/** Get Approved.
Indicates if this document requires approval */
public boolean isApproved() 
{
Object oo = get_Value("IsApproved");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Reconciled.
Payment is reconciled with bank statement */
public void setIsReconciled (boolean IsReconciled)
{
set_Value ("IsReconciled", new Boolean(IsReconciled));
}
/** Get Reconciled.
Payment is reconciled with bank statement */
public boolean isReconciled() 
{
Object oo = get_Value("IsReconciled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IVA Amount */
public void setIVAAmount (BigDecimal IVAAmount)
{
set_Value ("IVAAmount", IVAAmount);
}
/** Get IVA Amount */
public BigDecimal getIVAAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("IVAAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Net amount */
public void setNetAmount (BigDecimal NetAmount)
{
set_Value ("NetAmount", NetAmount);
}
/** Get Net amount */
public BigDecimal getNetAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("NetAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Payment */
public void setPayment (String Payment)
{
if (Payment != null && Payment.length() > 256)
{
log.warning("Length > 256 - truncated");
Payment = Payment.substring(0,256);
}
set_Value ("Payment", Payment);
}
/** Get Payment */
public String getPayment() 
{
return (String)get_Value("Payment");
}
/** Set Payment date */
public void setPaymentDate (Timestamp PaymentDate)
{
if (PaymentDate == null) throw new IllegalArgumentException ("PaymentDate is mandatory");
set_Value ("PaymentDate", PaymentDate);
}
/** Get Payment date */
public Timestamp getPaymentDate() 
{
return (Timestamp)get_Value("PaymentDate");
}
/** Set Perception */
public void setPerception (BigDecimal Perception)
{
set_Value ("Perception", Perception);
}
/** Get Perception */
public BigDecimal getPerception() 
{
BigDecimal bd = (BigDecimal)get_Value("Perception");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Posted.
Posting status */
public void setPosted (boolean Posted)
{
set_Value ("Posted", new Boolean(Posted));
}
/** Get Posted.
Posting status */
public boolean isPosted() 
{
Object oo = get_Value("Posted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Reconcile Coupons */
public void setReconcileCoupons (String ReconcileCoupons)
{
if (ReconcileCoupons != null && ReconcileCoupons.length() > 1)
{
log.warning("Length > 1 - truncated");
ReconcileCoupons = ReconcileCoupons.substring(0,1);
}
set_Value ("ReconcileCoupons", ReconcileCoupons);
}
/** Get Reconcile Coupons */
public String getReconcileCoupons() 
{
return (String)get_Value("ReconcileCoupons");
}
/** Set Select all coupons */
public void setSelectAllCoupons (String SelectAllCoupons)
{
if (SelectAllCoupons != null && SelectAllCoupons.length() > 1)
{
log.warning("Length > 1 - truncated");
SelectAllCoupons = SelectAllCoupons.substring(0,1);
}
set_Value ("SelectAllCoupons", SelectAllCoupons);
}
/** Get Select all coupons */
public String getSelectAllCoupons() 
{
return (String)get_Value("SelectAllCoupons");
}
/** Set SettlementNo */
public void setSettlementNo (String SettlementNo)
{
if (SettlementNo != null && SettlementNo.length() > 24)
{
log.warning("Length > 24 - truncated");
SettlementNo = SettlementNo.substring(0,24);
}
set_Value ("SettlementNo", SettlementNo);
}
/** Get SettlementNo */
public String getSettlementNo() 
{
return (String)get_Value("SettlementNo");
}
/** Set Unselect all coupons */
public void setUnselectAllCoupons (String UnselectAllCoupons)
{
if (UnselectAllCoupons != null && UnselectAllCoupons.length() > 1)
{
log.warning("Length > 1 - truncated");
UnselectAllCoupons = UnselectAllCoupons.substring(0,1);
}
set_Value ("UnselectAllCoupons", UnselectAllCoupons);
}
/** Get Unselect all coupons */
public String getUnselectAllCoupons() 
{
return (String)get_Value("UnselectAllCoupons");
}
/** Set Withholding */
public void setWithholding (BigDecimal Withholding)
{
set_Value ("Withholding", Withholding);
}
/** Get Withholding */
public BigDecimal getWithholding() 
{
BigDecimal bd = (BigDecimal)get_Value("Withholding");
if (bd == null) return Env.ZERO;
return bd;
}
}
