/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Val_Rule
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-03-20 16:41:00.766 */
public class X_AD_Val_Rule extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Val_Rule (Properties ctx, int AD_Val_Rule_ID, String trxName)
{
super (ctx, AD_Val_Rule_ID, trxName);
/** if (AD_Val_Rule_ID == 0)
{
setAD_Val_Rule_ID (0);
setEntityType (null);	// U
setName (null);
setType (null);
setUseType (null);	// T
}
 */
}
/** Load Constructor */
public X_AD_Val_Rule (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Val_Rule");

/** TableName=AD_Val_Rule */
public static final String Table_Name="AD_Val_Rule";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Val_Rule");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Val_Rule[").append(getID()).append("]");
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
/** Set Dynamic Validation.
Dynamic Validation Rule */
public void setAD_Val_Rule_ID (int AD_Val_Rule_ID)
{
set_ValueNoCheck ("AD_Val_Rule_ID", new Integer(AD_Val_Rule_ID));
}
/** Get Dynamic Validation.
Dynamic Validation Rule */
public int getAD_Val_Rule_ID() 
{
Integer ii = (Integer)get_Value("AD_Val_Rule_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Validation code.
Validation Code */
public void setCode (String Code)
{
if (Code != null && Code.length() > 2000)
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
if (EntityType.equals("A") || EntityType.equals("C") || EntityType.equals("D") || EntityType.equals("U") || EntityType.equals("CUST") || ( refContainsValue("CORE-AD_Reference-245", EntityType) ) );
 else throw new IllegalArgumentException ("EntityType Invalid value: " + EntityType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-245") );
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
public static final int TYPE_AD_Reference_ID = MReference.getReferenceID("AD_Validation Rule Types");
/** SQL = S */
public static final String TYPE_SQL = "S";
/** Java Language = J */
public static final String TYPE_JavaLanguage = "J";
/** Java Script = E */
public static final String TYPE_JavaScript = "E";
/** Context Logic = C */
public static final String TYPE_ContextLogic = "C";
/** Set Type.
Type of Validation (SQL, Java Script, Java Language) */
public void setType (String Type)
{
if (Type.equals("S") || Type.equals("J") || Type.equals("E") || Type.equals("C") || ( refContainsValue("CORE-AD_Reference-101", Type) ) );
 else throw new IllegalArgumentException ("Type Invalid value: " + Type + ".  Valid: " +  refValidOptions("CORE-AD_Reference-101") );
if (Type == null) throw new IllegalArgumentException ("Type is mandatory");
if (Type.length() > 1)
{
log.warning("Length > 1 - truncated");
Type = Type.substring(0,1);
}
set_Value ("Type", Type);
}
/** Get Type.
Type of Validation (SQL, Java Script, Java Language) */
public String getType() 
{
return (String)get_Value("Type");
}
public static final int USETYPE_AD_Reference_ID = MReference.getReferenceID("AD_Val_Rule Use Type");
/** Table = T */
public static final String USETYPE_Table = "T";
/** Tab = B */
public static final String USETYPE_Tab = "B";
/** Field = F */
public static final String USETYPE_Field = "F";
/** Set Use Type */
public void setUseType (String UseType)
{
if (UseType.equals("T") || UseType.equals("B") || UseType.equals("F") || ( refContainsValue("CORE-AD_Reference-1000085", UseType) ) );
 else throw new IllegalArgumentException ("UseType Invalid value: " + UseType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1000085") );
if (UseType == null) throw new IllegalArgumentException ("UseType is mandatory");
if (UseType.length() > 1)
{
log.warning("Length > 1 - truncated");
UseType = UseType.substring(0,1);
}
set_Value ("UseType", UseType);
}
/** Get Use Type */
public String getUseType() 
{
return (String)get_Value("UseType");
}
}
