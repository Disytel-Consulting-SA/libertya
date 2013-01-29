/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BP_EDI
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.703 */
public class X_C_BP_EDI extends PO
{
/** Constructor estándar */
public X_C_BP_EDI (Properties ctx, int C_BP_EDI_ID, String trxName)
{
super (ctx, C_BP_EDI_ID, trxName);
/** if (C_BP_EDI_ID == 0)
{
setAD_Sequence_ID (0);
setC_BP_EDI_ID (0);
setC_BPartner_ID (0);
setCustomerNo (null);
setEDIType (null);
setEMail_Error_To (null);
setEMail_Info_To (null);
setIsAudited (false);
setIsInfoSent (false);
setM_Warehouse_ID (0);
setName (null);
setReceiveInquiryReply (false);
setReceiveOrderReply (false);
setSendInquiry (false);
setSendOrder (false);
}
 */
}
/** Load Constructor */
public X_C_BP_EDI (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=366 */
public static final int Table_ID=366;

/** TableName=C_BP_EDI */
public static final String Table_Name="C_BP_EDI";

protected static KeyNamePair Model = new KeyNamePair(366,"C_BP_EDI");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BP_EDI[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_SEQUENCE_ID_AD_Reference_ID=128;
/** Set Sequence.
Document Sequence */
public void setAD_Sequence_ID (int AD_Sequence_ID)
{
set_Value ("AD_Sequence_ID", new Integer(AD_Sequence_ID));
}
/** Get Sequence.
Document Sequence */
public int getAD_Sequence_ID() 
{
Integer ii = (Integer)get_Value("AD_Sequence_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set EDI Definition.
Electronic Data Interchange */
public void setC_BP_EDI_ID (int C_BP_EDI_ID)
{
set_ValueNoCheck ("C_BP_EDI_ID", new Integer(C_BP_EDI_ID));
}
/** Get EDI Definition.
Electronic Data Interchange */
public int getC_BP_EDI_ID() 
{
Integer ii = (Integer)get_Value("C_BP_EDI_ID");
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
/** Set Customer No.
EDI Identification Number  */
public void setCustomerNo (String CustomerNo)
{
if (CustomerNo == null) throw new IllegalArgumentException ("CustomerNo is mandatory");
if (CustomerNo.length() > 20)
{
log.warning("Length > 20 - truncated");
CustomerNo = CustomerNo.substring(0,19);
}
set_Value ("CustomerNo", CustomerNo);
}
/** Get Customer No.
EDI Identification Number  */
public String getCustomerNo() 
{
return (String)get_Value("CustomerNo");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
public static final int EDITYPE_AD_Reference_ID=201;
/** ASC X12  = X */
public static final String EDITYPE_ASCX12 = "X";
/** EDIFACT = E */
public static final String EDITYPE_EDIFACT = "E";
/** Email EDI = M */
public static final String EDITYPE_EmailEDI = "M";
/** Set EDI Type */
public void setEDIType (String EDIType)
{
if (EDIType.equals("X") || EDIType.equals("E") || EDIType.equals("M"));
 else throw new IllegalArgumentException ("EDIType Invalid value - Reference_ID=201 - X - E - M");
if (EDIType == null) throw new IllegalArgumentException ("EDIType is mandatory");
if (EDIType.length() > 1)
{
log.warning("Length > 1 - truncated");
EDIType = EDIType.substring(0,0);
}
set_Value ("EDIType", EDIType);
}
/** Get EDI Type */
public String getEDIType() 
{
return (String)get_Value("EDIType");
}
/** Set Error EMail.
Email address to send error messages to */
public void setEMail_Error_To (String EMail_Error_To)
{
if (EMail_Error_To == null) throw new IllegalArgumentException ("EMail_Error_To is mandatory");
if (EMail_Error_To.length() > 60)
{
log.warning("Length > 60 - truncated");
EMail_Error_To = EMail_Error_To.substring(0,59);
}
set_Value ("EMail_Error_To", EMail_Error_To);
}
/** Get Error EMail.
Email address to send error messages to */
public String getEMail_Error_To() 
{
return (String)get_Value("EMail_Error_To");
}
/** Set From EMail.
Full EMail address used to send requests - e.g. edi@organization.com */
public void setEMail_From (String EMail_From)
{
if (EMail_From != null && EMail_From.length() > 60)
{
log.warning("Length > 60 - truncated");
EMail_From = EMail_From.substring(0,59);
}
set_Value ("EMail_From", EMail_From);
}
/** Get From EMail.
Full EMail address used to send requests - e.g. edi@organization.com */
public String getEMail_From() 
{
return (String)get_Value("EMail_From");
}
/** Set From EMail Password.
Password of the sending EMail address */
public void setEMail_From_Pwd (String EMail_From_Pwd)
{
if (EMail_From_Pwd != null && EMail_From_Pwd.length() > 20)
{
log.warning("Length > 20 - truncated");
EMail_From_Pwd = EMail_From_Pwd.substring(0,19);
}
set_Value ("EMail_From_Pwd", EMail_From_Pwd);
}
/** Get From EMail Password.
Password of the sending EMail address */
public String getEMail_From_Pwd() 
{
return (String)get_Value("EMail_From_Pwd");
}
/** Set From EMail User ID.
User ID of the sending EMail address (on default SMTP Host) - e.g. edi */
public void setEMail_From_Uid (String EMail_From_Uid)
{
if (EMail_From_Uid != null && EMail_From_Uid.length() > 20)
{
log.warning("Length > 20 - truncated");
EMail_From_Uid = EMail_From_Uid.substring(0,19);
}
set_Value ("EMail_From_Uid", EMail_From_Uid);
}
/** Get From EMail User ID.
User ID of the sending EMail address (on default SMTP Host) - e.g. edi */
public String getEMail_From_Uid() 
{
return (String)get_Value("EMail_From_Uid");
}
/** Set Info EMail.
EMail address to send informational messages and copies */
public void setEMail_Info_To (String EMail_Info_To)
{
if (EMail_Info_To == null) throw new IllegalArgumentException ("EMail_Info_To is mandatory");
if (EMail_Info_To.length() > 60)
{
log.warning("Length > 60 - truncated");
EMail_Info_To = EMail_Info_To.substring(0,59);
}
set_Value ("EMail_Info_To", EMail_Info_To);
}
/** Get Info EMail.
EMail address to send informational messages and copies */
public String getEMail_Info_To() 
{
return (String)get_Value("EMail_Info_To");
}
/** Set To EMail.
EMail address to send requests to - e.g. edi@manufacturer.com  */
public void setEMail_To (String EMail_To)
{
if (EMail_To != null && EMail_To.length() > 60)
{
log.warning("Length > 60 - truncated");
EMail_To = EMail_To.substring(0,59);
}
set_Value ("EMail_To", EMail_To);
}
/** Get To EMail.
EMail address to send requests to - e.g. edi@manufacturer.com  */
public String getEMail_To() 
{
return (String)get_Value("EMail_To");
}
/** Set Activate Audit.
Activate Audit Trail of what numbers are generated */
public void setIsAudited (boolean IsAudited)
{
set_Value ("IsAudited", new Boolean(IsAudited));
}
/** Get Activate Audit.
Activate Audit Trail of what numbers are generated */
public boolean isAudited() 
{
Object oo = get_Value("IsAudited");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Send Info.
Send informational messages and copies */
public void setIsInfoSent (boolean IsInfoSent)
{
set_Value ("IsInfoSent", new Boolean(IsInfoSent));
}
/** Get Send Info.
Send informational messages and copies */
public boolean isInfoSent() 
{
Object oo = get_Value("IsInfoSent");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Received Inquiry Reply */
public void setReceiveInquiryReply (boolean ReceiveInquiryReply)
{
set_Value ("ReceiveInquiryReply", new Boolean(ReceiveInquiryReply));
}
/** Get Received Inquiry Reply */
public boolean isReceiveInquiryReply() 
{
Object oo = get_Value("ReceiveInquiryReply");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Receive Order Reply */
public void setReceiveOrderReply (boolean ReceiveOrderReply)
{
set_Value ("ReceiveOrderReply", new Boolean(ReceiveOrderReply));
}
/** Get Receive Order Reply */
public boolean isReceiveOrderReply() 
{
Object oo = get_Value("ReceiveOrderReply");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Send Inquiry.
Quantity Availability Inquiry */
public void setSendInquiry (boolean SendInquiry)
{
set_Value ("SendInquiry", new Boolean(SendInquiry));
}
/** Get Send Inquiry.
Quantity Availability Inquiry */
public boolean isSendInquiry() 
{
Object oo = get_Value("SendInquiry");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Send Order */
public void setSendOrder (boolean SendOrder)
{
set_Value ("SendOrder", new Boolean(SendOrder));
}
/** Get Send Order */
public boolean isSendOrder() 
{
Object oo = get_Value("SendOrder");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
