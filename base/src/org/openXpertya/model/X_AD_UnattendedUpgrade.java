/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_UnattendedUpgrade
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-12-04 12:46:14.5 */
public class X_AD_UnattendedUpgrade extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_UnattendedUpgrade (Properties ctx, int AD_UnattendedUpgrade_ID, String trxName)
{
super (ctx, AD_UnattendedUpgrade_ID, trxName);
/** if (AD_UnattendedUpgrade_ID == 0)
{
setAD_UnattendedUpgrade_ID (0);
setDirectory (null);
setScheduledFor (new Timestamp(System.currentTimeMillis()));
setVersion (null);
}
 */
}
/** Load Constructor */
public X_AD_UnattendedUpgrade (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_UnattendedUpgrade");

/** TableName=AD_UnattendedUpgrade */
public static final String Table_Name="AD_UnattendedUpgrade";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_UnattendedUpgrade");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_UnattendedUpgrade[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_UnattendedUpgrade_ID */
public void setAD_UnattendedUpgrade_ID (int AD_UnattendedUpgrade_ID)
{
set_ValueNoCheck ("AD_UnattendedUpgrade_ID", new Integer(AD_UnattendedUpgrade_ID));
}
/** Get AD_UnattendedUpgrade_ID */
public int getAD_UnattendedUpgrade_ID() 
{
Integer ii = (Integer)get_Value("AD_UnattendedUpgrade_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Directory */
public void setDirectory (String Directory)
{
if (Directory == null) throw new IllegalArgumentException ("Directory is mandatory");
if (Directory.length() > 255)
{
log.warning("Length > 255 - truncated");
Directory = Directory.substring(0,255);
}
set_Value ("Directory", Directory);
}
/** Get Directory */
public String getDirectory() 
{
return (String)get_Value("Directory");
}
/** Set ScheduledFor */
public void setScheduledFor (Timestamp ScheduledFor)
{
if (ScheduledFor == null) throw new IllegalArgumentException ("ScheduledFor is mandatory");
set_Value ("ScheduledFor", ScheduledFor);
}
/** Get ScheduledFor */
public Timestamp getScheduledFor() 
{
return (Timestamp)get_Value("ScheduledFor");
}
/** Set ScheduleUpgradeAllHosts */
public void setScheduleUpgradeAllHosts (String ScheduleUpgradeAllHosts)
{
if (ScheduleUpgradeAllHosts != null && ScheduleUpgradeAllHosts.length() > 1)
{
log.warning("Length > 1 - truncated");
ScheduleUpgradeAllHosts = ScheduleUpgradeAllHosts.substring(0,1);
}
set_Value ("ScheduleUpgradeAllHosts", ScheduleUpgradeAllHosts);
}
/** Get ScheduleUpgradeAllHosts */
public String getScheduleUpgradeAllHosts() 
{
return (String)get_Value("ScheduleUpgradeAllHosts");
}
/** Set Version.
Version of the table definition */
public void setVersion (String Version)
{
if (Version == null) throw new IllegalArgumentException ("Version is mandatory");
if (Version.length() > 100)
{
log.warning("Length > 100 - truncated");
Version = Version.substring(0,100);
}
set_Value ("Version", Version);
}
/** Get Version.
Version of the table definition */
public String getVersion() 
{
return (String)get_Value("Version");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getVersion());
}
}
