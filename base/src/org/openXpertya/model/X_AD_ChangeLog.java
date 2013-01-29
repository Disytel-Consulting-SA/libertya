/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ChangeLog
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-11-25 09:34:05.039 */
public class X_AD_ChangeLog extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ChangeLog (Properties ctx, int AD_ChangeLog_ID, String trxName)
{
super (ctx, AD_ChangeLog_ID, trxName);
/** if (AD_ChangeLog_ID == 0)
{
setAD_ChangeLog_ID (0);
setAD_Column_ID (0);
setAD_Session_ID (0);
setAD_Table_ID (0);
setIsCustomization (false);
setRecord_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_ChangeLog (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ChangeLog");

/** TableName=AD_ChangeLog */
public static final String Table_Name="AD_ChangeLog";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ChangeLog");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ChangeLog[").append(getID()).append("]");
return sb.toString();
}
/** Set Change Log.
Log of data changes */
public void setAD_ChangeLog_ID (int AD_ChangeLog_ID)
{
set_ValueNoCheck ("AD_ChangeLog_ID", new Integer(AD_ChangeLog_ID));
}
/** Get Change Log.
Log of data changes */
public int getAD_ChangeLog_ID() 
{
Integer ii = (Integer)get_Value("AD_ChangeLog_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Column.
Column in the table */
public void setAD_Column_ID (int AD_Column_ID)
{
set_ValueNoCheck ("AD_Column_ID", new Integer(AD_Column_ID));
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
/** Set Session.
User Session Online or Web */
public void setAD_Session_ID (int AD_Session_ID)
{
set_ValueNoCheck ("AD_Session_ID", new Integer(AD_Session_ID));
}
/** Get Session.
User Session Online or Web */
public int getAD_Session_ID() 
{
Integer ii = (Integer)get_Value("AD_Session_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_Session_ID()));
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
set_ValueNoCheck ("AD_Table_ID", new Integer(AD_Table_ID));
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
/** Set ChangeLogGroup_ID */
public void setChangeLogGroup_ID (int ChangeLogGroup_ID)
{
if (ChangeLogGroup_ID <= 0) set_Value ("ChangeLogGroup_ID", null);
 else 
set_Value ("ChangeLogGroup_ID", new Integer(ChangeLogGroup_ID));
}
/** Get ChangeLogGroup_ID */
public int getChangeLogGroup_ID() 
{
Integer ii = (Integer)get_Value("ChangeLogGroup_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Customization.
The change is a customization of the data dictionary and can be applied after Migration */
public void setIsCustomization (boolean IsCustomization)
{
set_Value ("IsCustomization", new Boolean(IsCustomization));
}
/** Get Customization.
The change is a customization of the data dictionary and can be applied after Migration */
public boolean isCustomization() 
{
Object oo = get_Value("IsCustomization");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set New Value.
New field value */
public void setNewValue (String NewValue)
{
if (NewValue != null && NewValue.length() > 2000)
{
log.warning("Length > 2000 - truncated");
NewValue = NewValue.substring(0,2000);
}
set_ValueNoCheck ("NewValue", NewValue);
}
/** Get New Value.
New field value */
public String getNewValue() 
{
return (String)get_Value("NewValue");
}
/** Set Old Value.
The old file data */
public void setOldValue (String OldValue)
{
if (OldValue != null && OldValue.length() > 2000)
{
log.warning("Length > 2000 - truncated");
OldValue = OldValue.substring(0,2000);
}
set_ValueNoCheck ("OldValue", OldValue);
}
/** Get Old Value.
The old file data */
public String getOldValue() 
{
return (String)get_Value("OldValue");
}
public static final int OPERATIONTYPE_AD_Reference_ID = MReference.getReferenceID("Operation types");
/** Deletion = D */
public static final String OPERATIONTYPE_Deletion = "D";
/** Insertion = I */
public static final String OPERATIONTYPE_Insertion = "I";
/** Modification = M */
public static final String OPERATIONTYPE_Modification = "M";
/** Set Operation Type */
public void setOperationType (String OperationType)
{
if (OperationType == null || OperationType.equals("D") || OperationType.equals("I") || OperationType.equals("M"));
 else throw new IllegalArgumentException ("OperationType Invalid value - Reference = OPERATIONTYPE_AD_Reference_ID - D - I - M");
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
/** Set Record ID.
Direct internal record ID */
public void setRecord_ID (int Record_ID)
{
set_ValueNoCheck ("Record_ID", new Integer(Record_ID));
}
/** Get Record ID.
Direct internal record ID */
public int getRecord_ID() 
{
Integer ii = (Integer)get_Value("Record_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Redo */
public void setRedo (String Redo)
{
if (Redo != null && Redo.length() > 1)
{
log.warning("Length > 1 - truncated");
Redo = Redo.substring(0,1);
}
set_Value ("Redo", Redo);
}
/** Get Redo */
public String getRedo() 
{
return (String)get_Value("Redo");
}
/** Set Transaction.
Name of the transaction */
public void setTrxName (String TrxName)
{
if (TrxName != null && TrxName.length() > 60)
{
log.warning("Length > 60 - truncated");
TrxName = TrxName.substring(0,60);
}
set_ValueNoCheck ("TrxName", TrxName);
}
/** Get Transaction.
Name of the transaction */
public String getTrxName() 
{
return (String)get_Value("TrxName");
}
/** Set Undo */
public void setUndo (String Undo)
{
if (Undo != null && Undo.length() > 1)
{
log.warning("Length > 1 - truncated");
Undo = Undo.substring(0,1);
}
set_Value ("Undo", Undo);
}
/** Get Undo */
public String getUndo() 
{
return (String)get_Value("Undo");
}
}
