/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_InOut
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-12-30 19:21:22.086 */
public class X_M_InOut extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_InOut (Properties ctx, int M_InOut_ID, String trxName)
{
super (ctx, M_InOut_ID, trxName);
/** if (M_InOut_ID == 0)
{
setC_BPartner_ID (0);
setC_BPartner_Location_ID (0);
setC_DocType_ID (0);
setDateAcct (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDeliveryRule (null);	// A
setDeliveryViaRule (null);	// P
setDocAction (null);	// CO
setDocStatus (null);	// DR
setDocumentNo (null);
setFreightCostRule (null);	// I
setIsApproved (false);
setIsInDispute (false);
setIsInTransit (false);
setIsPrinted (false);
setIsSOTrx (false);	// @IsSOTrx@
setM_InOut_ID (0);
setMovementDate (new Timestamp(System.currentTimeMillis()));	// @#Date@
setMovementType (null);
setM_Warehouse_ID (0);
setPosted (false);
setPriorityRule (null);	// 5
setProcessed (false);
setSendEMail (false);
}
 */
}
/** Load Constructor */
public X_M_InOut (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_InOut");

/** TableName=M_InOut */
public static final String Table_Name="M_InOut";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_InOut");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_InOut[").append(getID()).append("]");
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
if (C_Invoice_ID <= 0) set_ValueNoCheck ("C_Invoice_ID", null);
 else 
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
/** Set Create Confirm */
public void setCreateConfirm (String CreateConfirm)
{
if (CreateConfirm != null && CreateConfirm.length() > 1)
{
log.warning("Length > 1 - truncated");
CreateConfirm = CreateConfirm.substring(0,1);
}
set_Value ("CreateConfirm", CreateConfirm);
}
/** Get Create Confirm */
public String getCreateConfirm() 
{
return (String)get_Value("CreateConfirm");
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
/** Set Create Package */
public void setCreatePackage (String CreatePackage)
{
if (CreatePackage != null && CreatePackage.length() > 1)
{
log.warning("Length > 1 - truncated");
CreatePackage = CreatePackage.substring(0,1);
}
set_Value ("CreatePackage", CreatePackage);
}
/** Get Create Package */
public String getCreatePackage() 
{
return (String)get_Value("CreatePackage");
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
/** Set Date received.
Date a product was received */
public void setDateReceived (Timestamp DateReceived)
{
set_Value ("DateReceived", DateReceived);
}
/** Get Date received.
Date a product was received */
public Timestamp getDateReceived() 
{
return (Timestamp)get_Value("DateReceived");
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
/** Set InOutDate */
public void setInOutDate (Timestamp InOutDate)
{
set_Value ("InOutDate", InOutDate);
}
/** Get InOutDate */
public Timestamp getInOutDate() 
{
return (Timestamp)get_Value("InOutDate");
}
/** Set InOutReceptionDate */
public void setInOutReceptionDate (Timestamp InOutReceptionDate)
{
set_Value ("InOutReceptionDate", InOutReceptionDate);
}
/** Get InOutReceptionDate */
public Timestamp getInOutReceptionDate() 
{
return (Timestamp)get_Value("InOutReceptionDate");
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
/** Set In Transit.
Movement is in transit */
public void setIsInTransit (boolean IsInTransit)
{
set_Value ("IsInTransit", new Boolean(IsInTransit));
}
/** Get In Transit.
Movement is in transit */
public boolean isInTransit() 
{
Object oo = get_Value("IsInTransit");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set isintrastat */
public void setisintrastat (boolean isintrastat)
{
set_Value ("isintrastat", new Boolean(isintrastat));
}
/** Get isintrastat */
public boolean isintrastat() 
{
Object oo = get_Value("isintrastat");
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
/** Set m_incotermcode_id */
public void setm_incotermcode_id (int m_incotermcode_id)
{
set_Value ("m_incotermcode_id", new Integer(m_incotermcode_id));
}
/** Get m_incotermcode_id */
public int getm_incotermcode_id() 
{
Integer ii = (Integer)get_Value("m_incotermcode_id");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipment/Receipt.
Material Shipment Document */
public void setM_InOut_ID (int M_InOut_ID)
{
set_ValueNoCheck ("M_InOut_ID", new Integer(M_InOut_ID));
}
/** Get Shipment/Receipt.
Material Shipment Document */
public int getM_InOut_ID() 
{
Integer ii = (Integer)get_Value("M_InOut_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Movement Date.
Date a product was moved in or out of inventory */
public void setMovementDate (Timestamp MovementDate)
{
if (MovementDate == null) throw new IllegalArgumentException ("MovementDate is mandatory");
set_Value ("MovementDate", MovementDate);
}
/** Get Movement Date.
Date a product was moved in or out of inventory */
public Timestamp getMovementDate() 
{
return (Timestamp)get_Value("MovementDate");
}
public static final int MOVEMENTTYPE_AD_Reference_ID = MReference.getReferenceID("M_Transaction Movement Type");
/** Vendor Receipts = V+ */
public static final String MOVEMENTTYPE_VendorReceipts = "V+";
/** Vendor Returns = V- */
public static final String MOVEMENTTYPE_VendorReturns = "V-";
/** Inventory Out = I- */
public static final String MOVEMENTTYPE_InventoryOut = "I-";
/** Inventory In = I+ */
public static final String MOVEMENTTYPE_InventoryIn = "I+";
/** Movement From = M- */
public static final String MOVEMENTTYPE_MovementFrom = "M-";
/** Movement To = M+ */
public static final String MOVEMENTTYPE_MovementTo = "M+";
/** Customer Shipment = C- */
public static final String MOVEMENTTYPE_CustomerShipment = "C-";
/** Customer Returns = C+ */
public static final String MOVEMENTTYPE_CustomerReturns = "C+";
/** Work Order + = W+ */
public static final String MOVEMENTTYPE_WorkOrderPlus = "W+";
/** Work Order - = W- */
public static final String MOVEMENTTYPE_WorkOrder_ = "W-";
/** Production + = P+ */
public static final String MOVEMENTTYPE_ProductionPlus = "P+";
/** Production - = P- */
public static final String MOVEMENTTYPE_Production_ = "P-";
/** Set Movement Type.
Method of moving the inventory */
public void setMovementType (String MovementType)
{
if (MovementType.equals("V+") || MovementType.equals("V-") || MovementType.equals("I-") || MovementType.equals("I+") || MovementType.equals("M-") || MovementType.equals("M+") || MovementType.equals("C-") || MovementType.equals("C+") || MovementType.equals("W+") || MovementType.equals("W-") || MovementType.equals("P+") || MovementType.equals("P-"));
 else throw new IllegalArgumentException ("MovementType Invalid value - Reference = MOVEMENTTYPE_AD_Reference_ID - V+ - V- - I- - I+ - M- - M+ - C- - C+ - W+ - W- - P+ - P-");
if (MovementType == null) throw new IllegalArgumentException ("MovementType is mandatory");
if (MovementType.length() > 2)
{
log.warning("Length > 2 - truncated");
MovementType = MovementType.substring(0,2);
}
set_ValueNoCheck ("MovementType", MovementType);
}
/** Get Movement Type.
Method of moving the inventory */
public String getMovementType() 
{
return (String)get_Value("MovementType");
}
/** Set m_portaircode_id */
public void setm_portaircode_id (int m_portaircode_id)
{
set_Value ("m_portaircode_id", new Integer(m_portaircode_id));
}
/** Get m_portaircode_id */
public int getm_portaircode_id() 
{
Integer ii = (Integer)get_Value("m_portaircode_id");
if (ii == null) return 0;
return ii.intValue();
}
/** Set m_shipmentcode_id */
public void setm_shipmentcode_id (int m_shipmentcode_id)
{
set_Value ("m_shipmentcode_id", new Integer(m_shipmentcode_id));
}
/** Get m_shipmentcode_id */
public int getm_shipmentcode_id() 
{
Integer ii = (Integer)get_Value("m_shipmentcode_id");
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
/** Set m_statetermscode_id */
public void setm_statetermscode_id (int m_statetermscode_id)
{
set_Value ("m_statetermscode_id", new Integer(m_statetermscode_id));
}
/** Get m_statetermscode_id */
public int getm_statetermscode_id() 
{
Integer ii = (Integer)get_Value("m_statetermscode_id");
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
/** Set No Packages.
Number of packages shipped */
public void setNoPackages (int NoPackages)
{
set_Value ("NoPackages", new Integer(NoPackages));
}
/** Get No Packages.
Number of packages shipped */
public int getNoPackages() 
{
Integer ii = (Integer)get_Value("NoPackages");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Pick Date.
Date/Time when picked for Shipment */
public void setPickDate (Timestamp PickDate)
{
set_Value ("PickDate", PickDate);
}
/** Get Pick Date.
Date/Time when picked for Shipment */
public Timestamp getPickDate() 
{
return (Timestamp)get_Value("PickDate");
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
/** Set PrePrinted Document No */
public void setPrePrinted_DocNo (String PrePrinted_DocNo)
{
if (PrePrinted_DocNo != null && PrePrinted_DocNo.length() > 100)
{
log.warning("Length > 100 - truncated");
PrePrinted_DocNo = PrePrinted_DocNo.substring(0,100);
}
set_Value ("PrePrinted_DocNo", PrePrinted_DocNo);
}
/** Get PrePrinted Document No */
public String getPrePrinted_DocNo() 
{
return (String)get_Value("PrePrinted_DocNo");
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
/** Set ReceptionDate */
public void setReceptionDate (Timestamp ReceptionDate)
{
set_Value ("ReceptionDate", ReceptionDate);
}
/** Get ReceptionDate */
public Timestamp getReceptionDate() 
{
return (Timestamp)get_Value("ReceptionDate");
}
public static final int REF_INOUT_ID_AD_Reference_ID = MReference.getReferenceID("M_InOut");
/** Set Referenced Shipment */
public void setRef_InOut_ID (int Ref_InOut_ID)
{
if (Ref_InOut_ID <= 0) set_Value ("Ref_InOut_ID", null);
 else 
set_Value ("Ref_InOut_ID", new Integer(Ref_InOut_ID));
}
/** Get Referenced Shipment */
public int getRef_InOut_ID() 
{
Integer ii = (Integer)get_Value("Ref_InOut_ID");
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
/** Set Ship Date.
Shipment Date/Time */
public void setShipDate (Timestamp ShipDate)
{
set_Value ("ShipDate", ShipDate);
}
/** Get Ship Date.
Shipment Date/Time */
public Timestamp getShipDate() 
{
return (Timestamp)get_Value("ShipDate");
}
/** Set Tracking No.
Number to track the shipment */
public void setTrackingNo (String TrackingNo)
{
if (TrackingNo != null && TrackingNo.length() > 60)
{
log.warning("Length > 60 - truncated");
TrackingNo = TrackingNo.substring(0,60);
}
set_Value ("TrackingNo", TrackingNo);
}
/** Get Tracking No.
Number to track the shipment */
public String getTrackingNo() 
{
return (String)get_Value("TrackingNo");
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

public boolean insertDirect() 
{
 
try 
{
 
 		 String sql = " INSERT INTO M_InOut(AD_Client_ID,AD_Org_ID,AD_OrgTrx_ID,AD_User_ID,C_Activity_ID,C_BPartner_ID,C_BPartner_Location_ID,C_Campaign_ID,C_Charge_ID,C_DocType_ID,ChargeAmt,C_Invoice_ID,C_Order_ID,C_Project_ID,CreateConfirm,Created,CreatedBy,CreateFrom,CreatePackage,DateAcct,DateOrdered,DatePrinted,DateReceived,DeliveryRule,DeliveryViaRule,Description,DocAction,DocStatus,DocumentNo,FreightAmt,FreightCostRule,GenerateTo,InOutDate,InOutReceptionDate,IsActive,IsApproved,IsInDispute,IsInTransit,isintrastat,IsPrinted,IsSOTrx,m_incotermcode_id,M_InOut_ID,MovementDate,MovementType,m_portaircode_id,m_shipmentcode_id,M_Shipper_ID,m_statetermscode_id,M_Warehouse_ID,NoPackages,PickDate,POReference,Posted,PrePrinted_DocNo,PrintType,PriorityRule,Processed,Processing,ReceptionDate,Ref_InOut_ID,SalesRep_ID,SendEMail,ShipDate,TrackingNo,Updated,UpdatedBy,User1_ID,User2_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		 if (getAD_Client_ID() == 0) sql = sql.replaceFirst("AD_Client_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_Org_ID() == 0) sql = sql.replaceFirst("AD_Org_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_OrgTrx_ID() == 0) sql = sql.replaceFirst("AD_OrgTrx_ID,","").replaceFirst("\\?,", "");
 		 if (getAD_User_ID() == 0) sql = sql.replaceFirst("AD_User_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Activity_ID() == 0) sql = sql.replaceFirst("C_Activity_ID,","").replaceFirst("\\?,", "");
 		 if (getC_BPartner_ID() == 0) sql = sql.replaceFirst("C_BPartner_ID,","").replaceFirst("\\?,", "");
 		 if (getC_BPartner_Location_ID() == 0) sql = sql.replaceFirst("C_BPartner_Location_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Campaign_ID() == 0) sql = sql.replaceFirst("C_Campaign_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Charge_ID() == 0) sql = sql.replaceFirst("C_Charge_ID,","").replaceFirst("\\?,", "");
 		 if (getC_DocType_ID() == 0) sql = sql.replaceFirst("C_DocType_ID,","").replaceFirst("\\?,", "");
 		 if (getChargeAmt() == null) sql = sql.replaceFirst("ChargeAmt,","").replaceFirst("\\?,", "");
 		 if (getC_Invoice_ID() == 0) sql = sql.replaceFirst("C_Invoice_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Order_ID() == 0) sql = sql.replaceFirst("C_Order_ID,","").replaceFirst("\\?,", "");
 		 if (getC_Project_ID() == 0) sql = sql.replaceFirst("C_Project_ID,","").replaceFirst("\\?,", "");
 		 if (getCreateConfirm() == null) sql = sql.replaceFirst("CreateConfirm,","").replaceFirst("\\?,", "");
 		 if (getCreated() == null) sql = sql.replaceFirst("Created,","").replaceFirst("\\?,", "");
 		 if (getCreatedBy() == 0) sql = sql.replaceFirst("CreatedBy,","").replaceFirst("\\?,", "");
 		 if (getCreateFrom() == null) sql = sql.replaceFirst("CreateFrom,","").replaceFirst("\\?,", "");
 		 if (getCreatePackage() == null) sql = sql.replaceFirst("CreatePackage,","").replaceFirst("\\?,", "");
 		 if (getDateAcct() == null) sql = sql.replaceFirst("DateAcct,","").replaceFirst("\\?,", "");
 		 if (getDateOrdered() == null) sql = sql.replaceFirst("DateOrdered,","").replaceFirst("\\?,", "");
 		 if (getDatePrinted() == null) sql = sql.replaceFirst("DatePrinted,","").replaceFirst("\\?,", "");
 		 if (getDateReceived() == null) sql = sql.replaceFirst("DateReceived,","").replaceFirst("\\?,", "");
 		 if (getDeliveryRule() == null) sql = sql.replaceFirst("DeliveryRule,","").replaceFirst("\\?,", "");
 		 if (getDeliveryViaRule() == null) sql = sql.replaceFirst("DeliveryViaRule,","").replaceFirst("\\?,", "");
 		 if (getDescription() == null) sql = sql.replaceFirst("Description,","").replaceFirst("\\?,", "");
 		 if (getDocAction() == null) sql = sql.replaceFirst("DocAction,","").replaceFirst("\\?,", "");
 		 if (getDocStatus() == null) sql = sql.replaceFirst("DocStatus,","").replaceFirst("\\?,", "");
 		 if (getDocumentNo() == null) sql = sql.replaceFirst("DocumentNo,","").replaceFirst("\\?,", "");
 		 if (getFreightAmt() == null) sql = sql.replaceFirst("FreightAmt,","").replaceFirst("\\?,", "");
 		 if (getFreightCostRule() == null) sql = sql.replaceFirst("FreightCostRule,","").replaceFirst("\\?,", "");
 		 if (getGenerateTo() == null) sql = sql.replaceFirst("GenerateTo,","").replaceFirst("\\?,", "");
 		 if (getInOutDate() == null) sql = sql.replaceFirst("InOutDate,","").replaceFirst("\\?,", "");
 		 if (getInOutReceptionDate() == null) sql = sql.replaceFirst("InOutReceptionDate,","").replaceFirst("\\?,", "");
 		 if (getm_incotermcode_id() == 0) sql = sql.replaceFirst("m_incotermcode_id,","").replaceFirst("\\?,", "");
 		 if (getM_InOut_ID() == 0) sql = sql.replaceFirst("M_InOut_ID,","").replaceFirst("\\?,", "");
 		 if (getMovementDate() == null) sql = sql.replaceFirst("MovementDate,","").replaceFirst("\\?,", "");
 		 if (getMovementType() == null) sql = sql.replaceFirst("MovementType,","").replaceFirst("\\?,", "");
 		 if (getm_portaircode_id() == 0) sql = sql.replaceFirst("m_portaircode_id,","").replaceFirst("\\?,", "");
 		 if (getm_shipmentcode_id() == 0) sql = sql.replaceFirst("m_shipmentcode_id,","").replaceFirst("\\?,", "");
 		 if (getM_Shipper_ID() == 0) sql = sql.replaceFirst("M_Shipper_ID,","").replaceFirst("\\?,", "");
 		 if (getm_statetermscode_id() == 0) sql = sql.replaceFirst("m_statetermscode_id,","").replaceFirst("\\?,", "");
 		 if (getM_Warehouse_ID() == 0) sql = sql.replaceFirst("M_Warehouse_ID,","").replaceFirst("\\?,", "");
 		 if (getNoPackages() == 0) sql = sql.replaceFirst("NoPackages,","").replaceFirst("\\?,", "");
 		 if (getPickDate() == null) sql = sql.replaceFirst("PickDate,","").replaceFirst("\\?,", "");
 		 if (getPOReference() == null) sql = sql.replaceFirst("POReference,","").replaceFirst("\\?,", "");
 		 if (getPrePrinted_DocNo() == null) sql = sql.replaceFirst("PrePrinted_DocNo,","").replaceFirst("\\?,", "");
 		 if (getPrintType() == null) sql = sql.replaceFirst("PrintType,","").replaceFirst("\\?,", "");
 		 if (getPriorityRule() == null) sql = sql.replaceFirst("PriorityRule,","").replaceFirst("\\?,", "");
 		 if (getReceptionDate() == null) sql = sql.replaceFirst("ReceptionDate,","").replaceFirst("\\?,", "");
 		 if (getRef_InOut_ID() == 0) sql = sql.replaceFirst("Ref_InOut_ID,","").replaceFirst("\\?,", "");
 		 if (getSalesRep_ID() == 0) sql = sql.replaceFirst("SalesRep_ID,","").replaceFirst("\\?,", "");
 		 if (getShipDate() == null) sql = sql.replaceFirst("ShipDate,","").replaceFirst("\\?,", "");
 		 if (getTrackingNo() == null) sql = sql.replaceFirst("TrackingNo,","").replaceFirst("\\?,", "");
 		 if (getUpdated() == null) sql = sql.replaceFirst("Updated,","").replaceFirst("\\?,", "");
 		 if (getUpdatedBy() == 0) sql = sql.replaceFirst("UpdatedBy,","").replaceFirst("\\?,", "");
 		 if (getUser1_ID() == 0) sql = sql.replaceFirst("User1_ID,","").replaceFirst("\\?,", "");
 		 if (getUser2_ID() == 0) sql = sql.replaceFirst("User2_ID,","").replaceFirst("\\?,", "");
 
 		 int col = 1;
 
		 CPreparedStatement pstmt = new CPreparedStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE, sql, get_TrxName(), true);
 
		 if (getAD_Client_ID() != 0) pstmt.setInt(col++, getAD_Client_ID());
		 if (getAD_Org_ID() != 0) pstmt.setInt(col++, getAD_Org_ID());
		 if (getAD_OrgTrx_ID() != 0) pstmt.setInt(col++, getAD_OrgTrx_ID());
		 if (getAD_User_ID() != 0) pstmt.setInt(col++, getAD_User_ID());
		 if (getC_Activity_ID() != 0) pstmt.setInt(col++, getC_Activity_ID());
		 if (getC_BPartner_ID() != 0) pstmt.setInt(col++, getC_BPartner_ID());
		 if (getC_BPartner_Location_ID() != 0) pstmt.setInt(col++, getC_BPartner_Location_ID());
		 if (getC_Campaign_ID() != 0) pstmt.setInt(col++, getC_Campaign_ID());
		 if (getC_Charge_ID() != 0) pstmt.setInt(col++, getC_Charge_ID());
		 if (getC_DocType_ID() != 0) pstmt.setInt(col++, getC_DocType_ID());
		 if (getChargeAmt() != null) pstmt.setBigDecimal(col++, getChargeAmt());
		 if (getC_Invoice_ID() != 0) pstmt.setInt(col++, getC_Invoice_ID());
		 if (getC_Order_ID() != 0) pstmt.setInt(col++, getC_Order_ID());
		 if (getC_Project_ID() != 0) pstmt.setInt(col++, getC_Project_ID());
		 if (getCreateConfirm() != null) pstmt.setString(col++, getCreateConfirm());
		 if (getCreated() != null) pstmt.setTimestamp(col++, getCreated());
		 if (getCreatedBy() != 0) pstmt.setInt(col++, getCreatedBy());
		 if (getCreateFrom() != null) pstmt.setString(col++, getCreateFrom());
		 if (getCreatePackage() != null) pstmt.setString(col++, getCreatePackage());
		 if (getDateAcct() != null) pstmt.setTimestamp(col++, getDateAcct());
		 if (getDateOrdered() != null) pstmt.setTimestamp(col++, getDateOrdered());
		 if (getDatePrinted() != null) pstmt.setTimestamp(col++, getDatePrinted());
		 if (getDateReceived() != null) pstmt.setTimestamp(col++, getDateReceived());
		 if (getDeliveryRule() != null) pstmt.setString(col++, getDeliveryRule());
		 if (getDeliveryViaRule() != null) pstmt.setString(col++, getDeliveryViaRule());
		 if (getDescription() != null) pstmt.setString(col++, getDescription());
		 if (getDocAction() != null) pstmt.setString(col++, getDocAction());
		 if (getDocStatus() != null) pstmt.setString(col++, getDocStatus());
		 if (getDocumentNo() != null) pstmt.setString(col++, getDocumentNo());
		 if (getFreightAmt() != null) pstmt.setBigDecimal(col++, getFreightAmt());
		 if (getFreightCostRule() != null) pstmt.setString(col++, getFreightCostRule());
		 if (getGenerateTo() != null) pstmt.setString(col++, getGenerateTo());
		 if (getInOutDate() != null) pstmt.setTimestamp(col++, getInOutDate());
		 if (getInOutReceptionDate() != null) pstmt.setTimestamp(col++, getInOutReceptionDate());
		 pstmt.setString(col++, isActive()?"Y":"N");
		 pstmt.setString(col++, isApproved()?"Y":"N");
		 pstmt.setString(col++, isInDispute()?"Y":"N");
		 pstmt.setString(col++, isInTransit()?"Y":"N");
		 pstmt.setString(col++, isintrastat()?"Y":"N");
		 pstmt.setString(col++, isPrinted()?"Y":"N");
		 pstmt.setString(col++, isSOTrx()?"Y":"N");
		 if (getm_incotermcode_id() != 0) pstmt.setInt(col++, getm_incotermcode_id());
		 if (getM_InOut_ID() != 0) pstmt.setInt(col++, getM_InOut_ID());
		 if (getMovementDate() != null) pstmt.setTimestamp(col++, getMovementDate());
		 if (getMovementType() != null) pstmt.setString(col++, getMovementType());
		 if (getm_portaircode_id() != 0) pstmt.setInt(col++, getm_portaircode_id());
		 if (getm_shipmentcode_id() != 0) pstmt.setInt(col++, getm_shipmentcode_id());
		 if (getM_Shipper_ID() != 0) pstmt.setInt(col++, getM_Shipper_ID());
		 if (getm_statetermscode_id() != 0) pstmt.setInt(col++, getm_statetermscode_id());
		 if (getM_Warehouse_ID() != 0) pstmt.setInt(col++, getM_Warehouse_ID());
		 if (getNoPackages() != 0) pstmt.setInt(col++, getNoPackages());
		 if (getPickDate() != null) pstmt.setTimestamp(col++, getPickDate());
		 if (getPOReference() != null) pstmt.setString(col++, getPOReference());
		 pstmt.setString(col++, isPosted()?"Y":"N");
		 if (getPrePrinted_DocNo() != null) pstmt.setString(col++, getPrePrinted_DocNo());
		 if (getPrintType() != null) pstmt.setString(col++, getPrintType());
		 if (getPriorityRule() != null) pstmt.setString(col++, getPriorityRule());
		 pstmt.setString(col++, isProcessed()?"Y":"N");
		 pstmt.setString(col++, isProcessing()?"Y":"N");
		 if (getReceptionDate() != null) pstmt.setTimestamp(col++, getReceptionDate());
		 if (getRef_InOut_ID() != 0) pstmt.setInt(col++, getRef_InOut_ID());
		 if (getSalesRep_ID() != 0) pstmt.setInt(col++, getSalesRep_ID());
		 pstmt.setString(col++, isSendEMail()?"Y":"N");
		 if (getShipDate() != null) pstmt.setTimestamp(col++, getShipDate());
		 if (getTrackingNo() != null) pstmt.setString(col++, getTrackingNo());
		 if (getUpdated() != null) pstmt.setTimestamp(col++, getUpdated());
		 if (getUpdatedBy() != 0) pstmt.setInt(col++, getUpdatedBy());
		 if (getUser1_ID() != 0) pstmt.setInt(col++, getUser1_ID());
		 if (getUser2_ID() != 0) pstmt.setInt(col++, getUser2_ID());

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
