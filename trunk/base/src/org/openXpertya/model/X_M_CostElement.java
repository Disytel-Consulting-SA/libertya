/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_CostElement
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:37.343 */
public class X_M_CostElement extends PO
{
/** Constructor estándar */
public X_M_CostElement (Properties ctx, int M_CostElement_ID, String trxName)
{
super (ctx, M_CostElement_ID, trxName);
/** if (M_CostElement_ID == 0)
{
setCostElementType (null);
setIsCalculated (false);
setM_CostElement_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_M_CostElement (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=770 */
public static final int Table_ID=770;

/** TableName=M_CostElement */
public static final String Table_Name="M_CostElement";

protected static KeyNamePair Model = new KeyNamePair(770,"M_CostElement");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_CostElement[").append(getID()).append("]");
return sb.toString();
}
public static final int COSTELEMENTTYPE_AD_Reference_ID=338;
/** Material = M */
public static final String COSTELEMENTTYPE_Material = "M";
/** Overhead = O */
public static final String COSTELEMENTTYPE_Overhead = "O";
/** Burden (M.Overhead) = B */
public static final String COSTELEMENTTYPE_BurdenMOverhead = "B";
/** Outside Processing = X */
public static final String COSTELEMENTTYPE_OutsideProcessing = "X";
/** Resource = R */
public static final String COSTELEMENTTYPE_Resource = "R";
/** Set Cost Element Type.
Type of Cost Element */
public void setCostElementType (String CostElementType)
{
if (CostElementType.equals("M") || CostElementType.equals("O") || CostElementType.equals("B") || CostElementType.equals("X") || CostElementType.equals("R"));
 else throw new IllegalArgumentException ("CostElementType Invalid value - Reference_ID=338 - M - O - B - X - R");
if (CostElementType == null) throw new IllegalArgumentException ("CostElementType is mandatory");
if (CostElementType.length() > 1)
{
log.warning("Length > 1 - truncated");
CostElementType = CostElementType.substring(0,0);
}
set_Value ("CostElementType", CostElementType);
}
/** Get Cost Element Type.
Type of Cost Element */
public String getCostElementType() 
{
return (String)get_Value("CostElementType");
}
public static final int COSTINGMETHOD_AD_Reference_ID=122;
/** Standard Costing = S */
public static final String COSTINGMETHOD_StandardCosting = "S";
/** Average = A */
public static final String COSTINGMETHOD_Average = "A";
/** Lifo = L */
public static final String COSTINGMETHOD_Lifo = "L";
/** Fifo = F */
public static final String COSTINGMETHOD_Fifo = "F";
/** Last PO Price = P */
public static final String COSTINGMETHOD_LastPOPrice = "P";
/** Set Costing Method.
Indicates how Costs will be calculated */
public void setCostingMethod (String CostingMethod)
{
if (CostingMethod == null || CostingMethod.equals("S") || CostingMethod.equals("A") || CostingMethod.equals("L") || CostingMethod.equals("F") || CostingMethod.equals("P"));
 else throw new IllegalArgumentException ("CostingMethod Invalid value - Reference_ID=122 - S - A - L - F - P");
if (CostingMethod != null && CostingMethod.length() > 1)
{
log.warning("Length > 1 - truncated");
CostingMethod = CostingMethod.substring(0,0);
}
set_Value ("CostingMethod", CostingMethod);
}
/** Get Costing Method.
Indicates how Costs will be calculated */
public String getCostingMethod() 
{
return (String)get_Value("CostingMethod");
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
/** Set Calculated.
The value is calculated by the system */
public void setIsCalculated (boolean IsCalculated)
{
set_Value ("IsCalculated", new Boolean(IsCalculated));
}
/** Get Calculated.
The value is calculated by the system */
public boolean isCalculated() 
{
Object oo = get_Value("IsCalculated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Cost Element.
Product Cost Element */
public void setM_CostElement_ID (int M_CostElement_ID)
{
set_ValueNoCheck ("M_CostElement_ID", new Integer(M_CostElement_ID));
}
/** Get Cost Element.
Product Cost Element */
public int getM_CostElement_ID() 
{
Integer ii = (Integer)get_Value("M_CostElement_ID");
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
}
