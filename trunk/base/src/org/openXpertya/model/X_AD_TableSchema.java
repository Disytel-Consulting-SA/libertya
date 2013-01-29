/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_TableSchema
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-11-02 14:16:54.553 */
public class X_AD_TableSchema extends PO
{
/** Constructor estándar */
public X_AD_TableSchema (Properties ctx, int AD_TableSchema_ID, String trxName)
{
super (ctx, AD_TableSchema_ID, trxName);
/** if (AD_TableSchema_ID == 0)
{
setAD_TableSchema_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_TableSchema (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1010193 */
public static final int Table_ID=1010193;

/** TableName=AD_TableSchema */
public static final String Table_Name="AD_TableSchema";

protected static KeyNamePair Model = new KeyNamePair(1010193,"AD_TableSchema");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_TableSchema[").append(getID()).append("]");
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
/** Set Add Tables.
Add Tables Process */
public void setAddTables (String AddTables)
{
if (AddTables != null && AddTables.length() > 1)
{
log.warning("Length > 1 - truncated");
AddTables = AddTables.substring(0,1);
}
set_Value ("AddTables", AddTables);
}
/** Get Add Tables.
Add Tables Process */
public String getAddTables() 
{
return (String)get_Value("AddTables");
}
/** Set Table Schema.
Table Schema */
public void setAD_TableSchema_ID (int AD_TableSchema_ID)
{
set_ValueNoCheck ("AD_TableSchema_ID", new Integer(AD_TableSchema_ID));
}
/** Get Table Schema.
Table Schema */
public int getAD_TableSchema_ID() 
{
Integer ii = (Integer)get_Value("AD_TableSchema_ID");
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
/** Set Purge Tables.
Purge Table Schema */
public void setPurgeTables (String PurgeTables)
{
if (PurgeTables != null && PurgeTables.length() > 1)
{
log.warning("Length > 1 - truncated");
PurgeTables = PurgeTables.substring(0,1);
}
set_Value ("PurgeTables", PurgeTables);
}
/** Get Purge Tables.
Purge Table Schema */
public String getPurgeTables() 
{
return (String)get_Value("PurgeTables");
}
}
