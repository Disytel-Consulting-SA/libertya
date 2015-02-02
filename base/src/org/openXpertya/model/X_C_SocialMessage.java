/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_SocialMessage
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-02-02 11:56:55.172 */
public class X_C_SocialMessage extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_SocialMessage (Properties ctx, int C_SocialMessage_ID, String trxName)
{
super (ctx, C_SocialMessage_ID, trxName);
/** if (C_SocialMessage_ID == 0)
{
setC_SocialConversation_ID (0);
setC_SocialMessage_ID (0);
setMsgContent (null);
setSent (new Timestamp(System.currentTimeMillis()));
setSentBy (0);
}
 */
}
/** Load Constructor */
public X_C_SocialMessage (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_SocialMessage");

/** TableName=C_SocialMessage */
public static final String Table_Name="C_SocialMessage";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_SocialMessage");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_SocialMessage[").append(getID()).append("]");
return sb.toString();
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
/** Set Social Message */
public void setC_SocialMessage_ID (int C_SocialMessage_ID)
{
set_ValueNoCheck ("C_SocialMessage_ID", new Integer(C_SocialMessage_ID));
}
/** Get Social Message */
public int getC_SocialMessage_ID() 
{
Integer ii = (Integer)get_Value("C_SocialMessage_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set In This Conversation */
public void setInThisConversation (String InThisConversation)
{
if (InThisConversation != null && InThisConversation.length() > 1024)
{
log.warning("Length > 1024 - truncated");
InThisConversation = InThisConversation.substring(0,1024);
}
set_Value ("InThisConversation", InThisConversation);
}
/** Get In This Conversation */
public String getInThisConversation() 
{
return (String)get_Value("InThisConversation");
}
/** Set Message Content */
public void setMsgContent (String MsgContent)
{
if (MsgContent == null) throw new IllegalArgumentException ("MsgContent is mandatory");
if (MsgContent.length() > 2147483647)
{
log.warning("Length > 2147483647 - truncated");
MsgContent = MsgContent.substring(0,2147483647);
}
set_Value ("MsgContent", MsgContent);
}
/** Get Message Content */
public String getMsgContent() 
{
return (String)get_Value("MsgContent");
}
public static final int NEWPARTICIPANT_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set New Participant */
public void setNewParticipant (int NewParticipant)
{
set_Value ("NewParticipant", new Integer(NewParticipant));
}
/** Get New Participant */
public int getNewParticipant() 
{
Integer ii = (Integer)get_Value("NewParticipant");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sent */
public void setSent (Timestamp Sent)
{
if (Sent == null) throw new IllegalArgumentException ("Sent is mandatory");
set_Value ("Sent", Sent);
}
/** Get Sent */
public Timestamp getSent() 
{
return (Timestamp)get_Value("Sent");
}
public static final int SENTBY_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set Sent By */
public void setSentBy (int SentBy)
{
set_Value ("SentBy", new Integer(SentBy));
}
/** Get Sent By */
public int getSentBy() 
{
Integer ii = (Integer)get_Value("SentBy");
if (ii == null) return 0;
return ii.intValue();
}
}
