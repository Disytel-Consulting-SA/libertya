package org.openXpertya.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

/**
 * Generador de Asignación. Esta clase es un Wrapper & Helper para la creación de 
 * MAllocationHdr con sus líneas.
 *  
 * @author Franco Bonafine - Disytel
 * @date 26/05/2009
 * @version 0.2
 * <br><em><b><p>En esta versión de la clase</p></b></em>	
 * <ul>
 * 		<li>Se asume que los montos a imputar de los documentos están expresados en
 * 			la misma moneda del encabezado de la asignación.</li>
 *		<li>No se validan los signos de los documentos</li>
 *		<li>No hay soporte para la asignación de montos Discount, WriteOff, OverUnder
 *			de las líneas generados. Estos valores siempre serán 0.0</li>
 *		<li>No se validan los montos pendientes de los documentos</li>   
 * </ul>		
 */
public class AllocationGenerator {

	/**
	 * Tipo de Documento de la Asignación
	 * Estos son los tipos de documento válidos que puede contener una asignación,
	 * ya sea como crédito o como débito.
	 */
	public enum AllocationDocumentType {
		PAYMENT,
		CASH_LINE,
		INVOICE
	}
	
	/** Transacción de BD utilizada para realizar consultas y creaciones */
	private String trxName = null;
	/** Contexto de la aplicación */
	private Properties ctx = Env.getCtx();
	/** Lista de débitos de la Asignación */
	private List<Document> debits;
	/** Lista de créditos de la Asignación */
	private List<Document> credits;

	/** Encabezado de la Asignación */
	private MAllocationHdr allocationHdr;
	
	/** Locale AR activo? */
	public final boolean LOCALE_AR_ACTIVE = CalloutInvoiceExt.ComprobantesFiscalesActivos();
	
	/**
	 * Constructor por defecto. Es privado dado que se
	 * deben utilizar alguno de los constructores que requieren
	 * mayor información para instanciar el generador.
	 */
	private AllocationGenerator() {
		super();
		this.debits = new ArrayList<Document>();
		this.credits = new ArrayList<Document>();
	}
	
	/**
	 * Constructor del Generador de Asignaciones.
	 * Crea un generador a partir de un encabezado de asignación existente.
	 * Tanto las líneas de asignación creadas como la actualización de datos del encabezado
	 * surtiran efecto sobre este encabezado existente. 
	 * @param allocationHdr Encabezado de la asignación. Las líneas de asignación
	 * serán creadas para este encabezado.
	 * @param trxName Transacción de BD a utilizar por el generador
	 */
	public AllocationGenerator(Properties ctx, MAllocationHdr allocationHdr, String trxName) {
		this();
		this.ctx = ctx;
		this.allocationHdr = allocationHdr;
		this.trxName = trxName;
	}

	/**
	 * Constructor del Generador de Asignaciones.
	 * Crea un generador de asignación donde el encabezado de la asignación deberá ser creado
	 * por este generador mediante la invocación de {@link #createAllocationHdr()}. Luego,
	 * las líneas de asignación creadas como la actualización de datos del encabezado
	 * surtiran efecto sobre este encabezado recientemente creado por el generador. 
	 * @param ctx Contexto para la creación de objetos
	 * @param trxName Transacción de BD a utilizar por el generador
	 */
	public AllocationGenerator(Properties ctx, String trxName) {
		this(ctx, null, trxName);
	}

	/**
	 * Agrega un documento a la lista de débitos de la Asignación.
	 * En caso de que el documento ya se encuentre en la lista de débitos
	 * (mismo Tipo e ID), entonces se suma el <code>amount</code> al documento
	 * existente en la lista de débitos
	 * @param docID ID del documento a agregar
	 * @param amount Monto a imputar expresado en la moneda del documento
	 * @param docType Tipo del documento
	 * @return Este <code>AllocationGenerator</code>
	 */
	public AllocationGenerator addDebitDocument(int docID, BigDecimal amount, AllocationDocumentType docType) {
		return addDocument(getDebits(), docID, amount, docType);
	}
	
	/**
	 * Agrega un documento a la lista de débitos de la Asignación.
	 * En caso de que el documento ya se encuentre en la lista de débitos
	 * (mismo Tipo e ID), entonces se suma el <code>amount</code> al documento
	 * existente en la lista de débitos
	 * @param document documento
	 * @return Este <code>AllocationGenerator</code>
	 */
	public AllocationGenerator addDebitDocument(Document document) {
		return addDocument(getDebits(), document);
	}

	/**
	 * Agrega una factura a la lista de débitos de la Asignación.<br>
	 * Equivale a invocar: <code>addDebitDocument(invoiceID, amount, PODocumentType.INVOICE)</code>
	 * @see AllocationGenerator#addDebitDocument(int, BigDecimal, AllocationDocumentType)
	 */
	public AllocationGenerator addDebitInvoice(int invoiceID, BigDecimal amount) {
		return addDebitDocument(invoiceID, amount, AllocationDocumentType.INVOICE);
	}

	/**
	 * Agrega un pago a la lista de débitos de la Asignación.<br>
	 * Equivale a invocar: <code>addDebitDocument(paymentID, amount, PODocumentType.PAYMENT)</code>
	 * @see AllocationGenerator#addDebitDocument(int, BigDecimal, AllocationDocumentType)
	 */
	public AllocationGenerator addDebitPayment(int paymentID, BigDecimal amount) {
		return addDebitDocument(paymentID, amount, AllocationDocumentType.PAYMENT);
	}
	
	/**
	 * Agrega una línea de caja a la lista de débitos de la Asignación.<br>
	 * Equivale a invocar: <code>addDebitDocument(cashLineID, amount, PODocumentType.CASH_LINE)</code>
	 * @see AllocationGenerator#addDebitDocument(int, BigDecimal, AllocationDocumentType)
	 */
	public AllocationGenerator addDebitCashLine(int cashLineID, BigDecimal amount) {
		return addDebitDocument(cashLineID, amount, AllocationDocumentType.CASH_LINE);
	}
	
	/**
	 * Agrega un documento a la lista de créditos de la Asignación.
	 * En caso de que el documento ya se encuentre en la lista de créditos
	 * (mismo Tipo e ID), entonces se suma el <code>amount</code> al documento
	 * existente en la lista de débitos
	 * @param docID ID del documento a agregar
	 * @param amount Monto a imputar expresado en la moneda del documento
	 * @param docType Tipo del documento
	 * @return Este <code>PaymentOrderGenerator</code>
	 */
	public AllocationGenerator addCreditDocument(int docID, BigDecimal amount, AllocationDocumentType docType) {
		return addDocument(getCredits(), docID, amount, docType);
	}
	
	/**
	 * Agrega un documento a la lista de créditos de la Asignación.
	 * En caso de que el documento ya se encuentre en la lista de créditos
	 * (mismo Tipo e ID), entonces se suma el <code>amount</code> al documento
	 * existente en la lista de débitos
	 * @param document documento
	 * @return Este <code>PaymentOrderGenerator</code>
	 */
	public AllocationGenerator addCreditDocument(Document document) {
		return addDocument(getCredits(), document);
	}

	/**
	 * Agrega una factura a la lista de créditos de la Asignación.<br>
	 * Equivale a invocar: <code>addCreditDocument(invoiceID, amount, PODocumentType.INVOICE)</code>
	 * @see AllocationGenerator#addCreditDocument(int, BigDecimal, AllocationDocumentType)
	 */
	public AllocationGenerator addCreditInvoice(int invoiceID, BigDecimal amount) {
		return addCreditDocument(invoiceID, amount, AllocationDocumentType.INVOICE);
	}

	/**
	 * Agrega un pago a la lista de créditos de la Asignación.<br>
	 * Equivale a invocar: <code>addCreditDocument(paymentID, amount, PODocumentType.PAYMENT)</code>
	 * @see AllocationGenerator#addCreditDocument(int, BigDecimal, AllocationDocumentType)
	 */
	public AllocationGenerator addCreditPayment(int paymentID, BigDecimal amount) {
		return addCreditDocument(paymentID, amount, AllocationDocumentType.PAYMENT);
	}
	
