/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Process_Para
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-04-10 18:37:32.512 */
public class X_AD_Process_Para extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Process_Para (Properties ctx, int AD_Process_Para_ID, String trxName)
{
super (ctx, AD_Process_Para_ID, trxName);
/** if (AD_Process_Para_ID == 0)
{
setAD_Process_ID (0);
setAD_Process_Para_ID (0);
setAD_Reference_ID (0);
setColumnName (null);
setEntityType (null);	// U
setFieldLength (0);
setIsCentrallyMaintained (true);	// Y
setIsMandatory (false);
setIsRange (false);
setName (null);
setSameLine (false);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM AD_Process_Para WHERE AD_Process_ID=@AD_Process_ID@
}
 */
}
/** Load Constructor */
public X_AD_Process_Para (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Process_Para");

/** TableName=AD_Process_Para */
public static final String Table_Name="AD_Process_Para";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Process_Para");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Process_Para[").append(getID()).append("]");
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
/** Set System Element.
System Element enables the central maintenance of column description and help. */
public void setAD_Element_ID (int AD_Element_ID)
{
if (AD_Element_ID <= 0) set_Value ("AD_Element_ID", null);
 else 
set_Value ("AD_Element_ID", new Integer(AD_Element_ID));
}
/** Get System Element.
System Element enables the central maintenance of column description and help. */
public int getAD_Element_ID() 
{
Integer ii = (Integer)get_Value("AD_Element_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Process.
Process or Report */
public void setAD_Process_ID (int AD_Process_ID)
{
set_ValueNoCheck ("AD_Process_ID", new Integer(AD_Process_ID));
}
/** Get Process.
Process or Report */
public int getAD_Process_ID() 
{
Integer ii = (Integer)get_Value("AD_Process_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Process Parameter */
public void setAD_Process_Para_ID (int AD_Process_Para_ID)
{
set_ValueNoCheck ("AD_Process_Para_ID", new Integer(AD_Process_Para_ID));
}
/** Get Process Parameter */
public int getAD_Process_Para_ID() 
{
Integer ii = (Integer)get_Value("AD_Process_Para_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_REFERENCE_ID_AD_Reference_ID = MReference.getReferenceID("AD_Reference Data Types");
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
public static final int AD_REFERENCE_VALUE_ID_AD_Reference_ID = MReference.getReferenceID("AD_Reference Values");
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
/** Set DB Column Name.
Name of the column in the database */
public void setColumnName (String ColumnName)
{
if (ColumnName == null) throw new IllegalArgumentException ("ColumnName is mandatory");
if (ColumnName.length() > 40)
{
log.warning("Length > 40 - truncated");
ColumnName = ColumnName.substring(0,40);
}
set_Value ("ColumnName", ColumnName);
}
/** Get DB Column Name.
Name of the column in the database */
public String getColumnName() 
{
return (String)get_Value("ColumnName");
}
/** Set Default Logic.
Default value hierarchy, separated by ;
 */
public void setDefaultValue (String DefaultValue)
{
if (DefaultValue != null && DefaultValue.length() > 60)
{
log.warning("Length > 60 - truncated");
DefaultValue = DefaultValue.substring(0,60);
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
/** Set Default Logic 2.
Default value hierarchy, separated by ;
 */
public void setDefaultValue2 (String DefaultValue2)
{
if (DefaultValue2 != null && DefaultValue2.length() > 60)
{
log.warning("Length > 60 - truncated");
DefaultValue2 = DefaultValue2.substring(0,60);
}
set_Value ("DefaultValue2", DefaultValue2);
}
/** Get Default Logic 2.
Default value hierarchy, separated by ;
 */
public String getDefaultValue2() 
{
return (String)get_Value("DefaultValue2");
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
/** Set Display Logic.
If the Field is displayed, the result determines if the field is actually displayed */
public void setDisplayLogic (String DisplayLogic)
{
if (DisplayLogic != null && DisplayLogic.length() > 1000)
{
log.warning("Length > 1000 - truncated");
DisplayLogic = DisplayLogic.substring(0,1000);
}
set_Value ("DisplayLogic", DisplayLogic);
}
/** Get Display Logic.
If the Field is displayed, the result determines if the field is actually displayed */
public String getDisplayLogic() 
{
return (String)get_Value("DisplayLogic");
}
public static final int ENTITYTYPE_AD_Reference_ID = MReference.getReferenceID("_Entity Type");
/** Applications Integrated with openXpertya = A */
public static final String ENTITYTYPE_ApplicationsIntegratedWithOpenXpertya = "A";
/** Country Version = C */
public static final String ENTITYTYPE_CountryVersion = "C";
/** Dictionary = D */
public static final String ENTITYTYPE_Dictionary = "D";
/** User maintained = U */
public static final String ENTITYTYPE_UserMaintained = "U";
/** Customization = CUST */
public static final String ENTITYTYPE_Customization = "CUST";
/** Set Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public void setEntityType (String EntityType)
{
if (EntityType.equals("A") || EntityType.equals("C") || EntityType.equals("D") || EntityType.equals("U") || EntityType.equals("CUST"));
 else throw new IllegalArgumentException ("EntityType Invalid value - Reference = ENTITYTYPE_AD_Reference_ID - A - C - D - U - CUST");
if (EntityType == null) throw new IllegalArgumentException ("EntityType is mandatory");
if (EntityType.length() > 4)
{
log.warning("Length > 4 - truncated");
EntityType = EntityType.substring(0,4);
}
set_Value ("EntityType", EntityType);
}
/** Get Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public String getEntityType() 
{
return (String)get_Value("EntityType");
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
/** Set Centrally maintained.
Information maintained in System Element table */
public void setIsCentrallyMaintained (boolean IsCentrallyMaintained)
{
set_Value ("IsCentrallyMaintained", new Boolean(IsCentrallyMaintained));
}
/** Get Centrally maintained.
Information maintained in System Element table */
public boolean isCentrallyMaintained() 
{
Object oo = get_Value("IsCentrallyMaintained");
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
/** Set Range.
The parameter is a range of values */
public void setIsRange (boolean IsRange)
{
set_Value ("IsRange", new Boolean(IsRange));
}
/** Get Range.
The parameter is a range of values */
public boolean isRange() 
{
Object oo = get_Value("IsRange");
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
/** Set Read Only Logic.
Logic to determine if field is read only (applies only when field is read-write) */
public void setReadOnlyLogic (String ReadOnlyLogic)
{
if (ReadOnlyLogic != null && ReadOnlyLogic.length() > 2000)
{
log.warning("Length > 2000 - truncated");
ReadOnlyLogic = ReadOnlyLogic.substring(0,2000);
}
set_Value ("ReadOnlyLogic", ReadOnlyLogic);
}
/** Get Read Only Logic.
Logic to determine if field is read only (applies only when field is read-write) */
public String getReadOnlyLogic() 
{
return (String)get_Value("ReadOnlyLogic");
}
/** Set Same Line */
public void setSameLine (boolean SameLine)
{
set_Value ("SameLine", new Boolean(SameLine));
}
/** Get Same Line */
public boolean isSameLine() 
{
Object oo = get_Value("SameLine");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Max. Value.
Maximum Value for a field */
public void setValueMax (String ValueMax)
{
if (ValueMax != null && ValueMax.length() > 20)
{
log.warning("Length > 20 - truncated");
ValueMax = ValueMax.substring(0,20);
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
ValueMin = ValueMin.substring(0,20);
}
set_Value ("ValueMin", ValueMin);
}
/** Get Min. Value.
Minimum Value for a field */
public String getValueMin() 
{
return (String)get_Value("ValueMin");
}
/** Set Value Format.
Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public void setVFormat (String VFormat)
{
if (VFormat != null && VFormat.length() > 20)
{
log.warning("Length > 20 - truncated");
VFormat = VFormat.substring(0,20);
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
}
