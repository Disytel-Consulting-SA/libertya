/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por W_MailMsg_Trl
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:39.152 */
public class X_W_MailMsg_Trl extends PO
{
/** Constructor estándar */
public X_W_MailMsg_Trl (Properties ctx, int W_MailMsg_Trl_ID, String trxName)
{
super (ctx, W_MailMsg_Trl_ID, trxName);
/** if (W_MailMsg_Trl_ID == 0)
{
setAD_Language (null);
setIsTranslated (false);
setMessage (null);
setSubject (null);
setW_MailMsg_ID (0);
}
 */
}
/** Load Constructor */
public X_W_MailMsg_Trl (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=781 */
public static final int Table_ID=781;

/** TableName=W_MailMsg_Trl */
public static final String Table_Name="W_MailMsg_Trl";

protected static KeyNamePair Model = new KeyNamePair(781,"W_MailMsg_Trl");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_W_MailMsg_Trl[").append(getID()).append("]");
return sb.toString();
}
/** Set Language.
Language for this entity */
public void setAD_Language (String AD_Language)
{
if (AD_Language == null) throw new IllegalArgumentException ("AD_Language is mandatory");
if (AD_Language.length() > 6)
{
log.warning("Length > 6 - truncated");
AD_Language = AD_Language.substring(0,6);
}
set_ValueNoCheck ("AD_Language", AD_Language);
}
/** Get Language.
Language for this entity */
public String getAD_Language() 
{
return (String)get_Value("AD_Language");
}
/** Set Translated.
This column is translated */
public void setIsTranslated (boolean IsTranslated)
{
set_Value ("IsTranslated", new Boolean(IsTranslated));
}
/** Get Translated.
This column is translated */
public boolean isTranslated() 
{
Object oo = get_Value("IsTranslated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Message.
EMail Message */
public void setMessage (String Message)
{
if (Message == null) throw new IllegalArgumentException ("Message is mandatory");
if (Message.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Message = Message.substring(0,2000);
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
Message2 = Message2.substring(0,2000);
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
Message3 = Message3.substring(0,2000);
}
set_Value ("Message3", Message3);
}
/** Get Message 3.
Optional third part of the EMail Message */
public String getMessage3() 
{
return (String)get_Value("Message3");
}
/** Set Subject.
Email Message Subject */
public void setSubject (String Subject)
{
if (Subject == null) throw new IllegalArgumentException ("Subject is mandatory");
if (Subject.length() > 255)
{
log.warning("Length > 255 - truncated");
Subject = Subject.substring(0,255);
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
}
