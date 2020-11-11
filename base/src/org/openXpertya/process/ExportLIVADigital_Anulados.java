package org.openXpertya.process;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.util.Env;

public class ExportLIVADigital_Anulados extends ExportProcessGeneralFormat {

	public ExportLIVADigital_Anulados(Properties ctx, Integer expFor, Map<String, Object> param,
			List<String> parametersNames, Map<String, Integer> parametersClass, String trxName) {
		super(ctx, expFor, param, parametersNames, parametersClass, trxName);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getFrom() {
		return "libro_iva_digital_comp_anulados(" + Env.getAD_Client_ID(getCtx()) + ", "
				+ getParametersValues().get("AD_Org_ID") + ", "
				+ (getParametersValues().get("Date") == null ? "null" : "'" + getParametersValues().get("Date") + "'")
				+ "::date, " + (getParametersValues().get("Date_TO") == null ? "null"
						: "'" + getParametersValues().get("Date_TO") + "'")
				+ "::date)";
	}
	
}
