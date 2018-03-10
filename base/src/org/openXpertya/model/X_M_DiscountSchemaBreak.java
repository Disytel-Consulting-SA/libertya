/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_DiscountSchemaBreak
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2018-03-10 20:39:53.406 */
public class X_M_DiscountSchemaBreak extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_DiscountSchemaBreak (Properties ctx, int M_DiscountSchemaBreak_ID, String trxName)
{
super (ctx, M_DiscountSchemaBreak_ID, trxName);
/** if (M_DiscountSchemaBreak_ID == 0)
{
setApplicationPolicy (null);	// A
setBreakDiscount (Env.ZERO);
setBreakValue (Env.ZERO);
setIsAppliedEveryday (false);
setIsAppliedOnFriday (false);
setIsAppliedOnMonday (false);
setIsAppliedOnSaturday (false);
setIsAppliedOnSunday (false);
setIsAppliedOnThursday (false);
setIsAppliedOnTuesday (false);
setIsAppliedOnWednesday (false);
setIsBPartnerFlatDiscount (false);	// N
setIsBreak (false);
setM_DiscountSchemaBreak_ID (0);
setM_DiscountSchema_ID (0);
setSeqNo (0);	// @SQL=SELECT NVL(MAX(SeqNo),0)+10 AS DefaultValue FROM M_DiscountSchemaBreak WHERE M_DiscountSchema_ID=@M_DiscountSchema_ID@
}
 */
}
/** Load Constructor */
public X_M_DiscountSchemaBreak (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_DiscountSchemaBreak");

/** TableName=M_DiscountSchemaBreak */
public static final String Table_Name="M_DiscountSchemaBreak";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_DiscountSchemaBreak");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_DiscountSchemaBreak[").append(getID()).append("]");
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
public static final int APPLICATIONPOLICY_AD_Reference_ID = MReference.getReferenceID("M_DiscountSchemaBreak Application Policy");
/** All True = A */
public static final String APPLICATIONPOLICY_AllTrue = "A";
/** Any True = O */
public static final String APPLICATIONPOLICY_AnyTrue = "O";
/** Set Application Policy.
Break Application Policy */
public void setApplicationPolicy (String ApplicationPolicy)
{
if (ApplicationPolicy.equals("A") || ApplicationPolicy.equals("O") || ( refContainsValue("CORE-AD_Reference-1010130", ApplicationPolicy) ) );
 else throw new IllegalArgumentException ("ApplicationPolicy Invalid value: " + ApplicationPolicy + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010130") );
if (ApplicationPolicy == null) throw new IllegalArgumentException ("ApplicationPolicy is mandatory");
if (ApplicationPolicy.length() > 1)
{
log.warning("Length > 1 - truncated");
ApplicationPolicy = ApplicationPolicy.substring(0,1);
}
set_Value ("ApplicationPolicy", ApplicationPolicy);
}
/** Get Application Policy.
Break Application Policy */
public String getApplicationPolicy() 
{
return (String)get_Value("ApplicationPolicy");
}
/** Set Break Discount %.
Trade Discount in Percent for the break level */
public void setBreakDiscount (BigDecimal BreakDiscount)
{
if (BreakDiscount == null) throw new IllegalArgumentException ("BreakDiscount is mandatory");
set_Value ("BreakDiscount", BreakDiscount);
}
/** Get Break Discount %.
Trade Discount in Percent for the break level */
public BigDecimal getBreakDiscount() 
{
BigDecimal bd = (BigDecimal)get_Value("BreakDiscount");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Break Value.
Low Value of trade discount break level */
public void setBreakValue (BigDecimal BreakValue)
{
if (BreakValue == null) throw new IllegalArgumentException ("BreakValue is mandatory");
set_Value ("BreakValue", BreakValue);
}
/** Get Break Value.
Low Value of trade discount break level */
public BigDecimal getBreakValue() 
{
BigDecimal bd = (BigDecimal)get_Value("BreakValue");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set C_BPartner_ID */
public void setC_BPartner_ID (int C_BPartner_ID)
{
if (C_BPartner_ID <= 0) set_Value ("C_BPartner_ID", null);
 else 
set_Value ("C_BPartner_ID", new Integer(C_BPartner_ID));
}
/** Get C_BPartner_ID */
public int getC_BPartner_ID() 
{
Integer ii = (Integer)get_Value("C_BPartner_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Everyday.
Applied Everyday */
public void setIsAppliedEveryday (boolean IsAppliedEveryday)
{
set_Value ("IsAppliedEveryday", new Boolean(IsAppliedEveryday));
}
/** Get Everyday.
Applied Everyday */
public boolean isAppliedEveryday() 
{
Object oo = get_Value("IsAppliedEveryday");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set On Friday.
Applied on Friday */
public void setIsAppliedOnFriday (boolean IsAppliedOnFriday)
{
set_Value ("IsAppliedOnFriday", new Boolean(IsAppliedOnFriday));
}
/** Get On Friday.
Applied on Friday */
public boolean isAppliedOnFriday() 
{
Object oo = get_Value("IsAppliedOnFriday");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set On Monday.
Applied on Monday */
public void setIsAppliedOnMonday (boolean IsAppliedOnMonday)
{
set_Value ("IsAppliedOnMonday", new Boolean(IsAppliedOnMonday));
}
/** Get On Monday.
Applied on Monday */
public boolean isAppliedOnMonday() 
{
Object oo = get_Value("IsAppliedOnMonday");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set On Saturday.
Applied on Saturday */
public void setIsAppliedOnSaturday (boolean IsAppliedOnSaturday)
{
set_Value ("IsAppliedOnSaturday", new Boolean(IsAppliedOnSaturday));
}
/** Get On Saturday.
Applied on Saturday */
public boolean isAppliedOnSaturday() 
{
Object oo = get_Value("IsAppliedOnSaturday");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set On Sunday.
Applied on Sunday */
public void setIsAppliedOnSunday (boolean IsAppliedOnSunday)
{
set_Value ("IsAppliedOnSunday", new Boolean(IsAppliedOnSunday));
}
/** Get On Sunday.
Applied on Sunday */
public boolean isAppliedOnSunday() 
{
Object oo = get_Value("IsAppliedOnSunday");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set On Thursday.
Applied on Thursday */
public void setIsAppliedOnThursday (boolean IsAppliedOnThursday)
{
set_Value ("IsAppliedOnThursday", new Boolean(IsAppliedOnThursday));
}
/** Get On Thursday.
Applied on Thursday */
public boolean isAppliedOnThursday() 
{
Object oo = get_Value("IsAppliedOnThursday");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set On Tuesday.
Applied on Tuesday */
public void setIsAppliedOnTuesday (boolean IsAppliedOnTuesday)
{
set_Value ("IsAppliedOnTuesday", new Boolean(IsAppliedOnTuesday));
}
/** Get On Tuesday.
Applied on Tuesday */
public boolean isAppliedOnTuesday() 
{
Object oo = get_Value("IsAppliedOnTuesday");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set On Wednesday.
Applied on Wednesday */
public void setIsAppliedOnWednesday (boolean IsAppliedOnWednesday)
{
set_Value ("IsAppliedOnWednesday", new Boolean(IsAppliedOnWednesday));
}
/** Get On Wednesday.
Applied on Wednesday */
public boolean isAppliedOnWednesday() 
{
Object oo = get_Value("IsAppliedOnWednesday");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set B.Partner Flat Discount.
Use flat discount defined on Business Partner Level */
public void setIsBPartnerFlatDiscount (boolean IsBPartnerFlatDiscount)
{
set_Value ("IsBPartnerFlatDiscount", new Boolean(IsBPartnerFlatDiscount));
}
/** Get B.Partner Flat Discount.
Use flat discount defined on Business Partner Level */
public boolean isBPartnerFlatDiscount() 
{
Object oo = get_Value("IsBPartnerFlatDiscount");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Break Rule.
Break Rule */
public void setIsBreak (boolean IsBreak)
{
set_Value ("IsBreak", new Boolean(IsBreak));
}
/** Get Break Rule.
Break Rule */
public boolean isBreak() 
{
Object oo = get_Value("IsBreak");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Discount Schema Break.
Trade Discount Break */
public void setM_DiscountSchemaBreak_ID (int M_DiscountSchemaBreak_ID)
{
set_ValueNoCheck ("M_DiscountSchemaBreak_ID", new Integer(M_DiscountSchemaBreak_ID));
}
/** Get Discount Schema Break.
Trade Discount Break */
public int getM_DiscountSchemaBreak_ID() 
{
Integer ii = (Integer)get_Value("M_DiscountSchemaBreak_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set price_gross */
public void setprice_gross (BigDecimal price_gross)
{
set_Value ("price_gross", price_gross);
}
/** Get price_gross */
public BigDecimal getprice_gross() 
{
BigDecimal bd = (BigDecimal)get_Value("price_gross");
if (bd == null) return Env.ZERO;
return bd;
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
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), String.valueOf(getSeqNo()));
}
}
