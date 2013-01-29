/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Scheduler_Para
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-07-21 16:24:30.123 */
public class X_AD_Scheduler_Para extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Scheduler_Para (Properties ctx, int AD_Scheduler_Para_ID, String trxName)
{
super (ctx, AD_Scheduler_Para_ID, trxName);
/** if (AD_Scheduler_Para_ID == 0)
{
setAD_Process_Para_ID (0);
setAD_Scheduler_ID (0);
setAD_Scheduler_Para_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_Scheduler_Para (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Scheduler_Para");

/** TableName=AD_Scheduler_Para */
public static final String Table_Name="AD_Scheduler_Para";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Scheduler_Para");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Scheduler_Para[").append(getID()).append("]");
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
/** Set Process Parameter */
public void setAD_Process_Para_ID (int AD_Process_Para_ID)
{
set_ValueNoCheck ("AD_Process_Para_ID", new Integer(AD_Process_Para_ID));
}
/** Get Process Parameter */
public int getAD_Process_Para_ID() 
{
Integer ii = (Integer)get_Value("AD_Process_Para_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Scheduler.
Schedule Processes */
public void setAD_Scheduler_ID (int AD_Scheduler_ID)
{
set_ValueNoCheck ("AD_Scheduler_ID", new Integer(AD_Scheduler_ID));
}
/** Get Scheduler.
Schedule Processes */
public int getAD_Scheduler_ID() 
{
Integer ii = (Integer)get_Value("AD_Scheduler_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set AD_Scheduler_Para_ID */
public void setAD_Scheduler_Para_ID (int AD_Scheduler_Para_ID)
{
set_ValueNoCheck ("AD_Scheduler_Para_ID", new Integer(AD_Scheduler_Para_ID));
}
/** Get AD_Scheduler_Para_ID */
public int getAD_Scheduler_Para_ID() 
{
Integer ii = (Integer)get_Value("AD_Scheduler_Para_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Default Parameter.
Default value of the parameter */
public void setParameterDefault (String ParameterDefault)
{
if (ParameterDefault != null && ParameterDefault.length() > 60)
{
log.warning("Length > 60 - truncated");
ParameterDefault = ParameterDefault.substring(0,60);
}
set_Value ("ParameterDefault", ParameterDefault);
}
/** Get Default Parameter.
Default value of the parameter */
public String getParameterDefault() 
{
return (String)get_Value("ParameterDefault");
}
}
