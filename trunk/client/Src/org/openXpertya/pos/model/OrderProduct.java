package org.openXpertya.pos.model;

import java.math.BigDecimal;

import org.openXpertya.model.MProduct;

public class OrderProduct {

	
	
	public enum DiscountApplication {
		ToPrice,
		Bonus
	}
	
	private Order order;
	
	private int count;

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
	
	private boolean manualDiscount = false;
	
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
	public OrderProduct(int count, BigDecimal discount,
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
	public int getCount() {
		return count;
	}

	/**
	 * @param count
	 *            Fija o asigna count.
	 */
	public void setCount(int count) {
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

		calculatePrice();
		setLineBonusAmt(BigDecimal.ZERO);
		setLineDiscountAmt(BigDecimal.ZERO);
		BigDecimal lineDiscountAmt = getDiscountAmt().multiply(getQty());
		if (discountApplication == DiscountApplication.Bonus) {
			setLineBonusAmt(lineDiscountAmt);
		} else {
			setLineDiscountAmt(lineDiscountAmt);
		}
		manualDiscount = true;
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
		// FIXME: Actualmente los descuentos automáticos no son compatibles con
		// los manuales. Si hay un descuento automático entonces no se pueden
		// realizar descuentos manuales. Esto se debe corregir, permitiendo
		// realizar descuentos manuales aún cuando la línea tenga automáticos.
		// Por esta restricción actual, si hay automáticos el descuento manual se
		// setea a cero, sino se calcula el descuento manual.
		if (hasAutomaticDiscount()) {
			this.price = price;
			discount = BigDecimal.ZERO;
		} else {
			this.price = price;
			// Tiene prioridad el descuento manual. Se calcula el precio a partir del descuento.
			// Esto fixea el problema de que el DiscountCalculator pisa el Price
			// ya que no "sabe" que aquí se usan descuentos manuales. Esto da a pensar
			// de que los descuentos manuales deben ser administrados por el DiscountCalculator.
			if (hasManualDiscount()) {
				this.price = calculatePrice(getDiscount());
			} else {
				calculateDiscount();
			}
		}
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
			price = (getPrice().multiply(getQty()).add(getLineBonusAmt()))
					.divide(getQty(), price.scale(), BigDecimal.ROUND_HALF_EVEN);
		}
		return getTaxedPrice(price);
	}

	public BigDecimal getTaxedPrice(BigDecimal price) {
		// Si el impuesto está incluído en el precio del producto, entonces
		// se retorna dicho precio.
		BigDecimal taxedPrice = price;

		// Sino, se calcula el nuevo precio sumando el monto implicado por la tasa
		// del impuesto del producto.
		if (!getProduct().isTaxIncludedInPrice())
			taxedPrice = price.add(price.multiply(getTax().getTaxRateMultiplier()));
		
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
		if (!getProduct().isTaxIncludedInPrice())
			price = price.divide(getTax().getTaxRateDivisor(),20, BigDecimal.ROUND_HALF_UP);
		return scalePrice(price);
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
			BigDecimal diff = getPriceList().subtract(price);
			cDiscount = diff.multiply(new BigDecimal(100)).divide(getPriceList(),10,BigDecimal.ROUND_HALF_UP);
		}
		return cDiscount;
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
				scaleAmount(getPrice(excludeBonus).multiply(getQty()))));
	}
	
	/**
	 * @return La cantidad del artículo como un {@link BigDecimal}. 
	 */
	public BigDecimal getQty() {
		return new BigDecimal(getCount());
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
		// Si el descuento NO es cero, se asume que es un descuento automático.
		// Luego si es manual, el método lo que asigne se encargará de setear la
		// marca a manual = true (setDiscount(...))
		if (lineDiscountAmt.compareTo(BigDecimal.ZERO) != 0) {
			manualDiscount = false;
		}
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
		// Si el descuento NO es cero, se asume que es un descuento automático.
		// Luego si es manual, el método lo que asigne se encargará de setear la
		// marca a manual = true (setDiscount(...))
		if (lineBonusAmt.compareTo(BigDecimal.ZERO) != 0) {
			manualDiscount = false;
		}
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
		return "("+ getProduct().getId() + "," + getQty() + ")";
	}


	/**
	 * @return Indica si el descuento realizado a esta línea es manual o no.
	 */
	public boolean isManualDiscount() {
		return manualDiscount;
	}

	/**
	 * @return Indica si esta línea tiene aplicados descuentos automáticos
	 *         (promociones,combos, EC).
	 */
	public boolean hasAutomaticDiscount() {
		return 
			(getLineBonusAmt().compareTo(BigDecimal.ZERO) != 0 
				|| getLineDiscountAmt().compareTo(BigDecimal.ZERO) != 0)
			&& !manualDiscount;	
	}

	/**
	 * @return Indica si esta línea tiene aplicado un descuento manual
	 */
	public boolean hasManualDiscount() {
		return getDiscount() != null && getDiscount().compareTo(BigDecimal.ZERO) != 0;
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
			price = (getPrice().multiply(getQty()).add(getLineBonusAmt()))
					.divide(getQty(), price.scale(), BigDecimal.ROUND_HALF_EVEN);
		}
		return price;
	}
}
