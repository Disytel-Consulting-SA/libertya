/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Attribute
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:22.203 */
public class X_AD_Attribute extends PO
{
/** Constructor estándar */
public X_AD_Attribute (Properties ctx, int AD_Attribute_ID, String trxName)
{
super (ctx, AD_Attribute_ID, trxName);
/** if (AD_Attribute_ID == 0)
{
setAD_Attribute_ID (0);
setAD_Reference_ID (0);
setAD_Table_ID (0);
setIsEncrypted (false);
setIsFieldOnly (false);
setIsHeading (false);
setIsMandatory (false);
setIsReadOnly (false);
setIsSameLine (false);
setIsUpdateable (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_Attribute (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=405 */
public static final int Table_ID=405;

/** TableName=AD_Attribute */
public static final String Table_Name="AD_Attribute";

protected static KeyNamePair Model = new KeyNamePair(405,"AD_Attribute");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Attribute[").append(getID()).append("]");
return sb.toString();
}
/** Set System Attribute */
public void setAD_Attribute_ID (int AD_Attribute_ID)
{
set_ValueNoCheck ("AD_Attribute_ID", new Integer(AD_Attribute_ID));
}
/** Get System Attribute */
public int getAD_Attribute_ID() 
{
Integer ii = (Integer)get_Value("AD_Attribute_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_REFERENCE_ID_AD_Reference_ID=1;
/** Set Reference.
System Reference (Pick List) */
public void setAD_Reference_ID (int AD_Reference_ID)
{
set_Value ("AD_Reference_ID", new Integer(AD_Reference_ID));
}
/** Get Reference.
System Reference (Pick List) */
public int getAD_Reference_ID() 
{
Integer ii = (Integer)get_Value("AD_Reference_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_REFERENCE_VALUE_ID_AD_Reference_ID=4;
/** Set Reference Key.
Required to specify, if data type is Table or List */
public void setAD_Reference_Value_ID (int AD_Reference_Value_ID)
{
if (AD_Reference_Value_ID <= 0) set_Value ("AD_Reference_Value_ID", null);
 else 
set_Value ("AD_Reference_Value_ID", new Integer(AD_Reference_Value_ID));
}
/** Get Reference Key.
Required to specify, if data type is Table or List */
public int getAD_Reference_Value_ID() 
{
Integer ii = (Integer)get_Value("AD_Reference_Value_ID");
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
/** Set Dynamic Validation.
Dynamic Validation Rule */
public void setAD_Val_Rule_ID (int AD_Val_Rule_ID)
{
if (AD_Val_Rule_ID <= 0) set_Value ("AD_Val_Rule_ID", null);
 else 
set_Value ("AD_Val_Rule_ID", new Integer(AD_Val_Rule_ID));
}
/** Get Dynamic Validation.
Dynamic Validation Rule */
public int getAD_Val_Rule_ID() 
{
Integer ii = (Integer)get_Value("AD_Val_Rule_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Callout.
Fully qualified class names and method - separated by semicolons */
public void setCallout (String Callout)
{
if (Callout != null && Callout.length() > 60)
{
log.warning("Length > 60 - truncated");
Callout = Callout.substring(0,59);
}
set_Value ("Callout", Callout);
}
/** Get Callout.
Fully qualified class names and method - separated by semicolons */
public String getCallout() 
{
return (String)get_Value("Callout");
}
/** Set Default Logic.
Default value hierarchy, separated by ;
 */
public void setDefaultValue (String DefaultValue)
{
if (DefaultValue != null && DefaultValue.length() > 2000)
{
log.warning("Length > 2000 - truncated");
DefaultValue = DefaultValue.substring(0,1999);
}
set_Value ("DefaultValue", DefaultValue);
}
/** Get Default Logic.
Default value hierarchy, separated by ;
 */
public String getDefaultValue() 
{
return (String)get_Value("DefaultValue");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Display Length.
Length of the display in characters */
public void setDisplayLength (int DisplayLength)
{
set_Value ("DisplayLength", new Integer(DisplayLength));
}
/** Get Display Length.
Length of the display in characters */
public int getDisplayLength() 
{
Integer ii = (Integer)get_Value("DisplayLength");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Display Logic.
If the Field is displayed, the result determines if the field is actually displayed */
public void setDisplayLogic (String DisplayLogic)
{
if (DisplayLogic != null && DisplayLogic.length() > 2000)
{
log.warning("Length > 2000 - truncated");
DisplayLogic = DisplayLogic.substring(0,1999);
}
set_Value ("DisplayLogic", DisplayLogic);
}
/** Get Display Logic.
If the Field is displayed, the result determines if the field is actually displayed */
public String getDisplayLogic() 
{
return (String)get_Value("DisplayLogic");
}
/** Set Length.
Length of the column in the database */
public void setFieldLength (int FieldLength)
{
set_Value ("FieldLength", new Integer(FieldLength));
}
/** Get Length.
Length of the column in the database */
public int getFieldLength() 
{
Integer ii = (Integer)get_Value("FieldLength");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Encrypted.
Display or Storage is encrypted */
public void setIsEncrypted (boolean IsEncrypted)
{
set_Value ("IsEncrypted", new Boolean(IsEncrypted));
}
/** Get Encrypted.
Display or Storage is encrypted */
public boolean isEncrypted() 
{
Object oo = get_Value("IsEncrypted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Field Only.
Label is not displayed */
public void setIsFieldOnly (boolean IsFieldOnly)
{
set_Value ("IsFieldOnly", new Boolean(IsFieldOnly));
}
/** Get Field Only.
Label is not displayed */
public boolean isFieldOnly() 
{
Object oo = get_Value("IsFieldOnly");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Heading only.
Field without Column - Only label is displayed */
public void setIsHeading (boolean IsHeading)
{
set_Value ("IsHeading", new Boolean(IsHeading));
}
/** Get Heading only.
Field without Column - Only label is displayed */
public boolean isHeading() 
{
Object oo = get_Value("IsHeading");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Mandatory.
Data entry is required in this column */
public void setIsMandatory (boolean IsMandatory)
{
set_Value ("IsMandatory", new Boolean(IsMandatory));
}
/** Get Mandatory.
Data entry is required in this column */
public boolean isMandatory() 
{
Object oo = get_Value("IsMandatory");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Read Only.
Field is read only */
public void setIsReadOnly (boolean IsReadOnly)
{
set_Value ("IsReadOnly", new Boolean(IsReadOnly));
}
/** Get Read Only.
Field is read only */
public boolean isReadOnly() 
{
Object oo = get_Value("IsReadOnly");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Same Line.
Displayed on same line as previous field */
public void setIsSameLine (boolean IsSameLine)
{
set_Value ("IsSameLine", new Boolean(IsSameLine));
}
/** Get Same Line.
Displayed on same line as previous field */
public boolean isSameLine() 
{
Object oo = get_Value("IsSameLine");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Updateable.
Determines, if the field can be updated */
public void setIsUpdateable (boolean IsUpdateable)
{
set_Value ("IsUpdateable", new Boolean(IsUpdateable));
}
/** Get Updateable.
Determines, if the field can be updated */
public boolean isUpdateable() 
{
Object oo = get_Value("IsUpdateable");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Value Format.
Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public void setVFormat (String VFormat)
{
if (VFormat != null && VFormat.length() > 60)
{
log.warning("Length > 60 - truncated");
VFormat = VFormat.substring(0,59);
}
set_Value ("VFormat", VFormat);
}
/** Get Value Format.
Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public String getVFormat() 
{
return (String)get_Value("VFormat");
}
/** Set Max. Value.
Maximum Value for a field */
public void setValueMax (String ValueMax)
{
if (ValueMax != null && ValueMax.length() > 20)
{
log.warning("Length > 20 - truncated");
ValueMax = ValueMax.substring(0,19);
}
set_Value ("ValueMax", ValueMax);
}
/** Get Max. Value.
Maximum Value for a field */
public String getValueMax() 
{
return (String)get_Value("ValueMax");
}
/** Set Min. Value.
Minimum Value for a field */
public void setValueMin (String ValueMin)
{
if (ValueMin != null && ValueMin.length() > 20)
{
log.warning("Length > 20 - truncated");
ValueMin = ValueMin.substring(0,19);
}
set_Value ("ValueMin", ValueMin);
}
/** Get Min. Value.
Minimum Value for a field */
public String getValueMin() 
{
return (String)get_Value("ValueMin");
}
}
