/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_CabalPayments
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2019-12-18 13:50:25.186 */
public class X_I_CabalPayments extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_CabalPayments (Properties ctx, int I_CabalPayments_ID, String trxName)
{
super (ctx, I_CabalPayments_ID, trxName);
/** if (I_CabalPayments_ID == 0)
{
setI_CabalPayments_ID (0);
setI_IsImported (false);
}
 */
}
/** Load Constructor */
public X_I_CabalPayments (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_CabalPayments");

/** TableName=I_CabalPayments */
public static final String Table_Name="I_CabalPayments";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_CabalPayments");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_CabalPayments[").append(getID()).append("]");
return sb.toString();
}
/** Set archivo_id */
public void setarchivo_id (String archivo_id)
{
if (archivo_id != null && archivo_id.length() > 32)
{
log.warning("Length > 32 - truncated");
archivo_id = archivo_id.substring(0,32);
}
set_Value ("archivo_id", archivo_id);
}
/** Get archivo_id */
public String getarchivo_id() 
{
return (String)get_Value("archivo_id");
}
/** Set comercio */
public void setcomercio (String comercio)
{
if (comercio != null && comercio.length() > 32)
{
log.warning("Length > 32 - truncated");
comercio = comercio.substring(0,32);
}
set_Value ("comercio", comercio);
}
/** Get comercio */
public String getcomercio() 
{
return (String)get_Value("comercio");
}
/** Set costo_fin_cup */
public void setcosto_fin_cup (String costo_fin_cup)
{
if (costo_fin_cup != null && costo_fin_cup.length() > 32)
{
log.warning("Length > 32 - truncated");
costo_fin_cup = costo_fin_cup.substring(0,32);
}
set_Value ("costo_fin_cup", costo_fin_cup);
}
/** Get costo_fin_cup */
public String getcosto_fin_cup() 
{
return (String)get_Value("costo_fin_cup");
}
/** Set Fecha Pago */
public void setFecha_Pago (String Fecha_Pago)
{
if (Fecha_Pago != null && Fecha_Pago.length() > 32)
{
log.warning("Length > 32 - truncated");
Fecha_Pago = Fecha_Pago.substring(0,32);
}
set_Value ("Fecha_Pago", Fecha_Pago);
}
/** Get Fecha Pago */
public String getFecha_Pago() 
{
return (String)get_Value("Fecha_Pago");
}
/** Set hash_modelo */
public void sethash_modelo (String hash_modelo)
{
if (hash_modelo != null && hash_modelo.length() > 32)
{
log.warning("Length > 32 - truncated");
hash_modelo = hash_modelo.substring(0,32);
}
set_Value ("hash_modelo", hash_modelo);
}
/** Get hash_modelo */
public String gethash_modelo() 
{
return (String)get_Value("hash_modelo");
}
/** Set I_CabalPayments_ID */
public void setI_CabalPayments_ID (int I_CabalPayments_ID)
{
set_ValueNoCheck ("I_CabalPayments_ID", new Integer(I_CabalPayments_ID));
}
/** Get I_CabalPayments_ID */
public int getI_CabalPayments_ID() 
{
Integer ii = (Integer)get_Value("I_CabalPayments_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set id */
public void setid (String id)
{
if (id != null && id.length() > 32)
{
log.warning("Length > 32 - truncated");
id = id.substring(0,32);
}
set_Value ("id", id);
}
/** Get id */
public String getid() 
{
return (String)get_Value("id");
}
/** Set Import Error Message.
Messages generated from import process */
public void setI_ErrorMsg (String I_ErrorMsg)
{
if (I_ErrorMsg != null && I_ErrorMsg.length() > 2000)
{
log.warning("Length > 2000 - truncated");
I_ErrorMsg = I_ErrorMsg.substring(0,2000);
}
set_Value ("I_ErrorMsg", I_ErrorMsg);
}
/** Get Import Error Message.
Messages generated from import process */
public String getI_ErrorMsg() 
{
return (String)get_Value("I_ErrorMsg");
}
/** Set Imported.
Has this import been processed */
public void setI_IsImported (boolean I_IsImported)
{
set_Value ("I_IsImported", new Boolean(I_IsImported));
}
/** Get Imported.
Has this import been processed */
public boolean isI_IsImported() 
{
Object oo = get_Value("I_IsImported");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set importe_arancel */
public void setimporte_arancel (String importe_arancel)
{
if (importe_arancel != null && importe_arancel.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_arancel = importe_arancel.substring(0,32);
}
set_Value ("importe_arancel", importe_arancel);
}
/** Get importe_arancel */
public String getimporte_arancel() 
{
return (String)get_Value("importe_arancel");
}
/** Set importe_iva_arancel */
public void setimporte_iva_arancel (String importe_iva_arancel)
{
if (importe_iva_arancel != null && importe_iva_arancel.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_iva_arancel = importe_iva_arancel.substring(0,32);
}
set_Value ("importe_iva_arancel", importe_iva_arancel);
}
/** Get importe_iva_arancel */
public String getimporte_iva_arancel() 
{
return (String)get_Value("importe_iva_arancel");
}
/** Set importe_neto_final */
public void setimporte_neto_final (String importe_neto_final)
{
if (importe_neto_final != null && importe_neto_final.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_neto_final = importe_neto_final.substring(0,32);
}
set_Value ("importe_neto_final", importe_neto_final);
}
/** Get importe_neto_final */
public String getimporte_neto_final() 
{
return (String)get_Value("importe_neto_final");
}
/** Set importe_venta */
public void setimporte_venta (String importe_venta)
{
if (importe_venta != null && importe_venta.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_venta = importe_venta.substring(0,32);
}
set_Value ("importe_venta", importe_venta);
}
/** Get importe_venta */
public String getimporte_venta() 
{
return (String)get_Value("importe_venta");
}
/** Set iva_cf_alicuota_10_5 */
public void setiva_cf_alicuota_10_5 (String iva_cf_alicuota_10_5)
{
if (iva_cf_alicuota_10_5 != null && iva_cf_alicuota_10_5.length() > 32)
{
log.warning("Length > 32 - truncated");
iva_cf_alicuota_10_5 = iva_cf_alicuota_10_5.substring(0,32);
}
set_Value ("iva_cf_alicuota_10_5", iva_cf_alicuota_10_5);
}
/** Get iva_cf_alicuota_10_5 */
public String getiva_cf_alicuota_10_5() 
{
return (String)get_Value("iva_cf_alicuota_10_5");
}
/** Set moneda_pago */
public void setmoneda_pago (String moneda_pago)
{
if (moneda_pago != null && moneda_pago.length() > 32)
{
log.warning("Length > 32 - truncated");
moneda_pago = moneda_pago.substring(0,32);
}
set_Value ("moneda_pago", moneda_pago);
}
/** Get moneda_pago */
public String getmoneda_pago() 
{
return (String)get_Value("moneda_pago");
}
/** Set numero_comercio */
public void setnumero_comercio (String numero_comercio)
{
if (numero_comercio != null && numero_comercio.length() > 32)
{
log.warning("Length > 32 - truncated");
numero_comercio = numero_comercio.substring(0,32);
}
set_Value ("numero_comercio", numero_comercio);
}
/** Get numero_comercio */
public String getnumero_comercio() 
{
return (String)get_Value("numero_comercio");
}
/** Set numero_liquidacion */
public void setnumero_liquidacion (String numero_liquidacion)
{
if (numero_liquidacion != null && numero_liquidacion.length() > 32)
{
log.warning("Length > 32 - truncated");
numero_liquidacion = numero_liquidacion.substring(0,32);
}
set_Value ("numero_liquidacion", numero_liquidacion);
}
/** Get numero_liquidacion */
public String getnumero_liquidacion() 
{
return (String)get_Value("numero_liquidacion");
}
/** Set percepcion_rg_3337 */
public void setpercepcion_rg_3337 (String percepcion_rg_3337)
{
if (percepcion_rg_3337 != null && percepcion_rg_3337.length() > 32)
{
log.warning("Length > 32 - truncated");
percepcion_rg_3337 = percepcion_rg_3337.substring(0,32);
}
set_Value ("percepcion_rg_3337", percepcion_rg_3337);
}
/** Get percepcion_rg_3337 */
public String getpercepcion_rg_3337() 
{
return (String)get_Value("percepcion_rg_3337");
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
/** Set retencion_ganancias */
public void setretencion_ganancias (String retencion_ganancias)
{
if (retencion_ganancias != null && retencion_ganancias.length() > 32)
{
log.warning("Length > 32 - truncated");
retencion_ganancias = retencion_ganancias.substring(0,32);
}
set_Value ("retencion_ganancias", retencion_ganancias);
}
/** Get retencion_ganancias */
public String getretencion_ganancias() 
{
return (String)get_Value("retencion_ganancias");
}
/** Set retencion_ingresos_brutos */
public void setretencion_ingresos_brutos (String retencion_ingresos_brutos)
{
if (retencion_ingresos_brutos != null && retencion_ingresos_brutos.length() > 32)
{
log.warning("Length > 32 - truncated");
retencion_ingresos_brutos = retencion_ingresos_brutos.substring(0,32);
}
set_Value ("retencion_ingresos_brutos", retencion_ingresos_brutos);
}
/** Get retencion_ingresos_brutos */
public String getretencion_ingresos_brutos() 
{
return (String)get_Value("retencion_ingresos_brutos");
}
/** Set retencion_iva */
public void setretencion_iva (String retencion_iva)
{
if (retencion_iva != null && retencion_iva.length() > 32)
{
log.warning("Length > 32 - truncated");
retencion_iva = retencion_iva.substring(0,32);
}
set_Value ("retencion_iva", retencion_iva);
}
/** Get retencion_iva */
public String getretencion_iva() 
{
return (String)get_Value("retencion_iva");
}
/** Set revisado */
public void setrevisado (String revisado)
{
if (revisado != null && revisado.length() > 32)
{
log.warning("Length > 32 - truncated");
revisado = revisado.substring(0,32);
}
set_Value ("revisado", revisado);
}
/** Get revisado */
public String getrevisado() 
{
return (String)get_Value("revisado");
}
/** Set signo_importe_arancel */
public void setsigno_importe_arancel (String signo_importe_arancel)
{
if (signo_importe_arancel != null && signo_importe_arancel.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_importe_arancel = signo_importe_arancel.substring(0,32);
}
set_Value ("signo_importe_arancel", signo_importe_arancel);
}
/** Get signo_importe_arancel */
public String getsigno_importe_arancel() 
{
return (String)get_Value("signo_importe_arancel");
}
/** Set signo_importe_bruto */
public void setsigno_importe_bruto (String signo_importe_bruto)
{
if (signo_importe_bruto != null && signo_importe_bruto.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_importe_bruto = signo_importe_bruto.substring(0,32);
}
set_Value ("signo_importe_bruto", signo_importe_bruto);
}
/** Get signo_importe_bruto */
public String getsigno_importe_bruto() 
{
return (String)get_Value("signo_importe_bruto");
}
/** Set signo_importe_neto_final */
public void setsigno_importe_neto_final (String signo_importe_neto_final)
{
if (signo_importe_neto_final != null && signo_importe_neto_final.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_importe_neto_final = signo_importe_neto_final.substring(0,32);
}
set_Value ("signo_importe_neto_final", signo_importe_neto_final);
}
/** Get signo_importe_neto_final */
public String getsigno_importe_neto_final() 
{
return (String)get_Value("signo_importe_neto_final");
}
/** Set signo_iva_cf_alicuota_10_5 */
public void setsigno_iva_cf_alicuota_10_5 (String signo_iva_cf_alicuota_10_5)
{
if (signo_iva_cf_alicuota_10_5 != null && signo_iva_cf_alicuota_10_5.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_iva_cf_alicuota_10_5 = signo_iva_cf_alicuota_10_5.substring(0,32);
}
set_Value ("signo_iva_cf_alicuota_10_5", signo_iva_cf_alicuota_10_5);
}
/** Get signo_iva_cf_alicuota_10_5 */
public String getsigno_iva_cf_alicuota_10_5() 
{
return (String)get_Value("signo_iva_cf_alicuota_10_5");
}
/** Set signo_iva_sobre_arancel */
public void setsigno_iva_sobre_arancel (String signo_iva_sobre_arancel)
{
if (signo_iva_sobre_arancel != null && signo_iva_sobre_arancel.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_iva_sobre_arancel = signo_iva_sobre_arancel.substring(0,32);
}
set_Value ("signo_iva_sobre_arancel", signo_iva_sobre_arancel);
}
/** Get signo_iva_sobre_arancel */
public String getsigno_iva_sobre_arancel() 
{
return (String)get_Value("signo_iva_sobre_arancel");
}
/** Set signo_percepcion_3337 */
public void setsigno_percepcion_3337 (String signo_percepcion_3337)
{
if (signo_percepcion_3337 != null && signo_percepcion_3337.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_percepcion_3337 = signo_percepcion_3337.substring(0,32);
}
set_Value ("signo_percepcion_3337", signo_percepcion_3337);
}
/** Get signo_percepcion_3337 */
public String getsigno_percepcion_3337() 
{
return (String)get_Value("signo_percepcion_3337");
}
/** Set signo_retencion_ganancias */
public void setsigno_retencion_ganancias (String signo_retencion_ganancias)
{
if (signo_retencion_ganancias != null && signo_retencion_ganancias.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_retencion_ganancias = signo_retencion_ganancias.substring(0,32);
}
set_Value ("signo_retencion_ganancias", signo_retencion_ganancias);
}
/** Get signo_retencion_ganancias */
public String getsigno_retencion_ganancias() 
{
return (String)get_Value("signo_retencion_ganancias");
}
/** Set signo_retencion_ingresos_brutos */
public void setsigno_retencion_ingresos_brutos (String signo_retencion_ingresos_brutos)
{
if (signo_retencion_ingresos_brutos != null && signo_retencion_ingresos_brutos.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_retencion_ingresos_brutos = signo_retencion_ingresos_brutos.substring(0,32);
}
set_Value ("signo_retencion_ingresos_brutos", signo_retencion_ingresos_brutos);
}
/** Get signo_retencion_ingresos_brutos */
public String getsigno_retencion_ingresos_brutos() 
{
return (String)get_Value("signo_retencion_ingresos_brutos");
}
/** Set signo_retencion_iva */
public void setsigno_retencion_iva (String signo_retencion_iva)
{
if (signo_retencion_iva != null && signo_retencion_iva.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_retencion_iva = signo_retencion_iva.substring(0,32);
}
set_Value ("signo_retencion_iva", signo_retencion_iva);
}
/** Get signo_retencion_iva */
public String getsigno_retencion_iva() 
{
return (String)get_Value("signo_retencion_iva");
}
}
