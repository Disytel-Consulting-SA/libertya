/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AttributeSet
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2011-12-14 12:26:08.403 */
public class X_M_AttributeSet extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_AttributeSet (Properties ctx, int M_AttributeSet_ID, String trxName)
{
super (ctx, M_AttributeSet_ID, trxName);
/** if (M_AttributeSet_ID == 0)
{
setIsDueDate (false);
setIsGuaranteeDate (false);
setIsGuaranteeDateMandatory (false);
setIsInstanceAttribute (false);
setIsLot (false);
setIsLotMandatory (false);
setIsSerNo (false);
setIsSerNoMandatory (false);
setMandatoryType (null);
setM_AttributeSet_ID (0);
setName (null);
setUseProductRelationsLikeSerNoPrefix (false);
}
 */
}
/** Load Constructor */
public X_M_AttributeSet (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_AttributeSet");

/** TableName=M_AttributeSet */
public static final String Table_Name="M_AttributeSet";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_AttributeSet");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_AttributeSet[").append(getID()).append("]");
return sb.toString();
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
/** Set Due Date.
Date when the payment is due */
public void setDueDate (Timestamp DueDate)
{
set_Value ("DueDate", DueDate);
}
/** Get Due Date.
Date when the payment is due */
public Timestamp getDueDate() 
{
return (Timestamp)get_Value("DueDate");
}
/** Set Guarantee Days.
Number of days the product is guaranteed or available */
public void setGuaranteeDays (int GuaranteeDays)
{
set_Value ("GuaranteeDays", new Integer(GuaranteeDays));
}
/** Get Guarantee Days.
Number of days the product is guaranteed or available */
public int getGuaranteeDays() 
{
Integer ii = (Integer)get_Value("GuaranteeDays");
if (ii == null) return 0;
return ii.intValue();
}
/** Set IsDueDate */
public void setIsDueDate (boolean IsDueDate)
{
set_Value ("IsDueDate", new Boolean(IsDueDate));
}
/** Get IsDueDate */
public boolean isDueDate() 
{
Object oo = get_Value("IsDueDate");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Guarantee Date.
Product has Guarantee or Expiry Date */
public void setIsGuaranteeDate (boolean IsGuaranteeDate)
{
set_Value ("IsGuaranteeDate", new Boolean(IsGuaranteeDate));
}
/** Get Guarantee Date.
Product has Guarantee or Expiry Date */
public boolean isGuaranteeDate() 
{
Object oo = get_Value("IsGuaranteeDate");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Mandatory Guarantee Date.
The entry of a Guarantee Date is mandatory when creating a Product Instance */
public void setIsGuaranteeDateMandatory (boolean IsGuaranteeDateMandatory)
{
set_Value ("IsGuaranteeDateMandatory", new Boolean(IsGuaranteeDateMandatory));
}
/** Get Mandatory Guarantee Date.
The entry of a Guarantee Date is mandatory when creating a Product Instance */
public boolean isGuaranteeDateMandatory() 
{
Object oo = get_Value("IsGuaranteeDateMandatory");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Instance Attribute.
The product attribute is specific to the instance (like Serial No, Lot or Guarantee Date) */
public void setIsInstanceAttribute (boolean IsInstanceAttribute)
{
set_Value ("IsInstanceAttribute", new Boolean(IsInstanceAttribute));
}
/** Get Instance Attribute.
The product attribute is specific to the instance (like Serial No, Lot or Guarantee Date) */
public boolean isInstanceAttribute() 
{
Object oo = get_Value("IsInstanceAttribute");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Lot.
The product instances have a Lot Number */
public void setIsLot (boolean IsLot)
{
set_Value ("IsLot", new Boolean(IsLot));
}
/** Get Lot.
The product instances have a Lot Number */
public boolean isLot() 
{
Object oo = get_Value("IsLot");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Mandatory Lot.
The entry of Lot info is mandatory when creating a Product Instance */
public void setIsLotMandatory (boolean IsLotMandatory)
{
set_Value ("IsLotMandatory", new Boolean(IsLotMandatory));
}
/** Get Mandatory Lot.
The entry of Lot info is mandatory when creating a Product Instance */
public boolean isLotMandatory() 
{
Object oo = get_Value("IsLotMandatory");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set IsOrderInstanceAttribute */
public void setIsOrderInstanceAttribute (boolean IsOrderInstanceAttribute)
{
set_Value ("IsOrderInstanceAttribute", new Boolean(IsOrderInstanceAttribute));
}
/** Get IsOrderInstanceAttribute */
public boolean isOrderInstanceAttribute() 
{
Object oo = get_Value("IsOrderInstanceAttribute");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Serial No.
The product instances have Serial Numbers */
public void setIsSerNo (boolean IsSerNo)
{
set_Value ("IsSerNo", new Boolean(IsSerNo));
}
/** Get Serial No.
The product instances have Serial Numbers */
public boolean isSerNo() 
{
Object oo = get_Value("IsSerNo");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Mandatory Serial No.
The entry of a Serial No is mandatory when creating a Product Instance */
public void setIsSerNoMandatory (boolean IsSerNoMandatory)
{
set_Value ("IsSerNoMandatory", new Boolean(IsSerNoMandatory));
}
/** Get Mandatory Serial No.
The entry of a Serial No is mandatory when creating a Product Instance */
public boolean isSerNoMandatory() 
{
Object oo = get_Value("IsSerNoMandatory");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
public static final int MANDATORYTYPE_AD_Reference_ID = MReference.getReferenceID("M_AttributeSet MandatoryType");
/** Not Mandatary = N */
public static final String MANDATORYTYPE_NotMandatary = "N";
/** Always Mandatory = Y */
public static final String MANDATORYTYPE_AlwaysMandatory = "Y";
/** When Shipping = S */
public static final String MANDATORYTYPE_WhenShipping = "S";
/** Set Mandatory Type.
The specification of a Product Attribute Instance is mandatory */
public void setMandatoryType (String MandatoryType)
{
if (MandatoryType.equals("N") || MandatoryType.equals("Y") || MandatoryType.equals("S"));
 else throw new IllegalArgumentException ("MandatoryType Invalid value - Reference = MANDATORYTYPE_AD_Reference_ID - N - Y - S");
if (MandatoryType == null) throw new IllegalArgumentException ("MandatoryType is mandatory");
if (MandatoryType.length() > 1)
{
log.warning("Length > 1 - truncated");
MandatoryType = MandatoryType.substring(0,1);
}
set_Value ("MandatoryType", MandatoryType);
}
/** Get Mandatory Type.
The specification of a Product Attribute Instance is mandatory */
public String getMandatoryType() 
{
return (String)get_Value("MandatoryType");
}
/** Set Attribute Set.
Product Attribute Set */
public void setM_AttributeSet_ID (int M_AttributeSet_ID)
{
set_ValueNoCheck ("M_AttributeSet_ID", new Integer(M_AttributeSet_ID));
}
/** Get Attribute Set.
Product Attribute Set */
public int getM_AttributeSet_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSet_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Lot Control.
Product Lot Control */
public void setM_LotCtl_ID (int M_LotCtl_ID)
{
if (M_LotCtl_ID <= 0) set_Value ("M_LotCtl_ID", null);
 else 
set_Value ("M_LotCtl_ID", new Integer(M_LotCtl_ID));
}
/** Get Lot Control.
Product Lot Control */
public int getM_LotCtl_ID() 
{
Integer ii = (Integer)get_Value("M_LotCtl_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Serial No Control.
Product Serial Number Control */
public void setM_SerNoCtl_ID (int M_SerNoCtl_ID)
{
if (M_SerNoCtl_ID <= 0) set_Value ("M_SerNoCtl_ID", null);
 else 
set_Value ("M_SerNoCtl_ID", new Integer(M_SerNoCtl_ID));
}
/** Get Serial No Control.
Product Serial Number Control */
public int getM_SerNoCtl_ID() 
{
Integer ii = (Integer)get_Value("M_SerNoCtl_ID");
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
/** Set Use Product Relations Like SerNo Prefix.
Use Product Relations Value Like SerNo Prefix */
public void setUseProductRelationsLikeSerNoPrefix (boolean UseProductRelationsLikeSerNoPrefix)
{
set_Value ("UseProductRelationsLikeSerNoPrefix", new Boolean(UseProductRelationsLikeSerNoPrefix));
}
/** Get Use Product Relations Like SerNo Prefix.
Use Product Relations Value Like SerNo Prefix */
public boolean isUseProductRelationsLikeSerNoPrefix() 
{
Object oo = get_Value("UseProductRelationsLikeSerNoPrefix");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
}
