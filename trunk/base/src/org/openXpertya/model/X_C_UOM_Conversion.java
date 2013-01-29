/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_UOM_Conversion
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:33.571 */
public class X_C_UOM_Conversion extends PO
{
/** Constructor estándar */
public X_C_UOM_Conversion (Properties ctx, int C_UOM_Conversion_ID, String trxName)
{
super (ctx, C_UOM_Conversion_ID, trxName);
/** if (C_UOM_Conversion_ID == 0)
{
setC_UOM_Conversion_ID (0);
setC_UOM_ID (0);
setC_UOM_To_ID (0);
setDivideRate (Env.ZERO);
setMultiplyRate (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_UOM_Conversion (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=175 */
public static final int Table_ID=175;

/** TableName=C_UOM_Conversion */
public static final String Table_Name="C_UOM_Conversion";

protected static KeyNamePair Model = new KeyNamePair(175,"C_UOM_Conversion");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_UOM_Conversion[").append(getID()).append("]");
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
/** Set UOM Conversion.
Unit of Measure Conversion */
public void setC_UOM_Conversion_ID (int C_UOM_Conversion_ID)
{
set_ValueNoCheck ("C_UOM_Conversion_ID", new Integer(C_UOM_Conversion_ID));
}
/** Get UOM Conversion.
Unit of Measure Conversion */
public int getC_UOM_Conversion_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_Conversion_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_UOM_Conversion_ID()));
}
public static final int C_UOM_ID_AD_Reference_ID=114;
/** Set UOM.
Unit of Measure */
public void setC_UOM_ID (int C_UOM_ID)
{
set_Value ("C_UOM_ID", new Integer(C_UOM_ID));
}
/** Get UOM.
Unit of Measure */
public int getC_UOM_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_UOM_TO_ID_AD_Reference_ID=114;
/** Set UoM To.
Target or destination Unit of Measure */
public void setC_UOM_To_ID (int C_UOM_To_ID)
{
set_Value ("C_UOM_To_ID", new Integer(C_UOM_To_ID));
}
/** Get UoM To.
Target or destination Unit of Measure */
public int getC_UOM_To_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_To_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Divide Rate.
To convert Source number to Target number, the Source is divided */
public void setDivideRate (BigDecimal DivideRate)
{
if (DivideRate == null) throw new IllegalArgumentException ("DivideRate is mandatory");
set_Value ("DivideRate", DivideRate);
}
/** Get Divide Rate.
To convert Source number to Target number, the Source is divided */
public BigDecimal getDivideRate() 
{
BigDecimal bd = (BigDecimal)get_Value("DivideRate");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Multiply Rate.
Rate to multiple the source by to calculate the target. */
public void setMultiplyRate (BigDecimal MultiplyRate)
{
if (MultiplyRate == null) throw new IllegalArgumentException ("MultiplyRate is mandatory");
set_Value ("MultiplyRate", MultiplyRate);
}
/** Get Multiply Rate.
Rate to multiple the source by to calculate the target. */
public BigDecimal getMultiplyRate() 
{
BigDecimal bd = (BigDecimal)get_Value("MultiplyRate");
if (bd == null) return Env.ZERO;
return bd;
}
}
