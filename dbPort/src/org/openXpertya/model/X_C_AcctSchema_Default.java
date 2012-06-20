/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_AcctSchema_Default
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.312 */
public class X_C_AcctSchema_Default extends PO
{
/** Constructor estándar */
public X_C_AcctSchema_Default (Properties ctx, int C_AcctSchema_Default_ID, String trxName)
{
super (ctx, C_AcctSchema_Default_ID, trxName);
/** if (C_AcctSchema_Default_ID == 0)
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
setCB_Asset_Acct (0);
setCB_CashTransfer_Acct (0);
setCB_Differences_Acct (0);
setCB_Expense_Acct (0);
setCB_Receipt_Acct (0);
setC_AcctSchema_ID (0);
setC_Prepayment_Acct (0);
setC_Receivable_Acct (0);
setCh_Expense_Acct (0);
setCh_Revenue_Acct (0);
setE_Expense_Acct (0);
setE_Prepayment_Acct (0);
setNotInvoicedReceipts_Acct (0);
setNotInvoicedReceivables_Acct (0);
setNotInvoicedRevenue_Acct (0);
setPJ_Asset_Acct (0);
setPJ_WIP_Acct (0);
setP_Asset_Acct (0);
setP_Cogs_Acct (0);
setP_Expense_Acct (0);
setP_InvoicePriceVariance_Acct (0);
setP_PurchasePriceVariance_Acct (0);
setP_Revenue_Acct (0);
setP_TradeDiscountGrant_Acct (0);
setP_TradeDiscountRec_Acct (0);
setPayDiscount_Exp_Acct (0);
setPayDiscount_Rev_Acct (0);
setRealizedGain_Acct (0);
setRealizedLoss_Acct (0);
setT_Credit_Acct (0);
setT_Due_Acct (0);
setT_Expense_Acct (0);
setT_Liability_Acct (0);
setT_Receivables_Acct (0);
setUnEarnedRevenue_Acct (0);
setUnrealizedGain_Acct (0);
setUnrealizedLoss_Acct (0);
setV_Liability_Acct (0);
setV_Liability_Services_Acct (0);
setV_Prepayment_Acct (0);
setW_Differences_Acct (0);
setW_InvActualAdjust_Acct (0);
setW_Inventory_Acct (0);
setW_Revaluation_Acct (0);
setWithholding_Acct (0);
setWriteOff_Acct (0);
}
 */
}
/** Load Constructor */
public X_C_AcctSchema_Default (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=315 */
public static final int Table_ID=315;

/** TableName=C_AcctSchema_Default */
public static final String Table_Name="C_AcctSchema_Default";

protected static KeyNamePair Model = new KeyNamePair(315,"C_AcctSchema_Default");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_AcctSchema_Default[").append(getID()).append("]");
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_AcctSchema_ID()));
}
/** Set Customer Prepayment.
Account for customer prepayments */
public void setC_Prepayment_Acct (int C_Prepayment_Acct)
{
set_Value ("C_Prepayment_Acct", new Integer(C_Prepayment_Acct));
}
/** Get Customer Prepayment.
Account for customer prepayments */
public int getC_Prepayment_Acct() 
{
Integer ii = (Integer)get_Value("C_Prepayment_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Customer Receivables.
Account for Customer Receivables */
public void setC_Receivable_Acct (int C_Receivable_Acct)
{
set_Value ("C_Receivable_Acct", new Integer(C_Receivable_Acct));
}
/** Get Customer Receivables.
Account for Customer Receivables */
public int getC_Receivable_Acct() 
{
Integer ii = (Integer)get_Value("C_Receivable_Acct");
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
/** Set Not-invoiced Receipts.
Account for not-invoiced Material Receipts */
public void setNotInvoicedReceipts_Acct (int NotInvoicedReceipts_Acct)
{
set_Value ("NotInvoicedReceipts_Acct", new Integer(NotInvoicedReceipts_Acct));
}
/** Get Not-invoiced Receipts.
Account for not-invoiced Material Receipts */
public int getNotInvoicedReceipts_Acct() 
{
Integer ii = (Integer)get_Value("NotInvoicedReceipts_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Not-invoiced Receivables.
Account for not invoiced Receivables */
public void setNotInvoicedReceivables_Acct (int NotInvoicedReceivables_Acct)
{
set_Value ("NotInvoicedReceivables_Acct", new Integer(NotInvoicedReceivables_Acct));
}
/** Get Not-invoiced Receivables.
Account for not invoiced Receivables */
public int getNotInvoicedReceivables_Acct() 
{
Integer ii = (Integer)get_Value("NotInvoicedReceivables_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Not-invoiced Revenue.
Account for not invoiced Revenue */
public void setNotInvoicedRevenue_Acct (int NotInvoicedRevenue_Acct)
{
set_Value ("NotInvoicedRevenue_Acct", new Integer(NotInvoicedRevenue_Acct));
}
/** Get Not-invoiced Revenue.
Account for not invoiced Revenue */
public int getNotInvoicedRevenue_Acct() 
{
Integer ii = (Integer)get_Value("NotInvoicedRevenue_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project Asset.
Project Asset Account */
public void setPJ_Asset_Acct (int PJ_Asset_Acct)
{
set_Value ("PJ_Asset_Acct", new Integer(PJ_Asset_Acct));
}
/** Get Project Asset.
Project Asset Account */
public int getPJ_Asset_Acct() 
{
Integer ii = (Integer)get_Value("PJ_Asset_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Work In Progress.
Account for Work in Progress */
public void setPJ_WIP_Acct (int PJ_WIP_Acct)
{
set_Value ("PJ_WIP_Acct", new Integer(PJ_WIP_Acct));
}
/** Get Work In Progress.
Account for Work in Progress */
public int getPJ_WIP_Acct() 
{
Integer ii = (Integer)get_Value("PJ_WIP_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product Asset.
Account for Product Asset (Inventory) */
public void setP_Asset_Acct (int P_Asset_Acct)
{
set_Value ("P_Asset_Acct", new Integer(P_Asset_Acct));
}
/** Get Product Asset.
Account for Product Asset (Inventory) */
public int getP_Asset_Acct() 
{
Integer ii = (Integer)get_Value("P_Asset_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product COGS.
Account for Cost of Goods Sold */
public void setP_Cogs_Acct (int P_Cogs_Acct)
{
set_Value ("P_Cogs_Acct", new Integer(P_Cogs_Acct));
}
/** Get Product COGS.
Account for Cost of Goods Sold */
public int getP_Cogs_Acct() 
{
Integer ii = (Integer)get_Value("P_Cogs_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product Expense.
Account for Product Expense */
public void setP_Expense_Acct (int P_Expense_Acct)
{
set_Value ("P_Expense_Acct", new Integer(P_Expense_Acct));
}
/** Get Product Expense.
Account for Product Expense */
public int getP_Expense_Acct() 
{
Integer ii = (Integer)get_Value("P_Expense_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Invoice Price Variance.
Difference between Costs and Invoice Price (IPV) */
public void setP_InvoicePriceVariance_Acct (int P_InvoicePriceVariance_Acct)
{
set_Value ("P_InvoicePriceVariance_Acct", new Integer(P_InvoicePriceVariance_Acct));
}
/** Get Invoice Price Variance.
Difference between Costs and Invoice Price (IPV) */
public int getP_InvoicePriceVariance_Acct() 
{
Integer ii = (Integer)get_Value("P_InvoicePriceVariance_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Purchase Price Variance.
Difference between Standard Cost and Purchase Price (PPV) */
public void setP_PurchasePriceVariance_Acct (int P_PurchasePriceVariance_Acct)
{
set_Value ("P_PurchasePriceVariance_Acct", new Integer(P_PurchasePriceVariance_Acct));
}
/** Get Purchase Price Variance.
Difference between Standard Cost and Purchase Price (PPV) */
public int getP_PurchasePriceVariance_Acct() 
{
Integer ii = (Integer)get_Value("P_PurchasePriceVariance_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product Revenue.
Account for Product Revenue (Sales Account) */
public void setP_Revenue_Acct (int P_Revenue_Acct)
{
set_Value ("P_Revenue_Acct", new Integer(P_Revenue_Acct));
}
/** Get Product Revenue.
Account for Product Revenue (Sales Account) */
public int getP_Revenue_Acct() 
{
Integer ii = (Integer)get_Value("P_Revenue_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Trade Discount Granted.
Trade Discount Granted Account */
public void setP_TradeDiscountGrant_Acct (int P_TradeDiscountGrant_Acct)
{
set_Value ("P_TradeDiscountGrant_Acct", new Integer(P_TradeDiscountGrant_Acct));
}
/** Get Trade Discount Granted.
Trade Discount Granted Account */
public int getP_TradeDiscountGrant_Acct() 
{
Integer ii = (Integer)get_Value("P_TradeDiscountGrant_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Trade Discount Received.
Trade Discount Receivable Account */
public void setP_TradeDiscountRec_Acct (int P_TradeDiscountRec_Acct)
{
set_Value ("P_TradeDiscountRec_Acct", new Integer(P_TradeDiscountRec_Acct));
}
/** Get Trade Discount Received.
Trade Discount Receivable Account */
public int getP_TradeDiscountRec_Acct() 
{
Integer ii = (Integer)get_Value("P_TradeDiscountRec_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Discount Expense.
Payment Discount Expense Account */
public void setPayDiscount_Exp_Acct (int PayDiscount_Exp_Acct)
{
set_Value ("PayDiscount_Exp_Acct", new Integer(PayDiscount_Exp_Acct));
}
/** Get Payment Discount Expense.
Payment Discount Expense Account */
public int getPayDiscount_Exp_Acct() 
{
Integer ii = (Integer)get_Value("PayDiscount_Exp_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Discount Revenue.
Payment Discount Revenue Account */
public void setPayDiscount_Rev_Acct (int PayDiscount_Rev_Acct)
{
set_Value ("PayDiscount_Rev_Acct", new Integer(PayDiscount_Rev_Acct));
}
/** Get Payment Discount Revenue.
Payment Discount Revenue Account */
public int getPayDiscount_Rev_Acct() 
{
Integer ii = (Integer)get_Value("PayDiscount_Rev_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Unearned Revenue.
Account for unearned revenue */
public void setUnEarnedRevenue_Acct (int UnEarnedRevenue_Acct)
{
set_Value ("UnEarnedRevenue_Acct", new Integer(UnEarnedRevenue_Acct));
}
/** Get Unearned Revenue.
Account for unearned revenue */
public int getUnEarnedRevenue_Acct() 
{
Integer ii = (Integer)get_Value("UnEarnedRevenue_Acct");
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
/** Set Vendor Liability.
Account for Vendor Liability */
public void setV_Liability_Acct (int V_Liability_Acct)
{
set_Value ("V_Liability_Acct", new Integer(V_Liability_Acct));
}
/** Get Vendor Liability.
Account for Vendor Liability */
public int getV_Liability_Acct() 
{
Integer ii = (Integer)get_Value("V_Liability_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Vendor Service Liability.
Account for Vender Service Liability */
public void setV_Liability_Services_Acct (int V_Liability_Services_Acct)
{
set_Value ("V_Liability_Services_Acct", new Integer(V_Liability_Services_Acct));
}
/** Get Vendor Service Liability.
Account for Vender Service Liability */
public int getV_Liability_Services_Acct() 
{
Integer ii = (Integer)get_Value("V_Liability_Services_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Vendor Prepayment.
Account for Vendor Prepayments */
public void setV_Prepayment_Acct (int V_Prepayment_Acct)
{
set_Value ("V_Prepayment_Acct", new Integer(V_Prepayment_Acct));
}
/** Get Vendor Prepayment.
Account for Vendor Prepayments */
public int getV_Prepayment_Acct() 
{
Integer ii = (Integer)get_Value("V_Prepayment_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Warehouse Differences.
Warehouse Differences Account */
public void setW_Differences_Acct (int W_Differences_Acct)
{
set_Value ("W_Differences_Acct", new Integer(W_Differences_Acct));
}
/** Get Warehouse Differences.
Warehouse Differences Account */
public int getW_Differences_Acct() 
{
Integer ii = (Integer)get_Value("W_Differences_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Inventory Adjustment.
Account for Inventory value adjustments for Actual Costing */
public void setW_InvActualAdjust_Acct (int W_InvActualAdjust_Acct)
{
set_Value ("W_InvActualAdjust_Acct", new Integer(W_InvActualAdjust_Acct));
}
/** Get Inventory Adjustment.
Account for Inventory value adjustments for Actual Costing */
public int getW_InvActualAdjust_Acct() 
{
Integer ii = (Integer)get_Value("W_InvActualAdjust_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set (Not Used).
Warehouse Inventory Asset Account - Currently not used */
public void setW_Inventory_Acct (int W_Inventory_Acct)
{
set_Value ("W_Inventory_Acct", new Integer(W_Inventory_Acct));
}
/** Get (Not Used).
Warehouse Inventory Asset Account - Currently not used */
public int getW_Inventory_Acct() 
{
Integer ii = (Integer)get_Value("W_Inventory_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Inventory Revaluation.
Account for Inventory Revaluation */
public void setW_Revaluation_Acct (int W_Revaluation_Acct)
{
set_Value ("W_Revaluation_Acct", new Integer(W_Revaluation_Acct));
}
/** Get Inventory Revaluation.
Account for Inventory Revaluation */
public int getW_Revaluation_Acct() 
{
Integer ii = (Integer)get_Value("W_Revaluation_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Withholding.
Account for Withholdings */
public void setWithholding_Acct (int Withholding_Acct)
{
set_Value ("Withholding_Acct", new Integer(Withholding_Acct));
}
/** Get Withholding.
Account for Withholdings */
public int getWithholding_Acct() 
{
Integer ii = (Integer)get_Value("Withholding_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Write-off.
Account for Receivables write-off */
public void setWriteOff_Acct (int WriteOff_Acct)
{
set_Value ("WriteOff_Acct", new Integer(WriteOff_Acct));
}
/** Get Write-off.
Account for Receivables write-off */
public int getWriteOff_Acct() 
{
Integer ii = (Integer)get_Value("WriteOff_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
