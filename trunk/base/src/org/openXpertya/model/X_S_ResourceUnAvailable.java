/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por S_ResourceUnAvailable
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:41.546 */
public class X_S_ResourceUnAvailable extends PO
{
/** Constructor estándar */
public X_S_ResourceUnAvailable (Properties ctx, int S_ResourceUnAvailable_ID, String trxName)
{
super (ctx, S_ResourceUnAvailable_ID, trxName);
/** if (S_ResourceUnAvailable_ID == 0)
{
setDateFrom (new Timestamp(System.currentTimeMillis()));
setS_ResourceUnAvailable_ID (0);
setS_Resource_ID (0);
}
 */
}
/** Load Constructor */
public X_S_ResourceUnAvailable (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=482 */
public static final int Table_ID=482;

/** TableName=S_ResourceUnAvailable */
public static final String Table_Name="S_ResourceUnAvailable";

protected static KeyNamePair Model = new KeyNamePair(482,"S_ResourceUnAvailable");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_S_ResourceUnAvailable[").append(getID()).append("]");
return sb.toString();
}
/** Set Date From.
Starting date for a range */
public void setDateFrom (Timestamp DateFrom)
{
if (DateFrom == null) throw new IllegalArgumentException ("DateFrom is mandatory");
set_Value ("DateFrom", DateFrom);
}
/** Get Date From.
Starting date for a range */
public Timestamp getDateFrom() 
{
return (Timestamp)get_Value("DateFrom");
}
/** Set Date To.
End date of a date range */
public void setDateTo (Timestamp DateTo)
{
set_Value ("DateTo", DateTo);
}
/** Get Date To.
End date of a date range */
public Timestamp getDateTo() 
{
return (Timestamp)get_Value("DateTo");
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
/** Set Resource Unavailability */
public void setS_ResourceUnAvailable_ID (int S_ResourceUnAvailable_ID)
{
set_ValueNoCheck ("S_ResourceUnAvailable_ID", new Integer(S_ResourceUnAvailable_ID));
}
/** Get Resource Unavailability */
public int getS_ResourceUnAvailable_ID() 
{
Integer ii = (Integer)get_Value("S_ResourceUnAvailable_ID");
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
