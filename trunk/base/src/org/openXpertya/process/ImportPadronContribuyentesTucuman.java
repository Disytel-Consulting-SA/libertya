package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.Properties;

public class ImportPadronContribuyentesTucuman extends ImportPadronTucuman {

	public ImportPadronContribuyentesTucuman(Properties ctx, int orgID, String fileName, String padronType, int chunkSize,
			String trxName) {
		super(ctx, orgID, fileName, padronType, chunkSize, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getInsertColumnNames() {
		return "fecha_desde, fecha_hasta, fecha_publicacion, percepcion, retencion";
	}

	@Override
	protected String getInsertValues(String line) {
		BigDecimal perc = BigDecimal.ZERO;
		// Exento
		String exento = line.substring(12, 14);
		// Convenio
		// Desde
		String desde = line.substring(20, 28);
		// Hasta
		String hasta = line.substring(30, 38);
		// Denominación
		// Porcentaje
		if(exento.trim().length() == 0) {
			perc = new BigDecimal(line.substring(192, 197).trim());
		}
		String values = "to_date('" + desde + "','YYYYMMDD'), to_date('" + hasta + "','YYYYMMDD'), to_date('"
				+ desde + "','YYYYMMDD'), "+perc+", "+perc;
		return values;
	}
}
