/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Message
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:26.926 */
public class X_AD_Message extends PO
{
/** Constructor estándar */
public X_AD_Message (Properties ctx, int AD_Message_ID, String trxName)
{
super (ctx, AD_Message_ID, trxName);
/** if (AD_Message_ID == 0)
{
setAD_Message_ID (0);
setEntityType (null);	// U
setMsgText (null);
setMsgType (null);	// I
setValue (null);
}
 */
}
/** Load Constructor */
public X_AD_Message (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=109 */
public static final int Table_ID=109;

/** TableName=AD_Message */
public static final String Table_Name="AD_Message";

protected static KeyNamePair Model = new KeyNamePair(109,"AD_Message");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Message[").append(getID()).append("]");
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
public static final int ENTITYTYPE_AD_Reference_ID=245;
/** Applications Integrated with openXpertya = A */
public static final String ENTITYTYPE_ApplicationsIntegratedWithOpenXpertya = "A";
/** Country Version = C */
public static final String ENTITYTYPE_CountryVersion = "C";
/** Dictionary = D */
public static final String ENTITYTYPE_Dictionary = "D";
/** User maintained = U */
public static final String ENTITYTYPE_UserMaintained = "U";
/** Customization = CUST */
public static final String ENTITYTYPE_Customization = "CUST";
/** Set Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public void setEntityType (String EntityType)
{
if (EntityType.equals("A") || EntityType.equals("C") || EntityType.equals("D") || EntityType.equals("U") || EntityType.equals("CUST"));
 else throw new IllegalArgumentException ("EntityType Invalid value - Reference_ID=245 - A - C - D - U - CUST");
if (EntityType == null) throw new IllegalArgumentException ("EntityType is mandatory");
if (EntityType.length() > 4)
{
log.warning("Length > 4 - truncated");
EntityType = EntityType.substring(0,4);
}
set_Value ("EntityType", EntityType);
}
/** Get Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public String getEntityType() 
{
return (String)get_Value("EntityType");
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
public static final int MSGTYPE_AD_Reference_ID=103;
/** Error = E */
public static final String MSGTYPE_Error = "E";
/** Information = I */
public static final String MSGTYPE_Information = "I";
/** Menu = M */
public static final String MSGTYPE_Menu = "M";
/** Set Message Type.
Type of message (Informational, Menu or Error) */
public void setMsgType (String MsgType)
{
if (MsgType.equals("E") || MsgType.equals("I") || MsgType.equals("M"));
 else throw new IllegalArgumentException ("MsgType Invalid value - Reference_ID=103 - E - I - M");
if (MsgType == null) throw new IllegalArgumentException ("MsgType is mandatory");
if (MsgType.length() > 1)
{
log.warning("Length > 1 - truncated");
MsgType = MsgType.substring(0,1);
}
set_Value ("MsgType", MsgType);
}
/** Get Message Type.
Type of message (Informational, Menu or Error) */
public String getMsgType() 
{
return (String)get_Value("MsgType");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getValue());
}
}
