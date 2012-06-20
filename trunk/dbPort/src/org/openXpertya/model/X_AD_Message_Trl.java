/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Message_Trl
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:26.944 */
public class X_AD_Message_Trl extends PO
{
/** Constructor estándar */
public X_AD_Message_Trl (Properties ctx, int AD_Message_Trl_ID, String trxName)
{
super (ctx, AD_Message_Trl_ID, trxName);
/** if (AD_Message_Trl_ID == 0)
{
setAD_Language (null);
setAD_Message_ID (0);
setIsTranslated (false);
setMsgText (null);
}
 */
}
/** Load Constructor */
public X_AD_Message_Trl (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=119 */
public static final int Table_ID=119;

/** TableName=AD_Message_Trl */
public static final String Table_Name="AD_Message_Trl";

protected static KeyNamePair Model = new KeyNamePair(119,"AD_Message_Trl");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Message_Trl[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
/** Set Component Version Identifier */
public void setAD_ComponentVersion_ID (int AD_ComponentVersion_ID)
{
if (AD_ComponentVersion_ID <= 0) set_Value ("AD_ComponentVersion_ID", null);
 else 
set_Value ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_LANGUAGE_AD_Reference_ID=106;
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
/** Set Message.
System Message */
public void setAD_Message_ID (int AD_Message_ID)
{
set_ValueNoCheck ("AD_Message_ID", new Integer(AD_Message_ID));
}
/** Get Message.
System Message */
public int getAD_Message_ID() 
{
Integer ii = (Integer)get_Value("AD_Message_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_Message_ID()));
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
/** Set Message Text.
Textual Informational, Menu or Error Message */
public void setMsgText (String MsgText)
{
if (MsgText == null) throw new IllegalArgumentException ("MsgText is mandatory");
if (MsgText.length() > 2000)
{
log.warning("Length > 2000 - truncated");
MsgText = MsgText.substring(0,2000);
}
set_Value ("MsgText", MsgText);
}
/** Get Message Text.
Textual Informational, Menu or Error Message */
public String getMsgText() 
{
return (String)get_Value("MsgText");
}
/** Set Message Tip.
Additional tip or help for this message */
public void setMsgTip (String MsgTip)
{
if (MsgTip != null && MsgTip.length() > 2000)
{
log.warning("Length > 2000 - truncated");
MsgTip = MsgTip.substring(0,2000);
}
set_Value ("MsgTip", MsgTip);
}
/** Get Message Tip.
Additional tip or help for this message */
public String getMsgTip() 
{
return (String)get_Value("MsgTip");
}
}
