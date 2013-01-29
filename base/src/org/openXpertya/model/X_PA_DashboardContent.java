/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por PA_DashboardContent
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-01-16 17:33:21.487 */
public class X_PA_DashboardContent extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_PA_DashboardContent (Properties ctx, int PA_DashboardContent_ID, String trxName)
{
super (ctx, PA_DashboardContent_ID, trxName);
/** if (PA_DashboardContent_ID == 0)
{
setIsCollapsible (false);
setName (null);
setPA_DashboardContent_ID (0);
}
 */
}
/** Load Constructor */
public X_PA_DashboardContent (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("PA_DashboardContent");

/** TableName=PA_DashboardContent */
public static final String Table_Name="PA_DashboardContent";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"PA_DashboardContent");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_PA_DashboardContent[").append(getID()).append("]");
return sb.toString();
}
/** Set Window.
Data entry or display window */
public void setAD_Window_ID (int AD_Window_ID)
{
if (AD_Window_ID <= 0) set_Value ("AD_Window_ID", null);
 else 
set_Value ("AD_Window_ID", new Integer(AD_Window_ID));
}
/** Get Window.
Data entry or display window */
public int getAD_Window_ID() 
{
Integer ii = (Integer)get_Value("AD_Window_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set ColumnNo */
public void setColumnNo (int ColumnNo)
{
set_Value ("ColumnNo", new Integer(ColumnNo));
}
/** Get ColumnNo */
public int getColumnNo() 
{
Integer ii = (Integer)get_Value("ColumnNo");
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
public static final int GOALDISPLAY_AD_Reference_ID = MReference.getReferenceID("GoalDisplay");
/** Chart = C */
public static final String GOALDISPLAY_Chart = "C";
/** HTMLTable = T */
public static final String GOALDISPLAY_HTMLTable = "T";
/** Set GoalDisplay */
public void setGoalDisplay (String GoalDisplay)
{
if (GoalDisplay == null || GoalDisplay.equals("C") || GoalDisplay.equals("T"));
 else throw new IllegalArgumentException ("GoalDisplay Invalid value - Reference = GOALDISPLAY_AD_Reference_ID - C - T");
if (GoalDisplay != null && GoalDisplay.length() > 1)
{
log.warning("Length > 1 - truncated");
GoalDisplay = GoalDisplay.substring(0,1);
}
set_Value ("GoalDisplay", GoalDisplay);
}
/** Get GoalDisplay */
public String getGoalDisplay() 
{
return (String)get_Value("GoalDisplay");
}
/** Set HTML */
public void setHTML (String HTML)
{
if (HTML != null && HTML.length() > 2147483647)
{
log.warning("Length > 2147483647 - truncated");
HTML = HTML.substring(0,2147483647);
}
set_Value ("HTML", HTML);
}
/** Get HTML */
public String getHTML() 
{
return (String)get_Value("HTML");
}
/** Set IsCollapsible */
public void setIsCollapsible (boolean IsCollapsible)
{
set_Value ("IsCollapsible", new Boolean(IsCollapsible));
}
/** Get IsCollapsible */
public boolean isCollapsible() 
{
Object oo = get_Value("IsCollapsible");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsOpenByDefault */
public void setIsOpenByDefault (boolean IsOpenByDefault)
{
set_Value ("IsOpenByDefault", new Boolean(IsOpenByDefault));
}
/** Get IsOpenByDefault */
public boolean isOpenByDefault() 
{
Object oo = get_Value("IsOpenByDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Line No.
Unique line for this document */
public void setLine (BigDecimal Line)
{
set_Value ("Line", Line);
}
/** Get Line No.
Unique line for this document */
public BigDecimal getLine() 
{
BigDecimal bd = (BigDecimal)get_Value("Line");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 120)
{
log.warning("Length > 120 - truncated");
Name = Name.substring(0,120);
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
/** Set PA_DashboardContent_ID */
public void setPA_DashboardContent_ID (int PA_DashboardContent_ID)
{
set_ValueNoCheck ("PA_DashboardContent_ID", new Integer(PA_DashboardContent_ID));
}
/** Get PA_DashboardContent_ID */
public int getPA_DashboardContent_ID() 
{
Integer ii = (Integer)get_Value("PA_DashboardContent_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Goal.
Performance Goal */
public void setPA_Goal_ID (int PA_Goal_ID)
{
if (PA_Goal_ID <= 0) set_Value ("PA_Goal_ID", null);
 else 
set_Value ("PA_Goal_ID", new Integer(PA_Goal_ID));
}
/** Get Goal.
Performance Goal */
public int getPA_Goal_ID() 
{
Integer ii = (Integer)get_Value("PA_Goal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set ZulFilePath */
public void setZulFilePath (String ZulFilePath)
{
if (ZulFilePath != null && ZulFilePath.length() > 255)
{
log.warning("Length > 255 - truncated");
ZulFilePath = ZulFilePath.substring(0,255);
}
set_Value ("ZulFilePath", ZulFilePath);
}
/** Get ZulFilePath */
public String getZulFilePath() 
{
return (String)get_Value("ZulFilePath");
}
}
