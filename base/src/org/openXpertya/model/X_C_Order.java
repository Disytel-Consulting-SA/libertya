/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Order
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-12-30 19:15:59.183 */
public class X_C_Order extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Order (Properties ctx, int C_Order_ID, String trxName)
{
super (ctx, C_Order_ID, trxName);
/** if (C_Order_ID == 0)
{
setAD_Org_Transfer_ID (0);
setC_BPartner_ID (0);
setC_BPartner_Location_ID (0);
setC_Currency_ID (0);	// @C_Currency_ID@
setC_DocType_ID (0);	// 0
setC_DocTypeTarget_ID (0);
setC_Order_ID (0);
setC_PaymentTerm_ID (0);
setDateAcct (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDateOrdered (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDatePromised (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDeliveryRule (null);	// F
setDeliveryViaRule (null);	// P
setDocAction (null);	// CO
setDocStatus (null);	// DR
setDocumentNo (null);
setFreightAmt (Env.ZERO);
setFreightCostRule (null);	// I
setGrandTotal (Env.ZERO);
setInvoiceRule (null);	// I
setIsApproved (false);	// @IsApproved@
setIsCreditApproved (false);
setIsDelivered (false);
setIsDiscountPrinted (false);
setIsDropShip (false);	// N
setIsExchange (false);
setIsInvoiced (false);
setIsPrinted (false);
setIsSelected (false);
setIsSelfService (false);
setIsSOTrx (false);	// @IsSOTrx@
setIsTaxIncluded (false);
setIsTransferred (false);
setM_PriceList_ID (0);
setM_Warehouse_ID (0);
setM_Warehouse_Transfer_ID (0);
setPaymentRule (null);	// B
setPosted (false);	// N
setPriorityRule (null);	// 5
setProcessed (false);
setSalesRep_ID (0);
setSendEMail (false);
setTotalLines (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_Order (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Order");

/** TableName=C_Order */
public static final String Table_Name="C_Order";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Order");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Order[").append(getID()).append("]");
return sb.toString();
}
/** Set Acceptance */
public void setAcceptance (BigDecimal Acceptance)
{
set_Value ("Acceptance", Acceptance);
}
/** Get Acceptance */
public BigDecimal getAcceptance() 
{
BigDecimal bd = (BigDecimal)get_Value("Acceptance");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int AD_ORG_TRANSFER_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (all but 0)");
/** Set Organization */
public void setAD_Org_Transfer_ID (int AD_Org_Transfer_ID)
{
set_Value ("AD_Org_Transfer_ID", new Integer(AD_Org_Transfer_ID));
}
/** Get Organization */
public int getAD_Org_Transfer_ID() 
{
Integer ii = (Integer)get_Value("AD_Org_Transfer_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Allow Change Price List */
public void setAllowChangePriceList (boolean AllowChangePriceList)
{
throw new IllegalArgumentException ("AllowChangePriceList is virtual column");
}
/** Get Allow Change Price List */
public boolean isAllowChangePriceList() 
{
Object oo = get_Value("AllowChangePriceList");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
public static final int BILL_BPARTNER_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner (all)");
/** Set Invoice Partner.
Business Partner to be invoiced */
public void setBill_BPartner_ID (int Bill_BPartner_ID)
{
if (Bill_BPartner_ID <= 0) set_Value ("Bill_BPartner_ID", null);
 else 
set_Value ("Bill_BPartner_ID", new Integer(Bill_BPartner_ID));
}
/** Get Invoice Partner.
Business Partner to be invoiced */
public int getBill_BPartner_ID() 
{
Integer ii = (Integer)get_Value("Bill_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int BILL_LOCATION_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner Location");
/** Set Invoice Location.
Business Partner Location for invoicing */
public void setBill_Location_ID (int Bill_Location_ID)
{
if (Bill_Location_ID <= 0) set_Value ("Bill_Location_ID", null);
 else 
set_Value ("Bill_Location_ID", new Integer(Bill_Location_ID));
}
/** Get Invoice Location.
Business Partner Location for invoicing */
public int getBill_Location_ID() 
{
Integer ii = (Integer)get_Value("Bill_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int BILL_USER_ID_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set Invoice Contact.
Business Partner Contact for invoicing */
public void setBill_User_ID (int Bill_User_ID)
{
if (Bill_User_ID <= 0) set_Value ("Bill_User_ID", null);
 else 
set_Value ("Bill_User_ID", new Integer(Bill_User_ID));
}
/** Get Invoice Contact.
Business Partner Contact for invoicing */
public int getBill_User_ID() 
{
Integer ii = (Integer)get_Value("Bill_User_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int C_DOCTYPE_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
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
/** Set CodigoCategoriaIVA */
public void setCodigoCategoriaIVA (int CodigoCategoriaIVA)
{
throw new IllegalArgumentException ("CodigoCategoriaIVA is virtual column");
}
/** Get CodigoCategoriaIVA */
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
/** Set Create Vendor Product Lines */
public void setCreateVendorProductLines (String CreateVendorProductLines)
{
if (CreateVendorProductLines != null && CreateVendorProductLines.length() > 1)
{
log.warning("Length > 1 - truncated");
CreateVendorProductLines = CreateVendorProductLines.substring(0,1);
}
set_Value ("CreateVendorProductLines", CreateVendorProductLines);
}
/** Get Create Vendor Product Lines */
public String getCreateVendorProductLines() 
{
return (String)get_Value("CreateVendorProductLines");
}
public static final int C_REPAIR_ORDER_ID_AD_Reference_ID = MReference.getReferenceID("Repair Order");
/** Set C_Repair_Order_ID */
public void setC_Repair_Order_ID (int C_Repair_Order_ID)
{
if (C_Repair_Order_ID <= 0) set_Value ("C_Repair_Order_ID", null);
 else 
set_Value ("C_Repair_Order_ID", new Integer(C_Repair_Order_ID));
}
/** Get C_Repair_Order_ID */
public int getC_Repair_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Repair_Order_ID");
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
/** Set Date Ordered.
Date of Order */
public void setDateOrdered (Timestamp DateOrdered)
{
if (DateOrdered == null) throw new IllegalArgumentException ("DateOrdered is mandatory");
set_Value ("DateOrdered", DateOrdered);
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
/** Set dateprod */
public void setdateprod (Timestamp dateprod)
{
set_Value ("dateprod", dateprod);
}
/** Get dateprod */
public Timestamp getdateprod() 
{
return (Timestamp)get_Value("dateprod");
}
/** Set Date Promised.
Date Order was promised */
public void setDatePromised (Timestamp DatePromised)
{
if (DatePromised == null) throw new IllegalArgumentException ("DatePromised is mandatory");
set_Value ("DatePromised", DatePromised);
}
/** Get Date Promised.
Date Order was promised */
public Timestamp getDatePromised() 
{
return (Timestamp)get_Value("DatePromised");
}
/** Set daterealprod */
public void setdaterealprod (Timestamp daterealprod)
{
set_Value ("daterealprod", daterealprod);
}
/** Get daterealprod */
public Timestamp getdaterealprod() 
{
return (Timestamp)get_Value("daterealprod");
}
public static final int DELIVERYRULE_AD_Reference_ID = MReference.getReferenceID("C_Order DeliveryRule");
/** Availability = A */
public static final String DELIVERYRULE_Availability = "A";
/** Complete Order = O */
public static final String DELIVERYRULE_CompleteOrder = "O";
/** After Receipt = R */
public static final String DELIVERYRULE_AfterReceipt = "R";
/** Complete Line = L */
public static final String DELIVERYRULE_CompleteLine = "L";
/** Force = F */
public static final String DELIVERYRULE_Force = "F";
/** After Invoicing = I */
public static final String DELIVERYRULE_AfterInvoicing = "I";
/** Force - After invoicing = Z */
public static final String DELIVERYRULE_Force_AfterInvoicing = "Z";
/** Set Delivery Rule.
Defines the timing of Delivery */
public void setDeliveryRule (String DeliveryRule)
{
if (DeliveryRule.equals("A") || DeliveryRule.equals("O") || DeliveryRule.equals("R") || DeliveryRule.equals("L") || DeliveryRule.equals("F") || DeliveryRule.equals("I") || DeliveryRule.equals("Z"));
 else throw new IllegalArgumentException ("DeliveryRule Invalid value - Reference = DELIVERYRULE_AD_Reference_ID - A - O - R - L - F - I - Z");
if (DeliveryRule == null) throw new IllegalArgumentException ("DeliveryRule is mandatory");
if (DeliveryRule.length() > 1)
{
log.warning("Length > 1 - truncated");
DeliveryRule = DeliveryRule.substring(0,1);
}
set_Value ("DeliveryRule", DeliveryRule);
}
/** Get Delivery Rule.
Defines the timing of Delivery */
public String getDeliveryRule() 
{
return (String)get_Value("DeliveryRule");
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
if (DeliveryViaRule.equals("P") || DeliveryViaRule.equals("S") || DeliveryViaRule.equals("D"));
 else throw new IllegalArgumentException ("DeliveryViaRule Invalid value - Reference = DELIVERYVIARULE_AD_Reference_ID - P - S - D");
if (DeliveryViaRule == null) throw new IllegalArgumentException ("DeliveryViaRule is mandatory");
if (DeliveryViaRule.length() > 1)
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
/** Set Freight Amount.
Freight Amount  */
public void setFreightAmt (BigDecimal FreightAmt)
{
if (FreightAmt == null) throw new IllegalArgumentException ("FreightAmt is mandatory");
set_Value ("FreightAmt", FreightAmt);
}
/** Get Freight Amount.
Freight Amount  */
public BigDecimal getFreightAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FreightAmt");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int FREIGHTCOSTRULE_AD_Reference_ID = MReference.getReferenceID("C_Order FreightCostRule");
/** Line = L */
public static final String FREIGHTCOSTRULE_Line = "L";
/** Fix price = F */
public static final String FREIGHTCOSTRULE_FixPrice = "F";
/** Calculated = C */
public static final String FREIGHTCOSTRULE_Calculated = "C";
/** Freight included = I */
public static final String FREIGHTCOSTRULE_FreightIncluded = "I";
/** Set Freight Cost Rule.
Method for charging Freight */
public void setFreightCostRule (String FreightCostRule)
{
if (FreightCostRule.equals("L") || FreightCostRule.equals("F") || FreightCostRule.equals("C") || FreightCostRule.equals("I"));
 else throw new IllegalArgumentException ("FreightCostRule Invalid value - Reference = FREIGHTCOSTRULE_AD_Reference_ID - L - F - C - I");
if (FreightCostRule == null) throw new IllegalArgumentException ("FreightCostRule is mandatory");
if (FreightCostRule.length() > 1)
{
log.warning("Length > 1 - truncated");
FreightCostRule = FreightCostRule.substring(0,1);
}
set_Value ("FreightCostRule", FreightCostRule);
}
/** Get Freight Cost Rule.
Method for charging Freight */
public String getFreightCostRule() 
{
return (String)get_Value("FreightCostRule");
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
public static final int INVOICERULE_AD_Reference_ID = MReference.getReferenceID("C_Order InvoiceRule");
/** After Delivery = D */
public static final String INVOICERULE_AfterDelivery = "D";
/** After Order delivered = O */
public static final String INVOICERULE_AfterOrderDelivered = "O";
/** Immediate = I */
public static final String INVOICERULE_Immediate = "I";
/** Customer Schedule after Delivery = S */
public static final String INVOICERULE_CustomerScheduleAfterDelivery = "S";
/** Set Invoice Rule.
Frequency and method of invoicing  */
public void setInvoiceRule (String InvoiceRule)
{
if (InvoiceRule.equals("D") || InvoiceRule.equals("O") || InvoiceRule.equals("I") || InvoiceRule.equals("S"));
 else throw new IllegalArgumentException ("InvoiceRule Invalid value - Reference = INVOICERULE_AD_Reference_ID - D - O - I - S");
if (InvoiceRule == null) throw new IllegalArgumentException ("InvoiceRule is mandatory");
if (InvoiceRule.length() > 1)
{
log.warning("Length > 1 - truncated");
InvoiceRule = InvoiceRule.substring(0,1);
}
set_Value ("InvoiceRule", InvoiceRule);
}
/** Get Invoice Rule.
Frequency and method of invoicing  */
public String getInvoiceRule() 
{
return (String)get_Value("InvoiceRule");
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
/** Set Credit Approved.
Credit  has been approved */
public void setIsCreditApproved (boolean IsCreditApproved)
{
set_ValueNoCheck ("IsCreditApproved", new Boolean(IsCreditApproved));
}
/** Get Credit Approved.
Credit  has been approved */
public boolean isCreditApproved() 
{
Object oo = get_Value("IsCreditApproved");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Delivered */
public void setIsDelivered (boolean IsDelivered)
{
set_ValueNoCheck ("IsDelivered", new Boolean(IsDelivered));
}
/** Get Delivered */
public boolean isDelivered() 
{
Object oo = get_Value("IsDelivered");
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
/** Set Drop Shipment.
Drop Shipments are sent from the Vendor directly to the Customer */
public void setIsDropShip (boolean IsDropShip)
{
set_ValueNoCheck ("IsDropShip", new Boolean(IsDropShip));
}
/** Get Drop Shipment.
Drop Shipments are sent from the Vendor directly to the Customer */
public boolean isDropShip() 
{
Object oo = get_Value("IsDropShip");
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
/** Set Invoiced.
Is this invoiced? */
public void setIsInvoiced (boolean IsInvoiced)
{
set_ValueNoCheck ("IsInvoiced", new Boolean(IsInvoiced));
}
/** Get Invoiced.
Is this invoiced? */
public boolean isInvoiced() 
{
Object oo = get_Value("IsInvoiced");
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
/** Set Selected */
public void setIsSelected (boolean IsSelected)
{
set_Value ("IsSelected", new Boolean(IsSelected));
}
/** Get Selected */
public boolean isSelected() 
{
Object oo = get_Value("IsSelected");
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
/** Set Is Tpv Used */
public void setIsTpvUsed (boolean IsTpvUsed)
{
set_Value ("IsTpvUsed", new Boolean(IsTpvUsed));
}
/** Get Is Tpv Used */
public boolean isTpvUsed() 
{
Object oo = get_Value("IsTpvUsed");
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
/** Set M_AuthorizationChain_ID */
public void setM_AuthorizationChain_ID (int M_AuthorizationChain_ID)
{
if (M_AuthorizationChain_ID <= 0) set_Value ("M_AuthorizationChain_ID", null);
 else 
set_Value ("M_AuthorizationChain_ID", new Integer(M_AuthorizationChain_ID));
}
/** Get M_AuthorizationChain_ID */
public int getM_AuthorizationChain_ID() 
{
Integer ii = (Integer)get_Value("M_AuthorizationChain_ID");
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
/** Set Shipper.
Method or manner of product delivery */
public void setM_Shipper_ID (int M_Shipper_ID)
{
if (M_Shipper_ID <= 0) set_Value ("M_Shipper_ID", null);
 else 
set_Value ("M_Shipper_ID", new Integer(M_Shipper_ID));
}
/** Get Shipper.
Method or manner of product delivery */
public int getM_Shipper_ID() 
{
Integer ii = (Integer)get_Value("M_Shipper_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_Value ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_WAREHOUSE_TRANSFER_ID_AD_Reference_ID = MReference.getReferenceID("M_Warehouse of Client");
/** Set Warehouse */
public void setM_Warehouse_Transfer_ID (int M_Warehouse_Transfer_ID)
{
set_Value ("M_Warehouse_Transfer_ID", new Integer(M_Warehouse_Transfer_ID));
}
/** Get Warehouse */
public int getM_Warehouse_Transfer_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_Transfer_ID");
if (ii == null) return 0;
return ii.intValue();
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
public static final int PAY_BPARTNER_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner (No Summary)");
/** Set Payment BPartner.
Business Partner responsible for the payment */
public void setPay_BPartner_ID (int Pay_BPartner_ID)
{
if (Pay_BPartner_ID <= 0) set_Value ("Pay_BPartner_ID", null);
 else 
set_Value ("Pay_BPartner_ID", new Integer(Pay_BPartner_ID));
}
/** Get Payment BPartner.
Business Partner responsible for the payment */
public int getPay_BPartner_ID() 
{
Integer ii = (Integer)get_Value("Pay_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PAY_LOCATION_ID_AD_Reference_ID = MReference.getReferenceID("C_Location");
/** Set Payment Location.
Location of the Business Partner responsible for the payment */
public void setPay_Location_ID (int Pay_Location_ID)
{
if (Pay_Location_ID <= 0) set_Value ("Pay_Location_ID", null);
 else 
set_Value ("Pay_Location_ID", new Integer(Pay_Location_ID));
}
/** Get Payment Location.
Location of the Business Partner responsible for the payment */
public int getPay_Location_ID() 
{
Integer ii = (Integer)get_Value("Pay_Location_ID");
if (ii == null) return 0;
return ii.intValue();
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
if (PaymentRule.equals("Tr") || PaymentRule.equals("K") || PaymentRule.equals("B") || PaymentRule.equals("P") || PaymentRule.equals("PC") || PaymentRule.equals("T") || PaymentRule.equals("Cf") || PaymentRule.equals("D") || PaymentRule.equals("S"));
 else throw new IllegalArgumentException ("PaymentRule Invalid value - Reference = PAYMENTRULE_AD_Reference_ID - Tr - K - B - P - PC - T - Cf - D - S");
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
public static final int PRIORITYRULE_AD_Reference_ID = MReference.getReferenceID("_PriorityRule");
/** Medium = 5 */
public static final String PRIORITYRULE_Medium = "5";
/** High = 3 */
public static final String PRIORITYRULE_High = "3";
/** Low = 7 */
public static final String PRIORITYRULE_Low = "7";
/** Urgent = 1 */
public static final String PRIORITYRULE_Urgent = "1";
/** Minor = 9 */
public static final String PRIORITYRULE_Minor = "9";
/** Set Priority.
Priority of a document */
public void setPriorityRule (String PriorityRule)
{
if (PriorityRule.equals("5") || PriorityRule.equals("3") || PriorityRule.equals("7") || PriorityRule.equals("1") || PriorityRule.equals("9"));
 else throw new IllegalArgumentException ("PriorityRule Invalid value - Reference = PRIORITYRULE_AD_Reference_ID - 5 - 3 - 7 - 1 - 9");
if (PriorityRule == null) throw new IllegalArgumentException ("PriorityRule is mandatory");
if (PriorityRule.length() > 1)
{
log.warning("Length > 1 - truncated");
PriorityRule = PriorityRule.substring(0,1);
}
set_Value ("PriorityRule", PriorityRule);
}
/** Get Priority.
Priority of a document */
public String getPriorityRule() 
{
return (String)get_Value("PriorityRule");
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
public static final int PROGRAM_INVOICE_AD_Reference_ID = MReference.getReferenceID("C_Invoice");
/** Set program_invoice */
public void setprogram_invoice (int program_invoice)
{
set_Value ("program_invoice", new Integer(program_invoice));
}
/** Get program_invoice */
public int getprogram_invoice() 
{
Integer ii = (Integer)get_Value("program_invoice");
if (ii == null) return 0;
return ii.intValue();
}
public static final int REF_ORDER_ID_AD_Reference_ID = MReference.getReferenceID("C_Order");
/** Set Referenced Order.
Reference to corresponding Sales/Purchase Order */
public void setRef_Order_ID (int Ref_Order_ID)
{
if (Ref_Order_ID <= 0) set_Value ("Ref_Order_ID", null);
 else 
set_Value ("Ref_Order_ID", new Integer(Ref_Order_ID));
}
/** Get Referenced Order.
Reference to corresponding Sales/Purchase Order */
public int getRef_Order_ID() 
{
Integer ii = (Integer)get_Value("Ref_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int REPAIR_PRIORITY_AD_Reference_ID = MReference.getReferenceID("repairpriority");
/** Larga estancia = Larga estancia */
public static final String REPAIR_PRIORITY_LargaEstancia = "Larga estancia";
/** Normal = Normal */
public static final String REPAIR_PRIORITY_Normal = "Normal";
/** Urgente = Urgente */
public static final String REPAIR_PRIORITY_Urgente = "Urgente";
/** Muy Urgente = Muy Urgente */
public static final String REPAIR_PRIORITY_MuyUrgente = "Muy Urgente";
/** Set repair_priority */
public void setrepair_priority (String repair_priority)
{
if (repair_priority == null || repair_priority.equals("Larga estancia") || repair_priority.equals("Normal") || repair_priority.equals("Urgente") || repair_priority.equals("Muy Urgente"));
 else throw new IllegalArgumentException ("repair_priority Invalid value - Reference = REPAIR_PRIORITY_AD_Reference_ID - Larga estancia - Normal - Urgente - Muy Urgente");
if (repair_priority != null && repair_priority.length() > 22)
{
log.warning("Length > 22 - truncated");
repair_priority = repair_priority.substring(0,22);
}
set_Value ("repair_priority", repair_priority);
}
/** Get repair_priority */
public String getrepair_priority() 
{
return (String)get_Value("repair_priority");
}
public static final int REPAIR_STATE_AD_Reference_ID = MReference.getReferenceID("repair_state");
/** No comenzado = No comenzado */
public static final String REPAIR_STATE_NoComenzado = "No comenzado";
/** Previsto = Previsto */
public static final String REPAIR_STATE_Previsto = "Previsto";
/** En proceso = En proceso */
public static final String REPAIR_STATE_EnProceso = "En proceso";
/** Terminado = Terminado */
public static final String REPAIR_STATE_Terminado = "Terminado";
/** Set repair_state */
public void setrepair_state (String repair_state)
{
if (repair_state == null || repair_state.equals("No comenzado") || repair_state.equals("Previsto") || repair_state.equals("En proceso") || repair_state.equals("Terminado"));
 else throw new IllegalArgumentException ("repair_state Invalid value - Reference = REPAIR_STATE_AD_Reference_ID - No comenzado - Previsto - En proceso - Terminado");
if (repair_state != null && repair_state.length() > 22)
{
log.warning("Length > 22 - truncated");
repair_state = repair_state.substring(0,22);
}
set_Value ("repair_state", repair_state);
}
/** Get repair_state */
public String getrepair_state() 
{
return (String)get_Value("repair_state");
}
public static final int SALESREP_ID_AD_Reference_ID = MReference.getReferenceID("AD_User - SalesRep");
/** Set Sales Representative.
Sales Representative or Company Agent */
public void setSalesRep_ID (int SalesRep_ID)
{
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
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO C_Order(Pay_BPartner_ID,Pay_Location_ID,IsDelivered,Processing,C_Payment_ID,IsSelected,AD_OrgTrx_ID,SendEMail,IsCreditApproved,IsInvoiced,C_Campaign_ID,IsTransferred,M_Warehouse_ID,IsApproved,FreightAmt,C_DocType_ID,TotalLines,GrandTotal,DeliveryViaRule,InvoiceRule,POReference,DateAcct,IsPrinted,Description,IsActive,C_Charge_ID,IsDropShip,C_Project_ID,DeliveryRule,AD_Org_ID,C_PaymentTerm_ID,Created,PriorityRule,AD_User_ID,Updated,ChargeAmt,FreightCostRule,C_CashLine_ID,C_Activity_ID,Posted,IsSOTrx,AD_Client_ID,IsDiscountPrinted,DatePrinted,C_Order_ID,IsTaxIncluded,C_Currency_ID,Processed,IsSelfService,CopyFrom,Bill_Location_ID,C_ConversionType_ID,Bill_BPartner_ID,DocAction,repair_priority,repair_state,dateprod,daterealprod,Acceptance,M_Shipper_ID,CreateVendorProductLines,PaymentRule,M_PriceList_ID,DateOrdered,DatePromised,DocStatus,C_BPartner_ID,ValidTo,IsExchange,NombreCli,NroIdentificCliente,Invoice_Adress,CUIT,M_Warehouse_Transfer_ID,AD_Org_Transfer_ID,DocumentNo,C_DocTypeTarget_ID,IsTpvUsed,program_invoice,C_BPartner_Location_ID,M_AuthorizationChain_ID,Authorize,OldGrandTotal,Ref_Order_ID,User2_ID,Bill_User_ID,CreatedBy,C_Repair_Order_ID,SalesRep_ID,UpdatedBy,User1_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		 if (getPay_BPartner_ID() == 0) sql = sql.replaceFirst("Pay_BPartner_ID,","").replaceFirst("\\?,", "");
 		 if (getPay_Location_ID() == 0) sql = sql.replaceFirst("Pay_Location_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Payment_ID() == 0) sql = sql.replaceFirst("C_Payment_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_OrgTrx_ID() == 0) sql = sql.replaceFirst("AD_OrgTrx_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Campaign_ID() == 0) sql = sql.replaceFirst("C_Campaign_ID,","").replaceFirst("\\?,", "");
 		 if (getM_Warehouse_ID() == 0) sql = sql.replaceFirst("M_Warehouse_ID,","").replaceFirst("\\?,", "");
 		 if (getFreightAmt() == null) sql = sql.replaceFirst("FreightAmt,","").replaceFirst("\\?,", "");
 		 if (getC_DocType_ID() == 0) sql = sql.replaceFirst("C_DocType_ID,","").replaceFirst("\\?,", "");
 		 if (getTotalLines() == null) sql = sql.replaceFirst("TotalLines,","").replaceFirst("\\?,", "");
 		 if (getGrandTotal() == null) sql = sql.replaceFirst("GrandTotal,","").replaceFirst("\\?,", "");
 		 if (getDeliveryViaRule() == null) sql = sql.replaceFirst("DeliveryViaRule,","").replaceFirst("\\?,", "");
 		 if (getInvoiceRule() == null) sql = sql.replaceFirst("InvoiceRule,","").replaceFirst("\\?,", "");
 		 if (getPOReference() == null) sql = sql.replaceFirst("POReference,","").replaceFirst("\\?,", "");
 		 if (getDateAcct() == null) sql = sql.replaceFirst("DateAcct,","").replaceFirst("\\?,", "");
 		 if (getDescription() == null) sql = sql.replaceFirst("Description,","").replaceFirst("\\?,", "");
 		 if (getC_Charge_ID() == 0) sql = sql.replaceFirst("C_Charge_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Project_ID() == 0) sql = sql.replaceFirst("C_Project_ID,","").replaceFirst("\\?,", "");
 		 if (getDeliveryRule() == null) sql = sql.replaceFirst("DeliveryRule,","").replaceFirst("\\?,", "");
 		 if (getAD_Org_ID() == 0) sql = sql.replaceFirst("AD_Org_ID,","").replaceFirst("\\?,", "");
 		 if (getC_PaymentTerm_ID() == 0) sql = sql.replaceFirst("C_PaymentTerm_ID,","").replaceFirst("\\?,", "");
 		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getPriorityRule() == null) sql = sql.replaceFirst("PriorityRule,","").replaceFirst("\\?,", "");
 		 if (getAD_User_ID() == 0) sql = sql.replaceFirst("AD_User_ID,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getChargeAmt() == null) sql = sql.replaceFirst("ChargeAmt,","").replaceFirst("\\?,", "");
 		 if (getFreightCostRule() == null) sql = sql.replaceFirst("FreightCostRule,","").replaceFirst("\\?,", "");
 		 if (getC_CashLine_ID() == 0) sql = sql.replaceFirst("C_CashLine_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Activity_ID() == 0) sql = sql.replaceFirst("C_Activity_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_Client_ID() == 0) sql = sql.replaceFirst("AD_Client_ID,","").replaceFirst("\\?,", "");
 		 if (getDatePrinted() == null) sql = sql.replaceFirst("DatePrinted,","").replaceFirst("\\?,", "");
 		 if (getC_Order_ID() == 0) sql = sql.replaceFirst("C_Order_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Currency_ID() == 0) sql = sql.replaceFirst("C_Currency_ID,","").replaceFirst("\\?,", "");
 		 if (getCopyFrom() == null) sql = sql.replaceFirst("CopyFrom,","").replaceFirst("\\?,", "");
 		 if (getBill_Location_ID() == 0) sql = sql.replaceFirst("Bill_Location_ID,","").replaceFirst("\\?,", "");
 		 if (getC_ConversionType_ID() == 0) sql = sql.replaceFirst("C_ConversionType_ID,","").replaceFirst("\\?,", "");
 		 if (getBill_BPartner_ID() == 0) sql = sql.replaceFirst("Bill_BPartner_ID,","").replaceFirst("\\?,", "");
 		 if (getDocAction() == null) sql = sql.replaceFirst("DocAction,","").replaceFirst("\\?,", "");
 		 if (getrepair_priority() == null) sql = sql.replaceFirst("repair_priority,","").replaceFirst("\\?,", "");
 		 if (getrepair_state() == null) sql = sql.replaceFirst("repair_state,","").replaceFirst("\\?,", "");
 		 if (getdateprod() == null) sql = sql.replaceFirst("dateprod,","").replaceFirst("\\?,", "");
 		 if (getdaterealprod() == null) sql = sql.replaceFirst("daterealprod,","").replaceFirst("\\?,", "");
 		 if (getAcceptance() == null) sql = sql.replaceFirst("Acceptance,","").replaceFirst("\\?,", "");
 		 if (getM_Shipper_ID() == 0) sql = sql.replaceFirst("M_Shipper_ID,","").replaceFirst("\\?,", "");
 		 if (getCreateVendorProductLines() == null) sql = sql.replaceFirst("CreateVendorProductLines,","").replaceFirst("\\?,", "");
 		 if (getPaymentRule() == null) sql = sql.replaceFirst("PaymentRule,","").replaceFirst("\\?,", "");
 		 if (getM_PriceList_ID() == 0) sql = sql.replaceFirst("M_PriceList_ID,","").replaceFirst("\\?,", "");
 		 if (getDateOrdered() == null) sql = sql.replaceFirst("DateOrdered,","").replaceFirst("\\?,", "");
 		 if (getDatePromised() == null) sql = sql.replaceFirst("DatePromised,","").replaceFirst("\\?,", "");
 		 if (getDocStatus() == null) sql = sql.replaceFirst("DocStatus,","").replaceFirst("\\?,", "");
 		 if (getC_BPartner_ID() == 0) sql = sql.replaceFirst("C_BPartner_ID,","").replaceFirst("\\?,", "");
 		 if (getValidTo() == null) sql = sql.replaceFirst("ValidTo,","").replaceFirst("\\?,", "");
 		 if (getNombreCli() == null) sql = sql.replaceFirst("NombreCli,","").replaceFirst("\\?,", "");
 		 if (getNroIdentificCliente() == null) sql = sql.replaceFirst("NroIdentificCliente,","").replaceFirst("\\?,", "");
 		 if (getInvoice_Adress() == null) sql = sql.replaceFirst("Invoice_Adress,","").replaceFirst("\\?,", "");
 		 if (getCUIT() == null) sql = sql.replaceFirst("CUIT,","").replaceFirst("\\?,", "");
 		 if (getM_Warehouse_Transfer_ID() == 0) sql = sql.replaceFirst("M_Warehouse_Transfer_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_Org_Transfer_ID() == 0) sql = sql.replaceFirst("AD_Org_Transfer_ID,","").replaceFirst("\\?,", "");
 		 if (getDocumentNo() == null) sql = sql.replaceFirst("DocumentNo,","").replaceFirst("\\?,", "");
 		 if (getC_DocTypeTarget_ID() == 0) sql = sql.replaceFirst("C_DocTypeTarget_ID,","").replaceFirst("\\?,", "");
 		 if (getprogram_invoice() == 0) sql = sql.replaceFirst("program_invoice,","").replaceFirst("\\?,", "");
 		 if (getC_BPartner_Location_ID() == 0) sql = sql.replaceFirst("C_BPartner_Location_ID,","").replaceFirst("\\?,", "");
 		 if (getM_AuthorizationChain_ID() == 0) sql = sql.replaceFirst("M_AuthorizationChain_ID,","").replaceFirst("\\?,", "");
 		 if (getAuthorize() == null) sql = sql.replaceFirst("Authorize,","").replaceFirst("\\?,", "");
 		 if (getOldGrandTotal() == null) sql = sql.replaceFirst("OldGrandTotal,","").replaceFirst("\\?,", "");
 		 if (getRef_Order_ID() == 0) sql = sql.replaceFirst("Ref_Order_ID,","").replaceFirst("\\?,", "");
 		 if (getUser2_ID() == 0) sql = sql.replaceFirst("User2_ID,","").replaceFirst("\\?,", "");
 		 if (getBill_User_ID() == 0) sql = sql.replaceFirst("Bill_User_ID,","").replaceFirst("\\?,", "");
 		 if (getCreatedBy() == 0) sql = sql.replaceFirst("CreatedBy,","").replaceFirst("\\?,", "");
 		 if (getC_Repair_Order_ID() == 0) sql = sql.replaceFirst("C_Repair_Order_ID,","").replaceFirst("\\?,", "");
 		 if (getSalesRep_ID() == 0) sql = sql.replaceFirst("SalesRep_ID,","").replaceFirst("\\?,", "");
 		 if (getUpdatedBy() == 0) sql = sql.replaceFirst("UpdatedBy,","").replaceFirst("\\?,", "");
 		 if (getUser1_ID() == 0) sql = sql.replaceFirst("User1_ID,","").replaceFirst("\\?,", "");
 
 		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 if (getPay_BPartner_ID() != 0) pstmt.setInt(col++, getPay_BPartner_ID());
		 if (getPay_Location_ID() != 0) pstmt.setInt(col++, getPay_Location_ID());
		 pstmt.setString(col++, isDelivered()?"Y":"N");
		 pstmt.setString(col++, isProcessing()?"Y":"N");
		 if (getC_Payment_ID() != 0) pstmt.setInt(col++, getC_Payment_ID());
		 pstmt.setString(col++, isSelected()?"Y":"N");
		 if (getAD_OrgTrx_ID() != 0) pstmt.setInt(col++, getAD_OrgTrx_ID());
		 pstmt.setString(col++, isSendEMail()?"Y":"N");
		 pstmt.setString(col++, isCreditApproved()?"Y":"N");
		 pstmt.setString(col++, isInvoiced()?"Y":"N");
		 if (getC_Campaign_ID() != 0) pstmt.setInt(col++, getC_Campaign_ID());
		 pstmt.setString(col++, isTransferred()?"Y":"N");
		 if (getM_Warehouse_ID() != 0) pstmt.setInt(col++, getM_Warehouse_ID());
		 pstmt.setString(col++, isApproved()?"Y":"N");
		 if (getFreightAmt() != null) pstmt.setBigDecimal(col++, getFreightAmt());
		 if (getC_DocType_ID() != 0) pstmt.setInt(col++, getC_DocType_ID());
		 if (getTotalLines() != null) pstmt.setBigDecimal(col++, getTotalLines());
		 if (getGrandTotal() != null) pstmt.setBigDecimal(col++, getGrandTotal());
		 if (getDeliveryViaRule() != null) pstmt.setString(col++, getDeliveryViaRule());
		 if (getInvoiceRule() != null) pstmt.setString(col++, getInvoiceRule());
		 if (getPOReference() != null) pstmt.setString(col++, getPOReference());
		 if (getDateAcct() != null) pstmt.setTimestamp(col++, getDateAcct());
		 pstmt.setString(col++, isPrinted()?"Y":"N");
		 if (getDescription() != null) pstmt.setString(col++, getDescription());
		 pstmt.setString(col++, isActive()?"Y":"N");
		 if (getC_Charge_ID() != 0) pstmt.setInt(col++, getC_Charge_ID());
		 pstmt.setString(col++, isDropShip()?"Y":"N");
		 if (getC_Project_ID() != 0) pstmt.setInt(col++, getC_Project_ID());
		 if (getDeliveryRule() != null) pstmt.setString(col++, getDeliveryRule());
		 if (getAD_Org_ID() != 0) pstmt.setInt(col++, getAD_Org_ID());
		 if (getC_PaymentTerm_ID() != 0) pstmt.setInt(col++, getC_PaymentTerm_ID());
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 if (getPriorityRule() != null) pstmt.setString(col++, getPriorityRule());
		 if (getAD_User_ID() != 0) pstmt.setInt(col++, getAD_User_ID());
		 if (getUpdated() != null) pstmt.setTimestamp(col++, getUpdated());
		 if (getChargeAmt() != null) pstmt.setBigDecimal(col++, getChargeAmt());
		 if (getFreightCostRule() != null) pstmt.setString(col++, getFreightCostRule());
		 if (getC_CashLine_ID() != 0) pstmt.setInt(col++, getC_CashLine_ID());
		 if (getC_Activity_ID() != 0) pstmt.setInt(col++, getC_Activity_ID());
		 pstmt.setString(col++, isPosted()?"Y":"N");
		 pstmt.setString(col++, isSOTrx()?"Y":"N");
		 if (getAD_Client_ID() != 0) pstmt.setInt(col++, getAD_Client_ID());
		 pstmt.setString(col++, isDiscountPrinted()?"Y":"N");
		 if (getDatePrinted() != null) pstmt.setTimestamp(col++, getDatePrinted());
		 if (getC_Order_ID() != 0) pstmt.setInt(col++, getC_Order_ID());
		 pstmt.setString(col++, isTaxIncluded()?"Y":"N");
		 if (getC_Currency_ID() != 0) pstmt.setInt(col++, getC_Currency_ID());
		 pstmt.setString(col++, isProcessed()?"Y":"N");
		 pstmt.setString(col++, isSelfService()?"Y":"N");
		 if (getCopyFrom() != null) pstmt.setString(col++, getCopyFrom());
		 if (getBill_Location_ID() != 0) pstmt.setInt(col++, getBill_Location_ID());
		 if (getC_ConversionType_ID() != 0) pstmt.setInt(col++, getC_ConversionType_ID());
		 if (getBill_BPartner_ID() != 0) pstmt.setInt(col++, getBill_BPartner_ID());
		 if (getDocAction() != null) pstmt.setString(col++, getDocAction());
		 if (getrepair_priority() != null) pstmt.setString(col++, getrepair_priority());
		 if (getrepair_state() != null) pstmt.setString(col++, getrepair_state());
		 if (getdateprod() != null) pstmt.setTimestamp(col++, getdateprod());
		 if (getdaterealprod() != null) pstmt.setTimestamp(col++, getdaterealprod());
		 if (getAcceptance() != null) pstmt.setBigDecimal(col++, getAcceptance());
		 if (getM_Shipper_ID() != 0) pstmt.setInt(col++, getM_Shipper_ID());
		 if (getCreateVendorProductLines() != null) pstmt.setString(col++, getCreateVendorProductLines());
		 if (getPaymentRule() != null) pstmt.setString(col++, getPaymentRule());
		 if (getM_PriceList_ID() != 0) pstmt.setInt(col++, getM_PriceList_ID());
		 if (getDateOrdered() != null) pstmt.setTimestamp(col++, getDateOrdered());
		 if (getDatePromised() != null) pstmt.setTimestamp(col++, getDatePromised());
		 if (getDocStatus() != null) pstmt.setString(col++, getDocStatus());
		 if (getC_BPartner_ID() != 0) pstmt.setInt(col++, getC_BPartner_ID());
		 if (getValidTo() != null) pstmt.setTimestamp(col++, getValidTo());
		 pstmt.setString(col++, isExchange()?"Y":"N");
		 if (getNombreCli() != null) pstmt.setString(col++, getNombreCli());
		 if (getNroIdentificCliente() != null) pstmt.setString(col++, getNroIdentificCliente());
		 if (getInvoice_Adress() != null) pstmt.setString(col++, getInvoice_Adress());
		 if (getCUIT() != null) pstmt.setString(col++, getCUIT());
		 if (getM_Warehouse_Transfer_ID() != 0) pstmt.setInt(col++, getM_Warehouse_Transfer_ID());
		 if (getAD_Org_Transfer_ID() != 0) pstmt.setInt(col++, getAD_Org_Transfer_ID());
		 if (getDocumentNo() != null) pstmt.setString(col++, getDocumentNo());
		 if (getC_DocTypeTarget_ID() != 0) pstmt.setInt(col++, getC_DocTypeTarget_ID());
		 pstmt.setString(col++, isTpvUsed()?"Y":"N");
		 if (getprogram_invoice() != 0) pstmt.setInt(col++, getprogram_invoice());
		 if (getC_BPartner_Location_ID() != 0) pstmt.setInt(col++, getC_BPartner_Location_ID());
		 if (getM_AuthorizationChain_ID() != 0) pstmt.setInt(col++, getM_AuthorizationChain_ID());
		 if (getAuthorize() != null) pstmt.setString(col++, getAuthorize());
		 if (getOldGrandTotal() != null) pstmt.setBigDecimal(col++, getOldGrandTotal());
		 if (getRef_Order_ID() != 0) pstmt.setInt(col++, getRef_Order_ID());
		 if (getUser2_ID() != 0) pstmt.setInt(col++, getUser2_ID());
		 if (getBill_User_ID() != 0) pstmt.setInt(col++, getBill_User_ID());
		 if (getCreatedBy() != 0) pstmt.setInt(col++, getCreatedBy());
		 if (getC_Repair_Order_ID() != 0) pstmt.setInt(col++, getC_Repair_Order_ID());
		 if (getSalesRep_ID() != 0) pstmt.setInt(col++, getSalesRep_ID());
		 if (getUpdatedBy() != 0) pstmt.setInt(col++, getUpdatedBy());
		 if (getUser1_ID() != 0) pstmt.setInt(col++, getUser1_ID());

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

}
