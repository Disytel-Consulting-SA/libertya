package org.openXpertya.JasperReport;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.SeguimientoDescuentosManualesDataSource;
import org.openXpertya.model.MOrg;
import org.openXpertya.util.Env;

public class LaunchSeguimientoDescuentosManuales extends JasperReportLaunch {

	@Override
	protected void loadReportParameters() throws Exception {
		MOrg org = MOrg.get(getCtx(), getOrgID());
		addReportParameter("ORG_VALUE", org.getValue());
		addReportParameter("ORG_NAME", org.getName());
		addReportParameter("DATE_FROM", getDateFrom());
		addReportParameter("DATE_TO", getDateTo());
		addReportParameter("PERCENTAGE", getPercentage());
	}
	
	protected Integer getOrgID(){
		return (Integer)getParameterValue("AD_Org_ID");
	}
	
	protected Timestamp getDateFrom(){
		return (Timestamp)getParameterValue("Date");
	}
	
	protected Timestamp getDateTo(){
		Timestamp dateTo = (Timestamp)getParameterValue("Date_To");
		if(dateTo == null){
			dateTo = Env.getDate();
		}
		return dateTo;
	}
	
	protected BigDecimal getPercentage(){
		return (BigDecimal)getParameterValue("Percentage");
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new SeguimientoDescuentosManualesDataSource(get_TrxName(),
				getOrgID(), getDateFrom(), getDateTo(), getPercentage());
	}

}
