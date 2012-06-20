/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por A_Asset_Delivery
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.703 */
public class X_A_Asset_Delivery extends PO
{
/** Constructor estándar */
public X_A_Asset_Delivery (Properties ctx, int A_Asset_Delivery_ID, String trxName)
{
super (ctx, A_Asset_Delivery_ID, trxName);
/** if (A_Asset_Delivery_ID == 0)
{
setA_Asset_Delivery_ID (0);
setA_Asset_ID (0);
setMovementDate (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_A_Asset_Delivery (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=541 */
public static final int Table_ID=541;

/** TableName=A_Asset_Delivery */
public static final String Table_Name="A_Asset_Delivery";

protected static KeyNamePair Model = new KeyNamePair(541,"A_Asset_Delivery");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_A_Asset_Delivery[").append(getID()).append("]");
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
/** Set Asset Delivery.
Delivery of Asset */
public void setA_Asset_Delivery_ID (int A_Asset_Delivery_ID)
{
set_ValueNoCheck ("A_Asset_Delivery_ID", new Integer(A_Asset_Delivery_ID));
}
/** Get Asset Delivery.
Delivery of Asset */
public int getA_Asset_Delivery_ID() 
{
Integer ii = (Integer)get_Value("A_Asset_Delivery_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Asset.
Asset used internally or by customers */
public void setA_Asset_ID (int A_Asset_ID)
{
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
/** Set Delivery Confirmation.
EMail Delivery confirmation */
public void setDeliveryConfirmation (String DeliveryConfirmation)
{
if (DeliveryConfirmation != null && DeliveryConfirmation.length() > 120)
{
log.warning("Length > 120 - truncated");
DeliveryConfirmation = DeliveryConfirmation.substring(0,119);
}
set_Value ("DeliveryConfirmation", DeliveryConfirmation);
}
/** Get Delivery Confirmation.
EMail Delivery confirmation */
public String getDeliveryConfirmation() 
{
return (String)get_Value("DeliveryConfirmation");
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
/** Set EMail.
Electronic Mail Address */
public void setEMail (String EMail)
{
if (EMail != null && EMail.length() > 60)
{
log.warning("Length > 60 - truncated");
EMail = EMail.substring(0,59);
}
set_ValueNoCheck ("EMail", EMail);
}
/** Get EMail.
Electronic Mail Address */
public String getEMail() 
{
return (String)get_Value("EMail");
}
/** Set Lot No.
Lot number (alphanumeric) */
public void setLot (String Lot)
{
if (Lot != null && Lot.length() > 40)
{
log.warning("Length > 40 - truncated");
Lot = Lot.substring(0,39);
}
set_ValueNoCheck ("Lot", Lot);
}
/** Get Lot No.
Lot number (alphanumeric) */
public String getLot() 
{
return (String)get_Value("Lot");
}
/** Set Shipment/Receipt Line.
Line on Shipment or Receipt document */
public void setM_InOutLine_ID (int M_InOutLine_ID)
{
if (M_InOutLine_ID <= 0) set_ValueNoCheck ("M_InOutLine_ID", null);
 else 
set_ValueNoCheck ("M_InOutLine_ID", new Integer(M_InOutLine_ID));
}
/** Get Shipment/Receipt Line.
Line on Shipment or Receipt document */
public int getM_InOutLine_ID() 
{
Integer ii = (Integer)get_Value("M_InOutLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product Download.
Product downloads */
public void setM_ProductDownload_ID (int M_ProductDownload_ID)
{
if (M_ProductDownload_ID <= 0) set_Value ("M_ProductDownload_ID", null);
 else 
set_Value ("M_ProductDownload_ID", new Integer(M_ProductDownload_ID));
}
/** Get Product Download.
Product downloads */
public int getM_ProductDownload_ID() 
{
Integer ii = (Integer)get_Value("M_ProductDownload_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Message ID.
EMail Message ID */
public void setMessageID (String MessageID)
{
if (MessageID != null && MessageID.length() > 120)
{
log.warning("Length > 120 - truncated");
MessageID = MessageID.substring(0,119);
}
set_ValueNoCheck ("MessageID", MessageID);
}
/** Get Message ID.
EMail Message ID */
public String getMessageID() 
{
return (String)get_Value("MessageID");
}
/** Set Movement Date.
Date a product was moved in or out of inventory */
public void setMovementDate (Timestamp MovementDate)
{
if (MovementDate == null) throw new IllegalArgumentException ("MovementDate is mandatory");
set_ValueNoCheck ("MovementDate", MovementDate);
}
/** Get Movement Date.
Date a product was moved in or out of inventory */
public Timestamp getMovementDate() 
{
return (Timestamp)get_Value("MovementDate");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getMovementDate()));
}
/** Set Referrer.
Referring web address */
public void setReferrer (String Referrer)
{
if (Referrer != null && Referrer.length() > 255)
{
log.warning("Length > 255 - truncated");
Referrer = Referrer.substring(0,254);
}
set_ValueNoCheck ("Referrer", Referrer);
}
/** Get Referrer.
Referring web address */
public String getReferrer() 
{
return (String)get_Value("Referrer");
}
/** Set Remote Addr.
Remote Address */
public void setRemote_Addr (String Remote_Addr)
{
if (Remote_Addr != null && Remote_Addr.length() > 60)
{
log.warning("Length > 60 - truncated");
Remote_Addr = Remote_Addr.substring(0,59);
}
set_ValueNoCheck ("Remote_Addr", Remote_Addr);
}
/** Get Remote Addr.
Remote Address */
public String getRemote_Addr() 
{
return (String)get_Value("Remote_Addr");
}
/** Set Remote Host */
public void setRemote_Host (String Remote_Host)
{
if (Remote_Host != null && Remote_Host.length() > 60)
{
log.warning("Length > 60 - truncated");
Remote_Host = Remote_Host.substring(0,59);
}
set_ValueNoCheck ("Remote_Host", Remote_Host);
}
/** Get Remote Host */
public String getRemote_Host() 
{
return (String)get_Value("Remote_Host");
}
/** Set Serial No.
Product Serial Number  */
public void setSerNo (String SerNo)
{
if (SerNo != null && SerNo.length() > 40)
{
log.warning("Length > 40 - truncated");
SerNo = SerNo.substring(0,39);
}
set_ValueNoCheck ("SerNo", SerNo);
}
/** Get Serial No.
Product Serial Number  */
public String getSerNo() 
{
return (String)get_Value("SerNo");
}
/** Set URL.
URL */
public void setURL (String URL)
{
if (URL != null && URL.length() > 120)
{
log.warning("Length > 120 - truncated");
URL = URL.substring(0,119);
}
set_ValueNoCheck ("URL", URL);
}
/** Get URL.
URL */
public String getURL() 
{
return (String)get_Value("URL");
}
/** Set Version No.
Version Number */
public void setVersionNo (String VersionNo)
{
if (VersionNo != null && VersionNo.length() > 20)
{
log.warning("Length > 20 - truncated");
VersionNo = VersionNo.substring(0,19);
}
set_ValueNoCheck ("VersionNo", VersionNo);
}
/** Get Version No.
Version Number */
public String getVersionNo() 
{
return (String)get_Value("VersionNo");
}
}
