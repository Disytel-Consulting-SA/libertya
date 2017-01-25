/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_AmexPaymentsAndTaxes
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-01-25 11:08:36.238 */
public class X_I_AmexPaymentsAndTaxes extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_AmexPaymentsAndTaxes (Properties ctx, int I_AmexPaymentsAndTaxes_ID, String trxName)
{
super (ctx, I_AmexPaymentsAndTaxes_ID, trxName);
/** if (I_AmexPaymentsAndTaxes_ID == 0)
{
setI_Amexpaymentsandtaxes_ID (0);
setI_IsImported (false);
}
 */
}
/** Load Constructor */
public X_I_AmexPaymentsAndTaxes (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_AmexPaymentsAndTaxes");

/** TableName=I_AmexPaymentsAndTaxes */
public static final String Table_Name="I_AmexPaymentsAndTaxes";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_AmexPaymentsAndTaxes");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_AmexPaymentsAndTaxes[").append(getID()).append("]");
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
/** Set base_imp */
public void setbase_imp (String base_imp)
{
if (base_imp != null && base_imp.length() > 32)
{
log.warning("Length > 32 - truncated");
base_imp = base_imp.substring(0,32);
}
set_Value ("base_imp", base_imp);
}
/** Get base_imp */
public String getbase_imp() 
{
return (String)get_Value("base_imp");
}
/** Set cod_banco */
public void setcod_banco (String cod_banco)
{
if (cod_banco != null && cod_banco.length() > 32)
{
log.warning("Length > 32 - truncated");
cod_banco = cod_banco.substring(0,32);
}
set_Value ("cod_banco", cod_banco);
}
/** Get cod_banco */
public String getcod_banco() 
{
return (String)get_Value("cod_banco");
}
/** Set cod_imp */
public void setcod_imp (String cod_imp)
{
if (cod_imp != null && cod_imp.length() > 32)
{
log.warning("Length > 32 - truncated");
cod_imp = cod_imp.substring(0,32);
}
set_Value ("cod_imp", cod_imp);
}
/** Get cod_imp */
public String getcod_imp() 
{
return (String)get_Value("cod_imp");
}
/** Set cod_imp_desc */
public void setcod_imp_desc (String cod_imp_desc)
{
if (cod_imp_desc != null && cod_imp_desc.length() > 32)
{
log.warning("Length > 32 - truncated");
cod_imp_desc = cod_imp_desc.substring(0,32);
}
set_Value ("cod_imp_desc", cod_imp_desc);
}
/** Get cod_imp_desc */
public String getcod_imp_desc() 
{
return (String)get_Value("cod_imp_desc");
}
/** Set Cod_Moneda */
public void setCod_Moneda (String Cod_Moneda)
{
if (Cod_Moneda != null && Cod_Moneda.length() > 32)
{
log.warning("Length > 32 - truncated");
Cod_Moneda = Cod_Moneda.substring(0,32);
}
set_Value ("Cod_Moneda", Cod_Moneda);
}
/** Get Cod_Moneda */
public String getCod_Moneda() 
{
return (String)get_Value("Cod_Moneda");
}
/** Set cod_suc_banc */
public void setcod_suc_banc (String cod_suc_banc)
{
if (cod_suc_banc != null && cod_suc_banc.length() > 32)
{
log.warning("Length > 32 - truncated");
cod_suc_banc = cod_suc_banc.substring(0,32);
}
set_Value ("cod_suc_banc", cod_suc_banc);
}
/** Get cod_suc_banc */
public String getcod_suc_banc() 
{
return (String)get_Value("cod_suc_banc");
}
/** Set estado_pago */
public void setestado_pago (String estado_pago)
{
if (estado_pago != null && estado_pago.length() > 32)
{
log.warning("Length > 32 - truncated");
estado_pago = estado_pago.substring(0,32);
}
set_Value ("estado_pago", estado_pago);
}
/** Get estado_pago */
public String getestado_pago() 
{
return (String)get_Value("estado_pago");
}
/** Set fecha_imp */
public void setfecha_imp (String fecha_imp)
{
if (fecha_imp != null && fecha_imp.length() > 32)
{
log.warning("Length > 32 - truncated");
fecha_imp = fecha_imp.substring(0,32);
}
set_Value ("fecha_imp", fecha_imp);
}
/** Get fecha_imp */
public String getfecha_imp() 
{
return (String)get_Value("fecha_imp");
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
/** Set I_Amexpaymentsandtaxes_ID */
public void setI_Amexpaymentsandtaxes_ID (int I_Amexpaymentsandtaxes_ID)
{
set_ValueNoCheck ("I_Amexpaymentsandtaxes_ID", new Integer(I_Amexpaymentsandtaxes_ID));
}
/** Get I_Amexpaymentsandtaxes_ID */
public int getI_Amexpaymentsandtaxes_ID() 
{
Integer ii = (Integer)get_Value("I_Amexpaymentsandtaxes_ID");
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
/** Set imp_bruto_est */
public void setimp_bruto_est (String imp_bruto_est)
{
if (imp_bruto_est != null && imp_bruto_est.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_bruto_est = imp_bruto_est.substring(0,32);
}
set_Value ("imp_bruto_est", imp_bruto_est);
}
/** Get imp_bruto_est */
public String getimp_bruto_est() 
{
return (String)get_Value("imp_bruto_est");
}
/** Set imp_desc_pago */
public void setimp_desc_pago (String imp_desc_pago)
{
if (imp_desc_pago != null && imp_desc_pago.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_desc_pago = imp_desc_pago.substring(0,32);
}
set_Value ("imp_desc_pago", imp_desc_pago);
}
/** Get imp_desc_pago */
public String getimp_desc_pago() 
{
return (String)get_Value("imp_desc_pago");
}
/** Set imp_neto_ajuste */
public void setimp_neto_ajuste (String imp_neto_ajuste)
{
if (imp_neto_ajuste != null && imp_neto_ajuste.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_neto_ajuste = imp_neto_ajuste.substring(0,32);
}
set_Value ("imp_neto_ajuste", imp_neto_ajuste);
}
/** Get imp_neto_ajuste */
public String getimp_neto_ajuste() 
{
return (String)get_Value("imp_neto_ajuste");
}
/** Set importe_deuda_ant */
public void setimporte_deuda_ant (String importe_deuda_ant)
{
if (importe_deuda_ant != null && importe_deuda_ant.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_deuda_ant = importe_deuda_ant.substring(0,32);
}
set_Value ("importe_deuda_ant", importe_deuda_ant);
}
/** Get importe_deuda_ant */
public String getimporte_deuda_ant() 
{
return (String)get_Value("importe_deuda_ant");
}
/** Set importe_imp */
public void setimporte_imp (String importe_imp)
{
if (importe_imp != null && importe_imp.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_imp = importe_imp.substring(0,32);
}
set_Value ("importe_imp", importe_imp);
}
/** Get importe_imp */
public String getimporte_imp() 
{
return (String)get_Value("importe_imp");
}
/** Set importe_pago */
public void setimporte_pago (String importe_pago)
{
if (importe_pago != null && importe_pago.length() > 32)
{
log.warning("Length > 32 - truncated");
importe_pago = importe_pago.substring(0,32);
}
set_Value ("importe_pago", importe_pago);
}
/** Get importe_pago */
public String getimporte_pago() 
{
return (String)get_Value("importe_pago");
}
/** Set imp_tot_desc_acel */
public void setimp_tot_desc_acel (String imp_tot_desc_acel)
{
if (imp_tot_desc_acel != null && imp_tot_desc_acel.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_tot_desc_acel = imp_tot_desc_acel.substring(0,32);
}
set_Value ("imp_tot_desc_acel", imp_tot_desc_acel);
}
/** Get imp_tot_desc_acel */
public String getimp_tot_desc_acel() 
{
return (String)get_Value("imp_tot_desc_acel");
}
/** Set imp_tot_impuestos */
public void setimp_tot_impuestos (String imp_tot_impuestos)
{
if (imp_tot_impuestos != null && imp_tot_impuestos.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_tot_impuestos = imp_tot_impuestos.substring(0,32);
}
set_Value ("imp_tot_impuestos", imp_tot_impuestos);
}
/** Get imp_tot_impuestos */
public String getimp_tot_impuestos() 
{
return (String)get_Value("imp_tot_impuestos");
}
/** Set nom_est */
public void setnom_est (String nom_est)
{
if (nom_est != null && nom_est.length() > 32)
{
log.warning("Length > 32 - truncated");
nom_est = nom_est.substring(0,32);
}
set_Value ("nom_est", nom_est);
}
/** Get nom_est */
public String getnom_est() 
{
return (String)get_Value("nom_est");
}
/** Set num_cuenta_banc */
public void setnum_cuenta_banc (String num_cuenta_banc)
{
if (num_cuenta_banc != null && num_cuenta_banc.length() > 32)
{
log.warning("Length > 32 - truncated");
num_cuenta_banc = num_cuenta_banc.substring(0,32);
}
set_Value ("num_cuenta_banc", num_cuenta_banc);
}
/** Get num_cuenta_banc */
public String getnum_cuenta_banc() 
{
return (String)get_Value("num_cuenta_banc");
}
/** Set num_est */
public void setnum_est (String num_est)
{
if (num_est != null && num_est.length() > 32)
{
log.warning("Length > 32 - truncated");
num_est = num_est.substring(0,32);
}
set_Value ("num_est", num_est);
}
/** Get num_est */
public String getnum_est() 
{
return (String)get_Value("num_est");
}
/** Set num_sec_pago */
public void setnum_sec_pago (String num_sec_pago)
{
if (num_sec_pago != null && num_sec_pago.length() > 32)
{
log.warning("Length > 32 - truncated");
num_sec_pago = num_sec_pago.substring(0,32);
}
set_Value ("num_sec_pago", num_sec_pago);
}
/** Get num_sec_pago */
public String getnum_sec_pago() 
{
return (String)get_Value("num_sec_pago");
}
/** Set porc_imp */
public void setporc_imp (String porc_imp)
{
if (porc_imp != null && porc_imp.length() > 32)
{
log.warning("Length > 32 - truncated");
porc_imp = porc_imp.substring(0,32);
}
set_Value ("porc_imp", porc_imp);
}
/** Get porc_imp */
public String getporc_imp() 
{
return (String)get_Value("porc_imp");
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
/** Set tipo_reg */
public void settipo_reg (String tipo_reg)
{
if (tipo_reg != null && tipo_reg.length() > 32)
{
log.warning("Length > 32 - truncated");
tipo_reg = tipo_reg.substring(0,32);
}
set_Value ("tipo_reg", tipo_reg);
}
/** Get tipo_reg */
public String gettipo_reg() 
{
return (String)get_Value("tipo_reg");
}
}
