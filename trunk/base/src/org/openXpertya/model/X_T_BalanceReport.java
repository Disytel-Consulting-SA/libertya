/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_BalanceReport
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-06-28 15:44:07.362 */
public class X_T_BalanceReport extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_BalanceReport (Properties ctx, int T_BalanceReport_ID, String trxName)
{
super (ctx, T_BalanceReport_ID, trxName);
/** if (T_BalanceReport_ID == 0)
{
setAD_PInstance_ID (0);
setCondition (null);
setDateCreated (new Timestamp(System.currentTimeMillis()));
setOnlyCurrentAccounts (false);
}
 */
}
/** Load Constructor */
public X_T_BalanceReport (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_BalanceReport");

/** TableName=T_BalanceReport */
public static final String Table_Name="T_BalanceReport";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_BalanceReport");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_BalanceReport[").append(getID()).append("]");
return sb.toString();
}
public static final int ACCOUNTTYPE_AD_Reference_ID = MReference.getReferenceID("Account type");
/** Customer = C */
public static final String ACCOUNTTYPE_Customer = "C";
/** Vendor = V */
public static final String ACCOUNTTYPE_Vendor = "V";
/** Set Account Type.
Indicates the type of account */
public void setAccountType (String AccountType)
{
if (AccountType == null || AccountType.equals("C") || AccountType.equals("V"));
 else throw new IllegalArgumentException ("AccountType Invalid value - Reference = ACCOUNTTYPE_AD_Reference_ID - C - V");
if (AccountType != null && AccountType.length() > 1)
{
log.warning("Length > 1 - truncated");
AccountType = AccountType.substring(0,1);
}
set_Value ("AccountType", AccountType);
}
/** Get Account Type.
Indicates the type of account */
public String getAccountType() 
{
return (String)get_Value("AccountType");
}
/** Set Actual balance */
public void setActualBalance (BigDecimal ActualBalance)
{
set_Value ("ActualBalance", ActualBalance);
}
/** Get Actual balance */
public BigDecimal getActualBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("ActualBalance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_Value ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Balance */
public void setBalance (BigDecimal Balance)
{
set_Value ("Balance", Balance);
}
/** Get Balance */
public BigDecimal getBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("Balance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner Group.
Business Partner Group */
public void setC_BP_Group_ID (int C_BP_Group_ID)
{
if (C_BP_Group_ID <= 0) set_Value ("C_BP_Group_ID", null);
 else 
set_Value ("C_BP_Group_ID", new Integer(C_BP_Group_ID));
}
/** Get Business Partner Group.
Business Partner Group */
public int getC_BP_Group_ID() 
{
Integer ii = (Integer)get_Value("C_BP_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cheques en cartera */
public void setChequesEnCartera (BigDecimal ChequesEnCartera)
{
set_Value ("ChequesEnCartera", ChequesEnCartera);
}
/** Get Cheques en cartera */
public BigDecimal getChequesEnCartera() 
{
BigDecimal bd = (BigDecimal)get_Value("ChequesEnCartera");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int CONDITION_AD_Reference_ID = MReference.getReferenceID("Document Condition");
/** Cash = B */
public static final String CONDITION_Cash = "B";
/** On Credit = P */
public static final String CONDITION_OnCredit = "P";
/** All = A */
public static final String CONDITION_All = "A";
/** Set Condition */
public void setCondition (String Condition)
{
if (Condition.equals("B") || Condition.equals("P") || Condition.equals("A"));
 else throw new IllegalArgumentException ("Condition Invalid value - Reference = CONDITION_AD_Reference_ID - B - P - A");
if (Condition == null) throw new IllegalArgumentException ("Condition is mandatory");
if (Condition.length() > 1)
{
log.warning("Length > 1 - truncated");
Condition = Condition.substring(0,1);
}
set_Value ("Condition", Condition);
}
/** Get Condition */
public String getCondition() 
{
return (String)get_Value("Condition");
}
/** Set Credit */
public void setCredit (BigDecimal Credit)
{
set_Value ("Credit", Credit);
}
/** Get Credit */
public BigDecimal getCredit() 
{
BigDecimal bd = (BigDecimal)get_Value("Credit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set DateCreated */
public void setDateCreated (Timestamp DateCreated)
{
if (DateCreated == null) throw new IllegalArgumentException ("DateCreated is mandatory");
set_Value ("DateCreated", DateCreated);
}
/** Get DateCreated */
public Timestamp getDateCreated() 
{
return (Timestamp)get_Value("DateCreated");
}
/** Set Date_Newest_Open_Invoice */
public void setDate_Newest_Open_Invoice (Timestamp Date_Newest_Open_Invoice)
{
set_Value ("Date_Newest_Open_Invoice", Date_Newest_Open_Invoice);
}
/** Get Date_Newest_Open_Invoice */
public Timestamp getDate_Newest_Open_Invoice() 
{
return (Timestamp)get_Value("Date_Newest_Open_Invoice");
}
/** Set Date_Oldest_Open_Invoice */
public void setDate_Oldest_Open_Invoice (Timestamp Date_Oldest_Open_Invoice)
{
set_Value ("Date_Oldest_Open_Invoice", Date_Oldest_Open_Invoice);
}
/** Get Date_Oldest_Open_Invoice */
public Timestamp getDate_Oldest_Open_Invoice() 
{
return (Timestamp)get_Value("Date_Oldest_Open_Invoice");
}
/** Set Debit */
public void setDebit (BigDecimal Debit)
{
set_Value ("Debit", Debit);
}
/** Get Debit */
public BigDecimal getDebit() 
{
BigDecimal bd = (BigDecimal)get_Value("Debit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Due debt */
public void setDueDebt (BigDecimal DueDebt)
{
set_Value ("DueDebt", DueDebt);
}
/** Get Due debt */
public BigDecimal getDueDebt() 
{
BigDecimal bd = (BigDecimal)get_Value("DueDebt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set General balance */
public void setGeneralBalance (BigDecimal GeneralBalance)
{
set_Value ("GeneralBalance", GeneralBalance);
}
/** Get General balance */
public BigDecimal getGeneralBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("GeneralBalance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Observaciones */
public void setObservaciones (String Observaciones)
{
if (Observaciones != null && Observaciones.length() > 255)
{
log.warning("Length > 255 - truncated");
Observaciones = Observaciones.substring(0,255);
}
set_Value ("Observaciones", Observaciones);
}
/** Get Observaciones */
public String getObservaciones() 
{
return (String)get_Value("Observaciones");
}
/** Set Only current accounts.
Only business partner with current account activated */
public void setOnlyCurrentAccounts (boolean OnlyCurrentAccounts)
{
set_Value ("OnlyCurrentAccounts", new Boolean(OnlyCurrentAccounts));
}
/** Get Only current accounts.
Only business partner with current account activated */
public boolean isOnlyCurrentAccounts() 
{
Object oo = get_Value("OnlyCurrentAccounts");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Scope */
public void setScope (String Scope)
{
if (Scope != null && Scope.length() > 2)
{
log.warning("Length > 2 - truncated");
Scope = Scope.substring(0,2);
}
set_Value ("Scope", Scope);
}
/** Get Scope */
public String getScope() 
{
return (String)get_Value("Scope");
}
/** Set SortCriteria */
public void setSortCriteria (String SortCriteria)
{
if (SortCriteria != null && SortCriteria.length() > 2)
{
log.warning("Length > 2 - truncated");
SortCriteria = SortCriteria.substring(0,2);
}
set_Value ("SortCriteria", SortCriteria);
}
/** Get SortCriteria */
public String getSortCriteria() 
{
return (String)get_Value("SortCriteria");
}
/** Set subindice */
public void setsubindice (int subindice)
{
set_Value ("subindice", new Integer(subindice));
}
/** Get subindice */
public int getsubindice() 
{
Integer ii = (Integer)get_Value("subindice");
if (ii == null) return 0;
return ii.intValue();
}
/** Set TrueDateTrx */
public void setTrueDateTrx (Timestamp TrueDateTrx)
{
set_Value ("TrueDateTrx", TrueDateTrx);
}
/** Get TrueDateTrx */
public Timestamp getTrueDateTrx() 
{
return (Timestamp)get_Value("TrueDateTrx");
}
/** Set Value From.
Initial value of search key in range */
public void setValueFrom (String ValueFrom)
{
if (ValueFrom != null && ValueFrom.length() > 42)
{
log.warning("Length > 42 - truncated");
ValueFrom = ValueFrom.substring(0,42);
}
set_Value ("ValueFrom", ValueFrom);
}
/** Get Value From.
Initial value of search key in range */
public String getValueFrom() 
{
return (String)get_Value("ValueFrom");
}
/** Set Value To.
End value of search key in range */
public void setValueTo (String ValueTo)
{
if (ValueTo != null && ValueTo.length() > 42)
{
log.warning("Length > 42 - truncated");
ValueTo = ValueTo.substring(0,42);
}
set_Value ("ValueTo", ValueTo);
}
/** Get Value To.
End value of search key in range */
public String getValueTo() 
{
return (String)get_Value("ValueTo");
}
}
