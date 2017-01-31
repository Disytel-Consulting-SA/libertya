package org.openXpertya.JasperReport.DataSource;

import org.openXpertya.model.X_C_CreditCardSettlement;
import org.openXpertya.model.X_M_EntidadFinanciera;

/**
 * Data Source para reporte de listado de liquidaciones.
 * @author Kevin Feuerschvenger - Sur Software S.H.
 */
public class SettlementListDataSource extends QueryDataSource {

	private int m_entidadfinanciera_id;
	private String creditcardtype;
	private String docstatus;
	private String dateFrom;
	private String dateTo;
	private int ad_org_id;

	public SettlementListDataSource(String trxName) {
		super(trxName);
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	LPAD(s.settlementno, 8, '0') AS settlementno, ");
		sql.append("	e.name AS creditcard, ");
		sql.append("	COALESCE(amount, 0) AS amount, ");
		sql.append("	COALESCE(couponstotalamount, 0) AS done, ");
		sql.append("	COALESCE(ivaamount, 0) AS iva, ");
		sql.append("	COALESCE(withholding, 0) AS retention, ");
		sql.append("	COALESCE(expenses, 0) AS expenses, ");
		sql.append("	COALESCE(commissionamount, 0) AS commission, ");
		sql.append("	COALESCE(netamount, 0) AS netamount, ");
		sql.append("	COALESCE(perception, 0) AS perception, ");
		sql.append("	paymentdate AS date ");
		sql.append("FROM ");
		sql.append("	" + X_C_CreditCardSettlement.Table_Name + " s ");
		sql.append("	INNER JOIN " + X_M_EntidadFinanciera.Table_Name + " e ON s.m_entidadfinanciera_id = e.m_entidadfinanciera_id ");
		sql.append("WHERE ");
		sql.append("	COALESCE(s.settlementno, '') <> '' ");

		if (m_entidadfinanciera_id > 0) {
			sql.append("	AND e.m_entidadfinanciera_id = " + m_entidadfinanciera_id + " ");
		}
		if (ad_org_id > 0) {
			sql.append("	AND s.ad_org_id = " + ad_org_id + " ");
		}
		if (creditcardtype != null && !creditcardtype.trim().isEmpty()) {
			sql.append("	AND s.creditcardtype = '" + creditcardtype + "' ");
		}
		if (docstatus != null && !docstatus.trim().isEmpty()) {
			sql.append("	AND s.docstatus = '" + docstatus + "' ");
		}
		if (dateFrom != null && !dateFrom.trim().isEmpty()) {
			sql.append("	AND s.paymentdate >= '" + dateFrom + "' ");
		}
		if (dateTo != null && !dateTo.trim().isEmpty()) {
			sql.append("	AND s.paymentdate <= '" + dateTo + "' ");
		}

		sql.append("ORDER BY ");
		sql.append("	s.settlementno ");

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

	// SETTERS

	public void setM_entidadfinanciera_id(int m_entidadfinanciera_id) {
		this.m_entidadfinanciera_id = m_entidadfinanciera_id;
	}

	public void setCreditcardtype(String creditcardtype) {
		this.creditcardtype = creditcardtype;
	}

	public void setDocstatus(String docstatus) {
		this.docstatus = docstatus;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public void setAd_org_id(int ad_org_id) {
		this.ad_org_id = ad_org_id;
	}

}
