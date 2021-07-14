package org.openXpertya.print.epson;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.openXpertya.print.fiscal.BasicFiscalPrinter;
import org.openXpertya.print.fiscal.ESCPOSFiscalPacket;
import org.openXpertya.print.fiscal.FiscalClosingResponseDTO;
import org.openXpertya.print.fiscal.FiscalInitData;
import org.openXpertya.print.fiscal.FiscalPacket;
import org.openXpertya.print.fiscal.comm.FiscalComm;
import org.openXpertya.print.fiscal.document.ClientOrgInfo;
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
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ESCPOSPrinter extends BasicFiscalPrinter implements ESCPOSCommands, ESCPOSConstants {

	/** Tamaño máximo de la descripción de cada línea */
	protected static final int DESCRIPTION_MAX_LENGTH = 38;
	
	/** Tamaño máximo de cada línea */
	protected static final int LINE_MAX_LENGTH = 48;
	
	/** Codificación de caracteres */
	public String encoding = "cp852"; // 18 PC852:Latin2
	
	/** Posibles mensajes de estado de la impresora */
	private Map<Integer,FiscalMessage> printerStatusMsgs;
	/** Posibles mensajes de estado offline de la impresora */
	private Map<Integer,FiscalMessage> offlineStatusMsgs;
	/** Posibles mensajes de estado de error de la impresora */
	private Map<Integer,FiscalMessage> errorStatusMsgs;
	/** Posibles mensajes de estado del papel de la impresora */
	private Map<Integer,FiscalMessage> paperStatusMsgs;
	
	/** Códigos de mensajes de estado de la impresora */
	private int[] printerStatusCodes = { PST_PRINTER_DRAWER, PST_PRINTER_OFFLINE };
	/** Códigos de mensajes de estado offline de la impresora */
	private int[] offlineStatusCodes  = { PST_OFFLINE_COVER, PST_OFFLINE_FEED, PST_OFFLINE_INTERRUPTED, PST_OFFLINE_ERROR };
	/** Códigos de mensajes de estado de error de la impresora */
	private int[] errorStatusCodes  = { PST_ERROR_CUTTER, PST_ERROR_UNRECOVERABLE, PST_ERROR_AUTORECOVERABLE };
	/** Códigos de mensajes de estado del papel de la impresora */
	private int[] paperStatusCodes  = { PST_PAPER_NEAR_END, PST_PAPER_END };
	
	
	public ESCPOSPrinter() {
		// TODO Auto-generated constructor stub
	}

	public ESCPOSPrinter(FiscalComm fiscalComm) {
		super(fiscalComm);
		// TODO Auto-generated constructor stub
	}

	protected Map<Integer,FiscalMessage> getPrinterStatusMsgs() {
		if(printerStatusMsgs == null) {
			printerStatusMsgs = new HashMap<Integer, FiscalMessage>();
			printerStatusMsgs.put(PST_PRINTER_DRAWER, MsgRepository.getFiscalMsg(PST_PRINTER_DRAWER,
					"Pst_ESCPOS_MoneyDrawerTitle", "Pst_ESCPOS_MoneyDrawerDesc", false));
			printerStatusMsgs.put(PST_PRINTER_OFFLINE, MsgRepository.getFiscalMsg(PST_PRINTER_OFFLINE,
					"PstPrinterOfflineTitle", "PstPrinterOfflineDesc", true));
		}
		return printerStatusMsgs;
	}

	protected void setPrinterStatusMsgs(Map<Integer,FiscalMessage> printerStatusMsgs) {
		this.printerStatusMsgs = printerStatusMsgs;
	}

	protected Map<Integer,FiscalMessage> getOfflineStatusMsgs() {
		if(offlineStatusMsgs == null) {
			offlineStatusMsgs = new HashMap<Integer, FiscalMessage>();
			offlineStatusMsgs.put(PST_OFFLINE_COVER, MsgRepository.getFiscalMsg(PST_OFFLINE_COVER,
					"PstPrinterCoverOpenTitle", "PstPrinterCoverOpenDesc", true));
			offlineStatusMsgs.put(PST_OFFLINE_FEED, MsgRepository.getFiscalMsg(PST_OFFLINE_FEED,
					"Pst_ESCPOS_PaperTitle", "Pst_ESCPOS_PaperFeedButtonDesc", false));
			offlineStatusMsgs.put(PST_OFFLINE_INTERRUPTED, MsgRepository.getFiscalMsg(PST_OFFLINE_INTERRUPTED,
					"Pst_ESCPOS_PrintingStoppedTitle", "Pst_ESCPOS_PrintingStoppedDesc", true));
			offlineStatusMsgs.put(PST_OFFLINE_ERROR, MsgRepository.getFiscalMsg(PST_OFFLINE_ERROR,
					"PstPrinterErrorTitle", "FstInvalidCommandDesc", true));
		}
		return offlineStatusMsgs;
	}

	protected void setOfflineStatusMsgs(Map<Integer,FiscalMessage> offlineStatusMsgs) {
		this.offlineStatusMsgs = offlineStatusMsgs;
	}

	protected Map<Integer,FiscalMessage> getErrorStatusMsgs() {
		if(errorStatusMsgs == null) {
			errorStatusMsgs = new HashMap<Integer, FiscalMessage>();
			errorStatusMsgs.put(PST_ERROR_CUTTER, MsgRepository.getFiscalMsg(PST_ERROR_CUTTER, "Pst_ESCPOS_CutterTitle",
					"Pst_ESCPOS_CutterDesc", true));
			errorStatusMsgs.put(PST_ERROR_UNRECOVERABLE, MsgRepository.getFiscalMsg(PST_ERROR_UNRECOVERABLE,
					"PstPrinterErrorTitle", "Pst_ESCPOS_UnrecoverableDesc", true));
			errorStatusMsgs.put(PST_ERROR_AUTORECOVERABLE, MsgRepository.getFiscalMsg(PST_ERROR_AUTORECOVERABLE,
					"PstPrinterErrorTitle", "Pst_ESCPOS_AutorecoverableDesc", false));
		}
		return errorStatusMsgs;
	}

	protected void setErrorStatusMsgs(Map<Integer,FiscalMessage> errorStatusMsgs) {
		this.errorStatusMsgs = errorStatusMsgs;
	}

	protected Map<Integer,FiscalMessage> getPaperStatusMsgs() {
		if(paperStatusMsgs == null) {
			paperStatusMsgs = new HashMap<Integer, FiscalMessage>();
			paperStatusMsgs.put(PST_PAPER_NEAR_END, MsgRepository.getFiscalMsg(PST_PAPER_NEAR_END, "Pst_ESCPOS_PaperTitle",
					"PstPaperAlmostOutDesc", false));
			paperStatusMsgs.put(PST_PAPER_END, MsgRepository.getFiscalMsg(PST_PAPER_END, "Pst_ESCPOS_PaperTitle",
					"PstPaperOutDesc", true));
		}
		return paperStatusMsgs;
	}

	protected void setPaperStatusMsgs(Map<Integer,FiscalMessage> paperStatusMsgs) {
		this.paperStatusMsgs = paperStatusMsgs;
	}
	
	@Override
	public String formatQuantity(BigDecimal quantity) {
		return quantity.toString();
	}

	@Override
	public String formatAmount(BigDecimal amount) {
		amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
		return amount.toString();
	}

	protected String truncDescription(String data) {
		return !Util.isEmpty(data, true) && data.length() > DESCRIPTION_MAX_LENGTH
				? data.substring(0, DESCRIPTION_MAX_LENGTH)
				: data;
	}
	
	protected String truncLine(String data) {
		return !Util.isEmpty(data, true) && data.length() > LINE_MAX_LENGTH ? data.substring(0, LINE_MAX_LENGTH) : data;
	}
	
	protected String fill(String data, int alignment, Integer fieldLength){
		return fillField(data, " ", alignment, fieldLength);
	}
	
	protected String fillDescription(String data, int alignment){
		return fillField(data, " ", alignment, DESCRIPTION_MAX_LENGTH);
	}
	
	protected String fillAmount(BigDecimal amt){
		return fillField(formatAmount(amt), " ", POS_RIGHT, LINE_MAX_LENGTH - DESCRIPTION_MAX_LENGTH);
	}
	
	protected String fillLine(String data, int alignment){
		return fillField(data, " ", alignment, LINE_MAX_LENGTH);
	}
	
	protected String fillField(String data, String fillCharacter, int alignment, Integer fieldLength){
		String newData = data != null ? data : "";
		int dataLength = newData.length();
		StringBuffer filling = new StringBuffer();
		while ((dataLength+filling.length()) < fieldLength) {
			filling.append(fillCharacter);
		}
		newData = (POS_LEFT == alignment ? newData
				+ filling.toString()
				: filling.toString() + newData);
		return newData.substring(0, fieldLength);
	}
	
	protected String createLine(String description, BigDecimal amount) {
		return fillDescription(description, DESCRIPTION_MAX_LENGTH) + fillAmount(amount);
	}
	
	/**
	 * Impresión de los datos de la compañía/organización parámetro
	 * @param coi
	 * @throws DocumentException
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	protected void printClientData(ClientOrgInfo coi) throws DocumentException, FiscalPrinterIOException, FiscalPrinterStatusError {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat dfh = new SimpleDateFormat("HH:mm");
		Timestamp now = Env.getTimestamp();
		execute(printLF(coi.getClientName()));
		if(!Util.isEmpty(coi.getCuit(), true)) {
			execute(printLF("C.U.I.T.:"+coi.getCuit()));
		}
		if(!Util.isEmpty(coi.getIIBB(), true)) {
			execute(printLF("Ing. Brutos:"+coi.getIIBB()));
		}
		if(!Util.isEmpty(coi.getAddress(), true)) {
			execute(printLF(coi.getAddress()));
		}
		String cityRegion = "";
		if(!Util.isEmpty(coi.getCity(), true)) {
			cityRegion = coi.getCity();
		}
		if(!Util.isEmpty(coi.getRegionName(), true)) {
			cityRegion += (!Util.isEmpty(cityRegion, true)?" - ":"")+coi.getRegionName();
		}
		if(!Util.isEmpty(cityRegion, true)) {
			execute(printLF(cityRegion));
		}
		
		if(coi.getActivityStartDate() != null) {
			execute(printLF("Inicio de Actividades: " + df.format(coi.getActivityStartDate())));
		}
		if(!Util.isEmpty(coi.getCategoriaIVA(), true)) {
			execute(printLF(coi.getCategoriaIVA()));
		}
		execute(printLF("Fecha: "+df.format(now)+" | Hora: "+dfh.format(now)));
		execute(printLF("------------------------------------------------"));
	}
	
	/**
	 * Imprimir la cabecera de los documentos
	 * 
	 * @param invoice documento a imprimir
	 * @throws DocumentException
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	protected void printHeader(Document invoice)
			throws DocumentException, FiscalPrinterIOException, FiscalPrinterStatusError {
		ClientOrgInfo coi = invoice.getClientOrgInfo();
		invoice.validate();
		printClientData(coi);
	}
	
	/**
	 * Cabecera del documento no fiscal si es que existe
	 * @param nfd
	 * @throws DocumentException
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	protected void printHeader(NonFiscalDocument nfd)
			throws DocumentException, FiscalPrinterIOException, FiscalPrinterStatusError {
		ClientOrgInfo coi = nfd.getClientOrgInfo();
		if(coi != null) {
			printClientData(coi);
		}
	}
	
	/**
	 * Impresión de las líneas del documento
	 * 
	 * @param document documento
	 * @throws DocumentException
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	protected void loadDocumentLineItems(Document document)
			throws DocumentException, FiscalPrinterIOException, FiscalPrinterStatusError {
		execute(printLF(fillLine("CANTIDAD/PRECIO UNIT.      %IVA", POS_LEFT)));
		execute(printLF(fillLine("DESCRIPCION      	         IMPORTE", POS_LEFT)));
		BigDecimal unitPrice, subtotal;
		for (DocumentLine item : document.getLines()) {
			unitPrice = Document.DOC_LETTER_A.equals(document.getLetter())?
					item.getUnitPriceNet():item.getAbsUnitPrice();
			subtotal = Document.DOC_LETTER_A.equals(document.getLetter())?
					item.getSubtotalNet():item.getSubtotal();
			execute(printLF(fill(item.getQuantity() + "/" + unitPrice, POS_LEFT, 28) + item.getIvaRate()));
			execute(printLF(fillDescription(item.getDescription(), DESCRIPTION_MAX_LENGTH)
					+ fillAmount(subtotal)));
			if(item.hasDiscount()) {
				DiscountLine discount = item.getDiscount();
				execute(setTextRight());
				execute(printLF(createLine(discount.getDescription(), discount.getAbsAmount())));
				execute(setTextLeft());
			}
		}
	}
	
	/**
	 * Impresión de los descuentos
	 * 
	 * @param document documento
	 * @throws DocumentException
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	protected void loadDocumentDiscounts(Document document)
			throws DocumentException, FiscalPrinterIOException, FiscalPrinterStatusError {
		execute(cmdFeed(1));
		//////////////////////////////////////////////////////////////
		// Se aplican las bonificaciones
		for (DiscountLine discount : document.getDocumentDiscounts()) {
			execute(setTextRight());
			execute(printLF(createLine(discount.getDescription(), discount.getAmount())));
			execute(setTextLeft());
		}
		
		//////////////////////////////////////////////////////////////
		// Se aplica el descuento general en caso de existir.
		if(document.hasGeneralDiscount()) {
			execute(setTextRight());
			DiscountLine generalDiscount = document.getGeneralDiscount();
			execute(printLF(createLine(generalDiscount.getDescription(), generalDiscount.getAmount())));
			execute(setTextLeft());
		}
	}
	
	/**
	 * Impresión de subtotal e impuestos automáticos en caso que sea comprobante A
	 * @param document
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 */
	private void loadAutomaticTaxes(Document document) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		if(Document.DOC_LETTER_A.equals(document.getLetter())) {
			execute(cmdFeed(1));
			execute(setTextRight());
			execute(printLF(truncDescription("Subtotal") + fillAmount(document.getNetAmount())));
			execute(setTextLeft());
			for (Tax tax : document.getTaxes()) {
				execute(setTextRight());
				execute(printLF(truncDescription("IVA "+tax.getRate().setScale(2)+" %") + fillAmount(tax.getAmt())));
				execute(setTextLeft());
			}
		}
	}
	
	/**
	 * Impresión de impuestos adicionales
	 * 
	 * @param document documento
	 * @throws FiscalPrinterStatusError
	 * @throws FiscalPrinterIOException
	 */
	private void loadOtherTaxes(Document document) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		// Se cargan los impuestos adicionales del documento.
		for (Tax otherTax : document.getOtherTaxes()) {
			if(otherTax.isPercepcion()){
				execute(setTextRight());
				execute(printLF(truncDescription(otherTax.getName()) + fillAmount(otherTax.getAmt())));
				execute(setTextLeft());
			}
		}
	}
	
	/**
	 * Impresión del cuerpo de la factura
	 * 
	 * @param invoice factura
	 * @throws DocumentException
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	protected void printBody(Invoice invoice)
			throws DocumentException, FiscalPrinterIOException, FiscalPrinterStatusError {
		Customer customer = invoice.getCustomer();
		execute(printLF("ORIGINAL"));
		execute(setBoldOn());
		execute(cmdPrintLine("FACTURA "));
		execute(printLF(invoice.getDocumentNo()));
		execute(printLF("------------------------------------------------"));
		execute(setBoldOff());
		if(!Util.isEmpty(invoice.getCae(), true)) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			execute(printLF("CAE: "+invoice.getCae()));
			execute(printLF("Vto. CAE: "+df.format(invoice.getCaeDueDate())));
			execute(printLF("------------------------------------------------"));
		}
		if(customer.getName() != null) {
			execute(printLF(customer.getName()));
		}
		if(customer.getLocation() != null) {
			execute(printLF(customer.getLocation()));
		}
		if(customer.getIdentificationNumber() != null) {
			execute(printLF(customer.getIdentificationName()+": "+customer.getIdentificationNumber()));
		}
		execute(printLF(customer.getIvaResponsibilityName()));
		execute(printLF("------------------------------------------------"));
		
		//////////////////////////////////////////////////////////////
		// Se carga el número de remito asignado a la factura
		// en caso de existir.
		// Comando: @SetEmbarkNumber
		if(invoice.hasPackingSlipNumber())
			execute(printLF("REMITO: "+invoice.getPackingSlipNumber()));

		setLastDocumentNo(invoice.getDocumentNo());
		setCancelAllowed(true);
		setDocumentOpened(true);
		
		//////////////////////////////////////////////////////////////		
		// Se cargan los ítems de la factura.
		loadDocumentLineItems(invoice);
		
		//////////////////////////////////////////////////////////////
		// Se cargan los impuestos automáticos
		loadAutomaticTaxes(invoice);
		
		//////////////////////////////////////////////////////////////
		// Se cargan los descuentos de la factura.
		loadDocumentDiscounts(invoice);
		
		//////////////////////////////////////////////////////////////
		// Se cargan los impuestos adicionales de la factura
		loadOtherTaxes(invoice);
		
		//////////////////////////////////////////////////////////////
		// Se imprime el total
		execute(cmdFeed(1));
		execute(setTextRight());
		execute(setTextDoubleSize());
		execute(setBoldOn());
		execute(printLF("TOTAL     $     "+	formatAmount(invoice.getTotal())));
		execute(setBoldOff());
		execute(setTextNormalSize());
		execute(setTextLeft());
		
		//////////////////////////////////////////////////////////////
		// Se ingresan los pagos realizados por el comprador.
		boolean hasCashPayments = false;
		BigDecimal cashRetirementAmt = BigDecimal.ZERO;
		BigDecimal paidAmt = BigDecimal.ZERO;
		execute(printLF("Recibimos:"));
		execute(setTextRight());
		for (Payment payment : invoice.getPayments()) {
			hasCashPayments = hasCashPayments || payment.isCash()
					|| payment.isCashRetirement();
			if(!payment.isCashRetirement()){
				execute(printLF(createLine(fillDescription(payment.getDescription(), POS_LEFT), payment.getAmount())));
				paidAmt = paidAmt.add(payment.getAmount());
			}
			else{
				cashRetirementAmt = cashRetirementAmt.add(payment.getAmount());
			}
			setCancelAllowed(false);
		}
		execute(setTextLeft());
		
		//////////////////////////////////////////////////////////////
		// Abrir el cajón de dinero
		// El cajón de dinero se abre si tenemos cambio en la factura,
		// existen pagos en efectivo ó si el último pago agregado tiene
		// vuelto. A menos que la factura contenga el flag positivo para
		// siempre abrir el cajón. 
		if (invoice.isAlwaysOpenDrawer()
				|| hasCashPayments
				|| invoice.getChangeAmt().compareTo(BigDecimal.ZERO) > 0) {
			execute(cmdOpenDrawer());
		}
		
		// Agrego los nuevos datos de la cola de impresión, previo a eliminar lo de la cola
		// Primeramente se imprime el extracash si es que existe. 
		if(!Util.isEmpty(cashRetirementAmt, true)){
			execute(setTextRight());
			execute(printLF("Retiro Efectivo Tarjeta $ " + cashRetirementAmt));
			execute(setTextLeft());
		}
		
		// Se agrega el CAMBIO
		execute(setTextRight());
		execute(setTextDoubleSize());
		execute(setBoldOn());
		execute(printLF("CAMBIO     $     "+formatAmount(invoice.getChangeAmt())));
		execute(setBoldOff());
		execute(setTextNormalSize());
		execute(setTextLeft());
	}
	
	/**
	 * Impresión del cuerpo de la ND
	 * 
	 * @param debitNote ND
	 * @throws DocumentException
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	protected void printBody(DebitNote debitNote)
			throws DocumentException, FiscalPrinterIOException, FiscalPrinterStatusError {
		Customer customer = debitNote.getCustomer();
		execute(printLF("ORIGINAL"));
		execute(cmdPrintLine("NOTA DE DEBITO "));
		execute(setBoldOn());
		execute(printLF(debitNote.getDocumentNo()));
		execute(setBoldOff());
		execute(printLF("------------------------------------------------"));
		if(!Util.isEmpty(debitNote.getCae(), true)) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			execute(printLF("CAE: "+debitNote.getCae()));
			execute(printLF("Vto. CAE: "+df.format(debitNote.getCaeDueDate())));
			execute(printLF("------------------------------------------------"));
		}
		if(customer.getName() != null) {
			execute(printLF(customer.getName()));
		}
		if(customer.getLocation() != null) {
			execute(printLF(customer.getLocation()));
		}
		if(customer.getIdentificationNumber() != null) {
			execute(printLF(customer.getIdentificationName()+": "+customer.getIdentificationNumber()));
		}
		execute(printLF(customer.getIvaResponsibilityName()));
		execute(printLF("------------------------------------------------"));
		
		//////////////////////////////////////////////////////////////
		// Se carga el número de remito asignado a la factura
		// en caso de existir.
		if(debitNote.hasPackingSlipNumber())
			execute(printLF("REMITO: "+debitNote.getPackingSlipNumber()));

		setLastDocumentNo(debitNote.getDocumentNo());
		setCancelAllowed(true);
		setDocumentOpened(true);
		
		//////////////////////////////////////////////////////////////		
		// Se cargan los ítems de la factura.
		loadDocumentLineItems(debitNote);
		
		//////////////////////////////////////////////////////////////
		// Se cargan los impuestos automáticos
		loadAutomaticTaxes(debitNote);
		
		//////////////////////////////////////////////////////////////
		// Se cargan los descuentos de la factura.
		loadDocumentDiscounts(debitNote);
		
		//////////////////////////////////////////////////////////////
		// Se cargan los impuestos adicionales de la factura
		loadOtherTaxes(debitNote);
		
		//////////////////////////////////////////////////////////////
		// Se calcula el subtotal.
		execute(setTextRight());
		execute(setTextDoubleSize());
		execute(setBoldOn());
		execute(printLF("TOTAL     $     "+formatAmount(debitNote.getTotal())));
		execute(setBoldOff());
		execute(setTextNormalSize());
		execute(setTextLeft());
	}

	/**
	 * Impresión del cuerpo de la NC
	 * 
	 * @param creditNote NC
	 * @throws DocumentException
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	protected void printBody(CreditNote creditNote)
			throws DocumentException, FiscalPrinterIOException, FiscalPrinterStatusError {
		Customer customer = creditNote.getCustomer();
		execute(printLF("ORIGINAL"));
		execute(cmdPrintLine("NOTA DE CREDITO "));
		execute(setBoldOn());
		execute(printLF(creditNote.getDocumentNo()));
		execute(setBoldOff());
		execute(printLF("------------------------------------------------"));
		if(!Util.isEmpty(creditNote.getCae(), true)) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			execute(printLF("CAE: "+creditNote.getCae()));
			execute(printLF("Vto. CAE: "+df.format(creditNote.getCaeDueDate())));
			execute(printLF("------------------------------------------------"));
		}
		if(customer.getName() != null) {
			execute(printLF(customer.getName()));
		}
		if(customer.getLocation() != null) {
			execute(printLF(customer.getLocation()));
		}
		if(customer.getIdentificationNumber() != null) {
			execute(printLF(customer.getIdentificationName()+": "+customer.getIdentificationNumber()));
		}
		execute(printLF(customer.getIvaResponsibilityName()));
		execute(printLF("------------------------------------------------"));
		
		//////////////////////////////////////////////////////////////
		// Se carga el número de remito asignado a la factura
		// en caso de existir.
		execute(printLF("COMPROBANTE ORIGINAL: "
				+ (creditNote.getOriginalLetter() == null ? "" : creditNote.getOriginalLetter())
				+ creditNote.getOriginalDocumentNo()));
		execute(printLF("------------------------------------------------"));
		
		setLastDocumentNo(creditNote.getDocumentNo());
		setCancelAllowed(true);
		setDocumentOpened(true);
		
		//////////////////////////////////////////////////////////////		
		// Se cargan los ítems de la factura.
		loadDocumentLineItems(creditNote);
		
		//////////////////////////////////////////////////////////////
		// Se cargan los impuestos automáticos
		loadAutomaticTaxes(creditNote);
		
		//////////////////////////////////////////////////////////////
		// Se cargan los descuentos de la factura.
		loadDocumentDiscounts(creditNote);
		
		//////////////////////////////////////////////////////////////
		// Se cargan los impuestos adicionales de la factura
		loadOtherTaxes(creditNote);
		
		//////////////////////////////////////////////////////////////
		// Se calcula el subtotal.
		execute(setTextRight());
		execute(setTextDoubleSize());
		execute(setBoldOn());
		execute(printLF("TOTAL     $     "+formatAmount(creditNote.getTotal())));
		execute(setBoldOff());
		execute(setTextNormalSize());
		execute(setTextLeft());
	}

	
	/**
	 * Impresión del pie de la impresión
	 * 
	 * @param invoice documento
	 * @throws DocumentException
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	protected void printFooter(Document invoice)
			throws DocumentException, FiscalPrinterIOException, FiscalPrinterStatusError {
		for (int i = 0; i < invoice.getFooterObservations().size(); i++) {
			execute(printLF(invoice.getFooterObservations().get(i)));
		}
		// Corte del papel
		execute(cutFull());
	}
	
	/**
	 * Impresión del pie de la impresión
	 * 
	 * @param invoice documento
	 * @throws DocumentException
	 * @throws FiscalPrinterIOException
	 * @throws FiscalPrinterStatusError
	 */
	protected void printFooter(NonFiscalDocument nfd)
			throws DocumentException, FiscalPrinterIOException, FiscalPrinterStatusError {
		for (int i = 0; i < nfd.getFooterObservations().size(); i++) {
			execute(printLF(nfd.getFooterObservations().get(i)));
		}
		// Corte del papel
		execute(cutFull());
	}
	
	@Override
	public void printDocument(Invoice invoice)
			throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException {
		setAskMoment(true);
		setCancelAllowed(false);
		printHeader(invoice);
		printBody(invoice);
		printFooter(invoice);
		try {
			getFiscalComm().finishPrint();
		} catch(Exception e) {
			throw new FiscalPrinterIOException(e.getMessage());
		}
		setDocumentOpened(false);
		setAskMoment(false);
		firePrintEnded();
	}

	@Override
	public void printDocument(CreditNote creditNote)
			throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException {
		setAskMoment(true);
		setCancelAllowed(false);
		printHeader(creditNote);
		printBody(creditNote);
		printFooter(creditNote);
		try {
			getFiscalComm().finishPrint();
		} catch(Exception e) {
			throw new FiscalPrinterIOException(e.getMessage());
		}
		setDocumentOpened(false);
		setAskMoment(false);
		firePrintEnded();
	}

	@Override
	public void printDocument(DebitNote debitNote)
			throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException {
		setAskMoment(true);
		setCancelAllowed(false);
		printHeader(debitNote);
		printBody(debitNote);
		printFooter(debitNote);
		try {
			getFiscalComm().finishPrint();
		} catch(Exception e) {
			throw new FiscalPrinterIOException(e.getMessage());
		}
		setDocumentOpened(false);
		setAskMoment(false);
		firePrintEnded();
	}

	@Override
	public void printDocument(NonFiscalDocument nonFiscalDocument)
			throws FiscalPrinterStatusError, FiscalPrinterIOException, DocumentException {
		setDocumentOpened(true);
		printHeader(nonFiscalDocument);
		for (String line : nonFiscalDocument.getLines()) {
			execute(cmdPrintLineFeed(line, 1));
		}
		printFooter(nonFiscalDocument);
		
		try {
			getFiscalComm().finishPrint();
		} catch(Exception e) {
			throw new FiscalPrinterIOException(e.getMessage());
		}
		
		setDocumentOpened(false);
		firePrintEnded();
	}

	
	
	@Override
	public void fiscalClose(String type) throws FiscalPrinterStatusError, FiscalPrinterIOException {
		// No existe cierre fiscal para las impresoras térmicas 
	}

	@Override
	public String getLastDocumentNoPrinted(String documentType, String letra)
			throws FiscalPrinterStatusError, FiscalPrinterIOException {
		// No posee memoria fiscal
		return null;
	}

	@Override
	public int getAllowedPaymentQty() {
		// Es posible imprimir todos los pagos ya que no existen restricciones
		return 1000;
	}

	@Override
	public void openDrawer() throws FiscalPrinterIOException {
		execute(cmdOpenDrawer());
		try {
			getFiscalComm().finishPrint();
		} catch(Exception e) {
			throw new FiscalPrinterIOException(e.getMessage());
		}
	}

	@Override
	public void cancelCurrentDocument() throws FiscalPrinterIOException, FiscalPrinterStatusError {
		// No existe cancelaciones de documentos ya que no existe memoria fiscal
	}

	@Override
	public FiscalClosingResponseDTO decodeClosingResponse(FiscalPacket closingResponse) {
		// No existe este comando en la impresora ya que no poseen memoria fiscal
		return null;
	}

	@Override
	public void getInitData() throws FiscalPrinterStatusError, FiscalPrinterIOException {
		// No existe este comando en la impresora ya que no poseen memoria fiscal  
	}

	@Override
	public FiscalInitData decodeInitData(FiscalPacket getInitDataResponse) {
		// No existe este comando en la impresora ya que no poseen memoria fiscal
		return null;
	}

	@Override
	protected FiscalPacket createFiscalPacket() {
		return new ESCPOSFiscalPacket(encoding, 1990, this);
	}

	@Override
	public FiscalPacket executeCmd(FiscalPacket command) throws FiscalPrinterIOException, FiscalPrinterStatusError {
		FiscalPacket response = createFiscalPacket();
		
		// Se guarda el comando como el último ejecutado.
		setLastRequest(command);
		setLastResponse(null);
		
		try {
			// Se envía el comando a la interfaz de comunicación para
			// ser ejecutado.
			setLastResponse(executeOnly(command, response));
			
		} catch (IOException e) {
			throw new FiscalPrinterIOException(e.getMessage(), command, response);
		}

		// Se chequea el status devuelto por la impresora.
		boolean statusChanged = checkStatus(command, response);
		//boolean statusChanged = false;
		
		// Si se produjeron cambios en el estado de la impresora se dispara
		// el evento correspondiente.
		if(statusChanged)
			fireStatusChanged(command, response);

		// Si la impresora quedó en estado de error entonces se lanza una
		// excepción.
		if(getMessages() != null && getMessages().hasErrors()) {
			throw new FiscalPrinterStatusError(command, response, getMessages());
		}
		
		// Se informa al manejador que el comando se ejecutó satisfactoriamente.
		fireCommandExecuted(command, response);
		
		return response;

	}

	protected FiscalPacket executeOnly(FiscalPacket command, FiscalPacket response) throws IOException{
		getFiscalComm().execute(command, response);
		return response;
	}
	
	protected boolean checkStatus(FiscalPacket command, FiscalPacket response) throws FiscalPrinterIOException {
		FiscalPacket statusResponse = createFiscalPacket();
		FiscalPacket offlineResponse = createFiscalPacket();
		FiscalPacket errorResponse = createFiscalPacket();
		FiscalPacket errorRealResponse = createFiscalPacket();
		FiscalPacket paperResponse = createFiscalPacket();
		FiscalPacket paperRealResponse = createFiscalPacket();
		// Ejecutar el comando que permite saber en tiempo real el estado de la impresora
		try {
			//statusResponse = executeOnly(cmdGetStatus(STATUS_PRINTER), statusResponse);
			offlineResponse = executeOnly(cmdGetStatus(STATUS_OFFLINE), offlineResponse);
			//errorResponse = executeOnly(cmdGetStatus(STATUS_ERROR), errorResponse);
			//paperResponse = executeOnly(cmdGetStatus(STATUS_PAPER), paperResponse);
			errorResponse = executeOnly(cmdASB(4), errorResponse);
			errorRealResponse = executeOnly(cmdGetStatus(STATUS_ERROR), errorRealResponse);
			paperResponse = executeOnly(cmdASB(8), paperResponse);
			paperRealResponse = executeOnly(cmdGetStatus(STATUS_PAPER), paperRealResponse);
		} catch (Exception e) {
			// Se puede producir un error de formato al querer obtener los estados
			// de la respuesta. Puede suceder que solo se reciba una parte de la
			// respuesta.
			throw new FiscalPrinterIOException(MsgRepository.get("ResponseFormatError"), getLastRequest(), response);
		}
		FiscalMessages msgs = new FiscalMessages();
		// Verificar los errores obtenidos
		// Se chequea el estado del controlador fiscal.
		/*addFiscalMessageErrors(statusResponse.getPrinterStatus(), getPrinterStatusCodes(), getPrinterStatusMsgs(),msgs);
		addFiscalMessageErrors(offlineResponse.getPrinterStatus(), getOfflineStatusCodes(), getOfflineStatusMsgs(), msgs);
		addFiscalMessageErrors(errorResponse.getPrinterStatus(), getErrorStatusCodes(), getErrorStatusMsgs(), msgs);
		addFiscalMessageErrors(paperResponse.getPrinterStatus(), getPaperStatusCodes(), getPaperStatusMsgs(), msgs);
		*/
		// Se setean los mensajes de la impresora.
		setMessages(msgs);
		
		return getMessages().hasErrors();
	}
	
	/**
	 * Verificar el valor de estado devuelto por la impresora, hace matching con los
	 * posibles mensajes de error y agrega los mismos al contenedor parámetro
	 * 
	 * @param statusCode  código de estado devuelto por la impresora
	 * @param statusCodes códigos de estado
	 * @param statusMsgs  mensajes de estado
	 * @param container   contenedor de mensajes
	 */
	private void addFiscalMessageErrors(int statusCode, int[] statusCodes, Map<Integer,FiscalMessage> statusMsgs, FiscalMessages container) {
		for(int i = 0; i < statusCodes.length; i++) {
			if(statusCode == statusCodes[i]) {
				FiscalMessage msg = statusMsgs.get(statusCode);
				if(msg != null) {
					container.add(msg);
				}
			}
		}
	}
	
	@Override
	public FiscalPacket cmdPrintLineFeed(String line, int n) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(line);
		fp.add(ESC);
		fp.add("d");
		fp.add(n);
		return fp;
	}

	@Override
	public FiscalPacket cmdPrintLine(String line) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(line);
		return fp;
	}

	@Override
	public FiscalPacket cmdFeed(int n) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(ESC);
		fp.add("d");
		fp.add(n);
		return fp;
	}

	public FiscalPacket printLF(String line) {
		return cmdPrintLineFeed(line, 1);
	}
	
	@Override
	public FiscalPacket cmdSetEncoding(int encodes) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(ESC);
		fp.add("t");
		fp.add(encodes);
		return fp;
	}

	@Override
	public FiscalPacket cmdPulsePin(int pin) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(ESC);
		fp.add("p");
		fp.add(pin);
		fp.add(50);
		fp.add(75);
		return fp;
	}

	@Override
	public FiscalPacket cmdGetStatus(int statusCode) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(DLE);
		fp.add(EOT);
		fp.add(statusCode);
		return fp;
	}
	
	@Override
	public FiscalPacket cmdASB(int statusCode) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(GS);
		fp.add("a");
		fp.add(statusCode);
		return fp;
	}

	@Override
	public FiscalPacket cmdCut(int cutMode) {
		FiscalPacket fp = cmdFeed(2);
		fp.add(GS);
		fp.add("V");
		fp.add(cutMode);
		return fp;
	}

	public FiscalPacket cutFull() {
		return cmdCut(CUT_FULL);
	}
	
	public FiscalPacket cutPartial() {
		return cmdCut(CUT_PARTIAL);
	}
	
	@Override
	public FiscalPacket cmdSetCharacterSize(int size) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(GS);
		fp.add("!");
		fp.add(size);
		return fp;
	}
	
	public FiscalPacket setTextNormalSize() {
		return cmdSetCharacterSize(TEXT_NORMAL_SIZE);
	}
	
	public FiscalPacket setTextDoubleSize() {
		return cmdSetCharacterSize(TEXT_DOUBLE_SIZE);
	}
	
	@Override
	public FiscalPacket cmdSetTextPosition(int position) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(ESC);
		fp.add("a");
		fp.add(position);
		return fp;
	}
	
	public FiscalPacket setTextLeft() {
		return cmdSetTextPosition(POS_LEFT);
	}
	
	public FiscalPacket setTextCenter() {
		return cmdSetTextPosition(POS_CENTER);
	}
	
	public FiscalPacket setTextRight() {
		return cmdSetTextPosition(POS_RIGHT);
	}

	@Override
	public FiscalPacket cmdSetUnderlineMode(int underlineMode) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(ESC);
		fp.add("-");
		fp.add(underlineMode);
		return fp;
	}
	
	public FiscalPacket setUnderlineOff() {
		return cmdSetUnderlineMode(UNDERLINE_OFF);
	}
	
	public FiscalPacket setUnderlineOn() {
		return cmdSetUnderlineMode(UNDERLINE_ONE_DOT);
	}

	@Override
	public FiscalPacket cmdSetEmphasizeMode(int emphasizeMode) {
		FiscalPacket fp = createFiscalPacket();
		fp.add(ESC);
		fp.add("E");
		fp.add(emphasizeMode);
		return fp;
	}
	
	public FiscalPacket setBoldOff() {
		return cmdSetEmphasizeMode(0);
	}
	
	public FiscalPacket setBoldOn() {
		return cmdSetEmphasizeMode(1);
	}

	@Override
	public FiscalPacket cmdOpenDrawer() {
		// El cajón del dinero probablemente se encuentre en el pin 2
		return cmdPulsePin(PIN_2);
	}

	protected int[] getPrinterStatusCodes() {
		return printerStatusCodes;
	}

	protected void setPrinterStatusCodes(int[] printerStatusCodes) {
		this.printerStatusCodes = printerStatusCodes;
	}

	protected int[] getOfflineStatusCodes() {
		return offlineStatusCodes;
	}

	protected void setOfflineStatusCodes(int[] offlineStatusCodes) {
		this.offlineStatusCodes = offlineStatusCodes;
	}

	protected int[] getErrorStatusCodes() {
		return errorStatusCodes;
	}

	protected void setErrorStatusCodes(int[] errorStatusCodes) {
		this.errorStatusCodes = errorStatusCodes;
	}

	protected int[] getPaperStatusCodes() {
		return paperStatusCodes;
	}

	protected void setPaperStatusCodes(int[] paperStatusCodes) {
		this.paperStatusCodes = paperStatusCodes;
	}

	@Override
	public int getFooterTrailerMaxLength() {
		// TODO Auto-generated method stub
		return 0;
	}

}
