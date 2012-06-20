/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_City
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:30.857 */
public class X_C_City extends PO
{
/** Constructor estándar */
public X_C_City (Properties ctx, int C_City_ID, String trxName)
{
super (ctx, C_City_ID, trxName);
/** if (C_City_ID == 0)
{
setC_City_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_City (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=186 */
public static final int Table_ID=186;

/** TableName=C_City */
public static final String Table_Name="C_City";

protected static KeyNamePair Model = new KeyNamePair(186,"C_City");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_City[").append(getID()).append("]");
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
/** Set Area Code.
Phone Area Code */
public void setAreaCode (String AreaCode)
{
if (AreaCode != null && AreaCode.length() > 10)
{
log.warning("Length > 10 - truncated");
AreaCode = AreaCode.substring(0,10);
}
set_Value ("AreaCode", AreaCode);
}
/** Get Area Code.
Phone Area Code */
public String getAreaCode() 
{
return (String)get_Value("AreaCode");
}
/** Set City.
City */
public void setC_City_ID (int C_City_ID)
{
set_ValueNoCheck ("C_City_ID", new Integer(C_City_ID));
}
/** Get City.
City */
public int getC_City_ID() 
{
Integer ii = (Integer)get_Value("C_City_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Country.
Country  */
public void setC_Country_ID (int C_Country_ID)
{
if (C_Country_ID <= 0) set_ValueNoCheck ("C_Country_ID", null);
 else 
set_ValueNoCheck ("C_Country_ID", new Integer(C_Country_ID));
}
/** Get Country.
Country  */
public int getC_Country_ID() 
{
Integer ii = (Integer)get_Value("C_Country_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Coordinates.
Location coordinate */
public void setCoordinates (String Coordinates)
{
if (Coordinates != null && Coordinates.length() > 15)
{
log.warning("Length > 15 - truncated");
Coordinates = Coordinates.substring(0,15);
}
set_Value ("Coordinates", Coordinates);
}
/** Get Coordinates.
Location coordinate */
public String getCoordinates() 
{
return (String)get_Value("Coordinates");
}
public static final int C_REGION_ID_AD_Reference_ID=157;
/** Set Region.
Identifies a geographical Region */
public void setC_Region_ID (int C_Region_ID)
{
if (C_Region_ID <= 0) set_Value ("C_Region_ID", null);
 else 
set_Value ("C_Region_ID", new Integer(C_Region_ID));
}
/** Get Region.
Identifies a geographical Region */
public int getC_Region_ID() 
{
Integer ii = (Integer)get_Value("C_Region_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Locode.
Location code - UN/LOCODE  */
public void setLocode (String Locode)
{
if (Locode != null && Locode.length() > 10)
{
log.warning("Length > 10 - truncated");
Locode = Locode.substring(0,10);
}
set_Value ("Locode", Locode);
}
/** Get Locode.
Location code - UN/LOCODE  */
public String getLocode() 
{
return (String)get_Value("Locode");
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
/** Set ZIP.
Postal code */
public void setPostal (String Postal)
{
if (Postal != null && Postal.length() > 10)
{
log.warning("Length > 10 - truncated");
Postal = Postal.substring(0,10);
}
set_Value ("Postal", Postal);
}
/** Get ZIP.
Postal code */
public String getPostal() 
{
return (String)get_Value("Postal");
}
}
