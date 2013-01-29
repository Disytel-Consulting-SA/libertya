/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ReportView_Col
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:27.979 */
public class X_AD_ReportView_Col extends PO
{
/** Constructor estándar */
public X_AD_ReportView_Col (Properties ctx, int AD_ReportView_Col_ID, String trxName)
{
super (ctx, AD_ReportView_Col_ID, trxName);
/** if (AD_ReportView_Col_ID == 0)
{
setAD_ReportView_Col_ID (0);
setAD_ReportView_ID (0);
setFunctionColumn (null);
setIsGroupFunction (false);
}
 */
}
/** Load Constructor */
public X_AD_ReportView_Col (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=428 */
public static final int Table_ID=428;

/** TableName=AD_ReportView_Col */
public static final String Table_Name="AD_ReportView_Col";

protected static KeyNamePair Model = new KeyNamePair(428,"AD_ReportView_Col");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ReportView_Col[").append(getID()).append("]");
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
/** Set Report view Column */
public void setAD_ReportView_Col_ID (int AD_ReportView_Col_ID)
{
set_ValueNoCheck ("AD_ReportView_Col_ID", new Integer(AD_ReportView_Col_ID));
}
/** Get Report view Column */
public int getAD_ReportView_Col_ID() 
{
Integer ii = (Integer)get_Value("AD_ReportView_Col_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Report View.
View used to generate this report */
public void setAD_ReportView_ID (int AD_ReportView_ID)
{
set_ValueNoCheck ("AD_ReportView_ID", new Integer(AD_ReportView_ID));
}
/** Get Report View.
View used to generate this report */
public int getAD_ReportView_ID() 
{
Integer ii = (Integer)get_Value("AD_ReportView_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_ReportView_ID()));
}
/** Set Function Column.
Overwrite Column with Function  */
public void setFunctionColumn (String FunctionColumn)
{
if (FunctionColumn == null) throw new IllegalArgumentException ("FunctionColumn is mandatory");
if (FunctionColumn.length() > 60)
{
log.warning("Length > 60 - truncated");
FunctionColumn = FunctionColumn.substring(0,60);
}
set_Value ("FunctionColumn", FunctionColumn);
}
/** Get Function Column.
Overwrite Column with Function  */
public String getFunctionColumn() 
{
return (String)get_Value("FunctionColumn");
}
/** Set SQL Group Function.
This function will generate a Group By Clause */
public void setIsGroupFunction (boolean IsGroupFunction)
{
set_Value ("IsGroupFunction", new Boolean(IsGroupFunction));
}
/** Get SQL Group Function.
This function will generate a Group By Clause */
public boolean isGroupFunction() 
{
Object oo = get_Value("IsGroupFunction");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
