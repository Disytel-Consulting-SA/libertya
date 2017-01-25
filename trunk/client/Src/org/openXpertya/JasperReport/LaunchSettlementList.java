package org.openXpertya.JasperReport;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.JasperReport.DataSource.SettlementListDataSource;
import org.openXpertya.model.X_AD_Org;
import org.openXpertya.model.X_M_EntidadFinanciera;
import org.openXpertya.util.DB;

/**
 * Reporte para listado de liquidaciones.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class LaunchSettlementList extends JasperReportLaunch {
	// Parámetros del reporte
	private final static String ENTIDAD_FINANCIERA = "Entidad_Financiera";
	private final static String ORGANIZATION = "Organization";
	private final static String DOC_STATUS = "Doc_Status";
	private final static String CARD_TYPE = "Card_Type";
	private final static String DATE_TO = "Date_To";
	private final static String DATE = "Date";

	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter(ENTIDAD_FINANCIERA, getEntidadFinanciera());
		addReportParameter(ORGANIZATION, getOrgName());
		addReportParameter(DOC_STATUS, getDocStatus());
		addReportParameter(CARD_TYPE, getCardType());
		addReportParameter(DATE_TO, getDateTo());
		addReportParameter(DATE, getDateFrom());
	}

	@Override
	protected OXPJasperDataSource createReportDataSource() {
		SettlementListDataSource dataSource = new SettlementListDataSource(get_TrxName());

		dataSource.setM_entidadfinanciera_id(getM_EntidadFinanciera_ID());
		dataSource.setCreditcardtype(getCardType());
		dataSource.setDocstatus(getDocStatus());
		dataSource.setAd_org_id(getAD_Org_ID());
		dataSource.setDateFrom(getDateFrom());
		dataSource.setDateTo(getDateTo());

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

	public String getDocStatus() {
		return (String) getParameterValue("DocStatus");
	}

	public String getCardType() {
		return (String) getParameterValue("CardType");
	}

}
