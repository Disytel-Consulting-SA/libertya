/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Subscription_Delivery
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.296 */
public class X_C_Subscription_Delivery extends PO
{
/** Constructor estándar */
public X_C_Subscription_Delivery (Properties ctx, int C_Subscription_Delivery_ID, String trxName)
{
super (ctx, C_Subscription_Delivery_ID, trxName);
/** if (C_Subscription_Delivery_ID == 0)
{
setC_Subscription_Delivery_ID (0);
setC_Subscription_ID (0);
}
 */
}
/** Load Constructor */
public X_C_Subscription_Delivery (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=667 */
public static final int Table_ID=667;

/** TableName=C_Subscription_Delivery */
public static final String Table_Name="C_Subscription_Delivery";

protected static KeyNamePair Model = new KeyNamePair(667,"C_Subscription_Delivery");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Subscription_Delivery[").append(getID()).append("]");
return sb.toString();
}
/** Set Subscription Delivery.
Optional Delivery Record for a Subscription */
public void setC_Subscription_Delivery_ID (int C_Subscription_Delivery_ID)
{
set_ValueNoCheck ("C_Subscription_Delivery_ID", new Integer(C_Subscription_Delivery_ID));
}
/** Get Subscription Delivery.
Optional Delivery Record for a Subscription */
public int getC_Subscription_Delivery_ID() 
{
Integer ii = (Integer)get_Value("C_Subscription_Delivery_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Subscription_Delivery_ID()));
}
/** Set Subscription.
Subscription of a Business Partner of a Product to renew */
public void setC_Subscription_ID (int C_Subscription_ID)
{
set_ValueNoCheck ("C_Subscription_ID", new Integer(C_Subscription_ID));
}
/** Get Subscription.
Subscription of a Business Partner of a Product to renew */
public int getC_Subscription_ID() 
{
Integer ii = (Integer)get_Value("C_Subscription_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
