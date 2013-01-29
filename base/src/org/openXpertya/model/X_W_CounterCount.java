/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por W_CounterCount
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:42.468 */
public class X_W_CounterCount extends PO
{
/** Constructor estándar */
public X_W_CounterCount (Properties ctx, int W_CounterCount_ID, String trxName)
{
super (ctx, W_CounterCount_ID, trxName);
/** if (W_CounterCount_ID == 0)
{
setCounter (0);
setName (null);
setPageURL (null);
setW_CounterCount_ID (0);
}
 */
}
/** Load Constructor */
public X_W_CounterCount (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=552 */
public static final int Table_ID=552;

/** TableName=W_CounterCount */
public static final String Table_Name="W_CounterCount";

protected static KeyNamePair Model = new KeyNamePair(552,"W_CounterCount");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_W_CounterCount[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Counter.
Count Value */
public void setCounter (int Counter)
{
set_Value ("Counter", new Integer(Counter));
}
/** Get Counter.
Count Value */
public int getCounter() 
{
Integer ii = (Integer)get_Value("Counter");
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
/** Set Page URL */
public void setPageURL (String PageURL)
{
if (PageURL == null) throw new IllegalArgumentException ("PageURL is mandatory");
if (PageURL.length() > 120)
{
log.warning("Length > 120 - truncated");
PageURL = PageURL.substring(0,119);
}
set_Value ("PageURL", PageURL);
}
/** Get Page URL */
public String getPageURL() 
{
return (String)get_Value("PageURL");
}
/** Set Counter Count.
Web Counter Count Management */
public void setW_CounterCount_ID (int W_CounterCount_ID)
{
set_ValueNoCheck ("W_CounterCount_ID", new Integer(W_CounterCount_ID));
}
/** Get Counter Count.
Web Counter Count Management */
public int getW_CounterCount_ID() 
{
Integer ii = (Integer)get_Value("W_CounterCount_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
