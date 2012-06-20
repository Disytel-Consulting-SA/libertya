/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_System
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:25.046 */
public class X_AD_System extends PO
{
/** Constructor estándar */
public X_AD_System (Properties ctx, int AD_System_ID, String trxName)
{
super (ctx, AD_System_ID, trxName);
/** if (AD_System_ID == 0)
{
setAD_System_ID (0);	// 0
setInfo (null);
setName (null);
setPassword (null);
setReplicationType (null);	// L
setUserName (null);
setVersion (null);
}
 */
}
/** Load Constructor */
public X_AD_System (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=531 */
public static final int Table_ID=531;

/** TableName=AD_System */
public static final String Table_Name="AD_System";

protected static KeyNamePair Model = new KeyNamePair(531,"AD_System");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_System[").append(getID()).append("]");
return sb.toString();
}
/** Set System.
System Definition */
public void setAD_System_ID (int AD_System_ID)
{
set_ValueNoCheck ("AD_System_ID", new Integer(AD_System_ID));
}
/** Get System.
System Definition */
public int getAD_System_ID() 
{
Integer ii = (Integer)get_Value("AD_System_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Custom Prefix.
Prefix for Custom entities */
public void setCustomPrefix (String CustomPrefix)
{
if (CustomPrefix != null && CustomPrefix.length() > 60)
{
log.warning("Length > 60 - truncated");
CustomPrefix = CustomPrefix.substring(0,59);
}
set_Value ("CustomPrefix", CustomPrefix);
}
/** Get Custom Prefix.
Prefix for Custom entities */
public String getCustomPrefix() 
{
return (String)get_Value("CustomPrefix");
}
/** Set Address of DB Server.
Address of the database server */
public void setDBAddress (String DBAddress)
{
if (DBAddress != null && DBAddress.length() > 60)
{
log.warning("Length > 60 - truncated");
DBAddress = DBAddress.substring(0,59);
}
set_Value ("DBAddress", DBAddress);
}
/** Get Address of DB Server.
Address of the database server */
public String getDBAddress() 
{
return (String)get_Value("DBAddress");
}
/** Set Database Name.
Database Name */
public void setDBInstance (String DBInstance)
{
if (DBInstance != null && DBInstance.length() > 60)
{
log.warning("Length > 60 - truncated");
DBInstance = DBInstance.substring(0,59);
}
set_Value ("DBInstance", DBInstance);
}
/** Get Database Name.
Database Name */
public String getDBInstance() 
{
return (String)get_Value("DBInstance");
}
/** Set Encryption Key.
Encryption Key used for securing data content */
public void setEncryptionKey (String EncryptionKey)
{
if (EncryptionKey != null && EncryptionKey.length() > 255)
{
log.warning("Length > 255 - truncated");
EncryptionKey = EncryptionKey.substring(0,254);
}
set_Value ("EncryptionKey", EncryptionKey);
}
/** Get Encryption Key.
Encryption Key used for securing data content */
public String getEncryptionKey() 
{
return (String)get_Value("EncryptionKey");
}
/** Set ID Range End.
End if the ID Range used */
public void setIDRangeEnd (BigDecimal IDRangeEnd)
{
set_Value ("IDRangeEnd", IDRangeEnd);
}
/** Get ID Range End.
End if the ID Range used */
public BigDecimal getIDRangeEnd() 
{
BigDecimal bd = (BigDecimal)get_Value("IDRangeEnd");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set ID Range Start.
Start of the ID Range used */
public void setIDRangeStart (BigDecimal IDRangeStart)
{
set_Value ("IDRangeStart", IDRangeStart);
}
/** Get ID Range Start.
Start of the ID Range used */
public BigDecimal getIDRangeStart() 
{
BigDecimal bd = (BigDecimal)get_Value("IDRangeStart");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Info.
Information */
public void setInfo (String Info)
{
if (Info == null) throw new IllegalArgumentException ("Info is mandatory");
if (Info.length() > 255)
{
log.warning("Length > 255 - truncated");
Info = Info.substring(0,254);
}
set_ValueNoCheck ("Info", Info);
}
/** Get Info.
Information */
public String getInfo() 
{
return (String)get_Value("Info");
}
/** Set Just Migrated.
Value set by Migration for post-Migation tasks. */
public void setIsJustMigrated (boolean IsJustMigrated)
{
set_Value ("IsJustMigrated", new Boolean(IsJustMigrated));
}
/** Get Just Migrated.
Value set by Migration for post-Migation tasks. */
public boolean isJustMigrated() 
{
Object oo = get_Value("IsJustMigrated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set LDAP Host.
Name or IP of the LDAP Server */
public void setLDAPHost (String LDAPHost)
{
if (LDAPHost != null && LDAPHost.length() > 60)
{
log.warning("Length > 60 - truncated");
LDAPHost = LDAPHost.substring(0,59);
}
set_Value ("LDAPHost", LDAPHost);
}
/** Get LDAP Host.
Name or IP of the LDAP Server */
public String getLDAPHost() 
{
return (String)get_Value("LDAPHost");
}
/** Set LDAP Port.
Port for the LDAP (directory) service */
public void setLDAPPort (int LDAPPort)
{
set_Value ("LDAPPort", new Integer(LDAPPort));
}
/** Get LDAP Port.
Port for the LDAP (directory) service */
public int getLDAPPort() 
{
Integer ii = (Integer)get_Value("LDAPPort");
if (ii == null) return 0;
return ii.intValue();
}
/** Set LDAP Query.
Directory service query string */
public void setLDAPQuery (String LDAPQuery)
{
if (LDAPQuery != null && LDAPQuery.length() > 255)
{
log.warning("Length > 255 - truncated");
LDAPQuery = LDAPQuery.substring(0,254);
}
set_Value ("LDAPQuery", LDAPQuery);
}
/** Get LDAP Query.
Directory service query string */
public String getLDAPQuery() 
{
return (String)get_Value("LDAPQuery");
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
/** Set Processors.
Number of Database Processors */
public void setNoProcessors (int NoProcessors)
{
set_Value ("NoProcessors", new Integer(NoProcessors));
}
/** Get Processors.
Number of Database Processors */
public int getNoProcessors() 
{
Integer ii = (Integer)get_Value("NoProcessors");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Password.
Password of any length (case sensitive) */
public void setPassword (String Password)
{
if (Password == null) throw new IllegalArgumentException ("Password is mandatory");
if (Password.length() > 20)
{
log.warning("Length > 20 - truncated");
Password = Password.substring(0,19);
}
set_Value ("Password", Password);
}
/** Get Password.
Password of any length (case sensitive) */
public String getPassword() 
{
return (String)get_Value("Password");
}
/** Set Release No.
Internal Release Number */
public void setReleaseNo (String ReleaseNo)
{
if (ReleaseNo != null && ReleaseNo.length() > 4)
{
log.warning("Length > 4 - truncated");
ReleaseNo = ReleaseNo.substring(0,3);
}
set_Value ("ReleaseNo", ReleaseNo);
}
/** Get Release No.
Internal Release Number */
public String getReleaseNo() 
{
return (String)get_Value("ReleaseNo");
}
public static final int REPLICATIONTYPE_AD_Reference_ID=126;
/** Local = L */
public static final String REPLICATIONTYPE_Local = "L";
/** Merge = M */
public static final String REPLICATIONTYPE_Merge = "M";
/** Reference = R */
public static final String REPLICATIONTYPE_Reference = "R";
/** Set Replication Type.
Type of Data Replication */
public void setReplicationType (String ReplicationType)
{
if (ReplicationType.equals("L") || ReplicationType.equals("M") || ReplicationType.equals("R"));
 else throw new IllegalArgumentException ("ReplicationType Invalid value - Reference_ID=126 - L - M - R");
if (ReplicationType == null) throw new IllegalArgumentException ("ReplicationType is mandatory");
if (ReplicationType.length() > 1)
{
log.warning("Length > 1 - truncated");
ReplicationType = ReplicationType.substring(0,0);
}
set_Value ("ReplicationType", ReplicationType);
}
/** Get Replication Type.
Type of Data Replication */
public String getReplicationType() 
{
return (String)get_Value("ReplicationType");
}
/** Set Summary.
Textual summary of this request */
public void setSummary (String Summary)
{
if (Summary != null && Summary.length() > 255)
{
log.warning("Length > 255 - truncated");
Summary = Summary.substring(0,254);
}
set_Value ("Summary", Summary);
}
/** Get Summary.
Textual summary of this request */
public String getSummary() 
{
return (String)get_Value("Summary");
}
/** Set Internal Users.
Number of Internal Users for openXpertya Support */
public void setSupportUnits (int SupportUnits)
{
set_ValueNoCheck ("SupportUnits", new Integer(SupportUnits));
}
/** Get Internal Users.
Number of Internal Users for openXpertya Support */
public int getSupportUnits() 
{
Integer ii = (Integer)get_Value("SupportUnits");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Registered EMail.
Email of the responsible for the System */
public void setUserName (String UserName)
{
if (UserName == null) throw new IllegalArgumentException ("UserName is mandatory");
if (UserName.length() > 60)
{
log.warning("Length > 60 - truncated");
UserName = UserName.substring(0,59);
}
set_Value ("UserName", UserName);
}
/** Get Registered EMail.
Email of the responsible for the System */
public String getUserName() 
{
return (String)get_Value("UserName");
}
/** Set Version.
Version of the table definition */
public void setVersion (String Version)
{
if (Version == null) throw new IllegalArgumentException ("Version is mandatory");
if (Version.length() > 20)
{
log.warning("Length > 20 - truncated");
Version = Version.substring(0,19);
}
set_Value ("Version", Version);
}
/** Get Version.
Version of the table definition */
public String getVersion() 
{
return (String)get_Value("Version");
}
}
