package org.openXpertya.print.fiscal.hasar;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.comm.FiscalComm;
import org.openXpertya.print.fiscal.document.CreditNote;
import org.openXpertya.print.fiscal.document.Document;
import org.openXpertya.print.fiscal.document.Payment;
import org.openXpertya.print.fiscal.document.Tax;
import org.openXpertya.print.fiscal.document.Payment.TenderType;
import org.openXpertya.print.fiscal.exception.FiscalPrinterIOException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterStatusError;

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
		cmd.setNumber(i++, BigDecimal.ZERO, 9, 8, false);
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
		cmd.setNumber(i++, BigDecimal.ZERO, 9, 8, false);
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
}
