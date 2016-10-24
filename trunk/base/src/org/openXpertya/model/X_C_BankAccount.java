/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BankAccount
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-24 20:11:56.082 */
public class X_C_BankAccount extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BankAccount (Properties ctx, int C_BankAccount_ID, String trxName)
{
super (ctx, C_BankAccount_ID, trxName);
/** if (C_BankAccount_ID == 0)
{
setAccountNo (null);
setBankAccountType (null);	// C
setC_BankAccount_ID (0);
setC_Bank_ID (0);
setCC (null);
setC_Currency_ID (0);
setCreditLimit (Env.ZERO);
setCurrentBalance (Env.ZERO);
setDescription (null);
setIsChequesEnCartera (false);
setIsDefault (false);
setOficina (null);
setSucursal (null);
}
 */
}
/** Load Constructor */
public X_C_BankAccount (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BankAccount");

/** TableName=C_BankAccount */
public static final String Table_Name="C_BankAccount";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BankAccount");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BankAccount[").append(getID()).append("]");
return sb.toString();
}
/** Set Account No.
Account Number */
public void setAccountNo (String AccountNo)
{
if (AccountNo == null) throw new IllegalArgumentException ("AccountNo is mandatory");
if (AccountNo.length() > 20)
{
log.warning("Length > 20 - truncated");
AccountNo = AccountNo.substring(0,20);
}
set_Value ("AccountNo", AccountNo);
}
/** Get Account No.
Account Number */
public String getAccountNo() 
{
return (String)get_Value("AccountNo");
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
public static final int BANKACCOUNTTYPE_AD_Reference_ID = MReference.getReferenceID("C_Bank Account Type");
/** Savings = S */
public static final String BANKACCOUNTTYPE_Savings = "S";
/** Checking = C */
public static final String BANKACCOUNTTYPE_Checking = "C";
/** Set Bank Account Type.
Bank Account Type */
public void setBankAccountType (String BankAccountType)
{
if (BankAccountType.equals("S") || BankAccountType.equals("C"));
 else throw new IllegalArgumentException ("BankAccountType Invalid value - Reference = BANKACCOUNTTYPE_AD_Reference_ID - S - C");
if (BankAccountType == null) throw new IllegalArgumentException ("BankAccountType is mandatory");
if (BankAccountType.length() > 1)
{
log.warning("Length > 1 - truncated");
BankAccountType = BankAccountType.substring(0,1);
}
set_Value ("BankAccountType", BankAccountType);
}
/** Get Bank Account Type.
Bank Account Type */
public String getBankAccountType() 
{
return (String)get_Value("BankAccountType");
}
/** Set BBAN.
Basic Bank Account Number */
public void setBBAN (String BBAN)
{
if (BBAN != null && BBAN.length() > 40)
{
log.warning("Length > 40 - truncated");
BBAN = BBAN.substring(0,40);
}
set_Value ("BBAN", BBAN);
}
/** Get BBAN.
Basic Bank Account Number */
public String getBBAN() 
{
return (String)get_Value("BBAN");
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
/** Set C_BankAccount_Location_ID */
public void setC_BankAccount_Location_ID (int C_BankAccount_Location_ID)
{
if (C_BankAccount_Location_ID <= 0) set_Value ("C_BankAccount_Location_ID", null);
 else 
set_Value ("C_BankAccount_Location_ID", new Integer(C_BankAccount_Location_ID));
}
/** Get C_BankAccount_Location_ID */
public int getC_BankAccount_Location_ID() 
{
Integer ii = (Integer)get_Value("C_BankAccount_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Bank.
Bank */
public void setC_Bank_ID (int C_Bank_ID)
{
set_ValueNoCheck ("C_Bank_ID", new Integer(C_Bank_ID));
}
/** Get Bank.
Bank */
public int getC_Bank_ID() 
{
Integer ii = (Integer)get_Value("C_Bank_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CC.
CC */
public void setCC (String CC)
{
if (CC == null) throw new IllegalArgumentException ("CC is mandatory");
if (CC.length() > 10)
{
log.warning("Length > 10 - truncated");
CC = CC.substring(0,10);
}
set_Value ("CC", CC);
}
/** Get CC.
CC */
public String getCC() 
{
return (String)get_Value("CC");
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
/** Set Credit limit.
Amount of Credit allowed */
public void setCreditLimit (BigDecimal CreditLimit)
{
if (CreditLimit == null) throw new IllegalArgumentException ("CreditLimit is mandatory");
set_Value ("CreditLimit", CreditLimit);
}
/** Get Credit limit.
Amount of Credit allowed */
public BigDecimal getCreditLimit() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditLimit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Current balance.
Current Balance */
public void setCurrentBalance (BigDecimal CurrentBalance)
{
if (CurrentBalance == null) throw new IllegalArgumentException ("CurrentBalance is mandatory");
set_Value ("CurrentBalance", CurrentBalance);
}
/** Get Current balance.
Current Balance */
public BigDecimal getCurrentBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("CurrentBalance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set DC.
DC */
public void setDC (String DC)
{
if (DC != null && DC.length() > 2)
{
log.warning("Length > 2 - truncated");
DC = DC.substring(0,2);
}
set_Value ("DC", DC);
}
/** Get DC.
DC */
public String getDC() 
{
return (String)get_Value("DC");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description == null) throw new IllegalArgumentException ("Description is mandatory");
if (Description.length() > 255)
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getDescription());
}
/** Set ElectronicPaymentsAccount */
public void setElectronicPaymentsAccount (boolean ElectronicPaymentsAccount)
{
set_Value ("ElectronicPaymentsAccount", new Boolean(ElectronicPaymentsAccount));
}
/** Get ElectronicPaymentsAccount */
public boolean isElectronicPaymentsAccount() 
{
Object oo = get_Value("ElectronicPaymentsAccount");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IBAN.
International Bank Account Number */
public void setIBAN (String IBAN)
{
if (IBAN != null && IBAN.length() > 40)
{
log.warning("Length > 40 - truncated");
IBAN = IBAN.substring(0,40);
}
set_Value ("IBAN", IBAN);
}
/** Get IBAN.
International Bank Account Number */
public String getIBAN() 
{
return (String)get_Value("IBAN");
}
/** Set Cuenta de Cheques en Cartera */
public void setIsChequesEnCartera (boolean IsChequesEnCartera)
{
set_Value ("IsChequesEnCartera", new Boolean(IsChequesEnCartera));
}
/** Get Cuenta de Cheques en Cartera */
public boolean isChequesEnCartera() 
{
Object oo = get_Value("IsChequesEnCartera");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Oficina.
Oficina */
public void setOficina (String Oficina)
{
if (Oficina == null) throw new IllegalArgumentException ("Oficina is mandatory");
if (Oficina.length() > 4)
{
log.warning("Length > 4 - truncated");
Oficina = Oficina.substring(0,4);
}
set_Value ("Oficina", Oficina);
}
/** Get Oficina.
Oficina */
public String getOficina() 
{
return (String)get_Value("Oficina");
}
/** Set Sucursal.
Sucursal */
public void setSucursal (String Sucursal)
{
if (Sucursal == null) throw new IllegalArgumentException ("Sucursal is mandatory");
if (Sucursal.length() > 4)
{
log.warning("Length > 4 - truncated");
Sucursal = Sucursal.substring(0,4);
}
set_Value ("Sucursal", Sucursal);
}
/** Get Sucursal.
Sucursal */
public String getSucursal() 
{
return (String)get_Value("Sucursal");
}
}
