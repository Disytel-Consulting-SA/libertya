/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BP_Employee_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.75 */
public class X_C_BP_Employee_Acct extends PO
{
/** Constructor estándar */
public X_C_BP_Employee_Acct (Properties ctx, int C_BP_Employee_Acct_ID, String trxName)
{
super (ctx, C_BP_Employee_Acct_ID, trxName);
/** if (C_BP_Employee_Acct_ID == 0)
{
setC_AcctSchema_ID (0);
setC_BPartner_ID (0);
setE_Expense_Acct (0);
setE_Prepayment_Acct (0);
}
 */
}
/** Load Constructor */
public X_C_BP_Employee_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=184 */
public static final int Table_ID=184;

/** TableName=C_BP_Employee_Acct */
public static final String Table_Name="C_BP_Employee_Acct";

protected static KeyNamePair Model = new KeyNamePair(184,"C_BP_Employee_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BP_Employee_Acct[").append(getID()).append("]");
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
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_ValueNoCheck ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Employee Expense.
Account for Employee Expenses */
public void setE_Expense_Acct (int E_Expense_Acct)
{
set_Value ("E_Expense_Acct", new Integer(E_Expense_Acct));
}
/** Get Employee Expense.
Account for Employee Expenses */
public int getE_Expense_Acct() 
{
Integer ii = (Integer)get_Value("E_Expense_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Employee Prepayment.
Account for Employee Expense Prepayments */
public void setE_Prepayment_Acct (int E_Prepayment_Acct)
{
set_Value ("E_Prepayment_Acct", new Integer(E_Prepayment_Acct));
}
/** Get Employee Prepayment.
Account for Employee Expense Prepayments */
public int getE_Prepayment_Acct() 
{
Integer ii = (Integer)get_Value("E_Prepayment_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
