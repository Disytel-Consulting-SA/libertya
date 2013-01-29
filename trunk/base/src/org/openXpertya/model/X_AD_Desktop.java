/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Desktop
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-27 12:54:23.567 */
public class X_AD_Desktop extends PO
{
/** Constructor estándar */
public X_AD_Desktop (Properties ctx, int AD_Desktop_ID, String trxName)
{
super (ctx, AD_Desktop_ID, trxName);
/** if (AD_Desktop_ID == 0)
{
setAD_Desktop_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_Desktop (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=458 */
public static final int Table_ID=458;

/** TableName=AD_Desktop */
public static final String Table_Name="AD_Desktop";

protected static KeyNamePair Model = new KeyNamePair(458,"AD_Desktop");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Desktop[").append(getID()).append("]");
return sb.toString();
}
/** Set System Color.
Color for backgrounds or indicators */
public void setAD_Color_ID (Object AD_Color_ID)
{
set_Value ("AD_Color_ID", AD_Color_ID);
}
/** Get System Color.
Color for backgrounds or indicators */
public Object getAD_Color_ID() 
{
return get_Value("AD_Color_ID");
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
/** Set Desktop.
Collection of Workbenches */
public void setAD_Desktop_ID (int AD_Desktop_ID)
{
set_ValueNoCheck ("AD_Desktop_ID", new Integer(AD_Desktop_ID));
}
/** Get Desktop.
Collection of Workbenches */
public int getAD_Desktop_ID() 
{
Integer ii = (Integer)get_Value("AD_Desktop_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Image.
System Image or Icon */
public void setAD_Image_ID (byte[] AD_Image_ID)
{
set_Value ("AD_Image_ID", AD_Image_ID);
}
/** Get Image.
System Image or Icon */
public byte[] getAD_Image_ID() 
{
return (byte[])get_Value("AD_Image_ID");
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
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,2000);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
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
}
