/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Country
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2015-08-23 17:44:27.29 */
public class X_C_Country extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Country (Properties ctx, int C_Country_ID, String trxName)
{
super (ctx, C_Country_ID, trxName);
/** if (C_Country_ID == 0)
{
setC_Country_ID (0);
setCountryCode (null);
setDisplaySequence (null);	// @C@, @R@ @P@
setHasPostal_Add (false);
setHasRegion (false);
setIsAddressLinesLocalReverse (false);
setIsAddressLinesReverse (false);
setIsPostCodeLookup (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_Country (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Country");

/** TableName=C_Country */
public static final String Table_Name="C_Country";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Country");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Country[").append(getID()).append("]");
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
public static final int AD_LANGUAGE_AD_Reference_ID = MReference.getReferenceID("AD_Language");
/** Set Language.
Language for this entity */
public void setAD_Language (String AD_Language)
{
if (AD_Language != null && AD_Language.length() > 6)
{
log.warning("Length > 6 - truncated");
AD_Language = AD_Language.substring(0,6);
}
set_Value ("AD_Language", AD_Language);
}
/** Get Language.
Language for this entity */
public String getAD_Language() 
{
return (String)get_Value("AD_Language");
}
/** Set AllowCitiesOutOfList */
public void setAllowCitiesOutOfList (boolean AllowCitiesOutOfList)
{
set_Value ("AllowCitiesOutOfList", new Boolean(AllowCitiesOutOfList));
}
/** Get AllowCitiesOutOfList */
public boolean isAllowCitiesOutOfList() 
{
Object oo = get_Value("AllowCitiesOutOfList");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set CaptureSequence */
public void setCaptureSequence (String CaptureSequence)
{
if (CaptureSequence != null && CaptureSequence.length() > 60)
{
log.warning("Length > 60 - truncated");
CaptureSequence = CaptureSequence.substring(0,60);
}
set_Value ("CaptureSequence", CaptureSequence);
}
/** Get CaptureSequence */
public String getCaptureSequence() 
{
return (String)get_Value("CaptureSequence");
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
/** Set ISO Country Code.
Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html */
public void setCountryCode (String CountryCode)
{
if (CountryCode == null) throw new IllegalArgumentException ("CountryCode is mandatory");
if (CountryCode.length() > 2)
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
/** Set Country Code FE */
public void setCountryCodeFE (String CountryCodeFE)
{
if (CountryCodeFE != null && CountryCodeFE.length() > 10)
{
log.warning("Length > 10 - truncated");
CountryCodeFE = CountryCodeFE.substring(0,10);
}
set_Value ("CountryCodeFE", CountryCodeFE);
}
/** Get Country Code FE */
public String getCountryCodeFE() 
{
return (String)get_Value("CountryCodeFE");
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
/** Set Address Print Format.
Format for printing this Address */
public void setDisplaySequence (String DisplaySequence)
{
if (DisplaySequence == null) throw new IllegalArgumentException ("DisplaySequence is mandatory");
if (DisplaySequence.length() > 20)
{
log.warning("Length > 20 - truncated");
DisplaySequence = DisplaySequence.substring(0,20);
}
set_Value ("DisplaySequence", DisplaySequence);
}
/** Get Address Print Format.
Format for printing this Address */
public String getDisplaySequence() 
{
return (String)get_Value("DisplaySequence");
}
/** Set Local Address Format.
Format for printing this Address locally */
public void setDisplaySequenceLocal (String DisplaySequenceLocal)
{
if (DisplaySequenceLocal != null && DisplaySequenceLocal.length() > 20)
{
log.warning("Length > 20 - truncated");
DisplaySequenceLocal = DisplaySequenceLocal.substring(0,20);
}
set_Value ("DisplaySequenceLocal", DisplaySequenceLocal);
}
/** Get Local Address Format.
Format for printing this Address locally */
public String getDisplaySequenceLocal() 
{
return (String)get_Value("DisplaySequenceLocal");
}
/** Set Bank Account No Format.
Format of the Bank Account */
public void setExpressionBankAccountNo (String ExpressionBankAccountNo)
{
if (ExpressionBankAccountNo != null && ExpressionBankAccountNo.length() > 20)
{
log.warning("Length > 20 - truncated");
ExpressionBankAccountNo = ExpressionBankAccountNo.substring(0,20);
}
set_Value ("ExpressionBankAccountNo", ExpressionBankAccountNo);
}
/** Get Bank Account No Format.
Format of the Bank Account */
public String getExpressionBankAccountNo() 
{
return (String)get_Value("ExpressionBankAccountNo");
}
/** Set Bank Routing No Format.
Format of the Bank Routing Number */
public void setExpressionBankRoutingNo (String ExpressionBankRoutingNo)
{
if (ExpressionBankRoutingNo != null && ExpressionBankRoutingNo.length() > 20)
{
log.warning("Length > 20 - truncated");
ExpressionBankRoutingNo = ExpressionBankRoutingNo.substring(0,20);
}
set_Value ("ExpressionBankRoutingNo", ExpressionBankRoutingNo);
}
/** Get Bank Routing No Format.
Format of the Bank Routing Number */
public String getExpressionBankRoutingNo() 
{
return (String)get_Value("ExpressionBankRoutingNo");
}
/** Set Phone Format.
Format of the phone;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public void setExpressionPhone (String ExpressionPhone)
{
if (ExpressionPhone != null && ExpressionPhone.length() > 20)
{
log.warning("Length > 20 - truncated");
ExpressionPhone = ExpressionPhone.substring(0,20);
}
set_Value ("ExpressionPhone", ExpressionPhone);
}
/** Get Phone Format.
Format of the phone;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public String getExpressionPhone() 
{
return (String)get_Value("ExpressionPhone");
}
/** Set Postal Code Format.
Format of the postal code;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public void setExpressionPostal (String ExpressionPostal)
{
if (ExpressionPostal != null && ExpressionPostal.length() > 20)
{
log.warning("Length > 20 - truncated");
ExpressionPostal = ExpressionPostal.substring(0,20);
}
set_Value ("ExpressionPostal", ExpressionPostal);
}
/** Get Postal Code Format.
Format of the postal code;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public String getExpressionPostal() 
{
return (String)get_Value("ExpressionPostal");
}
/** Set Additional Postal Format.
Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public void setExpressionPostal_Add (String ExpressionPostal_Add)
{
if (ExpressionPostal_Add != null && ExpressionPostal_Add.length() > 20)
{
log.warning("Length > 20 - truncated");
ExpressionPostal_Add = ExpressionPostal_Add.substring(0,20);
}
set_Value ("ExpressionPostal_Add", ExpressionPostal_Add);
}
/** Get Additional Postal Format.
Format of the value;
 Can contain fixed format elements, Variables: "_lLoOaAcCa09" */
public String getExpressionPostal_Add() 
{
return (String)get_Value("ExpressionPostal_Add");
}
/** Set Additional Postal code.
Has Additional Postal Code */
public void setHasPostal_Add (boolean HasPostal_Add)
{
set_Value ("HasPostal_Add", new Boolean(HasPostal_Add));
}
/** Get Additional Postal code.
Has Additional Postal Code */
public boolean isHasPostal_Add() 
{
Object oo = get_Value("HasPostal_Add");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Country has Region.
Country contains Regions */
public void setHasRegion (boolean HasRegion)
{
set_Value ("HasRegion", new Boolean(HasRegion));
}
/** Get Country has Region.
Country contains Regions */
public boolean isHasRegion() 
{
Object oo = get_Value("HasRegion");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Reverse Local Address Lines.
Print Local Address in reverse Order */
public void setIsAddressLinesLocalReverse (boolean IsAddressLinesLocalReverse)
{
set_Value ("IsAddressLinesLocalReverse", new Boolean(IsAddressLinesLocalReverse));
}
/** Get Reverse Local Address Lines.
Print Local Address in reverse Order */
public boolean isAddressLinesLocalReverse() 
{
Object oo = get_Value("IsAddressLinesLocalReverse");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Reverse Address Lines.
Print Address in reverse Order */
public void setIsAddressLinesReverse (boolean IsAddressLinesReverse)
{
set_Value ("IsAddressLinesReverse", new Boolean(IsAddressLinesReverse));
}
/** Get Reverse Address Lines.
Print Address in reverse Order */
public boolean isAddressLinesReverse() 
{
Object oo = get_Value("IsAddressLinesReverse");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsPostCodeLookup */
public void setIsPostCodeLookup (boolean IsPostCodeLookup)
{
set_Value ("IsPostCodeLookup", new Boolean(IsPostCodeLookup));
}
/** Get IsPostCodeLookup */
public boolean isPostCodeLookup() 
{
Object oo = get_Value("IsPostCodeLookup");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set LookupClassName */
public void setLookupClassName (String LookupClassName)
{
if (LookupClassName != null && LookupClassName.length() > 255)
{
log.warning("Length > 255 - truncated");
LookupClassName = LookupClassName.substring(0,255);
}
set_Value ("LookupClassName", LookupClassName);
}
/** Get LookupClassName */
public String getLookupClassName() 
{
return (String)get_Value("LookupClassName");
}
/** Set LookupClientID */
public void setLookupClientID (String LookupClientID)
{
if (LookupClientID != null && LookupClientID.length() > 50)
{
log.warning("Length > 50 - truncated");
LookupClientID = LookupClientID.substring(0,50);
}
set_Value ("LookupClientID", LookupClientID);
}
/** Get LookupClientID */
public String getLookupClientID() 
{
return (String)get_Value("LookupClientID");
}
/** Set LookupPassword */
public void setLookupPassword (String LookupPassword)
{
if (LookupPassword != null && LookupPassword.length() > 50)
{
log.warning("Length > 50 - truncated");
LookupPassword = LookupPassword.substring(0,50);
}
set_Value ("LookupPassword", LookupPassword);
}
/** Get LookupPassword */
public String getLookupPassword() 
{
return (String)get_Value("LookupPassword");
}
/** Set LookupUrl */
public void setLookupUrl (String LookupUrl)
{
if (LookupUrl != null && LookupUrl.length() > 100)
{
log.warning("Length > 100 - truncated");
LookupUrl = LookupUrl.substring(0,100);
}
set_Value ("LookupUrl", LookupUrl);
}
/** Get LookupUrl */
public String getLookupUrl() 
{
return (String)get_Value("LookupUrl");
}
/** Set Media Size.
Java Media Size */
public void setMediaSize (String MediaSize)
{
if (MediaSize != null && MediaSize.length() > 40)
{
log.warning("Length > 40 - truncated");
MediaSize = MediaSize.substring(0,40);
}
set_Value ("MediaSize", MediaSize);
}
/** Get Media Size.
Java Media Size */
public String getMediaSize() 
{
return (String)get_Value("MediaSize");
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name == null) throw new IllegalArgumentException ("Name is mandatory");
if (Name.length() > 60)
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
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
}
