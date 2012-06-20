/*
 * Exportación de duplicados electrónicos según resolución 1361 de la AFIP
 *  
 */

package org.openXpertya.model;

import java.util.Properties;

import org.openXpertya.util.Trx;

public class FiscalDocumentExport {

	// Constantes
	public static final String TABLAREF_TablaComprobantes = "TCOM";
	public static final String TABLAREF_TipoDocumento = "TDOC";
	public static final String TABLAREF_CodigosAduanas = "CADU";
	public static final String TABLAREF_TipoResponsable = "TRES";
	public static final String TABLAREF_CodigosMoneda = "CMON";
	public static final String TABLAREF_AlicuotasIva = "AIVA";
	public static final String TABLAREF_UnidadesMedida = "UMED";
	public static final String TABLAREF_CodigosJurisdiccionIIBB = "CJIB";
	public static final String TABLAREF_CodigosDestinacion = "CDES";
	public static final String TABLAREF_TablaPaises = "TPAI";
	public static final String TABLAREF_TablaImpuestos = "IMP";
	
	// Nombre de la transacción
	private String trxName;
	// La transacción
	private Trx trx;
	
	public FiscalDocumentExport(){
		// Creo la tansacción
		trxName = Trx.createTrxName();
		trx = Trx.get(trxName, true);
	}
	
	// Exportación de una factura electrónica realizada con impresora fiscal
	public void fiscalPrintingExport( Properties ctx, MInvoice inv ){
		this.createHdr( ctx, true, inv );
	}

	// Exportación de una factura electrónica que no fue realizada con impresora fiscal
	public void noFiscalPrintingExport( Properties ctx, MInvoice inv ){
		this.createHdr( ctx, false, inv );
	}
	
	public void createHdr( Properties ctx, boolean isFiscal, MInvoice inv ){
		try {
			// Instancio un nuevo registro del Header
			MElectronicInvoice invoiceHdr = new MElectronicInvoice(ctx, 0, trxName);
			// Lo exporto
			invoiceHdr.createHdr(isFiscal, inv);
			trx.commit();
		}
		catch (Exception e){
			trx.rollback();
		}
	}
}
