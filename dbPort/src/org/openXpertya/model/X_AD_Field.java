/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Field
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:26.502 */
public class X_AD_Field extends PO
{
/** Constructor estándar */
public X_AD_Field (Properties ctx, int AD_Field_ID, String trxName)
{
super (ctx, AD_Field_ID, trxName);
/** if (AD_Field_ID == 0)
{
setAD_Column_ID (0);
setAD_Field_ID (0);
setAD_Tab_ID (0);
setEntityType (null);	// U
setIsCentrallyMaintained (true);	// Y
setIsDisplayed (true);	// Y
setisdisplayedingrid (false);
setIsEncrypted (false);
setIsFieldOnly (false);
setIsHeading (false);
setIsReadOnly (false);
setIsSameLine (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_Field (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=107 */
public static final int Table_ID=107;

/** TableName=AD_Field */
public static final String Table_Name="AD_Field";

protected static KeyNamePair Model = new KeyNamePair(107,"AD_Field");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Field[").append(getID()).append("]");
return sb.toString();
}
/** Set Column.
Column in the table */
public void setAD_Column_ID (int AD_Column_ID)
{
set_Value ("AD_Column_ID", new Integer(AD_Column_ID));
}
/** Get Column.
Column in the table */
public int getAD_Column_ID() 
{
Integer ii = (Integer)get_Value("AD_Column_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Field Group.
Logical grouping of fields */
public void setAD_FieldGroup_ID (int AD_FieldGroup_ID)
{
if (AD_FieldGroup_ID <= 0) set_Value ("AD_FieldGroup_ID", null);
 else 
set_Value ("AD_FieldGroup_ID", new Integer(AD_FieldGroup_ID));
}
/** Get Field Group.
Logical grouping of fields */
public int getAD_FieldGroup_ID() 
{
Integer ii = (Integer)get_Value("AD_FieldGroup_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Field.
Field on a database table */
public void setAD_Field_ID (int AD_Field_ID)
{
set_ValueNoCheck ("AD_Field_ID", new Integer(AD_Field_ID));
}
/** Get Field.
Field on a database table */
public int getAD_Field_ID() 
{
Integer ii = (Integer)get_Value("AD_Field_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tab.
Tab within a Window */
public void setAD_Tab_ID (int AD_Tab_ID)
{
set_ValueNoCheck ("AD_Tab_ID", new Integer(AD_Tab_ID));
}
/** Get Tab.
Tab within a Window */
public int getAD_Tab_ID() 
{
Integer ii = (Integer)get_Value("AD_Tab_ID");
if (ii == null) return 0;
return ii.intValue();
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
DisplayLogic = DisplayLogic.substring(0,2000);
}
set_Value ("DisplayLogic", DisplayLogic);
}
/** Get Display Logic.
If the Field is displayed, the result determines if the field is actually displayed */
public String getDisplayLogic() 
{
return (String)get_Value("DisplayLogic");
}
public static final int ENTITYTYPE_AD_Reference_ID=245;
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
 else throw new IllegalArgumentException ("EntityType Invalid value - Reference_ID=245 - A - C - D - U - CUST");
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
/** Set Displayed.
Determines, if this field is displayed */
public void setIsDisplayed (boolean IsDisplayed)
{
set_Value ("IsDisplayed", new Boolean(IsDisplayed));
}
/** Get Displayed.
Determines, if this field is displayed */
public boolean isDisplayed() 
{
Object oo = get_Value("IsDisplayed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set isdisplayedingrid */
public void setisdisplayedingrid (boolean isdisplayedingrid)
{
set_Value ("isdisplayedingrid", new Boolean(isdisplayedingrid));
}
/** Get isdisplayedingrid */
public boolean isdisplayedingrid() 
{
Object oo = get_Value("isdisplayedingrid");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
public static final int OBSCURETYPE_AD_Reference_ID=291;
/** Obscure Digits but last 4 = 904 */
public static final String OBSCURETYPE_ObscureDigitsButLast4 = "904";
/** Obscure Digits but first/last 4 = 944 */
public static final String OBSCURETYPE_ObscureDigitsButFirstLast4 = "944";
/** Obscure AlphaNumeric but first/last 4 = A44 */
public static final String OBSCURETYPE_ObscureAlphaNumericButFirstLast4 = "A44";
/** Obscure AlphaNumeric but last 4 = A04 */
public static final String OBSCURETYPE_ObscureAlphaNumericButLast4 = "A04";
/** Set Obscure.
Type of obscuring the data (limiting the display) */
public void setObscureType (String ObscureType)
{
if (ObscureType == null || ObscureType.equals("904") || ObscureType.equals("944") || ObscureType.equals("A44") || ObscureType.equals("A04"));
 else throw new IllegalArgumentException ("ObscureType Invalid value - Reference_ID=291 - 904 - 944 - A44 - A04");
if (ObscureType != null && ObscureType.length() > 3)
{
log.warning("Length > 3 - truncated");
ObscureType = ObscureType.substring(0,3);
}
set_Value ("ObscureType", ObscureType);
}
/** Get Obscure.
Type of obscuring the data (limiting the display) */
public String getObscureType() 
{
return (String)get_Value("ObscureType");
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
/** Set Record Sort No.
Determines in what order the records are displayed */
public void setSortNo (BigDecimal SortNo)
{
set_Value ("SortNo", SortNo);
}
/** Get Record Sort No.
Determines in what order the records are displayed */
public BigDecimal getSortNo() 
{
BigDecimal bd = (BigDecimal)get_Value("SortNo");
if (bd == null) return Env.ZERO;
return bd;
}
}
