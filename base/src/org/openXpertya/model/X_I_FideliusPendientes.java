/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_FideliusPendientes
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2024-06-03 13:30:33.98 */
public class X_I_FideliusPendientes extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_FideliusPendientes (Properties ctx, int I_FideliusPendientes_ID, String trxName)
{
super (ctx, I_FideliusPendientes_ID, trxName);
/** if (I_FideliusPendientes_ID == 0)
{
setI_Fideliuspendientes_ID (0);
setI_IsImported (false);
}
 */
}
/** Load Constructor */
public X_I_FideliusPendientes (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_FideliusPendientes");

/** TableName=I_FideliusPendientes */
public static final String Table_Name="I_FideliusPendientes";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_FideliusPendientes");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_FideliusPendientes[").append(getID()).append("]");
return sb.toString();
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
/** Set codaut */
public void setcodaut (String codaut)
{
if (codaut != null && codaut.length() > 32)
{
log.warning("Length > 32 - truncated");
codaut = codaut.substring(0,32);
}
set_Value ("codaut", codaut);
}
/** Get codaut */
public String getcodaut() 
{
return (String)get_Value("codaut");
}
/** Set codcom */
public void setcodcom (String codcom)
{
if (codcom != null && codcom.length() > 32)
{
log.warning("Length > 32 - truncated");
codcom = codcom.substring(0,32);
}
set_Value ("codcom", codcom);
}
/** Get codcom */
public String getcodcom() 
{
return (String)get_Value("codcom");
}
/** Set cuota_tipeada */
public void setcuota_tipeada (String cuota_tipeada)
{
if (cuota_tipeada != null && cuota_tipeada.length() > 32)
{
log.warning("Length > 32 - truncated");
cuota_tipeada = cuota_tipeada.substring(0,32);
}
set_Value ("cuota_tipeada", cuota_tipeada);
}
/** Get cuota_tipeada */
public String getcuota_tipeada() 
{
return (String)get_Value("cuota_tipeada");
}
/** Set equipo */
public void setequipo (String equipo)
{
if (equipo != null && equipo.length() > 32)
{
log.warning("Length > 32 - truncated");
equipo = equipo.substring(0,32);
}
set_Value ("equipo", equipo);
}
/** Get equipo */
public String getequipo() 
{
return (String)get_Value("equipo");
}
/** Set factura */
public void setfactura (String factura)
{
if (factura != null && factura.length() > 32)
{
log.warning("Length > 32 - truncated");
factura = factura.substring(0,32);
}
set_Value ("factura", factura);
}
/** Get factura */
public String getfactura() 
{
return (String)get_Value("factura");
}
/** Set fechaoper */
public void setfechaoper (String fechaoper)
{
if (fechaoper != null && fechaoper.length() > 32)
{
log.warning("Length > 32 - truncated");
fechaoper = fechaoper.substring(0,32);
}
set_Value ("fechaoper", fechaoper);
}
/** Get fechaoper */
public String getfechaoper() 
{
return (String)get_Value("fechaoper");
}
/** Set fechapagoest */
public void setfechapagoest (String fechapagoest)
{
if (fechapagoest != null && fechapagoest.length() > 32)
{
log.warning("Length > 32 - truncated");
fechapagoest = fechapagoest.substring(0,32);
}
set_Value ("fechapagoest", fechapagoest);
}
/** Get fechapagoest */
public String getfechapagoest() 
{
return (String)get_Value("fechapagoest");
}
/** Set horaoper */
public void sethoraoper (String horaoper)
{
if (horaoper != null && horaoper.length() > 32)
{
log.warning("Length > 32 - truncated");
horaoper = horaoper.substring(0,32);
}
set_Value ("horaoper", horaoper);
}
/** Get horaoper */
public String gethoraoper() 
{
return (String)get_Value("horaoper");
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
/** Set id_clover */
public void setid_clover (String id_clover)
{
if (id_clover != null && id_clover.length() > 32)
{
log.warning("Length > 32 - truncated");
id_clover = id_clover.substring(0,32);
}
set_Value ("id_clover", id_clover);
}
/** Get id_clover */
public String getid_clover() 
{
return (String)get_Value("id_clover");
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
/** Set I_Fideliuspendientes_ID */
public void setI_Fideliuspendientes_ID (int I_Fideliuspendientes_ID)
{
set_ValueNoCheck ("I_Fideliuspendientes_ID", new Integer(I_Fideliuspendientes_ID));
}
/** Get I_Fideliuspendientes_ID */
public int getI_Fideliuspendientes_ID() 
{
Integer ii = (Integer)get_Value("I_Fideliuspendientes_ID");
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
/** Set Importe */
public void setImporte (String Importe)
{
if (Importe != null && Importe.length() > 32)
{
log.warning("Length > 32 - truncated");
Importe = Importe.substring(0,32);
}
set_Value ("Importe", Importe);
}
/** Get Importe */
public String getImporte() 
{
return (String)get_Value("Importe");
}
/** Set montosec */
public void setmontosec (String montosec)
{
if (montosec != null && montosec.length() > 32)
{
log.warning("Length > 32 - truncated");
montosec = montosec.substring(0,32);
}
set_Value ("montosec", montosec);
}
/** Get montosec */
public String getmontosec() 
{
return (String)get_Value("montosec");
}
/** Set nombre_comerc */
public void setnombre_comerc (String nombre_comerc)
{
if (nombre_comerc != null && nombre_comerc.length() > 32)
{
log.warning("Length > 32 - truncated");
nombre_comerc = nombre_comerc.substring(0,32);
}
set_Value ("nombre_comerc", nombre_comerc);
}
/** Get nombre_comerc */
public String getnombre_comerc() 
{
return (String)get_Value("nombre_comerc");
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
/** Set nrotarjeta */
public void setnrotarjeta (String nrotarjeta)
{
if (nrotarjeta != null && nrotarjeta.length() > 32)
{
log.warning("Length > 32 - truncated");
nrotarjeta = nrotarjeta.substring(0,32);
}
set_Value ("nrotarjeta", nrotarjeta);
}
/** Get nrotarjeta */
public String getnrotarjeta() 
{
return (String)get_Value("nrotarjeta");
}
/** Set nroterminal */
public void setnroterminal (String nroterminal)
{
if (nroterminal != null && nroterminal.length() > 32)
{
log.warning("Length > 32 - truncated");
nroterminal = nroterminal.substring(0,32);
}
set_Value ("nroterminal", nroterminal);
}
/** Get nroterminal */
public String getnroterminal() 
{
return (String)get_Value("nroterminal");
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
/** Set ticket */
public void setticket (String ticket)
{
if (ticket != null && ticket.length() > 32)
{
log.warning("Length > 32 - truncated");
ticket = ticket.substring(0,32);
}
set_Value ("ticket", ticket);
}
/** Get ticket */
public String getticket() 
{
return (String)get_Value("ticket");
}
/** Set tipotrx */
public void settipotrx (String tipotrx)
{
if (tipotrx != null && tipotrx.length() > 32)
{
log.warning("Length > 32 - truncated");
tipotrx = tipotrx.substring(0,32);
}
set_Value ("tipotrx", tipotrx);
}
/** Get tipotrx */
public String gettipotrx() 
{
return (String)get_Value("tipotrx");
}
}
