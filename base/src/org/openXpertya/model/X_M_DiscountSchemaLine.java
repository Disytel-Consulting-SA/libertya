/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_DiscountSchemaLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-03-29 11:30:04.599 */
public class X_M_DiscountSchemaLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_DiscountSchemaLine (Properties ctx, int M_DiscountSchemaLine_ID, String trxName)
{
super (ctx, M_DiscountSchemaLine_ID, trxName);
/** if (M_DiscountSchemaLine_ID == 0)
{
setC_ConversionType_ID (0);
setConversionDate (new Timestamp(System.currentTimeMillis()));	// @#Date@
setLimit_AddAmt (Env.ZERO);
setLimit_AddProductTax (false);
setLimit_Base (null);	// X
setLimit_Discount (Env.ZERO);
setLimit_MaxAmt (Env.ZERO);
setLimit_MinAmt (Env.ZERO);
setLimit_Rounding (null);	// C
setList_AddAmt (Env.ZERO);
setList_AddProductTax (false);
setList_Base (null);	// L
setList_Discount (Env.ZERO);
setList_MaxAmt (Env.ZERO);
setList_MinAmt (Env.ZERO);
setList_Rounding (null);	// C
setM_DiscountSchema_ID (0);
setM_DiscountSchemaLine_ID (0);
setName (null);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM M_DiscountSchemaLine WHERE M_DiscountSchema_ID=@M_DiscountSchema_ID@
setStd_AddAmt (Env.ZERO);
setStd_AddProductTax (false);
setStd_Base (null);	// S
setStd_Discount (Env.ZERO);
setStd_MaxAmt (Env.ZERO);
setStd_MinAmt (Env.ZERO);
setStd_Rounding (null);	// C
}
 */
}
/** Load Constructor */
public X_M_DiscountSchemaLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_DiscountSchemaLine");

