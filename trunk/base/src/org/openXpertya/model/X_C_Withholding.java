/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por C_Withholding
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-01-30 16:12:04.211 */
public class X_C_Withholding extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_C_Withholding (Properties ctx, int C_Withholding_ID, String trxName)
{
super (ctx, C_Withholding_ID, trxName);
/** if (C_Withholding_ID == 0)
{
setBeneficiary (0);
setC_PaymentTerm_ID (0);
setC_Withholding_ID (0);
setIsPaidTo3Party (false);
setIsPercentWithholding (false);
setIsTaxProrated (false);
setIsTaxWithholding (false);
setName (null);
}
 */
}
/** Load Constructor */
public X_C_Withholding (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("C_Withholding");

/** TableName=C_Withholding */
public static final String Table_Name="C_Withholding";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"C_Withholding");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_C_Withholding[").append(getID()).append("]");
return sb.toString();
}
public static final int BENEFICIARY_AD_Reference_ID = MReference.getReferenceID("C_BPartner (No Summary)");
/** Set Beneficiary.
Business Partner to whom payment is made */
public void setBeneficiary (int Beneficiary)
{
set_Value ("Beneficiary", new Integer(Beneficiary));
}
/** Get Beneficiary.
Business Partner to whom payment is made */
public int getBeneficiary() 
{
Integer ii = (Integer)get_Value("Beneficiary");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Payment Term.
The terms for Payment of this transaction */
public void setC_PaymentTerm_ID (int C_PaymentTerm_ID)
{
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
/** Set Withholding.
Withholding type defined */
public void setC_Withholding_ID (int C_Withholding_ID)
{
set_ValueNoCheck ("C_Withholding_ID", new Integer(C_Withholding_ID));
}
/** Get Withholding.
Withholding type defined */
public int getC_Withholding_ID() 
{
Integer ii = (Integer)get_Value("C_Withholding_ID");
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
/** Set Fix amount.
Fix amounted amount to be levied or paid */
public void setFixAmt (BigDecimal FixAmt)
{
set_Value ("FixAmt", FixAmt);
}
/** Get Fix amount.
Fix amounted amount to be levied or paid */
public BigDecimal getFixAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FixAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Paid to third party.
Amount paid to someone other than the Business Partner */
public void setIsPaidTo3Party (boolean IsPaidTo3Party)
{
set_Value ("IsPaidTo3Party", new Boolean(IsPaidTo3Party));
}
/** Get Paid to third party.
Amount paid to someone other than the Business Partner */
public boolean isPaidTo3Party() 
{
Object oo = get_Value("IsPaidTo3Party");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Percent withholding.
Withholding amount is a percentage of the invoice amount */
public void setIsPercentWithholding (boolean IsPercentWithholding)
{
set_Value ("IsPercentWithholding", new Boolean(IsPercentWithholding));
}
/** Get Percent withholding.
Withholding amount is a percentage of the invoice amount */
public boolean isPercentWithholding() 
{
Object oo = get_Value("IsPercentWithholding");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Prorate tax.
Tax is Prorated */
public void setIsTaxProrated (boolean IsTaxProrated)
{
set_Value ("IsTaxProrated", new Boolean(IsTaxProrated));
}
/** Get Prorate tax.
Tax is Prorated */
public boolean isTaxProrated() 
{
Object oo = get_Value("IsTaxProrated");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Tax withholding.
This is a tax related withholding */
public void setIsTaxWithholding (boolean IsTaxWithholding)
{
set_Value ("IsTaxWithholding", new Boolean(IsTaxWithholding));
}
/** Get Tax withholding.
This is a tax related withholding */
public boolean isTaxWithholding() 
{
Object oo = get_Value("IsTaxWithholding");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Max Amount.
Maximum Amount in invoice currency */
public void setMaxAmt (BigDecimal MaxAmt)
{
set_Value ("MaxAmt", MaxAmt);
}
/** Get Max Amount.
Maximum Amount in invoice currency */
public BigDecimal getMaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("MaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Min Amount.
Minimum Amount in invoice currency */
public void setMinAmt (BigDecimal MinAmt)
{
set_Value ("MinAmt", MinAmt);
}
/** Get Min Amount.
Minimum Amount in invoice currency */
public BigDecimal getMinAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("MinAmt");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Percent.
Percentage */
public void setPercent (BigDecimal Percent)
{
set_Value ("Percent", Percent);
}
/** Get Percent.
Percentage */
public BigDecimal getPercent() 
{
BigDecimal bd = (BigDecimal)get_Value("Percent");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Threshold max.
Maximum gross amount for withholding calculation  (0=no limit) */
public void setThresholdMax (BigDecimal ThresholdMax)
{
set_Value ("ThresholdMax", ThresholdMax);
}
/** Get Threshold max.
Maximum gross amount for withholding calculation  (0=no limit) */
public BigDecimal getThresholdMax() 
{
BigDecimal bd = (BigDecimal)get_Value("ThresholdMax");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Threshold min.
Minimum gross amount for withholding calculation */
public void setThresholdmin (BigDecimal Thresholdmin)
{
set_Value ("Thresholdmin", Thresholdmin);
}
/** Get Threshold min.
Minimum gross amount for withholding calculation */
public BigDecimal getThresholdmin() 
{
BigDecimal bd = (BigDecimal)get_Value("Thresholdmin");
if (bd == null) return Env.ZERO;
return bd;
}
}
