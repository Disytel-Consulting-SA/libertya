package org.openXpertya.util;

import java.math.BigInteger;
import java.text.SimpleDateFormat;

import org.apache.commons.codec.binary.Base64;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCurrency;
import org.openXpertya.model.MDocType;
import org.openXpertya.model.MInvoice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FacturaElectronicaQRCodeGenerator extends FacturaElectronicaBarcodeGenerator {

	MInvoice invoice = null;
	
	public FacturaElectronicaQRCodeGenerator(MInvoice invoice, MDocType docType, String clientCUIT) {
		super(invoice, docType, clientCUIT);
		this.invoice = invoice;
	}
	
	public String getQRCode(){
		
		MBPartner bPartner = new MBPartner(invoice.getCtx(), invoice.getC_BPartner_ID(), invoice.get_TrxName()); 
		
		/* Segun https://www.afip.gob.ar/fe/qr/especificaciones.asp */
		FacturaElectronicaQRData qrData = new FacturaElectronicaQRData();
		
		// OBLIGATORIO – versión del formato de los datos del comprobante
		qrData.setVer(getVer()); 
		// OBLIGATORIO – Fecha de emisión del comprobante
		qrData.setFecha(getFecha());
		// OBLIGATORIO – Cuit del Emisor del comprobante
		qrData.setCuit(getClientCUIT());
		// OBLIGATORIO – Punto de venta utilizado para emitir el comprobante
		qrData.setPtoVta(getPtoVta());
		// OBLIGATORIO – tipo de comprobante
		qrData.setTipoCmp(getTipoCmp());
		// OBLIGATORIO – Número del comprobante
		qrData.setNroCmp(getNroCmp());
		// OBLIGATORIO – Importe Total del comprobante (en la moneda en la que fue emitido)
		qrData.setImporte(getImporte());
		// OBLIGATORIO – Moneda del comprobante
		qrData.setMoneda(getMoneda(invoice)); 
		// Cotización en pesos argentinos de la moneda utilizada (1 cuando la moneda sea pesos)
		qrData.setCtz(getCotizacion(invoice)); 
		// DE CORRESPONDER – Código del Tipo de documento del receptor
		if (getTipoDocRec(bPartner)>0)
			qrData.setTipoDocRec(getTipoDocRec(bPartner));
		// DE CORRESPONDER – Número de documento del receptor correspondiente al tipo de documento indicado
		if (getNroDocRec()!=null)
			qrData.setNroDocRec(getNroDocRec());
		// OBLIGATORIO – “A” para comprobante autorizado por CAEA, “E” para comprobante autorizado por CAE
		qrData.setTipoCodAut(getTipoCodAut());  
		// OBLIGATORIO – Código de autorización otorgado por AFIP para el comprobante
		qrData.setCodAut(getCodAut());
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String jsonData = gson.toJson(qrData, FacturaElectronicaQRData.class);
		String base64Data = Base64.encodeBase64String(jsonData.getBytes());
					
		return getBaseURL() + base64Data;
	}
	
	/** Visualizar o no el Barcode
	 *  Busca el atributo del servicio WSFEQRCODE, bajo el value DisplayQRCode  
	 *  De no encontrarlo retorna true */
	@Override
	public boolean isDisplayed() {
		try {
			return "Y".equalsIgnoreCase((extService.getAttributeByName("DisplayQRCode")).getName());
		} catch (Exception e) {
			getLog().warning("Atributo DisplayQRCode para servicio externo WSFEQRCODE no encontrado. Default a Y.");
			return true;
		}
	}
	
	/** Retorna la version 
	 * Busca el atributo del servicio WSFEQRCODE, bajo el value Version 
	 *  De no encontrarlo retorna 1 */
	public int getVer() {
		try {
			return Integer.parseInt((extService.getAttributeByName("Version")).getName());
		} catch (Exception e) {
			getLog().warning("Atributo Version para servicio externo WSFEQRCODE no encontrado. Default a 1.");
			return 1;
		}
	}
	
	/** Fecha de facturacion */ 
	public String getFecha() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(invoice.getDateInvoiced());
	}
	
	/** CUIT del emisor del comprobante */
	public BigInteger getClientCUIT() {
		return new BigInteger(getClientCuit().replace("-", ""));
	}
	
	/** Punto de venta */
	public int getPtoVta() {
		return Integer.valueOf(getPuntoDeVenta());
	}
	
	/** Tipo de comprobante */ 
	public int getTipoCmp() {
		return Integer.valueOf(getDocTypeCodeFE());
	}

	/** Nro de Comprobante */
	public int getNroCmp() {
		return invoice.getNumeroComprobante();
	}
	
	/** Importe de la factura */
	public Double getImporte() {
		return invoice.getGrandTotal().doubleValue();
	}
	
	/** Moneda de la factura
	  *  De no poder determinar el codigo retorna PES por defecto */
	public String getMoneda(MInvoice invoice) {
		MCurrency currency = new MCurrency(invoice.getCtx(), invoice.getC_Currency_ID(), null);
		if (currency.getWSFECode()==null || currency.getWSFECode().length()==0) {
			String error = "ERROR: Codigo WSFE para la moneda del documento no encontrado";
			getLog().severe(error);
			throw new RuntimeException(error);
		}
		return currency.getWSFECode();
	}
	
	/** Cotizacion segun tasa de cambio 
	  * De no poder realizar la conversion, retorna 1 por defecto */
	public Double getCotizacion(MInvoice inv) {
		try {
			return MCurrency.currencyConvert(	Env.ONE,
											inv.getC_Currency_ID(), 
											Env.getContextAsInt(inv.getCtx(), "$C_Currency_ID"), 
											inv.getDateAcct(), 
											0,
											inv.getCtx()).doubleValue();
		} catch (Exception e) {
			String error = "ERROR: Sin cotizacion de conversion entre la moneda del documento y pesos";
			getLog().severe(error);
			throw new RuntimeException(error);
		}
	}
	
	/** Tipo Doc Receptor */ 
	public int getTipoDocRec(MBPartner bPartner) {
		try {
			return Integer.valueOf(bPartner.getTaxIdType());
		} catch (Exception e) {
			return -1;
		}
	}
	
	/** Nro Doc Receptor */
	public BigInteger getNroDocRec() {
		try {
			return new BigInteger(invoice.getCUIT());
		} catch (Exception e) {
			return null;
		}
	}
	
	/** Tipo de autorizacion */
	public String getTipoCodAut() {
		/* TODO: 	Por el momento LY solo genera mediante CAE (E), con lo cual la única opcion es E
		 * 			En caso de emitir mediante CAEA (A), se deberá des-hardcodear esta entrada */
		return "E"; 
	}
	
	/** Codigo autorizacion */
	public BigInteger getCodAut() {
		try {
			return new BigInteger(invoice.getcae());
		} catch (Exception e) {
			String error = "ERROR: Documento sin CAE o CAE con formato invalido";
			getLog().severe(error);
			throw new RuntimeException(error);
		}
	}
	
	/** URL Base a redireccionar el QR Code 
	 *  Busca la URL del servicio WSFEQRCODE */
	public String getBaseURL() {
		try {
			return extService.getURL();
		} catch (Exception e) {
			String defaultURL = "https://www.afip.gob.ar/fe/qr/?p=";
			getLog().warning("Servicio externo WSFEQRCODE no encontrado. Default a " + defaultURL);
			return defaultURL;
		}
	}


}

