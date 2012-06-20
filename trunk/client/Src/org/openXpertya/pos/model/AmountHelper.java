package org.openXpertya.pos.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AmountHelper {

	public static final int DEFAULT_CURRENCY = -1;
	
	private static final int AMOUNT_ROUND_TYPE = BigDecimal.ROUND_HALF_UP;
	private static Map<Integer, Integer> currencyPrecision;
	
	static {
		setCurrencyPrecision(new HashMap<Integer,Integer>());
		setPrecision(DEFAULT_CURRENCY, 2);
	}
	
	/**
	 * Modifica la precisión de un monto según la precisión configurada para
	 * la moneda en la que está expresado el monto.
	 * @param amount Monto al que se le configura la presicion.
	 * @param currencyID ID de la moneda del monto.
	 * @return <code>BigDecimal</code> que representa el monto con la presición
	 * actualizada.
	 */
	public static BigDecimal scale(BigDecimal amount, int currencyID) {
		return amount.setScale(getPrecision(currencyID), AMOUNT_ROUND_TYPE);
	}

	/**
	 * Modifica la precisión de un monto según la precisión configurada para
	 * la moneda por defecto.
	 * @param amount Monto al que se le configura la presicion.
	 * @return <code>BigDecimal</code> que representa el monto con la presición
	 * actualizada.
	 */
	public static BigDecimal scale(BigDecimal amount) {
		return scale(amount, DEFAULT_CURRENCY);
	}
	
	/**
	 * Asigna un valor de presicion para un moneda.
	 */
	public static void setPrecision(int currencyID, int precision) {
		getCurrencyPrecision().put(currencyID, precision);
	}

	/**
	 * Retorna el valor de presicion para un moneda. Si no existe una precisión
	 * configurada para la moneda, se retorna la presición por defecto. En ese
	 * caso la llamada a este método se comporta igual que:
	 * <code>AmountHelper.getPrecision(AmountHelper.DEFAULT_CURRENCY)</code>
	 */
	public static int getPrecision(int currencyID) {
		Integer precision = getCurrencyPrecision().get(currencyID);
		return precision == null ? 
				getCurrencyPrecision().get(DEFAULT_CURRENCY) : precision; 
	}

	/**
	 * @return Returns the currencyPrecision.
	 */
	private static Map<Integer, Integer> getCurrencyPrecision() {
		return currencyPrecision;
	}

	/**
	 * @param currencyPrecision The currencyPrecision to set.
	 */
	private static void setCurrencyPrecision(Map<Integer, Integer> currencyPrecision) {
		AmountHelper.currencyPrecision = currencyPrecision;
	}
}
