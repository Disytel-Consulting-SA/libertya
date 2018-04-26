package org.openXpertya.pos.model;

import java.math.BigDecimal;

import org.openXpertya.model.DiscountCalculator.IDocumentLine.DiscountApplication;
import org.openXpertya.model.MProduct;

public class OrderProduct {
	
	private Order order;
	
	private BigDecimal count;

	private BigDecimal price;

	private BigDecimal discount;

	private Tax tax;
	
	private Product product;
	
	private String checkoutPlace;
	
	/** ID de la línea de pedido creada a partir de este artículo del pedido
	 * TPV. Mientras no se haya creado la MOrderLine el valor de este atributo
	 * es <code>null</code> */
	private Integer orderLineID = null;

	/** ID de la línea de factura creada a partir de este artículo del pedido
	 * TPV. Mientras no se haya creado la MInvoiceLine el valor de este atributo
	 * es <code>null</code> */
	private Integer invoiceLineID = null;
	
	private BigDecimal lineDiscountAmt = BigDecimal.ZERO;
	
	private BigDecimal lineBonusAmt = BigDecimal.ZERO;
	
	private DiscountApplication discountApplication;
	
	private Integer lineManualDiscountID;
	
	private String lineDescription;
	
	private BigDecimal totalDocumentDiscount = BigDecimal.ZERO;
	
	private BigDecimal temporalTotalDocumentDiscount = BigDecimal.ZERO;
	
	/** Precio discriminado */
	private BigDecimal netPrice = BigDecimal.ZERO;
	private BigDecimal taxPrice = BigDecimal.ZERO;
	private BigDecimal otherTaxPrice = BigDecimal.ZERO;
	
	public OrderProduct() {
		super();
	}


	/**
	 * @param count
	 * @param discount
	 * @param tax
	 * @param product
	 * @param checkoutPlace
	 */
	public OrderProduct(BigDecimal count, BigDecimal discount,
			Tax tax, Product product, String checkoutPlace) {
		this();
		setProduct(product);
		setCount(count);
		setTax(tax);
		setDiscount(discount, DiscountApplication.ToPrice);
		setPrice(getPriceList());
		setCheckoutPlace(checkoutPlace);
		calculatePrice();
	}

	/**
	 * @return Devuelve count.
	 */
	public BigDecimal getCount() {
		return count;
	}

	/**
	 * @param count
	 *            Fija o asigna count.
	 */
	public void setCount(BigDecimal count) {
		this.count = count;
	}

	/**
	 * @return Devuelve discount.
	 */
	public BigDecimal getDiscount() {
		return discount;
	}


	/**
	 * @param discount
	 *            Fija o asigna discount.
	 */
	public void setDiscount(BigDecimal discount, DiscountApplication discountApplication, boolean calculatePrice) {
		this.discount = discount;
		this.setDiscountApplication(discountApplication);
		
		if(getOrder() != null){
			getOrder().addLineManualDiscount(this);
		}
		
		if(calculatePrice){
			calculatePrice();
		}
	}

	public void setDiscount(BigDecimal discount, DiscountApplication discountApplication){
		setDiscount(discount, discountApplication, true);
	}
	
	/**
	 * @return Devuelve el precio de tarifa.
	 */
	public BigDecimal getPriceList() {
		// En TPV, el precio de Tarifa es el precio STD.
		return getProduct().getStdPrice();
	}
	
