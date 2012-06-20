/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_PrintPaper
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:27.464 */
public class X_AD_PrintPaper extends PO
{
/** Constructor estándar */
public X_AD_PrintPaper (Properties ctx, int AD_PrintPaper_ID, String trxName)
{
super (ctx, AD_PrintPaper_ID, trxName);
/** if (AD_PrintPaper_ID == 0)
{
setAD_PrintPaper_ID (0);
setCode (null);	// iso-a4
setIsDefault (false);
setIsLandscape (true);	// Y
setMarginBottom (0);	// 36
setMarginLeft (0);	// 36
setMarginRight (0);	// 36
setMarginTop (0);	// 36
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_PrintPaper (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=492 */
public static final int Table_ID=492;

/** TableName=AD_PrintPaper */
public static final String Table_Name="AD_PrintPaper";

protected static KeyNamePair Model = new KeyNamePair(492,"AD_PrintPaper");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_PrintPaper[").append(getID()).append("]");
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
/** Set Print Paper.
Printer paper definition */
public void setAD_PrintPaper_ID (int AD_PrintPaper_ID)
{
set_ValueNoCheck ("AD_PrintPaper_ID", new Integer(AD_PrintPaper_ID));
}
/** Get Print Paper.
Printer paper definition */
public int getAD_PrintPaper_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintPaper_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Validation code.
Validation Code */
public void setCode (String Code)
{
if (Code == null) throw new IllegalArgumentException ("Code is mandatory");
if (Code.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Code = Code.substring(0,2000);
}
set_Value ("Code", Code);
}
/** Get Validation code.
Validation Code */
public String getCode() 
{
return (String)get_Value("Code");
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
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Landscape.
Landscape orientation */
public void setIsLandscape (boolean IsLandscape)
{
set_Value ("IsLandscape", new Boolean(IsLandscape));
}
/** Get Landscape.
Landscape orientation */
public boolean isLandscape() 
{
Object oo = get_Value("IsLandscape");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Bottom Margin.
Bottom Space in 1/72 inch */
public void setMarginBottom (int MarginBottom)
{
set_Value ("MarginBottom", new Integer(MarginBottom));
}
/** Get Bottom Margin.
Bottom Space in 1/72 inch */
public int getMarginBottom() 
{
Integer ii = (Integer)get_Value("MarginBottom");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Left Margin.
Left Space in 1/72 inch */
public void setMarginLeft (int MarginLeft)
{
set_Value ("MarginLeft", new Integer(MarginLeft));
}
/** Get Left Margin.
Left Space in 1/72 inch */
public int getMarginLeft() 
{
Integer ii = (Integer)get_Value("MarginLeft");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Right Margin.
Right Space in 1/72 inch */
public void setMarginRight (int MarginRight)
{
set_Value ("MarginRight", new Integer(MarginRight));
}
/** Get Right Margin.
Right Space in 1/72 inch */
public int getMarginRight() 
{
Integer ii = (Integer)get_Value("MarginRight");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Top Margin.
Top Space in 1/72 inch */
public void setMarginTop (int MarginTop)
{
set_Value ("MarginTop", new Integer(MarginTop));
}
/** Get Top Margin.
Top Space in 1/72 inch */
public int getMarginTop() 
{
Integer ii = (Integer)get_Value("MarginTop");
if (ii == null) return 0;
return ii.intValue();
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
}
