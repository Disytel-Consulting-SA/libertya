/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_ContactInterest
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.875 */
public class X_R_ContactInterest extends PO
{
/** Constructor estándar */
public X_R_ContactInterest (Properties ctx, int R_ContactInterest_ID, String trxName)
{
super (ctx, R_ContactInterest_ID, trxName);
/** if (R_ContactInterest_ID == 0)
{
setAD_User_ID (0);	// @AD_User_ID@
setR_InterestArea_ID (0);
}
 */
}
/** Load Constructor */
public X_R_ContactInterest (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=528 */
public static final int Table_ID=528;

/** TableName=R_ContactInterest */
public static final String Table_Name="R_ContactInterest";

protected static KeyNamePair Model = new KeyNamePair(528,"R_ContactInterest");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_ContactInterest[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
set_ValueNoCheck ("AD_User_ID", new Integer(AD_User_ID));
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
/** Set Opt-out Date.
Date the contact opted out */
public void setOptOutDate (Timestamp OptOutDate)
{
set_ValueNoCheck ("OptOutDate", OptOutDate);
}
/** Get Opt-out Date.
Date the contact opted out */
public Timestamp getOptOutDate() 
{
return (Timestamp)get_Value("OptOutDate");
}
/** Set Interest Area.
Interest Area or Topic */
public void setR_InterestArea_ID (int R_InterestArea_ID)
{
set_ValueNoCheck ("R_InterestArea_ID", new Integer(R_InterestArea_ID));
}
/** Get Interest Area.
Interest Area or Topic */
public int getR_InterestArea_ID() 
{
Integer ii = (Integer)get_Value("R_InterestArea_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Subscribe Date.
Date the contact actively subscribed */
public void setSubscribeDate (Timestamp SubscribeDate)
{
set_ValueNoCheck ("SubscribeDate", SubscribeDate);
}
/** Get Subscribe Date.
Date the contact actively subscribed */
public Timestamp getSubscribeDate() 
{
return (Timestamp)get_Value("SubscribeDate");
}
}
