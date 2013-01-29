/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_LabelPrinterFunction
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:26.845 */
public class X_AD_LabelPrinterFunction extends PO
{
/** Constructor estándar */
public X_AD_LabelPrinterFunction (Properties ctx, int AD_LabelPrinterFunction_ID, String trxName)
{
super (ctx, AD_LabelPrinterFunction_ID, trxName);
/** if (AD_LabelPrinterFunction_ID == 0)
{
setAD_LabelPrinterFunction_ID (0);
setAD_LabelPrinter_ID (0);
setIsXYPosition (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_AD_LabelPrinterFunction (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=624 */
public static final int Table_ID=624;

/** TableName=AD_LabelPrinterFunction */
public static final String Table_Name="AD_LabelPrinterFunction";

protected static KeyNamePair Model = new KeyNamePair(624,"AD_LabelPrinterFunction");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_LabelPrinterFunction[").append(getID()).append("]");
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
/** Set Label printer Function.
Function of Label Printer */
public void setAD_LabelPrinterFunction_ID (int AD_LabelPrinterFunction_ID)
{
set_ValueNoCheck ("AD_LabelPrinterFunction_ID", new Integer(AD_LabelPrinterFunction_ID));
}
/** Get Label printer Function.
Function of Label Printer */
public int getAD_LabelPrinterFunction_ID() 
{
Integer ii = (Integer)get_Value("AD_LabelPrinterFunction_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Label printer.
Label Printer Definition */
public void setAD_LabelPrinter_ID (int AD_LabelPrinter_ID)
{
set_ValueNoCheck ("AD_LabelPrinter_ID", new Integer(AD_LabelPrinter_ID));
}
/** Get Label printer.
Label Printer Definition */
public int getAD_LabelPrinter_ID() 
{
Integer ii = (Integer)get_Value("AD_LabelPrinter_ID");
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
/** Set Function Prefix.
Data sent before the function */
public void setFunctionPrefix (String FunctionPrefix)
{
if (FunctionPrefix != null && FunctionPrefix.length() > 40)
{
log.warning("Length > 40 - truncated");
FunctionPrefix = FunctionPrefix.substring(0,40);
}
set_Value ("FunctionPrefix", FunctionPrefix);
}
/** Get Function Prefix.
Data sent before the function */
public String getFunctionPrefix() 
{
return (String)get_Value("FunctionPrefix");
}
/** Set Function Suffix.
Data sent after the function */
public void setFunctionSuffix (String FunctionSuffix)
{
if (FunctionSuffix != null && FunctionSuffix.length() > 40)
{
log.warning("Length > 40 - truncated");
FunctionSuffix = FunctionSuffix.substring(0,40);
}
set_Value ("FunctionSuffix", FunctionSuffix);
}
/** Get Function Suffix.
Data sent after the function */
public String getFunctionSuffix() 
{
return (String)get_Value("FunctionSuffix");
}
/** Set functiontype */
public void setfunctiontype (String functiontype)
{
if (functiontype != null && functiontype.length() > 255)
{
log.warning("Length > 255 - truncated");
functiontype = functiontype.substring(0,255);
}
set_Value ("functiontype", functiontype);
}
/** Get functiontype */
public String getfunctiontype() 
{
return (String)get_Value("functiontype");
}
/** Set XY Position.
The Function is XY position */
public void setIsXYPosition (boolean IsXYPosition)
{
set_Value ("IsXYPosition", new Boolean(IsXYPosition));
}
/** Get XY Position.
The Function is XY position */
public boolean isXYPosition() 
{
Object oo = get_Value("IsXYPosition");
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
/** Set XY Separator.
The separator between the X and Y function. */
public void setXYSeparator (String XYSeparator)
{
if (XYSeparator != null && XYSeparator.length() > 20)
{
log.warning("Length > 20 - truncated");
XYSeparator = XYSeparator.substring(0,20);
}
set_Value ("XYSeparator", XYSeparator);
}
/** Get XY Separator.
The separator between the X and Y function. */
public String getXYSeparator() 
{
return (String)get_Value("XYSeparator");
}
}
