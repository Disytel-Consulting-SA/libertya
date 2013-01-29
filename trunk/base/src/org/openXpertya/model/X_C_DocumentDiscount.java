/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_DocumentDiscount
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-06-10 15:34:28.89 */
public class X_C_DocumentDiscount extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_DocumentDiscount (Properties ctx, int C_DocumentDiscount_ID, String trxName)
{
super (ctx, C_DocumentDiscount_ID, trxName);
/** if (C_DocumentDiscount_ID == 0)
{
setC_DocumentDiscount_ID (0);
setDiscountAmt (Env.ZERO);
setDiscountBaseAmt (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_C_DocumentDiscount (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_DocumentDiscount");

/** TableName=C_DocumentDiscount */
public static final String Table_Name="C_DocumentDiscount";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_DocumentDiscount");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_DocumentDiscount[").append(getID()).append("]");
return sb.toString();
}
/** Set Document Discount.
Document Discount */
public void setC_DocumentDiscount_ID (int C_DocumentDiscount_ID)
{
set_ValueNoCheck ("C_DocumentDiscount_ID", new Integer(C_DocumentDiscount_ID));
}
/** Get Document Discount.
Document Discount */
public int getC_DocumentDiscount_ID() 
{
Integer ii = (Integer)get_Value("C_DocumentDiscount_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_DOCUMENTDISCOUNT_PARENT_ID_AD_Reference_ID = MReference.getReferenceID("C_DocumentDiscount");
/** Set Parent Document Discount.
Parent Document Discount */
public void setC_DocumentDiscount_Parent_ID (int C_DocumentDiscount_Parent_ID)
{
if (C_DocumentDiscount_Parent_ID <= 0) set_Value ("C_DocumentDiscount_Parent_ID", null);
 else 
set_Value ("C_DocumentDiscount_Parent_ID", new Integer(C_DocumentDiscount_Parent_ID));
}
/** Get Parent Document Discount.
Parent Document Discount */
public int getC_DocumentDiscount_Parent_ID() 
{
Integer ii = (Integer)get_Value("C_DocumentDiscount_Parent_ID");
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
/** Set Order.
Order */
public void setC_Order_ID (int C_Order_ID)
{
if (C_Order_ID <= 0) set_Value ("C_Order_ID", null);
 else 
set_Value ("C_Order_ID", new Integer(C_Order_ID));
}
/** Get Order.
Order */
public int getC_Order_ID() 
{
Integer ii = (Integer)get_Value("C_Order_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int CUMULATIVELEVEL_AD_Reference_ID = MReference.getReferenceID("M_Discount CumulativeLevel");
/** Line = L */
public static final String CUMULATIVELEVEL_Line = "L";
/** Document = D */
public static final String CUMULATIVELEVEL_Document = "D";
/** Set Cumulative Level.
Level for cumulative calculations */
public void setCumulativeLevel (String CumulativeLevel)
{
if (CumulativeLevel == null || CumulativeLevel.equals("L") || CumulativeLevel.equals("D"));
 else throw new IllegalArgumentException ("CumulativeLevel Invalid value - Reference = CUMULATIVELEVEL_AD_Reference_ID - L - D");
if (CumulativeLevel != null && CumulativeLevel.length() > 1)
{
log.warning("Length > 1 - truncated");
CumulativeLevel = CumulativeLevel.substring(0,1);
}
set_Value ("CumulativeLevel", CumulativeLevel);
}
/** Get Cumulative Level.
Level for cumulative calculations */
public String getCumulativeLevel() 
{
return (String)get_Value("CumulativeLevel");
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
/** Set Discount Amount.
Calculated amount of discount */
public void setDiscountAmt (BigDecimal DiscountAmt)
{
if (DiscountAmt == null) throw new IllegalArgumentException ("DiscountAmt is mandatory");
set_Value ("DiscountAmt", DiscountAmt);
}
/** Get Discount Amount.
Calculated amount of discount */
public BigDecimal getDiscountAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("DiscountAmt");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int DISCOUNTAPPLICATION_AD_Reference_ID = MReference.getReferenceID("Discount Application");
/** Discount To Price = D */
public static final String DISCOUNTAPPLICATION_DiscountToPrice = "D";
/** Bonus = B */
public static final String DISCOUNTAPPLICATION_Bonus = "B";
/** Set Discount Application.
Discount Application */
public void setDiscountApplication (String DiscountApplication)
{
if (DiscountApplication == null || DiscountApplication.equals("D") || DiscountApplication.equals("B"));
 else throw new IllegalArgumentException ("DiscountApplication Invalid value - Reference = DISCOUNTAPPLICATION_AD_Reference_ID - D - B");
if (DiscountApplication != null && DiscountApplication.length() > 1)
{
log.warning("Length > 1 - truncated");
DiscountApplication = DiscountApplication.substring(0,1);
}
set_Value ("DiscountApplication", DiscountApplication);
}
/** Get Discount Application.
Discount Application */
public String getDiscountApplication() 
{
return (String)get_Value("DiscountApplication");
}
/** Set Discount Base Amount.
Discount Base Amount */
public void setDiscountBaseAmt (BigDecimal DiscountBaseAmt)
{
if (DiscountBaseAmt == null) throw new IllegalArgumentException ("DiscountBaseAmt is mandatory");
set_Value ("DiscountBaseAmt", DiscountBaseAmt);
}
/** Get Discount Base Amount.
Discount Base Amount */
public BigDecimal getDiscountBaseAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("DiscountBaseAmt");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int DISCOUNTKIND_AD_Reference_ID = MReference.getReferenceID("Discount Kind");
/** General Discount Schema = G */
public static final String DISCOUNTKIND_GeneralDiscountSchema = "G";
/** BPartner Discount Schema = B */
public static final String DISCOUNTKIND_BPartnerDiscountSchema = "B";
/** Products Combo = C */
public static final String DISCOUNTKIND_ProductsCombo = "C";
/** Promotion = P */
public static final String DISCOUNTKIND_Promotion = "P";
/** Payment Medium = M */
public static final String DISCOUNTKIND_PaymentMedium = "M";
/** Manual General Discount = Z */
public static final String DISCOUNTKIND_ManualGeneralDiscount = "Z";
/** Manual Discount = D */
public static final String DISCOUNTKIND_ManualDiscount = "D";
/** Discount Line = L */
public static final String DISCOUNTKIND_DiscountLine = "L";
/** Document Discount = X */
public static final String DISCOUNTKIND_DocumentDiscount = "X";
/** Set Discount Kind.
Discount Kind */
public void setDiscountKind (String DiscountKind)
{
if (DiscountKind == null || DiscountKind.equals("G") || DiscountKind.equals("B") || DiscountKind.equals("C") || DiscountKind.equals("P") || DiscountKind.equals("M") || DiscountKind.equals("Z") || DiscountKind.equals("D") || DiscountKind.equals("L") || DiscountKind.equals("X"));
 else throw new IllegalArgumentException ("DiscountKind Invalid value - Reference = DISCOUNTKIND_AD_Reference_ID - G - B - C - P - M - Z - D - L - X");
if (DiscountKind != null && DiscountKind.length() > 1)
{
log.warning("Length > 1 - truncated");
DiscountKind = DiscountKind.substring(0,1);
}
set_Value ("DiscountKind", DiscountKind);
}
/** Get Discount Kind.
Discount Kind */
public String getDiscountKind() 
{
return (String)get_Value("DiscountKind");
}
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
/** Set Tax Rate.
Tax Rate */
public void setTaxRate (BigDecimal TaxRate)
{
set_Value ("TaxRate", TaxRate);
}
/** Get Tax Rate.
Tax Rate */
public BigDecimal getTaxRate() 
{
BigDecimal bd = (BigDecimal)get_Value("TaxRate");
if (bd == null) return Env.ZERO;
return bd;
}
}
