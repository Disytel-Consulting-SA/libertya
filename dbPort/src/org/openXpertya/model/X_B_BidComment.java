/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por B_BidComment
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.953 */
public class X_B_BidComment extends PO
{
/** Constructor estándar */
public X_B_BidComment (Properties ctx, int B_BidComment_ID, String trxName)
{
super (ctx, B_BidComment_ID, trxName);
/** if (B_BidComment_ID == 0)
{
setAD_User_ID (0);
setB_BidComment_ID (0);
setB_Topic_ID (0);
setTextMsg (null);
}
 */
}
/** Load Constructor */
public X_B_BidComment (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=685 */
public static final int Table_ID=685;

/** TableName=B_BidComment */
public static final String Table_Name="B_BidComment";

protected static KeyNamePair Model = new KeyNamePair(685,"B_BidComment");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_B_BidComment[").append(getID()).append("]");
return sb.toString();
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
/** Set Bid Comment.
Make a comment to a Bid Topic */
public void setB_BidComment_ID (int B_BidComment_ID)
{
set_ValueNoCheck ("B_BidComment_ID", new Integer(B_BidComment_ID));
}
/** Get Bid Comment.
Make a comment to a Bid Topic */
public int getB_BidComment_ID() 
{
Integer ii = (Integer)get_Value("B_BidComment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Topic.
Auction Topic */
public void setB_Topic_ID (int B_Topic_ID)
{
set_Value ("B_Topic_ID", new Integer(B_Topic_ID));
}
/** Get Topic.
Auction Topic */
public int getB_Topic_ID() 
{
Integer ii = (Integer)get_Value("B_Topic_ID");
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
