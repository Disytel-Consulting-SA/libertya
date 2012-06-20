/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Cost_Group
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.734 */
public class X_MPC_Cost_Group extends PO
{
/** Constructor estándar */
public X_MPC_Cost_Group (Properties ctx, int MPC_Cost_Group_ID, String trxName)
{
super (ctx, MPC_Cost_Group_ID, trxName);
/** if (MPC_Cost_Group_ID == 0)
{
setIsGL (false);
setMPC_Cost_Group_ID (0);
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_MPC_Cost_Group (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000013 */
public static final int Table_ID=1000013;

/** TableName=MPC_Cost_Group */
public static final String Table_Name="MPC_Cost_Group";

protected static KeyNamePair Model = new KeyNamePair(1000013,"MPC_Cost_Group");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Cost_Group[").append(getID()).append("]");
return sb.toString();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 1020)
{
log.warning("Length > 1020 - truncated");
Description = Description.substring(0,1019);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set IsGL */
public void setIsGL (boolean IsGL)
{
set_Value ("IsGL", new Boolean(IsGL));
}
/** Get IsGL */
public boolean isGL() 
{
Object oo = get_Value("IsGL");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Cost Group CMPCS */
public void setMPC_Cost_Group_ID (int MPC_Cost_Group_ID)
{
set_ValueNoCheck ("MPC_Cost_Group_ID", new Integer(MPC_Cost_Group_ID));
}
/** Get Cost Group CMPCS */
public int getMPC_Cost_Group_ID() 
{
Integer ii = (Integer)get_Value("MPC_Cost_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 120)
{
log.warning("Length > 120 - truncated");
Name = Name.substring(0,119);
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
if (Value.length() > 80)
{
log.warning("Length > 80 - truncated");
Value = Value.substring(0,79);
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
