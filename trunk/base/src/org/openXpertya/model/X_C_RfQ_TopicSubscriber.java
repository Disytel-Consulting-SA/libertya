/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RfQ_TopicSubscriber
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.14 */
public class X_C_RfQ_TopicSubscriber extends PO
{
/** Constructor estándar */
public X_C_RfQ_TopicSubscriber (Properties ctx, int C_RfQ_TopicSubscriber_ID, String trxName)
{
super (ctx, C_RfQ_TopicSubscriber_ID, trxName);
/** if (C_RfQ_TopicSubscriber_ID == 0)
{
setC_BPartner_ID (0);
setC_BPartner_Location_ID (0);
setC_RfQ_TopicSubscriber_ID (0);
setC_RfQ_Topic_ID (0);
}
 */
}
/** Load Constructor */
public X_C_RfQ_TopicSubscriber (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=670 */
public static final int Table_ID=670;

/** TableName=C_RfQ_TopicSubscriber */
public static final String Table_Name="C_RfQ_TopicSubscriber";

protected static KeyNamePair Model = new KeyNamePair(670,"C_RfQ_TopicSubscriber");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RfQ_TopicSubscriber[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_Value ("AD_User_ID", null);
 else 
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
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Partner Location.
Identifies the (ship to) address for this Business Partner */
public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
{
set_Value ("C_BPartner_Location_ID", new Integer(C_BPartner_Location_ID));
}
/** Get Partner Location.
Identifies the (ship to) address for this Business Partner */
public int getC_BPartner_Location_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set RfQ Subscriber.
Request for Quotation Topic Subscriber */
public void setC_RfQ_TopicSubscriber_ID (int C_RfQ_TopicSubscriber_ID)
{
set_ValueNoCheck ("C_RfQ_TopicSubscriber_ID", new Integer(C_RfQ_TopicSubscriber_ID));
}
/** Get RfQ Subscriber.
Request for Quotation Topic Subscriber */
public int getC_RfQ_TopicSubscriber_ID() 
{
Integer ii = (Integer)get_Value("C_RfQ_TopicSubscriber_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set RfQ Topic.
Topic for Request for Quotations */
public void setC_RfQ_Topic_ID (int C_RfQ_Topic_ID)
{
set_ValueNoCheck ("C_RfQ_Topic_ID", new Integer(C_RfQ_Topic_ID));
}
/** Get RfQ Topic.
Topic for Request for Quotations */
public int getC_RfQ_Topic_ID() 
{
Integer ii = (Integer)get_Value("C_RfQ_Topic_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_RfQ_Topic_ID()));
}
/** Set Opt-out Date.
Date the contact opted out */
public void setOptOutDate (Timestamp OptOutDate)
{
set_Value ("OptOutDate", OptOutDate);
}
/** Get Opt-out Date.
Date the contact opted out */
public Timestamp getOptOutDate() 
{
return (Timestamp)get_Value("OptOutDate");
}
/** Set Subscribe Date.
Date the contact actively subscribed */
public void setSubscribeDate (Timestamp SubscribeDate)
{
set_Value ("SubscribeDate", SubscribeDate);
}
/** Get Subscribe Date.
Date the contact actively subscribed */
public Timestamp getSubscribeDate() 
{
return (Timestamp)get_Value("SubscribeDate");
}
}
