/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_RevenueRecognition_Plan
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:32.859 */
public class X_C_RevenueRecognition_Plan extends PO
{
/** Constructor estándar */
public X_C_RevenueRecognition_Plan (Properties ctx, int C_RevenueRecognition_Plan_ID, String trxName)
{
super (ctx, C_RevenueRecognition_Plan_ID, trxName);
/** if (C_RevenueRecognition_Plan_ID == 0)
{
setC_AcctSchema_ID (0);
setC_Currency_ID (0);
setC_InvoiceLine_ID (0);
setC_RevenueRecognition_ID (0);
setC_RevenueRecognition_Plan_ID (0);
setP_Revenue_Acct (0);
setRecognizedAmt (Env.ZERO);
setTotalAmt (Env.ZERO);
setUnEarnedRevenue_Acct (0);
}
 */
}
/** Load Constructor */
public X_C_RevenueRecognition_Plan (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=443 */
public static final int Table_ID=443;

/** TableName=C_RevenueRecognition_Plan */
public static final String Table_Name="C_RevenueRecognition_Plan";

protected static KeyNamePair Model = new KeyNamePair(443,"C_RevenueRecognition_Plan");
protected static BigDecimal AccessLevel = new BigDecimal(1);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_RevenueRecognition_Plan[").append(getID()).append("]");
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
/** Set Invoice Line.
Invoice Detail Line */
public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
{
set_ValueNoCheck ("C_InvoiceLine_ID", new Integer(C_InvoiceLine_ID));
}
/** Get Invoice Line.
Invoice Detail Line */
public int getC_InvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Revenue Recognition.
Method for recording revenue */
public void setC_RevenueRecognition_ID (int C_RevenueRecognition_ID)
{
set_ValueNoCheck ("C_RevenueRecognition_ID", new Integer(C_RevenueRecognition_ID));
}
/** Get Revenue Recognition.
Method for recording revenue */
public int getC_RevenueRecognition_ID() 
{
Integer ii = (Integer)get_Value("C_RevenueRecognition_ID");
if (ii == null) return 0;
return ii.intValue();
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_RevenueRecognition_ID()));
}
/** Set Revenue Recognition Plan.
Plan for recognizing or recording revenue */
public void setC_RevenueRecognition_Plan_ID (int C_RevenueRecognition_Plan_ID)
{
set_ValueNoCheck ("C_RevenueRecognition_Plan_ID", new Integer(C_RevenueRecognition_Plan_ID));
}
/** Get Revenue Recognition Plan.
Plan for recognizing or recording revenue */
public int getC_RevenueRecognition_Plan_ID() 
{
Integer ii = (Integer)get_Value("C_RevenueRecognition_Plan_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product Revenue.
Account for Product Revenue (Sales Account) */
public void setP_Revenue_Acct (int P_Revenue_Acct)
{
set_ValueNoCheck ("P_Revenue_Acct", new Integer(P_Revenue_Acct));
}
/** Get Product Revenue.
Account for Product Revenue (Sales Account) */
public int getP_Revenue_Acct() 
{
Integer ii = (Integer)get_Value("P_Revenue_Acct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Recognized Amount */
public void setRecognizedAmt (BigDecimal RecognizedAmt)
{
if (RecognizedAmt == null) throw new IllegalArgumentException ("RecognizedAmt is mandatory");
set_ValueNoCheck ("RecognizedAmt", RecognizedAmt);
}
/** Get Recognized Amount */
public BigDecimal getRecognizedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("RecognizedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Total Amount.
Total Amount */
public void setTotalAmt (BigDecimal TotalAmt)
{
if (TotalAmt == null) throw new IllegalArgumentException ("TotalAmt is mandatory");
set_ValueNoCheck ("TotalAmt", TotalAmt);
}
/** Get Total Amount.
Total Amount */
public BigDecimal getTotalAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Unearned Revenue.
Account for unearned revenue */
public void setUnEarnedRevenue_Acct (int UnEarnedRevenue_Acct)
{
set_ValueNoCheck ("UnEarnedRevenue_Acct", new Integer(UnEarnedRevenue_Acct));
}
/** Get Unearned Revenue.
Account for unearned revenue */
public int getUnEarnedRevenue_Acct() 
{
Integer ii = (Integer)get_Value("UnEarnedRevenue_Acct");
if (ii == null) return 0;
return ii.intValue();
}
}
