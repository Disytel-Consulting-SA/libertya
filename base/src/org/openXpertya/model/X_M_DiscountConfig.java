/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_DiscountConfig
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-06-10 15:35:41.983 */
public class X_M_DiscountConfig extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_DiscountConfig (Properties ctx, int M_DiscountConfig_ID, String trxName)
{
super (ctx, M_DiscountConfig_ID, trxName);
/** if (M_DiscountConfig_ID == 0)
{
setBPartner_DiscountProduct_ID (0);
setBPartner_SurchargeProduct_ID (0);
setCharge_DiscountProduct_ID (0);
setCharge_SurchargeProduct_ID (0);
setCredit_DocType_ID (0);
setCreditDocumentType (null);	// CCM
setDebit_DocType_ID (0);
setDebitDocumentType (null);	// CI
setIsApplyAllDocumentDiscount (false);	// N
setM_DiscountConfig_ID (0);
setPaymentMedium_DiscountProduct_ID (0);
setPaymentMedium_SurchargeProduct_ID (0);
setPaymentTerm_DiscountProduct_ID (0);
setPaymentTerm_SurchargeProduct_ID (0);
}
 */
}
/** Load Constructor */
public X_M_DiscountConfig (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_DiscountConfig");

/** TableName=M_DiscountConfig */
public static final String Table_Name="M_DiscountConfig";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_DiscountConfig");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_DiscountConfig[").append(getID()).append("]");
return sb.toString();
}
public static final int BPARTNER_DISCOUNTPRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set BPartner Discount Product */
public void setBPartner_DiscountProduct_ID (int BPartner_DiscountProduct_ID)
{
set_Value ("BPartner_DiscountProduct_ID", new Integer(BPartner_DiscountProduct_ID));
}
/** Get BPartner Discount Product */
public int getBPartner_DiscountProduct_ID() 
{
Integer ii = (Integer)get_Value("BPartner_DiscountProduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int BPARTNER_SURCHARGEPRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set BPartner Surcharge Product */
public void setBPartner_SurchargeProduct_ID (int BPartner_SurchargeProduct_ID)
{
set_Value ("BPartner_SurchargeProduct_ID", new Integer(BPartner_SurchargeProduct_ID));
}
/** Get BPartner Surcharge Product */
public int getBPartner_SurchargeProduct_ID() 
{
Integer ii = (Integer)get_Value("BPartner_SurchargeProduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CHARGE_DISCOUNTPRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set Charge Discount Product */
public void setCharge_DiscountProduct_ID (int Charge_DiscountProduct_ID)
{
set_Value ("Charge_DiscountProduct_ID", new Integer(Charge_DiscountProduct_ID));
}
/** Get Charge Discount Product */
public int getCharge_DiscountProduct_ID() 
{
Integer ii = (Integer)get_Value("Charge_DiscountProduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CHARGE_SURCHARGEPRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set Charge Surcharge Product */
public void setCharge_SurchargeProduct_ID (int Charge_SurchargeProduct_ID)
{
set_Value ("Charge_SurchargeProduct_ID", new Integer(Charge_SurchargeProduct_ID));
}
/** Get Charge Surcharge Product */
public int getCharge_SurchargeProduct_ID() 
{
Integer ii = (Integer)get_Value("Charge_SurchargeProduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CREDIT_DOCTYPE_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Credit Document Type */
public void setCredit_DocType_ID (int Credit_DocType_ID)
{
set_Value ("Credit_DocType_ID", new Integer(Credit_DocType_ID));
}
/** Get Credit Document Type */
public int getCredit_DocType_ID() 
{
Integer ii = (Integer)get_Value("Credit_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CREDITDOCUMENTTYPE_AD_Reference_ID = MReference.getReferenceID("Discount generic document types");
/** Credit Note = CCM */
public static final String CREDITDOCUMENTTYPE_CreditNote = "CCM";
/** Other = O */
public static final String CREDITDOCUMENTTYPE_Other = "O";
/** Set Credit Document Type.
Credit Document Type for Discounts in Customer Receipts */
public void setCreditDocumentType (String CreditDocumentType)
{
if (CreditDocumentType.equals("CCM") || CreditDocumentType.equals("O"));
 else throw new IllegalArgumentException ("CreditDocumentType Invalid value - Reference = CREDITDOCUMENTTYPE_AD_Reference_ID - CCM - O");
if (CreditDocumentType == null) throw new IllegalArgumentException ("CreditDocumentType is mandatory");
if (CreditDocumentType.length() > 4)
{
log.warning("Length > 4 - truncated");
CreditDocumentType = CreditDocumentType.substring(0,4);
}
set_Value ("CreditDocumentType", CreditDocumentType);
}
/** Get Credit Document Type.
Credit Document Type for Discounts in Customer Receipts */
public String getCreditDocumentType() 
{
return (String)get_Value("CreditDocumentType");
}
public static final int DEBIT_DOCTYPE_ID_AD_Reference_ID = MReference.getReferenceID("C_DocType");
/** Set Debit Document Type */
public void setDebit_DocType_ID (int Debit_DocType_ID)
{
set_Value ("Debit_DocType_ID", new Integer(Debit_DocType_ID));
}
/** Get Debit Document Type */
public int getDebit_DocType_ID() 
{
Integer ii = (Integer)get_Value("Debit_DocType_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int DEBITDOCUMENTTYPE_AD_Reference_ID = MReference.getReferenceID("Surcharge generic document types");
/** Debit Note = CCM */
public static final String DEBITDOCUMENTTYPE_DebitNote = "CCM";
/** Invoice = CI */
public static final String DEBITDOCUMENTTYPE_Invoice = "CI";
/** Other = O */
public static final String DEBITDOCUMENTTYPE_Other = "O";
/** Set Debit Document Type.
Debit Document Type for surcharges in Customer Receipts */
public void setDebitDocumentType (String DebitDocumentType)
{
if (DebitDocumentType.equals("CCM") || DebitDocumentType.equals("CI") || DebitDocumentType.equals("O"));
 else throw new IllegalArgumentException ("DebitDocumentType Invalid value - Reference = DEBITDOCUMENTTYPE_AD_Reference_ID - CCM - CI - O");
if (DebitDocumentType == null) throw new IllegalArgumentException ("DebitDocumentType is mandatory");
if (DebitDocumentType.length() > 4)
{
log.warning("Length > 4 - truncated");
DebitDocumentType = DebitDocumentType.substring(0,4);
}
set_Value ("DebitDocumentType", DebitDocumentType);
}
/** Get Debit Document Type.
Debit Document Type for surcharges in Customer Receipts */
public String getDebitDocumentType() 
{
return (String)get_Value("DebitDocumentType");
}
public static final int DOCUMENTDISCOUNT1_AD_Reference_ID = MReference.getReferenceID("Discount Kind");
/** General Discount Schema = G */
public static final String DOCUMENTDISCOUNT1_GeneralDiscountSchema = "G";
/** BPartner Discount Schema = B */
public static final String DOCUMENTDISCOUNT1_BPartnerDiscountSchema = "B";
/** Products Combo = C */
public static final String DOCUMENTDISCOUNT1_ProductsCombo = "C";
/** Promotion = P */
public static final String DOCUMENTDISCOUNT1_Promotion = "P";
/** Payment Medium = M */
public static final String DOCUMENTDISCOUNT1_PaymentMedium = "M";
/** Manual General Discount = Z */
public static final String DOCUMENTDISCOUNT1_ManualGeneralDiscount = "Z";
/** Manual Discount = D */
public static final String DOCUMENTDISCOUNT1_ManualDiscount = "D";
/** Discount Line = L */
public static final String DOCUMENTDISCOUNT1_DiscountLine = "L";
/** Document Discount = X */
public static final String DOCUMENTDISCOUNT1_DocumentDiscount = "X";
/** Set Document Discount Priority 1.
Document Discount Priority 1 */
public void setDocumentDiscount1 (String DocumentDiscount1)
{
if (DocumentDiscount1 == null || DocumentDiscount1.equals("G") || DocumentDiscount1.equals("B") || DocumentDiscount1.equals("C") || DocumentDiscount1.equals("P") || DocumentDiscount1.equals("M") || DocumentDiscount1.equals("Z") || DocumentDiscount1.equals("D") || DocumentDiscount1.equals("L") || DocumentDiscount1.equals("X"));
 else throw new IllegalArgumentException ("DocumentDiscount1 Invalid value - Reference = DOCUMENTDISCOUNT1_AD_Reference_ID - G - B - C - P - M - Z - D - L - X");
if (DocumentDiscount1 != null && DocumentDiscount1.length() > 1)
{
log.warning("Length > 1 - truncated");
DocumentDiscount1 = DocumentDiscount1.substring(0,1);
}
set_Value ("DocumentDiscount1", DocumentDiscount1);
}
/** Get Document Discount Priority 1.
Document Discount Priority 1 */
public String getDocumentDiscount1() 
{
return (String)get_Value("DocumentDiscount1");
}
public static final int DOCUMENTDISCOUNT2_AD_Reference_ID = MReference.getReferenceID("Discount Kind");
/** General Discount Schema = G */
public static final String DOCUMENTDISCOUNT2_GeneralDiscountSchema = "G";
/** BPartner Discount Schema = B */
public static final String DOCUMENTDISCOUNT2_BPartnerDiscountSchema = "B";
/** Products Combo = C */
public static final String DOCUMENTDISCOUNT2_ProductsCombo = "C";
/** Promotion = P */
public static final String DOCUMENTDISCOUNT2_Promotion = "P";
/** Payment Medium = M */
public static final String DOCUMENTDISCOUNT2_PaymentMedium = "M";
/** Manual General Discount = Z */
public static final String DOCUMENTDISCOUNT2_ManualGeneralDiscount = "Z";
/** Manual Discount = D */
public static final String DOCUMENTDISCOUNT2_ManualDiscount = "D";
/** Discount Line = L */
public static final String DOCUMENTDISCOUNT2_DiscountLine = "L";
/** Document Discount = X */
public static final String DOCUMENTDISCOUNT2_DocumentDiscount = "X";
/** Set Document Discount Priority 2.
Document Discount Priority 2 */
public void setDocumentDiscount2 (String DocumentDiscount2)
{
if (DocumentDiscount2 == null || DocumentDiscount2.equals("G") || DocumentDiscount2.equals("B") || DocumentDiscount2.equals("C") || DocumentDiscount2.equals("P") || DocumentDiscount2.equals("M") || DocumentDiscount2.equals("Z") || DocumentDiscount2.equals("D") || DocumentDiscount2.equals("L") || DocumentDiscount2.equals("X"));
 else throw new IllegalArgumentException ("DocumentDiscount2 Invalid value - Reference = DOCUMENTDISCOUNT2_AD_Reference_ID - G - B - C - P - M - Z - D - L - X");
if (DocumentDiscount2 != null && DocumentDiscount2.length() > 1)
{
log.warning("Length > 1 - truncated");
DocumentDiscount2 = DocumentDiscount2.substring(0,1);
}
set_Value ("DocumentDiscount2", DocumentDiscount2);
}
/** Get Document Discount Priority 2.
Document Discount Priority 2 */
public String getDocumentDiscount2() 
{
return (String)get_Value("DocumentDiscount2");
}
public static final int DOCUMENTDISCOUNTCHARGE_ID_AD_Reference_ID = MReference.getReferenceID("C_Charge");
/** Set Document Discount Charge.
Document Discount Charge */
public void setDocumentDiscountCharge_ID (int DocumentDiscountCharge_ID)
{
if (DocumentDiscountCharge_ID <= 0) set_Value ("DocumentDiscountCharge_ID", null);
 else 
set_Value ("DocumentDiscountCharge_ID", new Integer(DocumentDiscountCharge_ID));
}
/** Get Document Discount Charge.
Document Discount Charge */
public int getDocumentDiscountCharge_ID() 
{
Integer ii = (Integer)get_Value("DocumentDiscountCharge_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Apply All Document Discounts.
Apply All Document Discounts */
public void setIsApplyAllDocumentDiscount (boolean IsApplyAllDocumentDiscount)
{
set_Value ("IsApplyAllDocumentDiscount", new Boolean(IsApplyAllDocumentDiscount));
}
/** Get Apply All Document Discounts.
Apply All Document Discounts */
public boolean isApplyAllDocumentDiscount() 
{
Object oo = get_Value("IsApplyAllDocumentDiscount");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int LINEDISCOUNT1_AD_Reference_ID = MReference.getReferenceID("Discount Kind");
/** General Discount Schema = G */
public static final String LINEDISCOUNT1_GeneralDiscountSchema = "G";
/** BPartner Discount Schema = B */
public static final String LINEDISCOUNT1_BPartnerDiscountSchema = "B";
/** Products Combo = C */
public static final String LINEDISCOUNT1_ProductsCombo = "C";
/** Promotion = P */
public static final String LINEDISCOUNT1_Promotion = "P";
/** Payment Medium = M */
public static final String LINEDISCOUNT1_PaymentMedium = "M";
/** Manual General Discount = Z */
public static final String LINEDISCOUNT1_ManualGeneralDiscount = "Z";
/** Manual Discount = D */
public static final String LINEDISCOUNT1_ManualDiscount = "D";
/** Discount Line = L */
public static final String LINEDISCOUNT1_DiscountLine = "L";
/** Document Discount = X */
public static final String LINEDISCOUNT1_DocumentDiscount = "X";
/** Set Line Discount Priority 1.
Line Discount Priority 1 */
public void setLineDiscount1 (String LineDiscount1)
{
if (LineDiscount1 == null || LineDiscount1.equals("G") || LineDiscount1.equals("B") || LineDiscount1.equals("C") || LineDiscount1.equals("P") || LineDiscount1.equals("M") || LineDiscount1.equals("Z") || LineDiscount1.equals("D") || LineDiscount1.equals("L") || LineDiscount1.equals("X"));
 else throw new IllegalArgumentException ("LineDiscount1 Invalid value - Reference = LINEDISCOUNT1_AD_Reference_ID - G - B - C - P - M - Z - D - L - X");
if (LineDiscount1 != null && LineDiscount1.length() > 1)
{
log.warning("Length > 1 - truncated");
LineDiscount1 = LineDiscount1.substring(0,1);
}
set_Value ("LineDiscount1", LineDiscount1);
}
/** Get Line Discount Priority 1.
Line Discount Priority 1 */
public String getLineDiscount1() 
{
return (String)get_Value("LineDiscount1");
}
public static final int LINEDISCOUNT2_AD_Reference_ID = MReference.getReferenceID("Discount Kind");
/** General Discount Schema = G */
public static final String LINEDISCOUNT2_GeneralDiscountSchema = "G";
/** BPartner Discount Schema = B */
public static final String LINEDISCOUNT2_BPartnerDiscountSchema = "B";
/** Products Combo = C */
public static final String LINEDISCOUNT2_ProductsCombo = "C";
/** Promotion = P */
public static final String LINEDISCOUNT2_Promotion = "P";
/** Payment Medium = M */
public static final String LINEDISCOUNT2_PaymentMedium = "M";
/** Manual General Discount = Z */
public static final String LINEDISCOUNT2_ManualGeneralDiscount = "Z";
/** Manual Discount = D */
public static final String LINEDISCOUNT2_ManualDiscount = "D";
/** Discount Line = L */
public static final String LINEDISCOUNT2_DiscountLine = "L";
/** Document Discount = X */
public static final String LINEDISCOUNT2_DocumentDiscount = "X";
/** Set Line Discount Priority 2.
Line Discount Priority 2 */
public void setLineDiscount2 (String LineDiscount2)
{
if (LineDiscount2 == null || LineDiscount2.equals("G") || LineDiscount2.equals("B") || LineDiscount2.equals("C") || LineDiscount2.equals("P") || LineDiscount2.equals("M") || LineDiscount2.equals("Z") || LineDiscount2.equals("D") || LineDiscount2.equals("L") || LineDiscount2.equals("X"));
 else throw new IllegalArgumentException ("LineDiscount2 Invalid value - Reference = LINEDISCOUNT2_AD_Reference_ID - G - B - C - P - M - Z - D - L - X");
if (LineDiscount2 != null && LineDiscount2.length() > 1)
{
log.warning("Length > 1 - truncated");
LineDiscount2 = LineDiscount2.substring(0,1);
}
set_Value ("LineDiscount2", LineDiscount2);
}
/** Get Line Discount Priority 2.
Line Discount Priority 2 */
public String getLineDiscount2() 
{
return (String)get_Value("LineDiscount2");
}
public static final int LINEDISCOUNT3_AD_Reference_ID = MReference.getReferenceID("Discount Kind");
/** General Discount Schema = G */
public static final String LINEDISCOUNT3_GeneralDiscountSchema = "G";
/** BPartner Discount Schema = B */
public static final String LINEDISCOUNT3_BPartnerDiscountSchema = "B";
/** Products Combo = C */
public static final String LINEDISCOUNT3_ProductsCombo = "C";
/** Promotion = P */
public static final String LINEDISCOUNT3_Promotion = "P";
/** Payment Medium = M */
public static final String LINEDISCOUNT3_PaymentMedium = "M";
/** Manual General Discount = Z */
public static final String LINEDISCOUNT3_ManualGeneralDiscount = "Z";
/** Manual Discount = D */
public static final String LINEDISCOUNT3_ManualDiscount = "D";
/** Discount Line = L */
public static final String LINEDISCOUNT3_DiscountLine = "L";
/** Document Discount = X */
public static final String LINEDISCOUNT3_DocumentDiscount = "X";
/** Set Line Discount Priority 3.
Line Discount Priority 3 */
public void setLineDiscount3 (String LineDiscount3)
{
if (LineDiscount3 == null || LineDiscount3.equals("G") || LineDiscount3.equals("B") || LineDiscount3.equals("C") || LineDiscount3.equals("P") || LineDiscount3.equals("M") || LineDiscount3.equals("Z") || LineDiscount3.equals("D") || LineDiscount3.equals("L") || LineDiscount3.equals("X"));
 else throw new IllegalArgumentException ("LineDiscount3 Invalid value - Reference = LINEDISCOUNT3_AD_Reference_ID - G - B - C - P - M - Z - D - L - X");
if (LineDiscount3 != null && LineDiscount3.length() > 1)
{
log.warning("Length > 1 - truncated");
LineDiscount3 = LineDiscount3.substring(0,1);
}
set_Value ("LineDiscount3", LineDiscount3);
}
/** Get Line Discount Priority 3.
Line Discount Priority 3 */
public String getLineDiscount3() 
{
return (String)get_Value("LineDiscount3");
}
public static final int LINEDISCOUNT4_AD_Reference_ID = MReference.getReferenceID("Discount Kind");
/** General Discount Schema = G */
public static final String LINEDISCOUNT4_GeneralDiscountSchema = "G";
/** BPartner Discount Schema = B */
public static final String LINEDISCOUNT4_BPartnerDiscountSchema = "B";
/** Products Combo = C */
public static final String LINEDISCOUNT4_ProductsCombo = "C";
/** Promotion = P */
public static final String LINEDISCOUNT4_Promotion = "P";
/** Payment Medium = M */
public static final String LINEDISCOUNT4_PaymentMedium = "M";
/** Manual General Discount = Z */
public static final String LINEDISCOUNT4_ManualGeneralDiscount = "Z";
/** Manual Discount = D */
public static final String LINEDISCOUNT4_ManualDiscount = "D";
/** Discount Line = L */
public static final String LINEDISCOUNT4_DiscountLine = "L";
/** Document Discount = X */
public static final String LINEDISCOUNT4_DocumentDiscount = "X";
/** Set Line Discount Priority 4.
Line Discount Priority 4 */
public void setLineDiscount4 (String LineDiscount4)
{
if (LineDiscount4 == null || LineDiscount4.equals("G") || LineDiscount4.equals("B") || LineDiscount4.equals("C") || LineDiscount4.equals("P") || LineDiscount4.equals("M") || LineDiscount4.equals("Z") || LineDiscount4.equals("D") || LineDiscount4.equals("L") || LineDiscount4.equals("X"));
 else throw new IllegalArgumentException ("LineDiscount4 Invalid value - Reference = LINEDISCOUNT4_AD_Reference_ID - G - B - C - P - M - Z - D - L - X");
if (LineDiscount4 != null && LineDiscount4.length() > 1)
{
log.warning("Length > 1 - truncated");
LineDiscount4 = LineDiscount4.substring(0,1);
}
set_Value ("LineDiscount4", LineDiscount4);
}
/** Get Line Discount Priority 4.
Line Discount Priority 4 */
public String getLineDiscount4() 
{
return (String)get_Value("LineDiscount4");
}
/** Set Discount Configuration.
Discount Configuration */
public void setM_DiscountConfig_ID (int M_DiscountConfig_ID)
{
set_ValueNoCheck ("M_DiscountConfig_ID", new Integer(M_DiscountConfig_ID));
}
/** Get Discount Configuration.
Discount Configuration */
public int getM_DiscountConfig_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountConfig_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PAYMENTMEDIUM_DISCOUNTPRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set Payment Medium Discount Product */
public void setPaymentMedium_DiscountProduct_ID (int PaymentMedium_DiscountProduct_ID)
{
set_Value ("PaymentMedium_DiscountProduct_ID", new Integer(PaymentMedium_DiscountProduct_ID));
}
/** Get Payment Medium Discount Product */
public int getPaymentMedium_DiscountProduct_ID() 
{
Integer ii = (Integer)get_Value("PaymentMedium_DiscountProduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PAYMENTMEDIUM_SURCHARGEPRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set Payment Medium Surcharge Product */
public void setPaymentMedium_SurchargeProduct_ID (int PaymentMedium_SurchargeProduct_ID)
{
set_Value ("PaymentMedium_SurchargeProduct_ID", new Integer(PaymentMedium_SurchargeProduct_ID));
}
/** Get Payment Medium Surcharge Product */
public int getPaymentMedium_SurchargeProduct_ID() 
{
Integer ii = (Integer)get_Value("PaymentMedium_SurchargeProduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PAYMENTTERM_DISCOUNTPRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set Payment Term Discount Product */
public void setPaymentTerm_DiscountProduct_ID (int PaymentTerm_DiscountProduct_ID)
{
set_Value ("PaymentTerm_DiscountProduct_ID", new Integer(PaymentTerm_DiscountProduct_ID));
}
/** Get Payment Term Discount Product */
public int getPaymentTerm_DiscountProduct_ID() 
{
Integer ii = (Integer)get_Value("PaymentTerm_DiscountProduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int PAYMENTTERM_SURCHARGEPRODUCT_ID_AD_Reference_ID = MReference.getReferenceID("C_Product");
/** Set Payment Term Surcharge Product */
public void setPaymentTerm_SurchargeProduct_ID (int PaymentTerm_SurchargeProduct_ID)
{
set_Value ("PaymentTerm_SurchargeProduct_ID", new Integer(PaymentTerm_SurchargeProduct_ID));
}
/** Get Payment Term Surcharge Product */
public int getPaymentTerm_SurchargeProduct_ID() 
{
Integer ii = (Integer)get_Value("PaymentTerm_SurchargeProduct_ID");
if (ii == null) return 0;
return ii.intValue();
}
}
