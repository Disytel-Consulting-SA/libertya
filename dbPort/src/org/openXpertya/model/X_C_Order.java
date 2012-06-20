/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Order
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-01-28 16:12:22.305 */
public class X_C_Order extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Order (Properties ctx, int C_Order_ID, String trxName)
{
super (ctx, C_Order_ID, trxName);
/** if (C_Order_ID == 0)
{
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
setIsInvoiced (false);
setIsPrinted (false);
setIsSelected (false);
setIsSelfService (false);
setIsSOTrx (false);	// @IsSOTrx@
setIsTaxIncluded (false);
setIsTransferred (false);
setM_PriceList_ID (0);
setM_Warehouse_ID (0);
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
public static final int BILL_BPARTNER_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner (No Summary)");
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
public static final int C_BPARTNER_LOCATION_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner_Location_V");
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
/** Set Delivery Rule.
Defines the timing of Delivery */
public void setDeliveryRule (String DeliveryRule)
{
if (DeliveryRule.equals("A") || DeliveryRule.equals("O") || DeliveryRule.equals("R") || DeliveryRule.equals("L") || DeliveryRule.equals("F"));
 else throw new IllegalArgumentException ("DeliveryRule Invalid value - Reference = DELIVERYRULE_AD_Reference_ID - A - O - R - L - F");
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
set_ValueNoCheck ("DocumentNo", DocumentNo);
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
public static final int PROGRAM_INVOICE_AD_Reference_ID = MReference.getReferenceID("c_invoicet");
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
}
