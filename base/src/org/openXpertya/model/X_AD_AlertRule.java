/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_AlertRule
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:22.0 */
public class X_AD_AlertRule extends PO
{
/** Constructor estándar */
public X_AD_AlertRule (Properties ctx, int AD_AlertRule_ID, String trxName)
{
super (ctx, AD_AlertRule_ID, trxName);
/** if (AD_AlertRule_ID == 0)
{
setAD_AlertRule_ID (0);
setAD_Alert_ID (0);
setFromClause (null);
setIsValid (true);	// Y
setName (null);
setSelectClause (null);
}
 */
}
/** Load Constructor */
public X_AD_AlertRule (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=593 */
public static final int Table_ID=593;

/** TableName=AD_AlertRule */
public static final String Table_Name="AD_AlertRule";

protected static KeyNamePair Model = new KeyNamePair(593,"AD_AlertRule");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_AlertRule[").append(getID()).append("]");
return sb.toString();
}
/** Set Alert Rule.
Definition of the alert element */
public void setAD_AlertRule_ID (int AD_AlertRule_ID)
{
set_ValueNoCheck ("AD_AlertRule_ID", new Integer(AD_AlertRule_ID));
}
/** Get Alert Rule.
Definition of the alert element */
public int getAD_AlertRule_ID() 
{
Integer ii = (Integer)get_Value("AD_AlertRule_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Alert.
openXpertya Alert */
public void setAD_Alert_ID (int AD_Alert_ID)
{
set_ValueNoCheck ("AD_Alert_ID", new Integer(AD_Alert_ID));
}
/** Get Alert.
openXpertya Alert */
public int getAD_Alert_ID() 
{
Integer ii = (Integer)get_Value("AD_Alert_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
if (AD_Table_ID <= 0) set_Value ("AD_Table_ID", null);
 else 
set_Value ("AD_Table_ID", new Integer(AD_Table_ID));
}
/** Get Table.
Table for the Fields */
public int getAD_Table_ID() 
{
Integer ii = (Integer)get_Value("AD_Table_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Error Msg */
public void setErrorMsg (String ErrorMsg)
{
if (ErrorMsg != null && ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
ErrorMsg = ErrorMsg.substring(0,1999);
}
set_Value ("ErrorMsg", ErrorMsg);
}
/** Get Error Msg */
public String getErrorMsg() 
{
return (String)get_Value("ErrorMsg");
}
/** Set Sql FROM.
SQL FROM clause */
public void setFromClause (String FromClause)
{
if (FromClause == null) throw new IllegalArgumentException ("FromClause is mandatory");
if (FromClause.length() > 2000)
{
log.warning("Length > 2000 - truncated");
FromClause = FromClause.substring(0,1999);
}
set_Value ("FromClause", FromClause);
}
/** Get Sql FROM.
SQL FROM clause */
public String getFromClause() 
{
return (String)get_Value("FromClause");
}
/** Set Valid.
Element is valid */
public void setIsValid (boolean IsValid)
{
set_Value ("IsValid", new Boolean(IsValid));
}
/** Get Valid.
Element is valid */
public boolean isValid() 
{
Object oo = get_Value("IsValid");
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
Name = Name.substring(0,59);
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
/** Set Other Clause.
Other SQL Clause */
public void setOtherClause (String OtherClause)
{
if (OtherClause != null && OtherClause.length() > 2000)
{
log.warning("Length > 2000 - truncated");
OtherClause = OtherClause.substring(0,1999);
}
set_Value ("OtherClause", OtherClause);
}
/** Get Other Clause.
Other SQL Clause */
public String getOtherClause() 
{
return (String)get_Value("OtherClause");
}
/** Set Post Processing.
Process SQL after executing the query */
public void setPostProcessing (String PostProcessing)
{
if (PostProcessing != null && PostProcessing.length() > 2000)
{
log.warning("Length > 2000 - truncated");
PostProcessing = PostProcessing.substring(0,1999);
}
set_Value ("PostProcessing", PostProcessing);
}
/** Get Post Processing.
Process SQL after executing the query */
public String getPostProcessing() 
{
return (String)get_Value("PostProcessing");
}
/** Set Pre Processing.
Process SQL before executing the query */
public void setPreProcessing (String PreProcessing)
{
if (PreProcessing != null && PreProcessing.length() > 2000)
{
log.warning("Length > 2000 - truncated");
PreProcessing = PreProcessing.substring(0,1999);
}
set_Value ("PreProcessing", PreProcessing);
}
/** Get Pre Processing.
Process SQL before executing the query */
public String getPreProcessing() 
{
return (String)get_Value("PreProcessing");
}
/** Set Sql SELECT.
SQL SELECT clause */
public void setSelectClause (String SelectClause)
{
if (SelectClause == null) throw new IllegalArgumentException ("SelectClause is mandatory");
if (SelectClause.length() > 2000)
{
log.warning("Length > 2000 - truncated");
SelectClause = SelectClause.substring(0,1999);
}
set_Value ("SelectClause", SelectClause);
}
/** Get Sql SELECT.
SQL SELECT clause */
public String getSelectClause() 
{
return (String)get_Value("SelectClause");
}
/** Set Sql WHERE.
Fully qualified SQL WHERE clause */
public void setWhereClause (String WhereClause)
{
if (WhereClause != null && WhereClause.length() > 2000)
{
log.warning("Length > 2000 - truncated");
WhereClause = WhereClause.substring(0,1999);
}
set_Value ("WhereClause", WhereClause);
}
/** Get Sql WHERE.
Fully qualified SQL WHERE clause */
public String getWhereClause() 
{
return (String)get_Value("WhereClause");
}
}