	/**
	 * @return Devuelve el precio límite del producto con impuesto aplicados.
	 */
	public BigDecimal getTaxedLimitPrice() {
		return getTaxedPrice(getProduct().getLimitPrice());
	}
	
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * @param price
	 *            Fija o asigna price.
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
		decomposePrice(price);
	}

	/**
	 * @return Devuelve taxAmount.
	 */
	public BigDecimal getTaxRate() {
		return getTax().getRate();
	}
	
	/**
	 * @return Returns the tax.
	 */
	public Tax getTax() {
		return tax;
	}

	/**
	 * @param tax The tax to set.
	 */
	public void setTax(Tax tax) {
		this.tax = tax;
	}

	/**
	 * @return Devuelve product.
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product Fija o asigna product.
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	
	/**
	 * @return El importe unitario final de la línea con impuestos (y bonificaciones).
	 */
	public BigDecimal getTaxedPrice() {
		return getTaxedPrice(false);
	}

	/**
	 * Calcula y devuelve el precio unitario con impuestos de esta línea.
	 * 
	 * @param excludeBonus
	 *            <p>
	 *            Si es <code>true</code> devuelve el precio unitario sin tener
	 *            en cuenta bonificaciones. Si esta línea tiene algún porcentaje
	 *            de descuento y además su tipo de aplicación es
	 *            {@link DiscountApplication#Bonus} entonces se devuelve el
	 *            precio unitario real de la línea tal como si no se hubieran
	 *            aplicado bonificaciones.
	 *            </p>
	 *            <p>
	 *            Si es <code>false</code> devuelve el precio unitario
	 *            final de la línea incluyendo los importes aplicados por
	 *            bonificación.
	 *            </p>
	 * @return El importe unitario de la línea incluyendo impuestos.
	 */
	public BigDecimal getTaxedPrice(boolean excludeBonus) {
		BigDecimal price = getPrice();
		if (excludeBonus) {
			price = (getPrice().multiply(getCount()).add(getLineBonusAmt()))
					.divide(getCount(), price.scale(), BigDecimal.ROUND_HALF_EVEN);
		}
		return getTaxedPrice(price);
	}

	public BigDecimal getTaxedPrice(BigDecimal price) {
		// Si el impuesto está incluído en el precio del producto, entonces
		// se retorna dicho precio.
		BigDecimal taxedPrice = price;

		// Sino, se calcula el nuevo precio sumando el monto implicado por la tasa
		// del impuesto del producto.
		if (!getProduct().isTaxIncludedInPrice()){
			taxedPrice = price.add(price.multiply(getTax().getTaxRateMultiplier()));
		}
		
		return scalePrice(taxedPrice);
	}

	public BigDecimal getTaxedAmount(BigDecimal amount) {
		BigDecimal taxedAmount = amount;

		// Sino, se calcula el nuevo precio sumando el monto implicado por la tasa
		// del impuesto del producto.
		if (!getProduct().isTaxIncludedInPrice()){
			taxedAmount = amount.add(amount.multiply(getTax().getTaxRateMultiplier()));
		}
		
		return scaleAmount(taxedAmount);
	}

	public BigDecimal getOtherTaxedAmount(BigDecimal amount) {
		BigDecimal otherTaxedAmount = amount;

		if (!getProduct().isPerceptionIncludedInPrice()){
			otherTaxedAmount = amount.add(amount.multiply(getOrder().getSumOtherTaxesRateMultipliers()));
		}
		
		return scaleAmount(otherTaxedAmount);
	}
	
	public BigDecimal getNetAmount(BigDecimal amount) {
		BigDecimal netAmount = amount;

		if (getProduct().isTaxIncludedInPrice()){
			netAmount = amount.divide(getTax().getTaxRateDivisor(), 6, BigDecimal.ROUND_HALF_UP);
		}
		
		return scaleAmount(netAmount);
	}
	
	
	/**
	 * Calcula el precio a partir de un precio con impuestos.
	 * Si el producto tiene el impuesto incluído en la tarifa, entonces
	 * el precio retornado es igual a <code>taxedPrice</code>.
	 * Este método es el inverso a <code>getTaxedPrice(BigDecimal price)</code>
	 */
	/*public BigDecimal getPrice(BigDecimal taxedPrice) {
		BigDecimal price = decomposePrice(taxedPrice);
		
		// Suma los multiplicadores de todos los impuestos
		BigDecimal rateMultipliers = getOrder().getSumOtherTaxesRateMultipliers();
		
		if (!getProduct().isTaxIncludedInPrice()){
			rateMultipliers = rateMultipliers.add(getTax().getTaxRateMultiplier());
			price = price.divide(BigDecimal.ONE.add(rateMultipliers),20, BigDecimal.ROUND_HALF_UP);
		}
		else{
			// Determino el neto
			BigDecimal rateMultipliersAux = rateMultipliers.add(getTax().getTaxRateMultiplier());
			BigDecimal priceAux = price.divide(BigDecimal.ONE.add(rateMultipliersAux),20, BigDecimal.ROUND_HALF_UP);
			// Le aplico las tasas de los impuestos adicionales para saber qué
			// parte del precio es de impuesto adicional, luego se lo resto al
			// precio parámetro
			price = price.subtract(priceAux.multiply(rateMultipliers));
		}
		
		return scalePrice(price);
	}*/

	/**
	 * @param price
	 *            precio con impuesto, incluído en la tarifa o no
	 * @return Precio neto
	 */
	/*public BigDecimal getNetPrice(BigDecimal taxedPrice){
		BigDecimal netPrice = taxedPrice.divide(getTax().getTaxRateDivisor(),
				20, BigDecimal.ROUND_HALF_UP);		
		
		return scalePrice(netPrice);
	}*/
	
	/**
	 * @param price
	 *            precio con todos los impuestos, incluído en la tarifa o no y
	 *            con impuestos extras
	 * @return Precio neto
	 */
	/*public BigDecimal getNetPriceAllTaxes(BigDecimal taxedPrice){
		BigDecimal netPrice = taxedPrice.divide(
				BigDecimal.ONE.add(getTax().getTaxRateMultiplier().add(
						getOrder().getSumOtherTaxesRateMultipliers())), 20,
				BigDecimal.ROUND_HALF_UP);		
		
		return scalePrice(netPrice);
	}*/
	
	/**
	 * @return El importe total final de la línea incluyendo bonificaciones
	 *         aplicadas.
	 */
	public BigDecimal getTotalTaxedPrice() {
		return getTotalTaxedPrice(false);
	}
	
	private void calculatePrice() {
		price = calculatePrice(getDiscount(), true);
	}
	
	private void calculateDiscount() {
		discount = calculateDiscount(getPrice());
	}
	
	public BigDecimal calculateDiscount(BigDecimal price) {
		BigDecimal cDiscount = BigDecimal.ZERO;
		if(getPriceList().compareTo(BigDecimal.ZERO) != 0) {
			BigDecimal diff = getPricesDiff(getPriceList(), price);
			cDiscount = diff.multiply(new BigDecimal(100)).divide(getPriceList(),10,BigDecimal.ROUND_HALF_UP);
		}
		return cDiscount;
	}
	
	public BigDecimal getPricesDiff(BigDecimal priceList, BigDecimal price) {
		return priceList.subtract(price);
	}

	public BigDecimal calculatePrice(BigDecimal discount, boolean decompose) {
		BigDecimal cPrice = getPriceList();
		if(discount != null) {
			cPrice = cPrice.subtract(cPrice.multiply(discount.divide(new BigDecimal(100),4,BigDecimal.ROUND_HALF_UP)));
		}
		if(decompose){
			cPrice = decomposePrice(cPrice);
		}
		return cPrice;
	}

	/**
	 * Descomponer el precio parámetro en importe neto, importe de impuesto
	 * incluído, importe de percepciones incluídas.
	 * 
	 * @param basePrice
	 *            precio base para la descomposición
	 */
	public BigDecimal decomposePrice(BigDecimal basePrice){
		BigDecimal includedTaxesDivisors = getAllIncludedTaxesMultipliers();
		
		netPrice = basePrice.divide(BigDecimal.ONE.add(includedTaxesDivisors), basePrice.scale(),
				BigDecimal.ROUND_HALF_UP);
		if(getProduct().isTaxIncludedInPrice()){
			taxPrice = netPrice.multiply(getTax().getTaxRateMultiplier()).setScale(basePrice.scale(),
					BigDecimal.ROUND_HALF_UP);
		}
		if(getProduct().isPerceptionIncludedInPrice()){
			otherTaxPrice = netPrice.multiply(getOrder().getSumOtherTaxesRateMultipliers()).setScale(basePrice.scale(),
					BigDecimal.ROUND_HALF_UP);
		}
		
		BigDecimal thePrice = netPrice.add(taxPrice).add(otherTaxPrice);
		BigDecimal diff = basePrice.subtract(thePrice);
		
		netPrice = netPrice.add(diff);
		
		return netPrice.add(taxPrice).add(otherTaxPrice);
	}
	
	/**
	 * @return suma de todos los impuestos incluídos en el precio
	 */
	public BigDecimal getAllIncludedTaxesMultipliers(){
		// Sumo las alícuotas de otros impuestos como percepciones incluídas
		BigDecimal taxesToNet = BigDecimal.ZERO;
		if(getProduct().isPerceptionIncludedInPrice()){
			taxesToNet = taxesToNet.add(getOrder().getSumOtherTaxesRateMultipliers());
		}
		// Sumo el impuesto si es que está incluído
		if(getProduct().isTaxIncludedInPrice()){
			taxesToNet = taxesToNet.add(getTax().getTaxRateMultiplier());
		}
		return taxesToNet;
	}
	
	/**
	 * @return the checkoutPlace
	 */
	public String getCheckoutPlace() {
		return checkoutPlace;
	}

	/**
	 * @param checkoutPlace the checkoutPlace to set
	 */
	public void setCheckoutPlace(String checkoutPlace) {
		this.checkoutPlace = checkoutPlace;
	}
	
	/**
	 * @return Indica si este artículo del pedido debe ser retirado por
	 * almacén (el TPV no lo incluye en el remito que genera)
	 */
	public boolean isWarehouseCheckout() {
		return MProduct.CHECKOUTPLACE_Warehouse.equals(getCheckoutPlace());
	}
	
	/**
	 * @return Indica si este artículo del pedido debe ser retirado por
	 * el mismo TPV (el TPV lo incluye en el remito generado)
	 */
	public boolean isPOSCheckout() {
		return MProduct.CHECKOUTPLACE_PointOfSale.equals(getCheckoutPlace());
	}

	/**
	 * @return Devuelve el ID de la línea de pedido creada a partir de este artículo
	 * del pedido TPV
	 */
	public Integer getOrderLineID() {
		return orderLineID;
	}

	/**
	 * @param orderLineID ID de la línea de pedido creada a partir de este artículo
	 * del pedido TPV
	 */
	public void setOrderLineID(Integer orderLineID) {
		this.orderLineID = orderLineID;
	}

	/**
	 * Devuelve el importe total de la línea con o sin bonificaciones según
	 * <code>excludeBonus</code>.
	 * 
	 * @param excludeBonus
	 *            <p>
	 *            Si es <code>true</code> devuelve el precio de esta línea sin
	 *            tener en cuenta bonificaciones. Si esta línea tiene algún
	 *            porcentaje de descuento y además su tipo de aplicación es
	 *            {@link DiscountApplication#Bonus} entonces se devuelve el
	 *            precio total real de la línea tal como si no se hubieran
	 *            aplicado bonificaciones.
	 *            </p>
	 *            <p>
	 *            Si es <code>false</code> devuelve el precio total final de la
	 *            línea incluyendo los importes aplicados por bonificación.
	 *            </p>
	 * 
	 * @return {@link BigDecimal} con el importe total de la línea.
	 */
	public BigDecimal getTotalTaxedPrice(boolean excludeBonus) {
		return scaleAmount(getTaxedPrice(getPrice(excludeBonus)).multiply(getCount()));
	}

	/**
	 * @return the lineDiscountAmt
	 */
	public BigDecimal getLineDiscountAmt() {
		return lineDiscountAmt;
	}


	/**
	 * @param lineDiscountAmt the lineDiscountAmt to set
	 */
	public void setLineDiscountAmt(BigDecimal lineDiscountAmt) {
		this.lineDiscountAmt = lineDiscountAmt;
	}


	/**
	 * @return the lineBonusAmt
	 */
	public BigDecimal getLineBonusAmt() {
		return lineBonusAmt;
	}


	/**
	 * @param lineBonusAmt the lineBonusAmt to set
	 */
	public void setLineBonusAmt(BigDecimal lineBonusAmt) {
		this.lineBonusAmt = lineBonusAmt;
	}

	/**
	 * @return Devuelve el importe de descuento unitario de la línea calculado a
	 *         partir de la diferencia entre el precio de tarifa y el precio
	 *         actual.
	 */
	private BigDecimal getDiscountAmt() {
		return getPriceList().subtract(getPrice());
	}

	@Override
	public String toString() {
		return "("+ getProduct().getId() + "," + getCount() + ")";
	}
	
	/**
	 * @return El precio de lista con impuestos
	 */
	public BigDecimal getTaxedPriceList() {
		return getTaxedPrice(getPriceList());
	}
	
	/**
	 * @return el valor de order
	 */
	public Order getOrder() {
		return order;
	}


	/**
	 * @param order el valor de order a asignar
	 */
	protected void setOrder(Order order) {
		this.order = order;
	}

	/**
	 * Realiza el redondeo de importes según la precisión Std
	 * @param amount
	 * @return
	 */
	public BigDecimal scaleAmount(BigDecimal amount) {
		return getOrder().scaleAmount(amount);
	}
	
	/**
	 * Realiza el redondeo de precios según la precisión Costing
	 * @param price
	 * @return
	 */
	public BigDecimal scalePrice(BigDecimal price) {
		return getOrder().scalePrice(price);
	}
	
	/**	
	 * Calcula y devuelve el precio unitario sin impuestos de esta línea.
	 * 
	 * @param excludeBonus
	 *            <p>
	 *            Si es <code>true</code> devuelve el precio unitario sin tener
	 *            en cuenta bonificaciones. Si esta línea tiene algún porcentaje
	 *            de descuento y además su tipo de aplicación es
	 *            {@link DiscountApplication#Bonus} entonces se devuelve el
	 *            precio unitario real de la línea tal como si no se hubieran
	 *            aplicado bonificaciones.
	 *            </p>
	 *            <p>
	 *            Si es <code>false</code> devuelve el precio unitario
	 *            final de la línea incluyendo los importes aplicados por
	 *            bonificación.
	 *            </p>
	 * @return El importe unitario de la línea SIN impuestos.
	 */
	protected BigDecimal getPrice(boolean excludeBonus) {
		BigDecimal price = getPrice();
		if (excludeBonus) {
			price = (getPrice().multiply(getCount()).add(getLineBonusAmt()))
					.divide(getCount(), price.scale(), BigDecimal.ROUND_HALF_EVEN);
		}
		return price;
	}


	public void setDiscountApplication(DiscountApplication discountApplication) {
		this.discountApplication = discountApplication;
	}


	public DiscountApplication getDiscountApplication() {
		return discountApplication;
	}

	
	public void setLineManualDiscountID(Integer lineManualDiscountID) {
		this.lineManualDiscountID = lineManualDiscountID;
	}


	public Integer getLineManualDiscountID() {
		return lineManualDiscountID;
	}


	public String getLineDescription() {
		return lineDescription;
	}


	public void setLineDescription(String lineDescription) {
		this.lineDescription = lineDescription;
	}


	public Integer getInvoiceLineID() {
		return invoiceLineID;
	}


	public void setInvoiceLineID(Integer invoiceLineID) {
		this.invoiceLineID = invoiceLineID;
	}

	public BigDecimal getTotalDocumentDiscount() {
		return totalDocumentDiscount;
	}


	public void setTotalDocumentDiscount(BigDecimal totalDocumentDiscount) {
		this.totalDocumentDiscount = totalDocumentDiscount;
	}


	public BigDecimal getTemporalTotalDocumentDiscount() {
		return temporalTotalDocumentDiscount;
	}


	public void setTemporalTotalDocumentDiscount(
			BigDecimal temporalTotalDocumentDiscount) {
		this.temporalTotalDocumentDiscount = temporalTotalDocumentDiscount;
	}
	
	private BigDecimal getRealDocumentDiscountAmt(boolean includeDocumentDiscount, boolean temporalDocumentDiscount){
		return (includeDocumentDiscount ? getTotalDocumentDiscount() : BigDecimal.ZERO)
				.add(temporalDocumentDiscount ? getTemporalTotalDocumentDiscount() : BigDecimal.ZERO);
	}
	
	public BigDecimal getTotalNetAmt(){
		return getTotalNetAmt(false, false);
	}
	
	public BigDecimal getTotalNetAmt(boolean includeDocumentDiscount, boolean isTemporal){
		BigDecimal net = netPrice.multiply(getCount());
		if(includeDocumentDiscount){
			net = net.subtract(getNetAmount(getRealDocumentDiscountAmt(includeDocumentDiscount, isTemporal)));
		}
		return scaleAmount(net);
	}
	
	public BigDecimal getTotalTaxAmt(){
		return getTotalTaxAmt(true, false);
	}
	
	public BigDecimal getTotalTaxAmt(boolean includeDocumentDiscount, boolean isTemporal){
		return scaleAmount(
				getTaxBaseAmt(includeDocumentDiscount, isTemporal).multiply(getTax().getTaxRateMultiplier()));
	}
	
	public BigDecimal getTotalOtherTaxAmt(){
		return getTotalOtherTaxAmt(true, false);
	}
	
	public BigDecimal getTotalOtherTaxAmt(boolean includeDocumentDiscount, boolean isTemporal){
		return getTotalOtherTaxAmt(null, includeDocumentDiscount, isTemporal);
	}
	
	public BigDecimal getTotalOtherTaxAmt(Tax otherTax, boolean includeDocumentDiscount, boolean isTemporal){
		BigDecimal allMultipliers = getOrder().getSumOtherTaxesRateMultipliers();
		
		BigDecimal otherTaxMultiplier = otherTax == null? 
				new BigDecimal(1)
				: otherTax.getTaxRateMultiplier().divide(allMultipliers, 6, BigDecimal.ROUND_HALF_UP);
		
		return scaleAmount(
				getTaxBaseAmt(includeDocumentDiscount, isTemporal).multiply(otherTax == null ? allMultipliers : otherTax.getTaxRateMultiplier()));
	}
	
	public BigDecimal getTotalAmt(){
		return scaleAmount(price.multiply(getCount()));
	}
	
	public BigDecimal getAllTotalAmt(boolean includeTax, boolean includeOtherTax, boolean includeDocumentDiscount, boolean isTemporal){
		return scaleAmount(getTotalNetAmt(includeDocumentDiscount, isTemporal)
							.add(includeTax ? getTotalTaxAmt(includeDocumentDiscount, isTemporal) : BigDecimal.ZERO)
							.add(includeOtherTax ? getTotalOtherTaxAmt(includeDocumentDiscount, isTemporal) : BigDecimal.ZERO));
	}
	
	/**
	 * @return Importe base para el cálculo de impuestos para la línea 
	 */
	public BigDecimal getTaxBaseAmt(boolean includeDocumentDiscount, boolean isTemporal){
		return getTotalNetAmt(includeDocumentDiscount, isTemporal);
	}
	
	public BigDecimal getTotalTaxedPrice(boolean includeDocumentDiscount, boolean isTemporal) {
		return getTotalTaxedPrice(false).subtract(getRealDocumentDiscountAmt(includeDocumentDiscount, isTemporal));
	}
}
