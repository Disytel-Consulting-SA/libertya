/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CyclePhase
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.515 */
public class X_C_CyclePhase extends PO
{
/** Constructor estándar */
public X_C_CyclePhase (Properties ctx, int C_CyclePhase_ID, String trxName)
{
super (ctx, C_CyclePhase_ID, trxName);
/** if (C_CyclePhase_ID == 0)
{
setC_CycleStep_ID (0);
setC_Phase_ID (0);
}
 */
}
/** Load Constructor */
public X_C_CyclePhase (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=433 */
public static final int Table_ID=433;

/** TableName=C_CyclePhase */
public static final String Table_Name="C_CyclePhase";

protected static KeyNamePair Model = new KeyNamePair(433,"C_CyclePhase");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CyclePhase[").append(getID()).append("]");
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
/** Set Standard Phase.
Standard Phase of the Project Type */
public void setC_Phase_ID (int C_Phase_ID)
{
set_ValueNoCheck ("C_Phase_ID", new Integer(C_Phase_ID));
}
/** Get Standard Phase.
Standard Phase of the Project Type */
public int getC_Phase_ID() 
{
Integer ii = (Integer)get_Value("C_Phase_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
