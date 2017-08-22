/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_BPartner
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-08-22 12:40:10.347 */
public class X_C_BPartner extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_BPartner (Properties ctx, int C_BPartner_ID, String trxName)
{
super (ctx, C_BPartner_ID, trxName);
/** if (C_BPartner_ID == 0)
{
setAllowAdvancedPaymentReceipts (false);
setAllowPartialPayment (false);
setAutomaticCreditNotes (false);
setC_BPartner_ID (0);
setC_BP_Group_ID (0);
setCreditMinimumAmt (Env.ZERO);
setDiscountContext (null);	// B
setIsConvenioMultilateral (false);
setIsCustomer (false);
setIsEmployee (false);
setIsGroupInvoices (false);
setisiso (false);
setIsMandatoryCAI (false);
setIsMultiCUIT (false);
setIsOneTime (false);
setIsProspect (true);	// Y
setIsSalesRep (false);
setIsSummary (false);
setIsVendor (false);
setMinimumPurchasedAmt (Env.ZERO);
setName (null);
setProcessPO (null);
setSearchUnallocatedPayments (false);
setSecondaryCreditStatus (null);	// OK
setSendEMail (false);
setSO_CreditLimit (Env.ZERO);
setSO_CreditUsed (Env.ZERO);
setValue (null);
}
 */
}
/** Load Constructor */
public X_C_BPartner (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_BPartner");

/** TableName=C_BPartner */
public static final String Table_Name="C_BPartner";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_BPartner");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_BPartner[").append(getID()).append("]");
return sb.toString();
}
/** Set Acquisition Cost.
The cost of gaining the prospect as a customer */
public void setAcqusitionCost (BigDecimal AcqusitionCost)
{
set_Value ("AcqusitionCost", AcqusitionCost);
}
/** Get Acquisition Cost.
The cost of gaining the prospect as a customer */
public BigDecimal getAcqusitionCost() 
{
BigDecimal bd = (BigDecimal)get_Value("AcqusitionCost");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Actual Life Time Value.
Actual Life Time Revenue */
public void setActualLifeTimeValue (BigDecimal ActualLifeTimeValue)
{
set_Value ("ActualLifeTimeValue", ActualLifeTimeValue);
}
/** Get Actual Life Time Value.
Actual Life Time Revenue */
public BigDecimal getActualLifeTimeValue() 
{
BigDecimal bd = (BigDecimal)get_Value("ActualLifeTimeValue");
if (bd == null) return Env.ZERO;
return bd;
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
public static final int AD_LANGUAGE_AD_Reference_ID = MReference.getReferenceID("AD_Language System");
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
/** Set Linked Organization.
The Business Partner is another Organization for explicit Inter-Org transactions */
public void setAD_OrgBP_ID (String AD_OrgBP_ID)
{
if (AD_OrgBP_ID != null && AD_OrgBP_ID.length() > 22)
{
log.warning("Length > 22 - truncated");
AD_OrgBP_ID = AD_OrgBP_ID.substring(0,22);
}
set_Value ("AD_OrgBP_ID", AD_OrgBP_ID);
}
/** Get Linked Organization.
The Business Partner is another Organization for explicit Inter-Org transactions */
public String getAD_OrgBP_ID() 
{
return (String)get_Value("AD_OrgBP_ID");
}
/** Set Allow Advanced Payment Receipts */
public void setAllowAdvancedPaymentReceipts (boolean AllowAdvancedPaymentReceipts)
{
set_Value ("AllowAdvancedPaymentReceipts", new Boolean(AllowAdvancedPaymentReceipts));
}
/** Get Allow Advanced Payment Receipts */
public boolean isAllowAdvancedPaymentReceipts() 
{
Object oo = get_Value("AllowAdvancedPaymentReceipts");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Allow Partial Payment */
public void setAllowPartialPayment (boolean AllowPartialPayment)
{
set_Value ("AllowPartialPayment", new Boolean(AllowPartialPayment));
}
/** Get Allow Partial Payment */
public boolean isAllowPartialPayment() 
{
Object oo = get_Value("AllowPartialPayment");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Check to */
public void setA_Name_Check (String A_Name_Check)
{
if (A_Name_Check != null && A_Name_Check.length() > 60)
{
log.warning("Length > 60 - truncated");
A_Name_Check = A_Name_Check.substring(0,60);
}
set_Value ("A_Name_Check", A_Name_Check);
}
/** Get Check to */
public String getA_Name_Check() 
{
return (String)get_Value("A_Name_Check");
}
/** Set Automatic Credit Notes */
public void setAutomaticCreditNotes (boolean AutomaticCreditNotes)
{
set_Value ("AutomaticCreditNotes", new Boolean(AutomaticCreditNotes));
}
/** Get Automatic Credit Notes */
public boolean isAutomaticCreditNotes() 
{
Object oo = get_Value("AutomaticCreditNotes");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int BATCH_PAYMENT_RULE_AD_Reference_ID = MReference.getReferenceID("Batch Payment Rules");
/** Check = C */
public static final String BATCH_PAYMENT_RULE_Check = "C";
/** Electronic Check = E */
public static final String BATCH_PAYMENT_RULE_ElectronicCheck = "E";
/** Set Batch Payment Rule */
public void setBatch_Payment_Rule (String Batch_Payment_Rule)
{
if (Batch_Payment_Rule == null || Batch_Payment_Rule.equals("C") || Batch_Payment_Rule.equals("E") || ( refContainsValue("SSTE2CORE-AD_Reference-1010278-20161024122334", Batch_Payment_Rule) ) );
 else throw new IllegalArgumentException ("Batch_Payment_Rule Invalid value: " + Batch_Payment_Rule + ".  Valid: " +  refValidOptions("SSTE2CORE-AD_Reference-1010278-20161024122334") );
if (Batch_Payment_Rule != null && Batch_Payment_Rule.length() > 1)
{
log.warning("Length > 1 - truncated");
Batch_Payment_Rule = Batch_Payment_Rule.substring(0,1);
}
set_Value ("Batch_Payment_Rule", Batch_Payment_Rule);
}
/** Get Batch Payment Rule */
public String getBatch_Payment_Rule() 
{
return (String)get_Value("Batch_Payment_Rule");
}
public static final int BPARTNER_PARENT_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner (No Summary)");
/** Set Partner Parent.
Business Partner Parent */
public void setBPartner_Parent_ID (int BPartner_Parent_ID)
{
if (BPartner_Parent_ID <= 0) set_Value ("BPartner_Parent_ID", null);
 else 
set_Value ("BPartner_Parent_ID", new Integer(BPartner_Parent_ID));
}
/** Get Partner Parent.
Business Partner Parent */
public int getBPartner_Parent_ID() 
{
Integer ii = (Integer)get_Value("BPartner_Parent_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set BuiltCabaJurisdiction */
public void setBuiltCabaJurisdiction (boolean BuiltCabaJurisdiction)
{
set_Value ("BuiltCabaJurisdiction", new Boolean(BuiltCabaJurisdiction));
}
/** Get BuiltCabaJurisdiction */
public boolean isBuiltCabaJurisdiction() 
{
Object oo = get_Value("BuiltCabaJurisdiction");
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
if (C_BankAccount_ID <= 0) set_Value ("C_BankAccount_ID", null);
 else 
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
/** Set Business Partner Group.
Business Partner Group */
public void setC_BP_Group_ID (int C_BP_Group_ID)
{
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
/** Set Categoría de IVA */
public void setC_Categoria_Iva_ID (int C_Categoria_Iva_ID)
{
if (C_Categoria_Iva_ID <= 0) set_Value ("C_Categoria_Iva_ID", null);
 else 
set_Value ("C_Categoria_Iva_ID", new Integer(C_Categoria_Iva_ID));
}
/** Get Categoría de IVA */
public int getC_Categoria_Iva_ID() 
{
Integer ii = (Integer)get_Value("C_Categoria_Iva_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Dunning.
Dunning Rules for overdue invoices */
public void setC_Dunning_ID (int C_Dunning_ID)
{
if (C_Dunning_ID <= 0) set_Value ("C_Dunning_ID", null);
 else 
set_Value ("C_Dunning_ID", new Integer(C_Dunning_ID));
}
/** Get Dunning.
Dunning Rules for overdue invoices */
public int getC_Dunning_ID() 
{
Integer ii = (Integer)get_Value("C_Dunning_ID");
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
/** Set Invoice Schedule.
Schedule for generating Invoices */
public void setC_InvoiceSchedule_ID (int C_InvoiceSchedule_ID)
{
if (C_InvoiceSchedule_ID <= 0) set_Value ("C_InvoiceSchedule_ID", null);
 else 
set_Value ("C_InvoiceSchedule_ID", new Integer(C_InvoiceSchedule_ID));
}
/** Get Invoice Schedule.
Schedule for generating Invoices */
public int getC_InvoiceSchedule_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceSchedule_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Position Category.
Job Position Category */
public void setC_JobCategory_ID (int C_JobCategory_ID)
{
if (C_JobCategory_ID <= 0) set_Value ("C_JobCategory_ID", null);
 else 
set_Value ("C_JobCategory_ID", new Integer(C_JobCategory_ID));
}
/** Get Position Category.
Job Position Category */
public int getC_JobCategory_ID() 
{
Integer ii = (Integer)get_Value("C_JobCategory_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Copy From.
Copy From Record */
public void setCopyFrom (String CopyFrom)
{
if (CopyFrom != null && CopyFrom.length() > 1)
{
log.warning("Length > 1 - truncated");
CopyFrom = CopyFrom.substring(0,1);
}
set_Value ("CopyFrom", CopyFrom);
}
/** Get Copy From.
Copy From Record */
public String getCopyFrom() 
{
return (String)get_Value("CopyFrom");
}
/** Set Copy Vendor Products */
public void setCopyVendorProducts (String CopyVendorProducts)
{
if (CopyVendorProducts != null && CopyVendorProducts.length() > 1)
{
log.warning("Length > 1 - truncated");
CopyVendorProducts = CopyVendorProducts.substring(0,1);
}
set_Value ("CopyVendorProducts", CopyVendorProducts);
}
/** Get Copy Vendor Products */
public String getCopyVendorProducts() 
{
return (String)get_Value("CopyVendorProducts");
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
/** Set POS Payment Medium.
POS Terminal Payment Medium */
public void setC_POSPaymentMedium_ID (int C_POSPaymentMedium_ID)
{
if (C_POSPaymentMedium_ID <= 0) set_Value ("C_POSPaymentMedium_ID", null);
 else 
set_Value ("C_POSPaymentMedium_ID", new Integer(C_POSPaymentMedium_ID));
}
/** Get POS Payment Medium.
POS Terminal Payment Medium */
public int getC_POSPaymentMedium_ID() 
{
Integer ii = (Integer)get_Value("C_POSPaymentMedium_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Credit Minimum Amt */
public void setCreditMinimumAmt (BigDecimal CreditMinimumAmt)
{
if (CreditMinimumAmt == null) throw new IllegalArgumentException ("CreditMinimumAmt is mandatory");
set_Value ("CreditMinimumAmt", CreditMinimumAmt);
}
/** Get Credit Minimum Amt */
public BigDecimal getCreditMinimumAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CreditMinimumAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CreditSituation */
public void setCreditSituation (String CreditSituation)
{
if (CreditSituation != null && CreditSituation.length() > 255)
{
log.warning("Length > 255 - truncated");
CreditSituation = CreditSituation.substring(0,255);
}
set_Value ("CreditSituation", CreditSituation);
}
/** Get CreditSituation */
public String getCreditSituation() 
{
return (String)get_Value("CreditSituation");
}
public static final int CUSTOMERTYPE_AD_Reference_ID = MReference.getReferenceID("customertype");
/** Aplicadores = Aplicadores */
public static final String CUSTOMERTYPE_Aplicadores = "Aplicadores";
/** Metal o Plastico = Metal o Plastico */
public static final String CUSTOMERTYPE_MetalOPlastico = "Metal o Plastico";
/** Pintores = Pintores */
public static final String CUSTOMERTYPE_Pintores = "Pintores";
/** Leasing = Leasing */
public static final String CUSTOMERTYPE_Leasing = "Leasing";
/** Ingenierias = Ingenierias */
public static final String CUSTOMERTYPE_Ingenierias = "Ingenierias";
/** Sin Definir = Sin Definir */
public static final String CUSTOMERTYPE_SinDefinir = "Sin Definir";
/** Almacenistas o revendedores = Almacenistas o revendedores */
public static final String CUSTOMERTYPE_AlmacenistasORevendedores = "Almacenistas o revendedores";
/** Maderas = Maderas */
public static final String CUSTOMERTYPE_Maderas = "Maderas";
/** Set customertype */
public void setCustomerType (String CustomerType)
{
if (CustomerType == null || CustomerType.equals("Aplicadores") || CustomerType.equals("Metal o Plastico") || CustomerType.equals("Pintores") || CustomerType.equals("Leasing") || CustomerType.equals("Ingenierias") || CustomerType.equals("Sin Definir") || CustomerType.equals("Almacenistas o revendedores") || CustomerType.equals("Maderas") || ( refContainsValue("CORE-AD_Reference-1000047", CustomerType) ) );
 else throw new IllegalArgumentException ("CustomerType Invalid value: " + CustomerType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1000047") );
if (CustomerType != null && CustomerType.length() > 22)
{
log.warning("Length > 22 - truncated");
CustomerType = CustomerType.substring(0,22);
}
set_Value ("CustomerType", CustomerType);
}
/** Get customertype */
public String getCustomerType() 
{
return (String)get_Value("CustomerType");
}
public static final int DELIVERYRULE_AD_Reference_ID = MReference.getReferenceID("C_Order DeliveryRule");
/** Availability = A */
public static final String DELIVERYRULE_Availability = "A";
/** Complete Order = O */
public static final String DELIVERYRULE_CompleteOrder = "O";
/** After Receipt = R */
public static final String DELIVERYRULE_AfterReceipt = "R";
/** Complete Line = L */
public static final String DELIVERYRULE_CompleteLine = "L";
/** Force = F */
public static final String DELIVERYRULE_Force = "F";
/** After Invoicing = I */
public static final String DELIVERYRULE_AfterInvoicing = "I";
/** Force - After invoicing = Z */
public static final String DELIVERYRULE_Force_AfterInvoicing = "Z";
/** Set Delivery Rule.
Defines the timing of Delivery */
public void setDeliveryRule (String DeliveryRule)
{
if (DeliveryRule == null || DeliveryRule.equals("A") || DeliveryRule.equals("O") || DeliveryRule.equals("R") || DeliveryRule.equals("L") || DeliveryRule.equals("F") || DeliveryRule.equals("I") || DeliveryRule.equals("Z") || ( refContainsValue("CORE-AD_Reference-151", DeliveryRule) ) );
 else throw new IllegalArgumentException ("DeliveryRule Invalid value: " + DeliveryRule + ".  Valid: " +  refValidOptions("CORE-AD_Reference-151") );
if (DeliveryRule != null && DeliveryRule.length() > 1)
{
log.warning("Length > 1 - truncated");
DeliveryRule = DeliveryRule.substring(0,1);
}
set_Value ("DeliveryRule", DeliveryRule);
}
/** Get Delivery Rule.
Defines the timing of Delivery */
public String getDeliveryRule() 
{
return (String)get_Value("DeliveryRule");
}
public static final int DELIVERYVIARULE_AD_Reference_ID = MReference.getReferenceID("C_Order DeliveryViaRule");
/** Pickup = P */
public static final String DELIVERYVIARULE_Pickup = "P";
/** Shipper = S */
public static final String DELIVERYVIARULE_Shipper = "S";
/** Delivery = D */
public static final String DELIVERYVIARULE_Delivery = "D";
/** Set Delivery Via.
How the order will be delivered */
public void setDeliveryViaRule (String DeliveryViaRule)
{
if (DeliveryViaRule == null || DeliveryViaRule.equals("P") || DeliveryViaRule.equals("S") || DeliveryViaRule.equals("D") || ( refContainsValue("CORE-AD_Reference-152", DeliveryViaRule) ) );
 else throw new IllegalArgumentException ("DeliveryViaRule Invalid value: " + DeliveryViaRule + ".  Valid: " +  refValidOptions("CORE-AD_Reference-152") );
if (DeliveryViaRule != null && DeliveryViaRule.length() > 1)
{
log.warning("Length > 1 - truncated");
DeliveryViaRule = DeliveryViaRule.substring(0,1);
}
set_Value ("DeliveryViaRule", DeliveryViaRule);
}
/** Get Delivery Via.
How the order will be delivered */
public String getDeliveryViaRule() 
{
return (String)get_Value("DeliveryViaRule");
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
public static final int DISCOUNTCONTEXT_AD_Reference_ID = MReference.getReferenceID("Business Partner Discount Context");
/** Bill = B */
public static final String DISCOUNTCONTEXT_Bill = "B";
/** Receipt = R */
public static final String DISCOUNTCONTEXT_Receipt = "R";
/** Set Discount Context */
public void setDiscountContext (String DiscountContext)
{
if (DiscountContext.equals("B") || DiscountContext.equals("R") || ( refContainsValue("CORE-AD_Reference-1010160", DiscountContext) ) );
 else throw new IllegalArgumentException ("DiscountContext Invalid value: " + DiscountContext + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010160") );
if (DiscountContext == null) throw new IllegalArgumentException ("DiscountContext is mandatory");
if (DiscountContext.length() > 1)
{
log.warning("Length > 1 - truncated");
DiscountContext = DiscountContext.substring(0,1);
}
set_Value ("DiscountContext", DiscountContext);
}
/** Get Discount Context */
public String getDiscountContext() 
{
return (String)get_Value("DiscountContext");
}
/** Set Document Copies.
Number of copies to be printed */
public void setDocumentCopies (int DocumentCopies)
{
set_Value ("DocumentCopies", new Integer(DocumentCopies));
}
/** Get Document Copies.
Number of copies to be printed */
public int getDocumentCopies() 
{
Integer ii = (Integer)get_Value("DocumentCopies");
if (ii == null) return 0;
return ii.intValue();
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
/** Set EndHolidays */
public void setEndHolidays (Timestamp EndHolidays)
{
set_Value ("EndHolidays", EndHolidays);
}
/** Get EndHolidays */
public Timestamp getEndHolidays() 
{
return (Timestamp)get_Value("EndHolidays");
}
/** Set EndHolidays2 */
public void setEndHolidays2 (Timestamp EndHolidays2)
{
set_Value ("EndHolidays2", EndHolidays2);
}
/** Get EndHolidays2 */
public Timestamp getEndHolidays2() 
{
return (Timestamp)get_Value("EndHolidays2");
}
/** Set First Sale.
Date of First Sale */
public void setFirstSale (Timestamp FirstSale)
{
set_Value ("FirstSale", FirstSale);
}
/** Get First Sale.
Date of First Sale */
public Timestamp getFirstSale() 
{
return (Timestamp)get_Value("FirstSale");
}
/** Set Flat Discount %.
Flat discount percentage  */
public void setFlatDiscount (BigDecimal FlatDiscount)
{
set_Value ("FlatDiscount", FlatDiscount);
}
/** Get Flat Discount %.
Flat discount percentage  */
public BigDecimal getFlatDiscount() 
{
BigDecimal bd = (BigDecimal)get_Value("FlatDiscount");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int FREIGHTCOSTRULE_AD_Reference_ID = MReference.getReferenceID("C_Order FreightCostRule");
/** Line = L */
public static final String FREIGHTCOSTRULE_Line = "L";
/** Fix price = F */
public static final String FREIGHTCOSTRULE_FixPrice = "F";
/** Calculated = C */
public static final String FREIGHTCOSTRULE_Calculated = "C";
/** Freight included = I */
public static final String FREIGHTCOSTRULE_FreightIncluded = "I";
/** Set Freight Cost Rule.
Method for charging Freight */
public void setFreightCostRule (String FreightCostRule)
{
if (FreightCostRule == null || FreightCostRule.equals("L") || FreightCostRule.equals("F") || FreightCostRule.equals("C") || FreightCostRule.equals("I") || ( refContainsValue("CORE-AD_Reference-153", FreightCostRule) ) );
 else throw new IllegalArgumentException ("FreightCostRule Invalid value: " + FreightCostRule + ".  Valid: " +  refValidOptions("CORE-AD_Reference-153") );
if (FreightCostRule != null && FreightCostRule.length() > 1)
{
log.warning("Length > 1 - truncated");
FreightCostRule = FreightCostRule.substring(0,1);
}
set_Value ("FreightCostRule", FreightCostRule);
}
/** Get Freight Cost Rule.
Method for charging Freight */
public String getFreightCostRule() 
{
return (String)get_Value("FreightCostRule");
}
/** Set Goal */
public void setGoal (BigDecimal Goal)
{
set_Value ("Goal", Goal);
}
/** Get Goal */
public BigDecimal getGoal() 
{
BigDecimal bd = (BigDecimal)get_Value("Goal");
if (bd == null) return Env.ZERO;
return bd;
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
public static final int IIBBTYPE_AD_Reference_ID = MReference.getReferenceID("IIBB Type");
/** Local = 1 */
public static final String IIBBTYPE_Local = "1";
/** Convenio Multilateral = 2 */
public static final String IIBBTYPE_ConvenioMultilateral = "2";
/** No inscripto = 4 */
public static final String IIBBTYPE_NoInscripto = "4";
/** Reg.Simplificado = 5 */
public static final String IIBBTYPE_RegSimplificado = "5";
/** Set IIBB Type */
public void setIIBBType (String IIBBType)
{
if (IIBBType == null || IIBBType.equals("1") || IIBBType.equals("2") || IIBBType.equals("4") || IIBBType.equals("5") || ( refContainsValue("CORE-AD_Reference-1010262", IIBBType) ) );
 else throw new IllegalArgumentException ("IIBBType Invalid value: " + IIBBType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010262") );
if (IIBBType != null && IIBBType.length() > 1)
{
log.warning("Length > 1 - truncated");
IIBBType = IIBBType.substring(0,1);
}
set_Value ("IIBBType", IIBBType);
}
/** Get IIBB Type */
public String getIIBBType() 
{
return (String)get_Value("IIBBType");
}
public static final int INVOICE_PRINTFORMAT_ID_AD_Reference_ID = MReference.getReferenceID("AD_PrintFormat Invoice");
/** Set Invoice Print Format.
Print Format for printing Invoices */
public void setInvoice_PrintFormat_ID (int Invoice_PrintFormat_ID)
{
if (Invoice_PrintFormat_ID <= 0) set_Value ("Invoice_PrintFormat_ID", null);
 else 
set_Value ("Invoice_PrintFormat_ID", new Integer(Invoice_PrintFormat_ID));
}
/** Get Invoice Print Format.
Print Format for printing Invoices */
public int getInvoice_PrintFormat_ID() 
{
Integer ii = (Integer)get_Value("Invoice_PrintFormat_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int INVOICERULE_AD_Reference_ID = MReference.getReferenceID("C_Order InvoiceRule");
/** After Delivery = D */
public static final String INVOICERULE_AfterDelivery = "D";
/** After Order delivered = O */
public static final String INVOICERULE_AfterOrderDelivered = "O";
/** Immediate = I */
public static final String INVOICERULE_Immediate = "I";
/** Customer Schedule after Delivery = S */
public static final String INVOICERULE_CustomerScheduleAfterDelivery = "S";
/** Set Invoice Rule.
Frequency and method of invoicing  */
public void setInvoiceRule (String InvoiceRule)
{
if (InvoiceRule == null || InvoiceRule.equals("D") || InvoiceRule.equals("O") || InvoiceRule.equals("I") || InvoiceRule.equals("S") || ( refContainsValue("CORE-AD_Reference-150", InvoiceRule) ) );
 else throw new IllegalArgumentException ("InvoiceRule Invalid value: " + InvoiceRule + ".  Valid: " +  refValidOptions("CORE-AD_Reference-150") );
if (InvoiceRule != null && InvoiceRule.length() > 1)
{
log.warning("Length > 1 - truncated");
InvoiceRule = InvoiceRule.substring(0,1);
}
set_Value ("InvoiceRule", InvoiceRule);
}
/** Get Invoice Rule.
Frequency and method of invoicing  */
public String getInvoiceRule() 
{
return (String)get_Value("InvoiceRule");
}
/** Set IsCompoundtax */
public void setIsCompoundtax (boolean IsCompoundtax)
{
set_Value ("IsCompoundtax", new Boolean(IsCompoundtax));
}
/** Get IsCompoundtax */
public boolean isCompoundtax() 
{
Object oo = get_Value("IsCompoundtax");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Convenio Multilateral.
Entidades comerciales en Convenio Multilateral */
public void setIsConvenioMultilateral (boolean IsConvenioMultilateral)
{
set_Value ("IsConvenioMultilateral", new Boolean(IsConvenioMultilateral));
}
/** Get Convenio Multilateral.
Entidades comerciales en Convenio Multilateral */
public boolean isConvenioMultilateral() 
{
Object oo = get_Value("IsConvenioMultilateral");
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
/** Set Discount Printed.
Print Discount on Invoice and Order */
public void setIsDiscountPrinted (boolean IsDiscountPrinted)
{
set_Value ("IsDiscountPrinted", new Boolean(IsDiscountPrinted));
}
/** Get Discount Printed.
Print Discount on Invoice and Order */
public boolean isDiscountPrinted() 
{
Object oo = get_Value("IsDiscountPrinted");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Is Group Invoices */
public void setIsGroupInvoices (boolean IsGroupInvoices)
{
set_Value ("IsGroupInvoices", new Boolean(IsGroupInvoices));
}
/** Get Is Group Invoices */
public boolean isGroupInvoices() 
{
Object oo = get_Value("IsGroupInvoices");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set isiso */
public void setisiso (boolean isiso)
{
set_Value ("isiso", new Boolean(isiso));
}
/** Get isiso */
public boolean isiso() 
{
Object oo = get_Value("isiso");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Validate Mandatory CAI */
public void setIsMandatoryCAI (boolean IsMandatoryCAI)
{
set_Value ("IsMandatoryCAI", new Boolean(IsMandatoryCAI));
}
/** Get Validate Mandatory CAI */
public boolean isMandatoryCAI() 
{
Object oo = get_Value("IsMandatoryCAI");
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
/** Set One time transaction */
public void setIsOneTime (boolean IsOneTime)
{
set_Value ("IsOneTime", new Boolean(IsOneTime));
}
/** Get One time transaction */
public boolean isOneTime() 
{
Object oo = get_Value("IsOneTime");
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
/** Set Sales Representative.
Indicates if  the business partner is a sales representative or company agent */
public void setIsSalesRep (boolean IsSalesRep)
{
set_Value ("IsSalesRep", new Boolean(IsSalesRep));
}
/** Get Sales Representative.
Indicates if  the business partner is a sales representative or company agent */
public boolean isSalesRep() 
{
Object oo = get_Value("IsSalesRep");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Summary Level.
This is a summary entity */
public void setIsSummary (boolean IsSummary)
{
set_Value ("IsSummary", new Boolean(IsSummary));
}
/** Get Summary Level.
This is a summary entity */
public boolean isSummary() 
{
Object oo = get_Value("IsSummary");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Tax exempt.
Business partner is exempt from tax */
public void setIsTaxExempt (boolean IsTaxExempt)
{
set_Value ("IsTaxExempt", new Boolean(IsTaxExempt));
}
/** Get Tax exempt.
Business partner is exempt from tax */
public boolean isTaxExempt() 
{
Object oo = get_Value("IsTaxExempt");
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
public static final int M_DISCOUNTSCHEMA_ID_AD_Reference_ID = MReference.getReferenceID("M_DiscountSchema not PL");
/** Set Discount Schema.
Schema to calculate the trade discount percentage */
public void setM_DiscountSchema_ID (int M_DiscountSchema_ID)
{
if (M_DiscountSchema_ID <= 0) set_Value ("M_DiscountSchema_ID", null);
 else 
set_Value ("M_DiscountSchema_ID", new Integer(M_DiscountSchema_ID));
}
/** Get Discount Schema.
Schema to calculate the trade discount percentage */
public int getM_DiscountSchema_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Minimum Purchased Amt.
Minimum purchased amt allowed */
public void setMinimumPurchasedAmt (BigDecimal MinimumPurchasedAmt)
{
if (MinimumPurchasedAmt == null) throw new IllegalArgumentException ("MinimumPurchasedAmt is mandatory");
set_Value ("MinimumPurchasedAmt", MinimumPurchasedAmt);
}
/** Get Minimum Purchased Amt.
Minimum purchased amt allowed */
public BigDecimal getMinimumPurchasedAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("MinimumPurchasedAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set min_order_qty */
public void setmin_order_qty (BigDecimal min_order_qty)
{
set_Value ("min_order_qty", min_order_qty);
}
/** Get min_order_qty */
public BigDecimal getmin_order_qty() 
{
BigDecimal bd = (BigDecimal)get_Value("min_order_qty");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set M_Shipper_ID */
public void setM_Shipper_ID (int M_Shipper_ID)
{
if (M_Shipper_ID <= 0) set_Value ("M_Shipper_ID", null);
 else 
set_Value ("M_Shipper_ID", new Integer(M_Shipper_ID));
}
/** Get M_Shipper_ID */
public int getM_Shipper_ID() 
{
Integer ii = (Integer)get_Value("M_Shipper_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Employees.
Number of employees */
public void setNumberEmployees (int NumberEmployees)
{
set_Value ("NumberEmployees", new Integer(NumberEmployees));
}
/** Get Employees.
Number of employees */
public int getNumberEmployees() 
{
Integer ii = (Integer)get_Value("NumberEmployees");
if (ii == null) return 0;
return ii.intValue();
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
/** Payment Check = PC */
public static final String PAYMENTRULE_PaymentCheck = "PC";
/** Direct Deposit = T */
public static final String PAYMENTRULE_DirectDeposit = "T";
/** Confirming = Cf */
public static final String PAYMENTRULE_Confirming = "Cf";
/** Direct Debit = D */
public static final String PAYMENTRULE_DirectDebit = "D";
/** Check = S */
public static final String PAYMENTRULE_Check = "S";
/** Set Payment Rule.
How you pay the invoice */
public void setPaymentRule (String PaymentRule)
{
if (PaymentRule == null || PaymentRule.equals("Tr") || PaymentRule.equals("K") || PaymentRule.equals("B") || PaymentRule.equals("P") || PaymentRule.equals("PC") || PaymentRule.equals("T") || PaymentRule.equals("Cf") || PaymentRule.equals("D") || PaymentRule.equals("S") || ( refContainsValue("CORE-AD_Reference-195", PaymentRule) ) );
 else throw new IllegalArgumentException ("PaymentRule Invalid value: " + PaymentRule + ".  Valid: " +  refValidOptions("CORE-AD_Reference-195") );
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
public static final int PAYMENTRULEPO_AD_Reference_ID = MReference.getReferenceID("All_Payment Rule");
/** Transfer = Tr */
public static final String PAYMENTRULEPO_Transfer = "Tr";
/** Credit Card = K */
public static final String PAYMENTRULEPO_CreditCard = "K";
/** Cash = B */
public static final String PAYMENTRULEPO_Cash = "B";
/** On Credit = P */
public static final String PAYMENTRULEPO_OnCredit = "P";
/** Payment Check = PC */
public static final String PAYMENTRULEPO_PaymentCheck = "PC";
/** Direct Deposit = T */
public static final String PAYMENTRULEPO_DirectDeposit = "T";
/** Confirming = Cf */
public static final String PAYMENTRULEPO_Confirming = "Cf";
/** Direct Debit = D */
public static final String PAYMENTRULEPO_DirectDebit = "D";
/** Check = S */
public static final String PAYMENTRULEPO_Check = "S";
/** Set Payment Rule.
Purchase payment option */
public void setPaymentRulePO (String PaymentRulePO)
{
if (PaymentRulePO == null || PaymentRulePO.equals("Tr") || PaymentRulePO.equals("K") || PaymentRulePO.equals("B") || PaymentRulePO.equals("P") || PaymentRulePO.equals("PC") || PaymentRulePO.equals("T") || PaymentRulePO.equals("Cf") || PaymentRulePO.equals("D") || PaymentRulePO.equals("S") || ( refContainsValue("CORE-AD_Reference-195", PaymentRulePO) ) );
 else throw new IllegalArgumentException ("PaymentRulePO Invalid value: " + PaymentRulePO + ".  Valid: " +  refValidOptions("CORE-AD_Reference-195") );
if (PaymentRulePO != null && PaymentRulePO.length() > 2)
{
log.warning("Length > 2 - truncated");
PaymentRulePO = PaymentRulePO.substring(0,2);
}
set_Value ("PaymentRulePO", PaymentRulePO);
}
/** Get Payment Rule.
Purchase payment option */
public String getPaymentRulePO() 
{
return (String)get_Value("PaymentRulePO");
}
public static final int PO_DISCOUNTSCHEMA_ID_AD_Reference_ID = MReference.getReferenceID("M_DiscountSchema");
/** Set PO Discount Schema.
Schema to calculate the purchase trade discount percentage */
public void setPO_DiscountSchema_ID (int PO_DiscountSchema_ID)
{
if (PO_DiscountSchema_ID <= 0) set_Value ("PO_DiscountSchema_ID", null);
 else 
set_Value ("PO_DiscountSchema_ID", new Integer(PO_DiscountSchema_ID));
}
/** Get PO Discount Schema.
Schema to calculate the purchase trade discount percentage */
public int getPO_DiscountSchema_ID() 
{
Integer ii = (Integer)get_Value("PO_DiscountSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PO_PAYMENTTERM_ID_AD_Reference_ID = MReference.getReferenceID("C_PaymentTerm");
/** Set PO Payment Term.
Payment rules for a purchase order */
public void setPO_PaymentTerm_ID (int PO_PaymentTerm_ID)
{
if (PO_PaymentTerm_ID <= 0) set_Value ("PO_PaymentTerm_ID", null);
 else 
set_Value ("PO_PaymentTerm_ID", new Integer(PO_PaymentTerm_ID));
}
/** Get PO Payment Term.
Payment rules for a purchase order */
public int getPO_PaymentTerm_ID() 
{
Integer ii = (Integer)get_Value("PO_PaymentTerm_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PO_PRICELIST_ID_AD_Reference_ID = MReference.getReferenceID("M_PriceList");
/** Set Purchase Pricelist.
Price List used by this Business Partner */
public void setPO_PriceList_ID (int PO_PriceList_ID)
{
if (PO_PriceList_ID <= 0) set_Value ("PO_PriceList_ID", null);
 else 
set_Value ("PO_PriceList_ID", new Integer(PO_PriceList_ID));
}
/** Get Purchase Pricelist.
Price List used by this Business Partner */
public int getPO_PriceList_ID() 
{
Integer ii = (Integer)get_Value("PO_PriceList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Order Reference.
Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner */
public void setPOReference (String POReference)
{
if (POReference != null && POReference.length() > 20)
{
log.warning("Length > 20 - truncated");
POReference = POReference.substring(0,20);
}
set_Value ("POReference", POReference);
}
/** Get Order Reference.
Transaction Reference Number (Sales Order, Purchase Order) of your Business Partner */
public String getPOReference() 
{
return (String)get_Value("POReference");
}
/** Set Potential Life Time Value.
Total Revenue expected */
public void setPotentialLifeTimeValue (BigDecimal PotentialLifeTimeValue)
{
set_Value ("PotentialLifeTimeValue", PotentialLifeTimeValue);
}
/** Get Potential Life Time Value.
Total Revenue expected */
public BigDecimal getPotentialLifeTimeValue() 
{
BigDecimal bd = (BigDecimal)get_Value("PotentialLifeTimeValue");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int PROCESSPO_AD_Reference_ID = MReference.getReferenceID("Process PO Values");
/** Activate Vendor Products = Y */
public static final String PROCESSPO_ActivateVendorProducts = "Y";
/** Desactivate Vendor Products = N */
public static final String PROCESSPO_DesactivateVendorProducts = "N";
/** Set Process Vendor Products */
public void setProcessPO (String ProcessPO)
{
if (ProcessPO.equals("Y") || ProcessPO.equals("N") || ( refContainsValue("CORE-AD_Reference-1010382", ProcessPO) ) );
 else throw new IllegalArgumentException ("ProcessPO Invalid value: " + ProcessPO + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010382") );
if (ProcessPO == null) throw new IllegalArgumentException ("ProcessPO is mandatory");
if (ProcessPO.length() > 1)
{
log.warning("Length > 1 - truncated");
ProcessPO = ProcessPO.substring(0,1);
}
set_Value ("ProcessPO", ProcessPO);
}
/** Get Process Vendor Products */
public String getProcessPO() 
{
return (String)get_Value("ProcessPO");
}
/** Set program_amt */
public void setprogram_amt (BigDecimal program_amt)
{
set_Value ("program_amt", program_amt);
}
/** Get program_amt */
public BigDecimal getprogram_amt() 
{
BigDecimal bd = (BigDecimal)get_Value("program_amt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set program_amt_used */
public void setprogram_amt_used (BigDecimal program_amt_used)
{
set_Value ("program_amt_used", program_amt_used);
}
/** Get program_amt_used */
public BigDecimal getprogram_amt_used() 
{
BigDecimal bd = (BigDecimal)get_Value("program_amt_used");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int PROGRAM_INVOICE_AD_Reference_ID = MReference.getReferenceID("c_invoicet");
/** Set program_invoice */
public void setprogram_invoice (int program_invoice)
{
set_Value ("program_invoice", new Integer(program_invoice));
}
/** Get program_invoice */
public int getprogram_invoice() 
{
Integer ii = (Integer)get_Value("program_invoice");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Rating.
Classification or Importance */
public void setRating (String Rating)
{
if (Rating != null && Rating.length() > 1)
{
log.warning("Length > 1 - truncated");
Rating = Rating.substring(0,1);
}
set_Value ("Rating", Rating);
}
/** Get Rating.
Classification or Importance */
public String getRating() 
{
return (String)get_Value("Rating");
}
/** Set Reference No.
Your customer or vendor number at the Business Partner's site */
public void setReferenceNo (String ReferenceNo)
{
if (ReferenceNo != null && ReferenceNo.length() > 40)
{
log.warning("Length > 40 - truncated");
ReferenceNo = ReferenceNo.substring(0,40);
}
set_Value ("ReferenceNo", ReferenceNo);
}
/** Get Reference No.
Your customer or vendor number at the Business Partner's site */
public String getReferenceNo() 
{
return (String)get_Value("ReferenceNo");
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
/** Set Sales Volume in 1.000.
Total Volume of Sales in Thoisand of Currency */
public void setSalesVolume (int SalesVolume)
{
set_Value ("SalesVolume", new Integer(SalesVolume));
}
/** Get Sales Volume in 1.000.
Total Volume of Sales in Thoisand of Currency */
public int getSalesVolume() 
{
Integer ii = (Integer)get_Value("SalesVolume");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Search unallocated payments */
public void setSearchUnallocatedPayments (boolean SearchUnallocatedPayments)
{
set_Value ("SearchUnallocatedPayments", new Boolean(SearchUnallocatedPayments));
}
/** Get Search unallocated payments */
public boolean isSearchUnallocatedPayments() 
{
Object oo = get_Value("SearchUnallocatedPayments");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int SECONDARYCREDITSTATUS_AD_Reference_ID = MReference.getReferenceID("Secondary credit status");
/** OK = OK */
public static final String SECONDARYCREDITSTATUS_OK = "OK";
/** Automatic Stop = AS */
public static final String SECONDARYCREDITSTATUS_AutomaticStop = "AS";
/** First Note = FN */
public static final String SECONDARYCREDITSTATUS_FirstNote = "FN";
/** To Call = TC */
public static final String SECONDARYCREDITSTATUS_ToCall = "TC";
/** Second Note = SN */
public static final String SECONDARYCREDITSTATUS_SecondNote = "SN";
/** To Collector = CO */
public static final String SECONDARYCREDITSTATUS_ToCollector = "CO";
/** To Disable = TD */
public static final String SECONDARYCREDITSTATUS_ToDisable = "TD";
/** Automatic Disabling = AD */
public static final String SECONDARYCREDITSTATUS_AutomaticDisabling = "AD";
/** Set Secondary Credit Status */
public void setSecondaryCreditStatus (String SecondaryCreditStatus)
{
if (SecondaryCreditStatus.equals("OK") || SecondaryCreditStatus.equals("AS") || SecondaryCreditStatus.equals("FN") || SecondaryCreditStatus.equals("TC") || SecondaryCreditStatus.equals("SN") || SecondaryCreditStatus.equals("CO") || SecondaryCreditStatus.equals("TD") || SecondaryCreditStatus.equals("AD") || ( refContainsValue("CORE-AD_Reference-1010165", SecondaryCreditStatus) ) );
 else throw new IllegalArgumentException ("SecondaryCreditStatus Invalid value: " + SecondaryCreditStatus + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010165") );
if (SecondaryCreditStatus == null) throw new IllegalArgumentException ("SecondaryCreditStatus is mandatory");
if (SecondaryCreditStatus.length() > 2)
{
log.warning("Length > 2 - truncated");
SecondaryCreditStatus = SecondaryCreditStatus.substring(0,2);
}
set_Value ("SecondaryCreditStatus", SecondaryCreditStatus);
}
/** Get Secondary Credit Status */
public String getSecondaryCreditStatus() 
{
return (String)get_Value("SecondaryCreditStatus");
}
/** Set Send EMail.
Enable sending Document EMail */
public void setSendEMail (boolean SendEMail)
{
set_Value ("SendEMail", new Boolean(SendEMail));
}
/** Get Send EMail.
Enable sending Document EMail */
public boolean isSendEMail() 
{
Object oo = get_Value("SendEMail");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Share.
Share of Customer's business as a percentage */
public void setShareOfCustomer (int ShareOfCustomer)
{
set_Value ("ShareOfCustomer", new Integer(ShareOfCustomer));
}
/** Get Share.
Share of Customer's business as a percentage */
public int getShareOfCustomer() 
{
Integer ii = (Integer)get_Value("ShareOfCustomer");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Min Shelf Life %.
Minimum Shelf Life in percent based on Product Instance Guarantee Date */
public void setShelfLifeMinPct (int ShelfLifeMinPct)
{
set_Value ("ShelfLifeMinPct", new Integer(ShelfLifeMinPct));
}
/** Get Min Shelf Life %.
Minimum Shelf Life in percent based on Product Instance Guarantee Date */
public int getShelfLifeMinPct() 
{
Integer ii = (Integer)get_Value("ShelfLifeMinPct");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Credit Limit.
Total outstanding invoice amounts allowed */
public void setSO_CreditLimit (BigDecimal SO_CreditLimit)
{
if (SO_CreditLimit == null) throw new IllegalArgumentException ("SO_CreditLimit is mandatory");
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
if (SOCreditStatus == null || SOCreditStatus.equals("S") || SOCreditStatus.equals("O") || SOCreditStatus.equals("H") || SOCreditStatus.equals("X") || SOCreditStatus.equals("W") || SOCreditStatus.equals("D") || ( refContainsValue("CORE-AD_Reference-289", SOCreditStatus) ) );
 else throw new IllegalArgumentException ("SOCreditStatus Invalid value: " + SOCreditStatus + ".  Valid: " +  refValidOptions("CORE-AD_Reference-289") );
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
/** Set Credit Used.
Current open balance */
public void setSO_CreditUsed (BigDecimal SO_CreditUsed)
{
if (SO_CreditUsed == null) throw new IllegalArgumentException ("SO_CreditUsed is mandatory");
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
/** Set Order Description.
Description to be used on orders */
public void setSO_Description (String SO_Description)
{
if (SO_Description != null && SO_Description.length() > 255)
{
log.warning("Length > 255 - truncated");
SO_Description = SO_Description.substring(0,255);
}
set_Value ("SO_Description", SO_Description);
}
/** Get Order Description.
Description to be used on orders */
public String getSO_Description() 
{
return (String)get_Value("SO_Description");
}
/** Set StartHolidays */
public void setStartHolidays (Timestamp StartHolidays)
{
set_Value ("StartHolidays", StartHolidays);
}
/** Get StartHolidays */
public Timestamp getStartHolidays() 
{
return (Timestamp)get_Value("StartHolidays");
}
/** Set StartHolidays2 */
public void setStartHolidays2 (Timestamp StartHolidays2)
{
set_Value ("StartHolidays2", StartHolidays2);
}
/** Get StartHolidays2 */
public Timestamp getStartHolidays2() 
{
return (Timestamp)get_Value("StartHolidays2");
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
if (TaxIdType == null || TaxIdType.equals("80") || TaxIdType.equals("86") || TaxIdType.equals("87") || TaxIdType.equals("89") || TaxIdType.equals("90") || TaxIdType.equals("91") || TaxIdType.equals("92") || TaxIdType.equals("93") || TaxIdType.equals("95") || TaxIdType.equals("96") || TaxIdType.equals("94") || TaxIdType.equals("00") || TaxIdType.equals("01") || TaxIdType.equals("07") || TaxIdType.equals("08") || TaxIdType.equals("09") || TaxIdType.equals("10") || TaxIdType.equals("11") || TaxIdType.equals("12") || TaxIdType.equals("13") || TaxIdType.equals("14") || TaxIdType.equals("16") || TaxIdType.equals("17") || TaxIdType.equals("18") || TaxIdType.equals("19") || TaxIdType.equals("20") || TaxIdType.equals("02") || TaxIdType.equals("03") || TaxIdType.equals("04") || TaxIdType.equals("05") || TaxIdType.equals("06") || TaxIdType.equals("21") || TaxIdType.equals("22") || TaxIdType.equals("23") || TaxIdType.equals("24") || TaxIdType.equals("25") || TaxIdType.equals("99") || ( refContainsValue("CORE-AD_Reference-1010201", TaxIdType) ) );
 else throw new IllegalArgumentException ("TaxIdType Invalid value: " + TaxIdType + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010201") );
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
/** Set Open Balance.
Total Open Balance Amount in primary Accounting Currency */
public void setTotalOpenBalance (BigDecimal TotalOpenBalance)
{
set_Value ("TotalOpenBalance", TotalOpenBalance);
}
/** Get Open Balance.
Total Open Balance Amount in primary Accounting Currency */
public BigDecimal getTotalOpenBalance() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalOpenBalance");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Update Balance */
public void setUpdateBalance (String UpdateBalance)
{
if (UpdateBalance != null && UpdateBalance.length() > 1)
{
log.warning("Length > 1 - truncated");
UpdateBalance = UpdateBalance.substring(0,1);
}
set_Value ("UpdateBalance", UpdateBalance);
}
/** Get Update Balance */
public String getUpdateBalance() 
{
return (String)get_Value("UpdateBalance");
}
/** Set URL.
URL */
public void setURL (String URL)
{
if (URL != null && URL.length() > 120)
{
log.warning("Length > 120 - truncated");
URL = URL.substring(0,120);
}
set_Value ("URL", URL);
}
/** Get URL.
URL */
public String getURL() 
{
return (String)get_Value("URL");
}
/** Set Search Key.
Search key for the record in the format required - must be unique */
public void setValue (String Value)
{
if (Value == null) throw new IllegalArgumentException ("Value is mandatory");
if (Value.length() > 40)
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
