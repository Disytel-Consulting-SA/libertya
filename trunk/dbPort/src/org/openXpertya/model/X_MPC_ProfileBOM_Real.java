/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por MPC_ProfileBOM_Real
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:36.734 */
public class X_MPC_ProfileBOM_Real extends PO
{
/** Constructor estándar */
public X_MPC_ProfileBOM_Real (Properties ctx, int MPC_ProfileBOM_Real_ID, String trxName)
{
super (ctx, MPC_ProfileBOM_Real_ID, trxName);
/** if (MPC_ProfileBOM_Real_ID == 0)
{
setMPC_ProfileBOM_ID (0);
setMPC_ProfileBOM_Real_ID (0);
setM_Attribute_ID (0);
}
 */
}
/** Load Constructor */
public X_MPC_ProfileBOM_Real (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000004 */
public static final int Table_ID=1000004;

/** TableName=MPC_ProfileBOM_Real */
public static final String Table_Name="MPC_ProfileBOM_Real";

protected static KeyNamePair Model = new KeyNamePair(1000004,"MPC_ProfileBOM_Real");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_MPC_ProfileBOM_Real[").append(getID()).append("]");
return sb.toString();
}
/** Set Printed.
Indicates if this document / line is printed */
public void setIsPrinted (boolean IsPrinted)
{
set_Value ("IsPrinted", new Boolean(IsPrinted));
}
/** Get Printed.
Indicates if this document / line is printed */
public boolean isPrinted() 
{
Object oo = get_Value("IsPrinted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsPrintedMax */
public void setIsPrintedMax (boolean IsPrintedMax)
{
set_Value ("IsPrintedMax", new Boolean(IsPrintedMax));
}
/** Get IsPrintedMax */
public boolean isPrintedMax() 
{
Object oo = get_Value("IsPrintedMax");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set MPC_ProfileBOM_ID */
public void setMPC_ProfileBOM_ID (int MPC_ProfileBOM_ID)
{
set_ValueNoCheck ("MPC_ProfileBOM_ID", new Integer(MPC_ProfileBOM_ID));
}
/** Get MPC_ProfileBOM_ID */
public int getMPC_ProfileBOM_ID() 
{
Integer ii = (Integer)get_Value("MPC_ProfileBOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set MPC_ProfileBOM_Real_ID */
public void setMPC_ProfileBOM_Real_ID (int MPC_ProfileBOM_Real_ID)
{
set_ValueNoCheck ("MPC_ProfileBOM_Real_ID", new Integer(MPC_ProfileBOM_Real_ID));
}
/** Get MPC_ProfileBOM_Real_ID */
public int getMPC_ProfileBOM_Real_ID() 
{
Integer ii = (Integer)get_Value("MPC_ProfileBOM_Real_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute.
Product Attribute */
public void setM_Attribute_ID (int M_Attribute_ID)
{
set_Value ("M_Attribute_ID", new Integer(M_Attribute_ID));
}
/** Get Attribute.
Product Attribute */
public int getM_Attribute_ID() 
{
Integer ii = (Integer)get_Value("M_Attribute_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Maximum */
public void setMaximum (BigDecimal Maximum)
{
set_Value ("Maximum", Maximum);
}
/** Get Maximum */
public BigDecimal getMaximum() 
{
BigDecimal bd = (BigDecimal)get_Value("Maximum");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Minimum */
public void setMinimum (BigDecimal Minimum)
{
set_Value ("Minimum", Minimum);
}
/** Get Minimum */
public BigDecimal getMinimum() 
{
BigDecimal bd = (BigDecimal)get_Value("Minimum");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Realnut */
public void setRealnut (BigDecimal Realnut)
{
set_Value ("Realnut", Realnut);
}
/** Get Realnut */
public BigDecimal getRealnut() 
{
BigDecimal bd = (BigDecimal)get_Value("Realnut");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value != null && Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,39);
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
