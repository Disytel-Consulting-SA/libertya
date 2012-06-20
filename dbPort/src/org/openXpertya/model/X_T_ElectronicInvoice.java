/** Modelo Generado - NO CAMBIAR MANUALMENTE - Disytel */
package org.openXpertya.model;
import java.util.*;
import java.sql.*;
import java.math.*;
import org.openXpertya.util.*;
/** Modelo Generado por T_ElectronicInvoice
 *  @author Comunidad de Desarrollo Libertya*         *Basado en Codigo Original Modificado, Revisado y Optimizado de:*         * Jorg Janke 
 *  @version  - 2010-05-28 16:33:58.756 */
public class X_T_ElectronicInvoice extends org.openXpertya.model.PO
{
/** Constructor estándar */
public X_T_ElectronicInvoice (Properties ctx, int T_ElectronicInvoice_ID, String trxName)
{
super (ctx, T_ElectronicInvoice_ID, trxName);
/** if (T_ElectronicInvoice_ID == 0)
{
setAD_PInstance_ID (0);
setDateTrx (new Timestamp(System.currentTimeMillis()));
setT_ElectronicInvoice_ID (0);
}
 */
}
/** Load Constructor */
public X_T_ElectronicInvoice (Properties ctx, ResultSet rs, String trxName)
{
super (ctx, rs, trxName);
}
/** AD_Table_ID */
public static final int Table_ID = M_Table.getTableID("T_ElectronicInvoice");

/** TableName=T_ElectronicInvoice */
public static final String Table_Name="T_ElectronicInvoice";

protected static KeyNamePair Model = new KeyNamePair(Table_ID,"T_ElectronicInvoice");
protected static BigDecimal AccessLevel = new BigDecimal(7);

/** Load Meta Data */
protected POInfo initPO (Properties ctx)
{
POInfo poi = POInfo.getPOInfo (ctx, Table_ID);
return poi;
}
public String toString()
{
StringBuffer sb = new StringBuffer ("X_T_ElectronicInvoice[").append(getID()).append("]");
return sb.toString();
}
/** Set Process Instance.
Instance of the process */
public void setAD_PInstance_ID (int AD_PInstance_ID)
{
set_Value ("AD_PInstance_ID", new Integer(AD_PInstance_ID));
}
/** Get Process Instance.
Instance of the process */
public int getAD_PInstance_ID() 
{
Integer ii = (Integer)get_Value("AD_PInstance_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Anio */
public void setAnio (String Anio)
{
if (Anio != null && Anio.length() > 4)
{
log.warning("Length > 4 - truncated");
Anio = Anio.substring(0,4);
}
set_Value ("Anio", Anio);
}
/** Get Anio */
public String getAnio() 
{
return (String)get_Value("Anio");
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
/** Set CompraCantRegTipo1 */
public void setCompraCantRegTipo1 (int CompraCantRegTipo1)
{
set_Value ("CompraCantRegTipo1", new Integer(CompraCantRegTipo1));
}
/** Get CompraCantRegTipo1 */
public int getCompraCantRegTipo1() 
{
Integer ii = (Integer)get_Value("CompraCantRegTipo1");
if (ii == null) return 0;
return ii.intValue();
}
/** Set CompraGrandTotal */
public void setCompraGrandTotal (BigDecimal CompraGrandTotal)
{
set_Value ("CompraGrandTotal", CompraGrandTotal);
}
/** Get CompraGrandTotal */
public BigDecimal getCompraGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("CompraGrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CompraImportePercepciones */
public void setCompraImportePercepciones (BigDecimal CompraImportePercepciones)
{
set_Value ("CompraImportePercepciones", CompraImportePercepciones);
}
/** Get CompraImportePercepciones */
public BigDecimal getCompraImportePercepciones() 
{
BigDecimal bd = (BigDecimal)get_Value("CompraImportePercepciones");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CompraImpuestosInternos */
public void setCompraImpuestosInternos (BigDecimal CompraImpuestosInternos)
{
set_Value ("CompraImpuestosInternos", CompraImpuestosInternos);
}
/** Get CompraImpuestosInternos */
public BigDecimal getCompraImpuestosInternos() 
{
BigDecimal bd = (BigDecimal)get_Value("CompraImpuestosInternos");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CompraImpuestosMunicipales */
public void setCompraImpuestosMunicipales (BigDecimal CompraImpuestosMunicipales)
{
set_Value ("CompraImpuestosMunicipales", CompraImpuestosMunicipales);
}
/** Get CompraImpuestosMunicipales */
public BigDecimal getCompraImpuestosMunicipales() 
{
BigDecimal bd = (BigDecimal)get_Value("CompraImpuestosMunicipales");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CompraOperacionesExentas */
public void setCompraOperacionesExentas (BigDecimal CompraOperacionesExentas)
{
set_Value ("CompraOperacionesExentas", CompraOperacionesExentas);
}
/** Get CompraOperacionesExentas */
public BigDecimal getCompraOperacionesExentas() 
{
BigDecimal bd = (BigDecimal)get_Value("CompraOperacionesExentas");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CompraPercepcionesIIBB */
public void setCompraPercepcionesIIBB (BigDecimal CompraPercepcionesIIBB)
{
set_Value ("CompraPercepcionesIIBB", CompraPercepcionesIIBB);
}
/** Get CompraPercepcionesIIBB */
public BigDecimal getCompraPercepcionesIIBB() 
{
BigDecimal bd = (BigDecimal)get_Value("CompraPercepcionesIIBB");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CompraRNI */
public void setCompraRNI (BigDecimal CompraRNI)
{
set_Value ("CompraRNI", CompraRNI);
}
/** Get CompraRNI */
public BigDecimal getCompraRNI() 
{
BigDecimal bd = (BigDecimal)get_Value("CompraRNI");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CompraTaxAmt */
public void setCompraTaxAmt (BigDecimal CompraTaxAmt)
{
set_Value ("CompraTaxAmt", CompraTaxAmt);
}
/** Get CompraTaxAmt */
public BigDecimal getCompraTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CompraTaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CompraTaxBaseAmt */
public void setCompraTaxBaseAmt (BigDecimal CompraTaxBaseAmt)
{
set_Value ("CompraTaxBaseAmt", CompraTaxBaseAmt);
}
/** Get CompraTaxBaseAmt */
public BigDecimal getCompraTaxBaseAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("CompraTaxBaseAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set CompraTotalLines */
public void setCompraTotalLines (BigDecimal CompraTotalLines)
{
set_Value ("CompraTotalLines", CompraTotalLines);
}
/** Get CompraTotalLines */
public BigDecimal getCompraTotalLines() 
{
BigDecimal bd = (BigDecimal)get_Value("CompraTotalLines");
if (bd == null) return Env.ZERO;
return bd;
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
public void setDateCAI (String DateCAI)
{
if (DateCAI != null && DateCAI.length() > 8)
{
log.warning("Length > 8 - truncated");
DateCAI = DateCAI.substring(0,8);
}
set_Value ("DateCAI", DateCAI);
}
/** Get CAI Date */
public String getDateCAI() 
{
return (String)get_Value("DateCAI");
}
/** Set Date Invoiced.
Date printed on Invoice */
public void setDateInvoiced (String DateInvoiced)
{
if (DateInvoiced != null && DateInvoiced.length() > 8)
{
log.warning("Length > 8 - truncated");
DateInvoiced = DateInvoiced.substring(0,8);
}
set_Value ("DateInvoiced", DateInvoiced);
}
/** Get Date Invoiced.
Date printed on Invoice */
public String getDateInvoiced() 
{
return (String)get_Value("DateInvoiced");
}
/** Set Transaction Date.
Transaction Date */
public void setDateTrx (Timestamp DateTrx)
{
if (DateTrx == null) throw new IllegalArgumentException ("DateTrx is mandatory");
set_Value ("DateTrx", DateTrx);
}
/** Get Transaction Date.
Transaction Date */
public Timestamp getDateTrx() 
{
return (Timestamp)get_Value("DateTrx");
}
/** Set DateVoid */
public void setDateVoid (String DateVoid)
{
if (DateVoid != null && DateVoid.length() > 8)
{
log.warning("Length > 8 - truncated");
DateVoid = DateVoid.substring(0,8);
}
set_Value ("DateVoid", DateVoid);
}
/** Get DateVoid */
public String getDateVoid() 
{
return (String)get_Value("DateVoid");
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
/** Set Diseno_Libre */
public void setDiseno_Libre (String Diseno_Libre)
{
if (Diseno_Libre != null && Diseno_Libre.length() > 200)
{
log.warning("Length > 200 - truncated");
Diseno_Libre = Diseno_Libre.substring(0,200);
}
set_Value ("Diseno_Libre", Diseno_Libre);
}
/** Get Diseno_Libre */
public String getDiseno_Libre() 
{
return (String)get_Value("Diseno_Libre");
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
/** Set FactCantRegTipo1 */
public void setFactCantRegTipo1 (int FactCantRegTipo1)
{
set_Value ("FactCantRegTipo1", new Integer(FactCantRegTipo1));
}
/** Get FactCantRegTipo1 */
public int getFactCantRegTipo1() 
{
Integer ii = (Integer)get_Value("FactCantRegTipo1");
if (ii == null) return 0;
return ii.intValue();
}
/** Set FactGrandTotal */
public void setFactGrandTotal (BigDecimal FactGrandTotal)
{
set_Value ("FactGrandTotal", FactGrandTotal);
}
/** Get FactGrandTotal */
public BigDecimal getFactGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("FactGrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set FactImportePercepciones */
public void setFactImportePercepciones (BigDecimal FactImportePercepciones)
{
set_Value ("FactImportePercepciones", FactImportePercepciones);
}
/** Get FactImportePercepciones */
public BigDecimal getFactImportePercepciones() 
{
BigDecimal bd = (BigDecimal)get_Value("FactImportePercepciones");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set FactImpuestosInternos */
public void setFactImpuestosInternos (BigDecimal FactImpuestosInternos)
{
set_Value ("FactImpuestosInternos", FactImpuestosInternos);
}
/** Get FactImpuestosInternos */
public BigDecimal getFactImpuestosInternos() 
{
BigDecimal bd = (BigDecimal)get_Value("FactImpuestosInternos");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set FactImpuestosMunicipales */
public void setFactImpuestosMunicipales (BigDecimal FactImpuestosMunicipales)
{
set_Value ("FactImpuestosMunicipales", FactImpuestosMunicipales);
}
/** Get FactImpuestosMunicipales */
public BigDecimal getFactImpuestosMunicipales() 
{
BigDecimal bd = (BigDecimal)get_Value("FactImpuestosMunicipales");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set FactOperacionesExentas */
public void setFactOperacionesExentas (BigDecimal FactOperacionesExentas)
{
set_Value ("FactOperacionesExentas", FactOperacionesExentas);
}
/** Get FactOperacionesExentas */
public BigDecimal getFactOperacionesExentas() 
{
BigDecimal bd = (BigDecimal)get_Value("FactOperacionesExentas");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set FactPercepcionesIIBB */
public void setFactPercepcionesIIBB (BigDecimal FactPercepcionesIIBB)
{
set_Value ("FactPercepcionesIIBB", FactPercepcionesIIBB);
}
/** Get FactPercepcionesIIBB */
public BigDecimal getFactPercepcionesIIBB() 
{
BigDecimal bd = (BigDecimal)get_Value("FactPercepcionesIIBB");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set FactRNI */
public void setFactRNI (BigDecimal FactRNI)
{
set_Value ("FactRNI", FactRNI);
}
/** Get FactRNI */
public BigDecimal getFactRNI() 
{
BigDecimal bd = (BigDecimal)get_Value("FactRNI");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set FactTaxAmt */
public void setFactTaxAmt (BigDecimal FactTaxAmt)
{
set_Value ("FactTaxAmt", FactTaxAmt);
}
/** Get FactTaxAmt */
public BigDecimal getFactTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FactTaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set FactTaxBaseAmt */
public void setFactTaxBaseAmt (BigDecimal FactTaxBaseAmt)
{
set_Value ("FactTaxBaseAmt", FactTaxBaseAmt);
}
/** Get FactTaxBaseAmt */
public BigDecimal getFactTaxBaseAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("FactTaxBaseAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set FactTotalLines */
public void setFactTotalLines (BigDecimal FactTotalLines)
{
set_Value ("FactTotalLines", FactTotalLines);
}
/** Get FactTotalLines */
public BigDecimal getFactTotalLines() 
{
BigDecimal bd = (BigDecimal)get_Value("FactTotalLines");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set FechaDespachoPlaza */
public void setFechaDespachoPlaza (String FechaDespachoPlaza)
{
if (FechaDespachoPlaza != null && FechaDespachoPlaza.length() > 8)
{
log.warning("Length > 8 - truncated");
FechaDespachoPlaza = FechaDespachoPlaza.substring(0,8);
}
set_Value ("FechaDespachoPlaza", FechaDespachoPlaza);
}
/** Get FechaDespachoPlaza */
public String getFechaDespachoPlaza() 
{
return (String)get_Value("FechaDespachoPlaza");
}
/** Set Fiscal */
public void setFiscal (String Fiscal)
{
if (Fiscal != null && Fiscal.length() > 1)
{
log.warning("Length > 1 - truncated");
Fiscal = Fiscal.substring(0,1);
}
set_Value ("Fiscal", Fiscal);
}
/** Get Fiscal */
public String getFiscal() 
{
return (String)get_Value("Fiscal");
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
/** Set Importe_Bonificacion */
public void setImporte_Bonificacion (BigDecimal Importe_Bonificacion)
{
set_Value ("Importe_Bonificacion", Importe_Bonificacion);
}
/** Get Importe_Bonificacion */
public BigDecimal getImporte_Bonificacion() 
{
BigDecimal bd = (BigDecimal)get_Value("Importe_Bonificacion");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set Indica_Anulacion */
public void setIndica_Anulacion (String Indica_Anulacion)
{
if (Indica_Anulacion != null && Indica_Anulacion.length() > 1)
{
log.warning("Length > 1 - truncated");
Indica_Anulacion = Indica_Anulacion.substring(0,1);
}
set_Value ("Indica_Anulacion", Indica_Anulacion);
}
/** Get Indica_Anulacion */
public String getIndica_Anulacion() 
{
return (String)get_Value("Indica_Anulacion");
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
/** Set Line Amount.
Line Extended Amount (Quantity * Actual Price) without Freight and Charges */
public void setLineNetAmt (BigDecimal LineNetAmt)
{
set_Value ("LineNetAmt", LineNetAmt);
}
/** Get Line Amount.
Line Extended Amount (Quantity * Actual Price) without Freight and Charges */
public BigDecimal getLineNetAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineNetAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Line Total.
Total line amount incl. Tax */
public void setLineTotalAmt (BigDecimal LineTotalAmt)
{
set_Value ("LineTotalAmt", LineTotalAmt);
}
/** Get Line Total.
Total line amount incl. Tax */
public BigDecimal getLineTotalAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("LineTotalAmt");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set periodo */
public void setperiodo (String periodo)
{
if (periodo != null && periodo.length() > 6)
{
log.warning("Length > 6 - truncated");
periodo = periodo.substring(0,6);
}
set_Value ("periodo", periodo);
}
/** Get periodo */
public String getperiodo() 
{
return (String)get_Value("periodo");
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
/** Set Invoiced Quantity.
Invoiced Quantity */
public void setQtyInvoiced (BigDecimal QtyInvoiced)
{
set_Value ("QtyInvoiced", QtyInvoiced);
}
/** Get Invoiced Quantity.
Invoiced Quantity */
public BigDecimal getQtyInvoiced() 
{
BigDecimal bd = (BigDecimal)get_Value("QtyInvoiced");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set Rate.
Rate or Tax or Exchange */
public void setRate (BigDecimal Rate)
{
set_Value ("Rate", Rate);
}
/** Get Rate.
Rate or Tax or Exchange */
public BigDecimal getRate() 
{
BigDecimal bd = (BigDecimal)get_Value("Rate");
if (bd == null) return Env.ZERO;
return bd;
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
/** Set TaxExempt */
public void setTaxExempt (String TaxExempt)
{
if (TaxExempt != null && TaxExempt.length() > 1)
{
log.warning("Length > 1 - truncated");
TaxExempt = TaxExempt.substring(0,1);
}
set_Value ("TaxExempt", TaxExempt);
}
/** Get TaxExempt */
public String getTaxExempt() 
{
return (String)get_Value("TaxExempt");
}
/** Set T_ElectronicInvoice_ID */
public void setT_ElectronicInvoice_ID (int T_ElectronicInvoice_ID)
{
set_ValueNoCheck ("T_ElectronicInvoice_ID", new Integer(T_ElectronicInvoice_ID));
}
/** Get T_ElectronicInvoice_ID */
public int getT_ElectronicInvoice_ID() 
{
Integer ii = (Integer)get_Value("T_ElectronicInvoice_ID");
if (ii == null) return 0;
return ii.intValue();
}
/** Set Tipo */
public void settipo (String tipo)
{
if (tipo != null && tipo.length() > 1)
{
log.warning("Length > 1 - truncated");
tipo = tipo.substring(0,1);
}
set_Value ("tipo", tipo);
}
/** Get Tipo */
public String gettipo() 
{
return (String)get_Value("tipo");
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
/** Set TipoReg1 */
public void setTipoReg1 (int TipoReg1)
{
set_Value ("TipoReg1", new Integer(TipoReg1));
}
/** Get TipoReg1 */
public int getTipoReg1() 
{
Integer ii = (Integer)get_Value("TipoReg1");
if (ii == null) return 0;
return ii.intValue();
}
/** Set TipoReg2 */
public void setTipoReg2 (int TipoReg2)
{
set_Value ("TipoReg2", new Integer(TipoReg2));
}
/** Get TipoReg2 */
public int getTipoReg2() 
{
Integer ii = (Integer)get_Value("TipoReg2");
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
/** Set Unidad_Medida */
public void setUnidad_Medida (String Unidad_Medida)
{
if (Unidad_Medida != null && Unidad_Medida.length() > 2)
{
log.warning("Length > 2 - truncated");
Unidad_Medida = Unidad_Medida.substring(0,2);
}
set_Value ("Unidad_Medida", Unidad_Medida);
}
/** Get Unidad_Medida */
public String getUnidad_Medida() 
{
return (String)get_Value("Unidad_Medida");
}
/** Set VentaCantRegTipo1 */
public void setVentaCantRegTipo1 (int VentaCantRegTipo1)
{
set_Value ("VentaCantRegTipo1", new Integer(VentaCantRegTipo1));
}
/** Get VentaCantRegTipo1 */
public int getVentaCantRegTipo1() 
{
Integer ii = (Integer)get_Value("VentaCantRegTipo1");
if (ii == null) return 0;
return ii.intValue();
}
/** Set VentaGrandTotal */
public void setVentaGrandTotal (BigDecimal VentaGrandTotal)
{
set_Value ("VentaGrandTotal", VentaGrandTotal);
}
/** Get VentaGrandTotal */
public BigDecimal getVentaGrandTotal() 
{
BigDecimal bd = (BigDecimal)get_Value("VentaGrandTotal");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VentaImportePercepciones */
public void setVentaImportePercepciones (BigDecimal VentaImportePercepciones)
{
set_Value ("VentaImportePercepciones", VentaImportePercepciones);
}
/** Get VentaImportePercepciones */
public BigDecimal getVentaImportePercepciones() 
{
BigDecimal bd = (BigDecimal)get_Value("VentaImportePercepciones");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VentaImpuestosInternos */
public void setVentaImpuestosInternos (BigDecimal VentaImpuestosInternos)
{
set_Value ("VentaImpuestosInternos", VentaImpuestosInternos);
}
/** Get VentaImpuestosInternos */
public BigDecimal getVentaImpuestosInternos() 
{
BigDecimal bd = (BigDecimal)get_Value("VentaImpuestosInternos");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VentaImpuestosMunicipales */
public void setVentaImpuestosMunicipales (BigDecimal VentaImpuestosMunicipales)
{
set_Value ("VentaImpuestosMunicipales", VentaImpuestosMunicipales);
}
/** Get VentaImpuestosMunicipales */
public BigDecimal getVentaImpuestosMunicipales() 
{
BigDecimal bd = (BigDecimal)get_Value("VentaImpuestosMunicipales");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VentaOperacionesExentas */
public void setVentaOperacionesExentas (BigDecimal VentaOperacionesExentas)
{
set_Value ("VentaOperacionesExentas", VentaOperacionesExentas);
}
/** Get VentaOperacionesExentas */
public BigDecimal getVentaOperacionesExentas() 
{
BigDecimal bd = (BigDecimal)get_Value("VentaOperacionesExentas");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VentaPercepcionesIIBB */
public void setVentaPercepcionesIIBB (BigDecimal VentaPercepcionesIIBB)
{
set_Value ("VentaPercepcionesIIBB", VentaPercepcionesIIBB);
}
/** Get VentaPercepcionesIIBB */
public BigDecimal getVentaPercepcionesIIBB() 
{
BigDecimal bd = (BigDecimal)get_Value("VentaPercepcionesIIBB");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VentaRNI */
public void setVentaRNI (BigDecimal VentaRNI)
{
set_Value ("VentaRNI", VentaRNI);
}
/** Get VentaRNI */
public BigDecimal getVentaRNI() 
{
BigDecimal bd = (BigDecimal)get_Value("VentaRNI");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VentaTaxAmt */
public void setVentaTaxAmt (BigDecimal VentaTaxAmt)
{
set_Value ("VentaTaxAmt", VentaTaxAmt);
}
/** Get VentaTaxAmt */
public BigDecimal getVentaTaxAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("VentaTaxAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VentaTaxBaseAmt */
public void setVentaTaxBaseAmt (BigDecimal VentaTaxBaseAmt)
{
set_Value ("VentaTaxBaseAmt", VentaTaxBaseAmt);
}
/** Get VentaTaxBaseAmt */
public BigDecimal getVentaTaxBaseAmt() 
{
BigDecimal bd = (BigDecimal)get_Value("VentaTaxBaseAmt");
if (bd == null) return Env.ZERO;
return bd;
}
/** Set VentaTotalLines */
public void setVentaTotalLines (BigDecimal VentaTotalLines)
{
set_Value ("VentaTotalLines", VentaTotalLines);
}
/** Get VentaTotalLines */
public BigDecimal getVentaTotalLines() 
{
BigDecimal bd = (BigDecimal)get_Value("VentaTotalLines");
if (bd == null) return Env.ZERO;
return bd;
}
}
