package org.openXpertya.JasperReport;

import java.sql.Timestamp;

import org.openXpertya.JasperReport.DataSource.ListOfCouponsDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MOrg;
import org.openXpertya.model.MReference;
import org.openXpertya.model.X_AD_Ref_List;
import org.openXpertya.model.X_AD_Ref_List_Trl;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Reporte para listado de cupones totalizados por estado.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class LaunchListOfCouponsTotalizedByState extends JasperReportLaunch {
	// Parámetros del reporte
	private final static String ORGANIZATION = "Organization";
	private final static String AUDIT_STATE = "Audit_State";
	private final static String CARD_TYPE = "Card_Type";
	private final static String DATE_TO = "Date_To";
	private final static String DATE = "Date";

	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter(AUDIT_STATE, getAuditStateName());
		addReportParameter(ORGANIZATION, getOrgName());
		addReportParameter(CARD_TYPE, getBPartner());
		addReportParameter(DATE_TO, getDateTo());
		addReportParameter(DATE, getDateFrom());
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		ListOfCouponsDataSource dataSource = new ListOfCouponsDataSource(get_TrxName());

		dataSource.setC_BPartner_ID(getC_BPartner_ID());
		dataSource.setAuditState(getAuditState());
		dataSource.setAD_Org_ID(getAD_Org_ID());
		dataSource.setDateFrom(getDateFrom());
		dataSource.setDateTo(getDateTo());
		dataSource.setCtx(getCtx());

		return dataSource;
	}

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

	private String getBPartner() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	name ");
		sql.append("FROM ");
		sql.append("	" + MBPartner.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	C_BPartner_ID = ?");

		return DB.getSQLValueString(get_TrxName(), sql.toString(), getC_BPartner_ID());
	}

	private String getAuditStateName() {
		if (getAuditState() == null) {
			return null;
		}
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	t.name ");
		sql.append("FROM ");
		sql.append("	" + MReference.Table_Name + " r ");
		sql.append("	INNER JOIN " + X_AD_Ref_List.Table_Name + " l ON l.ad_reference_id = r.ad_reference_id ");
		sql.append("	INNER JOIN " + X_AD_Ref_List_Trl.Table_Name + " t ON t.ad_ref_list_id = l.ad_ref_list_id ");
		sql.append("WHERE ");
		sql.append("	r.name = 'CreditCardTypes' ");
		sql.append("	AND l.value = ? ");
		sql.append("	AND ad_language = ? ");

		return DB.getSQLValueString(get_TrxName(), sql.toString(), getAuditState(), Env.getAD_Language(getCtx()));
	}

	// GETTERS:

	public int getAD_Org_ID() {
		Object param = getParameterValue("AD_Org_ID");
		if (param != null) {
			return (Integer) param;
		}
		return 0;
	}

	public Timestamp getDateFrom() {
		return (Timestamp) getParameterValue("Date");
	}

	public Timestamp getDateTo() {
		return (Timestamp) getParameterValue("Date_To");
	}

	public int getC_BPartner_ID() {
		Object param = getParameterValue("C_BPartner_ID");
		if (param != null) {
			return (Integer) param;
		}
		return 0;
	}

	public String getAuditState() {
		Object param = getParameterValue("AuditState");
		if (param != null) {
			return (String) param;
		}
		return null;
	}

}
