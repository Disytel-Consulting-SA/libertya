/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Replication_Run
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:24.593 */
public class X_AD_Replication_Run extends PO
{
/** Constructor estándar */
public X_AD_Replication_Run (Properties ctx, int AD_Replication_Run_ID, String trxName)
{
super (ctx, AD_Replication_Run_ID, trxName);
/** if (AD_Replication_Run_ID == 0)
{
setAD_Replication_ID (0);
setAD_Replication_Run_ID (0);
setIsReplicated (false);	// N
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_Replication_Run (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=603 */
public static final int Table_ID=603;

/** TableName=AD_Replication_Run */
public static final String Table_Name="AD_Replication_Run";

protected static KeyNamePair Model = new KeyNamePair(603,"AD_Replication_Run");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Replication_Run[").append(getID()).append("]");
return sb.toString();
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
/** Set Replication Run.
Data Replication Run */
public void setAD_Replication_Run_ID (int AD_Replication_Run_ID)
{
set_ValueNoCheck ("AD_Replication_Run_ID", new Integer(AD_Replication_Run_ID));
}
/** Get Replication Run.
Data Replication Run */
public int getAD_Replication_Run_ID() 
{
Integer ii = (Integer)get_Value("AD_Replication_Run_ID");
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
/** Set Replicated.
The data is successfully replicated */
public void setIsReplicated (boolean IsReplicated)
{
set_ValueNoCheck ("IsReplicated", new Boolean(IsReplicated));
}
/** Get Replicated.
The data is successfully replicated */
public boolean isReplicated() 
{
Object oo = get_Value("IsReplicated");
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
