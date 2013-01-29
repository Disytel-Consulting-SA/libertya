/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Product_Acct
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-07-09 20:10:56.54 */
public class X_M_Product_Acct extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Product_Acct (Properties ctx, int M_Product_Acct_ID, String trxName)
{
super (ctx, M_Product_Acct_ID, trxName);
/** if (M_Product_Acct_ID == 0)
{
setC_AcctSchema_ID (0);
setIsManual (false);
setM_Product_ID (0);
setP_Asset_Acct (0);
setP_Cogs_Acct (0);
setP_Expense_Acct (0);
setP_InvoicePriceVariance_Acct (0);
setP_PurchasePriceVariance_Acct (0);
setP_Revenue_Acct (0);
setP_RevenueExchange_Acct (0);
setP_TradeDiscountGrant_Acct (0);
setP_TradeDiscountRec_Acct (0);
}
 */
}
/** Load Constructor */
public X_M_Product_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Product_Acct");

/** TableName=M_Product_Acct */
public static final String Table_Name="M_Product_Acct";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Product_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Product_Acct[").append(getID()).append("]");
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
/** Set Manual.
This is a manual process */
public void setIsManual (boolean IsManual)
{
set_Value ("IsManual", new Boolean(IsManual));
}
/** Get Manual.
This is a manual process */
public boolean isManual() 
{
Object oo = get_Value("IsManual");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
set_ValueNoCheck ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Amortization Account */
public void setP_Amortization_Acct (int P_Amortization_Acct)
{
set_Value ("P_Amortization_Acct", new Integer(P_Amortization_Acct));
}
/** Get Amortization Account */
public int getP_Amortization_Acct() 
{
Integer ii = (Integer)get_Value("P_Amortization_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Amortization Realized Account */
public void setP_Amortization_Realized_Acct (int P_Amortization_Realized_Acct)
{
set_Value ("P_Amortization_Realized_Acct", new Integer(P_Amortization_Realized_Acct));
}
/** Get Amortization Realized Account */
public int getP_Amortization_Realized_Acct() 
{
Integer ii = (Integer)get_Value("P_Amortization_Realized_Acct");
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
/** Set Revenue Exchange */
public void setP_RevenueExchange_Acct (int P_RevenueExchange_Acct)
{
set_Value ("P_RevenueExchange_Acct", new Integer(P_RevenueExchange_Acct));
}
/** Get Revenue Exchange */
public int getP_RevenueExchange_Acct() 
{
Integer ii = (Integer)get_Value("P_RevenueExchange_Acct");
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
}
