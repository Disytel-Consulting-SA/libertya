/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_FideliusLiquidaciones
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2022-11-09 17:24:58.1 */
public class X_I_FideliusLiquidaciones extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_FideliusLiquidaciones (Properties ctx, int I_FideliusLiquidaciones_ID, String trxName)
{
super (ctx, I_FideliusLiquidaciones_ID, trxName);
/** if (I_FideliusLiquidaciones_ID == 0)
{
setI_Fideliusliquidaciones_ID (0);
setI_IsImported (false);
}
 */
}
/** Load Constructor */
public X_I_FideliusLiquidaciones (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_FideliusLiquidaciones");

/** TableName=I_FideliusLiquidaciones */
public static final String Table_Name="I_FideliusLiquidaciones";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_FideliusLiquidaciones");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_FideliusLiquidaciones[").append(getID()).append("]");
return sb.toString();
}
/** Set anticipo */
public void setanticipo (String anticipo)
{
if (anticipo != null && anticipo.length() > 32)
{
log.warning("Length > 32 - truncated");
anticipo = anticipo.substring(0,32);
}
set_Value ("anticipo", anticipo);
}
/** Get anticipo */
public String getanticipo() 
{
return (String)get_Value("anticipo");
}
/** Set arancel */
public void setarancel (String arancel)
{
if (arancel != null && arancel.length() > 32)
{
log.warning("Length > 32 - truncated");
arancel = arancel.substring(0,32);
}
set_Value ("arancel", arancel);
}
/** Get arancel */
public String getarancel() 
{
return (String)get_Value("arancel");
}
/** Set archivo_id */
public void setarchivo_id (int archivo_id)
{
set_Value ("archivo_id", new Integer(archivo_id));
}
/** Get archivo_id */
public int getarchivo_id() 
{
Integer ii = (Integer)get_Value("archivo_id");
if (ii == null) return 0;
return ii.intValue();
}
/** Set bancopag */
public void setbancopag (String bancopag)
{
if (bancopag != null && bancopag.length() > 32)
{
log.warning("Length > 32 - truncated");
bancopag = bancopag.substring(0,32);
}
set_Value ("bancopag", bancopag);
}
/** Get bancopag */
public String getbancopag() 
{
return (String)get_Value("bancopag");
}
/** Set cfo_105 */
public void setcfo_105 (String cfo_105)
{
if (cfo_105 != null && cfo_105.length() > 32)
{
log.warning("Length > 32 - truncated");
cfo_105 = cfo_105.substring(0,32);
}
set_Value ("cfo_105", cfo_105);
}
/** Get cfo_105 */
public String getcfo_105() 
{
return (String)get_Value("cfo_105");
}
/** Set cfo_21 */
public void setcfo_21 (String cfo_21)
{
if (cfo_21 != null && cfo_21.length() > 32)
{
log.warning("Length > 32 - truncated");
cfo_21 = cfo_21.substring(0,32);
}
set_Value ("cfo_21", cfo_21);
}
/** Get cfo_21 */
public String getcfo_21() 
{
return (String)get_Value("cfo_21");
}
/** Set cfo_adel */
public void setcfo_adel (String cfo_adel)
{
if (cfo_adel != null && cfo_adel.length() > 32)
{
log.warning("Length > 32 - truncated");
cfo_adel = cfo_adel.substring(0,32);
}
set_Value ("cfo_adel", cfo_adel);
}
/** Get cfo_adel */
public String getcfo_adel() 
{
return (String)get_Value("cfo_adel");
}
/** Set cfo_total */
public void setcfo_total (String cfo_total)
{
if (cfo_total != null && cfo_total.length() > 32)
{
log.warning("Length > 32 - truncated");
cfo_total = cfo_total.substring(0,32);
}
set_Value ("cfo_total", cfo_total);
}
/** Get cfo_total */
public String getcfo_total() 
{
return (String)get_Value("cfo_total");
}
/** Set CUIT */
public void setCUIT (String CUIT)
{
if (CUIT != null && CUIT.length() > 32)
{
log.warning("Length > 32 - truncated");
CUIT = CUIT.substring(0,32);
}
set_Value ("CUIT", CUIT);
}
/** Get CUIT */
public String getCUIT() 
{
return (String)get_Value("CUIT");
}
/** Set deb_cred */
public void setdeb_cred (String deb_cred)
{
if (deb_cred != null && deb_cred.length() > 32)
{
log.warning("Length > 32 - truncated");
deb_cred = deb_cred.substring(0,32);
}
set_Value ("deb_cred", deb_cred);
}
/** Get deb_cred */
public String getdeb_cred() 
{
return (String)get_Value("deb_cred");
}
/** Set dto_financ */
public void setdto_financ (String dto_financ)
{
if (dto_financ != null && dto_financ.length() > 32)
{
log.warning("Length > 32 - truncated");
dto_financ = dto_financ.substring(0,32);
}
set_Value ("dto_financ", dto_financ);
}
/** Get dto_financ */
public String getdto_financ() 
{
return (String)get_Value("dto_financ");
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
/** Set fant */
public void setfant (String fant)
{
if (fant != null && fant.length() > 32)
{
log.warning("Length > 32 - truncated");
fant = fant.substring(0,32);
}
set_Value ("fant", fant);
}
/** Get fant */
public String getfant() 
{
return (String)get_Value("fant");
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
/** Set I_Fideliusliquidaciones_ID */
public void setI_Fideliusliquidaciones_ID (int I_Fideliusliquidaciones_ID)
{
set_ValueNoCheck ("I_Fideliusliquidaciones_ID", new Integer(I_Fideliusliquidaciones_ID));
}
/** Get I_Fideliusliquidaciones_ID */
public int getI_Fideliusliquidaciones_ID() 
{
Integer ii = (Integer)get_Value("I_Fideliusliquidaciones_ID");
if (ii == null) return 0;
return ii.intValue();
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
/** Set iva_adel21 */
public void setiva_adel21 (String iva_adel21)
{
if (iva_adel21 != null && iva_adel21.length() > 32)
{
log.warning("Length > 32 - truncated");
iva_adel21 = iva_adel21.substring(0,32);
}
set_Value ("iva_adel21", iva_adel21);
}
/** Get iva_adel21 */
public String getiva_adel21() 
{
return (String)get_Value("iva_adel21");
}
/** Set iva_arancel */
public void setiva_arancel (String iva_arancel)
{
if (iva_arancel != null && iva_arancel.length() > 32)
{
log.warning("Length > 32 - truncated");
iva_arancel = iva_arancel.substring(0,32);
}
set_Value ("iva_arancel", iva_arancel);
}
/** Get iva_arancel */
public String getiva_arancel() 
{
return (String)get_Value("iva_arancel");
}
/** Set iva_cfo105 */
public void setiva_cfo105 (String iva_cfo105)
{
if (iva_cfo105 != null && iva_cfo105.length() > 32)
{
log.warning("Length > 32 - truncated");
iva_cfo105 = iva_cfo105.substring(0,32);
}
set_Value ("iva_cfo105", iva_cfo105);
}
/** Get iva_cfo105 */
public String getiva_cfo105() 
{
return (String)get_Value("iva_cfo105");
}
/** Set iva_cfo21 */
public void setiva_cfo21 (String iva_cfo21)
{
if (iva_cfo21 != null && iva_cfo21.length() > 32)
{
log.warning("Length > 32 - truncated");
iva_cfo21 = iva_cfo21.substring(0,32);
}
set_Value ("iva_cfo21", iva_cfo21);
}
/** Get iva_cfo21 */
public String getiva_cfo21() 
{
return (String)get_Value("iva_cfo21");
}
/** Set iva_dtofinanc */
public void setiva_dtofinanc (String iva_dtofinanc)
{
if (iva_dtofinanc != null && iva_dtofinanc.length() > 32)
{
log.warning("Length > 32 - truncated");
iva_dtofinanc = iva_dtofinanc.substring(0,32);
}
set_Value ("iva_dtofinanc", iva_dtofinanc);
}
/** Get iva_dtofinanc */
public String getiva_dtofinanc() 
{
return (String)get_Value("iva_dtofinanc");
}
/** Set iva_otros */
public void setiva_otros (String iva_otros)
{
if (iva_otros != null && iva_otros.length() > 32)
{
log.warning("Length > 32 - truncated");
iva_otros = iva_otros.substring(0,32);
}
set_Value ("iva_otros", iva_otros);
}
/** Get iva_otros */
public String getiva_otros() 
{
return (String)get_Value("iva_otros");
}
/** Set iva_plana1218 */
public void setiva_plana1218 (String iva_plana1218)
{
if (iva_plana1218 != null && iva_plana1218.length() > 32)
{
log.warning("Length > 32 - truncated");
iva_plana1218 = iva_plana1218.substring(0,32);
}
set_Value ("iva_plana1218", iva_plana1218);
}
/** Get iva_plana1218 */
public String getiva_plana1218() 
{
return (String)get_Value("iva_plana1218");
}
/** Set iva_total */
public void setiva_total (String iva_total)
{
if (iva_total != null && iva_total.length() > 32)
{
log.warning("Length > 32 - truncated");
iva_total = iva_total.substring(0,32);
}
set_Value ("iva_total", iva_total);
}
/** Get iva_total */
public String getiva_total() 
{
return (String)get_Value("iva_total");
}
/** Set liq_anttn */
public void setliq_anttn (String liq_anttn)
{
if (liq_anttn != null && liq_anttn.length() > 32)
{
log.warning("Length > 32 - truncated");
liq_anttn = liq_anttn.substring(0,32);
}
set_Value ("liq_anttn", liq_anttn);
}
/** Get liq_anttn */
public String getliq_anttn() 
{
return (String)get_Value("liq_anttn");
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
/** Set otros_costos */
public void setotros_costos (String otros_costos)
{
if (otros_costos != null && otros_costos.length() > 32)
{
log.warning("Length > 32 - truncated");
otros_costos = otros_costos.substring(0,32);
}
set_Value ("otros_costos", otros_costos);
}
/** Get otros_costos */
public String getotros_costos() 
{
return (String)get_Value("otros_costos");
}
/** Set pciaiibb */
public void setpciaiibb (String pciaiibb)
{
if (pciaiibb != null && pciaiibb.length() > 32)
{
log.warning("Length > 32 - truncated");
pciaiibb = pciaiibb.substring(0,32);
}
set_Value ("pciaiibb", pciaiibb);
}
/** Get pciaiibb */
public String getpciaiibb() 
{
return (String)get_Value("pciaiibb");
}
/** Set perc_1135tn */
public void setperc_1135tn (String perc_1135tn)
{
if (perc_1135tn != null && perc_1135tn.length() > 32)
{
log.warning("Length > 32 - truncated");
perc_1135tn = perc_1135tn.substring(0,32);
}
set_Value ("perc_1135tn", perc_1135tn);
}
/** Get perc_1135tn */
public String getperc_1135tn() 
{
return (String)get_Value("perc_1135tn");
}
/** Set perc_iibb */
public void setperc_iibb (String perc_iibb)
{
if (perc_iibb != null && perc_iibb.length() > 32)
{
log.warning("Length > 32 - truncated");
perc_iibb = perc_iibb.substring(0,32);
}
set_Value ("perc_iibb", perc_iibb);
}
/** Get perc_iibb */
public String getperc_iibb() 
{
return (String)get_Value("perc_iibb");
}
/** Set perc_iva */
public void setperc_iva (String perc_iva)
{
if (perc_iva != null && perc_iva.length() > 32)
{
log.warning("Length > 32 - truncated");
perc_iva = perc_iva.substring(0,32);
}
set_Value ("perc_iva", perc_iva);
}
/** Get perc_iva */
public String getperc_iva() 
{
return (String)get_Value("perc_iva");
}
/** Set plan_a1218 */
public void setplan_a1218 (String plan_a1218)
{
if (plan_a1218 != null && plan_a1218.length() > 32)
{
log.warning("Length > 32 - truncated");
plan_a1218 = plan_a1218.substring(0,32);
}
set_Value ("plan_a1218", plan_a1218);
}
/** Get plan_a1218 */
public String getplan_a1218() 
{
return (String)get_Value("plan_a1218");
}
/** Set porc_ivaplana1218 */
public void setporc_ivaplana1218 (String porc_ivaplana1218)
{
if (porc_ivaplana1218 != null && porc_ivaplana1218.length() > 32)
{
log.warning("Length > 32 - truncated");
porc_ivaplana1218 = porc_ivaplana1218.substring(0,32);
}
set_Value ("porc_ivaplana1218", porc_ivaplana1218);
}
/** Get porc_ivaplana1218 */
public String getporc_ivaplana1218() 
{
return (String)get_Value("porc_ivaplana1218");
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
/** Set promo */
public void setpromo (String promo)
{
if (promo != null && promo.length() > 32)
{
log.warning("Length > 32 - truncated");
promo = promo.substring(0,32);
}
set_Value ("promo", promo);
}
/** Get promo */
public String getpromo() 
{
return (String)get_Value("promo");
}
/** Set ret_gcia */
public void setret_gcia (String ret_gcia)
{
if (ret_gcia != null && ret_gcia.length() > 32)
{
log.warning("Length > 32 - truncated");
ret_gcia = ret_gcia.substring(0,32);
}
set_Value ("ret_gcia", ret_gcia);
}
/** Get ret_gcia */
public String getret_gcia() 
{
return (String)get_Value("ret_gcia");
}
/** Set ret_ibsirtac */
public void setret_ibsirtac (String ret_ibsirtac)
{
if (ret_ibsirtac != null && ret_ibsirtac.length() > 32)
{
log.warning("Length > 32 - truncated");
ret_ibsirtac = ret_ibsirtac.substring(0,32);
}
set_Value ("ret_ibsirtac", ret_ibsirtac);
}
/** Get ret_ibsirtac */
public String getret_ibsirtac() 
{
return (String)get_Value("ret_ibsirtac");
}
/** Set ret_iibb */
public void setret_iibb (String ret_iibb)
{
if (ret_iibb != null && ret_iibb.length() > 32)
{
log.warning("Length > 32 - truncated");
ret_iibb = ret_iibb.substring(0,32);
}
set_Value ("ret_iibb", ret_iibb);
}
/** Get ret_iibb */
public String getret_iibb() 
{
return (String)get_Value("ret_iibb");
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
/** Set ret_munic */
public void setret_munic (String ret_munic)
{
if (ret_munic != null && ret_munic.length() > 32)
{
log.warning("Length > 32 - truncated");
ret_munic = ret_munic.substring(0,32);
}
set_Value ("ret_munic", ret_munic);
}
/** Get ret_munic */
public String getret_munic() 
{
return (String)get_Value("ret_munic");
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
/** Set saldos */
public void setsaldos (String saldos)
{
if (saldos != null && saldos.length() > 32)
{
log.warning("Length > 32 - truncated");
saldos = saldos.substring(0,32);
}
set_Value ("saldos", saldos);
}
/** Get saldos */
public String getsaldos() 
{
return (String)get_Value("saldos");
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
/** Set totdesc */
public void settotdesc (String totdesc)
{
if (totdesc != null && totdesc.length() > 32)
{
log.warning("Length > 32 - truncated");
totdesc = totdesc.substring(0,32);
}
set_Value ("totdesc", totdesc);
}
/** Get totdesc */
public String gettotdesc() 
{
return (String)get_Value("totdesc");
}
}
