/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_SocialConversation
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-02-02 11:56:54.597 */
public class X_C_SocialConversation extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_SocialConversation (Properties ctx, int C_SocialConversation_ID, String trxName)
{
super (ctx, C_SocialConversation_ID, trxName);
/** if (C_SocialConversation_ID == 0)
{
setC_SocialConversation_ID (0);
setMarkAsNotRead (null);
setMarkAsRead (null);
setSubscribe (null);
setUnsubscribe (null);
}
 */
}
/** Load Constructor */
public X_C_SocialConversation (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_SocialConversation");

/** TableName=C_SocialConversation */
public static final String Table_Name="C_SocialConversation";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_SocialConversation");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_SocialConversation[").append(getID()).append("]");
return sb.toString();
}
/** Set Tab.
Tab within a Window */
public void setAD_Tab_ID (int AD_Tab_ID)
{
if (AD_Tab_ID <= 0) set_Value ("AD_Tab_ID", null);
 else 
set_Value ("AD_Tab_ID", new Integer(AD_Tab_ID));
}
/** Get Tab.
Tab within a Window */
public int getAD_Tab_ID() 
{
Integer ii = (Integer)get_Value("AD_Tab_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
if (AD_Table_ID <= 0) set_Value ("AD_Table_ID", null);
 else 
set_Value ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Window.
Data entry or display window */
public void setAD_Window_ID (int AD_Window_ID)
{
if (AD_Window_ID <= 0) set_Value ("AD_Window_ID", null);
 else 
set_Value ("AD_Window_ID", new Integer(AD_Window_ID));
}
/** Get Window.
Data entry or display window */
public int getAD_Window_ID() 
{
Integer ii = (Integer)get_Value("AD_Window_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Conversation */
public void setC_SocialConversation_ID (int C_SocialConversation_ID)
{
set_ValueNoCheck ("C_SocialConversation_ID", new Integer(C_SocialConversation_ID));
}
/** Get Conversation */
public int getC_SocialConversation_ID() 
{
Integer ii = (Integer)get_Value("C_SocialConversation_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Mark As Not Read */
public void setMarkAsNotRead (String MarkAsNotRead)
{
if (MarkAsNotRead == null) throw new IllegalArgumentException ("MarkAsNotRead is mandatory");
if (MarkAsNotRead.length() > 1)
{
log.warning("Length > 1 - truncated");
MarkAsNotRead = MarkAsNotRead.substring(0,1);
}
set_Value ("MarkAsNotRead", MarkAsNotRead);
}
/** Get Mark As Not Read */
public String getMarkAsNotRead() 
{
return (String)get_Value("MarkAsNotRead");
}
/** Set Mark As Read */
public void setMarkAsRead (String MarkAsRead)
{
if (MarkAsRead == null) throw new IllegalArgumentException ("MarkAsRead is mandatory");
if (MarkAsRead.length() > 1)
{
log.warning("Length > 1 - truncated");
MarkAsRead = MarkAsRead.substring(0,1);
}
set_Value ("MarkAsRead", MarkAsRead);
}
/** Get Mark As Read */
public String getMarkAsRead() 
{
return (String)get_Value("MarkAsRead");
}
/** Set Record ID */
public void setRecordID (int RecordID)
{
set_Value ("RecordID", new Integer(RecordID));
}
/** Get Record ID */
public int getRecordID() 
{
Integer ii = (Integer)get_Value("RecordID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Started */
public void setStarted (Timestamp Started)
{
set_Value ("Started", Started);
}
/** Get Started */
public Timestamp getStarted() 
{
return (Timestamp)get_Value("Started");
}
public static final int STARTEDBY_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set Started By */
public void setStartedBy (int StartedBy)
{
set_Value ("StartedBy", new Integer(StartedBy));
}
/** Get Started By */
public int getStartedBy() 
{
Integer ii = (Integer)get_Value("StartedBy");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Subject.
Email Message Subject */
public void setSubject (String Subject)
{
if (Subject != null && Subject.length() > 255)
{
log.warning("Length > 255 - truncated");
Subject = Subject.substring(0,255);
}
set_Value ("Subject", Subject);
}
/** Get Subject.
Email Message Subject */
public String getSubject() 
{
return (String)get_Value("Subject");
}
/** Set Subscribe */
public void setSubscribe (String Subscribe)
{
if (Subscribe == null) throw new IllegalArgumentException ("Subscribe is mandatory");
if (Subscribe.length() > 1)
{
log.warning("Length > 1 - truncated");
Subscribe = Subscribe.substring(0,1);
}
set_Value ("Subscribe", Subscribe);
}
/** Get Subscribe */
public String getSubscribe() 
{
return (String)get_Value("Subscribe");
}
/** Set Unsubscribe */
public void setUnsubscribe (String Unsubscribe)
{
if (Unsubscribe == null) throw new IllegalArgumentException ("Unsubscribe is mandatory");
if (Unsubscribe.length() > 1)
{
log.warning("Length > 1 - truncated");
Unsubscribe = Unsubscribe.substring(0,1);
}
set_Value ("Unsubscribe", Unsubscribe);
}
/** Get Unsubscribe */
public String getUnsubscribe() 
{
return (String)get_Value("Unsubscribe");
}
}
