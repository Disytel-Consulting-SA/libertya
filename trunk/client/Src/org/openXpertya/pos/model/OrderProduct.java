package org.openXpertya.pos.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

	private BigDecimal lineDiscountAmt = BigDecimal.ZERO;
	
	private BigDecimal lineBonusAmt = BigDecimal.ZERO;
	
	private List<Tax> otherTaxes = new ArrayList<Tax>();
	
	private DiscountApplication discountApplication;
	
	private Integer lineManualDiscountID;
	
	private String lineDescription;
	
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
	public void setDiscount(BigDecimal discount, DiscountApplication discountApplication) {
		this.discount = discount;
		this.setDiscountApplication(discountApplication);
		
		if(getOrder() != null){
			getOrder().addLineManualDiscount(this);
		}
		
		calculatePrice();
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
	}

	/**
	 * @return Devuelve taxAmount.
	 */
	public BigDecimal getTaxRate() {
		BigDecimal taxRate = getTax().getRate();
		for (Tax otherTax : getOtherTaxes()) {
			taxRate = taxRate.add(otherTax.getRate());
		}
		return taxRate;
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
		
		if (!getProduct().isPerceptionIncludedInPrice()){
			// Sumo el monto con los otros impuestos
			taxedPrice = taxedPrice.add(getOtherTaxesAmt(getNetPrice(taxedPrice)));	
		}
		
		return scalePrice(taxedPrice);
	}
	
	/**
	 * Calcula el precio a partir de un precio con impuestos.
	 * Si el producto tiene el impuesto incluído en la tarifa, entonces
	 * el precio retornado es igual a <code>taxedPrice</code>.
	 * Este método es el inverso a <code>getTaxedPrice(BigDecimal price)</code>
	 */
	public BigDecimal getPrice(BigDecimal taxedPrice) {
		BigDecimal price = taxedPrice;
		
		// Suma los multiplicadores de todos los impuestos
		BigDecimal rateMultipliers = getSumOtherTaxesRateMultipliers();
		
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
	}

	/**
	 * @param price
	 *            precio con impuesto, incluído en la tarifa o no
	 * @return Precio neto
	 */
	public BigDecimal getNetPrice(BigDecimal taxedPrice){
		BigDecimal netPrice = taxedPrice.divide(getTax().getTaxRateDivisor(),
				20, BigDecimal.ROUND_HALF_UP);		
		
		return scalePrice(netPrice);
	}
	
	/**
	 * @param price
	 *            precio con todos los impuestos, incluído en la tarifa o no y
	 *            con impuestos extras
	 * @return Precio neto
	 */
	public BigDecimal getNetPriceAllTaxes(BigDecimal taxedPrice){
		BigDecimal netPrice = taxedPrice.divide(
				BigDecimal.ONE.add(getTax().getTaxRateMultiplier().add(
						getSumOtherTaxesRateMultipliers())), 20,
				BigDecimal.ROUND_HALF_UP);		
		
		return scalePrice(netPrice);
	}
	
	public BigDecimal getOtherTaxesAmt(BigDecimal netPrice){
		BigDecimal otherTaxesAmt = BigDecimal.ZERO;
		for (Tax otherTax : getOtherTaxes()) {
			otherTaxesAmt = otherTaxesAmt.add(netPrice.multiply(otherTax.getTaxRateMultiplier())); 
		}
		return scaleAmount(otherTaxesAmt);
	}
	
	public BigDecimal getSumOtherTaxesRateMultipliers(){
		BigDecimal otherTaxesrates = BigDecimal.ZERO;
		for (Tax otherTax : getOtherTaxes()) {
			otherTaxesrates = otherTaxesrates.add(otherTax.getTaxRateMultiplier());
		}
		return scaleAmount(otherTaxesrates);
	}
	
	public BigDecimal getSumOtherTaxesRateDivisors(){
		BigDecimal otherTaxesRates = BigDecimal.ZERO;
		for (Tax otherTax : getOtherTaxes()) {
			otherTaxesRates = otherTaxesRates.add(otherTax.getTaxRateDivisor());
		}
		return scaleAmount(otherTaxesRates);
	}

	/**
	 * Determina los montos de impuestos adicionales que se agregaron en
	 * principio al neto del precio con impuesto parámetro. El monto de
	 * impuestos adicionales agregado al precio se determina en
	 * base al siguiente cálculo: <br>
	 * PSIA = PCIA / (1 + STIA)<br>
	 * donde:
	 * <ul>
	 * <li>PSIA = Precio Sin Impuestos Adicionales</li>
	 * <li>PCIA = Precio Con Impuestos Adicionales</li>
	 * <li>STIA = Suma de las Tasas de Impuestos Adicionales</li>
	 * </ul>
	 * 
	 * @param taxedPrice
	 * @return PSIA
	 */
	public BigDecimal getPriceWithoutOtherTaxesAmt(BigDecimal taxedPrice){
		BigDecimal psia = BigDecimal.ZERO;
		psia = taxedPrice.divide(
				BigDecimal.ONE.add(getSumOtherTaxesRateMultipliers()), 20,
				BigDecimal.ROUND_HALF_UP);
		return scaleAmount(psia);
	}
	
	/**
	 * @return El importe total final de la línea incluyendo bonificaciones
	 *         aplicadas.
	 */
	public BigDecimal getTotalTaxedPrice() {
		return getTotalTaxedPrice(false);
	}
	
	private void calculatePrice() {
		price = calculatePrice(getDiscount());
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

	public BigDecimal calculatePrice(BigDecimal discount) {
		BigDecimal cPrice = getPriceList();
		if(discount != null) {
			cPrice = cPrice.subtract(cPrice.multiply(discount.divide(new BigDecimal(100),10,BigDecimal.ROUND_HALF_UP)));
		}
		return cPrice;
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
		//return scaleAmount(getTaxedPrice(excludeBonus).multiply(getQty()));
		return scaleAmount(getTaxedPrice(
				scaleAmount(getPrice(excludeBonus).multiply(getCount()))));
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
		// Actualiza los otros impuestos
		updateOtherTaxes();
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
	
	public void updateOtherTaxes(){
		// Actualizar los impuestos adicionales
		List<Tax> taxes = new ArrayList<Tax>();
		if(getOrder() != null){
			taxes = getOrder().getOtherTaxes();
		}
		setOtherTaxes(taxes);
	}


	public void setOtherTaxes(List<Tax> otherTaxes) {
		this.otherTaxes = otherTaxes;
	}


	public List<Tax> getOtherTaxes() {
		return otherTaxes;
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
}
