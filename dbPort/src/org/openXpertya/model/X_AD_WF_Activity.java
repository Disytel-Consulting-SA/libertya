/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_WF_Activity
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:25.859 */
public class X_AD_WF_Activity extends PO
{
/** Constructor estándar */
public X_AD_WF_Activity (Properties ctx, int AD_WF_Activity_ID, String trxName)
{
super (ctx, AD_WF_Activity_ID, trxName);
/** if (AD_WF_Activity_ID == 0)
{
setAD_Table_ID (0);
setAD_WF_Activity_ID (0);
setAD_WF_Node_ID (0);
setAD_WF_Process_ID (0);
setAD_Workflow_ID (0);
setProcessed (false);
setRecord_ID (0);
setWFState (null);
}
 */
}
/** Load Constructor */
public X_AD_WF_Activity (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=644 */
public static final int Table_ID=644;

/** TableName=AD_WF_Activity */
public static final String Table_Name="AD_WF_Activity";

protected static KeyNamePair Model = new KeyNamePair(644,"AD_WF_Activity");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_WF_Activity[").append(getID()).append("]");
return sb.toString();
}
/** Set Message.
System Message */
public void setAD_Message_ID (int AD_Message_ID)
{
if (AD_Message_ID <= 0) set_Value ("AD_Message_ID", null);
 else 
set_Value ("AD_Message_ID", new Integer(AD_Message_ID));
}
/** Get Message.
System Message */
public int getAD_Message_ID() 
{
Integer ii = (Integer)get_Value("AD_Message_ID");
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
public static final int AD_USER_ID_AD_Reference_ID=286;
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_Value ("AD_User_ID", null);
 else 
set_Value ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Workflow Activity.
Workflow Activity */
public void setAD_WF_Activity_ID (int AD_WF_Activity_ID)
{
set_ValueNoCheck ("AD_WF_Activity_ID", new Integer(AD_WF_Activity_ID));
}
/** Get Workflow Activity.
Workflow Activity */
public int getAD_WF_Activity_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_Activity_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Node.
Workflow Node (activity), step or process */
public void setAD_WF_Node_ID (int AD_WF_Node_ID)
{
set_Value ("AD_WF_Node_ID", new Integer(AD_WF_Node_ID));
}
/** Get Node.
Workflow Node (activity), step or process */
public int getAD_WF_Node_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_Node_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getAD_WF_Node_ID()));
}
/** Set Workflow Process.
Actual Workflow Process Instance */
public void setAD_WF_Process_ID (int AD_WF_Process_ID)
{
set_ValueNoCheck ("AD_WF_Process_ID", new Integer(AD_WF_Process_ID));
}
/** Get Workflow Process.
Actual Workflow Process Instance */
public int getAD_WF_Process_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_Process_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Workflow Responsible.
Responsible for Workflow Execution */
public void setAD_WF_Responsible_ID (int AD_WF_Responsible_ID)
{
if (AD_WF_Responsible_ID <= 0) set_Value ("AD_WF_Responsible_ID", null);
 else 
set_Value ("AD_WF_Responsible_ID", new Integer(AD_WF_Responsible_ID));
}
/** Get Workflow Responsible.
Responsible for Workflow Execution */
public int getAD_WF_Responsible_ID() 
{
Integer ii = (Integer)get_Value("AD_WF_Responsible_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Workflow.
Workflow or combination of tasks */
public void setAD_Workflow_ID (int AD_Workflow_ID)
{
set_Value ("AD_Workflow_ID", new Integer(AD_Workflow_ID));
}
/** Get Workflow.
Workflow or combination of tasks */
public int getAD_Workflow_ID() 
{
Integer ii = (Integer)get_Value("AD_Workflow_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Last Alert.
Date when last alert were sent */
public void setDateLastAlert (Timestamp DateLastAlert)
{
set_Value ("DateLastAlert", DateLastAlert);
}
/** Get Last Alert.
Date when last alert were sent */
public Timestamp getDateLastAlert() 
{
return (Timestamp)get_Value("DateLastAlert");
}
/** Set Dyn Priority Start.
Starting priority before changed dynamically */
public void setDynPriorityStart (int DynPriorityStart)
{
set_Value ("DynPriorityStart", new Integer(DynPriorityStart));
}
/** Get Dyn Priority Start.
Starting priority before changed dynamically */
public int getDynPriorityStart() 
{
Integer ii = (Integer)get_Value("DynPriorityStart");
if (ii == null) return 0;
return ii.intValue();
}
/** Set End Wait.
End of sleep time */
public void setEndWaitTime (Timestamp EndWaitTime)
{
set_Value ("EndWaitTime", EndWaitTime);
}
/** Get End Wait.
End of sleep time */
public Timestamp getEndWaitTime() 
{
return (Timestamp)get_Value("EndWaitTime");
}
/** Set Priority.
Indicates if this request is of a high, medium or low priority. */
public void setPriority (int Priority)
{
set_Value ("Priority", new Integer(Priority));
}
/** Get Priority.
Indicates if this request is of a high, medium or low priority. */
public int getPriority() 
{
Integer ii = (Integer)get_Value("Priority");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Record ID.
Direct internal record ID */
public void setRecord_ID (int Record_ID)
{
set_Value ("Record_ID", new Integer(Record_ID));
}
/** Get Record ID.
Direct internal record ID */
public int getRecord_ID() 
{
Integer ii = (Integer)get_Value("Record_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Text Message.
Text Message */
public void setTextMsg (String TextMsg)
{
if (TextMsg != null && TextMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
TextMsg = TextMsg.substring(0,1999);
}
set_Value ("TextMsg", TextMsg);
}
/** Get Text Message.
Text Message */
public String getTextMsg() 
{
return (String)get_Value("TextMsg");
}
public static final int WFSTATE_AD_Reference_ID=305;
/** Not Started = ON */
public static final String WFSTATE_NotStarted = "ON";
/** Running = OR */
public static final String WFSTATE_Running = "OR";
/** Suspended = OS */
public static final String WFSTATE_Suspended = "OS";
/** Completed = CC */
public static final String WFSTATE_Completed = "CC";
/** Aborted = CA */
public static final String WFSTATE_Aborted = "CA";
/** Terminated = CT */
public static final String WFSTATE_Terminated = "CT";
/** Set Workflow State.
State of the execution of the workflow */
public void setWFState (String WFState)
{
if (WFState.equals("ON") || WFState.equals("OR") || WFState.equals("OS") || WFState.equals("CC") || WFState.equals("CA") || WFState.equals("CT"));
 else throw new IllegalArgumentException ("WFState Invalid value - Reference_ID=305 - ON - OR - OS - CC - CA - CT");
if (WFState == null) throw new IllegalArgumentException ("WFState is mandatory");
if (WFState.length() > 2)
{
log.warning("Length > 2 - truncated");
WFState = WFState.substring(0,1);
}
set_Value ("WFState", WFState);
}
/** Get Workflow State.
State of the execution of the workflow */
public String getWFState() 
{
return (String)get_Value("WFState");
}
}
