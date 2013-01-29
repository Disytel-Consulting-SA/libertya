/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por K_Entry
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.437 */
public class X_K_Entry extends PO
{
/** Constructor estándar */
public X_K_Entry (Properties ctx, int K_Entry_ID, String trxName)
{
super (ctx, K_Entry_ID, trxName);
/** if (K_Entry_ID == 0)
{
setIsPublic (true);	// Y
setK_Entry_ID (0);
setK_Topic_ID (0);
setName (null);
setRating (0);
setTextMsg (null);
}
 */
}
/** Load Constructor */
public X_K_Entry (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=612 */
public static final int Table_ID=612;

/** TableName=K_Entry */
public static final String Table_Name="K_Entry";

protected static KeyNamePair Model = new KeyNamePair(612,"K_Entry");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_K_Entry[").append(getID()).append("]");
return sb.toString();
}
/** Set Session.
User Session Online or Web */
public void setAD_Session_ID (int AD_Session_ID)
{
if (AD_Session_ID <= 0) set_ValueNoCheck ("AD_Session_ID", null);
 else 
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
/** Set Description URL.
URL for the description */
public void setDescriptionURL (String DescriptionURL)
{
if (DescriptionURL != null && DescriptionURL.length() > 120)
{
log.warning("Length > 120 - truncated");
DescriptionURL = DescriptionURL.substring(0,119);
}
set_Value ("DescriptionURL", DescriptionURL);
}
/** Get Description URL.
URL for the description */
public String getDescriptionURL() 
{
return (String)get_Value("DescriptionURL");
}
/** Set Public.
Public can read entry */
public void setIsPublic (boolean IsPublic)
{
set_Value ("IsPublic", new Boolean(IsPublic));
}
/** Get Public.
Public can read entry */
public boolean isPublic() 
{
Object oo = get_Value("IsPublic");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Entry.
Knowledge Entry */
public void setK_Entry_ID (int K_Entry_ID)
{
set_ValueNoCheck ("K_Entry_ID", new Integer(K_Entry_ID));
}
/** Get Entry.
Knowledge Entry */
public int getK_Entry_ID() 
{
Integer ii = (Integer)get_Value("K_Entry_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Knowledge Source.
Source of a Knowledge Entry */
public void setK_Source_ID (int K_Source_ID)
{
if (K_Source_ID <= 0) set_Value ("K_Source_ID", null);
 else 
set_Value ("K_Source_ID", new Integer(K_Source_ID));
}
/** Get Knowledge Source.
Source of a Knowledge Entry */
public int getK_Source_ID() 
{
Integer ii = (Integer)get_Value("K_Source_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Knowledge Topic.
Knowledge Topic */
public void setK_Topic_ID (int K_Topic_ID)
{
set_ValueNoCheck ("K_Topic_ID", new Integer(K_Topic_ID));
}
/** Get Knowledge Topic.
Knowledge Topic */
public int getK_Topic_ID() 
{
Integer ii = (Integer)get_Value("K_Topic_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Keywords.
List of Keywords - separated by space, comma or semicolon */
public void setKeywords (String Keywords)
{
if (Keywords != null && Keywords.length() > 255)
{
log.warning("Length > 255 - truncated");
Keywords = Keywords.substring(0,254);
}
set_Value ("Keywords", Keywords);
}
/** Get Keywords.
List of Keywords - separated by space, comma or semicolon */
public String getKeywords() 
{
return (String)get_Value("Keywords");
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
/** Set Rating.
Classification or Importance */
public void setRating (int Rating)
{
set_Value ("Rating", new Integer(Rating));
}
/** Get Rating.
Classification or Importance */
public int getRating() 
{
Integer ii = (Integer)get_Value("Rating");
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
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
}
