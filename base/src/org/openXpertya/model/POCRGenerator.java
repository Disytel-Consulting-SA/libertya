package org.openXpertya.model;

import java.math.BigDecimal;
import java.util.Properties;

import org.openXpertya.util.Util;

/**
 * Generador de Ordenes de Pago & Recibos de Cliente. Esta clase es una especialización
 * de {@link AllocationGenerator} y contiene validaciones específicas de estos tipos
 * especiales de asignación, como así también cierta lógica extra necesaria para facilitar
 * la generación de OP y RC.<br><br>
 * 
 * Esta clase contiene métodos específicos para agregar tanto las facturas a pagar, como
 * los medios de pagos y sus montos. Las facturas son agregadas como débitos en la asignación,
 * mientras que los medios de pagos se agregan a la lista de créditos de la misma. Sabiendo 
 * esto, los métodos {@link AllocationGenerator#hasDebits()} y {@link AllocationGenerator#hasCredits()}
 * permiten saber si se han agregado facturas o medios de pagos respectivamente.<br><br> 
 * 
 * Al instancia un <code>POCRGenerator</code> se debe indicar el tipo de documento que se 
 * quiere generar (Orden de Pago o Recibo de Cliente). En la propia instanciación se crea
 * el encabezado de asignación para la OP/RC. Este encabezado se puede acceder mediante el
 * método {@link #getAllocationHdr()}. El tipo de asignación se determinará automáticamente
 * según el tipo de documento indicado en la instanciación junto con la cantidad de facturas
 * que se agreguen al generador. Por ejemplo, si instanciamos el generador con el tipo
 * <code>POCRType.PAYMENT_ORDER</code> y luego se agrega una o mas facturas, el tipo de
 * asignación será 'OP', mientras que si no se agrega factura alguna el tipo se asignará
 * a 'OPA', dado que solo se están efectuando pagos adelantados.<br><br>
 * 
 * Hay que tener en cuenta que es posible configurar al generador sin agregarle facturas
 * (lo que determina una OP o RC adelantado), pero no es posible no agregar medios de pago.
 * En tal caso se disparará una excepción a la hora de generar las líneas ({@link #generateLines()}
 *  
 * @author Franco Bonafine - Disytel
 * @date 03/06/2009
 */
public class POCRGenerator extends AllocationGenerator {

	/**
	 * Tipo del documento a generar
	 * <ul>
	 * 	<li>Orden de Pago</li>
	 * 	<li>Recibo de Cliente</li>
	 *</ul>
	 */
	public enum POCRType {
		PAYMENT_ORDER,
		CUSTOMER_RECEIPT
	}
	
	/** Tipo de documento */
	private POCRType type;
	
	/** Forma de Pago */
	private String paymentRule;
	
//	/**
//	 * Constructor del Generador de OP & RC.
//	 * Crea un generador de OP & RC creando el encabezado de asignación 
//	 * accesible desde {@link #getAllocationHdr()}.
//	 * El tipo de documento indica si se debe generar una Orden de Pago o un
//	 * Recibo de Cliente. Luego a partir de las facturas y medios de pago se
//	 * determina si la orden o el recibo es adelantado.   
//	 * @param ctx Contexto para la creación de objetos
//	 * @param type Tipo de documento a crear
//	 * @param trxName Transacción de BD a utilizar por el generador
//	 * @throws AllocationGeneratorException cuando de produce un error en la creación
//	 * del encabezado de asignación de la OP/RC.
//	 */
//	public POCRGenerator(Properties ctx, POCRType type, String trxName) throws AllocationGeneratorException {
//		super(ctx, trxName);
//		this.type = type;
//		createAllocationHdr(getAllocationType());
//	}
	
	/**
	 * Constructor del Generador de OP & RC.
	 * Crea un generador de OP & RC creando el encabezado de asignación 
	 * accesible desde {@link #getAllocationHdr()}.
	 * El tipo de documento indica si se debe generar una Orden de Pago o un
	 * Recibo de Cliente. Luego a partir de las facturas y medios de pago se
	 * determina si la orden o el recibo es adelantado.   
	 * @param ctx Contexto para la creación de objetos
	 * @param type Tipo de documento a crear
	 * @param trxName Transacción de BD a utilizar por el generador
	 * @throws AllocationGeneratorException cuando de produce un error en la creación
	 * del encabezado de asignación de la OP/RC.
	 */
	public POCRGenerator(Properties ctx, POCRType type, String trxName){
		this(ctx, type, MInvoice.PAYMENTRULE_OnCredit, trxName);
	}
	
