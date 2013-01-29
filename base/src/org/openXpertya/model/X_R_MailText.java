/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_MailText
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.937 */
public class X_R_MailText extends PO
{
/** Constructor estándar */
public X_R_MailText (Properties ctx, int R_MailText_ID, String trxName)
{
super (ctx, R_MailText_ID, trxName);
/** if (R_MailText_ID == 0)
{
setIsHtml (false);
setMailText (null);
setName (null);
setR_MailText_ID (0);
}
 */
}
/** Load Constructor */
public X_R_MailText (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=416 */
public static final int Table_ID=416;

/** TableName=R_MailText */
public static final String Table_Name="R_MailText";

protected static KeyNamePair Model = new KeyNamePair(416,"R_MailText");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_MailText[").append(getID()).append("]");
return sb.toString();
}
/** Set HTML.
Text has HTML tags */
public void setIsHtml (boolean IsHtml)
{
set_Value ("IsHtml", new Boolean(IsHtml));
}
/** Get HTML.
Text has HTML tags */
public boolean isHtml() 
{
Object oo = get_Value("IsHtml");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Subject.
Mail Header (Subject) */
public void setMailHeader (String MailHeader)
{
if (MailHeader != null && MailHeader.length() > 2000)
{
log.warning("Length > 2000 - truncated");
MailHeader = MailHeader.substring(0,1999);
}
set_Value ("MailHeader", MailHeader);
}
/** Get Subject.
Mail Header (Subject) */
public String getMailHeader() 
{
return (String)get_Value("MailHeader");
}
/** Set Mail Text.
Text used for Mail message */
public void setMailText (String MailText)
{
if (MailText == null) throw new IllegalArgumentException ("MailText is mandatory");
if (MailText.length() > 2000)
{
log.warning("Length > 2000 - truncated");
MailText = MailText.substring(0,1999);
}
set_Value ("MailText", MailText);
}
/** Get Mail Text.
Text used for Mail message */
public String getMailText() 
{
return (String)get_Value("MailText");
}
/** Set Mail Text 2.
Optional second text part used for Mail message */
public void setMailText2 (String MailText2)
{
if (MailText2 != null && MailText2.length() > 2000)
{
log.warning("Length > 2000 - truncated");
MailText2 = MailText2.substring(0,1999);
}
set_Value ("MailText2", MailText2);
}
/** Get Mail Text 2.
Optional second text part used for Mail message */
public String getMailText2() 
{
return (String)get_Value("MailText2");
}
/** Set Mail Text 3.
Optional third text part used for Mail message */
public void setMailText3 (String MailText3)
{
if (MailText3 != null && MailText3.length() > 2000)
{
log.warning("Length > 2000 - truncated");
MailText3 = MailText3.substring(0,1999);
}
set_Value ("MailText3", MailText3);
}
/** Get Mail Text 3.
Optional third text part used for Mail message */
public String getMailText3() 
{
return (String)get_Value("MailText3");
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
/** Set Mail Template.
Text templates for mailings */
public void setR_MailText_ID (int R_MailText_ID)
{
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
}
