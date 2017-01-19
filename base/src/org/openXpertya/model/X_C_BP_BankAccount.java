/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BP_BankAccount
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-01-19 14:00:44.371 */
public class X_C_BP_BankAccount extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BP_BankAccount (Properties ctx, int C_BP_BankAccount_ID, String trxName)
{
super (ctx, C_BP_BankAccount_ID, trxName);
/** if (C_BP_BankAccount_ID == 0)
{
setC_BPartner_ID (0);
setC_BP_BankAccount_ID (0);
setIsACH (true);	// 'Y'
}
 */
}
/** Load Constructor */
public X_C_BP_BankAccount (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BP_BankAccount");

/** TableName=C_BP_BankAccount */
public static final String Table_Name="C_BP_BankAccount";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BP_BankAccount");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BP_BankAccount[").append(getID()).append("]");
return sb.toString();
}
/** Set Account No.
Account Number */
public void setAccountNo (String AccountNo)
{
if (AccountNo != null && AccountNo.length() > 60)
{
log.warning("Length > 60 - truncated");
AccountNo = AccountNo.substring(0,60);
}
set_Value ("AccountNo", AccountNo);
}
/** Get Account No.
Account Number */
public String getAccountNo() 
{
return (String)get_Value("AccountNo");
}
/** Set Account City.
City or the Credit Card or Account Holder */
public void setA_City (String A_City)
{
if (A_City != null && A_City.length() > 60)
{
log.warning("Length > 60 - truncated");
A_City = A_City.substring(0,60);
}
set_Value ("A_City", A_City);
}
/** Get Account City.
City or the Credit Card or Account Holder */
public String getA_City() 
{
return (String)get_Value("A_City");
}
/** Set Account Country.
Country */
public void setA_Country (String A_Country)
{
if (A_Country != null && A_Country.length() > 40)
{
log.warning("Length > 40 - truncated");
A_Country = A_Country.substring(0,40);
}
set_Value ("A_Country", A_Country);
}
/** Get Account Country.
Country */
public String getA_Country() 
{
return (String)get_Value("A_Country");
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
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_Value ("AD_User_ID", null);
 else 
set_Value ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Account EMail.
Email Address */
public void setA_EMail (String A_EMail)
{
if (A_EMail != null && A_EMail.length() > 60)
{
log.warning("Length > 60 - truncated");
A_EMail = A_EMail.substring(0,60);
}
set_Value ("A_EMail", A_EMail);
}
/** Get Account EMail.
Email Address */
public String getA_EMail() 
{
return (String)get_Value("A_EMail");
}
/** Set Driver License.
Payment Identification - Driver License */
public void setA_Ident_DL (String A_Ident_DL)
{
if (A_Ident_DL != null && A_Ident_DL.length() > 20)
{
log.warning("Length > 20 - truncated");
A_Ident_DL = A_Ident_DL.substring(0,20);
}
set_Value ("A_Ident_DL", A_Ident_DL);
}
/** Get Driver License.
Payment Identification - Driver License */
public String getA_Ident_DL() 
{
return (String)get_Value("A_Ident_DL");
}
/** Set Social Security No.
Payment Identification - Social Security No */
public void setA_Ident_SSN (String A_Ident_SSN)
{
if (A_Ident_SSN != null && A_Ident_SSN.length() > 20)
{
log.warning("Length > 20 - truncated");
A_Ident_SSN = A_Ident_SSN.substring(0,20);
}
set_Value ("A_Ident_SSN", A_Ident_SSN);
}
/** Get Social Security No.
Payment Identification - Social Security No */
public String getA_Ident_SSN() 
{
return (String)get_Value("A_Ident_SSN");
}
/** Set Account Name.
Name on Credit Card or Account holder */
public void setA_Name (String A_Name)
{
if (A_Name != null && A_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
A_Name = A_Name.substring(0,60);
}
set_Value ("A_Name", A_Name);
}
/** Get Account Name.
Name on Credit Card or Account holder */
public String getA_Name() 
{
return (String)get_Value("A_Name");
}
/** Set Account State.
State of the Credit Card or Account holder */
public void setA_State (String A_State)
{
if (A_State != null && A_State.length() > 40)
{
log.warning("Length > 40 - truncated");
A_State = A_State.substring(0,40);
}
set_Value ("A_State", A_State);
}
/** Get Account State.
State of the Credit Card or Account holder */
public String getA_State() 
{
return (String)get_Value("A_State");
}
/** Set Account Street.
Street address of the Credit Card or Account holder */
public void setA_Street (String A_Street)
{
if (A_Street != null && A_Street.length() > 60)
{
log.warning("Length > 60 - truncated");
A_Street = A_Street.substring(0,60);
}
set_Value ("A_Street", A_Street);
}
/** Get Account Street.
Street address of the Credit Card or Account holder */
public String getA_Street() 
{
return (String)get_Value("A_Street");
}
/** Set Account Zip/Postal.
Zip Code of the Credit Card or Account Holder */
public void setA_Zip (String A_Zip)
{
if (A_Zip != null && A_Zip.length() > 20)
{
log.warning("Length > 20 - truncated");
A_Zip = A_Zip.substring(0,20);
}
set_Value ("A_Zip", A_Zip);
}
/** Get Account Zip/Postal.
Zip Code of the Credit Card or Account Holder */
public String getA_Zip() 
{
return (String)get_Value("A_Zip");
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
if (BankAccountType == null || BankAccountType.equals("S") || BankAccountType.equals("C"));
 else throw new IllegalArgumentException ("BankAccountType Invalid value - Reference = BANKACCOUNTTYPE_AD_Reference_ID - S - C");
if (BankAccountType != null && BankAccountType.length() > 1)
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
/** Set Bank.
Bank */
public void setC_Bank_ID (int C_Bank_ID)
{
if (C_Bank_ID <= 0) set_Value ("C_Bank_ID", null);
 else 
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getC_Bank_ID()));
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
set_ValueNoCheck ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get Business Partner .
Identifies a Business Partner */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Partner Bank Account.
Bank Account of the Business Partner */
public void setC_BP_BankAccount_ID (int C_BP_BankAccount_ID)
{
set_ValueNoCheck ("C_BP_BankAccount_ID", new Integer(C_BP_BankAccount_ID));
}
/** Get Partner Bank Account.
Bank Account of the Business Partner */
public int getC_BP_BankAccount_ID() 
{
Integer ii = (Integer)get_Value("C_BP_BankAccount_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CC */
public void setCC (String CC)
{
if (CC != null && CC.length() > 10)
{
log.warning("Length > 10 - truncated");
CC = CC.substring(0,10);
}
set_Value ("CC", CC);
}
/** Get CC */
public String getCC() 
{
return (String)get_Value("CC");
}
/** Set Exp. Month.
Expiry Month */
public void setCreditCardExpMM (int CreditCardExpMM)
{
set_Value ("CreditCardExpMM", new Integer(CreditCardExpMM));
}
/** Get Exp. Month.
Expiry Month */
public int getCreditCardExpMM() 
{
Integer ii = (Integer)get_Value("CreditCardExpMM");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Exp. Year.
Expiry Year */
public void setCreditCardExpYY (int CreditCardExpYY)
{
set_Value ("CreditCardExpYY", new Integer(CreditCardExpYY));
}
/** Get Exp. Year.
Expiry Year */
public int getCreditCardExpYY() 
{
Integer ii = (Integer)get_Value("CreditCardExpYY");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Number.
Credit Card Number  */
public void setCreditCardNumber (String CreditCardNumber)
{
if (CreditCardNumber != null && CreditCardNumber.length() > 20)
{
log.warning("Length > 20 - truncated");
CreditCardNumber = CreditCardNumber.substring(0,20);
}
set_Value ("CreditCardNumber", CreditCardNumber);
}
/** Get Number.
Credit Card Number  */
public String getCreditCardNumber() 
{
return (String)get_Value("CreditCardNumber");
}
public static final int CREDITCARDTYPE_AD_Reference_ID = MReference.getReferenceID("C_Payment CreditCard Type");
/** Diners = D */
public static final String CREDITCARDTYPE_Diners = "D";
/** ATM = C */
public static final String CREDITCARDTYPE_ATM = "C";
/** Purchase Card = P */
public static final String CREDITCARDTYPE_PurchaseCard = "P";
/** MasterCard = M */
public static final String CREDITCARDTYPE_MasterCard = "M";
/** Visa = V */
public static final String CREDITCARDTYPE_Visa = "V";
/** Amex = A */
public static final String CREDITCARDTYPE_Amex = "A";
/** Discover = N */
public static final String CREDITCARDTYPE_Discover = "N";
/** Set Credit Card.
Credit Card (Visa, MC, AmEx) */
public void setCreditCardType (String CreditCardType)
{
if (CreditCardType == null || CreditCardType.equals("D") || CreditCardType.equals("C") || CreditCardType.equals("P") || CreditCardType.equals("M") || CreditCardType.equals("V") || CreditCardType.equals("A") || CreditCardType.equals("N"));
 else throw new IllegalArgumentException ("CreditCardType Invalid value - Reference = CREDITCARDTYPE_AD_Reference_ID - D - C - P - M - V - A - N");
if (CreditCardType != null && CreditCardType.length() > 1)
{
log.warning("Length > 1 - truncated");
CreditCardType = CreditCardType.substring(0,1);
}
set_Value ("CreditCardType", CreditCardType);
}
/** Get Credit Card.
Credit Card (Visa, MC, AmEx) */
public String getCreditCardType() 
{
return (String)get_Value("CreditCardType");
}
/** Set Verification Code.
Credit Card Verification code on credit card */
public void setCreditCardVV (String CreditCardVV)
{
if (CreditCardVV != null && CreditCardVV.length() > 4)
{
log.warning("Length > 4 - truncated");
CreditCardVV = CreditCardVV.substring(0,4);
}
set_Value ("CreditCardVV", CreditCardVV);
}
/** Get Verification Code.
Credit Card Verification code on credit card */
public String getCreditCardVV() 
{
return (String)get_Value("CreditCardVV");
}
/** Set DC */
public void setDC (String DC)
{
if (DC != null && DC.length() > 2)
{
log.warning("Length > 2 - truncated");
DC = DC.substring(0,2);
}
set_Value ("DC", DC);
}
/** Get DC */
public String getDC() 
{
return (String)get_Value("DC");
}
/** Set ACH.
Automatic Clearing House */
public void setIsACH (boolean IsACH)
{
set_Value ("IsACH", new Boolean(IsACH));
}
/** Get ACH.
Automatic Clearing House */
public boolean isACH() 
{
Object oo = get_Value("IsACH");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Oficina */
public void setOficina (String Oficina)
{
if (Oficina != null && Oficina.length() > 4)
{
log.warning("Length > 4 - truncated");
Oficina = Oficina.substring(0,4);
}
set_Value ("Oficina", Oficina);
}
/** Get Oficina */
public String getOficina() 
{
return (String)get_Value("Oficina");
}
public static final int R_AVSADDR_AD_Reference_ID = MReference.getReferenceID("C_Payment AVS");
/** No Match = N */
public static final String R_AVSADDR_NoMatch = "N";
/** Unavailable = X */
public static final String R_AVSADDR_Unavailable = "X";
/** Match = Y */
public static final String R_AVSADDR_Match = "Y";
/** Set Address verified.
This address has been verified */
public void setR_AvsAddr (String R_AvsAddr)
{
if (R_AvsAddr == null || R_AvsAddr.equals("N") || R_AvsAddr.equals("X") || R_AvsAddr.equals("Y"));
 else throw new IllegalArgumentException ("R_AvsAddr Invalid value - Reference = R_AVSADDR_AD_Reference_ID - N - X - Y");
if (R_AvsAddr != null && R_AvsAddr.length() > 1)
{
log.warning("Length > 1 - truncated");
R_AvsAddr = R_AvsAddr.substring(0,1);
}
set_ValueNoCheck ("R_AvsAddr", R_AvsAddr);
}
/** Get Address verified.
This address has been verified */
public String getR_AvsAddr() 
{
return (String)get_Value("R_AvsAddr");
}
public static final int R_AVSZIP_AD_Reference_ID = MReference.getReferenceID("C_Payment AVS");
/** No Match = N */
public static final String R_AVSZIP_NoMatch = "N";
/** Unavailable = X */
public static final String R_AVSZIP_Unavailable = "X";
/** Match = Y */
public static final String R_AVSZIP_Match = "Y";
/** Set Zip verified.
The Zip Code has been verified */
public void setR_AvsZip (String R_AvsZip)
{
if (R_AvsZip == null || R_AvsZip.equals("N") || R_AvsZip.equals("X") || R_AvsZip.equals("Y"));
 else throw new IllegalArgumentException ("R_AvsZip Invalid value - Reference = R_AVSZIP_AD_Reference_ID - N - X - Y");
if (R_AvsZip != null && R_AvsZip.length() > 1)
{
log.warning("Length > 1 - truncated");
R_AvsZip = R_AvsZip.substring(0,1);
}
set_ValueNoCheck ("R_AvsZip", R_AvsZip);
}
/** Get Zip verified.
The Zip Code has been verified */
public String getR_AvsZip() 
{
return (String)get_Value("R_AvsZip");
}
/** Set Routing No.
Bank Routing Number */
public void setRoutingNo (String RoutingNo)
{
if (RoutingNo != null && RoutingNo.length() > 20)
{
log.warning("Length > 20 - truncated");
RoutingNo = RoutingNo.substring(0,20);
}
set_Value ("RoutingNo", RoutingNo);
}
/** Get Routing No.
Bank Routing Number */
public String getRoutingNo() 
{
return (String)get_Value("RoutingNo");
}
/** Set Sucursal */
public void setSucursal (String Sucursal)
{
if (Sucursal != null && Sucursal.length() > 4)
{
log.warning("Length > 4 - truncated");
Sucursal = Sucursal.substring(0,4);
}
set_Value ("Sucursal", Sucursal);
}
/** Get Sucursal */
public String getSucursal() 
{
return (String)get_Value("Sucursal");
}
}
