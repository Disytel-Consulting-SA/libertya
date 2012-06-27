package org.openXpertya.pos.model;

import java.math.BigDecimal;

public class Tax {

	private int id;
	private BigDecimal rate;
	private boolean isPercepcion = false;
	
	/**
	 * @param id
	 * @param rate
	 */
	public Tax(int id, BigDecimal rate, boolean isPercepcion) {
		super();
		this.id = id;
		this.rate = rate;
		this.setPercepcion(isPercepcion);
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
	 * @return Returns the rate.
	 */
	public BigDecimal getRate() {
		return rate;
	}

	/**
	 * @param rate The rate to set.
	 */
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	
	/**
	 * @return Retorna el multiplicador de la tasa de este impuesto.
	 * Ej. Si la tasa es 16 (16%), retorna 0.16
	 */
	public BigDecimal getTaxRateMultiplier() {
		return getRate().divide(new BigDecimal(100),10,BigDecimal.ROUND_HALF_UP);
	}
	
	/**
	 * @return Retorna el divisor de la tasa de este impuesto.
	 * Ej. Si la tasa es 16 (16%), retorna 1.16
	 */
	public BigDecimal getTaxRateDivisor() {
		return getTaxRateMultiplier().add(BigDecimal.ONE);
	}

	@Override
	public int hashCode() {
		return id;
	}

	public void setPercepcion(boolean isPercepcion) {
		this.isPercepcion = isPercepcion;
	}

	public boolean isPercepcion() {
		return isPercepcion;
	}
	
	
}
