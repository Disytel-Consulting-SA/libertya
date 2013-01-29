/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Session
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:25.031 */
public class X_AD_Session extends PO
{
/** Constructor estándar */
public X_AD_Session (Properties ctx, int AD_Session_ID, String trxName)
{
super (ctx, AD_Session_ID, trxName);
/** if (AD_Session_ID == 0)
{
setAD_Session_ID (0);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_AD_Session (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=566 */
public static final int Table_ID=566;

/** TableName=AD_Session */
public static final String Table_Name="AD_Session";

protected static KeyNamePair Model = new KeyNamePair(566,"AD_Session");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Session[").append(getID()).append("]");
return sb.toString();
}
/** Set Session.
User Session Online or Web */
public void setAD_Session_ID (int AD_Session_ID)
{
set_ValueNoCheck ("AD_Session_ID", new Integer(AD_Session_ID));
}
/** Get Session.
User Session Online or Web */
public int getAD_Session_ID() 
{
Integer ii = (Integer)get_Value("AD_Session_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_Session_ID()));
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_ValueNoCheck ("Processed", new Boolean(Processed));
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
if (Remote_Host != null && Remote_Host.length() > 120)
{
log.warning("Length > 120 - truncated");
Remote_Host = Remote_Host.substring(0,119);
}
set_ValueNoCheck ("Remote_Host", Remote_Host);
}
/** Get Remote Host */
public String getRemote_Host() 
{
return (String)get_Value("Remote_Host");
}
/** Set Web Session.
Web Session ID */
public void setWebSession (String WebSession)
{
if (WebSession != null && WebSession.length() > 40)
{
log.warning("Length > 40 - truncated");
WebSession = WebSession.substring(0,39);
}
set_ValueNoCheck ("WebSession", WebSession);
}
/** Get Web Session.
Web Session ID */
public String getWebSession() 
{
return (String)get_Value("WebSession");
}
}
