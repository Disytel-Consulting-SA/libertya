/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por K_Comment
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.421 */
public class X_K_Comment extends PO
{
/** Constructor estándar */
public X_K_Comment (Properties ctx, int K_Comment_ID, String trxName)
{
super (ctx, K_Comment_ID, trxName);
/** if (K_Comment_ID == 0)
{
setIsPublic (true);	// Y
setK_Comment_ID (0);
setK_Entry_ID (0);
setRating (0);
setTextMsg (null);
}
 */
}
/** Load Constructor */
public X_K_Comment (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=613 */
public static final int Table_ID=613;

/** TableName=K_Comment */
public static final String Table_Name="K_Comment";

protected static KeyNamePair Model = new KeyNamePair(613,"K_Comment");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_K_Comment[").append(getID()).append("]");
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
/** Set Entry Comment.
Knowledge Entry Comment */
public void setK_Comment_ID (int K_Comment_ID)
{
set_ValueNoCheck ("K_Comment_ID", new Integer(K_Comment_ID));
}
/** Get Entry Comment.
Knowledge Entry Comment */
public int getK_Comment_ID() 
{
Integer ii = (Integer)get_Value("K_Comment_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getK_Comment_ID()));
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
}
