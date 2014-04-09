package org.openXpertya.process;

import java.math.BigDecimal;

import org.openXpertya.util.Env;

public class RetencionIVA extends RetencionGanancias {

	protected BigDecimal calculateImporteDeterminado(BigDecimal baseImponible) {
		if (baseImponible.compareTo(Env.ZERO) == 1) {
			return getPayTotalAmount().subtract(getPayNetAmt())
					.multiply(getPorcentajeRetencion())
					.divide(Env.ONEHUNDRED, 2, BigDecimal.ROUND_HALF_EVEN);
		} else
			return Env.ZERO;
	}

	protected BigDecimal calculateImporteRetenido(BigDecimal importeDeterminado) {
		return importeDeterminado;
	}
}
