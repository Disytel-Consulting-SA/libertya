/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_AmortizationLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2012-01-04 18:21:23.053 */
public class X_M_AmortizationLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_AmortizationLine (Properties ctx, int M_AmortizationLine_ID, String trxName)
{
super (ctx, M_AmortizationLine_ID, trxName);
/** if (M_AmortizationLine_ID == 0)
{
setAltaAmt (Env.ZERO);
setAmortizationAmt (Env.ZERO);
setBajaAmt (Env.ZERO);
setEndPeriodAmortizationAmt (Env.ZERO);
setEndPeriodResidualAmt (Env.ZERO);
setIniPeriodAmortizationAmt (Env.ZERO);
setIniPeriodResidualAmt (Env.ZERO);
setM_Amortization_ID (0);
setM_AmortizationLine_ID (0);
setM_Product_ID (0);
setProcessed (false);
setQty (Env.ZERO);
setResidualAmt (Env.ZERO);
setTotalCost (Env.ZERO);
setUnitCost (Env.ZERO);
}
 */
}
/** Load Constructor */
public X_M_AmortizationLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_AmortizationLine");

/** TableName=M_AmortizationLine */
public static final String Table_Name="M_AmortizationLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_AmortizationLine");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_AmortizationLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Alta Amt */
public void setAltaAmt (BigDecimal AltaAmt)
{
if (AltaAmt == null) throw new IllegalArgumentException ("AltaAmt is mandatory");
set_Value ("AltaAmt", AltaAmt);
}
/** Get Alta Amt */
public BigDecimal getAltaAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("AltaAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Amortization Amount */
public void setAmortizationAmt (BigDecimal AmortizationAmt)
{
if (AmortizationAmt == null) throw new IllegalArgumentException ("AmortizationAmt is mandatory");
set_Value ("AmortizationAmt", AmortizationAmt);
}
/** Get Amortization Amount */
public BigDecimal getAmortizationAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("AmortizationAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Baja Amt */
public void setBajaAmt (BigDecimal BajaAmt)
{
if (BajaAmt == null) throw new IllegalArgumentException ("BajaAmt is mandatory");
set_Value ("BajaAmt", BajaAmt);
}
/** Get Baja Amt */
public BigDecimal getBajaAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("BajaAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 60)
{
log.warning("Length > 60 - truncated");
Description = Description.substring(0,60);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set End Period Amortization Amount */
public void setEndPeriodAmortizationAmt (BigDecimal EndPeriodAmortizationAmt)
{
if (EndPeriodAmortizationAmt == null) throw new IllegalArgumentException ("EndPeriodAmortizationAmt is mandatory");
set_Value ("EndPeriodAmortizationAmt", EndPeriodAmortizationAmt);
}
/** Get End Period Amortization Amount */
public BigDecimal getEndPeriodAmortizationAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("EndPeriodAmortizationAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set End Period Residual Amount */
public void setEndPeriodResidualAmt (BigDecimal EndPeriodResidualAmt)
{
if (EndPeriodResidualAmt == null) throw new IllegalArgumentException ("EndPeriodResidualAmt is mandatory");
set_Value ("EndPeriodResidualAmt", EndPeriodResidualAmt);
}
/** Get End Period Residual Amount */
public BigDecimal getEndPeriodResidualAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("EndPeriodResidualAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Initial Period Amortization Amount */
public void setIniPeriodAmortizationAmt (BigDecimal IniPeriodAmortizationAmt)
{
if (IniPeriodAmortizationAmt == null) throw new IllegalArgumentException ("IniPeriodAmortizationAmt is mandatory");
set_Value ("IniPeriodAmortizationAmt", IniPeriodAmortizationAmt);
}
/** Get Initial Period Amortization Amount */
public BigDecimal getIniPeriodAmortizationAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("IniPeriodAmortizationAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Initial Period Residual Amount */
public void setIniPeriodResidualAmt (BigDecimal IniPeriodResidualAmt)
{
if (IniPeriodResidualAmt == null) throw new IllegalArgumentException ("IniPeriodResidualAmt is mandatory");
set_Value ("IniPeriodResidualAmt", IniPeriodResidualAmt);
}
/** Get Initial Period Residual Amount */
public BigDecimal getIniPeriodResidualAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("IniPeriodResidualAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Amortization */
public void setM_Amortization_ID (int M_Amortization_ID)
{
set_Value ("M_Amortization_ID", new Integer(M_Amortization_ID));
}
/** Get Amortization */
public int getM_Amortization_ID() 
{
Integer ii = (Integer)get_Value("M_Amortization_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Amortization Line */
public void setM_AmortizationLine_ID (int M_AmortizationLine_ID)
{
set_ValueNoCheck ("M_AmortizationLine_ID", new Integer(M_AmortizationLine_ID));
}
/** Get Amortization Line */
public int getM_AmortizationLine_ID() 
{
Integer ii = (Integer)get_Value("M_AmortizationLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Attribute Set Instance.
Product Attribute Set Instance */
public void setM_AttributeSetInstance_ID (int M_AttributeSetInstance_ID)
{
if (M_AttributeSetInstance_ID <= 0) set_Value ("M_AttributeSetInstance_ID", null);
 else 
set_Value ("M_AttributeSetInstance_ID", new Integer(M_AttributeSetInstance_ID));
}
/** Get Attribute Set Instance.
Product Attribute Set Instance */
public int getM_AttributeSetInstance_ID() 
{
Integer ii = (Integer)get_Value("M_AttributeSetInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Product.
Product, Service, Item */
public void setM_Product_ID (int M_Product_ID)
{
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
/** Set Processed.
The document has been processed */
public void setProcessed (boolean Processed)
{
set_Value ("Processed", new Boolean(Processed));
}
/** Get Processed.
The document has been processed */
public boolean isProcessed() 
{
Object oo = get_Value("Processed");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Quantity.
Quantity */
public void setQty (BigDecimal Qty)
{
if (Qty == null) throw new IllegalArgumentException ("Qty is mandatory");
set_Value ("Qty", Qty);
}
/** Get Quantity.
Quantity */
public BigDecimal getQty() 
{
BigDecimal bd = (BigDecimal)get_Value("Qty");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Residual Amount */
public void setResidualAmt (BigDecimal ResidualAmt)
{
if (ResidualAmt == null) throw new IllegalArgumentException ("ResidualAmt is mandatory");
set_Value ("ResidualAmt", ResidualAmt);
}
/** Get Residual Amount */
public BigDecimal getResidualAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("ResidualAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Total Cost */
public void setTotalCost (BigDecimal TotalCost)
{
if (TotalCost == null) throw new IllegalArgumentException ("TotalCost is mandatory");
set_Value ("TotalCost", TotalCost);
}
/** Get Total Cost */
public BigDecimal getTotalCost() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalCost");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Unit Cost */
public void setUnitCost (BigDecimal UnitCost)
{
if (UnitCost == null) throw new IllegalArgumentException ("UnitCost is mandatory");
set_Value ("UnitCost", UnitCost);
}
/** Get Unit Cost */
public BigDecimal getUnitCost() 
{
BigDecimal bd = (BigDecimal)get_Value("UnitCost");
if (bd == null) return Env.ZERO;
return bd;
}
}
