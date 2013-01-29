/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RfQResponse
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.015 */
public class X_C_RfQResponse extends PO
{
/** Constructor estándar */
public X_C_RfQResponse (Properties ctx, int C_RfQResponse_ID, String trxName)
{
super (ctx, C_RfQResponse_ID, trxName);
/** if (C_RfQResponse_ID == 0)
{
setC_BPartner_ID (0);
setC_BPartner_Location_ID (0);
setC_Currency_ID (0);	// @C_Currency_ID@
setC_RfQResponse_ID (0);
setC_RfQ_ID (0);
setIsComplete (false);
setIsSelectedWinner (false);
setIsSelfService (false);
setName (null);
setPrice (Env.ZERO);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_C_RfQResponse (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=674 */
public static final int Table_ID=674;

/** TableName=C_RfQResponse */
public static final String Table_Name="C_RfQResponse";

protected static KeyNamePair Model = new KeyNamePair(674,"C_RfQResponse");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RfQResponse[").append(getID()).append("]");
return sb.toString();
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
/** Set RfQ Response.
Request for Quotation Response from a potential Vendor */
public void setC_RfQResponse_ID (int C_RfQResponse_ID)
{
set_ValueNoCheck ("C_RfQResponse_ID", new Integer(C_RfQResponse_ID));
}
/** Get RfQ Response.
Request for Quotation Response from a potential Vendor */
public int getC_RfQResponse_ID() 
{
Integer ii = (Integer)get_Value("C_RfQResponse_ID");
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
/** Set Check Complete */
public void setCheckComplete (String CheckComplete)
{
if (CheckComplete != null && CheckComplete.length() > 1)
{
log.warning("Length > 1 - truncated");
CheckComplete = CheckComplete.substring(0,0);
}
set_Value ("CheckComplete", CheckComplete);
}
/** Get Check Complete */
public String getCheckComplete() 
{
return (String)get_Value("CheckComplete");
}
/** Set Invited.
Date when (last) invitation was sent */
public void setDateInvited (Timestamp DateInvited)
{
set_Value ("DateInvited", DateInvited);
}
/** Get Invited.
Date when (last) invitation was sent */
public Timestamp getDateInvited() 
{
return (Timestamp)get_Value("DateInvited");
}
/** Set Response Date.
Date of the Response */
public void setDateResponse (Timestamp DateResponse)
{
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
/** Set Complete.
It is complete */
public void setIsComplete (boolean IsComplete)
{
set_Value ("IsComplete", new Boolean(IsComplete));
}
/** Get Complete.
It is complete */
public boolean isComplete() 
{
Object oo = get_Value("IsComplete");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Selected Winner.
The resonse is the selected winner */
public void setIsSelectedWinner (boolean IsSelectedWinner)
{
set_Value ("IsSelectedWinner", new Boolean(IsSelectedWinner));
}
/** Get Selected Winner.
The resonse is the selected winner */
public boolean isSelectedWinner() 
{
Object oo = get_Value("IsSelectedWinner");
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
/** Set Price.
Price */
public void setPrice (BigDecimal Price)
{
if (Price == null) throw new IllegalArgumentException ("Price is mandatory");
set_Value ("Price", Price);
}
/** Get Price.
Price */
public BigDecimal getPrice() 
{
BigDecimal bd = (BigDecimal)get_Value("Price");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Ranking.
Relative Rank Number */
public void setRanking (int Ranking)
{
set_Value ("Ranking", new Integer(Ranking));
}
/** Get Ranking.
Relative Rank Number */
public int getRanking() 
{
Integer ii = (Integer)get_Value("Ranking");
if (ii == null) return 0;
return ii.intValue();
}
}
