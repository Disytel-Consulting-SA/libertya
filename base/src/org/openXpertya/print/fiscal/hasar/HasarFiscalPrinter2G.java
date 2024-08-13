package org.openXpertya.print.fiscal.hasar;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jcp.xml.dsig.internal.dom.Utils;
import org.openXpertya.print.fiscal.FiscalClosingResponseDTO;
import org.openXpertya.print.fiscal.FiscalInitData;
import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.comm.FiscalComm;
import org.openXpertya.print.fiscal.document.CreditNote;
import org.openXpertya.print.fiscal.document.DiscountLine;
import org.openXpertya.print.fiscal.document.Document;
import org.openXpertya.print.fiscal.document.DocumentLine;
import org.openXpertya.print.fiscal.document.Payment;
import org.openXpertya.print.fiscal.document.Payment.TenderType;
import org.openXpertya.print.fiscal.document.Tax;
import org.openXpertya.print.fiscal.exception.FiscalPrinterIOException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterStatusError;
import org.openXpertya.util.Env;

public class HasarFiscalPrinter2G extends HasarFiscalPrinter {

	/** Responsabilidad frente al IVA: Pequeño contribuyente eventual */
	protected static final String PEQUENO_CONTRIBUYENTE_EVENTUAL = "V";
	/** Responsabilidad frente al IVA: Pequeño contribuyente eventual social */
	protected static final String PEQUENO_CONTRIBUYENTE_EVENTUAL_SOCIAL = "W";
	/** Longitud para campos sin límite de longitud */
	protected static final int lengthNoLimit = 1000;
	/** Key por defecto para percepciones no definidas */
	protected static final String PERCEPTION_DEFAULT_KEY = "PD";
	/** Documento Genérico */
	protected static final String GENERIC_DOCUMENT = "GD";
	
	/** Comando para obtener ConsultarAcumuladosComprobante 
	 * dREHER 
	 */
	protected static final int CMD_CONSULTAR_ACUMULADOS_COMPROBANTE = 0x8C; 
	
	/**
	 * Comando para obtener el primer bloque de auditoria guardado en la memoria fiscal
	 * dREHER
	 */
	protected static final int CMD_CONSULTAR_PRIMER_BLOQUE_AUDITORIA = 166; // 0xA6;
	
	/**
	 * Comando para obtener el siguiente bloque de auditoria guardado en la memoria fiscal
	 * dREHER
	 */
	protected static final int CMD_CONSULTAR_SIGUIENTE_BLOQUE_AUDITORIA = 167; // 0xA7;
	
	/**
	 * Comando para obtener el primer bloque de reporte de auditoria guardado en la memoria fiscal
	 * dREHER
	 */
	protected static final int CMD_CONSULTAR_PRIMER_BLOQUE_REPORTE_AUDITORIA = 118; // 0x76;
	
	/**
	 * Comando para obtener el siguiente bloque de reporte de auditoria guardado en la memoria fiscal
	 * dREHER
	 */
	protected static final int CMD_CONSULTAR_SIGUIENTE_BLOQUE_REPORTE_AUDITORIA = 119; // 0x77;
	
	
	/**
	 * Asociación entre los tipos de pago con los esperados por la impresora
	 * fiscal
	 */
	private Map<TenderType, String> tenderTypes;

	/**
	 * Asociación entre los tipos de percepción con los esperados por la
	 * impresora fiscal
	 */
	private Map<String, String> percepcionTypes;
	
	public HasarFiscalPrinter2G() {
		// TODO Auto-generated constructor stub
	}
	
