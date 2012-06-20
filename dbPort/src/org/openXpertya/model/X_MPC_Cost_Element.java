/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_Cost_Element
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:35.671 */
public class X_MPC_Cost_Element extends PO
{
/** Constructor estándar */
public X_MPC_Cost_Element (Properties ctx, int MPC_Cost_Element_ID, String trxName)
{
super (ctx, MPC_Cost_Element_ID, trxName);
/** if (MPC_Cost_Element_ID == 0)
{
setIsSimulation (false);	// N
setMPC_Cost_Element_ID (0);
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_MPC_Cost_Element (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000020 */
public static final int Table_ID=1000020;

/** TableName=MPC_Cost_Element */
public static final String Table_Name="MPC_Cost_Element";

protected static KeyNamePair Model = new KeyNamePair(1000020,"MPC_Cost_Element");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_Cost_Element[").append(getID()).append("]");
return sb.toString();
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
/** Set Formula Calculation */
public void setFormulaCalculation (String FormulaCalculation)
{
if (FormulaCalculation != null && FormulaCalculation.length() > 500)
{
log.warning("Length > 500 - truncated");
FormulaCalculation = FormulaCalculation.substring(0,499);
}
set_Value ("FormulaCalculation", FormulaCalculation);
}
/** Get Formula Calculation */
public String getFormulaCalculation() 
{
return (String)get_Value("FormulaCalculation");
}
/** Set Simulation.
Performing the function is only simulated */
public void setIsSimulation (boolean IsSimulation)
{
set_Value ("IsSimulation", new Boolean(IsSimulation));
}
/** Get Simulation.
Performing the function is only simulated */
public boolean isSimulation() 
{
Object oo = get_Value("IsSimulation");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Cost Element CMPCS.
ID of the cost element(an element of a cost type) */
public void setMPC_Cost_Element_ID (int MPC_Cost_Element_ID)
{
set_ValueNoCheck ("MPC_Cost_Element_ID", new Integer(MPC_Cost_Element_ID));
}
/** Get Cost Element CMPCS.
ID of the cost element(an element of a cost type) */
public int getMPC_Cost_Element_ID() 
{
Integer ii = (Integer)get_Value("MPC_Cost_Element_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int MPC_ELEMENTTYPE_AD_Reference_ID=1000018;
/** Material = M */
public static final String MPC_ELEMENTTYPE_Material = "M";
/** Labor = L */
public static final String MPC_ELEMENTTYPE_Labor = "L";
/** Burden = B */
public static final String MPC_ELEMENTTYPE_Burden = "B";
/** Overhead = O */
public static final String MPC_ELEMENTTYPE_Overhead = "O";
/** Subcontract = S */
public static final String MPC_ELEMENTTYPE_Subcontract = "S";
/** Distribution = D */
public static final String MPC_ELEMENTTYPE_Distribution = "D";
/** Set Cost Element Type CMPCS.
A group of Cost Elements */
public void setMPC_ElementType (String MPC_ElementType)
{
if (MPC_ElementType == null || MPC_ElementType.equals("M") || MPC_ElementType.equals("L") || MPC_ElementType.equals("B") || MPC_ElementType.equals("O") || MPC_ElementType.equals("S") || MPC_ElementType.equals("D"));
 else throw new IllegalArgumentException ("MPC_ElementType Invalid value - Reference_ID=1000018 - M - L - B - O - S - D");
if (MPC_ElementType != null && MPC_ElementType.length() > 1)
{
log.warning("Length > 1 - truncated");
MPC_ElementType = MPC_ElementType.substring(0,0);
}
set_ValueNoCheck ("MPC_ElementType", MPC_ElementType);
}
/** Get Cost Element Type CMPCS.
A group of Cost Elements */
public String getMPC_ElementType() 
{
return (String)get_Value("MPC_ElementType");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 120)
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
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
}
