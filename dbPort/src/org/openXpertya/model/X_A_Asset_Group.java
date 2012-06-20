/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por A_Asset_Group
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.75 */
public class X_A_Asset_Group extends PO
{
/** Constructor estándar */
public X_A_Asset_Group (Properties ctx, int A_Asset_Group_ID, String trxName)
{
super (ctx, A_Asset_Group_ID, trxName);
/** if (A_Asset_Group_ID == 0)
{
setA_Asset_Group_ID (0);
setIsCreateAsActive (true);	// Y
setIsDepreciated (false);
setIsOneAssetPerUOM (false);
setIsOwned (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_A_Asset_Group (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=542 */
public static final int Table_ID=542;

/** TableName=A_Asset_Group */
public static final String Table_Name="A_Asset_Group";

protected static KeyNamePair Model = new KeyNamePair(542,"A_Asset_Group");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_A_Asset_Group[").append(getID()).append("]");
return sb.toString();
}
/** Set Asset Group.
Group of Assets */
public void setA_Asset_Group_ID (int A_Asset_Group_ID)
{
set_ValueNoCheck ("A_Asset_Group_ID", new Integer(A_Asset_Group_ID));
}
/** Get Asset Group.
Group of Assets */
public int getA_Asset_Group_ID() 
{
Integer ii = (Integer)get_Value("A_Asset_Group_ID");
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
/** Set Create As Active.
Create Asset and activate it */
public void setIsCreateAsActive (boolean IsCreateAsActive)
{
set_Value ("IsCreateAsActive", new Boolean(IsCreateAsActive));
}
/** Get Create As Active.
Create Asset and activate it */
public boolean isCreateAsActive() 
{
Object oo = get_Value("IsCreateAsActive");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Depreciate.
The asset will be depreciated */
public void setIsDepreciated (boolean IsDepreciated)
{
set_Value ("IsDepreciated", new Boolean(IsDepreciated));
}
/** Get Depreciate.
The asset will be depreciated */
public boolean isDepreciated() 
{
Object oo = get_Value("IsDepreciated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set One Asset Per UOM.
Create one asset per UOM */
public void setIsOneAssetPerUOM (boolean IsOneAssetPerUOM)
{
set_Value ("IsOneAssetPerUOM", new Boolean(IsOneAssetPerUOM));
}
/** Get One Asset Per UOM.
Create one asset per UOM */
public boolean isOneAssetPerUOM() 
{
Object oo = get_Value("IsOneAssetPerUOM");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Owned.
The asset is owned by the organization */
public void setIsOwned (boolean IsOwned)
{
set_Value ("IsOwned", new Boolean(IsOwned));
}
/** Get Owned.
The asset is owned by the organization */
public boolean isOwned() 
{
Object oo = get_Value("IsOwned");
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
}
