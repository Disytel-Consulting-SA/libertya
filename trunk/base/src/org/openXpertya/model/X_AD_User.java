/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_User
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-02-06 09:42:19.753 */
public class X_AD_User extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_User (Properties ctx, int AD_User_ID, String trxName)
{
super (ctx, AD_User_ID, trxName);
/** if (AD_User_ID == 0)
{
setAD_User_ID (0);
setIsLDAPAuthorized (false);	// N
setIsSystemAccess (false);
setName (null);
setNotificationType (null);	// E
setNotifyOnConversationActivity (false);
}
 */
}
/** Load Constructor */
public X_AD_User (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_User");

/** TableName=AD_User */
public static final String Table_Name="AD_User";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_User");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_User[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
public static final int AD_ORGTRX_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (Trx)");
/** Set Trx Organization.
Performing or initiating organization */
public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
{
if (AD_OrgTrx_ID <= 0) set_Value ("AD_OrgTrx_ID", null);
 else 
set_Value ("AD_OrgTrx_ID", new Integer(AD_OrgTrx_ID));
}
/** Get Trx Organization.
Performing or initiating organization */
public int getAD_OrgTrx_ID() 
{
Integer ii = (Integer)get_Value("AD_OrgTrx_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Birthday.
Birthday or Anniversary day */
public void setBirthday (Timestamp Birthday)
{
set_Value ("Birthday", Birthday);
}
/** Get Birthday.
Birthday or Anniversary day */
public Timestamp getBirthday() 
{
return (Timestamp)get_Value("Birthday");
}
public static final int C_BPARTNER_CONTACT_ID_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set C_BPartner_Contact_ID */
public void setC_BPartner_Contact_ID (int C_BPartner_Contact_ID)
{
if (C_BPartner_Contact_ID <= 0) set_Value ("C_BPartner_Contact_ID", null);
 else 
set_Value ("C_BPartner_Contact_ID", new Integer(C_BPartner_Contact_ID));
}
/** Get C_BPartner_Contact_ID */
public int getC_BPartner_Contact_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Contact_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
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
if (C_BPartner_Location_ID <= 0) set_Value ("C_BPartner_Location_ID", null);
 else 
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
/** Set Greeting.
Greeting to print on correspondence */
public void setC_Greeting_ID (int C_Greeting_ID)
{
if (C_Greeting_ID <= 0) set_Value ("C_Greeting_ID", null);
 else 
set_Value ("C_Greeting_ID", new Integer(C_Greeting_ID));
}
/** Get Greeting.
Greeting to print on correspondence */
public int getC_Greeting_ID() 
{
Integer ii = (Integer)get_Value("C_Greeting_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Comments.
Comments or additional information */
public void setComments (String Comments)
{
if (Comments != null && Comments.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Comments = Comments.substring(0,2000);
}
set_Value ("Comments", Comments);
}
/** Get Comments.
Comments or additional information */
public String getComments() 
{
return (String)get_Value("Comments");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set EMail.
Electronic Mail Address */
public void setEMail (String EMail)
{
if (EMail != null && EMail.length() > 60)
{
log.warning("Length > 60 - truncated");
EMail = EMail.substring(0,60);
}
set_Value ("EMail", EMail);
}
/** Get EMail.
Electronic Mail Address */
public String getEMail() 
{
return (String)get_Value("EMail");
}
/** Set EMail User ID.
User Name (ID) in the Mail System */
public void setEMailUser (String EMailUser)
{
if (EMailUser != null && EMailUser.length() > 60)
{
log.warning("Length > 60 - truncated");
EMailUser = EMailUser.substring(0,60);
}
set_Value ("EMailUser", EMailUser);
}
/** Get EMail User ID.
User Name (ID) in the Mail System */
public String getEMailUser() 
{
return (String)get_Value("EMailUser");
}
/** Set EMail User Password.
Password of your email user id */
public void setEMailUserPW (String EMailUserPW)
{
if (EMailUserPW != null && EMailUserPW.length() > 20)
{
log.warning("Length > 20 - truncated");
EMailUserPW = EMailUserPW.substring(0,20);
}
set_ValueE ("EMailUserPW", EMailUserPW);
}
/** Get EMail User Password.
Password of your email user id */
public String getEMailUserPW() 
{
return (String)get_ValueE("EMailUserPW");
}
/** Set Verification Info.
Verification information of EMail Address */
public void setEMailVerify (String EMailVerify)
{
if (EMailVerify != null && EMailVerify.length() > 40)
{
log.warning("Length > 40 - truncated");
EMailVerify = EMailVerify.substring(0,40);
}
set_ValueNoCheck ("EMailVerify", EMailVerify);
}
/** Get Verification Info.
Verification information of EMail Address */
public String getEMailVerify() 
{
return (String)get_Value("EMailVerify");
}
/** Set EMail Verify.
Date Email was verified */
public void setEMailVerifyDate (Timestamp EMailVerifyDate)
{
set_ValueNoCheck ("EMailVerifyDate", EMailVerifyDate);
}
/** Get EMail Verify.
Date Email was verified */
public Timestamp getEMailVerifyDate() 
{
return (Timestamp)get_Value("EMailVerifyDate");
}
/** Set Fax.
Facsimile number */
public void setFax (String Fax)
{
if (Fax != null && Fax.length() > 40)
{
log.warning("Length > 40 - truncated");
Fax = Fax.substring(0,40);
}
set_Value ("Fax", Fax);
}
/** Get Fax.
Facsimile number */
public String getFax() 
{
return (String)get_Value("Fax");
}
/** Set Authorize via LDAP.
Authorize via LDAP (directory) services */
public void setIsLDAPAuthorized (boolean IsLDAPAuthorized)
{
set_Value ("IsLDAPAuthorized", new Boolean(IsLDAPAuthorized));
}
/** Get Authorize via LDAP.
Authorize via LDAP (directory) services */
public boolean isLDAPAuthorized() 
{
Object oo = get_Value("IsLDAPAuthorized");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is System Access */
public void setIsSystemAccess (boolean IsSystemAccess)
{
set_Value ("IsSystemAccess", new Boolean(IsSystemAccess));
}
/** Get Is System Access */
public boolean isSystemAccess() 
{
Object oo = get_Value("IsSystemAccess");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Last Contact.
Date this individual was last contacted */
public void setLastContact (Timestamp LastContact)
{
set_Value ("LastContact", LastContact);
}
/** Get Last Contact.
Date this individual was last contacted */
public Timestamp getLastContact() 
{
return (Timestamp)get_Value("LastContact");
}
/** Set Last Password Change Date */
public void setLastPasswordChangeDate (Timestamp LastPasswordChangeDate)
{
set_ValueNoCheck ("LastPasswordChangeDate", LastPasswordChangeDate);
}
/** Get Last Password Change Date */
public Timestamp getLastPasswordChangeDate() 
{
return (Timestamp)get_Value("LastPasswordChangeDate");
}
/** Set Last Result.
Result of last contact */
public void setLastResult (String LastResult)
{
if (LastResult != null && LastResult.length() > 255)
{
log.warning("Length > 255 - truncated");
LastResult = LastResult.substring(0,255);
}
set_Value ("LastResult", LastResult);
}
/** Get Last Result.
Result of last contact */
public String getLastResult() 
{
return (String)get_Value("LastResult");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
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
public static final int NOTIFICATIONTYPE_AD_Reference_ID = MReference.getReferenceID("AD_User NotificationType");
/** EMail+Notice = B */
public static final String NOTIFICATIONTYPE_EMailPlusNotice = "B";
/** Notice = N */
public static final String NOTIFICATIONTYPE_Notice = "N";
/** None = X */
public static final String NOTIFICATIONTYPE_None = "X";
/** EMail = E */
public static final String NOTIFICATIONTYPE_EMail = "E";
/** Set Notification Type.
Type of Notifications */
public void setNotificationType (String NotificationType)
{
if (NotificationType.equals("B") || NotificationType.equals("N") || NotificationType.equals("X") || NotificationType.equals("E"));
 else throw new IllegalArgumentException ("NotificationType Invalid value - Reference = NOTIFICATIONTYPE_AD_Reference_ID - B - N - X - E");
if (NotificationType == null) throw new IllegalArgumentException ("NotificationType is mandatory");
if (NotificationType.length() > 1)
{
log.warning("Length > 1 - truncated");
NotificationType = NotificationType.substring(0,1);
}
set_Value ("NotificationType", NotificationType);
}
/** Get Notification Type.
Type of Notifications */
public String getNotificationType() 
{
return (String)get_Value("NotificationType");
}
/** Set Notify On Conversation Activity */
public void setNotifyOnConversationActivity (boolean NotifyOnConversationActivity)
{
set_Value ("NotifyOnConversationActivity", new Boolean(NotifyOnConversationActivity));
}
/** Get Notify On Conversation Activity */
public boolean isNotifyOnConversationActivity() 
{
Object oo = get_Value("NotifyOnConversationActivity");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Goal.
Performance Goal */
public void setPA_Goal_ID (int PA_Goal_ID)
{
if (PA_Goal_ID <= 0) set_Value ("PA_Goal_ID", null);
 else 
set_Value ("PA_Goal_ID", new Integer(PA_Goal_ID));
}
/** Get Goal.
Performance Goal */
public int getPA_Goal_ID() 
{
Integer ii = (Integer)get_Value("PA_Goal_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PA_GOALPRIVATE_ID_AD_Reference_ID = MReference.getReferenceID("PA_Goal");
/** Set Personal Goal.
Goal hierarchy only visible to user */
public void setPA_GoalPrivate_ID (int PA_GoalPrivate_ID)
{
if (PA_GoalPrivate_ID <= 0) set_Value ("PA_GoalPrivate_ID", null);
 else 
set_Value ("PA_GoalPrivate_ID", new Integer(PA_GoalPrivate_ID));
}
/** Get Personal Goal.
Goal hierarchy only visible to user */
public int getPA_GoalPrivate_ID() 
{
Integer ii = (Integer)get_Value("PA_GoalPrivate_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Password.
Password of any length (case sensitive) */
public void setPassword (String Password)
{
if (Password != null && Password.length() > 40)
{
log.warning("Length > 40 - truncated");
Password = Password.substring(0,40);
}
set_ValueE ("Password", Password);
}
/** Get Password.
Password of any length (case sensitive) */
public String getPassword() 
{
return (String)get_ValueE("Password");
}
/** Set Phone.
Identifies a telephone number */
public void setPhone (String Phone)
{
if (Phone != null && Phone.length() > 40)
{
log.warning("Length > 40 - truncated");
Phone = Phone.substring(0,40);
}
set_Value ("Phone", Phone);
}
/** Get Phone.
Identifies a telephone number */
public String getPhone() 
{
return (String)get_Value("Phone");
}
/** Set 2nd Phone.
Identifies an alternate telephone number. */
public void setPhone2 (String Phone2)
{
if (Phone2 != null && Phone2.length() > 40)
{
log.warning("Length > 40 - truncated");
Phone2 = Phone2.substring(0,40);
}
set_Value ("Phone2", Phone2);
}
/** Get 2nd Phone.
Identifies an alternate telephone number. */
public String getPhone2() 
{
return (String)get_Value("Phone2");
}
/** Set phone3 */
public void setphone3 (String phone3)
{
if (phone3 != null && phone3.length() > 20)
{
log.warning("Length > 20 - truncated");
phone3 = phone3.substring(0,20);
}
set_Value ("phone3", phone3);
}
/** Get phone3 */
public String getphone3() 
{
return (String)get_Value("phone3");
}
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int SUPERVISOR_ID_AD_Reference_ID = MReference.getReferenceID("AD_User");
/** Set Supervisor.
Supervisor for this user/organization - used for escalation and approval */
public void setSupervisor_ID (int Supervisor_ID)
{
if (Supervisor_ID <= 0) set_Value ("Supervisor_ID", null);
 else 
set_Value ("Supervisor_ID", new Integer(Supervisor_ID));
}
/** Get Supervisor.
Supervisor for this user/organization - used for escalation and approval */
public int getSupervisor_ID() 
{
Integer ii = (Integer)get_Value("Supervisor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Title.
Name this entity is referred to as */
public void setTitle (String Title)
{
if (Title != null && Title.length() > 40)
{
log.warning("Length > 40 - truncated");
Title = Title.substring(0,40);
}
set_Value ("Title", Title);
}
/** Get Title.
Name this entity is referred to as */
public String getTitle() 
{
return (String)get_Value("Title");
}
}
