/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Client
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-05-20 14:46:51.644 */
public class X_AD_Client extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Client (Properties ctx, int AD_Client_ID, String trxName)
{
super (ctx, AD_Client_ID, trxName);
/** if (AD_Client_ID == 0)
{
setAutoArchive (null);	// N
setIsMultiLingualDocument (false);
setIsSmtpAuthorization (false);	// N
setIsUseBetaFunctions (true);	// Y
setMMPolicy (null);	// F
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_AD_Client (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Client");

/** TableName=AD_Client */
public static final String Table_Name="AD_Client";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Client");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Client[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_LANGUAGE_AD_Reference_ID = MReference.getReferenceID("AD_Language System");
/** Set Language.
Language for this entity */
public void setAD_Language (String AD_Language)
{
if (AD_Language != null && AD_Language.length() > 6)
{
log.warning("Length > 6 - truncated");
AD_Language = AD_Language.substring(0,6);
}
set_Value ("AD_Language", AD_Language);
}
/** Get Language.
Language for this entity */
public String getAD_Language() 
{
return (String)get_Value("AD_Language");
}
public static final int AUTOARCHIVE_AD_Reference_ID = MReference.getReferenceID("AD_Client AutoArchive");
/** Documents = 2 */
public static final String AUTOARCHIVE_Documents = "2";
/** External Documents = 3 */
public static final String AUTOARCHIVE_ExternalDocuments = "3";
/** None = N */
public static final String AUTOARCHIVE_None = "N";
/** All (Reports, Documents) = 1 */
public static final String AUTOARCHIVE_AllReportsDocuments = "1";
/** Set Auto Archive.
Enable and level of automatic Archive of documents */
public void setAutoArchive (String AutoArchive)
{
if (AutoArchive.equals("2") || AutoArchive.equals("3") || AutoArchive.equals("N") || AutoArchive.equals("1"));
 else throw new IllegalArgumentException ("AutoArchive Invalid value - Reference = AUTOARCHIVE_AD_Reference_ID - 2 - 3 - N - 1");
if (AutoArchive == null) throw new IllegalArgumentException ("AutoArchive is mandatory");
if (AutoArchive.length() > 1)
{
log.warning("Length > 1 - truncated");
AutoArchive = AutoArchive.substring(0,1);
}
set_Value ("AutoArchive", AutoArchive);
}
/** Get Auto Archive.
Enable and level of automatic Archive of documents */
public String getAutoArchive() 
{
return (String)get_Value("AutoArchive");
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
/** Set Document Directory.
Directory for documents from the application server */
public void setDocumentDir (String DocumentDir)
{
if (DocumentDir != null && DocumentDir.length() > 60)
{
log.warning("Length > 60 - truncated");
DocumentDir = DocumentDir.substring(0,60);
}
set_Value ("DocumentDir", DocumentDir);
}
/** Get Document Directory.
Directory for documents from the application server */
public String getDocumentDir() 
{
return (String)get_Value("DocumentDir");
}
/** Set Encryption Key.
Encryption Key used for securing data content */
public void setEncryptionKey (String EncryptionKey)
{
if (EncryptionKey != null && EncryptionKey.length() > 255)
{
log.warning("Length > 255 - truncated");
EncryptionKey = EncryptionKey.substring(0,255);
}
set_Value ("EncryptionKey", EncryptionKey);
}
/** Get Encryption Key.
Encryption Key used for securing data content */
public String getEncryptionKey() 
{
return (String)get_Value("EncryptionKey");
}
/** Set Multi Lingual Documents.
Documents are Multi Lingual */
public void setIsMultiLingualDocument (boolean IsMultiLingualDocument)
{
set_Value ("IsMultiLingualDocument", new Boolean(IsMultiLingualDocument));
}
/** Get Multi Lingual Documents.
Documents are Multi Lingual */
public boolean isMultiLingualDocument() 
{
Object oo = get_Value("IsMultiLingualDocument");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set SMTP Authentification.
Your mail server requires Authentification */
public void setIsSmtpAuthorization (boolean IsSmtpAuthorization)
{
set_Value ("IsSmtpAuthorization", new Boolean(IsSmtpAuthorization));
}
/** Get SMTP Authentification.
Your mail server requires Authentification */
public boolean isSmtpAuthorization() 
{
Object oo = get_Value("IsSmtpAuthorization");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Use Beta Functions.
Enable the use of Beta Functionality */
public void setIsUseBetaFunctions (boolean IsUseBetaFunctions)
{
set_Value ("IsUseBetaFunctions", new Boolean(IsUseBetaFunctions));
}
/** Get Use Beta Functions.
Enable the use of Beta Functionality */
public boolean isUseBetaFunctions() 
{
Object oo = get_Value("IsUseBetaFunctions");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set LDAP Query.
Directory service query string */
public void setLDAPQuery (String LDAPQuery)
{
if (LDAPQuery != null && LDAPQuery.length() > 255)
{
log.warning("Length > 255 - truncated");
LDAPQuery = LDAPQuery.substring(0,255);
}
set_Value ("LDAPQuery", LDAPQuery);
}
/** Get LDAP Query.
Directory service query string */
public String getLDAPQuery() 
{
return (String)get_Value("LDAPQuery");
}
/** Set Client Logo Image */
public void setLogoImg (byte[] LogoImg)
{
set_Value ("LogoImg", LogoImg);
}
/** Get Client Logo Image */
public byte[] getLogoImg() 
{
return (byte[])get_Value("LogoImg");
}
public static final int MMPOLICY_AD_Reference_ID = MReference.getReferenceID("_MMPolicy");
/** LiFo = L */
public static final String MMPOLICY_LiFo = "L";
/** FiFo = F */
public static final String MMPOLICY_FiFo = "F";
/** Set Material Policy.
Material Movement Policy */
public void setMMPolicy (String MMPolicy)
{
if (MMPolicy.equals("L") || MMPolicy.equals("F"));
 else throw new IllegalArgumentException ("MMPolicy Invalid value - Reference = MMPOLICY_AD_Reference_ID - L - F");
if (MMPolicy == null) throw new IllegalArgumentException ("MMPolicy is mandatory");
if (MMPolicy.length() > 1)
{
log.warning("Length > 1 - truncated");
MMPolicy = MMPolicy.substring(0,1);
}
set_Value ("MMPolicy", MMPolicy);
}
/** Get Material Policy.
Material Movement Policy */
public String getMMPolicy() 
{
return (String)get_Value("MMPolicy");
}
/** Set Model Validation Classes.
List of data model validation classes separated by ;
 */
public void setModelValidationClasses (String ModelValidationClasses)
{
if (ModelValidationClasses != null && ModelValidationClasses.length() > 255)
{
log.warning("Length > 255 - truncated");
ModelValidationClasses = ModelValidationClasses.substring(0,255);
}
set_Value ("ModelValidationClasses", ModelValidationClasses);
}
/** Get Model Validation Classes.
List of data model validation classes separated by ;
 */
public String getModelValidationClasses() 
{
return (String)get_Value("ModelValidationClasses");
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
/** Set Request EMail.
EMail address to send automated mails from or receive mails for automated processing (fully qualified) */
public void setRequestEMail (String RequestEMail)
{
if (RequestEMail != null && RequestEMail.length() > 60)
{
log.warning("Length > 60 - truncated");
RequestEMail = RequestEMail.substring(0,60);
}
set_Value ("RequestEMail", RequestEMail);
}
/** Get Request EMail.
EMail address to send automated mails from or receive mails for automated processing (fully qualified) */
public String getRequestEMail() 
{
return (String)get_Value("RequestEMail");
}
/** Set Request Folder.
EMail folder to process incoming emails;
 if empty INBOX is used */
public void setRequestFolder (String RequestFolder)
{
if (RequestFolder != null && RequestFolder.length() > 20)
{
log.warning("Length > 20 - truncated");
RequestFolder = RequestFolder.substring(0,20);
}
set_Value ("RequestFolder", RequestFolder);
}
/** Get Request Folder.
EMail folder to process incoming emails;
 if empty INBOX is used */
public String getRequestFolder() 
{
return (String)get_Value("RequestFolder");
}
/** Set Request User.
User Name (ID) of the email owner */
public void setRequestUser (String RequestUser)
{
if (RequestUser != null && RequestUser.length() > 60)
{
log.warning("Length > 60 - truncated");
RequestUser = RequestUser.substring(0,60);
}
set_Value ("RequestUser", RequestUser);
}
/** Get Request User.
User Name (ID) of the email owner */
public String getRequestUser() 
{
return (String)get_Value("RequestUser");
}
/** Set Request User Password.
Password of the user name (ID) for mail processing */
public void setRequestUserPW (String RequestUserPW)
{
if (RequestUserPW != null && RequestUserPW.length() > 20)
{
log.warning("Length > 20 - truncated");
RequestUserPW = RequestUserPW.substring(0,20);
}
set_Value ("RequestUserPW", RequestUserPW);
}
/** Get Request User Password.
Password of the user name (ID) for mail processing */
public String getRequestUserPW() 
{
return (String)get_Value("RequestUserPW");
}
/** Set Mail Host.
Hostname of Mail Server for SMTP and IMAP */
public void setSMTPHost (String SMTPHost)
{
if (SMTPHost != null && SMTPHost.length() > 60)
{
log.warning("Length > 60 - truncated");
SMTPHost = SMTPHost.substring(0,60);
}
set_Value ("SMTPHost", SMTPHost);
}
/** Get Mail Host.
Hostname of Mail Server for SMTP and IMAP */
public String getSMTPHost() 
{
return (String)get_Value("SMTPHost");
}
/** Set UseSSL */
public void setUseSSL (boolean UseSSL)
{
set_Value ("UseSSL", new Boolean(UseSSL));
}
/** Get UseSSL */
public boolean isUseSSL() 
{
Object oo = get_Value("UseSSL");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
/** Set Web Directory.
Web Interface  */
public void setWebDir (String WebDir)
{
if (WebDir != null && WebDir.length() > 60)
{
log.warning("Length > 60 - truncated");
WebDir = WebDir.substring(0,60);
}
set_Value ("WebDir", WebDir);
}
/** Get Web Directory.
Web Interface  */
public String getWebDir() 
{
return (String)get_Value("WebDir");
}
/** Set Web Store Info.
Web Store Header Information */
public void setWebInfo (String WebInfo)
{
if (WebInfo != null && WebInfo.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebInfo = WebInfo.substring(0,2000);
}
set_Value ("WebInfo", WebInfo);
}
/** Get Web Store Info.
Web Store Header Information */
public String getWebInfo() 
{
return (String)get_Value("WebInfo");
}
/** Set Web Order EMail.
EMail address to receive notifications when web orders were processed */
public void setWebOrderEMail (String WebOrderEMail)
{
if (WebOrderEMail != null && WebOrderEMail.length() > 60)
{
log.warning("Length > 60 - truncated");
WebOrderEMail = WebOrderEMail.substring(0,60);
}
set_Value ("WebOrderEMail", WebOrderEMail);
}
/** Get Web Order EMail.
EMail address to receive notifications when web orders were processed */
public String getWebOrderEMail() 
{
return (String)get_Value("WebOrderEMail");
}
/** Set Web Parameter 1.
Web Site Parameter 1 (default: header image) */
public void setWebParam1 (String WebParam1)
{
if (WebParam1 != null && WebParam1.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam1 = WebParam1.substring(0,2000);
}
set_Value ("WebParam1", WebParam1);
}
/** Get Web Parameter 1.
Web Site Parameter 1 (default: header image) */
public String getWebParam1() 
{
return (String)get_Value("WebParam1");
}
/** Set Web Parameter 2.
Web Site Parameter 2 (default index page) */
public void setWebParam2 (String WebParam2)
{
if (WebParam2 != null && WebParam2.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam2 = WebParam2.substring(0,2000);
}
set_Value ("WebParam2", WebParam2);
}
/** Get Web Parameter 2.
Web Site Parameter 2 (default index page) */
public String getWebParam2() 
{
return (String)get_Value("WebParam2");
}
/** Set Web Parameter 3.
Web Site Parameter 3 (default left - menu) */
public void setWebParam3 (String WebParam3)
{
if (WebParam3 != null && WebParam3.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam3 = WebParam3.substring(0,2000);
}
set_Value ("WebParam3", WebParam3);
}
/** Get Web Parameter 3.
Web Site Parameter 3 (default left - menu) */
public String getWebParam3() 
{
return (String)get_Value("WebParam3");
}
/** Set Web Parameter 4.
Web Site Parameter 4 (default footer left) */
public void setWebParam4 (String WebParam4)
{
if (WebParam4 != null && WebParam4.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam4 = WebParam4.substring(0,2000);
}
set_Value ("WebParam4", WebParam4);
}
/** Get Web Parameter 4.
Web Site Parameter 4 (default footer left) */
public String getWebParam4() 
{
return (String)get_Value("WebParam4");
}
/** Set Web Parameter 5.
Web Site Parameter 5 (default footer center) */
public void setWebParam5 (String WebParam5)
{
if (WebParam5 != null && WebParam5.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam5 = WebParam5.substring(0,2000);
}
set_Value ("WebParam5", WebParam5);
}
/** Get Web Parameter 5.
Web Site Parameter 5 (default footer center) */
public String getWebParam5() 
{
return (String)get_Value("WebParam5");
}
/** Set Web Parameter 6.
Web Site Parameter 6 (default footer right) */
public void setWebParam6 (String WebParam6)
{
if (WebParam6 != null && WebParam6.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WebParam6 = WebParam6.substring(0,2000);
}
set_Value ("WebParam6", WebParam6);
}
/** Get Web Parameter 6.
Web Site Parameter 6 (default footer right) */
public String getWebParam6() 
{
return (String)get_Value("WebParam6");
}
}