	/**
	 * Constructor del Generador de OP & RC.
	 * Crea un generador de OP & RC creando el encabezado de asignación 
	 * accesible desde {@link #getAllocationHdr()}.
	 * El tipo de documento indica si se debe generar una Orden de Pago o un
	 * Recibo de Cliente. Luego a partir de las facturas y medios de pago se
	 * determina si la orden o el recibo es adelantado.   
	 * @param ctx Contexto para la creación de objetos
	 * @param type Tipo de documento a crear
	 * @param paymentRule Forma de Pago de los documentos
	 * @param trxName Transacción de BD a utilizar por el generador
	 * @throws AllocationGeneratorException cuando de produce un error en la creación
	 * del encabezado de asignación de la OP/RC.
	 */
	public POCRGenerator(Properties ctx, POCRType type, String paymentRule, String trxName){
		super(ctx, trxName);
		this.type = type;
		if(!Util.isEmpty(paymentRule, true)){
			this.paymentRule = paymentRule;
		}
	}
	
	/**
	 * Agrega una factura a ser pagada.
	 * @param invoiceID ID de la factura a pagar
	 * @param toPay Monto del pago
	 * @return este <code>POCRGenerator</code>
	 */
	public POCRGenerator addInvoice(int invoiceID, BigDecimal toPay) {
		addDebitInvoice(invoiceID, toPay);
		// Si es la primera factura que se agrega se reasigna el
		// tipo de asignación dado que pasará de ser Adelantado a Normal
		if (getDebits().size() == 1)
			setAllocationType();
		return this;
	}
	
	/**
	 * Agrega un medio de pago a la lista de medios de pago de la OP/RC.
	 * En caso de que el documento ya se encuentre en la lista de medios de pago
	 * (mismo Tipo e ID), entonces se suma el <code>amount</code> al documento
	 * existente en la lista.
	 * @param docID ID del documento a agregar
	 * @param amount Monto de pago
	 * @param docType Tipo del documento
	 * @return Este <code>POCRGenerator</code>
	 */	
	public POCRGenerator addPaymentMedium(int docID, BigDecimal amount, AllocationDocumentType docType) {
		return (POCRGenerator)addCreditDocument(docID, amount, docType);
	}

	/**
	 * Agrega un pago (C_Payment) como medio de pago a la lista de medios de pago de la OP/RC.
	 * @param paymentID ID del pago a agregar
	 * @param amount Monto de pago
	 * @return Este <code>POCRGenerator</code>
	 * @see POCRGenerator#addPaymentMedium(int, BigDecimal, AllocationDocumentType)
	 */	
	public POCRGenerator addPaymentPaymentMedium(int paymentID, BigDecimal amount) {
		return (POCRGenerator)addCreditPayment(paymentID, amount);
	}

	/**
	 * Agrega una línea de caja (C_CashLine) como medio de pago a la lista 
	 * de medios de pago de la OP/RC.
	 * @param cashLineID ID de la línea de caja a agregar
	 * @param amount Monto de pago
	 * @return Este <code>POCRGenerator</code>
	 * @see POCRGenerator#addPaymentMedium(int, BigDecimal, AllocationDocumentType)
	 */	
	public POCRGenerator addCashLinePaymentMedium(int cashLineID, BigDecimal amount) {
		return (POCRGenerator)addCreditCashLine(cashLineID, amount);
	}

	/**
	 * Agrega una factura (C_Invoice) como medio de pago a la lista 
	 * de medios de pago de la OP/RC.
	 * @param invoiceID ID de la factura a agregar
	 * @param amount Monto de pago
	 * @return Este <code>POCRGenerator</code>
	 * @see POCRGenerator#addPaymentMedium(int, BigDecimal, AllocationDocumentType)
	 */	
	public POCRGenerator addInvoicePaymentMedium(int invoiceID, BigDecimal amount) {
		return (POCRGenerator)addCreditInvoice(invoiceID, amount);
	}
		
	/**
	 * @return the type
	 */
	public POCRType getType() {
		return type;
	}
	
