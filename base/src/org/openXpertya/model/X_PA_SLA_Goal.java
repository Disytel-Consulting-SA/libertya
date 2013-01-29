/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_SLA_Goal
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.687 */
public class X_PA_SLA_Goal extends PO
{
/** Constructor estándar */
public X_PA_SLA_Goal (Properties ctx, int PA_SLA_Goal_ID, String trxName)
{
super (ctx, PA_SLA_Goal_ID, trxName);
/** if (PA_SLA_Goal_ID == 0)
{
setC_BPartner_ID (0);
setMeasureActual (Env.ZERO);
setMeasureTarget (Env.ZERO);
setName (null);
setPA_SLA_Criteria_ID (0);
setPA_SLA_Goal_ID (0);
setProcessed (false);
}
 */
}
/** Load Constructor */
public X_PA_SLA_Goal (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=745 */
public static final int Table_ID=745;

/** TableName=PA_SLA_Goal */
public static final String Table_Name="PA_SLA_Goal";

protected static KeyNamePair Model = new KeyNamePair(745,"PA_SLA_Goal");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_SLA_Goal[").append(getID()).append("]");
return sb.toString();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_ValueNoCheck ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
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
/** Set Measure Actual.
Actual value that has been measured. */
public void setMeasureActual (BigDecimal MeasureActual)
{
if (MeasureActual == null) throw new IllegalArgumentException ("MeasureActual is mandatory");
set_Value ("MeasureActual", MeasureActual);
}
/** Get Measure Actual.
Actual value that has been measured. */
public BigDecimal getMeasureActual() 
{
BigDecimal bd = (BigDecimal)get_Value("MeasureActual");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Measure Target.
Target value for measure */
public void setMeasureTarget (BigDecimal MeasureTarget)
{
if (MeasureTarget == null) throw new IllegalArgumentException ("MeasureTarget is mandatory");
set_Value ("MeasureTarget", MeasureTarget);
}
/** Get Measure Target.
Target value for measure */
public BigDecimal getMeasureTarget() 
{
BigDecimal bd = (BigDecimal)get_Value("MeasureTarget");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set SLA Criteria.
Service Level Agreement Criteria */
public void setPA_SLA_Criteria_ID (int PA_SLA_Criteria_ID)
{
set_Value ("PA_SLA_Criteria_ID", new Integer(PA_SLA_Criteria_ID));
}
/** Get SLA Criteria.
Service Level Agreement Criteria */
public int getPA_SLA_Criteria_ID() 
{
Integer ii = (Integer)get_Value("PA_SLA_Criteria_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set SLA Goal.
Service Level Agreement Goal */
public void setPA_SLA_Goal_ID (int PA_SLA_Goal_ID)
{
set_ValueNoCheck ("PA_SLA_Goal_ID", new Integer(PA_SLA_Goal_ID));
}
/** Get SLA Goal.
Service Level Agreement Goal */
public int getPA_SLA_Goal_ID() 
{
Integer ii = (Integer)get_Value("PA_SLA_Goal_ID");
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
}
