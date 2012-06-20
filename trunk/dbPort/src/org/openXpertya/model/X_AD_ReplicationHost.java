/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ReplicationHost
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-11-08 12:14:32.258 */
public class X_AD_ReplicationHost extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ReplicationHost (Properties ctx, int AD_ReplicationHost_ID, String trxName)
{
super (ctx, AD_ReplicationHost_ID, trxName);
/** if (AD_ReplicationHost_ID == 0)
{
setAD_ReplicationHost_ID (0);
setHostAccessKey (null);
setHostName (null);
setHostPort (0);
setReplicationArrayPos (0);
}
 */
}
/** Load Constructor */
public X_AD_ReplicationHost (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ReplicationHost");

/** TableName=AD_ReplicationHost */
public static final String Table_Name="AD_ReplicationHost";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ReplicationHost");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ReplicationHost[").append(getID()).append("]");
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
/** Set Component Version Identifier */
public void setAD_ComponentVersion_ID (int AD_ComponentVersion_ID)
{
if (AD_ComponentVersion_ID <= 0) set_Value ("AD_ComponentVersion_ID", null);
 else 
set_Value ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_ReplicationHost_ID */
public void setAD_ReplicationHost_ID (int AD_ReplicationHost_ID)
{
set_ValueNoCheck ("AD_ReplicationHost_ID", new Integer(AD_ReplicationHost_ID));
}
/** Get AD_ReplicationHost_ID */
public int getAD_ReplicationHost_ID() 
{
Integer ii = (Integer)get_Value("AD_ReplicationHost_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set HostAccessKey */
public void setHostAccessKey (String HostAccessKey)
{
if (HostAccessKey == null) throw new IllegalArgumentException ("HostAccessKey is mandatory");
if (HostAccessKey.length() > 50)
{
log.warning("Length > 50 - truncated");
HostAccessKey = HostAccessKey.substring(0,50);
}
set_Value ("HostAccessKey", HostAccessKey);
}
/** Get HostAccessKey */
public String getHostAccessKey() 
{
return (String)get_Value("HostAccessKey");
}
/** Set HostName */
public void setHostName (String HostName)
{
if (HostName == null) throw new IllegalArgumentException ("HostName is mandatory");
if (HostName.length() > 255)
{
log.warning("Length > 255 - truncated");
HostName = HostName.substring(0,255);
}
set_Value ("HostName", HostName);
}
/** Get HostName */
public String getHostName() 
{
return (String)get_Value("HostName");
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
public static final int LASTCHANGELOG_ID_AD_Reference_ID = MReference.getReferenceID("AD_Changelog_Replication");
/** Set LastChangelog_ID */
public void setLastChangelog_ID (int LastChangelog_ID)
{
if (LastChangelog_ID <= 0) set_Value ("LastChangelog_ID", null);
 else 
set_Value ("LastChangelog_ID", new Integer(LastChangelog_ID));
}
/** Get LastChangelog_ID */
public int getLastChangelog_ID() 
{
Integer ii = (Integer)get_Value("LastChangelog_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set ReplicationArrayPos */
public void setReplicationArrayPos (int ReplicationArrayPos)
{
set_Value ("ReplicationArrayPos", new Integer(ReplicationArrayPos));
}
/** Get ReplicationArrayPos */
public int getReplicationArrayPos() 
{
Integer ii = (Integer)get_Value("ReplicationArrayPos");
if (ii == null) return 0;
return ii.intValue();
}
/** Set ThisHost */
public void setThisHost (boolean ThisHost)
{
set_Value ("ThisHost", new Boolean(ThisHost));
}
/** Get ThisHost */
public boolean isThisHost() 
{
Object oo = get_Value("ThisHost");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
