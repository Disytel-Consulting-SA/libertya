/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_PriceList
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-12-09 21:11:58.317 */
public class X_M_PriceList extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_PriceList (Properties ctx, int M_PriceList_ID, String trxName)
{
super (ctx, M_PriceList_ID, trxName);
/** if (M_PriceList_ID == 0)
{
setC_Currency_ID (0);
setEnforcePriceLimit (false);
setIsDefault (false);
setIsPerceptionsIncluded (false);
setIsSOPriceList (false);
setIsTaxIncluded (false);
setM_PriceList_ID (0);
setName (null);
setPricePrecision (Env.ZERO);	// 2
}
 */
}
/** Load Constructor */
public X_M_PriceList (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_PriceList");

/** TableName=M_PriceList */
public static final String Table_Name="M_PriceList";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_PriceList");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_PriceList[").append(getID()).append("]");
return sb.toString();
}
public static final int BASEPRICELIST_ID_AD_Reference_ID = MReference.getReferenceID("M_PriceList");
/** Set Base Pricelist.
Pricelist to be used, if product not found on this pricelist */
public void setBasePriceList_ID (int BasePriceList_ID)
{
if (BasePriceList_ID <= 0) set_Value ("BasePriceList_ID", null);
 else 
set_Value ("BasePriceList_ID", new Integer(BasePriceList_ID));
}
/** Get Base Pricelist.
Pricelist to be used, if product not found on this pricelist */
public int getBasePriceList_ID() 
{
Integer ii = (Integer)get_Value("BasePriceList_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Currency.
The Currency for this record */
public void setC_Currency_ID (int C_Currency_ID)
{
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
/** Set Enforce price limit.
Do not allow prices below the limit price */
public void setEnforcePriceLimit (boolean EnforcePriceLimit)
{
set_Value ("EnforcePriceLimit", new Boolean(EnforcePriceLimit));
}
/** Get Enforce price limit.
Do not allow prices below the limit price */
public boolean isEnforcePriceLimit() 
{
Object oo = get_Value("EnforcePriceLimit");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Default.
Default value */
public void setIsDefault (boolean IsDefault)
{
set_Value ("IsDefault", new Boolean(IsDefault));
}
/** Get Default.
Default value */
public boolean isDefault() 
{
Object oo = get_Value("IsDefault");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Is Perceptions Included */
public void setIsPerceptionsIncluded (boolean IsPerceptionsIncluded)
{
set_Value ("IsPerceptionsIncluded", new Boolean(IsPerceptionsIncluded));
}
/** Get Is Perceptions Included */
public boolean isPerceptionsIncluded() 
{
Object oo = get_Value("IsPerceptionsIncluded");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sales Price list.
This is a Sales Price List */
public void setIsSOPriceList (boolean IsSOPriceList)
{
set_Value ("IsSOPriceList", new Boolean(IsSOPriceList));
}
/** Get Sales Price list.
This is a Sales Price List */
public boolean isSOPriceList() 
{
Object oo = get_Value("IsSOPriceList");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Price includes Tax.
Tax is included in the price  */
public void setIsTaxIncluded (boolean IsTaxIncluded)
{
set_Value ("IsTaxIncluded", new Boolean(IsTaxIncluded));
}
/** Get Price includes Tax.
Tax is included in the price  */
public boolean isTaxIncluded() 
{
Object oo = get_Value("IsTaxIncluded");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Price List.
Unique identifier of a Price List */
public void setM_PriceList_ID (int M_PriceList_ID)
{
set_ValueNoCheck ("M_PriceList_ID", new Integer(M_PriceList_ID));
}
/** Get Price List.
Unique identifier of a Price List */
public int getM_PriceList_ID() 
{
Integer ii = (Integer)get_Value("M_PriceList_ID");
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
/** Set Price Precision.
Precision (number of decimals) for the Price */
public void setPricePrecision (BigDecimal PricePrecision)
{
if (PricePrecision == null) throw new IllegalArgumentException ("PricePrecision is mandatory");
set_Value ("PricePrecision", PricePrecision);
}
/** Get Price Precision.
Precision (number of decimals) for the Price */
public BigDecimal getPricePrecision() 
{
BigDecimal bd = (BigDecimal)get_Value("PricePrecision");
if (bd == null) return Env.ZERO;
return bd;
}
}
