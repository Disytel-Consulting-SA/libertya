/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ExpFormat_Row
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-10-21 18:23:38.961 */
public class X_AD_ExpFormat_Row extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ExpFormat_Row (Properties ctx, int AD_ExpFormat_Row_ID, String trxName)
{
super (ctx, AD_ExpFormat_Row_ID, trxName);
/** if (AD_ExpFormat_Row_ID == 0)
{
setAD_ExpFormat_ID (0);	// @AD_ExpFormat_ID@
setAD_ExpFormat_Row_ID (0);
setDataType (null);
setIsOrderField (false);
setName (null);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM AD_ExpFormat_Row WHERE AD_ExpFormat_ID=@AD_ExpFormat_ID@
}
 */
}
/** Load Constructor */
public X_AD_ExpFormat_Row (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ExpFormat_Row");

/** TableName=AD_ExpFormat_Row */
public static final String Table_Name="AD_ExpFormat_Row";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ExpFormat_Row");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ExpFormat_Row[").append(getID()).append("]");
return sb.toString();
}
/** Set Column.
Column in the table */
public void setAD_Column_ID (int AD_Column_ID)
{
if (AD_Column_ID <= 0) set_Value ("AD_Column_ID", null);
 else 
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
/** Set Export Format */
public void setAD_ExpFormat_ID (int AD_ExpFormat_ID)
{
set_Value ("AD_ExpFormat_ID", new Integer(AD_ExpFormat_ID));
}
/** Get Export Format */
public int getAD_ExpFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_ExpFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Export Format Field.
Export Format Field */
public void setAD_ExpFormat_Row_ID (int AD_ExpFormat_Row_ID)
{
set_ValueNoCheck ("AD_ExpFormat_Row_ID", new Integer(AD_ExpFormat_Row_ID));
}
/** Get Export Format Field.
Export Format Field */
public int getAD_ExpFormat_Row_ID() 
{
Integer ii = (Integer)get_Value("AD_ExpFormat_Row_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ALIGNMENT_AD_Reference_ID = MReference.getReferenceID("Field Alignment");
/** Left = L */
public static final String ALIGNMENT_Left = "L";
/** Right = R */
public static final String ALIGNMENT_Right = "R";
/** Set Alignment.
Field alignment */
public void setAlignment (String Alignment)
{
if (Alignment == null || Alignment.equals("L") || Alignment.equals("R"));
 else throw new IllegalArgumentException ("Alignment Invalid value - Reference = ALIGNMENT_AD_Reference_ID - L - R");
if (Alignment != null && Alignment.length() > 1)
{
log.warning("Length > 1 - truncated");
Alignment = Alignment.substring(0,1);
}
set_Value ("Alignment", Alignment);
}
/** Get Alignment.
Field alignment */
public String getAlignment() 
{
return (String)get_Value("Alignment");
}
/** Set Constant Value.
Constant value */
public void setConstantValue (String ConstantValue)
{
if (ConstantValue != null && ConstantValue.length() > 60)
{
log.warning("Length > 60 - truncated");
ConstantValue = ConstantValue.substring(0,60);
}
set_Value ("ConstantValue", ConstantValue);
}
/** Get Constant Value.
Constant value */
public String getConstantValue() 
{
return (String)get_Value("ConstantValue");
}
/** Set Data Format.
Format String in Java Notation, e.g. ddMMyy */
public void setDataFormat (String DataFormat)
{
if (DataFormat != null && DataFormat.length() > 20)
{
log.warning("Length > 20 - truncated");
DataFormat = DataFormat.substring(0,20);
}
set_Value ("DataFormat", DataFormat);
}
/** Get Data Format.
Format String in Java Notation, e.g. ddMMyy */
public String getDataFormat() 
{
return (String)get_Value("DataFormat");
}
public static final int DATATYPE_AD_Reference_ID = MReference.getReferenceID("AD_ImpFormat_Row Type");
/** String = S */
public static final String DATATYPE_String = "S";
/** Number = N */
public static final String DATATYPE_Number = "N";
/** Date = D */
public static final String DATATYPE_Date = "D";
/** Constant = C */
public static final String DATATYPE_Constant = "C";
/** Set Data Type.
Type of data */
public void setDataType (String DataType)
{
if (DataType.equals("S") || DataType.equals("N") || DataType.equals("D") || DataType.equals("C"));
 else throw new IllegalArgumentException ("DataType Invalid value - Reference = DATATYPE_AD_Reference_ID - S - N - D - C");
if (DataType == null) throw new IllegalArgumentException ("DataType is mandatory");
if (DataType.length() > 1)
{
log.warning("Length > 1 - truncated");
DataType = DataType.substring(0,1);
}
set_Value ("DataType", DataType);
}
/** Get Data Type.
Type of data */
public String getDataType() 
{
return (String)get_Value("DataType");
}
/** Set Decimal Point.
Decimal Point in the data file - if any */
public void setDecimalPoint (String DecimalPoint)
{
if (DecimalPoint != null && DecimalPoint.length() > 1)
{
log.warning("Length > 1 - truncated");
DecimalPoint = DecimalPoint.substring(0,1);
}
set_Value ("DecimalPoint", DecimalPoint);
}
/** Get Decimal Point.
Decimal Point in the data file - if any */
public String getDecimalPoint() 
{
return (String)get_Value("DecimalPoint");
}
/** Set Fill Character */
public void setFillCharacter (String FillCharacter)
{
if (FillCharacter != null && FillCharacter.length() > 1)
{
log.warning("Length > 1 - truncated");
FillCharacter = FillCharacter.substring(0,1);
}
set_Value ("FillCharacter", FillCharacter);
}
/** Get Fill Character */
public String getFillCharacter() 
{
return (String)get_Value("FillCharacter");
}
/** Set Is Order Field */
public void setIsOrderField (boolean IsOrderField)
{
set_Value ("IsOrderField", new Boolean(IsOrderField));
}
/** Get Is Order Field */
public boolean isOrderField() 
{
Object oo = get_Value("IsOrderField");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Length.
Field Length */
public void setLength (int Length)
{
set_Value ("Length", new Integer(Length));
}
/** Get Length.
Field Length */
public int getLength() 
{
Integer ii = (Integer)get_Value("Length");
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
public static final int NEGATIVE_POSITION_AD_Reference_ID = MReference.getReferenceID("Negative sign position");
/** Before Number = N */
public static final String NEGATIVE_POSITION_BeforeNumber = "N";
/** Before Filling = F */
public static final String NEGATIVE_POSITION_BeforeFilling = "F";
/** Set Negative sign position */
public void setNegative_Position (String Negative_Position)
{
if (Negative_Position == null || Negative_Position.equals("N") || Negative_Position.equals("F"));
 else throw new IllegalArgumentException ("Negative_Position Invalid value - Reference = NEGATIVE_POSITION_AD_Reference_ID - N - F");
if (Negative_Position != null && Negative_Position.length() > 1)
{
log.warning("Length > 1 - truncated");
Negative_Position = Negative_Position.substring(0,1);
}
set_Value ("Negative_Position", Negative_Position);
}
/** Get Negative sign position */
public String getNegative_Position() 
{
return (String)get_Value("Negative_Position");
}
public static final int ORDERDIRECTION_AD_Reference_ID = MReference.getReferenceID("Order of a List");
/** Ascending = A */
public static final String ORDERDIRECTION_Ascending = "A";
/** Descending = D */
public static final String ORDERDIRECTION_Descending = "D";
/** Set Order.
Order Direction (Ascending or Descending) */
public void setOrderDirection (String OrderDirection)
{
if (OrderDirection == null || OrderDirection.equals("A") || OrderDirection.equals("D"));
 else throw new IllegalArgumentException ("OrderDirection Invalid value - Reference = ORDERDIRECTION_AD_Reference_ID - A - D");
if (OrderDirection != null && OrderDirection.length() > 1)
{
log.warning("Length > 1 - truncated");
OrderDirection = OrderDirection.substring(0,1);
}
set_Value ("OrderDirection", OrderDirection);
}
/** Get Order.
Order Direction (Ascending or Descending) */
public String getOrderDirection() 
{
return (String)get_Value("OrderDirection");
}
/** Set Order Seq No */
public void setOrderSeqNo (int OrderSeqNo)
{
set_Value ("OrderSeqNo", new Integer(OrderSeqNo));
}
/** Get Order Seq No */
public int getOrderSeqNo() 
{
Integer ii = (Integer)get_Value("OrderSeqNo");
if (ii == null) return 0;
return ii.intValue();
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
}
