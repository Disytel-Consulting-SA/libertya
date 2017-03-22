/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ComponentVersion
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-03-22 16:37:50.591 */
public class X_AD_ComponentVersion extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ComponentVersion (Properties ctx, int AD_ComponentVersion_ID, String trxName)
{
super (ctx, AD_ComponentVersion_ID, trxName);
/** if (AD_ComponentVersion_ID == 0)
{
setAD_Component_ID (0);
setAD_ComponentVersion_ID (0);
setCurrentDevelopment (false);
setVersion (null);
}
 */
}
/** Load Constructor */
public X_AD_ComponentVersion (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ComponentVersion");

/** TableName=AD_ComponentVersion */
public static final String Table_Name="AD_ComponentVersion";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ComponentVersion");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ComponentVersion[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_Component_ID */
public void setAD_Component_ID (int AD_Component_ID)
{
set_Value ("AD_Component_ID", new Integer(AD_Component_ID));
}
/** Get AD_Component_ID */
public int getAD_Component_ID() 
{
Integer ii = (Integer)get_Value("AD_Component_ID");
if (ii == null) return 0;
return ii.intValue();
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
set_ValueNoCheck ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set ChangeLog */
public void setChangeLog (String ChangeLog)
{
if (ChangeLog != null && ChangeLog.length() > 2000)
{
log.warning("Length > 2000 - truncated");
ChangeLog = ChangeLog.substring(0,2000);
}
set_Value ("ChangeLog", ChangeLog);
}
/** Get ChangeLog */
public String getChangeLog() 
{
return (String)get_Value("ChangeLog");
}
/** Set Current Development */
public void setCurrentDevelopment (boolean CurrentDevelopment)
{
set_Value ("CurrentDevelopment", new Boolean(CurrentDevelopment));
}
/** Get Current Development */
public boolean isCurrentDevelopment() 
{
Object oo = get_Value("CurrentDevelopment");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Dependencies */
public void setDependencies (String Dependencies)
{
if (Dependencies != null && Dependencies.length() > 1000)
{
log.warning("Length > 1000 - truncated");
Dependencies = Dependencies.substring(0,1000);
}
set_Value ("Dependencies", Dependencies);
}
/** Get Dependencies */
public String getDependencies() 
{
return (String)get_Value("Dependencies");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 110)
{
log.warning("Length > 110 - truncated");
Name = Name.substring(0,110);
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
/** Set SimulateCVInstallation */
public void setSimulateCVInstallation (String SimulateCVInstallation)
{
if (SimulateCVInstallation != null && SimulateCVInstallation.length() > 1)
{
log.warning("Length > 1 - truncated");
SimulateCVInstallation = SimulateCVInstallation.substring(0,1);
}
set_Value ("SimulateCVInstallation", SimulateCVInstallation);
}
/** Get SimulateCVInstallation */
public String getSimulateCVInstallation() 
{
return (String)get_Value("SimulateCVInstallation");
}
public static final int STARTDEVELOPMENT_AD_Reference_ID = MReference.getReferenceID("AD_ComponentVersion States");
/** End Development = E */
public static final String STARTDEVELOPMENT_EndDevelopment = "E";
/** Start Development = S */
public static final String STARTDEVELOPMENT_StartDevelopment = "S";
/** Set Start Development */
public void setStartDevelopment (String StartDevelopment)
{
if (StartDevelopment == null || StartDevelopment.equals("E") || StartDevelopment.equals("S") || ( refContainsValue("CORE-AD_Reference-1010098", StartDevelopment) ) );
 else throw new IllegalArgumentException ("StartDevelopment Invalid value: " + StartDevelopment + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010098") );
if (StartDevelopment != null && StartDevelopment.length() > 1)
{
log.warning("Length > 1 - truncated");
StartDevelopment = StartDevelopment.substring(0,1);
}
set_Value ("StartDevelopment", StartDevelopment);
}
/** Get Start Development */
public String getStartDevelopment() 
{
return (String)get_Value("StartDevelopment");
}
/** Set Version.
Version of the table definition */
public void setVersion (String Version)
{
if (Version == null) throw new IllegalArgumentException ("Version is mandatory");
if (Version.length() > 10)
{
log.warning("Length > 10 - truncated");
Version = Version.substring(0,10);
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
