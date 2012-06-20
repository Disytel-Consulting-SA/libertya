/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por E_ElectronicInvoiceLine
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-03-12 12:10:16.201 */
public class X_E_ElectronicInvoiceLine extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_E_ElectronicInvoiceLine (Properties ctx, int E_ElectronicInvoiceLine_ID, String trxName)
{
super (ctx, E_ElectronicInvoiceLine_ID, trxName);
/** if (E_ElectronicInvoiceLine_ID == 0)
{
setE_ElectronicInvoice_ID (0);
setE_ElectronicInvoiceLine_ID (0);
}
 */
}
/** Load Constructor */
public X_E_ElectronicInvoiceLine (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("E_ElectronicInvoiceLine");

/** TableName=E_ElectronicInvoiceLine */
public static final String Table_Name="E_ElectronicInvoiceLine";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"E_ElectronicInvoiceLine");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_E_ElectronicInvoiceLine[").append(getID()).append("]");
return sb.toString();
}
/** Set Diseno_Libre */
public void setDiseno_Libre (String Diseno_Libre)
{
if (Diseno_Libre != null && Diseno_Libre.length() > 200)
{
log.warning("Length > 200 - truncated");
Diseno_Libre = Diseno_Libre.substring(0,200);
}
set_Value ("Diseno_Libre", Diseno_Libre);
}
/** Get Diseno_Libre */
public String getDiseno_Libre() 
{
return (String)get_Value("Diseno_Libre");
}
/** Set E_ElectronicInvoice_ID */
public void setE_ElectronicInvoice_ID (int E_ElectronicInvoice_ID)
{
set_Value ("E_ElectronicInvoice_ID", new Integer(E_ElectronicInvoice_ID));
}
/** Get E_ElectronicInvoice_ID */
public int getE_ElectronicInvoice_ID() 
{
Integer ii = (Integer)get_Value("E_ElectronicInvoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set E_ElectronicInvoiceLine_ID */
public void setE_ElectronicInvoiceLine_ID (int E_ElectronicInvoiceLine_ID)
{
set_ValueNoCheck ("E_ElectronicInvoiceLine_ID", new Integer(E_ElectronicInvoiceLine_ID));
}
/** Get E_ElectronicInvoiceLine_ID */
public int getE_ElectronicInvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("E_ElectronicInvoiceLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Importe_Bonificacion */
public void setImporte_Bonificacion (BigDecimal Importe_Bonificacion)
{
set_Value ("Importe_Bonificacion", Importe_Bonificacion);
}
/** Get Importe_Bonificacion */
public BigDecimal getImporte_Bonificacion() 
{
BigDecimal bd = (BigDecimal)get_Value("Importe_Bonificacion");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Indica_Anulacion */
public void setIndica_Anulacion (String Indica_Anulacion)
{
if (Indica_Anulacion != null && Indica_Anulacion.length() > 1)
{
log.warning("Length > 1 - truncated");
Indica_Anulacion = Indica_Anulacion.substring(0,1);
}
set_Value ("Indica_Anulacion", Indica_Anulacion);
}
/** Get Indica_Anulacion */
public String getIndica_Anulacion() 
{
return (String)get_Value("Indica_Anulacion");
}
/** Set Tax exempt.
Business partner is exempt from tax */
public void setIsTaxExempt (boolean IsTaxExempt)
{
set_Value ("IsTaxExempt", new Boolean(IsTaxExempt));
}
/** Get Tax exempt.
Business partner is exempt from tax */
public boolean isTaxExempt() 
{
Object oo = get_Value("IsTaxExempt");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Line Amount.
Line Extended Amount (Quantity * Actual Price) without Freight and Charges */
public void setLineNetAmt (BigDecimal LineNetAmt)
{
set_Value ("LineNetAmt", LineNetAmt);
}
/** Get Line Amount.
Line Extended Amount (Quantity * Actual Price) without Freight and Charges */
public BigDecimal getLineNetAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineNetAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line Total.
Total line amount incl. Tax */
public void setLineTotalAmt (BigDecimal LineTotalAmt)
{
set_Value ("LineTotalAmt", LineTotalAmt);
}
/** Get Line Total.
Total line amount incl. Tax */
public BigDecimal getLineTotalAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineTotalAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Invoiced Quantity.
Invoiced Quantity */
public void setQtyInvoiced (BigDecimal QtyInvoiced)
{
set_Value ("QtyInvoiced", QtyInvoiced);
}
/** Get Invoiced Quantity.
Invoiced Quantity */
public BigDecimal getQtyInvoiced() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyInvoiced");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Rate.
Rate or Tax or Exchange */
public void setRate (BigDecimal Rate)
{
set_Value ("Rate", Rate);
}
/** Get Rate.
Rate or Tax or Exchange */
public BigDecimal getRate() 
{
BigDecimal bd = (BigDecimal)get_Value("Rate");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Unidad_Medida */
public void setUnidad_Medida (String Unidad_Medida)
{
if (Unidad_Medida != null && Unidad_Medida.length() > 2)
{
log.warning("Length > 2 - truncated");
Unidad_Medida = Unidad_Medida.substring(0,2);
}
set_Value ("Unidad_Medida", Unidad_Medida);
}
/** Get Unidad_Medida */
public String getUnidad_Medida() 
{
return (String)get_Value("Unidad_Medida");
}
}
