package org.openXpertya.JasperReport.DataSource;

import java.sql.Timestamp;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MDocType;
import org.openXpertya.model.MPOSPaymentMedium;
import org.openXpertya.util.Util;

public class ResumenVentasPaymentMediumDataSource extends
		ResumenVentasDataSource {

	public ResumenVentasPaymentMediumDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID);
		// TODO Auto-generated constructor stub
	}

	public ResumenVentasPaymentMediumDataSource(String trxName, Properties ctx,
			Integer orgID, Timestamp dateFrom, Timestamp dateTo, Integer posID,
			Integer userID, boolean onlyCN,	boolean onlyDN) {
		super(trxName, ctx, orgID, dateFrom, dateTo, posID, userID, onlyCN, onlyDN);
	}

	@Override
	protected String getDSWhereClause() {
		return " AND trxtype IN ('CAI','P','NCC','PA','ND','CAIA') ";
	}

	@Override
	protected List<Object> getDSWhereClauseParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getGroupFields() {
		return "tendertype, pospaymentmediumname";
	}
	
	@Override
	protected String getLineDescription() {
		String trxType = (String)getCurrentRecord().get("TRXTYPE");
		String tenderType = (String)getCurrentRecord().get("TENDERTYPE");
		String description = "";
		if((trxType != null && (trxType.equals("CAI") || trxType.equals("CAIA"))) || tenderType.equals("CC")){
			description = "Cuenta Corriente";
		}
		else if(tenderType.equals(MPOSPaymentMedium.TENDERTYPE_Credit)){
			description = JasperReportsUtil.getListName(getCtx(),
					MDocType.DOCBASETYPE_AD_Reference_ID,
					(String) getCurrentRecord().get("POSPAYMENTMEDIUMNAME"));
		}
		else{
			description = JasperReportsUtil.getListName(getCtx(),
					MPOSPaymentMedium.TENDERTYPE_AD_Reference_ID,
					(String) getCurrentRecord().get("POSPAYMENTMEDIUMNAME"));
	
			if(Util.isEmpty(description, true)){
				description = (String)getCurrentRecord().get("POSPAYMENTMEDIUMNAME");
			}
		}
		return description;
	}
	
	@Override
	protected String getDSDataTable(){
		return "(SELECT * FROM v_dailysales UNION ALL SELECT * FROM v_dailysales_current_account)";
	}
	
}
