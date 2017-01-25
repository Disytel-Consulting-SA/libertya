package org.openXpertya.JasperReport.DataSource;

import java.util.Properties;

import org.openXpertya.util.Env;

/**
 * Data Source para reporte de listado de cupones totalizados por estado.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class ListOfCouponsDataSource extends QueryDataSource {
	/** Contexto de ejecución. */
	private Properties ctx;

	private int M_EntidadFinanciera_ID;
	private String auditState;
	private String dateFrom;
	private String dateTo;
	private int AD_Org_ID;

	public ListOfCouponsDataSource(String trxName) {
		super(trxName);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	ef.name, ");
		sql.append("	c.paymentbatch, ");
		sql.append("	s.settlementno, ");
		sql.append("	c.couponno, ");
		sql.append("	c.trxdate, ");
		sql.append("	l.name AS auditstatus, ");
		sql.append("	c.amount ");
		sql.append("FROM ");
		sql.append("	c_creditcardsettlement s ");
		sql.append("	JOIN c_couponssettlements c ON s.c_creditcardsettlement_id = c.c_creditcardsettlement_id ");
		sql.append("	JOIN c_payment p ON p.c_payment_id = c.c_payment_id ");
		sql.append("	JOIN m_entidadfinanciera ef ON s.m_entidadfinanciera_id = ef.m_entidadfinanciera_id ");
		sql.append("	LEFT JOIN ( ");
		sql.append("		SELECT ");
		sql.append("			r.value, ");
		sql.append("			tr.name ");
		sql.append("		FROM ");
		sql.append("			ad_reference a ");
		sql.append("			INNER JOIN ad_ref_list r ON a.ad_reference_id = r.ad_reference_id ");
		sql.append("			INNER JOIN ad_ref_list_trl tr ON tr.ad_ref_list_id = r.ad_ref_list_id ");
		sql.append("		WHERE ");
		sql.append("			a.name = 'Audit Status' ");
		sql.append("			AND tr.ad_language = '" + Env.getAD_Language(ctx) + "' ");
		sql.append("	) l ON l.value = p.auditstatus ");
		sql.append("WHERE ");
		sql.append("	COALESCE(s.settlementno, '') <> '' ");

		if (M_EntidadFinanciera_ID > 0) {
			sql.append("	AND e.m_entidadfinanciera_id = " + M_EntidadFinanciera_ID + " ");
		}
		if (AD_Org_ID > 0) {
			sql.append("	AND s.ad_org_id = " + AD_Org_ID + " ");
		}
		if (auditState != null && !auditState.trim().isEmpty()) {
			sql.append("	AND p.auditstatus = " + auditState + " ");
		}
		if (dateFrom != null && !dateFrom.trim().isEmpty()) {
			sql.append("	AND s.paymentdate >= '" + dateFrom + "' ");
		}
		if (dateTo != null && !dateTo.trim().isEmpty()) {
			sql.append("	AND s.paymentdate <= '" + dateTo + "' ");
		}

		sql.append("ORDER BY ");
		sql.append("	p.auditstatus, ");
		sql.append("	c.trxdate, ");
		sql.append("	ef.name, ");
		sql.append("	c.paymentbatch, ");
		sql.append("	s.settlementno, ");
		sql.append("	c.couponno ");

		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		return new Object[0];
	}

	@Override
	protected boolean isQueryNoConvert() {
		return true;
	}

	// SETTERS:

	public void setM_EntidadFinanciera_ID(int m_EntidadFinanciera_ID) {
		M_EntidadFinanciera_ID = m_EntidadFinanciera_ID;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public void setAD_Org_ID(int aD_Org_ID) {
		AD_Org_ID = aD_Org_ID;
	}

	public void setAuditState(String auditState) {
		this.auditState = auditState;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
