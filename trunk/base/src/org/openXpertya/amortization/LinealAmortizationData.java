package org.openXpertya.amortization;

import java.math.BigDecimal;

public class LinealAmortizationData extends AbstractAmortizationLineData {

	/** Porcentaje de amortización por año de vida útil */
	private BigDecimal amortizationYearPerc;
	
	public LinealAmortizationData() {
		// TODO Auto-generated constructor stub
	}

	public void setAmortizationYearPerc(BigDecimal amortizationYearPerc) {
		this.amortizationYearPerc = amortizationYearPerc;
	}

	public BigDecimal getAmortizationYearPerc() {
		return amortizationYearPerc;
	}

}
