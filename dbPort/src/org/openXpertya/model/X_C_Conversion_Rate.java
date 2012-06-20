/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Conversion_Rate
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.359 */
public class X_C_Conversion_Rate extends PO
{
/** Constructor estándar */
public X_C_Conversion_Rate (Properties ctx, int C_Conversion_Rate_ID, String trxName)
{
super (ctx, C_Conversion_Rate_ID, trxName);
/** if (C_Conversion_Rate_ID == 0)
{
setC_ConversionType_ID (0);
setC_Conversion_Rate_ID (0);
setC_Currency_ID (0);
setC_Currency_ID_To (0);
setDivideRate (Env.ZERO);
setMultiplyRate (Env.ZERO);
setValidFrom (new Timestamp(System.currentTimeMillis()));
}
 */
}
/** Load Constructor */
public X_C_Conversion_Rate (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=140 */
public static final int Table_ID=140;

/** TableName=C_Conversion_Rate */
public static final String Table_Name="C_Conversion_Rate";

protected static KeyNamePair Model = new KeyNamePair(140,"C_Conversion_Rate");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Conversion_Rate[").append(getID()).append("]");
return sb.toString();
}
/** Set Currency Type.
Currency Conversion Rate Type */
public void setC_ConversionType_ID (int C_ConversionType_ID)
{
set_Value ("C_ConversionType_ID", new Integer(C_ConversionType_ID));
}
/** Get Currency Type.
Currency Conversion Rate Type */
public int getC_ConversionType_ID() 
{
Integer ii = (Integer)get_Value("C_ConversionType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Conversion Rate.
Rate used for converting currencies */
public void setC_Conversion_Rate_ID (int C_Conversion_Rate_ID)
{
set_ValueNoCheck ("C_Conversion_Rate_ID", new Integer(C_Conversion_Rate_ID));
}
/** Get Conversion Rate.
Rate used for converting currencies */
public int getC_Conversion_Rate_ID() 
{
Integer ii = (Integer)get_Value("C_Conversion_Rate_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Conversion_Rate_ID()));
}
public static final int C_CURRENCY_ID_AD_Reference_ID=112;
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_CURRENCY_ID_TO_AD_Reference_ID=112;
/** Set Currency To.
Target currency */
public void setC_Currency_ID_To (int C_Currency_ID_To)
{
set_Value ("C_Currency_ID_To", new Integer(C_Currency_ID_To));
}
/** Get Currency To.
Target currency */
public int getC_Currency_ID_To() 
{
Integer ii = (Integer)get_Value("C_Currency_ID_To");
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
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
if (ValidFrom == null) throw new IllegalArgumentException ("ValidFrom is mandatory");
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
}
