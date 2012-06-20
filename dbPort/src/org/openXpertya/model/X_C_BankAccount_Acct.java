/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BankAccount_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:28.468 */
public class X_C_BankAccount_Acct extends PO
{
/** Constructor estándar */
public X_C_BankAccount_Acct (Properties ctx, int C_BankAccount_Acct_ID, String trxName)
{
super (ctx, C_BankAccount_Acct_ID, trxName);
/** if (C_BankAccount_Acct_ID == 0)
{
setB_Asset_Acct (0);
setB_Expense_Acct (0);
setB_InTransit_Acct (0);
setB_InterestExp_Acct (0);
setB_InterestRev_Acct (0);
setB_PaymentSelect_Acct (0);
setB_RevaluationGain_Acct (0);
setB_RevaluationLoss_Acct (0);
setB_SettlementGain_Acct (0);
setB_SettlementLoss_Acct (0);
setB_UnallocatedCash_Acct (0);
setB_Unidentified_Acct (0);
setC_AcctSchema_ID (0);
setC_BankAccount_ID (0);
}
 */
}
/** Load Constructor */
public X_C_BankAccount_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=391 */
public static final int Table_ID=391;

/** TableName=C_BankAccount_Acct */
public static final String Table_Name="C_BankAccount_Acct";

protected static KeyNamePair Model = new KeyNamePair(391,"C_BankAccount_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BankAccount_Acct[").append(getID()).append("]");
return sb.toString();
}
/** Set Bank Asset.
Bank Asset Account */
public void setB_Asset_Acct (int B_Asset_Acct)
{
set_Value ("B_Asset_Acct", new Integer(B_Asset_Acct));
}
/** Get Bank Asset.
Bank Asset Account */
public int getB_Asset_Acct() 
{
Integer ii = (Integer)get_Value("B_Asset_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Expense.
Bank Expense Account */
public void setB_Expense_Acct (int B_Expense_Acct)
{
set_Value ("B_Expense_Acct", new Integer(B_Expense_Acct));
}
/** Get Bank Expense.
Bank Expense Account */
public int getB_Expense_Acct() 
{
Integer ii = (Integer)get_Value("B_Expense_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank In Transit.
Bank In Transit Account */
public void setB_InTransit_Acct (int B_InTransit_Acct)
{
set_Value ("B_InTransit_Acct", new Integer(B_InTransit_Acct));
}
/** Get Bank In Transit.
Bank In Transit Account */
public int getB_InTransit_Acct() 
{
Integer ii = (Integer)get_Value("B_InTransit_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Interest Expense.
Bank Interest Expense Account */
public void setB_InterestExp_Acct (int B_InterestExp_Acct)
{
set_Value ("B_InterestExp_Acct", new Integer(B_InterestExp_Acct));
}
/** Get Bank Interest Expense.
Bank Interest Expense Account */
public int getB_InterestExp_Acct() 
{
Integer ii = (Integer)get_Value("B_InterestExp_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Interest Revenue.
Bank Interest Revenue Account */
public void setB_InterestRev_Acct (int B_InterestRev_Acct)
{
set_Value ("B_InterestRev_Acct", new Integer(B_InterestRev_Acct));
}
/** Get Bank Interest Revenue.
Bank Interest Revenue Account */
public int getB_InterestRev_Acct() 
{
Integer ii = (Integer)get_Value("B_InterestRev_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Selection.
AP Payment Selection Clearing Account */
public void setB_PaymentSelect_Acct (int B_PaymentSelect_Acct)
{
set_Value ("B_PaymentSelect_Acct", new Integer(B_PaymentSelect_Acct));
}
/** Get Payment Selection.
AP Payment Selection Clearing Account */
public int getB_PaymentSelect_Acct() 
{
Integer ii = (Integer)get_Value("B_PaymentSelect_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Revaluation Gain.
Bank Revaluation Gain Account */
public void setB_RevaluationGain_Acct (int B_RevaluationGain_Acct)
{
set_Value ("B_RevaluationGain_Acct", new Integer(B_RevaluationGain_Acct));
}
/** Get Bank Revaluation Gain.
Bank Revaluation Gain Account */
public int getB_RevaluationGain_Acct() 
{
Integer ii = (Integer)get_Value("B_RevaluationGain_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Revaluation Loss.
Bank Revaluation Loss Account */
public void setB_RevaluationLoss_Acct (int B_RevaluationLoss_Acct)
{
set_Value ("B_RevaluationLoss_Acct", new Integer(B_RevaluationLoss_Acct));
}
/** Get Bank Revaluation Loss.
Bank Revaluation Loss Account */
public int getB_RevaluationLoss_Acct() 
{
Integer ii = (Integer)get_Value("B_RevaluationLoss_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Settlement Gain.
Bank Settlement Gain Account */
public void setB_SettlementGain_Acct (int B_SettlementGain_Acct)
{
set_Value ("B_SettlementGain_Acct", new Integer(B_SettlementGain_Acct));
}
/** Get Bank Settlement Gain.
Bank Settlement Gain Account */
public int getB_SettlementGain_Acct() 
{
Integer ii = (Integer)get_Value("B_SettlementGain_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Settlement Loss.
Bank Settlement Loss Account */
public void setB_SettlementLoss_Acct (int B_SettlementLoss_Acct)
{
set_Value ("B_SettlementLoss_Acct", new Integer(B_SettlementLoss_Acct));
}
/** Get Bank Settlement Loss.
Bank Settlement Loss Account */
public int getB_SettlementLoss_Acct() 
{
Integer ii = (Integer)get_Value("B_SettlementLoss_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Unallocated Cash.
Unallocated Cash Clearing Account */
public void setB_UnallocatedCash_Acct (int B_UnallocatedCash_Acct)
{
set_Value ("B_UnallocatedCash_Acct", new Integer(B_UnallocatedCash_Acct));
}
/** Get Unallocated Cash.
Unallocated Cash Clearing Account */
public int getB_UnallocatedCash_Acct() 
{
Integer ii = (Integer)get_Value("B_UnallocatedCash_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank Unidentified Receipts.
Bank Unidentified Receipts Account */
public void setB_Unidentified_Acct (int B_Unidentified_Acct)
{
set_Value ("B_Unidentified_Acct", new Integer(B_Unidentified_Acct));
}
/** Get Bank Unidentified Receipts.
Bank Unidentified Receipts Account */
public int getB_Unidentified_Acct() 
{
Integer ii = (Integer)get_Value("B_Unidentified_Acct");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Bank Account.
Account at the Bank */
public void setC_BankAccount_ID (int C_BankAccount_ID)
{
set_ValueNoCheck ("C_BankAccount_ID", new Integer(C_BankAccount_ID));
}
/** Get Bank Account.
Account at the Bank */
public int getC_BankAccount_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccount_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
