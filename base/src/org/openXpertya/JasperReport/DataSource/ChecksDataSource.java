package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.sf.jasperreports.engine.JRException;

/*
 * *********************************************************** /
 * ChecksDataSource: Clase que contiene el DataSource para el subreporte de
 * Cheques del reporte de Orden de Pago.
 * ***********************************************************
 */
class ChecksDataSource extends OPDataSource {

	@Override
	protected Object getFieldValue(String name, Object record)
			throws JRException {
		M_Check check = (M_Check) record;
		if (name.toUpperCase().equals("DOCUMENTCHECKNO")) {
			return check.documentCheckNo;
		} else if (name.toUpperCase().equals("CURRENCY")) {
			return check.currency;
		} else if (name.toUpperCase().equals("BANK")) {
			return check.bank;
		} else if (name.toUpperCase().equals("DUEDATE")) {
			return check.dueDate;
		} else if (name.toUpperCase().equals("AMOUNT")) {
			return check.amount;
		}
		return null;
	}

	@Override
	protected Object createRecord(ResultSet rs) throws SQLException {
		return new M_Check(rs);
	}

	protected String getDataSQL() {
		String sql = ""
				+ "SELECT DISTINCT p.C_Payment_ID, "
				+ "        CASE "
				+ "                WHEN p.CheckNo IS NOT NULL "
				+ "                THEN p.checkNo "
				+ "                ELSE p.DocumentNo::CHARACTER VARYING "
				+ "        END AS DocumentCheckNo, "
				+ "        cu.iso_code as Currency, "
				+ "        CASE "
				+ "               WHEN P.A_BANK IS NOT NULL AND P.A_BANK <> '' "
				+ "               THEN P.A_BANK  "
				+ "				ELSE (SELECT b.Name "
				+ "               FROM C_Bank b INNER JOIN C_BankAccount ba "
				+ "               ON (b.C_Bank_ID = ba.C_Bank_ID) "
				+ "               WHERE ba.C_BankAccount_ID = p.C_BankAccount_ID)"
				+ "         END AS Bank,"
				+ "        p.DueDate, "
				+
				/*
				 * "        currencyconvert(al.amount, p.c_currency_id, ah.c_currency_id, ah.datetrx::TIMESTAMP "
				 * + "WITH TIME zone         , " +
				 * "        NULL::INTEGER  , " + "        ah.ad_client_id, "
				 * + "        ah.ad_org_id) AS Amount " +
				 */
				"        sum (currencyconvert(al.amount, ah.c_currency_id, p.c_currency_id, p.DateAcct::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id )) as Amount,"
				+ "        p.payamt as PayAmt "
				+ "FROM    c_allocationhdr ah "
				+ "        JOIN c_allocationline al "
				+ "        ON      ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "        JOIN c_payment p "
				+ "        ON      al.c_payment_id = p.c_payment_id "
				+ "  		 JOIN c_currency cu ON p.C_Currency_ID = cu.C_Currency_ID "
				+ "WHERE   p.tenderType            = 'K' "
				+ "    AND ah.C_AllocationHdr_ID   = ? "
				+ "GROUP BY p.C_Payment_ID, DocumentCheckNo, Bank, p.DueDate, PayAmt, Currency  ";
		// "ORDER BY documentCheckNo ";
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
	private class M_Check {
		protected String documentCheckNo;
		protected String currency;
		protected String bank;
		protected Timestamp dueDate;
		protected BigDecimal amount;

		public M_Check(String documentCheckNo, String currency,
				String bank, Timestamp dueDate, BigDecimal amount) {
			super();
			this.documentCheckNo = documentCheckNo;
			this.currency = currency;
			this.bank = bank;
			this.dueDate = dueDate;
			this.amount = amount;
		}

		public M_Check(ResultSet rs) throws SQLException {
			this(rs.getString("DocumentCheckNo"), rs.getString("Currency"),
					rs.getString("Bank"), rs.getTimestamp("DueDate"),
					(getOrdenPagoDataSource().getPaymentOrder().isAdvanced() ? rs
							.getBigDecimal("PayAmt") : rs
							.getBigDecimal("Amount")));
		}
	}
}