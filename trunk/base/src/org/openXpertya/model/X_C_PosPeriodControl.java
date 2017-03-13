/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model; 
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PosPeriodControl
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-02-23 13:27:55.069 */
public class X_C_PosPeriodControl extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_PosPeriodControl (Properties ctx, int C_PosPeriodControl_ID, String trxName)
{
super (ctx, C_PosPeriodControl_ID, trxName);
/** if (C_PosPeriodControl_ID == 0)
{
setC_Posperiodcontrol_ID (0);
setPeriodAction (null);	// N
}
 */
}
/** Load Constructor */
public X_C_PosPeriodControl (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_PosPeriodControl");

/** TableName=C_PosPeriodControl */
public static final String Table_Name="C_PosPeriodControl";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_PosPeriodControl");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PosPeriodControl[").append(getID()).append("]");
return sb.toString();
}
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
if (C_DocType_ID <= 0) set_Value ("C_DocType_ID", null);
 else 
set_Value ("C_DocType_ID", new Integer(C_DocType_ID));
}
/** Get Document Type.
Document type or rules */
public int getC_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Period Control */
public void setC_PeriodControl_ID (int C_PeriodControl_ID)
{
if (C_PeriodControl_ID <= 0) set_Value ("C_PeriodControl_ID", null);
 else 
set_Value ("C_PeriodControl_ID", new Integer(C_PeriodControl_ID));
}
/** Get Period Control */
public int getC_PeriodControl_ID() 
{
Integer ii = (Integer)get_Value("C_PeriodControl_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Posperiodcontrol_ID */
public void setC_Posperiodcontrol_ID (int C_Posperiodcontrol_ID)
{
set_ValueNoCheck ("C_Posperiodcontrol_ID", new Integer(C_Posperiodcontrol_ID));
}
/** Get C_Posperiodcontrol_ID */
public int getC_Posperiodcontrol_ID() 
{
Integer ii = (Integer)get_Value("C_Posperiodcontrol_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PERIODACTION_AD_Reference_ID = MReference.getReferenceID("C_PeriodControl Action");
/** Open Period = O */
public static final String PERIODACTION_OpenPeriod = "O";
/** Close Period = C */
public static final String PERIODACTION_ClosePeriod = "C";
/** Permanently Close Period = P */
public static final String PERIODACTION_PermanentlyClosePeriod = "P";
/** <No Action> = N */
public static final String PERIODACTION_NoAction = "N";
/** Set Period Action.
Action taken for this period */
public void setPeriodAction (String PeriodAction)
{
if (PeriodAction.equals("O") || PeriodAction.equals("C") || PeriodAction.equals("P") || PeriodAction.equals("N"));
 else throw new IllegalArgumentException ("PeriodAction Invalid value - Reference = PERIODACTION_AD_Reference_ID - O - C - P - N");
if (PeriodAction == null) throw new IllegalArgumentException ("PeriodAction is mandatory");
if (PeriodAction.length() > 1)
{
log.warning("Length > 1 - truncated");
PeriodAction = PeriodAction.substring(0,1);
}
set_Value ("PeriodAction", PeriodAction);
}
/** Get Period Action.
Action taken for this period */
public String getPeriodAction() 
{
return (String)get_Value("PeriodAction");
}
public static final int PERIODSTATUS_AD_Reference_ID = MReference.getReferenceID("C_PeriodControl Status");
/** Open = O */
public static final String PERIODSTATUS_Open = "O";
/** Closed = C */
public static final String PERIODSTATUS_Closed = "C";
/** Permanently closed = P */
public static final String PERIODSTATUS_PermanentlyClosed = "P";
/** Never opened = N */
public static final String PERIODSTATUS_NeverOpened = "N";
/** Set Period Status.
Current state of this period */
public void setPeriodStatus (String PeriodStatus)
{
if (PeriodStatus == null || PeriodStatus.equals("O") || PeriodStatus.equals("C") || PeriodStatus.equals("P") || PeriodStatus.equals("N"));
 else throw new IllegalArgumentException ("PeriodStatus Invalid value - Reference = PERIODSTATUS_AD_Reference_ID - O - C - P - N");
if (PeriodStatus != null && PeriodStatus.length() > 1)
{
log.warning("Length > 1 - truncated");
PeriodStatus = PeriodStatus.substring(0,1);
}
set_Value ("PeriodStatus", PeriodStatus);
}
/** Get Period Status.
Current state of this period */
public String getPeriodStatus() 
{
return (String)get_Value("PeriodStatus");
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
