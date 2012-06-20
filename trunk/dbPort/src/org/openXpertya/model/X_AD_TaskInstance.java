/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_TaskInstance
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:25.296 */
public class X_AD_TaskInstance extends PO
{
/** Constructor estándar */
public X_AD_TaskInstance (Properties ctx, int AD_TaskInstance_ID, String trxName)
{
super (ctx, AD_TaskInstance_ID, trxName);
/** if (AD_TaskInstance_ID == 0)
{
setAD_TaskInstance_ID (0);
setAD_Task_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_TaskInstance (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=125 */
public static final int Table_ID=125;

/** TableName=AD_TaskInstance */
public static final String Table_Name="AD_TaskInstance";

protected static KeyNamePair Model = new KeyNamePair(125,"AD_TaskInstance");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_TaskInstance[").append(getID()).append("]");
return sb.toString();
}
/** Set Task Instance */
public void setAD_TaskInstance_ID (int AD_TaskInstance_ID)
{
set_ValueNoCheck ("AD_TaskInstance_ID", new Integer(AD_TaskInstance_ID));
}
/** Get Task Instance */
public int getAD_TaskInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_TaskInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_TaskInstance_ID()));
}
/** Set OS Task.
Operation System Task */
public void setAD_Task_ID (int AD_Task_ID)
{
set_Value ("AD_Task_ID", new Integer(AD_Task_ID));
}
/** Get OS Task.
Operation System Task */
public int getAD_Task_ID() 
{
Integer ii = (Integer)get_Value("AD_Task_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
