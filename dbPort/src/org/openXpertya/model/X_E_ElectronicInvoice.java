/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por E_ElectronicInvoice
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-05-28 14:49:30.583 */
public class X_E_ElectronicInvoice extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_E_ElectronicInvoice (Properties ctx, int E_ElectronicInvoice_ID, String trxName)
{
super (ctx, E_ElectronicInvoice_ID, trxName);
/** if (E_ElectronicInvoice_ID == 0)
{
setE_ElectronicInvoice_ID (0);
}
 */
}
/** Load Constructor */
public X_E_ElectronicInvoice (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("E_ElectronicInvoice");

/** TableName=E_ElectronicInvoice */
public static final String Table_Name="E_ElectronicInvoice";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"E_ElectronicInvoice");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_E_ElectronicInvoice[").append(getID()).append("]");
return sb.toString();
}
/** Set CAI */
public void setCAI (String CAI)
{
if (CAI != null && CAI.length() > 14)
{
log.warning("Length > 14 - truncated");
CAI = CAI.substring(0,14);
}
set_Value ("CAI", CAI);
}
/** Get CAI */
public String getCAI() 
{
return (String)get_Value("CAI");
}
/** Set Cant_Alicuotas_Iva */
public void setCant_Alicuotas_Iva (int Cant_Alicuotas_Iva)
{
set_Value ("Cant_Alicuotas_Iva", new Integer(Cant_Alicuotas_Iva));
}
/** Get Cant_Alicuotas_Iva */
public int getCant_Alicuotas_Iva() 
{
Integer ii = (Integer)get_Value("Cant_Alicuotas_Iva");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cant_Hojas */
public void setCant_Hojas (int Cant_Hojas)
{
set_Value ("Cant_Hojas", new Integer(Cant_Hojas));
}
/** Get Cant_Hojas */
public int getCant_Hojas() 
{
Integer ii = (Integer)get_Value("Cant_Hojas");
if (ii == null) return 0;
return ii.intValue();
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
/** Set Cod_Aduana */
public void setCod_Aduana (int Cod_Aduana)
{
set_Value ("Cod_Aduana", new Integer(Cod_Aduana));
}
/** Get Cod_Aduana */
public int getCod_Aduana() 
{
Integer ii = (Integer)get_Value("Cod_Aduana");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cod_Destinacion */
public void setCod_Destinacion (String Cod_Destinacion)
{
if (Cod_Destinacion != null && Cod_Destinacion.length() > 4)
{
log.warning("Length > 4 - truncated");
Cod_Destinacion = Cod_Destinacion.substring(0,4);
}
set_Value ("Cod_Destinacion", Cod_Destinacion);
}
/** Get Cod_Destinacion */
public String getCod_Destinacion() 
{
return (String)get_Value("Cod_Destinacion");
}
/** Set Cod_Jurisdiccion_IIBB */
public void setCod_Jurisdiccion_IIBB (int Cod_Jurisdiccion_IIBB)
{
set_Value ("Cod_Jurisdiccion_IIBB", new Integer(Cod_Jurisdiccion_IIBB));
}
/** Get Cod_Jurisdiccion_IIBB */
public int getCod_Jurisdiccion_IIBB() 
{
Integer ii = (Integer)get_Value("Cod_Jurisdiccion_IIBB");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Cod_Moneda */
public void setCod_Moneda (String Cod_Moneda)
{
if (Cod_Moneda != null && Cod_Moneda.length() > 3)
{
log.warning("Length > 3 - truncated");
Cod_Moneda = Cod_Moneda.substring(0,3);
}
set_Value ("Cod_Moneda", Cod_Moneda);
}
/** Get Cod_Moneda */
public String getCod_Moneda() 
{
return (String)get_Value("Cod_Moneda");
}
/** Set Cod_Operacion */
public void setCod_Operacion (String Cod_Operacion)
{
if (Cod_Operacion != null && Cod_Operacion.length() > 1)
{
log.warning("Length > 1 - truncated");
Cod_Operacion = Cod_Operacion.substring(0,1);
}
set_Value ("Cod_Operacion", Cod_Operacion);
}
/** Get Cod_Operacion */
public String getCod_Operacion() 
{
return (String)get_Value("Cod_Operacion");
}
/** Set CUIT */
public void setCUIT (String CUIT)
{
if (CUIT != null && CUIT.length() > 20)
{
log.warning("Length > 20 - truncated");
CUIT = CUIT.substring(0,20);
}
set_Value ("CUIT", CUIT);
}
/** Get CUIT */
public String getCUIT() 
{
return (String)get_Value("CUIT");
}
/** Set CAI Date */
public void setDateCAI (Timestamp DateCAI)
{
set_Value ("DateCAI", DateCAI);
}
/** Get CAI Date */
public Timestamp getDateCAI() 
{
return (Timestamp)get_Value("DateCAI");
}
/** Set Date Invoiced.
Date printed on Invoice */
public void setDateInvoiced (Timestamp DateInvoiced)
{
set_Value ("DateInvoiced", DateInvoiced);
}
/** Get Date Invoiced.
Date printed on Invoice */
public Timestamp getDateInvoiced() 
{
return (Timestamp)get_Value("DateInvoiced");
}
/** Set DateVoid */
public void setDateVoid (Timestamp DateVoid)
{
set_Value ("DateVoid", DateVoid);
}
/** Get DateVoid */
public Timestamp getDateVoid() 
{
return (Timestamp)get_Value("DateVoid");
}
/** Set Description.
Optional short description of the record */
public void setDescription (String Description)
{
if (Description != null && Description.length() > 255)
{
log.warning("Length > 255 - truncated");
Description = Description.substring(0,255);
}
set_Value ("Description", Description);
}
/** Get Description.
Optional short description of the record */
public String getDescription() 
{
return (String)get_Value("Description");
}
/** Set DigVerifNroDespacho */
public void setDigVerifNroDespacho (String DigVerifNroDespacho)
{
if (DigVerifNroDespacho != null && DigVerifNroDespacho.length() > 1)
{
log.warning("Length > 1 - truncated");
DigVerifNroDespacho = DigVerifNroDespacho.substring(0,1);
}
set_Value ("DigVerifNroDespacho", DigVerifNroDespacho);
}
/** Get DigVerifNroDespacho */
public String getDigVerifNroDespacho() 
{
return (String)get_Value("DigVerifNroDespacho");
}
/** Set Doc_Identificatorio_Comprador */
public void setDoc_Identificatorio_Comprador (int Doc_Identificatorio_Comprador)
{
set_Value ("Doc_Identificatorio_Comprador", new Integer(Doc_Identificatorio_Comprador));
}
/** Get Doc_Identificatorio_Comprador */
public int getDoc_Identificatorio_Comprador() 
{
Integer ii = (Integer)get_Value("Doc_Identificatorio_Comprador");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Doc_Identificatorio_Vendedor */
public void setDoc_Identificatorio_Vendedor (int Doc_Identificatorio_Vendedor)
{
set_Value ("Doc_Identificatorio_Vendedor", new Integer(Doc_Identificatorio_Vendedor));
}
/** Get Doc_Identificatorio_Vendedor */
public int getDoc_Identificatorio_Vendedor() 
{
Integer ii = (Integer)get_Value("Doc_Identificatorio_Vendedor");
if (ii == null) return 0;
return ii.intValue();
}
/** Set E_ElectronicInvoice_ID */
public void setE_ElectronicInvoice_ID (int E_ElectronicInvoice_ID)
{
set_ValueNoCheck ("E_ElectronicInvoice_ID", new Integer(E_ElectronicInvoice_ID));
}
/** Get E_ElectronicInvoice_ID */
public int getE_ElectronicInvoice_ID() 
{
Integer ii = (Integer)get_Value("E_ElectronicInvoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set FechaDespachoPlaza */
public void setFechaDespachoPlaza (Timestamp FechaDespachoPlaza)
{
set_Value ("FechaDespachoPlaza", FechaDespachoPlaza);
}
/** Get FechaDespachoPlaza */
public Timestamp getFechaDespachoPlaza() 
{
return (Timestamp)get_Value("FechaDespachoPlaza");
}
/** Set Grand Total.
Total amount of document */
public void setGrandTotal (BigDecimal GrandTotal)
{
set_Value ("GrandTotal", GrandTotal);
}
/** Get Grand Total.
Total amount of document */
public BigDecimal getGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("GrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Identif_Comprador */
public void setIdentif_Comprador (String Identif_Comprador)
{
if (Identif_Comprador != null && Identif_Comprador.length() > 20)
{
log.warning("Length > 20 - truncated");
Identif_Comprador = Identif_Comprador.substring(0,20);
}
set_Value ("Identif_Comprador", Identif_Comprador);
}
/** Get Identif_Comprador */
public String getIdentif_Comprador() 
{
return (String)get_Value("Identif_Comprador");
}
/** Set Identif_Vendedor */
public void setIdentif_Vendedor (String Identif_Vendedor)
{
if (Identif_Vendedor != null && Identif_Vendedor.length() > 20)
{
log.warning("Length > 20 - truncated");
Identif_Vendedor = Identif_Vendedor.substring(0,20);
}
set_Value ("Identif_Vendedor", Identif_Vendedor);
}
/** Get Identif_Vendedor */
public String getIdentif_Vendedor() 
{
return (String)get_Value("Identif_Vendedor");
}
/** Set ImportePercepciones */
public void setImportePercepciones (BigDecimal ImportePercepciones)
{
set_Value ("ImportePercepciones", ImportePercepciones);
}
/** Get ImportePercepciones */
public BigDecimal getImportePercepciones() 
{
BigDecimal bd = (BigDecimal)get_Value("ImportePercepciones");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set ImpuestosInternos */
public void setImpuestosInternos (BigDecimal ImpuestosInternos)
{
set_Value ("ImpuestosInternos", ImpuestosInternos);
}
/** Get ImpuestosInternos */
public BigDecimal getImpuestosInternos() 
{
BigDecimal bd = (BigDecimal)get_Value("ImpuestosInternos");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set ImpuestosMunicipales */
public void setImpuestosMunicipales (BigDecimal ImpuestosMunicipales)
{
set_Value ("ImpuestosMunicipales", ImpuestosMunicipales);
}
/** Get ImpuestosMunicipales */
public BigDecimal getImpuestosMunicipales() 
{
BigDecimal bd = (BigDecimal)get_Value("ImpuestosMunicipales");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Is Fiscal */
public void setIsFiscal (boolean IsFiscal)
{
set_Value ("IsFiscal", new Boolean(IsFiscal));
}
/** Get Is Fiscal */
public boolean isFiscal() 
{
Object oo = get_Value("IsFiscal");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set Sales Transaction.
This is a Sales Transaction */
public void setIsSOTrx (boolean IsSOTrx)
{
set_Value ("IsSOTrx", new Boolean(IsSOTrx));
}
/** Get Sales Transaction.
This is a Sales Transaction */
public boolean isSOTrx() 
{
Object oo = get_Value("IsSOTrx");
if (oo != null) 
{
 if (oo instanceof Boolean) return ((Boolean)oo).booleanValue();
 return "Y".equals(oo);
}
return false;
}
/** Set JurImpuestosMunicipales */
public void setJurImpuestosMunicipales (String JurImpuestosMunicipales)
{
if (JurImpuestosMunicipales != null && JurImpuestosMunicipales.length() > 40)
{
log.warning("Length > 40 - truncated");
JurImpuestosMunicipales = JurImpuestosMunicipales.substring(0,40);
}
set_Value ("JurImpuestosMunicipales", JurImpuestosMunicipales);
}
/** Get JurImpuestosMunicipales */
public String getJurImpuestosMunicipales() 
{
return (String)get_Value("JurImpuestosMunicipales");
}
/** Set Multiply Rate.
Rate to multiple the source by to calculate the target. */
public void setMultiplyRate (BigDecimal MultiplyRate)
{
set_Value ("MultiplyRate", MultiplyRate);
}
/** Get Multiply Rate.
Rate to multiple the source by to calculate the target. */
public BigDecimal getMultiplyRate() 
{
BigDecimal bd = (BigDecimal)get_Value("MultiplyRate");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Name.
Alphanumeric identifier of the entity */
public void setName (String Name)
{
if (Name != null && Name.length() > 60)
{
log.warning("Length > 60 - truncated");
Name = Name.substring(0,60);
}
set_Value ("Name", Name);
}
/** Get Name.
Alphanumeric identifier of the entity */
public String getName() 
{
return (String)get_Value("Name");
}
public KeyNamePair getKeyNamePair() 
{
return new KeyNamePair(getID(), getName());
}
/** Set Nombre Entidad Facturada */
public void setNombreCli (String NombreCli)
{
if (NombreCli != null && NombreCli.length() > 40)
{
log.warning("Length > 40 - truncated");
NombreCli = NombreCli.substring(0,40);
}
set_Value ("NombreCli", NombreCli);
}
/** Get Nombre Entidad Facturada */
public String getNombreCli() 
{
return (String)get_Value("NombreCli");
}
/** Set NroDespacho */
public void setNroDespacho (int NroDespacho)
{
set_Value ("NroDespacho", new Integer(NroDespacho));
}
/** Get NroDespacho */
public int getNroDespacho() 
{
Integer ii = (Integer)get_Value("NroDespacho");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Numero Comprobante */
public void setNumeroComprobante (int NumeroComprobante)
{
set_Value ("NumeroComprobante", new Integer(NumeroComprobante));
}
/** Get Numero Comprobante */
public int getNumeroComprobante() 
{
Integer ii = (Integer)get_Value("NumeroComprobante");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Numero De Documento */
public void setNumeroDeDocumento (String NumeroDeDocumento)
{
if (NumeroDeDocumento != null && NumeroDeDocumento.length() > 30)
{
log.warning("Length > 30 - truncated");
NumeroDeDocumento = NumeroDeDocumento.substring(0,30);
}
set_Value ("NumeroDeDocumento", NumeroDeDocumento);
}
/** Get Numero De Documento */
public String getNumeroDeDocumento() 
{
return (String)get_Value("NumeroDeDocumento");
}
/** Set OperacionesExentas */
public void setOperacionesExentas (BigDecimal OperacionesExentas)
{
set_Value ("OperacionesExentas", OperacionesExentas);
}
/** Get OperacionesExentas */
public BigDecimal getOperacionesExentas() 
{
BigDecimal bd = (BigDecimal)get_Value("OperacionesExentas");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set PercepcionesIIBB */
public void setPercepcionesIIBB (BigDecimal PercepcionesIIBB)
{
set_Value ("PercepcionesIIBB", PercepcionesIIBB);
}
/** Get PercepcionesIIBB */
public BigDecimal getPercepcionesIIBB() 
{
BigDecimal bd = (BigDecimal)get_Value("PercepcionesIIBB");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set RNI */
public void setRNI (BigDecimal RNI)
{
set_Value ("RNI", RNI);
}
/** Get RNI */
public BigDecimal getRNI() 
{
BigDecimal bd = (BigDecimal)get_Value("RNI");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Tax Amount.
Tax Amount for a document */
public void setTaxAmt (BigDecimal TaxAmt)
{
set_Value ("TaxAmt", TaxAmt);
}
/** Get Tax Amount.
Tax Amount for a document */
public BigDecimal getTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Tax base Amount.
Base for calculating the tax amount */
public void setTaxBaseAmt (BigDecimal TaxBaseAmt)
{
set_Value ("TaxBaseAmt", TaxBaseAmt);
}
/** Get Tax base Amount.
Base for calculating the tax amount */
public BigDecimal getTaxBaseAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("TaxBaseAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Tipo_Comprobante */
public void setTipo_Comprobante (int Tipo_Comprobante)
{
set_Value ("Tipo_Comprobante", new Integer(Tipo_Comprobante));
}
/** Get Tipo_Comprobante */
public int getTipo_Comprobante() 
{
Integer ii = (Integer)get_Value("Tipo_Comprobante");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tipo_Responsable */
public void setTipo_Responsable (int Tipo_Responsable)
{
set_Value ("Tipo_Responsable", new Integer(Tipo_Responsable));
}
/** Get Tipo_Responsable */
public int getTipo_Responsable() 
{
Integer ii = (Integer)get_Value("Tipo_Responsable");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Total Lines.
Total of all document lines */
public void setTotalLines (BigDecimal TotalLines)
{
set_Value ("TotalLines", TotalLines);
}
/** Get Total Lines.
Total of all document lines */
public BigDecimal getTotalLines() 
{
BigDecimal bd = (BigDecimal)get_Value("TotalLines");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Transporte */
public void setTransporte (BigDecimal Transporte)
{
set_Value ("Transporte", Transporte);
}
/** Get Transporte */
public BigDecimal getTransporte() 
{
BigDecimal bd = (BigDecimal)get_Value("Transporte");
if (bd == null) return Env.ZERO;
return bd;
}
}