	/**
	 * Agrega una línea de caja a la lista de créditos de la Asignación.<br>
	 * Equivale a invocar: <code>addCreditDocument(cashLineID, amount, PODocumentType.CASH_LINE)</code>
	 * @see AllocationGenerator#addCreditDocument(int, BigDecimal, AllocationDocumentType)
	 */
	public AllocationGenerator addCreditCashLine(int cashLineID, BigDecimal amount) {
		return addCreditDocument(cashLineID, amount, AllocationDocumentType.CASH_LINE);
	}
	
	/**
	 * Crea un nuevo encabezado de asignación seteando la mayoría de sus atributos por defecto
	 * a partir de los valores del contexto.
	 * @param allocationType tipo de asignación del encabezado <code>X_C_AllocationHdr.ALLOCATIONTYPE_XXX</code>
	 * @throws IllegalStateException si el generador fue instanciado mediante el constructor
	 * que permite asociar un MAllocationHdr existente.
	 * @throws AllocationGeneratorException cuando se produce un error al guardar el nuevo
	 * encabezado de asignación.
	 * @return el <code>MAllocationHdr</code> recientemente creado. (Accesible también
	 * invocando el método {@link #getAllocationHdr()}.
	 */
	public MAllocationHdr createAllocationHdr(String allocationType) throws AllocationGeneratorException {
		MAllocationHdr newAllocationHdr = null;
		// No se permite crear un nuevo encabezado si ya fue asignado uno mediante el
		// constructor que permite asociar un Hdr a este generador.
		if (getAllocationHdr() != null)
			throw new IllegalStateException("This generator has an Allocation Header configured");
		
		// Se obtienen valores del contexto
		int clientCurrencyID = Env.getContextAsInt(getCtx(), "$C_Currency_ID"); // Moneda de la compañía
		Timestamp systemDate = Env.getContextAsDate(getCtx(), "#Date");         // Fecha actual
		// Se asignan los valores por defecto requeridos al encabezado
		newAllocationHdr = new MAllocationHdr(getCtx(), 0, getTrxName());
		newAllocationHdr.setAllocationType(allocationType);
		newAllocationHdr.setC_Currency_ID(clientCurrencyID);
		newAllocationHdr.setDateAcct(systemDate);
		newAllocationHdr.setDateTrx(systemDate);
		// Se guarda el nuevo encabezado.
		saveAllocationHdr(newAllocationHdr);
		setAllocationHdr(newAllocationHdr);
		return newAllocationHdr;
	}
	
	/**
	 * Crea un nuevo encabezado de asignación con tipo <code>MAllocationHdr.ALLOCATIONTYPE_Manual</code>.
	 * La invocación a este método provoca el mismo efecto que la invocación a
	 * <code>createAllocationHdr(MAllocationHdr.ALLOCATIONTYPE_Manual)</code>
	 * @see #createAllocationHdr(String)
	 */
	public MAllocationHdr createAllocationHdr() throws AllocationGeneratorException {
		return createAllocationHdr(MAllocationHdr.ALLOCATIONTYPE_Manual);
	}
	
	/**
	 * Genera las líneas de asignación correspondientes al encabezado de asignación
	 * configurado, a partir de los documentos de débitos y créditos agregados al
	 * generador.
	 * @throws AllocationGeneratorException cuando existe un problema en la creación de 
	 * las líneas de asignación.
	 */
	public void generateLines() throws AllocationGeneratorException {	
		try {
			generateDebitCreditExchangeDifference();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AllocationGeneratorException(e.toString());
		}
		
		// Validaciones requeridas antes de crear las líneas de asignación
		validate();

		// Se ordenan las listas de débitos y créditos según el monto a imputar
		sortDocuments(getDebits());
		sortDocuments(getCredits());
		
		// Si se configuraron débitos y créditos se deben crear líneas que imputen 
		// los documentos de cada lista, según los montos ingresados de cada uno.
		if (hasDebits() && hasCredits()) {
			generateImputationLines();
		// Si solo existen débitos o créditos (pero no ambos), entonces se crean líneas
		// de asignación parciales.	
		} else {
			generatePartialLines();
		}
		
		// En cualquiera de los dos casos de determina cuales de los créditos
		// son retenciones y se asigna el monto total de retención al encabezado
		// de la imputación.
		setRetentionAmount();
		// Se asigna el monto de aprobación (código extraído de VOrdenPagoModel).
		getAllocationHdr().setApprovalAmt(
			getAllocationHdr().getGrandTotal()
			.subtract(getAllocationHdr().getRetencion_Amt())
		);
	}
	
	/**
	 * Completa el encabezado de asignación contenido en este generador.
	 * Luego guarda los cambios realizados mediante <code>MAllocationHdr.save()</code>
	 * @throws AllocationGeneratorException cuando se produce un error al
	 * completar o guardar el encabezado.
	 */
	public void completeAllocation() throws AllocationGeneratorException {
		String errorMsg = null;
		// Se intenta procesar (Completar) la asignación
		if (!getAllocationHdr().processIt(DocAction.ACTION_Complete)) {
			errorMsg = Msg.parseTranslation(getCtx(), getAllocationHdr().getProcessMsg());
		// Luego se intentan guardar los cambios 
		} else if (!getAllocationHdr().save() && errorMsg == null) {
			errorMsg = CLogger.retrieveErrorAsString();
		}
		// Finalmente se dispara una excepción si se produjo algún error al 
		// completar o guardar.
		if (errorMsg != null) {
			throw new AllocationGeneratorException(errorMsg);
		}
	}
	
	/**
	 * @return the allocationHdr
	 */
	public MAllocationHdr getAllocationHdr() {
		return allocationHdr;
	}
	
	/**
	 * @return Devuelve la suma de los montos de imputación de los débitos.
	 * @throws AllocationGeneratorException 
	 */
	public BigDecimal getDebitsAmount() throws AllocationGeneratorException {
		return getDocumentsAmount(getDebits());
	}

	/**
	 * @return Devuelve la suma de los montos de imputación de los créditos.
	 * @throws AllocationGeneratorException 
	 */
	public BigDecimal getCreditsAmount() throws AllocationGeneratorException {
		return getDocumentsAmount(getCredits());
	}
	
	/**
	 * @return the trxName
	 */
	public String getTrxName() {
		return trxName;
	}

	public void setTrxName(String trxName) {
		this.trxName = trxName;
	}

	/**
	 * @return the ctx
	 */
	public Properties getCtx() {
		return ctx;
	}

	/**
	 * @return Indica si se han cargado débitos en este generador
	 */
	public boolean hasDebits() {
		return !getDebits().isEmpty();
	}
		
	/**
	 * @return Indica si se han cargado créditos en este generador
	 */
	public boolean hasCredits() {
		return !getCredits().isEmpty();
	}

	/**
	 * @return the debits
	 */
	protected List<Document> getDebits() {
		return debits;
	}

	/**
	 * @return the credits
	 */
	protected List<Document> getCredits() {
		return credits;
	}

	/**
	 * @param allocationHdr the allocationHdr to set
	 */
	protected void setAllocationHdr(MAllocationHdr allocationHdr) {
		this.allocationHdr = allocationHdr;
	}

	/**
	 * Agrega un documento una de las listas de documentos de la Asignación.
	 * En caso de que el documento ya se encuentre en la lista (mismo Tipo e ID), 
	 * entonces se suma el <code>amount</code> al documento  existente en la lista
	 * @param docID ID del documento a agregar
	 * @param amount Monto a imputar expresado en la moneda del documento
	 * @param docType Tipo del documento
	 * @return Este <code>PaymentOrderGenerator</code>
	 */
	private AllocationGenerator addDocument(List<Document> list, int docID, BigDecimal amount, AllocationDocumentType docType) {
		return addDocument(list, createDocument(docID, docType, amount.abs()));
	}
	
