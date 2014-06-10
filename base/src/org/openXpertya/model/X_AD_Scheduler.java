/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Scheduler
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-06-10 15:39:34.883 */
public class X_AD_Scheduler extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_Scheduler (Properties ctx, int AD_Scheduler_ID, String trxName)
{
super (ctx, AD_Scheduler_ID, trxName);
/** if (AD_Scheduler_ID == 0)
{
setAD_Org_Login_ID (0);
setAD_Process_ID (0);
setAD_Role_ID (0);
setAD_Scheduler_ID (0);
setFrequency (0);
setFrequencyType (null);
setKeepLogDays (0);	// 7
setName (null);
setScheduleType (null);	// F
setSupervisor_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_Scheduler (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_Scheduler");

/** TableName=AD_Scheduler */
public static final String Table_Name="AD_Scheduler";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_Scheduler");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Scheduler[").append(getID()).append("]");
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
public static final int AD_ORG_LOGIN_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (all)");
/** Set AD_Org_Login_ID */
public void setAD_Org_Login_ID (int AD_Org_Login_ID)
{
set_Value ("AD_Org_Login_ID", new Integer(AD_Org_Login_ID));
}
/** Get AD_Org_Login_ID */
public int getAD_Org_Login_ID() 
{
Integer ii = (Integer)get_Value("AD_Org_Login_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Process.
Process or Report */
public void setAD_Process_ID (int AD_Process_ID)
{
set_ValueNoCheck ("AD_Process_ID", new Integer(AD_Process_ID));
}
/** Get Process.
Process or Report */
public int getAD_Process_ID() 
{
Integer ii = (Integer)get_Value("AD_Process_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Role.
Responsibility Role */
public void setAD_Role_ID (int AD_Role_ID)
{
set_Value ("AD_Role_ID", new Integer(AD_Role_ID));
}
/** Get Role.
Responsibility Role */
public int getAD_Role_ID() 
{
Integer ii = (Integer)get_Value("AD_Role_ID");
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
public static final int FREQUENCYTYPE_AD_Reference_ID = MReference.getReferenceID("_Frequency Type");
/** Minute = M */
public static final String FREQUENCYTYPE_Minute = "M";
/** Hour = H */
public static final String FREQUENCYTYPE_Hour = "H";
/** Day = D */
public static final String FREQUENCYTYPE_Day = "D";
/** Seconds = S */
public static final String FREQUENCYTYPE_Seconds = "S";
/** Set Frequency Type.
Frequency of event */
public void setFrequencyType (String FrequencyType)
{
if (FrequencyType.equals("M") || FrequencyType.equals("H") || FrequencyType.equals("D") || FrequencyType.equals("S"));
 else throw new IllegalArgumentException ("FrequencyType Invalid value - Reference = FREQUENCYTYPE_AD_Reference_ID - M - H - D - S");
if (FrequencyType == null) throw new IllegalArgumentException ("FrequencyType is mandatory");
if (FrequencyType.length() > 1)
{
log.warning("Length > 1 - truncated");
FrequencyType = FrequencyType.substring(0,1);
}
set_Value ("FrequencyType", FrequencyType);
}
/** Get Frequency Type.
Frequency of event */
public String getFrequencyType() 
{
return (String)get_Value("FrequencyType");
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
/** Set Day of the Month.
Day of the month 1 to 28/29/30/31 */
public void setMonthDay (int MonthDay)
{
set_Value ("MonthDay", new Integer(MonthDay));
}
/** Get Day of the Month.
Day of the month 1 to 28/29/30/31 */
public int getMonthDay() 
{
Integer ii = (Integer)get_Value("MonthDay");
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
Name = Name.substring(0,60);
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
public static final int SCHEDULETYPE_AD_Reference_ID = MReference.getReferenceID("AD_Scheduler Type");
/** Frequency = F */
public static final String SCHEDULETYPE_Frequency = "F";
/** Week Day = W */
public static final String SCHEDULETYPE_WeekDay = "W";
/** Month Day = M */
public static final String SCHEDULETYPE_MonthDay = "M";
/** Set Schedule Type.
Type of schedule */
public void setScheduleType (String ScheduleType)
{
if (ScheduleType.equals("F") || ScheduleType.equals("W") || ScheduleType.equals("M"));
 else throw new IllegalArgumentException ("ScheduleType Invalid value - Reference = SCHEDULETYPE_AD_Reference_ID - F - W - M");
if (ScheduleType == null) throw new IllegalArgumentException ("ScheduleType is mandatory");
if (ScheduleType.length() > 1)
{
log.warning("Length > 1 - truncated");
ScheduleType = ScheduleType.substring(0,1);
}
set_Value ("ScheduleType", ScheduleType);
}
/** Get Schedule Type.
Type of schedule */
public String getScheduleType() 
{
return (String)get_Value("ScheduleType");
}
public static final int SUPERVISOR_ID_AD_Reference_ID = MReference.getReferenceID("AD_User - Supervisor");
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
public static final int WEEKDAY_AD_Reference_ID = MReference.getReferenceID("Weekdays");
/** Sunday = 7 */
public static final String WEEKDAY_Sunday = "7";
/** Monday = 1 */
public static final String WEEKDAY_Monday = "1";
/** Tuesday = 2 */
public static final String WEEKDAY_Tuesday = "2";
/** Wednesday = 3 */
public static final String WEEKDAY_Wednesday = "3";
/** Thursday = 4 */
public static final String WEEKDAY_Thursday = "4";
/** Friday = 5 */
public static final String WEEKDAY_Friday = "5";
/** Saturday = 6 */
public static final String WEEKDAY_Saturday = "6";
/** Set Day of the Week.
Day of the Week */
public void setWeekDay (String WeekDay)
{
if (WeekDay == null || WeekDay.equals("7") || WeekDay.equals("1") || WeekDay.equals("2") || WeekDay.equals("3") || WeekDay.equals("4") || WeekDay.equals("5") || WeekDay.equals("6"));
 else throw new IllegalArgumentException ("WeekDay Invalid value - Reference = WEEKDAY_AD_Reference_ID - 7 - 1 - 2 - 3 - 4 - 5 - 6");
if (WeekDay != null && WeekDay.length() > 1)
{
log.warning("Length > 1 - truncated");
WeekDay = WeekDay.substring(0,1);
}
set_Value ("WeekDay", WeekDay);
}
/** Get Day of the Week.
Day of the Week */
public String getWeekDay() 
{
return (String)get_Value("WeekDay");
}
}
