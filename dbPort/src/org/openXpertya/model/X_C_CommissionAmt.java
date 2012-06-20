/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CommissionAmt
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.203 */
public class X_C_CommissionAmt extends PO
{
/** Constructor estándar */
public X_C_CommissionAmt (Properties ctx, int C_CommissionAmt_ID, String trxName)
{
super (ctx, C_CommissionAmt_ID, trxName);
/** if (C_CommissionAmt_ID == 0)
{
setActualQty (Env.ZERO);
setC_CommissionAmt_ID (0);
setC_CommissionLine_ID (0);
setC_CommissionRun_ID (0);
setCommissionAmt (Env.ZERO);
setConvertedAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_CommissionAmt (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=430 */
public static final int Table_ID=430;

/** TableName=C_CommissionAmt */
public static final String Table_Name="C_CommissionAmt";

protected static KeyNamePair Model = new KeyNamePair(430,"C_CommissionAmt");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CommissionAmt[").append(getID()).append("]");
return sb.toString();
}
/** Set Actual Quantity.
The actual quantity */
public void setActualQty (BigDecimal ActualQty)
{
if (ActualQty == null) throw new IllegalArgumentException ("ActualQty is mandatory");
set_Value ("ActualQty", ActualQty);
}
/** Get Actual Quantity.
The actual quantity */
public BigDecimal getActualQty() 
{
BigDecimal bd = (BigDecimal)get_Value("ActualQty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Commission Amount.
Generated Commission Amount  */
public void setC_CommissionAmt_ID (int C_CommissionAmt_ID)
{
set_ValueNoCheck ("C_CommissionAmt_ID", new Integer(C_CommissionAmt_ID));
}
/** Get Commission Amount.
Generated Commission Amount  */
public int getC_CommissionAmt_ID() 
{
Integer ii = (Integer)get_Value("C_CommissionAmt_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Commission Line.
Commission Line */
public void setC_CommissionLine_ID (int C_CommissionLine_ID)
{
set_Value ("C_CommissionLine_ID", new Integer(C_CommissionLine_ID));
}
/** Get Commission Line.
Commission Line */
public int getC_CommissionLine_ID() 
{
Integer ii = (Integer)get_Value("C_CommissionLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Commission Run.
Commission Run or Process */
public void setC_CommissionRun_ID (int C_CommissionRun_ID)
{
set_ValueNoCheck ("C_CommissionRun_ID", new Integer(C_CommissionRun_ID));
}
/** Get Commission Run.
Commission Run or Process */
public int getC_CommissionRun_ID() 
{
Integer ii = (Integer)get_Value("C_CommissionRun_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_CommissionRun_ID()));
}
/** Set Commission Amount.
Commission Amount */
public void setCommissionAmt (BigDecimal CommissionAmt)
{
if (CommissionAmt == null) throw new IllegalArgumentException ("CommissionAmt is mandatory");
set_Value ("CommissionAmt", CommissionAmt);
}
/** Get Commission Amount.
Commission Amount */
public BigDecimal getCommissionAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CommissionAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Converted Amount.
Converted Amount */
public void setConvertedAmt (BigDecimal ConvertedAmt)
{
if (ConvertedAmt == null) throw new IllegalArgumentException ("ConvertedAmt is mandatory");
set_Value ("ConvertedAmt", ConvertedAmt);
}
/** Get Converted Amount.
Converted Amount */
public BigDecimal getConvertedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ConvertedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
}
