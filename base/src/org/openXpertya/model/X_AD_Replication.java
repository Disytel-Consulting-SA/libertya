/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Replication
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:24.453 */
public class X_AD_Replication extends PO
{
/** Constructor estándar */
public X_AD_Replication (Properties ctx, int AD_Replication_ID, String trxName)
{
super (ctx, AD_Replication_ID, trxName);
/** if (AD_Replication_ID == 0)
{
setAD_ReplicationStrategy_ID (0);
setAD_Replication_ID (0);
setHostAddress (null);
setHostPort (0);	// 80
setIsRMIoverHTTP (true);	// Y
setName (null);
setRemote_Client_ID (0);
setRemote_Org_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_Replication (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=605 */
public static final int Table_ID=605;

/** TableName=AD_Replication */
public static final String Table_Name="AD_Replication";

protected static KeyNamePair Model = new KeyNamePair(605,"AD_Replication");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Replication[").append(getID()).append("]");
return sb.toString();
}
/** Set Replication Strategy.
Data Replication Strategy */
public void setAD_ReplicationStrategy_ID (int AD_ReplicationStrategy_ID)
{
set_Value ("AD_ReplicationStrategy_ID", new Integer(AD_ReplicationStrategy_ID));
}
/** Get Replication Strategy.
Data Replication Strategy */
public int getAD_ReplicationStrategy_ID() 
{
Integer ii = (Integer)get_Value("AD_ReplicationStrategy_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Replication.
Data Replication Target */
public void setAD_Replication_ID (int AD_Replication_ID)
{
set_ValueNoCheck ("AD_Replication_ID", new Integer(AD_Replication_ID));
}
/** Get Replication.
Data Replication Target */
public int getAD_Replication_ID() 
{
Integer ii = (Integer)get_Value("AD_Replication_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Date last run.
Date the process was last run. */
public void setDateLastRun (Timestamp DateLastRun)
{
set_ValueNoCheck ("DateLastRun", DateLastRun);
}
/** Get Date last run.
Date the process was last run. */
public Timestamp getDateLastRun() 
{
return (Timestamp)get_Value("DateLastRun");
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Host Address.
Host Address URL or DNS */
public void setHostAddress (String HostAddress)
{
if (HostAddress == null) throw new IllegalArgumentException ("HostAddress is mandatory");
if (HostAddress.length() > 60)
{
log.warning("Length > 60 - truncated");
HostAddress = HostAddress.substring(0,59);
}
set_Value ("HostAddress", HostAddress);
}
/** Get Host Address.
Host Address URL or DNS */
public String getHostAddress() 
{
return (String)get_Value("HostAddress");
}
/** Set Host port.
Host Communication Port */
public void setHostPort (int HostPort)
{
set_Value ("HostPort", new Integer(HostPort));
}
/** Get Host port.
Host Communication Port */
public int getHostPort() 
{
Integer ii = (Integer)get_Value("HostPort");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Tunnel via HTTP.
Connect to Server via HTTP Tunnel */
public void setIsRMIoverHTTP (boolean IsRMIoverHTTP)
{
set_Value ("IsRMIoverHTTP", new Boolean(IsRMIoverHTTP));
}
/** Get Tunnel via HTTP.
Connect to Server via HTTP Tunnel */
public boolean isRMIoverHTTP() 
{
Object oo = get_Value("IsRMIoverHTTP");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Prefix.
Prefix before the sequence number */
public void setPrefix (String Prefix)
{
if (Prefix != null && Prefix.length() > 10)
{
log.warning("Length > 10 - truncated");
Prefix = Prefix.substring(0,9);
}
set_Value ("Prefix", Prefix);
}
/** Get Prefix.
Prefix before the sequence number */
public String getPrefix() 
{
return (String)get_Value("Prefix");
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
public static final int REMOTE_CLIENT_ID_AD_Reference_ID=129;
/** Set Remote Client.
Remote Client to be used to replicate / synchronize data with. */
public void setRemote_Client_ID (int Remote_Client_ID)
{
set_ValueNoCheck ("Remote_Client_ID", new Integer(Remote_Client_ID));
}
/** Get Remote Client.
Remote Client to be used to replicate / synchronize data with. */
public int getRemote_Client_ID() 
{
Integer ii = (Integer)get_Value("Remote_Client_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int REMOTE_ORG_ID_AD_Reference_ID=276;
/** Set Remote Organization.
Remote Organization to be used to replicate / synchronize data with. */
public void setRemote_Org_ID (int Remote_Org_ID)
{
set_ValueNoCheck ("Remote_Org_ID", new Integer(Remote_Org_ID));
}
/** Get Remote Organization.
Remote Organization to be used to replicate / synchronize data with. */
public int getRemote_Org_ID() 
{
Integer ii = (Integer)get_Value("Remote_Org_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Suffix.
Suffix after the number */
public void setSuffix (String Suffix)
{
if (Suffix != null && Suffix.length() > 10)
{
log.warning("Length > 10 - truncated");
Suffix = Suffix.substring(0,9);
}
set_Value ("Suffix", Suffix);
}
/** Get Suffix.
Suffix after the number */
public String getSuffix() 
{
return (String)get_Value("Suffix");
}
}
