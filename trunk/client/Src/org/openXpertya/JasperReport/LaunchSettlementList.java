package org.openXpertya.JasperReport;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.SettlementListDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrg;
import org.openXpertya.util.DB;

/**
 * Reporte para listado de liquidaciones.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class LaunchSettlementList extends JasperReportLaunch {
	// Parámetros del reporte
	private final static String ORGANIZATION = "Organization";
	private final static String DOC_STATUS = "Doc_Status";
	private final static String CARD_TYPE = "Card_Type";
	private final static String DATE_TO = "Date_To";
	private final static String DATE = "Date";

	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter(ORGANIZATION, getOrgName());
		addReportParameter(DOC_STATUS, getDocStatus());
		addReportParameter(CARD_TYPE, getCardType());
		addReportParameter(DATE_TO, getDateTo());
		addReportParameter(DATE, getDateFrom());
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		SettlementListDataSource dataSource = new SettlementListDataSource(getCtx(), get_TrxName());

		dataSource.setC_bpartner_id(getC_BPartner_ID());
		dataSource.setDocstatus(getDocStatus());
		dataSource.setAd_org_id(getAD_Org_ID());
		dataSource.setDateFrom(getDateFrom());
		dataSource.setDateTo(getDateTo());

		return dataSource;
	}

	/** @return nombre de la entidad comercial. */
	private String getCardType() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	name ");
		sql.append("FROM ");
		sql.append("	" + MBPartner.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_BPartner_ID = ?");

		return DB.getSQLValueString(get_TrxName(), sql.toString(), getC_BPartner_ID());
	}

	/** @return nombre de la organización. */
	private String getOrgName() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	name ");
		sql.append("FROM ");
		sql.append("	" + MOrg.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	AD_Org_ID = ?");

		return DB.getSQLValueString(get_TrxName(), sql.toString(), getAD_Org_ID());
	}

	// GETTERS:

	public int getAD_Org_ID() {
		Object param = getParameterValue("AD_Org_ID");
		if (param != null) {
			return (Integer) param;
		}
		return 0;
	}

	public String getDateFrom() {
		Timestamp leDate = (Timestamp) getParameterValue("Date");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(new Date(leDate.getTime()));
	}

	public String getDateTo() {
		Timestamp leDate = (Timestamp) getParameterValue("Date_To");
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(new Date(leDate.getTime()));
	}

	public String getDocStatus() {
		return (String) getParameterValue("DocStatus");
	}

	public int getC_BPartner_ID() {
		Object param = getParameterValue("C_BPartner_ID");
		if (param != null) {
			return (Integer) param;
		}
		return 0;
	}

}
