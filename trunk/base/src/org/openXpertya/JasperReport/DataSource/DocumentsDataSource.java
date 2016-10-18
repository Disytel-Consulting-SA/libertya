package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.jasperreports.engine.JRException;

/*
 * *********************************************************** /
 * DocumentsDataSource: Clase que contiene el DataSource para el subreporte
 * de Comprobantes del reporte de Orden de Pago.
 * ***********************************************************
 */
public class DocumentsDataSource extends OPDataSource {

	@Override
	protected Object getFieldValue(String name, Object record)
			throws JRException {
		M_Document document = (M_Document) record;
		if (name.toUpperCase().equals("DOCUMENTNO")) {
			return document.documentNo;
		} else if (name.toUpperCase().equals("CURRENCY")) {
			return document.currency;
		} else if (name.toUpperCase().equals("GRANDTOTALAMT")) {
			return document.grandTotalAmt;
		} else if (name.toUpperCase().equals("ALLOCATEDAMT")) {
			return document.allocatedAmt;
		} else if (name.toUpperCase().equals("OPENAMT")) {
			return document.openAmt;
		}
		return null;
	}

	@Override
	protected Object createRecord(ResultSet rs) throws SQLException {
		return new M_Document(rs);
	}

	protected String getDataSQL() {
		String sql = ""
				+ "SELECT "
				+ "	 CASE "
				+ "		WHEN dt.DocBaseType = 'API' THEN 'FAC ' || i.DocumentNo "
				+ "     ELSE i.DocumentNo "
				+ "	 END AS DocumentNo, "
				+ "	 cu.iso_code as Currency, "
				+ "	 i.grandtotal AS GrandTotalAmt, "
				// +"	 SUM(al.amount + al.discountamt + al.writeoffamt) AS AllocatedAmt, "
				+ "	 SUM(currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, ah.datetrx::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id)) AS AllocatedAmt, "
				+ "	 invoiceopen(i.C_Invoice_ID,0) AS OpenAmt "
				+ "FROM c_allocationhdr ah "
				+ "  JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "  JOIN c_invoice i ON i.C_Invoice_ID = al.C_Invoice_ID "
				+ "  JOIN c_currency cu ON i.C_Currency_ID = cu.C_Currency_ID "
				+ "  JOIN C_DocType dt ON dt.C_DocType_ID = i.C_DocType_ID "
				+ "WHERE ah.C_AllocationHdr_ID = ? "
				+ "GROUP BY al.C_Invoice_ID, i.DocumentNo, dt.DocBaseType, GrandTotalAmt, OpenAmt, Currency ";
		return sql;
	}

	@Override
	protected void setQueryParameters(PreparedStatement pstmt)
			throws SQLException {
		pstmt.setInt(1, getOrdenPagoDataSource().p_C_AllocationHdr_ID);
	}

	/**
	 * POJO de Cheques.
	 */
	public class M_Document {

		protected String documentNo;
		protected String currency;
		protected BigDecimal grandTotalAmt;
		protected BigDecimal allocatedAmt;
		protected BigDecimal openAmt;

		public M_Document(String documentNo, String currency,
				BigDecimal grandTotalAmt, BigDecimal allocatedAmt,
				BigDecimal openAmt) {
			super();
			this.documentNo = documentNo;
			this.currency = currency;
			this.grandTotalAmt = grandTotalAmt;
			this.allocatedAmt = allocatedAmt;
			this.openAmt = openAmt;
		}

		public M_Document(ResultSet rs) throws SQLException {
			this(rs.getString("DocumentNo"), rs.getString("Currency"), rs
					.getBigDecimal("GrandTotalAmt"), rs
					.getBigDecimal("AllocatedAmt"), rs
					.getBigDecimal("OpenAmt"));
		}
	}
}
