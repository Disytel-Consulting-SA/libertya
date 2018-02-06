package org.openXpertya.print.fiscal.hasar;

import java.math.BigDecimal;

import org.openXpertya.print.fiscal.comm.FiscalComm;

/**
 * Clase controladora de la impresora fiscal Hasar P715F versión 2 en adelante.
 * Posee el mismo comportamiento que la P715F versión 1 a excepción de la
 * precisión en decimales para el precio unitario.
 * 
 * @author Disytel
 *
 */
public class HasarPrinterP715F_v2 extends HasarPrinterP715F {

	public HasarPrinterP715F_v2() {
		// TODO Auto-generated constructor stub
	}

	public HasarPrinterP715F_v2(FiscalComm fiscalComm) {
		super(fiscalComm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String formatAmount(BigDecimal amount) {
		amount = amount.setScale(4, BigDecimal.ROUND_HALF_UP);
		return amount.toString();
	}
}
