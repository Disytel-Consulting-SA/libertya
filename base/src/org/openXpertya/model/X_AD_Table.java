/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Table
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-07-20 16:00:05.309 */
public class X_AD_Table extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Table (Properties ctx, int AD_Table_ID, String trxName)
{
super (ctx, AD_Table_ID, trxName);
/** if (AD_Table_ID == 0)
{
setAccessLevel (null);	// 4
setAD_Table_ID (0);
setEntityType (null);	// U
setIsChangeLog (false);
setIsDeleteable (false);
setIsHighVolume (false);
setIsSecurityEnabled (false);
setIsView (false);	// N
setName (null);
setReplicationType (null);	// L
setTableName (null);
}
 */
}
/** Load Constructor */
public X_AD_Table (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Table");

/** TableName=AD_Table */
public static final String Table_Name="AD_Table";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Table");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Table[").append(getID()).append("]");
return sb.toString();
}
public static final int ACCESSLEVEL_AD_Reference_ID = MReference.getReferenceID("AD_Table Access Levels");
/** Organization = 1 */
public static final String ACCESSLEVEL_Organization = "1";
/** Client+Organization = 3 */
public static final String ACCESSLEVEL_ClientPlusOrganization = "3";
/** System only = 4 */
public static final String ACCESSLEVEL_SystemOnly = "4";
/** All = 7 */
public static final String ACCESSLEVEL_All = "7";
/** Client only = 2 */
public static final String ACCESSLEVEL_ClientOnly = "2";
/** System+Client = 6 */
public static final String ACCESSLEVEL_SystemPlusClient = "6";
/** Set Data Access Level.
Access Level required */
public void setAccessLevel (String AccessLevel)
{
if (AccessLevel.equals("1") || AccessLevel.equals("3") || AccessLevel.equals("4") || AccessLevel.equals("7") || AccessLevel.equals("2") || AccessLevel.equals("6"));
 else throw new IllegalArgumentException ("AccessLevel Invalid value - Reference = ACCESSLEVEL_AD_Reference_ID - 1 - 3 - 4 - 7 - 2 - 6");
if (AccessLevel == null) throw new IllegalArgumentException ("AccessLevel is mandatory");
if (AccessLevel.length() > 1)
{
log.warning("Length > 1 - truncated");
AccessLevel = AccessLevel.substring(0,1);
}
set_Value ("AccessLevel", AccessLevel);
}
/** Get Data Access Level.
Access Level required */
public String getAccessLevel() 
{
return (String)get_Value("AccessLevel");
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
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
set_ValueNoCheck ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Dynamic Validation.
Dynamic Validation Rule */
public void setAD_Val_Rule_ID (int AD_Val_Rule_ID)
{
if (AD_Val_Rule_ID <= 0) set_Value ("AD_Val_Rule_ID", null);
 else 
set_Value ("AD_Val_Rule_ID", new Integer(AD_Val_Rule_ID));
}
/** Get Dynamic Validation.
Dynamic Validation Rule */
public int getAD_Val_Rule_ID() 
{
Integer ii = (Integer)get_Value("AD_Val_Rule_ID");
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
/** Set CreateReplicationTrigger */
public void setCreateReplicationTrigger (boolean CreateReplicationTrigger)
{
set_Value ("CreateReplicationTrigger", new Boolean(CreateReplicationTrigger));
}
/** Get CreateReplicationTrigger */
public boolean isCreateReplicationTrigger() 
{
Object oo = get_Value("CreateReplicationTrigger");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
public static final int ENTITYTYPE_AD_Reference_ID = MReference.getReferenceID("_Entity Type");
/** Applications Integrated with openXpertya = A */
public static final String ENTITYTYPE_ApplicationsIntegratedWithOpenXpertya = "A";
/** Country Version = C */
public static final String ENTITYTYPE_CountryVersion = "C";
/** Dictionary = D */
public static final String ENTITYTYPE_Dictionary = "D";
/** User maintained = U */
public static final String ENTITYTYPE_UserMaintained = "U";
/** Customization = CUST */
public static final String ENTITYTYPE_Customization = "CUST";
/** Set Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public void setEntityType (String EntityType)
{
if (EntityType.equals("A") || EntityType.equals("C") || EntityType.equals("D") || EntityType.equals("U") || EntityType.equals("CUST"));
 else throw new IllegalArgumentException ("EntityType Invalid value - Reference = ENTITYTYPE_AD_Reference_ID - A - C - D - U - CUST");
if (EntityType == null) throw new IllegalArgumentException ("EntityType is mandatory");
if (EntityType.length() > 4)
{
log.warning("Length > 4 - truncated");
EntityType = EntityType.substring(0,4);
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
/** Set GenerateDirectMethods */
public void setGenerateDirectMethods (boolean GenerateDirectMethods)
{
set_Value ("GenerateDirectMethods", new Boolean(GenerateDirectMethods));
}
/** Get GenerateDirectMethods */
public boolean isGenerateDirectMethods() 
{
Object oo = get_Value("GenerateDirectMethods");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Import Table.
Import Table Columns from Database */
public void setImportTable (String ImportTable)
{
if (ImportTable != null && ImportTable.length() > 1)
{
log.warning("Length > 1 - truncated");
ImportTable = ImportTable.substring(0,1);
}
set_Value ("ImportTable", ImportTable);
}
/** Get Import Table.
Import Table Columns from Database */
public String getImportTable() 
{
return (String)get_Value("ImportTable");
}
/** Set Maintain Change Log.
Maintain a log of changes */
public void setIsChangeLog (boolean IsChangeLog)
{
set_Value ("IsChangeLog", new Boolean(IsChangeLog));
}
/** Get Maintain Change Log.
Maintain a log of changes */
public boolean isChangeLog() 
{
Object oo = get_Value("IsChangeLog");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Records deleteable.
Indicates if records can be deleted from the database */
public void setIsDeleteable (boolean IsDeleteable)
{
set_Value ("IsDeleteable", new Boolean(IsDeleteable));
}
/** Get Records deleteable.
Indicates if records can be deleted from the database */
public boolean isDeleteable() 
{
Object oo = get_Value("IsDeleteable");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set High Volume.
Use Search instead of Pick list */
public void setIsHighVolume (boolean IsHighVolume)
{
set_Value ("IsHighVolume", new Boolean(IsHighVolume));
}
/** Get High Volume.
Use Search instead of Pick list */
public boolean isHighVolume() 
{
Object oo = get_Value("IsHighVolume");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Security enabled.
If security is enabled, user access to data can be restricted via Roles */
public void setIsSecurityEnabled (boolean IsSecurityEnabled)
{
set_Value ("IsSecurityEnabled", new Boolean(IsSecurityEnabled));
}
/** Get Security enabled.
If security is enabled, user access to data can be restricted via Roles */
public boolean isSecurityEnabled() 
{
Object oo = get_Value("IsSecurityEnabled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set View.
This is a view */
public void setIsView (boolean IsView)
{
set_Value ("IsView", new Boolean(IsView));
}
/** Get View.
This is a view */
public boolean isView() 
{
Object oo = get_Value("IsView");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sequence */
public void setLoadSeq (int LoadSeq)
{
set_ValueNoCheck ("LoadSeq", new Integer(LoadSeq));
}
/** Get Sequence */
public int getLoadSeq() 
{
Integer ii = (Integer)get_Value("LoadSeq");
if (ii == null) return 0;
return ii.intValue();
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
public static final int PO_WINDOW_ID_AD_Reference_ID = MReference.getReferenceID("AD_Window");
/** Set PO Window.
Purchase Order Window */
public void setPO_Window_ID (int PO_Window_ID)
{
if (PO_Window_ID <= 0) set_Value ("PO_Window_ID", null);
 else 
set_Value ("PO_Window_ID", new Integer(PO_Window_ID));
}
/** Get PO Window.
Purchase Order Window */
public int getPO_Window_ID() 
{
Integer ii = (Integer)get_Value("PO_Window_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int REPLICATIONTYPE_AD_Reference_ID = MReference.getReferenceID("AD_Table Replication Type");
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
 else throw new IllegalArgumentException ("ReplicationType Invalid value - Reference = REPLICATIONTYPE_AD_Reference_ID - L - M - R");
if (ReplicationType == null) throw new IllegalArgumentException ("ReplicationType is mandatory");
if (ReplicationType.length() > 1)
{
log.warning("Length > 1 - truncated");
ReplicationType = ReplicationType.substring(0,1);
}
set_Value ("ReplicationType", ReplicationType);
}
/** Get Replication Type.
Type of Data Replication */
public String getReplicationType() 
{
return (String)get_Value("ReplicationType");
}
/** Set DB Table Name.
Name of the table in the database */
public void setTableName (String TableName)
{
if (TableName == null) throw new IllegalArgumentException ("TableName is mandatory");
if (TableName.length() > 40)
{
log.warning("Length > 40 - truncated");
TableName = TableName.substring(0,40);
}
set_Value ("TableName", TableName);
}
/** Get DB Table Name.
Name of the table in the database */
public String getTableName() 
{
return (String)get_Value("TableName");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getTableName());
}
}