	/**
	 * Agrega un documento una de las listas de documentos de la Asignación. En
	 * caso de que el documento ya se encuentre en la lista (mismo Tipo e ID),
	 * entonces se suma el <code>amount</code> al documento existente en la
	 * lista
	 * 
	 * @param list
	 *            lista a agergar el documento
	 * @param document
	 *            documento a agregar a la lista
	 * @return Este <code>PaymentOrderGenerator</code>
	 */
	private AllocationGenerator addDocument(List<Document> list, Document document) { 
		// No se permiten montos de imputación iguales a cero.
		if (document.amount.compareTo(BigDecimal.ZERO) == 0)
			throw new IllegalArgumentException("Allocation amount must be greather than zero");
		// Se busca si el documento existe en la lista.
		if (list.contains(document)) {
			// En ese caso se incrementa el monto a impuatar del documento existente.
			int index = list.indexOf(document);
			Document oldDocument = list.get(index);
			oldDocument.amount = oldDocument.amount.add(document.amount);
		} else {
			list.add(document);
		}
		return this;
	}
	
	/**
	 * Validaciones previas a la generación de las líneas de asignación
	 * @throws AllocationGeneratorException cuando alguna de las condiciones
	 * requeridas no se cumple
	 */
	private void validate() throws AllocationGeneratorException {
		// Se intenta guardar el encabezado por si no fue guardado aún, de modo
		// que se validen sus datos.
		saveAllocationHdr();
		
		// Se requieren débitos y/o créditos para realizar una asignación. 
		if (!hasDebits() && !hasCredits()) {
			throw new AllocationGeneratorException(getMsg("CreditsOrDebitsRequiredError"));
		}
		
		for(Document doc : getDebits()){
			if (!doc.validateAmount()){
				throw new AllocationGeneratorException(getMsg("CreditDebitAmountsMatchError"));
			}
		}
		
		for(Document doc : getCredits()){
			if (!doc.validateAmount()){
				throw new AllocationGeneratorException(getMsg("CreditDebitAmountsMatchError"));
			}
		}
		
		// Si hay al menos un débito y un crédito entonces la imputación no puede ser parcial
		// con lo cual los totales de débitos y créditos deben coincidir.
		if (hasDebits() && hasCredits()) {
			// Comparación exacta (sin redondeos)
			// TODO: Ver si sería posible la tolerancia de algunos centavos de diferencia en
			// esta comparación.
			if (getDebitsAmount().compareTo(getCreditsAmount() ) != 0) {
				if ( Math.abs(  (getDebitsAmount().subtract(getCreditsAmount())).doubleValue() ) >  (Double.parseDouble(MPreference.GetCustomPreferenceValue("AllowExchangeDifference"))) )
				throw new AllocationGeneratorException(getMsg("CreditDebitAmountsMatchError"));
			}
		}
		
		// Se invoca el método de validación específicas (destinado a las subclases)
		customValidate();
	}
	
	/**
	 * Validación específica del generador. Este método debe ser sobrescrito por las
	 * subclases que requieran validaciones extras a las que se realizan por esta clase.
	 * El método se ejecuta luego de las validaciones estructurales obligatorias de la 
	 * asignación, y antes  de comenzar con la creación de las líneas de asignación.
	 * @throws AllocationGeneratorException cuando alguna de las condiciones
	 * requeridas no se cumple
	 */
	protected void customValidate() throws AllocationGeneratorException {
		// Ninguna validación específica aquí por el momento
	}
	
	/**
	 * Ordena la lista de documentos (Créditos o Débitos) según el monto de imputación,
	 * en orden ascendente
	 * @param documents Lista de documentos a ordenar
	 */
	private void sortDocuments(List<Document> documents) {
		Collections.sort(documents, new Comparator<Document>() {

			public int compare(Document d0, Document d1) {
				return d0.amount.compareTo(d1.amount);
			}
			
		});
	}
	
	/**
	 * Calcula la suma de los montos de imputación de una lista de documentos
	 * @param documents Lista de documentos involucrados en el cálculo (Débitos o Créditos)
	 * @return BigDecimal total imputación de la lista
	 * @throws AllocationGeneratorException 
	 */
	private BigDecimal getDocumentsAmount(List<Document> documents) throws AllocationGeneratorException{
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (Document document : documents) {
			if (document.getConvertedAmount() == null)
					throw new AllocationGeneratorException(getMsg("NoConversionRate") + ": " + (new MCurrency(getCtx(),document.getCurrencyId(),getTrxName())).getISO_Code() + " - " + (new MCurrency(getCtx(),Env.getContextAsInt( getCtx(), "$C_Currency_ID" ),getTrxName())).getISO_Code());
			totalAmount = totalAmount.add(document.getConvertedAmount());
		}
		return totalAmount;
	}
	
	/**
	 * Calcula la suma de los montos de imputación de una lista de documentos
	 * @param documents Lista de documentos involucrados en el cálculo (Débitos o Créditos)
	 * @return BigDecimal total imputación de la lista
	 * @throws AllocationGeneratorException 
	 */
	private BigDecimal getDocumentsExchangeDifferenceAmount(List<Document> documents) throws AllocationGeneratorException{
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (Document document : documents) {
			if ( (document.getConvertedAmount() == null) || (document.getConvertedAmountToday() == null) ) 
					throw new AllocationGeneratorException(getMsg("NoConversionRate") + ": " + (new MCurrency(getCtx(),document.getCurrencyId(),getTrxName())).getISO_Code() + " - " + (new MCurrency(getCtx(),Env.getContextAsInt( getCtx(), "$C_Currency_ID" ),getTrxName())).getISO_Code());
			//totalAmount = totalAmount.add(document.getConvertedAmount().add(document.getConvertedAmount());
		}
		return totalAmount;
	}
	
