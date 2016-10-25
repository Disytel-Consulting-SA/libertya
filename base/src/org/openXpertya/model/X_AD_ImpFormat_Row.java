/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ImpFormat_Row
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-25 15:17:08.978 */
public class X_AD_ImpFormat_Row extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ImpFormat_Row (Properties ctx, int AD_ImpFormat_Row_ID, String trxName)
{
super (ctx, AD_ImpFormat_Row_ID, trxName);
/** if (AD_ImpFormat_Row_ID == 0)
{
setAD_Column_ID (0);
setAD_ImpFormat_ID (0);
setAD_ImpFormat_Row_ID (0);
setDataType (null);
setDecimalPoint (null);	// .
setDivideBy100 (false);
setName (null);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM AD_ImpFormat_Row WHERE AD_ImpFormat_ID=@AD_ImpFormat_ID@
}
 */
}
/** Load Constructor */
public X_AD_ImpFormat_Row (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ImpFormat_Row");

/** TableName=AD_ImpFormat_Row */
public static final String Table_Name="AD_ImpFormat_Row";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ImpFormat_Row");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ImpFormat_Row[").append(getID()).append("]");
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
/** Set Format Field */
public void setAD_ImpFormat_Row_ID (int AD_ImpFormat_Row_ID)
{
set_ValueNoCheck ("AD_ImpFormat_Row_ID", new Integer(AD_ImpFormat_Row_ID));
}
/** Get Format Field */
public int getAD_ImpFormat_Row_ID() 
{
Integer ii = (Integer)get_Value("AD_ImpFormat_Row_ID");
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
Callout = Callout.substring(0,60);
}
set_Value ("Callout", Callout);
}
/** Get Callout.
Fully qualified class names and method - separated by semicolons */
public String getCallout() 
{
return (String)get_Value("Callout");
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
if (DecimalPoint == null) throw new IllegalArgumentException ("DecimalPoint is mandatory");
if (DecimalPoint.length() > 1)
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
/** Set Divide by 100.
Divide number by 100 to get correct amount */
public void setDivideBy100 (boolean DivideBy100)
{
set_Value ("DivideBy100", new Boolean(DivideBy100));
}
/** Get Divide by 100.
Divide number by 100 to get correct amount */
public boolean isDivideBy100() 
{
Object oo = get_Value("DivideBy100");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set End No */
public void setEndNo (int EndNo)
{
set_Value ("EndNo", new Integer(EndNo));
}
/** Get End No */
public int getEndNo() 
{
Integer ii = (Integer)get_Value("EndNo");
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
/** Set Script.
Dynamic Java Language Script to calculate result */
public void setScript (String Script)
{
if (Script != null && Script.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Script = Script.substring(0,2000);
}
set_Value ("Script", Script);
}
/** Get Script.
Dynamic Java Language Script to calculate result */
public String getScript() 
{
return (String)get_Value("Script");
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
/** Set Start No.
Starting number/position */
public void setStartNo (int StartNo)
{
set_Value ("StartNo", new Integer(StartNo));
}
/** Get Start No.
Starting number/position */
public int getStartNo() 
{
Integer ii = (Integer)get_Value("StartNo");
if (ii == null) return 0;
return ii.intValue();
}
}
