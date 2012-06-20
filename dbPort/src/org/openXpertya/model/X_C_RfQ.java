/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RfQ
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.921 */
public class X_C_RfQ extends PO
{
/** Constructor estándar */
public X_C_RfQ (Properties ctx, int C_RfQ_ID, String trxName)
{
super (ctx, C_RfQ_ID, trxName);
/** if (C_RfQ_ID == 0)
{
setC_Currency_ID (0);	// @$C_Currency_ID @
setC_RfQ_ID (0);
setC_RfQ_Topic_ID (0);
setDateResponse (new Timestamp(System.currentTimeMillis()));
setDocumentNo (null);
setIsInvitedVendorsOnly (false);
setIsQuoteAllQty (false);
setIsQuoteTotalAmt (false);
setIsRfQResponseAccepted (true);	// Y
setIsSelfService (true);	// Y
setName (null);
setProcessed (false);
setQuoteType (null);	// S
setSalesRep_ID (0);
}
 */
}
/** Load Constructor */
public X_C_RfQ (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=677 */
public static final int Table_ID=677;

/** TableName=C_RfQ */
public static final String Table_Name="C_RfQ";

protected static KeyNamePair Model = new KeyNamePair(677,"C_RfQ");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RfQ[").append(getID()).append("]");
return sb.toString();
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
/** Set Partner Location.
Identifies the (ship to) address for this Business Partner */
public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
{
if (C_BPartner_Location_ID <= 0) set_Value ("C_BPartner_Location_ID", null);
 else 
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
/** Set RfQ.
Request for Quotation */
public void setC_RfQ_ID (int C_RfQ_ID)
{
set_ValueNoCheck ("C_RfQ_ID", new Integer(C_RfQ_ID));
}
/** Get RfQ.
Request for Quotation */
public int getC_RfQ_ID() 
{
Integer ii = (Integer)get_Value("C_RfQ_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set RfQ Topic.
Topic for Request for Quotations */
public void setC_RfQ_Topic_ID (int C_RfQ_Topic_ID)
{
set_Value ("C_RfQ_Topic_ID", new Integer(C_RfQ_Topic_ID));
}
/** Get RfQ Topic.
Topic for Request for Quotations */
public int getC_RfQ_Topic_ID() 
{
Integer ii = (Integer)get_Value("C_RfQ_Topic_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Copy Lines */
public void setCopyLines (String CopyLines)
{
if (CopyLines != null && CopyLines.length() > 1)
{
log.warning("Length > 1 - truncated");
CopyLines = CopyLines.substring(0,0);
}
set_Value ("CopyLines", CopyLines);
}
/** Get Copy Lines */
public String getCopyLines() 
{
return (String)get_Value("CopyLines");
}
/** Set Create PO.
Create Purchase Order */
public void setCreatePO (String CreatePO)
{
if (CreatePO != null && CreatePO.length() > 1)
{
log.warning("Length > 1 - truncated");
CreatePO = CreatePO.substring(0,0);
}
set_Value ("CreatePO", CreatePO);
}
/** Get Create PO.
Create Purchase Order */
public String getCreatePO() 
{
return (String)get_Value("CreatePO");
}
/** Set Create SO */
public void setCreateSO (String CreateSO)
{
if (CreateSO != null && CreateSO.length() > 1)
{
log.warning("Length > 1 - truncated");
CreateSO = CreateSO.substring(0,0);
}
set_Value ("CreateSO", CreateSO);
}
/** Get Create SO */
public String getCreateSO() 
{
return (String)get_Value("CreateSO");
}
/** Set Response Date.
Date of the Response */
public void setDateResponse (Timestamp DateResponse)
{
if (DateResponse == null) throw new IllegalArgumentException ("DateResponse is mandatory");
set_Value ("DateResponse", DateResponse);
}
/** Get Response Date.
Date of the Response */
public Timestamp getDateResponse() 
{
return (Timestamp)get_Value("DateResponse");
}
/** Set Work Complete.
Date when work is (planned to be) complete */
public void setDateWorkComplete (Timestamp DateWorkComplete)
{
set_Value ("DateWorkComplete", DateWorkComplete);
}
/** Get Work Complete.
Date when work is (planned to be) complete */
public Timestamp getDateWorkComplete() 
{
return (Timestamp)get_Value("DateWorkComplete");
}
/** Set Work Start.
Date when work is (planned to be) started */
public void setDateWorkStart (Timestamp DateWorkStart)
{
set_Value ("DateWorkStart", DateWorkStart);
}
/** Get Work Start.
Date when work is (planned to be) started */
public Timestamp getDateWorkStart() 
{
return (Timestamp)get_Value("DateWorkStart");
}
/** Set Delivery Days.
Number of Days (planned) until Delivery */
public void setDeliveryDays (int DeliveryDays)
{
set_Value ("DeliveryDays", new Integer(DeliveryDays));
}
/** Get Delivery Days.
Number of Days (planned) until Delivery */
public int getDeliveryDays() 
{
Integer ii = (Integer)get_Value("DeliveryDays");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Invited Vendors Only.
Only invited vendors can respond to an RfQ */
public void setIsInvitedVendorsOnly (boolean IsInvitedVendorsOnly)
{
set_Value ("IsInvitedVendorsOnly", new Boolean(IsInvitedVendorsOnly));
}
/** Get Invited Vendors Only.
Only invited vendors can respond to an RfQ */
public boolean isInvitedVendorsOnly() 
{
Object oo = get_Value("IsInvitedVendorsOnly");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Quote All Quantities.
Suppliers are requested to provide responses for all quantities */
public void setIsQuoteAllQty (boolean IsQuoteAllQty)
{
set_Value ("IsQuoteAllQty", new Boolean(IsQuoteAllQty));
}
/** Get Quote All Quantities.
Suppliers are requested to provide responses for all quantities */
public boolean isQuoteAllQty() 
{
Object oo = get_Value("IsQuoteAllQty");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Quote Total Amt.
The respnse can have just the total amount for the RfQ */
public void setIsQuoteTotalAmt (boolean IsQuoteTotalAmt)
{
set_Value ("IsQuoteTotalAmt", new Boolean(IsQuoteTotalAmt));
}
/** Get Quote Total Amt.
The respnse can have just the total amount for the RfQ */
public boolean isQuoteTotalAmt() 
{
Object oo = get_Value("IsQuoteTotalAmt");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Responses Accepted.
Are Resonses to the Request for Quotation accepted */
public void setIsRfQResponseAccepted (boolean IsRfQResponseAccepted)
{
set_Value ("IsRfQResponseAccepted", new Boolean(IsRfQResponseAccepted));
}
/** Get Responses Accepted.
Are Resonses to the Request for Quotation accepted */
public boolean isRfQResponseAccepted() 
{
Object oo = get_Value("IsRfQResponseAccepted");
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
/** Set Margin %.
Margin for a product as a percentage */
public void setMargin (BigDecimal Margin)
{
set_Value ("Margin", Margin);
}
/** Get Margin %.
Margin for a product as a percentage */
public BigDecimal getMargin() 
{
BigDecimal bd = (BigDecimal)get_Value("Margin");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Publish RfQ */
public void setPublishRfQ (String PublishRfQ)
{
if (PublishRfQ != null && PublishRfQ.length() > 1)
{
log.warning("Length > 1 - truncated");
PublishRfQ = PublishRfQ.substring(0,0);
}
set_Value ("PublishRfQ", PublishRfQ);
}
/** Get Publish RfQ */
public String getPublishRfQ() 
{
return (String)get_Value("PublishRfQ");
}
public static final int QUOTETYPE_AD_Reference_ID=314;
/** Quote Total only = T */
public static final String QUOTETYPE_QuoteTotalOnly = "T";
/** Quote Selected Lines = S */
public static final String QUOTETYPE_QuoteSelectedLines = "S";
/** Quote All Lines = A */
public static final String QUOTETYPE_QuoteAllLines = "A";
/** Set RfQ Type.
Request for Quotation Type */
public void setQuoteType (String QuoteType)
{
if (QuoteType.equals("T") || QuoteType.equals("S") || QuoteType.equals("A"));
 else throw new IllegalArgumentException ("QuoteType Invalid value - Reference_ID=314 - T - S - A");
if (QuoteType == null) throw new IllegalArgumentException ("QuoteType is mandatory");
if (QuoteType.length() > 1)
{
log.warning("Length > 1 - truncated");
QuoteType = QuoteType.substring(0,0);
}
set_Value ("QuoteType", QuoteType);
}
/** Get RfQ Type.
Request for Quotation Type */
public String getQuoteType() 
{
return (String)get_Value("QuoteType");
}
/** Set Rank RfQ */
public void setRankRfQ (String RankRfQ)
{
if (RankRfQ != null && RankRfQ.length() > 1)
{
log.warning("Length > 1 - truncated");
RankRfQ = RankRfQ.substring(0,0);
}
set_Value ("RankRfQ", RankRfQ);
}
/** Get Rank RfQ */
public String getRankRfQ() 
{
return (String)get_Value("RankRfQ");
}
public static final int SALESREP_ID_AD_Reference_ID=190;
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
}
