/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_SubscriptionType
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.281 */
public class X_C_SubscriptionType extends PO
{
/** Constructor estándar */
public X_C_SubscriptionType (Properties ctx, int C_SubscriptionType_ID, String trxName)
{
super (ctx, C_SubscriptionType_ID, trxName);
/** if (C_SubscriptionType_ID == 0)
{
setC_SubscriptionType_ID (0);
setFrequency (0);
setFrequencyType (null);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_SubscriptionType (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=668 */
public static final int Table_ID=668;

/** TableName=C_SubscriptionType */
public static final String Table_Name="C_SubscriptionType";

protected static KeyNamePair Model = new KeyNamePair(668,"C_SubscriptionType");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_SubscriptionType[").append(getID()).append("]");
return sb.toString();
}
/** Set Subscription Type.
Type of subscription */
public void setC_SubscriptionType_ID (int C_SubscriptionType_ID)
{
set_ValueNoCheck ("C_SubscriptionType_ID", new Integer(C_SubscriptionType_ID));
}
/** Get Subscription Type.
Type of subscription */
public int getC_SubscriptionType_ID() 
{
Integer ii = (Integer)get_Value("C_SubscriptionType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Frequency.
Frequency of events */
public void setFrequency (int Frequency)
{
set_Value ("Frequency", new Integer(Frequency));
}
/** Get Frequency.
Frequency of events */
public int getFrequency() 
{
Integer ii = (Integer)get_Value("Frequency");
if (ii == null) return 0;
return ii.intValue();
}
public static final int FREQUENCYTYPE_AD_Reference_ID=221;
/** Minute = M */
public static final String FREQUENCYTYPE_Minute = "M";
/** Hour = H */
public static final String FREQUENCYTYPE_Hour = "H";
/** Day = D */
public static final String FREQUENCYTYPE_Day = "D";
/** Set Frequency Type.
Frequency of event */
public void setFrequencyType (String FrequencyType)
{
if (FrequencyType.equals("M") || FrequencyType.equals("H") || FrequencyType.equals("D"));
 else throw new IllegalArgumentException ("FrequencyType Invalid value - Reference_ID=221 - M - H - D");
if (FrequencyType == null) throw new IllegalArgumentException ("FrequencyType is mandatory");
if (FrequencyType.length() > 1)
{
log.warning("Length > 1 - truncated");
FrequencyType = FrequencyType.substring(0,0);
}
set_Value ("FrequencyType", FrequencyType);
}
/** Get Frequency Type.
Frequency of event */
public String getFrequencyType() 
{
return (String)get_Value("FrequencyType");
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
}
