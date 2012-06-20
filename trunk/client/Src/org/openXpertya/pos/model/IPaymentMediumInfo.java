package org.openXpertya.pos.model;

/**
 * Las clases que implementan esta interface contienen información adicional
 * de un Medio de Pago (o Plan de Financiación) del TPV.
 */
public interface IPaymentMediumInfo {

	/**
	 * @return Devuelve el esquema de descuento asociado a este medio de pago
	 */
	public abstract DiscountSchema getDiscountSchema();
	
	/**
	 * @return Devuelve el nombre de este medio de pago
	 */
	public abstract String getName();
	
	/**
	 * @return Devuelve el tipo de este medio de pago
	 */
	public abstract String getTenderType();
}
