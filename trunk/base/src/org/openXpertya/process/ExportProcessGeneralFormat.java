package org.openXpertya.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openXpertya.model.MExpFormat;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

public class ExportProcessGeneralFormat extends ExportProcess {

	/** Contexto local */
	private Properties localCtx;
	/** Nombre de transacción local */
	private String localTrxName;
	/** Process Info local */
	private ProcessInfo localProcessInfo;
	/** Dato Info de los Parámetros del proceso */
	private Map<String, Integer> parametersClass;

	public ExportProcessGeneralFormat(java.util.Properties ctx, int expFor,
			HashMap<String, Object> param, List<String> parametersNames,
			HashMap<String, Integer> parametersClass, String trxName) {
		setExportFormat(new MExpFormat(ctx, expFor, trxName));
		setParametersValues(param);
		this.localCtx = ctx;
		this.localTrxName = trxName;

		int processID = DB.getSQLValue(null,
				"SELECT AD_Process_ID FROM AD_Process WHERE ad_expformat_id='"
						+ expFor + "' LIMIT 1 ");

		this.localProcessInfo = new ProcessInfo(trxName, processID);
		setParametersNames(parametersNames);
		setParametersClass(parametersClass);
	}

	@Override
	public Properties getCtx() {
		if (localCtx != null) {
			return localCtx;
		} else {
			return super.getCtx();
		}
	}

	@Override
	public String get_TrxName() {
		if (!Util.isEmpty(localTrxName, true)) {
			return localTrxName;
		} else {
			return super.get_TrxName();
		}
	}

	@Override
	public ProcessInfo getProcessInfo() {
		if (super.getProcessInfo() == null) {
			return localProcessInfo;
		} else {
			return super.getProcessInfo();
		}
	}

	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer();
		setWhereClauseParams(new ArrayList<Object>());
		for (String paramName : getParametersNames()) {
			whereClause.append(" ( ");
			if (DisplayType.Date == getParametersClass().get(paramName)) {
				if ("_To".equals(paramName.substring(paramName.length() - 3,
						paramName.length()))) {
					whereClause.append(paramName.substring(0,
							paramName.length() - 3));
					whereClause.append("::date  <= ?::date  ");
					getWhereClauseParams().add(
							getParametersValues().get(paramName));
				} else {
					whereClause.append(paramName);
					whereClause.append("::date  >= ?::date  ");
					getWhereClauseParams().add(
							getParametersValues().get(paramName));
				}
			} else {
				whereClause.append(paramName);
				whereClause.append(" = ? ");
				getWhereClauseParams()
						.add(getParametersValues().get(paramName));
			}

			whereClause.append(" ) ");
			whereClause.append(" AND ");
		}
		whereClause.append(" ad_client_id = ").append(
				Env.getAD_Client_ID(getCtx()));
		return whereClause.toString();
	}

	protected Map<String, Integer> getParametersClass() {
		return parametersClass;
	}

	protected void setParametersClass(Map<String, Integer> parametersClass) {
		this.parametersClass = parametersClass;
	}

}
