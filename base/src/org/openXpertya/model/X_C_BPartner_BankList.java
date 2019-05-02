/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner_BankList
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-05-02 10:35:20.24 */
public class X_C_BPartner_BankList extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BPartner_BankList (Properties ctx, int C_BPartner_BankList_ID, String trxName)
{
super (ctx, C_BPartner_BankList_ID, trxName);
/** if (C_BPartner_BankList_ID == 0)
{
setC_BPartner_BankList_ID (0);
setC_BPartner_ID (0);
setC_DocType_ID (0);
setC_ElectronicPaymentBranch_ID (0);
setNotToOrder (false);
}
 */
}
/** Load Constructor */
public X_C_BPartner_BankList (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BPartner_BankList");

/** TableName=C_BPartner_BankList */
public static final String Table_Name="C_BPartner_BankList";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BPartner_BankList");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner_BankList[").append(getID()).append("]");
return sb.toString();
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
/** Set C_BPartner_BankList_ID */
public void setC_BPartner_BankList_ID (int C_BPartner_BankList_ID)
{
set_ValueNoCheck ("C_BPartner_BankList_ID", new Integer(C_BPartner_BankList_ID));
}
/** Get C_BPartner_BankList_ID */
public int getC_BPartner_BankList_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_BankList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
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
/** Set CBU.
Unique Bank Code */
public void setCBU (String CBU)
{
if (CBU != null && CBU.length() > 30)
{
log.warning("Length > 30 - truncated");
CBU = CBU.substring(0,30);
}
set_Value ("CBU", CBU);
}
/** Get CBU.
Unique Bank Code */
public String getCBU() 
{
return (String)get_Value("CBU");
}
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
set_Value ("C_DocType_ID", new Integer(C_DocType_ID));
}
/** Get Document Type.
Document type or rules */
public int getC_DocType_ID() 
{
Integer ii = (Integer)get_Value("C_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_ELECTRONICPAYMENTBRANCH_ID_AD_Reference_ID = MReference.getReferenceID("C_ElectronicPaymentBranch");
/** Set C_ElectronicPaymentBranch_ID */
public void setC_ElectronicPaymentBranch_ID (int C_ElectronicPaymentBranch_ID)
{
set_Value ("C_ElectronicPaymentBranch_ID", new Integer(C_ElectronicPaymentBranch_ID));
}
/** Get C_ElectronicPaymentBranch_ID */
public int getC_ElectronicPaymentBranch_ID() 
{
Integer ii = (Integer)get_Value("C_ElectronicPaymentBranch_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Nombre Retirante */
public void setNombre_Retirante (String Nombre_Retirante)
{
if (Nombre_Retirante != null && Nombre_Retirante.length() > 30)
{
log.warning("Length > 30 - truncated");
Nombre_Retirante = Nombre_Retirante.substring(0,30);
}
set_Value ("Nombre_Retirante", Nombre_Retirante);
}
/** Get Nombre Retirante */
public String getNombre_Retirante() 
{
return (String)get_Value("Nombre_Retirante");
}
/** Set Not to order */
public void setNotToOrder (boolean NotToOrder)
{
set_Value ("NotToOrder", new Boolean(NotToOrder));
}
/** Get Not to order */
public boolean isNotToOrder() 
{
Object oo = get_Value("NotToOrder");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int TRANSFERBANKACCOUNTTYPE_AD_Reference_ID = MReference.getReferenceID("Bank Account Type");
/** Savings Bank = CA */
public static final String TRANSFERBANKACCOUNTTYPE_SavingsBank = "CA";
/** Current Account = CC */
public static final String TRANSFERBANKACCOUNTTYPE_CurrentAccount = "CC";
/** Set Bank Account Type */
public void setTransferBankAccountType (String TransferBankAccountType)
{
if (TransferBankAccountType == null || TransferBankAccountType.equals("CA") || TransferBankAccountType.equals("CC") || ( refContainsValue("T0082CORE-AD_Reference-1010416-20190502100958", TransferBankAccountType) ) );
 else throw new IllegalArgumentException ("TransferBankAccountType Invalid value: " + TransferBankAccountType + ".  Valid: " +  refValidOptions("T0082CORE-AD_Reference-1010416-20190502100958") );
if (TransferBankAccountType != null && TransferBankAccountType.length() > 2)
{
log.warning("Length > 2 - truncated");
TransferBankAccountType = TransferBankAccountType.substring(0,2);
}
set_Value ("TransferBankAccountType", TransferBankAccountType);
}
/** Get Bank Account Type */
public String getTransferBankAccountType() 
{
return (String)get_Value("TransferBankAccountType");
}
public static final int TRANSFERCONCEPT_AD_Reference_ID = MReference.getReferenceID("Transfer Concepts");
/** Expenses = EXP */
public static final String TRANSFERCONCEPT_Expenses = "EXP";
/** Rents = ALQ */
public static final String TRANSFERCONCEPT_Rents = "ALQ";
/** Fee = CUO */
public static final String TRANSFERCONCEPT_Fee = "CUO";
/** Invoice = FAC */
public static final String TRANSFERCONCEPT_Invoice = "FAC";
/** Honoraries = HON */
public static final String TRANSFERCONCEPT_Honoraries = "HON";
/** Loan = PRE */
public static final String TRANSFERCONCEPT_Loan = "PRE";
/** Assurance = SEG */
public static final String TRANSFERCONCEPT_Assurance = "SEG";
/** Others = VAR */
public static final String TRANSFERCONCEPT_Others = "VAR";
/** Set Transfer Concept */
public void setTransferConcept (String TransferConcept)
{
if (TransferConcept == null || TransferConcept.equals("EXP") || TransferConcept.equals("ALQ") || TransferConcept.equals("CUO") || TransferConcept.equals("FAC") || TransferConcept.equals("HON") || TransferConcept.equals("PRE") || TransferConcept.equals("SEG") || TransferConcept.equals("VAR") || ( refContainsValue("T0082CORE-AD_Reference-1010417-20190502101111", TransferConcept) ) );
 else throw new IllegalArgumentException ("TransferConcept Invalid value: " + TransferConcept + ".  Valid: " +  refValidOptions("T0082CORE-AD_Reference-1010417-20190502101111") );
if (TransferConcept != null && TransferConcept.length() > 3)
{
log.warning("Length > 3 - truncated");
TransferConcept = TransferConcept.substring(0,3);
}
set_Value ("TransferConcept", TransferConcept);
}
/** Get Transfer Concept */
public String getTransferConcept() 
{
return (String)get_Value("TransferConcept");
}
}
