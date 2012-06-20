/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_PrintLabelLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:27.42 */
public class X_AD_PrintLabelLine extends PO
{
/** Constructor estándar */
public X_AD_PrintLabelLine (Properties ctx, int AD_PrintLabelLine_ID, String trxName)
{
super (ctx, AD_PrintLabelLine_ID, trxName);
/** if (AD_PrintLabelLine_ID == 0)
{
setAD_LabelPrinterFunction_ID (0);
setAD_PrintLabel_ID (0);
setAD_PrintLabelLine_ID (0);
setLabelFormatType (null);	// F
setName (null);
setSeqNo (0);
setXPosition (0);
setYPosition (0);
}
 */
}
/** Load Constructor */
public X_AD_PrintLabelLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=569 */
public static final int Table_ID=569;

/** TableName=AD_PrintLabelLine */
public static final String Table_Name="AD_PrintLabelLine";

protected static KeyNamePair Model = new KeyNamePair(569,"AD_PrintLabelLine");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_PrintLabelLine[").append(getID()).append("]");
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
/** Set Label printer Function.
Function of Label Printer */
public void setAD_LabelPrinterFunction_ID (int AD_LabelPrinterFunction_ID)
{
set_Value ("AD_LabelPrinterFunction_ID", new Integer(AD_LabelPrinterFunction_ID));
}
/** Get Label printer Function.
Function of Label Printer */
public int getAD_LabelPrinterFunction_ID() 
{
Integer ii = (Integer)get_Value("AD_LabelPrinterFunction_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_PRINTFORMAT_ID_AD_Reference_ID=259;
/** Set AD_PrintFormat_ID */
public void setAD_PrintFormat_ID (int AD_PrintFormat_ID)
{
if (AD_PrintFormat_ID <= 0) set_Value ("AD_PrintFormat_ID", null);
 else 
set_Value ("AD_PrintFormat_ID", new Integer(AD_PrintFormat_ID));
}
/** Get AD_PrintFormat_ID */
public int getAD_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Print Label.
Label Format to print */
public void setAD_PrintLabel_ID (int AD_PrintLabel_ID)
{
set_ValueNoCheck ("AD_PrintLabel_ID", new Integer(AD_PrintLabel_ID));
}
/** Get Print Label.
Label Format to print */
public int getAD_PrintLabel_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintLabel_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Print Label Line.
Print Label Line Format */
public void setAD_PrintLabelLine_ID (int AD_PrintLabelLine_ID)
{
set_ValueNoCheck ("AD_PrintLabelLine_ID", new Integer(AD_PrintLabelLine_ID));
}
/** Get Print Label Line.
Print Label Line Format */
public int getAD_PrintLabelLine_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintLabelLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int LABELFORMATTYPE_AD_Reference_ID=280;
/** PrintFormat = I */
public static final String LABELFORMATTYPE_PrintFormat = "I";
/** Print Total NUMERIC = L */
public static final String LABELFORMATTYPE_PrintTotalNUMERIC = "L";
/** Set Label Quantity = Q */
public static final String LABELFORMATTYPE_SetLabelQuantity = "Q";
/** Function = F */
public static final String LABELFORMATTYPE_Function = "F";
/** Text = T */
public static final String LABELFORMATTYPE_Text = "T";
/** Set Label Format Type.
Label Format Type */
public void setLabelFormatType (String LabelFormatType)
{
if (LabelFormatType.equals("I") || LabelFormatType.equals("L") || LabelFormatType.equals("Q") || LabelFormatType.equals("F") || LabelFormatType.equals("T"));
 else throw new IllegalArgumentException ("LabelFormatType Invalid value - Reference_ID=280 - I - L - Q - F - T");
if (LabelFormatType == null) throw new IllegalArgumentException ("LabelFormatType is mandatory");
if (LabelFormatType.length() > 1)
{
log.warning("Length > 1 - truncated");
LabelFormatType = LabelFormatType.substring(0,1);
}
set_Value ("LabelFormatType", LabelFormatType);
}
/** Get Label Format Type.
Label Format Type */
public String getLabelFormatType() 
{
return (String)get_Value("LabelFormatType");
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
/** Set Print Text.
The label text to be printed on a document or correspondence. */
public void setPrintName (String PrintName)
{
if (PrintName != null && PrintName.length() > 60)
{
log.warning("Length > 60 - truncated");
PrintName = PrintName.substring(0,60);
}
set_Value ("PrintName", PrintName);
}
/** Get Print Text.
The label text to be printed on a document or correspondence. */
public String getPrintName() 
{
return (String)get_Value("PrintName");
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getSeqNo()));
}
/** Set X Position.
Absolute X (horizontal) position in 1/72 of an inch */
public void setXPosition (int XPosition)
{
set_Value ("XPosition", new Integer(XPosition));
}
/** Get X Position.
Absolute X (horizontal) position in 1/72 of an inch */
public int getXPosition() 
{
Integer ii = (Integer)get_Value("XPosition");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Y Position.
Absolute Y (vertical) position in 1/72 of an inch */
public void setYPosition (int YPosition)
{
set_Value ("YPosition", new Integer(YPosition));
}
/** Get Y Position.
Absolute Y (vertical) position in 1/72 of an inch */
public int getYPosition() 
{
Integer ii = (Integer)get_Value("YPosition");
if (ii == null) return 0;
return ii.intValue();
}
}
