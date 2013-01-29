/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_RequestAction
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.078 */
public class X_R_RequestAction extends PO
{
/** Constructor estándar */
public X_R_RequestAction (Properties ctx, int R_RequestAction_ID, String trxName)
{
super (ctx, R_RequestAction_ID, trxName);
/** if (R_RequestAction_ID == 0)
{
setR_RequestAction_ID (0);
setR_Request_ID (0);
}
 */
}
/** Load Constructor */
public X_R_RequestAction (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=418 */
public static final int Table_ID=418;

/** TableName=R_RequestAction */
public static final String Table_Name="R_RequestAction";

protected static KeyNamePair Model = new KeyNamePair(418,"R_RequestAction");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_RequestAction[").append(getID()).append("]");
return sb.toString();
}
/** Set Role.
Responsibility Role */
public void setAD_Role_ID (int AD_Role_ID)
{
if (AD_Role_ID <= 0) set_ValueNoCheck ("AD_Role_ID", null);
 else 
set_ValueNoCheck ("AD_Role_ID", new Integer(AD_Role_ID));
}
/** Get Role.
Responsibility Role */
public int getAD_Role_ID() 
{
Integer ii = (Integer)get_Value("AD_Role_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_ValueNoCheck ("AD_User_ID", null);
 else 
set_ValueNoCheck ("AD_User_ID", new Integer(AD_User_ID));
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
if (A_Asset_ID <= 0) set_ValueNoCheck ("A_Asset_ID", null);
 else 
set_ValueNoCheck ("A_Asset_ID", new Integer(A_Asset_ID));
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
if (C_Activity_ID <= 0) set_ValueNoCheck ("C_Activity_ID", null);
 else 
set_ValueNoCheck ("C_Activity_ID", new Integer(C_Activity_ID));
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
if (C_BPartner_ID <= 0) set_ValueNoCheck ("C_BPartner_ID", null);
 else 
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
if (C_Payment_ID <= 0) set_ValueNoCheck ("C_Payment_ID", null);
 else 
set_ValueNoCheck ("C_Payment_ID", new Integer(C_Payment_ID));
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
if (C_Project_ID <= 0) set_ValueNoCheck ("C_Project_ID", null);
 else 
set_ValueNoCheck ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
if (ii == null) return 0;
return ii.intValue();
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
if (ConfidentialType == null || ConfidentialType.equals("A") || ConfidentialType.equals("P") || ConfidentialType.equals("C") || ConfidentialType.equals("I"));
 else throw new IllegalArgumentException ("ConfidentialType Invalid value - Reference_ID=340 - A - P - C - I");
if (ConfidentialType != null && ConfidentialType.length() > 1)
{
log.warning("Length > 1 - truncated");
ConfidentialType = ConfidentialType.substring(0,0);
}
set_ValueNoCheck ("ConfidentialType", ConfidentialType);
}
/** Get Confidentiality.
Type of Confidentiality */
public String getConfidentialType() 
{
return (String)get_Value("ConfidentialType");
}
/** Set Date next action.
Date that this request should be acted on */
public void setDateNextAction (Timestamp DateNextAction)
{
set_ValueNoCheck ("DateNextAction", DateNextAction);
}
/** Get Date next action.
Date that this request should be acted on */
public Timestamp getDateNextAction() 
{
return (Timestamp)get_Value("DateNextAction");
}
public static final int ISESCALATED_AD_Reference_ID=319;
/** Yes = Y */
public static final String ISESCALATED_Yes = "Y";
/** No = N */
public static final String ISESCALATED_No = "N";
/** Set Escalated.
This request has been escalated */
public void setIsEscalated (String IsEscalated)
{
if (IsEscalated == null || IsEscalated.equals("Y") || IsEscalated.equals("N"));
 else throw new IllegalArgumentException ("IsEscalated Invalid value - Reference_ID=319 - Y - N");
if (IsEscalated != null && IsEscalated.length() > 1)
{
log.warning("Length > 1 - truncated");
IsEscalated = IsEscalated.substring(0,0);
}
set_ValueNoCheck ("IsEscalated", IsEscalated);
}
/** Get Escalated.
This request has been escalated */
public String getIsEscalated() 
{
return (String)get_Value("IsEscalated");
}
public static final int ISINVOICED_AD_Reference_ID=319;
/** Yes = Y */
public static final String ISINVOICED_Yes = "Y";
/** No = N */
public static final String ISINVOICED_No = "N";
/** Set Invoiced.
Is this invoiced? */
public void setIsInvoiced (String IsInvoiced)
{
if (IsInvoiced == null || IsInvoiced.equals("Y") || IsInvoiced.equals("N"));
 else throw new IllegalArgumentException ("IsInvoiced Invalid value - Reference_ID=319 - Y - N");
if (IsInvoiced != null && IsInvoiced.length() > 1)
{
log.warning("Length > 1 - truncated");
IsInvoiced = IsInvoiced.substring(0,0);
}
set_ValueNoCheck ("IsInvoiced", IsInvoiced);
}
/** Get Invoiced.
Is this invoiced? */
public String getIsInvoiced() 
{
return (String)get_Value("IsInvoiced");
}
public static final int ISSELFSERVICE_AD_Reference_ID=319;
/** Yes = Y */
public static final String ISSELFSERVICE_Yes = "Y";
/** No = N */
public static final String ISSELFSERVICE_No = "N";
/** Set Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public void setIsSelfService (String IsSelfService)
{
if (IsSelfService == null || IsSelfService.equals("Y") || IsSelfService.equals("N"));
 else throw new IllegalArgumentException ("IsSelfService Invalid value - Reference_ID=319 - Y - N");
if (IsSelfService != null && IsSelfService.length() > 1)
{
log.warning("Length > 1 - truncated");
IsSelfService = IsSelfService.substring(0,0);
}
set_ValueNoCheck ("IsSelfService", IsSelfService);
}
/** Get Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public String getIsSelfService() 
{
return (String)get_Value("IsSelfService");
}
/** Set Shipment/Receipt.
Material Shipment Document */
public void setM_InOut_ID (int M_InOut_ID)
{
if (M_InOut_ID <= 0) set_ValueNoCheck ("M_InOut_ID", null);
 else 
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
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_ValueNoCheck ("M_Product_ID", null);
 else 
set_ValueNoCheck ("M_Product_ID", new Integer(M_Product_ID));
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
if (M_RMA_ID <= 0) set_ValueNoCheck ("M_RMA_ID", null);
 else 
set_ValueNoCheck ("M_RMA_ID", new Integer(M_RMA_ID));
}
/** Get RMA.
Return Material Authorization */
public int getM_RMA_ID() 
{
Integer ii = (Integer)get_Value("M_RMA_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Null Columns.
Columns with NULL value */
public void setNullColumns (String NullColumns)
{
if (NullColumns != null && NullColumns.length() > 255)
{
log.warning("Length > 255 - truncated");
NullColumns = NullColumns.substring(0,254);
}
set_ValueNoCheck ("NullColumns", NullColumns);
}
/** Get Null Columns.
Columns with NULL value */
public String getNullColumns() 
{
return (String)get_Value("NullColumns");
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
if (Priority == null || Priority.equals("3") || Priority.equals("7") || Priority.equals("9") || Priority.equals("5") || Priority.equals("1"));
 else throw new IllegalArgumentException ("Priority Invalid value - Reference_ID=154 - 3 - 7 - 9 - 5 - 1");
if (Priority != null && Priority.length() > 1)
{
log.warning("Length > 1 - truncated");
Priority = Priority.substring(0,0);
}
set_ValueNoCheck ("Priority", Priority);
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
set_ValueNoCheck ("PriorityUser", PriorityUser);
}
/** Get User Importance.
Priority of the issue for the User */
public String getPriorityUser() 
{
return (String)get_Value("PriorityUser");
}
/** Set Category.
Request Category */
public void setR_Category_ID (int R_Category_ID)
{
if (R_Category_ID <= 0) set_ValueNoCheck ("R_Category_ID", null);
 else 
set_ValueNoCheck ("R_Category_ID", new Integer(R_Category_ID));
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
if (R_Group_ID <= 0) set_ValueNoCheck ("R_Group_ID", null);
 else 
set_ValueNoCheck ("R_Group_ID", new Integer(R_Group_ID));
}
/** Get Group.
Request Group */
public int getR_Group_ID() 
{
Integer ii = (Integer)get_Value("R_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Request History.
Request has been changed */
public void setR_RequestAction_ID (int R_RequestAction_ID)
{
set_ValueNoCheck ("R_RequestAction_ID", new Integer(R_RequestAction_ID));
}
/** Get Request History.
Request has been changed */
public int getR_RequestAction_ID() 
{
Integer ii = (Integer)get_Value("R_RequestAction_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Request Type.
Type of request (e.g. Inquiry, Complaint, ..) */
public void setR_RequestType_ID (int R_RequestType_ID)
{
if (R_RequestType_ID <= 0) set_ValueNoCheck ("R_RequestType_ID", null);
 else 
set_ValueNoCheck ("R_RequestType_ID", new Integer(R_RequestType_ID));
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
if (R_Resolution_ID <= 0) set_ValueNoCheck ("R_Resolution_ID", null);
 else 
set_ValueNoCheck ("R_Resolution_ID", new Integer(R_Resolution_ID));
}
/** Get Resolution.
Request Resolution */
public int getR_Resolution_ID() 
{
Integer ii = (Integer)get_Value("R_Resolution_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Status.
Request Status */
public void setR_Status_ID (int R_Status_ID)
{
if (R_Status_ID <= 0) set_ValueNoCheck ("R_Status_ID", null);
 else 
set_ValueNoCheck ("R_Status_ID", new Integer(R_Status_ID));
}
/** Get Status.
Request Status */
public int getR_Status_ID() 
{
Integer ii = (Integer)get_Value("R_Status_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SALESREP_ID_AD_Reference_ID=110;
/** Set Sales Representative.
Sales Representative or Company Agent */
public void setSalesRep_ID (int SalesRep_ID)
{
if (SalesRep_ID <= 0) set_ValueNoCheck ("SalesRep_ID", null);
 else 
set_ValueNoCheck ("SalesRep_ID", new Integer(SalesRep_ID));
}
/** Get Sales Representative.
Sales Representative or Company Agent */
public int getSalesRep_ID() 
{
Integer ii = (Integer)get_Value("SalesRep_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Summary.
Textual summary of this request */
public void setSummary (String Summary)
{
if (Summary != null && Summary.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Summary = Summary.substring(0,1999);
}
set_ValueNoCheck ("Summary", Summary);
}
/** Get Summary.
Textual summary of this request */
public String getSummary() 
{
return (String)get_Value("Summary");
}
}
