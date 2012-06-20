/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por B_SellerFunds
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.078 */
public class X_B_SellerFunds extends PO
{
/** Constructor estándar */
public X_B_SellerFunds (Properties ctx, int B_SellerFunds_ID, String trxName)
{
super (ctx, B_SellerFunds_ID, trxName);
/** if (B_SellerFunds_ID == 0)
{
setAD_User_ID (0);
setB_SellerFunds_ID (0);
setCommittedAmt (Env.ZERO);
setNonCommittedAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_B_SellerFunds (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=680 */
public static final int Table_ID=680;

/** TableName=B_SellerFunds */
public static final String Table_Name="B_SellerFunds";

protected static KeyNamePair Model = new KeyNamePair(680,"B_SellerFunds");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_B_SellerFunds[").append(getID()).append("]");
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_User_ID()));
}
/** Set Seller Funds.
Seller Funds from Offers on Topics */
public void setB_SellerFunds_ID (int B_SellerFunds_ID)
{
set_ValueNoCheck ("B_SellerFunds_ID", new Integer(B_SellerFunds_ID));
}
/** Get Seller Funds.
Seller Funds from Offers on Topics */
public int getB_SellerFunds_ID() 
{
Integer ii = (Integer)get_Value("B_SellerFunds_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
if (C_Order_ID <= 0) set_Value ("C_Order_ID", null);
 else 
set_Value ("C_Order_ID", new Integer(C_Order_ID));
}
/** Get Order.
Order */
public int getC_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment.
Payment identifier */
public void setC_Payment_ID (int C_Payment_ID)
{
if (C_Payment_ID <= 0) set_Value ("C_Payment_ID", null);
 else 
set_Value ("C_Payment_ID", new Integer(C_Payment_ID));
}
/** Get Payment.
Payment identifier */
public int getC_Payment_ID() 
{
Integer ii = (Integer)get_Value("C_Payment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Committed Amount.
The (legal) commitment amount */
public void setCommittedAmt (BigDecimal CommittedAmt)
{
if (CommittedAmt == null) throw new IllegalArgumentException ("CommittedAmt is mandatory");
set_Value ("CommittedAmt", CommittedAmt);
}
/** Get Committed Amount.
The (legal) commitment amount */
public BigDecimal getCommittedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CommittedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Not Committed Aount.
Amount not committed yet */
public void setNonCommittedAmt (BigDecimal NonCommittedAmt)
{
if (NonCommittedAmt == null) throw new IllegalArgumentException ("NonCommittedAmt is mandatory");
set_Value ("NonCommittedAmt", NonCommittedAmt);
}
/** Get Not Committed Aount.
Amount not committed yet */
public BigDecimal getNonCommittedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("NonCommittedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
}