	/**
	 * Algoritmo de generación de líneas de asignación que imputan los débitos con los
	 * créditos configurados en este generador
	 * @throws AllocationGeneratorException cuando se produce un error en la generación
	 */
	private void generateImputationLines() throws AllocationGeneratorException {
		/*
		 * El código de este algoritmo fue extraído y adaptado según el algoritmo
		 * codificado en la clase VOrdenPagoModel, método doPostProcesarNormal().
		 * El mismo contiene partes que deben ser rediseñadas y corregidas para
		 * que el funcionamiento sea correcto en todos los casos.
		 */
		
		int creditIdx = 0;               // Indice del crédito actual
		BigDecimal creditSurplus = null; // Monto sobrante de un crédito
		BigDecimal balance =             // Saldo (Débitos - Créditos)
			getDebitsAmount().subtract(getCreditsAmount());  
		BigDecimal allowExchangeDifference = getDebitsAmount().subtract(getCreditsAmount());
		
		// TODO: El Saldo debería ser cero aunque actualmente puede ser distinto de cero 
		// y esta diferencia se agrega como WriteOff en la primer línea de asignación creada
		// Ver como mejorar esta solución para que sea configurable.
		
		int debitNumber = 0;
		
		for (Document debitDocument : getDebits()) {
			debitNumber++;
			// Se recorren todos los débitos para ser imputados con los créditos.
			// Se puede dar el caso que el monto de imputación de un débito
			// requiera mas de un crédito para ser satisfacido.
			if (debitDocument.getConvertedAmount() == null)
				throw new AllocationGeneratorException(getMsg("NoConversionRate") + ": " + (new MCurrency(getCtx(),debitDocument.getCurrencyId(),getTrxName())).getISO_Code() + " - " + (new MCurrency(getCtx(),Env.getContextAsInt( getCtx(), "$C_Currency_ID" ),getTrxName())).getISO_Code());
			BigDecimal debitAmount = debitDocument.getConvertedAmount();   // Monto a cubrir del débito
			
			BigDecimal creditAmountSum;
			if	(creditSurplus != null){                 // Inicializar lo que se cubre 
				creditAmountSum = creditSurplus;
			}
			else{                      // Si hay sobrante, entonces se utiliza
				if (getCredits().get(creditIdx).getConvertedAmount() == null)
					throw new AllocationGeneratorException(getMsg("NoConversionRate") + ": " + (new MCurrency(getCtx(),getCredits().get(creditIdx).getCurrencyId(),getTrxName())).getISO_Code() + " - " + (new MCurrency(getCtx(),Env.getContextAsInt( getCtx(), "$C_Currency_ID" ),getTrxName())).getISO_Code());
				creditAmountSum = getCredits().get(creditIdx).getConvertedAmount();  // Sino, se utiliza el total del crédito actual
			}
			// Lista de créditos y sus montos a utilizar para cubrir el monto a inputar del
			// débito actual.
			List<Document> subCredits = new ArrayList<Document>();
			List<BigDecimal> subCreditsAmounts = new ArrayList<BigDecimal>();
			
			// Se agrega el crédito actual a la lista de créditos utilizados.
			subCredits.add(getCredits().get(creditIdx));
			subCreditsAmounts.add(creditAmountSum);
			
			// ----------------------------------------------------------------------------
			// Precondición: Se asume que en este los crédito alcanzan para cubrir los débitos
			// ----------------------------------------------------------------------------
			// Si el monto del débito supera el monto de la suma de los créditos utilizados
			// hasta el momento, entonces es necesario utilizar el siguiente crédito para
			// cubrir el monto del débito
			while (debitAmount.compareTo(creditAmountSum.add(allowExchangeDifference)) > 0) { 
				creditIdx++;
				Document credit = getCredits().get(creditIdx); // Siguiente crédito
				// Se actualiza la lista de créditos utilizados, la lista de montos
				// y la suma de los montos de todos los créditos utilizados.
				subCredits.add(credit);
				if (credit.getConvertedAmount() == null)
					throw new AllocationGeneratorException(getMsg("NoConversionRate") + ": " + (new MCurrency(getCtx(),credit.getCurrencyId(),getTrxName())).getISO_Code() + " - " + (new MCurrency(getCtx(),Env.getContextAsInt( getCtx(), "$C_Currency_ID" ),getTrxName())).getISO_Code());
				subCreditsAmounts.add(credit.getConvertedAmount());
				creditAmountSum = creditAmountSum.add(credit.getConvertedAmount());
			}
			
			// Si la suma de créditos supera el monto del débito, se resta la diferencia
			// al último crédito utilizado y esta diferencia pasa a ser el monto sobrante
			// del crédito actual.
			if (debitAmount.compareTo(creditAmountSum.add(allowExchangeDifference)) < 0) {
				int lastCreditIdx = subCreditsAmounts.size() - 1;
				creditSurplus = creditAmountSum.subtract(debitAmount);
				subCreditsAmounts.set( lastCreditIdx, subCreditsAmounts.get(lastCreditIdx).subtract(creditSurplus) );
				creditAmountSum = creditAmountSum.subtract(creditSurplus);
			// Si con el crédito actual se cubrió exactamente el monto del débito, entonces
			// no hay sobrante y se incrementa el índice de créditos de modo que para el
			// el siguiente débito se utilice el siguiente crédito sin asignar.
			} else {
				creditSurplus = null;
				creditIdx++;
			}
			
			// Aquí se sabe que debitAmount y creditAmountSum son iguales
			// Se recorren los créditos seleccionados para cubrir el débito, y por cada crédito
			// se crea una línea de asignación
			for (int subCreditIdx = 0; subCreditIdx < subCredits.size(); subCreditIdx++) {
				// Se obtiene el crédito y el monto a aplicar
				Document creditDocument = subCredits.get(subCreditIdx);
				BigDecimal creditApplyingAmt = subCreditsAmounts.get(subCreditIdx);
				
				BigDecimal discountAmt = Env.ZERO;
				BigDecimal writeoffAmt = Env.ZERO;
				BigDecimal overunderAmt = Env.ZERO;

				// Redondeo de diferencia de balance en caso que los montos de débitos
				// y créditos sean diferentes.
				// Se Valida que esta diferencia esté dentro de un rango permitido.		

				if ((debitNumber == getDebits().size()) && (balance.signum() != 0) && (balance.abs().compareTo(new BigDecimal(MPreference.GetCustomPreferenceValue("AllowExchangeDifference"))) <= 0)) {
					writeoffAmt = balance; 
					balance = BigDecimal.ZERO;
				}
				
				// Si el débito y crédito son ambos Pagos o Líneas de Caja, la estructura
				// actual de la tabla de Línea de Asignación (C_AllocationLine) no permite
				// la imputación entre estos documentos. En estos dos casos se crean
				// entonces 2 líneas de asignación parciales, en donde cada una refiere
				// al documento débito y crédito respectivamente. Esta solución permite
				// mantener correctamente los montos pendientes de los documentos, aunque
				// inebitablemente no permite mantener un referencia entre ambos documentos
				// imputados (para ello se debería ampliar la tabla C_AllocationLine con
				// nuevas columnas).
				if (debitDocument.type == creditDocument.type 
						&& (debitDocument.type == AllocationDocumentType.PAYMENT
						||  debitDocument.type == AllocationDocumentType.CASH_LINE)) {

					generateImputationSplit( 
							debitDocument, creditDocument, 
							creditApplyingAmt, discountAmt, 
							writeoffAmt, overunderAmt
					);
					
				// Si la estructura soporta la imputación, entonces se crea una línea
				// que imputa ambos documentos.
				} else {
					generateImputationLine( 
							debitDocument, creditDocument, 
							creditApplyingAmt, discountAmt, 
							writeoffAmt, overunderAmt
					);
				}
			}
		}
		// El total de la asignación es la suma de todos los débitos.
		getAllocationHdr().setGrandTotal(getDebitsAmount());
	}
	
	/**
	 * Algoritmo de generación de líneas de asignación parciales en las que solo aparece
	 * un débito o un crédito, pero no la línea no realiza una imputación entre dos
	 * documentos
	 * @throws AllocationGeneratorException cuando se produce un error en la generación
	 */
	private void generatePartialLines() throws AllocationGeneratorException {
		/*
		 * En este punto se sabe que o existen solo débitos o solo créditos, pero
		 * no ambos. !(hasCredits() && hasDebits())
		 * A partir de esta precondición, se determina la lista de documentos a asignar
		 * y se crea una línea de asignación por cada documento   
		 */

		// Indica si hay que asignar solo créditos (true) o débitos (false)
		boolean onlyCredits = !hasDebits() && hasCredits(); 
		// Se determina la lista de documentos a asignar
		List<Document> documents = (onlyCredits ? getCredits() : getDebits());
		// Se crean las líneas de asignación
		for (Document document : documents) {
			BigDecimal discountAmt = Env.ZERO;
			BigDecimal writeoffAmt = Env.ZERO;
			BigDecimal overunderAmt = Env.ZERO;
			BigDecimal amount = getAllocationLineAmountFor(document);

			// Se crea la línea de asignación
			MAllocationLine allocLine = 
				new MAllocationLine( getAllocationHdr(), amount, discountAmt, writeoffAmt, overunderAmt);

			// Se setean el IDs del documento según corresponda
			if (onlyCredits)
				document.setAsCreditIn(allocLine); // Credito
			else
				document.setAsDebitIn(allocLine);  // Debito
			allocLine.setOverUnderAmt(overunderAmt);

			// Se asigna la EC de la línea a partir de la EC del encabezado (si existe)
			allocLine.setC_BPartner_ID(getAllocationHdr().getC_BPartner_ID());

			// Si no se puede guardar la línea se dispara una excepción
			if (!allocLine.save()) {
				throw new AllocationGeneratorException(getMsg("AllocationLineSaveError") + ": " + CLogger.retrieveErrorAsString());
			}
		}
		// El total de la asignación es la suma de los débitos o créditos según
		// corresponda
		getAllocationHdr().setGrandTotal(onlyCredits ? getCreditsAmount() : getDebitsAmount());
	}
	
