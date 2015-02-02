/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_SocialSubscription
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-02-02 11:56:55.848 */
public class X_C_SocialSubscription extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_SocialSubscription (Properties ctx, int C_SocialSubscription_ID, String trxName)
{
super (ctx, C_SocialSubscription_ID, trxName);
/** if (C_SocialSubscription_ID == 0)
{
setAD_User_ID (0);
setC_SocialConversation_ID (0);
setC_SocialSubscription_ID (0);
setRead (false);
}
 */
}
/** Load Constructor */
public X_C_SocialSubscription (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_SocialSubscription");

/** TableName=C_SocialSubscription */
public static final String Table_Name="C_SocialSubscription";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_SocialSubscription");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_SocialSubscription[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_USER_ID_AD_Reference_ID = MReference.getReferenceID("AD_User");
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
/** Set Conversation */
public void setC_SocialConversation_ID (int C_SocialConversation_ID)
{
set_Value ("C_SocialConversation_ID", new Integer(C_SocialConversation_ID));
}
/** Get Conversation */
public int getC_SocialConversation_ID() 
{
Integer ii = (Integer)get_Value("C_SocialConversation_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Social Subscription */
public void setC_SocialSubscription_ID (int C_SocialSubscription_ID)
{
set_ValueNoCheck ("C_SocialSubscription_ID", new Integer(C_SocialSubscription_ID));
}
/** Get Social Subscription */
public int getC_SocialSubscription_ID() 
{
Integer ii = (Integer)get_Value("C_SocialSubscription_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Read */
public void setRead (boolean Read)
{
set_Value ("Read", new Boolean(Read));
}
/** Get Read */
public boolean isRead() 
{
Object oo = get_Value("Read");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
