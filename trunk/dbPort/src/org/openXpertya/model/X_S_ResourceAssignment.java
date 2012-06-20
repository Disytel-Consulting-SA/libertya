/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por S_ResourceAssignment
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.5 */
public class X_S_ResourceAssignment extends PO
{
/** Constructor estándar */
public X_S_ResourceAssignment (Properties ctx, int S_ResourceAssignment_ID, String trxName)
{
super (ctx, S_ResourceAssignment_ID, trxName);
/** if (S_ResourceAssignment_ID == 0)
{
setAssignDateFrom (new Timestamp(System.currentTimeMillis()));
setIsConfirmed (false);
setName (null);
setS_ResourceAssignment_ID (0);
setS_Resource_ID (0);
}
 */
}
/** Load Constructor */
public X_S_ResourceAssignment (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=485 */
public static final int Table_ID=485;

/** TableName=S_ResourceAssignment */
public static final String Table_Name="S_ResourceAssignment";

protected static KeyNamePair Model = new KeyNamePair(485,"S_ResourceAssignment");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_S_ResourceAssignment[").append(getID()).append("]");
return sb.toString();
}
/** Set Assign From.
Assign resource from */
public void setAssignDateFrom (Timestamp AssignDateFrom)
{
if (AssignDateFrom == null) throw new IllegalArgumentException ("AssignDateFrom is mandatory");
set_ValueNoCheck ("AssignDateFrom", AssignDateFrom);
}
/** Get Assign From.
Assign resource from */
public Timestamp getAssignDateFrom() 
{
return (Timestamp)get_Value("AssignDateFrom");
}
/** Set Assign To.
Assign resource until */
public void setAssignDateTo (Timestamp AssignDateTo)
{
set_ValueNoCheck ("AssignDateTo", AssignDateTo);
}
/** Get Assign To.
Assign resource until */
public Timestamp getAssignDateTo() 
{
return (Timestamp)get_Value("AssignDateTo");
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
/** Set Confirmed.
Assignment is confirmed */
public void setIsConfirmed (boolean IsConfirmed)
{
set_ValueNoCheck ("IsConfirmed", new Boolean(IsConfirmed));
}
/** Get Confirmed.
Assignment is confirmed */
public boolean isConfirmed() 
{
Object oo = get_Value("IsConfirmed");
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
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
set_ValueNoCheck ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Resource Assignment.
Resource Assignment */
public void setS_ResourceAssignment_ID (int S_ResourceAssignment_ID)
{
set_ValueNoCheck ("S_ResourceAssignment_ID", new Integer(S_ResourceAssignment_ID));
}
/** Get Resource Assignment.
Resource Assignment */
public int getS_ResourceAssignment_ID() 
{
Integer ii = (Integer)get_Value("S_ResourceAssignment_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Resource.
Resource */
public void setS_Resource_ID (int S_Resource_ID)
{
set_ValueNoCheck ("S_Resource_ID", new Integer(S_Resource_ID));
}
/** Get Resource.
Resource */
public int getS_Resource_ID() 
{
Integer ii = (Integer)get_Value("S_Resource_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getS_Resource_ID()));
}
}
