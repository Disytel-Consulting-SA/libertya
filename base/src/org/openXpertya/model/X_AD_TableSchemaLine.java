/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_TableSchemaLine
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-11-02 14:16:54.593 */
public class X_AD_TableSchemaLine extends PO
{
/** Constructor estándar */
public X_AD_TableSchemaLine (Properties ctx, int AD_TableSchemaLine_ID, String trxName)
{
super (ctx, AD_TableSchemaLine_ID, trxName);
/** if (AD_TableSchemaLine_ID == 0)
{
setAD_Table_ID (0);
setAD_TableSchema_ID (0);
setAD_TableSchemaLine_ID (0);
setIsInList (true);	// Y
}
 */
}
/** Load Constructor */
public X_AD_TableSchemaLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1010194 */
public static final int Table_ID=1010194;

/** TableName=AD_TableSchemaLine */
public static final String Table_Name="AD_TableSchemaLine";

protected static KeyNamePair Model = new KeyNamePair(1010194,"AD_TableSchemaLine");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_TableSchemaLine[").append(getID()).append("]");
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
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
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
/** Set Table Schema.
Table Schema */
public void setAD_TableSchema_ID (int AD_TableSchema_ID)
{
set_Value ("AD_TableSchema_ID", new Integer(AD_TableSchema_ID));
}
/** Get Table Schema.
Table Schema */
public int getAD_TableSchema_ID() 
{
Integer ii = (Integer)get_Value("AD_TableSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Table Schema Line.
Table Schema Line */
public void setAD_TableSchemaLine_ID (int AD_TableSchemaLine_ID)
{
set_ValueNoCheck ("AD_TableSchemaLine_ID", new Integer(AD_TableSchemaLine_ID));
}
/** Get Table Schema Line.
Table Schema Line */
public int getAD_TableSchemaLine_ID() 
{
Integer ii = (Integer)get_Value("AD_TableSchemaLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set In List.
In List */
public void setIsInList (boolean IsInList)
{
set_Value ("IsInList", new Boolean(IsInList));
}
/** Get In List.
In List */
public boolean isInList() 
{
Object oo = get_Value("IsInList");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set DB Table Name.
Name of the table in the database */
public void setTableName (String TableName)
{
if (TableName != null && TableName.length() > 40)
{
log.warning("Length > 40 - truncated");
TableName = TableName.substring(0,40);
}
set_Value ("TableName", TableName);
}
/** Get DB Table Name.
Name of the table in the database */
public String getTableName() 
{
return (String)get_Value("TableName");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getTableName());
}
}
