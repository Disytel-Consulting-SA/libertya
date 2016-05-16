package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;

public class RetencionGananciasMonotributo extends RetencionGanancias {

	protected Timestamp getFechaDesde(Timestamp vFecha) {
		//Setea la fecha desde un año antes.
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(vFecha.getTime());
		cal.add(Calendar.YEAR, -1);
		Timestamp b = new Timestamp(cal.getTimeInMillis());
		return b;
	}
	
	protected BigDecimal calculateImporteRetenido(BigDecimal importeDeterminado) {
		return importeDeterminado;
	}

}