/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Invoice
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2022-04-11 13:50:53.217 */
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
setM_PriceList_ID (0);
setManageDragOrderDiscounts (false);
setManageDragOrderSurcharges (false);
setManualDocumentNo (false);
setNetAmount (Env.ZERO);
setNotExchangeableCredit (false);
setPaymentRule (null);	// P
setPosted (false);	// N
setProcessed (false);
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
/** Set ActualizarPreciosConFacturaDeCompra */
public void setActualizarPreciosConFacturaDeCompra (boolean ActualizarPreciosConFacturaDeCompra)
{
throw new IllegalArgumentException ("ActualizarPreciosConFacturaDeCompra is virtual column");
}
/** Get ActualizarPreciosConFacturaDeCompra */
public boolean isActualizarPreciosConFacturaDeCompra() 
{
Object oo = get_Value("ActualizarPreciosConFacturaDeCompra");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
public static final int AD_USER_ID_AD_Reference_ID = MReference.getReferenceID("AD_User");
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
/** Set Preload Invoice */
public void setPreloadInvoice (boolean PreloadInvoice)
{
set_Value ("PreloadInvoice", new Boolean(PreloadInvoice));
}
/** Get Preload Invoice */
public boolean isPreloadInvoice() 
{
Object oo = get_Value("PreloadInvoice");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
public static final int AUTHORIZATIONCHAINSTATUS_AD_Reference_ID = MReference.getReferenceID("Authorization Status");
/** Pending = P */
public static final String AUTHORIZATIONCHAINSTATUS_Pending = "P";
/** Authorized = A */
public static final String AUTHORIZATIONCHAINSTATUS_Authorized = "A";
/** Set Authorization Status */
public void setAuthorizationChainStatus (String AuthorizationChainStatus)
{
if (AuthorizationChainStatus == null || AuthorizationChainStatus.equals("P") || AuthorizationChainStatus.equals("A") || ( refContainsValue("CORE-AD_Reference-1010261", AuthorizationChainStatus) ) );
 else throw new IllegalArgumentException ("AuthorizationChainStatus Invalid value: " + AuthorizationChainStatus + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010261") );
if (AuthorizationChainStatus != null && AuthorizationChainStatus.length() > 1)
{
log.warning("Length > 1 - truncated");
AuthorizationChainStatus = AuthorizationChainStatus.substring(0,1);
}
set_Value ("AuthorizationChainStatus", AuthorizationChainStatus);
}
/** Get Authorization Status */
public String getAuthorizationChainStatus() 
{
return (String)get_Value("AuthorizationChainStatus");
}
/** Set Authorize */
public void setAuthorize (String Authorize)
{
if (Authorize != null && Authorize.length() > 1)
{
log.warning("Length > 1 - truncated");
Authorize = Authorize.substring(0,1);
}
set_Value ("Authorize", Authorize);
}
/** Get Authorize */
public String getAuthorize() 
{
return (String)get_Value("Authorize");
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
public static final int C_BPARTNER_LOCATION_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner Location");
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
public static final int C_ORDER_ORIG_ID_AD_Reference_ID = MReference.getReferenceID("C_Order");
/** Set Original Order */
public void setC_Order_Orig_ID (int C_Order_Orig_ID)
{
if (C_Order_Orig_ID <= 0) set_Value ("C_Order_Orig_ID", null);
 else 
set_Value ("C_Order_Orig_ID", new Integer(C_Order_Orig_ID));
}
/** Get Original Order */
public int getC_Order_Orig_ID() 
{
Integer ii = (Integer)get_Value("C_Order_Orig_ID");
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
public static final int C_POSJOURNAL_ID_AD_Reference_ID = MReference.getReferenceID("C_POSJournal");
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
public static final int C_POSPAYMENTMEDIUM_ID_AD_Reference_ID = MReference.getReferenceID("C_POSPaymentMedium");
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
public static final int C_REGION_ID_AD_Reference_ID = MReference.getReferenceID("C_Region");
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
/** Set CAT_Iva_ID */
public void setCAT_Iva_ID (int CAT_Iva_ID)
{
if (CAT_Iva_ID <= 0) set_Value ("CAT_Iva_ID", null);
 else 
set_Value ("CAT_Iva_ID", new Integer(CAT_Iva_ID));
}
/** Get CAT_Iva_ID */
public int getCAT_Iva_ID() 
{
Integer ii = (Integer)get_Value("CAT_Iva_ID");
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
/** Set CP */
public void setCP (String CP)
{
if (CP != null && CP.length() > 8)
{
log.warning("Length > 8 - truncated");
CP = CP.substring(0,8);
}
set_Value ("CP", CP);
}
/** Get CP */
public String getCP() 
{
return (String)get_Value("CP");
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
/** Set Date recepted */
public void setDateRecepted (Timestamp DateRecepted)
{
set_Value ("DateRecepted", DateRecepted);
}
/** Get Date recepted */
public Timestamp getDateRecepted() 
{
return (Timestamp)get_Value("DateRecepted");
}
public static final int DELIVERYVIARULE_AD_Reference_ID = MReference.getReferenceID("C_Order DeliveryViaRule");
/** Pickup = P */
public static final String DELIVERYVIARULE_Pickup = "P";
/** Shipper = S */
public static final String DELIVERYVIARULE_Shipper = "S";
/** Delivery = D */
public static final String DELIVERYVIARULE_Delivery = "D";
/** Set Delivery Via.
How the order will be delivered */
public void setDeliveryViaRule (String DeliveryViaRule)
{
if (DeliveryViaRule == null || DeliveryViaRule.equals("P") || DeliveryViaRule.equals("S") || DeliveryViaRule.equals("D") || ( refContainsValue("CORE-AD_Reference-152", DeliveryViaRule) ) );
 else throw new IllegalArgumentException ("DeliveryViaRule Invalid value: " + DeliveryViaRule + ".  Valid: " +  refValidOptions("CORE-AD_Reference-152") );
if (DeliveryViaRule != null && DeliveryViaRule.length() > 1)
{
log.warning("Length > 1 - truncated");
DeliveryViaRule = DeliveryViaRule.substring(0,1);
}
set_Value ("DeliveryViaRule", DeliveryViaRule);
}
/** Get Delivery Via.
How the order will be delivered */
public String getDeliveryViaRule() 
{
return (String)get_Value("DeliveryViaRule");
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
/** Set Direccion */
public void setDireccion (String Direccion)
{
if (Direccion != null && Direccion.length() > 20)
{
log.warning("Length > 20 - truncated");
Direccion = Direccion.substring(0,20);
}
set_Value ("Direccion", Direccion);
}
/** Get Direccion */
public String getDireccion() 
{
return (String)get_Value("Direccion");
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
if (DocAction.equals("AP") || DocAction.equals("CL") || DocAction.equals("PR") || DocAction.equals("IN") || DocAction.equals("CO") || DocAction.equals("--") || DocAction.equals("RC") || DocAction.equals("RJ") || DocAction.equals("RA") || DocAction.equals("WC") || DocAction.equals("XL") || DocAction.equals("RE") || DocAction.equals("PO") || DocAction.equals("VO") || ( refContainsValue("CORE-AD_Reference-135", DocAction) ) );
 else throw new IllegalArgumentException ("DocAction Invalid value: " + DocAction + ".  Valid: " +  refValidOptions("CORE-AD_Reference-135") );
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
if (DocStatus.equals("VO") || DocStatus.equals("NA") || DocStatus.equals("IP") || DocStatus.equals("CO") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("WC") || DocStatus.equals("WP") || DocStatus.equals("??") || DocStatus.equals("DR") || DocStatus.equals("IN") || DocStatus.equals("RE") || ( refContainsValue("CORE-AD_Reference-131", DocStatus) ) );
 else throw new IllegalArgumentException ("DocStatus Invalid value: " + DocStatus + ".  Valid: " +  refValidOptions("CORE-AD_Reference-131") );
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
/** Set FechadeTCparaActualizarPrecios.
Fecha de TC para Actualizar Precios de Tarifa */
public void setFechadeTCparaActualizarPrecios (Timestamp FechadeTCparaActualizarPrecios)
{
set_Value ("FechadeTCparaActualizarPrecios", FechadeTCparaActualizarPrecios);
}
/** Get FechadeTCparaActualizarPrecios.
Fecha de TC para Actualizar Precios de Tarifa */
public Timestamp getFechadeTCparaActualizarPrecios() 
{
return (Timestamp)get_Value("FechadeTCparaActualizarPrecios");
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
set_Value ("GrandTotal", GrandTotal);
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
/** Set Import Clearance */
public void setImportClearance (String ImportClearance)
{
if (ImportClearance != null && ImportClearance.length() > 30)
{
log.warning("Length > 30 - truncated");
ImportClearance = ImportClearance.substring(0,30);
}
set_Value ("ImportClearance", ImportClearance);
}
/** Get Import Clearance */
public String getImportClearance() 
{
return (String)get_Value("ImportClearance");
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
/** Set Localidad */
public void setLocalidad (String Localidad)
{
if (Localidad != null && Localidad.length() > 40)
{
log.warning("Length > 40 - truncated");
Localidad = Localidad.substring(0,40);
}
set_Value ("Localidad", Localidad);
}
/** Get Localidad */
public String getLocalidad() 
{
return (String)get_Value("Localidad");
}
/** Set Authorization Chain */
public void setM_AuthorizationChain_ID (int M_AuthorizationChain_ID)
{
if (M_AuthorizationChain_ID <= 0) set_Value ("M_AuthorizationChain_ID", null);
 else 
set_Value ("M_AuthorizationChain_ID", new Integer(M_AuthorizationChain_ID));
}
/** Get Authorization Chain */
public int getM_AuthorizationChain_ID() 
{
Integer ii = (Integer)get_Value("M_AuthorizationChain_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_INOUTTRANSPORT_ID_AD_Reference_ID = MReference.getReferenceID("M_InOut");
/** Set Transport In Out */
public void setM_InOutTransport_ID (int M_InOutTransport_ID)
{
if (M_InOutTransport_ID <= 0) set_Value ("M_InOutTransport_ID", null);
 else 
set_Value ("M_InOutTransport_ID", new Integer(M_InOutTransport_ID));
}
/** Get Transport In Out */
public int getM_InOutTransport_ID() 
{
Integer ii = (Integer)get_Value("M_InOutTransport_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int M_RMA_ID_AD_Reference_ID = MReference.getReferenceID("RMA");
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
/** Set Manage Drag Order Surcharges */
public void setManageDragOrderSurcharges (boolean ManageDragOrderSurcharges)
{
set_Value ("ManageDragOrderSurcharges", new Boolean(ManageDragOrderSurcharges));
}
/** Get Manage Drag Order Surcharges */
public boolean isManageDragOrderSurcharges() 
{
Object oo = get_Value("ManageDragOrderSurcharges");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set ManageElectronicInvoice */
public void setManageElectronicInvoice (String ManageElectronicInvoice)
{
if (ManageElectronicInvoice != null && ManageElectronicInvoice.length() > 1)
{
log.warning("Length > 1 - truncated");
ManageElectronicInvoice = ManageElectronicInvoice.substring(0,1);
}
set_Value ("ManageElectronicInvoice", ManageElectronicInvoice);
}
/** Get ManageElectronicInvoice */
public String getManageElectronicInvoice() 
{
return (String)get_Value("ManageElectronicInvoice");
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
set_Value ("ManualGeneralDiscount", ManualGeneralDiscount);
}
/** Get Manual General Discount */
public BigDecimal getManualGeneralDiscount() 
{
BigDecimal bd = (BigDecimal)get_Value("ManualGeneralDiscount");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set OldGrandTotal */
public void setOldGrandTotal (BigDecimal OldGrandTotal)
{
set_Value ("OldGrandTotal", OldGrandTotal);
}
/** Get OldGrandTotal */
public BigDecimal getOldGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("OldGrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set OrigInvFecha */
public void setOrigInvFecha (Timestamp OrigInvFecha)
{
set_Value ("OrigInvFecha", OrigInvFecha);
}
/** Get OrigInvFecha */
public Timestamp getOrigInvFecha() 
{
return (Timestamp)get_Value("OrigInvFecha");
}
/** Set OrigInvNro */
public void setOrigInvNro (int OrigInvNro)
{
set_Value ("OrigInvNro", new Integer(OrigInvNro));
}
/** Get OrigInvNro */
public int getOrigInvNro() 
{
Integer ii = (Integer)get_Value("OrigInvNro");
if (ii == null) return 0;
return ii.intValue();
}
/** Set OrigInvPtoVta */
public void setOrigInvPtoVta (int OrigInvPtoVta)
{
set_Value ("OrigInvPtoVta", new Integer(OrigInvPtoVta));
}
/** Get OrigInvPtoVta */
public int getOrigInvPtoVta() 
{
Integer ii = (Integer)get_Value("OrigInvPtoVta");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ORIGINVTIPO_AD_Reference_ID = MReference.getReferenceID("DocSubTypeCae");
/** Facturas A = 01 */
public static final String ORIGINVTIPO_FacturasA = "01";
/** Notas de Debito A = 02 */
public static final String ORIGINVTIPO_NotasDeDebitoA = "02";
/** Notas de Credito A = 03 */
public static final String ORIGINVTIPO_NotasDeCreditoA = "03";
/** Recibos A = 04 */
public static final String ORIGINVTIPO_RecibosA = "04";
/** Notas de Venta al Contado A = 05 */
public static final String ORIGINVTIPO_NotasDeVentaAlContadoA = "05";
/** Facturas B = 06 */
public static final String ORIGINVTIPO_FacturasB = "06";
/** Notas de Debito B = 07 */
public static final String ORIGINVTIPO_NotasDeDebitoB = "07";
/** Notas de Credito B = 08 */
public static final String ORIGINVTIPO_NotasDeCreditoB = "08";
/** Recibos B = 09 */
public static final String ORIGINVTIPO_RecibosB = "09";
/** Notas de Venta al contado B = 10 */
public static final String ORIGINVTIPO_NotasDeVentaAlContadoB = "10";
/** Nota de Débito por Operaciones en el Exterior = 20 */
public static final String ORIGINVTIPO_NotaDeDébitoPorOperacionesEnElExterior = "20";
/** Factura de Exportación E = 19 */
public static final String ORIGINVTIPO_FacturaDeExportaciónE = "19";
/** Nota de Crédito por Operaciones en el Exterior = 21 */
public static final String ORIGINVTIPO_NotaDeCréditoPorOperacionesEnElExterior = "21";
/** Facturas C = 11 */
public static final String ORIGINVTIPO_FacturasC = "11";
/** Notas de Credito C = 13 */
public static final String ORIGINVTIPO_NotasDeCreditoC = "13";
/** Notas de Debito C = 12 */
public static final String ORIGINVTIPO_NotasDeDebitoC = "12";
/** Notas de Venta al contado C = 16 */
public static final String ORIGINVTIPO_NotasDeVentaAlContadoC = "16";
/** Recibos C = 15 */
public static final String ORIGINVTIPO_RecibosC = "15";
/** Notas de Debito MiPyME A = 202 */
public static final String ORIGINVTIPO_NotasDeDebitoMiPyMEA = "202";
/** Notas de Debito MiPyME B = 207 */
public static final String ORIGINVTIPO_NotasDeDebitoMiPyMEB = "207";
/** Notas de Debito MiPyME C = 212 */
public static final String ORIGINVTIPO_NotasDeDebitoMiPyMEC = "212";
/** Notas de Credito MiPyME A = 203 */
public static final String ORIGINVTIPO_NotasDeCreditoMiPyMEA = "203";
/** Notas de Credito MiPyME B = 208 */
public static final String ORIGINVTIPO_NotasDeCreditoMiPyMEB = "208";
/** Notas de Credito MiPyME C = 213 */
public static final String ORIGINVTIPO_NotasDeCreditoMiPyMEC = "213";
/** Facturas M = 51 */
public static final String ORIGINVTIPO_FacturasM = "51";
/** Notas de Debito M = 52 */
public static final String ORIGINVTIPO_NotasDeDebitoM = "52";
/** Notas de Credito M = 53 */
public static final String ORIGINVTIPO_NotasDeCreditoM = "53";
/** Facturas de Exportacion Simplificado = 22 */
public static final String ORIGINVTIPO_FacturasDeExportacionSimplificado = "22";
/** Facturas MiPyME A = 201 */
public static final String ORIGINVTIPO_FacturasMiPyMEA = "201";
/** Facturas MiPyME B = 206 */
public static final String ORIGINVTIPO_FacturasMiPyMEB = "206";
/** Facturas MiPyME C = 211 */
public static final String ORIGINVTIPO_FacturasMiPyMEC = "211";
/** Liquidacion de Servicios Publicos A = 17 */
public static final String ORIGINVTIPO_LiquidacionDeServiciosPublicosA = "17";
/** Liquidacion de Servicios Publicos B = 18 */
public static final String ORIGINVTIPO_LiquidacionDeServiciosPublicosB = "18";
/** Cptes A Compra Sector Pesquero Maritimo = 23 */
public static final String ORIGINVTIPO_CptesACompraSectorPesqueroMaritimo = "23";
/** Cptes B Compra Sector Pesquero Maritimo = 25 */
public static final String ORIGINVTIPO_CptesBCompraSectorPesqueroMaritimo = "25";
/** Liquidacion Comercial Impositiva A = 27 */
public static final String ORIGINVTIPO_LiquidacionComercialImpositivaA = "27";
/** Liquidacion Comercial Impositiva B = 28 */
public static final String ORIGINVTIPO_LiquidacionComercialImpositivaB = "28";
/** Cbtes Compra Bienes Usados = 30 */
public static final String ORIGINVTIPO_CbtesCompraBienesUsados = "30";
/** Cbtes para Reciclar Materiales = 32 */
public static final String ORIGINVTIPO_CbtesParaReciclarMateriales = "32";
/** Liquidacion Primaria de Granos = 33 */
public static final String ORIGINVTIPO_LiquidacionPrimariaDeGranos = "33";
/** Cbtes A Apartado A Inciso F RGN 1415 = 34 */
public static final String ORIGINVTIPO_CbtesAApartadoAIncisoFRGN1415 = "34";
/** Cbtes B Anexo I Apartado A Inc. F RGN 1415 = 35 */
public static final String ORIGINVTIPO_CbtesBAnexoIApartadoAIncFRGN1415 = "35";
/** Cbtes C Anexo I Apartado A Inc. F RGN 1415 = 36 */
public static final String ORIGINVTIPO_CbtesCAnexoIApartadoAIncFRGN1415 = "36";
/** Nota de Debito RGN 1415 = 37 */
public static final String ORIGINVTIPO_NotaDeDebitoRGN1415 = "37";
/** Nota de Credito RGN 1415 = 38 */
public static final String ORIGINVTIPO_NotaDeCreditoRGN1415 = "38";
/** Otros Cbtes A RGN 1415 = 39 */
public static final String ORIGINVTIPO_OtrosCbtesARGN1415 = "39";
/** Otros Cbtes B RGN 1415 = 40 */
public static final String ORIGINVTIPO_OtrosCbtesBRGN1415 = "40";
/** Otros Cbtes C RGN 1415 = 41 */
public static final String ORIGINVTIPO_OtrosCbtesCRGN1415 = "41";
/** Nota de Credito Liquidacion Impositiva B = 43 */
public static final String ORIGINVTIPO_NotaDeCreditoLiquidacionImpositivaB = "43";
/** Nota de Debito Liquidacion Impositiva A = 45 */
public static final String ORIGINVTIPO_NotaDeDebitoLiquidacionImpositivaA = "45";
/** Nota de Debito Liquidacion Impositiva B = 46 */
public static final String ORIGINVTIPO_NotaDeDebitoLiquidacionImpositivaB = "46";
/** Nota de Credito Liquidacion Impositiva A = 48 */
public static final String ORIGINVTIPO_NotaDeCreditoLiquidacionImpositivaA = "48";
/** Cbtes Compra Bienes a CF = 49 */
public static final String ORIGINVTIPO_CbtesCompraBienesACF = "49";
/** Recibo Factura A Reg. Factura de Credito = 50 */
public static final String ORIGINVTIPO_ReciboFacturaARegFacturaDeCredito = "50";
/** Recibos M = 54 */
public static final String ORIGINVTIPO_RecibosM = "54";
/** Nota de Venta al Contado M = 55 */
public static final String ORIGINVTIPO_NotaDeVentaAlContadoM = "55";
/** Cbtes M Anexo I Apartado A Inc. F RGN 1415 = 56 */
public static final String ORIGINVTIPO_CbtesMAnexoIApartadoAIncFRGN1415 = "56";
/** Otros Cbtes M RGN 1415 = 57 */
public static final String ORIGINVTIPO_OtrosCbtesMRGN1415 = "57";
/** Cuentas de Venta y Liquido Prod. M = 58 */
public static final String ORIGINVTIPO_CuentasDeVentaYLiquidoProdM = "58";
/** Liquidaciones M = 59 */
public static final String ORIGINVTIPO_LiquidacionesM = "59";
/** Cuentas de Venta y Liquido Prod. A = 60 */
public static final String ORIGINVTIPO_CuentasDeVentaYLiquidoProdA = "60";
/** Cuentas de Venta y Liquido Prod. B = 61 */
public static final String ORIGINVTIPO_CuentasDeVentaYLiquidoProdB = "61";
/** Liquidaciones A = 63 */
public static final String ORIGINVTIPO_LiquidacionesA = "63";
/** Liquidaciones B = 64 */
public static final String ORIGINVTIPO_LiquidacionesB = "64";
/** Despacho de Importacion = 66 */
public static final String ORIGINVTIPO_DespachoDeImportacion = "66";
/** Recibo Factura de Credito = 70 */
public static final String ORIGINVTIPO_ReciboFacturaDeCredito = "70";
/** Tique Factura A = 81 */
public static final String ORIGINVTIPO_TiqueFacturaA = "81";
/** Tique Factura B = 82 */
public static final String ORIGINVTIPO_TiqueFacturaB = "82";
/** Tique = 83 */
public static final String ORIGINVTIPO_Tique = "83";
/** Nota de Credito No Cumplen RGN 1415 = 90 */
public static final String ORIGINVTIPO_NotaDeCreditoNoCumplenRGN1415 = "90";
/** Otros Cbtes No Cumplen RGN 1415 = 99 */
public static final String ORIGINVTIPO_OtrosCbtesNoCumplenRGN1415 = "99";
/** Tique Nota de Credito = 110 */
public static final String ORIGINVTIPO_TiqueNotaDeCredito = "110";
/** Tique Factura C = 111 */
public static final String ORIGINVTIPO_TiqueFacturaC = "111";
/** Tique Nota de Credito A = 112 */
public static final String ORIGINVTIPO_TiqueNotaDeCreditoA = "112";
/** Tique Nota de Credito B = 113 */
public static final String ORIGINVTIPO_TiqueNotaDeCreditoB = "113";
/** Tique Nota de Credito C = 114 */
public static final String ORIGINVTIPO_TiqueNotaDeCreditoC = "114";
/** Tique Nota de Debito A = 115 */
public static final String ORIGINVTIPO_TiqueNotaDeDebitoA = "115";
/** Tique Nota de Debito B = 116 */
public static final String ORIGINVTIPO_TiqueNotaDeDebitoB = "116";
/** Tique Nota de Debito C = 117 */
public static final String ORIGINVTIPO_TiqueNotaDeDebitoC = "117";
/** Tique Factura M = 118 */
public static final String ORIGINVTIPO_TiqueFacturaM = "118";
/** Tique Nota de Credito M = 119 */
public static final String ORIGINVTIPO_TiqueNotaDeCreditoM = "119";
/** Tique Nota de Debito M = 120 */
public static final String ORIGINVTIPO_TiqueNotaDeDebitoM = "120";
/** Liquidacion Secundaria de Granos = 331 */
public static final String ORIGINVTIPO_LiquidacionSecundariaDeGranos = "331";
/** Liquidacion Deposito Granos en Planta = 332 */
public static final String ORIGINVTIPO_LiquidacionDepositoGranosEnPlanta = "332";
/** Cptes A Consignacion Sector Pesquero Maritimo = 24 */
public static final String ORIGINVTIPO_CptesAConsignacionSectorPesqueroMaritimo = "24";
/** Cptes B Consignacion Sector Pesquero Maritimo = 26 */
public static final String ORIGINVTIPO_CptesBConsignacionSectorPesqueroMaritimo = "26";
/** Remito R = 91 */
public static final String ORIGINVTIPO_RemitoR = "91";
/** Set OrigInvTipo */
public void setOrigInvTipo (String OrigInvTipo)
{
if (OrigInvTipo == null || OrigInvTipo.equals("01") || OrigInvTipo.equals("02") || OrigInvTipo.equals("03") || OrigInvTipo.equals("04") || OrigInvTipo.equals("05") || OrigInvTipo.equals("06") || OrigInvTipo.equals("07") || OrigInvTipo.equals("08") || OrigInvTipo.equals("09") || OrigInvTipo.equals("10") || OrigInvTipo.equals("20") || OrigInvTipo.equals("19") || OrigInvTipo.equals("21") || OrigInvTipo.equals("11") || OrigInvTipo.equals("13") || OrigInvTipo.equals("12") || OrigInvTipo.equals("16") || OrigInvTipo.equals("15") || OrigInvTipo.equals("202") || OrigInvTipo.equals("207") || OrigInvTipo.equals("212") || OrigInvTipo.equals("203") || OrigInvTipo.equals("208") || OrigInvTipo.equals("213") || OrigInvTipo.equals("51") || OrigInvTipo.equals("52") || OrigInvTipo.equals("53") || OrigInvTipo.equals("22") || OrigInvTipo.equals("201") || OrigInvTipo.equals("206") || OrigInvTipo.equals("211") || OrigInvTipo.equals("17") || OrigInvTipo.equals("18") || OrigInvTipo.equals("23") || OrigInvTipo.equals("25") || OrigInvTipo.equals("27") || OrigInvTipo.equals("28") || OrigInvTipo.equals("30") || OrigInvTipo.equals("32") || OrigInvTipo.equals("33") || OrigInvTipo.equals("34") || OrigInvTipo.equals("35") || OrigInvTipo.equals("36") || OrigInvTipo.equals("37") || OrigInvTipo.equals("38") || OrigInvTipo.equals("39") || OrigInvTipo.equals("40") || OrigInvTipo.equals("41") || OrigInvTipo.equals("43") || OrigInvTipo.equals("45") || OrigInvTipo.equals("46") || OrigInvTipo.equals("48") || OrigInvTipo.equals("49") || OrigInvTipo.equals("50") || OrigInvTipo.equals("54") || OrigInvTipo.equals("55") || OrigInvTipo.equals("56") || OrigInvTipo.equals("57") || OrigInvTipo.equals("58") || OrigInvTipo.equals("59") || OrigInvTipo.equals("60") || OrigInvTipo.equals("61") || OrigInvTipo.equals("63") || OrigInvTipo.equals("64") || OrigInvTipo.equals("66") || OrigInvTipo.equals("70") || OrigInvTipo.equals("81") || OrigInvTipo.equals("82") || OrigInvTipo.equals("83") || OrigInvTipo.equals("90") || OrigInvTipo.equals("99") || OrigInvTipo.equals("110") || OrigInvTipo.equals("111") || OrigInvTipo.equals("112") || OrigInvTipo.equals("113") || OrigInvTipo.equals("114") || OrigInvTipo.equals("115") || OrigInvTipo.equals("116") || OrigInvTipo.equals("117") || OrigInvTipo.equals("118") || OrigInvTipo.equals("119") || OrigInvTipo.equals("120") || OrigInvTipo.equals("331") || OrigInvTipo.equals("332") || OrigInvTipo.equals("24") || OrigInvTipo.equals("26") || OrigInvTipo.equals("91") || ( refContainsValue("CORE-AD_Reference-1010096", OrigInvTipo) ) );
 else throw new IllegalArgumentException ("OrigInvTipo Invalid value: " + OrigInvTipo + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010096") );
if (OrigInvTipo != null && OrigInvTipo.length() > 10)
{
log.warning("Length > 10 - truncated");
OrigInvTipo = OrigInvTipo.substring(0,10);
}
set_Value ("OrigInvTipo", OrigInvTipo);
}
/** Get OrigInvTipo */
public String getOrigInvTipo() 
{
return (String)get_Value("OrigInvTipo");
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
/** Payment Check = PC */
public static final String PAYMENTRULE_PaymentCheck = "PC";
/** Direct Deposit = T */
public static final String PAYMENTRULE_DirectDeposit = "T";
/** Confirming = Cf */
public static final String PAYMENTRULE_Confirming = "Cf";
/** Direct Debit = D */
public static final String PAYMENTRULE_DirectDebit = "D";
/** Check = S */
public static final String PAYMENTRULE_Check = "S";
/** Set Payment Rule.
How you pay the invoice */
public void setPaymentRule (String PaymentRule)
{
if (PaymentRule.equals("Tr") || PaymentRule.equals("K") || PaymentRule.equals("B") || PaymentRule.equals("P") || PaymentRule.equals("PC") || PaymentRule.equals("T") || PaymentRule.equals("Cf") || PaymentRule.equals("D") || PaymentRule.equals("S") || ( refContainsValue("CORE-AD_Reference-195", PaymentRule) ) );
 else throw new IllegalArgumentException ("PaymentRule Invalid value: " + PaymentRule + ".  Valid: " +  refValidOptions("CORE-AD_Reference-195") );
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
/** Set PriceListCurrency */
public void setPriceListCurrency (int PriceListCurrency)
{
throw new IllegalArgumentException ("PriceListCurrency is virtual column");
}
/** Get PriceListCurrency */
public int getPriceListCurrency() 
{
Integer ii = (Integer)get_Value("PriceListCurrency");
if (ii == null) return 0;
return ii.intValue();
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
if (PrintType == null || PrintType.equals("O") || PrintType.equals("D") || PrintType.equals("T") || ( refContainsValue("CORE-AD_Reference-1010235", PrintType) ) );
 else throw new IllegalArgumentException ("PrintType Invalid value: " + PrintType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010235") );
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
/** Set provincia */
public void setprovincia (String provincia)
{
if (provincia != null && provincia.length() > 40)
{
log.warning("Length > 40 - truncated");
provincia = provincia.substring(0,40);
}
set_Value ("provincia", provincia);
}
/** Get provincia */
public String getprovincia() 
{
return (String)get_Value("provincia");
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
/** Set SkipIPNoCaeValidation */
public void setSkipIPNoCaeValidation (boolean SkipIPNoCaeValidation)
{
set_Value ("SkipIPNoCaeValidation", new Boolean(SkipIPNoCaeValidation));
}
/** Get SkipIPNoCaeValidation */
public boolean isSkipIPNoCaeValidation() 
{
Object oo = get_Value("SkipIPNoCaeValidation");
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
if (TipoComprobante == null || TipoComprobante.equals("FC") || TipoComprobante.equals("ND") || TipoComprobante.equals("NC") || ( refContainsValue("CORE-AD_Reference-1010202", TipoComprobante) ) );
 else throw new IllegalArgumentException ("TipoComprobante Invalid value: " + TipoComprobante + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010202") );
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

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO C_Invoice(Ref_Invoice_ID,IsPayScheduleValid,vtocae,C_Invoice_ID,cae,idcae,caeerror,C_Activity_ID,Description,DocAction,Posted,C_Order_ID,C_DocType_ID,GenerateTo,C_Campaign_ID,DocStatus,POReference,DateOrdered,Created,C_Currency_ID,IsTransferred,C_Project_ID,C_Payment_ID,TotalLines,IsPaid,DateAcct,AD_Client_ID,Processing,IsActive,UpdatedBy,DatePrinted,IsSOTrx,C_Charge_ID,CopyFrom,Processed,Updated,IsSelfService,IsDiscountPrinted,AD_OrgTrx_ID,SalesRep_ID,ChargeAmt,IsTaxIncluded,IsInDispute,SendEMail,C_CashLine_ID,C_ConversionType_ID,IsPrinted,NumeroDeDocumento,Authorize,C_Invoice_Orig_ID,OldGrandTotal,C_Letra_Comprobante_ID,DateInvoiced,IsVoidable,CAI,CUIT,Caja,DateCAI,FiscalAlreadyPrinted,M_PriceList_ID,PaymentRule,caecbte,CreateCashLine,IsCopy,AuthCode,AuthMatch,C_POSJournal_ID,IsApproved,TipoComprobante,IsExchange,NotExchangeableCredit,InitialCurrentAccountAmt,C_POSPaymentMedium_ID,ManageDragOrderDiscounts,ManualDocumentNo,ApplyPercepcion,M_RMA_ID,NetAmount,C_POSPaymentMedium_Credit_ID,FiscalDescription,PrintType,C_Region_Delivery_ID,UpdateOrderQty,DateRecepted,ImportClearance,SkipIPNoCaeValidation,ManageElectronicInvoice,M_InOutTransport_ID,AD_User_ID,C_BPartner_Location_ID,CreatedBy,C_Region_ID,User1_ID,User2_ID,AuthorizationChainStatus,FechadeTCparaActualizarPrecios,ManageDragOrderSurcharges,Invoice_Adress,NombreCli,NroIdentificCliente,C_BPartner_ID,C_DocTypeTarget_ID,DocumentNo,AD_Org_ID,NumeroComprobante,C_PaymentTerm_ID,CreateFrom,C_Order_Orig_ID,M_AuthorizationChain_ID,ManualGeneralDiscount,DeliveryViaRule,GrandTotal,PuntoDeVenta,OrigInvFecha,OrigInvNro,OrigInvPtoVta,OrigInvTipo,Direccion,Localidad,provincia,CP,CAT_Iva_ID," + getAdditionalParamNames() + ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," + getAdditionalParamMarks() + ") ";

		 if (getRef_Invoice_ID() == 0) sql = sql.replaceFirst("Ref_Invoice_ID,","").replaceFirst("\\?,", "");
 		 if (getvtocae() == null) sql = sql.replaceFirst("vtocae,","").replaceFirst("\\?,", "");
 		 if (getcae() == null) sql = sql.replaceFirst("cae,","").replaceFirst("\\?,", "");
 		 if (getidcae() == null) sql = sql.replaceFirst("idcae,","").replaceFirst("\\?,", "");
 		 if (getcaeerror() == null) sql = sql.replaceFirst("caeerror,","").replaceFirst("\\?,", "");
 		 if (getC_Activity_ID() == 0) sql = sql.replaceFirst("C_Activity_ID,","").replaceFirst("\\?,", "");
 		 if (getDescription() == null) sql = sql.replaceFirst("Description,","").replaceFirst("\\?,", "");
 		 if (getDocAction() == null) sql = sql.replaceFirst("DocAction,","").replaceFirst("\\?,", "");
 		 if (getC_Order_ID() == 0) sql = sql.replaceFirst("C_Order_ID,","").replaceFirst("\\?,", "");
 		 if (getGenerateTo() == null) sql = sql.replaceFirst("GenerateTo,","").replaceFirst("\\?,", "");
 		 if (getC_Campaign_ID() == 0) sql = sql.replaceFirst("C_Campaign_ID,","").replaceFirst("\\?,", "");
 		 if (getDocStatus() == null) sql = sql.replaceFirst("DocStatus,","").replaceFirst("\\?,", "");
 		 if (getPOReference() == null) sql = sql.replaceFirst("POReference,","").replaceFirst("\\?,", "");
 		 if (getDateOrdered() == null) sql = sql.replaceFirst("DateOrdered,","").replaceFirst("\\?,", "");
 		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getC_Project_ID() == 0) sql = sql.replaceFirst("C_Project_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Payment_ID() == 0) sql = sql.replaceFirst("C_Payment_ID,","").replaceFirst("\\?,", "");
 		 if (getTotalLines() == null) sql = sql.replaceFirst("TotalLines,","").replaceFirst("\\?,", "");
 		 if (getDateAcct() == null) sql = sql.replaceFirst("DateAcct,","").replaceFirst("\\?,", "");
 		 if (getDatePrinted() == null) sql = sql.replaceFirst("DatePrinted,","").replaceFirst("\\?,", "");
 		 if (getC_Charge_ID() == 0) sql = sql.replaceFirst("C_Charge_ID,","").replaceFirst("\\?,", "");
 		 if (getCopyFrom() == null) sql = sql.replaceFirst("CopyFrom,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getAD_OrgTrx_ID() == 0) sql = sql.replaceFirst("AD_OrgTrx_ID,","").replaceFirst("\\?,", "");
 		 if (getSalesRep_ID() == 0) sql = sql.replaceFirst("SalesRep_ID,","").replaceFirst("\\?,", "");
 		 if (getChargeAmt() == null) sql = sql.replaceFirst("ChargeAmt,","").replaceFirst("\\?,", "");
 		 if (getC_CashLine_ID() == 0) sql = sql.replaceFirst("C_CashLine_ID,","").replaceFirst("\\?,", "");
 		 if (getC_ConversionType_ID() == 0) sql = sql.replaceFirst("C_ConversionType_ID,","").replaceFirst("\\?,", "");
 		 if (getNumeroDeDocumento() == null) sql = sql.replaceFirst("NumeroDeDocumento,","").replaceFirst("\\?,", "");
 		 if (getAuthorize() == null) sql = sql.replaceFirst("Authorize,","").replaceFirst("\\?,", "");
 		 if (getC_Invoice_Orig_ID() == 0) sql = sql.replaceFirst("C_Invoice_Orig_ID,","").replaceFirst("\\?,", "");
 		 if (getOldGrandTotal() == null) sql = sql.replaceFirst("OldGrandTotal,","").replaceFirst("\\?,", "");
 		 if (getC_Letra_Comprobante_ID() == 0) sql = sql.replaceFirst("C_Letra_Comprobante_ID,","").replaceFirst("\\?,", "");
 		 if (getDateInvoiced() == null) sql = sql.replaceFirst("DateInvoiced,","").replaceFirst("\\?,", "");
 		 if (getCAI() == null) sql = sql.replaceFirst("CAI,","").replaceFirst("\\?,", "");
 		 if (getCUIT() == null) sql = sql.replaceFirst("CUIT,","").replaceFirst("\\?,", "");
 		 if (getCaja() == null) sql = sql.replaceFirst("Caja,","").replaceFirst("\\?,", "");
 		 if (getDateCAI() == null) sql = sql.replaceFirst("DateCAI,","").replaceFirst("\\?,", "");
 		 if (getPaymentRule() == null) sql = sql.replaceFirst("PaymentRule,","").replaceFirst("\\?,", "");
 		 if (getcaecbte() == 0) sql = sql.replaceFirst("caecbte,","").replaceFirst("\\?,", "");
 		 if (getAuthCode() == null) sql = sql.replaceFirst("AuthCode,","").replaceFirst("\\?,", "");
 		 if (getC_POSJournal_ID() == 0) sql = sql.replaceFirst("C_POSJournal_ID,","").replaceFirst("\\?,", "");
 		 if (getTipoComprobante() == null) sql = sql.replaceFirst("TipoComprobante,","").replaceFirst("\\?,", "");
 		 if (getInitialCurrentAccountAmt() == null) sql = sql.replaceFirst("InitialCurrentAccountAmt,","").replaceFirst("\\?,", "");
 		 if (getC_POSPaymentMedium_ID() == 0) sql = sql.replaceFirst("C_POSPaymentMedium_ID,","").replaceFirst("\\?,", "");
 		 if (getM_RMA_ID() == 0) sql = sql.replaceFirst("M_RMA_ID,","").replaceFirst("\\?,", "");
 		 if (getNetAmount() == null) sql = sql.replaceFirst("NetAmount,","").replaceFirst("\\?,", "");
 		 if (getC_POSPaymentMedium_Credit_ID() == 0) sql = sql.replaceFirst("C_POSPaymentMedium_Credit_ID,","").replaceFirst("\\?,", "");
 		 if (getFiscalDescription() == null) sql = sql.replaceFirst("FiscalDescription,","").replaceFirst("\\?,", "");
 		 if (getPrintType() == null) sql = sql.replaceFirst("PrintType,","").replaceFirst("\\?,", "");
 		 if (getC_Region_Delivery_ID() == 0) sql = sql.replaceFirst("C_Region_Delivery_ID,","").replaceFirst("\\?,", "");
 		 if (getDateRecepted() == null) sql = sql.replaceFirst("DateRecepted,","").replaceFirst("\\?,", "");
 		 if (getImportClearance() == null) sql = sql.replaceFirst("ImportClearance,","").replaceFirst("\\?,", "");
 		 if (getManageElectronicInvoice() == null) sql = sql.replaceFirst("ManageElectronicInvoice,","").replaceFirst("\\?,", "");
 		 if (getM_InOutTransport_ID() == 0) sql = sql.replaceFirst("M_InOutTransport_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_User_ID() == 0) sql = sql.replaceFirst("AD_User_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Region_ID() == 0) sql = sql.replaceFirst("C_Region_ID,","").replaceFirst("\\?,", "");
 		 if (getUser1_ID() == 0) sql = sql.replaceFirst("User1_ID,","").replaceFirst("\\?,", "");
 		 if (getUser2_ID() == 0) sql = sql.replaceFirst("User2_ID,","").replaceFirst("\\?,", "");
 		 if (getAuthorizationChainStatus() == null) sql = sql.replaceFirst("AuthorizationChainStatus,","").replaceFirst("\\?,", "");
 		 if (getFechadeTCparaActualizarPrecios() == null) sql = sql.replaceFirst("FechadeTCparaActualizarPrecios,","").replaceFirst("\\?,", "");
 		 if (getInvoice_Adress() == null) sql = sql.replaceFirst("Invoice_Adress,","").replaceFirst("\\?,", "");
 		 if (getNombreCli() == null) sql = sql.replaceFirst("NombreCli,","").replaceFirst("\\?,", "");
 		 if (getNroIdentificCliente() == null) sql = sql.replaceFirst("NroIdentificCliente,","").replaceFirst("\\?,", "");
 		 if (getDocumentNo() == null) sql = sql.replaceFirst("DocumentNo,","").replaceFirst("\\?,", "");
 		 if (getNumeroComprobante() == 0) sql = sql.replaceFirst("NumeroComprobante,","").replaceFirst("\\?,", "");
 		 if (getCreateFrom() == null) sql = sql.replaceFirst("CreateFrom,","").replaceFirst("\\?,", "");
 		 if (getC_Order_Orig_ID() == 0) sql = sql.replaceFirst("C_Order_Orig_ID,","").replaceFirst("\\?,", "");
 		 if (getM_AuthorizationChain_ID() == 0) sql = sql.replaceFirst("M_AuthorizationChain_ID,","").replaceFirst("\\?,", "");
 		 if (getManualGeneralDiscount() == null) sql = sql.replaceFirst("ManualGeneralDiscount,","").replaceFirst("\\?,", "");
 		 if (getDeliveryViaRule() == null) sql = sql.replaceFirst("DeliveryViaRule,","").replaceFirst("\\?,", "");
 		 if (getGrandTotal() == null) sql = sql.replaceFirst("GrandTotal,","").replaceFirst("\\?,", "");
 		 if (getPuntoDeVenta() == 0) sql = sql.replaceFirst("PuntoDeVenta,","").replaceFirst("\\?,", "");
 		 if (getOrigInvFecha() == null) sql = sql.replaceFirst("OrigInvFecha,","").replaceFirst("\\?,", "");
 		 if (getOrigInvNro() == 0) sql = sql.replaceFirst("OrigInvNro,","").replaceFirst("\\?,", "");
 		 if (getOrigInvPtoVta() == 0) sql = sql.replaceFirst("OrigInvPtoVta,","").replaceFirst("\\?,", "");
 		 if (getOrigInvTipo() == null) sql = sql.replaceFirst("OrigInvTipo,","").replaceFirst("\\?,", "");
 		 if (getDireccion() == null) sql = sql.replaceFirst("Direccion,","").replaceFirst("\\?,", "");
 		 if (getLocalidad() == null) sql = sql.replaceFirst("Localidad,","").replaceFirst("\\?,", "");
 		 if (getprovincia() == null) sql = sql.replaceFirst("provincia,","").replaceFirst("\\?,", "");
 		 if (getCP() == null) sql = sql.replaceFirst("CP,","").replaceFirst("\\?,", "");
 		 if (getCAT_Iva_ID() == 0) sql = sql.replaceFirst("CAT_Iva_ID,","").replaceFirst("\\?,", "");
 		 skipAdditionalNullValues(sql);
 

 		 sql = sql.replace(",)", ")");
 
		 sql = sql.replace(",,)", ",");
 
		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 if (getRef_Invoice_ID() != 0) pstmt.setInt(col++, getRef_Invoice_ID());
		 pstmt.setString(col++, isPayScheduleValid()?"Y":"N");
		 if (getvtocae() != null) pstmt.setTimestamp(col++, getvtocae());
		 pstmt.setInt(col++, getC_Invoice_ID());
		 if (getcae() != null) pstmt.setString(col++, getcae());
		 if (getidcae() != null) pstmt.setString(col++, getidcae());
		 if (getcaeerror() != null) pstmt.setString(col++, getcaeerror());
		 if (getC_Activity_ID() != 0) pstmt.setInt(col++, getC_Activity_ID());
		 if (getDescription() != null) pstmt.setString(col++, getDescription());
		 if (getDocAction() != null) pstmt.setString(col++, getDocAction());
		 pstmt.setString(col++, isPosted()?"Y":"N");
		 if (getC_Order_ID() != 0) pstmt.setInt(col++, getC_Order_ID());
		 pstmt.setInt(col++, getC_DocType_ID());
		 if (getGenerateTo() != null) pstmt.setString(col++, getGenerateTo());
		 if (getC_Campaign_ID() != 0) pstmt.setInt(col++, getC_Campaign_ID());
		 if (getDocStatus() != null) pstmt.setString(col++, getDocStatus());
		 if (getPOReference() != null) pstmt.setString(col++, getPOReference());
		 if (getDateOrdered() != null) pstmt.setTimestamp(col++, getDateOrdered());
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 pstmt.setInt(col++, getC_Currency_ID());
		 pstmt.setString(col++, isTransferred()?"Y":"N");
		 if (getC_Project_ID() != 0) pstmt.setInt(col++, getC_Project_ID());
		 if (getC_Payment_ID() != 0) pstmt.setInt(col++, getC_Payment_ID());
		 if (getTotalLines() != null) pstmt.setBigDecimal(col++, getTotalLines());
		 pstmt.setString(col++, isPaid()?"Y":"N");
		 if (getDateAcct() != null) pstmt.setTimestamp(col++, getDateAcct());
		 pstmt.setInt(col++, getAD_Client_ID());
		 pstmt.setString(col++, isProcessing()?"Y":"N");
		 pstmt.setString(col++, isActive()?"Y":"N");
		 pstmt.setInt(col++, getUpdatedBy());
		 if (getDatePrinted() != null) pstmt.setTimestamp(col++, getDatePrinted());
		 pstmt.setString(col++, isSOTrx()?"Y":"N");
		 if (getC_Charge_ID() != 0) pstmt.setInt(col++, getC_Charge_ID());
		 if (getCopyFrom() != null) pstmt.setString(col++, getCopyFrom());
		 pstmt.setString(col++, isProcessed()?"Y":"N");
		 if (getUpdated() != null) pstmt.setTimestamp(col++, getUpdated());
		 pstmt.setString(col++, isSelfService()?"Y":"N");
		 pstmt.setString(col++, isDiscountPrinted()?"Y":"N");
		 if (getAD_OrgTrx_ID() != 0) pstmt.setInt(col++, getAD_OrgTrx_ID());
		 if (getSalesRep_ID() != 0) pstmt.setInt(col++, getSalesRep_ID());
		 if (getChargeAmt() != null) pstmt.setBigDecimal(col++, getChargeAmt());
		 pstmt.setString(col++, isTaxIncluded()?"Y":"N");
		 pstmt.setString(col++, isInDispute()?"Y":"N");
		 pstmt.setString(col++, isSendEMail()?"Y":"N");
		 if (getC_CashLine_ID() != 0) pstmt.setInt(col++, getC_CashLine_ID());
		 if (getC_ConversionType_ID() != 0) pstmt.setInt(col++, getC_ConversionType_ID());
		 pstmt.setString(col++, isPrinted()?"Y":"N");
		 if (getNumeroDeDocumento() != null) pstmt.setString(col++, getNumeroDeDocumento());
		 if (getAuthorize() != null) pstmt.setString(col++, getAuthorize());
		 if (getC_Invoice_Orig_ID() != 0) pstmt.setInt(col++, getC_Invoice_Orig_ID());
		 if (getOldGrandTotal() != null) pstmt.setBigDecimal(col++, getOldGrandTotal());
		 if (getC_Letra_Comprobante_ID() != 0) pstmt.setInt(col++, getC_Letra_Comprobante_ID());
		 if (getDateInvoiced() != null) pstmt.setTimestamp(col++, getDateInvoiced());
		 pstmt.setString(col++, isVoidable()?"Y":"N");
		 if (getCAI() != null) pstmt.setString(col++, getCAI());
		 if (getCUIT() != null) pstmt.setString(col++, getCUIT());
		 if (getCaja() != null) pstmt.setString(col++, getCaja());
		 if (getDateCAI() != null) pstmt.setTimestamp(col++, getDateCAI());
		 pstmt.setString(col++, isFiscalAlreadyPrinted()?"Y":"N");
		 pstmt.setInt(col++, getM_PriceList_ID());
		 if (getPaymentRule() != null) pstmt.setString(col++, getPaymentRule());
		 if (getcaecbte() != 0) pstmt.setInt(col++, getcaecbte());
		 pstmt.setString(col++, isCreateCashLine()?"Y":"N");
		 pstmt.setString(col++, isCopy()?"Y":"N");
		 if (getAuthCode() != null) pstmt.setString(col++, getAuthCode());
		 pstmt.setString(col++, isAuthMatch()?"Y":"N");
		 if (getC_POSJournal_ID() != 0) pstmt.setInt(col++, getC_POSJournal_ID());
		 pstmt.setString(col++, isApproved()?"Y":"N");
		 if (getTipoComprobante() != null) pstmt.setString(col++, getTipoComprobante());
		 pstmt.setString(col++, isExchange()?"Y":"N");
		 pstmt.setString(col++, isNotExchangeableCredit()?"Y":"N");
		 if (getInitialCurrentAccountAmt() != null) pstmt.setBigDecimal(col++, getInitialCurrentAccountAmt());
		 if (getC_POSPaymentMedium_ID() != 0) pstmt.setInt(col++, getC_POSPaymentMedium_ID());
		 pstmt.setString(col++, isManageDragOrderDiscounts()?"Y":"N");
		 pstmt.setString(col++, isManualDocumentNo()?"Y":"N");
		 pstmt.setString(col++, isApplyPercepcion()?"Y":"N");
		 if (getM_RMA_ID() != 0) pstmt.setInt(col++, getM_RMA_ID());
		 if (getNetAmount() != null) pstmt.setBigDecimal(col++, getNetAmount());
		 if (getC_POSPaymentMedium_Credit_ID() != 0) pstmt.setInt(col++, getC_POSPaymentMedium_Credit_ID());
		 if (getFiscalDescription() != null) pstmt.setString(col++, getFiscalDescription());
		 if (getPrintType() != null) pstmt.setString(col++, getPrintType());
		 if (getC_Region_Delivery_ID() != 0) pstmt.setInt(col++, getC_Region_Delivery_ID());
		 pstmt.setString(col++, isUpdateOrderQty()?"Y":"N");
		 if (getDateRecepted() != null) pstmt.setTimestamp(col++, getDateRecepted());
		 if (getImportClearance() != null) pstmt.setString(col++, getImportClearance());
		 pstmt.setString(col++, isSkipIPNoCaeValidation()?"Y":"N");
		 if (getManageElectronicInvoice() != null) pstmt.setString(col++, getManageElectronicInvoice());
		 if (getM_InOutTransport_ID() != 0) pstmt.setInt(col++, getM_InOutTransport_ID());
		 if (getAD_User_ID() != 0) pstmt.setInt(col++, getAD_User_ID());
		 pstmt.setInt(col++, getC_BPartner_Location_ID());
		 pstmt.setInt(col++, getCreatedBy());
		 if (getC_Region_ID() != 0) pstmt.setInt(col++, getC_Region_ID());
		 if (getUser1_ID() != 0) pstmt.setInt(col++, getUser1_ID());
		 if (getUser2_ID() != 0) pstmt.setInt(col++, getUser2_ID());
		 if (getAuthorizationChainStatus() != null) pstmt.setString(col++, getAuthorizationChainStatus());
		 if (getFechadeTCparaActualizarPrecios() != null) pstmt.setTimestamp(col++, getFechadeTCparaActualizarPrecios());
		 pstmt.setString(col++, isManageDragOrderSurcharges()?"Y":"N");
		 if (getInvoice_Adress() != null) pstmt.setString(col++, getInvoice_Adress());
		 if (getNombreCli() != null) pstmt.setString(col++, getNombreCli());
		 if (getNroIdentificCliente() != null) pstmt.setString(col++, getNroIdentificCliente());
		 pstmt.setInt(col++, getC_BPartner_ID());
		 pstmt.setInt(col++, getC_DocTypeTarget_ID());
		 if (getDocumentNo() != null) pstmt.setString(col++, getDocumentNo());
		 pstmt.setInt(col++, getAD_Org_ID());
		 if (getNumeroComprobante() != 0) pstmt.setInt(col++, getNumeroComprobante());
		 pstmt.setInt(col++, getC_PaymentTerm_ID());
		 if (getCreateFrom() != null) pstmt.setString(col++, getCreateFrom());
		 if (getC_Order_Orig_ID() != 0) pstmt.setInt(col++, getC_Order_Orig_ID());
		 if (getM_AuthorizationChain_ID() != 0) pstmt.setInt(col++, getM_AuthorizationChain_ID());
		 if (getManualGeneralDiscount() != null) pstmt.setBigDecimal(col++, getManualGeneralDiscount());
		 if (getDeliveryViaRule() != null) pstmt.setString(col++, getDeliveryViaRule());
		 if (getGrandTotal() != null) pstmt.setBigDecimal(col++, getGrandTotal());
		 if (getPuntoDeVenta() != 0) pstmt.setInt(col++, getPuntoDeVenta());
		 if (getOrigInvFecha() != null) pstmt.setTimestamp(col++, getOrigInvFecha());
		 if (getOrigInvNro() != 0) pstmt.setInt(col++, getOrigInvNro());
		 if (getOrigInvPtoVta() != 0) pstmt.setInt(col++, getOrigInvPtoVta());
		 if (getOrigInvTipo() != null) pstmt.setString(col++, getOrigInvTipo());
		 if (getDireccion() != null) pstmt.setString(col++, getDireccion());
		 if (getLocalidad() != null) pstmt.setString(col++, getLocalidad());
		 if (getprovincia() != null) pstmt.setString(col++, getprovincia());
		 if (getCP() != null) pstmt.setString(col++, getCP());
		 if (getCAT_Iva_ID() != 0) pstmt.setInt(col++, getCAT_Iva_ID());
		 col = setAdditionalInsertValues(col, pstmt);
 

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

protected String getAdditionalParamNames() 
{
 return "";
 }
 
protected String getAdditionalParamMarks() 
{
 return "";
 }
 
protected void skipAdditionalNullValues(String sql) 
{
  }
 
protected int setAdditionalInsertValues(int col, PreparedStatement pstmt) throws Exception 
{
 return col;
 }
 
}
