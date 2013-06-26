package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.ChecksByAccountDataSource;
import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MBankAccount;
import org.openXpertya.model.X_T_BankBalances;
import org.openXpertya.util.Util;

public class LaunchChecksByAccount extends JasperReportLaunch {

	@Override
	protected void loadReportParameters() throws Exception {
		if(!Util.isEmpty(getBankAccountID(), true)){
			MBankAccount bankAccount = MBankAccount.get(getCtx(), getBankAccountID());
			addReportParameter("BANK_ACCOUNT", bankAccount.getDescription());
		}
		addReportParameter("DATE_KIND", JasperReportsUtil.getListName(getCtx(),
				X_T_BankBalances.DATEORDER_AD_Reference_ID, getDateOrder()));
		addReportParameter("DATE", getDate());
	}

	protected Timestamp getDate(){
		return (Timestamp)getParameterValue("Date");
	}
	
	protected Integer getBankAccountID(){
		return (Integer)getParameterValue("C_BankAccount_ID");
	}
	
	protected String getDateOrder(){
		return (String)getParameterValue("DateOrder");
	}
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		return new ChecksByAccountDataSource(getCtx(), getBankAccountID(),
				getDate(), getDateOrder(), get_TrxName());
	}

}
