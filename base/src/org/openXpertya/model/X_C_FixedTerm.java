/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_FixedTerm
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2023-08-30 12:47:19.193 */
public class X_C_FixedTerm extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_FixedTerm (Properties ctx, int C_FixedTerm_ID, String trxName)
{
super (ctx, C_FixedTerm_ID, trxName);
/** if (C_FixedTerm_ID == 0)
{
setAccredit (null);
setAccredited (false);
setC_Bank_ID (0);
setC_BankAccount_ID (0);
setC_BankAccountFixedTerm_ID (0);
setC_Currency_ID (0);
setC_FixedTerm_ID (0);
setConstitute (null);
setConstituted (false);
setDueDate (new Timestamp(System.currentTimeMillis()));
setInitialAmount (Env.ZERO);
setReturnAmt (Env.ZERO);
setTerm (0);
setTNA (Env.ZERO);
setTNATerm (0);
setTrxDate (new Timestamp(System.currentTimeMillis()));
setVoidAccredit (null);
setVoidConstitute (null);
}
 */
}
/** Load Constructor */
public X_C_FixedTerm (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_FixedTerm");

/** TableName=C_FixedTerm */
public static final String Table_Name="C_FixedTerm";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_FixedTerm");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_FixedTerm[").append(getID()).append("]");
return sb.toString();
}
/** Set Accredit */
public void setAccredit (String Accredit)
{
if (Accredit == null) throw new IllegalArgumentException ("Accredit is mandatory");
if (Accredit.length() > 1)
{
log.warning("Length > 1 - truncated");
Accredit = Accredit.substring(0,1);
}
set_Value ("Accredit", Accredit);
}
/** Get Accredit */
public String getAccredit() 
{
return (String)get_Value("Accredit");
}
/** Set Accredited */
public void setAccredited (boolean Accredited)
{
set_Value ("Accredited", new Boolean(Accredited));
}
/** Get Accredited */
public boolean isAccredited() 
{
Object oo = get_Value("Accredited");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int C_ALLOCATIONRETENTIONHDR_ID_AD_Reference_ID = MReference.getReferenceID("C_Allocation");
/** Set C_AllocationRetentionHdr_ID */
public void setC_AllocationRetentionHdr_ID (int C_AllocationRetentionHdr_ID)
{
if (C_AllocationRetentionHdr_ID <= 0) set_Value ("C_AllocationRetentionHdr_ID", null);
 else 
set_Value ("C_AllocationRetentionHdr_ID", new Integer(C_AllocationRetentionHdr_ID));
}
/** Get C_AllocationRetentionHdr_ID */
public int getC_AllocationRetentionHdr_ID() 
{
Integer ii = (Integer)get_Value("C_AllocationRetentionHdr_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank.
Bank */
public void setC_Bank_ID (int C_Bank_ID)
{
set_Value ("C_Bank_ID", new Integer(C_Bank_ID));
}
/** Get Bank.
Bank */
public int getC_Bank_ID() 
{
Integer ii = (Integer)get_Value("C_Bank_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_BANKACCOUNT_ID_AD_Reference_ID = MReference.getReferenceID("C_BankAccount");
/** Set Bank Account.
Account at the Bank */
public void setC_BankAccount_ID (int C_BankAccount_ID)
{
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
public static final int C_BANKACCOUNTFIXEDTERM_ID_AD_Reference_ID = MReference.getReferenceID("C_BankAccount");
/** Set C_BankAccountFixedTerm_ID */
public void setC_BankAccountFixedTerm_ID (int C_BankAccountFixedTerm_ID)
{
set_Value ("C_BankAccountFixedTerm_ID", new Integer(C_BankAccountFixedTerm_ID));
}
/** Get C_BankAccountFixedTerm_ID */
public int getC_BankAccountFixedTerm_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccountFixedTerm_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_BANKTRANSFERACCREDITATION_ID_AD_Reference_ID = MReference.getReferenceID("C_BankTransfer");
/** Set C_BankTransferAccreditation_ID */
public void setC_BankTransferAccreditation_ID (int C_BankTransferAccreditation_ID)
{
if (C_BankTransferAccreditation_ID <= 0) set_Value ("C_BankTransferAccreditation_ID", null);
 else 
set_Value ("C_BankTransferAccreditation_ID", new Integer(C_BankTransferAccreditation_ID));
}
/** Get C_BankTransferAccreditation_ID */
public int getC_BankTransferAccreditation_ID() 
{
Integer ii = (Integer)get_Value("C_BankTransferAccreditation_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_BANKTRANSFERCONSTITUTION_ID_AD_Reference_ID = MReference.getReferenceID("C_BankTransfer");
/** Set C_BankTransferConstitution_ID */
public void setC_BankTransferConstitution_ID (int C_BankTransferConstitution_ID)
{
if (C_BankTransferConstitution_ID <= 0) set_Value ("C_BankTransferConstitution_ID", null);
 else 
set_Value ("C_BankTransferConstitution_ID", new Integer(C_BankTransferConstitution_ID));
}
/** Get C_BankTransferConstitution_ID */
public int getC_BankTransferConstitution_ID() 
{
Integer ii = (Integer)get_Value("C_BankTransferConstitution_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_CURRENCY_ID_AD_Reference_ID = MReference.getReferenceID("C_Currency");
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
/** Set C_FixedTerm_ID */
public void setC_FixedTerm_ID (int C_FixedTerm_ID)
{
set_ValueNoCheck ("C_FixedTerm_ID", new Integer(C_FixedTerm_ID));
}
/** Get C_FixedTerm_ID */
public int getC_FixedTerm_ID() 
{
Integer ii = (Integer)get_Value("C_FixedTerm_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_PAYMENTINTEREST_ID_AD_Reference_ID = MReference.getReferenceID("C_Payment");
/** Set C_PaymentInterest_ID */
public void setC_PaymentInterest_ID (int C_PaymentInterest_ID)
{
if (C_PaymentInterest_ID <= 0) set_Value ("C_PaymentInterest_ID", null);
 else 
set_Value ("C_PaymentInterest_ID", new Integer(C_PaymentInterest_ID));
}
/** Get C_PaymentInterest_ID */
public int getC_PaymentInterest_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentInterest_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Certificate */
public void setCertificate (String Certificate)
{
if (Certificate != null && Certificate.length() > 40)
{
log.warning("Length > 40 - truncated");
Certificate = Certificate.substring(0,40);
}
set_Value ("Certificate", Certificate);
}
/** Get Certificate */
public String getCertificate() 
{
return (String)get_Value("Certificate");
}
/** Set Constitute */
public void setConstitute (String Constitute)
{
if (Constitute == null) throw new IllegalArgumentException ("Constitute is mandatory");
if (Constitute.length() > 1)
{
log.warning("Length > 1 - truncated");
Constitute = Constitute.substring(0,1);
}
set_Value ("Constitute", Constitute);
}
/** Get Constitute */
public String getConstitute() 
{
return (String)get_Value("Constitute");
}
/** Set Constituted */
public void setConstituted (boolean Constituted)
{
set_Value ("Constituted", new Boolean(Constituted));
}
/** Get Constituted */
public boolean isConstituted() 
{
Object oo = get_Value("Constituted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Due Date.
Date when the payment is due */
public void setDueDate (Timestamp DueDate)
{
if (DueDate == null) throw new IllegalArgumentException ("DueDate is mandatory");
set_Value ("DueDate", DueDate);
}
/** Get Due Date.
Date when the payment is due */
public Timestamp getDueDate() 
{
return (Timestamp)get_Value("DueDate");
}
/** Set InitialAmount */
public void setInitialAmount (BigDecimal InitialAmount)
{
if (InitialAmount == null) throw new IllegalArgumentException ("InitialAmount is mandatory");
set_Value ("InitialAmount", InitialAmount);
}
/** Get InitialAmount */
public BigDecimal getInitialAmount() 
{
BigDecimal bd = (BigDecimal)get_Value("InitialAmount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set NetAmt */
public void setNetAmt (BigDecimal NetAmt)
{
set_Value ("NetAmt", NetAmt);
}
/** Get NetAmt */
public BigDecimal getNetAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("NetAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set RetentionAmt */
public void setRetentionAmt (BigDecimal RetentionAmt)
{
set_Value ("RetentionAmt", RetentionAmt);
}
/** Get RetentionAmt */
public BigDecimal getRetentionAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("RetentionAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set ReturnAmt */
public void setReturnAmt (BigDecimal ReturnAmt)
{
if (ReturnAmt == null) throw new IllegalArgumentException ("ReturnAmt is mandatory");
set_Value ("ReturnAmt", ReturnAmt);
}
/** Get ReturnAmt */
public BigDecimal getReturnAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ReturnAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set TEA */
public void setTEA (BigDecimal TEA)
{
set_Value ("TEA", TEA);
}
/** Get TEA */
public BigDecimal getTEA() 
{
BigDecimal bd = (BigDecimal)get_Value("TEA");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Term */
public void setTerm (int Term)
{
set_Value ("Term", new Integer(Term));
}
/** Get Term */
public int getTerm() 
{
Integer ii = (Integer)get_Value("Term");
if (ii == null) return 0;
return ii.intValue();
}
/** Set TNA */
public void setTNA (BigDecimal TNA)
{
if (TNA == null) throw new IllegalArgumentException ("TNA is mandatory");
set_Value ("TNA", TNA);
}
/** Get TNA */
public BigDecimal getTNA() 
{
BigDecimal bd = (BigDecimal)get_Value("TNA");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set TNATerm */
public void setTNATerm (int TNATerm)
{
set_Value ("TNATerm", new Integer(TNATerm));
}
/** Get TNATerm */
public int getTNATerm() 
{
Integer ii = (Integer)get_Value("TNATerm");
if (ii == null) return 0;
return ii.intValue();
}
/** Set TrxDate */
public void setTrxDate (Timestamp TrxDate)
{
if (TrxDate == null) throw new IllegalArgumentException ("TrxDate is mandatory");
set_Value ("TrxDate", TrxDate);
}
/** Get TrxDate */
public Timestamp getTrxDate() 
{
return (Timestamp)get_Value("TrxDate");
}
/** Set VoidAccredit */
public void setVoidAccredit (String VoidAccredit)
{
if (VoidAccredit == null) throw new IllegalArgumentException ("VoidAccredit is mandatory");
if (VoidAccredit.length() > 1)
{
log.warning("Length > 1 - truncated");
VoidAccredit = VoidAccredit.substring(0,1);
}
set_Value ("VoidAccredit", VoidAccredit);
}
/** Get VoidAccredit */
public String getVoidAccredit() 
{
return (String)get_Value("VoidAccredit");
}
/** Set VoidConstitute */
public void setVoidConstitute (String VoidConstitute)
{
if (VoidConstitute == null) throw new IllegalArgumentException ("VoidConstitute is mandatory");
if (VoidConstitute.length() > 1)
{
log.warning("Length > 1 - truncated");
VoidConstitute = VoidConstitute.substring(0,1);
}
set_Value ("VoidConstitute", VoidConstitute);
}
/** Get VoidConstitute */
public String getVoidConstitute() 
{
return (String)get_Value("VoidConstitute");
}
}
