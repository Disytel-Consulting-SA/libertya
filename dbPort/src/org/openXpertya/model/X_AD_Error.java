/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Error
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:22.796 */
public class X_AD_Error extends PO
{
/** Constructor estándar */
public X_AD_Error (Properties ctx, int AD_Error_ID, String trxName)
{
super (ctx, AD_Error_ID, trxName);
/** if (AD_Error_ID == 0)
{
setAD_Error_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_Error (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=380 */
public static final int Table_ID=380;

/** TableName=AD_Error */
public static final String Table_Name="AD_Error";

protected static KeyNamePair Model = new KeyNamePair(380,"AD_Error");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Error[").append(getID()).append("]");
return sb.toString();
}
/** Set Error */
public void setAD_Error_ID (int AD_Error_ID)
{
set_ValueNoCheck ("AD_Error_ID", new Integer(AD_Error_ID));
}
/** Get Error */
public int getAD_Error_ID() 
{
Integer ii = (Integer)get_Value("AD_Error_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_LANGUAGE_AD_Reference_ID=106;
/** Set Language.
Language for this entity */
public void setAD_Language (String AD_Language)
{
if (AD_Language != null && AD_Language.length() > 6)
{
log.warning("Length > 6 - truncated");
AD_Language = AD_Language.substring(0,5);
}
set_Value ("AD_Language", AD_Language);
}
/** Get Language.
Language for this entity */
public String getAD_Language() 
{
return (String)get_Value("AD_Language");
}
/** Set Validation code.
Validation Code */
public void setCode (String Code)
{
if (Code != null && Code.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Code = Code.substring(0,1999);
}
set_Value ("Code", Code);
}
/** Get Validation code.
Validation Code */
public String getCode() 
{
return (String)get_Value("Code");
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
