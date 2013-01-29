/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_MRP
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.75 */
public class X_MPC_MRP extends PO
{
/** Constructor estándar */
public X_MPC_MRP (Properties ctx, int MPC_MRP_ID, String trxName)
{
super (ctx, MPC_MRP_ID, trxName);
/** if (MPC_MRP_ID == 0)
{
setDateOrdered (new Timestamp(System.currentTimeMillis()));
setDatePromised (new Timestamp(System.currentTimeMillis()));
setMPC_MRP_ID (0);
setM_Warehouse_ID (0);
setValue (null);
}
 */
}
/** Load Constructor */
public X_MPC_MRP (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000027 */
public static final int Table_ID=1000027;

/** TableName=MPC_MRP */
public static final String Table_Name="MPC_MRP";

protected static KeyNamePair Model = new KeyNamePair(1000027,"MPC_MRP");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_MRP[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sales Order Line.
Sales Order Line */
public void setC_OrderLine_ID (int C_OrderLine_ID)
{
if (C_OrderLine_ID <= 0) set_Value ("C_OrderLine_ID", null);
 else 
set_Value ("C_OrderLine_ID", new Integer(C_OrderLine_ID));
}
/** Get Sales Order Line.
Sales Order Line */
public int getC_OrderLine_ID() 
{
Integer ii = (Integer)get_Value("C_OrderLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
if (C_Order_ID <= 0) set_Value ("C_Order_ID", null);
 else 
set_Value ("C_Order_ID", new Integer(C_Order_ID));
}
/** Get Order.
Order */
public int getC_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Date Confirm.
Date when the planned order was Confirmed from MRP. */
public void setDateConfirm (Timestamp DateConfirm)
{
set_Value ("DateConfirm", DateConfirm);
}
/** Get Date Confirm.
Date when the planned order was Confirmed from MRP. */
public Timestamp getDateConfirm() 
{
return (Timestamp)get_Value("DateConfirm");
}
/** Set Finish Date Scheduled.
Last date of the schedule. */
public void setDateFinishSchedule (Timestamp DateFinishSchedule)
{
set_Value ("DateFinishSchedule", DateFinishSchedule);
}
/** Get Finish Date Scheduled.
Last date of the schedule. */
public Timestamp getDateFinishSchedule() 
{
return (Timestamp)get_Value("DateFinishSchedule");
}
/** Set Date Ordered.
Date of Order */
public void setDateOrdered (Timestamp DateOrdered)
{
if (DateOrdered == null) throw new IllegalArgumentException ("DateOrdered is mandatory");
set_Value ("DateOrdered", DateOrdered);
}
/** Get Date Ordered.
Date of Order */
public Timestamp getDateOrdered() 
{
return (Timestamp)get_Value("DateOrdered");
}
/** Set Date Promised.
Date Order was promised */
public void setDatePromised (Timestamp DatePromised)
{
if (DatePromised == null) throw new IllegalArgumentException ("DatePromised is mandatory");
set_Value ("DatePromised", DatePromised);
}
/** Get Date Promised.
Date Order was promised */
public Timestamp getDatePromised() 
{
return (Timestamp)get_Value("DatePromised");
}
/** Set Date Simulation */
public void setDateSimulation (Timestamp DateSimulation)
{
set_Value ("DateSimulation", DateSimulation);
}
/** Get Date Simulation */
public Timestamp getDateSimulation() 
{
return (Timestamp)get_Value("DateSimulation");
}
/** Set Start Date.
Starting Date */
public void setDateStart (Timestamp DateStart)
{
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
if (Description != null && Description.length() > 1020)
{
log.warning("Length > 1020 - truncated");
Description = Description.substring(0,1019);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
public static final int DOCSTATUS_AD_Reference_ID=131;
/** Reversed = RE */
public static final String DOCSTATUS_Reversed = "RE";
/** Voided = VO */
public static final String DOCSTATUS_Voided = "VO";
/** Not Approved = NA */
public static final String DOCSTATUS_NotApproved = "NA";
/** In Progress = IP */
public static final String DOCSTATUS_InProgress = "IP";
/** Completed = CO */
public static final String DOCSTATUS_Completed = "CO";
/** Approved = AP */
public static final String DOCSTATUS_Approved = "AP";
/** Closed = CL */
public static final String DOCSTATUS_Closed = "CL";
/** Waiting Confirmation = WC */
public static final String DOCSTATUS_WaitingConfirmation = "WC";
/** Waiting Payment = WP */
public static final String DOCSTATUS_WaitingPayment = "WP";
/** Unknown = ?? */
public static final String DOCSTATUS_Unknown = "??";
/** Drafted = DR */
public static final String DOCSTATUS_Drafted = "DR";
/** Invalid = IN */
public static final String DOCSTATUS_Invalid = "IN";
/** Set Document Status.
The current status of the document */
public void setDocStatus (String DocStatus)
{
if (DocStatus == null || DocStatus.equals("RE") || DocStatus.equals("VO") || DocStatus.equals("NA") || DocStatus.equals("IP") || DocStatus.equals("CO") || DocStatus.equals("AP") || DocStatus.equals("CL") || DocStatus.equals("WC") || DocStatus.equals("WP") || DocStatus.equals("??") || DocStatus.equals("DR") || DocStatus.equals("IN"));
 else throw new IllegalArgumentException ("DocStatus Invalid value - Reference_ID=131 - RE - VO - NA - IP - CO - AP - CL - WC - WP - ?? - DR - IN");
if (DocStatus != null && DocStatus.length() > 2)
{
log.warning("Length > 2 - truncated");
DocStatus = DocStatus.substring(0,1);
}
set_Value ("DocStatus", DocStatus);
}
/** Get Document Status.
The current status of the document */
public String getDocStatus() 
{
return (String)get_Value("DocStatus");
}
/** Set Available.
Resource is available */
public void setIsAvailable (boolean IsAvailable)
{
set_Value ("IsAvailable", new Boolean(IsAvailable));
}
/** Get Available.
Resource is available */
public boolean isAvailable() 
{
Object oo = get_Value("IsAvailable");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set MPC_MRP_ID */
public void setMPC_MRP_ID (int MPC_MRP_ID)
{
set_ValueNoCheck ("MPC_MRP_ID", new Integer(MPC_MRP_ID));
}
/** Get MPC_MRP_ID */
public int getMPC_MRP_ID() 
{
Integer ii = (Integer)get_Value("MPC_MRP_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order BOM Line ID */
public void setMPC_Order_BOMLine_ID (int MPC_Order_BOMLine_ID)
{
if (MPC_Order_BOMLine_ID <= 0) set_Value ("MPC_Order_BOMLine_ID", null);
 else 
set_Value ("MPC_Order_BOMLine_ID", new Integer(MPC_Order_BOMLine_ID));
}
/** Get Order BOM Line ID */
public int getMPC_Order_BOMLine_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_BOMLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Manufacturing Order.
Manufacturing Order */
public void setMPC_Order_ID (int MPC_Order_ID)
{
if (MPC_Order_ID <= 0) set_Value ("MPC_Order_ID", null);
 else 
set_Value ("MPC_Order_ID", new Integer(MPC_Order_ID));
}
/** Get Manufacturing Order.
Manufacturing Order */
public int getMPC_Order_ID() 
{
Integer ii = (Integer)get_Value("MPC_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Forecast Line.
Forecast Line */
public void setM_ForecastLine_ID (int M_ForecastLine_ID)
{
if (M_ForecastLine_ID <= 0) set_Value ("M_ForecastLine_ID", null);
 else 
set_Value ("M_ForecastLine_ID", new Integer(M_ForecastLine_ID));
}
/** Get Forecast Line.
Forecast Line */
public int getM_ForecastLine_ID() 
{
Integer ii = (Integer)get_Value("M_ForecastLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Forecast.
Material Forecast */
public void setM_Forecast_ID (int M_Forecast_ID)
{
if (M_Forecast_ID <= 0) set_Value ("M_Forecast_ID", null);
 else 
set_Value ("M_Forecast_ID", new Integer(M_Forecast_ID));
}
/** Get Forecast.
Material Forecast */
public int getM_Forecast_ID() 
{
Integer ii = (Integer)get_Value("M_Forecast_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Requisition Line.
Material Requisition Line */
public void setM_RequisitionLine_ID (int M_RequisitionLine_ID)
{
if (M_RequisitionLine_ID <= 0) set_Value ("M_RequisitionLine_ID", null);
 else 
set_Value ("M_RequisitionLine_ID", new Integer(M_RequisitionLine_ID));
}
/** Get Requisition Line.
Material Requisition Line */
public int getM_RequisitionLine_ID() 
{
Integer ii = (Integer)get_Value("M_RequisitionLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Requisition.
Material Requisition */
public void setM_Requisition_ID (int M_Requisition_ID)
{
if (M_Requisition_ID <= 0) set_Value ("M_Requisition_ID", null);
 else 
set_Value ("M_Requisition_ID", new Integer(M_Requisition_ID));
}
/** Get Requisition.
Material Requisition */
public int getM_Requisition_ID() 
{
Integer ii = (Integer)get_Value("M_Requisition_ID");
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
if (Name != null && Name.length() > 120)
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
public static final int PLANNER_ID_AD_Reference_ID=110;
/** Set Planner .
ID of the person responsible of planning the product. */
public void setPlanner_ID (int Planner_ID)
{
if (Planner_ID <= 0) set_Value ("Planner_ID", null);
 else 
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
/** Set Priority.
Indicates if this request is of a high, medium or low priority. */
public void setPriority (String Priority)
{
if (Priority != null && Priority.length() > 10)
{
log.warning("Length > 10 - truncated");
Priority = Priority.substring(0,9);
}
set_Value ("Priority", Priority);
}
/** Get Priority.
Indicates if this request is of a high, medium or low priority. */
public String getPriority() 
{
return (String)get_Value("Priority");
}
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
set_Value ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Type.
Type of Validation (SQL, Java Script, Java Language) */
public void setType (String Type)
{
if (Type != null && Type.length() > 1)
{
log.warning("Length > 1 - truncated");
Type = Type.substring(0,0);
}
set_Value ("Type", Type);
}
/** Get Type.
Type of Validation (SQL, Java Script, Java Language) */
public String getType() 
{
return (String)get_Value("Type");
}
/** Set TypeMRP */
public void setTypeMRP (String TypeMRP)
{
if (TypeMRP != null && TypeMRP.length() > 3)
{
log.warning("Length > 3 - truncated");
TypeMRP = TypeMRP.substring(0,2);
}
set_Value ("TypeMRP", TypeMRP);
}
/** Get TypeMRP */
public String getTypeMRP() 
{
return (String)get_Value("TypeMRP");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 80)
{
log.warning("Length > 80 - truncated");
Value = Value.substring(0,79);
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
}
