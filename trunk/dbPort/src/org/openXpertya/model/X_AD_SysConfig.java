/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_SysConfig
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-01-16 17:33:07.018 */
public class X_AD_SysConfig extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_SysConfig (Properties ctx, int AD_SysConfig_ID, String trxName)
{
super (ctx, AD_SysConfig_ID, trxName);
/** if (AD_SysConfig_ID == 0)
{
setAD_SysConfig_ID (0);
setEntityType (null);
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_AD_SysConfig (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_SysConfig");

/** TableName=AD_SysConfig */
public static final String Table_Name="AD_SysConfig";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_SysConfig");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_SysConfig[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_SysConfig_ID */
public void setAD_SysConfig_ID (int AD_SysConfig_ID)
{
set_ValueNoCheck ("AD_SysConfig_ID", new Integer(AD_SysConfig_ID));
}
/** Get AD_SysConfig_ID */
public int getAD_SysConfig_ID() 
{
Integer ii = (Integer)get_Value("AD_SysConfig_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CONFIGURATIONLEVEL_AD_Reference_ID = MReference.getReferenceID("ConfigurationLevel");
/** Client = C */
public static final String CONFIGURATIONLEVEL_Client = "C";
/** Organization = O */
public static final String CONFIGURATIONLEVEL_Organization = "O";
/** System = S */
public static final String CONFIGURATIONLEVEL_System = "S";
/** Set ConfigurationLevel */
public void setConfigurationLevel (String ConfigurationLevel)
{
if (ConfigurationLevel == null || ConfigurationLevel.equals("C") || ConfigurationLevel.equals("O") || ConfigurationLevel.equals("S"));
 else throw new IllegalArgumentException ("ConfigurationLevel Invalid value - Reference = CONFIGURATIONLEVEL_AD_Reference_ID - C - O - S");
if (ConfigurationLevel != null && ConfigurationLevel.length() > 1)
{
log.warning("Length > 1 - truncated");
ConfigurationLevel = ConfigurationLevel.substring(0,1);
}
set_Value ("ConfigurationLevel", ConfigurationLevel);
}
/** Get ConfigurationLevel */
public String getConfigurationLevel() 
{
return (String)get_Value("ConfigurationLevel");
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
/** Set Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public void setEntityType (String EntityType)
{
if (EntityType == null) throw new IllegalArgumentException ("EntityType is mandatory");
if (EntityType.length() > 40)
{
log.warning("Length > 40 - truncated");
EntityType = EntityType.substring(0,40);
}
set_Value ("EntityType", EntityType);
}
/** Get Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public String getEntityType() 
{
return (String)get_Value("EntityType");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 50)
{
log.warning("Length > 50 - truncated");
Name = Name.substring(0,50);
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
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 255)
{
log.warning("Length > 255 - truncated");
Value = Value.substring(0,255);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
