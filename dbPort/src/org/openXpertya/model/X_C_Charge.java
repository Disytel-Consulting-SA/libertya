/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Charge
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-01-13 14:26:07.709 */
public class X_C_Charge extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Charge (Properties ctx, int C_Charge_ID, String trxName)
{
super (ctx, C_Charge_ID, trxName);
/** if (C_Charge_ID == 0)
{
setC_Charge_ID (0);
setChargeAmt (Env.ZERO);
setChargeType (null);	// D
setC_TaxCategory_ID (0);
setIsSameCurrency (false);
setIsSameTax (false);
setIsTaxIncluded (false);	// N
setName (null);
}
 */
}
/** Load Constructor */
public X_C_Charge (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Charge");

/** TableName=C_Charge */
public static final String Table_Name="C_Charge";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Charge");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Charge[").append(getID()).append("]");
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
/** Set Charge.
Additional document charges */
public void setC_Charge_ID (int C_Charge_ID)
{
set_ValueNoCheck ("C_Charge_ID", new Integer(C_Charge_ID));
}
/** Get Charge.
Additional document charges */
public int getC_Charge_ID() 
{
Integer ii = (Integer)get_Value("C_Charge_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Charge amount.
Charge Amount */
public void setChargeAmt (BigDecimal ChargeAmt)
{
if (ChargeAmt == null) throw new IllegalArgumentException ("ChargeAmt is mandatory");
set_Value ("ChargeAmt", ChargeAmt);
}
/** Get Charge amount.
Charge Amount */
public BigDecimal getChargeAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ChargeAmt");
if (bd == null) return Env.ZERO;
return bd;
}
public static final int CHARGETYPE_AD_Reference_ID = MReference.getReferenceID("C_Charge Type");
/** Document Charge = D */
public static final String CHARGETYPE_DocumentCharge = "D";
/** Warehouse Settings = W */
public static final String CHARGETYPE_WarehouseSettings = "W";
/** Simple Material InOut = S */
public static final String CHARGETYPE_SimpleMaterialInOut = "S";
/** Warehouse Transfer = T */
public static final String CHARGETYPE_WarehouseTransfer = "T";
/** Two Phase Movement = M */
public static final String CHARGETYPE_TwoPhaseMovement = "M";
/** Set Charge Type.
Charge Type */
public void setChargeType (String ChargeType)
{
if (ChargeType.equals("D") || ChargeType.equals("W") || ChargeType.equals("S") || ChargeType.equals("T") || ChargeType.equals("M"));
 else throw new IllegalArgumentException ("ChargeType Invalid value - Reference = CHARGETYPE_AD_Reference_ID - D - W - S - T - M");
if (ChargeType == null) throw new IllegalArgumentException ("ChargeType is mandatory");
if (ChargeType.length() > 2)
{
log.warning("Length > 2 - truncated");
ChargeType = ChargeType.substring(0,2);
}
set_Value ("ChargeType", ChargeType);
}
/** Get Charge Type.
Charge Type */
public String getChargeType() 
{
return (String)get_Value("ChargeType");
}
/** Set Tax Category.
Tax Category */
public void setC_TaxCategory_ID (int C_TaxCategory_ID)
{
set_Value ("C_TaxCategory_ID", new Integer(C_TaxCategory_ID));
}
/** Get Tax Category.
Tax Category */
public int getC_TaxCategory_ID() 
{
Integer ii = (Integer)get_Value("C_TaxCategory_ID");
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
/** Set Same Currency */
public void setIsSameCurrency (boolean IsSameCurrency)
{
set_Value ("IsSameCurrency", new Boolean(IsSameCurrency));
}
/** Get Same Currency */
public boolean isSameCurrency() 
{
Object oo = get_Value("IsSameCurrency");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Same Tax.
Use the same tax as the main transaction */
public void setIsSameTax (boolean IsSameTax)
{
set_Value ("IsSameTax", new Boolean(IsSameTax));
}
/** Get Same Tax.
Use the same tax as the main transaction */
public boolean isSameTax() 
{
Object oo = get_Value("IsSameTax");
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
}
