/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_PrintFormatItem
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-05-27 13:29:42.1 */
public class X_AD_PrintFormatItem extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_PrintFormatItem (Properties ctx, int AD_PrintFormatItem_ID, String trxName)
{
super (ctx, AD_PrintFormatItem_ID, trxName);
/** if (AD_PrintFormatItem_ID == 0)
{
setAD_Column_ID (0);
setAD_PrintFormatChild_ID (0);
setAD_PrintFormat_ID (0);
setAD_PrintFormatItem_ID (0);
setFieldAlignmentType (null);	// D
setImageIsAttached (false);
setIsAveraged (false);
setIsCentrallyMaintained (false);
setIsCounted (false);
setIsDeviationCalc (false);
setIsFilledRectangle (false);	// N
setIsFixedWidth (false);
setIsGroupBy (false);
setIsHeightOneLine (true);	// Y
setIsImageField (false);
setIsMaxCalc (false);
setIsMinCalc (false);
setIsNextLine (true);	// Y
setIsNextPage (false);
setIsOrderBy (false);
setIsPageBreak (false);
setIsPrinted (true);	// Y
setIsRelativePosition (true);	// Y
setIsRunningTotal (false);
setIsSetNLPosition (false);
setIsSummarized (false);
setIsSuppressNull (false);
setIsVarianceCalc (false);
setLineAlignmentType (null);	// X
setMaxHeight (0);
setMaxWidth (0);
setName (null);
setPrintAreaType (null);	// C
setPrintFormatType (null);	// F
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM AD_PrintFormatItem WHERE AD_PrintFormat_ID=@AD_PrintFormat_ID@
setSortNo (0);
setXPosition (0);
setXSpace (0);
setYPosition (0);
setYSpace (0);
}
 */
}
/** Load Constructor */
public X_AD_PrintFormatItem (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_PrintFormatItem");

/** TableName=AD_PrintFormatItem */
public static final String Table_Name="AD_PrintFormatItem";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_PrintFormatItem");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_PrintFormatItem[").append(getID()).append("]");
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
/** Set Print Color.
Color used for printing and display */
public void setAD_PrintColor_ID (int AD_PrintColor_ID)
{
if (AD_PrintColor_ID <= 0) set_Value ("AD_PrintColor_ID", null);
 else 
set_Value ("AD_PrintColor_ID", new Integer(AD_PrintColor_ID));
}
/** Get Print Color.
Color used for printing and display */
public int getAD_PrintColor_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintColor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Print Font.
Maintain Print Font */
public void setAD_PrintFont_ID (int AD_PrintFont_ID)
{
if (AD_PrintFont_ID <= 0) set_Value ("AD_PrintFont_ID", null);
 else 
set_Value ("AD_PrintFont_ID", new Integer(AD_PrintFont_ID));
}
/** Get Print Font.
Maintain Print Font */
public int getAD_PrintFont_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintFont_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_PRINTFORMATCHILD_ID_AD_Reference_ID = MReference.getReferenceID("AD_PrintFormat");
/** Set Included Print Format.
Print format that is included here. */
public void setAD_PrintFormatChild_ID (int AD_PrintFormatChild_ID)
{
set_Value ("AD_PrintFormatChild_ID", new Integer(AD_PrintFormatChild_ID));
}
/** Get Included Print Format.
Print format that is included here. */
public int getAD_PrintFormatChild_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintFormatChild_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Print Format.
Data Print Format */
public void setAD_PrintFormat_ID (int AD_PrintFormat_ID)
{
set_ValueNoCheck ("AD_PrintFormat_ID", new Integer(AD_PrintFormat_ID));
}
/** Get Print Format.
Data Print Format */
public int getAD_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Print Format Item.
Item/Column in the Print format */
public void setAD_PrintFormatItem_ID (int AD_PrintFormatItem_ID)
{
set_ValueNoCheck ("AD_PrintFormatItem_ID", new Integer(AD_PrintFormatItem_ID));
}
/** Get Print Format Item.
Item/Column in the Print format */
public int getAD_PrintFormatItem_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintFormatItem_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Graph.
Graph included in Reports */
public void setAD_PrintGraph_ID (int AD_PrintGraph_ID)
{
if (AD_PrintGraph_ID <= 0) set_Value ("AD_PrintGraph_ID", null);
 else 
set_Value ("AD_PrintGraph_ID", new Integer(AD_PrintGraph_ID));
}
/** Get Graph.
Graph included in Reports */
public int getAD_PrintGraph_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintGraph_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Arc Diameter.
Arc Diameter for rounded Rectangles */
public void setArcDiameter (int ArcDiameter)
{
set_Value ("ArcDiameter", new Integer(ArcDiameter));
}
/** Get Arc Diameter.
Arc Diameter for rounded Rectangles */
public int getArcDiameter() 
{
Integer ii = (Integer)get_Value("ArcDiameter");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Below Column.
Print this column below the column index entered */
public void setBelowColumn (int BelowColumn)
{
set_Value ("BelowColumn", new Integer(BelowColumn));
}
/** Get Below Column.
Print this column below the column index entered */
public int getBelowColumn() 
{
Integer ii = (Integer)get_Value("BelowColumn");
if (ii == null) return 0;
return ii.intValue();
}
public static final int FIELDALIGNMENTTYPE_AD_Reference_ID = MReference.getReferenceID("AD_Print Field Alignment");
/** Default = D */
public static final String FIELDALIGNMENTTYPE_Default = "D";
/** Leading (left) = L */
public static final String FIELDALIGNMENTTYPE_LeadingLeft = "L";
/** Trailing (right) = T */
public static final String FIELDALIGNMENTTYPE_TrailingRight = "T";
/** Block = B */
public static final String FIELDALIGNMENTTYPE_Block = "B";
/** Center = C */
public static final String FIELDALIGNMENTTYPE_Center = "C";
/** Set Field Alignment.
Field Text Alignment */
public void setFieldAlignmentType (String FieldAlignmentType)
{
if (FieldAlignmentType.equals("D") || FieldAlignmentType.equals("L") || FieldAlignmentType.equals("T") || FieldAlignmentType.equals("B") || FieldAlignmentType.equals("C"));
 else throw new IllegalArgumentException ("FieldAlignmentType Invalid value - Reference = FIELDALIGNMENTTYPE_AD_Reference_ID - D - L - T - B - C");
if (FieldAlignmentType == null) throw new IllegalArgumentException ("FieldAlignmentType is mandatory");
if (FieldAlignmentType.length() > 1)
{
log.warning("Length > 1 - truncated");
FieldAlignmentType = FieldAlignmentType.substring(0,1);
}
set_Value ("FieldAlignmentType", FieldAlignmentType);
}
/** Get Field Alignment.
Field Text Alignment */
public String getFieldAlignmentType() 
{
return (String)get_Value("FieldAlignmentType");
}
/** Set Image attached.
The image to be printed is attached to the record */
public void setImageIsAttached (boolean ImageIsAttached)
{
set_Value ("ImageIsAttached", new Boolean(ImageIsAttached));
}
/** Get Image attached.
The image to be printed is attached to the record */
public boolean isImageIsAttached() 
{
Object oo = get_Value("ImageIsAttached");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Image URL.
URL of  image */
public void setImageURL (String ImageURL)
{
if (ImageURL != null && ImageURL.length() > 120)
{
log.warning("Length > 120 - truncated");
ImageURL = ImageURL.substring(0,120);
}
set_Value ("ImageURL", ImageURL);
}
/** Get Image URL.
URL of  image */
public String getImageURL() 
{
return (String)get_Value("ImageURL");
}
/** Set Calculate Mean (μ).
Calculate Average of numeric content or length */
public void setIsAveraged (boolean IsAveraged)
{
set_Value ("IsAveraged", new Boolean(IsAveraged));
}
/** Get Calculate Mean (μ).
Calculate Average of numeric content or length */
public boolean isAveraged() 
{
Object oo = get_Value("IsAveraged");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Calculate Count (№).
Count number of not empty elements */
public void setIsCounted (boolean IsCounted)
{
set_Value ("IsCounted", new Boolean(IsCounted));
}
/** Get Calculate Count (№).
Count number of not empty elements */
public boolean isCounted() 
{
Object oo = get_Value("IsCounted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Calculate Deviation (σ).
Calculate Standard Deviation */
public void setIsDeviationCalc (boolean IsDeviationCalc)
{
set_Value ("IsDeviationCalc", new Boolean(IsDeviationCalc));
}
/** Get Calculate Deviation (σ).
Calculate Standard Deviation */
public boolean isDeviationCalc() 
{
Object oo = get_Value("IsDeviationCalc");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Fill Shape.
Fill the shape with the color selected */
public void setIsFilledRectangle (boolean IsFilledRectangle)
{
set_Value ("IsFilledRectangle", new Boolean(IsFilledRectangle));
}
/** Get Fill Shape.
Fill the shape with the color selected */
public boolean isFilledRectangle() 
{
Object oo = get_Value("IsFilledRectangle");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Fixed Width.
Column has a fixed width */
public void setIsFixedWidth (boolean IsFixedWidth)
{
set_Value ("IsFixedWidth", new Boolean(IsFixedWidth));
}
/** Get Fixed Width.
Column has a fixed width */
public boolean isFixedWidth() 
{
Object oo = get_Value("IsFixedWidth");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Group by.
After a group change, totals, etc. are printed */
public void setIsGroupBy (boolean IsGroupBy)
{
set_Value ("IsGroupBy", new Boolean(IsGroupBy));
}
/** Get Group by.
After a group change, totals, etc. are printed */
public boolean isGroupBy() 
{
Object oo = get_Value("IsGroupBy");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set One Line Only.
If selected, only one line is printed */
public void setIsHeightOneLine (boolean IsHeightOneLine)
{
set_Value ("IsHeightOneLine", new Boolean(IsHeightOneLine));
}
/** Get One Line Only.
If selected, only one line is printed */
public boolean isHeightOneLine() 
{
Object oo = get_Value("IsHeightOneLine");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Image Field.
The image is retrieved from the data column */
public void setIsImageField (boolean IsImageField)
{
set_Value ("IsImageField", new Boolean(IsImageField));
}
/** Get Image Field.
The image is retrieved from the data column */
public boolean isImageField() 
{
Object oo = get_Value("IsImageField");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Calculate Maximim (↑).
Calculate the maximim amount */
public void setIsMaxCalc (boolean IsMaxCalc)
{
set_Value ("IsMaxCalc", new Boolean(IsMaxCalc));
}
/** Get Calculate Maximim (↑).
Calculate the maximim amount */
public boolean isMaxCalc() 
{
Object oo = get_Value("IsMaxCalc");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Calculate Minimum (↓).
Calculate the minimum amount */
public void setIsMinCalc (boolean IsMinCalc)
{
set_Value ("IsMinCalc", new Boolean(IsMinCalc));
}
/** Get Calculate Minimum (↓).
Calculate the minimum amount */
public boolean isMinCalc() 
{
Object oo = get_Value("IsMinCalc");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Next Line.
Print item on next line */
public void setIsNextLine (boolean IsNextLine)
{
set_Value ("IsNextLine", new Boolean(IsNextLine));
}
/** Get Next Line.
Print item on next line */
public boolean isNextLine() 
{
Object oo = get_Value("IsNextLine");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Next Page.
The column is printed on the next page */
public void setIsNextPage (boolean IsNextPage)
{
set_Value ("IsNextPage", new Boolean(IsNextPage));
}
/** Get Next Page.
The column is printed on the next page */
public boolean isNextPage() 
{
Object oo = get_Value("IsNextPage");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Order by.
Include in sort order */
public void setIsOrderBy (boolean IsOrderBy)
{
set_Value ("IsOrderBy", new Boolean(IsOrderBy));
}
/** Get Order by.
Include in sort order */
public boolean isOrderBy() 
{
Object oo = get_Value("IsOrderBy");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Page break.
Start with new page */
public void setIsPageBreak (boolean IsPageBreak)
{
set_Value ("IsPageBreak", new Boolean(IsPageBreak));
}
/** Get Page break.
Start with new page */
public boolean isPageBreak() 
{
Object oo = get_Value("IsPageBreak");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Printed.
Indicates if this document / line is printed */
public void setIsPrinted (boolean IsPrinted)
{
set_Value ("IsPrinted", new Boolean(IsPrinted));
}
/** Get Printed.
Indicates if this document / line is printed */
public boolean isPrinted() 
{
Object oo = get_Value("IsPrinted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Relative Position.
The item is relative positioned (not absolute) */
public void setIsRelativePosition (boolean IsRelativePosition)
{
set_Value ("IsRelativePosition", new Boolean(IsRelativePosition));
}
/** Get Relative Position.
The item is relative positioned (not absolute) */
public boolean isRelativePosition() 
{
Object oo = get_Value("IsRelativePosition");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Running Total.
Create a running total (sum) */
public void setIsRunningTotal (boolean IsRunningTotal)
{
set_Value ("IsRunningTotal", new Boolean(IsRunningTotal));
}
/** Get Running Total.
Create a running total (sum) */
public boolean isRunningTotal() 
{
Object oo = get_Value("IsRunningTotal");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Set NL Position.
Set New Line Position */
public void setIsSetNLPosition (boolean IsSetNLPosition)
{
set_Value ("IsSetNLPosition", new Boolean(IsSetNLPosition));
}
/** Get Set NL Position.
Set New Line Position */
public boolean isSetNLPosition() 
{
Object oo = get_Value("IsSetNLPosition");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Calculate Sum (Σ).
Calculate the Sum of numeric content or length */
public void setIsSummarized (boolean IsSummarized)
{
set_Value ("IsSummarized", new Boolean(IsSummarized));
}
/** Get Calculate Sum (Σ).
Calculate the Sum of numeric content or length */
public boolean isSummarized() 
{
Object oo = get_Value("IsSummarized");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Suppress Null.
Suppress columns or elements with NULL value */
public void setIsSuppressNull (boolean IsSuppressNull)
{
set_Value ("IsSuppressNull", new Boolean(IsSuppressNull));
}
/** Get Suppress Null.
Suppress columns or elements with NULL value */
public boolean isSuppressNull() 
{
Object oo = get_Value("IsSuppressNull");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Calculate Variance (σ²).
Calculate Variance */
public void setIsVarianceCalc (boolean IsVarianceCalc)
{
set_Value ("IsVarianceCalc", new Boolean(IsVarianceCalc));
}
/** Get Calculate Variance (σ²).
Calculate Variance */
public boolean isVarianceCalc() 
{
Object oo = get_Value("IsVarianceCalc");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int LINEALIGNMENTTYPE_AD_Reference_ID = MReference.getReferenceID("AD_Print Line Alignment");
/** None = X */
public static final String LINEALIGNMENTTYPE_None = "X";
/** Leading (left) = L */
public static final String LINEALIGNMENTTYPE_LeadingLeft = "L";
/** Center = C */
public static final String LINEALIGNMENTTYPE_Center = "C";
/** Trailing (right) = T */
public static final String LINEALIGNMENTTYPE_TrailingRight = "T";
/** Set Line Alignment.
Line Alignment */
public void setLineAlignmentType (String LineAlignmentType)
{
if (LineAlignmentType.equals("X") || LineAlignmentType.equals("L") || LineAlignmentType.equals("C") || LineAlignmentType.equals("T"));
 else throw new IllegalArgumentException ("LineAlignmentType Invalid value - Reference = LINEALIGNMENTTYPE_AD_Reference_ID - X - L - C - T");
if (LineAlignmentType == null) throw new IllegalArgumentException ("LineAlignmentType is mandatory");
if (LineAlignmentType.length() > 1)
{
log.warning("Length > 1 - truncated");
LineAlignmentType = LineAlignmentType.substring(0,1);
}
set_Value ("LineAlignmentType", LineAlignmentType);
}
/** Get Line Alignment.
Line Alignment */
public String getLineAlignmentType() 
{
return (String)get_Value("LineAlignmentType");
}
/** Set Line Width.
Width of the lines */
public void setLineWidth (int LineWidth)
{
set_Value ("LineWidth", new Integer(LineWidth));
}
/** Get Line Width.
Width of the lines */
public int getLineWidth() 
{
Integer ii = (Integer)get_Value("LineWidth");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Max Height.
Maximum Height in 1/72 if an inch - 0 = no restriction */
public void setMaxHeight (int MaxHeight)
{
set_Value ("MaxHeight", new Integer(MaxHeight));
}
/** Get Max Height.
Maximum Height in 1/72 if an inch - 0 = no restriction */
public int getMaxHeight() 
{
Integer ii = (Integer)get_Value("MaxHeight");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Max Width.
Maximum Width in 1/72 if an inch - 0 = no restriction */
public void setMaxWidth (int MaxWidth)
{
set_Value ("MaxWidth", new Integer(MaxWidth));
}
/** Get Max Width.
Maximum Width in 1/72 if an inch - 0 = no restriction */
public int getMaxWidth() 
{
Integer ii = (Integer)get_Value("MaxWidth");
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
/** Set Override Value */
public void setOverrideValue (String OverrideValue)
{
if (OverrideValue != null && OverrideValue.length() > 100)
{
log.warning("Length > 100 - truncated");
OverrideValue = OverrideValue.substring(0,100);
}
set_Value ("OverrideValue", OverrideValue);
}
/** Get Override Value */
public String getOverrideValue() 
{
return (String)get_Value("OverrideValue");
}
public static final int PRINTAREATYPE_AD_Reference_ID = MReference.getReferenceID("AD_Print Area");
/** Content = C */
public static final String PRINTAREATYPE_Content = "C";
/** Header = H */
public static final String PRINTAREATYPE_Header = "H";
/** Footer = F */
public static final String PRINTAREATYPE_Footer = "F";
/** Set Area.
Print Area */
public void setPrintAreaType (String PrintAreaType)
{
if (PrintAreaType.equals("C") || PrintAreaType.equals("H") || PrintAreaType.equals("F"));
 else throw new IllegalArgumentException ("PrintAreaType Invalid value - Reference = PRINTAREATYPE_AD_Reference_ID - C - H - F");
if (PrintAreaType == null) throw new IllegalArgumentException ("PrintAreaType is mandatory");
if (PrintAreaType.length() > 1)
{
log.warning("Length > 1 - truncated");
PrintAreaType = PrintAreaType.substring(0,1);
}
set_Value ("PrintAreaType", PrintAreaType);
}
/** Get Area.
Print Area */
public String getPrintAreaType() 
{
return (String)get_Value("PrintAreaType");
}
public static final int PRINTFORMATTYPE_AD_Reference_ID = MReference.getReferenceID("AD_Print Format Type");
/** Rectangle = R */
public static final String PRINTFORMATTYPE_Rectangle = "R";
/** Line = L */
public static final String PRINTFORMATTYPE_Line = "L";
/** Image = I */
public static final String PRINTFORMATTYPE_Image = "I";
/** Field = F */
public static final String PRINTFORMATTYPE_Field = "F";
/** Text = T */
public static final String PRINTFORMATTYPE_Text = "T";
/** Print Format = P */
public static final String PRINTFORMATTYPE_PrintFormat = "P";
/** Set Format Type.
Print Format Type */
public void setPrintFormatType (String PrintFormatType)
{
if (PrintFormatType.equals("R") || PrintFormatType.equals("L") || PrintFormatType.equals("I") || PrintFormatType.equals("F") || PrintFormatType.equals("T") || PrintFormatType.equals("P"));
 else throw new IllegalArgumentException ("PrintFormatType Invalid value - Reference = PRINTFORMATTYPE_AD_Reference_ID - R - L - I - F - T - P");
if (PrintFormatType == null) throw new IllegalArgumentException ("PrintFormatType is mandatory");
if (PrintFormatType.length() > 1)
{
log.warning("Length > 1 - truncated");
PrintFormatType = PrintFormatType.substring(0,1);
}
set_Value ("PrintFormatType", PrintFormatType);
}
/** Get Format Type.
Print Format Type */
public String getPrintFormatType() 
{
return (String)get_Value("PrintFormatType");
}
/** Set Print Text.
The label text to be printed on a document or correspondence. */
public void setPrintName (String PrintName)
{
if (PrintName != null && PrintName.length() > 2000)
{
log.warning("Length > 2000 - truncated");
PrintName = PrintName.substring(0,2000);
}
set_Value ("PrintName", PrintName);
}
/** Get Print Text.
The label text to be printed on a document or correspondence. */
public String getPrintName() 
{
return (String)get_Value("PrintName");
}
/** Set Print Label Suffix.
The label text to be printed on a document or correspondence after the field */
public void setPrintNameSuffix (String PrintNameSuffix)
{
if (PrintNameSuffix != null && PrintNameSuffix.length() > 60)
{
log.warning("Length > 60 - truncated");
PrintNameSuffix = PrintNameSuffix.substring(0,60);
}
set_Value ("PrintNameSuffix", PrintNameSuffix);
}
/** Get Print Label Suffix.
The label text to be printed on a document or correspondence after the field */
public String getPrintNameSuffix() 
{
return (String)get_Value("PrintNameSuffix");
}
/** Set Running Total Lines.
Create Running Total Lines (page break) every x lines */
public void setRunningTotalLines (int RunningTotalLines)
{
set_Value ("RunningTotalLines", new Integer(RunningTotalLines));
}
/** Get Running Total Lines.
Create Running Total Lines (page break) every x lines */
public int getRunningTotalLines() 
{
Integer ii = (Integer)get_Value("RunningTotalLines");
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
public static final int SHAPETYPE_AD_Reference_ID = MReference.getReferenceID("AD_PrintFormatItem ShapeType");
/** 3D Rectangle = 3 */
public static final String SHAPETYPE_3DRectangle = "3";
/** Oval = O */
public static final String SHAPETYPE_Oval = "O";
/** Round Rectangle = R */
public static final String SHAPETYPE_RoundRectangle = "R";
/** Normal Rectangle = N */
public static final String SHAPETYPE_NormalRectangle = "N";
/** Set Shape Type.
Type of the shape to be painted */
public void setShapeType (String ShapeType)
{
if (ShapeType == null || ShapeType.equals("3") || ShapeType.equals("O") || ShapeType.equals("R") || ShapeType.equals("N"));
 else throw new IllegalArgumentException ("ShapeType Invalid value - Reference = SHAPETYPE_AD_Reference_ID - 3 - O - R - N");
if (ShapeType != null && ShapeType.length() > 1)
{
log.warning("Length > 1 - truncated");
ShapeType = ShapeType.substring(0,1);
}
set_Value ("ShapeType", ShapeType);
}
/** Get Shape Type.
Type of the shape to be painted */
public String getShapeType() 
{
return (String)get_Value("ShapeType");
}
/** Set Record Sort No.
Determines in what order the records are displayed */
public void setSortNo (int SortNo)
{
set_Value ("SortNo", new Integer(SortNo));
}
/** Get Record Sort No.
Determines in what order the records are displayed */
public int getSortNo() 
{
Integer ii = (Integer)get_Value("SortNo");
if (ii == null) return 0;
return ii.intValue();
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
/** Set X Space.
Relative X (horizontal) space in 1/72 of an inch */
public void setXSpace (int XSpace)
{
set_Value ("XSpace", new Integer(XSpace));
}
/** Get X Space.
Relative X (horizontal) space in 1/72 of an inch */
public int getXSpace() 
{
Integer ii = (Integer)get_Value("XSpace");
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
/** Set Y Space.
Relative Y (vertical) space in 1/72 of an inch */
public void setYSpace (int YSpace)
{
set_Value ("YSpace", new Integer(YSpace));
}
/** Get Y Space.
Relative Y (vertical) space in 1/72 of an inch */
public int getYSpace() 
{
Integer ii = (Integer)get_Value("YSpace");
if (ii == null) return 0;
return ii.intValue();
}
}