	public HasarFiscalPrinter2G(FiscalComm fiscalComm) {
		super(fiscalComm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String formatQuantity(BigDecimal quantity) {
		return quantity.toString();
	}

	@Override
	public String formatAmount(BigDecimal amount) {
		amount = amount.setScale(4, BigDecimal.ROUND_HALF_UP);
		return amount.toString();
	}

	@Override
	protected Map<String, String> getDocumentTypesCodes() {
		Map<String, String> dtCodes = new HashMap<String, String>();
		dtCodes.put(Document.DT_INVOICE + Document.DOC_LETTER_A, "81");
		dtCodes.put(Document.DT_INVOICE + Document.DOC_LETTER_B, "82");
		dtCodes.put(Document.DT_INVOICE + Document.DOC_LETTER_C, "111");
		dtCodes.put(Document.DT_CREDIT_NOTE + Document.DOC_LETTER_A, "112");
		dtCodes.put(Document.DT_CREDIT_NOTE + Document.DOC_LETTER_B, "113");
		dtCodes.put(Document.DT_CREDIT_NOTE + Document.DOC_LETTER_C, "114");
		dtCodes.put(Document.DT_DEBIT_NOTE + Document.DOC_LETTER_A, "115");
		dtCodes.put(Document.DT_DEBIT_NOTE + Document.DOC_LETTER_B, "116");
		dtCodes.put(Document.DT_DEBIT_NOTE + Document.DOC_LETTER_C, "117");
		dtCodes.put(GENERIC_DOCUMENT, "910");
		return dtCodes;
	}
	
	protected Map<TenderType, String> getTenderTypesCodes() {
		Map<TenderType, String> tts = new HashMap<Payment.TenderType, String>();
		tts.put(TenderType.CHEQUE, "3");
		tts.put(TenderType.CREDITO, "10");
		tts.put(TenderType.CUENTA_CORRIENTE, "6");
		tts.put(TenderType.EFECTIVO, "8");
		tts.put(TenderType.TARJETA, "20");
		tts.put(TenderType.TRANSFERENCIA_BANCARIA, "23");
		tts.put(TenderType.OTROS, "99");
		return tts;
	}
	
	protected Map<TenderType, String> getTenderTypes() {
		if(tenderTypes == null) {
			tenderTypes = getTenderTypesCodes();
		}
		return tenderTypes;
	}

	protected void setTenderTypes(Map<TenderType, String> tenderTypes) {
		this.tenderTypes = tenderTypes;
	}
	
	protected Map<String, String> getPercepcionTypesCodes(){
		Map<String, String> perceptionTypes = new HashMap<String, String>();
		// Percepción IVA
		perceptionTypes.put("I", "6");
		// Percepción IIBB
		perceptionTypes.put("B", "7");
		// Otras percepciones - Por defecto cuando no es ninguno de los
		// anteriores
		perceptionTypes.put(PERCEPTION_DEFAULT_KEY, "9");
		return perceptionTypes;
	}
	
	protected Map<String, String> getPercepcionTypes() {
		if(percepcionTypes == null){
			percepcionTypes = getPercepcionTypesCodes();
		}
		return percepcionTypes;
	}

	protected void setPercepcionTypes(Map<String, String> percepcionTypes) {
		this.percepcionTypes = percepcionTypes;
	}
	
	@Override
	public FiscalPacket cmdSetCustomerData(String name, String customerDocNumber, String ivaResponsibility, String docType, String location) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_CUSTOMER_DATA);
		int i = 1;
		cmd.setText(i++, name, lengthNoLimit, true);
		cmd.setText(i++, formatDocNumber(docType,customerDocNumber), true);
		cmd.setText(i++, ivaResponsibility, false);
		cmd.setText(i++, docType, true);
		cmd.setText(i++, location, lengthNoLimit, true);
		// Datos Adicionales 1
		cmd.setText(i++, "", true);
		// Datos Adicionales 2
		cmd.setText(i++, "", true);
		// Datos Adicionales 3
		cmd.setText(i++, "", true);
		return cmd;
	}
	
	/**
	 * Permite consultar los totales acumulados en un documento fiscal
	 *  
	 * @param docType
	 * @param nroComprobante
	 * @return paquete de info recibida desde el controlador fiscal
	 * 
	 * dREHER
	 */
	public BigDecimal getTotal(String docType, Integer nroComprobante) {
		BigDecimal total = null;
		
		FiscalPacket cmd = createFiscalPacket(CMD_CONSULTAR_ACUMULADOS_COMPROBANTE);
		int i = 1;
		cmd.setText(i++, docType, false);
		cmd.setInt(i++, nroComprobante);
		
		total = cmd.getBigDecimal(6);
		
		return total;
	}

	@Override
	protected void setEmbarkNumber(Integer line, CreditNote creditNote) throws FiscalPrinterStatusError, FiscalPrinterIOException{
		execute(cmdSetEmbarkNumber(line, creditNote.getOriginalLetter(), creditNote.getOriginalPOS(),
				creditNote.getOriginalNo()));
	}
	
