/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_AlertRecipient
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:21.968 */
public class X_AD_AlertRecipient extends PO
{
/** Constructor estándar */
public X_AD_AlertRecipient (Properties ctx, int AD_AlertRecipient_ID, String trxName)
{
super (ctx, AD_AlertRecipient_ID, trxName);
/** if (AD_AlertRecipient_ID == 0)
{
setAD_AlertRecipient_ID (0);
setAD_Alert_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_AlertRecipient (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=592 */
public static final int Table_ID=592;

/** TableName=AD_AlertRecipient */
public static final String Table_Name="AD_AlertRecipient";

protected static KeyNamePair Model = new KeyNamePair(592,"AD_AlertRecipient");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_AlertRecipient[").append(getID()).append("]");
return sb.toString();
}
/** Set Alert Recipient.
Recipient of the Alert Notification */
public void setAD_AlertRecipient_ID (int AD_AlertRecipient_ID)
{
set_ValueNoCheck ("AD_AlertRecipient_ID", new Integer(AD_AlertRecipient_ID));
}
/** Get Alert Recipient.
Recipient of the Alert Notification */
public int getAD_AlertRecipient_ID() 
{
Integer ii = (Integer)get_Value("AD_AlertRecipient_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Alert.
openXpertya Alert */
public void setAD_Alert_ID (int AD_Alert_ID)
{
set_ValueNoCheck ("AD_Alert_ID", new Integer(AD_Alert_ID));
}
/** Get Alert.
openXpertya Alert */
public int getAD_Alert_ID() 
{
Integer ii = (Integer)get_Value("AD_Alert_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Role.
Responsibility Role */
public void setAD_Role_ID (int AD_Role_ID)
{
if (AD_Role_ID <= 0) set_Value ("AD_Role_ID", null);
 else 
set_Value ("AD_Role_ID", new Integer(AD_Role_ID));
}
/** Get Role.
Responsibility Role */
public int getAD_Role_ID() 
{
Integer ii = (Integer)get_Value("AD_Role_ID");
if (ii == null) return 0;
return ii.intValue();
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_User_ID()));
}
}
