package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MDocType;

public class ResumenVentasPaymentMediumDataSource extends
		ResumenVentasDataSource {

	public ResumenVentasPaymentMediumDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String getDSWhereClause() {
		return " AND trxtype IN ('CAI','P') ";
	}

	@Override
	protected List<Object> getDSWhereClauseParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getGroupFields() {
		return "trxtype, tendertype, pospaymentmediumname";
	}
	
	@Override
	protected String getLineDescription() {
		String trxType = (String)getCurrentRecord().get("TRXTYPE");
		String tenderType = (String)getCurrentRecord().get("TENDERTYPE");
		String description = "";
		if(trxType.equals("CAI")){
			description = "Cuenta Corriente";
		}
		else if(tenderType.equals("CR")){
			description = JasperReportsUtil.getListName(getCtx(),
					MDocType.DOCBASETYPE_AD_Reference_ID,
					(String) getCurrentRecord().get("POSPAYMENTMEDIUMNAME"));
		}
		else{
			description = (String)getCurrentRecord().get("POSPAYMENTMEDIUMNAME");
		}
		return description;
	}
	
}