	/**
	 * Crea una línea de asignación que imputa un débito con un crédito.
	 * @precondition: 
	 * 	(debitDocument.type != creditDocument.type) ||
	 *  (debitDocument.type == creditDocument.type && debitDocument.type == INVOICE) 
	 */
	private void generateImputationLine(
			Document debitDocument, Document creditDocument, 
			BigDecimal amount, BigDecimal discountAmt, 
			BigDecimal writeoffAmt, BigDecimal overunderAmt) throws AllocationGeneratorException {

		// Se crea la línea de asignación
		MAllocationLine allocLine = 
			new MAllocationLine( getAllocationHdr(), amount, discountAmt, writeoffAmt, overunderAmt);
		
		// Se setean los IDs de débito y crédito según corresponda
		debitDocument.setAsDebitIn(allocLine);
		creditDocument.setAsCreditIn(allocLine);
		allocLine.setOverUnderAmt(overunderAmt);

		// Se asigna la EC de la línea a partir de la EC del encabezado (si existe)
		allocLine.setC_BPartner_ID(getAllocationHdr().getC_BPartner_ID());
		
		// Si no se puede guardar la línea se dispara una excepción
		if (!allocLine.save()) {
			throw new AllocationGeneratorException(getMsg("AllocationLineSaveError") + ": " + CLogger.retrieveErrorAsString());
		}
	}
	
	/**
	 * Crea una línea de asignación parcial para el débito y otra para el crédito.
	 */
	private void generateImputationSplit(
			Document debitDocument, Document creditDocument, 
			BigDecimal amount, BigDecimal discountAmt, 
			BigDecimal writeoffAmt, BigDecimal overunderAmt) throws AllocationGeneratorException {
		
		// Se crea primero la línea del débito y luego la del crétdito.
		Document[] documents = new Document[] {debitDocument, creditDocument};
		for (int i = 0; i < documents.length; i++) {
			Document document = documents[i];
			boolean isTheCredit = (i == 1);
			// Si es el crédito se anulan los montos de Discount y Writeoff. Solo
			// se tienen en cuenta para el débito.
			if (isTheCredit) {
				discountAmt = Env.ZERO;
				writeoffAmt = Env.ZERO;
			}
			
			// Se crea la línea de asignación parcial
			MAllocationLine allocLine = 
				new MAllocationLine( getAllocationHdr(), amount, discountAmt, writeoffAmt, overunderAmt);
			
			// Se sete los IDs de débito
			document.setAsDebitIn(allocLine);
			allocLine.setOverUnderAmt(overunderAmt);

			// Se asigna la EC de la línea a partir de la EC del encabezado (si existe)
			allocLine.setC_BPartner_ID(getAllocationHdr().getC_BPartner_ID());
			
			// Si no se puede guardar la línea se dispara una excepción
			if (!allocLine.save()) {
				throw new AllocationGeneratorException(getMsg("AllocationLineSaveError") + ": " + CLogger.retrieveErrorAsString());
			}
		}
	}
	
	/**
	 * Devuelve el monto que se asigna en la línea de asignación que involucra
	 * un documento. Comunmente este valor es el monto indicado cuando se agregó
	 * el docuemento, pero en algunos casos puede no ser así (ej. OPA y RCA). 
	 * Las subclases pueden redifinir este método para modificar el comportamiento
	 * por defecto codificado en esta clase, el cual devuelve <code>document.amount</code>
	 * @param document Documento a asignar en la línea de asignación
	 * @return Monto a setear en el campo <code>amount</code> de la línea
	 */
	protected BigDecimal getAllocationLineAmountFor(Document document) {
		return document.getConvertedAmount();
	}
	
	/**
	 * Calcula y asigna el monto total de retención según los créditos agregados.
	 * La suma calculada es asignada al campo Retencion_Amt del encabezado de
	 * la asignación.
	 */
	private void setRetentionAmount() {
		BigDecimal retentionAmt = BigDecimal.ZERO;
		// Solo se tienen en cuenta retenciones utilizadas como crédito
		for (Document credit : getCredits()) {
			retentionAmt = retentionAmt.add(credit.getAmountIfRetention());
		}
		getAllocationHdr().setRetencion_Amt(retentionAmt);
	}
	
	/**
	 * Guarda los cambios de un encabezado de asignación
	 * @throws AllocationGeneratorException si la ejecución del método {@link MAllocationHdr#save()}
	 * devuelve <code>false</code>.
	 */
	private void saveAllocationHdr(MAllocationHdr allocationHdr) throws AllocationGeneratorException {
		if (!allocationHdr.save()) {
			throw new AllocationGeneratorException(getMsg("AllocationHeaderSaveError") + ": " + CLogger.retrieveErrorAsString());
		}
	}
	
	/**
	 * Guardar los cambios del encabezado de asignación asociado con este generador.
	 * @throws AllocationGeneratorException si la ejecución del método {@link MAllocationHdr#save()}
	 * devuelve <code>false</code>.
	 */
	private void saveAllocationHdr() throws AllocationGeneratorException {
		saveAllocationHdr(getAllocationHdr());
	}
	
	/**
	 * @return Devuelve un mensaje traducido.
	 */
	protected String getMsg(String name) {
		return Msg.translate(Env.getCtx(), name);
	}
	
	/**
	 * Crea un nuevo documento de crédito / débito según su tipo.
	 * @param id ID del documento
	 * @param type Tipo del documento
	 * @param amount Monto a imputar expresado en la moneda del documento
	 * @return {@link Document}
	 */
	private Document createDocument(Integer id, AllocationDocumentType type, BigDecimal amount) {
		Document newDocument = null;
		switch (type) {
		case INVOICE:
			newDocument = new Invoice(id, amount); break;
		case CASH_LINE:
			newDocument = new CashLine(id, amount); break;
		case PAYMENT:
			newDocument = new Payment(id, amount); break;
		}
		return newDocument;
	}
	
	/**
	 * Tipo de dato interno. Utilizado para almacenar los débitos
	 * y créditos involucrados en la OP.
	 */
	public abstract class Document {

		public Integer id;
		public AllocationDocumentType type;
		public BigDecimal amount;
		public Integer currencyId;
		public Timestamp date;
		public Integer orgID;
		private BigDecimal amountAllocated = BigDecimal.ZERO;
		
		/**
		 * @param id ID del documento
		 * @param type Tipo del Documento
		 * @param amount Monto a imputar
		 */
		public Document(Integer id, AllocationDocumentType type, BigDecimal amount) {
			super();
			this.id = id;
			this.type = type;
			this.amount = amount;
		}

		public boolean validateAmount() {
			return false;
		}

		/**
		 * Se asigna este documento como un débito en la línea de asignación.
		 * Solo se asigna el ID del documento en el campo adecuado de la línea, sin
		 * modificar el monto de asignación de la línea
		 * @param allocationLine Línea de asignación en la cual se asigna este documento
		 */
		public abstract void setAsDebitIn(MAllocationLine allocationLine);

		/**
		 * Se asigna este documento como un crédito en la línea de asignación.
		 * Solo se asigna el ID del documento en el campo adecuado de la línea, sin
		 * modificar el monto de asignación de la línea
		 * @param allocationLine Línea de asignación en la cual se asigna este documento
		 */
		public abstract void setAsCreditIn(MAllocationLine allocationLine);

		/**
		 * @return Devuelve el monto de este documento solo si el mismo es un
		 * documento de retención. La implementación por defecto devuelve 
		 * <code>BigDecimal.ZERO</code> y las subclases que implementen un
		 * documento que puede ser retención deben sobrescribir este método.
		 */
		public BigDecimal getAmountIfRetention() {
			return BigDecimal.ZERO;
		}
		
