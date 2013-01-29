/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por B_Bid
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.921 */
public class X_B_Bid extends PO
{
/** Constructor estándar */
public X_B_Bid (Properties ctx, int B_Bid_ID, String trxName)
{
super (ctx, B_Bid_ID, trxName);
/** if (B_Bid_ID == 0)
{
setAD_User_ID (0);
setB_Bid_ID (0);
setB_BuyerFunds_ID (0);
setB_Topic_ID (0);
setIsWillingToCommit (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_B_Bid (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=686 */
public static final int Table_ID=686;

/** TableName=B_Bid */
public static final String Table_Name="B_Bid";

protected static KeyNamePair Model = new KeyNamePair(686,"B_Bid");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_B_Bid[").append(getID()).append("]");
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
/** Set Bid.
Bid for a Topic */
public void setB_Bid_ID (int B_Bid_ID)
{
set_ValueNoCheck ("B_Bid_ID", new Integer(B_Bid_ID));
}
/** Get Bid.
Bid for a Topic */
public int getB_Bid_ID() 
{
Integer ii = (Integer)get_Value("B_Bid_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Buyer Funds.
Buyer Funds for Bids on Topics */
public void setB_BuyerFunds_ID (int B_BuyerFunds_ID)
{
set_Value ("B_BuyerFunds_ID", new Integer(B_BuyerFunds_ID));
}
/** Get Buyer Funds.
Buyer Funds for Bids on Topics */
public int getB_BuyerFunds_ID() 
{
Integer ii = (Integer)get_Value("B_BuyerFunds_ID");
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
/** Set Willing to commit */
public void setIsWillingToCommit (boolean IsWillingToCommit)
{
set_Value ("IsWillingToCommit", new Boolean(IsWillingToCommit));
}
/** Get Willing to commit */
public boolean isWillingToCommit() 
{
Object oo = get_Value("IsWillingToCommit");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Private Note.
Private Note - not visible to the other parties */
public void setPrivateNote (String PrivateNote)
{
if (PrivateNote != null && PrivateNote.length() > 2000)
{
log.warning("Length > 2000 - truncated");
PrivateNote = PrivateNote.substring(0,1999);
}
set_Value ("PrivateNote", PrivateNote);
}
/** Get Private Note.
Private Note - not visible to the other parties */
public String getPrivateNote() 
{
return (String)get_Value("PrivateNote");
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
