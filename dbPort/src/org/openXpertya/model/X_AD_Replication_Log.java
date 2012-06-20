/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Replication_Log
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:24.562 */
public class X_AD_Replication_Log extends PO
{
/** Constructor estándar */
public X_AD_Replication_Log (Properties ctx, int AD_Replication_Log_ID, String trxName)
{
super (ctx, AD_Replication_Log_ID, trxName);
/** if (AD_Replication_Log_ID == 0)
{
setAD_Replication_Log_ID (0);
setAD_Replication_Run_ID (0);
setIsReplicated (false);	// N
}
 */
}
/** Load Constructor */
public X_AD_Replication_Log (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=604 */
public static final int Table_ID=604;

/** TableName=AD_Replication_Log */
public static final String Table_Name="AD_Replication_Log";

protected static KeyNamePair Model = new KeyNamePair(604,"AD_Replication_Log");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Replication_Log[").append(getID()).append("]");
return sb.toString();
}
/** Set Replication Table.
Data Replication Strategy Table Info */
public void setAD_ReplicationTable_ID (int AD_ReplicationTable_ID)
{
if (AD_ReplicationTable_ID <= 0) set_Value ("AD_ReplicationTable_ID", null);
 else 
set_Value ("AD_ReplicationTable_ID", new Integer(AD_ReplicationTable_ID));
}
/** Get Replication Table.
Data Replication Strategy Table Info */
public int getAD_ReplicationTable_ID() 
{
Integer ii = (Integer)get_Value("AD_ReplicationTable_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Replication Log.
Data Replication Log Details */
public void setAD_Replication_Log_ID (int AD_Replication_Log_ID)
{
set_ValueNoCheck ("AD_Replication_Log_ID", new Integer(AD_Replication_Log_ID));
}
/** Get Replication Log.
Data Replication Log Details */
public int getAD_Replication_Log_ID() 
{
Integer ii = (Integer)get_Value("AD_Replication_Log_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Replication Run.
Data Replication Run */
public void setAD_Replication_Run_ID (int AD_Replication_Run_ID)
{
set_ValueNoCheck ("AD_Replication_Run_ID", new Integer(AD_Replication_Run_ID));
}
/** Get Replication Run.
Data Replication Run */
public int getAD_Replication_Run_ID() 
{
Integer ii = (Integer)get_Value("AD_Replication_Run_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_Replication_Run_ID()));
}
/** Set Replicated.
The data is successfully replicated */
public void setIsReplicated (boolean IsReplicated)
{
set_Value ("IsReplicated", new Boolean(IsReplicated));
}
/** Get Replicated.
The data is successfully replicated */
public boolean isReplicated() 
{
Object oo = get_Value("IsReplicated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Process Message */
public void setP_Msg (String P_Msg)
{
if (P_Msg != null && P_Msg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
P_Msg = P_Msg.substring(0,1999);
}
set_Value ("P_Msg", P_Msg);
}
/** Get Process Message */
public String getP_Msg() 
{
return (String)get_Value("P_Msg");
}
}
