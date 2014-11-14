/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_BPartner
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-11-14 11:00:54.413 */
public class X_I_BPartner extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_BPartner (Properties ctx, int I_BPartner_ID, String trxName)
{
super (ctx, I_BPartner_ID, trxName);
/** if (I_BPartner_ID == 0)
{
setI_BPartner_ID (0);
setI_IsImported (false);	// N
setIsCustomer (false);
setIsEmployee (false);
setIsMultiCUIT (false);
setIsProspect (false);
setIsVendor (false);
}
 */
}
/** Load Constructor */
public X_I_BPartner (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_BPartner");

/** TableName=I_BPartner */
public static final String Table_Name="I_BPartner";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_BPartner");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_BPartner[").append(getID()).append("]");
return sb.toString();
}
/** Set Address 1.
Address line 1 for this location */
public void setAddress1 (String Address1)
{
if (Address1 != null && Address1.length() > 60)
{
log.warning("Length > 60 - truncated");
Address1 = Address1.substring(0,60);
}
set_Value ("Address1", Address1);
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
Address2 = Address2.substring(0,60);
}
set_Value ("Address2", Address2);
}
/** Get Address 2.
Address line 2 for this location */
public String getAddress2() 
{
return (String)get_Value("Address2");
}
/** Set Address 3.
Address Line 3 for the location */
public void setAddress3 (String Address3)
{
if (Address3 != null && Address3.length() > 60)
{
log.warning("Length > 60 - truncated");
Address3 = Address3.substring(0,60);
}
set_Value ("Address3", Address3);
}
/** Get Address 3.
Address Line 3 for the location */
public String getAddress3() 
{
return (String)get_Value("Address3");
}
/** Set Address 4.
Address Line 4 for the location */
public void setAddress4 (String Address4)
{
if (Address4 != null && Address4.length() > 60)
{
log.warning("Length > 60 - truncated");
Address4 = Address4.substring(0,60);
}
set_Value ("Address4", Address4);
}
/** Get Address 4.
Address Line 4 for the location */
public String getAddress4() 
{
return (String)get_Value("Address4");
}
public static final int AD_ORG_CONTACT_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (all)");
/** Set AD_Org_Contact_ID */
public void setAD_Org_Contact_ID (int AD_Org_Contact_ID)
{
if (AD_Org_Contact_ID <= 0) set_Value ("AD_Org_Contact_ID", null);
 else 
set_Value ("AD_Org_Contact_ID", new Integer(AD_Org_Contact_ID));
}
/** Get AD_Org_Contact_ID */
public int getAD_Org_Contact_ID() 
{
Integer ii = (Integer)get_Value("AD_Org_Contact_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Birthday.
Birthday or Anniversary day */
public void setBirthday (Timestamp Birthday)
{
set_Value ("Birthday", Birthday);
}
/** Get Birthday.
Birthday or Anniversary day */
public Timestamp getBirthday() 
{
return (Timestamp)get_Value("Birthday");
}
/** Set BP Contact Greeting.
Greeting for Business Partner Contact */
public void setBPContactGreeting (String BPContactGreeting)
{
if (BPContactGreeting != null && BPContactGreeting.length() > 60)
{
log.warning("Length > 60 - truncated");
BPContactGreeting = BPContactGreeting.substring(0,60);
}
set_Value ("BPContactGreeting", BPContactGreeting);
}
/** Get BP Contact Greeting.
Greeting for Business Partner Contact */
public String getBPContactGreeting() 
{
return (String)get_Value("BPContactGreeting");
}
/** Set Business Partner .
Identifies a Business Partner */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
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
/** Set Partner Location.
Identifies the (ship to) address for this Business Partner */
public void setC_BPartner_Location_ID (int C_BPartner_Location_ID)
{
if (C_BPartner_Location_ID <= 0) set_Value ("C_BPartner_Location_ID", null);
 else 
set_Value ("C_BPartner_Location_ID", new Integer(C_BPartner_Location_ID));
}
/** Get Partner Location.
Identifies the (ship to) address for this Business Partner */
public int getC_BPartner_Location_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Business Partner Group.
Business Partner Group */
public void setC_BP_Group_ID (int C_BP_Group_ID)
{
if (C_BP_Group_ID <= 0) set_Value ("C_BP_Group_ID", null);
 else 
set_Value ("C_BP_Group_ID", new Integer(C_BP_Group_ID));
}
/** Get Business Partner Group.
Business Partner Group */
public int getC_BP_Group_ID() 
{
Integer ii = (Integer)get_Value("C_BP_Group_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Codigo de Categoría de IVA.
Codigo de Categoría de IVA */
public void setC_Categoria_Iva_Codigo (int C_Categoria_Iva_Codigo)
{
set_Value ("C_Categoria_Iva_Codigo", new Integer(C_Categoria_Iva_Codigo));
}
/** Get Codigo de Categoría de IVA.
Codigo de Categoría de IVA */
public int getC_Categoria_Iva_Codigo() 
{
Integer ii = (Integer)get_Value("C_Categoria_Iva_Codigo");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Categoria de IVA */
public void setC_Categoria_Iva_ID (int C_Categoria_Iva_ID)
{
if (C_Categoria_Iva_ID <= 0) set_Value ("C_Categoria_Iva_ID", null);
 else 
set_Value ("C_Categoria_Iva_ID", new Integer(C_Categoria_Iva_ID));
}
/** Get Categoria de IVA */
public int getC_Categoria_Iva_ID() 
{
Integer ii = (Integer)get_Value("C_Categoria_Iva_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Country.
Country  */
public void setC_Country_ID (int C_Country_ID)
{
if (C_Country_ID <= 0) set_Value ("C_Country_ID", null);
 else 
set_Value ("C_Country_ID", new Integer(C_Country_ID));
}
/** Get Country.
Country  */
public int getC_Country_ID() 
{
Integer ii = (Integer)get_Value("C_Country_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Greeting.
Greeting to print on correspondence */
public void setC_Greeting_ID (int C_Greeting_ID)
{
if (C_Greeting_ID <= 0) set_Value ("C_Greeting_ID", null);
 else 
set_Value ("C_Greeting_ID", new Integer(C_Greeting_ID));
}
/** Get Greeting.
Greeting to print on correspondence */
public int getC_Greeting_ID() 
{
Integer ii = (Integer)get_Value("C_Greeting_ID");
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
City = City.substring(0,60);
}
set_Value ("City", City);
}
/** Get City.
Identifies a City */
public String getCity() 
{
return (String)get_Value("City");
}
/** Set Comments.
Comments or additional information */
public void setComments (String Comments)
{
if (Comments != null && Comments.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Comments = Comments.substring(0,2000);
}
set_Value ("Comments", Comments);
}
/** Get Comments.
Comments or additional information */
public String getComments() 
{
return (String)get_Value("Comments");
}
/** Set Contact Description.
Description of Contact */
public void setContactDescription (String ContactDescription)
{
if (ContactDescription != null && ContactDescription.length() > 255)
{
log.warning("Length > 255 - truncated");
ContactDescription = ContactDescription.substring(0,255);
}
set_Value ("ContactDescription", ContactDescription);
}
/** Get Contact Description.
Description of Contact */
public String getContactDescription() 
{
return (String)get_Value("ContactDescription");
}
/** Set Contact Fax */
public void setContactFax (String ContactFax)
{
if (ContactFax != null && ContactFax.length() > 40)
{
log.warning("Length > 40 - truncated");
ContactFax = ContactFax.substring(0,40);
}
set_Value ("ContactFax", ContactFax);
}
/** Get Contact Fax */
public String getContactFax() 
{
return (String)get_Value("ContactFax");
}
/** Set Contact Name.
Business Partner Contact Name */
public void setContactName (String ContactName)
{
if (ContactName != null && ContactName.length() > 60)
{
log.warning("Length > 60 - truncated");
ContactName = ContactName.substring(0,60);
}
set_Value ("ContactName", ContactName);
}
/** Get Contact Name.
Business Partner Contact Name */
public String getContactName() 
{
return (String)get_Value("ContactName");
}
/** Set Contact Org */
public void setContactOrg (String ContactOrg)
{
if (ContactOrg != null && ContactOrg.length() > 60)
{
log.warning("Length > 60 - truncated");
ContactOrg = ContactOrg.substring(0,60);
}
set_Value ("ContactOrg", ContactOrg);
}
/** Get Contact Org */
public String getContactOrg() 
{
return (String)get_Value("ContactOrg");
}
/** Set Contact Phone */
public void setContactPhone (String ContactPhone)
{
if (ContactPhone != null && ContactPhone.length() > 40)
{
log.warning("Length > 40 - truncated");
ContactPhone = ContactPhone.substring(0,40);
}
set_Value ("ContactPhone", ContactPhone);
}
/** Get Contact Phone */
public String getContactPhone() 
{
return (String)get_Value("ContactPhone");
}
/** Set Contact Phone 2 */
public void setContactPhone2 (String ContactPhone2)
{
if (ContactPhone2 != null && ContactPhone2.length() > 40)
{
log.warning("Length > 40 - truncated");
ContactPhone2 = ContactPhone2.substring(0,40);
}
set_Value ("ContactPhone2", ContactPhone2);
}
/** Get Contact Phone 2 */
public String getContactPhone2() 
{
return (String)get_Value("ContactPhone2");
}
/** Set Contact Phone 3 */
public void setContactPhone3 (String ContactPhone3)
{
if (ContactPhone3 != null && ContactPhone3.length() > 40)
{
log.warning("Length > 40 - truncated");
ContactPhone3 = ContactPhone3.substring(0,40);
}
set_Value ("ContactPhone3", ContactPhone3);
}
/** Get Contact Phone 3 */
public String getContactPhone3() 
{
return (String)get_Value("ContactPhone3");
}
/** Set ISO Country Code.
Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html */
public void setCountryCode (String CountryCode)
{
if (CountryCode != null && CountryCode.length() > 2)
{
log.warning("Length > 2 - truncated");
CountryCode = CountryCode.substring(0,2);
}
set_Value ("CountryCode", CountryCode);
}
/** Get ISO Country Code.
Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html */
public String getCountryCode() 
{
return (String)get_Value("CountryCode");
}
/** Set Region.
Identifies a geographical Region */
public void setC_Region_ID (int C_Region_ID)
{
if (C_Region_ID <= 0) set_Value ("C_Region_ID", null);
 else 
set_Value ("C_Region_ID", new Integer(C_Region_ID));
}
/** Get Region.
Identifies a geographical Region */
public int getC_Region_ID() 
{
Integer ii = (Integer)get_Value("C_Region_ID");
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
/** Set D-U-N-S.
Dun & Bradstreet Number */
public void setDUNS (String DUNS)
{
if (DUNS != null && DUNS.length() > 11)
{
log.warning("Length > 11 - truncated");
DUNS = DUNS.substring(0,11);
}
set_Value ("DUNS", DUNS);
}
/** Get D-U-N-S.
Dun & Bradstreet Number */
public String getDUNS() 
{
return (String)get_Value("DUNS");
}
/** Set EMail.
Electronic Mail Address */
public void setEMail (String EMail)
{
if (EMail != null && EMail.length() > 60)
{
log.warning("Length > 60 - truncated");
EMail = EMail.substring(0,60);
}
set_Value ("EMail", EMail);
}
/** Get EMail.
Electronic Mail Address */
public String getEMail() 
{
return (String)get_Value("EMail");
}
/** Set Fax.
Facsimile number */
public void setFax (String Fax)
{
if (Fax != null && Fax.length() > 40)
{
log.warning("Length > 40 - truncated");
Fax = Fax.substring(0,40);
}
set_Value ("Fax", Fax);
}
/** Get Fax.
Facsimile number */
public String getFax() 
{
return (String)get_Value("Fax");
}
/** Set Group Key.
Business Partner Group Key */
public void setGroupValue (String GroupValue)
{
if (GroupValue != null && GroupValue.length() > 40)
{
log.warning("Length > 40 - truncated");
GroupValue = GroupValue.substring(0,40);
}
set_Value ("GroupValue", GroupValue);
}
/** Get Group Key.
Business Partner Group Key */
public String getGroupValue() 
{
return (String)get_Value("GroupValue");
}
/** Set Import Business Partner */
public void setI_BPartner_ID (int I_BPartner_ID)
{
set_ValueNoCheck ("I_BPartner_ID", new Integer(I_BPartner_ID));
}
/** Get Import Business Partner */
public int getI_BPartner_ID() 
{
Integer ii = (Integer)get_Value("I_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Import Error Message.
Messages generated from import process */
public void setI_ErrorMsg (String I_ErrorMsg)
{
if (I_ErrorMsg != null && I_ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
I_ErrorMsg = I_ErrorMsg.substring(0,2000);
}
set_Value ("I_ErrorMsg", I_ErrorMsg);
}
/** Get Import Error Message.
Messages generated from import process */
public String getI_ErrorMsg() 
{
return (String)get_Value("I_ErrorMsg");
}
/** Set Número de Ingresos Brutos */
public void setIIBB (String IIBB)
{
if (IIBB != null && IIBB.length() > 128)
{
log.warning("Length > 128 - truncated");
IIBB = IIBB.substring(0,128);
}
set_Value ("IIBB", IIBB);
}
/** Get Número de Ingresos Brutos */
public String getIIBB() 
{
return (String)get_Value("IIBB");
}
/** Set Imported.
Has this import been processed */
public void setI_IsImported (boolean I_IsImported)
{
set_Value ("I_IsImported", new Boolean(I_IsImported));
}
/** Get Imported.
Has this import been processed */
public boolean isI_IsImported() 
{
Object oo = get_Value("I_IsImported");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Customer.
Indicates if this Business Partner is a Customer */
public void setIsCustomer (boolean IsCustomer)
{
set_Value ("IsCustomer", new Boolean(IsCustomer));
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
/** Set ISDN.
ISDN or modem line */
public void setISDN (String ISDN)
{
if (ISDN != null && ISDN.length() > 40)
{
log.warning("Length > 40 - truncated");
ISDN = ISDN.substring(0,40);
}
set_Value ("ISDN", ISDN);
}
/** Get ISDN.
ISDN or modem line */
public String getISDN() 
{
return (String)get_Value("ISDN");
}
/** Set Employee.
Indicates if  this Business Partner is an employee */
public void setIsEmployee (boolean IsEmployee)
{
set_Value ("IsEmployee", new Boolean(IsEmployee));
}
/** Get Employee.
Indicates if  this Business Partner is an employee */
public boolean isEmployee() 
{
Object oo = get_Value("IsEmployee");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set CUIT/CUIL múltiple.
El CUIT/CUIL ingresado puede repetirse dentro del sistema */
public void setIsMultiCUIT (boolean IsMultiCUIT)
{
set_Value ("IsMultiCUIT", new Boolean(IsMultiCUIT));
}
/** Get CUIT/CUIL múltiple.
El CUIT/CUIL ingresado puede repetirse dentro del sistema */
public boolean isMultiCUIT() 
{
Object oo = get_Value("IsMultiCUIT");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Active Prospect/Customer.
Indicates a Prospect or Customer */
public void setIsProspect (boolean IsProspect)
{
set_Value ("IsProspect", new Boolean(IsProspect));
}
/** Get Active Prospect/Customer.
Indicates a Prospect or Customer */
public boolean isProspect() 
{
Object oo = get_Value("IsProspect");
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
set_Value ("IsVendor", new Boolean(IsVendor));
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
/** Set NAICS/SIC.
Standard Industry Code or its successor NAIC - http://www.osha.gov/oshstats/sicser.html */
public void setNAICS (String NAICS)
{
if (NAICS != null && NAICS.length() > 6)
{
log.warning("Length > 6 - truncated");
NAICS = NAICS.substring(0,6);
}
set_Value ("NAICS", NAICS);
}
/** Get NAICS/SIC.
Standard Industry Code or its successor NAIC - http://www.osha.gov/oshstats/sicser.html */
public String getNAICS() 
{
return (String)get_Value("NAICS");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 60)
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
/** Set Name 2.
Additional Name */
public void setName2 (String Name2)
{
if (Name2 != null && Name2.length() > 60)
{
log.warning("Length > 60 - truncated");
Name2 = Name2.substring(0,60);
}
set_Value ("Name2", Name2);
}
/** Get Name 2.
Additional Name */
public String getName2() 
{
return (String)get_Value("Name2");
}
/** Set Password.
Password of any length (case sensitive) */
public void setPassword (String Password)
{
if (Password != null && Password.length() > 20)
{
log.warning("Length > 20 - truncated");
Password = Password.substring(0,20);
}
set_Value ("Password", Password);
}
/** Get Password.
Password of any length (case sensitive) */
public String getPassword() 
{
return (String)get_Value("Password");
}
/** Set Phone.
Identifies a telephone number */
public void setPhone (String Phone)
{
if (Phone != null && Phone.length() > 40)
{
log.warning("Length > 40 - truncated");
Phone = Phone.substring(0,40);
}
set_Value ("Phone", Phone);
}
/** Get Phone.
Identifies a telephone number */
public String getPhone() 
{
return (String)get_Value("Phone");
}
/** Set 2nd Phone.
Identifies an alternate telephone number. */
public void setPhone2 (String Phone2)
{
if (Phone2 != null && Phone2.length() > 40)
{
log.warning("Length > 40 - truncated");
Phone2 = Phone2.substring(0,40);
}
set_Value ("Phone2", Phone2);
}
/** Get 2nd Phone.
Identifies an alternate telephone number. */
public String getPhone2() 
{
return (String)get_Value("Phone2");
}
/** Set Plaza */
public void setPlaza (String Plaza)
{
if (Plaza != null && Plaza.length() > 100)
{
log.warning("Length > 100 - truncated");
Plaza = Plaza.substring(0,100);
}
set_Value ("Plaza", Plaza);
}
/** Get Plaza */
public String getPlaza() 
{
return (String)get_Value("Plaza");
}
/** Set ZIP.
Postal code */
public void setPostal (String Postal)
{
if (Postal != null && Postal.length() > 10)
{
log.warning("Length > 10 - truncated");
Postal = Postal.substring(0,10);
}
set_Value ("Postal", Postal);
}
/** Get ZIP.
Postal code */
public String getPostal() 
{
return (String)get_Value("Postal");
}
/** Set -.
Additional ZIP or Postal code */
public void setPostal_Add (String Postal_Add)
{
if (Postal_Add != null && Postal_Add.length() > 10)
{
log.warning("Length > 10 - truncated");
Postal_Add = Postal_Add.substring(0,10);
}
set_Value ("Postal_Add", Postal_Add);
}
/** Get -.
Additional ZIP or Postal code */
public String getPostal_Add() 
{
return (String)get_Value("Postal_Add");
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_ValueNoCheck ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Process Now */
public void setProcessing (boolean Processing)
{
set_Value ("Processing", new Boolean(Processing));
}
/** Get Process Now */
public boolean isProcessing() 
{
Object oo = get_Value("Processing");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Region.
Name of the Region */
public void setRegionName (String RegionName)
{
if (RegionName != null && RegionName.length() > 60)
{
log.warning("Length > 60 - truncated");
RegionName = RegionName.substring(0,60);
}
set_Value ("RegionName", RegionName);
}
/** Get Region.
Name of the Region */
public String getRegionName() 
{
return (String)get_Value("RegionName");
}
public static final int SALESREP_ID_AD_Reference_ID = MReference.getReferenceID("AD_User - SalesRep");
/** Set Sales Representative.
Sales Representative or Company Agent */
public void setSalesRep_ID (int SalesRep_ID)
{
if (SalesRep_ID <= 0) set_Value ("SalesRep_ID", null);
 else 
set_Value ("SalesRep_ID", new Integer(SalesRep_ID));
}
/** Get Sales Representative.
Sales Representative or Company Agent */
public int getSalesRep_ID() 
{
Integer ii = (Integer)get_Value("SalesRep_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Sales Representative */
public void setSalesRep_Name (String SalesRep_Name)
{
if (SalesRep_Name != null && SalesRep_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
SalesRep_Name = SalesRep_Name.substring(0,60);
}
set_Value ("SalesRep_Name", SalesRep_Name);
}
/** Get Sales Representative */
public String getSalesRep_Name() 
{
return (String)get_Value("SalesRep_Name");
}
/** Set Credit Limit.
Total outstanding invoice amounts allowed */
public void setSO_CreditLimit (BigDecimal SO_CreditLimit)
{
set_Value ("SO_CreditLimit", SO_CreditLimit);
}
/** Get Credit Limit.
Total outstanding invoice amounts allowed */
public BigDecimal getSO_CreditLimit() 
{
BigDecimal bd = (BigDecimal)get_Value("SO_CreditLimit");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int SOCREDITSTATUS_AD_Reference_ID = MReference.getReferenceID("C_BPartner SOCreditStatus");
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
/** Credit Disabled = D */
public static final String SOCREDITSTATUS_CreditDisabled = "D";
/** Set Credit Status.
Business Partner Credit Status */
public void setSOCreditStatus (String SOCreditStatus)
{
if (SOCreditStatus == null || SOCreditStatus.equals("S") || SOCreditStatus.equals("O") || SOCreditStatus.equals("H") || SOCreditStatus.equals("X") || SOCreditStatus.equals("W") || SOCreditStatus.equals("D"));
 else throw new IllegalArgumentException ("SOCreditStatus Invalid value - Reference = SOCREDITSTATUS_AD_Reference_ID - S - O - H - X - W - D");
if (SOCreditStatus != null && SOCreditStatus.length() > 1)
{
log.warning("Length > 1 - truncated");
SOCreditStatus = SOCreditStatus.substring(0,1);
}
set_Value ("SOCreditStatus", SOCreditStatus);
}
/** Get Credit Status.
Business Partner Credit Status */
public String getSOCreditStatus() 
{
return (String)get_Value("SOCreditStatus");
}
/** Set Tax ID.
Tax Identification */
public void setTaxID (String TaxID)
{
if (TaxID != null && TaxID.length() > 20)
{
log.warning("Length > 20 - truncated");
TaxID = TaxID.substring(0,20);
}
set_Value ("TaxID", TaxID);
}
/** Get Tax ID.
Tax Identification */
public String getTaxID() 
{
return (String)get_Value("TaxID");
}
public static final int TAXIDTYPE_AD_Reference_ID = MReference.getReferenceID("Tax Id Type");
/** CUIT = 80 */
public static final String TAXIDTYPE_CUIT = "80";
/** CUIL = 86 */
public static final String TAXIDTYPE_CUIL = "86";
/** CDI = 87 */
public static final String TAXIDTYPE_CDI = "87";
/** LE = 89 */
public static final String TAXIDTYPE_LE = "89";
/** LC = 90 */
public static final String TAXIDTYPE_LC = "90";
/** CI extranjera = 91 */
public static final String TAXIDTYPE_CIExtranjera = "91";
/** En Trámite = 92 */
public static final String TAXIDTYPE_EnTrámite = "92";
/** Acta Nacimiento = 93 */
public static final String TAXIDTYPE_ActaNacimiento = "93";
/** CI Bs. As. RNP = 95 */
public static final String TAXIDTYPE_CIBsAsRNP = "95";
/** DNI = 96 */
public static final String TAXIDTYPE_DNI = "96";
/** Pasaporte = 94 */
public static final String TAXIDTYPE_Pasaporte = "94";
/** CI Policía Federal = 00 */
public static final String TAXIDTYPE_CIPolicíaFederal = "00";
/** CI Buenos Aires = 01 */
public static final String TAXIDTYPE_CIBuenosAires = "01";
/** CI Mendoza = 07 */
public static final String TAXIDTYPE_CIMendoza = "07";
/** CI La Rioja = 08 */
public static final String TAXIDTYPE_CILaRioja = "08";
/** CI Salta = 09 */
public static final String TAXIDTYPE_CISalta = "09";
/** CI San Juan = 10 */
public static final String TAXIDTYPE_CISanJuan = "10";
/** CI San Luis = 11 */
public static final String TAXIDTYPE_CISanLuis = "11";
/** CI Santa Fe = 12 */
public static final String TAXIDTYPE_CISantaFe = "12";
/** CI Santiago del Estero = 13 */
public static final String TAXIDTYPE_CISantiagoDelEstero = "13";
/** CI Tucumán = 14 */
public static final String TAXIDTYPE_CITucumán = "14";
/** CI Chaco = 16 */
public static final String TAXIDTYPE_CIChaco = "16";
/** CI Chubut = 17 */
public static final String TAXIDTYPE_CIChubut = "17";
/** CI Formosa = 18 */
public static final String TAXIDTYPE_CIFormosa = "18";
/** CI Misiones = 19 */
public static final String TAXIDTYPE_CIMisiones = "19";
/** CI Neuquén = 20 */
public static final String TAXIDTYPE_CINeuquén = "20";
/** CI Catamarca = 02 */
public static final String TAXIDTYPE_CICatamarca = "02";
/** CI Córdoba = 03 */
public static final String TAXIDTYPE_CICórdoba = "03";
/** CI Corrientes = 04 */
public static final String TAXIDTYPE_CICorrientes = "04";
/** CI Entre Ríos = 05 */
public static final String TAXIDTYPE_CIEntreRíos = "05";
/** CI Jujuy = 06 */
public static final String TAXIDTYPE_CIJujuy = "06";
/** CI La Pampa = 21 */
public static final String TAXIDTYPE_CILaPampa = "21";
/** CI Río Negro = 22 */
public static final String TAXIDTYPE_CIRíoNegro = "22";
/** CI Santa Cruz = 23 */
public static final String TAXIDTYPE_CISantaCruz = "23";
/** CI T. Del Fuego = 24 */
public static final String TAXIDTYPE_CITDelFuego = "24";
/** RUC = 25 */
public static final String TAXIDTYPE_RUC = "25";
/** Sin ID Tipo Documento = 99 */
public static final String TAXIDTYPE_SinIDTipoDocumento = "99";
/** Set Tax Id Type */
public void setTaxIdType (String TaxIdType)
{
if (TaxIdType == null || TaxIdType.equals("80") || TaxIdType.equals("86") || TaxIdType.equals("87") || TaxIdType.equals("89") || TaxIdType.equals("90") || TaxIdType.equals("91") || TaxIdType.equals("92") || TaxIdType.equals("93") || TaxIdType.equals("95") || TaxIdType.equals("96") || TaxIdType.equals("94") || TaxIdType.equals("00") || TaxIdType.equals("01") || TaxIdType.equals("07") || TaxIdType.equals("08") || TaxIdType.equals("09") || TaxIdType.equals("10") || TaxIdType.equals("11") || TaxIdType.equals("12") || TaxIdType.equals("13") || TaxIdType.equals("14") || TaxIdType.equals("16") || TaxIdType.equals("17") || TaxIdType.equals("18") || TaxIdType.equals("19") || TaxIdType.equals("20") || TaxIdType.equals("02") || TaxIdType.equals("03") || TaxIdType.equals("04") || TaxIdType.equals("05") || TaxIdType.equals("06") || TaxIdType.equals("21") || TaxIdType.equals("22") || TaxIdType.equals("23") || TaxIdType.equals("24") || TaxIdType.equals("25") || TaxIdType.equals("99"));
 else throw new IllegalArgumentException ("TaxIdType Invalid value - Reference = TAXIDTYPE_AD_Reference_ID - 80 - 86 - 87 - 89 - 90 - 91 - 92 - 93 - 95 - 96 - 94 - 00 - 01 - 07 - 08 - 09 - 10 - 11 - 12 - 13 - 14 - 16 - 17 - 18 - 19 - 20 - 02 - 03 - 04 - 05 - 06 - 21 - 22 - 23 - 24 - 25 - 99");
if (TaxIdType != null && TaxIdType.length() > 2)
{
log.warning("Length > 2 - truncated");
TaxIdType = TaxIdType.substring(0,2);
}
set_Value ("TaxIdType", TaxIdType);
}
/** Get Tax Id Type */
public String getTaxIdType() 
{
return (String)get_Value("TaxIdType");
}
/** Set Title.
Name this entity is referred to as */
public void setTitle (String Title)
{
if (Title != null && Title.length() > 40)
{
log.warning("Length > 40 - truncated");
Title = Title.substring(0,40);
}
set_Value ("Title", Title);
}
/** Get Title.
Name this entity is referred to as */
public String getTitle() 
{
return (String)get_Value("Title");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value != null && Value.length() > 40)
{
log.warning("Length > 40 - truncated");
Value = Value.substring(0,40);
}
set_Value ("Value", Value);
}
/** Get Search Key.
Search key for the record in the format required - must be unique */
public String getValue() 
{
return (String)get_Value("Value");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getValue());
}
}
