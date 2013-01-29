/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Campaign
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:28.812 */
public class X_C_Campaign extends PO
{
/** Constructor estándar */
public X_C_Campaign (Properties ctx, int C_Campaign_ID, String trxName)
{
super (ctx, C_Campaign_ID, trxName);
/** if (C_Campaign_ID == 0)
{
setC_Campaign_ID (0);
setCosts (Env.ZERO);
setIsSummary (false);
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_Campaign (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=274 */
public static final int Table_ID=274;

/** TableName=C_Campaign */
public static final String Table_Name="C_Campaign";

protected static KeyNamePair Model = new KeyNamePair(274,"C_Campaign");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Campaign[").append(getID()).append("]");
return sb.toString();
}
/** Set Campaign.
Marketing Campaign */
public void setC_Campaign_ID (int C_Campaign_ID)
{
set_ValueNoCheck ("C_Campaign_ID", new Integer(C_Campaign_ID));
}
/** Get Campaign.
Marketing Campaign */
public int getC_Campaign_ID() 
{
Integer ii = (Integer)get_Value("C_Campaign_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Channel.
Sales Channel */
public void setC_Channel_ID (int C_Channel_ID)
{
if (C_Channel_ID <= 0) set_Value ("C_Channel_ID", null);
 else 
set_Value ("C_Channel_ID", new Integer(C_Channel_ID));
}
/** Get Channel.
Sales Channel */
public int getC_Channel_ID() 
{
Integer ii = (Integer)get_Value("C_Channel_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Costs.
Costs in accounting currency */
public void setCosts (BigDecimal Costs)
{
if (Costs == null) throw new IllegalArgumentException ("Costs is mandatory");
set_Value ("Costs", Costs);
}
/** Get Costs.
Costs in accounting currency */
public BigDecimal getCosts() 
{
BigDecimal bd = (BigDecimal)get_Value("Costs");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set End Date.
Last effective date (inclusive) */
public void setEndDate (Timestamp EndDate)
{
set_Value ("EndDate", EndDate);
}
/** Get End Date.
Last effective date (inclusive) */
public Timestamp getEndDate() 
{
return (Timestamp)get_Value("EndDate");
}
/** Set Summary Level.
This is a summary entity */
public void setIsSummary (boolean IsSummary)
{
set_Value ("IsSummary", new Boolean(IsSummary));
}
/** Get Summary Level.
This is a summary entity */
public boolean isSummary() 
{
Object oo = get_Value("IsSummary");
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
/** Set Start Date.
First effective day (inclusive) */
public void setStartDate (Timestamp StartDate)
{
set_Value ("StartDate", StartDate);
}
/** Get Start Date.
First effective day (inclusive) */
public Timestamp getStartDate() 
{
return (Timestamp)get_Value("StartDate");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,39);
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
