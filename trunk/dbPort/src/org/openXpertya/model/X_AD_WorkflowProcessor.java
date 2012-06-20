/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_WorkflowProcessor
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:26.562 */
public class X_AD_WorkflowProcessor extends PO
{
/** Constructor estándar */
public X_AD_WorkflowProcessor (Properties ctx, int AD_WorkflowProcessor_ID, String trxName)
{
super (ctx, AD_WorkflowProcessor_ID, trxName);
/** if (AD_WorkflowProcessor_ID == 0)
{
setAD_WorkflowProcessor_ID (0);
setFrequency (0);
setFrequencyType (null);
setKeepLogDays (0);	// 7
setName (null);
setSupervisor_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_WorkflowProcessor (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=697 */
public static final int Table_ID=697;

/** TableName=AD_WorkflowProcessor */
public static final String Table_Name="AD_WorkflowProcessor";

protected static KeyNamePair Model = new KeyNamePair(697,"AD_WorkflowProcessor");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_WorkflowProcessor[").append(getID()).append("]");
return sb.toString();
}
/** Set Workflow Processor.
Workflow Processor Server */
public void setAD_WorkflowProcessor_ID (int AD_WorkflowProcessor_ID)
{
set_ValueNoCheck ("AD_WorkflowProcessor_ID", new Integer(AD_WorkflowProcessor_ID));
}
/** Get Workflow Processor.
Workflow Processor Server */
public int getAD_WorkflowProcessor_ID() 
{
Integer ii = (Integer)get_Value("AD_WorkflowProcessor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Alert over Priority.
Send alert email when over priority */
public void setAlertOverPriority (int AlertOverPriority)
{
set_Value ("AlertOverPriority", new Integer(AlertOverPriority));
}
/** Get Alert over Priority.
Send alert email when over priority */
public int getAlertOverPriority() 
{
Integer ii = (Integer)get_Value("AlertOverPriority");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Date last run.
Date the process was last run. */
public void setDateLastRun (Timestamp DateLastRun)
{
set_Value ("DateLastRun", DateLastRun);
}
/** Get Date last run.
Date the process was last run. */
public Timestamp getDateLastRun() 
{
return (Timestamp)get_Value("DateLastRun");
}
/** Set Date next run.
Date the process will run next */
public void setDateNextRun (Timestamp DateNextRun)
{
set_Value ("DateNextRun", DateNextRun);
}
/** Get Date next run.
Date the process will run next */
public Timestamp getDateNextRun() 
{
return (Timestamp)get_Value("DateNextRun");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Frequency.
Frequency of events */
public void setFrequency (int Frequency)
{
set_Value ("Frequency", new Integer(Frequency));
}
/** Get Frequency.
Frequency of events */
public int getFrequency() 
{
Integer ii = (Integer)get_Value("Frequency");
if (ii == null) return 0;
return ii.intValue();
}
public static final int FREQUENCYTYPE_AD_Reference_ID=221;
/** Minute = M */
public static final String FREQUENCYTYPE_Minute = "M";
/** Hour = H */
public static final String FREQUENCYTYPE_Hour = "H";
/** Day = D */
public static final String FREQUENCYTYPE_Day = "D";
/** Set Frequency Type.
Frequency of event */
public void setFrequencyType (String FrequencyType)
{
if (FrequencyType.equals("M") || FrequencyType.equals("H") || FrequencyType.equals("D"));
 else throw new IllegalArgumentException ("FrequencyType Invalid value - Reference_ID=221 - M - H - D");
if (FrequencyType == null) throw new IllegalArgumentException ("FrequencyType is mandatory");
if (FrequencyType.length() > 1)
{
log.warning("Length > 1 - truncated");
FrequencyType = FrequencyType.substring(0,0);
}
set_Value ("FrequencyType", FrequencyType);
}
/** Get Frequency Type.
Frequency of event */
public String getFrequencyType() 
{
return (String)get_Value("FrequencyType");
}
/** Set Inactivity Alert Days.
Send Alert when there is no activity after days (0= no alert) */
public void setInactivityAlertDays (int InactivityAlertDays)
{
set_Value ("InactivityAlertDays", new Integer(InactivityAlertDays));
}
/** Get Inactivity Alert Days.
Send Alert when there is no activity after days (0= no alert) */
public int getInactivityAlertDays() 
{
Integer ii = (Integer)get_Value("InactivityAlertDays");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Days to keep Log.
Number of days to keep the log entries */
public void setKeepLogDays (int KeepLogDays)
{
set_Value ("KeepLogDays", new Integer(KeepLogDays));
}
/** Get Days to keep Log.
Number of days to keep the log entries */
public int getKeepLogDays() 
{
Integer ii = (Integer)get_Value("KeepLogDays");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Reminder Days.
Days between sending Reminder Emails for a due or inactive Document */
public void setRemindDays (int RemindDays)
{
set_Value ("RemindDays", new Integer(RemindDays));
}
/** Get Reminder Days.
Days between sending Reminder Emails for a due or inactive Document */
public int getRemindDays() 
{
Integer ii = (Integer)get_Value("RemindDays");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SUPERVISOR_ID_AD_Reference_ID=316;
/** Set Supervisor.
Supervisor for this user/organization - used for escalation and approval */
public void setSupervisor_ID (int Supervisor_ID)
{
set_Value ("Supervisor_ID", new Integer(Supervisor_ID));
}
/** Get Supervisor.
Supervisor for this user/organization - used for escalation and approval */
public int getSupervisor_ID() 
{
Integer ii = (Integer)get_Value("Supervisor_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
