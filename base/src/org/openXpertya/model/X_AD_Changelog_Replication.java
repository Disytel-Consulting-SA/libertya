/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Changelog_Replication
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-06-01 09:35:39.965 */
public class X_AD_Changelog_Replication extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Changelog_Replication (Properties ctx, int AD_Changelog_Replication_ID, String trxName)
{
super (ctx, AD_Changelog_Replication_ID, trxName);
/** if (AD_Changelog_Replication_ID == 0)
{
setAD_Changelog_Replication_ID (0);
setAD_Table_ID (0);
setRecordUID (null);
}
 */
}
/** Load Constructor */
public X_AD_Changelog_Replication (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Changelog_Replication");

/** TableName=AD_Changelog_Replication */
public static final String Table_Name="AD_Changelog_Replication";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Changelog_Replication");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Changelog_Replication[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_Changelog_Replication_ID */
public void setAD_Changelog_Replication_ID (int AD_Changelog_Replication_ID)
{
set_ValueNoCheck ("AD_Changelog_Replication_ID", new Integer(AD_Changelog_Replication_ID));
}
/** Get AD_Changelog_Replication_ID */
public int getAD_Changelog_Replication_ID() 
{
Integer ii = (Integer)get_Value("AD_Changelog_Replication_ID");
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
/** Set Binary Value */
public void setBinaryValue (byte[] BinaryValue)
{
set_Value ("BinaryValue", BinaryValue);
}
/** Get Binary Value */
public byte[] getBinaryValue() 
{
return (byte[])get_Value("BinaryValue");
}
/** Set ColumnValues */
public void setColumnValues (String ColumnValues)
{
if (ColumnValues != null && ColumnValues.length() > 999999)
{
log.warning("Length > 999999 - truncated");
ColumnValues = ColumnValues.substring(0,999999);
}
set_Value ("ColumnValues", ColumnValues);
}
/** Get ColumnValues */
public String getColumnValues() 
{
return (String)get_Value("ColumnValues");
}
/** Set Operation Type */
public void setOperationType (String OperationType)
{
if (OperationType != null && OperationType.length() > 1)
{
log.warning("Length > 1 - truncated");
OperationType = OperationType.substring(0,1);
}
set_Value ("OperationType", OperationType);
}
/** Get Operation Type */
public String getOperationType() 
{
return (String)get_Value("OperationType");
}
/** Set RecordUID */
public void setRecordUID (String RecordUID)
{
if (RecordUID == null) throw new IllegalArgumentException ("RecordUID is mandatory");
if (RecordUID.length() > 100)
{
log.warning("Length > 100 - truncated");
RecordUID = RecordUID.substring(0,100);
}
set_Value ("RecordUID", RecordUID);
}
/** Get RecordUID */
public String getRecordUID() 
{
return (String)get_Value("RecordUID");
}
/** Set ReplicationArray */
public void setReplicationArray (String ReplicationArray)
{
if (ReplicationArray != null && ReplicationArray.length() > 9999)
{
log.warning("Length > 9999 - truncated");
ReplicationArray = ReplicationArray.substring(0,9999);
}
set_Value ("ReplicationArray", ReplicationArray);
}
/** Get ReplicationArray */
public String getReplicationArray() 
{
return (String)get_Value("ReplicationArray");
}
}
