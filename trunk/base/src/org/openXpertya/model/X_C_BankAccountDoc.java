/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BankAccountDoc
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-11-25 18:23:37.033 */
public class X_C_BankAccountDoc extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BankAccountDoc (Properties ctx, int C_BankAccountDoc_ID, String trxName)
{
super (ctx, C_BankAccountDoc_ID, trxName);
/** if (C_BankAccountDoc_ID == 0)
{
setAllowManualCheckNo (false);
setC_BankAccountDoc_ID (0);
setC_BankAccount_ID (0);
setCurrentNext (0);
setIsUserAssigned (false);
setName (null);
setPaymentRule (null);
}
 */
}
/** Load Constructor */
public X_C_BankAccountDoc (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BankAccountDoc");

/** TableName=C_BankAccountDoc */
public static final String Table_Name="C_BankAccountDoc";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BankAccountDoc");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BankAccountDoc[").append(getID()).append("]");
return sb.toString();
}
/** Set AD_ComponentObjectUID */
public void setAD_ComponentObjectUID (String AD_ComponentObjectUID)
{
if (AD_ComponentObjectUID != null && AD_ComponentObjectUID.length() > 100)
{
log.warning("Length > 100 - truncated");
AD_ComponentObjectUID = AD_ComponentObjectUID.substring(0,100);
}
set_Value ("AD_ComponentObjectUID", AD_ComponentObjectUID);
}
/** Get AD_ComponentObjectUID */
public String getAD_ComponentObjectUID() 
{
return (String)get_Value("AD_ComponentObjectUID");
}
/** Set Allow Manual Check No */
public void setAllowManualCheckNo (boolean AllowManualCheckNo)
{
set_Value ("AllowManualCheckNo", new Boolean(AllowManualCheckNo));
}
/** Get Allow Manual Check No */
public boolean isAllowManualCheckNo() 
{
Object oo = get_Value("AllowManualCheckNo");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Bank Account Document.
Checks, Transfers, etc. */
public void setC_BankAccountDoc_ID (int C_BankAccountDoc_ID)
{
set_ValueNoCheck ("C_BankAccountDoc_ID", new Integer(C_BankAccountDoc_ID));
}
/** Get Bank Account Document.
Checks, Transfers, etc. */
public int getC_BankAccountDoc_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccountDoc_ID");
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
public static final int CHECK_PRINTFORMAT_ID_AD_Reference_ID = MReference.getReferenceID("AD_PrintFormat Check");
/** Set Check Print Format.
Print Format for printing Checks */
public void setCheck_PrintFormat_ID (int Check_PrintFormat_ID)
{
if (Check_PrintFormat_ID <= 0) set_Value ("Check_PrintFormat_ID", null);
 else 
set_Value ("Check_PrintFormat_ID", new Integer(Check_PrintFormat_ID));
}
/** Get Check Print Format.
Print Format for printing Checks */
public int getCheck_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("Check_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Current Next.
The next number to be used */
public void setCurrentNext (int CurrentNext)
{
set_Value ("CurrentNext", new Integer(CurrentNext));
}
/** Get Current Next.
The next number to be used */
public int getCurrentNext() 
{
Integer ii = (Integer)get_Value("CurrentNext");
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
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set End No */
public void setEndNo (int EndNo)
{
set_Value ("EndNo", new Integer(EndNo));
}
/** Get End No */
public int getEndNo() 
{
Integer ii = (Integer)get_Value("EndNo");
if (ii == null) return 0;
return ii.intValue();
}
/** Set User Assigned.
Bank account doc assigned by user */
public void setIsUserAssigned (boolean IsUserAssigned)
{
set_Value ("IsUserAssigned", new Boolean(IsUserAssigned));
}
/** Get User Assigned.
Bank account doc assigned by user */
public boolean isUserAssigned() 
{
Object oo = get_Value("IsUserAssigned");
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
Name = Name.substring(0,60);
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
public static final int PAYMENTRULE_AD_Reference_ID = MReference.getReferenceID("All_Payment Rule");
/** Transfer = Tr */
public static final String PAYMENTRULE_Transfer = "Tr";
/** Credit Card = K */
public static final String PAYMENTRULE_CreditCard = "K";
/** Cash = B */
public static final String PAYMENTRULE_Cash = "B";
/** On Credit = P */
public static final String PAYMENTRULE_OnCredit = "P";
/** Payment Check = PC */
public static final String PAYMENTRULE_PaymentCheck = "PC";
/** Direct Deposit = T */
public static final String PAYMENTRULE_DirectDeposit = "T";
/** Confirming = Cf */
public static final String PAYMENTRULE_Confirming = "Cf";
/** Direct Debit = D */
public static final String PAYMENTRULE_DirectDebit = "D";
/** Check = S */
public static final String PAYMENTRULE_Check = "S";
/** Set Payment Rule.
How you pay the invoice */
public void setPaymentRule (String PaymentRule)
{
if (PaymentRule.equals("Tr") || PaymentRule.equals("K") || PaymentRule.equals("B") || PaymentRule.equals("P") || PaymentRule.equals("PC") || PaymentRule.equals("T") || PaymentRule.equals("Cf") || PaymentRule.equals("D") || PaymentRule.equals("S") || ( refContainsValue("CORE-AD_Reference-195", PaymentRule) ) );
 else throw new IllegalArgumentException ("PaymentRule Invalid value: " + PaymentRule + ".  Valid: " +  refValidOptions("CORE-AD_Reference-195") );
if (PaymentRule == null) throw new IllegalArgumentException ("PaymentRule is mandatory");
if (PaymentRule.length() > 1)
{
log.warning("Length > 1 - truncated");
PaymentRule = PaymentRule.substring(0,1);
}
set_Value ("PaymentRule", PaymentRule);
}
/** Get Payment Rule.
How you pay the invoice */
public String getPaymentRule() 
{
return (String)get_Value("PaymentRule");
}
/** Set Start No.
Starting number/position */
public void setStartNo (int StartNo)
{
set_Value ("StartNo", new Integer(StartNo));
}
/** Get Start No.
Starting number/position */
public int getStartNo() 
{
Integer ii = (Integer)get_Value("StartNo");
if (ii == null) return 0;
return ii.intValue();
}
}