		@Override
		public boolean equals(Object obj) {
			boolean equals = false;
			// Los documentos son iguales si tienen el mismo Tipo e ID.
			if (obj instanceof Document) {
				Document anotherDoc = (Document) obj;
				equals = (id == anotherDoc.id) && (type == anotherDoc.type);
			}
			return equals;
		}
		
		public BigDecimal getConvertedAmount(){
			return MCurrency.currencyConvert(this.amount, this.currencyId, Env.getContextAsInt( getCtx(), "$C_Currency_ID" ), getSqlDate(), Env.getAD_Org_ID(getCtx()), getCtx());
		}
		
		public abstract Date getSqlDate();

		public BigDecimal getConvertedAmountToday(){
			return MCurrency.currencyConvert(this.amount, this.currencyId, Env.getContextAsInt( getCtx(), "$C_Currency_ID" ), Env.getContextAsDate(getCtx(), "#Date"), Env.getAD_Org_ID(getCtx()), getCtx());
		}

		public Integer getCurrencyId() {
			return currencyId;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public BigDecimal getAmountAllocated() {
			return amountAllocated;
		}

		public void setAmountAllocated(BigDecimal amountAllocated) {
			this.amountAllocated = amountAllocated;
		}
		
		/**
		 * El monto disponible es el monto del documento - el monto imputado
		 * actualmente
		 * 
		 * @return el monto pendiente no utilizado o no imputado
		 */
		public BigDecimal getAvailableAmt(){
			return getAmount().subtract(getAmountAllocated());
		}
	}
	
	/**
	 * Tipo de Crédito / Débito: Factura
	 */
	public class Invoice extends Document {

		/**
		 * @param id ID de la factura
		 * @param amount Monto a imputar
		 */
		public Invoice(Integer id, BigDecimal amount) {
			super(id, AllocationDocumentType.INVOICE, amount);
			this.currencyId = getSqlCurrencyId();
			this.date = getSqlDate();
		}
		
		/**
		 * @param id ID de la factura
		 */
		public Invoice(Integer id) {
			super(id, AllocationDocumentType.INVOICE, BigDecimal.ZERO);
		}

		private Integer getSqlCurrencyId() {
			return DB.getSQLValue(getTrxName(), "SELECT C_Currency_ID FROM C_Invoice WHERE C_Invoice_ID = ?", id);
		}
		
		public Timestamp getSqlDate() {
			return DB.getSQLValueTimestamp(getTrxName(), "SELECT DateAcct FROM C_Invoice WHERE C_Invoice_ID = "+ id);
		}

		@Override
		public void setAsCreditIn(MAllocationLine allocationLine) {
			allocationLine.setC_Invoice_Credit_ID(id);			
		}

		@Override
		public void setAsDebitIn(MAllocationLine allocationLine) {
			allocationLine.setC_Invoice_ID(id);
		}

		@Override
		public BigDecimal getAmountIfRetention() {
			// Verifica si la factura es un comprobante de retención.
			// Si lo es, debe figurar en la tabla M_Retencion_Invoice que asocia
			// el comprobante de retención con la factura del fisco.
			BigDecimal amt = BigDecimal.ZERO;
			int isRetention = DB.getSQLValue(getTrxName(), 
					"SELECT COUNT(*) FROM M_Retencion_Invoice WHERE C_Invoice_ID = ?", id);
			if (isRetention > 0)
				amt = amount;
			return amt;
		}
		
		public boolean validateAmount() {
			return ( (DB.getSQLValueBD(getTrxName(), "SELECT invoiceopen(?,0)", id, true)).subtract(amount.setScale(2, RoundingMode.HALF_EVEN)).compareTo(BigDecimal.ZERO) >= 0 );
		}
	}

	/**
	 * Tipo de Crédito / Débito: Línea de Caja
	 */
	public class CashLine extends Document {

		/**
		 * @param id ID de la línea de caja
		 * @param amount Monto a imputar
		 */
		public CashLine(Integer id, BigDecimal amount) {
			super(id, AllocationDocumentType.CASH_LINE, amount);
			this.currencyId = getSqlCurrencyId();
			this.date = getSqlDate();
		}
		
		/**
		 * @param id ID de la línea de caja
		 */
		public CashLine(Integer id) {
			super(id, AllocationDocumentType.CASH_LINE, BigDecimal.ZERO);
		}
		
		private Integer getSqlCurrencyId() {
			return DB.getSQLValue(getTrxName(), "SELECT C_Currency_ID FROM C_CashLine WHERE C_CashLine_ID = ?", id);
		}
		
		public Timestamp getSqlDate() {
			return DB.getSQLValueTimestamp(getTrxName(), "SELECT DateAcct FROM C_Cash c INNER JOIN C_CashLine cl ON c.C_Cash_ID = cl.C_Cash_ID WHERE C_CashLine_ID = "+ id);
		}
		
		@Override
		public void setAsCreditIn(MAllocationLine allocationLine) {
			allocationLine.setC_CashLine_ID(id);			
		}

		@Override
		public void setAsDebitIn(MAllocationLine allocationLine) {
			allocationLine.setC_CashLine_ID(id);
		}
		
		public boolean validateAmount() {
			return ( (DB.getSQLValueBD(getTrxName(), "SELECT abs(cashlineavailable(?))", id,true)).subtract(amount.setScale(2, RoundingMode.HALF_EVEN)).compareTo(BigDecimal.ZERO) >= 0 );
		}
	}

	/**
	 * Tipo de Crédito / Débito: Pago
	 */
	public class Payment extends Document {

		/**
		 * @param id ID del pago
		 * @param amount Monto a imputar
		 */
		public Payment(Integer id, BigDecimal amount) {
			super(id, AllocationDocumentType.PAYMENT, amount);
			this.currencyId = getSqlCurrencyId();
			this.date = getSqlDate();
		}
		
		/**
		 * @param id ID del pago
		 */
		public Payment(Integer id) {
			super(id, AllocationDocumentType.PAYMENT, BigDecimal.ZERO);
		}
		
		private Integer getSqlCurrencyId() {
			return DB.getSQLValue(getTrxName(), "SELECT C_Currency_ID FROM C_Payment WHERE C_Payment_ID = ?", id);
		}
		
		public Timestamp getSqlDate() {
			return DB.getSQLValueTimestamp(getTrxName(), "SELECT DateAcct FROM C_Payment WHERE C_Payment_ID = "+ id);
		}
		
		@Override
		public void setAsCreditIn(MAllocationLine allocationLine) {
			allocationLine.setC_Payment_ID(id);			
		}

		@Override
		public void setAsDebitIn(MAllocationLine allocationLine) {
			allocationLine.setC_Payment_ID(id);
		}
		
		public boolean validateAmount() {
			return ( (DB.getSQLValueBD(getTrxName(), "SELECT paymentavailable(?)", id,true)).subtract(amount.setScale(2, RoundingMode.HALF_EVEN)).compareTo(BigDecimal.ZERO) >= 0 );
		}
	}
	
