/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_ElementValue
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:29.875 */
public class X_C_ElementValue extends PO
{
/** Constructor estándar */
public X_C_ElementValue (Properties ctx, int C_ElementValue_ID, String trxName)
{
super (ctx, C_ElementValue_ID, trxName);
/** if (C_ElementValue_ID == 0)
{
setAccountSign (null);	// N
setAccountType (null);	// E
setC_ElementValue_ID (0);
setC_Element_ID (0);
setIsSummary (false);
setName (null);
setPostActual (true);	// Y
setPostBudget (true);	// Y
setPostEncumbrance (true);	// Y
setPostStatistical (true);	// Y
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_ElementValue (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=188 */
public static final int Table_ID=188;

/** TableName=C_ElementValue */
public static final String Table_Name="C_ElementValue";

protected static KeyNamePair Model = new KeyNamePair(188,"C_ElementValue");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_ElementValue[").append(getID()).append("]");
return sb.toString();
}
public static final int ACCOUNTSIGN_AD_Reference_ID=118;
/** Natural = N */
public static final String ACCOUNTSIGN_Natural = "N";
/** Debit = D */
public static final String ACCOUNTSIGN_Debit = "D";
/** Credit = C */
public static final String ACCOUNTSIGN_Credit = "C";
/** Set Account Sign.
Indicates the Natural Sign of the Account as a Debit or Credit */
public void setAccountSign (String AccountSign)
{
if (AccountSign.equals("N") || AccountSign.equals("D") || AccountSign.equals("C"));
 else throw new IllegalArgumentException ("AccountSign Invalid value - Reference_ID=118 - N - D - C");
if (AccountSign == null) throw new IllegalArgumentException ("AccountSign is mandatory");
if (AccountSign.length() > 1)
{
log.warning("Length > 1 - truncated");
AccountSign = AccountSign.substring(0,0);
}
set_Value ("AccountSign", AccountSign);
}
/** Get Account Sign.
Indicates the Natural Sign of the Account as a Debit or Credit */
public String getAccountSign() 
{
return (String)get_Value("AccountSign");
}
public static final int ACCOUNTTYPE_AD_Reference_ID=117;
/** Asset = A */
public static final String ACCOUNTTYPE_Asset = "A";
/** Liability = L */
public static final String ACCOUNTTYPE_Liability = "L";
/** Revenue = R */
public static final String ACCOUNTTYPE_Revenue = "R";
/** Expense = E */
public static final String ACCOUNTTYPE_Expense = "E";
/** Owner's Equity = O */
public static final String ACCOUNTTYPE_OwnerSEquity = "O";
/** Memo = M */
public static final String ACCOUNTTYPE_Memo = "M";
/** Set Account Type.
Indicates the type of account */
public void setAccountType (String AccountType)
{
if (AccountType.equals("A") || AccountType.equals("L") || AccountType.equals("R") || AccountType.equals("E") || AccountType.equals("O") || AccountType.equals("M"));
 else throw new IllegalArgumentException ("AccountType Invalid value - Reference_ID=117 - A - L - R - E - O - M");
if (AccountType == null) throw new IllegalArgumentException ("AccountType is mandatory");
if (AccountType.length() > 1)
{
log.warning("Length > 1 - truncated");
AccountType = AccountType.substring(0,0);
}
set_Value ("AccountType", AccountType);
}
/** Get Account Type.
Indicates the type of account */
public String getAccountType() 
{
return (String)get_Value("AccountType");
}
/** Set Bank Account.
Account at the Bank */
public void setC_BankAccount_ID (int C_BankAccount_ID)
{
if (C_BankAccount_ID <= 0) set_Value ("C_BankAccount_ID", null);
 else 
set_Value ("C_BankAccount_ID", new Integer(C_BankAccount_ID));
}
/** Get Bank Account.
Account at the Bank */
public int getC_BankAccount_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccount_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
if (C_Currency_ID <= 0) set_Value ("C_Currency_ID", null);
 else 
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
/** Set Account Element.
Account Element */
public void setC_ElementValue_ID (int C_ElementValue_ID)
{
set_ValueNoCheck ("C_ElementValue_ID", new Integer(C_ElementValue_ID));
}
/** Get Account Element.
Account Element */
public int getC_ElementValue_ID() 
{
Integer ii = (Integer)get_Value("C_ElementValue_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Element.
Accounting Element */
public void setC_Element_ID (int C_Element_ID)
{
set_ValueNoCheck ("C_Element_ID", new Integer(C_Element_ID));
}
/** Get Element.
Accounting Element */
public int getC_Element_ID() 
{
Integer ii = (Integer)get_Value("C_Element_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Bank Account.
Indicates if this is the Bank Account */
public void setIsBankAccount (boolean IsBankAccount)
{
set_Value ("IsBankAccount", new Boolean(IsBankAccount));
}
/** Get Bank Account.
Indicates if this is the Bank Account */
public boolean isBankAccount() 
{
Object oo = get_Value("IsBankAccount");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Document Controlled.
Control account - If an account is controlled by a document, you cannot post manually to it */
public void setIsDocControlled (boolean IsDocControlled)
{
set_Value ("IsDocControlled", new Boolean(IsDocControlled));
}
/** Get Document Controlled.
Control account - If an account is controlled by a document, you cannot post manually to it */
public boolean isDocControlled() 
{
Object oo = get_Value("IsDocControlled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Foreign Currency Account.
Balances in foreign currency accounts are held in the nominated currency */
public void setIsForeignCurrency (boolean IsForeignCurrency)
{
set_Value ("IsForeignCurrency", new Boolean(IsForeignCurrency));
}
/** Get Foreign Currency Account.
Balances in foreign currency accounts are held in the nominated currency */
public boolean isForeignCurrency() 
{
Object oo = get_Value("IsForeignCurrency");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Summary Level.
This is a summary entity */
public void setIsSummary (boolean IsSummary)
{
set_Value ("IsSummary", new Boolean(IsSummary));
}
/** Get Summary Level.
This is a summary entity */
public boolean isSummary() 
{
Object oo = get_Value("IsSummary");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Post Actual.
Actual Values can be posted */
public void setPostActual (boolean PostActual)
{
set_Value ("PostActual", new Boolean(PostActual));
}
/** Get Post Actual.
Actual Values can be posted */
public boolean isPostActual() 
{
Object oo = get_Value("PostActual");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Post Budget.
Budget values can be posted */
public void setPostBudget (boolean PostBudget)
{
set_Value ("PostBudget", new Boolean(PostBudget));
}
/** Get Post Budget.
Budget values can be posted */
public boolean isPostBudget() 
{
Object oo = get_Value("PostBudget");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Post Encumbrance.
Post commitments to this account */
public void setPostEncumbrance (boolean PostEncumbrance)
{
set_Value ("PostEncumbrance", new Boolean(PostEncumbrance));
}
/** Get Post Encumbrance.
Post commitments to this account */
public boolean isPostEncumbrance() 
{
Object oo = get_Value("PostEncumbrance");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Post Statistical.
Post statistical quantities to this account? */
public void setPostStatistical (boolean PostStatistical)
{
set_Value ("PostStatistical", new Boolean(PostStatistical));
}
/** Get Post Statistical.
Post statistical quantities to this account? */
public boolean isPostStatistical() 
{
Object oo = get_Value("PostStatistical");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,39);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getValue());
}
}
