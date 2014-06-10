/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_AlertProcessor
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-06-10 15:38:35.437 */
public class X_AD_AlertProcessor extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_AlertProcessor (Properties ctx, int AD_AlertProcessor_ID, String trxName)
{
super (ctx, AD_AlertProcessor_ID, trxName);
/** if (AD_AlertProcessor_ID == 0)
{
setAD_AlertProcessor_ID (0);
setFrequency (0);
setFrequencyType (null);
setKeepLogDays (0);	// 7
setName (null);
setSupervisor_ID (0);
}
 */
}
/** Load Constructor */
public X_AD_AlertProcessor (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_AlertProcessor");

/** TableName=AD_AlertProcessor */
public static final String Table_Name="AD_AlertProcessor";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_AlertProcessor");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_AlertProcessor[").append(getID()).append("]");
return sb.toString();
}
/** Set Alert Processor.
Alert Processor/Server Parameter */
public void setAD_AlertProcessor_ID (int AD_AlertProcessor_ID)
{
set_ValueNoCheck ("AD_AlertProcessor_ID", new Integer(AD_AlertProcessor_ID));
}
/** Get Alert Processor.
Alert Processor/Server Parameter */
public int getAD_AlertProcessor_ID() 
{
Integer ii = (Integer)get_Value("AD_AlertProcessor_ID");
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
}
