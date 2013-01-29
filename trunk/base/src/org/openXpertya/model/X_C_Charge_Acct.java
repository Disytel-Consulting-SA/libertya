/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Charge_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.093 */
public class X_C_Charge_Acct extends PO
{
/** Constructor estándar */
public X_C_Charge_Acct (Properties ctx, int C_Charge_Acct_ID, String trxName)
{
super (ctx, C_Charge_Acct_ID, trxName);
/** if (C_Charge_Acct_ID == 0)
{
setC_AcctSchema_ID (0);
setC_Charge_ID (0);
setCh_Expense_Acct (0);
setCh_Revenue_Acct (0);
}
 */
}
/** Load Constructor */
public X_C_Charge_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=396 */
public static final int Table_ID=396;

/** TableName=C_Charge_Acct */
public static final String Table_Name="C_Charge_Acct";

protected static KeyNamePair Model = new KeyNamePair(396,"C_Charge_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Charge_Acct[").append(getID()).append("]");
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
/** Set Charge.
Additional document charges */
public void setC_Charge_ID (int C_Charge_ID)
{
set_ValueNoCheck ("C_Charge_ID", new Integer(C_Charge_ID));
}
/** Get Charge.
Additional document charges */
public int getC_Charge_ID() 
{
Integer ii = (Integer)get_Value("C_Charge_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Charge Expense.
Charge Expense Account */
public void setCh_Expense_Acct (int Ch_Expense_Acct)
{
set_Value ("Ch_Expense_Acct", new Integer(Ch_Expense_Acct));
}
/** Get Charge Expense.
Charge Expense Account */
public int getCh_Expense_Acct() 
{
Integer ii = (Integer)get_Value("Ch_Expense_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Charge Revenue.
Charge Revenue Account */
public void setCh_Revenue_Acct (int Ch_Revenue_Acct)
{
set_Value ("Ch_Revenue_Acct", new Integer(Ch_Revenue_Acct));
}
/** Get Charge Revenue.
Charge Revenue Account */
public int getCh_Revenue_Acct() 
{
Integer ii = (Integer)get_Value("Ch_Revenue_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
