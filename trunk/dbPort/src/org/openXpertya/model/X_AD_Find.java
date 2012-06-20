/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Find
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:22.937 */
public class X_AD_Find extends PO
{
/** Constructor estándar */
public X_AD_Find (Properties ctx, int AD_Find_ID, String trxName)
{
super (ctx, AD_Find_ID, trxName);
/** if (AD_Find_ID == 0)
{
setAD_Column_ID (0);
setAD_Find_ID (0);
setAndOr (null);	// A
setFind_ID (Env.ZERO);
setOperation (null);	// ==
setValue (null);
}
 */
}
/** Load Constructor */
public X_AD_Find (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=404 */
public static final int Table_ID=404;

/** TableName=AD_Find */
public static final String Table_Name="AD_Find";

protected static KeyNamePair Model = new KeyNamePair(404,"AD_Find");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Find[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_COLUMN_ID_AD_Reference_ID=251;
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
/** Set Find */
public void setAD_Find_ID (int AD_Find_ID)
{
set_ValueNoCheck ("AD_Find_ID", new Integer(AD_Find_ID));
}
/** Get Find */
public int getAD_Find_ID() 
{
Integer ii = (Integer)get_Value("AD_Find_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_Find_ID()));
}
public static final int ANDOR_AD_Reference_ID=204;
/** And = A */
public static final String ANDOR_And = "A";
/** Or = O */
public static final String ANDOR_Or = "O";
/** Set And/Or.
Logical operation: AND or OR */
public void setAndOr (String AndOr)
{
if (AndOr.equals("A") || AndOr.equals("O"));
 else throw new IllegalArgumentException ("AndOr Invalid value - Reference_ID=204 - A - O");
if (AndOr == null) throw new IllegalArgumentException ("AndOr is mandatory");
if (AndOr.length() > 1)
{
log.warning("Length > 1 - truncated");
AndOr = AndOr.substring(0,0);
}
set_Value ("AndOr", AndOr);
}
/** Get And/Or.
Logical operation: AND or OR */
public String getAndOr() 
{
return (String)get_Value("AndOr");
}
/** Set Find_ID */
public void setFind_ID (BigDecimal Find_ID)
{
if (Find_ID == null) throw new IllegalArgumentException ("Find_ID is mandatory");
set_Value ("Find_ID", Find_ID);
}
/** Get Find_ID */
public BigDecimal getFind_ID() 
{
BigDecimal bd = (BigDecimal)get_Value("Find_ID");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int OPERATION_AD_Reference_ID=205;
/** != = != */
public static final String OPERATION_NotEq = "!=";
/**  = = == */
public static final String OPERATION_Eq = "==";
/** >= = >= */
public static final String OPERATION_GtEq = ">=";
/** > = >> */
public static final String OPERATION_Gt = ">>";
/** < = << */
public static final String OPERATION_Le = "<<";
/**  ~ = ~~ */
public static final String OPERATION_Like = "~~";
/** <= = <= */
public static final String OPERATION_LeEq = "<=";
/** |<x>| = AB */
public static final String OPERATION_X = "AB";
/** sql = SQ */
public static final String OPERATION_Sql = "SQ";
/** Set Operation.
Compare Operation */
public void setOperation (String Operation)
{
if (Operation.equals("!=") || Operation.equals("==") || Operation.equals(">=") || Operation.equals(">>") || Operation.equals("<<") || Operation.equals("~~") || Operation.equals("<=") || Operation.equals("AB") || Operation.equals("SQ"));
 else throw new IllegalArgumentException ("Operation Invalid value - Reference_ID=205 - != - == - >= - >> - << - ~~ - <= - AB - SQ");
if (Operation == null) throw new IllegalArgumentException ("Operation is mandatory");
if (Operation.length() > 2)
{
log.warning("Length > 2 - truncated");
Operation = Operation.substring(0,1);
}
set_Value ("Operation", Operation);
}
/** Get Operation.
Compare Operation */
public String getOperation() 
{
return (String)get_Value("Operation");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,39);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
/** Set Value To.
Value To */
public void setValue2 (String Value2)
{
if (Value2 != null && Value2.length() > 40)
{
log.warning("Length > 40 - truncated");
Value2 = Value2.substring(0,39);
}
set_Value ("Value2", Value2);
}
/** Get Value To.
Value To */
public String getValue2() 
{
return (String)get_Value("Value2");
}
}
