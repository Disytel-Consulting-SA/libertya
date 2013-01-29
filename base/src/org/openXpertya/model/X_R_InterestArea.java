/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por R_InterestArea
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.921 */
public class X_R_InterestArea extends PO
{
/** Constructor estándar */
public X_R_InterestArea (Properties ctx, int R_InterestArea_ID, String trxName)
{
super (ctx, R_InterestArea_ID, trxName);
/** if (R_InterestArea_ID == 0)
{
setIsSelfService (true);	// Y
setName (null);
setR_InterestArea_ID (0);
}
 */
}
/** Load Constructor */
public X_R_InterestArea (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=530 */
public static final int Table_ID=530;

/** TableName=R_InterestArea */
public static final String Table_Name="R_InterestArea";

protected static KeyNamePair Model = new KeyNamePair(530,"R_InterestArea");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_R_InterestArea[").append(getID()).append("]");
return sb.toString();
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
/** Set Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public void setIsSelfService (boolean IsSelfService)
{
set_Value ("IsSelfService", new Boolean(IsSelfService));
}
/** Get Self-Service.
This is a Self-Service entry or this entry can be changed via Self-Service */
public boolean isSelfService() 
{
Object oo = get_Value("IsSelfService");
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
/** Set Interest Area.
Interest Area or Topic */
public void setR_InterestArea_ID (int R_InterestArea_ID)
{
set_ValueNoCheck ("R_InterestArea_ID", new Integer(R_InterestArea_ID));
}
/** Get Interest Area.
Interest Area or Topic */
public int getR_InterestArea_ID() 
{
Integer ii = (Integer)get_Value("R_InterestArea_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
