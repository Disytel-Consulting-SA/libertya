/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Product_Category_Acct
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:39.187 */
public class X_M_Product_Category_Acct extends PO
{
/** Constructor estándar */
public X_M_Product_Category_Acct (Properties ctx, int M_Product_Category_Acct_ID, String trxName)
{
super (ctx, M_Product_Category_Acct_ID, trxName);
/** if (M_Product_Category_Acct_ID == 0)
{
setC_AcctSchema_ID (0);
setM_Product_Category_ID (0);
setP_Asset_Acct (0);
setP_Cogs_Acct (0);
setP_Expense_Acct (0);
setP_InvoicePriceVariance_Acct (0);
setP_PurchasePriceVariance_Acct (0);
setP_Revenue_Acct (0);
setP_TradeDiscountGrant_Acct (0);
setP_TradeDiscountRec_Acct (0);
}
 */
}
/** Load Constructor */
public X_M_Product_Category_Acct (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=401 */
public static final int Table_ID=401;

/** TableName=M_Product_Category_Acct */
public static final String Table_Name="M_Product_Category_Acct";

protected static KeyNamePair Model = new KeyNamePair(401,"M_Product_Category_Acct");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Product_Category_Acct[").append(getID()).append("]");
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
public static final int COSTINGMETHOD_AD_Reference_ID=122;
/** Standard Costing = S */
public static final String COSTINGMETHOD_StandardCosting = "S";
/** Average = A */
public static final String COSTINGMETHOD_Average = "A";
/** Lifo = L */
public static final String COSTINGMETHOD_Lifo = "L";
/** Fifo = F */
public static final String COSTINGMETHOD_Fifo = "F";
/** Last PO Price = P */
public static final String COSTINGMETHOD_LastPOPrice = "P";
/** Set Costing Method.
Indicates how Costs will be calculated */
public void setCostingMethod (String CostingMethod)
{
if (CostingMethod == null || CostingMethod.equals("S") || CostingMethod.equals("A") || CostingMethod.equals("L") || CostingMethod.equals("F") || CostingMethod.equals("P"));
 else throw new IllegalArgumentException ("CostingMethod Invalid value - Reference_ID=122 - S - A - L - F - P");
if (CostingMethod != null && CostingMethod.length() > 1)
{
log.warning("Length > 1 - truncated");
CostingMethod = CostingMethod.substring(0,0);
}
set_Value ("CostingMethod", CostingMethod);
}
/** Get Costing Method.
Indicates how Costs will be calculated */
public String getCostingMethod() 
{
return (String)get_Value("CostingMethod");
}
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
set_ValueNoCheck ("M_Product_Category_ID", new Integer(M_Product_Category_ID));
}
/** Get Product Category.
Category of a Product */
public int getM_Product_Category_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Category_ID");
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
}
