/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Cycle
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.5 */
public class X_C_Cycle extends PO
{
/** Constructor estándar */
public X_C_Cycle (Properties ctx, int C_Cycle_ID, String trxName)
{
super (ctx, C_Cycle_ID, trxName);
/** if (C_Cycle_ID == 0)
{
setC_Currency_ID (0);
setC_Cycle_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_Cycle (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=432 */
public static final int Table_ID=432;

/** TableName=C_Cycle */
public static final String Table_Name="C_Cycle";

protected static KeyNamePair Model = new KeyNamePair(432,"C_Cycle");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Cycle[").append(getID()).append("]");
return sb.toString();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project Cycle.
Identifier for this Project Reporting Cycle */
public void setC_Cycle_ID (int C_Cycle_ID)
{
set_ValueNoCheck ("C_Cycle_ID", new Integer(C_Cycle_ID));
}
/** Get Project Cycle.
Identifier for this Project Reporting Cycle */
public int getC_Cycle_ID() 
{
Integer ii = (Integer)get_Value("C_Cycle_ID");
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
