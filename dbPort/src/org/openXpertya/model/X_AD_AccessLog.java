/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_AccessLog
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:21.656 */
public class X_AD_AccessLog extends PO
{
/** Constructor estándar */
public X_AD_AccessLog (Properties ctx, int AD_AccessLog_ID, String trxName)
{
super (ctx, AD_AccessLog_ID, trxName);
/** if (AD_AccessLog_ID == 0)
{
setAD_AccessLog_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_AccessLog (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=717 */
public static final int Table_ID=717;

/** TableName=AD_AccessLog */
public static final String Table_Name="AD_AccessLog";

protected static KeyNamePair Model = new KeyNamePair(717,"AD_AccessLog");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_AccessLog[").append(getID()).append("]");
return sb.toString();
}
/** Set Access Log.
Log of Access to the System */
public void setAD_AccessLog_ID (int AD_AccessLog_ID)
{
set_ValueNoCheck ("AD_AccessLog_ID", new Integer(AD_AccessLog_ID));
}
/** Get Access Log.
Log of Access to the System */
public int getAD_AccessLog_ID() 
{
Integer ii = (Integer)get_Value("AD_AccessLog_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_AccessLog_ID()));
}
/** Set Column.
Column in the table */
public void setAD_Column_ID (int AD_Column_ID)
{
if (AD_Column_ID <= 0) set_Value ("AD_Column_ID", null);
 else 
set_Value ("AD_Column_ID", new Integer(AD_Column_ID));
}
/** Get Column.
Column in the table */
public int getAD_Column_ID() 
{
Integer ii = (Integer)get_Value("AD_Column_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
if (AD_Table_ID <= 0) set_Value ("AD_Table_ID", null);
 else 
set_Value ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
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
/** Set Record ID.
Direct internal record ID */
public void setRecord_ID (int Record_ID)
{
if (Record_ID <= 0) set_Value ("Record_ID", null);
 else 
set_Value ("Record_ID", new Integer(Record_ID));
}
/** Get Record ID.
Direct internal record ID */
public int getRecord_ID() 
{
Integer ii = (Integer)get_Value("Record_ID");
if (ii == null) return 0;
return ii.intValue();
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
set_Value ("Remote_Addr", Remote_Addr);
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
set_Value ("Remote_Host", Remote_Host);
}
/** Get Remote Host */
public String getRemote_Host() 
{
return (String)get_Value("Remote_Host");
}
/** Set Reply.
Reply or Answer */
public void setReply (String Reply)
{
if (Reply != null && Reply.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Reply = Reply.substring(0,1999);
}
set_Value ("Reply", Reply);
}
/** Get Reply.
Reply or Answer */
public String getReply() 
{
return (String)get_Value("Reply");
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
}
