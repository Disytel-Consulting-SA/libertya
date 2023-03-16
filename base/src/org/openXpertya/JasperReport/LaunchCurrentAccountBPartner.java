package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.CurrentAccountBPartnerDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.util.Util;

public class LaunchCurrentAccountBPartner extends JasperReportLaunch {

	public LaunchCurrentAccountBPartner() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter("DATEFROM", getDateFrom());
		addReportParameter("DATETO", getDateTo());
		addReportParameter("ACCOUNTTYPE", getAccountType());
		if(!Util.isEmpty(getBPartnerID(), true)) {
			MBPartner bp = new MBPartner(getCtx(), getBPartnerID(), get_TrxName());
			addReportParameter("C_BPARTNER_ID", getBPartnerID());
			addReportParameter("BPARTNER_VALUE", bp.getValue());
			addReportParameter("BPARTNER_NAME", bp.getName());
		}
	}

	protected Integer getBPartnerID() {
		return (Integer)getParameterValue("C_BPARTNER_ID");
	}
	
	protected Timestamp getDateFrom() {
		return (Timestamp)getParameterValue("DATE");
	}
	
	protected Timestamp getDateTo() {
		return (Timestamp)getParameterValue("DATE_TO");
	}
	
	protected String getAccountType() {
		return (String)getParameterValue("ACCOUNTTYPE");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new CurrentAccountBPartnerDataSource(getCtx(), getAD_PInstance_ID(), getDateFrom(), getDateTo(),
				getAccountType(), getBPartnerID(), get_TrxName());
	}

}
