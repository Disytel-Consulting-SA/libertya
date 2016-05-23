/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.logging.Level;
 import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por I_Lista_Galicia
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2016-05-23 11:01:07.616 */
public class X_I_Lista_Galicia extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_I_Lista_Galicia (Properties ctx, int I_Lista_Galicia_ID, String trxName)
{
super (ctx, I_Lista_Galicia_ID, trxName);
/** if (I_Lista_Galicia_ID == 0)
{
setI_IsImported (false);
setI_Lista_Galicia_ID (0);
}
 */
}
/** Load Constructor */
public X_I_Lista_Galicia (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("I_Lista_Galicia");

/** TableName=I_Lista_Galicia */
public static final String Table_Name="I_Lista_Galicia";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"I_Lista_Galicia");
protected static BigDecimal AccessLevel = new BigDecimal(3);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_I_Lista_Galicia[").append(getID()).append("]");
return sb.toString();
}
/** Set Codigo */
public void setCodigo (String Codigo)
{
if (Codigo != null && Codigo.length() > 2)
{
log.warning("Length > 2 - truncated");
Codigo = Codigo.substring(0,2);
}
set_Value ("Codigo", Codigo);
}
/** Get Codigo */
public String getCodigo() 
{
return (String)get_Value("Codigo");
}
/** Set Comision */
public void setComision (String Comision)
{
if (Comision != null && Comision.length() > 1)
{
log.warning("Length > 1 - truncated");
Comision = Comision.substring(0,1);
}
set_Value ("Comision", Comision);
}
/** Get Comision */
public String getComision() 
{
return (String)get_Value("Comision");
}
/** Set Condicion */
public void setCondicion (String Condicion)
{
if (Condicion != null && Condicion.length() > 1)
{
log.warning("Length > 1 - truncated");
Condicion = Condicion.substring(0,1);
}
set_Value ("Condicion", Condicion);
}
/** Get Condicion */
public String getCondicion() 
{
return (String)get_Value("Condicion");
}
/** Set CP */
public void setCP (String CP)
{
if (CP != null && CP.length() > 6)
{
log.warning("Length > 6 - truncated");
CP = CP.substring(0,6);
}
set_Value ("CP", CP);
}
/** Get CP */
public String getCP() 
{
return (String)get_Value("CP");
}
/** Set Cuenta_Especifica */
public void setCuenta_Especifica (String Cuenta_Especifica)
{
if (Cuenta_Especifica != null && Cuenta_Especifica.length() > 14)
{
log.warning("Length > 14 - truncated");
Cuenta_Especifica = Cuenta_Especifica.substring(0,14);
}
set_Value ("Cuenta_Especifica", Cuenta_Especifica);
}
/** Get Cuenta_Especifica */
public String getCuenta_Especifica() 
{
return (String)get_Value("Cuenta_Especifica");
}
/** Set CUIT */
public void setCUIT (String CUIT)
{
if (CUIT != null && CUIT.length() > 11)
{
log.warning("Length > 11 - truncated");
CUIT = CUIT.substring(0,11);
}
set_Value ("CUIT", CUIT);
}
/** Get CUIT */
public String getCUIT() 
{
return (String)get_Value("CUIT");
}
/** Set Direccion */
public void setDireccion (String Direccion)
{
if (Direccion != null && Direccion.length() > 30)
{
log.warning("Length > 30 - truncated");
Direccion = Direccion.substring(0,30);
}
set_Value ("Direccion", Direccion);
}
/** Get Direccion */
public String getDireccion() 
{
return (String)get_Value("Direccion");
}
/** Set Document No.
Document sequence NUMERIC of the document */
public void setDocumentNo (String DocumentNo)
{
if (DocumentNo != null && DocumentNo.length() > 8)
{
log.warning("Length > 8 - truncated");
DocumentNo = DocumentNo.substring(0,8);
}
set_Value ("DocumentNo", DocumentNo);
}
/** Get Document No.
Document sequence NUMERIC of the document */
public String getDocumentNo() 
{
return (String)get_Value("DocumentNo");
}
/** Set Estado */
public void setEstado (String Estado)
{
if (Estado != null && Estado.length() > 2)
{
log.warning("Length > 2 - truncated");
Estado = Estado.substring(0,2);
}
set_Value ("Estado", Estado);
}
/** Get Estado */
public String getEstado() 
{
return (String)get_Value("Estado");
}
/** Set Fecha_Emision */
public void setFecha_Emision (Timestamp Fecha_Emision)
{
set_Value ("Fecha_Emision", Fecha_Emision);
}
/** Get Fecha_Emision */
public Timestamp getFecha_Emision() 
{
return (Timestamp)get_Value("Fecha_Emision");
}
/** Set Fecha Modificacion */
public void setFecha_Modificacion (Timestamp Fecha_Modificacion)
{
set_Value ("Fecha_Modificacion", Fecha_Modificacion);
}
/** Get Fecha Modificacion */
public Timestamp getFecha_Modificacion() 
{
return (Timestamp)get_Value("Fecha_Modificacion");
}
/** Set Fecha Pago */
public void setFecha_Pago (Timestamp Fecha_Pago)
{
set_Value ("Fecha_Pago", Fecha_Pago);
}
/** Get Fecha Pago */
public Timestamp getFecha_Pago() 
{
return (Timestamp)get_Value("Fecha_Pago");
}
/** Set Fecha Recepcion */
public void setFecha_Recepcion (Timestamp Fecha_Recepcion)
{
set_Value ("Fecha_Recepcion", Fecha_Recepcion);
}
/** Get Fecha Recepcion */
public Timestamp getFecha_Recepcion() 
{
return (Timestamp)get_Value("Fecha_Recepcion");
}
/** Set Fecha Vencimiento */
public void setFecha_Vencimiento (Timestamp Fecha_Vencimiento)
{
set_Value ("Fecha_Vencimiento", Fecha_Vencimiento);
}
/** Get Fecha Vencimiento */
public Timestamp getFecha_Vencimiento() 
{
return (Timestamp)get_Value("Fecha_Vencimiento");
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
/** Set I_Lista_Galicia_ID */
public void setI_Lista_Galicia_ID (int I_Lista_Galicia_ID)
{
set_ValueNoCheck ("I_Lista_Galicia_ID", new Integer(I_Lista_Galicia_ID));
}
/** Get I_Lista_Galicia_ID */
public int getI_Lista_Galicia_ID() 
{
Integer ii = (Integer)get_Value("I_Lista_Galicia_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Lista */
public void setLista (String Lista)
{
if (Lista != null && Lista.length() > 8)
{
log.warning("Length > 8 - truncated");
Lista = Lista.substring(0,8);
}
set_Value ("Lista", Lista);
}
/** Get Lista */
public String getLista() 
{
return (String)get_Value("Lista");
}
/** Set Localidad */
public void setLocalidad (String Localidad)
{
if (Localidad != null && Localidad.length() > 20)
{
log.warning("Length > 20 - truncated");
Localidad = Localidad.substring(0,20);
}
set_Value ("Localidad", Localidad);
}
/** Get Localidad */
public String getLocalidad() 
{
return (String)get_Value("Localidad");
}
/** Set Monto */
public void setMonto (BigDecimal Monto)
{
set_Value ("Monto", Monto);
}
/** Get Monto */
public BigDecimal getMonto() 
{
BigDecimal bd = (BigDecimal)get_Value("Monto");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Numero */
public void setNumero (String Numero)
{
if (Numero != null && Numero.length() > 10)
{
log.warning("Length > 10 - truncated");
Numero = Numero.substring(0,10);
}
set_Value ("Numero", Numero);
}
/** Get Numero */
public String getNumero() 
{
return (String)get_Value("Numero");
}
/** Set Orden De Pago */
public void setOrden_De_Pago (String Orden_De_Pago)
{
if (Orden_De_Pago != null && Orden_De_Pago.length() > 10)
{
log.warning("Length > 10 - truncated");
Orden_De_Pago = Orden_De_Pago.substring(0,10);
}
set_Value ("Orden_De_Pago", Orden_De_Pago);
}
/** Get Orden De Pago */
public String getOrden_De_Pago() 
{
return (String)get_Value("Orden_De_Pago");
}
/** Set Posicion */
public void setPosicion (String Posicion)
{
if (Posicion != null && Posicion.length() > 3)
{
log.warning("Length > 3 - truncated");
Posicion = Posicion.substring(0,3);
}
set_Value ("Posicion", Posicion);
}
/** Get Posicion */
public String getPosicion() 
{
return (String)get_Value("Posicion");
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
/** Set Proveedor */
public void setProveedor (String Proveedor)
{
if (Proveedor != null && Proveedor.length() > 50)
{
log.warning("Length > 50 - truncated");
Proveedor = Proveedor.substring(0,50);
}
set_Value ("Proveedor", Proveedor);
}
/** Get Proveedor */
public String getProveedor() 
{
return (String)get_Value("Proveedor");
}
/** Set Recibo */
public void setRecibo (String Recibo)
{
if (Recibo != null && Recibo.length() > 15)
{
log.warning("Length > 15 - truncated");
Recibo = Recibo.substring(0,15);
}
set_Value ("Recibo", Recibo);
}
/** Get Recibo */
public String getRecibo() 
{
return (String)get_Value("Recibo");
}
/** Set Referencia */
public void setReferencia (String Referencia)
{
if (Referencia != null && Referencia.length() > 10)
{
log.warning("Length > 10 - truncated");
Referencia = Referencia.substring(0,10);
}
set_Value ("Referencia", Referencia);
}
/** Get Referencia */
public String getReferencia() 
{
return (String)get_Value("Referencia");
}
/** Set Relleno */
public void setRelleno (String Relleno)
{
if (Relleno != null && Relleno.length() > 1)
{
log.warning("Length > 1 - truncated");
Relleno = Relleno.substring(0,1);
}
set_Value ("Relleno", Relleno);
}
/** Get Relleno */
public String getRelleno() 
{
return (String)get_Value("Relleno");
}
/** Set Retirante */
public void setRetirante (String Retirante)
{
if (Retirante != null && Retirante.length() > 30)
{
log.warning("Length > 30 - truncated");
Retirante = Retirante.substring(0,30);
}
set_Value ("Retirante", Retirante);
}
/** Get Retirante */
public String getRetirante() 
{
return (String)get_Value("Retirante");
}
/** Set Sucursal */
public void setSucursal (String Sucursal)
{
if (Sucursal != null && Sucursal.length() > 3)
{
log.warning("Length > 3 - truncated");
Sucursal = Sucursal.substring(0,3);
}
set_Value ("Sucursal", Sucursal);
}
/** Get Sucursal */
public String getSucursal() 
{
return (String)get_Value("Sucursal");
}
/** Set Tipo Documento */
public void setTipo_Documento (String Tipo_Documento)
{
if (Tipo_Documento != null && Tipo_Documento.length() > 3)
{
log.warning("Length > 3 - truncated");
Tipo_Documento = Tipo_Documento.substring(0,3);
}
set_Value ("Tipo_Documento", Tipo_Documento);
}
/** Get Tipo Documento */
public String getTipo_Documento() 
{
return (String)get_Value("Tipo_Documento");
}
/** Set Tipo Recibo */
public void setTipo_Recibo (String Tipo_Recibo)
{
if (Tipo_Recibo != null && Tipo_Recibo.length() > 2)
{
log.warning("Length > 2 - truncated");
Tipo_Recibo = Tipo_Recibo.substring(0,2);
}
set_Value ("Tipo_Recibo", Tipo_Recibo);
}
/** Get Tipo Recibo */
public String getTipo_Recibo() 
{
return (String)get_Value("Tipo_Recibo");
}
/** Set Vacio1 */
public void setVacio1 (String Vacio1)
{
if (Vacio1 != null && Vacio1.length() > 10)
{
log.warning("Length > 10 - truncated");
Vacio1 = Vacio1.substring(0,10);
}
set_Value ("Vacio1", Vacio1);
}
/** Get Vacio1 */
public String getVacio1() 
{
return (String)get_Value("Vacio1");
}
/** Set Vacio2 */
public void setVacio2 (String Vacio2)
{
if (Vacio2 != null && Vacio2.length() > 17)
{
log.warning("Length > 17 - truncated");
Vacio2 = Vacio2.substring(0,17);
}
set_Value ("Vacio2", Vacio2);
}
/** Get Vacio2 */
public String getVacio2() 
{
return (String)get_Value("Vacio2");
}
}
