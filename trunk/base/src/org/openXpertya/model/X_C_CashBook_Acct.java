/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_CashBook_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:28.937 */
public class X_C_CashBook_Acct extends PO
{
/** Constructor estándar */
public X_C_CashBook_Acct (Properties ctx, int C_CashBook_Acct_ID, String trxName)
{
super (ctx, C_CashBook_Acct_ID, trxName);
/** if (C_CashBook_Acct_ID == 0)
{
setCB_Asset_Acct (0);
setCB_CashTransfer_Acct (0);
setCB_Differences_Acct (0);
setCB_Expense_Acct (0);
setCB_Receipt_Acct (0);
setC_AcctSchema_ID (0);
setC_CashBook_ID (0);
}
 */
}
/** Load Constructor */
public X_C_CashBook_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=409 */
public static final int Table_ID=409;

/** TableName=C_CashBook_Acct */
public static final String Table_Name="C_CashBook_Acct";

protected static KeyNamePair Model = new KeyNamePair(409,"C_CashBook_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_CashBook_Acct[").append(getID()).append("]");
return sb.toString();
}
/** Set Cash Book Asset.
Cash Book Asset Account */
public void setCB_Asset_Acct (int CB_Asset_Acct)
{
set_Value ("CB_Asset_Acct", new Integer(CB_Asset_Acct));
}
/** Get Cash Book Asset.
Cash Book Asset Account */
public int getCB_Asset_Acct() 
{
Integer ii = (Integer)get_Value("CB_Asset_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cash Transfer.
Cash Transfer Clearing Account */
public void setCB_CashTransfer_Acct (int CB_CashTransfer_Acct)
{
set_Value ("CB_CashTransfer_Acct", new Integer(CB_CashTransfer_Acct));
}
/** Get Cash Transfer.
Cash Transfer Clearing Account */
public int getCB_CashTransfer_Acct() 
{
Integer ii = (Integer)get_Value("CB_CashTransfer_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cash Book Differences.
Cash Book Differences Account */
public void setCB_Differences_Acct (int CB_Differences_Acct)
{
set_Value ("CB_Differences_Acct", new Integer(CB_Differences_Acct));
}
/** Get Cash Book Differences.
Cash Book Differences Account */
public int getCB_Differences_Acct() 
{
Integer ii = (Integer)get_Value("CB_Differences_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cash Book Expense.
Cash Book Expense Account */
public void setCB_Expense_Acct (int CB_Expense_Acct)
{
set_Value ("CB_Expense_Acct", new Integer(CB_Expense_Acct));
}
/** Get Cash Book Expense.
Cash Book Expense Account */
public int getCB_Expense_Acct() 
{
Integer ii = (Integer)get_Value("CB_Expense_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cash Book Receipt.
Cash Book Receipts Account */
public void setCB_Receipt_Acct (int CB_Receipt_Acct)
{
set_Value ("CB_Receipt_Acct", new Integer(CB_Receipt_Acct));
}
/** Get Cash Book Receipt.
Cash Book Receipts Account */
public int getCB_Receipt_Acct() 
{
Integer ii = (Integer)get_Value("CB_Receipt_Acct");
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
/** Set Cash Book.
Cash Book for recording petty cash transactions */
public void setC_CashBook_ID (int C_CashBook_ID)
{
set_ValueNoCheck ("C_CashBook_ID", new Integer(C_CashBook_ID));
}
/** Get Cash Book.
Cash Book for recording petty cash transactions */
public int getC_CashBook_ID() 
{
Integer ii = (Integer)get_Value("C_CashBook_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
