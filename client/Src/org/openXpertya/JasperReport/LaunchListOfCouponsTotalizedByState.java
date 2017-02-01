package org.openXpertya.JasperReport;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.JasperReport.DataSource.ListOfCouponsDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.X_AD_Org;
import org.openXpertya.model.X_AD_Ref_List;
import org.openXpertya.model.X_AD_Ref_List_Trl;
import org.openXpertya.model.X_AD_Reference;
import org.openXpertya.model.X_M_EntidadFinanciera;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Reporte para listado de cupones totalizados por estado.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class LaunchListOfCouponsTotalizedByState extends JasperReportLaunch {
	// Parámetros del reporte
	private final static String ENTIDAD_FINANCIERA = "Entidad_Financiera";
	private final static String ORGANIZATION = "Organization";
	private final static String AUDIT_STATE = "Audit_State";
	private final static String DATE_TO = "Date_To";
	private final static String DATE = "Date";

	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter(ENTIDAD_FINANCIERA, getEntidadFinanciera());
		addReportParameter(AUDIT_STATE, getAuditStateName());
		addReportParameter(ORGANIZATION, getOrgName());
		addReportParameter(DATE_TO, getDateTo());
		addReportParameter(DATE, getDateFrom());
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		ListOfCouponsDataSource dataSource = new ListOfCouponsDataSource(get_TrxName());

		dataSource.setM_EntidadFinanciera_ID(getM_EntidadFinanciera_ID());
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
		sql.append("	" + X_AD_Org.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	AD_Org_ID = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, getAD_Org_ID());
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getOrgName", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return null;
	}

	private String getEntidadFinanciera() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	name ");
		sql.append("FROM ");
		sql.append("	" + X_M_EntidadFinanciera.Table_Name + " ");
		sql.append("WHERE ");
		sql.append("	M_EntidadFinanciera_ID = ?");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setInt(1, getM_EntidadFinanciera_ID());
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getEntidadFinanciera", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return null;
	}

	private String getAuditStateName() {
		if (getAuditState() == null) {
			return null;
		}
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	t.name ");
		sql.append("FROM ");
		sql.append("	" + X_AD_Reference.Table_Name + " r ");
		sql.append("	INNER JOIN " + X_AD_Ref_List.Table_Name + " l ON l.ad_reference_id = r.ad_reference_id ");
		sql.append("	INNER JOIN " + X_AD_Ref_List_Trl.Table_Name + " t ON t.ad_ref_list_id = l.ad_ref_list_id ");
		sql.append("WHERE ");
		sql.append("	r.NAME = 'CreditCardTypes' ");
		sql.append("	AND l.value = ? ");
		sql.append("	AND ad_language = ? ");

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = DB.prepareStatement(sql.toString());
			ps.setString(1, getAuditState());
			ps.setString(2, Env.getAD_Language(getCtx()));
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("name");
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "getAuditStateName", e);
		} finally {
			try {
				rs.close();
				ps.close();
			} catch (SQLException e) {
				log.log(Level.SEVERE, "Cannot close statement or resultset");
			}
		}
		return null;
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

	public int getM_EntidadFinanciera_ID() {
		Object param = getParameterValue("M_EntidadFinanciera_ID");
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