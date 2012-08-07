package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import org.openXpertya.process.DocAction;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

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
	
	private int C_Currency_ID = Env.getContextAsInt( getCtx(), "$C_Currency_ID" );
	
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
	private void setAllocationHdr(MAllocationHdr allocationHdr) {
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
		Document newDocument = createDocument(docID, docType, amount.abs()); 
		// No se permiten montos de imputación iguales a cero.
		if (amount.compareTo(BigDecimal.ZERO) == 0)
			throw new IllegalArgumentException("Allocation amount must be greather than zero");
		// Se busca si el documento existe en la lista.
		if (list.contains(newDocument)) {
			// En ese caso se incrementa el monto a impuatar del documento existente.
			int index = list.indexOf(newDocument);
			Document oldDocument = list.get(index);
			oldDocument.amount = oldDocument.amount.add(newDocument.amount);
		} else {
			list.add(newDocument);
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
		
		// Si hay al menos un débito y un crédito entonces la imputación no puede ser parcial
		// con lo cual los totales de débitos y créditos deben coincidir.
		if (hasDebits() && hasCredits()) {
			// Comparación exacta (sin redondeos)
			// TODO: Ver si sería posible la tolerancia de algunos centavos de diferencia en
			// esta comparación.
			if (getDebitsAmount().compareTo(getCreditsAmount() ) != 0) {
				if ( Math.abs(  (getDebitsAmount().subtract(getCreditsAmount())).doubleValue() ) > 0.01 ) 
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
					throw new AllocationGeneratorException(getMsg("NoConversionRate") + ": " + document.getCurrencyId() + " - " + C_Currency_ID);
			totalAmount = totalAmount.add(document.getConvertedAmount());
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
		// TODO: El Saldo debería ser cero aunque actualmente puede ser distinto de cero 
		// y esta diferencia se agrega como WriteOff en la primer línea de asignación creada
		// Ver como mejorar esta solución para que sea configurable.
		
		for (Document debitDocument : getDebits()) {
			// Se recorren todos los débitos para ser imputados con los créditos.
			// Se puede dar el caso que el monto de imputación de un débito
			// requiera mas de un crédito para ser satisfacido.
			if (debitDocument.getConvertedAmount() == null)
				throw new AllocationGeneratorException(getMsg("NoConversionRate") + ": " + debitDocument.getCurrencyId() + " - " + C_Currency_ID);
			BigDecimal debitAmount = debitDocument.getConvertedAmount();   // Monto a cubrir del débito
			
			BigDecimal creditAmountSum;
			if	(creditSurplus != null){                 // Inicializar lo que se cubre 
				creditAmountSum = creditSurplus;
			}
			else{                      // Si hay sobrante, entonces se utiliza
				if (getCredits().get(creditIdx).getConvertedAmount() == null)
					throw new AllocationGeneratorException(getMsg("NoConversionRate") + ": " + getCredits().get(creditIdx).getCurrencyId() + " - " + C_Currency_ID);
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
			while (debitAmount.compareTo(creditAmountSum) > 0) { 
				creditIdx++;
				Document credit = getCredits().get(creditIdx); // Siguiente crédito
				// Se actualiza la lista de créditos utilizados, la lista de montos
				// y la suma de los montos de todos los créditos utilizados.
				subCredits.add(credit);
				if (credit.getConvertedAmount() == null)
					throw new AllocationGeneratorException(getMsg("NoConversionRate") + ": " + credit.getCurrencyId() + " - " + C_Currency_ID);
				subCreditsAmounts.add(credit.getConvertedAmount());
				creditAmountSum = creditAmountSum.add(credit.getConvertedAmount());
			}
			
			// Si la suma de créditos supera el monto del débito, se resta la diferencia
			// al último crédito utilizado y esta diferencia pasa a ser el monto sobrante
			// del crédito actual.
			if (debitAmount.compareTo(creditAmountSum) < 0) {
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
				// FIXME: Validar que esta diferencia esté dentro de un rango permitido.
				if (balance.signum() != 0) {
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
	protected abstract class Document {

		public Integer id;
		public AllocationDocumentType type;
		public BigDecimal amount;
		public Integer currencyId;
		public Timestamp date;
		
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
			return MCurrency.currencyConvert(this.amount, this.currencyId, Env.getContextAsInt( getCtx(), "$C_Currency_ID" ), this.date, Env.getAD_Org_ID(getCtx()), getCtx());
			//return this.amount;
		}

		public Integer getCurrencyId() {
			return currencyId;
		}
	}
	
	/**
	 * Tipo de Crédito / Débito: Factura
	 */
	protected class Invoice extends Document {

		/**
		 * @param id ID de la factura
		 * @param amount Monto a imputar
		 */
		public Invoice(Integer id, BigDecimal amount) {
			super(id, AllocationDocumentType.INVOICE, amount);
			this.currencyId = getSqlCurrencyId();
			this.date = getSqlDate();
		}

		private Integer getSqlCurrencyId() {
			return DB.getSQLValue(getTrxName(), "SELECT C_Currency_ID FROM C_Invoice WHERE C_Invoice_ID = ?", id);
		}
		
		private Timestamp getSqlDate() {
			return DB.getSQLValueTimestamp(getTrxName(), "SELECT DateInvoiced FROM C_Invoice WHERE C_Invoice_ID = "+ id);
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
	}

	/**
	 * Tipo de Crédito / Débito: Línea de Caja
	 */
	protected class CashLine extends Document {

		/**
		 * @param id ID de la línea de caja
		 * @param amount Monto a imputar
		 */
		public CashLine(Integer id, BigDecimal amount) {
			super(id, AllocationDocumentType.CASH_LINE, amount);
			this.currencyId = getSqlCurrencyId();
			this.date = getSqlDate();
		}
		
		private Integer getSqlCurrencyId() {
			return DB.getSQLValue(getTrxName(), "SELECT C_Currency_ID FROM C_CashLine WHERE C_CashLine_ID = ?", id);
		}
		
		private Timestamp getSqlDate() {
			return DB.getSQLValueTimestamp(getTrxName(), "SELECT StatementDate FROM C_Cash c INNER JOIN C_CashLine cl ON c.C_Cash_ID = cl.C_Cash_ID WHERE C_CashLine_ID = "+ id);
		}
		
		@Override
		public void setAsCreditIn(MAllocationLine allocationLine) {
			allocationLine.setC_CashLine_ID(id);			
		}

		@Override
		public void setAsDebitIn(MAllocationLine allocationLine) {
			allocationLine.setC_CashLine_ID(id);
		}
	}

	/**
	 * Tipo de Crédito / Débito: Pago
	 */
	protected class Payment extends Document {

		/**
		 * @param id ID del pago
		 * @param amount Monto a imputar
		 */
		public Payment(Integer id, BigDecimal amount) {
			super(id, AllocationDocumentType.PAYMENT, amount);
			this.currencyId = getSqlCurrencyId();
			this.date = getSqlDate();
		}
		
		private Integer getSqlCurrencyId() {
			return DB.getSQLValue(getTrxName(), "SELECT C_Currency_ID FROM C_Payment WHERE C_Payment_ID = ?", id);
		}
		
		private Timestamp getSqlDate() {
			return DB.getSQLValueTimestamp(getTrxName(), "SELECT DateTrx FROM C_Payment WHERE C_Payment_ID = "+ id);
		}
		
		@Override
		public void setAsCreditIn(MAllocationLine allocationLine) {
			allocationLine.setC_Payment_ID(id);			
		}

		@Override
		public void setAsDebitIn(MAllocationLine allocationLine) {
			allocationLine.setC_Payment_ID(id);
		}
	}
}


