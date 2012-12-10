package org.openXpertya.pos.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openXpertya.model.DiscountCalculator;
import org.openXpertya.model.DiscountCalculator.GeneralDiscountKind;
import org.openXpertya.model.DiscountCalculator.IDocument;
import org.openXpertya.model.MBPartner;
import org.openXpertya.util.Env;

public class Order  {

	private static final BigDecimal ROUND_TOLERANCE = new BigDecimal(0.01);
	
	private int id = 0;
	private Timestamp date = Env.getDate();
	private List<OrderProduct> orderProducts;
	private List<Payment> payments;
	private BusinessPartner businessPartner;
	private int orderRep;
	private BigDecimal changeAmount;
	private BigDecimal totalDocumentDiscount = BigDecimal.ZERO;
	private BigDecimal totalBPartnerDiscount = BigDecimal.ZERO;
	private BigDecimal totalManualGeneralDiscount = BigDecimal.ZERO;
	private List<Tax> otherTaxes = new ArrayList<Tax>();
	
	/** Precisión para importes */ 
	private int stdPrecision = 2;
	/** Preciosión para precios */
	private int costingPresicion = 4;
	
	/** Calculador de descuentos */
	private DiscountCalculator discountCalculator = null;
	
	/** ID del Pedido (C_Order) generado a partir de este pedido TPV */
	private int generatedOrderID = 0;
	
	/** ID de la Factura (C_Invoice) generada a partir de este pedido TPV */
	private int generatedInvoiceID = 0;
	
	/** Esquema de vencimientos */
	private PaymentTerm paymentTerm;
	
	/** Organización del pedido */
	private Organization organization;
		
	private Order() {
		super();
		setOrderProducts(new ArrayList<OrderProduct>());
		setPayments(new ArrayList<Payment>());
		discountCalculator = DiscountCalculator.create(
				getDiscountableOrderWrapper(), MBPartner.DISCOUNTCONTEXT_Bill);
		// Se cargan los descuentos configurados en este momento ya que la instanciación de un
		// Order se debe hacer en modo online.
		discountCalculator.loadConfiguredDiscounts();
	}
	
	public Order(Organization organization){
		this();
		setOrganization(organization);
	}

	public void addOrderProduct(OrderProduct orderProduct) {
		getOrderProducts().add(orderProduct);
		orderProduct.setOrder(this);
		updateDiscounts();
	}
	
	public void removeOrderProduct(OrderProduct orderProduct) {
		getOrderProducts().remove(orderProduct);
		orderProduct.setOrder(null);
		updateDiscounts();
	}
	
	public void updateOrderProduct() {
		updateDiscounts();
	}

	/**
	 * @return Devuelve date.
	 */
	public Timestamp getDate() {
		return date;
	}

	/**
	 * @param date
	 *            Fija o asigna date.
	 */
	public void setDate(Timestamp date) {
		this.date = date;
	}

	/**
	 * @return Devuelve orderProducts.
	 */
	public List<OrderProduct> getOrderProducts() {
		return orderProducts;
	}

	/**
	 * @param orderProducts Fija o asigna orderProducts.
	 */
	public void setOrderProducts(List<OrderProduct> orderProducts) {
		this.orderProducts = orderProducts;
	}

	/**
	 * @return Devuelve payments.
	 */
	public List<Payment> getPayments() {
		return payments;
	}

