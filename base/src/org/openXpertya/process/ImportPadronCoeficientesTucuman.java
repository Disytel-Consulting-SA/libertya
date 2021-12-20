package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.Properties;

public class ImportPadronCoeficientesTucuman extends ImportPadronTucuman {

	public ImportPadronCoeficientesTucuman(Properties ctx, int orgID, String fileName, String padronType, int chunkSize,
			String trxName) {
		super(ctx, orgID, fileName, padronType, chunkSize, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getInsertColumnNames() {
		return "fecha_desde, fecha_hasta, fecha_publicacion, percepcion, retencion, coeficiente";
	}
	
	@Override
	protected String getInsertValues(String line) {
		BigDecimal perc = BigDecimal.ZERO;
		BigDecimal coeficiente = BigDecimal.ZERO;
		// Exento
		String exento = line.substring(12, 14);
		// Mes
		String mes = line.substring(24, 31);
		mes = mes.trim()+"01";
		// Denominación
		// Porcentaje y Coeficiente
		if(exento.trim().length() == 0) {
			perc = new BigDecimal(line.substring(184, 189).trim());
			coeficiente = new BigDecimal(line.substring(16, 22).trim());
		}
		String values = "to_date('" + mes + "','YYYYMMDD'), (to_date('" + mes
				+ "','YYYYMMDD') + interval '1 month') - interval '1 day', to_date('" + mes + "','YYYYMMDD'), " + perc
				+ ", " + perc + ", "+coeficiente;
		return values;
	}
}
