/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_AsyncReplication
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-06-07 10:27:34.674 */
public class X_AD_AsyncReplication extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_AsyncReplication (Properties ctx, int AD_AsyncReplication_ID, String trxName)
{
super (ctx, AD_AsyncReplication_ID, trxName);
/** if (AD_AsyncReplication_ID == 0)
{
setAD_AsyncReplication_ID (0);
setFinalChangelog_ID (0);
setInitialChangelog_ID (0);
setORG_Source_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_AsyncReplication (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_AsyncReplication");

/** TableName=AD_AsyncReplication */
public static final String Table_Name="AD_AsyncReplication";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_AsyncReplication");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_AsyncReplication[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_AsyncReplication_ID */
public void setAD_AsyncReplication_ID (int AD_AsyncReplication_ID)
{
set_ValueNoCheck ("AD_AsyncReplication_ID", new Integer(AD_AsyncReplication_ID));
}
/** Get AD_AsyncReplication_ID */
public int getAD_AsyncReplication_ID() 
{
Integer ii = (Integer)get_Value("AD_AsyncReplication_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ASYNC_ACTION_AD_Reference_ID = MReference.getReferenceID("Replication Action");
/** Replicate = R */
public static final String ASYNC_ACTION_Replicate = "R";
/** Error = E */
public static final String ASYNC_ACTION_Error = "E";
/** Set async_action */
public void setasync_action (String async_action)
{
if (async_action == null || async_action.equals("R") || async_action.equals("E"));
 else throw new IllegalArgumentException ("async_action Invalid value - Reference = ASYNC_ACTION_AD_Reference_ID - R - E");
if (async_action != null && async_action.length() > 1)
{
log.warning("Length > 1 - truncated");
async_action = async_action.substring(0,1);
}
set_Value ("async_action", async_action);
}
/** Get async_action */
public String getasync_action() 
{
return (String)get_Value("async_action");
}
/** Set async_content */
public void setasync_content (String async_content)
{
if (async_content != null && async_content.length() > 999999)
{
log.warning("Length > 999999 - truncated");
async_content = async_content.substring(0,999999);
}
set_Value ("async_content", async_content);
}
/** Get async_content */
public String getasync_content() 
{
return (String)get_Value("async_content");
}
public static final int ASYNC_STATUS_AD_Reference_ID = MReference.getReferenceID("Replication Status");
/** Error in replication = KO */
public static final String ASYNC_STATUS_ErrorInReplication = "KO";
/** Replicated = OK */
public static final String ASYNC_STATUS_Replicated = "OK";
/** Set async_status */
public void setasync_status (String async_status)
{
if (async_status == null || async_status.equals("KO") || async_status.equals("OK"));
 else throw new IllegalArgumentException ("async_status Invalid value - Reference = ASYNC_STATUS_AD_Reference_ID - KO - OK");
if (async_status != null && async_status.length() > 2)
{
log.warning("Length > 2 - truncated");
async_status = async_status.substring(0,2);
}
set_Value ("async_status", async_status);
}
/** Get async_status */
public String getasync_status() 
{
return (String)get_Value("async_status");
}
/** Set FinalChangelog_ID.
Registro final en la bitacora del host origen para esta replicacion */
public void setFinalChangelog_ID (int FinalChangelog_ID)
{
set_Value ("FinalChangelog_ID", new Integer(FinalChangelog_ID));
}
/** Get FinalChangelog_ID.
Registro final en la bitacora del host origen para esta replicacion */
public int getFinalChangelog_ID() 
{
Integer ii = (Integer)get_Value("FinalChangelog_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set InitialChangelog_ID.
Registro inicial en la bitacora del host origen para esta replicacion */
public void setInitialChangelog_ID (int InitialChangelog_ID)
{
set_Value ("InitialChangelog_ID", new Integer(InitialChangelog_ID));
}
/** Get InitialChangelog_ID.
Registro inicial en la bitacora del host origen para esta replicacion */
public int getInitialChangelog_ID() 
{
Integer ii = (Integer)get_Value("InitialChangelog_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int ORG_SOURCE_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (all)");
/** Set ORG_Source_ID.
Organizacion origen para esta replicacion */
public void setORG_Source_ID (int ORG_Source_ID)
{
set_Value ("ORG_Source_ID", new Integer(ORG_Source_ID));
}
/** Get ORG_Source_ID.
Organizacion origen para esta replicacion */
public int getORG_Source_ID() 
{
Integer ii = (Integer)get_Value("ORG_Source_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
