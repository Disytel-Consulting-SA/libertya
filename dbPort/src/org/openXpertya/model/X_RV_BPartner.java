/** Modelo Generado - NO CAMBIAR MANUALMENTE - Copyright (C) 2006 FUNDESLE */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por RV_BPartner
 *  @author Comunidad de Desarrollo openXpertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2008-01-03 10:26:40.734 */
public class X_RV_BPartner extends PO
{
/** Constructor estándar */
public X_RV_BPartner (Properties ctx, int RV_BPartner_ID, String trxName)
{
super (ctx, RV_BPartner_ID, trxName);
/** if (RV_BPartner_ID == 0)
{
setC_BPartner_ID (0);
setC_BPartner_Location_ID (0);
setC_Country_ID (0);
setCountryName (null);
setIsCustomer (false);
setIsVendor (false);
setName (null);
setValue (null);
}
 */
}
/** Load Constructor */
public X_RV_BPartner (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID=520 */
public static final int Table_ID=520;

/** TableName=RV_BPartner */
public static final String Table_Name="RV_BPartner";

protected static KeyNamePair Model = new KeyNamePair(520,"RV_BPartner");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_RV_BPartner[").append(getID()).append("]");
return sb.toString();
}
/** Set User/Contact.
User within the system - Internal or Business Partner Contact */
public void setAD_User_ID (int AD_User_ID)
{
if (AD_User_ID <= 0) set_ValueNoCheck ("AD_User_ID", null);
 else 
set_ValueNoCheck ("AD_User_ID", new Integer(AD_User_ID));
}
/** Get User/Contact.
User within the system - Internal or Business Partner Contact */
public int getAD_User_ID() 
{
Integer ii = (Integer)get_Value("AD_User_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Address 1.
Address line 1 for this location */
public void setAddress1 (String Address1)
{
if (Address1 != null && Address1.length() > 60)
{
log.warning("Length > 60 - truncated");
Address1 = Address1.substring(0,59);
}
set_ValueNoCheck ("Address1", Address1);
}
/** Get Address 1.
Address line 1 for this location */
public String getAddress1() 
{
return (String)get_Value("Address1");
}
/** Set Address 2.
Address line 2 for this location */
public void setAddress2 (String Address2)
{
if (Address2 != null && Address2.length() > 60)
{
log.warning("Length > 60 - truncated");
Address2 = Address2.substring(0,59);
}
set_ValueNoCheck ("Address2", Address2);
}
/** Get Address 2.
Address line 2 for this location */
public String getAddress2() 
{
return (String)get_Value("Address2");
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
/** Set Partner Location.
Identifies the (ship to) address for this Business Partner */
public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
{
set_ValueNoCheck ("C_BPartner_Location_ID", new Integer(C_BPartner_Location_ID));
}
/** Get Partner Location.
Identifies the (ship to) address for this Business Partner */
public int getC_BPartner_Location_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Country.
Country  */
public void setC_Country_ID (int C_Country_ID)
{
set_ValueNoCheck ("C_Country_ID", new Integer(C_Country_ID));
}
/** Get Country.
Country  */
public int getC_Country_ID() 
{
Integer ii = (Integer)get_Value("C_Country_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Region.
Identifies a geographical Region */
public void setC_Region_ID (int C_Region_ID)
{
if (C_Region_ID <= 0) set_ValueNoCheck ("C_Region_ID", null);
 else 
set_ValueNoCheck ("C_Region_ID", new Integer(C_Region_ID));
}
/** Get Region.
Identifies a geographical Region */
public int getC_Region_ID() 
{
Integer ii = (Integer)get_Value("C_Region_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set City.
Identifies a City */
public void setCity (String City)
{
if (City != null && City.length() > 60)
{
log.warning("Length > 60 - truncated");
City = City.substring(0,59);
}
set_ValueNoCheck ("City", City);
}
/** Get City.
Identifies a City */
public String getCity() 
{
return (String)get_Value("City");
}
/** Set Contact */
public void setContact (String Contact)
{
if (Contact != null && Contact.length() > 60)
{
log.warning("Length > 60 - truncated");
Contact = Contact.substring(0,59);
}
set_ValueNoCheck ("Contact", Contact);
}
/** Get Contact */
public String getContact() 
{
return (String)get_Value("Contact");
}
/** Set Country.
Country Name */
public void setCountryName (String CountryName)
{
if (CountryName == null) throw new IllegalArgumentException ("CountryName is mandatory");
if (CountryName.length() > 60)
{
log.warning("Length > 60 - truncated");
CountryName = CountryName.substring(0,59);
}
set_ValueNoCheck ("CountryName", CountryName);
}
/** Get Country.
Country Name */
public String getCountryName() 
{
return (String)get_Value("CountryName");
}
/** Set EMail.
Electronic Mail Address */
public void setEMail (String EMail)
{
if (EMail != null && EMail.length() > 60)
{
log.warning("Length > 60 - truncated");
EMail = EMail.substring(0,59);
}
set_ValueNoCheck ("EMail", EMail);
}
/** Get EMail.
Electronic Mail Address */
public String getEMail() 
{
return (String)get_Value("EMail");
}
/** Set Customer.
Indicates if this Business Partner is a Customer */
public void setIsCustomer (boolean IsCustomer)
{
set_ValueNoCheck ("IsCustomer", new Boolean(IsCustomer));
}
/** Get Customer.
Indicates if this Business Partner is a Customer */
public boolean isCustomer() 
{
Object oo = get_Value("IsCustomer");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Vendor.
Indicates if this Business Partner is a Vendor */
public void setIsVendor (boolean IsVendor)
{
set_ValueNoCheck ("IsVendor", new Boolean(IsVendor));
}
/** Get Vendor.
Indicates if this Business Partner is a Vendor */
public boolean isVendor() 
{
Object oo = get_Value("IsVendor");
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
Name = Name.substring(0,59);
}
set_ValueNoCheck ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
/** Set Phone.
Identifies a telephone number */
public void setPhone (String Phone)
{
if (Phone != null && Phone.length() > 40)
{
log.warning("Length > 40 - truncated");
Phone = Phone.substring(0,39);
}
set_ValueNoCheck ("Phone", Phone);
}
/** Get Phone.
Identifies a telephone number */
public String getPhone() 
{
return (String)get_Value("Phone");
}
/** Set ZIP.
Postal code */
public void setPostal (String Postal)
{
if (Postal != null && Postal.length() > 10)
{
log.warning("Length > 10 - truncated");
Postal = Postal.substring(0,9);
}
set_ValueNoCheck ("Postal", Postal);
}
/** Get ZIP.
Postal code */
public String getPostal() 
{
return (String)get_Value("Postal");
}
/** Set Reference No.
Your customer or vendor number at the Business Partner's site */
public void setReferenceNo (String ReferenceNo)
{
if (ReferenceNo != null && ReferenceNo.length() > 40)
{
log.warning("Length > 40 - truncated");
ReferenceNo = ReferenceNo.substring(0,39);
}
set_ValueNoCheck ("ReferenceNo", ReferenceNo);
}
/** Get Reference No.
Your customer or vendor number at the Business Partner's site */
public String getReferenceNo() 
{
return (String)get_Value("ReferenceNo");
}
/** Set Region.
Name of the Region */
public void setRegionName (String RegionName)
{
if (RegionName != null && RegionName.length() > 60)
{
log.warning("Length > 60 - truncated");
RegionName = RegionName.substring(0,59);
}
set_ValueNoCheck ("RegionName", RegionName);
}
/** Get Region.
Name of the Region */
public String getRegionName() 
{
return (String)get_Value("RegionName");
}
/** Set Revenue.
Revenue */
public void setRevenue (BigDecimal Revenue)
{
set_ValueNoCheck ("Revenue", Revenue);
}
/** Get Revenue.
Revenue */
public BigDecimal getRevenue() 
{
BigDecimal bd = (BigDecimal)get_Value("Revenue");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int SOCREDITSTATUS_AD_Reference_ID=289;
/** Credit Stop = S */
public static final String SOCREDITSTATUS_CreditStop = "S";
/** Credit OK = O */
public static final String SOCREDITSTATUS_CreditOK = "O";
/** Credit Hold = H */
public static final String SOCREDITSTATUS_CreditHold = "H";
/** No Credit Check = X */
public static final String SOCREDITSTATUS_NoCreditCheck = "X";
/** Credit Watch = W */
public static final String SOCREDITSTATUS_CreditWatch = "W";
/** Set Credit Status.
Business Partner Credit Status */
public void setSOCreditStatus (String SOCreditStatus)
{
if (SOCreditStatus == null || SOCreditStatus.equals("S") || SOCreditStatus.equals("O") || SOCreditStatus.equals("H") || SOCreditStatus.equals("X") || SOCreditStatus.equals("W"));
 else throw new IllegalArgumentException ("SOCreditStatus Invalid value - Reference_ID=289 - S - O - H - X - W");
if (SOCreditStatus != null && SOCreditStatus.length() > 1)
{
log.warning("Length > 1 - truncated");
SOCreditStatus = SOCreditStatus.substring(0,0);
}
set_ValueNoCheck ("SOCreditStatus", SOCreditStatus);
}
/** Get Credit Status.
Business Partner Credit Status */
public String getSOCreditStatus() 
{
return (String)get_Value("SOCreditStatus");
}
/** Set Credit Available.
Available Credit based on Credit Limit (not Total Open Balance) and Credit Used */
public void setSO_CreditAvailable (BigDecimal SO_CreditAvailable)
{
set_ValueNoCheck ("SO_CreditAvailable", SO_CreditAvailable);
}
/** Get Credit Available.
Available Credit based on Credit Limit (not Total Open Balance) and Credit Used */
public BigDecimal getSO_CreditAvailable() 
{
BigDecimal bd = (BigDecimal)get_Value("SO_CreditAvailable");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Limit.
Total outstanding invoice amounts allowed */
public void setSO_CreditLimit (BigDecimal SO_CreditLimit)
{
set_ValueNoCheck ("SO_CreditLimit", SO_CreditLimit);
}
/** Get Credit Limit.
Total outstanding invoice amounts allowed */
public BigDecimal getSO_CreditLimit() 
{
BigDecimal bd = (BigDecimal)get_Value("SO_CreditLimit");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Credit Used.
Current open balance */
public void setSO_CreditUsed (BigDecimal SO_CreditUsed)
{
set_ValueNoCheck ("SO_CreditUsed", SO_CreditUsed);
}
/** Get Credit Used.
Current open balance */
public BigDecimal getSO_CreditUsed() 
{
BigDecimal bd = (BigDecimal)get_Value("SO_CreditUsed");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Open Balance.
Total Open Balance Amount in primary Accounting Currency */
public void setTotalOpenBalance (BigDecimal TotalOpenBalance)
{
set_ValueNoCheck ("TotalOpenBalance", TotalOpenBalance);
}
/** Get Open Balance.
Total Open Balance Amount in primary Accounting Currency */
public BigDecimal getTotalOpenBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalOpenBalance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,39);
}
set_ValueNoCheck ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
}
