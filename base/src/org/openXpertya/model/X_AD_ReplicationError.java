/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ReplicationError
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-08-11 14:00:29.069 */
public class X_AD_ReplicationError extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ReplicationError (Properties ctx, int AD_ReplicationError_ID, String trxName)
{
super (ctx, AD_ReplicationError_ID, trxName);
/** if (AD_ReplicationError_ID == 0)
{
setAD_ReplicationError_ID (0);
setFinalChangelog_ID (0);
setInitialChangelog_ID (0);
setORG_Target_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_ReplicationError (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ReplicationError");

/** TableName=AD_ReplicationError */
public static final String Table_Name="AD_ReplicationError";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ReplicationError");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ReplicationError[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ReplicationError_ID */
public void setAD_ReplicationError_ID (int AD_ReplicationError_ID)
{
set_ValueNoCheck ("AD_ReplicationError_ID", new Integer(AD_ReplicationError_ID));
}
/** Get AD_ReplicationError_ID */
public int getAD_ReplicationError_ID() 
{
Integer ii = (Integer)get_Value("AD_ReplicationError_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set FinalChangelog_ID.
Registro final en la bitacora para esta replicacion */
public void setFinalChangelog_ID (int FinalChangelog_ID)
{
set_Value ("FinalChangelog_ID", new Integer(FinalChangelog_ID));
}
/** Get FinalChangelog_ID.
Registro final en la bitacora para esta replicacion */
public int getFinalChangelog_ID() 
{
Integer ii = (Integer)get_Value("FinalChangelog_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set InitialChangelog_ID.
Registro inicial en la bitacora para esta replicacion */
public void setInitialChangelog_ID (int InitialChangelog_ID)
{
set_Value ("InitialChangelog_ID", new Integer(InitialChangelog_ID));
}
/** Get InitialChangelog_ID.
Registro inicial en la bitacora para esta replicacion */
public int getInitialChangelog_ID() 
{
Integer ii = (Integer)get_Value("InitialChangelog_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ORG_TARGET_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (all)");
/** Set ORG_Target_ID.
Organizacion destino para replicación */
public void setORG_Target_ID (int ORG_Target_ID)
{
set_Value ("ORG_Target_ID", new Integer(ORG_Target_ID));
}
/** Get ORG_Target_ID.
Organizacion destino para replicación */
public int getORG_Target_ID() 
{
Integer ii = (Integer)get_Value("ORG_Target_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Replication_Error */
public void setReplication_Error (String Replication_Error)
{
if (Replication_Error != null && Replication_Error.length() > 999999)
{
log.warning("Length > 999999 - truncated");
Replication_Error = Replication_Error.substring(0,999999);
}
set_Value ("Replication_Error", Replication_Error);
}
/** Get Replication_Error */
public String getReplication_Error() 
{
return (String)get_Value("Replication_Error");
}
public static final int REPLICATION_TYPE_AD_Reference_ID = MReference.getReferenceID("Replication Type");
/** Asynchronous = A */
public static final String REPLICATION_TYPE_Asynchronous = "A";
/** Synchronous = S */
public static final String REPLICATION_TYPE_Synchronous = "S";
/** Set Replication_Type.
Sincronica o Asincronica */
public void setReplication_Type (String Replication_Type)
{
if (Replication_Type == null || Replication_Type.equals("A") || Replication_Type.equals("S"));
 else throw new IllegalArgumentException ("Replication_Type Invalid value - Reference = REPLICATION_TYPE_AD_Reference_ID - A - S");
if (Replication_Type != null && Replication_Type.length() > 1)
{
log.warning("Length > 1 - truncated");
Replication_Type = Replication_Type.substring(0,1);
}
set_Value ("Replication_Type", Replication_Type);
}
/** Get Replication_Type.
Sincronica o Asincronica */
public String getReplication_Type() 
{
return (String)get_Value("Replication_Type");
}
}
