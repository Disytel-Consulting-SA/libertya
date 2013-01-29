/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Search
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:24.937 */
public class X_AD_Search extends PO
{
/** Constructor estándar */
public X_AD_Search (Properties ctx, int AD_Search_ID, String trxName)
{
super (ctx, AD_Search_ID, trxName);
/** if (AD_Search_ID == 0)
{
setAD_Search_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_Search (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000133 */
public static final int Table_ID=1000133;

/** TableName=AD_Search */
public static final String Table_Name="AD_Search";

protected static KeyNamePair Model = new KeyNamePair(1000133,"AD_Search");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Search[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_Search_ID */
public void setAD_Search_ID (int AD_Search_ID)
{
set_ValueNoCheck ("AD_Search_ID", new Integer(AD_Search_ID));
}
/** Get AD_Search_ID */
public int getAD_Search_ID() 
{
Integer ii = (Integer)get_Value("AD_Search_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
if (AD_Table_ID <= 0) set_Value ("AD_Table_ID", null);
 else 
set_Value ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
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
if (Name.length() > 12)
{
log.warning("Length > 12 - truncated");
Name = Name.substring(0,11);
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
/** Set op */
public void setop (String op)
{
if (op != null && op.length() > 60)
{
log.warning("Length > 60 - truncated");
op = op.substring(0,59);
}
set_Value ("op", op);
}
/** Get op */
public String getop() 
{
return (String)get_Value("op");
}
/** Set val1 */
public void setval1 (String val1)
{
if (val1 != null && val1.length() > 60)
{
log.warning("Length > 60 - truncated");
val1 = val1.substring(0,59);
}
set_Value ("val1", val1);
}
/** Get val1 */
public String getval1() 
{
return (String)get_Value("val1");
}
/** Set val2 */
public void setval2 (String val2)
{
if (val2 != null && val2.length() > 60)
{
log.warning("Length > 60 - truncated");
val2 = val2.substring(0,59);
}
set_Value ("val2", val2);
}
/** Get val2 */
public String getval2() 
{
return (String)get_Value("val2");
}
/** Set val3 */
public void setval3 (String val3)
{
if (val3 != null && val3.length() > 60)
{
log.warning("Length > 60 - truncated");
val3 = val3.substring(0,59);
}
set_Value ("val3", val3);
}
/** Get val3 */
public String getval3() 
{
return (String)get_Value("val3");
}
}
