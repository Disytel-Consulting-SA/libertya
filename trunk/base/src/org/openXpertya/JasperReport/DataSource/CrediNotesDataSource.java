package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.sf.jasperreports.engine.JRException;

/*
 * *********************************************************** /
 * CreditNotesDataSource: Clase que contiene el DataSource para el
 * subreporte de Notas de Crédito del reporte de Recibos de Cliente.
 * ***********************************************************
 */
class CreditNotesDataSource extends OPDataSource {

	public Object getFieldValue(String name, Object record)
			throws JRException {
		M_CreditNote payment = (M_CreditNote) record;
		if (name.toUpperCase().equals("PAYMENTTYPE")) {
			return payment.paymentType;
		} else if (name.toUpperCase().equals("DESCRIPTION")) {
			return payment.description;
		} else if (name.toUpperCase().equals("DOCUMENTNO")) {
			return payment.documentNo;
		} else if (name.toUpperCase().equals("DOCUMENTDATE")) {
			return payment.documentDate;
		} else if (name.toUpperCase().equals("AMOUNT")) {
			return payment.amount;
		} else if (name.toUpperCase().equals("CURRENCY")) {
			return payment.currency;
		}
		return null;
	}

	@Override
	protected Object createRecord(ResultSet rs) throws SQLException {
		return new M_CreditNote(rs);
	}

	@Override
	protected String getDataSQL() {
		String sql = getOrdenPagoDataSource().getCreditNotesQuery();
		return sql;
	}
	
	@Override
	protected void setQueryParameters(PreparedStatement pstmt)
			throws SQLException {
		int i = 1;
		pstmt.setInt(i++, getOrdenPagoDataSource().p_C_AllocationHdr_ID);
	}
	
	/**
	 * POJO de Notas de Crédito.
	 */
	private class M_CreditNote {
		protected String paymentType;
		protected String description;
		protected String documentNo;
		protected Timestamp documentDate;
		protected BigDecimal amount;
		protected String currency;

		public M_CreditNote(String paymentType, String description,
				String documentNo, Timestamp documentDate, BigDecimal amount, String currency) {
			super();
			this.paymentType = paymentType;
			this.description = description;
			this.documentNo = documentNo;
			this.documentDate = documentDate;
			this.amount = amount;
			this.currency = currency;
		}

		public M_CreditNote(ResultSet rs) throws SQLException {
			this(rs.getString("PaymentType"), rs.getString("description"), rs
					.getString("DocumentNo"),
					rs.getTimestamp("DocumentDate"), (getOrdenPagoDataSource().getPaymentOrder()
							.isAdvanced() ? rs.getBigDecimal("PayAmt") : rs
							.getBigDecimal("Amount")), rs
							.getString("Currency"));
		}
	}
}