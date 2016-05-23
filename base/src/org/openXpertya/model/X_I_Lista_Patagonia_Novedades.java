/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_Lista_Patagonia_Novedades
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-05-23 11:01:08.31 */
public class X_I_Lista_Patagonia_Novedades extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_Lista_Patagonia_Novedades (Properties ctx, int I_Lista_Patagonia_Novedades_ID, String trxName)
{
super (ctx, I_Lista_Patagonia_Novedades_ID, trxName);
/** if (I_Lista_Patagonia_Novedades_ID == 0)
{
setI_IsImported (false);
setI_Lista_Patagonia_Novedades_ID (0);
}
 */
}
/** Load Constructor */
public X_I_Lista_Patagonia_Novedades (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_Lista_Patagonia_Novedades");

/** TableName=I_Lista_Patagonia_Novedades */
public static final String Table_Name="I_Lista_Patagonia_Novedades";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_Lista_Patagonia_Novedades");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_Lista_Patagonia_Novedades[").append(getID()).append("]");
return sb.toString();
}
/** Set Constante */
public void setConstante (String Constante)
{
if (Constante != null && Constante.length() > 2)
{
log.warning("Length > 2 - truncated");
Constante = Constante.substring(0,2);
}
set_Value ("Constante", Constante);
}
/** Get Constante */
public String getConstante() 
{
return (String)get_Value("Constante");
}
/** Set Fh_AInformante */
public void setFh_AInformante (String Fh_AInformante)
{
if (Fh_AInformante != null && Fh_AInformante.length() > 3)
{
log.warning("Length > 3 - truncated");
Fh_AInformante = Fh_AInformante.substring(0,3);
}
set_Value ("Fh_AInformante", Fh_AInformante);
}
/** Get Fh_AInformante */
public String getFh_AInformante() 
{
return (String)get_Value("Fh_AInformante");
}
/** Set Fh_FechaProceso */
public void setFh_FechaProceso (String Fh_FechaProceso)
{
if (Fh_FechaProceso != null && Fh_FechaProceso.length() > 8)
{
log.warning("Length > 8 - truncated");
Fh_FechaProceso = Fh_FechaProceso.substring(0,8);
}
set_Value ("Fh_FechaProceso", Fh_FechaProceso);
}
/** Get Fh_FechaProceso */
public String getFh_FechaProceso() 
{
return (String)get_Value("Fh_FechaProceso");
}
/** Set Fh_HoraCreacion */
public void setFh_HoraCreacion (String Fh_HoraCreacion)
{
if (Fh_HoraCreacion != null && Fh_HoraCreacion.length() > 6)
{
log.warning("Length > 6 - truncated");
Fh_HoraCreacion = Fh_HoraCreacion.substring(0,6);
}
set_Value ("Fh_HoraCreacion", Fh_HoraCreacion);
}
/** Get Fh_HoraCreacion */
public String getFh_HoraCreacion() 
{
return (String)get_Value("Fh_HoraCreacion");
}
/** Set Fh_IDArchivo */
public void setFh_IDArchivo (String Fh_IDArchivo)
{
if (Fh_IDArchivo != null && Fh_IDArchivo.length() > 20)
{
log.warning("Length > 20 - truncated");
Fh_IDArchivo = Fh_IDArchivo.substring(0,20);
}
set_Value ("Fh_IDArchivo", Fh_IDArchivo);
}
/** Get Fh_IDArchivo */
public String getFh_IDArchivo() 
{
return (String)get_Value("Fh_IDArchivo");
}
/** Set Fh_Identificacion */
public void setFh_Identificacion (String Fh_Identificacion)
{
if (Fh_Identificacion != null && Fh_Identificacion.length() > 11)
{
log.warning("Length > 11 - truncated");
Fh_Identificacion = Fh_Identificacion.substring(0,11);
}
set_Value ("Fh_Identificacion", Fh_Identificacion);
}
/** Get Fh_Identificacion */
public String getFh_Identificacion() 
{
return (String)get_Value("Fh_Identificacion");
}
/** Set Fh_NroInformante */
public void setFh_NroInformante (String Fh_NroInformante)
{
if (Fh_NroInformante != null && Fh_NroInformante.length() > 7)
{
log.warning("Length > 7 - truncated");
Fh_NroInformante = Fh_NroInformante.substring(0,7);
}
set_Value ("Fh_NroInformante", Fh_NroInformante);
}
/** Get Fh_NroInformante */
public String getFh_NroInformante() 
{
return (String)get_Value("Fh_NroInformante");
}
/** Set Fh_NroSecuencial */
public void setFh_NroSecuencial (String Fh_NroSecuencial)
{
if (Fh_NroSecuencial != null && Fh_NroSecuencial.length() > 7)
{
log.warning("Length > 7 - truncated");
Fh_NroSecuencial = Fh_NroSecuencial.substring(0,7);
}
set_Value ("Fh_NroSecuencial", Fh_NroSecuencial);
}
/** Get Fh_NroSecuencial */
public String getFh_NroSecuencial() 
{
return (String)get_Value("Fh_NroSecuencial");
}
/** Set File Name.
Name of the local file or URL */
public void setFileName (String FileName)
{
if (FileName != null && FileName.length() > 100)
{
log.warning("Length > 100 - truncated");
FileName = FileName.substring(0,100);
}
set_Value ("FileName", FileName);
}
/** Get File Name.
Name of the local file or URL */
public String getFileName() 
{
return (String)get_Value("FileName");
}
/** Set Ft_TotalArchivo */
public void setFt_TotalArchivo (String Ft_TotalArchivo)
{
if (Ft_TotalArchivo != null && Ft_TotalArchivo.length() > 10)
{
log.warning("Length > 10 - truncated");
Ft_TotalArchivo = Ft_TotalArchivo.substring(0,10);
}
set_Value ("Ft_TotalArchivo", Ft_TotalArchivo);
}
/** Get Ft_TotalArchivo */
public String getFt_TotalArchivo() 
{
return (String)get_Value("Ft_TotalArchivo");
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
/** Set I_Lista_Patagonia_Novedades_ID */
public void setI_Lista_Patagonia_Novedades_ID (int I_Lista_Patagonia_Novedades_ID)
{
set_ValueNoCheck ("I_Lista_Patagonia_Novedades_ID", new Integer(I_Lista_Patagonia_Novedades_ID));
}
/** Get I_Lista_Patagonia_Novedades_ID */
public int getI_Lista_Patagonia_Novedades_ID() 
{
Integer ii = (Integer)get_Value("I_Lista_Patagonia_Novedades_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set N1_IDArchivoRecibido */
public void setN1_IDArchivoRecibido (String N1_IDArchivoRecibido)
{
if (N1_IDArchivoRecibido != null && N1_IDArchivoRecibido.length() > 20)
{
log.warning("Length > 20 - truncated");
N1_IDArchivoRecibido = N1_IDArchivoRecibido.substring(0,20);
}
set_Value ("N1_IDArchivoRecibido", N1_IDArchivoRecibido);
}
/** Get N1_IDArchivoRecibido */
public String getN1_IDArchivoRecibido() 
{
return (String)get_Value("N1_IDArchivoRecibido");
}
/** Set N1_NroInstrumento */
public void setN1_NroInstrumento (String N1_NroInstrumento)
{
if (N1_NroInstrumento != null && N1_NroInstrumento.length() > 15)
{
log.warning("Length > 15 - truncated");
N1_NroInstrumento = N1_NroInstrumento.substring(0,15);
}
set_Value ("N1_NroInstrumento", N1_NroInstrumento);
}
/** Get N1_NroInstrumento */
public String getN1_NroInstrumento() 
{
return (String)get_Value("N1_NroInstrumento");
}
/** Set N1_NroPagoSistema */
public void setN1_NroPagoSistema (String N1_NroPagoSistema)
{
if (N1_NroPagoSistema != null && N1_NroPagoSistema.length() > 8)
{
log.warning("Length > 8 - truncated");
N1_NroPagoSistema = N1_NroPagoSistema.substring(0,8);
}
set_Value ("N1_NroPagoSistema", N1_NroPagoSistema);
}
/** Get N1_NroPagoSistema */
public String getN1_NroPagoSistema() 
{
return (String)get_Value("N1_NroPagoSistema");
}
/** Set N1_ReferenciaPago */
public void setN1_ReferenciaPago (String N1_ReferenciaPago)
{
if (N1_ReferenciaPago != null && N1_ReferenciaPago.length() > 25)
{
log.warning("Length > 25 - truncated");
N1_ReferenciaPago = N1_ReferenciaPago.substring(0,25);
}
set_Value ("N1_ReferenciaPago", N1_ReferenciaPago);
}
/** Get N1_ReferenciaPago */
public String getN1_ReferenciaPago() 
{
return (String)get_Value("N1_ReferenciaPago");
}
/** Set N1_SubNroPago */
public void setN1_SubNroPago (String N1_SubNroPago)
{
if (N1_SubNroPago != null && N1_SubNroPago.length() > 3)
{
log.warning("Length > 3 - truncated");
N1_SubNroPago = N1_SubNroPago.substring(0,3);
}
set_Value ("N1_SubNroPago", N1_SubNroPago);
}
/** Get N1_SubNroPago */
public String getN1_SubNroPago() 
{
return (String)get_Value("N1_SubNroPago");
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
/** Set Qn_EstadoPago */
public void setQn_EstadoPago (String Qn_EstadoPago)
{
if (Qn_EstadoPago != null && Qn_EstadoPago.length() > 60)
{
log.warning("Length > 60 - truncated");
Qn_EstadoPago = Qn_EstadoPago.substring(0,60);
}
set_Value ("Qn_EstadoPago", Qn_EstadoPago);
}
/** Get Qn_EstadoPago */
public String getQn_EstadoPago() 
{
return (String)get_Value("Qn_EstadoPago");
}
/** Set Qn_Eventos */
public void setQn_Eventos (String Qn_Eventos)
{
if (Qn_Eventos != null && Qn_Eventos.length() > 60)
{
log.warning("Length > 60 - truncated");
Qn_Eventos = Qn_Eventos.substring(0,60);
}
set_Value ("Qn_Eventos", Qn_Eventos);
}
/** Get Qn_Eventos */
public String getQn_Eventos() 
{
return (String)get_Value("Qn_Eventos");
}
/** Set Qn_NroPagoSistema */
public void setQn_NroPagoSistema (String Qn_NroPagoSistema)
{
if (Qn_NroPagoSistema != null && Qn_NroPagoSistema.length() > 8)
{
log.warning("Length > 8 - truncated");
Qn_NroPagoSistema = Qn_NroPagoSistema.substring(0,8);
}
set_Value ("Qn_NroPagoSistema", Qn_NroPagoSistema);
}
/** Get Qn_NroPagoSistema */
public String getQn_NroPagoSistema() 
{
return (String)get_Value("Qn_NroPagoSistema");
}
/** Set Qn_SubNroPago */
public void setQn_SubNroPago (String Qn_SubNroPago)
{
if (Qn_SubNroPago != null && Qn_SubNroPago.length() > 3)
{
log.warning("Length > 3 - truncated");
Qn_SubNroPago = Qn_SubNroPago.substring(0,3);
}
set_Value ("Qn_SubNroPago", Qn_SubNroPago);
}
/** Get Qn_SubNroPago */
public String getQn_SubNroPago() 
{
return (String)get_Value("Qn_SubNroPago");
}
/** Set Registro */
public void setRegistro (String Registro)
{
if (Registro != null && Registro.length() > 255)
{
log.warning("Length > 255 - truncated");
Registro = Registro.substring(0,255);
}
set_Value ("Registro", Registro);
}
/** Get Registro */
public String getRegistro() 
{
return (String)get_Value("Registro");
}
/** Set T1_TotalRegPago */
public void setT1_TotalRegPago (String T1_TotalRegPago)
{
if (T1_TotalRegPago != null && T1_TotalRegPago.length() > 10)
{
log.warning("Length > 10 - truncated");
T1_TotalRegPago = T1_TotalRegPago.substring(0,10);
}
set_Value ("T1_TotalRegPago", T1_TotalRegPago);
}
/** Get T1_TotalRegPago */
public String getT1_TotalRegPago() 
{
return (String)get_Value("T1_TotalRegPago");
}
}
