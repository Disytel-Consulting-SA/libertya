package org.openXpertya.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public interface ITime {

	/**
	 * @return la fecha inicial correspondiente equivalente con el tiempo que
	 *         implemente esta interface
	 */
	public Date getDateFrom();

	/**
	 * @return la fecha final correspondiente equivalente con el tiempo que
	 *         implemente esta interface
	 */
	public Date getDateTo();
	
	/**
	 * @return la constante de la clase Calendar correspondiente a este tiempo.
	 *         Por ejemplo, si estamos en el tiempo año entonces el valor
	 *         resultante es {@link Calendar#YEAR}
	 */
	public int getDateField();

	/**
	 * @return la constante de la clase Calendar correspondiente al día del
	 *         período. Por ejemplo, si estamos en el tiempo año entonces el
	 *         valor resultante es {@link Calendar#DAY_OF_YEAR}
	 */
	public int getDayField();
	
	/**
	 * @return la descripción de este tiempo
	 */
	public String getITimeDescription();
	
	/**
	 * @param date fecha a comparar
	 * @return true si el período actual incluye a la fecha parámetro
	 */
	public boolean isIncludedInPeriod(Timestamp date);
	
	/**
	 * @return cantidad de días en total de este período
	 */
	public Integer getDaysCount();
}
