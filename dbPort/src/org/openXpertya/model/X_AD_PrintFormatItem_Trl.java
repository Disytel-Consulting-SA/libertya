/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_PrintFormatItem_Trl
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:27.349 */
public class X_AD_PrintFormatItem_Trl extends PO
{
/** Constructor estándar */
public X_AD_PrintFormatItem_Trl (Properties ctx, int AD_PrintFormatItem_Trl_ID, String trxName)
{
super (ctx, AD_PrintFormatItem_Trl_ID, trxName);
/** if (AD_PrintFormatItem_Trl_ID == 0)
{
setAD_Language (null);
setAD_PrintFormatItem_ID (0);
setIsTranslated (false);
}
 */
}
/** Load Constructor */
public X_AD_PrintFormatItem_Trl (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=522 */
public static final int Table_ID=522;

/** TableName=AD_PrintFormatItem_Trl */
public static final String Table_Name="AD_PrintFormatItem_Trl";

protected static KeyNamePair Model = new KeyNamePair(522,"AD_PrintFormatItem_Trl");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_PrintFormatItem_Trl[").append(getID()).append("]");
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
/** Set Component Version Identifier */
public void setAD_ComponentVersion_ID (int AD_ComponentVersion_ID)
{
if (AD_ComponentVersion_ID <= 0) set_Value ("AD_ComponentVersion_ID", null);
 else 
set_Value ("AD_ComponentVersion_ID", new Integer(AD_ComponentVersion_ID));
}
/** Get Component Version Identifier */
public int getAD_ComponentVersion_ID() 
{
Integer ii = (Integer)get_Value("AD_ComponentVersion_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_LANGUAGE_AD_Reference_ID=106;
/** Set Language.
Language for this entity */
public void setAD_Language (String AD_Language)
{
if (AD_Language == null) throw new IllegalArgumentException ("AD_Language is mandatory");
if (AD_Language.length() > 6)
{
log.warning("Length > 6 - truncated");
AD_Language = AD_Language.substring(0,6);
}
set_ValueNoCheck ("AD_Language", AD_Language);
}
/** Get Language.
Language for this entity */
public String getAD_Language() 
{
return (String)get_Value("AD_Language");
}
/** Set Print Format Item.
Item/Column in the Print format */
public void setAD_PrintFormatItem_ID (int AD_PrintFormatItem_ID)
{
set_ValueNoCheck ("AD_PrintFormatItem_ID", new Integer(AD_PrintFormatItem_ID));
}
/** Get Print Format Item.
Item/Column in the Print format */
public int getAD_PrintFormatItem_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintFormatItem_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Translated.
This column is translated */
public void setIsTranslated (boolean IsTranslated)
{
set_Value ("IsTranslated", new Boolean(IsTranslated));
}
/** Get Translated.
This column is translated */
public boolean isTranslated() 
{
Object oo = get_Value("IsTranslated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Print Text.
The label text to be printed on a document or correspondence. */
public void setPrintName (String PrintName)
{
if (PrintName != null && PrintName.length() > 2000)
{
log.warning("Length > 2000 - truncated");
PrintName = PrintName.substring(0,2000);
}
set_Value ("PrintName", PrintName);
}
/** Get Print Text.
The label text to be printed on a document or correspondence. */
public String getPrintName() 
{
return (String)get_Value("PrintName");
}
/** Set Print Label Suffix.
The label text to be printed on a document or correspondence after the field */
public void setPrintNameSuffix (String PrintNameSuffix)
{
if (PrintNameSuffix != null && PrintNameSuffix.length() > 60)
{
log.warning("Length > 60 - truncated");
PrintNameSuffix = PrintNameSuffix.substring(0,60);
}
set_Value ("PrintNameSuffix", PrintNameSuffix);
}
/** Get Print Label Suffix.
The label text to be printed on a document or correspondence after the field */
public String getPrintNameSuffix() 
{
return (String)get_Value("PrintNameSuffix");
}
}