	public static BigDecimal getExchangeDifference(HashMap<Integer, BigDecimal> facts, ArrayList<PaymentMediumInfo> pays, Properties ctx, String trxName, Date allocDate) {
		BigDecimal sumaPayments = new BigDecimal(0);
		BigDecimal sumaPaymentsAllocDate = new BigDecimal(0);
		for (PaymentMediumInfo mp : pays){
			sumaPaymentsAllocDate = sumaPaymentsAllocDate.add(MCurrency.currencyConvert(mp.getAmount(), mp.getCurrencyId(), Env.getContextAsInt(ctx, "$C_Currency_ID"), allocDate, Env.getAD_Org_ID(ctx), ctx));
			sumaPayments = sumaPayments.add(MCurrency.currencyConvert(mp.getAmount(), mp.getCurrencyId(), Env.getContextAsInt(ctx, "$C_Currency_ID"), mp.getDate(), Env.getAD_Org_ID(ctx), ctx));
		}
		
		BigDecimal sumInvoices = new BigDecimal(0);
		BigDecimal sumInvoicesAllocDate = new BigDecimal(0);
		for (Integer id : facts.keySet()){
			MInvoice invoice = new MInvoice(ctx, id, trxName);
		
			sumInvoicesAllocDate = sumInvoicesAllocDate.add(MCurrency.currencyConvert(facts.get(id), invoice.getC_Currency_ID(), Env.getContextAsInt(ctx, "$C_Currency_ID"), allocDate, Env.getAD_Org_ID(ctx), ctx));
			sumInvoices = sumInvoices.add(MCurrency.currencyConvert(facts.get(id), invoice.getC_Currency_ID(), Env.getContextAsInt(ctx, "$C_Currency_ID"), invoice.getDateAcct(), Env.getAD_Org_ID(ctx), ctx));
		}
		return sumaPayments.add(sumInvoicesAllocDate).subtract(sumaPaymentsAllocDate).subtract(sumInvoices);
	}
	
	public HashMap<Integer, BigDecimal> generateDebitsForExchangeDifference() {
		HashMap<Integer, BigDecimal> facts = new HashMap<Integer, BigDecimal>();
		if (this.getDebits() != null) {
			for (Document x : this.getDebits()){
				if(x.getAmount().compareTo(BigDecimal.ZERO) > 0){
					facts.put(x.getId(), x.getAmount());	
				}
			}
		}	
		return facts;
	}
	
	public ArrayList<PaymentMediumInfo> generateCreditsForExchangeDifference() {
		ArrayList<PaymentMediumInfo> pays = new ArrayList<PaymentMediumInfo>();
		if (this.getDebits() != null) {
			for (Document doc : this.getCredits()){
				pays.add(new PaymentMediumInfo(doc.getAmount(), doc.getCurrencyId(), doc.getSqlDate()));
			}
		}	
		return pays;	
	}
	
	public void generateDebitCreditExchangeDifference() throws Exception{
		BigDecimal amt = getExchangeDifference(generateDebitsForExchangeDifference(), generateCreditsForExchangeDifference(), ctx, trxName, getAllocationHdr().getDateAcct());
		MInvoice credit = null; 
		MInvoice debit = null;
		MInvoice inv = null; 
		MInvoiceLine invoiceLine = null; 
		boolean isCredit;
		boolean createInvoice;
		MTax tax = MTax.getTaxExemptRate(getCtx(),getTrxName());
		if(amt.compareTo(BigDecimal.ZERO) != 0){
			isCredit = amt.compareTo(BigDecimal.ZERO) < 0;
			createInvoice = isCredit?credit == null:debit==null;
			if(createInvoice){
				// Crear la factura
				inv = createCreditDebitInvoice(isCredit);
				
				Document doc = getDebits().get(getDebits().size() - 1);
				MInvoice debInv = new MInvoice(ctx, doc.getId(), trxName);
					
				inv.setC_Project_ID(debInv.getC_Project_ID());
				inv.setC_Campaign_ID(debInv.getC_Campaign_ID());
				if(!inv.save()){
					throw new Exception("Can't create " + (isCredit ? "credit" : "debit")
							+ " document for discounts. Original Error: "+CLogger.retrieveErrorAsString());
				}
				if(isCredit){
					credit = inv;
				}
				else{
					debit = inv;
				}
			}
			// Si es crédito 
			inv = isCredit?credit:debit;
			// Creo la línea de la factura
			invoiceLine = createInvoiceLine(inv,isCredit,amt,tax);				
			if(!invoiceLine.save()){
				throw new Exception("Can't create " + (isCredit ? "credit" : "debit")
						+ " document line for discounts. Original Error: "+CLogger.retrieveErrorAsString());  
			}
		}
		// - Si es hay crédito lo guardo como un medio de pago
		// - Si es débito lo guardo donde se encuentran las facturas
		if(credit != null){			
			// Completar el crédito en el caso que no requiera impresión fiscal,
			// ya que si requieren se realiza al final del procesamiento
			if(!needFiscalPrint(credit)){
				credit.setSkipAutomaticCreditAllocCreation(true);
				processDocument(credit, MInvoice.DOCACTION_Complete);
			}
			
			this.addCreditDocument(credit.getID(), amt, AllocationDocumentType.INVOICE);
		}
		if(debit != null){
			// Completar el crédito en el caso que no requiera impresión fiscal,
			// ya que si requieren se realiza al final del procesamiento
			if(!needFiscalPrint(debit)){
				processDocument(debit, MInvoice.DOCACTION_Complete);
			}

			this.addDebitDocument(debit.getID(), amt, AllocationDocumentType.INVOICE);
		}
	}
	
	/**
	 * Creo una factura como crédito o débito, dependiendo configuración.
	 * 
	 * @param credit
	 *            true si se debe crear un crédito o false si es débito
	 * @return factura creada
	 * @throws Exception en caso de error
	 */
	protected MInvoice createCreditDebitInvoice(boolean credit) throws Exception{
		MInvoice invoice = new MInvoice(getCtx(), 0, getTrxName());
		invoice.setBPartner(new MBPartner(getCtx(),getAllocationHdr().getC_BPartner_ID(), getTrxName()));
		// Setear el tipo de documento
		invoice = setDocType(invoice, credit);
		
		if(LOCALE_AR_ACTIVE){
			invoice = addLocaleARData(invoice, credit);
		}
		
		// Se indica que no se debe crear una línea de caja al completar la factura ya
		// que es el propio TPV el que se encarga de crear los pagos e imputarlos con
		// la factura (esto soluciona el problema de líneas de caja duplicadas que 
		// se había detectado).
		invoice.setCreateCashLine(false);
		
		invoice.setDocAction(MInvoice.DOCACTION_Complete);
		invoice.setDocStatus(MInvoice.DOCSTATUS_Drafted);
		// Seteo el bypass de la factura para que no chequee el saldo del
		// cliente porque ya lo chequea el tpv
		invoice.setCurrentAccountVerified(true);
		// Seteo el bypass para que no actualice el crédito del cliente ya
		// que se realiza luego al finalizar las operaciones
		invoice.setUpdateBPBalance(false);
		return invoice;
	}
	
	/**
	 * Crea una línea de factura de la factura y datos parámetro.
	 * 
	 * @param invoice
	 *            factura
	 * @param isCredit
	 *            true si estamos creando un crédito, false caso contrario
	 * @param amt
	 *            monto de la línea
	 * @param tax
	 *            impuesto para la línea
	 * @return línea de la factura creada
	 * @throws Excepción
	 *             en caso de error
	 */
	public MInvoiceLine createInvoiceLine(MInvoice invoice, boolean isCredit, BigDecimal amt, MTax tax) throws Exception{
		
		MInvoiceLine invoiceLine = new MInvoiceLine(invoice);
		invoiceLine.setQty(1);
		// Setear el precio con el monto del descuento
		amt = amt.abs();

		invoiceLine.setPriceEntered(amt);
		invoiceLine.setPriceActual(amt);
		invoiceLine.setPriceList(amt);
		invoiceLine.setC_Tax_ID(tax.getID());
		invoiceLine.setLineNetAmt();
		invoiceLine.setC_Project_ID(invoice.getC_Project_ID());
		// Setear el artículo
		String valueProduct = MPreference.GetCustomPreferenceValue("DIF_CAMBIO_ARTICULO");

		if(Util.isEmpty(valueProduct,true)){
			throw new Exception(
					"Falta configuracion de articulos para crear créditos/débitos para descuentos/recargos");
		}
		Integer productID = DB.getSQLValue(getTrxName(), "SELECT M_Product_ID FROM M_Product WHERE value = ?", valueProduct);
		invoiceLine.setM_Product_ID(productID);
		return invoiceLine;
	}
	
