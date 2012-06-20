/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Currency_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.484 */
public class X_C_Currency_Acct extends PO
{
/** Constructor estándar */
public X_C_Currency_Acct (Properties ctx, int C_Currency_Acct_ID, String trxName)
{
super (ctx, C_Currency_Acct_ID, trxName);
/** if (C_Currency_Acct_ID == 0)
{
setC_AcctSchema_ID (0);
setC_Currency_ID (0);
setRealizedGain_Acct (0);
setRealizedLoss_Acct (0);
setUnrealizedGain_Acct (0);
setUnrealizedLoss_Acct (0);
}
 */
}
/** Load Constructor */
public X_C_Currency_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=638 */
public static final int Table_ID=638;

/** TableName=C_Currency_Acct */
public static final String Table_Name="C_Currency_Acct";

protected static KeyNamePair Model = new KeyNamePair(638,"C_Currency_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Currency_Acct[").append(getID()).append("]");
return sb.toString();
}
/** Set Accounting Schema.
Rules for accounting */
public void setC_AcctSchema_ID (int C_AcctSchema_ID)
{
set_ValueNoCheck ("C_AcctSchema_ID", new Integer(C_AcctSchema_ID));
}
/** Get Accounting Schema.
Rules for accounting */
public int getC_AcctSchema_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
set_ValueNoCheck ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Realized Gain Acct.
Realized Gain Account */
public void setRealizedGain_Acct (int RealizedGain_Acct)
{
set_Value ("RealizedGain_Acct", new Integer(RealizedGain_Acct));
}
/** Get Realized Gain Acct.
Realized Gain Account */
public int getRealizedGain_Acct() 
{
Integer ii = (Integer)get_Value("RealizedGain_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Realized Loss Acct.
Realized Loss Account */
public void setRealizedLoss_Acct (int RealizedLoss_Acct)
{
set_Value ("RealizedLoss_Acct", new Integer(RealizedLoss_Acct));
}
/** Get Realized Loss Acct.
Realized Loss Account */
public int getRealizedLoss_Acct() 
{
Integer ii = (Integer)get_Value("RealizedLoss_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Unrealized Gain Acct.
Unrealized Gain Account for currency revaluation */
public void setUnrealizedGain_Acct (int UnrealizedGain_Acct)
{
set_Value ("UnrealizedGain_Acct", new Integer(UnrealizedGain_Acct));
}
/** Get Unrealized Gain Acct.
Unrealized Gain Account for currency revaluation */
public int getUnrealizedGain_Acct() 
{
Integer ii = (Integer)get_Value("UnrealizedGain_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Unrealized Loss Acct.
Unrealized Loss Account for currency revaluation */
public void setUnrealizedLoss_Acct (int UnrealizedLoss_Acct)
{
set_Value ("UnrealizedLoss_Acct", new Integer(UnrealizedLoss_Acct));
}
/** Get Unrealized Loss Acct.
Unrealized Loss Account for currency revaluation */
public int getUnrealizedLoss_Acct() 
{
Integer ii = (Integer)get_Value("UnrealizedLoss_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
