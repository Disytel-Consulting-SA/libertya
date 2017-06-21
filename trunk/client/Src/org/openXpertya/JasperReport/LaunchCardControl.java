package org.openXpertya.JasperReport;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;

import org.openXpertya.JasperReport.DataSource.CardControlDataSource;
import org.openXpertya.JasperReport.DataSource.OXPJasperDataSource;
import org.openXpertya.model.X_AD_Org;
import org.openXpertya.model.X_M_EntidadFinanciera;
import org.openXpertya.util.DB;

/**
 * Reporte para control de tarjetas.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class LaunchCardControl extends JasperReportLaunch {
	// Parámetros del reporte
	private final static String M_ENTIDADFINANCIERA_NAME = "M_ENTIDADFINANCIERA_NAME";
	private final static String AD_ORG_NAME = "AD_ORG_NAME";
	private final static String DATE_FROM = "DATE_FROM";
	private final static String DATE_TO = "DATE_TO";

	@Override
	protected void loadReportParameters() throws Exception {
		addReportParameter(M_ENTIDADFINANCIERA_NAME, getM_EntidadFinanciera_Name());
		addReportParameter(AD_ORG_NAME, getAD_Org_Name());
		addReportParameter(DATE_FROM, getDateFrom());
		addReportParameter(DATE_TO, getDateTo());
	}

	protected CardControlDataSource dataSource;
	
	@Override
	protected OXPJasperDataSource createReportDataSource() {
		dataSource = new CardControlDataSource(get_TrxName());

		dataSource.setM_EntidadFinanciera_ID(getM_EntidadFinanciera_ID());
		dataSource.setAD_Org_ID(getAD_Org_ID());
		dataSource.setDateFrom(getDateFrom());
		dataSource.setDateTo(getDateTo());

		return dataSource;
	}

	private String getM_EntidadFinanciera_Name() {
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
			log.log(Level.SEVERE, "getM_EntidadFinanciera_Name", e);
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

	private String getAD_Org_Name() {
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
			log.log(Level.SEVERE, "getAD_Org_Name", e);
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
		return (Integer) getParameterValue("AD_Org_ID");
	}

	public Timestamp getDateFrom() {
		return (Timestamp) getParameterValue("Date");
	}

	public Timestamp getDateTo() {
		return (Timestamp) getParameterValue("Date_To");
	}

	public int getM_EntidadFinanciera_ID() {
		return (Integer) getParameterValue("M_EntidadFinanciera_ID");
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
	
	@Override
	public void cancelProcess() {
		if (dataSource!=null)
			DB.cancelStatement(dataSource.getPstmt());
	}
	
}
