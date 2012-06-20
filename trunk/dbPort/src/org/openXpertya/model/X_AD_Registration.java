/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_Registration
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:24.421 */
public class X_AD_Registration extends PO
{
/** Constructor estándar */
public X_AD_Registration (Properties ctx, int AD_Registration_ID, String trxName)
{
super (ctx, AD_Registration_ID, trxName);
/** if (AD_Registration_ID == 0)
{
setAD_Registration_ID (0);	// 0
setAD_System_ID (0);	// 0
setIsAllowPublish (true);	// Y
setIsAllowStatistics (true);	// Y
setIsInProduction (false);
setIsRegistered (false);	// N
}
 */
}
/** Load Constructor */
public X_AD_Registration (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=625 */
public static final int Table_ID=625;

/** TableName=AD_Registration */
public static final String Table_Name="AD_Registration";

protected static KeyNamePair Model = new KeyNamePair(625,"AD_Registration");
protected static BigDecimal AccessLevel = new BigDecimal(4);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_Registration[").append(getID()).append("]");
return sb.toString();
}
/** Set System Registration.
System Registration */
public void setAD_Registration_ID (int AD_Registration_ID)
{
set_ValueNoCheck ("AD_Registration_ID", new Integer(AD_Registration_ID));
}
/** Get System Registration.
System Registration */
public int getAD_Registration_ID() 
{
Integer ii = (Integer)get_Value("AD_Registration_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set System.
System Definition */
public void setAD_System_ID (int AD_System_ID)
{
set_ValueNoCheck ("AD_System_ID", new Integer(AD_System_ID));
}
/** Get System.
System Definition */
public int getAD_System_ID() 
{
Integer ii = (Integer)get_Value("AD_System_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
if (C_Currency_ID <= 0) set_Value ("C_Currency_ID", null);
 else 
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Address.
Location or Address */
public void setC_Location_ID (int C_Location_ID)
{
if (C_Location_ID <= 0) set_Value ("C_Location_ID", null);
 else 
set_Value ("C_Location_ID", new Integer(C_Location_ID));
}
/** Get Address.
Location or Address */
public int getC_Location_ID() 
{
Integer ii = (Integer)get_Value("C_Location_ID");
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
/** Set Industry Info.
Information of the industry (e.g. professional service, distribution of furnitures, ..) */
public void setIndustryInfo (String IndustryInfo)
{
if (IndustryInfo != null && IndustryInfo.length() > 255)
{
log.warning("Length > 255 - truncated");
IndustryInfo = IndustryInfo.substring(0,254);
}
set_Value ("IndustryInfo", IndustryInfo);
}
/** Get Industry Info.
Information of the industry (e.g. professional service, distribution of furnitures, ..) */
public String getIndustryInfo() 
{
return (String)get_Value("IndustryInfo");
}
/** Set Allowed to be Published.
You allow to publish the information, not just statistical summary info */
public void setIsAllowPublish (boolean IsAllowPublish)
{
set_Value ("IsAllowPublish", new Boolean(IsAllowPublish));
}
/** Get Allowed to be Published.
You allow to publish the information, not just statistical summary info */
public boolean isAllowPublish() 
{
Object oo = get_Value("IsAllowPublish");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Allow Statistics.
Allow to transfer general statistics (number of clients, orgs, business partners, users, products, invoices) */
public void setIsAllowStatistics (boolean IsAllowStatistics)
{
set_Value ("IsAllowStatistics", new Boolean(IsAllowStatistics));
}
/** Get Allow Statistics.
Allow to transfer general statistics (number of clients, orgs, business partners, users, products, invoices) */
public boolean isAllowStatistics() 
{
Object oo = get_Value("IsAllowStatistics");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set In Production.
The system is in production */
public void setIsInProduction (boolean IsInProduction)
{
set_Value ("IsInProduction", new Boolean(IsInProduction));
}
/** Get In Production.
The system is in production */
public boolean isInProduction() 
{
Object oo = get_Value("IsInProduction");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Registered.
The application is registered. */
public void setIsRegistered (boolean IsRegistered)
{
set_ValueNoCheck ("IsRegistered", new Boolean(IsRegistered));
}
/** Get Registered.
The application is registered. */
public boolean isRegistered() 
{
Object oo = get_Value("IsRegistered");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Employees.
Number of employees */
public void setNumberEmployees (int NumberEmployees)
{
set_Value ("NumberEmployees", new Integer(NumberEmployees));
}
/** Get Employees.
Number of employees */
public int getNumberEmployees() 
{
Integer ii = (Integer)get_Value("NumberEmployees");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Platform Info.
Information about Server and Client Platform */
public void setPlatformInfo (String PlatformInfo)
{
if (PlatformInfo != null && PlatformInfo.length() > 255)
{
log.warning("Length > 255 - truncated");
PlatformInfo = PlatformInfo.substring(0,254);
}
set_Value ("PlatformInfo", PlatformInfo);
}
/** Get Platform Info.
Information about Server and Client Platform */
public String getPlatformInfo() 
{
return (String)get_Value("PlatformInfo");
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
if (Record_ID <= 0) set_ValueNoCheck ("Record_ID", null);
 else 
set_ValueNoCheck ("Record_ID", new Integer(Record_ID));
}
/** Get Record ID.
Direct internal record ID */
public int getRecord_ID() 
{
Integer ii = (Integer)get_Value("Record_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Remote Addr.
Remote Address */
public void setRemote_Addr (String Remote_Addr)
{
if (Remote_Addr != null && Remote_Addr.length() > 60)
{
log.warning("Length > 60 - truncated");
Remote_Addr = Remote_Addr.substring(0,59);
}
set_ValueNoCheck ("Remote_Addr", Remote_Addr);
}
/** Get Remote Addr.
Remote Address */
public String getRemote_Addr() 
{
return (String)get_Value("Remote_Addr");
}
/** Set Remote Host */
public void setRemote_Host (String Remote_Host)
{
if (Remote_Host != null && Remote_Host.length() > 120)
{
log.warning("Length > 120 - truncated");
Remote_Host = Remote_Host.substring(0,119);
}
set_ValueNoCheck ("Remote_Host", Remote_Host);
}
/** Get Remote Host */
public String getRemote_Host() 
{
return (String)get_Value("Remote_Host");
}
/** Set Sales Volume in 1.000.
Total Volume of Sales in Thoisand of Currency */
public void setSalesVolume (int SalesVolume)
{
set_Value ("SalesVolume", new Integer(SalesVolume));
}
/** Get Sales Volume in 1.000.
Total Volume of Sales in Thoisand of Currency */
public int getSalesVolume() 
{
Integer ii = (Integer)get_Value("SalesVolume");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Start Implementation/Production.
The day you started the implementation (if implementing) - or production (went life) with openXpertya */
public void setStartProductionDate (Timestamp StartProductionDate)
{
set_Value ("StartProductionDate", StartProductionDate);
}
/** Get Start Implementation/Production.
The day you started the implementation (if implementing) - or production (went life) with openXpertya */
public Timestamp getStartProductionDate() 
{
return (Timestamp)get_Value("StartProductionDate");
}
}
