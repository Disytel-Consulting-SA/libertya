/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CycleStep
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.531 */
public class X_C_CycleStep extends PO
{
/** Constructor estándar */
public X_C_CycleStep (Properties ctx, int C_CycleStep_ID, String trxName)
{
super (ctx, C_CycleStep_ID, trxName);
/** if (C_CycleStep_ID == 0)
{
setC_CycleStep_ID (0);
setC_Cycle_ID (0);
setName (null);
setRelativeWeight (Env.ZERO);	// 1
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM C_CycleStep WHERE C_Cycle_ID=@C_Cycle_ID@
}
 */
}
/** Load Constructor */
public X_C_CycleStep (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=590 */
public static final int Table_ID=590;

/** TableName=C_CycleStep */
public static final String Table_Name="C_CycleStep";

protected static KeyNamePair Model = new KeyNamePair(590,"C_CycleStep");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CycleStep[").append(getID()).append("]");
return sb.toString();
}
/** Set Cycle Step.
The step for this Cycle */
public void setC_CycleStep_ID (int C_CycleStep_ID)
{
set_ValueNoCheck ("C_CycleStep_ID", new Integer(C_CycleStep_ID));
}
/** Get Cycle Step.
The step for this Cycle */
public int getC_CycleStep_ID() 
{
Integer ii = (Integer)get_Value("C_CycleStep_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project Cycle.
Identifier for this Project Reporting Cycle */
public void setC_Cycle_ID (int C_Cycle_ID)
{
set_ValueNoCheck ("C_Cycle_ID", new Integer(C_Cycle_ID));
}
/** Get Project Cycle.
Identifier for this Project Reporting Cycle */
public int getC_Cycle_ID() 
{
Integer ii = (Integer)get_Value("C_Cycle_ID");
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
