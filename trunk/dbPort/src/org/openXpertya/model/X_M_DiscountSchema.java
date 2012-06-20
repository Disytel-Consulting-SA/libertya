/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_DiscountSchema
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-01-19 17:17:34.475 */
public class X_M_DiscountSchema extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_DiscountSchema (Properties ctx, int M_DiscountSchema_ID, String trxName)
{
super (ctx, M_DiscountSchema_ID, trxName);
/** if (M_DiscountSchema_ID == 0)
{
setCumulativeLevel (null);	// D
setDiscountApplication (null);
setDiscountContextType (null);
setDiscountType (null);
setIsBPartnerFlatDiscount (false);
setIsBPartnerScope (false);	// N
setIsGeneralScope (true);	// Y
setIsQuantityBased (true);	// Y
setM_DiscountSchema_ID (0);
setName (null);
setValidFrom (new Timestamp(System.currentTimeMillis()));	// @#Date@
}
 */
}
/** Load Constructor */
public X_M_DiscountSchema (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_DiscountSchema");

/** TableName=M_DiscountSchema */
public static final String Table_Name="M_DiscountSchema";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_DiscountSchema");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_DiscountSchema[").append(getID()).append("]");
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
public static final int CUMULATIVELEVEL_AD_Reference_ID = MReference.getReferenceID("M_Discount CumulativeLevel");
/** Line = L */
public static final String CUMULATIVELEVEL_Line = "L";
/** Document = D */
public static final String CUMULATIVELEVEL_Document = "D";
/** Set Cumulative Level.
Level for cumulative calculations */
public void setCumulativeLevel (String CumulativeLevel)
{
if (CumulativeLevel.equals("L") || CumulativeLevel.equals("D"));
 else throw new IllegalArgumentException ("CumulativeLevel Invalid value - Reference = CUMULATIVELEVEL_AD_Reference_ID - L - D");
if (CumulativeLevel == null) throw new IllegalArgumentException ("CumulativeLevel is mandatory");
if (CumulativeLevel.length() > 1)
{
log.warning("Length > 1 - truncated");
CumulativeLevel = CumulativeLevel.substring(0,1);
}
set_ValueNoCheck ("CumulativeLevel", CumulativeLevel);
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
public static final int DISCOUNTAPPLICATION_AD_Reference_ID = MReference.getReferenceID("Discount Application");
/** Discount To Price = D */
public static final String DISCOUNTAPPLICATION_DiscountToPrice = "D";
/** Bonus = B */
public static final String DISCOUNTAPPLICATION_Bonus = "B";
/** Set Discount Application.
Discount Application */
public void setDiscountApplication (String DiscountApplication)
{
if (DiscountApplication.equals("D") || DiscountApplication.equals("B"));
 else throw new IllegalArgumentException ("DiscountApplication Invalid value - Reference = DISCOUNTAPPLICATION_AD_Reference_ID - D - B");
if (DiscountApplication == null) throw new IllegalArgumentException ("DiscountApplication is mandatory");
if (DiscountApplication.length() > 1)
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
public static final int DISCOUNTCONTEXTTYPE_AD_Reference_ID = MReference.getReferenceID("Discount Schema Context Type");
/** Commercial = C */
public static final String DISCOUNTCONTEXTTYPE_Commercial = "C";
/** Financial = F */
public static final String DISCOUNTCONTEXTTYPE_Financial = "F";
/** Set Discount Context Type */
public void setDiscountContextType (String DiscountContextType)
{
if (DiscountContextType.equals("C") || DiscountContextType.equals("F"));
 else throw new IllegalArgumentException ("DiscountContextType Invalid value - Reference = DISCOUNTCONTEXTTYPE_AD_Reference_ID - C - F");
if (DiscountContextType == null) throw new IllegalArgumentException ("DiscountContextType is mandatory");
if (DiscountContextType.length() > 1)
{
log.warning("Length > 1 - truncated");
DiscountContextType = DiscountContextType.substring(0,1);
}
set_ValueNoCheck ("DiscountContextType", DiscountContextType);
}
/** Get Discount Context Type */
public String getDiscountContextType() 
{
return (String)get_Value("DiscountContextType");
}
public static final int DISCOUNTTYPE_AD_Reference_ID = MReference.getReferenceID("M_Discount Type");
/** Flat Percent = F */
public static final String DISCOUNTTYPE_FlatPercent = "F";
/** Formula = S */
public static final String DISCOUNTTYPE_Formula = "S";
/** Breaks = B */
public static final String DISCOUNTTYPE_Breaks = "B";
/** Pricelist = P */
public static final String DISCOUNTTYPE_Pricelist = "P";
/** Set Discount Type.
Type of trade discount calculation */
public void setDiscountType (String DiscountType)
{
if (DiscountType.equals("F") || DiscountType.equals("S") || DiscountType.equals("B") || DiscountType.equals("P"));
 else throw new IllegalArgumentException ("DiscountType Invalid value - Reference = DISCOUNTTYPE_AD_Reference_ID - F - S - B - P");
if (DiscountType == null) throw new IllegalArgumentException ("DiscountType is mandatory");
if (DiscountType.length() > 1)
{
log.warning("Length > 1 - truncated");
DiscountType = DiscountType.substring(0,1);
}
set_Value ("DiscountType", DiscountType);
}
/** Get Discount Type.
Type of trade discount calculation */
public String getDiscountType() 
{
return (String)get_Value("DiscountType");
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
/** Set Business Partner Scope.
Business Partner Scope */
public void setIsBPartnerScope (boolean IsBPartnerScope)
{
set_Value ("IsBPartnerScope", new Boolean(IsBPartnerScope));
}
/** Get Business Partner Scope.
Business Partner Scope */
public boolean isBPartnerScope() 
{
Object oo = get_Value("IsBPartnerScope");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set General Scope.
General Scope */
public void setIsGeneralScope (boolean IsGeneralScope)
{
set_Value ("IsGeneralScope", new Boolean(IsGeneralScope));
}
/** Get General Scope.
General Scope */
public boolean isGeneralScope() 
{
Object oo = get_Value("IsGeneralScope");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Quantity based.
Trade discount break level based on Quantity (not value) */
public void setIsQuantityBased (boolean IsQuantityBased)
{
set_Value ("IsQuantityBased", new Boolean(IsQuantityBased));
}
/** Get Quantity based.
Trade discount break level based on Quantity (not value) */
public boolean isQuantityBased() 
{
Object oo = get_Value("IsQuantityBased");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set Script.
Dynamic Java Language Script to calculate result */
public void setScript (String Script)
{
if (Script != null && Script.length() > 2000)
{
log.warning("Length > 2000 - truncated");
Script = Script.substring(0,2000);
}
set_Value ("Script", Script);
}
/** Get Script.
Dynamic Java Language Script to calculate result */
public String getScript() 
{
return (String)get_Value("Script");
}
/** Set Valid from.
Valid from including this date (first day) */
public void setValidFrom (Timestamp ValidFrom)
{
if (ValidFrom == null) throw new IllegalArgumentException ("ValidFrom is mandatory");
set_Value ("ValidFrom", ValidFrom);
}
/** Get Valid from.
Valid from including this date (first day) */
public Timestamp getValidFrom() 
{
return (Timestamp)get_Value("ValidFrom");
}
/** Set Valid to.
Valid to including this date (last day) */
public void setValidTo (Timestamp ValidTo)
{
set_Value ("ValidTo", ValidTo);
}
/** Get Valid to.
Valid to including this date (last day) */
public Timestamp getValidTo() 
{
return (Timestamp)get_Value("ValidTo");
}
}
