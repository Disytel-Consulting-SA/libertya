/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_EntityType
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-01-16 17:33:04.438 */
public class X_AD_EntityType extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_EntityType (Properties ctx, int AD_EntityType_ID, String trxName)
{
super (ctx, AD_EntityType_ID, trxName);
/** if (AD_EntityType_ID == 0)
{
setAD_Entitytype_ID (0);
setEntityType (null);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_EntityType (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_EntityType");

/** TableName=AD_EntityType */
public static final String Table_Name="AD_EntityType";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_EntityType");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_EntityType[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_Entitytype_ID */
public void setAD_Entitytype_ID (int AD_Entitytype_ID)
{
set_ValueNoCheck ("AD_Entitytype_ID", new Integer(AD_Entitytype_ID));
}
/** Get AD_Entitytype_ID */
public int getAD_Entitytype_ID() 
{
Integer ii = (Integer)get_Value("AD_Entitytype_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Classpath */
public void setClasspath (String Classpath)
{
if (Classpath != null && Classpath.length() > 255)
{
log.warning("Length > 255 - truncated");
Classpath = Classpath.substring(0,255);
}
set_Value ("Classpath", Classpath);
}
/** Get Classpath */
public String getClasspath() 
{
return (String)get_Value("Classpath");
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,2000);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set ModelPackage */
public void setModelPackage (String ModelPackage)
{
if (ModelPackage != null && ModelPackage.length() > 255)
{
log.warning("Length > 255 - truncated");
ModelPackage = ModelPackage.substring(0,255);
}
set_Value ("ModelPackage", ModelPackage);
}
/** Get ModelPackage */
public String getModelPackage() 
{
return (String)get_Value("ModelPackage");
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
/** Set Version.
Version of the table definition */
public void setVersion (String Version)
{
if (Version != null && Version.length() > 20)
{
log.warning("Length > 20 - truncated");
Version = Version.substring(0,20);
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
