/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_TableReplication
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-07-21 16:24:30.408 */
public class X_AD_TableReplication extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_TableReplication (Properties ctx, int AD_TableReplication_ID, String trxName)
{
super (ctx, AD_TableReplication_ID, trxName);
/** if (AD_TableReplication_ID == 0)
{
setAD_Table_ID (0);
setAD_TableReplication_ID (0);
setReplicationArray (null);
}
 */
}
/** Load Constructor */
public X_AD_TableReplication (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_TableReplication");

/** TableName=AD_TableReplication */
public static final String Table_Name="AD_TableReplication";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_TableReplication");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_TableReplication[").append(getID()).append("]");
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
/** Set AD_TableReplication_ID */
public void setAD_TableReplication_ID (int AD_TableReplication_ID)
{
set_ValueNoCheck ("AD_TableReplication_ID", new Integer(AD_TableReplication_ID));
}
/** Get AD_TableReplication_ID */
public int getAD_TableReplication_ID() 
{
Integer ii = (Integer)get_Value("AD_TableReplication_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CreateReplicationTrigger */
public void setCreateReplicationTrigger (String CreateReplicationTrigger)
{
if (CreateReplicationTrigger != null && CreateReplicationTrigger.length() > 1)
{
log.warning("Length > 1 - truncated");
CreateReplicationTrigger = CreateReplicationTrigger.substring(0,1);
}
set_Value ("CreateReplicationTrigger", CreateReplicationTrigger);
}
/** Get CreateReplicationTrigger */
public String getCreateReplicationTrigger() 
{
return (String)get_Value("CreateReplicationTrigger");
}
/** Set ReplicationArray */
public void setReplicationArray (String ReplicationArray)
{
if (ReplicationArray == null) throw new IllegalArgumentException ("ReplicationArray is mandatory");
if (ReplicationArray.length() > 9999)
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
