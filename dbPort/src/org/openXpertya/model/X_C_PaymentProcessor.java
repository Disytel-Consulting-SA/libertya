/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_PaymentProcessor
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:31.593 */
public class X_C_PaymentProcessor extends PO
{
/** Constructor estándar */
public X_C_PaymentProcessor (Properties ctx, int C_PaymentProcessor_ID, String trxName)
{
super (ctx, C_PaymentProcessor_ID, trxName);
/** if (C_PaymentProcessor_ID == 0)
{
setAcceptAMEX (false);
setAcceptATM (false);
setAcceptCheck (false);
setAcceptCorporate (false);
setAcceptDiners (false);
setAcceptDirectDebit (false);
setAcceptDirectDeposit (false);
setAcceptDiscover (false);
setAcceptMC (false);
setAcceptVisa (false);
setC_BankAccount_ID (0);
setC_PaymentProcessor_ID (0);
setCommission (Env.ZERO);
setCostPerTrx (Env.ZERO);
setHostAddress (null);
setHostPort (0);
setName (null);
setPassword (null);
setRequireVV (false);
setUserID (null);
}
 */
}
/** Load Constructor */
public X_C_PaymentProcessor (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=398 */
public static final int Table_ID=398;

/** TableName=C_PaymentProcessor */
public static final String Table_Name="C_PaymentProcessor";

protected static KeyNamePair Model = new KeyNamePair(398,"C_PaymentProcessor");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_PaymentProcessor[").append(getID()).append("]");
return sb.toString();
}
public static final int AD_SEQUENCE_ID_AD_Reference_ID=128;
/** Set Sequence.
Document Sequence */
public void setAD_Sequence_ID (int AD_Sequence_ID)
{
if (AD_Sequence_ID <= 0) set_Value ("AD_Sequence_ID", null);
 else 
set_Value ("AD_Sequence_ID", new Integer(AD_Sequence_ID));
}
/** Get Sequence.
Document Sequence */
public int getAD_Sequence_ID() 
{
Integer ii = (Integer)get_Value("AD_Sequence_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Accept AMEX.
Accept American Express Card */
public void setAcceptAMEX (boolean AcceptAMEX)
{
set_Value ("AcceptAMEX", new Boolean(AcceptAMEX));
}
/** Get Accept AMEX.
Accept American Express Card */
public boolean isAcceptAMEX() 
{
Object oo = get_Value("AcceptAMEX");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Accept ATM.
Accept Bank ATM Card */
public void setAcceptATM (boolean AcceptATM)
{
set_Value ("AcceptATM", new Boolean(AcceptATM));
}
/** Get Accept ATM.
Accept Bank ATM Card */
public boolean isAcceptATM() 
{
Object oo = get_Value("AcceptATM");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Accept Electronic Check.
Accept ECheck (Electronic Checks) */
public void setAcceptCheck (boolean AcceptCheck)
{
set_Value ("AcceptCheck", new Boolean(AcceptCheck));
}
/** Get Accept Electronic Check.
Accept ECheck (Electronic Checks) */
public boolean isAcceptCheck() 
{
Object oo = get_Value("AcceptCheck");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Accept Corporate.
Accept Corporate Purchase Cards */
public void setAcceptCorporate (boolean AcceptCorporate)
{
set_Value ("AcceptCorporate", new Boolean(AcceptCorporate));
}
/** Get Accept Corporate.
Accept Corporate Purchase Cards */
public boolean isAcceptCorporate() 
{
Object oo = get_Value("AcceptCorporate");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Accept Diners.
Accept Diner's Club */
public void setAcceptDiners (boolean AcceptDiners)
{
set_Value ("AcceptDiners", new Boolean(AcceptDiners));
}
/** Get Accept Diners.
Accept Diner's Club */
public boolean isAcceptDiners() 
{
Object oo = get_Value("AcceptDiners");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Accept Direct Debit.
Accept Direct Debits (vendor initiated) */
public void setAcceptDirectDebit (boolean AcceptDirectDebit)
{
set_Value ("AcceptDirectDebit", new Boolean(AcceptDirectDebit));
}
/** Get Accept Direct Debit.
Accept Direct Debits (vendor initiated) */
public boolean isAcceptDirectDebit() 
{
Object oo = get_Value("AcceptDirectDebit");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Accept Direct Deposit.
Accept Direct Deposit (payee initiated) */
public void setAcceptDirectDeposit (boolean AcceptDirectDeposit)
{
set_Value ("AcceptDirectDeposit", new Boolean(AcceptDirectDeposit));
}
/** Get Accept Direct Deposit.
Accept Direct Deposit (payee initiated) */
public boolean isAcceptDirectDeposit() 
{
Object oo = get_Value("AcceptDirectDeposit");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Accept Discover.
Accept Discover Card */
public void setAcceptDiscover (boolean AcceptDiscover)
{
set_Value ("AcceptDiscover", new Boolean(AcceptDiscover));
}
/** Get Accept Discover.
Accept Discover Card */
public boolean isAcceptDiscover() 
{
Object oo = get_Value("AcceptDiscover");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Accept MasterCard.
Accept Master Card */
public void setAcceptMC (boolean AcceptMC)
{
set_Value ("AcceptMC", new Boolean(AcceptMC));
}
/** Get Accept MasterCard.
Accept Master Card */
public boolean isAcceptMC() 
{
Object oo = get_Value("AcceptMC");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Accept Visa.
Accept Visa Cards */
public void setAcceptVisa (boolean AcceptVisa)
{
set_Value ("AcceptVisa", new Boolean(AcceptVisa));
}
/** Get Accept Visa.
Accept Visa Cards */
public boolean isAcceptVisa() 
{
Object oo = get_Value("AcceptVisa");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Payment Processor.
Payment processor for electronic payments */
public void setC_PaymentProcessor_ID (int C_PaymentProcessor_ID)
{
set_ValueNoCheck ("C_PaymentProcessor_ID", new Integer(C_PaymentProcessor_ID));
}
/** Get Payment Processor.
Payment processor for electronic payments */
public int getC_PaymentProcessor_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentProcessor_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Commission %.
Commission stated as a percentage */
public void setCommission (BigDecimal Commission)
{
if (Commission == null) throw new IllegalArgumentException ("Commission is mandatory");
set_Value ("Commission", Commission);
}
/** Get Commission %.
Commission stated as a percentage */
public BigDecimal getCommission() 
{
BigDecimal bd = (BigDecimal)get_Value("Commission");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Cost per transaction.
Fixed cost per transaction */
public void setCostPerTrx (BigDecimal CostPerTrx)
{
if (CostPerTrx == null) throw new IllegalArgumentException ("CostPerTrx is mandatory");
set_Value ("CostPerTrx", CostPerTrx);
}
/** Get Cost per transaction.
Fixed cost per transaction */
public BigDecimal getCostPerTrx() 
{
BigDecimal bd = (BigDecimal)get_Value("CostPerTrx");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Host Address.
Host Address URL or DNS */
public void setHostAddress (String HostAddress)
{
if (HostAddress == null) throw new IllegalArgumentException ("HostAddress is mandatory");
if (HostAddress.length() > 60)
{
log.warning("Length > 60 - truncated");
HostAddress = HostAddress.substring(0,59);
}
set_Value ("HostAddress", HostAddress);
}
/** Get Host Address.
Host Address URL or DNS */
public String getHostAddress() 
{
return (String)get_Value("HostAddress");
}
/** Set Host port.
Host Communication Port */
public void setHostPort (int HostPort)
{
set_Value ("HostPort", new Integer(HostPort));
}
/** Get Host port.
Host Communication Port */
public int getHostPort() 
{
Integer ii = (Integer)get_Value("HostPort");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Minimum Amt.
Minumum Amout in Document Currency */
public void setMinimumAmt (BigDecimal MinimumAmt)
{
set_Value ("MinimumAmt", MinimumAmt);
}
/** Get Minimum Amt.
Minumum Amout in Document Currency */
public BigDecimal getMinimumAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("MinimumAmt");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Partner ID.
Partner ID for the Payment Processor */
public void setPartnerID (String PartnerID)
{
if (PartnerID != null && PartnerID.length() > 60)
{
log.warning("Length > 60 - truncated");
PartnerID = PartnerID.substring(0,59);
}
set_Value ("PartnerID", PartnerID);
}
/** Get Partner ID.
Partner ID for the Payment Processor */
public String getPartnerID() 
{
return (String)get_Value("PartnerID");
}
/** Set Password.
Password of any length (case sensitive) */
public void setPassword (String Password)
{
if (Password == null) throw new IllegalArgumentException ("Password is mandatory");
if (Password.length() > 60)
{
log.warning("Length > 60 - truncated");
Password = Password.substring(0,59);
}
set_ValueE ("Password", Password);
}
/** Get Password.
Password of any length (case sensitive) */
public String getPassword() 
{
return (String)get_ValueE("Password");
}
/** Set Payment Processor Class.
Payment Processor Java Class */
public void setPayProcessorClass (String PayProcessorClass)
{
if (PayProcessorClass != null && PayProcessorClass.length() > 60)
{
log.warning("Length > 60 - truncated");
PayProcessorClass = PayProcessorClass.substring(0,59);
}
set_Value ("PayProcessorClass", PayProcessorClass);
}
/** Get Payment Processor Class.
Payment Processor Java Class */
public String getPayProcessorClass() 
{
return (String)get_Value("PayProcessorClass");
}
/** Set Proxy address.
 Address of your proxy server */
public void setProxyAddress (String ProxyAddress)
{
if (ProxyAddress != null && ProxyAddress.length() > 60)
{
log.warning("Length > 60 - truncated");
ProxyAddress = ProxyAddress.substring(0,59);
}
set_Value ("ProxyAddress", ProxyAddress);
}
/** Get Proxy address.
 Address of your proxy server */
public String getProxyAddress() 
{
return (String)get_Value("ProxyAddress");
}
/** Set Proxy logon.
Logon of your proxy server */
public void setProxyLogon (String ProxyLogon)
{
if (ProxyLogon != null && ProxyLogon.length() > 60)
{
log.warning("Length > 60 - truncated");
ProxyLogon = ProxyLogon.substring(0,59);
}
set_Value ("ProxyLogon", ProxyLogon);
}
/** Get Proxy logon.
Logon of your proxy server */
public String getProxyLogon() 
{
return (String)get_Value("ProxyLogon");
}
/** Set Proxy password.
Password of your proxy server */
public void setProxyPassword (String ProxyPassword)
{
if (ProxyPassword != null && ProxyPassword.length() > 60)
{
log.warning("Length > 60 - truncated");
ProxyPassword = ProxyPassword.substring(0,59);
}
set_ValueE ("ProxyPassword", ProxyPassword);
}
/** Get Proxy password.
Password of your proxy server */
public String getProxyPassword() 
{
return (String)get_ValueE("ProxyPassword");
}
/** Set Proxy port.
Port of your proxy server */
public void setProxyPort (int ProxyPort)
{
set_Value ("ProxyPort", new Integer(ProxyPort));
}
/** Get Proxy port.
Port of your proxy server */
public int getProxyPort() 
{
Integer ii = (Integer)get_Value("ProxyPort");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Require CreditCard Verification Code.
Require 3/4 digit Credit Verification Code */
public void setRequireVV (boolean RequireVV)
{
set_Value ("RequireVV", new Boolean(RequireVV));
}
/** Get Require CreditCard Verification Code.
Require 3/4 digit Credit Verification Code */
public boolean isRequireVV() 
{
Object oo = get_Value("RequireVV");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set User ID.
User ID */
public void setUserID (String UserID)
{
if (UserID == null) throw new IllegalArgumentException ("UserID is mandatory");
if (UserID.length() > 60)
{
log.warning("Length > 60 - truncated");
UserID = UserID.substring(0,59);
}
set_Value ("UserID", UserID);
}
/** Get User ID.
User ID */
public String getUserID() 
{
return (String)get_Value("UserID");
}
/** Set Vendor ID.
Vendor ID for the Payment Processor */
public void setVendorID (String VendorID)
{
if (VendorID != null && VendorID.length() > 60)
{
log.warning("Length > 60 - truncated");
VendorID = VendorID.substring(0,59);
}
set_Value ("VendorID", VendorID);
}
/** Get Vendor ID.
Vendor ID for the Payment Processor */
public String getVendorID() 
{
return (String)get_Value("VendorID");
}
}
