/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_C_City
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:34.562 */
public class X_I_C_City extends PO
{
/** Constructor estándar */
public X_I_C_City (Properties ctx, int I_C_City_ID, String trxName)
{
super (ctx, I_C_City_ID, trxName);
/** if (I_C_City_ID == 0)
{
setI_C_City_ID (0);
}
 */
}
/** Load Constructor */
public X_I_C_City (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000109 */
public static final int Table_ID=1000109;

/** TableName=I_C_City */
public static final String Table_Name="I_C_City";

protected static KeyNamePair Model = new KeyNamePair(1000109,"I_C_City");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_C_City[").append(getID()).append("]");
return sb.toString();
}
/** Set Area Code.
Phone Area Code */
public void setAreaCode (String AreaCode)
{
if (AreaCode != null && AreaCode.length() > 10)
{
log.warning("Length > 10 - truncated");
AreaCode = AreaCode.substring(0,9);
}
set_Value ("AreaCode", AreaCode);
}
/** Get Area Code.
Phone Area Code */
public String getAreaCode() 
{
return (String)get_Value("AreaCode");
}
/** Set Country.
Country  */
public void setC_Country_ID (int C_Country_ID)
{
if (C_Country_ID <= 0) set_Value ("C_Country_ID", null);
 else 
set_Value ("C_Country_ID", new Integer(C_Country_ID));
}
/** Get Country.
Country  */
public int getC_Country_ID() 
{
Integer ii = (Integer)get_Value("C_Country_ID");
if (ii == null) return 0;
return ii.intValue();
}
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
/** Set Coordinates.
Location coordinate */
public void setCoordinates (String Coordinates)
{
if (Coordinates != null && Coordinates.length() > 15)
{
log.warning("Length > 15 - truncated");
Coordinates = Coordinates.substring(0,14);
}
set_Value ("Coordinates", Coordinates);
}
/** Get Coordinates.
Location coordinate */
public String getCoordinates() 
{
return (String)get_Value("Coordinates");
}
/** Set I_C_City_ID */
public void setI_C_City_ID (int I_C_City_ID)
{
set_ValueNoCheck ("I_C_City_ID", new Integer(I_C_City_ID));
}
/** Get I_C_City_ID */
public int getI_C_City_ID() 
{
Integer ii = (Integer)get_Value("I_C_City_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Import Error Message.
Messages generated from import process */
public void setI_ErrorMsg (String I_ErrorMsg)
{
if (I_ErrorMsg != null && I_ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
I_ErrorMsg = I_ErrorMsg.substring(0,1999);
}
set_Value ("I_ErrorMsg", I_ErrorMsg);
}
/** Get Import Error Message.
Messages generated from import process */
public String getI_ErrorMsg() 
{
return (String)get_Value("I_ErrorMsg");
}
/** Set Imported.
Has this import been processed */
public void setI_IsImported (boolean I_IsImported)
{
set_Value ("I_IsImported", new Boolean(I_IsImported));
}
/** Get Imported.
Has this import been processed */
public boolean isI_IsImported() 
{
Object oo = get_Value("I_IsImported");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Locode.
Location code - UN/LOCODE  */
public void setLocode (String Locode)
{
if (Locode != null && Locode.length() > 10)
{
log.warning("Length > 10 - truncated");
Locode = Locode.substring(0,9);
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
if (Name != null && Name.length() > 60)
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
/** Set ZIP.
Postal code */
public void setPostal (String Postal)
{
if (Postal != null && Postal.length() > 10)
{
log.warning("Length > 10 - truncated");
Postal = Postal.substring(0,9);
}
set_Value ("Postal", Postal);
}
/** Get ZIP.
Postal code */
public String getPostal() 
{
return (String)get_Value("Postal");
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
