package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.ProductMovementsDataSource;

public class LaunchProductMovements extends JasperReportLaunch {

	@Override
	protected void loadReportParameters() throws Exception {
		Integer orgID = getOrgID();
		addReportParameter(
				"ORG_NAME",
				orgID == null ? null : JasperReportsUtil.getOrgName(getCtx(),
						orgID));
		addReportParameter("DATE_FROM", getDateFrom());
		addReportParameter("DATE_TO", getDateTo());
	}

	protected Integer getOrgID(){
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	protected Timestamp getDateFrom(){
		return (Timestamp) getParameterValue("Date");
	}
	
	protected Timestamp getDateTo(){
		return (Timestamp) getParameterValue("Date_TO");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new ProductMovementsDataSource(getCtx(), getOrgID(),
				getDateFrom(), getDateTo(), get_TrxName());
	}

}
