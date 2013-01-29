/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_Goal
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-01-16 18:04:10.614 */
public class X_PA_Goal extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_PA_Goal (Properties ctx, int PA_Goal_ID, String trxName)
{
super (ctx, PA_Goal_ID, trxName);
/** if (PA_Goal_ID == 0)
{
setGoalPerformance (Env.ZERO);
setIsSummary (false);
setMeasureActual (Env.ZERO);
setMeasureTarget (Env.ZERO);
setName (null);
setPA_Goal_ID (0);
setPA_Measure_ID (0);
setRelativeWeight (Env.ZERO);	// 1
setSeqNo (0);
}
 */
}
/** Load Constructor */
public X_PA_Goal (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("PA_Goal");

/** TableName=PA_Goal */
public static final String Table_Name="PA_Goal";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"PA_Goal");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_Goal[").append(getID()).append("]");
return sb.toString();
}
/** Set Date last run.
Date the process was last run. */
public void setDateLastRun (Timestamp DateLastRun)
{
set_ValueNoCheck ("DateLastRun", DateLastRun);
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
/** Set Performance Goal.
Target achievement from 0..1 */
public void setGoalPerformance (BigDecimal GoalPerformance)
{
if (GoalPerformance == null) throw new IllegalArgumentException ("GoalPerformance is mandatory");
set_ValueNoCheck ("GoalPerformance", GoalPerformance);
}
/** Get Performance Goal.
Target achievement from 0..1 */
public BigDecimal getGoalPerformance() 
{
BigDecimal bd = (BigDecimal)get_Value("GoalPerformance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Summary Level.
This is a summary entity */
public void setIsSummary (boolean IsSummary)
{
set_Value ("IsSummary", new Boolean(IsSummary));
}
/** Get Summary Level.
This is a summary entity */
public boolean isSummary() 
{
Object oo = get_Value("IsSummary");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Measure Actual.
Actual value that has been measured. */
public void setMeasureActual (BigDecimal MeasureActual)
{
if (MeasureActual == null) throw new IllegalArgumentException ("MeasureActual is mandatory");
set_ValueNoCheck ("MeasureActual", MeasureActual);
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
/** Set Note.
Optional additional user defined information */
public void setNote (String Note)
{
if (Note != null && Note.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Note = Note.substring(0,2000);
}
set_Value ("Note", Note);
}
/** Get Note.
Optional additional user defined information */
public String getNote() 
{
return (String)get_Value("Note");
}
/** Set Goal.
Performance Goal */
public void setPA_Goal_ID (int PA_Goal_ID)
{
set_ValueNoCheck ("PA_Goal_ID", new Integer(PA_Goal_ID));
}
/** Get Goal.
Performance Goal */
public int getPA_Goal_ID() 
{
Integer ii = (Integer)get_Value("PA_Goal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Measure.
Concrete Performance Measurement */
public void setPA_Measure_ID (int PA_Measure_ID)
{
set_Value ("PA_Measure_ID", new Integer(PA_Measure_ID));
}
/** Get Measure.
Concrete Performance Measurement */
public int getPA_Measure_ID() 
{
Integer ii = (Integer)get_Value("PA_Measure_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PARENT_ID_AD_Reference_ID = MReference.getReferenceID("PA_Goal Parent");
/** Set Parent.
Parent of Entity */
public void setParent_ID (int Parent_ID)
{
if (Parent_ID <= 0) set_Value ("Parent_ID", null);
 else 
set_Value ("Parent_ID", new Integer(Parent_ID));
}
/** Get Parent.
Parent of Entity */
public int getParent_ID() 
{
Integer ii = (Integer)get_Value("Parent_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Relative Weight.
Relative weight of this step (0 = ignored) */
public void setRelativeWeight (BigDecimal RelativeWeight)
{
if (RelativeWeight == null) throw new IllegalArgumentException ("RelativeWeight is mandatory");
set_Value ("RelativeWeight", RelativeWeight);
}
/** Get Relative Weight.
Relative weight of this step (0 = ignored) */
public BigDecimal getRelativeWeight() 
{
BigDecimal bd = (BigDecimal)get_Value("RelativeWeight");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
}
