/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_EntidadFinanciera
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2009-10-22 14:51:35.424 */
public class X_M_EntidadFinanciera extends PO
{
/** Constructor estándar */
public X_M_EntidadFinanciera (Properties ctx, int M_EntidadFinanciera_ID, String trxName)
{
super (ctx, M_EntidadFinanciera_ID, trxName);
/** if (M_EntidadFinanciera_ID == 0)
{
setC_BankAccount_ID (0);
setC_BPartner_ID (0);
setCreditCardType (null);
setM_EntidadFinanciera_ID (0);
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_M_EntidadFinanciera (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=1000148 */
public static final int Table_ID=1000148;

/** TableName=M_EntidadFinanciera */
public static final String Table_Name="M_EntidadFinanciera";

protected static KeyNamePair Model = new KeyNamePair(1000148,"M_EntidadFinanciera");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_EntidadFinanciera[").append(getID()).append("]");
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
public static final int C_BPARTNER_ID_AD_Reference_ID=138;
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
/** Set City.
City */
public void setC_City_ID (int C_City_ID)
{
if (C_City_ID <= 0) set_Value ("C_City_ID", null);
 else 
set_Value ("C_City_ID", new Integer(C_City_ID));
}
/** Get City.
City */
public int getC_City_ID() 
{
Integer ii = (Integer)get_Value("C_City_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CREDITCARDTYPE_AD_Reference_ID=149;
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
if (CreditCardType.equals("D") || CreditCardType.equals("C") || CreditCardType.equals("P") || CreditCardType.equals("M") || CreditCardType.equals("V") || CreditCardType.equals("A") || CreditCardType.equals("N"));
 else throw new IllegalArgumentException ("CreditCardType Invalid value - Reference_ID=149 - D - C - P - M - V - A - N");
if (CreditCardType == null) throw new IllegalArgumentException ("CreditCardType is mandatory");
if (CreditCardType.length() > 20)
{
log.warning("Length > 20 - truncated");
CreditCardType = CreditCardType.substring(0,20);
}
set_Value ("CreditCardType", CreditCardType);
}
/** Get Credit Card.
Credit Card (Visa, MC, AmEx) */
public String getCreditCardType() 
{
return (String)get_Value("CreditCardType");
}
/** Set Entidad Financiera */
public void setM_EntidadFinanciera_ID (int M_EntidadFinanciera_ID)
{
set_ValueNoCheck ("M_EntidadFinanciera_ID", new Integer(M_EntidadFinanciera_ID));
}
/** Get Entidad Financiera */
public int getM_EntidadFinanciera_ID() 
{
Integer ii = (Integer)get_Value("M_EntidadFinanciera_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 255)
{
log.warning("Length > 255 - truncated");
Name = Name.substring(0,255);
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
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 255)
{
log.warning("Length > 255 - truncated");
Value = Value.substring(0,255);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
