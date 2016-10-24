/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BankList_Config
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-10-24 20:11:57.664 */
public class X_C_BankList_Config extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BankList_Config (Properties ctx, int C_BankList_Config_ID, String trxName)
{
super (ctx, C_BankList_Config_ID, trxName);
/** if (C_BankList_Config_ID == 0)
{
setC_BankAccount_ID (0);
setC_Bank_ID (0);
setC_BankList_Config_ID (0);
setC_DocType_ID (0);
setPaymentType (null);	// EC
}
 */
}
/** Load Constructor */
public X_C_BankList_Config (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BankList_Config");

/** TableName=C_BankList_Config */
public static final String Table_Name="C_BankList_Config";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BankList_Config");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BankList_Config[").append(getID()).append("]");
return sb.toString();
}
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
/** Set C_BankList_Config_ID */
public void setC_BankList_Config_ID (int C_BankList_Config_ID)
{
set_ValueNoCheck ("C_BankList_Config_ID", new Integer(C_BankList_Config_ID));
}
/** Get C_BankList_Config_ID */
public int getC_BankList_Config_ID() 
{
Integer ii = (Integer)get_Value("C_BankList_Config_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Client Acronym */
public void setClientAcronym (String ClientAcronym)
{
if (ClientAcronym != null && ClientAcronym.length() > 10)
{
log.warning("Length > 10 - truncated");
ClientAcronym = ClientAcronym.substring(0,10);
}
set_Value ("ClientAcronym", ClientAcronym);
}
/** Get Client Acronym */
public String getClientAcronym() 
{
return (String)get_Value("ClientAcronym");
}
/** Set Client Name */
public void setClientName (String ClientName)
{
if (ClientName != null && ClientName.length() > 40)
{
log.warning("Length > 40 - truncated");
ClientName = ClientName.substring(0,40);
}
set_Value ("ClientName", ClientName);
}
/** Get Client Name */
public String getClientName() 
{
return (String)get_Value("ClientName");
}
public static final int PAYMENTTYPE_AD_Reference_ID = MReference.getReferenceID("Electronic Payment Types");
/** Electronic Check = EC */
public static final String PAYMENTTYPE_ElectronicCheck = "EC";
/** Set PaymentType */
public void setPaymentType (String PaymentType)
{
if (PaymentType.equals("EC"));
 else throw new IllegalArgumentException ("PaymentType Invalid value - Reference = PAYMENTTYPE_AD_Reference_ID - EC");
if (PaymentType == null) throw new IllegalArgumentException ("PaymentType is mandatory");
if (PaymentType.length() > 2)
{
log.warning("Length > 2 - truncated");
PaymentType = PaymentType.substring(0,2);
}
set_Value ("PaymentType", PaymentType);
}
/** Get PaymentType */
public String getPaymentType() 
{
return (String)get_Value("PaymentType");
}
/** Set Register Number */
public void setRegisterNumber (String RegisterNumber)
{
if (RegisterNumber != null && RegisterNumber.length() > 60)
{
log.warning("Length > 60 - truncated");
RegisterNumber = RegisterNumber.substring(0,60);
}
set_Value ("RegisterNumber", RegisterNumber);
}
/** Get Register Number */
public String getRegisterNumber() 
{
return (String)get_Value("RegisterNumber");
}
/** Set Sucursal Default */
public void setSucursalDefault (String SucursalDefault)
{
if (SucursalDefault != null && SucursalDefault.length() > 10)
{
log.warning("Length > 10 - truncated");
SucursalDefault = SucursalDefault.substring(0,10);
}
set_Value ("SucursalDefault", SucursalDefault);
}
/** Get Sucursal Default */
public String getSucursalDefault() 
{
return (String)get_Value("SucursalDefault");
}
}