	/**
	 * @param payments Fija o asigna payments.
	 */
	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}
	
	/**
	 * @return Devuelve vendedor.
	 */
	public int getOrderRep() {
		return orderRep;
	}
	
	/**
	 * Asigna el vendedor
	 */
	public void setOrderRep(int orderRep) {
		this.orderRep = orderRep;
	}

	/**
	 * @return Devuelve client.
	 */
	public BusinessPartner getBusinessPartner() {
		return businessPartner;
	}

	/**
	 * Asigna la Entidad Comercial del pedido. Se recalculan descuentos
	 * si la EC tiene algún descuento asociado.
	 * @param businessPartner Entidad Comercial a asignar
	 */
	public void setBusinessPartner(BusinessPartner businessPartner) {
		this.businessPartner = businessPartner;
		// Por defecto no hay descuento de EC
		DiscountSchema discountSchema = null;
		BigDecimal flatDiscount = null;
		String discountContext = null;
		// Se obtiene el descuento asociado a la EC si no es null
		if(businessPartner != null){
			discountSchema = businessPartner.getDiscountSchema();
			flatDiscount = businessPartner.getFlatDiscount();
			discountContext = businessPartner.getDiscountSchemaContext();
		}
		else{
			setOtherTaxes(new ArrayList<Tax>());
		}
		// Se asigna el descuento de EC al 
		getDiscountCalculator().loadBPartnerDiscount(discountSchema,
				flatDiscount, discountContext);
		// Actualiza los descuentos
		updateDiscounts();
	}
	
	/**
	 * Actualiza los impuestos adicionales a cada línea
	 */
	public void updateOtherTaxesInLines(){
		for (OrderProduct op : getOrderProducts()) {
			op.updateOtherTaxes();
		}
	}
	
	/**
	 * @return El importe total del pedido, incluyendo impuestos y descuentos a
	 *         nivel de documento.
	 */
	public BigDecimal getTotalAmount() {
		return getOrderProductsTotalAmt().subtract(getTotalDocumentDiscount());
	}

	/**
	 * @return El importe total de los artículos agregados a este pedido
	 *         incluyendo impuestos. Este importe no contempla descuentos a
	 *         nivel documento. Para obtener el importe total de artículos
	 *         incluyendo descuentos a nivel documento utilizar el método
	 *         {@link #getTotalAmount()}.
	 */
	public BigDecimal getOrderProductsTotalAmt() {
		BigDecimal amount = BigDecimal.ZERO;
		// Suma el importe total con impuestos de cada artículo en el pedido
		Map<Tax, BigDecimal> taxBaseAmt = new HashMap<Tax, BigDecimal>();
		BigDecimal baseAmt = null;
		BigDecimal totalNetAmt = BigDecimal.ZERO;
		boolean isPerceptionIncludedInPrice = true;
		for (OrderProduct orderProduct : getOrderProducts()) {
			isPerceptionIncludedInPrice = orderProduct.getProduct().isPerceptionIncludedInPrice();
			//amount = amount.add(orderProduct.getTotalTaxedPrice());
			BigDecimal lineAmt = orderProduct.getPrice().multiply(orderProduct.getCount());
			if (orderProduct.getProduct().isTaxIncludedInPrice()) {
				amount = amount.add(lineAmt);
				lineAmt = orderProduct.getNetPrice(lineAmt);
			} else {
				Tax tax = orderProduct.getTax();
				baseAmt = lineAmt;
				if (taxBaseAmt.containsKey(tax)) {
					baseAmt = baseAmt.add(taxBaseAmt.get(tax));
				}
				taxBaseAmt.put(tax, baseAmt);
			}
			totalNetAmt = totalNetAmt.add(lineAmt);
		}
		
		for (Tax tax : taxBaseAmt.keySet()) {
			baseAmt = scaleAmount(taxBaseAmt.get(tax));
			amount = amount.add(baseAmt).add(baseAmt.multiply(tax.getTaxRateMultiplier()));
		}
		
		if (!isPerceptionIncludedInPrice){
			// Impuestos adicionales
			for (Tax otherTax : getOtherTaxes()) {
				baseAmt = scaleAmount(totalNetAmt);
				amount = amount.add(baseAmt.multiply(otherTax.getTaxRateMultiplier()));
			}
		}

		
		// return AmountHelper.scale(amount);
		return scaleAmount(amount);
	}

	/**
	 * Agrega un pago a la lista de pagos de este pedido. Se actualizan los
	 * importes de descuentos si el pago tiene esquema de descuento asociado.
	 * 
	 * @param payment
	 *            Pago a agregar
	 */
	public void addPayment(Payment payment) {
		Payment existentPayment = null;
		// Se busca si ya existe un pago en Efectivo o a Crédito cuyas
		// características de comparación sean iguales a las del pago que se
		// quiere agregar. En ese caso se suman los importes al pago existente y
		// no se agrega un nuevo pago a la lista
		// - Pago en Efectivo:
		if (payment.isCashPayment()) {
			CashPayment existentCashPayment = findCashPaymentLike(payment);
			if (existentCashPayment != null) {
				existentCashPayment.addAmount(payment.getAmount());
				existentCashPayment.addConvertedAmount(payment.getConvertedAmount());
				existentPayment = existentCashPayment;
			}
		// - Pago a Crédito:
		} else if (payment.isCreditPayment()){
			CreditPayment existentCreditPayment = findCreditPaymentLike(payment);
			if (existentCreditPayment != null) {
				existentCreditPayment.addAmount(payment.getAmount());
				existentPayment = existentCreditPayment;
			}
			// Asocio el paymentTerm al pedido
			setPaymentTerm(((CreditPayment)payment).getPaymentTerm());
		}
		
		// Si se actualizó un pago existente se lo quita de la lista de pagos
		// actuales a fin de recalcular correctamente el descuento del pago
		// total, como si el mismo hubiese sido agregado por el usuario de una
		// vez (y no en dos o mas partes). 
		if (existentPayment != null) {
			removePayment(existentPayment);
			payment = existentPayment;
		}
		
		//
		// Actualización de descuentos
		//

		// Actualiza el pago para determinar el importe real del mismo.
		calculatePaymentRealAmount(payment);
		
		// Agrega el esquema de descuento del pago como un descuento
		// general para el calculo del descuento total general del pedido.
		// Los descuentos se totalizan segun el medio de pago. Por eso que el ID
		// del descuento dentro del calculador se guarda y obtiene desde el
		// medio de pago.
		Integer discountID = payment.getPaymentMedium().getInternalID(payment);
		if (getDiscountCalculator().containsDiscount(discountID)) {
			getDiscountCalculator().addDiscountBaseAmount(discountID, payment.getRealAmount());
		} else {
			discountID = getDiscountCalculator().addGeneralDiscount(
					payment.getDiscountSchema(),
					GeneralDiscountKind.PaymentMedium,
					payment.getRealAmount(),
					payment.getPaymentMedium().getName());
			payment.getPaymentMedium().setInternalID(discountID, payment);
		}

		// Se aplican los descuentos generales al pedido
		getDiscountCalculator().applyDocumentHeaderDiscounts();
		
		// Si es un pago en efectivo y supera lo que resta pagar del pedido,
		// entonces la diferencia entre el efectivo y el pendiente es el cambio
		// del pago
		if(payment.isCashPayment()){
			BigDecimal pendingAmt = getTotalAmount().subtract(getPaidAmount());
			if(pendingAmt.compareTo(payment.getConvertedAmount()) < 0){
				payment.setChangeAmt(payment.getConvertedAmount().subtract(pendingAmt));
			}
			else{
				payment.setChangeAmt(BigDecimal.ZERO);
			}
		}
		
		// Se agrega el pago a la lista. Si era un pago existente, anteriormente
		// se había eliminado para el recálculo de descuentos y ahora se vuelve
		// a agregar
		getPayments().add(payment);
	}
	
	/**
	 * Calcula el importe real de un pago
	 * @param payment Pago
	 */
	private void calculatePaymentRealAmount(Payment payment) {
		/*
		 * Determina cual es el importe REAL del pago. El importe real es el
		 * importe que este pago cubre del total del pedido. Está aumentado o
		 * decrementado según el descuento que tenga asociado el medio de pago.
		 * Es decir, debemos calcular RP de forma que:
		 * 
		 *   RP = P / (1 - T)
		 * 
		 * Donde: 
		 * - RP: Importe real del pago 
		 * - P: Importe ingresado del pago 
		 * - T: Tasa del descuento aplicado (-1 < T < 1)
		 * 
		 * La tasa no la conocemos ya que un esquema de descuento puede tener
		 * una tasa del 10% pero sea aplicable solo a un subconjunto de las
		 * líneas del pedido, en ese caso la tasa no sería 10% sobre el total.
		 * Es por esto que se aplica el esquema de descuento sobre un importe
		 * constante, y al obtener el importe de descuento podemos obtener
		 * también la tasa de esa aplicación. De esta forma:
		 * 
		 *   T = C / D
		 * 
		 * Donde: 
		 * - C: Importe constante de aplicación (se usará 100) 
		 * - D: Importe del descuento basado en C.
		 */
		
		BigDecimal currentToPayAmt = getToPayAmount(payment.getPaymentMediumInfo());
		// Obtiene el importe del pago para el cálculo de descuento.
		// Si el pago es menor o igual al pendiente a pagar según el medio de
		// pago, entonces el importe del pago se toma como base para calcular el
		// descuento del medio de pago
		BigDecimal paymentAmt = null;
		if (payment.getAmount().compareTo(currentToPayAmt) <= 0) {
			paymentAmt = payment.getAmount();

		// Si el pago supera el importe pendiente, para no calcular
		// descuentos sobre importes que no se van a cobrar (e.d vueltos o
		// créditos a favor del cliente), el importe base para el descuento
		// es el importe pendiente de pago según el medio de pago indicado.
		} else {
			paymentAmt = currentToPayAmt;
		}
		
		// Previene la división por cero en el cálculo. (solo se puede dar para
		// recálculos de descuentos de líneas).
		if (paymentAmt.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
	
		// Efectúa el cálculo de la fórmula
		final int SCALE           = 20; 
		BigDecimal discount       = BigDecimal.ZERO;     // D
		BigDecimal rate           = BigDecimal.ZERO;     // T
		BigDecimal paymentRealAmt = null;                // RP
		BigDecimal constantAmt    = new BigDecimal(100); // C. Utilizado para calcular T
		
		// Calcula la tasa real de aplicación (puede diferir de la tasa del
		// esquema debido a aplicaciones parciales en las líneas - descuentos
		// selectivos -)
		if (payment.getDiscountSchema() != null
				&& isPaymentMediumDiscountApplicable(payment
						.getDiscountSchema().getDiscountContextType())) {
			discount = getDiscountCalculator().calculateDiscount(
					payment.getDiscountSchema(), constantAmt);
			rate = discount.divide(constantAmt, SCALE, BigDecimal.ROUND_HALF_EVEN);
		}
		// Calcula el importe real del pago a partir del importe original y la
		// tasa calculada.
		paymentRealAmt = paymentAmt.divide(BigDecimal.ONE.subtract(rate), SCALE,
				BigDecimal.ROUND_HALF_EVEN);
		
		// Obtiene el pendiente y lo compara con el importe real del pago. Si
		// hay una diferencia mínima de centavos agrega esta diferencia al pago
		// real para permitir completar el pedido
		BigDecimal openAmt = getOpenAmount();
		BigDecimal diff = openAmt.subtract(paymentRealAmt);
		if (diff.abs().compareTo(ROUND_TOLERANCE) <= 0) {
			paymentRealAmt = paymentRealAmt.add(diff);
		}
		
		// Guarda el importe real en el pago
		payment.setRealAmount(paymentRealAmt);
	}
	
	public void removePayment(Payment payment) {
		getPayments().remove(payment);
		paymentRemoved(payment);
		getDiscountCalculator().subtractDiscountBaseAmount(
				payment.getPaymentMedium().getInternalID(payment), payment.getRealAmount());
		updateDiscounts();
	}

	/**
	 * Operaciones que se deben realizar por la eliminación del pago dependiendo
	 * su tipo.
	 * 
	 * @param payment
	 *            pago
	 */
	protected void paymentRemoved(Payment payment){
		// Crédito
		if(payment.isCreditPayment()){
			// Anular el esquema de vencimientos configurado
			setPaymentTerm(null);
		}
	}
	
	
	public boolean hasPayments() {
		return getPayments().size() > 0;
	}
	
	public boolean hasOrderProducts() {
		return getOrderProducts().size() > 0;
	}
	
	public BigDecimal getPaidAmount() {
		BigDecimal paidAmt = BigDecimal.ZERO;
		for (Payment payment : getPayments()) {
			paidAmt = paidAmt.add(payment.getConvertedAmount());
		}
		return AmountHelper.scale(paidAmt);
	}

	/**
	 * @return El importe total neto pagado (sin contemplar
	 *         descuentos/recargos). Este método devuelve un {@link BigDecimal}
	 *         menor o igual que {@link #getOrderProductsTotalAmt()}
	 */
	private BigDecimal getRealPaidAmount() {
		BigDecimal realPaidAmt = BigDecimal.ZERO;
		for (Payment payment : getPayments()) {
			if (payment!=null && payment.getRealAmount() != null)
				realPaidAmt = realPaidAmt.add(payment.getRealAmount());
		}
		return realPaidAmt;
	}
	
	public BigDecimal getCashPaidAmount() {
		BigDecimal paidAmt = BigDecimal.ZERO;
		for (Payment payment : getPayments()) {
			if(payment.isCashPayment())
				paidAmt = paidAmt.add(payment.getConvertedAmount());
		}
		return AmountHelper.scale(paidAmt);
	}
	
	public BigDecimal getBalance() {
		return AmountHelper.scale(getPaidAmount().subtract(getTotalAmount()));
	}
	
	/**
	 * Recalcula los descuentos a nivel de líneas y documento.
	 */
	public void updateDiscounts() {
		/*
		 * Dado que el recalculo de descuentos de línea puede modificar el
		 * importe total del pedido, antes de hacer este cálculo se deben
		 * eliminar todos los pagos actualmente agregados al pedido para
		 * eliminar los descuentos a nivel de documento realizados por los medios
		 * de pago. Luego se recalculan los descuentos de líneas y finalmente se
		 * agregan nuevamente (en el mismo orden) los pagos que tenía el pedido
		 * a fin de recalcular correctamente los descuentos de los medios de
		 * pago tal como si hubiesen sido agregados por el usuario.
		 */
		
		// Guarda una copia de la lista de pagos actuales y elimina cada pago de
		// la lista de pagos asociados a este pedido
		List<Payment> currentPayments = new ArrayList<Payment>(getPayments());
		for (Payment payment : currentPayments) {
			removePayment(payment);
		}
		// Recalcula descuentos a nivel de línea.
		getDiscountCalculator().applyDocumentLineDiscounts();
		// Agrega nuevamente los pagos.
		for (Payment payment : currentPayments) {
			// Si ya se ha cubierto el total del pedido entonces se ignoran los
			// siguientes pagos (este caso se puede dar si al aplicar descuentos
			// a nivel de línea se ha reducido el importe total a pagar)
			if (getBalance().compareTo(BigDecimal.ZERO) < 0) {
				addPayment(payment);
			}
		}
		// Recalcula descuentos a nivel de documento.
		getDiscountCalculator().applyDocumentHeaderDiscounts();
	}
	
	public void updateManualGeneralDiscount(BigDecimal percentage){
		getDiscountCalculator().updateManualGeneralDiscount(percentage);
	}
	
	public BigDecimal getTotalChangeAmt(){
		BigDecimal changeAmt = BigDecimal.ZERO;
		for (Payment payment : getPayments()) {
			changeAmt = changeAmt.add(payment.getChangeAmt()); 
		}
		return changeAmt;
	}
	
	public BigDecimal getTotalChangeCashAmt(){
		BigDecimal changeAmt = BigDecimal.ZERO;
		for (Payment payment : getPayments()) {
			if(payment.isCashPayment()){
				changeAmt = changeAmt.add(payment.getChangeAmt()); 
			}
		}
		return changeAmt;
	}
	
	public void clear() {
		setId(0);
		this.businessPartner = null;
		getOrderProducts().clear();
		getPayments().clear();
		setTotalDocumentDiscount(BigDecimal.ZERO);
		getDiscountCalculator().reset();
		getDiscountCalculator().setDocument(getDiscountableOrderWrapper());
		setPaymentTerm(null);
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Agrega todas las líneas de un pedido a este pedido. Ambos pedidos referencian
	 * a las mismas líneas (no se hace una copia de la línea) con lo cual los cambios
	 * realizados en las líneas de un pedido serán reflejados en las líneas del otro.
	 * @param anotherOrder Pedido del cual se obtienen las líneas a agregar.
	 */
	public void addOrderProductsFrom(Order anotherOrder) {
		for (OrderProduct orderProduct : anotherOrder.getOrderProducts()) {
			addOrderProduct(orderProduct);
		}
		updateDiscounts();
	}
	
	/**
	 * @return Devuelve la cantidad de artículos en este pedido cuyo
	 * lugar de retiro es el TPV.
	 */
	public int getPOSCheckoutProductsCount() {
		int count = 0;
		for (OrderProduct orderProduct : getOrderProducts()) {
			count += orderProduct.isPOSCheckout()?1:0;
		}
		return count;
	}
	
	/**
	 * @return Devuelve la cantidad de artículos en este pedido cuyo
	 * lugar de retiro es el Almacén.
	 */
	public int getWarehouseCheckoutProductsCount() {
		int count = 0;
		for (OrderProduct orderProduct : getOrderProducts()) {
			count += orderProduct.isWarehouseCheckout()?1:0;
		}
		return count;
	}

	/**
	 * @return Devuelve el calculador de descuentos asociado a este pedido
	 */
	public DiscountCalculator getDiscountCalculator() {
		return discountCalculator;
	}

	/**
	 * @return Devuelve el importe pendiente de pago de este pedido sin tener en
	 *         cuenta el descuento de este pedido, es decir, el descuento del
	 *         pedido se toma como un importe pendiente de pago
	 */
	public BigDecimal getOpenAmount() {
		BigDecimal toPay = BigDecimal.ZERO;
		if (getBalance().compareTo(BigDecimal.ZERO) <= 0) {
			//toPay = getOrderProductsTotalAmt().subtract(getPaidAmount());
			toPay = getOrderProductsTotalAmt()
					.subtract(getTotalManualGeneralDiscount())
					.subtract(getRealPaidAmount())
					.subtract(getTotalBPartnerDiscount());
		}
		return toPay;
	}
	
	/**
	 * Devuelve el importe pendiente de pago de este pedido teniendo en cuenta
	 * el descuento / recargo asociado a un medio de pago para el calculo del
	 * importe final
	 * 
	 * @param paymentMediumInfo
	 *            Información del medio de pago que pondera el cálculo
	 * @return <ul>
	 *         <li>Si el medio de pago tiene un esquema de descuento asociado:
	 *         un {@link BigDecimal} con el importe pendiente descontado /
	 *         recargado</li>
	 *         <li>Si el medio de pago no tiene un esquema de descuento
	 *         asociado: el resultado de {@link #getOpenAmount()}</li>
	 */
	public BigDecimal getToPayAmount(IPaymentMediumInfo paymentMediumInfo) {
		// Calcula el descuento de documento según el esquema del Medio de Pago
		// Se ignora el esquema de EC ya que el mismo ya está (o no) aplicado
		// dentro del pedido 
		BigDecimal discountAmt = BigDecimal.ZERO;
		BigDecimal openAmt = getOpenAmount();

		if (paymentMediumInfo != null) {
			// Calcula el importe del descuento general a partir de importe
			// pendiente (que incluye los descuentos realizados al documento)
			if (paymentMediumInfo.getDiscountSchema() != null
					&& getDiscountCalculator()
							.isGeneralDocumentDiscountApplicable(
									paymentMediumInfo.getDiscountSchema()
											.getDiscountContextType())) {
				getDiscountCalculator().setApplyScale(false);
				discountAmt = getDiscountCalculator().calculateDiscount(
						paymentMediumInfo.getDiscountSchema(), openAmt);
				getDiscountCalculator().setApplyScale(true);
				discountAmt = getDiscountCalculator().scaleAmount(discountAmt);
			}
			
		}
		// Al pendiente se le resta el descuento del Medio de Pago, y se le
		// quita también el descuento total del pedido ya que ese importe no se
		// debe pagar.
		//return openAmt.subtract(discountAmt).subtract(getTotalDocumentDiscount());
		return openAmt.subtract(discountAmt);
	}

	/**
	 * Busca en la colección de pagos agregados a este pedido un pago que sea
	 * Efectivo y sus propiedades sean similares a un pago determinado
	 * Las condiciones de búsqueda son:<br>
	 * <ol>
	 * <li>Sea un {@link CashPayment}</li>
	 * <li>Tenga la misma moneda que el pago parámetro</li>
	 * <li>Tenga el mismo esquema de descuento que el pago parámetro</li>
	 * </ol>
     *
	 * @param payment
	 *            Pago de búsqueda. Si no es un {@link CashPayment} devuelve
	 *            <code>null</code>
	 * @return el {@link CashPayment} encontrado o <code>null</code> si no
	 *         existe ninguno con las características buscadas
	 */
	private CashPayment findCashPaymentLike(Payment payment) {
		CashPayment result = null;
		// El parámetro debe ser un CashPayment
		if(payment.isCashPayment()) {
			for (Payment p : getPayments()) {
				// Se busca un pago que:
				// 1. Sea un CashPayment
				// 2. Tenga la misma moneda que el pago parámetro
				// 3. Tenga el mismo esquema de descuento que el pago parámetro
				if(p.isCashPayment() 
						&& p.getCurrencyId() == payment.getCurrencyId()
						&& p.equalsDiscountSchema(payment.getDiscountSchema())) {
					
					result = (CashPayment)p;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * Busca en la colección de pagos agregados a este pedido un pago que sea
	 * A Crédito y sus propiedades sean similares a un pago determinado
	 * Las condiciones de búsqueda son:<br>
	 * <ol>
	 * <li>Sea un {@link CreditPayment}</li>
	 * <li>Tenga el mismo esquema de descuento que el pago parámetro</li>
	 * </ol>
     *
	 * @param payment
	 *            Pago de búsqueda. Si no es un {@link CreditPayment} devuelve
	 *            <code>null</code>
	 * @return el {@link CreditPayment} encontrado o <code>null</code> si no
	 *         existe ninguno con las características buscadas
	 */
	private CreditPayment findCreditPaymentLike(Payment payment) {
		CreditPayment result = null;
		// El parámetro debe ser un CreditPayment
		if(payment.isCreditPayment()) {
			for (Payment p : getPayments()) {
				// Se busca un pago que:
				// 1. Sea un CreditPayment
				// 3. Tenga el mismo esquema de descuento que el pago parámetro
				if(p.isCreditPayment() 
						&& p.equalsDiscountSchema(payment.getDiscountSchema())) {
					
					result = (CreditPayment)p;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * @return Este pedido de TPV wrappeado para poder ser utilizado por el
	 *         Calculador de Descuentos
	 */
	public IDocument getDiscountableOrderWrapper() {
		return new DiscountableOrderWrapper(this);
	}

	/**
	 * @return the totalDocumentDiscount
	 */
	public BigDecimal getTotalDocumentDiscount() {
		return totalDocumentDiscount;
	}

	/**
	 * @param totalDocumentDiscount the totalDocumentDiscount to set
	 */
	protected void setTotalDocumentDiscount(BigDecimal totalDocumentDiscount) {
		if (totalDocumentDiscount == null) {
			totalDocumentDiscount = BigDecimal.ZERO;
		}
		this.totalDocumentDiscount = totalDocumentDiscount;
	}

	/**
	 * @return the generatedOrderID
	 */
	public int getGeneratedOrderID() {
		return generatedOrderID;
	}

	/**
	 * @param generatedOrderID the generatedOrderID to set
	 */
	public void setGeneratedOrderID(int generatedOrderID) {
		this.generatedOrderID = generatedOrderID;
	}

	/**
	 * @return the generatedInvoiceID
	 */
	public int getGeneratedInvoiceID() {
		return generatedInvoiceID;
	}

	/**
	 * @param generatedInvoiceID the generatedInvoiceID to set
	 */
	public void setGeneratedInvoiceID(int generatedInvoiceID) {
		this.generatedInvoiceID = generatedInvoiceID;
	}
	
	/**
	 * @return Indica si son aplicables los descuentos por medios de pagos.
	 */
	public boolean isPaymentMediumDiscountApplicable() {
		return getDiscountCalculator().isGeneralDocumentDiscountApplicable();
	}

	/**
	 * @param discountContextType
	 *            tipo de contexto de descuento
	 * @return Indica si son aplicables los descuentos por medios de pagos en el
	 *         contexto pasado como parámetro
	 */
	public boolean isPaymentMediumDiscountApplicable(String discountContextType) {
		return getDiscountCalculator().isGeneralDocumentDiscountApplicable(
				discountContextType);
	}
	
	/**
	 * @return Indica si es aplicable el descuento de EC.
	 */
	public boolean isBPartnerDiscountApplicable() {
		return getDiscountCalculator()
				.isBPartnerDiscountApplicable(
						getBusinessPartner().getDiscountSchema() != null ? getBusinessPartner()
								.getDiscountSchema().getDiscountContextType()
								: null,
						getBusinessPartner().getDiscountSchemaContext());
	}

	/**
	 * @param totalBPartnerDiscount the totalBPartnerDiscount to set
	 */
	protected void setTotalBPartnerDiscount(BigDecimal totalBPartnerDiscount) {
		this.totalBPartnerDiscount = totalBPartnerDiscount;
	}

	/**
	 * @return the totalBPartnerDiscount
	 */
	protected BigDecimal getTotalBPartnerDiscount() {
		return totalBPartnerDiscount;
	}

	public void setPaymentTerm(PaymentTerm paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public PaymentTerm getPaymentTerm() {
		return paymentTerm;
	}

	/**
	 * @return el valor de stdPrecision
	 */
	public int getStdPrecision() {
		return stdPrecision;
	}

	/**
	 * @param stdPrecision el valor de stdPrecision a asignar
	 */
	public void setStdPrecision(int stdPrecision) {
		this.stdPrecision = stdPrecision;
	}

	/**
	 * @return el valor de costingPresicion
	 */
	public int getCostingPresicion() {
		return costingPresicion;
	}

	/**
	 * @param costingPresicion el valor de costingPresicion a asignar
	 */
	public void setCostingPresicion(int costingPresicion) {
		this.costingPresicion = costingPresicion;
	}
	
	/**
	 * Realiza el redondeo de importes según la precisión Std
	 * @param amount
	 * @return
	 */
	public BigDecimal scaleAmount(BigDecimal amount) {
		return amount.setScale(getStdPrecision(), BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * Realiza el redondeo de precios según la precisión Costing
	 * @param price
	 * @return
	 */
	public BigDecimal scalePrice(BigDecimal price) {
		return price.setScale(getCostingPresicion(), BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * @param value
	 *            Indica si el calculador de descuento debe asumir que existe un
	 *            descuento general agregado para calcular los descuento. Esto
	 *            es utilizado para el caso en que la configuración de
	 *            descuentos prioriza los esquemas generales, y actualmente la
	 *            EC tiene un esquema de dto y no se ha agregado ningún medio de
	 *            cobro con descuento. En el momento de seleccionar un medio de
	 *            cobro con descuento se debe anular el descuento de entidad
	 *            comercial.<br>
	 *            Al cambiar este valor se recalcularan los descuentos automáticamente.
	 */
	public void setAssumeGeneralDiscountAdded(boolean value) {
		getDiscountCalculator().setAssumeGeneralDiscountAdded(value);
		updateDiscounts();
	}

	/**
	 * @return Valor a devolver en efectivo de las notas de crédito existentes
	 */
	public BigDecimal getCreditNoteChangeAmount(){
		BigDecimal returnAmt = BigDecimal.ZERO;
		for (Payment payment : getPayments()) {
			if(payment.isCreditNotePayment())
				returnAmt = returnAmt.add(payment.getChangeAmt());
		}
		return AmountHelper.scale(returnAmt);
	}

	/**
	 * @param creditNoteID
	 *            id de nota de crédito
	 * @return true si existe como pago agregado anteriormente la nota de
	 *         crédito, false caso contrario
	 */
	public boolean existsCreditNote(Integer creditNoteID){
		boolean found = false;
		for (int i = 0; i < getPayments().size() && !found; i++) {
			found = getPayments().get(i).isCreditNotePayment()
					&& ((CreditNotePayment) getPayments().get(i))
							.getInvoiceID() == creditNoteID;
		}
		return found;
	}

	public void setTotalManualGeneralDiscount(BigDecimal totalManualGeneralDiscount) {
		this.totalManualGeneralDiscount = totalManualGeneralDiscount;
	}

	public BigDecimal getTotalManualGeneralDiscount() {
		return totalManualGeneralDiscount;
	}

	public void setOtherTaxes(List<Tax> otherTaxes) {
		this.otherTaxes = otherTaxes;
		updateOtherTaxesInLines();
	}

	public List<Tax> getOtherTaxes() {
		return otherTaxes;
	}
	
	public boolean isManualDiscountApplicable(OrderProduct orderProduct){
		return getDiscountCalculator().isManualDiscountApplicable(
				((DiscountableOrderWrapper) getDiscountCalculator()
						.getDocument()).createDocumentLine(orderProduct));
	}
	
	
	public Integer addLineManualDiscount(OrderProduct op){
		return getDiscountCalculator().addLineManualDiscount(
				((DiscountableOrderWrapper) getDiscountCalculator()
						.getDocument()).createDocumentLine(op),
				op.getDiscountApplication());
	}
	
	public Integer getOrgID(){
		return getOrganization().getId();
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
}
