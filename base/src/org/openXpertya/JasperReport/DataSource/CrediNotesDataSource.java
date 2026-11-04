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
		// OP#8516 Ademas de las notas de credito imputadas, se informan las de diferencia de cambio del
		// comprobante que quedaron en borrador (no imputadas), con su importe en moneda de la nota, para
		// que se vea como se cubre la diferencia entre la factura y lo cobrado/pagado
		String sql = "SELECT q.C_Invoice_ID, q.PaymentType, "
				+ "	 (CASE WHEN ci.Cintolo_Apply_Exchange_Dif = 'Y' THEN q.Description || ' (imputado)' ELSE q.Description END)::CHARACTER VARYING AS Description, "
				+ "	 q.DocumentNo, q.DocumentDate, q.Amount, q.PayAmt, q.Currency "
				+ "FROM (" + getOrdenPagoDataSource().getCreditNotesQuery() + ") q "
				+ "  JOIN c_invoice ci ON ci.C_Invoice_ID = q.C_Invoice_ID "
				+ "UNION ALL "
				+ "SELECT dn.C_Invoice_ID, "
				+ "	 'Crédito'::CHARACTER VARYING AS PaymentType, "
				+ "	 (dt.PrintName || ' (s/imputar)')::CHARACTER VARYING AS Description, "
				+ "	 dn.DocumentNo::CHARACTER VARYING AS DocumentNo, "
				+ "	 dn.DateAcct AS DocumentDate, "
				+ "	 dn.GrandTotal AS Amount, "
				+ "	 dn.GrandTotal AS PayAmt, "
				+ "	 cu.iso_code AS Currency "
				+ "FROM c_invoice dn "
				+ "  JOIN c_doctype dt ON dt.C_DocType_ID = dn.C_DocTypetarget_ID "
				+ "  JOIN c_currency cu ON cu.C_Currency_ID = dn.C_Currency_ID "
				+ "WHERE dn.Cintolo_Exchange_Dif_Receipt = ? "
				+ "  AND dn.DocStatus IN ('DR', 'IP') "
				+ "  AND dt.DocBaseType IN ('ARC', 'APC') "
				+ "  AND NOT EXISTS (SELECT 1 FROM c_allocationline x WHERE x.C_AllocationHdr_ID = dn.Cintolo_Exchange_Dif_Receipt AND x.C_Invoice_Credit_ID = dn.C_Invoice_ID)";
		return sql;
	}
	
	@Override
	protected void setQueryParameters(PreparedStatement pstmt)
			throws SQLException {
		int i = 1;
		pstmt.setInt(i++, getOrdenPagoDataSource().getP_C_AllocationHdr_ID());
		pstmt.setInt(i++, getOrdenPagoDataSource().getP_C_AllocationHdr_ID());
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