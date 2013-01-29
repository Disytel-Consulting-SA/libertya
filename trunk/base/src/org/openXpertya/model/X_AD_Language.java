/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Language
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:26.862 */
public class X_AD_Language extends PO
{
/** Constructor estándar */
public X_AD_Language (Properties ctx, int AD_Language_ID, String trxName)
{
super (ctx, AD_Language_ID, trxName);
/** if (AD_Language_ID == 0)
{
setAD_Language (null);
setAD_Language_ID (0);	// @SQL=SELECT NVL(MAX(AD_Language_ID),0)+1 AS DefaultValue FROM AD_Language
setIsBaseLanguage (false);	// N
setIsDecimalPoint (false);
setIsSystemLanguage (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_Language (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=111 */
public static final int Table_ID=111;

/** TableName=AD_Language */
public static final String Table_Name="AD_Language";

protected static KeyNamePair Model = new KeyNamePair(111,"AD_Language");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Language[").append(getID()).append("]");
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
/** Set Language ID */
public void setAD_Language_ID (int AD_Language_ID)
{
set_ValueNoCheck ("AD_Language_ID", new Integer(AD_Language_ID));
}
/** Get Language ID */
public int getAD_Language_ID() 
{
Integer ii = (Integer)get_Value("AD_Language_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set ISO Country Code.
Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html */
public void setCountryCode (String CountryCode)
{
if (CountryCode != null && CountryCode.length() > 2)
{
log.warning("Length > 2 - truncated");
CountryCode = CountryCode.substring(0,2);
}
set_Value ("CountryCode", CountryCode);
}
/** Get ISO Country Code.
Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html */
public String getCountryCode() 
{
return (String)get_Value("CountryCode");
}
/** Set Date Pattern.
Java Date Pattern */
public void setDatePattern (String DatePattern)
{
if (DatePattern != null && DatePattern.length() > 20)
{
log.warning("Length > 20 - truncated");
DatePattern = DatePattern.substring(0,20);
}
set_Value ("DatePattern", DatePattern);
}
/** Get Date Pattern.
Java Date Pattern */
public String getDatePattern() 
{
return (String)get_Value("DatePattern");
}
/** Set Base Language.
The system information is maintained in this language */
public void setIsBaseLanguage (boolean IsBaseLanguage)
{
set_ValueNoCheck ("IsBaseLanguage", new Boolean(IsBaseLanguage));
}
/** Get Base Language.
The system information is maintained in this language */
public boolean isBaseLanguage() 
{
Object oo = get_Value("IsBaseLanguage");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Decimal Point.
The number notation has a decimal point (no decimal comma) */
public void setIsDecimalPoint (boolean IsDecimalPoint)
{
set_Value ("IsDecimalPoint", new Boolean(IsDecimalPoint));
}
/** Get Decimal Point.
The number notation has a decimal point (no decimal comma) */
public boolean isDecimalPoint() 
{
Object oo = get_Value("IsDecimalPoint");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set System Language.
The screens, etc. are maintained in this Language */
public void setIsSystemLanguage (boolean IsSystemLanguage)
{
set_Value ("IsSystemLanguage", new Boolean(IsSystemLanguage));
}
/** Get System Language.
The screens, etc. are maintained in this Language */
public boolean isSystemLanguage() 
{
Object oo = get_Value("IsSystemLanguage");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set ISO Language Code.
Lower-case two-letter ISO-3166 code - http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt  */
public void setLanguageISO (String LanguageISO)
{
if (LanguageISO != null && LanguageISO.length() > 2)
{
log.warning("Length > 2 - truncated");
LanguageISO = LanguageISO.substring(0,2);
}
set_Value ("LanguageISO", LanguageISO);
}
/** Get ISO Language Code.
Lower-case two-letter ISO-3166 code - http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt  */
public String getLanguageISO() 
{
return (String)get_Value("LanguageISO");
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
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Time Pattern.
Java Time Pattern */
public void setTimePattern (String TimePattern)
{
if (TimePattern != null && TimePattern.length() > 20)
{
log.warning("Length > 20 - truncated");
TimePattern = TimePattern.substring(0,20);
}
set_Value ("TimePattern", TimePattern);
}
/** Get Time Pattern.
Java Time Pattern */
public String getTimePattern() 
{
return (String)get_Value("TimePattern");
}
}