	public FiscalPacket cmdSetEmbarkNumber(int line, String letra, Integer puntodeventa, Integer nroComprobante) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_EMBARK_NUMBER);
		int i = 1;
		cmd.setNumber(i++, line, false);
		// Código del comprobante original		
		cmd.setText(i++, letra != null ? traduceDocumentType(Document.DT_INVOICE, letra) : "0", true);
		// Punto de venta del comprobante original
		cmd.setInt(i++, puntodeventa != null?puntodeventa:0);
		// Número de comprobante del comprobante original
		cmd.setInt(i++, nroComprobante != null?nroComprobante:0);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdOpenFiscalReceipt(String docType) {
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_FISCAL_RECEIPT);
		int i = 1;
		cmd.setInt(i++, Integer.parseInt(docType));
		return cmd;
	}
	
	@Override
	protected FiscalPacket cmdPrintLineItem(String description, BigDecimal quantity, BigDecimal price, BigDecimal ivaPercent, boolean substract, BigDecimal internalTaxes, boolean basePrice, Integer display, int descMaxLength) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_LINE_ITEM);
		int i = 1;
		cmd.setText(i++, description, lengthNoLimit, false);
		cmd.setQuantity(i++, quantity, false);
		cmd.setAmount(i++, price, false, true);
		
		// TODO Se suma 1 por omisión de la condición de iva. Valor por defecto gravado.
		cmd.setString(i++, "7");
		
		if(ivaPercent == null)
			cmd.setNumber(i++, BigDecimal.ZERO, 2, 2, false);
		else
			cmd.setNumber(i++, ivaPercent, 2, 2, false);
		cmd.setBoolean(i++, substract, "m", "M", false);
		// TODO Tipo de Impuesto interno
		cmd.setString(i++, "0");
		// TODO Magnitud del impuesto interno, por lo pronto va 0 si el valor de
		// los impuestos internos es 0
		cmd.setNumber(i++, BigDecimal.ZERO, false);
		/*if(Util.isEmpty(internalTaxes, true)){
			cmd.setNumber(i++, BigDecimal.ZERO, 9, 8, false);
		}
		else{
			i++;
		}*/
		//cmd.setOptionalField(i++, display);
		cmd.setInt(i++, display == null?0:display);
		cmd.setBoolean(i++, basePrice, "B", "T", false);
		// Unidad Referencia
		cmd.setInt(i++, 1);
		// Codigo Producto
		cmd.setString(i++, " ");
		// Codigo Interno
		cmd.setString(i++, " ");
		// Unidad de Medida
		cmd.setString(i++, "7");
		
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdLastItemDiscount(String description, BigDecimal amount, boolean substract, boolean baseAmount, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_LAST_ITEM_DISCOUNT);
		int i = 1;
		cmd.setText(i++, description, lengthNoLimit, false);
		cmd.setAmount(i++, amount, false, true);
		cmd.setNumber(i++, display, true);
		cmd.setBoolean(i++, baseAmount, "x", "T", false);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdReturnRecharge(String description,
			BigDecimal amount, BigDecimal ivaPercent, boolean subtract,
			BigDecimal internalTaxes, boolean baseAmount, Integer display, String operation) {
		return cmdReturnRecharge(description, amount, ivaPercent, subtract,
				internalTaxes, baseAmount, display, operation, lengthNoLimit);
	}
	
	@Override
	protected FiscalPacket cmdReturnRecharge(String description,
			BigDecimal amount, BigDecimal ivaPercent, boolean subtract,
			BigDecimal internalTaxes, boolean baseAmount, Integer display,
			String operation, int descMaxLength) {
		FiscalPacket cmd = createFiscalPacket(CMD_RETURN_RECHARGE);
		// Si el importe es negativo es bonificación de iva, el positivo recargo
		// de iva
		// BONIFICACIÓN DE IVA = B
		// RECARGO DE IVA = R
		operation = amount.compareTo(BigDecimal.ZERO) <= 0?"B":"R";
		int i = 1;
		cmd.setText(i++, description, descMaxLength, false);
		cmd.setNumber(i++, amount.abs(), 9, 2, false);
		// Condición de iva: Valor por defecto gravado.
		cmd.setString(i++, "7");
		cmd.setNumber(i++, ivaPercent, 2, 2, false);
		// TODO Tipo de Impuesto interno
		cmd.setString(i++, "0");
		// TODO Magnitud del impuesto interno, por lo pronto va 0 si el valor de
		// los impuestos internos es 0
		cmd.setNumber(i++, BigDecimal.ZERO, false);
		/*if(Util.isEmpty(internalTaxes, true)){
			cmd.setNumber(i++, BigDecimal.ZERO, 9, 8, false);
		}
		else{
			i++;
		}*/
		//cmd.setOptionalField(i++, display);
		cmd.setInt(i++, display == null?0:display);
		cmd.setBoolean(i++, baseAmount, "B", "T", false);
		// Codigo Producto
		cmd.setString(i++, " ");
		
		cmd.setText(i++, operation, false);
		return cmd;
	}

	@Override
	public FiscalPacket cmdGeneralDiscount(String description, BigDecimal amount, boolean substract, boolean baseAmount, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_GENERAL_DISCOUNT);
		int i = 1;
		cmd.setText(i++, description, lengthNoLimit, false);
		cmd.setNumber(i++, amount.abs(), 9, 2, false);
		cmd.setInt(i++, display == null?0:display);
		cmd.setBoolean(i++, baseAmount, "B", "T", false);
		cmd.setString(i++, "");
		cmd.setText(i++, DISCOUNT_RECHARGE, false);
		return cmd;
	}

	@Override
	public FiscalPacket cmdSubtotal(boolean print, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_SUBTOTAL);
		int i = 1;
		cmd.setBoolean(i++, print, "P", "x", false);
		cmd.setNumber(i++, display, true);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdPerceptions(Tax otherTax) {
		FiscalPacket cmd = createFiscalPacket(CMD_PERCEPTIONS_2G);
		int i = 1;
		// Codigo de percepción
		String percepcionFiscalCode = getPercepcionTypes().get(otherTax.getPercepcionType()) != null?
				getPercepcionTypes().get(otherTax.getPercepcionType()):
				getPercepcionTypes().get(PERCEPTION_DEFAULT_KEY);
		cmd.setText(i++, percepcionFiscalCode, false);
		
		cmd.setText(i++, otherTax.getName(), lengthNoLimit, false);
		cmd.setAmount(i++, otherTax.getBaseAmt(), false, false);
		cmd.setAmount(i++, otherTax.getAmt(), false, false);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdTotalTender(String description, BigDecimal amount, boolean cancel, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_TOTAL_TENDER);
		// TODO Agregar toda la info de este comando, Codigo de Forma de Pago, cuotas, nro de cupon, etc.
		int i = 1;
		cmd.setText(i++, description, lengthNoLimit, false);
		cmd.setNumber(i++, amount, 9, 2, false);
		cmd.setBoolean(i++, cancel, "R", "T");
		cmd.setNumber(i++, display, true);
		cmd.setString(i++, "");
		cmd.setString(i++, "");
		// Cuotas
		cmd.setInt(i++, 0);
		// Nro de cupon
		cmd.setString(i++, "");
		// Referencia
		cmd.setString(i++, "");
		
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdTotalTender(String description, BigDecimal amount, boolean cancel, Integer display, Payment payment) {
		FiscalPacket cmd = createFiscalPacket(CMD_TOTAL_TENDER);
		// TODO Agregar toda la info de este comando, Codigo de Forma de Pago, cuotas, nro de cupon, etc.
		int i = 1;
		cmd.setText(i++, description, lengthNoLimit, false);
		cmd.setNumber(i++, amount, 9, 2, false);
		cmd.setBoolean(i++, cancel, "R", "T");
		cmd.setNumber(i++, display, true);
		cmd.setString(i++, "");
		cmd.setString(i++, getTenderTypes().get(payment.getTenderType()));
		// Cuotas
		cmd.setInt(i++, 0);
		// Nro de cupon
		cmd.setString(i++, "");
		// Referencia
		cmd.setString(i++, "");
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdOpenDrawer(){
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_DRAWER);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdSetHeaderTrailer(int line, String text) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_HEADER_TRAILER_2G);
		int i = 1;
		// La numeración de la cola del comprobante comienza en 1, mientras que
		// las hasar 1G comienzan en 11. Nos quedamos con el resto de la
		// división por 10 para determinar qué línea de la zona es. Podemos
		// tener 2 zonas de cola de impresión con 4 líneas cada una
		BigDecimal line2G = (new BigDecimal(line)).remainder(BigDecimal.TEN);
		line = line2G.intValue();
		String zona = line <= 4?"T":"t";
		// Número de Línea
		cmd.setInt(i++, line);
		// Atributos
		cmd.setString(i++, "");
		// Descripción
		cmd.setText(i++, text, 120, false);
		// Estación
		cmd.setString(i++, "D");
		// Zona
		cmd.setString(i++, zona);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdCloseFiscalReceipt(Integer copies) {
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_FISCAL_RECEIPT);
		int i = 1;
		cmd.setNumber(i++, copies == null?0:copies, true);
		// Dirección de Correo Electrónico
		cmd.setString(i++, "");
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdOpenDNFH(String docType, String identification) {
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_FISCAL_RECEIPT);
		int i = 1;
		cmd.setInt(i++, Integer.parseInt(docType));
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdCloseDNFH(Integer copies) {
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_FISCAL_RECEIPT);
		int i = 1;
		cmd.setNumber(i++, copies == null?0:copies, true);
		// Dirección de Correo Electrónico
		cmd.setString(i++, "");
		return cmd;
	}
	
	@Override
	protected void checkTaxes(Document document) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		// La información de IVA se realiza automáticamente con el comando de
		// cierre de comprobante
	}
	
	@Override
	public FiscalPacket cmdOpenNonFiscalReceipt() {
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_FISCAL_RECEIPT);
		int i = 1;
		cmd.setInt(i++, Integer.parseInt(getDocumentTypes().get(GENERIC_DOCUMENT)));
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdPrintNonFiscalText(String text, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_NON_FISCAL_TEXT);
		int i = 1;
		// Atributos del texto
		cmd.setText(i++, "", false);
		cmd.setText(i++, text, lengthNoLimit, false);
		cmd.setInt(i++, display == null?0:display);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdCloseNonFiscalReceipt(Integer copies) {
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_FISCAL_RECEIPT);
		int i = 1;
		cmd.setNumber(i++, copies == null?0:copies, true);
		// Dirección de Correo Electrónico
		cmd.setString(i++, "");
		return cmd;
	}

	@Override
	public FiscalClosingResponseDTO decodeClosingResponse(FiscalPacket closingResponse) {
		String closingType = closingResponse.getString(3);
		Timestamp responseDate = null;
		try{
			responseDate = new Timestamp((new SimpleDateFormat("yyMMdd").parse(closingResponse.getString(5))).getTime());
		} catch(ParseException pe){
			pe.printStackTrace();
			responseDate = Env.getDate();
		}
		FiscalClosingResponseDTO dto = new FiscalClosingResponseDTO();
		dto.closingDate = responseDate;
		if(closingType.equals("X")){
			dto.creditnoteamt = closingResponse.getBigDecimal(13);
			dto.creditnoteperceptionamt = closingResponse.getBigDecimal(15);
			dto.creditnotetaxamt = closingResponse.getBigDecimal(14);
			dto.fiscalclosingno = closingResponse.getInt(4);
			dto.fiscaldocumentamt = closingResponse.getBigDecimal(9);
			dto.fiscaldocumentperceptionamt = closingResponse.getBigDecimal(11);
			dto.fiscaldocumenttaxamt = closingResponse.getBigDecimal(10);
			dto.qtycreditnote = closingResponse.getInt(16);
			dto.qtyfiscaldocument = closingResponse.getInt(12);
			dto.qtynofiscalhomologated = closingResponse.getInt(17);
		}
		else{
			dto.creditnoteamt = closingResponse.getBigDecimal(14);
			dto.creditnotegravadoamt = closingResponse.getBigDecimal(15);
			dto.creditnotenogravadoamt = closingResponse.getBigDecimal(16);
			dto.creditnoteexemptamt = closingResponse.getBigDecimal(17);
			dto.creditnoteperceptionamt = closingResponse.getBigDecimal(19);
			dto.creditnotetaxamt = closingResponse.getBigDecimal(18);
			dto.fiscalclosingno = closingResponse.getInt(4);
			dto.fiscaldocumentamt = closingResponse.getBigDecimal(6);
			dto.fiscaldocumentgravadoamt = closingResponse.getBigDecimal(7);
			dto.fiscaldocumentnogravadoamt = closingResponse.getBigDecimal(8);
			dto.fiscaldocumentexemptamt = closingResponse.getBigDecimal(9);
			dto.fiscaldocumentperceptionamt = closingResponse.getBigDecimal(11);
			dto.fiscaldocumenttaxamt = closingResponse.getBigDecimal(10);
			dto.nofiscalhomologatedamt = closingResponse.getBigDecimal(22);
			dto.qtycanceledcreditnote = closingResponse.getInt(21);
			dto.qtycanceledfiscaldocument = closingResponse.getInt(13);
			dto.qtycreditnote = closingResponse.getInt(20);
			dto.qtyfiscaldocument = closingResponse.getInt(12);
			dto.qtynofiscalhomologated = closingResponse.getInt(23);
		}
		return dto;
	}
	
	@Override
	public FiscalInitData decodeInitData(FiscalPacket getInitDataResponse){
		FiscalInitData fid = new FiscalInitData();
		fid.cuit = getInitDataResponse.getString(3);
		fid.name = getInitDataResponse.getString(4);
		fid.registerNo = getInitDataResponse.getString(5);
		fid.posNo = getInitDataResponse.getInt(6);
		fid.initDate = getInitDataResponse.getDate(7);
		fid.iibb = getInitDataResponse.getString(8);
		fid.categoriaIVA = getInitDataResponse.getString(9);
		fid.activityDate = getInitDataResponse.getDate(10);
		return fid;
	}
	
	@Override
	public int getFooterTrailerMaxLength() {
		return 120;
	}
	
	@Override
	protected boolean checkStatus(FiscalPacket command, FiscalPacket response) throws FiscalPrinterIOException {
		// TODO: HARDCODE en varios comandos viene el estado de fiscal "" en lugar de "0000" pero en el spooler está correcto, hack para ignorar
		
		// dREHER, puede darse que el response venga vacio y eso genera una excepcion!
		if(response.getSize() <= 1)
			return false;
			
		if (response.get(1).length != 0)
			return super.checkStatus(command, response);
		else 
			return false;
	}
	
	@Override
	public String getLastDocumentNoPrinted(String documentType, String letra)
			throws FiscalPrinterStatusError, FiscalPrinterIOException {
		String lastNro = "";
		
		//////////////////////////////////////////////////////////////
		// Incia la transmisión de información de Estado de impresora
		// Comando: @StatusRequest
		// FiscalPacket response = execute(cmdStatusRequest());
		int codigoComprobante = -1;
		if(documentType.equals(Document.DT_INVOICE)) {
			if(letra.equals(Document.DOC_LETTER_A))
				codigoComprobante = 1;
			else
				codigoComprobante = 6;
		}
		if(documentType.equals(Document.DT_CREDIT_NOTE)) {
				if(letra.equals(Document.DOC_LETTER_A))
					codigoComprobante = 3;
				else
					codigoComprobante = 8;
		}	
		if(documentType.equals(Document.DT_DEBIT_NOTE)) {
			if(letra.equals(Document.DOC_LETTER_A))
				codigoComprobante = 2;
			else
				codigoComprobante = 7;
		}
		
		FiscalPacket response = execute(cmdStatusRequestDocument(codigoComprobante));
		
		int index = 7;
		
		// Hasar 2da generacion siempre devuelve ultimo numero impreso en la misma posicion
		/*
		if(documentType.equals(Document.DT_CREDIT_NOTE)){
			index = letra.equals(Document.DOC_LETTER_A)?8:7;
		}
		else{
			index = letra.equals(Document.DOC_LETTER_A)?5:3;
		}
		 */
		
		
		System.out.println("DocumentType=" + documentType + " Letra=" + letra);
		
		lastNro = response.getString(index);
		
		setLastDocumentNo(lastNro);
		
		return lastNro;
	}
	
	/**
	 * Ejecuta los comandos necesarios para cargar las líneas de item
	 * del documento en la impresora fiscal.
	 */
	@Override
	protected void loadDocumentLineItems(Document document) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		// Se cargan los ítems del documento.
		// Comando: @PrintLineItem
		for (DocumentLine item : document.getLines()) {
	        
        	// dREHER sumarle la bonificacion de linea, para que luego lo descuente correctamente
			// caso contrario el monto del item YA incluye el descuento y se vuelve a descontar 
			// por segunda vez
        	BigDecimal monto = item.getAbsUnitPrice();
            DiscountLine discount = null;
            
            if (item.hasDiscount()) {
				discount = item.getDiscount();
                // monto = monto.add(discount.getAbsAmount());
				// dREHER 2024-01-11 NO pasa en todos los casos, por lo tanto NO se puede generalizar este FIX
            }
            
            debug("Se trata de un item con descuento? " + item.hasDiscount());
            debug("Precio registrado (Precio Lista): " + item.getAbsUnitPrice());
            debug("Descuento: " + item.getDiscount());
            debug("Monto considerando descuento: " + monto);
            
        
			execute(cmdPrintLineItem(
				item.getDescription(), 
				item.getQuantity(), 
				monto, 
				item.getIvaRate(), 
				item.isSubstract(), 
				BigDecimal.ZERO, // Impuestos internos
				!item.isPriceIncludeIva(), 
				null)
			);
			
			// Se carga el descuento del ítem si es que posee.
			// Comando: @LastItemDiscount
			if (discount!=null) {
				execute(cmdLastItemDiscount(
					discount.getDescription(), 
					discount.getAbsAmount(), 
					false, 
					!discount.isAmountIncludeIva(),
					null));		
			}
		}
	}

	// dREHER
	private void debug(String string) {
		System.out.println("HasarFiscalPrint2G." + string);
	}

	// BLOQUE DE REPORTE DE AUDITORIA
	// ES INFORMACION PARA CONOCER LOS COMPROBANTES EMITIDOS
	// SE PRESENTA COMO DDJJ AFIP
	
	/**
	 * Comando para consultar el inicio de bloque de reporte de auditoria
	 * @param fechaDesde
	 * @param fechaHasta
	 * @return
	 * 
	 * dREHER
	 */
	private FiscalPacket cmdConsultarInicioBloqueReporteAuditoria(String fechaDesde, String fechaHasta) {
		return cmdConsultarInicioBloqueReporteAuditoria(fechaDesde, fechaHasta, false);
	}
	
	/**
	 * Comando para consultar el inicio de bloque de auditoria
	 * @param fechaDesde
	 * @param fechaHasta
	 * @return
	 * 
	 * dREHER
	 */
	private FiscalPacket cmdConsultarInicioBloqueReporteAuditoria(String fechaDesde, String fechaHasta, boolean completo) {
		FiscalPacket cmd = createFiscalPacket(CMD_CONSULTAR_PRIMER_BLOQUE_REPORTE_AUDITORIA);
		int i = 1;
		
		// parametros necesarios
		cmd.setText(i++, fechaDesde, false); // AAMMDD
		cmd.setText(i++, fechaHasta, false); // AAMMDD
		cmd.setText(i++, (completo?"P":"N"), false); // N=solo memoria fiscal, P=Completo
		
		return cmd;
	}
	
	/**
	 * Comando para consultar el siguiente de bloque de reporte de auditoria
	 * @return
	 * 
	 * dREHER
	 */
	private FiscalPacket cmdConsultarSiguienteBloqueReporteAuditoria() {
		FiscalPacket cmd = createFiscalPacket(CMD_CONSULTAR_SIGUIENTE_BLOQUE_REPORTE_AUDITORIA);
		return cmd;
	}
	
	/**
	 * Consultar primer bloque de reporte de auditoria
	 * 
	 * Se abre el bloque y se continua pidiendo siguiente bloque hasta que no haya mas resultado
	 * 
	 * @return
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 * dREHER
	 */
	public StringBuffer consultarInicioBloqueReporteAuditoria(String fechaDesde, String fechaHasta) throws FiscalPrinterStatusError, FiscalPrinterIOException{
		StringBuffer xml = new StringBuffer();
		
		FiscalPacket response = execute(cmdConsultarInicioBloqueReporteAuditoria(fechaDesde, fechaHasta));
		System.out.println("response cmdConsultarInicioBloqueReporteAuditoria= " + response);
		
		if(response!=null)
			xml.append(response.getString(4));
		return xml;
	}

	/**
	 * Consultar primer bloque de reporte de auditoria
	 * 
	 * Se abre el bloque y se continua pidiendo siguiente bloque hasta que no haya mas resultado
	 * 
	 * @return
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 * dREHER
	 */
	public StringBuffer consultarInicioBloqueReporteAuditoria(String fechaDesde, String fechaHasta, boolean completo) throws FiscalPrinterStatusError, FiscalPrinterIOException{
		StringBuffer xml = new StringBuffer();
		
		FiscalPacket response = execute(cmdConsultarInicioBloqueReporteAuditoria(fechaDesde, fechaHasta, completo));
		System.out.println("response cmdConsultarInicioBloqueReporteAuditoria= " + response);
		
		if(response!=null)
			xml.append(response.getString(4));
		return xml;
	}
	
	/**
	 * Consultar siguiente bloque de reporte de auditoria
	 * 
	 * Se abre el bloque y se continua pidiendo siguiente bloque hasta que no haya mas resultado
	 * 
	 * @return
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 * dREHER
	 */
	public ArrayList<StringBuffer> consultarSiguienteBloqueReporteAuditoria() throws FiscalPrinterStatusError, FiscalPrinterIOException{
		StringBuffer xml = new StringBuffer();
		String bloque = "";
		FiscalPacket response = execute(cmdConsultarSiguienteBloqueReporteAuditoria());
		System.out.println("response ConsultarSiguienteBloqueReporteAuditoria= " + response);
		
		if(response!=null && response.getSize() >= 4) {
			bloque = response.getString(3);
			if(bloque.equals("1") || bloque.equals("0")) { // sigue habiendo informacion o es el ultimo bloque
				System.out.println("Bloque: " + bloque + " Respuesta len: " + response.getSize());
				if(response.getSize()>=4)
					xml.append(response.getString(4));
			}
		}
		ArrayList<StringBuffer> respuesta = new ArrayList<StringBuffer>();
		respuesta.add(new StringBuffer(bloque));
		respuesta.add(xml);
		return respuesta;
	}
	
	/**
	 * Consultar primer bloque de reporte de auditoria
	 * 
	 * Se abre el bloque y se continua pidiendo siguiente bloque hasta que no haya mas resultado
	 * 
	 * @return
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 * dREHER
	 */
	public StringBuilder fiscalReportAudit(String fechaDesde, String fechaHasta) throws FiscalPrinterStatusError, FiscalPrinterIOException{
		StringBuilder xml = new StringBuilder();
		
		StringBuffer bloque = consultarInicioBloqueReporteAuditoria(fechaDesde, fechaHasta);
		if(bloque!=null) {
			xml.append(bloque);
			while(true) {
				ArrayList<StringBuffer>siguiente = consultarSiguienteBloqueReporteAuditoria();
				if(siguiente.get(1)!=null && siguiente.get(1).length() > 0)
					xml.append(siguiente.get(1));
				else
					break;
				
				if(siguiente.get(0).toString().equals("0"))
					break;
			}
		}
		return xml;
	}
	
