/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_Request
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.953 */
public class X_R_Request extends PO
{
/** Constructor estándar */
public X_R_Request (Properties ctx, int R_Request_ID, String trxName)
{
super (ctx, R_Request_ID, trxName);
/** if (R_Request_ID == 0)
{
setConfidentialType (null);	// C
setConfidentialTypeEntry (null);	// C
setDocumentNo (null);
setDueType (null);	// 5
setIsEscalated (false);
setIsInvoiced (false);
setIsSelfService (false);	// N
setPriority (null);	// 5
setProcessed (false);
setR_RequestType_ID (0);
setR_Request_ID (0);
setRequestAmt (Env.ZERO);
setSalesRep_ID (0);	// @AD_User_ID@
setSummary (null);
}
 */
}
/** Load Constructor */
public X_R_Request (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=417 */
public static final int Table_ID=417;

/** TableName=R_Request */
public static final String Table_Name="R_Request";

protected static KeyNamePair Model = new KeyNamePair(417,"R_Request");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_Request[").append(getID()).append("]");
return sb.toString();
}
/** Set Role.
Responsibility Role */
public void setAD_Role_ID (int AD_Role_ID)
{
if (AD_Role_ID <= 0) set_Value ("AD_Role_ID", null);
 else 
set_Value ("AD_Role_ID", new Integer(AD_Role_ID));
}
/** Get Role.
Responsibility Role */
public int getAD_Role_ID() 
{
Integer ii = (Integer)get_Value("AD_Role_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
if (AD_Table_ID <= 0) set_ValueNoCheck ("AD_Table_ID", null);
 else 
set_ValueNoCheck ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
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
/** Set Asset.
Asset used internally or by customers */
public void setA_Asset_ID (int A_Asset_ID)
{
if (A_Asset_ID <= 0) set_Value ("A_Asset_ID", null);
 else 
set_Value ("A_Asset_ID", new Integer(A_Asset_ID));
}
/** Get Asset.
Asset used internally or by customers */
public int getA_Asset_ID() 
{
Integer ii = (Integer)get_Value("A_Asset_ID");
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
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
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
public static final int C_INVOICEREQUEST_ID_AD_Reference_ID=336;
/** Set Request Invoice.
The generated invoice for this request */
public void setC_InvoiceRequest_ID (int C_InvoiceRequest_ID)
{
if (C_InvoiceRequest_ID <= 0) set_ValueNoCheck ("C_InvoiceRequest_ID", null);
 else 
set_ValueNoCheck ("C_InvoiceRequest_ID", new Integer(C_InvoiceRequest_ID));
}
/** Get Request Invoice.
The generated invoice for this request */
public int getC_InvoiceRequest_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceRequest_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_Value ("C_Invoice_ID", null);
 else 
set_Value ("C_Invoice_ID", new Integer(C_Invoice_ID));
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
/** Set Close Date.
Close Date */
public void setCloseDate (Timestamp CloseDate)
{
set_Value ("CloseDate", CloseDate);
}
/** Get Close Date.
Close Date */
public Timestamp getCloseDate() 
{
return (Timestamp)get_Value("CloseDate");
}
public static final int CONFIDENTIALTYPE_AD_Reference_ID=340;
/** Public Information = A */
public static final String CONFIDENTIALTYPE_PublicInformation = "A";
/** Private Information = P */
public static final String CONFIDENTIALTYPE_PrivateInformation = "P";
/** Customer Confidential = C */
public static final String CONFIDENTIALTYPE_CustomerConfidential = "C";
/** Internal = I */
public static final String CONFIDENTIALTYPE_Internal = "I";
/** Set Confidentiality.
Type of Confidentiality */
public void setConfidentialType (String ConfidentialType)
{
if (ConfidentialType.equals("A") || ConfidentialType.equals("P") || ConfidentialType.equals("C") || ConfidentialType.equals("I"));
 else throw new IllegalArgumentException ("ConfidentialType Invalid value - Reference_ID=340 - A - P - C - I");
if (ConfidentialType == null) throw new IllegalArgumentException ("ConfidentialType is mandatory");
if (ConfidentialType.length() > 1)
{
log.warning("Length > 1 - truncated");
ConfidentialType = ConfidentialType.substring(0,0);
}
set_Value ("ConfidentialType", ConfidentialType);
}
/** Get Confidentiality.
Type of Confidentiality */
public String getConfidentialType() 
{
return (String)get_Value("ConfidentialType");
}
public static final int CONFIDENTIALTYPEENTRY_AD_Reference_ID=340;
/** Public Information = A */
public static final String CONFIDENTIALTYPEENTRY_PublicInformation = "A";
/** Private Information = P */
public static final String CONFIDENTIALTYPEENTRY_PrivateInformation = "P";
/** Customer Confidential = C */
public static final String CONFIDENTIALTYPEENTRY_CustomerConfidential = "C";
/** Internal = I */
public static final String CONFIDENTIALTYPEENTRY_Internal = "I";
/** Set Entry Confidentiality.
Confidentiality of the individual entry */
public void setConfidentialTypeEntry (String ConfidentialTypeEntry)
{
if (ConfidentialTypeEntry.equals("A") || ConfidentialTypeEntry.equals("P") || ConfidentialTypeEntry.equals("C") || ConfidentialTypeEntry.equals("I"));
 else throw new IllegalArgumentException ("ConfidentialTypeEntry Invalid value - Reference_ID=340 - A - P - C - I");
if (ConfidentialTypeEntry == null) throw new IllegalArgumentException ("ConfidentialTypeEntry is mandatory");
if (ConfidentialTypeEntry.length() > 1)
{
log.warning("Length > 1 - truncated");
ConfidentialTypeEntry = ConfidentialTypeEntry.substring(0,0);
}
set_Value ("ConfidentialTypeEntry", ConfidentialTypeEntry);
}
/** Get Entry Confidentiality.
Confidentiality of the individual entry */
public String getConfidentialTypeEntry() 
{
return (String)get_Value("ConfidentialTypeEntry");
}
/** Set Date last action.
Date this request was last acted on */
public void setDateLastAction (Timestamp DateLastAction)
{
set_ValueNoCheck ("DateLastAction", DateLastAction);
}
/** Get Date last action.
Date this request was last acted on */
public Timestamp getDateLastAction() 
{
return (Timestamp)get_Value("DateLastAction");
}
/** Set Last Alert.
Date when last alert were sent */
public void setDateLastAlert (Timestamp DateLastAlert)
{
set_Value ("DateLastAlert", DateLastAlert);
}
/** Get Last Alert.
Date when last alert were sent */
public Timestamp getDateLastAlert() 
{
return (Timestamp)get_Value("DateLastAlert");
}
/** Set Date next action.
Date that this request should be acted on */
public void setDateNextAction (Timestamp DateNextAction)
{
set_Value ("DateNextAction", DateNextAction);
}
/** Get Date next action.
Date that this request should be acted on */
public Timestamp getDateNextAction() 
{
return (Timestamp)get_Value("DateNextAction");
}
/** Set Document No.
Document sequence number of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo == null) throw new IllegalArgumentException ("DocumentNo is mandatory");
if (DocumentNo.length() > 30)
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDocumentNo());
}
public static final int DUETYPE_AD_Reference_ID=222;
/** Overdue = 3 */
public static final String DUETYPE_Overdue = "3";
/** Due = 5 */
public static final String DUETYPE_Due = "5";
/** Scheduled = 7 */
public static final String DUETYPE_Scheduled = "7";
/** Set Due type.
Status of the next action for this Request */
public void setDueType (String DueType)
{
if (DueType.equals("3") || DueType.equals("5") || DueType.equals("7"));
 else throw new IllegalArgumentException ("DueType Invalid value - Reference_ID=222 - 3 - 5 - 7");
if (DueType == null) throw new IllegalArgumentException ("DueType is mandatory");
if (DueType.length() > 1)
{
log.warning("Length > 1 - truncated");
DueType = DueType.substring(0,0);
}
set_Value ("DueType", DueType);
}
/** Get Due type.
Status of the next action for this Request */
public String getDueType() 
{
return (String)get_Value("DueType");
}
/** Set End Time.
End of the time span */
public void setEndTime (Timestamp EndTime)
{
set_Value ("EndTime", EndTime);
}
/** Get End Time.
End of the time span */
public Timestamp getEndTime() 
{
return (Timestamp)get_Value("EndTime");
}
/** Set Escalated.
This request has been escalated */
public void setIsEscalated (boolean IsEscalated)
{
set_Value ("IsEscalated", new Boolean(IsEscalated));
}
/** Get Escalated.
This request has been escalated */
public boolean isEscalated() 
{
Object oo = get_Value("IsEscalated");
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
set_Value ("IsInvoiced", new Boolean(IsInvoiced));
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
/** Set Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public void setIsSelfService (boolean IsSelfService)
{
set_ValueNoCheck ("IsSelfService", new Boolean(IsSelfService));
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
/** Set Last Result.
Result of last contact */
public void setLastResult (String LastResult)
{
if (LastResult != null && LastResult.length() > 2000)
{
log.warning("Length > 2000 - truncated");
LastResult = LastResult.substring(0,1999);
}
set_Value ("LastResult", LastResult);
}
/** Get Last Result.
Result of last contact */
public String getLastResult() 
{
return (String)get_Value("LastResult");
}
/** Set Change Request.
BOM (Engineering) Change Request */
public void setM_ChangeRequest_ID (int M_ChangeRequest_ID)
{
if (M_ChangeRequest_ID <= 0) set_Value ("M_ChangeRequest_ID", null);
 else 
set_Value ("M_ChangeRequest_ID", new Integer(M_ChangeRequest_ID));
}
/** Get Change Request.
BOM (Engineering) Change Request */
public int getM_ChangeRequest_ID() 
{
Integer ii = (Integer)get_Value("M_ChangeRequest_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Shipment/Receipt.
Material Shipment Document */
public void setM_InOut_ID (int M_InOut_ID)
{
if (M_InOut_ID <= 0) set_Value ("M_InOut_ID", null);
 else 
set_Value ("M_InOut_ID", new Integer(M_InOut_ID));
}
/** Get Shipment/Receipt.
Material Shipment Document */
public int getM_InOut_ID() 
{
Integer ii = (Integer)get_Value("M_InOut_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int M_PRODUCTSPENT_ID_AD_Reference_ID=162;
/** Set Product Used.
Product/Resource/Service used in Request */
public void setM_ProductSpent_ID (int M_ProductSpent_ID)
{
if (M_ProductSpent_ID <= 0) set_Value ("M_ProductSpent_ID", null);
 else 
set_Value ("M_ProductSpent_ID", new Integer(M_ProductSpent_ID));
}
/** Get Product Used.
Product/Resource/Service used in Request */
public int getM_ProductSpent_ID() 
{
Integer ii = (Integer)get_Value("M_ProductSpent_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
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
public static final int NEXTACTION_AD_Reference_ID=219;
/** None = N */
public static final String NEXTACTION_None = "N";
/** Follow up = F */
public static final String NEXTACTION_FollowUp = "F";
/** Set Next action.
Next Action to be taken */
public void setNextAction (String NextAction)
{
if (NextAction == null || NextAction.equals("N") || NextAction.equals("F"));
 else throw new IllegalArgumentException ("NextAction Invalid value - Reference_ID=219 - N - F");
if (NextAction != null && NextAction.length() > 1)
{
log.warning("Length > 1 - truncated");
NextAction = NextAction.substring(0,0);
}
set_Value ("NextAction", NextAction);
}
/** Get Next action.
Next Action to be taken */
public String getNextAction() 
{
return (String)get_Value("NextAction");
}
public static final int PRIORITY_AD_Reference_ID=154;
/** High = 3 */
public static final String PRIORITY_High = "3";
/** Low = 7 */
public static final String PRIORITY_Low = "7";
/** Minor = 9 */
public static final String PRIORITY_Minor = "9";
/** Medium = 5 */
public static final String PRIORITY_Medium = "5";
/** Urgent = 1 */
public static final String PRIORITY_Urgent = "1";
/** Set Priority.
Indicates if this request is of a high, medium or low priority. */
public void setPriority (String Priority)
{
if (Priority.equals("3") || Priority.equals("7") || Priority.equals("9") || Priority.equals("5") || Priority.equals("1"));
 else throw new IllegalArgumentException ("Priority Invalid value - Reference_ID=154 - 3 - 7 - 9 - 5 - 1");
if (Priority == null) throw new IllegalArgumentException ("Priority is mandatory");
if (Priority.length() > 1)
{
log.warning("Length > 1 - truncated");
Priority = Priority.substring(0,0);
}
set_Value ("Priority", Priority);
}
/** Get Priority.
Indicates if this request is of a high, medium or low priority. */
public String getPriority() 
{
return (String)get_Value("Priority");
}
public static final int PRIORITYUSER_AD_Reference_ID=154;
/** High = 3 */
public static final String PRIORITYUSER_High = "3";
/** Low = 7 */
public static final String PRIORITYUSER_Low = "7";
/** Minor = 9 */
public static final String PRIORITYUSER_Minor = "9";
/** Medium = 5 */
public static final String PRIORITYUSER_Medium = "5";
/** Urgent = 1 */
public static final String PRIORITYUSER_Urgent = "1";
/** Set User Importance.
Priority of the issue for the User */
public void setPriorityUser (String PriorityUser)
{
if (PriorityUser == null || PriorityUser.equals("3") || PriorityUser.equals("7") || PriorityUser.equals("9") || PriorityUser.equals("5") || PriorityUser.equals("1"));
 else throw new IllegalArgumentException ("PriorityUser Invalid value - Reference_ID=154 - 3 - 7 - 9 - 5 - 1");
if (PriorityUser != null && PriorityUser.length() > 1)
{
log.warning("Length > 1 - truncated");
PriorityUser = PriorityUser.substring(0,0);
}
set_Value ("PriorityUser", PriorityUser);
}
/** Get User Importance.
Priority of the issue for the User */
public String getPriorityUser() 
{
return (String)get_Value("PriorityUser");
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
/** Set Invoiced Quantity.
Invoiced Quantity */
public void setQtyInvoiced (BigDecimal QtyInvoiced)
{
set_Value ("QtyInvoiced", QtyInvoiced);
}
/** Get Invoiced Quantity.
Invoiced Quantity */
public BigDecimal getQtyInvoiced() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyInvoiced");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Quantity Used.
Quantity used for this event */
public void setQtySpent (BigDecimal QtySpent)
{
set_Value ("QtySpent", QtySpent);
}
/** Get Quantity Used.
Quantity used for this event */
public BigDecimal getQtySpent() 
{
BigDecimal bd = (BigDecimal)get_Value("QtySpent");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Category.
Request Category */
public void setR_Category_ID (int R_Category_ID)
{
if (R_Category_ID <= 0) set_Value ("R_Category_ID", null);
 else 
set_Value ("R_Category_ID", new Integer(R_Category_ID));
}
/** Get Category.
Request Category */
public int getR_Category_ID() 
{
Integer ii = (Integer)get_Value("R_Category_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Group.
Request Group */
public void setR_Group_ID (int R_Group_ID)
{
if (R_Group_ID <= 0) set_Value ("R_Group_ID", null);
 else 
set_Value ("R_Group_ID", new Integer(R_Group_ID));
}
/** Get Group.
Request Group */
public int getR_Group_ID() 
{
Integer ii = (Integer)get_Value("R_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Mail Template.
Text templates for mailings */
public void setR_MailText_ID (int R_MailText_ID)
{
if (R_MailText_ID <= 0) set_Value ("R_MailText_ID", null);
 else 
set_Value ("R_MailText_ID", new Integer(R_MailText_ID));
}
/** Get Mail Template.
Text templates for mailings */
public int getR_MailText_ID() 
{
Integer ii = (Integer)get_Value("R_MailText_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int R_REQUESTRELATED_ID_AD_Reference_ID=341;
/** Set Related Request.
Related Request (Master Issue, ..) */
public void setR_RequestRelated_ID (int R_RequestRelated_ID)
{
if (R_RequestRelated_ID <= 0) set_Value ("R_RequestRelated_ID", null);
 else 
set_Value ("R_RequestRelated_ID", new Integer(R_RequestRelated_ID));
}
/** Get Related Request.
Related Request (Master Issue, ..) */
public int getR_RequestRelated_ID() 
{
Integer ii = (Integer)get_Value("R_RequestRelated_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Request Type.
Type of request (e.g. Inquiry, Complaint, ..) */
public void setR_RequestType_ID (int R_RequestType_ID)
{
set_Value ("R_RequestType_ID", new Integer(R_RequestType_ID));
}
/** Get Request Type.
Type of request (e.g. Inquiry, Complaint, ..) */
public int getR_RequestType_ID() 
{
Integer ii = (Integer)get_Value("R_RequestType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Request.
Request from a Business Partner or Prospect */
public void setR_Request_ID (int R_Request_ID)
{
set_ValueNoCheck ("R_Request_ID", new Integer(R_Request_ID));
}
/** Get Request.
Request from a Business Partner or Prospect */
public int getR_Request_ID() 
{
Integer ii = (Integer)get_Value("R_Request_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Resolution.
Request Resolution */
public void setR_Resolution_ID (int R_Resolution_ID)
{
if (R_Resolution_ID <= 0) set_Value ("R_Resolution_ID", null);
 else 
set_Value ("R_Resolution_ID", new Integer(R_Resolution_ID));
}
/** Get Resolution.
Request Resolution */
public int getR_Resolution_ID() 
{
Integer ii = (Integer)get_Value("R_Resolution_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Standard Response.
Request Standard Response  */
public void setR_StandardResponse_ID (int R_StandardResponse_ID)
{
if (R_StandardResponse_ID <= 0) set_Value ("R_StandardResponse_ID", null);
 else 
set_Value ("R_StandardResponse_ID", new Integer(R_StandardResponse_ID));
}
/** Get Standard Response.
Request Standard Response  */
public int getR_StandardResponse_ID() 
{
Integer ii = (Integer)get_Value("R_StandardResponse_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Status.
Request Status */
public void setR_Status_ID (int R_Status_ID)
{
if (R_Status_ID <= 0) set_Value ("R_Status_ID", null);
 else 
set_Value ("R_Status_ID", new Integer(R_Status_ID));
}
/** Get Status.
Request Status */
public int getR_Status_ID() 
{
Integer ii = (Integer)get_Value("R_Status_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Record ID.
Direct internal record ID */
public void setRecord_ID (int Record_ID)
{
if (Record_ID <= 0) set_ValueNoCheck ("Record_ID", null);
 else 
set_ValueNoCheck ("Record_ID", new Integer(Record_ID));
}
/** Get Record ID.
Direct internal record ID */
public int getRecord_ID() 
{
Integer ii = (Integer)get_Value("Record_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Request Amount.
Amount associated with this request */
public void setRequestAmt (BigDecimal RequestAmt)
{
if (RequestAmt == null) throw new IllegalArgumentException ("RequestAmt is mandatory");
set_Value ("RequestAmt", RequestAmt);
}
/** Get Request Amount.
Amount associated with this request */
public BigDecimal getRequestAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("RequestAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Result.
Result of the action taken */
public void setResult (String Result)
{
if (Result != null && Result.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Result = Result.substring(0,1999);
}
set_Value ("Result", Result);
}
/** Get Result.
Result of the action taken */
public String getResult() 
{
return (String)get_Value("Result");
}
public static final int SALESREP_ID_AD_Reference_ID=286;
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
/** Set Start Date.
First effective day (inclusive) */
public void setStartDate (Timestamp StartDate)
{
set_Value ("StartDate", StartDate);
}
/** Get Start Date.
First effective day (inclusive) */
public Timestamp getStartDate() 
{
return (Timestamp)get_Value("StartDate");
}
/** Set Start Time.
Time started */
public void setStartTime (Timestamp StartTime)
{
set_Value ("StartTime", StartTime);
}
/** Get Start Time.
Time started */
public Timestamp getStartTime() 
{
return (Timestamp)get_Value("StartTime");
}
/** Set Summary.
Textual summary of this request */
public void setSummary (String Summary)
{
if (Summary == null) throw new IllegalArgumentException ("Summary is mandatory");
if (Summary.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Summary = Summary.substring(0,1999);
}
set_Value ("Summary", Summary);
}
/** Get Summary.
Textual summary of this request */
public String getSummary() 
{
return (String)get_Value("Summary");
}
}
