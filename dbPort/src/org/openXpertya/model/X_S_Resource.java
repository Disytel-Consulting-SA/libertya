/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por S_Resource
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:38.468 */
public class X_S_Resource extends PO
{
/** Constructor estándar */
public X_S_Resource (Properties ctx, int S_Resource_ID, String trxName)
{
super (ctx, S_Resource_ID, trxName);
/** if (S_Resource_ID == 0)
{
setIsAvailable (true);	// Y
setM_Warehouse_ID (0);
setName (null);
setS_Resource_ID (0);
setS_ResourceType_ID (0);
setValue (null);
}
 */
}
/** Load Constructor */
public X_S_Resource (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=487 */
public static final int Table_ID=487;

/** TableName=S_Resource */
public static final String Table_Name="S_Resource";

protected static KeyNamePair Model = new KeyNamePair(487,"S_Resource");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_S_Resource[").append(getID()).append("]");
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
/** Set Chargeable Quantity */
public void setChargeableQty (BigDecimal ChargeableQty)
{
set_Value ("ChargeableQty", ChargeableQty);
}
/** Get Chargeable Quantity */
public BigDecimal getChargeableQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ChargeableQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Daily Capacity.
Daily Availability for production */
public void setDailyCapacity (BigDecimal DailyCapacity)
{
set_Value ("DailyCapacity", DailyCapacity);
}
/** Get Daily Capacity.
Daily Availability for production */
public BigDecimal getDailyCapacity() 
{
BigDecimal bd = (BigDecimal)get_Value("DailyCapacity");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Is Manufacturing Resource */
public void setIsManufacturingResource (boolean IsManufacturingResource)
{
set_Value ("IsManufacturingResource", new Boolean(IsManufacturingResource));
}
/** Get Is Manufacturing Resource */
public boolean isManufacturingResource() 
{
Object oo = get_Value("IsManufacturingResource");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int MANUFACTURINGRESOURCETYPE_AD_Reference_ID=1000016;
/** Plant = PT */
public static final String MANUFACTURINGRESOURCETYPE_Plant = "PT";
/** Production Line = PL */
public static final String MANUFACTURINGRESOURCETYPE_ProductionLine = "PL";
/** Work Center = WC */
public static final String MANUFACTURINGRESOURCETYPE_WorkCenter = "WC";
/** Work Station = WS */
public static final String MANUFACTURINGRESOURCETYPE_WorkStation = "WS";
/** Set Manufacturing Resource Type */
public void setManufacturingResourceType (String ManufacturingResourceType)
{
if (ManufacturingResourceType == null || ManufacturingResourceType.equals("PT") || ManufacturingResourceType.equals("PL") || ManufacturingResourceType.equals("WC") || ManufacturingResourceType.equals("WS"));
 else throw new IllegalArgumentException ("ManufacturingResourceType Invalid value - Reference_ID=1000016 - PT - PL - WC - WS");
if (ManufacturingResourceType != null && ManufacturingResourceType.length() > 2)
{
log.warning("Length > 2 - truncated");
ManufacturingResourceType = ManufacturingResourceType.substring(0,2);
}
set_Value ("ManufacturingResourceType", ManufacturingResourceType);
}
/** Get Manufacturing Resource Type */
public String getManufacturingResourceType() 
{
return (String)get_Value("ManufacturingResourceType");
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
/** Set Percent Utillization.
Total time used in a work center divided by time available in that work center multiplied by 100. */
public void setPercentUtillization (BigDecimal PercentUtillization)
{
set_Value ("PercentUtillization", PercentUtillization);
}
/** Get Percent Utillization.
Total time used in a work center divided by time available in that work center multiplied by 100. */
public BigDecimal getPercentUtillization() 
{
BigDecimal bd = (BigDecimal)get_Value("PercentUtillization");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set QueuingTime */
public void setQueuingTime (BigDecimal QueuingTime)
{
set_Value ("QueuingTime", QueuingTime);
}
/** Get QueuingTime */
public BigDecimal getQueuingTime() 
{
BigDecimal bd = (BigDecimal)get_Value("QueuingTime");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Resource.
Resource */
public void setS_Resource_ID (int S_Resource_ID)
{
set_ValueNoCheck ("S_Resource_ID", new Integer(S_Resource_ID));
}
/** Get Resource.
Resource */
public int getS_Resource_ID() 
{
Integer ii = (Integer)get_Value("S_Resource_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Resource Type */
public void setS_ResourceType_ID (int S_ResourceType_ID)
{
set_Value ("S_ResourceType_ID", new Integer(S_ResourceType_ID));
}
/** Get Resource Type */
public int getS_ResourceType_ID() 
{
Integer ii = (Integer)get_Value("S_ResourceType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
/** Set Waiting Time.
Workflow Simulation Waiting time */
public void setWaitingTime (BigDecimal WaitingTime)
{
set_Value ("WaitingTime", WaitingTime);
}
/** Get Waiting Time.
Workflow Simulation Waiting time */
public BigDecimal getWaitingTime() 
{
BigDecimal bd = (BigDecimal)get_Value("WaitingTime");
if (bd == null) return Env.ZERO;
return bd;
}
}
