/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por B_Offer
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.031 */
public class X_B_Offer extends PO
{
/** Constructor estándar */
public X_B_Offer (Properties ctx, int B_Offer_ID, String trxName)
{
super (ctx, B_Offer_ID, trxName);
/** if (B_Offer_ID == 0)
{
setAD_User_ID (0);
setB_Offer_ID (0);
setB_SellerFunds_ID (0);
setB_Topic_ID (0);
setIsWillingToCommit (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_B_Offer (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=682 */
public static final int Table_ID=682;

/** TableName=B_Offer */
public static final String Table_Name="B_Offer";

protected static KeyNamePair Model = new KeyNamePair(682,"B_Offer");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_B_Offer[").append(getID()).append("]");
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
/** Set Offer.
Offer for a Topic */
public void setB_Offer_ID (int B_Offer_ID)
{
set_ValueNoCheck ("B_Offer_ID", new Integer(B_Offer_ID));
}
/** Get Offer.
Offer for a Topic */
public int getB_Offer_ID() 
{
Integer ii = (Integer)get_Value("B_Offer_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Seller Funds.
Seller Funds from Offers on Topics */
public void setB_SellerFunds_ID (int B_SellerFunds_ID)
{
set_Value ("B_SellerFunds_ID", new Integer(B_SellerFunds_ID));
}
/** Get Seller Funds.
Seller Funds from Offers on Topics */
public int getB_SellerFunds_ID() 
{
Integer ii = (Integer)get_Value("B_SellerFunds_ID");
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
