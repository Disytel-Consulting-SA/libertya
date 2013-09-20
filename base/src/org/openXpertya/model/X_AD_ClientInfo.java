/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por AD_ClientInfo
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2013-09-20 17:24:32.794 */
public class X_AD_ClientInfo extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_AD_ClientInfo (Properties ctx, int AD_ClientInfo_ID, String trxName)
{
super (ctx, AD_ClientInfo_ID, trxName);
/** if (AD_ClientInfo_ID == 0)
{
setAcct2_Active (false);
setAcct3_Active (false);
setIsDiscountLineAmt (false);
setIsPOSJournalActive (false);
setIsWarehouseCloseControl (false);
setPasswordExpirationActive (false);
setPasswordExpirationDays (0);
setPaymentsPOSJournalOpen (false);
setUniqueKeyActive (false);
}
 */
}
/** Load Constructor */
public X_AD_ClientInfo (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("AD_ClientInfo");

/** TableName=AD_ClientInfo */
public static final String Table_Name="AD_ClientInfo";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"AD_ClientInfo");
protected static BigDecimal AccessLevel = new BigDecimal(6);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_AD_ClientInfo[").append(getID()).append("]");
return sb.toString();
}
/** Set Second Accounting Schema.
For parallel reporting using different accounting currency or field selection */
public void setAcct2_Active (boolean Acct2_Active)
{
set_Value ("Acct2_Active", new Boolean(Acct2_Active));
}
/** Get Second Accounting Schema.
For parallel reporting using different accounting currency or field selection */
public boolean isAcct2_Active() 
{
Object oo = get_Value("Acct2_Active");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Third Accounting Schema.
For parallel reporting using different accounting currency or field selection */
public void setAcct3_Active (boolean Acct3_Active)
{
set_Value ("Acct3_Active", new Boolean(Acct3_Active));
}
/** Get Third Accounting Schema.
For parallel reporting using different accounting currency or field selection */
public boolean isAcct3_Active() 
{
Object oo = get_Value("Acct3_Active");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int AD_TREE_ACTIVITY_ID_AD_Reference_ID = MReference.getReferenceID("AD_Tree");
/** Set Activity Tree.
Tree to determine activity hierarchy */
public void setAD_Tree_Activity_ID (int AD_Tree_Activity_ID)
{
if (AD_Tree_Activity_ID <= 0) set_ValueNoCheck ("AD_Tree_Activity_ID", null);
 else 
set_ValueNoCheck ("AD_Tree_Activity_ID", new Integer(AD_Tree_Activity_ID));
}
/** Get Activity Tree.
Tree to determine activity hierarchy */
public int getAD_Tree_Activity_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_Activity_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_TREE_BPARTNER_ID_AD_Reference_ID = MReference.getReferenceID("AD_Tree");
/** Set BPartner Tree.
Tree to determine business partner hierarchy */
public void setAD_Tree_BPartner_ID (int AD_Tree_BPartner_ID)
{
if (AD_Tree_BPartner_ID <= 0) set_ValueNoCheck ("AD_Tree_BPartner_ID", null);
 else 
set_ValueNoCheck ("AD_Tree_BPartner_ID", new Integer(AD_Tree_BPartner_ID));
}
/** Get BPartner Tree.
Tree to determine business partner hierarchy */
public int getAD_Tree_BPartner_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_TREE_CAMPAIGN_ID_AD_Reference_ID = MReference.getReferenceID("AD_Tree");
/** Set Campaign Tree.
Tree to determine marketing campaign hierarchy */
public void setAD_Tree_Campaign_ID (int AD_Tree_Campaign_ID)
{
if (AD_Tree_Campaign_ID <= 0) set_ValueNoCheck ("AD_Tree_Campaign_ID", null);
 else 
set_ValueNoCheck ("AD_Tree_Campaign_ID", new Integer(AD_Tree_Campaign_ID));
}
/** Get Campaign Tree.
Tree to determine marketing campaign hierarchy */
public int getAD_Tree_Campaign_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_Campaign_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_TREE_MENU_ID_AD_Reference_ID = MReference.getReferenceID("AD_Tree");
/** Set Menu Tree.
Tree of the menu */
public void setAD_Tree_Menu_ID (int AD_Tree_Menu_ID)
{
if (AD_Tree_Menu_ID <= 0) set_ValueNoCheck ("AD_Tree_Menu_ID", null);
 else 
set_ValueNoCheck ("AD_Tree_Menu_ID", new Integer(AD_Tree_Menu_ID));
}
/** Get Menu Tree.
Tree of the menu */
public int getAD_Tree_Menu_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_Menu_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_TREE_ORG_ID_AD_Reference_ID = MReference.getReferenceID("AD_Tree");
/** Set Organization Tree.
Tree to determine organizational hierarchy */
public void setAD_Tree_Org_ID (int AD_Tree_Org_ID)
{
if (AD_Tree_Org_ID <= 0) set_ValueNoCheck ("AD_Tree_Org_ID", null);
 else 
set_ValueNoCheck ("AD_Tree_Org_ID", new Integer(AD_Tree_Org_ID));
}
/** Get Organization Tree.
Tree to determine organizational hierarchy */
public int getAD_Tree_Org_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_Org_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_TREE_PRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("AD_Tree");
/** Set Product Tree.
Tree to determine product hierarchy */
public void setAD_Tree_Product_ID (int AD_Tree_Product_ID)
{
if (AD_Tree_Product_ID <= 0) set_ValueNoCheck ("AD_Tree_Product_ID", null);
 else 
set_ValueNoCheck ("AD_Tree_Product_ID", new Integer(AD_Tree_Product_ID));
}
/** Get Product Tree.
Tree to determine product hierarchy */
public int getAD_Tree_Product_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_Product_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_TREE_PROJECT_ID_AD_Reference_ID = MReference.getReferenceID("AD_Tree");
/** Set Project Tree.
Tree to determine project hierarchy */
public void setAD_Tree_Project_ID (int AD_Tree_Project_ID)
{
if (AD_Tree_Project_ID <= 0) set_ValueNoCheck ("AD_Tree_Project_ID", null);
 else 
set_ValueNoCheck ("AD_Tree_Project_ID", new Integer(AD_Tree_Project_ID));
}
/** Get Project Tree.
Tree to determine project hierarchy */
public int getAD_Tree_Project_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_Project_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int AD_TREE_SALESREGION_ID_AD_Reference_ID = MReference.getReferenceID("AD_Tree");
/** Set Sales Region Tree.
Tree to determine sales regional hierarchy */
public void setAD_Tree_SalesRegion_ID (int AD_Tree_SalesRegion_ID)
{
if (AD_Tree_SalesRegion_ID <= 0) set_ValueNoCheck ("AD_Tree_SalesRegion_ID", null);
 else 
set_ValueNoCheck ("AD_Tree_SalesRegion_ID", new Integer(AD_Tree_SalesRegion_ID));
}
/** Get Sales Region Tree.
Tree to determine sales regional hierarchy */
public int getAD_Tree_SalesRegion_ID() 
{
Integer ii = (Integer)get_Value("AD_Tree_SalesRegion_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_ACCTSCHEMA1_ID_AD_Reference_ID = MReference.getReferenceID("C_AcctSchema");
/** Set Primary Accounting Schema.
Primary rules for accounting */
public void setC_AcctSchema1_ID (int C_AcctSchema1_ID)
{
if (C_AcctSchema1_ID <= 0) set_ValueNoCheck ("C_AcctSchema1_ID", null);
 else 
set_ValueNoCheck ("C_AcctSchema1_ID", new Integer(C_AcctSchema1_ID));
}
/** Get Primary Accounting Schema.
Primary rules for accounting */
public int getC_AcctSchema1_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema1_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_ACCTSCHEMA2_ID_AD_Reference_ID = MReference.getReferenceID("C_AcctSchema");
/** Set Second Accounting Schema.
Secondary rules for accounting */
public void setC_AcctSchema2_ID (int C_AcctSchema2_ID)
{
if (C_AcctSchema2_ID <= 0) set_Value ("C_AcctSchema2_ID", null);
 else 
set_Value ("C_AcctSchema2_ID", new Integer(C_AcctSchema2_ID));
}
/** Get Second Accounting Schema.
Secondary rules for accounting */
public int getC_AcctSchema2_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema2_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_ACCTSCHEMA3_ID_AD_Reference_ID = MReference.getReferenceID("C_AcctSchema");
/** Set Third Accounting Schema.
Terceary rules for accounting */
public void setC_AcctSchema3_ID (int C_AcctSchema3_ID)
{
if (C_AcctSchema3_ID <= 0) set_Value ("C_AcctSchema3_ID", null);
 else 
set_Value ("C_AcctSchema3_ID", new Integer(C_AcctSchema3_ID));
}
/** Get Third Accounting Schema.
Terceary rules for accounting */
public int getC_AcctSchema3_ID() 
{
Integer ii = (Integer)get_Value("C_AcctSchema3_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_BPARTNERCASHTRX_ID_AD_Reference_ID = MReference.getReferenceID("C_BPartner (No Summary)");
/** Set Template B.Partner.
Business Partner used for creating new Business Partners on the fly */
public void setC_BPartnerCashTrx_ID (int C_BPartnerCashTrx_ID)
{
if (C_BPartnerCashTrx_ID <= 0) set_Value ("C_BPartnerCashTrx_ID", null);
 else 
set_Value ("C_BPartnerCashTrx_ID", new Integer(C_BPartnerCashTrx_ID));
}
/** Get Template B.Partner.
Business Partner used for creating new Business Partners on the fly */
public int getC_BPartnerCashTrx_ID() 
{
Integer ii = (Integer)get_Value("C_BPartnerCashTrx_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Calendar.
Accounting Calendar Name */
public void setC_Calendar_ID (int C_Calendar_ID)
{
if (C_Calendar_ID <= 0) set_Value ("C_Calendar_ID", null);
 else 
set_Value ("C_Calendar_ID", new Integer(C_Calendar_ID));
}
/** Get Calendar.
Accounting Calendar Name */
public int getC_Calendar_ID() 
{
Integer ii = (Integer)get_Value("C_Calendar_ID");
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
public static final int C_INCOMINGTRANSFER_DT_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Incoming Transfer Document Type */
public void setC_IncomingTransfer_DT_ID (int C_IncomingTransfer_DT_ID)
{
if (C_IncomingTransfer_DT_ID <= 0) set_Value ("C_IncomingTransfer_DT_ID", null);
 else 
set_Value ("C_IncomingTransfer_DT_ID", new Integer(C_IncomingTransfer_DT_ID));
}
/** Get Incoming Transfer Document Type */
public int getC_IncomingTransfer_DT_ID() 
{
Integer ii = (Integer)get_Value("C_IncomingTransfer_DT_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set C_Location_ID */
public void setC_Location_ID (int C_Location_ID)
{
if (C_Location_ID <= 0) set_Value ("C_Location_ID", null);
 else 
set_Value ("C_Location_ID", new Integer(C_Location_ID));
}
/** Get C_Location_ID */
public int getC_Location_ID() 
{
Integer ii = (Integer)get_Value("C_Location_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_OUTGOINGTRANSFER_DT_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Outgoing Transfer Document Type */
public void setC_OutgoingTransfer_DT_ID (int C_OutgoingTransfer_DT_ID)
{
if (C_OutgoingTransfer_DT_ID <= 0) set_Value ("C_OutgoingTransfer_DT_ID", null);
 else 
set_Value ("C_OutgoingTransfer_DT_ID", new Integer(C_OutgoingTransfer_DT_ID));
}
/** Get Outgoing Transfer Document Type */
public int getC_OutgoingTransfer_DT_ID() 
{
Integer ii = (Integer)get_Value("C_OutgoingTransfer_DT_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CUIT */
public void setCUIT (String CUIT)
{
if (CUIT != null && CUIT.length() > 128)
{
log.warning("Length > 128 - truncated");
CUIT = CUIT.substring(0,128);
}
set_Value ("CUIT", CUIT);
}
/** Get CUIT */
public String getCUIT() 
{
return (String)get_Value("CUIT");
}
public static final int C_UOM_LENGTH_ID_AD_Reference_ID = MReference.getReferenceID("C_UOM");
/** Set UOM for Length.
Standard Unit of Measure for Length */
public void setC_UOM_Length_ID (int C_UOM_Length_ID)
{
if (C_UOM_Length_ID <= 0) set_Value ("C_UOM_Length_ID", null);
 else 
set_Value ("C_UOM_Length_ID", new Integer(C_UOM_Length_ID));
}
/** Get UOM for Length.
Standard Unit of Measure for Length */
public int getC_UOM_Length_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_Length_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_UOM_TIME_ID_AD_Reference_ID = MReference.getReferenceID("C_UOM");
/** Set UOM for Time.
Standard Unit of Measure for Time */
public void setC_UOM_Time_ID (int C_UOM_Time_ID)
{
if (C_UOM_Time_ID <= 0) set_Value ("C_UOM_Time_ID", null);
 else 
set_Value ("C_UOM_Time_ID", new Integer(C_UOM_Time_ID));
}
/** Get UOM for Time.
Standard Unit of Measure for Time */
public int getC_UOM_Time_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_Time_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_UOM_VOLUME_ID_AD_Reference_ID = MReference.getReferenceID("C_UOM");
/** Set UOM for Volume.
Standard Unit of Measure for Volume */
public void setC_UOM_Volume_ID (int C_UOM_Volume_ID)
{
if (C_UOM_Volume_ID <= 0) set_Value ("C_UOM_Volume_ID", null);
 else 
set_Value ("C_UOM_Volume_ID", new Integer(C_UOM_Volume_ID));
}
/** Get UOM for Volume.
Standard Unit of Measure for Volume */
public int getC_UOM_Volume_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_Volume_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_UOM_WEIGHT_ID_AD_Reference_ID = MReference.getReferenceID("C_UOM");
/** Set UOM for Weight.
Standard Unit of Measure for Weight */
public void setC_UOM_Weight_ID (int C_UOM_Weight_ID)
{
if (C_UOM_Weight_ID <= 0) set_Value ("C_UOM_Weight_ID", null);
 else 
set_Value ("C_UOM_Weight_ID", new Integer(C_UOM_Weight_ID));
}
/** Get UOM for Weight.
Standard Unit of Measure for Weight */
public int getC_UOM_Weight_ID() 
{
Integer ii = (Integer)get_Value("C_UOM_Weight_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set EMail.
Electronic Mail Address */
public void setEMail (String EMail)
{
if (EMail != null && EMail.length() > 255)
{
log.warning("Length > 255 - truncated");
EMail = EMail.substring(0,255);
}
set_Value ("EMail", EMail);
}
/** Get EMail.
Electronic Mail Address */
public String getEMail() 
{
return (String)get_Value("EMail");
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
/** Set Discount calculated from Line Amounts.
Payment Discount calculation does not include Taxes and Charges */
public void setIsDiscountLineAmt (boolean IsDiscountLineAmt)
{
set_Value ("IsDiscountLineAmt", new Boolean(IsDiscountLineAmt));
}
/** Get Discount calculated from Line Amounts.
Payment Discount calculation does not include Taxes and Charges */
public boolean isDiscountLineAmt() 
{
Object oo = get_Value("IsDiscountLineAmt");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set POS Journal Active.
POS Journal Active */
public void setIsPOSJournalActive (boolean IsPOSJournalActive)
{
set_Value ("IsPOSJournalActive", new Boolean(IsPOSJournalActive));
}
/** Get POS Journal Active.
POS Journal Active */
public boolean isPOSJournalActive() 
{
Object oo = get_Value("IsPOSJournalActive");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Warehouse Close Control */
public void setIsWarehouseCloseControl (boolean IsWarehouseCloseControl)
{
set_Value ("IsWarehouseCloseControl", new Boolean(IsWarehouseCloseControl));
}
/** Get Warehouse Close Control */
public boolean isWarehouseCloseControl() 
{
Object oo = get_Value("IsWarehouseCloseControl");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Days to keep Log.
Number of days to keep the log entries */
public void setKeepLogDays (int KeepLogDays)
{
set_Value ("KeepLogDays", new Integer(KeepLogDays));
}
/** Get Days to keep Log.
Number of days to keep the log entries */
public int getKeepLogDays() 
{
Integer ii = (Integer)get_Value("KeepLogDays");
if (ii == null) return 0;
return ii.intValue();
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
public static final int M_PRODUCTFREIGHT_ID_AD_Reference_ID = MReference.getReferenceID("M_Product (no summary)");
/** Set Product for Freight */
public void setM_ProductFreight_ID (int M_ProductFreight_ID)
{
if (M_ProductFreight_ID <= 0) set_Value ("M_ProductFreight_ID", null);
 else 
set_Value ("M_ProductFreight_ID", new Integer(M_ProductFreight_ID));
}
/** Get Product for Freight */
public int getM_ProductFreight_ID() 
{
Integer ii = (Integer)get_Value("M_ProductFreight_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Goal.
Performance Goal */
public void setPA_Goal_ID (int PA_Goal_ID)
{
if (PA_Goal_ID <= 0) set_Value ("PA_Goal_ID", null);
 else 
set_Value ("PA_Goal_ID", new Integer(PA_Goal_ID));
}
/** Get Goal.
Performance Goal */
public int getPA_Goal_ID() 
{
Integer ii = (Integer)get_Value("PA_Goal_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Password Expiration Active */
public void setPasswordExpirationActive (boolean PasswordExpirationActive)
{
set_Value ("PasswordExpirationActive", new Boolean(PasswordExpirationActive));
}
/** Get Password Expiration Active */
public boolean isPasswordExpirationActive() 
{
Object oo = get_Value("PasswordExpirationActive");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Password Expiration Days */
public void setPasswordExpirationDays (int PasswordExpirationDays)
{
set_Value ("PasswordExpirationDays", new Integer(PasswordExpirationDays));
}
/** Get Password Expiration Days */
public int getPasswordExpirationDays() 
{
Integer ii = (Integer)get_Value("PasswordExpirationDays");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payments POS Journal Open */
public void setPaymentsPOSJournalOpen (boolean PaymentsPOSJournalOpen)
{
set_Value ("PaymentsPOSJournalOpen", new Boolean(PaymentsPOSJournalOpen));
}
/** Get Payments POS Journal Open */
public boolean isPaymentsPOSJournalOpen() 
{
Object oo = get_Value("PaymentsPOSJournalOpen");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Unique Key Active.
Unique Key Active */
public void setUniqueKeyActive (boolean UniqueKeyActive)
{
set_Value ("UniqueKeyActive", new Boolean(UniqueKeyActive));
}
/** Get Unique Key Active.
Unique Key Active */
public boolean isUniqueKeyActive() 
{
Object oo = get_Value("UniqueKeyActive");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int VOIDINGINVOICEPAYMENTSPOSJOURNALCONFIG_AD_Reference_ID = MReference.getReferenceID("Invoice Global Voiding POS Journal Options");
/** Select = S */
public static final String VOIDINGINVOICEPAYMENTSPOSJOURNALCONFIG_Select = "S";
/** Original Payment = P */
public static final String VOIDINGINVOICEPAYMENTSPOSJOURNALCONFIG_OriginalPayment = "P";
/** Original Document = D */
public static final String VOIDINGINVOICEPAYMENTSPOSJOURNALCONFIG_OriginalDocument = "D";
/** User = U */
public static final String VOIDINGINVOICEPAYMENTSPOSJOURNALCONFIG_User = "U";
/** Set Voiding Invoice Payments POS Journal Config */
public void setVoidingInvoicePaymentsPOSJournalConfig (String VoidingInvoicePaymentsPOSJournalConfig)
{
if (VoidingInvoicePaymentsPOSJournalConfig == null || VoidingInvoicePaymentsPOSJournalConfig.equals("S") || VoidingInvoicePaymentsPOSJournalConfig.equals("P") || VoidingInvoicePaymentsPOSJournalConfig.equals("D") || VoidingInvoicePaymentsPOSJournalConfig.equals("U"));
 else throw new IllegalArgumentException ("VoidingInvoicePaymentsPOSJournalConfig Invalid value - Reference = VOIDINGINVOICEPAYMENTSPOSJOURNALCONFIG_AD_Reference_ID - S - P - D - U");
if (VoidingInvoicePaymentsPOSJournalConfig != null && VoidingInvoicePaymentsPOSJournalConfig.length() > 1)
{
log.warning("Length > 1 - truncated");
VoidingInvoicePaymentsPOSJournalConfig = VoidingInvoicePaymentsPOSJournalConfig.substring(0,1);
}
set_Value ("VoidingInvoicePaymentsPOSJournalConfig", VoidingInvoicePaymentsPOSJournalConfig);
}
/** Get Voiding Invoice Payments POS Journal Config */
public String getVoidingInvoicePaymentsPOSJournalConfig() 
{
return (String)get_Value("VoidingInvoicePaymentsPOSJournalConfig");
}
public static final int VOIDINGINVOICEPOSJOURNALCONFIG_AD_Reference_ID = MReference.getReferenceID("Invoice Global Voiding POS Journal Options");
/** Select = S */
public static final String VOIDINGINVOICEPOSJOURNALCONFIG_Select = "S";
/** Original Payment = P */
public static final String VOIDINGINVOICEPOSJOURNALCONFIG_OriginalPayment = "P";
/** Original Document = D */
public static final String VOIDINGINVOICEPOSJOURNALCONFIG_OriginalDocument = "D";
/** User = U */
public static final String VOIDINGINVOICEPOSJOURNALCONFIG_User = "U";
/** Set Voiding Invoice POS Journal Config */
public void setVoidingInvoicePOSJournalConfig (String VoidingInvoicePOSJournalConfig)
{
if (VoidingInvoicePOSJournalConfig == null || VoidingInvoicePOSJournalConfig.equals("S") || VoidingInvoicePOSJournalConfig.equals("P") || VoidingInvoicePOSJournalConfig.equals("D") || VoidingInvoicePOSJournalConfig.equals("U"));
 else throw new IllegalArgumentException ("VoidingInvoicePOSJournalConfig Invalid value - Reference = VOIDINGINVOICEPOSJOURNALCONFIG_AD_Reference_ID - S - P - D - U");
if (VoidingInvoicePOSJournalConfig != null && VoidingInvoicePOSJournalConfig.length() > 1)
{
log.warning("Length > 1 - truncated");
VoidingInvoicePOSJournalConfig = VoidingInvoicePOSJournalConfig.substring(0,1);
}
set_Value ("VoidingInvoicePOSJournalConfig", VoidingInvoicePOSJournalConfig);
}
/** Get Voiding Invoice POS Journal Config */
public String getVoidingInvoicePOSJournalConfig() 
{
return (String)get_Value("VoidingInvoicePOSJournalConfig");
}
/** Set Web Site.
Client Web Site */
public void setWeb (String Web)
{
if (Web != null && Web.length() > 255)
{
log.warning("Length > 255 - truncated");
Web = Web.substring(0,255);
}
set_Value ("Web", Web);
}
/** Get Web Site.
Client Web Site */
public String getWeb() 
{
return (String)get_Value("Web");
}
}
