/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Invoice
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-07-24 00:26:51.256 */
public class X_C_Invoice extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Invoice (Properties ctx, int C_Invoice_ID, String trxName)
{
super (ctx, C_Invoice_ID, trxName);
/** if (C_Invoice_ID == 0)
{
setApplyPercepcion (true);	// Y
setC_BPartner_ID (0);
setC_BPartner_Location_ID (0);
setC_Currency_ID (0);	// @C_Currency_ID@
setC_DocType_ID (0);	// 0
setC_DocTypeTarget_ID (0);
setC_Invoice_ID (0);
setC_PaymentTerm_ID (0);
setCreateCashLine (true);	// Y
setDateAcct (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDateInvoiced (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDocAction (null);	// CO
setDocStatus (null);	// DR
setDocumentNo (null);
setFiscalAlreadyPrinted (false);
setGrandTotal (Env.ZERO);
setInitialCurrentAccountAmt (Env.ZERO);
setIsApproved (false);	// N
setIsCopy (false);
setIsDiscountPrinted (false);
setIsExchange (false);
setIsInDispute (false);	// N
setIsPaid (false);
setIsPayScheduleValid (false);
setIsPrinted (false);
setIsSelfService (false);
setIsSOTrx (false);	// @IsSOTrx@
setIsTaxIncluded (false);
setIsTransferred (false);
setIsVoidable (false);
setManageDragOrderDiscounts (false);
setManualDocumentNo (false);
setManualGeneralDiscount (Env.ZERO);
setM_PriceList_ID (0);
setNetAmount (Env.ZERO);
setNotExchangeableCredit (false);
setNumeroComprobante (0);
setPaymentRule (null);	// P
setPosted (false);	// N
setProcessed (false);
setPuntoDeVenta (0);
setSendEMail (false);
setTotalLines (Env.ZERO);
setUpdateOrderQty (false);
}
 */
}
/** Load Constructor */
public X_C_Invoice (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Invoice");

/** TableName=C_Invoice */
public static final String Table_Name="C_Invoice";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Invoice");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Invoice[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_ORGTRX_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (Trx)");
/** Set Trx Organization.
Performing or initiating organization */
public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
{
if (AD_OrgTrx_ID <= 0) set_Value ("AD_OrgTrx_ID", null);
 else 
set_Value ("AD_OrgTrx_ID", new Integer(AD_OrgTrx_ID));
}
/** Get Trx Organization.
Performing or initiating organization */
public int getAD_OrgTrx_ID() 
{
Integer ii = (Integer)get_Value("AD_OrgTrx_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_Value ("AD_User_ID", null);
 else 
set_Value ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Apply Percepcion */
public void setApplyPercepcion (boolean ApplyPercepcion)
{
set_Value ("ApplyPercepcion", new Boolean(ApplyPercepcion));
}
/** Get Apply Percepcion */
public boolean isApplyPercepcion() 
{
Object oo = get_Value("ApplyPercepcion");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Authorization Code */
public void setAuthCode (String AuthCode)
{
if (AuthCode != null && AuthCode.length() > 255)
{
log.warning("Length > 255 - truncated");
AuthCode = AuthCode.substring(0,255);
}
set_Value ("AuthCode", AuthCode);
}
/** Get Authorization Code */
public String getAuthCode() 
{
return (String)get_Value("AuthCode");
}
/** Set Authorization Matching */
public void setAuthMatch (boolean AuthMatch)
{
set_Value ("AuthMatch", new Boolean(AuthMatch));
}
/** Get Authorization Matching */
public boolean isAuthMatch() 
{
Object oo = get_Value("AuthMatch");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Activity.
Business Activity */
public void setC_Activity_ID (int C_Activity_ID)
{
if (C_Activity_ID <= 0) set_Value ("C_Activity_ID", null);
 else 
set_Value ("C_Activity_ID", new Integer(C_Activity_ID));
}
/** Get Activity.
Business Activity */
public int getC_Activity_ID() 
{
Integer ii = (Integer)get_Value("C_Activity_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set cae */
public void setcae (String cae)
{
if (cae != null && cae.length() > 14)
{
log.warning("Length > 14 - truncated");
cae = cae.substring(0,14);
}
set_Value ("cae", cae);
}
/** Get cae */
public String getcae() 
{
return (String)get_Value("cae");
}
/** Set caecbte */
public void setcaecbte (int caecbte)
{
set_Value ("caecbte", new Integer(caecbte));
}
/** Get caecbte */
public int getcaecbte() 
{
Integer ii = (Integer)get_Value("caecbte");
if (ii == null) return 0;
return ii.intValue();
}
/** Set caeerror */
public void setcaeerror (String caeerror)
{
if (caeerror != null && caeerror.length() > 255)
{
log.warning("Length > 255 - truncated");
caeerror = caeerror.substring(0,255);
}
set_Value ("caeerror", caeerror);
}
/** Get caeerror */
public String getcaeerror() 
{
return (String)get_Value("caeerror");
}
/** Set CAI */
public void setCAI (String CAI)
{
if (CAI != null && CAI.length() > 14)
{
log.warning("Length > 14 - truncated");
CAI = CAI.substring(0,14);
}
set_Value ("CAI", CAI);
}
/** Get CAI */
public String getCAI() 
{
return (String)get_Value("CAI");
}
/** Set Caja */
public void setCaja (String Caja)
{
if (Caja != null && Caja.length() > 2)
{
log.warning("Length > 2 - truncated");
Caja = Caja.substring(0,2);
}
set_Value ("Caja", Caja);
}
/** Get Caja */
public String getCaja() 
{
return (String)get_Value("Caja");
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
/** Set Campaign.
Marketing Campaign */
public void setC_Campaign_ID (int C_Campaign_ID)
{
if (C_Campaign_ID <= 0) set_Value ("C_Campaign_ID", null);
 else 
set_Value ("C_Campaign_ID", new Integer(C_Campaign_ID));
}
/** Get Campaign.
Marketing Campaign */
public int getC_Campaign_ID() 
{
Integer ii = (Integer)get_Value("C_Campaign_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cash Journal Line.
Cash Journal Line */
public void setC_CashLine_ID (int C_CashLine_ID)
{
if (C_CashLine_ID <= 0) set_Value ("C_CashLine_ID", null);
 else 
set_Value ("C_CashLine_ID", new Integer(C_CashLine_ID));
}
/** Get Cash Journal Line.
Cash Journal Line */
public int getC_CashLine_ID() 
{
Integer ii = (Integer)get_Value("C_CashLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_CHARGE_ID_AD_Reference_ID = MReference.getReferenceID("C_Charge");
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
/** Set Currency Type.
Currency Conversion Rate Type */
public void setC_ConversionType_ID (int C_ConversionType_ID)
{
if (C_ConversionType_ID <= 0) set_Value ("C_ConversionType_ID", null);
 else 
set_Value ("C_ConversionType_ID", new Integer(C_ConversionType_ID));
}
/** Get Currency Type.
Currency Conversion Rate Type */
public int getC_ConversionType_ID() 
{
Integer ii = (Integer)get_Value("C_ConversionType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
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
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
set_ValueNoCheck ("C_DocType_ID", new Integer(C_DocType_ID));
}
/** Get Document Type.
Document type or rules */
public int getC_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCTYPETARGET_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Target Document Type.
Target document type for conversing documents */
public void setC_DocTypeTarget_ID (int C_DocTypeTarget_ID)
{
set_Value ("C_DocTypeTarget_ID", new Integer(C_DocTypeTarget_ID));
}
/** Get Target Document Type.
Target document type for conversing documents */
public int getC_DocTypeTarget_ID() 
{
Integer ii = (Integer)get_Value("C_DocTypeTarget_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Charge amount.
Charge Amount */
public void setChargeAmt (BigDecimal ChargeAmt)
{
set_Value ("ChargeAmt", ChargeAmt);
}
/** Get Charge amount.
Charge Amount */
public BigDecimal getChargeAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ChargeAmt");
if (bd == null) return Env.ZERO;
return bd;
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
public static final int C_INVOICE_ORIG_ID_AD_Reference_ID = MReference.getReferenceID("C_Invoice");
/** Set Factura Original */
public void setC_Invoice_Orig_ID (int C_Invoice_Orig_ID)
{
if (C_Invoice_Orig_ID <= 0) set_Value ("C_Invoice_Orig_ID", null);
 else 
set_Value ("C_Invoice_Orig_ID", new Integer(C_Invoice_Orig_ID));
}
/** Get Factura Original */
public int getC_Invoice_Orig_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_Orig_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Letra del comprobante */
public void setC_Letra_Comprobante_ID (int C_Letra_Comprobante_ID)
{
if (C_Letra_Comprobante_ID <= 0) set_Value ("C_Letra_Comprobante_ID", null);
 else 
set_Value ("C_Letra_Comprobante_ID", new Integer(C_Letra_Comprobante_ID));
}
/** Get Letra del comprobante */
public int getC_Letra_Comprobante_ID() 
{
Integer ii = (Integer)get_Value("C_Letra_Comprobante_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Codigo Categoria IVA */
public void setCodigoCategoriaIVA (int CodigoCategoriaIVA)
{
throw new IllegalArgumentException ("CodigoCategoriaIVA is virtual column");
}
/** Get Codigo Categoria IVA */
public int getCodigoCategoriaIVA() 
{
Integer ii = (Integer)get_Value("CodigoCategoriaIVA");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Copy From.
Copy From Record */
public void setCopyFrom (String CopyFrom)
{
if (CopyFrom != null && CopyFrom.length() > 1)
{
log.warning("Length > 1 - truncated");
CopyFrom = CopyFrom.substring(0,1);
}
set_Value ("CopyFrom", CopyFrom);
}
/** Get Copy From.
Copy From Record */
public String getCopyFrom() 
{
return (String)get_Value("CopyFrom");
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
/** Set Payment Term.
The terms for Payment of this transaction */
public void setC_PaymentTerm_ID (int C_PaymentTerm_ID)
{
set_Value ("C_PaymentTerm_ID", new Integer(C_PaymentTerm_ID));
}
/** Get Payment Term.
The terms for Payment of this transaction */
public int getC_PaymentTerm_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentTerm_ID");
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
public static final int C_POSPAYMENTMEDIUM_CREDIT_ID_AD_Reference_ID = MReference.getReferenceID("C_POSPaymentMedium");
/** Set Credit payment medium */
public void setC_POSPaymentMedium_Credit_ID (int C_POSPaymentMedium_Credit_ID)
{
if (C_POSPaymentMedium_Credit_ID <= 0) set_Value ("C_POSPaymentMedium_Credit_ID", null);
 else 
set_Value ("C_POSPaymentMedium_Credit_ID", new Integer(C_POSPaymentMedium_Credit_ID));
}
/** Get Credit payment medium */
public int getC_POSPaymentMedium_Credit_ID() 
{
Integer ii = (Integer)get_Value("C_POSPaymentMedium_Credit_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set POS Payment Medium.
POS Terminal Payment Medium */
public void setC_POSPaymentMedium_ID (int C_POSPaymentMedium_ID)
{
if (C_POSPaymentMedium_ID <= 0) set_Value ("C_POSPaymentMedium_ID", null);
 else 
set_Value ("C_POSPaymentMedium_ID", new Integer(C_POSPaymentMedium_ID));
}
/** Get POS Payment Medium.
POS Terminal Payment Medium */
public int getC_POSPaymentMedium_ID() 
{
Integer ii = (Integer)get_Value("C_POSPaymentMedium_ID");
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
/** Set Create Cash Line.
Create Cash Line */
public void setCreateCashLine (boolean CreateCashLine)
{
set_Value ("CreateCashLine", new Boolean(CreateCashLine));
}
/** Get Create Cash Line.
Create Cash Line */
public boolean isCreateCashLine() 
{
Object oo = get_Value("CreateCashLine");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Create lines from.
Process which will generate a new document lines based on an existing document */
public void setCreateFrom (String CreateFrom)
{
if (CreateFrom != null && CreateFrom.length() > 1)
{
log.warning("Length > 1 - truncated");
CreateFrom = CreateFrom.substring(0,1);
}
set_Value ("CreateFrom", CreateFrom);
}
/** Get Create lines from.
Process which will generate a new document lines based on an existing document */
public String getCreateFrom() 
{
return (String)get_Value("CreateFrom");
}
public static final int C_REGION_DELIVERY_ID_AD_Reference_ID = MReference.getReferenceID("C_Region");
/** Set Delivery Region */
public void setC_Region_Delivery_ID (int C_Region_Delivery_ID)
{
if (C_Region_Delivery_ID <= 0) set_Value ("C_Region_Delivery_ID", null);
 else 
set_Value ("C_Region_Delivery_ID", new Integer(C_Region_Delivery_ID));
}
/** Get Delivery Region */
public int getC_Region_Delivery_ID() 
{
Integer ii = (Integer)get_Value("C_Region_Delivery_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Region.
Identifies a geographical Region */
public void setC_Region_ID (int C_Region_ID)
{
if (C_Region_ID <= 0) set_Value ("C_Region_ID", null);
 else 
set_Value ("C_Region_ID", new Integer(C_Region_ID));
}
/** Get Region.
Identifies a geographical Region */
public int getC_Region_ID() 
{
Integer ii = (Integer)get_Value("C_Region_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CUIT */
public void setCUIT (String CUIT)
{
if (CUIT != null && CUIT.length() > 20)
{
log.warning("Length > 20 - truncated");
CUIT = CUIT.substring(0,20);
}
set_Value ("CUIT", CUIT);
}
/** Get CUIT */
public String getCUIT() 
{
return (String)get_Value("CUIT");
}
/** Set Account Date.
Accounting Date */
public void setDateAcct (Timestamp DateAcct)
{
if (DateAcct == null) throw new IllegalArgumentException ("DateAcct is mandatory");
set_Value ("DateAcct", DateAcct);
}
/** Get Account Date.
Accounting Date */
public Timestamp getDateAcct() 
{
return (Timestamp)get_Value("DateAcct");
}
/** Set CAI Date */
public void setDateCAI (Timestamp DateCAI)
{
set_Value ("DateCAI", DateCAI);
}
/** Get CAI Date */
public Timestamp getDateCAI() 
{
return (Timestamp)get_Value("DateCAI");
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
/** Set Date Ordered.
Date of Order */
public void setDateOrdered (Timestamp DateOrdered)
{
set_ValueNoCheck ("DateOrdered", DateOrdered);
}
/** Get Date Ordered.
Date of Order */
public Timestamp getDateOrdered() 
{
return (Timestamp)get_Value("DateOrdered");
}
/** Set Date printed.
Date the document was printed. */
public void setDatePrinted (Timestamp DatePrinted)
{
set_Value ("DatePrinted", DatePrinted);
}
/** Get Date printed.
Date the document was printed. */
public Timestamp getDatePrinted() 
{
return (Timestamp)get_Value("DatePrinted");
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
 else throw new IllegalArgumentException ("DocAction Invalid value - Reference = DOCACTION_AD_Reference_ID - AP - CL - PR - IN - CO - -- - RC - RJ - RA - WC - XL - RE - PO - VO");
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
 else throw new IllegalArgumentException ("DocStatus Invalid value - Reference = DOCSTATUS_AD_Reference_ID - VO - NA - IP - CO - AP - CL - WC - WP - ?? - DR - IN - RE");
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
/** Set Document No.
Document sequence number of the document */
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
Document sequence number of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDocumentNo());
}
/** Set Already printed */
public void setFiscalAlreadyPrinted (boolean FiscalAlreadyPrinted)
{
set_Value ("FiscalAlreadyPrinted", new Boolean(FiscalAlreadyPrinted));
}
/** Get Already printed */
public boolean isFiscalAlreadyPrinted() 
{
Object oo = get_Value("FiscalAlreadyPrinted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Fiscal Description */
public void setFiscalDescription (String FiscalDescription)
{
if (FiscalDescription != null && FiscalDescription.length() > 255)
{
log.warning("Length > 255 - truncated");
FiscalDescription = FiscalDescription.substring(0,255);
}
set_Value ("FiscalDescription", FiscalDescription);
}
/** Get Fiscal Description */
public String getFiscalDescription() 
{
return (String)get_Value("FiscalDescription");
}
/** Set Generate To.
Generate To */
public void setGenerateTo (String GenerateTo)
{
if (GenerateTo != null && GenerateTo.length() > 1)
{
log.warning("Length > 1 - truncated");
GenerateTo = GenerateTo.substring(0,1);
}
set_Value ("GenerateTo", GenerateTo);
}
/** Get Generate To.
Generate To */
public String getGenerateTo() 
{
return (String)get_Value("GenerateTo");
}
/** Set Grand Total.
Total amount of document */
public void setGrandTotal (BigDecimal GrandTotal)
{
if (GrandTotal == null) throw new IllegalArgumentException ("GrandTotal is mandatory");
set_ValueNoCheck ("GrandTotal", GrandTotal);
}
/** Get Grand Total.
Total amount of document */
public BigDecimal getGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("GrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set idcae */
public void setidcae (String idcae)
{
if (idcae != null && idcae.length() > 15)
{
log.warning("Length > 15 - truncated");
idcae = idcae.substring(0,15);
}
set_Value ("idcae", idcae);
}
/** Get idcae */
public String getidcae() 
{
return (String)get_Value("idcae");
}
/** Set Initial Current Account Amt */
public void setInitialCurrentAccountAmt (BigDecimal InitialCurrentAccountAmt)
{
if (InitialCurrentAccountAmt == null) throw new IllegalArgumentException ("InitialCurrentAccountAmt is mandatory");
set_Value ("InitialCurrentAccountAmt", InitialCurrentAccountAmt);
}
/** Get Initial Current Account Amt */
public BigDecimal getInitialCurrentAccountAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("InitialCurrentAccountAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Invoice Adress */
public void setInvoice_Adress (String Invoice_Adress)
{
if (Invoice_Adress != null && Invoice_Adress.length() > 120)
{
log.warning("Length > 120 - truncated");
Invoice_Adress = Invoice_Adress.substring(0,120);
}
set_Value ("Invoice_Adress", Invoice_Adress);
}
/** Get Invoice Adress */
public String getInvoice_Adress() 
{
return (String)get_Value("Invoice_Adress");
}
/** Set Approved.
Indicates if this document requires approval */
public void setIsApproved (boolean IsApproved)
{
set_ValueNoCheck ("IsApproved", new Boolean(IsApproved));
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
/** Set IsCopy.
This records is a copy from another record */
public void setIsCopy (boolean IsCopy)
{
set_Value ("IsCopy", new Boolean(IsCopy));
}
/** Get IsCopy.
This records is a copy from another record */
public boolean isCopy() 
{
Object oo = get_Value("IsCopy");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Discount Printed.
Print Discount on Invoice and Order */
public void setIsDiscountPrinted (boolean IsDiscountPrinted)
{
set_Value ("IsDiscountPrinted", new Boolean(IsDiscountPrinted));
}
/** Get Discount Printed.
Print Discount on Invoice and Order */
public boolean isDiscountPrinted() 
{
Object oo = get_Value("IsDiscountPrinted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Exchange */
public void setIsExchange (boolean IsExchange)
{
set_Value ("IsExchange", new Boolean(IsExchange));
}
/** Get Is Exchange */
public boolean isExchange() 
{
Object oo = get_Value("IsExchange");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set In Dispute.
Document is in dispute */
public void setIsInDispute (boolean IsInDispute)
{
set_Value ("IsInDispute", new Boolean(IsInDispute));
}
/** Get In Dispute.
Document is in dispute */
public boolean isInDispute() 
{
Object oo = get_Value("IsInDispute");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Paid.
The document is paid */
public void setIsPaid (boolean IsPaid)
{
set_Value ("IsPaid", new Boolean(IsPaid));
}
/** Get Paid.
The document is paid */
public boolean isPaid() 
{
Object oo = get_Value("IsPaid");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Pay Schedule valid.
Is the Payment Schedule is valid */
public void setIsPayScheduleValid (boolean IsPayScheduleValid)
{
set_ValueNoCheck ("IsPayScheduleValid", new Boolean(IsPayScheduleValid));
}
/** Get Pay Schedule valid.
Is the Payment Schedule is valid */
public boolean isPayScheduleValid() 
{
Object oo = get_Value("IsPayScheduleValid");
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
set_ValueNoCheck ("IsPrinted", new Boolean(IsPrinted));
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
/** Set Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public void setIsSelfService (boolean IsSelfService)
{
set_Value ("IsSelfService", new Boolean(IsSelfService));
}
/** Get Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public boolean isSelfService() 
{
Object oo = get_Value("IsSelfService");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sales Transaction.
This is a Sales Transaction */
public void setIsSOTrx (boolean IsSOTrx)
{
set_ValueNoCheck ("IsSOTrx", new Boolean(IsSOTrx));
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
/** Set Price includes Tax.
Tax is included in the price  */
public void setIsTaxIncluded (boolean IsTaxIncluded)
{
set_Value ("IsTaxIncluded", new Boolean(IsTaxIncluded));
}
/** Get Price includes Tax.
Tax is included in the price  */
public boolean isTaxIncluded() 
{
Object oo = get_Value("IsTaxIncluded");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Transferred.
Transferred to General Ledger (i.e. accounted) */
public void setIsTransferred (boolean IsTransferred)
{
set_ValueNoCheck ("IsTransferred", new Boolean(IsTransferred));
}
/** Get Transferred.
Transferred to General Ledger (i.e. accounted) */
public boolean isTransferred() 
{
Object oo = get_Value("IsTransferred");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Voidable */
public void setIsVoidable (boolean IsVoidable)
{
set_Value ("IsVoidable", new Boolean(IsVoidable));
}
/** Get Is Voidable */
public boolean isVoidable() 
{
Object oo = get_Value("IsVoidable");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Manage Drag Order Discounts */
public void setManageDragOrderDiscounts (boolean ManageDragOrderDiscounts)
{
set_Value ("ManageDragOrderDiscounts", new Boolean(ManageDragOrderDiscounts));
}
/** Get Manage Drag Order Discounts */
public boolean isManageDragOrderDiscounts() 
{
Object oo = get_Value("ManageDragOrderDiscounts");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Manual DocumentNo */
public void setManualDocumentNo (boolean ManualDocumentNo)
{
set_Value ("ManualDocumentNo", new Boolean(ManualDocumentNo));
}
/** Get Manual DocumentNo */
public boolean isManualDocumentNo() 
{
Object oo = get_Value("ManualDocumentNo");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Manual General Discount */
public void setManualGeneralDiscount (BigDecimal ManualGeneralDiscount)
{
if (ManualGeneralDiscount == null) throw new IllegalArgumentException ("ManualGeneralDiscount is mandatory");
set_Value ("ManualGeneralDiscount", ManualGeneralDiscount);
}
/** Get Manual General Discount */
public BigDecimal getManualGeneralDiscount() 
{
BigDecimal bd = (BigDecimal)get_Value("ManualGeneralDiscount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Price List.
Unique identifier of a Price List */
public void setM_PriceList_ID (int M_PriceList_ID)
{
set_Value ("M_PriceList_ID", new Integer(M_PriceList_ID));
}
/** Get Price List.
Unique identifier of a Price List */
public int getM_PriceList_ID() 
{
Integer ii = (Integer)get_Value("M_PriceList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set RMA.
Return Material Authorization */
public void setM_RMA_ID (int M_RMA_ID)
{
if (M_RMA_ID <= 0) set_Value ("M_RMA_ID", null);
 else 
set_Value ("M_RMA_ID", new Integer(M_RMA_ID));
}
/** Get RMA.
Return Material Authorization */
public int getM_RMA_ID() 
{
Integer ii = (Integer)get_Value("M_RMA_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Net Amount */
public void setNetAmount (BigDecimal NetAmount)
{
if (NetAmount == null) throw new IllegalArgumentException ("NetAmount is mandatory");
set_Value ("NetAmount", NetAmount);
}
/** Get Net Amount */
public BigDecimal getNetAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("NetAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Nombre Entidad Facturada */
public void setNombreCli (String NombreCli)
{
if (NombreCli != null && NombreCli.length() > 40)
{
log.warning("Length > 40 - truncated");
NombreCli = NombreCli.substring(0,40);
}
set_Value ("NombreCli", NombreCli);
}
/** Get Nombre Entidad Facturada */
public String getNombreCli() 
{
return (String)get_Value("NombreCli");
}
/** Set Not Exchangeable Credit */
public void setNotExchangeableCredit (boolean NotExchangeableCredit)
{
set_Value ("NotExchangeableCredit", new Boolean(NotExchangeableCredit));
}
/** Get Not Exchangeable Credit */
public boolean isNotExchangeableCredit() 
{
Object oo = get_Value("NotExchangeableCredit");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Nro Identificacion del Cliente.
Número de DNI, Cédula, Libreta de Enrolamiento, Pasaporte o Libreta Cívica del cliente. */
public void setNroIdentificCliente (String NroIdentificCliente)
{
if (NroIdentificCliente != null && NroIdentificCliente.length() > 120)
{
log.warning("Length > 120 - truncated");
NroIdentificCliente = NroIdentificCliente.substring(0,120);
}
set_Value ("NroIdentificCliente", NroIdentificCliente);
}
/** Get Nro Identificacion del Cliente.
Número de DNI, Cédula, Libreta de Enrolamiento, Pasaporte o Libreta Cívica del cliente. */
public String getNroIdentificCliente() 
{
return (String)get_Value("NroIdentificCliente");
}
/** Set Numero Comprobante */
public void setNumeroComprobante (int NumeroComprobante)
{
set_Value ("NumeroComprobante", new Integer(NumeroComprobante));
}
/** Get Numero Comprobante */
public int getNumeroComprobante() 
{
Integer ii = (Integer)get_Value("NumeroComprobante");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Numero De Documento */
public void setNumeroDeDocumento (String NumeroDeDocumento)
{
if (NumeroDeDocumento != null && NumeroDeDocumento.length() > 30)
{
log.warning("Length > 30 - truncated");
NumeroDeDocumento = NumeroDeDocumento.substring(0,30);
}
set_Value ("NumeroDeDocumento", NumeroDeDocumento);
}
/** Get Numero De Documento */
public String getNumeroDeDocumento() 
{
return (String)get_Value("NumeroDeDocumento");
}
public static final int PAYMENTRULE_AD_Reference_ID = MReference.getReferenceID("All_Payment Rule");
/** Transfer = Tr */
public static final String PAYMENTRULE_Transfer = "Tr";
/** Credit Card = K */
public static final String PAYMENTRULE_CreditCard = "K";
/** Cash = B */
public static final String PAYMENTRULE_Cash = "B";
/** On Credit = P */
public static final String PAYMENTRULE_OnCredit = "P";
/** Check = S */
public static final String PAYMENTRULE_Check = "S";
/** Payment Check = PC */
public static final String PAYMENTRULE_PaymentCheck = "PC";
/** Direct Deposit = T */
public static final String PAYMENTRULE_DirectDeposit = "T";
/** Confirming = Cf */
public static final String PAYMENTRULE_Confirming = "Cf";
/** Direct Debit = D */
public static final String PAYMENTRULE_DirectDebit = "D";
/** Set Payment Rule.
How you pay the invoice */
public void setPaymentRule (String PaymentRule)
{
if (PaymentRule.equals("Tr") || PaymentRule.equals("K") || PaymentRule.equals("B") || PaymentRule.equals("P") || PaymentRule.equals("S") || PaymentRule.equals("PC") || PaymentRule.equals("T") || PaymentRule.equals("Cf") || PaymentRule.equals("D"));
 else throw new IllegalArgumentException ("PaymentRule Invalid value - Reference = PAYMENTRULE_AD_Reference_ID - Tr - K - B - P - S - PC - T - Cf - D");
if (PaymentRule == null) throw new IllegalArgumentException ("PaymentRule is mandatory");
if (PaymentRule.length() > 2)
{
log.warning("Length > 2 - truncated");
PaymentRule = PaymentRule.substring(0,2);
}
set_Value ("PaymentRule", PaymentRule);
}
/** Get Payment Rule.
How you pay the invoice */
public String getPaymentRule() 
{
return (String)get_Value("PaymentRule");
}
/** Set Order Reference.
Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner */
public void setPOReference (String POReference)
{
if (POReference != null && POReference.length() > 20)
{
log.warning("Length > 20 - truncated");
POReference = POReference.substring(0,20);
}
set_Value ("POReference", POReference);
}
/** Get Order Reference.
Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner */
public String getPOReference() 
{
return (String)get_Value("POReference");
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
public static final int PRINTTYPE_AD_Reference_ID = MReference.getReferenceID("Print Type");
/** Original = O */
public static final String PRINTTYPE_Original = "O";
/** Duplicate = D */
public static final String PRINTTYPE_Duplicate = "D";
/** Triplicate = T */
public static final String PRINTTYPE_Triplicate = "T";
/** Set Print Type */
public void setPrintType (String PrintType)
{
if (PrintType == null || PrintType.equals("O") || PrintType.equals("D") || PrintType.equals("T"));
 else throw new IllegalArgumentException ("PrintType Invalid value - Reference = PRINTTYPE_AD_Reference_ID - O - D - T");
if (PrintType != null && PrintType.length() > 1)
{
log.warning("Length > 1 - truncated");
PrintType = PrintType.substring(0,1);
}
set_Value ("PrintType", PrintType);
}
/** Get Print Type */
public String getPrintType() 
{
return (String)get_Value("PrintType");
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_ValueNoCheck ("Processed", new Boolean(Processed));
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
/** Set Punto De Venta */
public void setPuntoDeVenta (int PuntoDeVenta)
{
set_Value ("PuntoDeVenta", new Integer(PuntoDeVenta));
}
/** Get Punto De Venta */
public int getPuntoDeVenta() 
{
Integer ii = (Integer)get_Value("PuntoDeVenta");
if (ii == null) return 0;
return ii.intValue();
}
public static final int REF_INVOICE_ID_AD_Reference_ID = MReference.getReferenceID("C_Invoice");
/** Set Referenced Invoice */
public void setRef_Invoice_ID (int Ref_Invoice_ID)
{
if (Ref_Invoice_ID <= 0) set_Value ("Ref_Invoice_ID", null);
 else 
set_Value ("Ref_Invoice_ID", new Integer(Ref_Invoice_ID));
}
/** Get Referenced Invoice */
public int getRef_Invoice_ID() 
{
Integer ii = (Integer)get_Value("Ref_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SALESREP_ID_AD_Reference_ID = MReference.getReferenceID("AD_User - SalesRep");
/** Set Sales Representative.
Sales Representative or Company Agent */
public void setSalesRep_ID (int SalesRep_ID)
{
if (SalesRep_ID <= 0) set_Value ("SalesRep_ID", null);
 else 
set_Value ("SalesRep_ID", new Integer(SalesRep_ID));
}
/** Get Sales Representative.
Sales Representative or Company Agent */
public int getSalesRep_ID() 
{
Integer ii = (Integer)get_Value("SalesRep_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Send EMail.
Enable sending Document EMail */
public void setSendEMail (boolean SendEMail)
{
set_Value ("SendEMail", new Boolean(SendEMail));
}
/** Get Send EMail.
Enable sending Document EMail */
public boolean isSendEMail() 
{
Object oo = get_Value("SendEMail");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int TIPOCOMPROBANTE_AD_Reference_ID = MReference.getReferenceID("Tipo Comprobante");
/** Factura = FC */
public static final String TIPOCOMPROBANTE_Factura = "FC";
/** Nota de Débito = ND */
public static final String TIPOCOMPROBANTE_NotaDeDébito = "ND";
/** Nota de Crédito = NC */
public static final String TIPOCOMPROBANTE_NotaDeCrédito = "NC";
/** Set Tipo Comprobante */
public void setTipoComprobante (String TipoComprobante)
{
if (TipoComprobante == null || TipoComprobante.equals("FC") || TipoComprobante.equals("ND") || TipoComprobante.equals("NC"));
 else throw new IllegalArgumentException ("TipoComprobante Invalid value - Reference = TIPOCOMPROBANTE_AD_Reference_ID - FC - ND - NC");
if (TipoComprobante != null && TipoComprobante.length() > 2)
{
log.warning("Length > 2 - truncated");
TipoComprobante = TipoComprobante.substring(0,2);
}
set_Value ("TipoComprobante", TipoComprobante);
}
/** Get Tipo Comprobante */
public String getTipoComprobante() 
{
return (String)get_Value("TipoComprobante");
}
/** Set Total Lines.
Total of all document lines */
public void setTotalLines (BigDecimal TotalLines)
{
if (TotalLines == null) throw new IllegalArgumentException ("TotalLines is mandatory");
set_ValueNoCheck ("TotalLines", TotalLines);
}
/** Get Total Lines.
Total of all document lines */
public BigDecimal getTotalLines() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalLines");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Update Order Qty.
Update qty ordered in the related order  */
public void setUpdateOrderQty (boolean UpdateOrderQty)
{
set_Value ("UpdateOrderQty", new Boolean(UpdateOrderQty));
}
/** Get Update Order Qty.
Update qty ordered in the related order  */
public boolean isUpdateOrderQty() 
{
Object oo = get_Value("UpdateOrderQty");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int USER1_ID_AD_Reference_ID = MReference.getReferenceID("Account_ID - User1");
/** Set User1.
User defined element #1 */
public void setUser1_ID (int User1_ID)
{
if (User1_ID <= 0) set_Value ("User1_ID", null);
 else 
set_Value ("User1_ID", new Integer(User1_ID));
}
/** Get User1.
User defined element #1 */
public int getUser1_ID() 
{
Integer ii = (Integer)get_Value("User1_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int USER2_ID_AD_Reference_ID = MReference.getReferenceID("Account_ID - User2");
/** Set User2.
User defined element #2 */
public void setUser2_ID (int User2_ID)
{
if (User2_ID <= 0) set_Value ("User2_ID", null);
 else 
set_Value ("User2_ID", new Integer(User2_ID));
}
/** Get User2.
User defined element #2 */
public int getUser2_ID() 
{
Integer ii = (Integer)get_Value("User2_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set vtocae */
public void setvtocae (Timestamp vtocae)
{
set_Value ("vtocae", vtocae);
}
/** Get vtocae */
public Timestamp getvtocae() 
{
return (Timestamp)get_Value("vtocae");
}
}