/** TableName=M_DiscountSchemaLine */
public static final String Table_Name="M_DiscountSchemaLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_DiscountSchemaLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_DiscountSchemaLine[").append(getID()).append("]");
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
/** Set Currency Type.
Currency Conversion Rate Type */
public void setC_ConversionType_ID (int C_ConversionType_ID)
{
set_Value ("C_ConversionType_ID", new Integer(C_ConversionType_ID));
}
/** Get Currency Type.
Currency Conversion Rate Type */
public int getC_ConversionType_ID() 
{
Integer ii = (Integer)get_Value("C_ConversionType_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Conversion Date.
Date for selecting conversion rate */
public void setConversionDate (Timestamp ConversionDate)
{
if (ConversionDate == null) throw new IllegalArgumentException ("ConversionDate is mandatory");
set_Value ("ConversionDate", ConversionDate);
}
/** Get Conversion Date.
Date for selecting conversion rate */
public Timestamp getConversionDate() 
{
return (Timestamp)get_Value("ConversionDate");
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
/** Set IsStrong */
public void setIsStrong (boolean IsStrong)
{
set_Value ("IsStrong", new Boolean(IsStrong));
}
/** Get IsStrong */
public boolean isStrong() 
{
Object oo = get_Value("IsStrong");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Limit price Surcharge Amount.
Amount added to the converted/copied price before multiplying */
public void setLimit_AddAmt (BigDecimal Limit_AddAmt)
{
if (Limit_AddAmt == null) throw new IllegalArgumentException ("Limit_AddAmt is mandatory");
set_Value ("Limit_AddAmt", Limit_AddAmt);
}
/** Get Limit price Surcharge Amount.
Amount added to the converted/copied price before multiplying */
public BigDecimal getLimit_AddAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("Limit_AddAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Add product tax.
Add product tax rate to price limit */
public void setLimit_AddProductTax (boolean Limit_AddProductTax)
{
set_Value ("Limit_AddProductTax", new Boolean(Limit_AddProductTax));
}
/** Get Add product tax.
Add product tax rate to price limit */
public boolean isLimit_AddProductTax() 
{
Object oo = get_Value("Limit_AddProductTax");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int LIMIT_BASE_AD_Reference_ID = MReference.getReferenceID("M_DiscountPriceList Base");
/** Fixed Price = F */
public static final String LIMIT_BASE_FixedPrice = "F";
/** List Price = L */
public static final String LIMIT_BASE_ListPrice = "L";
/** Standard Price = S */
public static final String LIMIT_BASE_StandardPrice = "S";
/** Limit (PO) Price = X */
public static final String LIMIT_BASE_LimitPOPrice = "X";
/** Set Limit price Base.
Base price for calculation of the new price */
public void setLimit_Base (String Limit_Base)
{
if (Limit_Base.equals("F") || Limit_Base.equals("L") || Limit_Base.equals("S") || Limit_Base.equals("X") || ( refContainsValue("CORE-AD_Reference-194", Limit_Base) ) );
 else throw new IllegalArgumentException ("Limit_Base Invalid value: " + Limit_Base + ".  Valid: " +  refValidOptions("CORE-AD_Reference-194") );
if (Limit_Base == null) throw new IllegalArgumentException ("Limit_Base is mandatory");
if (Limit_Base.length() > 1)
{
log.warning("Length > 1 - truncated");
Limit_Base = Limit_Base.substring(0,1);
}
set_Value ("Limit_Base", Limit_Base);
}
/** Get Limit price Base.
Base price for calculation of the new price */
public String getLimit_Base() 
{
return (String)get_Value("Limit_Base");
}
/** Set Limit price Discount %.
Discount in percent to be subtracted from base, if negative it will be added to base price */
public void setLimit_Discount (BigDecimal Limit_Discount)
{
if (Limit_Discount == null) throw new IllegalArgumentException ("Limit_Discount is mandatory");
set_Value ("Limit_Discount", Limit_Discount);
}
/** Get Limit price Discount %.
Discount in percent to be subtracted from base, if negative it will be added to base price */
public BigDecimal getLimit_Discount() 
{
BigDecimal bd = (BigDecimal)get_Value("Limit_Discount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fixed Limit Price.
Fixed Limit Price (not calculated) */
public void setLimit_Fixed (BigDecimal Limit_Fixed)
{
set_Value ("Limit_Fixed", Limit_Fixed);
}
/** Get Fixed Limit Price.
Fixed Limit Price (not calculated) */
public BigDecimal getLimit_Fixed() 
{
BigDecimal bd = (BigDecimal)get_Value("Limit_Fixed");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Limit price max Margin.
Maximum difference to original limit price;
 ignored if zero */
public void setLimit_MaxAmt (BigDecimal Limit_MaxAmt)
{
if (Limit_MaxAmt == null) throw new IllegalArgumentException ("Limit_MaxAmt is mandatory");
set_Value ("Limit_MaxAmt", Limit_MaxAmt);
}
/** Get Limit price max Margin.
Maximum difference to original limit price;
 ignored if zero */
public BigDecimal getLimit_MaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("Limit_MaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Limit price min Margin.
Minimum difference to original limit price;
 ignored if zero */
public void setLimit_MinAmt (BigDecimal Limit_MinAmt)
{
if (Limit_MinAmt == null) throw new IllegalArgumentException ("Limit_MinAmt is mandatory");
set_Value ("Limit_MinAmt", Limit_MinAmt);
}
/** Get Limit price min Margin.
Minimum difference to original limit price;
 ignored if zero */
public BigDecimal getLimit_MinAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("Limit_MinAmt");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int LIMIT_ROUNDING_AD_Reference_ID = MReference.getReferenceID("M_DiscountPriceList RoundingRule");
/** Currency Precision = C */
public static final String LIMIT_ROUNDING_CurrencyPrecision = "C";
/** Whole Number .00 = 0 */
public static final String LIMIT_ROUNDING_WholeNumber00 = "0";
/** No Rounding = N */
public static final String LIMIT_ROUNDING_NoRounding = "N";
/** Quarter .25 .50 .75 = Q */
public static final String LIMIT_ROUNDING_Quarter255075 = "Q";
/** Dime .10, .20, .30, ... = D */
public static final String LIMIT_ROUNDING_Dime102030 = "D";
/** Nickel .05, .10, .15, ... = 5 */
public static final String LIMIT_ROUNDING_Nickel051015 = "5";
/** Ten 10.00, 20.00, .. = T */
public static final String LIMIT_ROUNDING_Ten10002000 = "T";
/** 0.90 round up = U */
public static final String LIMIT_ROUNDING_090RoundUp = "U";
/** Always 0.90 = 9 */
public static final String LIMIT_ROUNDING_Always090 = "9";
/** Set Limit price Rounding.
Rounding of the final result */
public void setLimit_Rounding (String Limit_Rounding)
{
if (Limit_Rounding.equals("C") || Limit_Rounding.equals("0") || Limit_Rounding.equals("N") || Limit_Rounding.equals("Q") || Limit_Rounding.equals("D") || Limit_Rounding.equals("5") || Limit_Rounding.equals("T") || Limit_Rounding.equals("U") || Limit_Rounding.equals("9") || ( refContainsValue("CORE-AD_Reference-155", Limit_Rounding) ) );
 else throw new IllegalArgumentException ("Limit_Rounding Invalid value: " + Limit_Rounding + ".  Valid: " +  refValidOptions("CORE-AD_Reference-155") );
if (Limit_Rounding == null) throw new IllegalArgumentException ("Limit_Rounding is mandatory");
if (Limit_Rounding.length() > 1)
{
log.warning("Length > 1 - truncated");
Limit_Rounding = Limit_Rounding.substring(0,1);
}
set_Value ("Limit_Rounding", Limit_Rounding);
}
/** Get Limit price Rounding.
Rounding of the final result */
public String getLimit_Rounding() 
{
return (String)get_Value("Limit_Rounding");
}
/** Set List price Surcharge Amount.
List Price Surcharge Amount */
public void setList_AddAmt (BigDecimal List_AddAmt)
{
if (List_AddAmt == null) throw new IllegalArgumentException ("List_AddAmt is mandatory");
set_Value ("List_AddAmt", List_AddAmt);
}
/** Get List price Surcharge Amount.
List Price Surcharge Amount */
public BigDecimal getList_AddAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("List_AddAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Add product tax.
Add product tax rate to price list */
public void setList_AddProductTax (boolean List_AddProductTax)
{
set_Value ("List_AddProductTax", new Boolean(List_AddProductTax));
}
/** Get Add product tax.
Add product tax rate to price list */
public boolean isList_AddProductTax() 
{
Object oo = get_Value("List_AddProductTax");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int LIST_BASE_AD_Reference_ID = MReference.getReferenceID("M_DiscountPriceList Base");
/** Fixed Price = F */
public static final String LIST_BASE_FixedPrice = "F";
/** List Price = L */
public static final String LIST_BASE_ListPrice = "L";
/** Standard Price = S */
public static final String LIST_BASE_StandardPrice = "S";
/** Limit (PO) Price = X */
public static final String LIST_BASE_LimitPOPrice = "X";
/** Set List price Base.
Price used as the basis for price list calculations */
public void setList_Base (String List_Base)
{
if (List_Base.equals("F") || List_Base.equals("L") || List_Base.equals("S") || List_Base.equals("X") || ( refContainsValue("CORE-AD_Reference-194", List_Base) ) );
 else throw new IllegalArgumentException ("List_Base Invalid value: " + List_Base + ".  Valid: " +  refValidOptions("CORE-AD_Reference-194") );
if (List_Base == null) throw new IllegalArgumentException ("List_Base is mandatory");
if (List_Base.length() > 1)
{
log.warning("Length > 1 - truncated");
List_Base = List_Base.substring(0,1);
}
set_Value ("List_Base", List_Base);
}
/** Get List price Base.
Price used as the basis for price list calculations */
public String getList_Base() 
{
return (String)get_Value("List_Base");
}
/** Set List price Discount %.
Discount from list price as a percentage */
public void setList_Discount (BigDecimal List_Discount)
{
if (List_Discount == null) throw new IllegalArgumentException ("List_Discount is mandatory");
set_Value ("List_Discount", List_Discount);
}
/** Get List price Discount %.
Discount from list price as a percentage */
public BigDecimal getList_Discount() 
{
BigDecimal bd = (BigDecimal)get_Value("List_Discount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fixed List Price.
Fixes List Price (not calculated) */
public void setList_Fixed (BigDecimal List_Fixed)
{
set_Value ("List_Fixed", List_Fixed);
}
/** Get Fixed List Price.
Fixes List Price (not calculated) */
public BigDecimal getList_Fixed() 
{
BigDecimal bd = (BigDecimal)get_Value("List_Fixed");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set List price max Margin.
Maximum margin for a product */
public void setList_MaxAmt (BigDecimal List_MaxAmt)
{
if (List_MaxAmt == null) throw new IllegalArgumentException ("List_MaxAmt is mandatory");
set_Value ("List_MaxAmt", List_MaxAmt);
}
/** Get List price max Margin.
Maximum margin for a product */
public BigDecimal getList_MaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("List_MaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set List price min Margin.
Minimum margin for a product */
public void setList_MinAmt (BigDecimal List_MinAmt)
{
if (List_MinAmt == null) throw new IllegalArgumentException ("List_MinAmt is mandatory");
set_Value ("List_MinAmt", List_MinAmt);
}
/** Get List price min Margin.
Minimum margin for a product */
public BigDecimal getList_MinAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("List_MinAmt");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int LIST_ROUNDING_AD_Reference_ID = MReference.getReferenceID("M_DiscountPriceList RoundingRule");
/** Currency Precision = C */
public static final String LIST_ROUNDING_CurrencyPrecision = "C";
/** Whole Number .00 = 0 */
public static final String LIST_ROUNDING_WholeNumber00 = "0";
/** No Rounding = N */
public static final String LIST_ROUNDING_NoRounding = "N";
/** Quarter .25 .50 .75 = Q */
public static final String LIST_ROUNDING_Quarter255075 = "Q";
/** Dime .10, .20, .30, ... = D */
public static final String LIST_ROUNDING_Dime102030 = "D";
/** Nickel .05, .10, .15, ... = 5 */
public static final String LIST_ROUNDING_Nickel051015 = "5";
/** Ten 10.00, 20.00, .. = T */
public static final String LIST_ROUNDING_Ten10002000 = "T";
/** 0.90 round up = U */
public static final String LIST_ROUNDING_090RoundUp = "U";
/** Always 0.90 = 9 */
public static final String LIST_ROUNDING_Always090 = "9";
/** Set List price Rounding.
Rounding rule for final list price */
public void setList_Rounding (String List_Rounding)
{
if (List_Rounding.equals("C") || List_Rounding.equals("0") || List_Rounding.equals("N") || List_Rounding.equals("Q") || List_Rounding.equals("D") || List_Rounding.equals("5") || List_Rounding.equals("T") || List_Rounding.equals("U") || List_Rounding.equals("9") || ( refContainsValue("CORE-AD_Reference-155", List_Rounding) ) );
 else throw new IllegalArgumentException ("List_Rounding Invalid value: " + List_Rounding + ".  Valid: " +  refValidOptions("CORE-AD_Reference-155") );
if (List_Rounding == null) throw new IllegalArgumentException ("List_Rounding is mandatory");
if (List_Rounding.length() > 1)
{
log.warning("Length > 1 - truncated");
List_Rounding = List_Rounding.substring(0,1);
}
set_Value ("List_Rounding", List_Rounding);
}
/** Get List price Rounding.
Rounding rule for final list price */
public String getList_Rounding() 
{
return (String)get_Value("List_Rounding");
}
/** Set Discount Schema.
Schema to calculate the trade discount percentage */
public void setM_DiscountSchema_ID (int M_DiscountSchema_ID)
{
set_ValueNoCheck ("M_DiscountSchema_ID", new Integer(M_DiscountSchema_ID));
}
/** Get Discount Schema.
Schema to calculate the trade discount percentage */
public int getM_DiscountSchema_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Discount Pricelist.
Line of the pricelist trade discount schema */
public void setM_DiscountSchemaLine_ID (int M_DiscountSchemaLine_ID)
{
set_ValueNoCheck ("M_DiscountSchemaLine_ID", new Integer(M_DiscountSchemaLine_ID));
}
/** Get Discount Pricelist.
Line of the pricelist trade discount schema */
public int getM_DiscountSchemaLine_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountSchemaLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product Category.
Category of a Product */
public void setM_Product_Category_ID (int M_Product_Category_ID)
{
if (M_Product_Category_ID <= 0) set_Value ("M_Product_Category_ID", null);
 else 
set_Value ("M_Product_Category_ID", new Integer(M_Product_Category_ID));
}
/** Get Product Category.
Category of a Product */
public int getM_Product_Category_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Category_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_Product_Family_ID */
public void setM_Product_Family_ID (int M_Product_Family_ID)
{
if (M_Product_Family_ID <= 0) set_Value ("M_Product_Family_ID", null);
 else 
set_Value ("M_Product_Family_ID", new Integer(M_Product_Family_ID));
}
/** Get M_Product_Family_ID */
public int getM_Product_Family_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Family_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set M_Product_Gamas_ID */
public void setM_Product_Gamas_ID (int M_Product_Gamas_ID)
{
if (M_Product_Gamas_ID <= 0) set_Value ("M_Product_Gamas_ID", null);
 else 
set_Value ("M_Product_Gamas_ID", new Integer(M_Product_Gamas_ID));
}
/** Get M_Product_Gamas_ID */
public int getM_Product_Gamas_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Gamas_ID");
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
/** Set Product Line */
public void setM_Product_Lines_ID (int M_Product_Lines_ID)
{
if (M_Product_Lines_ID <= 0) set_Value ("M_Product_Lines_ID", null);
 else 
set_Value ("M_Product_Lines_ID", new Integer(M_Product_Lines_ID));
}
/** Get Product Line */
public int getM_Product_Lines_ID() 
{
Integer ii = (Integer)get_Value("M_Product_Lines_ID");
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
/** Set Sequence.
Method of ordering records;
 lowest number comes first */
public void setSeqNo (int SeqNo)
{
set_Value ("SeqNo", new Integer(SeqNo));
}
/** Get Sequence.
Method of ordering records;
 lowest number comes first */
public int getSeqNo() 
{
Integer ii = (Integer)get_Value("SeqNo");
if (ii == null) return 0;
return ii.intValue();
}
public static final int SOLDPURCHASEDOPTION_AD_Reference_ID = MReference.getReferenceID("Sold/Purchased Options");
/** Sold = S */
public static final String SOLDPURCHASEDOPTION_Sold = "S";
/** Purchased = P */
public static final String SOLDPURCHASEDOPTION_Purchased = "P";
/** Sold and Purchased = B */
public static final String SOLDPURCHASEDOPTION_SoldAndPurchased = "B";
/** Set Sold/Purchased option */
public void setSoldPurchasedOption (String SoldPurchasedOption)
{
if (SoldPurchasedOption == null || SoldPurchasedOption.equals("S") || SoldPurchasedOption.equals("P") || SoldPurchasedOption.equals("B") || ( refContainsValue("CORE-AD_Reference-1010233", SoldPurchasedOption) ) );
 else throw new IllegalArgumentException ("SoldPurchasedOption Invalid value: " + SoldPurchasedOption + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010233") );
if (SoldPurchasedOption != null && SoldPurchasedOption.length() > 1)
{
log.warning("Length > 1 - truncated");
SoldPurchasedOption = SoldPurchasedOption.substring(0,1);
}
set_Value ("SoldPurchasedOption", SoldPurchasedOption);
}
/** Get Sold/Purchased option */
public String getSoldPurchasedOption() 
{
return (String)get_Value("SoldPurchasedOption");
}
/** Set Standard price Surcharge Amount.
Amount added to a price as a surcharge */
public void setStd_AddAmt (BigDecimal Std_AddAmt)
{
if (Std_AddAmt == null) throw new IllegalArgumentException ("Std_AddAmt is mandatory");
set_Value ("Std_AddAmt", Std_AddAmt);
}
/** Get Standard price Surcharge Amount.
Amount added to a price as a surcharge */
public BigDecimal getStd_AddAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("Std_AddAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Add product tax.
Add product tax rate to price std */
public void setStd_AddProductTax (boolean Std_AddProductTax)
{
set_Value ("Std_AddProductTax", new Boolean(Std_AddProductTax));
}
/** Get Add product tax.
Add product tax rate to price std */
public boolean isStd_AddProductTax() 
{
Object oo = get_Value("Std_AddProductTax");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int STD_BASE_AD_Reference_ID = MReference.getReferenceID("M_DiscountPriceList Base");
/** Fixed Price = F */
public static final String STD_BASE_FixedPrice = "F";
/** List Price = L */
public static final String STD_BASE_ListPrice = "L";
/** Standard Price = S */
public static final String STD_BASE_StandardPrice = "S";
/** Limit (PO) Price = X */
public static final String STD_BASE_LimitPOPrice = "X";
/** Set Standard price Base.
Base price for calculating new standard price */
public void setStd_Base (String Std_Base)
{
if (Std_Base.equals("F") || Std_Base.equals("L") || Std_Base.equals("S") || Std_Base.equals("X") || ( refContainsValue("CORE-AD_Reference-194", Std_Base) ) );
 else throw new IllegalArgumentException ("Std_Base Invalid value: " + Std_Base + ".  Valid: " +  refValidOptions("CORE-AD_Reference-194") );
if (Std_Base == null) throw new IllegalArgumentException ("Std_Base is mandatory");
if (Std_Base.length() > 1)
{
log.warning("Length > 1 - truncated");
Std_Base = Std_Base.substring(0,1);
}
set_Value ("Std_Base", Std_Base);
}
/** Get Standard price Base.
Base price for calculating new standard price */
public String getStd_Base() 
{
return (String)get_Value("Std_Base");
}
/** Set Standard price Discount %.
Discount percentage to subtract from base price */
public void setStd_Discount (BigDecimal Std_Discount)
{
if (Std_Discount == null) throw new IllegalArgumentException ("Std_Discount is mandatory");
set_Value ("Std_Discount", Std_Discount);
}
/** Get Standard price Discount %.
Discount percentage to subtract from base price */
public BigDecimal getStd_Discount() 
{
BigDecimal bd = (BigDecimal)get_Value("Std_Discount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Fixed Standard Price.
Fixed Standard Price (not calculated) */
public void setStd_Fixed (BigDecimal Std_Fixed)
{
set_Value ("Std_Fixed", Std_Fixed);
}
/** Get Fixed Standard Price.
Fixed Standard Price (not calculated) */
public BigDecimal getStd_Fixed() 
{
BigDecimal bd = (BigDecimal)get_Value("Std_Fixed");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Standard max Margin.
Maximum margin allowed for a product */
public void setStd_MaxAmt (BigDecimal Std_MaxAmt)
{
if (Std_MaxAmt == null) throw new IllegalArgumentException ("Std_MaxAmt is mandatory");
set_Value ("Std_MaxAmt", Std_MaxAmt);
}
/** Get Standard max Margin.
Maximum margin allowed for a product */
public BigDecimal getStd_MaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("Std_MaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Standard price min Margin.
Minimum margin allowed for a product */
public void setStd_MinAmt (BigDecimal Std_MinAmt)
{
if (Std_MinAmt == null) throw new IllegalArgumentException ("Std_MinAmt is mandatory");
set_Value ("Std_MinAmt", Std_MinAmt);
}
/** Get Standard price min Margin.
Minimum margin allowed for a product */
public BigDecimal getStd_MinAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("Std_MinAmt");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int STD_ROUNDING_AD_Reference_ID = MReference.getReferenceID("M_DiscountPriceList RoundingRule");
/** Currency Precision = C */
public static final String STD_ROUNDING_CurrencyPrecision = "C";
/** Whole Number .00 = 0 */
public static final String STD_ROUNDING_WholeNumber00 = "0";
/** No Rounding = N */
public static final String STD_ROUNDING_NoRounding = "N";
/** Quarter .25 .50 .75 = Q */
public static final String STD_ROUNDING_Quarter255075 = "Q";
/** Dime .10, .20, .30, ... = D */
public static final String STD_ROUNDING_Dime102030 = "D";
/** Nickel .05, .10, .15, ... = 5 */
public static final String STD_ROUNDING_Nickel051015 = "5";
/** Ten 10.00, 20.00, .. = T */
public static final String STD_ROUNDING_Ten10002000 = "T";
/** 0.90 round up = U */
public static final String STD_ROUNDING_090RoundUp = "U";
/** Always 0.90 = 9 */
public static final String STD_ROUNDING_Always090 = "9";
/** Set Standard price Rounding.
Rounding rule for calculated price */
public void setStd_Rounding (String Std_Rounding)
{
if (Std_Rounding.equals("C") || Std_Rounding.equals("0") || Std_Rounding.equals("N") || Std_Rounding.equals("Q") || Std_Rounding.equals("D") || Std_Rounding.equals("5") || Std_Rounding.equals("T") || Std_Rounding.equals("U") || Std_Rounding.equals("9") || ( refContainsValue("CORE-AD_Reference-155", Std_Rounding) ) );
 else throw new IllegalArgumentException ("Std_Rounding Invalid value: " + Std_Rounding + ".  Valid: " +  refValidOptions("CORE-AD_Reference-155") );
if (Std_Rounding == null) throw new IllegalArgumentException ("Std_Rounding is mandatory");
if (Std_Rounding.length() > 1)
{
log.warning("Length > 1 - truncated");
Std_Rounding = Std_Rounding.substring(0,1);
}
set_Value ("Std_Rounding", Std_Rounding);
}
/** Get Standard price Rounding.
Rounding rule for calculated price */
public String getStd_Rounding() 
{
return (String)get_Value("Std_Rounding");
}
}
