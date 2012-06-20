/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BankStatementLoader
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:28.64 */
public class X_C_BankStatementLoader extends PO
{
/** Constructor estándar */
public X_C_BankStatementLoader (Properties ctx, int C_BankStatementLoader_ID, String trxName)
{
super (ctx, C_BankStatementLoader_ID, trxName);
/** if (C_BankStatementLoader_ID == 0)
{
setC_BankAccount_ID (0);
setC_BankStatementLoader_ID (0);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_BankStatementLoader (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=640 */
public static final int Table_ID=640;

/** TableName=C_BankStatementLoader */
public static final String Table_Name="C_BankStatementLoader";

protected static KeyNamePair Model = new KeyNamePair(640,"C_BankStatementLoader");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BankStatementLoader[").append(getID()).append("]");
return sb.toString();
}
/** Set Account No.
Account Number */
public void setAccountNo (String AccountNo)
{
if (AccountNo != null && AccountNo.length() > 20)
{
log.warning("Length > 20 - truncated");
AccountNo = AccountNo.substring(0,19);
}
set_Value ("AccountNo", AccountNo);
}
/** Get Account No.
Account Number */
public String getAccountNo() 
{
return (String)get_Value("AccountNo");
}
/** Set Branch ID.
Bank Branch ID */
public void setBranchID (String BranchID)
{
if (BranchID != null && BranchID.length() > 20)
{
log.warning("Length > 20 - truncated");
BranchID = BranchID.substring(0,19);
}
set_Value ("BranchID", BranchID);
}
/** Get Branch ID.
Bank Branch ID */
public String getBranchID() 
{
return (String)get_Value("BranchID");
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
/** Set Bank Statement Loader.
Definition of Bank Statement Loader (SWIFT, OFX) */
public void setC_BankStatementLoader_ID (int C_BankStatementLoader_ID)
{
set_ValueNoCheck ("C_BankStatementLoader_ID", new Integer(C_BankStatementLoader_ID));
}
/** Get Bank Statement Loader.
Definition of Bank Statement Loader (SWIFT, OFX) */
public int getC_BankStatementLoader_ID() 
{
Integer ii = (Integer)get_Value("C_BankStatementLoader_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Date Format.
Date format used in the imput format */
public void setDateFormat (String DateFormat)
{
if (DateFormat != null && DateFormat.length() > 20)
{
log.warning("Length > 20 - truncated");
DateFormat = DateFormat.substring(0,19);
}
set_Value ("DateFormat", DateFormat);
}
/** Get Date Format.
Date format used in the imput format */
public String getDateFormat() 
{
return (String)get_Value("DateFormat");
}
/** Set Date last run.
Date the process was last run. */
public void setDateLastRun (Timestamp DateLastRun)
{
set_Value ("DateLastRun", DateLastRun);
}
/** Get Date last run.
Date the process was last run. */
public Timestamp getDateLastRun() 
{
return (Timestamp)get_Value("DateLastRun");
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
/** Set File Name.
Name of the local file or URL */
public void setFileName (String FileName)
{
if (FileName != null && FileName.length() > 120)
{
log.warning("Length > 120 - truncated");
FileName = FileName.substring(0,119);
}
set_Value ("FileName", FileName);
}
/** Get File Name.
Name of the local file or URL */
public String getFileName() 
{
return (String)get_Value("FileName");
}
/** Set Financial Institution ID.
The ID of the Financial Institution / Bank */
public void setFinancialInstitutionID (String FinancialInstitutionID)
{
if (FinancialInstitutionID != null && FinancialInstitutionID.length() > 20)
{
log.warning("Length > 20 - truncated");
FinancialInstitutionID = FinancialInstitutionID.substring(0,19);
}
set_Value ("FinancialInstitutionID", FinancialInstitutionID);
}
/** Get Financial Institution ID.
The ID of the Financial Institution / Bank */
public String getFinancialInstitutionID() 
{
return (String)get_Value("FinancialInstitutionID");
}
/** Set Host Address.
Host Address URL or DNS */
public void setHostAddress (String HostAddress)
{
if (HostAddress != null && HostAddress.length() > 60)
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
/** Set PIN.
Personal Identification Number */
public void setPIN (String PIN)
{
if (PIN != null && PIN.length() > 20)
{
log.warning("Length > 20 - truncated");
PIN = PIN.substring(0,19);
}
set_Value ("PIN", PIN);
}
/** Get PIN.
Personal Identification Number */
public String getPIN() 
{
return (String)get_Value("PIN");
}
/** Set Password.
Password of any length (case sensitive) */
public void setPassword (String Password)
{
if (Password != null && Password.length() > 60)
{
log.warning("Length > 60 - truncated");
Password = Password.substring(0,59);
}
set_Value ("Password", Password);
}
/** Get Password.
Password of any length (case sensitive) */
public String getPassword() 
{
return (String)get_Value("Password");
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
set_Value ("ProxyPassword", ProxyPassword);
}
/** Get Proxy password.
Password of your proxy server */
public String getProxyPassword() 
{
return (String)get_Value("ProxyPassword");
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
/** Set Statement Loader Class.
Class name of the bank statement loader */
public void setStmtLoaderClass (String StmtLoaderClass)
{
if (StmtLoaderClass != null && StmtLoaderClass.length() > 60)
{
log.warning("Length > 60 - truncated");
StmtLoaderClass = StmtLoaderClass.substring(0,59);
}
set_Value ("StmtLoaderClass", StmtLoaderClass);
}
/** Get Statement Loader Class.
Class name of the bank statement loader */
public String getStmtLoaderClass() 
{
return (String)get_Value("StmtLoaderClass");
}
/** Set User ID.
User ID */
public void setUserID (String UserID)
{
if (UserID != null && UserID.length() > 60)
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
}
