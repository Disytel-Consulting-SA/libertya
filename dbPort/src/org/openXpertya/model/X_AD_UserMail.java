/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_UserMail
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:25.718 */
public class X_AD_UserMail extends PO
{
/** Constructor estándar */
public X_AD_UserMail (Properties ctx, int AD_UserMail_ID, String trxName)
{
super (ctx, AD_UserMail_ID, trxName);
/** if (AD_UserMail_ID == 0)
{
setAD_UserMail_ID (0);
setAD_User_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_UserMail (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=782 */
public static final int Table_ID=782;

/** TableName=AD_UserMail */
public static final String Table_Name="AD_UserMail";

protected static KeyNamePair Model = new KeyNamePair(782,"AD_UserMail");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_UserMail[").append(getID()).append("]");
return sb.toString();
}
/** Set User Mail.
Mail sent to the user */
public void setAD_UserMail_ID (int AD_UserMail_ID)
{
set_ValueNoCheck ("AD_UserMail_ID", new Integer(AD_UserMail_ID));
}
/** Get User Mail.
Mail sent to the user */
public int getAD_UserMail_ID() 
{
Integer ii = (Integer)get_Value("AD_UserMail_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_User_ID()));
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
set_ValueNoCheck ("DeliveryConfirmation", DeliveryConfirmation);
}
/** Get Delivery Confirmation.
EMail Delivery confirmation */
public String getDeliveryConfirmation() 
{
return (String)get_Value("DeliveryConfirmation");
}
public static final int ISDELIVERED_AD_Reference_ID=319;
/** Yes = Y */
public static final String ISDELIVERED_Yes = "Y";
/** No = N */
public static final String ISDELIVERED_No = "N";
/** Set Delivered */
public void setIsDelivered (String IsDelivered)
{
if (IsDelivered == null || IsDelivered.equals("Y") || IsDelivered.equals("N"));
 else throw new IllegalArgumentException ("IsDelivered Invalid value - Reference_ID=319 - Y - N");
if (IsDelivered != null && IsDelivered.length() > 1)
{
log.warning("Length > 1 - truncated");
IsDelivered = IsDelivered.substring(0,0);
}
set_ValueNoCheck ("IsDelivered", IsDelivered);
}
/** Get Delivered */
public String getIsDelivered() 
{
return (String)get_Value("IsDelivered");
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
/** Set Mail Template.
Text templates for mailings */
public void setR_MailText_ID (int R_MailText_ID)
{
if (R_MailText_ID <= 0) set_ValueNoCheck ("R_MailText_ID", null);
 else 
set_ValueNoCheck ("R_MailText_ID", new Integer(R_MailText_ID));
}
/** Get Mail Template.
Text templates for mailings */
public int getR_MailText_ID() 
{
Integer ii = (Integer)get_Value("R_MailText_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Mail Message.
Web Store Mail Message Template */
public void setW_MailMsg_ID (int W_MailMsg_ID)
{
if (W_MailMsg_ID <= 0) set_ValueNoCheck ("W_MailMsg_ID", null);
 else 
set_ValueNoCheck ("W_MailMsg_ID", new Integer(W_MailMsg_ID));
}
/** Get Mail Message.
Web Store Mail Message Template */
public int getW_MailMsg_ID() 
{
Integer ii = (Integer)get_Value("W_MailMsg_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