	/**
	 * @return Devuelve el monto total de las facturas agregadas 
	 * @throws AllocationGeneratorException 
	 */
	public BigDecimal getInvoiceTotal() throws AllocationGeneratorException {
		return getDebitsAmount();
	}
	
	/**
	 * @return Devuelve el monto total de los medios de pagos agregados
	 * @throws AllocationGeneratorException 
	 */
	public BigDecimal getPaymentMediumTotal() throws AllocationGeneratorException {
		return getCreditsAmount();
	}
	
	@Override
	protected BigDecimal getAllocationLineAmountFor(Document document) {
		BigDecimal amount = null;
		// El monto de la línea de asignación de una OPA o RCA debe ser cero
		// para que no surta efecto en los pendientes de los documentos.
		if (getAllocationHdr().isAdvanced()) {
			amount = BigDecimal.ZERO;
		} else {
			amount = document.amount;
		}
		return amount;
	}

	@Override
	protected void customValidate() throws AllocationGeneratorException {
		// Se requieren medios de pago para toda OP o RC.
		if (!hasCredits()) {
			throw new AllocationGeneratorException("PaymentMediumsRequiredError");
		}
		// Se reasigna el tipo de asignación teniendo en cuenta que externamente
		// se podría haber modificado y la asignación se crearía con un tipo
		// erróneo.
		setAllocationType();
	}

	/**
	 * Verifica si un tipo de asignación es válido para este generador
	 */
	@SuppressWarnings("unused")
	private boolean validAllocationType(String allocationType) {
		boolean valid = true;
		if (!MAllocationHdr.ALLOCATIONTYPE_PaymentOrder.equals(allocationType)
				&& !MAllocationHdr.ALLOCATIONTYPE_CustomerReceipt.equals(allocationType)
				&& !MAllocationHdr.ALLOCATIONTYPE_AdvancedPaymentOrder.equals(allocationType)
				&& !MAllocationHdr.ALLOCATIONTYPE_AdvancedCustomerReceipt.equals(allocationType)) {
			
			valid = false;
		}
		return valid;
	}
	
	/**
	 * Asigna el tipo de asignación del encabezado.
	 */
	private void setAllocationType() {
		getAllocationHdr().setAllocationType(getAllocationType());
	}
	
	/**
	 * @return Devuelve el tipo de asignación a utilizar a partir del tipo de documento
	 * de este generador, y de las facturas y medios de pago agregadas.
	 */
	private String getAllocationType() {
		String allocationType = null;
		boolean hasInvoices = hasDebits(); // Indica si se agregaron facturas
		// Orden de Pago
		if (getType() == POCRType.PAYMENT_ORDER) {
			allocationType = MAllocationHdr.ALLOCATIONTYPE_PaymentOrder;
			// Si no hay facturas a pagar entonces es adelantado
			if (!hasInvoices) {
				allocationType = MAllocationHdr.ALLOCATIONTYPE_AdvancedPaymentOrder;
			}
		// Recibo de Cliente
		} else if (getType() == POCRType.CUSTOMER_RECEIPT) {
			allocationType = MAllocationHdr.ALLOCATIONTYPE_CustomerReceipt;
			// Si no hay facturas a pagar entonces es adelantado
			if (!hasInvoices) {
				allocationType = MAllocationHdr.ALLOCATIONTYPE_AdvancedCustomerReceipt;
			}
		}
		return allocationType;
	}

	public void reset() {
		getDebits().clear();
		getCredits().clear();
		setAllocationHdr(null);
	}
	
	/**
	 * Genera las líneas de asignación correspondientes al encabezado de asignación
	 * configurado, a partir de los documentos de débitos y créditos agregados al
	 * generador.
	 * @throws AllocationGeneratorException cuando existe un problema en la creación de 
	 * las líneas de asignación.
	 */
	public void generateLines() throws AllocationGeneratorException {
		if (((getAllocationType() == MAllocationHdr.ALLOCATIONTYPE_PaymentOrder) || (getAllocationType() == MAllocationHdr.ALLOCATIONTYPE_CustomerReceipt)) && (getDebits().isEmpty())){
			throw new AllocationGeneratorException(getMsg("CreditsOrDebitsRequiredError"));
		}
		super.generateLines();
	}

	public String getPaymentRule() {
		return paymentRule;
	}

	public void setPaymentRule(String paymentRule) {
		this.paymentRule = paymentRule;
	}
	
	
}
