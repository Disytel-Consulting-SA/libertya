/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_Invoice
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2014-11-17 11:34:19.974 */
public class X_I_Invoice extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_Invoice (Properties ctx, int I_Invoice_ID, String trxName)
{
super (ctx, I_Invoice_ID, trxName);
/** if (I_Invoice_ID == 0)
{
setCreateCashLine (false);
setDocumentNoBySequence (false);
setI_Invoice_ID (0);
setI_IsImported (false);
}
 */
}
/** Load Constructor */
public X_I_Invoice (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_Invoice");

/** TableName=I_Invoice */
public static final String Table_Name="I_Invoice";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_Invoice");
protected static BigDecimal AccessLevel = new BigDecimal(2);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_Invoice[").append(getID()).append("]");
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
public static final int AD_ORGTRX_ID_AD_Reference_ID = MReference.getReferenceID("AD_Org (Trx)");
/** Set Trx Organization.
Performing or initiating organization */
public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
{
if (AD_OrgTrx_ID <= 0) set_Value ("AD_OrgTrx_ID", null);
 else 
set_Value ("AD_OrgTrx_ID", new Integer(AD_OrgTrx_ID));
}
/** Get Trx Organization.
Performing or initiating organization */
public int getAD_OrgTrx_ID() 
{
Integer ii = (Integer)get_Value("AD_OrgTrx_ID");
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
/** Set Business Partner Key.
Key of the Business Partner */
public void setBPartnerValue (String BPartnerValue)
{
if (BPartnerValue != null && BPartnerValue.length() > 40)
{
log.warning("Length > 40 - truncated");
BPartnerValue = BPartnerValue.substring(0,40);
}
set_Value ("BPartnerValue", BPartnerValue);
}
/** Get Business Partner Key.
Key of the Business Partner */
public String getBPartnerValue() 
{
return (String)get_Value("BPartnerValue");
}
/** Set Activity.
Business Activity */
public void setC_Activity_ID (int C_Activity_ID)
{
if (C_Activity_ID <= 0) set_Value ("C_Activity_ID", null);
 else 
set_Value ("C_Activity_ID", new Integer(C_Activity_ID));
}
/** Get Activity.
Business Activity */
public int getC_Activity_ID() 
{
Integer ii = (Integer)get_Value("C_Activity_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Campaign.
Marketing Campaign */
public void setC_Campaign_ID (int C_Campaign_ID)
{
if (C_Campaign_ID <= 0) set_Value ("C_Campaign_ID", null);
 else 
set_Value ("C_Campaign_ID", new Integer(C_Campaign_ID));
}
/** Get Campaign.
Marketing Campaign */
public int getC_Campaign_ID() 
{
Integer ii = (Integer)get_Value("C_Campaign_ID");
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
/** Set Document Type.
Document type or rules */
public void setC_DocType_ID (int C_DocType_ID)
{
if (C_DocType_ID <= 0) set_Value ("C_DocType_ID", null);
 else 
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
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_Value ("C_Invoice_ID", null);
 else 
set_Value ("C_Invoice_ID", new Integer(C_Invoice_ID));
}
/** Get Invoice.
Invoice Identifier */
public int getC_Invoice_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Invoice Line.
Invoice Detail Line */
public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
{
if (C_InvoiceLine_ID <= 0) set_Value ("C_InvoiceLine_ID", null);
 else 
set_Value ("C_InvoiceLine_ID", new Integer(C_InvoiceLine_ID));
}
/** Get Invoice Line.
Invoice Detail Line */
public int getC_InvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceLine_ID");
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
/** Set Address.
Location or Address */
public void setC_Location_ID (int C_Location_ID)
{
if (C_Location_ID <= 0) set_Value ("C_Location_ID", null);
 else 
set_Value ("C_Location_ID", new Integer(C_Location_ID));
}
/** Get Address.
Location or Address */
public int getC_Location_ID() 
{
Integer ii = (Integer)get_Value("C_Location_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set ISO Country Code.
Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html */
public void setCountryCode (boolean CountryCode)
{
set_Value ("CountryCode", new Boolean(CountryCode));
}
/** Get ISO Country Code.
Upper-case two-letter alphanumeric ISO Country code according to ISO 3166-1 - http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html */
public boolean isCountryCode() 
{
Object oo = get_Value("CountryCode");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Payment Term.
The terms for Payment of this transaction */
public void setC_PaymentTerm_ID (int C_PaymentTerm_ID)
{
if (C_PaymentTerm_ID <= 0) set_Value ("C_PaymentTerm_ID", null);
 else 
set_Value ("C_PaymentTerm_ID", new Integer(C_PaymentTerm_ID));
}
/** Get Payment Term.
The terms for Payment of this transaction */
public int getC_PaymentTerm_ID() 
{
Integer ii = (Integer)get_Value("C_PaymentTerm_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Project.
Financial Project */
public void setC_Project_ID (int C_Project_ID)
{
if (C_Project_ID <= 0) set_Value ("C_Project_ID", null);
 else 
set_Value ("C_Project_ID", new Integer(C_Project_ID));
}
/** Get Project.
Financial Project */
public int getC_Project_ID() 
{
Integer ii = (Integer)get_Value("C_Project_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Create Cash Line.
Create Cash Line */
public void setCreateCashLine (boolean CreateCashLine)
{
set_Value ("CreateCashLine", new Boolean(CreateCashLine));
}
/** Get Create Cash Line.
Create Cash Line */
public boolean isCreateCashLine() 
{
Object oo = get_Value("CreateCashLine");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Tax.
Tax identifier */
public void setC_Tax_ID (int C_Tax_ID)
{
if (C_Tax_ID <= 0) set_Value ("C_Tax_ID", null);
 else 
set_Value ("C_Tax_ID", new Integer(C_Tax_ID));
}
/** Get Tax.
Tax identifier */
public int getC_Tax_ID() 
{
Integer ii = (Integer)get_Value("C_Tax_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Account Date.
Accounting Date */
public void setDateAcct (Timestamp DateAcct)
{
set_Value ("DateAcct", DateAcct);
}
/** Get Account Date.
Accounting Date */
public Timestamp getDateAcct() 
{
return (Timestamp)get_Value("DateAcct");
}
/** Set Date Invoiced.
Date printed on Invoice */
public void setDateInvoiced (Timestamp DateInvoiced)
{
set_Value ("DateInvoiced", DateInvoiced);
}
/** Get Date Invoiced.
Date printed on Invoice */
public Timestamp getDateInvoiced() 
{
return (Timestamp)get_Value("DateInvoiced");
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
/** Set Document Type Name.
Name of the Document Type */
public void setDocTypeName (String DocTypeName)
{
if (DocTypeName != null && DocTypeName.length() > 60)
{
log.warning("Length > 60 - truncated");
DocTypeName = DocTypeName.substring(0,60);
}
set_Value ("DocTypeName", DocTypeName);
}
/** Get Document Type Name.
Name of the Document Type */
public String getDocTypeName() 
{
return (String)get_Value("DocTypeName");
}
/** Set Document No.
Document sequence number of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 30)
{
log.warning("Length > 30 - truncated");
DocumentNo = DocumentNo.substring(0,30);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence number of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set DocumentNo By Sequence */
public void setDocumentNoBySequence (boolean DocumentNoBySequence)
{
set_Value ("DocumentNoBySequence", new Boolean(DocumentNoBySequence));
}
/** Get DocumentNo By Sequence */
public boolean isDocumentNoBySequence() 
{
Object oo = get_Value("DocumentNoBySequence");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Import Invoice.
Import Invoice */
public void setI_Invoice_ID (int I_Invoice_ID)
{
set_ValueNoCheck ("I_Invoice_ID", new Integer(I_Invoice_ID));
}
/** Get Import Invoice.
Import Invoice */
public int getI_Invoice_ID() 
{
Integer ii = (Integer)get_Value("I_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set ISO Currency Code.
Three letter ISO 4217 Code of the Currency */
public void setISO_Code (String ISO_Code)
{
if (ISO_Code != null && ISO_Code.length() > 3)
{
log.warning("Length > 3 - truncated");
ISO_Code = ISO_Code.substring(0,3);
}
set_Value ("ISO_Code", ISO_Code);
}
/** Get ISO Currency Code.
Three letter ISO 4217 Code of the Currency */
public String getISO_Code() 
{
return (String)get_Value("ISO_Code");
}
/** Set Sales Transaction.
This is a Sales Transaction */
public void setIsSOTrx (boolean IsSOTrx)
{
set_Value ("IsSOTrx", new Boolean(IsSOTrx));
}
/** Get Sales Transaction.
This is a Sales Transaction */
public boolean isSOTrx() 
{
Object oo = get_Value("IsSOTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Line Description.
Description of the Line */
public void setLineDescription (String LineDescription)
{
if (LineDescription != null && LineDescription.length() > 255)
{
log.warning("Length > 255 - truncated");
LineDescription = LineDescription.substring(0,255);
}
set_Value ("LineDescription", LineDescription);
}
/** Get Line Description.
Description of the Line */
public String getLineDescription() 
{
return (String)get_Value("LineDescription");
}
/** Set Price List.
Unique identifier of a Price List */
public void setM_PriceList_ID (int M_PriceList_ID)
{
if (M_PriceList_ID <= 0) set_Value ("M_PriceList_ID", null);
 else 
set_Value ("M_PriceList_ID", new Integer(M_PriceList_ID));
}
/** Get Price List.
Unique identifier of a Price List */
public int getM_PriceList_ID() 
{
Integer ii = (Integer)get_Value("M_PriceList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
if (M_Product_ID <= 0) set_Value ("M_Product_ID", null);
 else 
set_Value ("M_Product_ID", new Integer(M_Product_ID));
}
/** Get Product.
Product, Service, Item */
public int getM_Product_ID() 
{
Integer ii = (Integer)get_Value("M_Product_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Org Key.
Key of the Organization */
public void setOrgValue (String OrgValue)
{
if (OrgValue != null && OrgValue.length() > 40)
{
log.warning("Length > 40 - truncated");
OrgValue = OrgValue.substring(0,40);
}
set_Value ("OrgValue", OrgValue);
}
/** Get Org Key.
Key of the Organization */
public String getOrgValue() 
{
return (String)get_Value("OrgValue");
}
public static final int PAYMENTRULE_AD_Reference_ID = MReference.getReferenceID("All_Payment Rule");
/** Transfer = Tr */
public static final String PAYMENTRULE_Transfer = "Tr";
/** Credit Card = K */
public static final String PAYMENTRULE_CreditCard = "K";
/** Cash = B */
public static final String PAYMENTRULE_Cash = "B";
/** On Credit = P */
public static final String PAYMENTRULE_OnCredit = "P";
/** Check = S */
public static final String PAYMENTRULE_Check = "S";
/** Payment Check = PC */
public static final String PAYMENTRULE_PaymentCheck = "PC";
/** Direct Deposit = T */
public static final String PAYMENTRULE_DirectDeposit = "T";
/** Confirming = Cf */
public static final String PAYMENTRULE_Confirming = "Cf";
/** Direct Debit = D */
public static final String PAYMENTRULE_DirectDebit = "D";
/** Set Payment Rule.
How you pay the invoice */
public void setPaymentRule (String PaymentRule)
{
if (PaymentRule == null || PaymentRule.equals("Tr") || PaymentRule.equals("K") || PaymentRule.equals("B") || PaymentRule.equals("P") || PaymentRule.equals("S") || PaymentRule.equals("PC") || PaymentRule.equals("T") || PaymentRule.equals("Cf") || PaymentRule.equals("D"));
 else throw new IllegalArgumentException ("PaymentRule Invalid value - Reference = PAYMENTRULE_AD_Reference_ID - Tr - K - B - P - S - PC - T - Cf - D");
if (PaymentRule != null && PaymentRule.length() > 2)
{
log.warning("Length > 2 - truncated");
PaymentRule = PaymentRule.substring(0,2);
}
set_Value ("PaymentRule", PaymentRule);
}
/** Get Payment Rule.
How you pay the invoice */
public String getPaymentRule() 
{
return (String)get_Value("PaymentRule");
}
/** Set Payment Term Key.
Key of the Payment Term */
public void setPaymentTermValue (String PaymentTermValue)
{
if (PaymentTermValue != null && PaymentTermValue.length() > 40)
{
log.warning("Length > 40 - truncated");
PaymentTermValue = PaymentTermValue.substring(0,40);
}
set_Value ("PaymentTermValue", PaymentTermValue);
}
/** Get Payment Term Key.
Key of the Payment Term */
public String getPaymentTermValue() 
{
return (String)get_Value("PaymentTermValue");
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
/** Set Unit Price.
Actual Price  */
public void setPriceActual (BigDecimal PriceActual)
{
set_Value ("PriceActual", PriceActual);
}
/** Get Unit Price.
Actual Price  */
public BigDecimal getPriceActual() 
{
BigDecimal bd = (BigDecimal)get_Value("PriceActual");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Price List Name */
public void setPriceList_Name (String PriceList_Name)
{
if (PriceList_Name != null && PriceList_Name.length() > 60)
{
log.warning("Length > 60 - truncated");
PriceList_Name = PriceList_Name.substring(0,60);
}
set_Value ("PriceList_Name", PriceList_Name);
}
/** Get Price List Name */
public String getPriceList_Name() 
{
return (String)get_Value("PriceList_Name");
}
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
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
/** Set Product Key.
Key of the Product */
public void setProductValue (String ProductValue)
{
if (ProductValue != null && ProductValue.length() > 40)
{
log.warning("Length > 40 - truncated");
ProductValue = ProductValue.substring(0,40);
}
set_Value ("ProductValue", ProductValue);
}
/** Get Product Key.
Key of the Product */
public String getProductValue() 
{
return (String)get_Value("ProductValue");
}
/** Set Ordered Quantity.
Ordered Quantity */
public void setQtyOrdered (BigDecimal QtyOrdered)
{
set_Value ("QtyOrdered", QtyOrdered);
}
/** Get Ordered Quantity.
Ordered Quantity */
public BigDecimal getQtyOrdered() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyOrdered");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set SKU.
Stock Keeping Unit */
public void setSKU (String SKU)
{
if (SKU != null && SKU.length() > 30)
{
log.warning("Length > 30 - truncated");
SKU = SKU.substring(0,30);
}
set_Value ("SKU", SKU);
}
/** Get SKU.
Stock Keeping Unit */
public String getSKU() 
{
return (String)get_Value("SKU");
}
/** Set Tax Amount.
Tax Amount for a document */
public void setTaxAmt (BigDecimal TaxAmt)
{
set_Value ("TaxAmt", TaxAmt);
}
/** Get Tax Amount.
Tax Amount for a document */
public BigDecimal getTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TaxAmt");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Tax Indicator.
Short form for Tax to be printed on documents */
public void setTaxIndicator (String TaxIndicator)
{
if (TaxIndicator != null && TaxIndicator.length() > 10)
{
log.warning("Length > 10 - truncated");
TaxIndicator = TaxIndicator.substring(0,10);
}
set_Value ("TaxIndicator", TaxIndicator);
}
/** Get Tax Indicator.
Short form for Tax to be printed on documents */
public String getTaxIndicator() 
{
return (String)get_Value("TaxIndicator");
}
/** Set UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public void setUPC (String UPC)
{
if (UPC != null && UPC.length() > 30)
{
log.warning("Length > 30 - truncated");
UPC = UPC.substring(0,30);
}
set_Value ("UPC", UPC);
}
/** Get UPC/EAN.
Bar Code (Universal Product Code or its superset European Article Number) */
public String getUPC() 
{
return (String)get_Value("UPC");
}
}
