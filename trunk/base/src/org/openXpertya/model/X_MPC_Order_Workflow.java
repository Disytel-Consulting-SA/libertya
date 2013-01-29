/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Order_Workflow
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.25 */
public class X_MPC_Order_Workflow extends PO
{
/** Constructor estándar */
public X_MPC_Order_Workflow (Properties ctx, int MPC_Order_Workflow_ID, String trxName)
{
super (ctx, MPC_Order_Workflow_ID, trxName);
/** if (MPC_Order_Workflow_ID == 0)
{
setAD_Workflow_ID (0);
setAccessLevel (null);
setAuthor (null);
setCost (0);
setDuration (0);	// 0
setDurationLimit (0);
setDurationUnit (null);	// h
setEntityType (null);
setMPC_Order_ID (0);
setMPC_Order_Workflow_ID (0);
setName (null);
setPriority (0);
setPublishStatus (null);	// U
setValidateWorkflow (null);
setVersion (0);
setWaitingTime (0);
}
 */
}
/** Load Constructor */
public X_MPC_Order_Workflow (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000008 */
public static final int Table_ID=1000008;

/** TableName=MPC_Order_Workflow */
public static final String Table_Name="MPC_Order_Workflow";

protected static KeyNamePair Model = new KeyNamePair(1000008,"MPC_Order_Workflow");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Order_Workflow[").append(getID()).append("]");
return sb.toString();
}
/** Set Table.
Table for the Fields */
public void setAD_Table_ID (int AD_Table_ID)
{
if (AD_Table_ID <= 0) set_Value ("AD_Table_ID", null);
 else 
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
/** Set Node.
Workflow Node (activity), step or process */
public void setAD_WF_Node_ID (int AD_WF_Node_ID)
{
if (AD_WF_Node_ID <= 0) set_Value ("AD_WF_Node_ID", null);
 else 
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
/** Set Workflow Processor.
Workflow Processor Server */
public void setAD_WorkflowProcessor_ID (int AD_WorkflowProcessor_ID)
{
if (AD_WorkflowProcessor_ID <= 0) set_Value ("AD_WorkflowProcessor_ID", null);
 else 
set_Value ("AD_WorkflowProcessor_ID", new Integer(AD_WorkflowProcessor_ID));
}
/** Get Workflow Processor.
Workflow Processor Server */
public int getAD_WorkflowProcessor_ID() 
{
Integer ii = (Integer)get_Value("AD_WorkflowProcessor_ID");
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
public static final int ACCESSLEVEL_AD_Reference_ID=5;
/** System+Client = 6 */
public static final String ACCESSLEVEL_SystemPlusClient = "6";
/** Client only = 2 */
public static final String ACCESSLEVEL_ClientOnly = "2";
/** Organization = 1 */
public static final String ACCESSLEVEL_Organization = "1";
/** Client+Organization = 3 */
public static final String ACCESSLEVEL_ClientPlusOrganization = "3";
/** System only = 4 */
public static final String ACCESSLEVEL_SystemOnly = "4";
/** All = 7 */
public static final String ACCESSLEVEL_All = "7";
/** Set Data Access Level.
Access Level required */
public void setAccessLevel (String AccessLevel)
{
if (AccessLevel.equals("6") || AccessLevel.equals("2") || AccessLevel.equals("1") || AccessLevel.equals("3") || AccessLevel.equals("4") || AccessLevel.equals("7"));
 else throw new IllegalArgumentException ("AccessLevel Invalid value - Reference_ID=5 - 6 - 2 - 1 - 3 - 4 - 7");
if (AccessLevel == null) throw new IllegalArgumentException ("AccessLevel is mandatory");
if (AccessLevel.length() > 1)
{
log.warning("Length > 1 - truncated");
AccessLevel = AccessLevel.substring(0,0);
}
set_Value ("AccessLevel", AccessLevel);
}
/** Get Data Access Level.
Access Level required */
public String getAccessLevel() 
{
return (String)get_Value("AccessLevel");
}
/** Set Author.
Author/Creator of the Entity */
public void setAuthor (String Author)
{
if (Author == null) throw new IllegalArgumentException ("Author is mandatory");
if (Author.length() > 20)
{
log.warning("Length > 20 - truncated");
Author = Author.substring(0,19);
}
set_Value ("Author", Author);
}
/** Get Author.
Author/Creator of the Entity */
public String getAuthor() 
{
return (String)get_Value("Author");
}
/** Set Cost.
Cost information */
public void setCost (int Cost)
{
set_Value ("Cost", new Integer(Cost));
}
/** Get Cost.
Cost information */
public int getCost() 
{
Integer ii = (Integer)get_Value("Cost");
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
/** Set Document No.
Document sequence number of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 32)
{
log.warning("Length > 32 - truncated");
DocumentNo = DocumentNo.substring(0,31);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence number of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set Duration.
Normal Duration in Duration Unit */
public void setDuration (int Duration)
{
set_Value ("Duration", new Integer(Duration));
}
/** Get Duration.
Normal Duration in Duration Unit */
public int getDuration() 
{
Integer ii = (Integer)get_Value("Duration");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Duration Limit.
Maximum Duration in Duration Unit */
public void setDurationLimit (int DurationLimit)
{
set_Value ("DurationLimit", new Integer(DurationLimit));
}
/** Get Duration Limit.
Maximum Duration in Duration Unit */
public int getDurationLimit() 
{
Integer ii = (Integer)get_Value("DurationLimit");
if (ii == null) return 0;
return ii.intValue();
}
public static final int DURATIONUNIT_AD_Reference_ID=299;
/** Year = Y */
public static final String DURATIONUNIT_Year = "Y";
/** Month = M */
public static final String DURATIONUNIT_Month = "M";
/** Day = D */
public static final String DURATIONUNIT_Day = "D";
/** hour = h */
public static final String DURATIONUNIT_Hour = "h";
/** minute = m */
public static final String DURATIONUNIT_Minute = "m";
/** second = s */
public static final String DURATIONUNIT_Second = "s";
/** Set Duration Unit.
Unit of Duration */
public void setDurationUnit (String DurationUnit)
{
if (DurationUnit.equals("Y") || DurationUnit.equals("M") || DurationUnit.equals("D") || DurationUnit.equals("h") || DurationUnit.equals("m") || DurationUnit.equals("s"));
 else throw new IllegalArgumentException ("DurationUnit Invalid value - Reference_ID=299 - Y - M - D - h - m - s");
if (DurationUnit == null) throw new IllegalArgumentException ("DurationUnit is mandatory");
if (DurationUnit.length() > 1)
{
log.warning("Length > 1 - truncated");
DurationUnit = DurationUnit.substring(0,0);
}
set_Value ("DurationUnit", DurationUnit);
}
/** Get Duration Unit.
Unit of Duration */
public String getDurationUnit() 
{
return (String)get_Value("DurationUnit");
}
public static final int ENTITYTYPE_AD_Reference_ID=245;
/** Customization = CUST */
public static final String ENTITYTYPE_Customization = "CUST";
/** Dictionary = D */
public static final String ENTITYTYPE_Dictionary = "D";
/** User maintained = U */
public static final String ENTITYTYPE_UserMaintained = "U";
/** Applications Integrated with openXpertya = A */
public static final String ENTITYTYPE_ApplicationsIntegratedWithOpenXpertya = "A";
/** Country Version = C */
public static final String ENTITYTYPE_CountryVersion = "C";
/** Set Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public void setEntityType (String EntityType)
{
if (EntityType.equals("CUST") || EntityType.equals("D") || EntityType.equals("U") || EntityType.equals("A") || EntityType.equals("C"));
 else throw new IllegalArgumentException ("EntityType Invalid value - Reference_ID=245 - CUST - D - U - A - C");
if (EntityType == null) throw new IllegalArgumentException ("EntityType is mandatory");
if (EntityType.length() > 1)
{
log.warning("Length > 1 - truncated");
EntityType = EntityType.substring(0,0);
}
set_Value ("EntityType", EntityType);
}
/** Get Entity Type.
Dictionary Entity Type;
 Determines ownership and synchronization */
public String getEntityType() 
{
return (String)get_Value("EntityType");
}
/** Set Comment/Help.
Comment or Hint */
public void setHelp (String Help)
{
if (Help != null && Help.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Help = Help.substring(0,1999);
}
set_Value ("Help", Help);
}
/** Get Comment/Help.
Comment or Hint */
public String getHelp() 
{
return (String)get_Value("Help");
}
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Routing */
public void setIsRouting (boolean IsRouting)
{
set_Value ("IsRouting", new Boolean(IsRouting));
}
/** Get Is Routing */
public boolean isRouting() 
{
Object oo = get_Value("IsRouting");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Manufacturing Order.
Manufacturing Order */
public void setMPC_Order_ID (int MPC_Order_ID)
{
set_ValueNoCheck ("MPC_Order_ID", new Integer(MPC_Order_ID));
}
/** Get Manufacturing Order.
Manufacturing Order */
public int getMPC_Order_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order Node ID */
public void setMPC_Order_Node_ID (int MPC_Order_Node_ID)
{
if (MPC_Order_Node_ID <= 0) set_Value ("MPC_Order_Node_ID", null);
 else 
set_Value ("MPC_Order_Node_ID", new Integer(MPC_Order_Node_ID));
}
/** Get Order Node ID */
public int getMPC_Order_Node_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_Node_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order Workflow */
public void setMPC_Order_Workflow_ID (int MPC_Order_Workflow_ID)
{
set_ValueNoCheck ("MPC_Order_Workflow_ID", new Integer(MPC_Order_Workflow_ID));
}
/** Get Order Workflow */
public int getMPC_Order_Workflow_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_Workflow_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Move Time.
Time to move material form one operation to another */
public void setMovingTime (int MovingTime)
{
set_Value ("MovingTime", new Integer(MovingTime));
}
/** Get Move Time.
Time to move material form one operation to another */
public int getMovingTime() 
{
Integer ii = (Integer)get_Value("MovingTime");
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
public static final int PROCESSTYPE_AD_Reference_ID=1000010;
/** Plant = PL */
public static final String PROCESSTYPE_Plant = "PL";
/** Continuous Flow = CF */
public static final String PROCESSTYPE_ContinuousFlow = "CF";
/** Dedicate Repetititive Flow = DR */
public static final String PROCESSTYPE_DedicateRepetititiveFlow = "DR";
/** Batch Flow  = BF */
public static final String PROCESSTYPE_BatchFlow = "BF";
/** Mixed Repetitive Flow = MR */
public static final String PROCESSTYPE_MixedRepetitiveFlow = "MR";
/** Job Shop = JS */
public static final String PROCESSTYPE_JobShop = "JS";
/** Set Process Type */
public void setProcessType (String ProcessType)
{
if (ProcessType == null || ProcessType.equals("PL") || ProcessType.equals("CF") || ProcessType.equals("DR") || ProcessType.equals("BF") || ProcessType.equals("MR") || ProcessType.equals("JS"));
 else throw new IllegalArgumentException ("ProcessType Invalid value - Reference_ID=1000010 - PL - CF - DR - BF - MR - JS");
if (ProcessType != null && ProcessType.length() > 2)
{
log.warning("Length > 2 - truncated");
ProcessType = ProcessType.substring(0,1);
}
set_Value ("ProcessType", ProcessType);
}
/** Get Process Type */
public String getProcessType() 
{
return (String)get_Value("ProcessType");
}
public static final int PUBLISHSTATUS_AD_Reference_ID=310;
/** Released = R */
public static final String PUBLISHSTATUS_Released = "R";
/** Test = T */
public static final String PUBLISHSTATUS_Test = "T";
/** Under Revision = U */
public static final String PUBLISHSTATUS_UnderRevision = "U";
/** Void = V */
public static final String PUBLISHSTATUS_Void = "V";
/** Set Publication Status.
Status of Publication */
public void setPublishStatus (String PublishStatus)
{
if (PublishStatus.equals("R") || PublishStatus.equals("T") || PublishStatus.equals("U") || PublishStatus.equals("V"));
 else throw new IllegalArgumentException ("PublishStatus Invalid value - Reference_ID=310 - R - T - U - V");
if (PublishStatus == null) throw new IllegalArgumentException ("PublishStatus is mandatory");
if (PublishStatus.length() > 1)
{
log.warning("Length > 1 - truncated");
PublishStatus = PublishStatus.substring(0,0);
}
set_Value ("PublishStatus", PublishStatus);
}
/** Get Publication Status.
Status of Publication */
public String getPublishStatus() 
{
return (String)get_Value("PublishStatus");
}
/** Set Qty Batch Size */
public void setQtyBatchSize (BigDecimal QtyBatchSize)
{
set_Value ("QtyBatchSize", QtyBatchSize);
}
/** Get Qty Batch Size */
public BigDecimal getQtyBatchSize() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyBatchSize");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set QueuingTime */
public void setQueuingTime (int QueuingTime)
{
set_Value ("QueuingTime", new Integer(QueuingTime));
}
/** Get QueuingTime */
public int getQueuingTime() 
{
Integer ii = (Integer)get_Value("QueuingTime");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Resource.
Resource */
public void setS_Resource_ID (int S_Resource_ID)
{
if (S_Resource_ID <= 0) set_Value ("S_Resource_ID", null);
 else 
set_Value ("S_Resource_ID", new Integer(S_Resource_ID));
}
/** Get Resource.
Resource */
public int getS_Resource_ID() 
{
Integer ii = (Integer)get_Value("S_Resource_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Setup Time CMPCS */
public void setSetupTime (int SetupTime)
{
set_Value ("SetupTime", new Integer(SetupTime));
}
/** Get Setup Time CMPCS */
public int getSetupTime() 
{
Integer ii = (Integer)get_Value("SetupTime");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
/** Set Validate Workflow */
public void setValidateWorkflow (String ValidateWorkflow)
{
if (ValidateWorkflow == null) throw new IllegalArgumentException ("ValidateWorkflow is mandatory");
if (ValidateWorkflow.length() > 1)
{
log.warning("Length > 1 - truncated");
ValidateWorkflow = ValidateWorkflow.substring(0,0);
}
set_Value ("ValidateWorkflow", ValidateWorkflow);
}
/** Get Validate Workflow */
public String getValidateWorkflow() 
{
return (String)get_Value("ValidateWorkflow");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value != null && Value.length() > 240)
{
log.warning("Length > 240 - truncated");
Value = Value.substring(0,239);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
/** Set Version.
Version of the table definition */
public void setVersion (int Version)
{
set_Value ("Version", new Integer(Version));
}
/** Get Version.
Version of the table definition */
public int getVersion() 
{
Integer ii = (Integer)get_Value("Version");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Waiting Time.
Workflow Simulation Waiting time */
public void setWaitingTime (int WaitingTime)
{
set_Value ("WaitingTime", new Integer(WaitingTime));
}
/** Get Waiting Time.
Workflow Simulation Waiting time */
public int getWaitingTime() 
{
Integer ii = (Integer)get_Value("WaitingTime");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Working Time.
Workflow Simulation Execution Time */
public void setWorkingTime (int WorkingTime)
{
set_Value ("WorkingTime", new Integer(WorkingTime));
}
/** Get Working Time.
Workflow Simulation Execution Time */
public int getWorkingTime() 
{
Integer ii = (Integer)get_Value("WorkingTime");
if (ii == null) return 0;
return ii.intValue();
}
}