	/**
	 * Indica si la factura debe ser emitida mediante un controlador fiscal.
	 * @param invoice Factura a evaluar.
	 */
	private boolean needFiscalPrint(MInvoice invoice) {
		return MDocType.isFiscalDocType(invoice.getC_DocTypeTarget_ID())
				&& LOCALE_AR_ACTIVE;
	}
	
	/**
	 * Procesa la factura en base a un docaction parámetro
	 * 
	 * @param invoice
	 *            factura
	 * @param docAction
	 *            acción
	 * @throws Exception
	 *             si hubo error al realizar el procesamiento o al guardar
	 */
	public void processDocument(MInvoice invoice, String docAction) throws Exception{
		invoice.setSkipAutomaticCreditAllocCreation(true);
		// Procesar el documento
		if(!invoice.processIt(docAction)){
			throw new Exception(invoice.getProcessMsg());
		}
		// Guardar
		if(!invoice.save()){
			throw new Exception(CLogger.retrieveErrorAsString());
		}
	}
	
	/**
	 * Agregar la info de locale ar necesaria a la factura con localización
	 * argentina.
	 * 
	 * @param invoice
	 *            factura
	 * @param credit true si estamos ante un crédito, false si es débito
	 * @return factura parámetro con toda la info localeAr necesaria cargada
	 * @throws Exception en caso de error
	 */
	protected MInvoice addLocaleARData(MInvoice invoice, boolean credit) throws Exception{
		MBPartner bPartner = new MBPartner(getCtx(),invoice.getC_BPartner_ID(), getTrxName());
		
		MLetraComprobante letraCbte = getLetraComprobante(bPartner);
		// Se asigna la letra de comprobante, punto de venta y número de comprobante
		// a la factura creada.
		invoice.setC_Letra_Comprobante_ID(letraCbte.getID());
		// Nro de comprobante.
		Integer nroComprobante = CalloutInvoiceExt
				.getNextNroComprobante(invoice.getC_DocTypeTarget_ID());
		if (nroComprobante != null)
			invoice.setNumeroComprobante(nroComprobante);
		
		// Asignación de CUIT en caso de que se requiera.
		
		String cuit = bPartner.getTaxID();
		invoice.setCUIT(cuit);
		
		// Setear una factura original al crédito que estamos creando
		if(credit && LOCALE_AR_ACTIVE){
			// Obtengo la primer factura como random (la impresora fiscal puede tirar un error si no existe una factura original seteada)
			if (hasDebits()){
				getDebits().get(0);
				Invoice firstInvoice = (Invoice) getDebits().get(0);
				if(firstInvoice != null){
					invoice.setC_Invoice_Orig_ID(firstInvoice.id);
				}	
			}
		}
		return invoice;
	}
	
	/**
	 * Obtener la letra del comprobante
	 * 
	 * @return letra del comprobante
	 * @throws Exception
	 *             si la compañía o el cliente no tienen configurado una
	 *             categoría de IVA y si no existe una Letra que los corresponda
	 */
	protected MLetraComprobante getLetraComprobante(MBPartner bPartner) throws Exception{
		Integer categoriaIVAclient = CalloutInvoiceExt.darCategoriaIvaClient();
		Integer categoriaIVACustomer = bPartner.getC_Categoria_Iva_ID();
		// Se validan las categorias de IVA de la compañia y el cliente.
		if (categoriaIVAclient == null || categoriaIVAclient == 0) {
			throw new Exception(getMsg("ClientWithoutIVAError"));
		} else if (categoriaIVACustomer == null || categoriaIVACustomer == 0) {
			throw new Exception(getMsg("BPartnerWithoutIVAError"));
		}
		// Se obtiene el ID de la letra del comprobante a partir de las categorias de IVA.
		Integer letraID = CalloutInvoiceExt.darLetraComprobante(categoriaIVACustomer, categoriaIVAclient);
		if (letraID == null || letraID == 0){
			throw new Exception(getMsg("LetraCalculationError"));
		}
		// Se obtiene el PO de letra del comprobante.
		return new MLetraComprobante(getCtx(), letraID, getTrxName());
	}
	
	/**
	 * Obtener la clave del tipo de documento real en base al general y si el
	 * comprobante que estoy creando es un crédito o un débito
	 * 
	 * @param generalDocType
	 *            tipo de documento general
	 * @param isCredit
	 *            true si estamos ante un crédito, false caso contrario
	 * @return
	 */
	protected String getRealDocTypeKey(boolean isCredit){
		// La lista de tipos de documento generales tiene como value los doc
		// type keys de los tipos de documento
		String docTypeKey = null;
		// Para Locale AR, Abono de Cliente es Nota de Crédito o Nota de Débito
		// dependiendo si estamos creando un crédito o un débito respectivamente 
		if(LOCALE_AR_ACTIVE){
			// Nota de Crédito
			if (isCredit) {	docTypeKey = MDocType.DOCTYPE_CustomerCreditNote; }
			// Nota de Débito
			else{ docTypeKey = MDocType.DOCTYPE_CustomerDebitNote; }
		}
		else{
			// Nota de Crédito
			if (isCredit) {	docTypeKey = "CreditDocumentType"; }
			// Nota de Débito
			else{ docTypeKey = "DebitDocumentType";	}
		}
		return docTypeKey;
	}
	
	/**
	 * Setea el tipo de documento a la factura parámetro
	 * 
	 * @param invoice
	 *            factura a modificar
	 * @param isCredit
	 *            booleano que determina si lo que estoy creando es un débito o
	 *            un crédito
	 * @return factura con el tipo de doc seteada
	 */
	protected MInvoice setDocType(MInvoice invoice, boolean isCredit) throws Exception{
		MDocType documentType = null;

		// Obtener la clave del tipo de documento a general
		String docTypeKey = getRealDocTypeKey(isCredit);
		// Si está activo locale_ar entonces se debe obtener en base al pto de venta y la letra
		if(LOCALE_AR_ACTIVE){
			MLetraComprobante letra = getLetraComprobante(new MBPartner(getCtx(), invoice.getC_BPartner_ID(), getTrxName()));
			Integer posNumber = Integer.valueOf(MPreference.GetCustomPreferenceValue("DIF_CAMBIO_PTO_VENTA", Env.getAD_Client_ID(getCtx())));

			if(Util.isEmpty(posNumber,true)) throw new Exception(getMsg("NotExistPOSNumber"));
			// Se obtiene el tipo de documento para la factura.
			documentType = MDocType.getDocType(getCtx(), invoice.getAD_Org_ID(), docTypeKey,letra.getLetra(), posNumber, getTrxName());
			if (documentType == null) {
				throw new Exception(Msg.getMsg(getCtx(),
						"NonexistentPOSDocType", new Object[] { letra,
								posNumber }));
			}
			if(!Util.isEmpty(posNumber,true)){
				invoice.setPuntoDeVenta(posNumber);
			}
		}
		else{
			// Tipo de documento en base a la key
			documentType = MDocType.getDocType(getCtx(), docTypeKey, getTrxName()); 
		}
		
		invoice.setC_DocTypeTarget_ID(documentType.getID());
		return invoice;
	}
	
	public class PaymentMediumInfo{
		private BigDecimal amount;
		private Integer currencyId;
		private Date date;
		
		/**
		 * @param id ID de la factura
		 * @param amount Monto a imputar
		 * @param date Fecha a imputar
		 */
		public PaymentMediumInfo(BigDecimal amount, Integer currencyId, Date date) {
			this.amount = amount;
			this.currencyId = currencyId;
			this.date = date;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		public void setAmount(BigDecimal amount) {
			this.amount = amount;
		}

		public Integer getCurrencyId() {
			return currencyId;
		}

		public void setCurrencyId(Integer currencyId) {
			this.currencyId = currencyId;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}
	}
	
}