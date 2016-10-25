/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ImpFormat
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-25 15:17:08.523 */
public class X_AD_ImpFormat extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ImpFormat (Properties ctx, int AD_ImpFormat_ID, String trxName)
{
super (ctx, AD_ImpFormat_ID, trxName);
/** if (AD_ImpFormat_ID == 0)
{
setAD_ImpFormat_ID (0);
setAD_Table_ID (0);
setFormatType (null);
setName (null);
setProcessing (false);
}
 */
}
/** Load Constructor */
public X_AD_ImpFormat (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ImpFormat");

/** TableName=AD_ImpFormat */
public static final String Table_Name="AD_ImpFormat";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ImpFormat");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ImpFormat[").append(getID()).append("]");
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
/** Set Import Format */
public void setAD_ImpFormat_ID (int AD_ImpFormat_ID)
{
set_ValueNoCheck ("AD_ImpFormat_ID", new Integer(AD_ImpFormat_ID));
}
/** Get Import Format */
public int getAD_ImpFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_ImpFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_PROCESS_ID_AD_Reference_ID = MReference.getReferenceID("Ad_Process");
/** Set Process.
Process or Report */
public void setAD_Process_ID (int AD_Process_ID)
{
if (AD_Process_ID <= 0) set_Value ("AD_Process_ID", null);
 else 
set_Value ("AD_Process_ID", new Integer(AD_Process_ID));
}
/** Get Process.
Process or Report */
public int getAD_Process_ID() 
{
Integer ii = (Integer)get_Value("AD_Process_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
set_Value ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Delimiter */
public void setDelimiter (String Delimiter)
{
if (Delimiter != null && Delimiter.length() > 1)
{
log.warning("Length > 1 - truncated");
Delimiter = Delimiter.substring(0,1);
}
set_Value ("Delimiter", Delimiter);
}
/** Get Delimiter */
public String getDelimiter() 
{
return (String)get_Value("Delimiter");
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
public static final int FORMATTYPE_AD_Reference_ID = MReference.getReferenceID("AD_ImpFormat FormatType");
/** Fixed Position = F */
public static final String FORMATTYPE_FixedPosition = "F";
/** Comma Separated = C */
public static final String FORMATTYPE_CommaSeparated = "C";
/** Tab Separated = T */
public static final String FORMATTYPE_TabSeparated = "T";
/** XML = X */
public static final String FORMATTYPE_XML = "X";
/** Set Format.
Format of the data */
public void setFormatType (String FormatType)
{
if (FormatType.equals("F") || FormatType.equals("C") || FormatType.equals("T") || FormatType.equals("X"));
 else throw new IllegalArgumentException ("FormatType Invalid value - Reference = FORMATTYPE_AD_Reference_ID - F - C - T - X");
if (FormatType == null) throw new IllegalArgumentException ("FormatType is mandatory");
if (FormatType.length() > 1)
{
log.warning("Length > 1 - truncated");
FormatType = FormatType.substring(0,1);
}
set_Value ("FormatType", FormatType);
}
/** Get Format.
Format of the data */
public String getFormatType() 
{
return (String)get_Value("FormatType");
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
