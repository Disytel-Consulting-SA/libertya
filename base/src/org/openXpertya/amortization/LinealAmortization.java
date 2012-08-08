package org.openXpertya.amortization;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MAmortization;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

public class LinealAmortization extends AbstractAmortizationProcessor {

	public LinealAmortization(Properties ctx, Integer amortizationID,
			String trxName) {
		super(ctx, amortizationID, trxName);
		// TODO Auto-generated constructor stub
	}

	public LinealAmortization(Properties ctx, MAmortization amortization,
			String trxName) {
		super(ctx, amortization, trxName);
		// TODO Auto-generated constructor stub
	}

	public LinealAmortization(Properties ctx, String trxName) {
		super(ctx, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public LinealAmortization(Properties ctx, Timestamp amortizationDate,
			String trxName) {
		super(ctx, amortizationDate, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doAmortization() throws Exception {
		LinealAmortizationData linealAmortizationLineData = null; 
		List<AbstractAmortizationLineData> newLines = new ArrayList<AbstractAmortizationLineData>();
		for (AbstractAmortizationLineData amortizationLineData : getAmortizationLines()) {
			linealAmortizationLineData = (LinealAmortizationData)amortizationLineData;
			// Si la fecha de alta del bien de uso se encuentra antes de la
			// fecha de procesamiento de la amortización o la amortización
			// anterior contenía la baja del bien de uso, entonces no se agregan
			// a la amortización actual
			if (linealAmortizationLineData.getAsset().getDateFrom() == null
					|| linealAmortizationLineData.getAsset().getDateFrom()
							.compareTo(getProcessDate()) > 0
					|| (linealAmortizationLineData.getBeforeAmortizationLine() != null
							&& linealAmortizationLineData
									.getBeforeAmortizationLine().getBajaAmt() != null && linealAmortizationLineData
							.getBeforeAmortizationLine().getBajaAmt()
							.compareTo(BigDecimal.ZERO) != 0)) {
				continue;
			}
			// Costo unitario
			// Amortización acumulada inicio ejercicio
			linealAmortizationLineData
					.setAmortizationInitialAmt(linealAmortizationLineData
							.getBeforeAmortizationLine() != null ? linealAmortizationLineData
							.getBeforeAmortizationLine()
							.getEndPeriodAmortizationAmt() : BigDecimal.ZERO);
			// Residual al inicio del ejercicio
			linealAmortizationLineData
					.setResidualInitialAmt(linealAmortizationLineData
							.getBeforeAmortizationLine() != null ? linealAmortizationLineData
							.getBeforeAmortizationLine()
							.getEndPeriodResidualAmt() : BigDecimal.ZERO);
			// Amortización actual
			BigDecimal amortizationAmt = getAmortizationAmt(linealAmortizationLineData, getProcessDate());
			linealAmortizationLineData.setAmortizationAmt(amortizationAmt);
			linealAmortizationLineData
					.setResidualAmt(linealAmortizationLineData
							.getResidualInitialAmt().compareTo(BigDecimal.ZERO) == 0 ? linealAmortizationLineData
							.getTotalCost().subtract(amortizationAmt)
							: linealAmortizationLineData
									.getResidualInitialAmt().subtract(
											amortizationAmt));
			// Si la fecha de procesamiento es igual a la de fin del período
			// entonces no calculo nuevamente el monto para el cierre del
			// período
//			if(getProcessDate().compareTo(getTimePeriod().getDateTo()) != 0){
			if (!DisplayType
					.getDateFormat()
					.format(getProcessDate())
					.equalsIgnoreCase(
							DisplayType.getDateFormat().format(
									getTimePeriod().getDateTo()))) {
				amortizationAmt = getAmortizationAmt(
						linealAmortizationLineData, getTimePeriod().getDateTo());
			}
			linealAmortizationLineData
					.setAmortizationEndAmt(linealAmortizationLineData
							.getAmortizationInitialAmt().add(amortizationAmt));
			linealAmortizationLineData
					.setResidualEndAmt(linealAmortizationLineData
							.getResidualInitialAmt().compareTo(BigDecimal.ZERO) == 0 ? linealAmortizationLineData
									.getTotalCost().subtract(amortizationAmt)
									: linealAmortizationLineData
											.getResidualInitialAmt().subtract(
													amortizationAmt));
			// Si la fecha de alta se encuentra en este período se agrega al alta
			if (linealAmortizationLineData.getAsset().getDateFrom() != null
					&& getTimePeriod()
							.isIncludedInPeriod(
									linealAmortizationLineData.getAsset()
											.getDateFrom())) {
				linealAmortizationLineData.setAlta(linealAmortizationLineData.getTotalCost());
			}
			// Si la fecha de baja se encuentra en este período se agrega al baja
			if (linealAmortizationLineData.getAsset().getDateTo() != null
					&& getTimePeriod().isIncludedInPeriod(
							linealAmortizationLineData.getAsset().getDateTo())) {
				linealAmortizationLineData.setBaja(linealAmortizationLineData.getTotalCost());
			}
			newLines.add(linealAmortizationLineData);
		}
		setAmortizationLines(null);
		setAmortizationLines(newLines);
	}

	/**
	 * Obtiene un monto de amortización
	 * @param linealAmortizationData
	 * @param processDate
	 * @return
	 */
	public BigDecimal getAmortizationAmt(LinealAmortizationData linealAmortizationData, Date processDate){
		BigDecimal amortizationAmt = BigDecimal.ZERO;
		// Cantidad de días vividos en este período
		// Si la fecha de alta es mayor a la fecha de inicio del período
		// entonces tomo la fecha de alta del bien, sino la fecha de inicio del
		// período
		Timestamp initialDate = new Timestamp((linealAmortizationData.getAsset().getDateFrom()
				.after(getTimePeriod().getDateFrom()) ? linealAmortizationData
				.getAsset().getDateFrom() : getTimePeriod().getDateFrom()).getTime());
		
		// El periodo de vida se calcula restando la cantidad de dias del initialDate a los dias del periodo
		Calendar periodCalendar = Calendar.getInstance();
		periodCalendar.setTime(getTimePeriod().getDateFrom());
		Calendar initialCalendar = Calendar.getInstance();
		initialCalendar.setTime(initialDate);
		Calendar processCalendar = Calendar.getInstance();
		processCalendar.setTime(processDate);
		Integer diffDays = 365
				* (processCalendar.get(Calendar.YEAR) - initialCalendar
						.get(Calendar.YEAR))
				+ (processCalendar.get(Calendar.DAY_OF_YEAR) - initialCalendar
						.get(Calendar.DAY_OF_YEAR));
		Integer periodLivedDays = diffDays
				- periodCalendar.get(getTimePeriod().getDayField()) + 2;
		
		// Si la cantidad de días vividos en este ejercicio supera la cantidad
		// de días del período entonces tomo la cantidad del período, sino los
		// días vividos
		periodLivedDays = periodLivedDays <= getTimePeriod().getDaysCount() ? periodLivedDays
				: getTimePeriod().getDaysCount();
		periodLivedDays = periodLivedDays <= 0?1:periodLivedDays;
		// Si la amortización acumulada al inicio del período es igual al costo
		// total del artículo, entonces el monto de amortización es 0
		if (linealAmortizationData.getAmortizationInitialAmt().compareTo(
				linealAmortizationData.getTotalCost()) != 0) {
			// Si poseo configurado un porcentaje de amortización por periodo, entonces
			// uso ese, si es 0 entonces se debe realizar un decremento diario
			// Decremento porcentual por período
			if(linealAmortizationData.getAmortizationYearPerc().compareTo(BigDecimal.ZERO) != 0){
				// Vida útil transcurrida del ejercicio
				BigDecimal periodYearLifeLived = new BigDecimal(periodLivedDays)
						.setScale(2)
						.divide(new BigDecimal(getTimePeriod().getDaysCount())
								.setScale(2),
								2, BigDecimal.ROUND_HALF_UP);		
				BigDecimal perc = periodYearLifeLived.multiply(
						linealAmortizationData.getAmortizationYearPerc()).setScale(
						2, BigDecimal.ROUND_HALF_UP);
				amortizationAmt = linealAmortizationData.getTotalCost()
						.multiply(perc)
						.divide(Env.ONEHUNDRED, BigDecimal.ROUND_HALF_UP);	
			}
			// Decremento diario
			else{
				// Cantidad de días de vida útil
				Integer daysLife = linealAmortizationData.getYearLife()*365;
				amortizationAmt = linealAmortizationData
						.getTotalCost()
						.divide(new BigDecimal(daysLife), BigDecimal.ROUND_HALF_UP)
						.multiply(new BigDecimal(periodLivedDays));
			}
			// Si el monto de amortización + el de inicio del período supera el
			// costo, entonces es la resta del costo y el de inicio del período
			BigDecimal totalAmortizationAmt = amortizationAmt
					.add(linealAmortizationData.getAmortizationInitialAmt());
			if(totalAmortizationAmt.compareTo(linealAmortizationData.getTotalCost()) > 0){
				amortizationAmt = linealAmortizationData.getTotalCost()
						.subtract(
								linealAmortizationData
										.getAmortizationInitialAmt());
			}
		}		
		return amortizationAmt;
	}
	
	@Override
	public AbstractAmortizationLineData getAmortizationLineData() {
		return new LinealAmortizationData();
	}

	@Override
	protected void preLoadAmortizationLinesData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void postLoadAmortizationLinesData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AbstractAmortizationLineData preLoadAmortizationLineData(
			AbstractAmortizationLineData amortizationLineData, ResultSet rs)
			throws Exception {
		// TODO Auto-generated method stub
		return amortizationLineData;
	}
	
	@Override
	protected AbstractAmortizationLineData postLoadAmortizationLineData(
			AbstractAmortizationLineData amortizationLineData, ResultSet rs)
			throws Exception {
		// TODO Auto-generated method stub
		LinealAmortizationData linealAmortizationData = (LinealAmortizationData) amortizationLineData;
		linealAmortizationData.setAmortizationYearPerc(rs.getBigDecimal("amortizationperc"));
		return linealAmortizationData;
	}

	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		
	}
}
