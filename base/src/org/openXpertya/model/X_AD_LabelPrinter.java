/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_LabelPrinter
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:26.827 */
public class X_AD_LabelPrinter extends PO
{
/** Constructor estándar */
public X_AD_LabelPrinter (Properties ctx, int AD_LabelPrinter_ID, String trxName)
{
super (ctx, AD_LabelPrinter_ID, trxName);
/** if (AD_LabelPrinter_ID == 0)
{
setAD_LabelPrinter_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_LabelPrinter (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=626 */
public static final int Table_ID=626;

/** TableName=AD_LabelPrinter */
public static final String Table_Name="AD_LabelPrinter";

protected static KeyNamePair Model = new KeyNamePair(626,"AD_LabelPrinter");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_LabelPrinter[").append(getID()).append("]");
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
/** Set Label printer.
Label Printer Definition */
public void setAD_LabelPrinter_ID (int AD_LabelPrinter_ID)
{
set_ValueNoCheck ("AD_LabelPrinter_ID", new Integer(AD_LabelPrinter_ID));
}
/** Get Label printer.
Label Printer Definition */
public int getAD_LabelPrinter_ID() 
{
Integer ii = (Integer)get_Value("AD_LabelPrinter_ID");
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
/** Set port */
public void setport (String port)
{
if (port != null && port.length() > 255)
{
log.warning("Length > 255 - truncated");
port = port.substring(0,255);
}
set_Value ("port", port);
}
/** Get port */
public String getport() 
{
return (String)get_Value("port");
}
}
