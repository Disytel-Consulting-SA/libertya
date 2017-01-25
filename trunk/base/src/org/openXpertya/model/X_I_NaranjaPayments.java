/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_NaranjaPayments
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-01-25 11:08:42.434 */
public class X_I_NaranjaPayments extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_NaranjaPayments (Properties ctx, int I_NaranjaPayments_ID, String trxName)
{
super (ctx, I_NaranjaPayments_ID, trxName);
/** if (I_NaranjaPayments_ID == 0)
{
setI_IsImported (false);
setI_Naranjapayments_ID (0);
}
 */
}
/** Load Constructor */
public X_I_NaranjaPayments (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_NaranjaPayments");

/** TableName=I_NaranjaPayments */
public static final String Table_Name="I_NaranjaPayments";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_NaranjaPayments");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_NaranjaPayments[").append(getID()).append("]");
return sb.toString();
}
/** Set alicuota_ing_brutos */
public void setalicuota_ing_brutos (String alicuota_ing_brutos)
{
if (alicuota_ing_brutos != null && alicuota_ing_brutos.length() > 32)
{
log.warning("Length > 32 - truncated");
alicuota_ing_brutos = alicuota_ing_brutos.substring(0,32);
}
set_Value ("alicuota_ing_brutos", alicuota_ing_brutos);
}
/** Get alicuota_ing_brutos */
public String getalicuota_ing_brutos() 
{
return (String)get_Value("alicuota_ing_brutos");
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
/** Set base_imponible_ing_bru */
public void setbase_imponible_ing_bru (String base_imponible_ing_bru)
{
if (base_imponible_ing_bru != null && base_imponible_ing_bru.length() > 32)
{
log.warning("Length > 32 - truncated");
base_imponible_ing_bru = base_imponible_ing_bru.substring(0,32);
}
set_Value ("base_imponible_ing_bru", base_imponible_ing_bru);
}
/** Get base_imponible_ing_bru */
public String getbase_imponible_ing_bru() 
{
return (String)get_Value("base_imponible_ing_bru");
}
/** Set bines */
public void setbines (String bines)
{
if (bines != null && bines.length() > 32)
{
log.warning("Length > 32 - truncated");
bines = bines.substring(0,32);
}
set_Value ("bines", bines);
}
/** Get bines */
public String getbines() 
{
return (String)get_Value("bines");
}
/** Set codigo_aut */
public void setcodigo_aut (String codigo_aut)
{
if (codigo_aut != null && codigo_aut.length() > 32)
{
log.warning("Length > 32 - truncated");
codigo_aut = codigo_aut.substring(0,32);
}
set_Value ("codigo_aut", codigo_aut);
}
/** Get codigo_aut */
public String getcodigo_aut() 
{
return (String)get_Value("codigo_aut");
}
/** Set codigo_especial */
public void setcodigo_especial (String codigo_especial)
{
if (codigo_especial != null && codigo_especial.length() > 32)
{
log.warning("Length > 32 - truncated");
codigo_especial = codigo_especial.substring(0,32);
}
set_Value ("codigo_especial", codigo_especial);
}
/** Get codigo_especial */
public String getcodigo_especial() 
{
return (String)get_Value("codigo_especial");
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
/** Set compra */
public void setcompra (String compra)
{
if (compra != null && compra.length() > 32)
{
log.warning("Length > 32 - truncated");
compra = compra.substring(0,32);
}
set_Value ("compra", compra);
}
/** Get compra */
public String getcompra() 
{
return (String)get_Value("compra");
}
/** Set cuit_ag_retencion */
public void setcuit_ag_retencion (String cuit_ag_retencion)
{
if (cuit_ag_retencion != null && cuit_ag_retencion.length() > 32)
{
log.warning("Length > 32 - truncated");
cuit_ag_retencion = cuit_ag_retencion.substring(0,32);
}
set_Value ("cuit_ag_retencion", cuit_ag_retencion);
}
/** Get cuit_ag_retencion */
public String getcuit_ag_retencion() 
{
return (String)get_Value("cuit_ag_retencion");
}
/** Set cupon */
public void setcupon (String cupon)
{
if (cupon != null && cupon.length() > 32)
{
log.warning("Length > 32 - truncated");
cupon = cupon.substring(0,32);
}
set_Value ("cupon", cupon);
}
/** Get cupon */
public String getcupon() 
{
return (String)get_Value("cupon");
}
/** Set debitos_creditos */
public void setdebitos_creditos (String debitos_creditos)
{
if (debitos_creditos != null && debitos_creditos.length() > 32)
{
log.warning("Length > 32 - truncated");
debitos_creditos = debitos_creditos.substring(0,32);
}
set_Value ("debitos_creditos", debitos_creditos);
}
/** Get debitos_creditos */
public String getdebitos_creditos() 
{
return (String)get_Value("debitos_creditos");
}
/** Set Descripcion */
public void setDescripcion (String Descripcion)
{
if (Descripcion != null && Descripcion.length() > 32)
{
log.warning("Length > 32 - truncated");
Descripcion = Descripcion.substring(0,32);
}
set_Value ("Descripcion", Descripcion);
}
/** Get Descripcion */
public String getDescripcion() 
{
return (String)get_Value("Descripcion");
}
/** Set embargo_y_cesiones */
public void setembargo_y_cesiones (String embargo_y_cesiones)
{
if (embargo_y_cesiones != null && embargo_y_cesiones.length() > 32)
{
log.warning("Length > 32 - truncated");
embargo_y_cesiones = embargo_y_cesiones.substring(0,32);
}
set_Value ("embargo_y_cesiones", embargo_y_cesiones);
}
/** Get embargo_y_cesiones */
public String getembargo_y_cesiones() 
{
return (String)get_Value("embargo_y_cesiones");
}
/** Set entrega */
public void setentrega (String entrega)
{
if (entrega != null && entrega.length() > 32)
{
log.warning("Length > 32 - truncated");
entrega = entrega.substring(0,32);
}
set_Value ("entrega", entrega);
}
/** Get entrega */
public String getentrega() 
{
return (String)get_Value("entrega");
}
/** Set Estado */
public void setEstado (String Estado)
{
if (Estado != null && Estado.length() > 32)
{
log.warning("Length > 32 - truncated");
Estado = Estado.substring(0,32);
}
set_Value ("Estado", Estado);
}
/** Get Estado */
public String getEstado() 
{
return (String)get_Value("Estado");
}
/** Set fecha_compra */
public void setfecha_compra (String fecha_compra)
{
if (fecha_compra != null && fecha_compra.length() > 32)
{
log.warning("Length > 32 - truncated");
fecha_compra = fecha_compra.substring(0,32);
}
set_Value ("fecha_compra", fecha_compra);
}
/** Get fecha_compra */
public String getfecha_compra() 
{
return (String)get_Value("fecha_compra");
}
/** Set fecha_cuota */
public void setfecha_cuota (String fecha_cuota)
{
if (fecha_cuota != null && fecha_cuota.length() > 32)
{
log.warning("Length > 32 - truncated");
fecha_cuota = fecha_cuota.substring(0,32);
}
set_Value ("fecha_cuota", fecha_cuota);
}
/** Get fecha_cuota */
public String getfecha_cuota() 
{
return (String)get_Value("fecha_cuota");
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
/** Set fecha_presentacion */
public void setfecha_presentacion (String fecha_presentacion)
{
if (fecha_presentacion != null && fecha_presentacion.length() > 32)
{
log.warning("Length > 32 - truncated");
fecha_presentacion = fecha_presentacion.substring(0,32);
}
set_Value ("fecha_presentacion", fecha_presentacion);
}
/** Get fecha_presentacion */
public String getfecha_presentacion() 
{
return (String)get_Value("fecha_presentacion");
}
/** Set fecha_proceso */
public void setfecha_proceso (String fecha_proceso)
{
if (fecha_proceso != null && fecha_proceso.length() > 32)
{
log.warning("Length > 32 - truncated");
fecha_proceso = fecha_proceso.substring(0,32);
}
set_Value ("fecha_proceso", fecha_proceso);
}
/** Get fecha_proceso */
public String getfecha_proceso() 
{
return (String)get_Value("fecha_proceso");
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
/** Set imp_acre_liq_ant_facturado_120 */
public void setimp_acre_liq_ant_facturado_120 (String imp_acre_liq_ant_facturado_120)
{
if (imp_acre_liq_ant_facturado_120 != null && imp_acre_liq_ant_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_acre_liq_ant_facturado_120 = imp_acre_liq_ant_facturado_120.substring(0,32);
}
set_Value ("imp_acre_liq_ant_facturado_120", imp_acre_liq_ant_facturado_120);
}
/** Get imp_acre_liq_ant_facturado_120 */
public String getimp_acre_liq_ant_facturado_120() 
{
return (String)get_Value("imp_acre_liq_ant_facturado_120");
}
/** Set imp_acre_liq_ant_facturado_30 */
public void setimp_acre_liq_ant_facturado_30 (String imp_acre_liq_ant_facturado_30)
{
if (imp_acre_liq_ant_facturado_30 != null && imp_acre_liq_ant_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_acre_liq_ant_facturado_30 = imp_acre_liq_ant_facturado_30.substring(0,32);
}
set_Value ("imp_acre_liq_ant_facturado_30", imp_acre_liq_ant_facturado_30);
}
/** Get imp_acre_liq_ant_facturado_30 */
public String getimp_acre_liq_ant_facturado_30() 
{
return (String)get_Value("imp_acre_liq_ant_facturado_30");
}
/** Set imp_acre_liq_ant_facturado_60 */
public void setimp_acre_liq_ant_facturado_60 (String imp_acre_liq_ant_facturado_60)
{
if (imp_acre_liq_ant_facturado_60 != null && imp_acre_liq_ant_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_acre_liq_ant_facturado_60 = imp_acre_liq_ant_facturado_60.substring(0,32);
}
set_Value ("imp_acre_liq_ant_facturado_60", imp_acre_liq_ant_facturado_60);
}
/** Get imp_acre_liq_ant_facturado_60 */
public String getimp_acre_liq_ant_facturado_60() 
{
return (String)get_Value("imp_acre_liq_ant_facturado_60");
}
/** Set imp_acre_liq_ant_facturado_90 */
public void setimp_acre_liq_ant_facturado_90 (String imp_acre_liq_ant_facturado_90)
{
if (imp_acre_liq_ant_facturado_90 != null && imp_acre_liq_ant_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_acre_liq_ant_facturado_90 = imp_acre_liq_ant_facturado_90.substring(0,32);
}
set_Value ("imp_acre_liq_ant_facturado_90", imp_acre_liq_ant_facturado_90);
}
/** Get imp_acre_liq_ant_facturado_90 */
public String getimp_acre_liq_ant_facturado_90() 
{
return (String)get_Value("imp_acre_liq_ant_facturado_90");
}
/** Set imp_acre_liq_ant_vto */
public void setimp_acre_liq_ant_vto (String imp_acre_liq_ant_vto)
{
if (imp_acre_liq_ant_vto != null && imp_acre_liq_ant_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_acre_liq_ant_vto = imp_acre_liq_ant_vto.substring(0,32);
}
set_Value ("imp_acre_liq_ant_vto", imp_acre_liq_ant_vto);
}
/** Get imp_acre_liq_ant_vto */
public String getimp_acre_liq_ant_vto() 
{
return (String)get_Value("imp_acre_liq_ant_vto");
}
/** Set imp_int_plan_esp_facturado_120 */
public void setimp_int_plan_esp_facturado_120 (String imp_int_plan_esp_facturado_120)
{
if (imp_int_plan_esp_facturado_120 != null && imp_int_plan_esp_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_int_plan_esp_facturado_120 = imp_int_plan_esp_facturado_120.substring(0,32);
}
set_Value ("imp_int_plan_esp_facturado_120", imp_int_plan_esp_facturado_120);
}
/** Get imp_int_plan_esp_facturado_120 */
public String getimp_int_plan_esp_facturado_120() 
{
return (String)get_Value("imp_int_plan_esp_facturado_120");
}
/** Set imp_int_plan_esp_facturado_30 */
public void setimp_int_plan_esp_facturado_30 (String imp_int_plan_esp_facturado_30)
{
if (imp_int_plan_esp_facturado_30 != null && imp_int_plan_esp_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_int_plan_esp_facturado_30 = imp_int_plan_esp_facturado_30.substring(0,32);
}
set_Value ("imp_int_plan_esp_facturado_30", imp_int_plan_esp_facturado_30);
}
/** Get imp_int_plan_esp_facturado_30 */
public String getimp_int_plan_esp_facturado_30() 
{
return (String)get_Value("imp_int_plan_esp_facturado_30");
}
/** Set imp_int_plan_esp_facturado_60 */
public void setimp_int_plan_esp_facturado_60 (String imp_int_plan_esp_facturado_60)
{
if (imp_int_plan_esp_facturado_60 != null && imp_int_plan_esp_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_int_plan_esp_facturado_60 = imp_int_plan_esp_facturado_60.substring(0,32);
}
set_Value ("imp_int_plan_esp_facturado_60", imp_int_plan_esp_facturado_60);
}
/** Get imp_int_plan_esp_facturado_60 */
public String getimp_int_plan_esp_facturado_60() 
{
return (String)get_Value("imp_int_plan_esp_facturado_60");
}
/** Set imp_int_plan_esp_facturado_90 */
public void setimp_int_plan_esp_facturado_90 (String imp_int_plan_esp_facturado_90)
{
if (imp_int_plan_esp_facturado_90 != null && imp_int_plan_esp_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_int_plan_esp_facturado_90 = imp_int_plan_esp_facturado_90.substring(0,32);
}
set_Value ("imp_int_plan_esp_facturado_90", imp_int_plan_esp_facturado_90);
}
/** Get imp_int_plan_esp_facturado_90 */
public String getimp_int_plan_esp_facturado_90() 
{
return (String)get_Value("imp_int_plan_esp_facturado_90");
}
/** Set imp_int_plan_esp_vto */
public void setimp_int_plan_esp_vto (String imp_int_plan_esp_vto)
{
if (imp_int_plan_esp_vto != null && imp_int_plan_esp_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_int_plan_esp_vto = imp_int_plan_esp_vto.substring(0,32);
}
set_Value ("imp_int_plan_esp_vto", imp_int_plan_esp_vto);
}
/** Get imp_int_plan_esp_vto */
public String getimp_int_plan_esp_vto() 
{
return (String)get_Value("imp_int_plan_esp_vto");
}
/** Set imp_int_z_facturado_120 */
public void setimp_int_z_facturado_120 (String imp_int_z_facturado_120)
{
if (imp_int_z_facturado_120 != null && imp_int_z_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_int_z_facturado_120 = imp_int_z_facturado_120.substring(0,32);
}
set_Value ("imp_int_z_facturado_120", imp_int_z_facturado_120);
}
/** Get imp_int_z_facturado_120 */
public String getimp_int_z_facturado_120() 
{
return (String)get_Value("imp_int_z_facturado_120");
}
/** Set imp_int_z_facturado_30 */
public void setimp_int_z_facturado_30 (String imp_int_z_facturado_30)
{
if (imp_int_z_facturado_30 != null && imp_int_z_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_int_z_facturado_30 = imp_int_z_facturado_30.substring(0,32);
}
set_Value ("imp_int_z_facturado_30", imp_int_z_facturado_30);
}
/** Get imp_int_z_facturado_30 */
public String getimp_int_z_facturado_30() 
{
return (String)get_Value("imp_int_z_facturado_30");
}
/** Set imp_int_z_facturado_60 */
public void setimp_int_z_facturado_60 (String imp_int_z_facturado_60)
{
if (imp_int_z_facturado_60 != null && imp_int_z_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_int_z_facturado_60 = imp_int_z_facturado_60.substring(0,32);
}
set_Value ("imp_int_z_facturado_60", imp_int_z_facturado_60);
}
/** Get imp_int_z_facturado_60 */
public String getimp_int_z_facturado_60() 
{
return (String)get_Value("imp_int_z_facturado_60");
}
/** Set imp_int_z_facturado_90 */
public void setimp_int_z_facturado_90 (String imp_int_z_facturado_90)
{
if (imp_int_z_facturado_90 != null && imp_int_z_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_int_z_facturado_90 = imp_int_z_facturado_90.substring(0,32);
}
set_Value ("imp_int_z_facturado_90", imp_int_z_facturado_90);
}
/** Get imp_int_z_facturado_90 */
public String getimp_int_z_facturado_90() 
{
return (String)get_Value("imp_int_z_facturado_90");
}
/** Set imp_int_z_vto */
public void setimp_int_z_vto (String imp_int_z_vto)
{
if (imp_int_z_vto != null && imp_int_z_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_int_z_vto = imp_int_z_vto.substring(0,32);
}
set_Value ("imp_int_z_vto", imp_int_z_vto);
}
/** Get imp_int_z_vto */
public String getimp_int_z_vto() 
{
return (String)get_Value("imp_int_z_vto");
}
/** Set imp_iva_21_facturado_120 */
public void setimp_iva_21_facturado_120 (String imp_iva_21_facturado_120)
{
if (imp_iva_21_facturado_120 != null && imp_iva_21_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_iva_21_facturado_120 = imp_iva_21_facturado_120.substring(0,32);
}
set_Value ("imp_iva_21_facturado_120", imp_iva_21_facturado_120);
}
/** Get imp_iva_21_facturado_120 */
public String getimp_iva_21_facturado_120() 
{
return (String)get_Value("imp_iva_21_facturado_120");
}
/** Set imp_iva_21_facturado_30 */
public void setimp_iva_21_facturado_30 (String imp_iva_21_facturado_30)
{
if (imp_iva_21_facturado_30 != null && imp_iva_21_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_iva_21_facturado_30 = imp_iva_21_facturado_30.substring(0,32);
}
set_Value ("imp_iva_21_facturado_30", imp_iva_21_facturado_30);
}
/** Get imp_iva_21_facturado_30 */
public String getimp_iva_21_facturado_30() 
{
return (String)get_Value("imp_iva_21_facturado_30");
}
/** Set imp_iva_21_facturado_60 */
public void setimp_iva_21_facturado_60 (String imp_iva_21_facturado_60)
{
if (imp_iva_21_facturado_60 != null && imp_iva_21_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_iva_21_facturado_60 = imp_iva_21_facturado_60.substring(0,32);
}
set_Value ("imp_iva_21_facturado_60", imp_iva_21_facturado_60);
}
/** Get imp_iva_21_facturado_60 */
public String getimp_iva_21_facturado_60() 
{
return (String)get_Value("imp_iva_21_facturado_60");
}
/** Set imp_iva_21_facturado_90 */
public void setimp_iva_21_facturado_90 (String imp_iva_21_facturado_90)
{
if (imp_iva_21_facturado_90 != null && imp_iva_21_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_iva_21_facturado_90 = imp_iva_21_facturado_90.substring(0,32);
}
set_Value ("imp_iva_21_facturado_90", imp_iva_21_facturado_90);
}
/** Get imp_iva_21_facturado_90 */
public String getimp_iva_21_facturado_90() 
{
return (String)get_Value("imp_iva_21_facturado_90");
}
/** Set imp_iva_21_vto */
public void setimp_iva_21_vto (String imp_iva_21_vto)
{
if (imp_iva_21_vto != null && imp_iva_21_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_iva_21_vto = imp_iva_21_vto.substring(0,32);
}
set_Value ("imp_iva_21_vto", imp_iva_21_vto);
}
/** Get imp_iva_21_vto */
public String getimp_iva_21_vto() 
{
return (String)get_Value("imp_iva_21_vto");
}
/** Set importe_ara_facturado_120 */
public void setimporte_ara_facturado_120 (String importe_ara_facturado_120)
{
if (importe_ara_facturado_120 != null && importe_ara_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_ara_facturado_120 = importe_ara_facturado_120.substring(0,32);
}
set_Value ("importe_ara_facturado_120", importe_ara_facturado_120);
}
/** Get importe_ara_facturado_120 */
public String getimporte_ara_facturado_120() 
{
return (String)get_Value("importe_ara_facturado_120");
}
/** Set importe_ara_facturado_30 */
public void setimporte_ara_facturado_30 (String importe_ara_facturado_30)
{
if (importe_ara_facturado_30 != null && importe_ara_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_ara_facturado_30 = importe_ara_facturado_30.substring(0,32);
}
set_Value ("importe_ara_facturado_30", importe_ara_facturado_30);
}
/** Get importe_ara_facturado_30 */
public String getimporte_ara_facturado_30() 
{
return (String)get_Value("importe_ara_facturado_30");
}
/** Set importe_ara_facturado_60 */
public void setimporte_ara_facturado_60 (String importe_ara_facturado_60)
{
if (importe_ara_facturado_60 != null && importe_ara_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_ara_facturado_60 = importe_ara_facturado_60.substring(0,32);
}
set_Value ("importe_ara_facturado_60", importe_ara_facturado_60);
}
/** Get importe_ara_facturado_60 */
public String getimporte_ara_facturado_60() 
{
return (String)get_Value("importe_ara_facturado_60");
}
/** Set importe_ara_facturado_90 */
public void setimporte_ara_facturado_90 (String importe_ara_facturado_90)
{
if (importe_ara_facturado_90 != null && importe_ara_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_ara_facturado_90 = importe_ara_facturado_90.substring(0,32);
}
set_Value ("importe_ara_facturado_90", importe_ara_facturado_90);
}
/** Get importe_ara_facturado_90 */
public String getimporte_ara_facturado_90() 
{
return (String)get_Value("importe_ara_facturado_90");
}
/** Set importe_ara_vto */
public void setimporte_ara_vto (String importe_ara_vto)
{
if (importe_ara_vto != null && importe_ara_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_ara_vto = importe_ara_vto.substring(0,32);
}
set_Value ("importe_ara_vto", importe_ara_vto);
}
/** Get importe_ara_vto */
public String getimporte_ara_vto() 
{
return (String)get_Value("importe_ara_vto");
}
/** Set importe_cuota */
public void setimporte_cuota (String importe_cuota)
{
if (importe_cuota != null && importe_cuota.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_cuota = importe_cuota.substring(0,32);
}
set_Value ("importe_cuota", importe_cuota);
}
/** Get importe_cuota */
public String getimporte_cuota() 
{
return (String)get_Value("importe_cuota");
}
/** Set importe_total_anticipos */
public void setimporte_total_anticipos (String importe_total_anticipos)
{
if (importe_total_anticipos != null && importe_total_anticipos.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_total_anticipos = importe_total_anticipos.substring(0,32);
}
set_Value ("importe_total_anticipos", importe_total_anticipos);
}
/** Get importe_total_anticipos */
public String getimporte_total_anticipos() 
{
return (String)get_Value("importe_total_anticipos");
}
/** Set importe_total_cheque_diferido */
public void setimporte_total_cheque_diferido (String importe_total_cheque_diferido)
{
if (importe_total_cheque_diferido != null && importe_total_cheque_diferido.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_total_cheque_diferido = importe_total_cheque_diferido.substring(0,32);
}
set_Value ("importe_total_cheque_diferido", importe_total_cheque_diferido);
}
/** Get importe_total_cheque_diferido */
public String getimporte_total_cheque_diferido() 
{
return (String)get_Value("importe_total_cheque_diferido");
}
/** Set importe_total_creditos */
public void setimporte_total_creditos (String importe_total_creditos)
{
if (importe_total_creditos != null && importe_total_creditos.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_total_creditos = importe_total_creditos.substring(0,32);
}
set_Value ("importe_total_creditos", importe_total_creditos);
}
/** Get importe_total_creditos */
public String getimporte_total_creditos() 
{
return (String)get_Value("importe_total_creditos");
}
/** Set importe_total_otros_debitos */
public void setimporte_total_otros_debitos (String importe_total_otros_debitos)
{
if (importe_total_otros_debitos != null && importe_total_otros_debitos.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_total_otros_debitos = importe_total_otros_debitos.substring(0,32);
}
set_Value ("importe_total_otros_debitos", importe_total_otros_debitos);
}
/** Get importe_total_otros_debitos */
public String getimporte_total_otros_debitos() 
{
return (String)get_Value("importe_total_otros_debitos");
}
/** Set imp_perc_iibb_facturado_120 */
public void setimp_perc_iibb_facturado_120 (String imp_perc_iibb_facturado_120)
{
if (imp_perc_iibb_facturado_120 != null && imp_perc_iibb_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_perc_iibb_facturado_120 = imp_perc_iibb_facturado_120.substring(0,32);
}
set_Value ("imp_perc_iibb_facturado_120", imp_perc_iibb_facturado_120);
}
/** Get imp_perc_iibb_facturado_120 */
public String getimp_perc_iibb_facturado_120() 
{
return (String)get_Value("imp_perc_iibb_facturado_120");
}
/** Set imp_perc_iibb_facturado_30 */
public void setimp_perc_iibb_facturado_30 (String imp_perc_iibb_facturado_30)
{
if (imp_perc_iibb_facturado_30 != null && imp_perc_iibb_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_perc_iibb_facturado_30 = imp_perc_iibb_facturado_30.substring(0,32);
}
set_Value ("imp_perc_iibb_facturado_30", imp_perc_iibb_facturado_30);
}
/** Get imp_perc_iibb_facturado_30 */
public String getimp_perc_iibb_facturado_30() 
{
return (String)get_Value("imp_perc_iibb_facturado_30");
}
/** Set imp_perc_iibb_facturado_60 */
public void setimp_perc_iibb_facturado_60 (String imp_perc_iibb_facturado_60)
{
if (imp_perc_iibb_facturado_60 != null && imp_perc_iibb_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_perc_iibb_facturado_60 = imp_perc_iibb_facturado_60.substring(0,32);
}
set_Value ("imp_perc_iibb_facturado_60", imp_perc_iibb_facturado_60);
}
/** Get imp_perc_iibb_facturado_60 */
public String getimp_perc_iibb_facturado_60() 
{
return (String)get_Value("imp_perc_iibb_facturado_60");
}
/** Set imp_perc_iibb_facturado_90 */
public void setimp_perc_iibb_facturado_90 (String imp_perc_iibb_facturado_90)
{
if (imp_perc_iibb_facturado_90 != null && imp_perc_iibb_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_perc_iibb_facturado_90 = imp_perc_iibb_facturado_90.substring(0,32);
}
set_Value ("imp_perc_iibb_facturado_90", imp_perc_iibb_facturado_90);
}
/** Get imp_perc_iibb_facturado_90 */
public String getimp_perc_iibb_facturado_90() 
{
return (String)get_Value("imp_perc_iibb_facturado_90");
}
/** Set imp_perc_iibb_vto */
public void setimp_perc_iibb_vto (String imp_perc_iibb_vto)
{
if (imp_perc_iibb_vto != null && imp_perc_iibb_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_perc_iibb_vto = imp_perc_iibb_vto.substring(0,32);
}
set_Value ("imp_perc_iibb_vto", imp_perc_iibb_vto);
}
/** Get imp_perc_iibb_vto */
public String getimp_perc_iibb_vto() 
{
return (String)get_Value("imp_perc_iibb_vto");
}
/** Set imp_perc_iva_facturado_120 */
public void setimp_perc_iva_facturado_120 (String imp_perc_iva_facturado_120)
{
if (imp_perc_iva_facturado_120 != null && imp_perc_iva_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_perc_iva_facturado_120 = imp_perc_iva_facturado_120.substring(0,32);
}
set_Value ("imp_perc_iva_facturado_120", imp_perc_iva_facturado_120);
}
/** Get imp_perc_iva_facturado_120 */
public String getimp_perc_iva_facturado_120() 
{
return (String)get_Value("imp_perc_iva_facturado_120");
}
/** Set imp_perc_iva_facturado_30 */
public void setimp_perc_iva_facturado_30 (String imp_perc_iva_facturado_30)
{
if (imp_perc_iva_facturado_30 != null && imp_perc_iva_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_perc_iva_facturado_30 = imp_perc_iva_facturado_30.substring(0,32);
}
set_Value ("imp_perc_iva_facturado_30", imp_perc_iva_facturado_30);
}
/** Get imp_perc_iva_facturado_30 */
public String getimp_perc_iva_facturado_30() 
{
return (String)get_Value("imp_perc_iva_facturado_30");
}
/** Set imp_perc_iva_facturado_60 */
public void setimp_perc_iva_facturado_60 (String imp_perc_iva_facturado_60)
{
if (imp_perc_iva_facturado_60 != null && imp_perc_iva_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_perc_iva_facturado_60 = imp_perc_iva_facturado_60.substring(0,32);
}
set_Value ("imp_perc_iva_facturado_60", imp_perc_iva_facturado_60);
}
/** Get imp_perc_iva_facturado_60 */
public String getimp_perc_iva_facturado_60() 
{
return (String)get_Value("imp_perc_iva_facturado_60");
}
/** Set imp_perc_iva_facturado_90 */
public void setimp_perc_iva_facturado_90 (String imp_perc_iva_facturado_90)
{
if (imp_perc_iva_facturado_90 != null && imp_perc_iva_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_perc_iva_facturado_90 = imp_perc_iva_facturado_90.substring(0,32);
}
set_Value ("imp_perc_iva_facturado_90", imp_perc_iva_facturado_90);
}
/** Get imp_perc_iva_facturado_90 */
public String getimp_perc_iva_facturado_90() 
{
return (String)get_Value("imp_perc_iva_facturado_90");
}
/** Set imp_perc_iva_vto */
public void setimp_perc_iva_vto (String imp_perc_iva_vto)
{
if (imp_perc_iva_vto != null && imp_perc_iva_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_perc_iva_vto = imp_perc_iva_vto.substring(0,32);
}
set_Value ("imp_perc_iva_vto", imp_perc_iva_vto);
}
/** Get imp_perc_iva_vto */
public String getimp_perc_iva_vto() 
{
return (String)get_Value("imp_perc_iva_vto");
}
/** Set I_Naranjapayments_ID */
public void setI_Naranjapayments_ID (int I_Naranjapayments_ID)
{
set_ValueNoCheck ("I_Naranjapayments_ID", new Integer(I_Naranjapayments_ID));
}
/** Get I_Naranjapayments_ID */
public int getI_Naranjapayments_ID() 
{
Integer ii = (Integer)get_Value("I_Naranjapayments_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set liq_negativa_dia_ant */
public void setliq_negativa_dia_ant (String liq_negativa_dia_ant)
{
if (liq_negativa_dia_ant != null && liq_negativa_dia_ant.length() > 32)
{
log.warning("Length > 32 - truncated");
liq_negativa_dia_ant = liq_negativa_dia_ant.substring(0,32);
}
set_Value ("liq_negativa_dia_ant", liq_negativa_dia_ant);
}
/** Get liq_negativa_dia_ant */
public String getliq_negativa_dia_ant() 
{
return (String)get_Value("liq_negativa_dia_ant");
}
/** Set marca */
public void setmarca (String marca)
{
if (marca != null && marca.length() > 32)
{
log.warning("Length > 32 - truncated");
marca = marca.substring(0,32);
}
set_Value ("marca", marca);
}
/** Get marca */
public String getmarca() 
{
return (String)get_Value("marca");
}
/** Set Moneda */
public void setMoneda (String Moneda)
{
if (Moneda != null && Moneda.length() > 32)
{
log.warning("Length > 32 - truncated");
Moneda = Moneda.substring(0,32);
}
set_Value ("Moneda", Moneda);
}
/** Get Moneda */
public String getMoneda() 
{
return (String)get_Value("Moneda");
}
/** Set Neto */
public void setneto (String neto)
{
if (neto != null && neto.length() > 32)
{
log.warning("Length > 32 - truncated");
neto = neto.substring(0,32);
}
set_Value ("neto", neto);
}
/** Get Neto */
public String getneto() 
{
return (String)get_Value("neto");
}
/** Set nro_debito */
public void setnro_debito (String nro_debito)
{
if (nro_debito != null && nro_debito.length() > 32)
{
log.warning("Length > 32 - truncated");
nro_debito = nro_debito.substring(0,32);
}
set_Value ("nro_debito", nro_debito);
}
/** Get nro_debito */
public String getnro_debito() 
{
return (String)get_Value("nro_debito");
}
/** Set nro_liquidacion */
public void setnro_liquidacion (String nro_liquidacion)
{
if (nro_liquidacion != null && nro_liquidacion.length() > 32)
{
log.warning("Length > 32 - truncated");
nro_liquidacion = nro_liquidacion.substring(0,32);
}
set_Value ("nro_liquidacion", nro_liquidacion);
}
/** Get nro_liquidacion */
public String getnro_liquidacion() 
{
return (String)get_Value("nro_liquidacion");
}
/** Set nro_lote */
public void setnro_lote (String nro_lote)
{
if (nro_lote != null && nro_lote.length() > 32)
{
log.warning("Length > 32 - truncated");
nro_lote = nro_lote.substring(0,32);
}
set_Value ("nro_lote", nro_lote);
}
/** Get nro_lote */
public String getnro_lote() 
{
return (String)get_Value("nro_lote");
}
/** Set nro_terminal */
public void setnro_terminal (String nro_terminal)
{
if (nro_terminal != null && nro_terminal.length() > 32)
{
log.warning("Length > 32 - truncated");
nro_terminal = nro_terminal.substring(0,32);
}
set_Value ("nro_terminal", nro_terminal);
}
/** Get nro_terminal */
public String getnro_terminal() 
{
return (String)get_Value("nro_terminal");
}
/** Set num_cliente */
public void setnum_cliente (String num_cliente)
{
if (num_cliente != null && num_cliente.length() > 32)
{
log.warning("Length > 32 - truncated");
num_cliente = num_cliente.substring(0,32);
}
set_Value ("num_cliente", num_cliente);
}
/** Get num_cliente */
public String getnum_cliente() 
{
return (String)get_Value("num_cliente");
}
/** Set numero_cuota */
public void setnumero_cuota (String numero_cuota)
{
if (numero_cuota != null && numero_cuota.length() > 32)
{
log.warning("Length > 32 - truncated");
numero_cuota = numero_cuota.substring(0,32);
}
set_Value ("numero_cuota", numero_cuota);
}
/** Get numero_cuota */
public String getnumero_cuota() 
{
return (String)get_Value("numero_cuota");
}
/** Set numero_devolucion */
public void setnumero_devolucion (String numero_devolucion)
{
if (numero_devolucion != null && numero_devolucion.length() > 32)
{
log.warning("Length > 32 - truncated");
numero_devolucion = numero_devolucion.substring(0,32);
}
set_Value ("numero_devolucion", numero_devolucion);
}
/** Get numero_devolucion */
public String getnumero_devolucion() 
{
return (String)get_Value("numero_devolucion");
}
/** Set numero_recap */
public void setnumero_recap (String numero_recap)
{
if (numero_recap != null && numero_recap.length() > 32)
{
log.warning("Length > 32 - truncated");
numero_recap = numero_recap.substring(0,32);
}
set_Value ("numero_recap", numero_recap);
}
/** Get numero_recap */
public String getnumero_recap() 
{
return (String)get_Value("numero_recap");
}
/** Set percepcion_1135 */
public void setpercepcion_1135 (String percepcion_1135)
{
if (percepcion_1135 != null && percepcion_1135.length() > 32)
{
log.warning("Length > 32 - truncated");
percepcion_1135 = percepcion_1135.substring(0,32);
}
set_Value ("percepcion_1135", percepcion_1135);
}
/** Get percepcion_1135 */
public String getpercepcion_1135() 
{
return (String)get_Value("percepcion_1135");
}
/** Set plan */
public void setplan (String plan)
{
if (plan != null && plan.length() > 32)
{
log.warning("Length > 32 - truncated");
plan = plan.substring(0,32);
}
set_Value ("plan", plan);
}
/** Get plan */
public String getplan() 
{
return (String)get_Value("plan");
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
/** Set retencion_3130 */
public void setretencion_3130 (String retencion_3130)
{
if (retencion_3130 != null && retencion_3130.length() > 32)
{
log.warning("Length > 32 - truncated");
retencion_3130 = retencion_3130.substring(0,32);
}
set_Value ("retencion_3130", retencion_3130);
}
/** Get retencion_3130 */
public String getretencion_3130() 
{
return (String)get_Value("retencion_3130");
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
/** Set retencion_iva_140 */
public void setretencion_iva_140 (String retencion_iva_140)
{
if (retencion_iva_140 != null && retencion_iva_140.length() > 32)
{
log.warning("Length > 32 - truncated");
retencion_iva_140 = retencion_iva_140.substring(0,32);
}
set_Value ("retencion_iva_140", retencion_iva_140);
}
/** Get retencion_iva_140 */
public String getretencion_iva_140() 
{
return (String)get_Value("retencion_iva_140");
}
/** Set retencion_municipal */
public void setretencion_municipal (String retencion_municipal)
{
if (retencion_municipal != null && retencion_municipal.length() > 32)
{
log.warning("Length > 32 - truncated");
retencion_municipal = retencion_municipal.substring(0,32);
}
set_Value ("retencion_municipal", retencion_municipal);
}
/** Get retencion_municipal */
public String getretencion_municipal() 
{
return (String)get_Value("retencion_municipal");
}
/** Set ret_ingresos_brutos */
public void setret_ingresos_brutos (String ret_ingresos_brutos)
{
if (ret_ingresos_brutos != null && ret_ingresos_brutos.length() > 32)
{
log.warning("Length > 32 - truncated");
ret_ingresos_brutos = ret_ingresos_brutos.substring(0,32);
}
set_Value ("ret_ingresos_brutos", ret_ingresos_brutos);
}
/** Get ret_ingresos_brutos */
public String getret_ingresos_brutos() 
{
return (String)get_Value("ret_ingresos_brutos");
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
/** Set rubro */
public void setrubro (String rubro)
{
if (rubro != null && rubro.length() > 32)
{
log.warning("Length > 32 - truncated");
rubro = rubro.substring(0,32);
}
set_Value ("rubro", rubro);
}
/** Get rubro */
public String getrubro() 
{
return (String)get_Value("rubro");
}
/** Set sig_acre_liq_ant_facturado_120 */
public void setsig_acre_liq_ant_facturado_120 (String sig_acre_liq_ant_facturado_120)
{
if (sig_acre_liq_ant_facturado_120 != null && sig_acre_liq_ant_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_acre_liq_ant_facturado_120 = sig_acre_liq_ant_facturado_120.substring(0,32);
}
set_Value ("sig_acre_liq_ant_facturado_120", sig_acre_liq_ant_facturado_120);
}
/** Get sig_acre_liq_ant_facturado_120 */
public String getsig_acre_liq_ant_facturado_120() 
{
return (String)get_Value("sig_acre_liq_ant_facturado_120");
}
/** Set sig_acre_liq_ant_facturado_30 */
public void setsig_acre_liq_ant_facturado_30 (String sig_acre_liq_ant_facturado_30)
{
if (sig_acre_liq_ant_facturado_30 != null && sig_acre_liq_ant_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_acre_liq_ant_facturado_30 = sig_acre_liq_ant_facturado_30.substring(0,32);
}
set_Value ("sig_acre_liq_ant_facturado_30", sig_acre_liq_ant_facturado_30);
}
/** Get sig_acre_liq_ant_facturado_30 */
public String getsig_acre_liq_ant_facturado_30() 
{
return (String)get_Value("sig_acre_liq_ant_facturado_30");
}
/** Set sig_acre_liq_ant_facturado_60 */
public void setsig_acre_liq_ant_facturado_60 (String sig_acre_liq_ant_facturado_60)
{
if (sig_acre_liq_ant_facturado_60 != null && sig_acre_liq_ant_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_acre_liq_ant_facturado_60 = sig_acre_liq_ant_facturado_60.substring(0,32);
}
set_Value ("sig_acre_liq_ant_facturado_60", sig_acre_liq_ant_facturado_60);
}
/** Get sig_acre_liq_ant_facturado_60 */
public String getsig_acre_liq_ant_facturado_60() 
{
return (String)get_Value("sig_acre_liq_ant_facturado_60");
}
/** Set sig_acre_liq_ant_facturado_90 */
public void setsig_acre_liq_ant_facturado_90 (String sig_acre_liq_ant_facturado_90)
{
if (sig_acre_liq_ant_facturado_90 != null && sig_acre_liq_ant_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_acre_liq_ant_facturado_90 = sig_acre_liq_ant_facturado_90.substring(0,32);
}
set_Value ("sig_acre_liq_ant_facturado_90", sig_acre_liq_ant_facturado_90);
}
/** Get sig_acre_liq_ant_facturado_90 */
public String getsig_acre_liq_ant_facturado_90() 
{
return (String)get_Value("sig_acre_liq_ant_facturado_90");
}
/** Set sig_acre_liq_ant_vto */
public void setsig_acre_liq_ant_vto (String sig_acre_liq_ant_vto)
{
if (sig_acre_liq_ant_vto != null && sig_acre_liq_ant_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_acre_liq_ant_vto = sig_acre_liq_ant_vto.substring(0,32);
}
set_Value ("sig_acre_liq_ant_vto", sig_acre_liq_ant_vto);
}
/** Get sig_acre_liq_ant_vto */
public String getsig_acre_liq_ant_vto() 
{
return (String)get_Value("sig_acre_liq_ant_vto");
}
/** Set sig_int_plan_esp_facturado_120 */
public void setsig_int_plan_esp_facturado_120 (String sig_int_plan_esp_facturado_120)
{
if (sig_int_plan_esp_facturado_120 != null && sig_int_plan_esp_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_int_plan_esp_facturado_120 = sig_int_plan_esp_facturado_120.substring(0,32);
}
set_Value ("sig_int_plan_esp_facturado_120", sig_int_plan_esp_facturado_120);
}
/** Get sig_int_plan_esp_facturado_120 */
public String getsig_int_plan_esp_facturado_120() 
{
return (String)get_Value("sig_int_plan_esp_facturado_120");
}
/** Set sig_int_plan_esp_facturado_30 */
public void setsig_int_plan_esp_facturado_30 (String sig_int_plan_esp_facturado_30)
{
if (sig_int_plan_esp_facturado_30 != null && sig_int_plan_esp_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_int_plan_esp_facturado_30 = sig_int_plan_esp_facturado_30.substring(0,32);
}
set_Value ("sig_int_plan_esp_facturado_30", sig_int_plan_esp_facturado_30);
}
/** Get sig_int_plan_esp_facturado_30 */
public String getsig_int_plan_esp_facturado_30() 
{
return (String)get_Value("sig_int_plan_esp_facturado_30");
}
/** Set sig_int_plan_esp_facturado_60 */
public void setsig_int_plan_esp_facturado_60 (String sig_int_plan_esp_facturado_60)
{
if (sig_int_plan_esp_facturado_60 != null && sig_int_plan_esp_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_int_plan_esp_facturado_60 = sig_int_plan_esp_facturado_60.substring(0,32);
}
set_Value ("sig_int_plan_esp_facturado_60", sig_int_plan_esp_facturado_60);
}
/** Get sig_int_plan_esp_facturado_60 */
public String getsig_int_plan_esp_facturado_60() 
{
return (String)get_Value("sig_int_plan_esp_facturado_60");
}
/** Set sig_int_plan_esp_facturado_90 */
public void setsig_int_plan_esp_facturado_90 (String sig_int_plan_esp_facturado_90)
{
if (sig_int_plan_esp_facturado_90 != null && sig_int_plan_esp_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_int_plan_esp_facturado_90 = sig_int_plan_esp_facturado_90.substring(0,32);
}
set_Value ("sig_int_plan_esp_facturado_90", sig_int_plan_esp_facturado_90);
}
/** Get sig_int_plan_esp_facturado_90 */
public String getsig_int_plan_esp_facturado_90() 
{
return (String)get_Value("sig_int_plan_esp_facturado_90");
}
/** Set sig_int_plan_esp_vto */
public void setsig_int_plan_esp_vto (String sig_int_plan_esp_vto)
{
if (sig_int_plan_esp_vto != null && sig_int_plan_esp_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_int_plan_esp_vto = sig_int_plan_esp_vto.substring(0,32);
}
set_Value ("sig_int_plan_esp_vto", sig_int_plan_esp_vto);
}
/** Get sig_int_plan_esp_vto */
public String getsig_int_plan_esp_vto() 
{
return (String)get_Value("sig_int_plan_esp_vto");
}
/** Set sig_iva_21_facturado_120 */
public void setsig_iva_21_facturado_120 (String sig_iva_21_facturado_120)
{
if (sig_iva_21_facturado_120 != null && sig_iva_21_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_iva_21_facturado_120 = sig_iva_21_facturado_120.substring(0,32);
}
set_Value ("sig_iva_21_facturado_120", sig_iva_21_facturado_120);
}
/** Get sig_iva_21_facturado_120 */
public String getsig_iva_21_facturado_120() 
{
return (String)get_Value("sig_iva_21_facturado_120");
}
/** Set sig_iva_21_facturado_30 */
public void setsig_iva_21_facturado_30 (String sig_iva_21_facturado_30)
{
if (sig_iva_21_facturado_30 != null && sig_iva_21_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_iva_21_facturado_30 = sig_iva_21_facturado_30.substring(0,32);
}
set_Value ("sig_iva_21_facturado_30", sig_iva_21_facturado_30);
}
/** Get sig_iva_21_facturado_30 */
public String getsig_iva_21_facturado_30() 
{
return (String)get_Value("sig_iva_21_facturado_30");
}
/** Set sig_iva_21_facturado_60 */
public void setsig_iva_21_facturado_60 (String sig_iva_21_facturado_60)
{
if (sig_iva_21_facturado_60 != null && sig_iva_21_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_iva_21_facturado_60 = sig_iva_21_facturado_60.substring(0,32);
}
set_Value ("sig_iva_21_facturado_60", sig_iva_21_facturado_60);
}
/** Get sig_iva_21_facturado_60 */
public String getsig_iva_21_facturado_60() 
{
return (String)get_Value("sig_iva_21_facturado_60");
}
/** Set sig_iva_21_facturado_90 */
public void setsig_iva_21_facturado_90 (String sig_iva_21_facturado_90)
{
if (sig_iva_21_facturado_90 != null && sig_iva_21_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_iva_21_facturado_90 = sig_iva_21_facturado_90.substring(0,32);
}
set_Value ("sig_iva_21_facturado_90", sig_iva_21_facturado_90);
}
/** Get sig_iva_21_facturado_90 */
public String getsig_iva_21_facturado_90() 
{
return (String)get_Value("sig_iva_21_facturado_90");
}
/** Set sig_iva_21_vto */
public void setsig_iva_21_vto (String sig_iva_21_vto)
{
if (sig_iva_21_vto != null && sig_iva_21_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_iva_21_vto = sig_iva_21_vto.substring(0,32);
}
set_Value ("sig_iva_21_vto", sig_iva_21_vto);
}
/** Get sig_iva_21_vto */
public String getsig_iva_21_vto() 
{
return (String)get_Value("sig_iva_21_vto");
}
/** Set signo_ara_facturado_120 */
public void setsigno_ara_facturado_120 (String signo_ara_facturado_120)
{
if (signo_ara_facturado_120 != null && signo_ara_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_ara_facturado_120 = signo_ara_facturado_120.substring(0,32);
}
set_Value ("signo_ara_facturado_120", signo_ara_facturado_120);
}
/** Get signo_ara_facturado_120 */
public String getsigno_ara_facturado_120() 
{
return (String)get_Value("signo_ara_facturado_120");
}
/** Set signo_ara_facturado_30 */
public void setsigno_ara_facturado_30 (String signo_ara_facturado_30)
{
if (signo_ara_facturado_30 != null && signo_ara_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_ara_facturado_30 = signo_ara_facturado_30.substring(0,32);
}
set_Value ("signo_ara_facturado_30", signo_ara_facturado_30);
}
/** Get signo_ara_facturado_30 */
public String getsigno_ara_facturado_30() 
{
return (String)get_Value("signo_ara_facturado_30");
}
/** Set signo_ara_facturado_60 */
public void setsigno_ara_facturado_60 (String signo_ara_facturado_60)
{
if (signo_ara_facturado_60 != null && signo_ara_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_ara_facturado_60 = signo_ara_facturado_60.substring(0,32);
}
set_Value ("signo_ara_facturado_60", signo_ara_facturado_60);
}
/** Get signo_ara_facturado_60 */
public String getsigno_ara_facturado_60() 
{
return (String)get_Value("signo_ara_facturado_60");
}
/** Set signo_ara_facturado_90 */
public void setsigno_ara_facturado_90 (String signo_ara_facturado_90)
{
if (signo_ara_facturado_90 != null && signo_ara_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_ara_facturado_90 = signo_ara_facturado_90.substring(0,32);
}
set_Value ("signo_ara_facturado_90", signo_ara_facturado_90);
}
/** Get signo_ara_facturado_90 */
public String getsigno_ara_facturado_90() 
{
return (String)get_Value("signo_ara_facturado_90");
}
/** Set signo_ara_vto */
public void setsigno_ara_vto (String signo_ara_vto)
{
if (signo_ara_vto != null && signo_ara_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_ara_vto = signo_ara_vto.substring(0,32);
}
set_Value ("signo_ara_vto", signo_ara_vto);
}
/** Get signo_ara_vto */
public String getsigno_ara_vto() 
{
return (String)get_Value("signo_ara_vto");
}
/** Set signo_base_imponible */
public void setsigno_base_imponible (String signo_base_imponible)
{
if (signo_base_imponible != null && signo_base_imponible.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_base_imponible = signo_base_imponible.substring(0,32);
}
set_Value ("signo_base_imponible", signo_base_imponible);
}
/** Get signo_base_imponible */
public String getsigno_base_imponible() 
{
return (String)get_Value("signo_base_imponible");
}
/** Set signo_dbtos_cdtos */
public void setsigno_dbtos_cdtos (String signo_dbtos_cdtos)
{
if (signo_dbtos_cdtos != null && signo_dbtos_cdtos.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_dbtos_cdtos = signo_dbtos_cdtos.substring(0,32);
}
set_Value ("signo_dbtos_cdtos", signo_dbtos_cdtos);
}
/** Get signo_dbtos_cdtos */
public String getsigno_dbtos_cdtos() 
{
return (String)get_Value("signo_dbtos_cdtos");
}
/** Set signo_embargo_y_cesiones */
public void setsigno_embargo_y_cesiones (String signo_embargo_y_cesiones)
{
if (signo_embargo_y_cesiones != null && signo_embargo_y_cesiones.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_embargo_y_cesiones = signo_embargo_y_cesiones.substring(0,32);
}
set_Value ("signo_embargo_y_cesiones", signo_embargo_y_cesiones);
}
/** Get signo_embargo_y_cesiones */
public String getsigno_embargo_y_cesiones() 
{
return (String)get_Value("signo_embargo_y_cesiones");
}
/** Set signo_int_z_facturado_120 */
public void setsigno_int_z_facturado_120 (String signo_int_z_facturado_120)
{
if (signo_int_z_facturado_120 != null && signo_int_z_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_int_z_facturado_120 = signo_int_z_facturado_120.substring(0,32);
}
set_Value ("signo_int_z_facturado_120", signo_int_z_facturado_120);
}
/** Get signo_int_z_facturado_120 */
public String getsigno_int_z_facturado_120() 
{
return (String)get_Value("signo_int_z_facturado_120");
}
/** Set signo_int_z_facturado_30 */
public void setsigno_int_z_facturado_30 (String signo_int_z_facturado_30)
{
if (signo_int_z_facturado_30 != null && signo_int_z_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_int_z_facturado_30 = signo_int_z_facturado_30.substring(0,32);
}
set_Value ("signo_int_z_facturado_30", signo_int_z_facturado_30);
}
/** Get signo_int_z_facturado_30 */
public String getsigno_int_z_facturado_30() 
{
return (String)get_Value("signo_int_z_facturado_30");
}
/** Set signo_int_z_facturado_60 */
public void setsigno_int_z_facturado_60 (String signo_int_z_facturado_60)
{
if (signo_int_z_facturado_60 != null && signo_int_z_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_int_z_facturado_60 = signo_int_z_facturado_60.substring(0,32);
}
set_Value ("signo_int_z_facturado_60", signo_int_z_facturado_60);
}
/** Get signo_int_z_facturado_60 */
public String getsigno_int_z_facturado_60() 
{
return (String)get_Value("signo_int_z_facturado_60");
}
/** Set signo_int_z_facturado_90 */
public void setsigno_int_z_facturado_90 (String signo_int_z_facturado_90)
{
if (signo_int_z_facturado_90 != null && signo_int_z_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_int_z_facturado_90 = signo_int_z_facturado_90.substring(0,32);
}
set_Value ("signo_int_z_facturado_90", signo_int_z_facturado_90);
}
/** Get signo_int_z_facturado_90 */
public String getsigno_int_z_facturado_90() 
{
return (String)get_Value("signo_int_z_facturado_90");
}
/** Set signo_int_z_vto */
public void setsigno_int_z_vto (String signo_int_z_vto)
{
if (signo_int_z_vto != null && signo_int_z_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_int_z_vto = signo_int_z_vto.substring(0,32);
}
set_Value ("signo_int_z_vto", signo_int_z_vto);
}
/** Get signo_int_z_vto */
public String getsigno_int_z_vto() 
{
return (String)get_Value("signo_int_z_vto");
}
/** Set signo_liq_negativa_ant */
public void setsigno_liq_negativa_ant (String signo_liq_negativa_ant)
{
if (signo_liq_negativa_ant != null && signo_liq_negativa_ant.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_liq_negativa_ant = signo_liq_negativa_ant.substring(0,32);
}
set_Value ("signo_liq_negativa_ant", signo_liq_negativa_ant);
}
/** Get signo_liq_negativa_ant */
public String getsigno_liq_negativa_ant() 
{
return (String)get_Value("signo_liq_negativa_ant");
}
/** Set signo_neto */
public void setsigno_neto (String signo_neto)
{
if (signo_neto != null && signo_neto.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_neto = signo_neto.substring(0,32);
}
set_Value ("signo_neto", signo_neto);
}
/** Get signo_neto */
public String getsigno_neto() 
{
return (String)get_Value("signo_neto");
}
/** Set signo_percepcion_1135 */
public void setsigno_percepcion_1135 (String signo_percepcion_1135)
{
if (signo_percepcion_1135 != null && signo_percepcion_1135.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_percepcion_1135 = signo_percepcion_1135.substring(0,32);
}
set_Value ("signo_percepcion_1135", signo_percepcion_1135);
}
/** Get signo_percepcion_1135 */
public String getsigno_percepcion_1135() 
{
return (String)get_Value("signo_percepcion_1135");
}
/** Set signo_retencion_municipal */
public void setsigno_retencion_municipal (String signo_retencion_municipal)
{
if (signo_retencion_municipal != null && signo_retencion_municipal.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_retencion_municipal = signo_retencion_municipal.substring(0,32);
}
set_Value ("signo_retencion_municipal", signo_retencion_municipal);
}
/** Get signo_retencion_municipal */
public String getsigno_retencion_municipal() 
{
return (String)get_Value("signo_retencion_municipal");
}
/** Set signo_ret_ganancias */
public void setsigno_ret_ganancias (String signo_ret_ganancias)
{
if (signo_ret_ganancias != null && signo_ret_ganancias.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_ret_ganancias = signo_ret_ganancias.substring(0,32);
}
set_Value ("signo_ret_ganancias", signo_ret_ganancias);
}
/** Get signo_ret_ganancias */
public String getsigno_ret_ganancias() 
{
return (String)get_Value("signo_ret_ganancias");
}
/** Set signo_ret_ing_brutos */
public void setsigno_ret_ing_brutos (String signo_ret_ing_brutos)
{
if (signo_ret_ing_brutos != null && signo_ret_ing_brutos.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_ret_ing_brutos = signo_ret_ing_brutos.substring(0,32);
}
set_Value ("signo_ret_ing_brutos", signo_ret_ing_brutos);
}
/** Get signo_ret_ing_brutos */
public String getsigno_ret_ing_brutos() 
{
return (String)get_Value("signo_ret_ing_brutos");
}
/** Set signo_ret_iva_140 */
public void setsigno_ret_iva_140 (String signo_ret_iva_140)
{
if (signo_ret_iva_140 != null && signo_ret_iva_140.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_ret_iva_140 = signo_ret_iva_140.substring(0,32);
}
set_Value ("signo_ret_iva_140", signo_ret_iva_140);
}
/** Get signo_ret_iva_140 */
public String getsigno_ret_iva_140() 
{
return (String)get_Value("signo_ret_iva_140");
}
/** Set signo_ret_iva_3130 */
public void setsigno_ret_iva_3130 (String signo_ret_iva_3130)
{
if (signo_ret_iva_3130 != null && signo_ret_iva_3130.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_ret_iva_3130 = signo_ret_iva_3130.substring(0,32);
}
set_Value ("signo_ret_iva_3130", signo_ret_iva_3130);
}
/** Get signo_ret_iva_3130 */
public String getsigno_ret_iva_3130() 
{
return (String)get_Value("signo_ret_iva_3130");
}
/** Set signo_total_anticipos */
public void setsigno_total_anticipos (String signo_total_anticipos)
{
if (signo_total_anticipos != null && signo_total_anticipos.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_total_anticipos = signo_total_anticipos.substring(0,32);
}
set_Value ("signo_total_anticipos", signo_total_anticipos);
}
/** Get signo_total_anticipos */
public String getsigno_total_anticipos() 
{
return (String)get_Value("signo_total_anticipos");
}
/** Set signo_total_cheque_diferido */
public void setsigno_total_cheque_diferido (String signo_total_cheque_diferido)
{
if (signo_total_cheque_diferido != null && signo_total_cheque_diferido.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_total_cheque_diferido = signo_total_cheque_diferido.substring(0,32);
}
set_Value ("signo_total_cheque_diferido", signo_total_cheque_diferido);
}
/** Get signo_total_cheque_diferido */
public String getsigno_total_cheque_diferido() 
{
return (String)get_Value("signo_total_cheque_diferido");
}
/** Set signo_total_creditos */
public void setsigno_total_creditos (String signo_total_creditos)
{
if (signo_total_creditos != null && signo_total_creditos.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_total_creditos = signo_total_creditos.substring(0,32);
}
set_Value ("signo_total_creditos", signo_total_creditos);
}
/** Get signo_total_creditos */
public String getsigno_total_creditos() 
{
return (String)get_Value("signo_total_creditos");
}
/** Set signo_total_descuentos */
public void setsigno_total_descuentos (String signo_total_descuentos)
{
if (signo_total_descuentos != null && signo_total_descuentos.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_total_descuentos = signo_total_descuentos.substring(0,32);
}
set_Value ("signo_total_descuentos", signo_total_descuentos);
}
/** Get signo_total_descuentos */
public String getsigno_total_descuentos() 
{
return (String)get_Value("signo_total_descuentos");
}
/** Set signo_total_otros_debitos */
public void setsigno_total_otros_debitos (String signo_total_otros_debitos)
{
if (signo_total_otros_debitos != null && signo_total_otros_debitos.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_total_otros_debitos = signo_total_otros_debitos.substring(0,32);
}
set_Value ("signo_total_otros_debitos", signo_total_otros_debitos);
}
/** Get signo_total_otros_debitos */
public String getsigno_total_otros_debitos() 
{
return (String)get_Value("signo_total_otros_debitos");
}
/** Set sig_perc_iibb_facturado_120 */
public void setsig_perc_iibb_facturado_120 (String sig_perc_iibb_facturado_120)
{
if (sig_perc_iibb_facturado_120 != null && sig_perc_iibb_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_perc_iibb_facturado_120 = sig_perc_iibb_facturado_120.substring(0,32);
}
set_Value ("sig_perc_iibb_facturado_120", sig_perc_iibb_facturado_120);
}
/** Get sig_perc_iibb_facturado_120 */
public String getsig_perc_iibb_facturado_120() 
{
return (String)get_Value("sig_perc_iibb_facturado_120");
}
/** Set sig_perc_iibb_facturado_30 */
public void setsig_perc_iibb_facturado_30 (String sig_perc_iibb_facturado_30)
{
if (sig_perc_iibb_facturado_30 != null && sig_perc_iibb_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_perc_iibb_facturado_30 = sig_perc_iibb_facturado_30.substring(0,32);
}
set_Value ("sig_perc_iibb_facturado_30", sig_perc_iibb_facturado_30);
}
/** Get sig_perc_iibb_facturado_30 */
public String getsig_perc_iibb_facturado_30() 
{
return (String)get_Value("sig_perc_iibb_facturado_30");
}
/** Set sig_perc_iibb_facturado_60 */
public void setsig_perc_iibb_facturado_60 (String sig_perc_iibb_facturado_60)
{
if (sig_perc_iibb_facturado_60 != null && sig_perc_iibb_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_perc_iibb_facturado_60 = sig_perc_iibb_facturado_60.substring(0,32);
}
set_Value ("sig_perc_iibb_facturado_60", sig_perc_iibb_facturado_60);
}
/** Get sig_perc_iibb_facturado_60 */
public String getsig_perc_iibb_facturado_60() 
{
return (String)get_Value("sig_perc_iibb_facturado_60");
}
/** Set sig_perc_iibb_facturado_90 */
public void setsig_perc_iibb_facturado_90 (String sig_perc_iibb_facturado_90)
{
if (sig_perc_iibb_facturado_90 != null && sig_perc_iibb_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_perc_iibb_facturado_90 = sig_perc_iibb_facturado_90.substring(0,32);
}
set_Value ("sig_perc_iibb_facturado_90", sig_perc_iibb_facturado_90);
}
/** Get sig_perc_iibb_facturado_90 */
public String getsig_perc_iibb_facturado_90() 
{
return (String)get_Value("sig_perc_iibb_facturado_90");
}
/** Set sig_perc_iibb_vto */
public void setsig_perc_iibb_vto (String sig_perc_iibb_vto)
{
if (sig_perc_iibb_vto != null && sig_perc_iibb_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_perc_iibb_vto = sig_perc_iibb_vto.substring(0,32);
}
set_Value ("sig_perc_iibb_vto", sig_perc_iibb_vto);
}
/** Get sig_perc_iibb_vto */
public String getsig_perc_iibb_vto() 
{
return (String)get_Value("sig_perc_iibb_vto");
}
/** Set sig_perc_iva_facturado_120 */
public void setsig_perc_iva_facturado_120 (String sig_perc_iva_facturado_120)
{
if (sig_perc_iva_facturado_120 != null && sig_perc_iva_facturado_120.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_perc_iva_facturado_120 = sig_perc_iva_facturado_120.substring(0,32);
}
set_Value ("sig_perc_iva_facturado_120", sig_perc_iva_facturado_120);
}
/** Get sig_perc_iva_facturado_120 */
public String getsig_perc_iva_facturado_120() 
{
return (String)get_Value("sig_perc_iva_facturado_120");
}
/** Set sig_perc_iva_facturado_30 */
public void setsig_perc_iva_facturado_30 (String sig_perc_iva_facturado_30)
{
if (sig_perc_iva_facturado_30 != null && sig_perc_iva_facturado_30.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_perc_iva_facturado_30 = sig_perc_iva_facturado_30.substring(0,32);
}
set_Value ("sig_perc_iva_facturado_30", sig_perc_iva_facturado_30);
}
/** Get sig_perc_iva_facturado_30 */
public String getsig_perc_iva_facturado_30() 
{
return (String)get_Value("sig_perc_iva_facturado_30");
}
/** Set sig_perc_iva_facturado_60 */
public void setsig_perc_iva_facturado_60 (String sig_perc_iva_facturado_60)
{
if (sig_perc_iva_facturado_60 != null && sig_perc_iva_facturado_60.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_perc_iva_facturado_60 = sig_perc_iva_facturado_60.substring(0,32);
}
set_Value ("sig_perc_iva_facturado_60", sig_perc_iva_facturado_60);
}
/** Get sig_perc_iva_facturado_60 */
public String getsig_perc_iva_facturado_60() 
{
return (String)get_Value("sig_perc_iva_facturado_60");
}
/** Set sig_perc_iva_facturado_90 */
public void setsig_perc_iva_facturado_90 (String sig_perc_iva_facturado_90)
{
if (sig_perc_iva_facturado_90 != null && sig_perc_iva_facturado_90.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_perc_iva_facturado_90 = sig_perc_iva_facturado_90.substring(0,32);
}
set_Value ("sig_perc_iva_facturado_90", sig_perc_iva_facturado_90);
}
/** Get sig_perc_iva_facturado_90 */
public String getsig_perc_iva_facturado_90() 
{
return (String)get_Value("sig_perc_iva_facturado_90");
}
/** Set sig_perc_iva_vto */
public void setsig_perc_iva_vto (String sig_perc_iva_vto)
{
if (sig_perc_iva_vto != null && sig_perc_iva_vto.length() > 32)
{
log.warning("Length > 32 - truncated");
sig_perc_iva_vto = sig_perc_iva_vto.substring(0,32);
}
set_Value ("sig_perc_iva_vto", sig_perc_iva_vto);
}
/** Get sig_perc_iva_vto */
public String getsig_perc_iva_vto() 
{
return (String)get_Value("sig_perc_iva_vto");
}
/** Set tarjeta */
public void settarjeta (String tarjeta)
{
if (tarjeta != null && tarjeta.length() > 32)
{
log.warning("Length > 32 - truncated");
tarjeta = tarjeta.substring(0,32);
}
set_Value ("tarjeta", tarjeta);
}
/** Get tarjeta */
public String gettarjeta() 
{
return (String)get_Value("tarjeta");
}
/** Set tipo_cd */
public void settipo_cd (String tipo_cd)
{
if (tipo_cd != null && tipo_cd.length() > 32)
{
log.warning("Length > 32 - truncated");
tipo_cd = tipo_cd.substring(0,32);
}
set_Value ("tipo_cd", tipo_cd);
}
/** Get tipo_cd */
public String gettipo_cd() 
{
return (String)get_Value("tipo_cd");
}
/** Set tipo_mov */
public void settipo_mov (String tipo_mov)
{
if (tipo_mov != null && tipo_mov.length() > 32)
{
log.warning("Length > 32 - truncated");
tipo_mov = tipo_mov.substring(0,32);
}
set_Value ("tipo_mov", tipo_mov);
}
/** Get tipo_mov */
public String gettipo_mov() 
{
return (String)get_Value("tipo_mov");
}
/** Set tipo_op */
public void settipo_op (String tipo_op)
{
if (tipo_op != null && tipo_op.length() > 32)
{
log.warning("Length > 32 - truncated");
tipo_op = tipo_op.substring(0,32);
}
set_Value ("tipo_op", tipo_op);
}
/** Get tipo_op */
public String gettipo_op() 
{
return (String)get_Value("tipo_op");
}
/** Set total_descuentos */
public void settotal_descuentos (String total_descuentos)
{
if (total_descuentos != null && total_descuentos.length() > 32)
{
log.warning("Length > 32 - truncated");
total_descuentos = total_descuentos.substring(0,32);
}
set_Value ("total_descuentos", total_descuentos);
}
/** Get total_descuentos */
public String gettotal_descuentos() 
{
return (String)get_Value("total_descuentos");
}
}
