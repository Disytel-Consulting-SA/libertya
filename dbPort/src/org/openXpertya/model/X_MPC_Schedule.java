/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Schedule
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.781 */
public class X_MPC_Schedule extends PO
{
/** Constructor estándar */
public X_MPC_Schedule (Properties ctx, int MPC_Schedule_ID, String trxName)
{
super (ctx, MPC_Schedule_ID, trxName);
/** if (MPC_Schedule_ID == 0)
{
setAD_OrgTrx_ID (0);
setDateFinish (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDateFinishSchedule (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDateStart (new Timestamp(System.currentTimeMillis()));	// @#Date@
setDateStartSchedule (new Timestamp(System.currentTimeMillis()));	// @#Date@
setIsApproved (false);
setIsCreated (false);
setIsPrinted (false);
setIsSOTrx (false);
setIsSelected (false);
setMPC_Schedule_ID (0);
setM_Warehouse_ID (0);
setName (null);
setPlanner_ID (0);
setProcessed (false);	// N
setS_Resource_ID (0);
setScheduleType (null);
}
 */
}
/** Load Constructor */
public X_MPC_Schedule (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000016 */
public static final int Table_ID=1000016;

/** TableName=MPC_Schedule */
public static final String Table_Name="MPC_Schedule";

protected static KeyNamePair Model = new KeyNamePair(1000016,"MPC_Schedule");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Schedule[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_ORGTRX_ID_AD_Reference_ID=130;
/** Set Trx Organization.
Performing or initiating organization */
public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
{
set_Value ("AD_OrgTrx_ID", new Integer(AD_OrgTrx_ID));
}
/** Get Trx Organization.
Performing or initiating organization */
public int getAD_OrgTrx_ID() 
{
Integer ii = (Integer)get_Value("AD_OrgTrx_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Activity.
Business Activity */
public void setC_Activity_ID (int C_Activity_ID)
{
if (C_Activity_ID <= 0) set_Value ("C_Activity_ID", null);
 else 
set_Value ("C_Activity_ID", new Integer(C_Activity_ID));
}
/** Get Activity.
Business Activity */
public int getC_Activity_ID() 
{
Integer ii = (Integer)get_Value("C_Activity_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Campaign.
Marketing Campaign */
public void setC_Campaign_ID (int C_Campaign_ID)
{
if (C_Campaign_ID <= 0) set_Value ("C_Campaign_ID", null);
 else 
set_Value ("C_Campaign_ID", new Integer(C_Campaign_ID));
}
/** Get Campaign.
Marketing Campaign */
public int getC_Campaign_ID() 
{
Integer ii = (Integer)get_Value("C_Campaign_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
if (C_Project_ID <= 0) set_Value ("C_Project_ID", null);
 else 
set_Value ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Copy From.
Copy From Record */
public void setCopyFrom (String CopyFrom)
{
if (CopyFrom != null && CopyFrom.length() > 1)
{
log.warning("Length > 1 - truncated");
CopyFrom = CopyFrom.substring(0,0);
}
set_Value ("CopyFrom", CopyFrom);
}
/** Get Copy From.
Copy From Record */
public String getCopyFrom() 
{
return (String)get_Value("CopyFrom");
}
/** Set Finish Date.
Finish or (planned) completion date */
public void setDateFinish (Timestamp DateFinish)
{
if (DateFinish == null) throw new IllegalArgumentException ("DateFinish is mandatory");
set_Value ("DateFinish", DateFinish);
}
/** Get Finish Date.
Finish or (planned) completion date */
public Timestamp getDateFinish() 
{
return (Timestamp)get_Value("DateFinish");
}
/** Set Finish Date Scheduled.
Last date of the schedule. */
public void setDateFinishSchedule (Timestamp DateFinishSchedule)
{
if (DateFinishSchedule == null) throw new IllegalArgumentException ("DateFinishSchedule is mandatory");
set_Value ("DateFinishSchedule", DateFinishSchedule);
}
/** Get Finish Date Scheduled.
Last date of the schedule. */
public Timestamp getDateFinishSchedule() 
{
return (Timestamp)get_Value("DateFinishSchedule");
}
/** Set Start Date.
Starting Date */
public void setDateStart (Timestamp DateStart)
{
if (DateStart == null) throw new IllegalArgumentException ("DateStart is mandatory");
set_Value ("DateStart", DateStart);
}
/** Get Start Date.
Starting Date */
public Timestamp getDateStart() 
{
return (Timestamp)get_Value("DateStart");
}
/** Set Starting Date Scheduled.
Starting Date Scheduled */
public void setDateStartSchedule (Timestamp DateStartSchedule)
{
if (DateStartSchedule == null) throw new IllegalArgumentException ("DateStartSchedule is mandatory");
set_Value ("DateStartSchedule", DateStartSchedule);
}
/** Get Starting Date Scheduled.
Starting Date Scheduled */
public Timestamp getDateStartSchedule() 
{
return (Timestamp)get_Value("DateStartSchedule");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 510)
{
log.warning("Length > 510 - truncated");
Description = Description.substring(0,509);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set Approved.
Indicates if this document requires approval */
public void setIsApproved (boolean IsApproved)
{
set_Value ("IsApproved", new Boolean(IsApproved));
}
/** Get Approved.
Indicates if this document requires approval */
public boolean isApproved() 
{
Object oo = get_Value("IsApproved");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Records created */
public void setIsCreated (boolean IsCreated)
{
set_Value ("IsCreated", new Boolean(IsCreated));
}
/** Get Records created */
public boolean isCreated() 
{
Object oo = get_Value("IsCreated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Printed.
Indicates if this document / line is printed */
public void setIsPrinted (boolean IsPrinted)
{
set_Value ("IsPrinted", new Boolean(IsPrinted));
}
/** Get Printed.
Indicates if this document / line is printed */
public boolean isPrinted() 
{
Object oo = get_Value("IsPrinted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sales Transaction.
This is a Sales Transaction */
public void setIsSOTrx (boolean IsSOTrx)
{
set_Value ("IsSOTrx", new Boolean(IsSOTrx));
}
/** Get Sales Transaction.
This is a Sales Transaction */
public boolean isSOTrx() 
{
Object oo = get_Value("IsSOTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Selected */
public void setIsSelected (boolean IsSelected)
{
set_Value ("IsSelected", new Boolean(IsSelected));
}
/** Get Selected */
public boolean isSelected() 
{
Object oo = get_Value("IsSelected");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Manufacturing Schedule.
ID of a production schedule */
public void setMPC_Schedule_ID (int MPC_Schedule_ID)
{
set_ValueNoCheck ("MPC_Schedule_ID", new Integer(MPC_Schedule_ID));
}
/** Get Manufacturing Schedule.
ID of a production schedule */
public int getMPC_Schedule_ID() 
{
Integer ii = (Integer)get_Value("MPC_Schedule_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Warehouse.
Storage Warehouse and Service Point */
public void setM_Warehouse_ID (int M_Warehouse_ID)
{
set_Value ("M_Warehouse_ID", new Integer(M_Warehouse_ID));
}
/** Get Warehouse.
Storage Warehouse and Service Point */
public int getM_Warehouse_ID() 
{
Integer ii = (Integer)get_Value("M_Warehouse_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 120)
{
log.warning("Length > 120 - truncated");
Name = Name.substring(0,119);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public static final int PLANNER_ID_AD_Reference_ID=286;
/** Set Planner .
ID of the person responsible of planning the product. */
public void setPlanner_ID (int Planner_ID)
{
set_Value ("Planner_ID", new Integer(Planner_ID));
}
/** Get Planner .
ID of the person responsible of planning the product. */
public int getPlanner_ID() 
{
Integer ii = (Integer)get_Value("Planner_ID");
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
/** Set Resource.
Resource */
public void setS_Resource_ID (int S_Resource_ID)
{
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
/** Set Schedule Type.
Type of schedule */
public void setScheduleType (String ScheduleType)
{
if (ScheduleType == null) throw new IllegalArgumentException ("ScheduleType is mandatory");
if (ScheduleType.length() > 3)
{
log.warning("Length > 3 - truncated");
ScheduleType = ScheduleType.substring(0,2);
}
set_Value ("ScheduleType", ScheduleType);
}
/** Get Schedule Type.
Type of schedule */
public String getScheduleType() 
{
return (String)get_Value("ScheduleType");
}
public static final int USER1_ID_AD_Reference_ID=134;
/** Set User1.
User defined element #1 */
public void setUser1_ID (int User1_ID)
{
if (User1_ID <= 0) set_Value ("User1_ID", null);
 else 
set_Value ("User1_ID", new Integer(User1_ID));
}
/** Get User1.
User defined element #1 */
public int getUser1_ID() 
{
Integer ii = (Integer)get_Value("User1_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int USER2_ID_AD_Reference_ID=137;
/** Set User2.
User defined element #2 */
public void setUser2_ID (int User2_ID)
{
if (User2_ID <= 0) set_Value ("User2_ID", null);
 else 
set_Value ("User2_ID", new Integer(User2_ID));
}
/** Get User2.
User defined element #2 */
public int getUser2_ID() 
{
Integer ii = (Integer)get_Value("User2_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
