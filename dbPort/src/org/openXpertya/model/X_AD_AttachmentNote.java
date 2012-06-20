/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_AttachmentNote
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:22.156 */
public class X_AD_AttachmentNote extends PO
{
/** Constructor estándar */
public X_AD_AttachmentNote (Properties ctx, int AD_AttachmentNote_ID, String trxName)
{
super (ctx, AD_AttachmentNote_ID, trxName);
/** if (AD_AttachmentNote_ID == 0)
{
setAD_AttachmentNote_ID (0);
setAD_Attachment_ID (0);
setAD_User_ID (0);
setTextMsg (null);
setTitle (null);
}
 */
}
/** Load Constructor */
public X_AD_AttachmentNote (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=705 */
public static final int Table_ID=705;

/** TableName=AD_AttachmentNote */
public static final String Table_Name="AD_AttachmentNote";

protected static KeyNamePair Model = new KeyNamePair(705,"AD_AttachmentNote");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_AttachmentNote[").append(getID()).append("]");
return sb.toString();
}
/** Set Attachment Note.
Personal Attachment Note */
public void setAD_AttachmentNote_ID (int AD_AttachmentNote_ID)
{
set_ValueNoCheck ("AD_AttachmentNote_ID", new Integer(AD_AttachmentNote_ID));
}
/** Get Attachment Note.
Personal Attachment Note */
public int getAD_AttachmentNote_ID() 
{
Integer ii = (Integer)get_Value("AD_AttachmentNote_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attachment.
Attachment for the document */
public void setAD_Attachment_ID (int AD_Attachment_ID)
{
set_ValueNoCheck ("AD_Attachment_ID", new Integer(AD_Attachment_ID));
}
/** Get Attachment.
Attachment for the document */
public int getAD_Attachment_ID() 
{
Integer ii = (Integer)get_Value("AD_Attachment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
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
/** Set Text Message.
Text Message */
public void setTextMsg (String TextMsg)
{
if (TextMsg == null) throw new IllegalArgumentException ("TextMsg is mandatory");
if (TextMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
TextMsg = TextMsg.substring(0,1999);
}
set_Value ("TextMsg", TextMsg);
}
/** Get Text Message.
Text Message */
public String getTextMsg() 
{
return (String)get_Value("TextMsg");
}
/** Set Title.
Name this entity is referred to as */
public void setTitle (String Title)
{
if (Title == null) throw new IllegalArgumentException ("Title is mandatory");
if (Title.length() > 60)
{
log.warning("Length > 60 - truncated");
Title = Title.substring(0,59);
}
set_Value ("Title", Title);
}
/** Get Title.
Name this entity is referred to as */
public String getTitle() 
{
return (String)get_Value("Title");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getTitle());
}
}
