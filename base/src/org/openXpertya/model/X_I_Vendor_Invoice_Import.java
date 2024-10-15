/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_Vendor_Invoice_Import
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2024-10-11 14:38:31.173 */
public class X_I_Vendor_Invoice_Import extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_Vendor_Invoice_Import (Properties ctx, int I_Vendor_Invoice_Import_ID, String trxName)
{
super (ctx, I_Vendor_Invoice_Import_ID, trxName);
/** if (I_Vendor_Invoice_Import_ID == 0)
{
setI_IsImported (false);
setI_Vendor_Invoice_Import_ID (0);
}
 */
}
/** Load Constructor */
public X_I_Vendor_Invoice_Import (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_Vendor_Invoice_Import");

/** TableName=I_Vendor_Invoice_Import */
public static final String Table_Name="I_Vendor_Invoice_Import";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_Vendor_Invoice_Import");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_Vendor_Invoice_Import[").append(getID()).append("]");
return sb.toString();
}
/** Set cae */
public void setcae (String cae)
{
if (cae != null && cae.length() > 14)
{
log.warning("Length > 14 - truncated");
cae = cae.substring(0,14);
}
set_Value ("cae", cae);
}
/** Get cae */
public String getcae() 
{
return (String)get_Value("cae");
}
/** Set Invoice.
Invoice Identifier */
public void setC_Invoice_ID (int C_Invoice_ID)
{
if (C_Invoice_ID <= 0) set_Value ("C_Invoice_ID", null);
 else 
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
/** Set Fecha */
public void setFecha (Timestamp Fecha)
{
set_Value ("Fecha", Fecha);
}
/** Get Fecha */
public Timestamp getFecha() 
{
return (Timestamp)get_Value("Fecha");
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
/** Set importeopexentas */
public void setimporteopexentas (BigDecimal importeopexentas)
{
set_Value ("importeopexentas", importeopexentas);
}
/** Get importeopexentas */
public BigDecimal getimporteopexentas() 
{
BigDecimal bd = (BigDecimal)get_Value("importeopexentas");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set iva */
public void setiva (BigDecimal iva)
{
set_Value ("iva", iva);
}
/** Get iva */
public BigDecimal getiva() 
{
BigDecimal bd = (BigDecimal)get_Value("iva");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set I_Vendor_Invoice_Import_ID */
public void setI_Vendor_Invoice_Import_ID (int I_Vendor_Invoice_Import_ID)
{
set_ValueNoCheck ("I_Vendor_Invoice_Import_ID", new Integer(I_Vendor_Invoice_Import_ID));
}
/** Get I_Vendor_Invoice_Import_ID */
public int getI_Vendor_Invoice_Import_ID() 
{
Integer ii = (Integer)get_Value("I_Vendor_Invoice_Import_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Moneda */
public void setMoneda (String Moneda)
{
if (Moneda != null && Moneda.length() > 3)
{
log.warning("Length > 3 - truncated");
Moneda = Moneda.substring(0,3);
}
set_Value ("Moneda", Moneda);
}
/** Get Moneda */
public String getMoneda() 
{
return (String)get_Value("Moneda");
}
/** Set netogravado */
public void setnetogravado (BigDecimal netogravado)
{
set_Value ("netogravado", netogravado);
}
/** Get netogravado */
public BigDecimal getnetogravado() 
{
BigDecimal bd = (BigDecimal)get_Value("netogravado");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set netonogravado */
public void setnetonogravado (BigDecimal netonogravado)
{
set_Value ("netonogravado", netonogravado);
}
/** Get netonogravado */
public BigDecimal getnetonogravado() 
{
BigDecimal bd = (BigDecimal)get_Value("netonogravado");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set numerocomprobantedesde */
public void setnumerocomprobantedesde (int numerocomprobantedesde)
{
set_Value ("numerocomprobantedesde", new Integer(numerocomprobantedesde));
}
/** Get numerocomprobantedesde */
public int getnumerocomprobantedesde() 
{
Integer ii = (Integer)get_Value("numerocomprobantedesde");
if (ii == null) return 0;
return ii.intValue();
}
/** Set numerocomprobantehasta */
public void setnumerocomprobantehasta (int numerocomprobantehasta)
{
set_Value ("numerocomprobantehasta", new Integer(numerocomprobantehasta));
}
/** Get numerocomprobantehasta */
public int getnumerocomprobantehasta() 
{
Integer ii = (Integer)get_Value("numerocomprobantehasta");
if (ii == null) return 0;
return ii.intValue();
}
/** Set numeroidentificacion */
public void setnumeroidentificacion (String numeroidentificacion)
{
if (numeroidentificacion != null && numeroidentificacion.length() > 20)
{
log.warning("Length > 20 - truncated");
numeroidentificacion = numeroidentificacion.substring(0,20);
}
set_Value ("numeroidentificacion", numeroidentificacion);
}
/** Get numeroidentificacion */
public String getnumeroidentificacion() 
{
return (String)get_Value("numeroidentificacion");
}
/** Set otros_tributos */
public void setotros_tributos (BigDecimal otros_tributos)
{
set_Value ("otros_tributos", otros_tributos);
}
/** Get otros_tributos */
public BigDecimal getotros_tributos() 
{
BigDecimal bd = (BigDecimal)get_Value("otros_tributos");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Punto De Venta */
public void setPuntoDeVenta (int PuntoDeVenta)
{
set_Value ("PuntoDeVenta", new Integer(PuntoDeVenta));
}
/** Get Punto De Venta */
public int getPuntoDeVenta() 
{
Integer ii = (Integer)get_Value("PuntoDeVenta");
if (ii == null) return 0;
return ii.intValue();
}
/** Set razonsocial */
public void setrazonsocial (String razonsocial)
{
if (razonsocial != null && razonsocial.length() > 60)
{
log.warning("Length > 60 - truncated");
razonsocial = razonsocial.substring(0,60);
}
set_Value ("razonsocial", razonsocial);
}
/** Get razonsocial */
public String getrazonsocial() 
{
return (String)get_Value("razonsocial");
}
/** Set tipocambio */
public void settipocambio (BigDecimal tipocambio)
{
set_Value ("tipocambio", tipocambio);
}
/** Get tipocambio */
public BigDecimal gettipocambio() 
{
BigDecimal bd = (BigDecimal)get_Value("tipocambio");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Tipo Comprobante */
public void setTipoComprobante (int TipoComprobante)
{
set_Value ("TipoComprobante", new Integer(TipoComprobante));
}
/** Get Tipo Comprobante */
public int getTipoComprobante() 
{
Integer ii = (Integer)get_Value("TipoComprobante");
if (ii == null) return 0;
return ii.intValue();
}
public static final int TIPOIDENTIFICACION_AD_Reference_ID = MReference.getReferenceID("Tax Id Type");
/** CUIT = 80 */
public static final String TIPOIDENTIFICACION_CUIT = "80";
/** CUIL = 86 */
public static final String TIPOIDENTIFICACION_CUIL = "86";
/** CDI = 87 */
public static final String TIPOIDENTIFICACION_CDI = "87";
/** LE = 89 */
public static final String TIPOIDENTIFICACION_LE = "89";
/** LC = 90 */
public static final String TIPOIDENTIFICACION_LC = "90";
/** CI extranjera = 91 */
public static final String TIPOIDENTIFICACION_CIExtranjera = "91";
/** En Trámite = 92 */
public static final String TIPOIDENTIFICACION_EnTrámite = "92";
/** Acta Nacimiento = 93 */
public static final String TIPOIDENTIFICACION_ActaNacimiento = "93";
/** CI Bs. As. RNP = 95 */
public static final String TIPOIDENTIFICACION_CIBsAsRNP = "95";
/** DNI = 96 */
public static final String TIPOIDENTIFICACION_DNI = "96";
/** Pasaporte = 94 */
public static final String TIPOIDENTIFICACION_Pasaporte = "94";
/** CI Policía Federal = 00 */
public static final String TIPOIDENTIFICACION_CIPolicíaFederal = "00";
/** CI Buenos Aires = 01 */
public static final String TIPOIDENTIFICACION_CIBuenosAires = "01";
/** CI Mendoza = 07 */
public static final String TIPOIDENTIFICACION_CIMendoza = "07";
/** CI La Rioja = 08 */
public static final String TIPOIDENTIFICACION_CILaRioja = "08";
/** CI Salta = 09 */
public static final String TIPOIDENTIFICACION_CISalta = "09";
/** CI San Juan = 10 */
public static final String TIPOIDENTIFICACION_CISanJuan = "10";
/** CI San Luis = 11 */
public static final String TIPOIDENTIFICACION_CISanLuis = "11";
/** CI Santa Fe = 12 */
public static final String TIPOIDENTIFICACION_CISantaFe = "12";
/** CI Santiago del Estero = 13 */
public static final String TIPOIDENTIFICACION_CISantiagoDelEstero = "13";
/** CI Tucumán = 14 */
public static final String TIPOIDENTIFICACION_CITucumán = "14";
/** CI Chaco = 16 */
public static final String TIPOIDENTIFICACION_CIChaco = "16";
/** CI Chubut = 17 */
public static final String TIPOIDENTIFICACION_CIChubut = "17";
/** CI Formosa = 18 */
public static final String TIPOIDENTIFICACION_CIFormosa = "18";
/** CI Misiones = 19 */
public static final String TIPOIDENTIFICACION_CIMisiones = "19";
/** CI Neuquén = 20 */
public static final String TIPOIDENTIFICACION_CINeuquén = "20";
/** CI Catamarca = 02 */
public static final String TIPOIDENTIFICACION_CICatamarca = "02";
/** CI Córdoba = 03 */
public static final String TIPOIDENTIFICACION_CICórdoba = "03";
/** CI Corrientes = 04 */
public static final String TIPOIDENTIFICACION_CICorrientes = "04";
/** CI Entre Ríos = 05 */
public static final String TIPOIDENTIFICACION_CIEntreRíos = "05";
/** CI Jujuy = 06 */
public static final String TIPOIDENTIFICACION_CIJujuy = "06";
/** CI La Pampa = 21 */
public static final String TIPOIDENTIFICACION_CILaPampa = "21";
/** CI Río Negro = 22 */
public static final String TIPOIDENTIFICACION_CIRíoNegro = "22";
/** CI Santa Cruz = 23 */
public static final String TIPOIDENTIFICACION_CISantaCruz = "23";
/** CI T. Del Fuego = 24 */
public static final String TIPOIDENTIFICACION_CITDelFuego = "24";
/** RUC = 25 */
public static final String TIPOIDENTIFICACION_RUC = "25";
/** Sin ID Tipo Documento = 99 */
public static final String TIPOIDENTIFICACION_SinIDTipoDocumento = "99";
/** Set tipoidentificacion */
public void settipoidentificacion (String tipoidentificacion)
{
if (tipoidentificacion == null || tipoidentificacion.equals("80") || tipoidentificacion.equals("86") || tipoidentificacion.equals("87") || tipoidentificacion.equals("89") || tipoidentificacion.equals("90") || tipoidentificacion.equals("91") || tipoidentificacion.equals("92") || tipoidentificacion.equals("93") || tipoidentificacion.equals("95") || tipoidentificacion.equals("96") || tipoidentificacion.equals("94") || tipoidentificacion.equals("00") || tipoidentificacion.equals("01") || tipoidentificacion.equals("07") || tipoidentificacion.equals("08") || tipoidentificacion.equals("09") || tipoidentificacion.equals("10") || tipoidentificacion.equals("11") || tipoidentificacion.equals("12") || tipoidentificacion.equals("13") || tipoidentificacion.equals("14") || tipoidentificacion.equals("16") || tipoidentificacion.equals("17") || tipoidentificacion.equals("18") || tipoidentificacion.equals("19") || tipoidentificacion.equals("20") || tipoidentificacion.equals("02") || tipoidentificacion.equals("03") || tipoidentificacion.equals("04") || tipoidentificacion.equals("05") || tipoidentificacion.equals("06") || tipoidentificacion.equals("21") || tipoidentificacion.equals("22") || tipoidentificacion.equals("23") || tipoidentificacion.equals("24") || tipoidentificacion.equals("25") || tipoidentificacion.equals("99") || ( refContainsValue("CORE-AD_Reference-1010201", tipoidentificacion) ) );
 else throw new IllegalArgumentException ("tipoidentificacion Invalid value: " + tipoidentificacion + ".  Valid: " +  refValidOptions("CORE-AD_Reference-1010201") );
if (tipoidentificacion != null && tipoidentificacion.length() > 2)
{
log.warning("Length > 2 - truncated");
tipoidentificacion = tipoidentificacion.substring(0,2);
}
set_Value ("tipoidentificacion", tipoidentificacion);
}
/** Get tipoidentificacion */
public String gettipoidentificacion() 
{
return (String)get_Value("tipoidentificacion");
}
/** Set total */
public void settotal (BigDecimal total)
{
set_Value ("total", total);
}
/** Get total */
public BigDecimal gettotal() 
{
BigDecimal bd = (BigDecimal)get_Value("total");
if (bd == null) return Env.ZERO;
return bd;
}
}
