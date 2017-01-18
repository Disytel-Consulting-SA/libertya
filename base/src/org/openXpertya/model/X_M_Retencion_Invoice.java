/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por M_Retencion_Invoice
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-01-18 08:54:18.094 */
public class X_M_Retencion_Invoice extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_M_Retencion_Invoice (Properties ctx, int M_Retencion_Invoice_ID, String trxName)
{
super (ctx, M_Retencion_Invoice_ID, trxName);
/** if (M_Retencion_Invoice_ID == 0)
{
setC_Currency_ID (0);
setC_Invoice_ID (0);
setC_InvoiceLine_ID (0);
setC_Invoice_Retenc_ID (0);
setC_RetencionSchema_ID (0);
setM_Retencion_Invoice_ID (0);
}
 */
}
/** Load Constructor */
public X_M_Retencion_Invoice (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("M_Retencion_Invoice");

/** TableName=M_Retencion_Invoice */
public static final String Table_Name="M_Retencion_Invoice";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"M_Retencion_Invoice");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_M_Retencion_Invoice[").append(getID()).append("]");
return sb.toString();
}
/** Set Retencion Amount */
public void setamt_retenc (BigDecimal amt_retenc)
{
set_Value ("amt_retenc", amt_retenc);
}
/** Get Retencion Amount */
public BigDecimal getamt_retenc() 
{
BigDecimal bd = (BigDecimal)get_Value("amt_retenc");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Base Percent */
public void setbase_calculo_percent (BigDecimal base_calculo_percent)
{
set_Value ("base_calculo_percent", base_calculo_percent);
}
/** Get Base Percent */
public BigDecimal getbase_calculo_percent() 
{
BigDecimal bd = (BigDecimal)get_Value("base_calculo_percent");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Imponible Base Amount */
public void setbaseimponible_amt (BigDecimal baseimponible_amt)
{
set_Value ("baseimponible_amt", baseimponible_amt);
}
/** Get Imponible Base Amount */
public BigDecimal getbaseimponible_amt() 
{
BigDecimal bd = (BigDecimal)get_Value("baseimponible_amt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Allocation.
Payment allocation */
public void setC_AllocationHdr_ID (int C_AllocationHdr_ID)
{
if (C_AllocationHdr_ID <= 0) set_Value ("C_AllocationHdr_ID", null);
 else 
set_Value ("C_AllocationHdr_ID", new Integer(C_AllocationHdr_ID));
}
/** Get Allocation.
Payment allocation */
public int getC_AllocationHdr_ID() 
{
Integer ii = (Integer)get_Value("C_AllocationHdr_ID");
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
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
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
/** Set Invoice Line.
Invoice Detail Line */
public void setC_InvoiceLine_ID (int C_InvoiceLine_ID)
{
set_Value ("C_InvoiceLine_ID", new Integer(C_InvoiceLine_ID));
}
/** Get Invoice Line.
Invoice Detail Line */
public int getC_InvoiceLine_ID() 
{
Integer ii = (Integer)get_Value("C_InvoiceLine_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_INVOICE_RETENC_ID_AD_Reference_ID = MReference.getReferenceID("C_Invoice");
/** Set Retencion */
public void setC_Invoice_Retenc_ID (int C_Invoice_Retenc_ID)
{
set_Value ("C_Invoice_Retenc_ID", new Integer(C_Invoice_Retenc_ID));
}
/** Get Retencion */
public int getC_Invoice_Retenc_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_Retenc_ID");
if (ii == null) return 0;
return ii.intValue();
}
public static final int C_INVOICE_SRC_ID_AD_Reference_ID = MReference.getReferenceID("C_Invoice");
/** Set Source Invoice */
public void setC_Invoice_Src_ID (int C_Invoice_Src_ID)
{
if (C_Invoice_Src_ID <= 0) set_Value ("C_Invoice_Src_ID", null);
 else 
set_Value ("C_Invoice_Src_ID", new Integer(C_Invoice_Src_ID));
}
/** Get Source Invoice */
public int getC_Invoice_Src_ID() 
{
Integer ii = (Integer)get_Value("C_Invoice_Src_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Retencion Schema */
public void setC_RetencionSchema_ID (int C_RetencionSchema_ID)
{
set_Value ("C_RetencionSchema_ID", new Integer(C_RetencionSchema_ID));
}
/** Get Retencion Schema */
public int getC_RetencionSchema_ID() 
{
Integer ii = (Integer)get_Value("C_RetencionSchema_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Determined Amount */
public void setimporte_determinado_amt (BigDecimal importe_determinado_amt)
{
set_Value ("importe_determinado_amt", importe_determinado_amt);
}
/** Get Determined Amount */
public BigDecimal getimporte_determinado_amt() 
{
BigDecimal bd = (BigDecimal)get_Value("importe_determinado_amt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set No Imponible Amount */
public void setimporte_no_imponible_amt (BigDecimal importe_no_imponible_amt)
{
set_Value ("importe_no_imponible_amt", importe_no_imponible_amt);
}
/** Get No Imponible Amount */
public BigDecimal getimporte_no_imponible_amt() 
{
BigDecimal bd = (BigDecimal)get_Value("importe_no_imponible_amt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Sales Transaction.
This is a Sales Transaction */
public void setIsSOTrx (boolean IsSOTrx)
{
set_Value ("IsSOTrx", new Boolean(IsSOTrx));
}
/** Get Sales Transaction.
This is a Sales Transaction */
public boolean isSOTrx() 
{
Object oo = get_Value("IsSOTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Retencion */
public void setM_Retencion_Invoice_ID (int M_Retencion_Invoice_ID)
{
set_ValueNoCheck ("M_Retencion_Invoice_ID", new Integer(M_Retencion_Invoice_ID));
}
/** Get Retencion */
public int getM_Retencion_Invoice_ID() 
{
Integer ii = (Integer)get_Value("M_Retencion_Invoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Current Payment Amount */
public void setpago_actual_amt (BigDecimal pago_actual_amt)
{
set_Value ("pago_actual_amt", pago_actual_amt);
}
/** Get Current Payment Amount */
public BigDecimal getpago_actual_amt() 
{
BigDecimal bd = (BigDecimal)get_Value("pago_actual_amt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Acumuled Payments */
public void setpagos_ant_acumulados_amt (BigDecimal pagos_ant_acumulados_amt)
{
set_Value ("pagos_ant_acumulados_amt", pagos_ant_acumulados_amt);
}
/** Get Acumuled Payments */
public BigDecimal getpagos_ant_acumulados_amt() 
{
BigDecimal bd = (BigDecimal)get_Value("pagos_ant_acumulados_amt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Acumuled Rentencion */
public void setretenciones_ant_acumuladas_amt (BigDecimal retenciones_ant_acumuladas_amt)
{
set_Value ("retenciones_ant_acumuladas_amt", retenciones_ant_acumuladas_amt);
}
/** Get Acumuled Rentencion */
public BigDecimal getretenciones_ant_acumuladas_amt() 
{
BigDecimal bd = (BigDecimal)get_Value("retenciones_ant_acumuladas_amt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Pagos acumulados */
public void setretencion_percent (BigDecimal retencion_percent)
{
set_Value ("retencion_percent", retencion_percent);
}
/** Get Pagos acumulados */
public BigDecimal getretencion_percent() 
{
BigDecimal bd = (BigDecimal)get_Value("retencion_percent");
if (bd == null) return Env.ZERO;
return bd;
}
}
