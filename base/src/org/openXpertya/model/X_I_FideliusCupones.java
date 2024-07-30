/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_FideliusCupones
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2022-11-23 17:58:38.94 */
public class X_I_FideliusCupones extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_FideliusCupones (Properties ctx, int I_FideliusCupones_ID, String trxName)
{
super (ctx, I_FideliusCupones_ID, trxName);
/** if (I_FideliusCupones_ID == 0)
{
setI_Fideliuscupones_ID (0);
setI_IsImported (false);
}
 */
}
/** Load Constructor */
public X_I_FideliusCupones (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_FideliusCupones");

/** TableName=I_FideliusCupones */
public static final String Table_Name="I_FideliusCupones";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_FideliusCupones");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_FideliusCupones[").append(getID()).append("]");
return sb.toString();
}
/** Set alic_ivacfo */
public void setalic_ivacfo (String alic_ivacfo)
{
if (alic_ivacfo != null && alic_ivacfo.length() > 32)
{
log.warning("Length > 32 - truncated");
alic_ivacfo = alic_ivacfo.substring(0,32);
}
set_Value ("alic_ivacfo", alic_ivacfo);
}
/** Get alic_ivacfo */
public String getalic_ivacfo() 
{
return (String)get_Value("alic_ivacfo");
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
/** Set autorizacion */
public void setautorizacion (String autorizacion)
{
if (autorizacion != null && autorizacion.length() > 32)
{
log.warning("Length > 32 - truncated");
autorizacion = autorizacion.substring(0,32);
}
set_Value ("autorizacion", autorizacion);
}
/** Get autorizacion */
public String getautorizacion() 
{
return (String)get_Value("autorizacion");
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
/** Set C_Couponssettlements_ID */
public void setC_Couponssettlements_ID (int C_Couponssettlements_ID)
{
if (C_Couponssettlements_ID <= 0) set_Value ("C_Couponssettlements_ID", null);
 else 
set_Value ("C_Couponssettlements_ID", new Integer(C_Couponssettlements_ID));
}
/** Get C_Couponssettlements_ID */
public int getC_Couponssettlements_ID() 
{
Integer ii = (Integer)get_Value("C_Couponssettlements_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set cfo */
public void setcfo (String cfo)
{
if (cfo != null && cfo.length() > 32)
{
log.warning("Length > 32 - truncated");
cfo = cfo.substring(0,32);
}
set_Value ("cfo", cfo);
}
/** Get cfo */
public String getcfo() 
{
return (String)get_Value("cfo");
}
/** Set cuotas */
public void setcuotas (String cuotas)
{
if (cuotas != null && cuotas.length() > 32)
{
log.warning("Length > 32 - truncated");
cuotas = cuotas.substring(0,32);
}
set_Value ("cuotas", cuotas);
}
/** Get cuotas */
public String getcuotas() 
{
return (String)get_Value("cuotas");
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
/** Set extra_cash */
public void setextra_cash (String extra_cash)
{
if (extra_cash != null && extra_cash.length() > 32)
{
log.warning("Length > 32 - truncated");
extra_cash = extra_cash.substring(0,32);
}
set_Value ("extra_cash", extra_cash);
}
/** Get extra_cash */
public String getextra_cash() 
{
return (String)get_Value("extra_cash");
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
/** Set fvta */
public void setfvta (String fvta)
{
if (fvta != null && fvta.length() > 32)
{
log.warning("Length > 32 - truncated");
fvta = fvta.substring(0,32);
}
set_Value ("fvta", fvta);
}
/** Get fvta */
public String getfvta() 
{
return (String)get_Value("fvta");
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
/** Set id_unico */
public void setid_unico (String id_unico)
{
if (id_unico != null && id_unico.length() > 32)
{
log.warning("Length > 32 - truncated");
id_unico = id_unico.substring(0,32);
}
set_Value ("id_unico", id_unico);
}
/** Get id_unico */
public String getid_unico() 
{
return (String)get_Value("id_unico");
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
/** Set I_Fideliuscupones_ID */
public void setI_Fideliuscupones_ID (int I_Fideliuscupones_ID)
{
set_ValueNoCheck ("I_Fideliuscupones_ID", new Integer(I_Fideliuscupones_ID));
}
/** Get I_Fideliuscupones_ID */
public int getI_Fideliuscupones_ID() 
{
Integer ii = (Integer)get_Value("I_Fideliuscupones_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set I_Fideliusliquidaciones_ID */
public void setI_Fideliusliquidaciones_ID (int I_Fideliusliquidaciones_ID)
{
if (I_Fideliusliquidaciones_ID <= 0) set_Value ("I_Fideliusliquidaciones_ID", null);
 else 
set_Value ("I_Fideliusliquidaciones_ID", new Integer(I_Fideliusliquidaciones_ID));
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
/** Set imp_vta */
public void setimp_vta (String imp_vta)
{
if (imp_vta != null && imp_vta.length() > 32)
{
log.warning("Length > 32 - truncated");
imp_vta = imp_vta.substring(0,32);
}
set_Value ("imp_vta", imp_vta);
}
/** Get imp_vta */
public String getimp_vta() 
{
return (String)get_Value("imp_vta");
}
/** Set Is_Reconciled */
public void setIs_Reconciled (boolean Is_Reconciled)
{
set_Value ("Is_Reconciled", new Boolean(Is_Reconciled));
}
/** Get Is_Reconciled */
public boolean is_Reconciled() 
{
Object oo = get_Value("Is_Reconciled");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
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
/** Set iva_cfo */
public void setiva_cfo (String iva_cfo)
{
if (iva_cfo != null && iva_cfo.length() > 32)
{
log.warning("Length > 32 - truncated");
iva_cfo = iva_cfo.substring(0,32);
}
set_Value ("iva_cfo", iva_cfo);
}
/** Get iva_cfo */
public String getiva_cfo() 
{
return (String)get_Value("iva_cfo");
}
/** Set nomequipo */
public void setnomequipo (String nomequipo)
{
if (nomequipo != null && nomequipo.length() > 32)
{
log.warning("Length > 32 - truncated");
nomequipo = nomequipo.substring(0,32);
}
set_Value ("nomequipo", nomequipo);
}
/** Get nomequipo */
public String getnomequipo() 
{
return (String)get_Value("nomequipo");
}
/** Set nrocupon */
public void setnrocupon (String nrocupon)
{
if (nrocupon != null && nrocupon.length() > 32)
{
log.warning("Length > 32 - truncated");
nrocupon = nrocupon.substring(0,32);
}
set_Value ("nrocupon", nrocupon);
}
/** Get nrocupon */
public String getnrocupon() 
{
return (String)get_Value("nrocupon");
}
/** Set nroequipo */
public void setnroequipo (String nroequipo)
{
if (nroequipo != null && nroequipo.length() > 32)
{
log.warning("Length > 32 - truncated");
nroequipo = nroequipo.substring(0,32);
}
set_Value ("nroequipo", nroequipo);
}
/** Get nroequipo */
public String getnroequipo() 
{
return (String)get_Value("nroequipo");
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
/** Set nrolote */
public void setnrolote (String nrolote)
{
if (nrolote != null && nrolote.length() > 32)
{
log.warning("Length > 32 - truncated");
nrolote = nrolote.substring(0,32);
}
set_Value ("nrolote", nrolote);
}
/** Get nrolote */
public String getnrolote() 
{
return (String)get_Value("nrolote");
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
/** Set rechazo */
public void setrechazo (String rechazo)
{
if (rechazo != null && rechazo.length() > 32)
{
log.warning("Length > 32 - truncated");
rechazo = rechazo.substring(0,32);
}
set_Value ("rechazo", rechazo);
}
/** Get rechazo */
public String getrechazo() 
{
return (String)get_Value("rechazo");
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
/** Set tipo_oper */
public void settipo_oper (String tipo_oper)
{
if (tipo_oper != null && tipo_oper.length() > 32)
{
log.warning("Length > 32 - truncated");
tipo_oper = tipo_oper.substring(0,32);
}
set_Value ("tipo_oper", tipo_oper);
}
/** Get tipo_oper */
public String gettipo_oper() 
{
return (String)get_Value("tipo_oper");
}
/** Set ult4tarjeta */
public void setult4tarjeta (String ult4tarjeta)
{
if (ult4tarjeta != null && ult4tarjeta.length() > 32)
{
log.warning("Length > 32 - truncated");
ult4tarjeta = ult4tarjeta.substring(0,32);
}
set_Value ("ult4tarjeta", ult4tarjeta);
}
/** Get ult4tarjeta */
public String getult4tarjeta() 
{
return (String)get_Value("ult4tarjeta");
}
}