// ---------------------------------------------------------------------------------------------------------------
	
	// BLOQUE DE AUDITORIA
	// ES INFORMACION PARA CONOCER LOS COMPROBANTES EMITIDOS
	// NO SE PRESENTA COMO DDJJ AFIP
	
	/**
	 * Comando para consultar el inicio de bloque de auditoria
	 * @param fechaDesde
	 * @param fechaHasta
	 * @return
	 * 
	 * dREHER
	 */
	private FiscalPacket cmdConsultarInicioBloqueAuditoria(String fechaDesde, String fechaHasta) {
		return cmdConsultarInicioBloqueAuditoria(fechaDesde, fechaHasta, false);
	}
	
	/**
	 * Comando para consultar el inicio de bloque de auditoria
	 * @param fechaDesde
	 * @param fechaHasta
	 * @return
	 * 
	 * dREHER
	 */
	private FiscalPacket cmdConsultarInicioBloqueAuditoria(String fechaDesde, String fechaHasta, boolean comprimir) {
		FiscalPacket cmd = createFiscalPacket(CMD_CONSULTAR_PRIMER_BLOQUE_AUDITORIA);
		int i = 1;
		
		// parametros necesarios
		cmd.setText(i++, fechaDesde, false); // AAMMDD
		cmd.setText(i++, fechaHasta, false); // AAMMDD
		cmd.setText(i++, "F", false); // por rango de fechas del cierre Z (Z=Numero de cierre, utilizamos la opcion de rangos de fechas, mas facil)
		cmd.setText(i++, (comprimir?"P":"N"), false); // N=no comprime, P=Comprime info
		cmd.setText(i++, "P", false); // N=uno por cada jornada fiscal, P=Un solo archivo para todas las jornadas incluidas en el rango de fechas
		
		return cmd;
	}
	
	/**
	 * Comando para consultar el siguiente de bloque de auditoria
	 * @return
	 * 
	 * dREHER
	 */
	private FiscalPacket cmdConsultarSiguienteBloqueAuditoria() {
		FiscalPacket cmd = createFiscalPacket(CMD_CONSULTAR_SIGUIENTE_BLOQUE_AUDITORIA);
		return cmd;
	}
	
	/**
	 * Consultar primer bloque de auditoria
	 * 
	 * Se abre el bloque y se continua pidiendo siguiente bloque hasta que no haya mas resultado
	 * 
	 * @return
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 * dREHER
	 */
	public StringBuffer consultarInicioBloqueAuditoria(String fechaDesde, String fechaHasta) throws FiscalPrinterStatusError, FiscalPrinterIOException{
		StringBuffer xml = new StringBuffer();
		
		FiscalPacket response = execute(cmdConsultarInicioBloqueAuditoria(fechaDesde, fechaHasta));
		System.out.println("response cmdConsultarInicioBloqueAuditoria= " + response);
		
		if(response!=null && response.getSize() >= 4)
			xml.append(response.getString(4));
		return xml;
	}

	/**
	 * Consultar primer bloque de auditoria
	 * 
	 * Se abre el bloque y se continua pidiendo siguiente bloque hasta que no haya mas resultado
	 * 
	 * @return
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 * dREHER
	 */
	public StringBuffer consultarInicioBloqueAuditoria(String fechaDesde, String fechaHasta, boolean comprimido) throws FiscalPrinterStatusError, FiscalPrinterIOException{
		StringBuffer xml = new StringBuffer();
		
		FiscalPacket response = execute(cmdConsultarInicioBloqueAuditoria(fechaDesde, fechaHasta, comprimido));
		System.out.println("response cmdConsultarInicioBloqueAuditoria= " + response);
		
		if(response!=null)
			xml.append(response.getString(4));
		return xml;
	}
	
	/**
	 * Consultar siguiente bloque de auditoria
	 * 
	 * Se abre el bloque y se continua pidiendo siguiente bloque hasta que no haya mas resultado
	 * 
	 * @return
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 * dREHER
	 */
	public ArrayList<StringBuffer> consultarSiguienteBloqueAuditoria() throws FiscalPrinterStatusError, FiscalPrinterIOException{
		StringBuffer xml = new StringBuffer();
		String bloque = "";
		FiscalPacket response = execute(cmdConsultarSiguienteBloqueAuditoria());
		System.out.println("response ConsultarSiguienteBloqueAuditoria= " + response);
		
		if(response!=null && response.getSize() >= 4) {
			bloque = response.getString(3);
			if(bloque.equals("1") || bloque.equals("0")) { // sigue habiendo informacion o es el ultimo bloque
				System.out.println("Bloque: " + bloque + " Respuesta len: " + response.getSize());
				if(response.getSize()>=4)
					xml.append(response.getString(4));
			}
		}
		ArrayList<StringBuffer> respuesta = new ArrayList<StringBuffer>();
		respuesta.add(new StringBuffer(bloque));
		respuesta.add(xml);
		return respuesta;
	}
	
	/**
	 * Consultar primer bloque de auditoria
	 * 
	 * Se abre el bloque y se continua pidiendo siguiente bloque hasta que no haya mas resultado
	 * 
	 * @return
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 * dREHER
	 */
	public StringBuilder fiscalAudit(String fechaDesde, String fechaHasta) throws FiscalPrinterStatusError, FiscalPrinterIOException{
		StringBuilder xml = new StringBuilder();
		
		StringBuffer bloque = consultarInicioBloqueAuditoria(fechaDesde, fechaHasta);
		if(bloque!=null) {
			xml.append(bloque);
			while(true) {
				ArrayList<StringBuffer>siguiente = consultarSiguienteBloqueAuditoria();
				if(siguiente.get(1)!=null && siguiente.get(1).length() > 0)
					xml.append(siguiente.get(1));
				else
					break;
				
				if(siguiente.get(0).toString().equals("0"))
					break;
			}
		}
		return xml;
	}
	
	/**
	 * Consultar primer bloque de auditoria
	 * 
	 * Se abre el bloque y se continua pidiendo siguiente bloque hasta que no haya mas resultado
	 * La informacion se recibe en ASCII85, debe convertirse a binario y luego eso guardarlo en un archivo con extension .ZIP
	 * @return
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 * dREHER
	 */
	public StringBuilder fiscalAuditComprimido(String fechaDesde, String fechaHasta) throws FiscalPrinterStatusError, FiscalPrinterIOException{
		StringBuilder xml = new StringBuilder();
		
		StringBuffer bloque = consultarInicioBloqueAuditoria(fechaDesde, fechaHasta, true);
		if(bloque!=null) {
			xml.append(bloque);
			while(true) {
				ArrayList<StringBuffer>siguiente = consultarSiguienteBloqueAuditoria();
				if(siguiente.get(1)!=null && siguiente.get(1).length() > 0)
					xml.append(siguiente.get(1));
				else
					break;
				
				if(siguiente.get(0).toString().equals("0"))
					break;
			}
		}
		
		return xml;
	}
	
}
