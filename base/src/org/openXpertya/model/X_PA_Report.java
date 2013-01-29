/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_Report
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:37.743 */
public class X_PA_Report extends PO
{
/** Constructor estándar */
public X_PA_Report (Properties ctx, int PA_Report_ID, String trxName)
{
super (ctx, PA_Report_ID, trxName);
/** if (PA_Report_ID == 0)
{
setC_AcctSchema_ID (0);
setC_Calendar_ID (0);
setListSources (false);
setListTrx (false);
setName (null);
setPA_ReportColumnSet_ID (0);
setPA_Report_ID (0);
setPA_ReportLineSet_ID (0);
setProcessing (false);
}
 */
}
/** Load Constructor */
public X_PA_Report (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=445 */
public static final int Table_ID=445;

/** TableName=PA_Report */
public static final String Table_Name="PA_Report";

protected static KeyNamePair Model = new KeyNamePair(445,"PA_Report");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_Report[").append(getID()).append("]");
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
/** Set Print Format.
Data Print Format */
public void setAD_PrintFormat_ID (int AD_PrintFormat_ID)
{
if (AD_PrintFormat_ID <= 0) set_Value ("AD_PrintFormat_ID", null);
 else 
set_Value ("AD_PrintFormat_ID", new Integer(AD_PrintFormat_ID));
}
/** Get Print Format.
Data Print Format */
public int getAD_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("AD_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Accounting Schema.
Rules for accounting */
public void setC_AcctSchema_ID (int C_AcctSchema_ID)
{
set_Value ("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
}
/** Get Accounting Schema.
Rules for accounting */
public int getC_AcctSchema_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Calendar.
Accounting Calendar Name */
public void setC_Calendar_ID (int C_Calendar_ID)
{
set_Value ("C_Calendar_ID", new Integer(C_Calendar_ID));
}
/** Get Calendar.
Accounting Calendar Name */
public int getC_Calendar_ID() 
{
Integer ii = (Integer)get_Value("C_Calendar_ID");
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
/** Set List Sources.
List Report Line Sources */
public void setListSources (boolean ListSources)
{
set_Value ("ListSources", new Boolean(ListSources));
}
/** Get List Sources.
List Report Line Sources */
public boolean isListSources() 
{
Object oo = get_Value("ListSources");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set List Transactions.
List the report transactions */
public void setListTrx (boolean ListTrx)
{
set_Value ("ListTrx", new Boolean(ListTrx));
}
/** Get List Transactions.
List the report transactions */
public boolean isListTrx() 
{
Object oo = get_Value("ListTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Report Column Set.
Collection of Columns for Report */
public void setPA_ReportColumnSet_ID (int PA_ReportColumnSet_ID)
{
set_Value ("PA_ReportColumnSet_ID", new Integer(PA_ReportColumnSet_ID));
}
/** Get Report Column Set.
Collection of Columns for Report */
public int getPA_ReportColumnSet_ID() 
{
Integer ii = (Integer)get_Value("PA_ReportColumnSet_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Financial Report.
Financial Report */
public void setPA_Report_ID (int PA_Report_ID)
{
set_ValueNoCheck ("PA_Report_ID", new Integer(PA_Report_ID));
}
/** Get Financial Report.
Financial Report */
public int getPA_Report_ID() 
{
Integer ii = (Integer)get_Value("PA_Report_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Report Line Set */
public void setPA_ReportLineSet_ID (int PA_ReportLineSet_ID)
{
set_Value ("PA_ReportLineSet_ID", new Integer(PA_ReportLineSet_ID));
}
/** Get Report Line Set */
public int getPA_ReportLineSet_ID() 
{
Integer ii = (Integer)get_Value("PA_ReportLineSet_ID");
if (ii == null) return 0;
return ii.intValue();
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
}
