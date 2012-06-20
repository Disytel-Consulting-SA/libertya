/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RetSchema_Range
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:33.148 */
public class X_C_RetSchema_Range extends PO
{
/** Constructor estándar */
public X_C_RetSchema_Range (Properties ctx, int C_RetSchema_Range_ID, String trxName)
{
super (ctx, C_RetSchema_Range_ID, trxName);
/** if (C_RetSchema_Range_ID == 0)
{
setC_RetSchema_Config_ID (0);
setC_RetSchema_Range_ID (0);
}
 */
}
/** Load Constructor */
public X_C_RetSchema_Range (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000147 */
public static final int Table_ID=1000147;

/** TableName=C_RetSchema_Range */
public static final String Table_Name="C_RetSchema_Range";

protected static KeyNamePair Model = new KeyNamePair(1000147,"C_RetSchema_Range");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RetSchema_Range[").append(getID()).append("]");
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
/** Set Retencion Schema Config */
public void setC_RetSchema_Config_ID (int C_RetSchema_Config_ID)
{
set_ValueNoCheck ("C_RetSchema_Config_ID", new Integer(C_RetSchema_Config_ID));
}
/** Get Retencion Schema Config */
public int getC_RetSchema_Config_ID() 
{
Integer ii = (Integer)get_Value("C_RetSchema_Config_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Schema Range */
public void setC_RetSchema_Range_ID (int C_RetSchema_Range_ID)
{
set_ValueNoCheck ("C_RetSchema_Range_ID", new Integer(C_RetSchema_Range_ID));
}
/** Get Retencion Schema Range */
public int getC_RetSchema_Range_ID() 
{
Integer ii = (Integer)get_Value("C_RetSchema_Range_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Apply Value */
public void setvalue_apply (BigDecimal value_apply)
{
set_Value ("value_apply", value_apply);
}
/** Get Apply Value */
public BigDecimal getvalue_apply() 
{
BigDecimal bd = (BigDecimal)get_Value("value_apply");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set From Value */
public void setvalue_from (BigDecimal value_from)
{
set_Value ("value_from", value_from);
}
/** Get From Value */
public BigDecimal getvalue_from() 
{
BigDecimal bd = (BigDecimal)get_Value("value_from");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set To Value */
public void setvalue_to (BigDecimal value_to)
{
set_Value ("value_to", value_to);
}
/** Get To Value */
public BigDecimal getvalue_to() 
{
BigDecimal bd = (BigDecimal)get_Value("value_to");
if (bd == null) return Env.ZERO;
return bd;
}
}
