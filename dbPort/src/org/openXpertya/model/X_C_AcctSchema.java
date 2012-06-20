/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_AcctSchema
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:27.234 */
public class X_C_AcctSchema extends PO
{
/** Constructor estándar */
public X_C_AcctSchema (Properties ctx, int C_AcctSchema_ID, String trxName)
{
super (ctx, C_AcctSchema_ID, trxName);
/** if (C_AcctSchema_ID == 0)
{
setAutoPeriodControl (false);
setC_AcctSchema_ID (0);
setC_Currency_ID (0);
setCostingMethod (null);
setGAAP (null);
setHasAlias (false);
setHasCombination (false);
setIsAccrual (true);	// Y
setIsDiscountCorrectsTax (false);
setIsTradeDiscountPosted (false);
setM_CostType_ID (0);
setName (null);
setSeparator (null);	// -
}
 */
}
/** Load Constructor */
public X_C_AcctSchema (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=265 */
public static final int Table_ID=265;

/** TableName=C_AcctSchema */
public static final String Table_Name="C_AcctSchema";

protected static KeyNamePair Model = new KeyNamePair(265,"C_AcctSchema");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_AcctSchema[").append(getID()).append("]");
return sb.toString();
}
/** Set Automatic Period Control.
If selected, the periods are automatically opened and closed */
public void setAutoPeriodControl (boolean AutoPeriodControl)
{
set_Value ("AutoPeriodControl", new Boolean(AutoPeriodControl));
}
/** Get Automatic Period Control.
If selected, the periods are automatically opened and closed */
public boolean isAutoPeriodControl() 
{
Object oo = get_Value("AutoPeriodControl");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
set_Value ("C_Currency_ID", new Integer(C_Currency_ID));
}
/** Get Currency.
The Currency for this record */
public int getC_Currency_ID() 
{
Integer ii = (Integer)get_Value("C_Currency_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Period.
Period of the Calendar */
public void setC_Period_ID (int C_Period_ID)
{
if (C_Period_ID <= 0) set_ValueNoCheck ("C_Period_ID", null);
 else 
set_ValueNoCheck ("C_Period_ID", new Integer(C_Period_ID));
}
/** Get Period.
Period of the Calendar */
public int getC_Period_ID() 
{
Integer ii = (Integer)get_Value("C_Period_ID");
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
if (CostingMethod.equals("S") || CostingMethod.equals("A") || CostingMethod.equals("L") || CostingMethod.equals("F") || CostingMethod.equals("P"));
 else throw new IllegalArgumentException ("CostingMethod Invalid value - Reference_ID=122 - S - A - L - F - P");
if (CostingMethod == null) throw new IllegalArgumentException ("CostingMethod is mandatory");
if (CostingMethod.length() > 1)
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
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,254);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
public static final int GAAP_AD_Reference_ID=123;
/** International GAAP = UN */
public static final String GAAP_InternationalGAAP = "UN";
/** US GAAP = US */
public static final String GAAP_USGAAP = "US";
/** German HGB = DE */
public static final String GAAP_GermanHGB = "DE";
/** French Accounting Standard = FR */
public static final String GAAP_FrenchAccountingStandard = "FR";
/** Custom Accounting Rules = XX */
public static final String GAAP_CustomAccountingRules = "XX";
/** Set GAAP.
Generally Accepted Accounting Principles */
public void setGAAP (String GAAP)
{
if (GAAP.equals("UN") || GAAP.equals("US") || GAAP.equals("DE") || GAAP.equals("FR") || GAAP.equals("XX"));
 else throw new IllegalArgumentException ("GAAP Invalid value - Reference_ID=123 - UN - US - DE - FR - XX");
if (GAAP == null) throw new IllegalArgumentException ("GAAP is mandatory");
if (GAAP.length() > 2)
{
log.warning("Length > 2 - truncated");
GAAP = GAAP.substring(0,1);
}
set_Value ("GAAP", GAAP);
}
/** Get GAAP.
Generally Accepted Accounting Principles */
public String getGAAP() 
{
return (String)get_Value("GAAP");
}
/** Set Use Account Alias.
Ability to select (partial) account combinations by an Alias */
public void setHasAlias (boolean HasAlias)
{
set_Value ("HasAlias", new Boolean(HasAlias));
}
/** Get Use Account Alias.
Ability to select (partial) account combinations by an Alias */
public boolean isHasAlias() 
{
Object oo = get_Value("HasAlias");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Use Account Combination Control.
Combination of account elements are checked */
public void setHasCombination (boolean HasCombination)
{
set_Value ("HasCombination", new Boolean(HasCombination));
}
/** Get Use Account Combination Control.
Combination of account elements are checked */
public boolean isHasCombination() 
{
Object oo = get_Value("HasCombination");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Accrual.
Indicates if Accrual or Cash Based accounting will be used */
public void setIsAccrual (boolean IsAccrual)
{
set_Value ("IsAccrual", new Boolean(IsAccrual));
}
/** Get Accrual.
Indicates if Accrual or Cash Based accounting will be used */
public boolean isAccrual() 
{
Object oo = get_Value("IsAccrual");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Correct tax for Discounts/Charges.
Correct the tax for payment discount and charges */
public void setIsDiscountCorrectsTax (boolean IsDiscountCorrectsTax)
{
set_Value ("IsDiscountCorrectsTax", new Boolean(IsDiscountCorrectsTax));
}
/** Get Correct tax for Discounts/Charges.
Correct the tax for payment discount and charges */
public boolean isDiscountCorrectsTax() 
{
Object oo = get_Value("IsDiscountCorrectsTax");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Post Trade Discount.
Generate postings for trade discounts */
public void setIsTradeDiscountPosted (boolean IsTradeDiscountPosted)
{
set_Value ("IsTradeDiscountPosted", new Boolean(IsTradeDiscountPosted));
}
/** Get Post Trade Discount.
Generate postings for trade discounts */
public boolean isTradeDiscountPosted() 
{
Object oo = get_Value("IsTradeDiscountPosted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Cost Type.
Type of Cost (e.g. Current, Plan, Future) */
public void setM_CostType_ID (int M_CostType_ID)
{
set_Value ("M_CostType_ID", new Integer(M_CostType_ID));
}
/** Get Cost Type.
Type of Cost (e.g. Current, Plan, Future) */
public int getM_CostType_ID() 
{
Integer ii = (Integer)get_Value("M_CostType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,59);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Future Days.
Number of days to be able to post to a future date (based on system date) */
public void setPeriod_OpenFuture (int Period_OpenFuture)
{
set_Value ("Period_OpenFuture", new Integer(Period_OpenFuture));
}
/** Get Future Days.
Number of days to be able to post to a future date (based on system date) */
public int getPeriod_OpenFuture() 
{
Integer ii = (Integer)get_Value("Period_OpenFuture");
if (ii == null) return 0;
return ii.intValue();
}
/** Set History Days.
Number of days to be able to post in the past (based on system date) */
public void setPeriod_OpenHistory (int Period_OpenHistory)
{
set_Value ("Period_OpenHistory", new Integer(Period_OpenHistory));
}
/** Get History Days.
Number of days to be able to post in the past (based on system date) */
public int getPeriod_OpenHistory() 
{
Integer ii = (Integer)get_Value("Period_OpenHistory");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Element Separator.
Element Separator */
public void setSeparator (String Separator)
{
if (Separator == null) throw new IllegalArgumentException ("Separator is mandatory");
if (Separator.length() > 1)
{
log.warning("Length > 1 - truncated");
Separator = Separator.substring(0,0);
}
set_Value ("Separator", Separator);
}
/** Get Element Separator.
Element Separator */
public String getSeparator() 
{
return (String)get_Value("Separator");
}
}
