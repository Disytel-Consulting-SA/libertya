package org.openXpertya.print.fiscal.hasar;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.print.fiscal.BasicFiscalPrinter;
import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.comm.FiscalComm;
import org.openXpertya.print.fiscal.document.CreditNote;
import org.openXpertya.print.fiscal.document.Customer;
import org.openXpertya.print.fiscal.document.DebitNote;
import org.openXpertya.print.fiscal.document.DiscountLine;
import org.openXpertya.print.fiscal.document.Document;
import org.openXpertya.print.fiscal.document.DocumentLine;
import org.openXpertya.print.fiscal.document.Invoice;
import org.openXpertya.print.fiscal.document.NonFiscalDocument;
import org.openXpertya.print.fiscal.document.Payment;
import org.openXpertya.print.fiscal.document.Tax;
import org.openXpertya.print.fiscal.exception.DocumentException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterIOException;
import org.openXpertya.print.fiscal.exception.FiscalPrinterStatusError;
import org.openXpertya.print.fiscal.msg.FiscalMessage;
import org.openXpertya.print.fiscal.msg.FiscalMessages;
import org.openXpertya.print.fiscal.msg.MsgRepository;

/**
 * Impresora Fiscal Hasar. Funcionalidad común a todos los modelos de Hasar. 
 * Implementa la interfaz <code>HasarCommands</code>. Cualquier modelo nuevo
 * de Hasar a implementar deberá ser una especialización de esta clase,
 * permitiendo sobreescribir algunos o todos los comandos implementados
 * por defecto en esta clase.
 * @author Franco Bonafine
 * @date 24/01/2008
 */
public abstract class HasarFiscalPrinter extends BasicFiscalPrinter implements HasarCommands, HasarConstants {

    // Tipo de comprobante fiscal
	public static final int FACTURA = 1;
	public static final int RECIBO = 2;
	public static final int NOTA_DEBITO = 3;

	// Responsabilidad frente al IVA
	/** Responsabilidad frente al IVA: Responsable inscripto */
	protected static final String RESPONSABLE_INSCRIPTO = "I";
	/** Responsabilidad frente al IVA: Responsable no inscripto */
	protected static final String RESPONSABLE_NO_INSCRIPTO = "N";
	/** Responsabilidad frente al IVA: Exento */
	protected static final String EXENTO = "E";
	/** Responsabilidad frente al IVA: No responsable */
	protected static final String NO_RESPONSABLE = "A";
	/** Responsabilidad frente al IVA: Consumidor final */
	protected static final String CONSUMIDOR_FINAL = "C";
	/** Responsabilidad frente al IVA: Responsable no inscripto, venta de bienes de uso */
	protected static final String RESPONSABLE_NO_INSCRIPTO_BIENES_DE_USO = "B";
	/** Responsabilidad frente al IVA: Responsable monotributo */
	protected static final String RESPONSABLE_MONOTRIBUTO = "M";
	/** Responsabilidad frente al IVA: Monotributista social */
	protected static final String MONOTRIBUTISTA_SOCIAL = "S";
	/** Responsabilidad frente al IVA: Pequeño contribuyente eventual */
	protected static final String PEQUENO_CONTRIBUYENTE_EVENTUAL = "S";
	/** Responsabilidad frente al IVA: Pequeño contribuyente eventual social */
	protected static final String PEQUENO_CONTRIBUYENTE_EVENTUAL_SOCIAL = "S";
	/** Responsabilidad frente al IVA: No categorizado */
	protected static final String NO_CATEGORIZADO = "T";

	// Tipo de documento  
	/** C.U.I.T. */
	protected static final String CUIT = "C";
	/** C.U.I.L. */
	protected static final String CUIL = "L";
	/** Libreta de enrolamiento */
	protected static final String LIBRETA_DE_ENROLAMIENTO = "0";
	/** Libreta cívica */
	protected static final String LIBRETA_CIVICA = "1";
	/** Documento nacional de identidad */
	protected static final String DNI = "2";
	/** Pasaporte */
	protected static final String PASAPORTE = "3";
	/** Cédula de identidad */
	protected static final String CEDULA = "4";
	/** Sin calificador */
	protected static final String SIN_CALIFICADOR = " ";

	// Formato de código de barras
	/** Código de barras EAN 13 */
	protected static final Integer EAN_13 = 1;
	/** Código de barras EAN 8 */
	protected static final Integer EAN_8 = 2;
	/** Código de barras UPCA */
	protected static final Integer UPCA  = 3;
	/** Código de barras ITS 2 de 5 */
	protected static final Integer ITS = 4;
	
	// Opciones de operación del comando ReturnRecharge
	/** ReturnRecharge: Operación Devolución de Envases */
	protected static final String CONTAINER_RETURN = "e";
	/** ReturnRecharge: Operación Descuento / Recargo */
	protected static final String DISCOUNT_RECHARGE = "B";
	
	/** Conjunto de caracteres para realizar la conversión a string de los paquetes fiscales */
	private String encoding = "ISO8859_1";	// ISO 8859-1, Latin alphabet No. 1.
	/** Año base para validación de fechas */
	private int baseRolloverYear = 1997;
	/** Estado actual de la impresora */
	private int printerStatus;
	/** Estado actual del controlador fiscal */
	private int fiscalStatus;
	/** Posibles mensajes de estado de la impresora */
	private Map<Integer,FiscalMessage> printerStatusMsgs;
	/** Posibles mensajes de estado del controlador fiscal */
	private Map<Integer,FiscalMessage> fiscalStatusMsgs;
	/** Códigos de mensajes de estado de la impresora */
	private int[] printerStatusCodes = { PST_PRINTER_BUSY, PST_PRINTER_ERROR, PST_PRINTER_OFFLINE,
										 PST_JOURNAL_PAPER_OUT, PST_TICKET_PAPER_OUT, PST_PRINT_BUFFER_FULL, 
										 PST_PRINT_BUFFER_EMPTY, PST_PRINTER_COVER_OPEN, PST_MONEY_DRAWER_CLOSED
										};
	/** Códigos de mensajes de estado del controlador fiscal */
	private int[] fiscalStatusCodes  = { FST_FISCAL_MEMORY_CRC_ERROR, FST_WORKING_MEMORY_CRC_ERROR, FST_UNKNOWN_COMMAND,    
										 FST_INVALID_DATA_FIELD, FST_INVALID_COMMAND, FST_ACCUMULATOR_OVERFLOW,
										 FST_FISCAL_MEMORY_FULL, FST_FISCAL_MEMORY_ALMOST_FULL, FST_DEVICE_CERTIFIED,
										 FST_DEVICE_FISCALIZED, FST_DATE_ERROR, FST_FISCAL_DOCUMENT_OPEN,
										 FST_DOCUMENT_OPEN, FST_STATPRN_ACTIVE
										};

	/** Mapeo entre categorias de IVA de las clases de documentos y los valores
	 * esperados por las impresoras fiscales de esta marca. */
	private Map<Integer, String> ivaResponsabilities;
	/** Mapeo entre los tipos de identificación de clientes de las clases
	 * de documentos y los valores esperados por las impresoras de esta marca. */
	private Map<Integer, String> identificationTypes;
	/** Mapeo entre los tipos de documentos de las clases de documentos y 
	 * los valores esperados por las impresoras de esta marca. */
	private Map<String, String> documentTypes;
	

	public HasarFiscalPrinter() {
		super();
	}
	
	/**
	 * @param fiscalComm
	 */
	public HasarFiscalPrinter(FiscalComm fiscalComm) {
		super(fiscalComm);
	}

