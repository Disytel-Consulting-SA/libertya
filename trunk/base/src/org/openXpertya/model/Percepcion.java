package org.openXpertya.model;

import java.math.BigDecimal;

public class Percepcion extends DocumentTax {

	/** Referencia a la configuración de percepción de la organización */
	public int orgPercepcionID = 0;
	/** Código de Norma Arciba */
	public String arcibaNormCode = null;
	
	public Percepcion() {
		// TODO Auto-generated constructor stub
	}

	public Percepcion(int taxID, BigDecimal taxBaseAmt, BigDecimal taxAmt, BigDecimal rate) {
		setTaxAmt(taxAmt);
		setTaxBaseAmt(taxBaseAmt);
		setTaxID(taxID);
		setTaxRate(rate);
	}
	
	public Percepcion(int taxID, BigDecimal taxBaseAmt, BigDecimal taxAmt, BigDecimal rate, int orgPercepcionID) {
		this(taxID, taxBaseAmt, taxAmt, rate);
		this.orgPercepcionID = orgPercepcionID;
	}
	
}
