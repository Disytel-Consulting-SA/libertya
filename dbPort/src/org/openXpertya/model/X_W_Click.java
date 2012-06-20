/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por W_Click
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.406 */
public class X_W_Click extends PO
{
/** Constructor estándar */
public X_W_Click (Properties ctx, int W_Click_ID, String trxName)
{
super (ctx, W_Click_ID, trxName);
/** if (W_Click_ID == 0)
{
setProcessed (false);
setW_Click_ID (0);
}
 */
}
/** Load Constructor */
public X_W_Click (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=550 */
public static final int Table_ID=550;

/** TableName=W_Click */
public static final String Table_Name="W_Click";

protected static KeyNamePair Model = new KeyNamePair(550,"W_Click");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_W_Click[").append(getID()).append("]");
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
/** Set Accept Language.
Language accepted based on browser information */
public void setAcceptLanguage (String AcceptLanguage)
{
if (AcceptLanguage != null && AcceptLanguage.length() > 60)
{
log.warning("Length > 60 - truncated");
AcceptLanguage = AcceptLanguage.substring(0,59);
}
set_Value ("AcceptLanguage", AcceptLanguage);
}
/** Get Accept Language.
Language accepted based on browser information */
public String getAcceptLanguage() 
{
return (String)get_Value("AcceptLanguage");
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
set_Value ("EMail", EMail);
}
/** Get EMail.
Electronic Mail Address */
public String getEMail() 
{
return (String)get_Value("EMail");
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
/** Set Referrer.
Referring web address */
public void setReferrer (String Referrer)
{
if (Referrer != null && Referrer.length() > 120)
{
log.warning("Length > 120 - truncated");
Referrer = Referrer.substring(0,119);
}
set_Value ("Referrer", Referrer);
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
set_Value ("Remote_Addr", Remote_Addr);
}
/** Get Remote Addr.
Remote Address */
public String getRemote_Addr() 
{
return (String)get_Value("Remote_Addr");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getRemote_Addr());
}
/** Set Remote Host */
public void setRemote_Host (String Remote_Host)
{
if (Remote_Host != null && Remote_Host.length() > 120)
{
log.warning("Length > 120 - truncated");
Remote_Host = Remote_Host.substring(0,119);
}
set_Value ("Remote_Host", Remote_Host);
}
/** Get Remote Host */
public String getRemote_Host() 
{
return (String)get_Value("Remote_Host");
}
/** Set Target URL.
URL for the Target */
public void setTargetURL (String TargetURL)
{
if (TargetURL != null && TargetURL.length() > 120)
{
log.warning("Length > 120 - truncated");
TargetURL = TargetURL.substring(0,119);
}
set_Value ("TargetURL", TargetURL);
}
/** Get Target URL.
URL for the Target */
public String getTargetURL() 
{
return (String)get_Value("TargetURL");
}
/** Set User Agent.
Browser Used */
public void setUserAgent (String UserAgent)
{
if (UserAgent != null && UserAgent.length() > 255)
{
log.warning("Length > 255 - truncated");
UserAgent = UserAgent.substring(0,254);
}
set_Value ("UserAgent", UserAgent);
}
/** Get User Agent.
Browser Used */
public String getUserAgent() 
{
return (String)get_Value("UserAgent");
}
/** Set Click Count.
Web Click Management */
public void setW_ClickCount_ID (int W_ClickCount_ID)
{
if (W_ClickCount_ID <= 0) set_ValueNoCheck ("W_ClickCount_ID", null);
 else 
set_ValueNoCheck ("W_ClickCount_ID", new Integer(W_ClickCount_ID));
}
/** Get Click Count.
Web Click Management */
public int getW_ClickCount_ID() 
{
Integer ii = (Integer)get_Value("W_ClickCount_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Web Click.
Individual Web Click */
public void setW_Click_ID (int W_Click_ID)
{
set_ValueNoCheck ("W_Click_ID", new Integer(W_Click_ID));
}
/** Get Web Click.
Individual Web Click */
public int getW_Click_ID() 
{
Integer ii = (Integer)get_Value("W_Click_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