	public FiscalPacket cmdBarCode(Integer codeType, String data, boolean printNumbers) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_BAR_CODE);
		int i = 1;
		cmd.setNumber(i++, codeType, false);
		cmd.setText(i++, data, false);
		cmd.setBoolean(i++, printNumbers, "N", "x", false);
		cmd.setText(i++, "x", false);
		return cmd;
	}

	public FiscalPacket cmdCancelDocument() {
		FiscalPacket cmd = createFiscalPacket(CMD_CANCEL_DOCUMENT);
		return cmd;
	}

	public FiscalPacket cmdChangeIVAResponsibility(String ivaResponsability) {
		FiscalPacket cmd = createFiscalPacket(CMD_CHANGE_IVA_RESPONSIBILITY);
		int i = 1;
		cmd.setText(i++, ivaResponsability, false);
		return cmd;
	}

	public FiscalPacket cmdCloseDNFH(Integer copies) {
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_DNFH);
		int i = 1;
		cmd.setNumber(i++, copies, true);
		return cmd;
	}

	public FiscalPacket cmdCloseFiscalReceipt(Integer copies) {
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_FISCAL_RECEIPT);
		int i = 1;
		cmd.setNumber(i++, copies, true);
		return cmd;
	}

	public FiscalPacket cmdCloseNonFiscalReceipt(Integer copies) {
		FiscalPacket cmd = createFiscalPacket(CMD_CLOSE_NON_FISCAL_RECEIPT);
		int i = 1;
		cmd.setNumber(i++, copies, true);
		return cmd;
	}

	public FiscalPacket cmdDailyClose(String docType) {
		FiscalPacket cmd = createFiscalPacket(CMD_DAILY_CLOSE);
		int i = 1;
		cmd.setText(i++, docType, false);
		return cmd;
	}

	public FiscalPacket cmdDoubleWidth() {
		FiscalPacket cmd = createFiscalPacket(CMD_DOUBLE_WIDTH);
		return cmd;
	}

	public FiscalPacket cmdSetGeneralConfiguration(boolean printConfigReport, boolean loadDefaultData, BigDecimal finalConsumerLimit, BigDecimal ticketInvoiceLimit, BigDecimal ivaNonInscript, Integer copies, Boolean printChange, Boolean printLabels, String ticketCutType, Boolean printFramework, Boolean reprintDocuments, String balanceText, Boolean paperSound, String paperSize) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_GENERAL_CONFIGURATION);
		int i = 1;
		cmd.setBoolean(i++, printConfigReport, "P", "x", false);
		cmd.setBoolean(i++, loadDefaultData, "P", "x", false);
		cmd.setNumber(i++, finalConsumerLimit, 9, 2, true);
		cmd.setNumber(i++, ticketInvoiceLimit, 9, 2, true);
		cmd.setNumber(i++, ivaNonInscript, 2, 2, true);
		cmd.setNumber(i++, copies, true);
		cmd.setBoolean(i++, printChange, "P", "x", true);
		cmd.setBoolean(i++, printLabels, "P", "x", true);
		cmd.setText(i++, ticketCutType, true);
		cmd.setBoolean(i++, printFramework, "P", "x", true);
		cmd.setBoolean(i++, reprintDocuments, "P", "x", true);
		cmd.setText(i++, balanceText, 80, true);
		cmd.setBoolean(i++, paperSound, "P", "x", true);
		cmd.setText(i++, paperSize, true);
		return cmd;
	}

	public FiscalPacket cmdGeneralDiscount(String description, BigDecimal amount, boolean substract, boolean baseAmount, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_GENERAL_DISCOUNT);
		int i = 1;
		cmd.setText(i++, description, 50, false);
		cmd.setNumber(i++, amount, 9, 2, false);
		cmd.setBoolean(i++, substract, "m", "M", false);
		cmd.setNumber(i++, display, true);
		cmd.setBoolean(i++, baseAmount, "x", "T", false);
		return cmd;
	}

	public FiscalPacket cmdGetGeneralConfigurationData() {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_GENERAL_CONFIGURATION);
		return cmd;
	}

	public FiscalPacket cmdGetDateTime() {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_DATE_TIME);
		return cmd;
	}

	public FiscalPacket cmdGetEmbarkNumber(int line) {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_EMBARK_NUMBER);
		int i = 1;
		cmd.setNumber(i++, line, false);
		return cmd;
	}

	public FiscalPacket cmdGetFantasyName(int line) {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_FANTASY_NAME);
		int i = 1;
		cmd.setNumber(i++, line, false);
		return cmd;
	}

	public FiscalPacket cmdGetHeaderTrailer(int line) {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_HEADER_TRAILER);
		int i = 1;
		cmd.setNumber(i++, line, false);
		return cmd;
	}

	public FiscalPacket cmdGetWorkingMemory() {
		FiscalPacket cmd = createFiscalPacket(CMD_GET_WORKING_MEMORY);
		return cmd;
	}

	public FiscalPacket cmdLastItemDiscount(String description, BigDecimal amount, boolean substract, boolean baseAmount, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_LAST_ITEM_DISCOUNT);
		int i = 1;
		cmd.setText(i++, description, 50, false);
		cmd.setAmount(i++, amount, false, true);
		cmd.setBoolean(i++, substract, "m", "M", false);
		cmd.setNumber(i++, display, true);
		cmd.setBoolean(i++, baseAmount, "x", "T", false);
		return cmd;
	}

	public FiscalPacket cmdOpenDNFH(String docType, String identification) {
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_DNFH);
		int i = 1;
		cmd.setText(i++, docType, false);
		cmd.setText(i++, "T", true);
		cmd.setText(i++, identification, true);
		return cmd;
	}

	public FiscalPacket cmdOpenFiscalReceipt(String docType) {
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_FISCAL_RECEIPT);
		int i = 1;
		cmd.setText(i++, docType, false);
		cmd.setText(i++, "T", true);
		return cmd;
	}

	public FiscalPacket cmdOpenNonFiscalReceipt() {
		FiscalPacket cmd = createFiscalPacket(CMD_OPEN_NON_FISCAL_RECEIPT);
		return cmd;
	}

	public FiscalPacket cmdPerceptions(String description, BigDecimal amount, BigDecimal alicuotaIVA) {
		FiscalPacket cmd = createFiscalPacket(CMD_PERCEPTIONS);
		int i = 1;
		if(alicuotaIVA == null)
			cmd.setText(i++, "**.**", false);
		else
			cmd.setNumber(i++, alicuotaIVA, 2, 2, false);
		cmd.setText(i++, description, 20,false);
		cmd.setAmount(i++, amount, false, false);
		return cmd;
	}

	public FiscalPacket cmdPrintAccountItem(Date date, String docNumber, String description, BigDecimal debitAmount, BigDecimal creditAmount, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_ACCOUNT_ITEM);
		int i = 1;
		cmd.setDate(i++, date);
		cmd.setText(i++, docNumber, 20, false);
		cmd.setText(i++, description, 60, false);
		cmd.setNumber(i++, debitAmount, 9, 2, false);
		cmd.setNumber(i++, creditAmount, 9, 2, false);
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdPrintEmbarkItem(String description, BigDecimal quantity, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_EMBARK_ITEM);
		int i = 1;
		cmd.setText(i++, description, 108, false);
		cmd.setQuantity(i++, quantity, false);
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdPrintFiscalText(String text, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_FISCAL_TEXT);
		int i = 1;
		cmd.setText(i++, text, 50, false);
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdPrintLineItem(String description, BigDecimal quantity, BigDecimal price, BigDecimal ivaPercent, boolean substract, BigDecimal internalTaxes, boolean basePrice, Integer display) {
		//Most models' description max length is 50
		int maxLength = 50;
		return cmdPrintLineItem(description, quantity, price, ivaPercent, substract, internalTaxes, basePrice, display, maxLength);
	}
	
	//Cuspide Computacion: metodo que permite especificar la longitud máxima de la descripcion, para los modelos que lo requieran.
	protected FiscalPacket cmdPrintLineItem(String description, BigDecimal quantity, BigDecimal price, BigDecimal ivaPercent, boolean substract, BigDecimal internalTaxes, boolean basePrice, Integer display, int descMaxLength) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_LINE_ITEM);
		int i = 1;
		cmd.setText(i++, description, descMaxLength, false);
		cmd.setQuantity(i++, quantity, false);
		cmd.setAmount(i++, price, false, true);
		if(ivaPercent == null)
			cmd.setText(i++, "**.**", false);
		else
			cmd.setNumber(i++, ivaPercent, 2, 2, false);		
		cmd.setBoolean(i++, substract, "m", "M", false);
		cmd.setNumber(i++, internalTaxes, 6, 8, false);
		cmd.setNumber(i++, display, true);
		cmd.setBoolean(i++, basePrice, "x", "T", false);
		return cmd;
	}

	public FiscalPacket cmdPrintNonFiscalText(String text, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_NON_FISCAL_TEXT);
		int i = 1;
		cmd.setText(i++, text, 120, false);
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdPrintQuotationItem(String description, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_PRINT_QUOTATION_ITEM);
		int i = 1;
		cmd.setText(i++, description, 120, false);
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdReprint() {
		FiscalPacket cmd = createFiscalPacket(CMD_REPRINT_DOCUMENT);
		return cmd;
	}

	public FiscalPacket cmdSendFirstIVA() {
		FiscalPacket cmd = createFiscalPacket(CMD_SEND_FIRST_IVA);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdNextIVATransmission() {
		FiscalPacket cmd = createFiscalPacket(CMD_NEXT_TRANSMISSION);
		return cmd;
	}

	public FiscalPacket cmdSetComSpeed(Long speed) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_COM_SPEED);
		int i = 1;
		cmd.setLong(i++, speed);
		return cmd;
	}

	public FiscalPacket cmdSetCustomerData(String name, String customerDocNumber, String ivaResponsibility, String docType, String location) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_CUSTOMER_DATA);
		int i = 1;
		cmd.setText(i++, name, 50, true);
		cmd.setText(i++, formatDocNumber(docType,customerDocNumber), true);
		cmd.setText(i++, ivaResponsibility, false);
		cmd.setText(i++, docType, true);
		cmd.setText(i++, location, 50, true);
		return cmd;
	}

	public FiscalPacket cmdSetDateTime(Date dateTime) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_DATE_TIME);
		cmd.setDateAndTime(1,2, dateTime);
		return cmd;
	}

	public FiscalPacket cmdSetEmbarkNumber(int line, String text) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_EMBARK_NUMBER);
		int i = 1;
		cmd.setNumber(i++, line, false);
		cmd.setText(i++, text, 20, false);
		return cmd;
	}

	public FiscalPacket cmdSetFantasyName(int line, String text) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_FANTASY_NAME);
		int i = 1;
		cmd.setNumber(i++, line, false);
		cmd.setText(i++, text, 50, false);
		return cmd;
	}

	public FiscalPacket cmdSetHeaderTrailer(int line, String text) {
		FiscalPacket cmd = createFiscalPacket(CMD_SET_HEADER_TRAILER);
		int i = 1;
		cmd.setNumber(i++, line, false);
		cmd.setText(i++, text, 120, false);
		return cmd;
	}

	public FiscalPacket cmdSTATPRN() {
		FiscalPacket cmd = createFiscalPacket(CMD_STATPRN);
		return cmd;
	}

	public FiscalPacket cmdStatusRequest() {
		FiscalPacket cmd = createFiscalPacket(CMD_STATUS_REQUEST);
		return cmd;
	}

	public FiscalPacket cmdSubtotal(boolean print, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_SUBTOTAL);
		int i = 1;
		cmd.setBoolean(i++, print, "P", "x", false);
		cmd.setString(i++, "x");
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	public FiscalPacket cmdTotalTender(String description, BigDecimal amount, boolean cancel, Integer display) {
		FiscalPacket cmd = createFiscalPacket(CMD_TOTAL_TENDER);
		int i = 1;
		cmd.setText(i++, description, 80, false);
		cmd.setNumber(i++, amount, 9, 2, false);
		cmd.setBoolean(i++, cancel, "C", "T");
		cmd.setNumber(i++, display, true);
		return cmd;
	}

	@Override
	public FiscalPacket cmdReturnRecharge(String description,
			BigDecimal amount, BigDecimal ivaPercent, boolean subtract,
			BigDecimal internalTaxes, boolean baseAmount, Integer display, String operation) {
		int descMaxLength = 50;
		return cmdReturnRecharge(description, amount, ivaPercent, subtract,
				internalTaxes, baseAmount, display, operation, descMaxLength);
	}
	

	protected FiscalPacket cmdReturnRecharge(String description,
			BigDecimal amount, BigDecimal ivaPercent, boolean subtract,
			BigDecimal internalTaxes, boolean baseAmount, Integer display,
			String operation, int descMaxLength) {
		FiscalPacket cmd = createFiscalPacket(CMD_RETURN_RECHARGE);
		int i = 1;
		cmd.setText(i++, description, descMaxLength, false);
		cmd.setNumber(i++, amount, 9, 2, false);
		if(ivaPercent == null){
			ivaPercent = BigDecimal.ZERO;
		}
		cmd.setNumber(i++, ivaPercent, 2, 2, false);		
		cmd.setBoolean(i++, subtract, "m", "M", false);
		cmd.setNumber(i++, internalTaxes, 6, 8, false);
		cmd.setNumber(i++, display, true);
		cmd.setBoolean(i++, baseAmount, "x", "T", false);
		cmd.setText(i++, operation, false);
		return cmd;
	}
	
	@Override
	public FiscalPacket cmdOpenDrawer(){
		// Se retorna null porque la gran mayoría no implementa este comando
		return null;
	}
	
	@Override
	public FiscalPacket cmdDeleteHeaderTrailerLine(int lineNo){
		if(lineNo > 0){
			FiscalPacket cmd = createFiscalPacket(CMD_SET_HEADER_TRAILER);
			int i = 1;
			cmd.setInt(i++, lineNo);
			cmd.setText(i++, SET_HEADER_TRAILER_DEL, 40, false);
			return cmd;
		}
		return cmdDeleteHeaderTrailerGroup(lineNo);
	}
	
	@Override
	public FiscalPacket cmdDeleteHeaderTrailerGroup(int delOption){
		FiscalPacket cmd = createFiscalPacket(CMD_SET_HEADER_TRAILER);
		int i = 1;
		cmd.setInt(i++, delOption);
		cmd.setText(i++, SET_HEADER_TRAILER_DEL, 40, false);
		return cmd;
	}
	
	@Override
	public void openDrawer() throws FiscalPrinterIOException{
		FiscalPacket packet = cmdOpenDrawer();
		if(packet == null){
			throw new FiscalPrinterIOException("El comando de apertura del cajon de dinero no esta soportado para la impresora fiscal");
		}
		execute(packet);
		
		// Se indica al manejador de eventos que la impresión ha finalizado.
		fireOpenDrawerEnded();
	}

	protected FiscalPacket createFiscalPacket() {
		return new HasarFiscalPacket(getEncoding(),getBaseRolloverYear(), this);
	}
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getBaseRolloverYear() {
		return baseRolloverYear;
	}

	public void setBaseRolloverYear(int baseRolloverYear) {
		this.baseRolloverYear = baseRolloverYear;
	}
	
	/**
	 * Ejecuta un comando fiscal en la impresora y analiza la existencia
	 * de errores en la respuesta. En caso de que se produzca algún error
	 * se propagan mediante excepciones.
	 * @param command Comando a ejecutar.
	 * @return Retorna un <code>FiscalPacket</code> que contiene la respuesta
	 * de la impresora.
	 * @throws FiscalPrinterIOException cuando se producce algún error de
	 * comunicación con el dispositivo.
	 * @throws FiscalPrinterStatusError cuando la impresora responde con un
	 * código de estado de error.
	 */
	protected FiscalPacket execute(FiscalPacket command) throws FiscalPrinterIOException, FiscalPrinterStatusError {
		FiscalPacket response = createFiscalPacket();
		
		// Se guarda el comando como el último ejecutado.
		setLastRequest(command);
		setLastResponse(null);
		
		try {
			// Se envía el comando a la interfaz de comunicación para
			// ser ejecutado.
			getFiscalComm().execute(command, response);
			setLastResponse(response);
			
		} catch (IOException e) {
			throw new FiscalPrinterIOException(e.getMessage(), command, response);
		}

		// Se chequea el status devuelto por la impresora.
		boolean statusChanged = checkStatus(command, response);

		// Si se produjeron cambios en el estado de la impresora se dispara
		// el evento correspondiente.
		if(statusChanged)
			fireStatusChanged(command, response);

		// Si la impresora quedó en estado de error entonces se lanza una
		// excepción.
		if(getMessages().hasErrors()) {
			throw new FiscalPrinterStatusError(command, response, getMessages());
		}
		
		// Se informa al manejador que el comando se ejecutó satisfactoriamente.
		fireCommandExecuted(command, response);
		
		return response;
	}
	
	private boolean checkStatus(FiscalPacket command, FiscalPacket response) throws FiscalPrinterIOException {
		int newPrinterStatus;
		int newFiscalStatus;

		try {
			// Se obtiene los estados a partir de la respuesta.
			newPrinterStatus = response.getPrinterStatus();	
			newFiscalStatus = response.getFiscalStatus();
		} catch (Exception e) {
			// Se puede producir un error de formato al querer obtener los estados
			// de la respuesta. Puede suceder que solo se reciba una parte de la
			// respuesta.
			throw new FiscalPrinterIOException(MsgRepository.get("ResponseFormatError"), getLastRequest(), response);
		}

		// Se comprueba si el status fue modificado. 
		boolean stsChanged = getPrinterStatus() != newPrinterStatus ||
							 getFiscalStatus() != newFiscalStatus;
		
		// Se asignan los estados de impresora y controlador fiscal.
		setPrinterStatus(newPrinterStatus);
		setFiscalStatus(newFiscalStatus);
		
		FiscalMessages msgs = new FiscalMessages();
		// Se chequea el estado del controlador fiscal.
		for(int i = 0; i < getFiscalStatusCodes().length; i++) {
			int statusCode = getFiscalStatusCodes()[i];
			if(command.getCommandCode() == CMD_CANCEL_DOCUMENT && statusCode != FST_DOCUMENT_OPEN)
				continue;
			// Si el comando es de cancelación y no hay ningún documento abierto, se desestima
			if ((getFiscalStatus() & statusCode) != 0) {
				FiscalMessage msg = getFiscalStatusMsgs().get(statusCode);
				msgs.add(msg);
			}
		}
			
		// Se chequea el estado de la impresora.
		for(int i = 0; i < getPrinterStatusCodes().length; i++) {
			int statusCode = getPrinterStatusCodes()[i];
			// Si el comando es el de cierre del documento y tenemos el flag de
			// que falta papel se desestima ya que ya a esta altura se imprimió
			// el ticket y además generalmente el error de falta papel como
			// respuesta a este comando es un WARNING "Queda poco papel"
			if (!(isCloseCommand(command) && (statusCode == PST_JOURNAL_PAPER_OUT || statusCode == PST_TICKET_PAPER_OUT))) {
				if((getPrinterStatus() & statusCode) != 0) {
					FiscalMessage msg = getPrinterStatusMsgs().get(statusCode);
					msgs.add(msg);
				}
				// Se chequea el estado del papel de la impresora y se 
				// setea el mismo.
				if(statusCode == PST_JOURNAL_PAPER_OUT || statusCode == PST_TICKET_PAPER_OUT)
					setWithoutPaper((getPrinterStatus() & statusCode) != 0);
			}
		}
		
		// Se setean los mensajes de la impresora.
		setMessages(msgs);
		
		return stsChanged;
	}
	
	private boolean isCloseCommand(FiscalPacket command){
		return command.getCommandCode() == CMD_CLOSE_DNFH
				|| command.getCommandCode() == CMD_CLOSE_FISCAL_RECEIPT
				|| command.getCommandCode() == CMD_CLOSE_NON_FISCAL_RECEIPT;
	}
	
	/**
	 * @return Returns the fiscalStatus.
	 */
	public int getFiscalStatus() {
		return fiscalStatus;
	}

	/**
	 * @param fiscalStatus The fiscalStatus to set.
	 */
	protected void setFiscalStatus(int fiscalStatus) {
		this.fiscalStatus = fiscalStatus;
	}

	/**
	 * @return Returns the printerStatus.
	 */
	public int getPrinterStatus() {
		return printerStatus;
	}

	/**
	 * @param printerStatus The printerStatus to set.
	 */
	protected void setPrinterStatus(int printerStatus) {
		this.printerStatus = printerStatus;
	}

	/**
	 * @return Returns the fiscalStatusMsgs.
	 */
	protected Map<Integer, FiscalMessage> getFiscalStatusMsgs() {
		if(fiscalStatusMsgs == null) {
			fiscalStatusMsgs = new HashMap<Integer,FiscalMessage>();
			Map<Integer,FiscalMessage> st = fiscalStatusMsgs; // Short Alias.
	
			// Se cargan los mensajes de estado fiscal.
			st.put(FST_FISCAL_MEMORY_CRC_ERROR,   MsgRepository.getFiscalMsg(FST_FISCAL_MEMORY_CRC_ERROR, "FstFiscalMemoryCrcErrorTitle","FstFiscalMemoryCrcErrorDesc", true));
			st.put(FST_WORKING_MEMORY_CRC_ERROR,  MsgRepository.getFiscalMsg(FST_WORKING_MEMORY_CRC_ERROR, "FstWorkingMemoryCrcErrorTitle","FstWorkingMemoryCrcErrorDesc", true));
			st.put(FST_UNKNOWN_COMMAND,           MsgRepository.getFiscalMsg(FST_UNKNOWN_COMMAND,"FstUnknownCommandTitle","FstUnknownCommandDesc",true));
			st.put(FST_INVALID_DATA_FIELD,        MsgRepository.getFiscalMsg(FST_INVALID_DATA_FIELD,"FstInvalidDataFieldTitle","FstInvalidDataFieldDesc",true));
			st.put(FST_INVALID_COMMAND,           MsgRepository.getFiscalMsg(FST_INVALID_COMMAND, "FstInvalidCommandTitle", "FstInvalidCommandDesc", true));
			st.put(FST_ACCUMULATOR_OVERFLOW,      MsgRepository.getFiscalMsg(FST_ACCUMULATOR_OVERFLOW, "FstAccumulatorOverflowTitle", "FstAccumulatorOverflowDesc", true));
			st.put(FST_FISCAL_MEMORY_FULL,        MsgRepository.getFiscalMsg(FST_FISCAL_MEMORY_FULL,"FstFiscalMemoryFullTitle", "FstFiscalMemoryFullDesc", true));
			st.put(FST_FISCAL_MEMORY_ALMOST_FULL, MsgRepository.getFiscalMsg(FST_FISCAL_MEMORY_ALMOST_FULL,"FstFiscalMemoryAlmostFullTitle", "FstFiscalMemoryAlmostFullDesc", true));
			st.put(FST_DEVICE_CERTIFIED,          MsgRepository.getFiscalMsg(FST_DEVICE_CERTIFIED, "FstDeviceCertifiedTitle", "FstDeviceCertifiedDesc", false));
			st.put(FST_DEVICE_FISCALIZED,         MsgRepository.getFiscalMsg(FST_DEVICE_FISCALIZED,"FstDeviceFiscalizedTitle", "FstDeviceFiscalizedDesc", false));
			st.put(FST_DATE_ERROR,                MsgRepository.getFiscalMsg(FST_DATE_ERROR, "FstDateErrorTitle", "FstDateErrorDesc", true));
			st.put(FST_FISCAL_DOCUMENT_OPEN,      MsgRepository.getFiscalMsg(FST_FISCAL_DOCUMENT_OPEN, "FstFiscalDocumentOpenTitle", "FstFiscalDocumentOpenDesc", false));
			st.put(FST_DOCUMENT_OPEN,             MsgRepository.getFiscalMsg(FST_DOCUMENT_OPEN, "FstDocumentOpenTitle", "FstDocumentOpenDesc", false));
			st.put(FST_STATPRN_ACTIVE,            MsgRepository.getFiscalMsg(FST_STATPRN_ACTIVE, "FstSTATPRNActiveTitle", "FstSTATPRNActiveDesc", true));
		}
		return fiscalStatusMsgs;
	}

	/**
	 * @return Returns the printerStatusMsgs.
	 */
	protected Map<Integer, FiscalMessage> getPrinterStatusMsgs() {
		if(printerStatusMsgs == null) {
			printerStatusMsgs = new HashMap<Integer,FiscalMessage>();
			Map<Integer,FiscalMessage> st = printerStatusMsgs; // Short Alias.
			
			// Se cargan los mensajes de estado de la impresora.
			st.put(PST_PRINTER_BUSY,        MsgRepository.getFiscalMsg(PST_PRINTER_BUSY, "PstPrinterBusyTitle", "PstPrinterBusyDesc", false));
			st.put(PST_PRINTER_ERROR,       MsgRepository.getFiscalMsg(PST_PRINTER_ERROR, "PstPrinterErrorTitle", "PstPrinterErrorDesc", true));
			st.put(PST_PRINTER_OFFLINE,     MsgRepository.getFiscalMsg(PST_PRINTER_OFFLINE, "PstPrinterOfflineTitle", "PstPrinterOfflineDesc", true));
			st.put(PST_JOURNAL_PAPER_OUT,   MsgRepository.getFiscalMsg(PST_JOURNAL_PAPER_OUT, "PstJournalPaperOutTitle", "PstJournalPaperOutDesc", true));
			st.put(PST_TICKET_PAPER_OUT,    MsgRepository.getFiscalMsg(PST_TICKET_PAPER_OUT, "PstTicketPaperOutTitle", "PstTicketPaperOutDesc", true));
			st.put(PST_PRINT_BUFFER_FULL,   MsgRepository.getFiscalMsg(PST_PRINT_BUFFER_FULL, "PstPrintBufferFullTitle", "PstPrintBufferFullDesc", false));
			st.put(PST_PRINT_BUFFER_EMPTY,  MsgRepository.getFiscalMsg(PST_PRINT_BUFFER_EMPTY, "PstPrintBufferEmptyTitle", "PstPrintBufferEmptyDesc", false));
			st.put(PST_PRINTER_COVER_OPEN,  MsgRepository.getFiscalMsg(PST_PRINTER_COVER_OPEN, "PstPrinterCoverOpenTitle", "PstPrinterCoverOpenDesc", true));
			st.put(PST_MONEY_DRAWER_CLOSED, MsgRepository.getFiscalMsg(PST_MONEY_DRAWER_CLOSED, "PstMoneyDrawerClosedTitle", "PstMoneyDrawerClosedDesc", false));
		}
		return printerStatusMsgs;
	}

	/**
	 * @return Returns the fiscalStatusCodes.
	 */
	protected int[] getFiscalStatusCodes() {
		return fiscalStatusCodes;
	}

	/**
	 * @return Returns the printerStatusCodes.
	 */
	protected int[] getPrinterStatusCodes() {
		return printerStatusCodes;
	}

	public void printDocument(Invoice invoice) throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException {
		Customer customer = invoice.getCustomer();
		FiscalPacket response;
		// Se valida la factura.
		invoice.validate();
		boolean hasCashPayments = false;
		try {
			// Enviar comando de cancelación antes de imprimir si así se
			// requiere
			setAskMoment(true);
			if(isCancelBeforePrint()){
				setCancelAllowed(true);
				cancelCurrentDocument();
			}
			setCancelAllowed(false);
			//////////////////////////////////////////////////////////////
			// Se setean los datos del comprador.
			// Comando: @SetCustomerData
			loadCustomerData(customer);

			//////////////////////////////////////////////////////////////
			// Se carga el número de remito asignado a la factura
			// en caso de existir.
			// Comando: @SetEmbarkNumber
			if(invoice.hasPackingSlipNumber())
				execute(cmdSetEmbarkNumber(1, invoice.getPackingSlipNumber()));
			
			//////////////////////////////////////////////////////////////
			// Se abre el documento fiscal.
			// Comando: @OpenFiscalReceipt
			response = execute(cmdOpenFiscalReceipt(
				traduceDocumentType(Document.DT_INVOICE, invoice.getLetter()))
			);
			setLastDocumentNo("");
			setCancelAllowed(true);
			setDocumentOpened(true);
			
			//////////////////////////////////////////////////////////////
			// Se cargan las observaciones de la cabecera de la factura 
			// como texto fiscal. 
			// Comando: @SetHeaderTrailer
			// TODO Por ahora mejor no se imprimen observaciones en la cabecera
			// porque corremos el riesgo de pisar datos agregados fijos como por
			// ejemplo el nombre y/o cuit de la compañía. En el caso que no se
			// pise ninguno de ellos, agregar estas observaciones de cabecera. 
			/*
			for (String observation : invoice.getHeaderObservations()) {
				execute(cmdPrintFiscalText(observation,null));
			}*/
			
			//////////////////////////////////////////////////////////////		
			// Se cargan los ítems de la factura.
			// Comando: @PrintLineItem
			loadDocumentLineItems(invoice);
			
			//////////////////////////////////////////////////////////////
			// Se cargan los descuentos de la factura.
			loadDocumentDiscounts(invoice);
			
			//////////////////////////////////////////////////////////////
			// Se calcula el subtotal.
			// Comando: @Subtotal
			execute(cmdSubtotal(true, null));
			
			//////////////////////////////////////////////////////////////
			// Se cargan los impuestos adicionales de la factura
			loadOtherTaxes(invoice);
			
			//////////////////////////////////////////////////////////////
			// Se ingresan los pagos realizados por el comprador.
			// Comando: @TotalTender
			for (Payment payment : invoice.getPayments()) {
				hasCashPayments = hasCashPayments || payment.isCash();
				response = execute(cmdTotalTender(
					payment.getDescription(), 
					payment.getAmount(), 
					false, 
					null)
				);
				setCancelAllowed(false);
			}
			
			//////////////////////////////////////////////////////////////
			// Abrir el cajón de dinero
			// Comando: @OpenDrawer
			// El cajón de dinero se abre si tenemos cambio en la factura,
			// existen pagos en efectivo ó si el último pago agregado tiene
			// vuelto
			if (hasCashPayments
					|| invoice.getChangeAmt().compareTo(BigDecimal.ZERO) > 0
					|| response.getBigDecimal(3).compareTo(BigDecimal.ZERO) < 0) {
				// Sólo las impresoras que soportan el comando
				if(cmdOpenDrawer() != null){
					execute(cmdOpenDrawer());
				}
			}
			
			// Agrego los nuevos datos de la cola de impresión, previo a eliminar lo de la cola
			addFooterObservations(11, 20, invoice.getFooterObservations(),
					false, -2);
			
			//////////////////////////////////////////////////////////////
			// Se cierra el comprobante fiscal.
			// Comando: @CloseFiscalReceipt
			response = execute(cmdCloseFiscalReceipt(null));
			
			setDocumentOpened(false);
			// Chequeo de impuestos
			checkTaxes(invoice);
			
			// Se obtiene el número de comprobante emitido.
			setLastDocumentNo(response.getString(3));
			invoice.setDocumentNo(getLastDocumentNo());
			// Se obtiene el número del CAI.
			if(invoice.getLetter().equals(Document.DOC_LETTER_A));
				invoice.setCAINumber(getCAINumber(response));
			
			setAskMoment(false);
			
			// Se indica al manejador de eventos que la impresión ha finalizado.
			firePrintEnded();
			
		} catch (FiscalPrinterIOException e) {
			if(!isDocumentPrintAsk()){
				// Si ocurrió algún error se intenta cancelar el documento
				// actual y se relanza la excepción.
				cancelCurrentDocument();
			}
			throw e;
		}
	}
	
	public void fiscalClose(String closeType) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		execute(cmdDailyClose(closeType));
		
		// Se indica al manejador de eventos que la impresión ha finalizado.
		fireFiscalCloseEnded();
	}
	
	/**
	 * Borra las líneas de la cabecera o cola de impresión que corresponden a
	 * los nros de línea parámetro
	 * 
	 * @param lineNoFrom
	 *            nro línea inicial a eliminar
	 * @param lineNoTo
	 *            nro de línea final a eliminar
	 */
	protected void delHeaderTrailerLines(int lineNoFrom, int lineNoTo) throws FiscalPrinterIOException{
		for (int i = lineNoFrom; i <= lineNoTo; i++) {
			execute(cmdDeleteHeaderTrailerLine(i));
		}
	}
	
	protected void delHeaderTrailerGroup(int delOption) throws FiscalPrinterIOException{
		execute(cmdDeleteHeaderTrailerGroup(delOption));
	} 
	
	protected void addFooterObservations(int lineNoFrom, int lineNoTo, List<String> observations, boolean resetGroup, int delGroupOption) throws FiscalPrinterIOException{
		if(resetGroup){
			execute(cmdDeleteHeaderTrailerGroup(delGroupOption));
		}
		for (int i = 0; i < observations.size() && lineNoFrom <= lineNoTo; i++, lineNoFrom++) {
			execute(cmdSetHeaderTrailer(lineNoFrom, observations.get(i)));
		}
	}
	
	public void printDocument(CreditNote creditNote) throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException {
		Customer customer = creditNote.getCustomer();
		FiscalPacket response;
		// Se valida la nota de crédito.
		creditNote.validate();
		try {
			// Enviar comando de cancelación antes de imprimir si así se
			// requiere
			setAskMoment(true);
			if(isCancelBeforePrint()){
				setCancelAllowed(true);
				cancelCurrentDocument();
			}
			setCancelAllowed(false);
			//////////////////////////////////////////////////////////////
			// Se setean los datos del comprador.
			// Comando: @SetCustomerData
			loadCustomerData(customer);

			//////////////////////////////////////////////////////////////
			// Se setea el número de comprobante original.
			// Comando: @SetEmbarkNumber
			execute(cmdSetEmbarkNumber(
				1, creditNote.getOriginalDocumentNo())
			);

			//////////////////////////////////////////////////////////////
			// Se abre un documento no fiscal homologado.
			// Comando: @OpenDNFH
			execute(cmdOpenDNFH(
				traduceDocumentType(Document.DT_CREDIT_NOTE, creditNote.getLetter()),
				"x"
			));
			setLastDocumentNo("");
			setCancelAllowed(true);
			setDocumentOpened(true);
			
			//////////////////////////////////////////////////////////////
			// Se cargan las observaciones de la cabecera de la factura 
			// como texto fiscal. 
			// Comando: @SetHeaderTrailer
			// TODO Por ahora mejor no se imprimen observaciones en la cabecera
			// porque corremos el riesgo de pisar datos agregados fijos como por
			// ejemplo el nombre y/o cuit de la compañía. En el caso que no se
			// pise ninguno de ellos, agregar estas observaciones de cabecera. 
			/*
			for (String observation : invoice.getHeaderObservations()) {
			execute(cmdPrintFiscalText(observation,null));
			}*/

			//////////////////////////////////////////////////////////////		
			// Se cargan los ítems de la nota de crédito.
			// Comando: @PrintLineItem
			loadDocumentLineItems(creditNote);

			//////////////////////////////////////////////////////////////
			// Se cargan los descuentos de la factura.
			loadDocumentDiscounts(creditNote);
			
			//////////////////////////////////////////////////////////////
			// Se cargan los impuestos adicionales de la factura
			loadOtherTaxes(creditNote);
			
			// Agrego los nuevos datos de la cola de impresión, previo a eliminar lo de la cola
			addFooterObservations(11, 20, creditNote.getFooterObservations(),
					false, -2);
			
			//////////////////////////////////////////////////////////////
			// Se cierra el comprobante no fiscal homologado.
			// Comando: @CloseDNFH
			response = execute(cmdCloseDNFH(null));
			setDocumentOpened(false);
			setCancelAllowed(false);
			// Se obtiene el número de la nota de crédito emitida.
			setLastDocumentNo(response.getString(3));
			creditNote.setDocumentNo(getLastDocumentNo());
			
			setAskMoment(false);
			
			// Se indica al manejador de eventos que la impresión ha finalizado.
			firePrintEnded();

		} catch (FiscalPrinterIOException e) {
			if(!isDocumentPrintAsk()){
				// Si ocurrió algún error se intenta cancelar el documento
				// actual y se relanza la excepción.
				cancelCurrentDocument();
			}
			throw e;
		}
	}
	
	public void printDocument(DebitNote debitNote) throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException {
		Customer customer = debitNote.getCustomer();
		FiscalPacket response;
		// Se valida la nota de débito.
		debitNote.validate();
		try {
			// Enviar comando de cancelación antes de imprimir si así se
			// requiere
			setAskMoment(true);
			if(isCancelBeforePrint()){
				setCancelAllowed(true);
				cancelCurrentDocument();
			}
			setCancelAllowed(false);
			//////////////////////////////////////////////////////////////
			// Se setean los datos del comprador.
			// Comando: @SetCustomerData
			loadCustomerData(customer);

			//////////////////////////////////////////////////////////////
			// Se carga el número de remito asignado a la nota de débito
			// en caso de existir.
			// Comando: @SetEmbarkNumber
			if(debitNote.hasPackingSlipNumber())
				execute(cmdSetEmbarkNumber(1, debitNote.getPackingSlipNumber()));
			
			//////////////////////////////////////////////////////////////
			// Se abre el documento fiscal.
			// Comando: @OpenFiscalReceipt
			response = execute(cmdOpenFiscalReceipt(
				traduceDocumentType(Document.DT_DEBIT_NOTE, debitNote.getLetter()))
			);
			setLastDocumentNo("");
			setCancelAllowed(true);
			setDocumentOpened(true);

			//////////////////////////////////////////////////////////////		
			// Se cargan los ítems de la nota de débito.
			// Comando: @PrintLineItem
			loadDocumentLineItems(debitNote);

			//////////////////////////////////////////////////////////////
			// Se cargan los descuentos de la nota de débito.
			loadDocumentDiscounts(debitNote);
			
			//////////////////////////////////////////////////////////////
			// Se cargan los impuestos adicionales de la factura
			loadOtherTaxes(debitNote);
			
			//////////////////////////////////////////////////////////////
			// Se cargan las observaciones del pie de la nota de débito 
			// como texto fiscal.
			// Comando: @PrintFiscalText
//			for (String observation : debitNote.getFooterObservations()) {
//				execute(cmdPrintFiscalText(observation,null));
//			}
			
			// Agrego los nuevos datos de la cola de impresión, previo a eliminar lo de la cola
			addFooterObservations(11, 20, debitNote.getFooterObservations(),
					false, -2);
			
			//////////////////////////////////////////////////////////////
			// Se cierra el comprobante fiscal.
			// Comando: @CloseFiscalReceipt
			response = execute(cmdCloseFiscalReceipt(null));
			setDocumentOpened(false);
			// Se obtiene el número de comprobante emitido.
			setLastDocumentNo(response.getString(3));
			debitNote.setDocumentNo(getLastDocumentNo());
			// Se obtiene el número del CAI.
			if(debitNote.getLetter().equals(Document.DOC_LETTER_A));
				debitNote.setCAINumber(getCAINumber(response));
			
			setAskMoment(false);
				
			// Se indica al manejador de eventos que la impresión ha finalizado.
			firePrintEnded();

		} catch (FiscalPrinterIOException e) {
			if(!isDocumentPrintAsk()){
				// Si ocurrió algún error se intenta cancelar el documento
				// actual y se relanza la excepción.
				cancelCurrentDocument();
			}
			throw e;
		}
	}
	@Override
	public void printDocument(NonFiscalDocument nonFiscalDocument)
			throws FiscalPrinterStatusError, FiscalPrinterIOException,
			DocumentException {

		// Se valida el documento no fiscal.
		nonFiscalDocument.validate();
		try {
			// Enviar comando de cancelación antes de imprimir si así se
			// requiere
			setAskMoment(true);
			if(isCancelBeforePrint()){
				setCancelAllowed(true);
				cancelCurrentDocument();
			}
			setCancelAllowed(false);
			//////////////////////////////////////////////////////////////
			// Se abre el documento no fiscal.
			// Comando: @OpenNonFiscalReceipt
			execute(cmdOpenNonFiscalReceipt());
			setLastDocumentNo("");
			setCancelAllowed(true);
			setDocumentOpened(true);

			//////////////////////////////////////////////////////////////		
			// Se cargan las líneas del documento no fiscal
			// Comando: @PrintNonFiscalText
			for (String line : nonFiscalDocument.getLines()) {
				execute(cmdPrintNonFiscalText(line, 0));
			}
			
			//////////////////////////////////////////////////////////////
			// Se cierra el comprobante fiscal.
			// Comando: @CloseFiscalReceipt
			execute(cmdCloseNonFiscalReceipt(nonFiscalDocument.getCopies()));
			setDocumentOpened(false);
			
			setAskMoment(false);
			
			// Se indica al manejador de eventos que la impresión ha finalizado.
			firePrintEnded();

		} catch (FiscalPrinterIOException e) {
			if(!isDocumentPrintAsk()){
				// Si ocurrió algún error se intenta cancelar el documento
				// actual y se relanza la excepción.
				cancelCurrentDocument();
			}
			throw e;
		}
	}

	/**
	 * Realiza la conversión entre el entero que representa a la categoría
	 * de IVA en las clases de documentos y el string que espera la impresora
	 * fiscal. 
	 * @param ivaResponsibility Valor de la responsabilidad frente a IVA.
	 * @return El string que representa la responsabilidad frente al IVA.
	 */
	protected String traduceIvaResponsibility(Integer ivaResponsibility) {
		String result = getIvaResponsabilities().get(ivaResponsibility);
		if(result == null)
			result = NO_CATEGORIZADO;
		return result;
	}
	
	/**
	 * Realiza la conversión entre el entero que representa el tipo de identificación
	 * en las clases de documentos y el string que espera la impresora fiscal. 
	 * @param identificationTypes Tipo de identificación a convertir
	 * @return Retorna el string que representa el tipo de identificación que espera
	 * la impresora fiscal.
	 */
	protected String traduceIdentificationType(Integer identificationType) {
		String result = getIdentificationTypes().get(identificationType);
		if(result == null)
			result = SIN_CALIFICADOR;
		return result;
	}

	/**
	 * Realiza la conversión entre el el tipo de documento en las clases de 
	 * documentos y el string que espera la impresora fiscal. 
	 * @param documentType Tipo de documento a convertir.
	 * @param letter Letra del documento.
	 * @return Retorna el string que representa el tipo de documento que espera
	 * la impresora fiscal.
	 */
	protected String traduceDocumentType(String documentType, String letter) {
		return getDocumentTypes().get(documentType + letter);
	}
	
	/**
	 * @return Returns the ivaResponsabilities.
	 */
	protected Map<Integer, String> getIvaResponsabilities() {
		if(ivaResponsabilities == null) {
			ivaResponsabilities = new HashMap<Integer,String>();
			ivaResponsabilities.put(Customer.CONSUMIDOR_FINAL, CONSUMIDOR_FINAL);
			ivaResponsabilities.put(Customer.EXENTO, EXENTO);
			ivaResponsabilities.put(Customer.MONOTRIBUTISTA_SOCIAL, MONOTRIBUTISTA_SOCIAL);
			ivaResponsabilities.put(Customer.NO_CATEGORIZADO, NO_CATEGORIZADO);
			ivaResponsabilities.put(Customer.NO_RESPONSABLE, NO_RESPONSABLE);
			ivaResponsabilities.put(Customer.PEQUENO_CONTRIBUYENTE_EVENTUAL, PEQUENO_CONTRIBUYENTE_EVENTUAL);
			ivaResponsabilities.put(Customer.PEQUENO_CONTRIBUYENTE_EVENTUAL_SOCIAL, PEQUENO_CONTRIBUYENTE_EVENTUAL_SOCIAL);
			ivaResponsabilities.put(Customer.RESPONSABLE_INSCRIPTO, RESPONSABLE_INSCRIPTO);
			ivaResponsabilities.put(Customer.RESPONSABLE_MONOTRIBUTO, RESPONSABLE_MONOTRIBUTO);
			ivaResponsabilities.put(Customer.RESPONSABLE_NO_INSCRIPTO, RESPONSABLE_NO_INSCRIPTO);
			ivaResponsabilities.put(Customer.RESPONSABLE_NO_INSCRIPTO_BIENES_DE_USO, RESPONSABLE_NO_INSCRIPTO_BIENES_DE_USO);
		}
		return ivaResponsabilities;
	}

	/**
	 * @return Returns the identificationTypes.
	 */
	protected Map<Integer, String> getIdentificationTypes() {
		if(identificationTypes == null) {
			identificationTypes = new HashMap<Integer, String>();
			identificationTypes.put(Customer.CEDULA, CEDULA);
			identificationTypes.put(Customer.CUIL, CUIL);
			identificationTypes.put(Customer.CUIT, CUIT);
			identificationTypes.put(Customer.DNI, DNI);
			identificationTypes.put(Customer.LIBRETA_CIVICA, LIBRETA_CIVICA);
			identificationTypes.put(Customer.LIBRETA_DE_ENROLAMIENTO, LIBRETA_DE_ENROLAMIENTO);
			identificationTypes.put(Customer.PASAPORTE, PASAPORTE);
			identificationTypes.put(Customer.SIN_CALIFICADOR, SIN_CALIFICADOR);
		}
		return identificationTypes;
	}

	/**
	 * @return Returns the documentTypes.
	 */
	protected Map<String, String> getDocumentTypes() {
		if(documentTypes == null) {
			documentTypes = new HashMap<String, String>();
			documentTypes.put(Document.DT_INVOICE + Document.DOC_LETTER_A, "A");
			documentTypes.put(Document.DT_INVOICE + Document.DOC_LETTER_B, "B");
			documentTypes.put(Document.DT_INVOICE + Document.DOC_LETTER_C, "B");
			documentTypes.put(Document.DT_CREDIT_NOTE + Document.DOC_LETTER_A, "R");
			documentTypes.put(Document.DT_CREDIT_NOTE + Document.DOC_LETTER_B, "S");
			documentTypes.put(Document.DT_CREDIT_NOTE + Document.DOC_LETTER_C, "S");
			documentTypes.put(Document.DT_DEBIT_NOTE + Document.DOC_LETTER_A, "D");
			documentTypes.put(Document.DT_DEBIT_NOTE + Document.DOC_LETTER_B, "E");
			documentTypes.put(Document.DT_DEBIT_NOTE + Document.DOC_LETTER_C, "E");
		}
		return documentTypes;
	}
	
	/**
	 * Formatea un número de identificación de un comprador para que sea
	 * aceptado por la impresora fiscal. En caso de ser un número de
	 * documento le extrae los ".", si es un CUIL/CUIT le extrae los "-", etc.
	 * @param docType Tipo de identificación.
	 * @param docNumber Número de identificación a formatear
	 * @return El string formateado.
	 */
	protected String formatDocNumber(String docType, String docNumber) {
		String result = docNumber;
		if(docNumber != null) {
			if(docType.equals(DNI))
				result = docNumber.replace(".","");
			if(docType.equals(CUIT) || docType.equals(CUIL)) 
				result = docNumber.replace("-","");
			result.trim();
		}
		return result;
	}
	
	/**
	 * Cancela el documento actualmente abierto en la impresora.
	 * En caso positivo, se indica que la impresora no contiene un
	 * documento abierto.
	 */
	@Override
	public void cancelCurrentDocument() throws FiscalPrinterIOException, FiscalPrinterStatusError{
		if(isCancelAllowed()){
			execute(cmdCancelDocument());
			setDocumentOpened(false);
		}
	}
	
	/**
	 * Obtiene el número del CAI de la respuesta al comando CloseFiscalReceipt. 
	 * @param response Respuesta obtenida de la impresora
	 * @return El número del CAI o null en caso de que la impresora fiscal
	 * no soporte esta opción.
	 */
	protected String getCAINumber(FiscalPacket response) {
		return null;
	}
	
	/**
	 * 	Ejecuta el comando para asignar los datos del comprador en caso
	 *	de que el comprador exista.
	 */
	protected FiscalPacket loadCustomerData(Customer customer) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		FiscalPacket response = null;
		if(customer != null) {
			// Según el manual de las impresoras HASAR, en este comando, si
			// el cliente es Consumidor Final y le monto no supera el máximo
			// los campos Nombre, DNI y Dirección son opcionales. Parece que la 
			// impresora no contempla esto y pide los valores de los campos
			// de todos modos. Por esto, si es consumido final se reemplazan
			// nulls en los campos por un String espacio " ".
			if(customer.getIvaResponsibility() == Customer.CONSUMIDOR_FINAL) {
				customer.setName(customer.getName() == null?" ":customer.getName());
				customer.setLocation(customer.getLocation() == null?" ":customer.getLocation());
				customer.setIdentificationNumber(customer
						.getIdentificationNumber() == null ? " " : customer
						.getIdentificationNumber());
			}
			execute(cmdSetCustomerData(
					customer.getName(), 
					customer.getIdentificationNumber(), 
					traduceIvaResponsibility(customer.getIvaResponsibility()), 
					traduceIdentificationType(customer.getIdentificationType()), 
					customer.getLocation())
			);
		}
		return response;
	}
	
	/**
	 * Ejecuta los comandos necesarios para cargar las líneas de item
	 * del documento en la impresora fiscal.
	 */
	private void loadDocumentLineItems(Document document) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		// Se cargan los ítems del documento.
		// Comando: @PrintLineItem
		for (DocumentLine item : document.getLines()) {
			execute(cmdPrintLineItem(
				item.getDescription(), 
				item.getQuantity(), 
				item.getAbsUnitPrice(), 
				item.getIvaRate(), 
				item.isSubstract(), 
				BigDecimal.ZERO, // Impuestos internos
				!item.isPriceIncludeIva(), 
				null)
			);
			// Se carga el descuento del ítem si es que posee.
			// Comando: @LastItemDiscount
			if (item.hasDiscount()) {
				DiscountLine discount = item.getDiscount();
				execute(cmdLastItemDiscount(
					discount.getDescription(), 
					discount.getAbsAmount(), 
					false, 
					!discount.isAmountIncludeIva(),
					null));		
			}
		}
	}

	/**
	 * Ejecuta los comandos necesarios para cargar todos los descuentos del
	 * documento sobre la impresora fiscal.
	 */
	private void loadDocumentDiscounts(Document document) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		//////////////////////////////////////////////////////////////
		// Se aplican las bonificaciones
		// Comando: @ReturnRecharge
		for (DiscountLine discount : document.getDocumentDiscounts()) {
			execute(cmdReturnRecharge(
				discount.getDescription(), 
				discount.getAmount(), 
				discount.getTaxRate(), 
				false, 
				BigDecimal.ZERO, // Impuestos internos 
				!discount.isAmountIncludeIva(), 
				null, // Display
				DISCOUNT_RECHARGE));
		}
		
		//////////////////////////////////////////////////////////////
		// Se aplica el descuento general en caso de existir.
		// Comando: @GeneralDiscount
		if(document.hasGeneralDiscount()) {
			DiscountLine generalDiscount = document.getGeneralDiscount();
			execute(cmdGeneralDiscount(
				generalDiscount.getDescription(), 
				generalDiscount.getAmount(), 
				false, 
				!generalDiscount.isAmountIncludeIva(),
				null));		
		}
	}

	/**
	 * Ejecuta los comandos necesarios para cargar los impuestos adicionales del
	 * documento en la impresora fiscal.
	 */
	private void loadOtherTaxes(Document document) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		// Se cargan los impuestos adicionales del documento.
		// Comando: @PrintLineItem
		for (Tax otherTax : document.getOtherTaxes()) {
			// FIXME por ahora se imprimen solamente las percepciones
			if(otherTax.isPercepcion()){
				execute(cmdPerceptions(otherTax.getName(), otherTax.getAmt(),
						null));
			}
		}
	}

	@Override
	public String formatText(String text, int maxLength) {
		StringBuffer result = new StringBuffer(); 
		text = super.formatText(text, maxLength);
		if(text == null)
			return text;
		// Reemplaza caracteres especiales que las impresoras hasar no permiten.
		for(int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			/*if(ch > 125)
				switch (ch) {
					case 'a': ch = 'a'; break;
					case 'b': ch = 'e'; break;
					case 'c': ch = 'i'; break;
					case 'd': ch = 'o'; break;
					case 'e': ch = 'u'; break;
					case 'f': ch = 'A'; break;
					case 'g': ch = 'E'; break;
					case 'h': ch = 'I'; break;
					case 'i': ch = 'O'; break;
					case 'j': ch = 'U'; break;
					case 'k': ch = 'n'; break;
					case 'l': ch = 'N'; break;
					case 'm': ch = ' '; break;
					default: ch = '#'; break;
				}*/
			result.append(ch);
		}
		return result.toString();
	}
	
	private void checkTaxes(Document document) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		//////////////////////////////////////////////////////////////
		// Incia la transmisión de información de IVA
		// Comando: @SendFirstIVA
		FiscalPacket response = execute(cmdSendFirstIVA());
		BigDecimal taxRate = null;
		BigDecimal taxAmount = null;
		BigDecimal taxBaseAmt = null;
		int type = response.getInt(3);
		while (type == 1) { // Tipo = 1 es IVA del documento
			taxRate = response.getBigDecimal(4);
			taxAmount = response.getBigDecimal(5);
			taxBaseAmt = response.getBigDecimal(8);
		
			System.out.println("- IVA: " + taxRate + ", Importe: " + taxAmount
					+ ", Base:" + taxBaseAmt);
			
			response = execute(cmdNextIVATransmission());
			type = response.getInt(3);
		}
	}

	@Override
	public int getAllowedPaymentQty() {
		// Por defecto todos los modelos Hasar permiten 4 pagos. Cada modelo
		// deberá sobreescribir este método para indicar un número de pagos
		// diferentes.
		return 4;
	}
	
	@Override
	public String getLastDocumentNoPrinted(String documentType, String letra)
			throws FiscalPrinterStatusError, FiscalPrinterIOException {
		String lastNro = "";
		
		//////////////////////////////////////////////////////////////
		// Incia la transmisión de información de IVA
		// Comando: @StatusRequest
		FiscalPacket response = execute(cmdStatusRequest());
		int index = 0;
		
		if(documentType.equals(Document.DT_CREDIT_NOTE)){
			index = letra.equals(Document.DOC_LETTER_A)?8:7;
		}
		else{
			index = letra.equals(Document.DOC_LETTER_A)?5:3;
		}
		
		lastNro = response.getString(index);
		
		setLastDocumentNo(lastNro);
		
		return lastNro;
	}
	
	
}
