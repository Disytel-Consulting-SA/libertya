/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Tax_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:33.484 */
public class X_C_Tax_Acct extends PO
{
/** Constructor estándar */
public X_C_Tax_Acct (Properties ctx, int C_Tax_Acct_ID, String trxName)
{
super (ctx, C_Tax_Acct_ID, trxName);
/** if (C_Tax_Acct_ID == 0)
{
setC_AcctSchema_ID (0);
setC_Tax_ID (0);
setT_Credit_Acct (0);
setT_Due_Acct (0);
setT_Expense_Acct (0);
setT_Liability_Acct (0);
setT_Receivables_Acct (0);
}
 */
}
/** Load Constructor */
public X_C_Tax_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=399 */
public static final int Table_ID=399;

/** TableName=C_Tax_Acct */
public static final String Table_Name="C_Tax_Acct";

protected static KeyNamePair Model = new KeyNamePair(399,"C_Tax_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Tax_Acct[").append(getID()).append("]");
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
/** Set Tax.
Tax identifier */
public void setC_Tax_ID (int C_Tax_ID)
{
set_ValueNoCheck ("C_Tax_ID", new Integer(C_Tax_ID));
}
/** Get Tax.
Tax identifier */
public int getC_Tax_ID() 
{
Integer ii = (Integer)get_Value("C_Tax_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax Credit.
Account for Tax you can reclaim */
public void setT_Credit_Acct (int T_Credit_Acct)
{
set_Value ("T_Credit_Acct", new Integer(T_Credit_Acct));
}
/** Get Tax Credit.
Account for Tax you can reclaim */
public int getT_Credit_Acct() 
{
Integer ii = (Integer)get_Value("T_Credit_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax Due.
Account for Tax you have to pay */
public void setT_Due_Acct (int T_Due_Acct)
{
set_Value ("T_Due_Acct", new Integer(T_Due_Acct));
}
/** Get Tax Due.
Account for Tax you have to pay */
public int getT_Due_Acct() 
{
Integer ii = (Integer)get_Value("T_Due_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax Expense.
Account for paid tax you cannot reclaim */
public void setT_Expense_Acct (int T_Expense_Acct)
{
set_Value ("T_Expense_Acct", new Integer(T_Expense_Acct));
}
/** Get Tax Expense.
Account for paid tax you cannot reclaim */
public int getT_Expense_Acct() 
{
Integer ii = (Integer)get_Value("T_Expense_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax Liability.
Account for Tax declaration liability */
public void setT_Liability_Acct (int T_Liability_Acct)
{
set_Value ("T_Liability_Acct", new Integer(T_Liability_Acct));
}
/** Get Tax Liability.
Account for Tax declaration liability */
public int getT_Liability_Acct() 
{
Integer ii = (Integer)get_Value("T_Liability_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tax Receivables.
Account for Tax credit after tax declaration */
public void setT_Receivables_Acct (int T_Receivables_Acct)
{
set_Value ("T_Receivables_Acct", new Integer(T_Receivables_Acct));
}
/** Get Tax Receivables.
Account for Tax credit after tax declaration */
public int getT_Receivables_Acct() 
{
Integer ii = (Integer)get_Value("T_Receivables_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
