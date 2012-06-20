/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Attribute
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-12-02 16:44:43.532 */
public class X_M_Attribute extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Attribute (Properties ctx, int M_Attribute_ID, String trxName)
{
super (ctx, M_Attribute_ID, trxName);
/** if (M_Attribute_ID == 0)
{
setAttributeValueType (null);	// S
setIsInstanceAttribute (false);
setIsMandatory (false);
setIsReadOnly (false);
setM_Attribute_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_M_Attribute (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Attribute");

/** TableName=M_Attribute */
public static final String Table_Name="M_Attribute";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Attribute");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Attribute[").append(getID()).append("]");
return sb.toString();
}
public static final int ATTRIBUTEVALUETYPE_AD_Reference_ID = MReference.getReferenceID("M_Attribute Value Type");
/** String (max 40) = S */
public static final String ATTRIBUTEVALUETYPE_StringMax40 = "S";
/** Number = N */
public static final String ATTRIBUTEVALUETYPE_Number = "N";
/** List = L */
public static final String ATTRIBUTEVALUETYPE_List = "L";
/** Date = D */
public static final String ATTRIBUTEVALUETYPE_Date = "D";
/** Set Attribute Value Type.
Type of Attribute Value */
public void setAttributeValueType (String AttributeValueType)
{
if (AttributeValueType.equals("S") || AttributeValueType.equals("N") || AttributeValueType.equals("L") || AttributeValueType.equals("D"));
 else throw new IllegalArgumentException ("AttributeValueType Invalid value - Reference = ATTRIBUTEVALUETYPE_AD_Reference_ID - S - N - L - D");
if (AttributeValueType == null) throw new IllegalArgumentException ("AttributeValueType is mandatory");
if (AttributeValueType.length() > 1)
{
log.warning("Length > 1 - truncated");
AttributeValueType = AttributeValueType.substring(0,1);
}
set_Value ("AttributeValueType", AttributeValueType);
}
/** Get Attribute Value Type.
Type of Attribute Value */
public String getAttributeValueType() 
{
return (String)get_Value("AttributeValueType");
}
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
if (C_UOM_ID <= 0) set_Value ("C_UOM_ID", null);
 else 
set_Value ("C_UOM_ID", new Integer(C_UOM_ID));
}
/** Get UOM.
Unit of Measure */
public int getC_UOM_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_ID");
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
/** Set Instance Attribute.
The product attribute is specific to the instance (like Serial No, Lot or Guarantee Date) */
public void setIsInstanceAttribute (boolean IsInstanceAttribute)
{
set_Value ("IsInstanceAttribute", new Boolean(IsInstanceAttribute));
}
/** Get Instance Attribute.
The product attribute is specific to the instance (like Serial No, Lot or Guarantee Date) */
public boolean isInstanceAttribute() 
{
Object oo = get_Value("IsInstanceAttribute");
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
/** Set Attribute.
Product Attribute */
public void setM_Attribute_ID (int M_Attribute_ID)
{
set_ValueNoCheck ("M_Attribute_ID", new Integer(M_Attribute_ID));
}
/** Get Attribute.
Product Attribute */
public int getM_Attribute_ID() 
{
Integer ii = (Integer)get_Value("M_Attribute_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute Search.
Common Search Attribute  */
public void setM_AttributeSearch_ID (int M_AttributeSearch_ID)
{
if (M_AttributeSearch_ID <= 0) set_Value ("M_AttributeSearch_ID", null);
 else 
set_Value ("M_AttributeSearch_ID", new Integer(M_AttributeSearch_ID));
}
/** Get Attribute Search.
Common Search Attribute  */
public int getM_AttributeSearch_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSearch_ID");
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
}
