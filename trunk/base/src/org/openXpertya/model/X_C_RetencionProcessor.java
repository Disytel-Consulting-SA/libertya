/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RetencionProcessor
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:33.069 */
public class X_C_RetencionProcessor extends PO
{
/** Constructor estándar */
public X_C_RetencionProcessor (Properties ctx, int C_RetencionProcessor_ID, String trxName)
{
super (ctx, C_RetencionProcessor_ID, trxName);
/** if (C_RetencionProcessor_ID == 0)
{
setC_RetencionProcessor_ID (0);
setName (null);
setnameclass (null);
}
 */
}
/** Load Constructor */
public X_C_RetencionProcessor (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000141 */
public static final int Table_ID=1000141;

/** TableName=C_RetencionProcessor */
public static final String Table_Name="C_RetencionProcessor";

protected static KeyNamePair Model = new KeyNamePair(1000141,"C_RetencionProcessor");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RetencionProcessor[").append(getID()).append("]");
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
/** Set Retencion Processor */
public void setC_RetencionProcessor_ID (int C_RetencionProcessor_ID)
{
set_ValueNoCheck ("C_RetencionProcessor_ID", new Integer(C_RetencionProcessor_ID));
}
/** Get Retencion Processor */
public int getC_RetencionProcessor_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionProcessor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 60)
{
log.warning("Length > 60 - truncated");
Description = Description.substring(0,60);
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
/** Set Class Name */
public void setnameclass (String nameclass)
{
if (nameclass == null) throw new IllegalArgumentException ("nameclass is mandatory");
if (nameclass.length() > 60)
{
log.warning("Length > 60 - truncated");
nameclass = nameclass.substring(0,60);
}
set_Value ("nameclass", nameclass);
}
/** Get Class Name */
public String getnameclass() 
{
return (String)get_Value("nameclass");
}
}
