/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_VisaPayments
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2017-01-25 11:08:47.791 */
public class X_I_VisaPayments extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_VisaPayments (Properties ctx, int I_VisaPayments_ID, String trxName)
{
super (ctx, I_VisaPayments_ID, trxName);
/** if (I_VisaPayments_ID == 0)
{
setI_IsImported (false);
setI_Visapayments_ID (0);
}
 */
}
/** Load Constructor */
public X_I_VisaPayments (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_VisaPayments");

/** TableName=I_VisaPayments */
public static final String Table_Name="I_VisaPayments";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_VisaPayments");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_VisaPayments[").append(getID()).append("]");
return sb.toString();
}
/** Set adic_altacom */
public void setadic_altacom (String adic_altacom)
{
if (adic_altacom != null && adic_altacom.length() > 32)
{
log.warning("Length > 32 - truncated");
adic_altacom = adic_altacom.substring(0,32);
}
set_Value ("adic_altacom", adic_altacom);
}
/** Get adic_altacom */
public String getadic_altacom() 
{
return (String)get_Value("adic_altacom");
}
/** Set adic_altacom_bco */
public void setadic_altacom_bco (String adic_altacom_bco)
{
if (adic_altacom_bco != null && adic_altacom_bco.length() > 32)
{
log.warning("Length > 32 - truncated");
adic_altacom_bco = adic_altacom_bco.substring(0,32);
}
set_Value ("adic_altacom_bco", adic_altacom_bco);
}
/** Get adic_altacom_bco */
public String getadic_altacom_bco() 
{
return (String)get_Value("adic_altacom_bco");
}
/** Set adic_cupmanu */
public void setadic_cupmanu (String adic_cupmanu)
{
if (adic_cupmanu != null && adic_cupmanu.length() > 32)
{
log.warning("Length > 32 - truncated");
adic_cupmanu = adic_cupmanu.substring(0,32);
}
set_Value ("adic_cupmanu", adic_cupmanu);
}
/** Get adic_cupmanu */
public String getadic_cupmanu() 
{
return (String)get_Value("adic_cupmanu");
}
/** Set adic_opinter */
public void setadic_opinter (String adic_opinter)
{
if (adic_opinter != null && adic_opinter.length() > 32)
{
log.warning("Length > 32 - truncated");
adic_opinter = adic_opinter.substring(0,32);
}
set_Value ("adic_opinter", adic_opinter);
}
/** Get adic_opinter */
public String getadic_opinter() 
{
return (String)get_Value("adic_opinter");
}
/** Set adic_plancuo */
public void setadic_plancuo (String adic_plancuo)
{
if (adic_plancuo != null && adic_plancuo.length() > 32)
{
log.warning("Length > 32 - truncated");
adic_plancuo = adic_plancuo.substring(0,32);
}
set_Value ("adic_plancuo", adic_plancuo);
}
/** Get adic_plancuo */
public String getadic_plancuo() 
{
return (String)get_Value("adic_plancuo");
}
/** Set alic_percep_ib_agip */
public void setalic_percep_ib_agip (String alic_percep_ib_agip)
{
if (alic_percep_ib_agip != null && alic_percep_ib_agip.length() > 32)
{
log.warning("Length > 32 - truncated");
alic_percep_ib_agip = alic_percep_ib_agip.substring(0,32);
}
set_Value ("alic_percep_ib_agip", alic_percep_ib_agip);
}
/** Get alic_percep_ib_agip */
public String getalic_percep_ib_agip() 
{
return (String)get_Value("alic_percep_ib_agip");
}
/** Set alic_reten_ib_agip */
public void setalic_reten_ib_agip (String alic_reten_ib_agip)
{
if (alic_reten_ib_agip != null && alic_reten_ib_agip.length() > 32)
{
log.warning("Length > 32 - truncated");
alic_reten_ib_agip = alic_reten_ib_agip.substring(0,32);
}
set_Value ("alic_reten_ib_agip", alic_reten_ib_agip);
}
/** Get alic_reten_ib_agip */
public String getalic_reten_ib_agip() 
{
return (String)get_Value("alic_reten_ib_agip");
}
/** Set ali_ingbru */
public void setali_ingbru (String ali_ingbru)
{
if (ali_ingbru != null && ali_ingbru.length() > 32)
{
log.warning("Length > 32 - truncated");
ali_ingbru = ali_ingbru.substring(0,32);
}
set_Value ("ali_ingbru", ali_ingbru);
}
/** Get ali_ingbru */
public String getali_ingbru() 
{
return (String)get_Value("ali_ingbru");
}
/** Set ali_ingbru2 */
public void setali_ingbru2 (String ali_ingbru2)
{
if (ali_ingbru2 != null && ali_ingbru2.length() > 32)
{
log.warning("Length > 32 - truncated");
ali_ingbru2 = ali_ingbru2.substring(0,32);
}
set_Value ("ali_ingbru2", ali_ingbru2);
}
/** Get ali_ingbru2 */
public String getali_ingbru2() 
{
return (String)get_Value("ali_ingbru2");
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
/** Set aster */
public void setaster (String aster)
{
if (aster != null && aster.length() > 32)
{
log.warning("Length > 32 - truncated");
aster = aster.substring(0,32);
}
set_Value ("aster", aster);
}
/** Get aster */
public String getaster() 
{
return (String)get_Value("aster");
}
/** Set cargo_cit_b */
public void setcargo_cit_b (String cargo_cit_b)
{
if (cargo_cit_b != null && cargo_cit_b.length() > 32)
{
log.warning("Length > 32 - truncated");
cargo_cit_b = cargo_cit_b.substring(0,32);
}
set_Value ("cargo_cit_b", cargo_cit_b);
}
/** Get cargo_cit_b */
public String getcargo_cit_b() 
{
return (String)get_Value("cargo_cit_b");
}
/** Set cargo_cit_e */
public void setcargo_cit_e (String cargo_cit_e)
{
if (cargo_cit_e != null && cargo_cit_e.length() > 32)
{
log.warning("Length > 32 - truncated");
cargo_cit_e = cargo_cit_e.substring(0,32);
}
set_Value ("cargo_cit_e", cargo_cit_e);
}
/** Get cargo_cit_e */
public String getcargo_cit_e() 
{
return (String)get_Value("cargo_cit_e");
}
/** Set cargo_edc_b */
public void setcargo_edc_b (String cargo_edc_b)
{
if (cargo_edc_b != null && cargo_edc_b.length() > 32)
{
log.warning("Length > 32 - truncated");
cargo_edc_b = cargo_edc_b.substring(0,32);
}
set_Value ("cargo_edc_b", cargo_edc_b);
}
/** Get cargo_edc_b */
public String getcargo_edc_b() 
{
return (String)get_Value("cargo_edc_b");
}
/** Set cargo_edc_e */
public void setcargo_edc_e (String cargo_edc_e)
{
if (cargo_edc_e != null && cargo_edc_e.length() > 32)
{
log.warning("Length > 32 - truncated");
cargo_edc_e = cargo_edc_e.substring(0,32);
}
set_Value ("cargo_edc_e", cargo_edc_e);
}
/** Get cargo_edc_e */
public String getcargo_edc_e() 
{
return (String)get_Value("cargo_edc_e");
}
/** Set cargo_pex */
public void setcargo_pex (String cargo_pex)
{
if (cargo_pex != null && cargo_pex.length() > 32)
{
log.warning("Length > 32 - truncated");
cargo_pex = cargo_pex.substring(0,32);
}
set_Value ("cargo_pex", cargo_pex);
}
/** Get cargo_pex */
public String getcargo_pex() 
{
return (String)get_Value("cargo_pex");
}
/** Set cargo_x_liq */
public void setcargo_x_liq (String cargo_x_liq)
{
if (cargo_x_liq != null && cargo_x_liq.length() > 32)
{
log.warning("Length > 32 - truncated");
cargo_x_liq = cargo_x_liq.substring(0,32);
}
set_Value ("cargo_x_liq", cargo_x_liq);
}
/** Get cargo_x_liq */
public String getcargo_x_liq() 
{
return (String)get_Value("cargo_x_liq");
}
/** Set casacta */
public void setcasacta (String casacta)
{
if (casacta != null && casacta.length() > 32)
{
log.warning("Length > 32 - truncated");
casacta = casacta.substring(0,32);
}
set_Value ("casacta", casacta);
}
/** Get casacta */
public String getcasacta() 
{
return (String)get_Value("casacta");
}
/** Set cf_exento_iva */
public void setcf_exento_iva (String cf_exento_iva)
{
if (cf_exento_iva != null && cf_exento_iva.length() > 32)
{
log.warning("Length > 32 - truncated");
cf_exento_iva = cf_exento_iva.substring(0,32);
}
set_Value ("cf_exento_iva", cf_exento_iva);
}
/** Get cf_exento_iva */
public String getcf_exento_iva() 
{
return (String)get_Value("cf_exento_iva");
}
/** Set cf_no_reduce_iva */
public void setcf_no_reduce_iva (String cf_no_reduce_iva)
{
if (cf_no_reduce_iva != null && cf_no_reduce_iva.length() > 32)
{
log.warning("Length > 32 - truncated");
cf_no_reduce_iva = cf_no_reduce_iva.substring(0,32);
}
set_Value ("cf_no_reduce_iva", cf_no_reduce_iva);
}
/** Get cf_no_reduce_iva */
public String getcf_no_reduce_iva() 
{
return (String)get_Value("cf_no_reduce_iva");
}
/** Set costo_cuoemi */
public void setcosto_cuoemi (String costo_cuoemi)
{
if (costo_cuoemi != null && costo_cuoemi.length() > 32)
{
log.warning("Length > 32 - truncated");
costo_cuoemi = costo_cuoemi.substring(0,32);
}
set_Value ("costo_cuoemi", costo_cuoemi);
}
/** Get costo_cuoemi */
public String getcosto_cuoemi() 
{
return (String)get_Value("costo_cuoemi");
}
/** Set ctabco */
public void setctabco (String ctabco)
{
if (ctabco != null && ctabco.length() > 32)
{
log.warning("Length > 32 - truncated");
ctabco = ctabco.substring(0,32);
}
set_Value ("ctabco", ctabco);
}
/** Get ctabco */
public String getctabco() 
{
return (String)get_Value("ctabco");
}
/** Set dealer */
public void setdealer (String dealer)
{
if (dealer != null && dealer.length() > 32)
{
log.warning("Length > 32 - truncated");
dealer = dealer.substring(0,32);
}
set_Value ("dealer", dealer);
}
/** Get dealer */
public String getdealer() 
{
return (String)get_Value("dealer");
}
/** Set dto_campania */
public void setdto_campania (String dto_campania)
{
if (dto_campania != null && dto_campania.length() > 32)
{
log.warning("Length > 32 - truncated");
dto_campania = dto_campania.substring(0,32);
}
set_Value ("dto_campania", dto_campania);
}
/** Get dto_campania */
public String getdto_campania() 
{
return (String)get_Value("dto_campania");
}
/** Set empresa */
public void setempresa (String empresa)
{
if (empresa != null && empresa.length() > 32)
{
log.warning("Length > 32 - truncated");
empresa = empresa.substring(0,32);
}
set_Value ("empresa", empresa);
}
/** Get empresa */
public String getempresa() 
{
return (String)get_Value("empresa");
}
/** Set fpag */
public void setfpag (String fpag)
{
if (fpag != null && fpag.length() > 32)
{
log.warning("Length > 32 - truncated");
fpag = fpag.substring(0,32);
}
set_Value ("fpag", fpag);
}
/** Get fpag */
public String getfpag() 
{
return (String)get_Value("fpag");
}
/** Set fpres */
public void setfpres (String fpres)
{
if (fpres != null && fpres.length() > 32)
{
log.warning("Length > 32 - truncated");
fpres = fpres.substring(0,32);
}
set_Value ("fpres", fpres);
}
/** Get fpres */
public String getfpres() 
{
return (String)get_Value("fpres");
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
/** Set impbruto */
public void setimpbruto (String impbruto)
{
if (impbruto != null && impbruto.length() > 32)
{
log.warning("Length > 32 - truncated");
impbruto = impbruto.substring(0,32);
}
set_Value ("impbruto", impbruto);
}
/** Get impbruto */
public String getimpbruto() 
{
return (String)get_Value("impbruto");
}
/** Set imp_db_cr */
public void setimp_db_cr (String imp_db_cr)
{
if (imp_db_cr != null && imp_db_cr.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_db_cr = imp_db_cr.substring(0,32);
}
set_Value ("imp_db_cr", imp_db_cr);
}
/** Get imp_db_cr */
public String getimp_db_cr() 
{
return (String)get_Value("imp_db_cr");
}
/** Set impneto */
public void setimpneto (String impneto)
{
if (impneto != null && impneto.length() > 32)
{
log.warning("Length > 32 - truncated");
impneto = impneto.substring(0,32);
}
set_Value ("impneto", impneto);
}
/** Get impneto */
public String getimpneto() 
{
return (String)get_Value("impneto");
}
/** Set impret */
public void setimpret (String impret)
{
if (impret != null && impret.length() > 32)
{
log.warning("Length > 32 - truncated");
impret = impret.substring(0,32);
}
set_Value ("impret", impret);
}
/** Get impret */
public String getimpret() 
{
return (String)get_Value("impret");
}
/** Set imp_serv */
public void setimp_serv (String imp_serv)
{
if (imp_serv != null && imp_serv.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_serv = imp_serv.substring(0,32);
}
set_Value ("imp_serv", imp_serv);
}
/** Get imp_serv */
public String getimp_serv() 
{
return (String)get_Value("imp_serv");
}
/** Set iva1_ad_altacom */
public void setiva1_ad_altacom (String iva1_ad_altacom)
{
if (iva1_ad_altacom != null && iva1_ad_altacom.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_ad_altacom = iva1_ad_altacom.substring(0,32);
}
set_Value ("iva1_ad_altacom", iva1_ad_altacom);
}
/** Get iva1_ad_altacom */
public String getiva1_ad_altacom() 
{
return (String)get_Value("iva1_ad_altacom");
}
/** Set iva1_ad_altacom_bco */
public void setiva1_ad_altacom_bco (String iva1_ad_altacom_bco)
{
if (iva1_ad_altacom_bco != null && iva1_ad_altacom_bco.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_ad_altacom_bco = iva1_ad_altacom_bco.substring(0,32);
}
set_Value ("iva1_ad_altacom_bco", iva1_ad_altacom_bco);
}
/** Get iva1_ad_altacom_bco */
public String getiva1_ad_altacom_bco() 
{
return (String)get_Value("iva1_ad_altacom_bco");
}
/** Set iva1_ad_cupmanu */
public void setiva1_ad_cupmanu (String iva1_ad_cupmanu)
{
if (iva1_ad_cupmanu != null && iva1_ad_cupmanu.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_ad_cupmanu = iva1_ad_cupmanu.substring(0,32);
}
set_Value ("iva1_ad_cupmanu", iva1_ad_cupmanu);
}
/** Get iva1_ad_cupmanu */
public String getiva1_ad_cupmanu() 
{
return (String)get_Value("iva1_ad_cupmanu");
}
/** Set iva1_ad_opinter */
public void setiva1_ad_opinter (String iva1_ad_opinter)
{
if (iva1_ad_opinter != null && iva1_ad_opinter.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_ad_opinter = iva1_ad_opinter.substring(0,32);
}
set_Value ("iva1_ad_opinter", iva1_ad_opinter);
}
/** Get iva1_ad_opinter */
public String getiva1_ad_opinter() 
{
return (String)get_Value("iva1_ad_opinter");
}
/** Set iva1_ad_plancuo */
public void setiva1_ad_plancuo (String iva1_ad_plancuo)
{
if (iva1_ad_plancuo != null && iva1_ad_plancuo.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_ad_plancuo = iva1_ad_plancuo.substring(0,32);
}
set_Value ("iva1_ad_plancuo", iva1_ad_plancuo);
}
/** Get iva1_ad_plancuo */
public String getiva1_ad_plancuo() 
{
return (String)get_Value("iva1_ad_plancuo");
}
/** Set iva1_cargo_x_liq */
public void setiva1_cargo_x_liq (String iva1_cargo_x_liq)
{
if (iva1_cargo_x_liq != null && iva1_cargo_x_liq.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_cargo_x_liq = iva1_cargo_x_liq.substring(0,32);
}
set_Value ("iva1_cargo_x_liq", iva1_cargo_x_liq);
}
/** Get iva1_cargo_x_liq */
public String getiva1_cargo_x_liq() 
{
return (String)get_Value("iva1_cargo_x_liq");
}
/** Set iva1_cit_b */
public void setiva1_cit_b (String iva1_cit_b)
{
if (iva1_cit_b != null && iva1_cit_b.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_cit_b = iva1_cit_b.substring(0,32);
}
set_Value ("iva1_cit_b", iva1_cit_b);
}
/** Get iva1_cit_b */
public String getiva1_cit_b() 
{
return (String)get_Value("iva1_cit_b");
}
/** Set iva1_cit_e */
public void setiva1_cit_e (String iva1_cit_e)
{
if (iva1_cit_e != null && iva1_cit_e.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_cit_e = iva1_cit_e.substring(0,32);
}
set_Value ("iva1_cit_e", iva1_cit_e);
}
/** Get iva1_cit_e */
public String getiva1_cit_e() 
{
return (String)get_Value("iva1_cit_e");
}
/** Set iva1_dto_campania */
public void setiva1_dto_campania (String iva1_dto_campania)
{
if (iva1_dto_campania != null && iva1_dto_campania.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_dto_campania = iva1_dto_campania.substring(0,32);
}
set_Value ("iva1_dto_campania", iva1_dto_campania);
}
/** Get iva1_dto_campania */
public String getiva1_dto_campania() 
{
return (String)get_Value("iva1_dto_campania");
}
/** Set iva1_edc_b */
public void setiva1_edc_b (String iva1_edc_b)
{
if (iva1_edc_b != null && iva1_edc_b.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_edc_b = iva1_edc_b.substring(0,32);
}
set_Value ("iva1_edc_b", iva1_edc_b);
}
/** Get iva1_edc_b */
public String getiva1_edc_b() 
{
return (String)get_Value("iva1_edc_b");
}
/** Set iva1_edc_e */
public void setiva1_edc_e (String iva1_edc_e)
{
if (iva1_edc_e != null && iva1_edc_e.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_edc_e = iva1_edc_e.substring(0,32);
}
set_Value ("iva1_edc_e", iva1_edc_e);
}
/** Get iva1_edc_e */
public String getiva1_edc_e() 
{
return (String)get_Value("iva1_edc_e");
}
/** Set iva1_xlj */
public void setiva1_xlj (String iva1_xlj)
{
if (iva1_xlj != null && iva1_xlj.length() > 32)
{
log.warning("Length > 32 - truncated");
iva1_xlj = iva1_xlj.substring(0,32);
}
set_Value ("iva1_xlj", iva1_xlj);
}
/** Get iva1_xlj */
public String getiva1_xlj() 
{
return (String)get_Value("iva1_xlj");
}
/** Set iva2_cit_b */
public void setiva2_cit_b (String iva2_cit_b)
{
if (iva2_cit_b != null && iva2_cit_b.length() > 32)
{
log.warning("Length > 32 - truncated");
iva2_cit_b = iva2_cit_b.substring(0,32);
}
set_Value ("iva2_cit_b", iva2_cit_b);
}
/** Get iva2_cit_b */
public String getiva2_cit_b() 
{
return (String)get_Value("iva2_cit_b");
}
/** Set iva2_cit_e */
public void setiva2_cit_e (String iva2_cit_e)
{
if (iva2_cit_e != null && iva2_cit_e.length() > 32)
{
log.warning("Length > 32 - truncated");
iva2_cit_e = iva2_cit_e.substring(0,32);
}
set_Value ("iva2_cit_e", iva2_cit_e);
}
/** Get iva2_cit_e */
public String getiva2_cit_e() 
{
return (String)get_Value("iva2_cit_e");
}
/** Set iva2_edc_b */
public void setiva2_edc_b (String iva2_edc_b)
{
if (iva2_edc_b != null && iva2_edc_b.length() > 32)
{
log.warning("Length > 32 - truncated");
iva2_edc_b = iva2_edc_b.substring(0,32);
}
set_Value ("iva2_edc_b", iva2_edc_b);
}
/** Get iva2_edc_b */
public String getiva2_edc_b() 
{
return (String)get_Value("iva2_edc_b");
}
/** Set iva2_edc_e */
public void setiva2_edc_e (String iva2_edc_e)
{
if (iva2_edc_e != null && iva2_edc_e.length() > 32)
{
log.warning("Length > 32 - truncated");
iva2_edc_e = iva2_edc_e.substring(0,32);
}
set_Value ("iva2_edc_e", iva2_edc_e);
}
/** Get iva2_edc_e */
public String getiva2_edc_e() 
{
return (String)get_Value("iva2_edc_e");
}
/** Set iva2_xlj */
public void setiva2_xlj (String iva2_xlj)
{
if (iva2_xlj != null && iva2_xlj.length() > 32)
{
log.warning("Length > 32 - truncated");
iva2_xlj = iva2_xlj.substring(0,32);
}
set_Value ("iva2_xlj", iva2_xlj);
}
/** Get iva2_xlj */
public String getiva2_xlj() 
{
return (String)get_Value("iva2_xlj");
}
/** Set I_Visapayments_ID */
public void setI_Visapayments_ID (int I_Visapayments_ID)
{
set_ValueNoCheck ("I_Visapayments_ID", new Integer(I_Visapayments_ID));
}
/** Get I_Visapayments_ID */
public int getI_Visapayments_ID() 
{
Integer ii = (Integer)get_Value("I_Visapayments_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set ley_25063 */
public void setley_25063 (String ley_25063)
{
if (ley_25063 != null && ley_25063.length() > 32)
{
log.warning("Length > 32 - truncated");
ley_25063 = ley_25063.substring(0,32);
}
set_Value ("ley_25063", ley_25063);
}
/** Get ley_25063 */
public String getley_25063() 
{
return (String)get_Value("ley_25063");
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
/** Set nroliq */
public void setnroliq (String nroliq)
{
if (nroliq != null && nroliq.length() > 32)
{
log.warning("Length > 32 - truncated");
nroliq = nroliq.substring(0,32);
}
set_Value ("nroliq", nroliq);
}
/** Get nroliq */
public String getnroliq() 
{
return (String)get_Value("nroliq");
}
/** Set num_com */
public void setnum_com (String num_com)
{
if (num_com != null && num_com.length() > 32)
{
log.warning("Length > 32 - truncated");
num_com = num_com.substring(0,32);
}
set_Value ("num_com", num_com);
}
/** Get num_com */
public String getnum_com() 
{
return (String)get_Value("num_com");
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
/** Set percep_ba */
public void setpercep_ba (String percep_ba)
{
if (percep_ba != null && percep_ba.length() > 32)
{
log.warning("Length > 32 - truncated");
percep_ba = percep_ba.substring(0,32);
}
set_Value ("percep_ba", percep_ba);
}
/** Get percep_ba */
public String getpercep_ba() 
{
return (String)get_Value("percep_ba");
}
/** Set percep_ib_agip */
public void setpercep_ib_agip (String percep_ib_agip)
{
if (percep_ib_agip != null && percep_ib_agip.length() > 32)
{
log.warning("Length > 32 - truncated");
percep_ib_agip = percep_ib_agip.substring(0,32);
}
set_Value ("percep_ib_agip", percep_ib_agip);
}
/** Get percep_ib_agip */
public String getpercep_ib_agip() 
{
return (String)get_Value("percep_ib_agip");
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
/** Set prov_ingbru */
public void setprov_ingbru (String prov_ingbru)
{
if (prov_ingbru != null && prov_ingbru.length() > 32)
{
log.warning("Length > 32 - truncated");
prov_ingbru = prov_ingbru.substring(0,32);
}
set_Value ("prov_ingbru", prov_ingbru);
}
/** Get prov_ingbru */
public String getprov_ingbru() 
{
return (String)get_Value("prov_ingbru");
}
/** Set reten_ib_agip */
public void setreten_ib_agip (String reten_ib_agip)
{
if (reten_ib_agip != null && reten_ib_agip.length() > 32)
{
log.warning("Length > 32 - truncated");
reten_ib_agip = reten_ib_agip.substring(0,32);
}
set_Value ("reten_ib_agip", reten_ib_agip);
}
/** Get reten_ib_agip */
public String getreten_ib_agip() 
{
return (String)get_Value("reten_ib_agip");
}
/** Set retesp */
public void setretesp (String retesp)
{
if (retesp != null && retesp.length() > 32)
{
log.warning("Length > 32 - truncated");
retesp = retesp.substring(0,32);
}
set_Value ("retesp", retesp);
}
/** Get retesp */
public String getretesp() 
{
return (String)get_Value("retesp");
}
/** Set ret_gcias */
public void setret_gcias (String ret_gcias)
{
if (ret_gcias != null && ret_gcias.length() > 32)
{
log.warning("Length > 32 - truncated");
ret_gcias = ret_gcias.substring(0,32);
}
set_Value ("ret_gcias", ret_gcias);
}
/** Get ret_gcias */
public String getret_gcias() 
{
return (String)get_Value("ret_gcias");
}
/** Set ret_ingbru */
public void setret_ingbru (String ret_ingbru)
{
if (ret_ingbru != null && ret_ingbru.length() > 32)
{
log.warning("Length > 32 - truncated");
ret_ingbru = ret_ingbru.substring(0,32);
}
set_Value ("ret_ingbru", ret_ingbru);
}
/** Get ret_ingbru */
public String getret_ingbru() 
{
return (String)get_Value("ret_ingbru");
}
/** Set ret_ingbru2 */
public void setret_ingbru2 (String ret_ingbru2)
{
if (ret_ingbru2 != null && ret_ingbru2.length() > 32)
{
log.warning("Length > 32 - truncated");
ret_ingbru2 = ret_ingbru2.substring(0,32);
}
set_Value ("ret_ingbru2", ret_ingbru2);
}
/** Get ret_ingbru2 */
public String getret_ingbru2() 
{
return (String)get_Value("ret_ingbru2");
}
/** Set ret_iva */
public void setret_iva (String ret_iva)
{
if (ret_iva != null && ret_iva.length() > 32)
{
log.warning("Length > 32 - truncated");
ret_iva = ret_iva.substring(0,32);
}
set_Value ("ret_iva", ret_iva);
}
/** Get ret_iva */
public String getret_iva() 
{
return (String)get_Value("ret_iva");
}
/** Set retiva_cuo1 */
public void setretiva_cuo1 (String retiva_cuo1)
{
if (retiva_cuo1 != null && retiva_cuo1.length() > 32)
{
log.warning("Length > 32 - truncated");
retiva_cuo1 = retiva_cuo1.substring(0,32);
}
set_Value ("retiva_cuo1", retiva_cuo1);
}
/** Get retiva_cuo1 */
public String getretiva_cuo1() 
{
return (String)get_Value("retiva_cuo1");
}
/** Set retiva_cuo2 */
public void setretiva_cuo2 (String retiva_cuo2)
{
if (retiva_cuo2 != null && retiva_cuo2.length() > 32)
{
log.warning("Length > 32 - truncated");
retiva_cuo2 = retiva_cuo2.substring(0,32);
}
set_Value ("retiva_cuo2", retiva_cuo2);
}
/** Get retiva_cuo2 */
public String getretiva_cuo2() 
{
return (String)get_Value("retiva_cuo2");
}
/** Set retiva_d1 */
public void setretiva_d1 (String retiva_d1)
{
if (retiva_d1 != null && retiva_d1.length() > 32)
{
log.warning("Length > 32 - truncated");
retiva_d1 = retiva_d1.substring(0,32);
}
set_Value ("retiva_d1", retiva_d1);
}
/** Get retiva_d1 */
public String getretiva_d1() 
{
return (String)get_Value("retiva_d1");
}
/** Set retiva_d2 */
public void setretiva_d2 (String retiva_d2)
{
if (retiva_d2 != null && retiva_d2.length() > 32)
{
log.warning("Length > 32 - truncated");
retiva_d2 = retiva_d2.substring(0,32);
}
set_Value ("retiva_d2", retiva_d2);
}
/** Get retiva_d2 */
public String getretiva_d2() 
{
return (String)get_Value("retiva_d2");
}
/** Set retiva_esp */
public void setretiva_esp (String retiva_esp)
{
if (retiva_esp != null && retiva_esp.length() > 32)
{
log.warning("Length > 32 - truncated");
retiva_esp = retiva_esp.substring(0,32);
}
set_Value ("retiva_esp", retiva_esp);
}
/** Get retiva_esp */
public String getretiva_esp() 
{
return (String)get_Value("retiva_esp");
}
/** Set retiva_pex1 */
public void setretiva_pex1 (String retiva_pex1)
{
if (retiva_pex1 != null && retiva_pex1.length() > 32)
{
log.warning("Length > 32 - truncated");
retiva_pex1 = retiva_pex1.substring(0,32);
}
set_Value ("retiva_pex1", retiva_pex1);
}
/** Get retiva_pex1 */
public String getretiva_pex1() 
{
return (String)get_Value("retiva_pex1");
}
/** Set retiva_pex2 */
public void setretiva_pex2 (String retiva_pex2)
{
if (retiva_pex2 != null && retiva_pex2.length() > 32)
{
log.warning("Length > 32 - truncated");
retiva_pex2 = retiva_pex2.substring(0,32);
}
set_Value ("retiva_pex2", retiva_pex2);
}
/** Get retiva_pex2 */
public String getretiva_pex2() 
{
return (String)get_Value("retiva_pex2");
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
/** Set signo_04_1 */
public void setsigno_04_1 (String signo_04_1)
{
if (signo_04_1 != null && signo_04_1.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_1 = signo_04_1.substring(0,32);
}
set_Value ("signo_04_1", signo_04_1);
}
/** Get signo_04_1 */
public String getsigno_04_1() 
{
return (String)get_Value("signo_04_1");
}
/** Set signo_04_10 */
public void setsigno_04_10 (String signo_04_10)
{
if (signo_04_10 != null && signo_04_10.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_10 = signo_04_10.substring(0,32);
}
set_Value ("signo_04_10", signo_04_10);
}
/** Get signo_04_10 */
public String getsigno_04_10() 
{
return (String)get_Value("signo_04_10");
}
/** Set signo_04_11 */
public void setsigno_04_11 (String signo_04_11)
{
if (signo_04_11 != null && signo_04_11.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_11 = signo_04_11.substring(0,32);
}
set_Value ("signo_04_11", signo_04_11);
}
/** Get signo_04_11 */
public String getsigno_04_11() 
{
return (String)get_Value("signo_04_11");
}
/** Set signo_04_12 */
public void setsigno_04_12 (String signo_04_12)
{
if (signo_04_12 != null && signo_04_12.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_12 = signo_04_12.substring(0,32);
}
set_Value ("signo_04_12", signo_04_12);
}
/** Get signo_04_12 */
public String getsigno_04_12() 
{
return (String)get_Value("signo_04_12");
}
/** Set signo_04_13 */
public void setsigno_04_13 (String signo_04_13)
{
if (signo_04_13 != null && signo_04_13.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_13 = signo_04_13.substring(0,32);
}
set_Value ("signo_04_13", signo_04_13);
}
/** Get signo_04_13 */
public String getsigno_04_13() 
{
return (String)get_Value("signo_04_13");
}
/** Set signo_04_14 */
public void setsigno_04_14 (String signo_04_14)
{
if (signo_04_14 != null && signo_04_14.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_14 = signo_04_14.substring(0,32);
}
set_Value ("signo_04_14", signo_04_14);
}
/** Get signo_04_14 */
public String getsigno_04_14() 
{
return (String)get_Value("signo_04_14");
}
/** Set signo_04_15 */
public void setsigno_04_15 (String signo_04_15)
{
if (signo_04_15 != null && signo_04_15.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_15 = signo_04_15.substring(0,32);
}
set_Value ("signo_04_15", signo_04_15);
}
/** Get signo_04_15 */
public String getsigno_04_15() 
{
return (String)get_Value("signo_04_15");
}
/** Set signo_04_16 */
public void setsigno_04_16 (String signo_04_16)
{
if (signo_04_16 != null && signo_04_16.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_16 = signo_04_16.substring(0,32);
}
set_Value ("signo_04_16", signo_04_16);
}
/** Get signo_04_16 */
public String getsigno_04_16() 
{
return (String)get_Value("signo_04_16");
}
/** Set signo_04_17 */
public void setsigno_04_17 (String signo_04_17)
{
if (signo_04_17 != null && signo_04_17.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_17 = signo_04_17.substring(0,32);
}
set_Value ("signo_04_17", signo_04_17);
}
/** Get signo_04_17 */
public String getsigno_04_17() 
{
return (String)get_Value("signo_04_17");
}
/** Set signo_04_18 */
public void setsigno_04_18 (String signo_04_18)
{
if (signo_04_18 != null && signo_04_18.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_18 = signo_04_18.substring(0,32);
}
set_Value ("signo_04_18", signo_04_18);
}
/** Get signo_04_18 */
public String getsigno_04_18() 
{
return (String)get_Value("signo_04_18");
}
/** Set signo_04_19 */
public void setsigno_04_19 (String signo_04_19)
{
if (signo_04_19 != null && signo_04_19.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_19 = signo_04_19.substring(0,32);
}
set_Value ("signo_04_19", signo_04_19);
}
/** Get signo_04_19 */
public String getsigno_04_19() 
{
return (String)get_Value("signo_04_19");
}
/** Set signo_04_2 */
public void setsigno_04_2 (String signo_04_2)
{
if (signo_04_2 != null && signo_04_2.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_2 = signo_04_2.substring(0,32);
}
set_Value ("signo_04_2", signo_04_2);
}
/** Get signo_04_2 */
public String getsigno_04_2() 
{
return (String)get_Value("signo_04_2");
}
/** Set signo_04_20 */
public void setsigno_04_20 (String signo_04_20)
{
if (signo_04_20 != null && signo_04_20.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_20 = signo_04_20.substring(0,32);
}
set_Value ("signo_04_20", signo_04_20);
}
/** Get signo_04_20 */
public String getsigno_04_20() 
{
return (String)get_Value("signo_04_20");
}
/** Set signo_04_21 */
public void setsigno_04_21 (String signo_04_21)
{
if (signo_04_21 != null && signo_04_21.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_21 = signo_04_21.substring(0,32);
}
set_Value ("signo_04_21", signo_04_21);
}
/** Get signo_04_21 */
public String getsigno_04_21() 
{
return (String)get_Value("signo_04_21");
}
/** Set signo_04_22 */
public void setsigno_04_22 (String signo_04_22)
{
if (signo_04_22 != null && signo_04_22.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_22 = signo_04_22.substring(0,32);
}
set_Value ("signo_04_22", signo_04_22);
}
/** Get signo_04_22 */
public String getsigno_04_22() 
{
return (String)get_Value("signo_04_22");
}
/** Set signo_04_23 */
public void setsigno_04_23 (String signo_04_23)
{
if (signo_04_23 != null && signo_04_23.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_23 = signo_04_23.substring(0,32);
}
set_Value ("signo_04_23", signo_04_23);
}
/** Get signo_04_23 */
public String getsigno_04_23() 
{
return (String)get_Value("signo_04_23");
}
/** Set signo_04_24 */
public void setsigno_04_24 (String signo_04_24)
{
if (signo_04_24 != null && signo_04_24.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_24 = signo_04_24.substring(0,32);
}
set_Value ("signo_04_24", signo_04_24);
}
/** Get signo_04_24 */
public String getsigno_04_24() 
{
return (String)get_Value("signo_04_24");
}
/** Set signo_04_3 */
public void setsigno_04_3 (String signo_04_3)
{
if (signo_04_3 != null && signo_04_3.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_3 = signo_04_3.substring(0,32);
}
set_Value ("signo_04_3", signo_04_3);
}
/** Get signo_04_3 */
public String getsigno_04_3() 
{
return (String)get_Value("signo_04_3");
}
/** Set signo_04_4 */
public void setsigno_04_4 (String signo_04_4)
{
if (signo_04_4 != null && signo_04_4.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_4 = signo_04_4.substring(0,32);
}
set_Value ("signo_04_4", signo_04_4);
}
/** Get signo_04_4 */
public String getsigno_04_4() 
{
return (String)get_Value("signo_04_4");
}
/** Set signo_04_5 */
public void setsigno_04_5 (String signo_04_5)
{
if (signo_04_5 != null && signo_04_5.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_5 = signo_04_5.substring(0,32);
}
set_Value ("signo_04_5", signo_04_5);
}
/** Get signo_04_5 */
public String getsigno_04_5() 
{
return (String)get_Value("signo_04_5");
}
/** Set signo_04_8 */
public void setsigno_04_8 (String signo_04_8)
{
if (signo_04_8 != null && signo_04_8.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_8 = signo_04_8.substring(0,32);
}
set_Value ("signo_04_8", signo_04_8);
}
/** Get signo_04_8 */
public String getsigno_04_8() 
{
return (String)get_Value("signo_04_8");
}
/** Set signo_04_9 */
public void setsigno_04_9 (String signo_04_9)
{
if (signo_04_9 != null && signo_04_9.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_04_9 = signo_04_9.substring(0,32);
}
set_Value ("signo_04_9", signo_04_9);
}
/** Get signo_04_9 */
public String getsigno_04_9() 
{
return (String)get_Value("signo_04_9");
}
/** Set signo_1 */
public void setsigno_1 (String signo_1)
{
if (signo_1 != null && signo_1.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_1 = signo_1.substring(0,32);
}
set_Value ("signo_1", signo_1);
}
/** Get signo_1 */
public String getsigno_1() 
{
return (String)get_Value("signo_1");
}
/** Set signo_10 */
public void setsigno_10 (String signo_10)
{
if (signo_10 != null && signo_10.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_10 = signo_10.substring(0,32);
}
set_Value ("signo_10", signo_10);
}
/** Get signo_10 */
public String getsigno_10() 
{
return (String)get_Value("signo_10");
}
/** Set signo_11 */
public void setsigno_11 (String signo_11)
{
if (signo_11 != null && signo_11.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_11 = signo_11.substring(0,32);
}
set_Value ("signo_11", signo_11);
}
/** Get signo_11 */
public String getsigno_11() 
{
return (String)get_Value("signo_11");
}
/** Set signo_12 */
public void setsigno_12 (String signo_12)
{
if (signo_12 != null && signo_12.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_12 = signo_12.substring(0,32);
}
set_Value ("signo_12", signo_12);
}
/** Get signo_12 */
public String getsigno_12() 
{
return (String)get_Value("signo_12");
}
/** Set signo_13 */
public void setsigno_13 (String signo_13)
{
if (signo_13 != null && signo_13.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_13 = signo_13.substring(0,32);
}
set_Value ("signo_13", signo_13);
}
/** Get signo_13 */
public String getsigno_13() 
{
return (String)get_Value("signo_13");
}
/** Set signo_14 */
public void setsigno_14 (String signo_14)
{
if (signo_14 != null && signo_14.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_14 = signo_14.substring(0,32);
}
set_Value ("signo_14", signo_14);
}
/** Get signo_14 */
public String getsigno_14() 
{
return (String)get_Value("signo_14");
}
/** Set signo_15 */
public void setsigno_15 (String signo_15)
{
if (signo_15 != null && signo_15.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_15 = signo_15.substring(0,32);
}
set_Value ("signo_15", signo_15);
}
/** Get signo_15 */
public String getsigno_15() 
{
return (String)get_Value("signo_15");
}
/** Set signo_16 */
public void setsigno_16 (String signo_16)
{
if (signo_16 != null && signo_16.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_16 = signo_16.substring(0,32);
}
set_Value ("signo_16", signo_16);
}
/** Get signo_16 */
public String getsigno_16() 
{
return (String)get_Value("signo_16");
}
/** Set signo_17 */
public void setsigno_17 (String signo_17)
{
if (signo_17 != null && signo_17.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_17 = signo_17.substring(0,32);
}
set_Value ("signo_17", signo_17);
}
/** Get signo_17 */
public String getsigno_17() 
{
return (String)get_Value("signo_17");
}
/** Set signo_18 */
public void setsigno_18 (String signo_18)
{
if (signo_18 != null && signo_18.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_18 = signo_18.substring(0,32);
}
set_Value ("signo_18", signo_18);
}
/** Get signo_18 */
public String getsigno_18() 
{
return (String)get_Value("signo_18");
}
/** Set signo_19 */
public void setsigno_19 (String signo_19)
{
if (signo_19 != null && signo_19.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_19 = signo_19.substring(0,32);
}
set_Value ("signo_19", signo_19);
}
/** Get signo_19 */
public String getsigno_19() 
{
return (String)get_Value("signo_19");
}
/** Set signo_2 */
public void setsigno_2 (String signo_2)
{
if (signo_2 != null && signo_2.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_2 = signo_2.substring(0,32);
}
set_Value ("signo_2", signo_2);
}
/** Get signo_2 */
public String getsigno_2() 
{
return (String)get_Value("signo_2");
}
/** Set signo_20 */
public void setsigno_20 (String signo_20)
{
if (signo_20 != null && signo_20.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_20 = signo_20.substring(0,32);
}
set_Value ("signo_20", signo_20);
}
/** Get signo_20 */
public String getsigno_20() 
{
return (String)get_Value("signo_20");
}
/** Set signo_21 */
public void setsigno_21 (String signo_21)
{
if (signo_21 != null && signo_21.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_21 = signo_21.substring(0,32);
}
set_Value ("signo_21", signo_21);
}
/** Get signo_21 */
public String getsigno_21() 
{
return (String)get_Value("signo_21");
}
/** Set signo_22 */
public void setsigno_22 (String signo_22)
{
if (signo_22 != null && signo_22.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_22 = signo_22.substring(0,32);
}
set_Value ("signo_22", signo_22);
}
/** Get signo_22 */
public String getsigno_22() 
{
return (String)get_Value("signo_22");
}
/** Set signo_23 */
public void setsigno_23 (String signo_23)
{
if (signo_23 != null && signo_23.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_23 = signo_23.substring(0,32);
}
set_Value ("signo_23", signo_23);
}
/** Get signo_23 */
public String getsigno_23() 
{
return (String)get_Value("signo_23");
}
/** Set signo_24 */
public void setsigno_24 (String signo_24)
{
if (signo_24 != null && signo_24.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_24 = signo_24.substring(0,32);
}
set_Value ("signo_24", signo_24);
}
/** Get signo_24 */
public String getsigno_24() 
{
return (String)get_Value("signo_24");
}
/** Set signo_25 */
public void setsigno_25 (String signo_25)
{
if (signo_25 != null && signo_25.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_25 = signo_25.substring(0,32);
}
set_Value ("signo_25", signo_25);
}
/** Get signo_25 */
public String getsigno_25() 
{
return (String)get_Value("signo_25");
}
/** Set signo_26 */
public void setsigno_26 (String signo_26)
{
if (signo_26 != null && signo_26.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_26 = signo_26.substring(0,32);
}
set_Value ("signo_26", signo_26);
}
/** Get signo_26 */
public String getsigno_26() 
{
return (String)get_Value("signo_26");
}
/** Set signo_27 */
public void setsigno_27 (String signo_27)
{
if (signo_27 != null && signo_27.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_27 = signo_27.substring(0,32);
}
set_Value ("signo_27", signo_27);
}
/** Get signo_27 */
public String getsigno_27() 
{
return (String)get_Value("signo_27");
}
/** Set signo_28 */
public void setsigno_28 (String signo_28)
{
if (signo_28 != null && signo_28.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_28 = signo_28.substring(0,32);
}
set_Value ("signo_28", signo_28);
}
/** Get signo_28 */
public String getsigno_28() 
{
return (String)get_Value("signo_28");
}
/** Set signo_29 */
public void setsigno_29 (String signo_29)
{
if (signo_29 != null && signo_29.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_29 = signo_29.substring(0,32);
}
set_Value ("signo_29", signo_29);
}
/** Get signo_29 */
public String getsigno_29() 
{
return (String)get_Value("signo_29");
}
/** Set signo_3 */
public void setsigno_3 (String signo_3)
{
if (signo_3 != null && signo_3.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_3 = signo_3.substring(0,32);
}
set_Value ("signo_3", signo_3);
}
/** Get signo_3 */
public String getsigno_3() 
{
return (String)get_Value("signo_3");
}
/** Set signo_30 */
public void setsigno_30 (String signo_30)
{
if (signo_30 != null && signo_30.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_30 = signo_30.substring(0,32);
}
set_Value ("signo_30", signo_30);
}
/** Get signo_30 */
public String getsigno_30() 
{
return (String)get_Value("signo_30");
}
/** Set signo_31 */
public void setsigno_31 (String signo_31)
{
if (signo_31 != null && signo_31.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_31 = signo_31.substring(0,32);
}
set_Value ("signo_31", signo_31);
}
/** Get signo_31 */
public String getsigno_31() 
{
return (String)get_Value("signo_31");
}
/** Set signo_32 */
public void setsigno_32 (String signo_32)
{
if (signo_32 != null && signo_32.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_32 = signo_32.substring(0,32);
}
set_Value ("signo_32", signo_32);
}
/** Get signo_32 */
public String getsigno_32() 
{
return (String)get_Value("signo_32");
}
/** Set signo_4 */
public void setsigno_4 (String signo_4)
{
if (signo_4 != null && signo_4.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_4 = signo_4.substring(0,32);
}
set_Value ("signo_4", signo_4);
}
/** Get signo_4 */
public String getsigno_4() 
{
return (String)get_Value("signo_4");
}
/** Set signo_5 */
public void setsigno_5 (String signo_5)
{
if (signo_5 != null && signo_5.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_5 = signo_5.substring(0,32);
}
set_Value ("signo_5", signo_5);
}
/** Get signo_5 */
public String getsigno_5() 
{
return (String)get_Value("signo_5");
}
/** Set signo_6 */
public void setsigno_6 (String signo_6)
{
if (signo_6 != null && signo_6.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_6 = signo_6.substring(0,32);
}
set_Value ("signo_6", signo_6);
}
/** Get signo_6 */
public String getsigno_6() 
{
return (String)get_Value("signo_6");
}
/** Set signo_7 */
public void setsigno_7 (String signo_7)
{
if (signo_7 != null && signo_7.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_7 = signo_7.substring(0,32);
}
set_Value ("signo_7", signo_7);
}
/** Get signo_7 */
public String getsigno_7() 
{
return (String)get_Value("signo_7");
}
/** Set signo_8 */
public void setsigno_8 (String signo_8)
{
if (signo_8 != null && signo_8.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_8 = signo_8.substring(0,32);
}
set_Value ("signo_8", signo_8);
}
/** Get signo_8 */
public String getsigno_8() 
{
return (String)get_Value("signo_8");
}
/** Set signo_9 */
public void setsigno_9 (String signo_9)
{
if (signo_9 != null && signo_9.length() > 32)
{
log.warning("Length > 32 - truncated");
signo_9 = signo_9.substring(0,32);
}
set_Value ("signo_9", signo_9);
}
/** Get signo_9 */
public String getsigno_9() 
{
return (String)get_Value("signo_9");
}
/** Set subtot_retiva_rg3130 */
public void setsubtot_retiva_rg3130 (String subtot_retiva_rg3130)
{
if (subtot_retiva_rg3130 != null && subtot_retiva_rg3130.length() > 32)
{
log.warning("Length > 32 - truncated");
subtot_retiva_rg3130 = subtot_retiva_rg3130.substring(0,32);
}
set_Value ("subtot_retiva_rg3130", subtot_retiva_rg3130);
}
/** Get subtot_retiva_rg3130 */
public String getsubtot_retiva_rg3130() 
{
return (String)get_Value("subtot_retiva_rg3130");
}
/** Set tasa_pex */
public void settasa_pex (String tasa_pex)
{
if (tasa_pex != null && tasa_pex.length() > 32)
{
log.warning("Length > 32 - truncated");
tasa_pex = tasa_pex.substring(0,32);
}
set_Value ("tasa_pex", tasa_pex);
}
/** Get tasa_pex */
public String gettasa_pex() 
{
return (String)get_Value("tasa_pex");
}
/** Set tipcta */
public void settipcta (String tipcta)
{
if (tipcta != null && tipcta.length() > 32)
{
log.warning("Length > 32 - truncated");
tipcta = tipcta.substring(0,32);
}
set_Value ("tipcta", tipcta);
}
/** Get tipcta */
public String gettipcta() 
{
return (String)get_Value("tipcta");
}
/** Set tipoliq */
public void settipoliq (String tipoliq)
{
if (tipoliq != null && tipoliq.length() > 32)
{
log.warning("Length > 32 - truncated");
tipoliq = tipoliq.substring(0,32);
}
set_Value ("tipoliq", tipoliq);
}
/** Get tipoliq */
public String gettipoliq() 
{
return (String)get_Value("tipoliq");
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
