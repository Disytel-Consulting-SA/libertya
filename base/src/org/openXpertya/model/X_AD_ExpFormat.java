/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ExpFormat
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-03-08 18:00:13.708 */
public class X_AD_ExpFormat extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ExpFormat (Properties ctx, int AD_ExpFormat_ID, String trxName)
{
super (ctx, AD_ExpFormat_ID, trxName);
/** if (AD_ExpFormat_ID == 0)
{
setAD_ExpFormat_ID (0);
setAD_Table_ID (0);
setConcatenateTimestamp (false);
setEncodingType (null);	// U
setEndLineType (null);	// W
setExtension (null);	// C
setFileName (null);
setFormatType (null);
setName (null);
setTimestampPattern (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_AD_ExpFormat (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ExpFormat");

/** TableName=AD_ExpFormat */
public static final String Table_Name="AD_ExpFormat";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ExpFormat");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ExpFormat[").append(getID()).append("]");
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
/** Set Export Format */
public void setAD_ExpFormat_ID (int AD_ExpFormat_ID)
{
set_ValueNoCheck ("AD_ExpFormat_ID", new Integer(AD_ExpFormat_ID));
}
/** Get Export Format */
public int getAD_ExpFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_ExpFormat_ID");
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
/** Set Concatenate Timestamp.
Concatenate timestamp to filename */
public void setConcatenateTimestamp (boolean ConcatenateTimestamp)
{
set_Value ("ConcatenateTimestamp", new Boolean(ConcatenateTimestamp));
}
/** Get Concatenate Timestamp.
Concatenate timestamp to filename */
public boolean isConcatenateTimestamp() 
{
Object oo = get_Value("ConcatenateTimestamp");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
public static final int ENCODINGTYPE_AD_Reference_ID = MReference.getReferenceID("AD_ImpFormat EncodingType");
/** 8859_1 = I */
public static final String ENCODINGTYPE_8859_1 = "I";
/** UTF-8 = U */
public static final String ENCODINGTYPE_UTF_8 = "U";
/** Set Encoding Type */
public void setEncodingType (String EncodingType)
{
if (EncodingType.equals("I") || EncodingType.equals("U"));
 else throw new IllegalArgumentException ("EncodingType Invalid value - Reference = ENCODINGTYPE_AD_Reference_ID - I - U");
if (EncodingType == null) throw new IllegalArgumentException ("EncodingType is mandatory");
if (EncodingType.length() > 1)
{
log.warning("Length > 1 - truncated");
EncodingType = EncodingType.substring(0,1);
}
set_Value ("EncodingType", EncodingType);
}
/** Get Encoding Type */
public String getEncodingType() 
{
return (String)get_Value("EncodingType");
}
public static final int ENDLINETYPE_AD_Reference_ID = MReference.getReferenceID("AD_ImpFormat EndLineType");
/** Unix = U */
public static final String ENDLINETYPE_Unix = "U";
/** Windows = W */
public static final String ENDLINETYPE_Windows = "W";
/** Set End Line Type */
public void setEndLineType (String EndLineType)
{
if (EndLineType.equals("U") || EndLineType.equals("W"));
 else throw new IllegalArgumentException ("EndLineType Invalid value - Reference = ENDLINETYPE_AD_Reference_ID - U - W");
if (EndLineType == null) throw new IllegalArgumentException ("EndLineType is mandatory");
if (EndLineType.length() > 1)
{
log.warning("Length > 1 - truncated");
EndLineType = EndLineType.substring(0,1);
}
set_Value ("EndLineType", EndLineType);
}
/** Get End Line Type */
public String getEndLineType() 
{
return (String)get_Value("EndLineType");
}
public static final int EXTENSION_AD_Reference_ID = MReference.getReferenceID("AD_ImpFormat Extension");
/** CSV = C */
public static final String EXTENSION_CSV = "C";
/** TXT = T */
public static final String EXTENSION_TXT = "T";
/** Set Extension */
public void setExtension (String Extension)
{
if (Extension.equals("C") || Extension.equals("T"));
 else throw new IllegalArgumentException ("Extension Invalid value - Reference = EXTENSION_AD_Reference_ID - C - T");
if (Extension == null) throw new IllegalArgumentException ("Extension is mandatory");
if (Extension.length() > 1)
{
log.warning("Length > 1 - truncated");
Extension = Extension.substring(0,1);
}
set_Value ("Extension", Extension);
}
/** Get Extension */
public String getExtension() 
{
return (String)get_Value("Extension");
}
/** Set File Name.
Name of the local file or URL */
public void setFileName (String FileName)
{
if (FileName == null) throw new IllegalArgumentException ("FileName is mandatory");
if (FileName.length() > 100)
{
log.warning("Length > 100 - truncated");
FileName = FileName.substring(0,100);
}
set_Value ("FileName", FileName);
}
/** Get File Name.
Name of the local file or URL */
public String getFileName() 
{
return (String)get_Value("FileName");
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
/** Set Date/Time Pattern.
Date/Time pattern concatenated to the filename.  */
public void setTimestampPattern (String TimestampPattern)
{
if (TimestampPattern == null) throw new IllegalArgumentException ("TimestampPattern is mandatory");
if (TimestampPattern.length() > 40)
{
log.warning("Length > 40 - truncated");
TimestampPattern = TimestampPattern.substring(0,40);
}
set_Value ("TimestampPattern", TimestampPattern);
}
/** Get Date/Time Pattern.
Date/Time pattern concatenated to the filename.  */
public String getTimestampPattern() 
{
return (String)get_Value("TimestampPattern");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
