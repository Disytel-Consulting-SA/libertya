/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Controlador_Fiscal_Type
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:30.992 */
public class X_C_Controlador_Fiscal_Type extends PO
{
/** Constructor estándar */
public X_C_Controlador_Fiscal_Type (Properties ctx, int C_Controlador_Fiscal_Type_ID, String trxName)
{
super (ctx, C_Controlador_Fiscal_Type_ID, trxName);
/** if (C_Controlador_Fiscal_Type_ID == 0)
{
setC_Controlador_Fiscal_Type_ID (0);
setclazz (null);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_Controlador_Fiscal_Type (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000159 */
public static final int Table_ID=1000159;

/** TableName=C_Controlador_Fiscal_Type */
public static final String Table_Name="C_Controlador_Fiscal_Type";

protected static KeyNamePair Model = new KeyNamePair(1000159,"C_Controlador_Fiscal_Type");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Controlador_Fiscal_Type[").append(getID()).append("]");
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
/** Set C_Controlador_Fiscal_Type_ID */
public void setC_Controlador_Fiscal_Type_ID (int C_Controlador_Fiscal_Type_ID)
{
set_ValueNoCheck ("C_Controlador_Fiscal_Type_ID", new Integer(C_Controlador_Fiscal_Type_ID));
}
/** Get C_Controlador_Fiscal_Type_ID */
public int getC_Controlador_Fiscal_Type_ID() 
{
Integer ii = (Integer)get_Value("C_Controlador_Fiscal_Type_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set clazz */
public void setclazz (String clazz)
{
if (clazz == null) throw new IllegalArgumentException ("clazz is mandatory");
if (clazz.length() > 100)
{
log.warning("Length > 100 - truncated");
clazz = clazz.substring(0,100);
}
set_Value ("clazz", clazz);
}
/** Get clazz */
public String getclazz() 
{
return (String)get_Value("clazz");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 200)
{
log.warning("Length > 200 - truncated");
Description = Description.substring(0,200);
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
if (Name.length() > 100)
{
log.warning("Length > 100 - truncated");
Name = Name.substring(0,100);
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
/** Set parameters */
public void setparameters (String parameters)
{
if (parameters != null && parameters.length() > 500)
{
log.warning("Length > 500 - truncated");
parameters = parameters.substring(0,500);
}
set_Value ("parameters", parameters);
}
/** Get parameters */
public String getparameters() 
{
return (String)get_Value("parameters");
}
}
