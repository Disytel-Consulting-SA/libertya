/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por W_MailMsg
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.515 */
public class X_W_MailMsg extends PO
{
/** Constructor estándar */
public X_W_MailMsg (Properties ctx, int W_MailMsg_ID, String trxName)
{
super (ctx, W_MailMsg_ID, trxName);
/** if (W_MailMsg_ID == 0)
{
setMailMsgType (null);
setMessage (null);
setName (null);
setSubject (null);
setW_MailMsg_ID (0);
setW_Store_ID (0);
}
 */
}
/** Load Constructor */
public X_W_MailMsg (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=780 */
public static final int Table_ID=780;

/** TableName=W_MailMsg */
public static final String Table_Name="W_MailMsg";

protected static KeyNamePair Model = new KeyNamePair(780,"W_MailMsg");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_W_MailMsg[").append(getID()).append("]");
return sb.toString();
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
public static final int MAILMSGTYPE_AD_Reference_ID=342;
/** Order Acknowledgement = OA */
public static final String MAILMSGTYPE_OrderAcknowledgement = "OA";
/** Payment Acknowledgement = PA */
public static final String MAILMSGTYPE_PaymentAcknowledgement = "PA";
/** Payment Error = PE */
public static final String MAILMSGTYPE_PaymentError = "PE";
/** User Validation = UV */
public static final String MAILMSGTYPE_UserValidation = "UV";
/** User Password = UP */
public static final String MAILMSGTYPE_UserPassword = "UP";
/** Set Message Type.
Mail Message Type */
public void setMailMsgType (String MailMsgType)
{
if (MailMsgType.equals("OA") || MailMsgType.equals("PA") || MailMsgType.equals("PE") || MailMsgType.equals("UV") || MailMsgType.equals("UP"));
 else throw new IllegalArgumentException ("MailMsgType Invalid value - Reference_ID=342 - OA - PA - PE - UV - UP");
if (MailMsgType == null) throw new IllegalArgumentException ("MailMsgType is mandatory");
if (MailMsgType.length() > 2)
{
log.warning("Length > 2 - truncated");
MailMsgType = MailMsgType.substring(0,1);
}
set_Value ("MailMsgType", MailMsgType);
}
/** Get Message Type.
Mail Message Type */
public String getMailMsgType() 
{
return (String)get_Value("MailMsgType");
}
/** Set Message.
EMail Message */
public void setMessage (String Message)
{
if (Message == null) throw new IllegalArgumentException ("Message is mandatory");
if (Message.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Message = Message.substring(0,1999);
}
set_Value ("Message", Message);
}
/** Get Message.
EMail Message */
public String getMessage() 
{
return (String)get_Value("Message");
}
/** Set Message 2.
Optional second part of the EMail Message */
public void setMessage2 (String Message2)
{
if (Message2 != null && Message2.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Message2 = Message2.substring(0,1999);
}
set_Value ("Message2", Message2);
}
/** Get Message 2.
Optional second part of the EMail Message */
public String getMessage2() 
{
return (String)get_Value("Message2");
}
/** Set Message 3.
Optional third part of the EMail Message */
public void setMessage3 (String Message3)
{
if (Message3 != null && Message3.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Message3 = Message3.substring(0,1999);
}
set_Value ("Message3", Message3);
}
/** Get Message 3.
Optional third part of the EMail Message */
public String getMessage3() 
{
return (String)get_Value("Message3");
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
/** Set Subject.
Email Message Subject */
public void setSubject (String Subject)
{
if (Subject == null) throw new IllegalArgumentException ("Subject is mandatory");
if (Subject.length() > 255)
{
log.warning("Length > 255 - truncated");
Subject = Subject.substring(0,254);
}
set_Value ("Subject", Subject);
}
/** Get Subject.
Email Message Subject */
public String getSubject() 
{
return (String)get_Value("Subject");
}
/** Set Mail Message.
Web Store Mail Message Template */
public void setW_MailMsg_ID (int W_MailMsg_ID)
{
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
/** Set Web Store.
A Web Store of the Client */
public void setW_Store_ID (int W_Store_ID)
{
set_Value ("W_Store_ID", new Integer(W_Store_ID));
}
/** Get Web Store.
A Web Store of the Client */
public int getW_Store_ID() 
{
Integer ii = (Integer)get_Value("W_Store_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
