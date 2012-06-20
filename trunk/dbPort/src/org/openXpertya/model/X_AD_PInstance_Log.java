/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_PInstance_Log
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:23.468 */
public class X_AD_PInstance_Log extends PO
{
/** Constructor estándar */
public X_AD_PInstance_Log (Properties ctx, int AD_PInstance_Log_ID, String trxName)
{
super (ctx, AD_PInstance_Log_ID, trxName);
/** if (AD_PInstance_Log_ID == 0)
{
setAD_PInstance_ID (0);
setLog_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_PInstance_Log (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=578 */
public static final int Table_ID=578;

/** TableName=AD_PInstance_Log */
public static final String Table_Name="AD_PInstance_Log";

protected static KeyNamePair Model = new KeyNamePair(578,"AD_PInstance_Log");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_PInstance_Log[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_ValueNoCheck ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Log */
public void setLog_ID (int Log_ID)
{
set_ValueNoCheck ("Log_ID", new Integer(Log_ID));
}
/** Get Log */
public int getLog_ID() 
{
Integer ii = (Integer)get_Value("Log_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Process Date.
Process Parameter */
public void setP_Date (Timestamp P_Date)
{
set_ValueNoCheck ("P_Date", P_Date);
}
/** Get Process Date.
Process Parameter */
public Timestamp getP_Date() 
{
return (Timestamp)get_Value("P_Date");
}
/** Set Process ID */
public void setP_ID (int P_ID)
{
if (P_ID <= 0) set_ValueNoCheck ("P_ID", null);
 else 
set_ValueNoCheck ("P_ID", new Integer(P_ID));
}
/** Get Process ID */
public int getP_ID() 
{
Integer ii = (Integer)get_Value("P_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Process Message */
public void setP_Msg (String P_Msg)
{
if (P_Msg != null && P_Msg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
P_Msg = P_Msg.substring(0,1999);
}
set_ValueNoCheck ("P_Msg", P_Msg);
}
/** Get Process Message */
public String getP_Msg() 
{
return (String)get_Value("P_Msg");
}
/** Set Process Number.
Process Parameter */
public void setP_Number (BigDecimal P_Number)
{
set_ValueNoCheck ("P_Number", P_Number);
}
/** Get Process Number.
Process Parameter */
public BigDecimal getP_Number() 
{
BigDecimal bd = (BigDecimal)get_Value("P_Number");
if (bd == null) return Env.ZERO;
return bd;
}
}
