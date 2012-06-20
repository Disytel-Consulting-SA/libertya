/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_DunningLevel_Trl
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:31.352 */
public class X_C_DunningLevel_Trl extends PO
{
/** Constructor estándar */
public X_C_DunningLevel_Trl (Properties ctx, int C_DunningLevel_Trl_ID, String trxName)
{
super (ctx, C_DunningLevel_Trl_ID, trxName);
/** if (C_DunningLevel_Trl_ID == 0)
{
setAD_Language (null);
setC_DunningLevel_ID (0);
setIsTranslated (false);
setPrintName (null);
}
 */
}
/** Load Constructor */
public X_C_DunningLevel_Trl (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=332 */
public static final int Table_ID=332;

/** TableName=C_DunningLevel_Trl */
public static final String Table_Name="C_DunningLevel_Trl";

protected static KeyNamePair Model = new KeyNamePair(332,"C_DunningLevel_Trl");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_DunningLevel_Trl[").append(getID()).append("]");
return sb.toString();
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
/** Set Dunning Level */
public void setC_DunningLevel_ID (int C_DunningLevel_ID)
{
set_ValueNoCheck ("C_DunningLevel_ID", new Integer(C_DunningLevel_ID));
}
/** Get Dunning Level */
public int getC_DunningLevel_ID() 
{
Integer ii = (Integer)get_Value("C_DunningLevel_ID");
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
/** Set Note.
Optional additional user defined information */
public void setNote (String Note)
{
if (Note != null && Note.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Note = Note.substring(0,2000);
}
set_Value ("Note", Note);
}
/** Get Note.
Optional additional user defined information */
public String getNote() 
{
return (String)get_Value("Note");
}
/** Set Print Text.
The label text to be printed on a document or correspondence. */
public void setPrintName (String PrintName)
{
if (PrintName == null) throw new IllegalArgumentException ("PrintName is mandatory");
if (PrintName.length() > 60)
{
log.warning("Length > 60 - truncated");
PrintName = PrintName.substring(0,60);
}
set_Value ("PrintName", PrintName);
}
/** Get Print Text.
The label text to be printed on a document or correspondence. */
public String getPrintName() 
{
return (String)get_Value("PrintName");
}
}
