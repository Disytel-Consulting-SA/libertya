/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Attachment
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:22.125 */
public class X_AD_Attachment extends PO
{
/** Constructor estándar */
public X_AD_Attachment (Properties ctx, int AD_Attachment_ID, String trxName)
{
super (ctx, AD_Attachment_ID, trxName);
/** if (AD_Attachment_ID == 0)
{
setAD_Attachment_ID (0);
setAD_Table_ID (0);
setRecord_ID (0);
setTitle (null);
}
 */
}
/** Load Constructor */
public X_AD_Attachment (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=254 */
public static final int Table_ID=254;

/** TableName=AD_Attachment */
public static final String Table_Name="AD_Attachment";

protected static KeyNamePair Model = new KeyNamePair(254,"AD_Attachment");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Attachment[").append(getID()).append("]");
return sb.toString();
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
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
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
/** Set BinaryData.
Binary Data */
public void setBinaryData (byte[] BinaryData)
{
set_ValueNoCheck ("BinaryData", BinaryData);
}
/** Get BinaryData.
Binary Data */
public byte[] getBinaryData() 
{
return (byte[])get_Value("BinaryData");
}
/** Set Record ID.
Direct internal record ID */
public void setRecord_ID (int Record_ID)
{
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
/** Set Text Message.
Text Message */
public void setTextMsg (String TextMsg)
{
if (TextMsg != null && TextMsg.length() > 2000)
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
