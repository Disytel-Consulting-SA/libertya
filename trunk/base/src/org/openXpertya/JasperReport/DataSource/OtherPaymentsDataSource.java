package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.sf.jasperreports.engine.JRException;

/*
 * *********************************************************** /
 * OtherPaymentsDataSource: Clase que contiene el DataSource para el
 * subreporte de Otros Pagos del reporte de Orden de Pago.
 * ***********************************************************
 */
class OtherPaymentsDataSource extends OPDataSource {

	public Object getFieldValue(String name, Object record)
			throws JRException {
		M_Payment payment = (M_Payment) record;
		if (name.toUpperCase().equals("PAYMENTTYPE")) {
			return payment.paymentType;
		} else if (name.toUpperCase().equals("CASHNAME")) {
			return payment.cashName;
		} else if (name.toUpperCase().equals("BANKACCOUNT")) {
			return payment.bankAccount;
		} else if (name.toUpperCase().equals("ROUTINGNO")) {
			return payment.routingNo;
		} else if (name.toUpperCase().equals("TRANSFERDATE")) {
			return payment.transferDate;
		} else if (name.toUpperCase().equals("AMOUNT")) {
			return payment.amount;
		} else if (name.toUpperCase().equals("CURRENCY")) {
			return payment.currency;
		}
		return null;
	}

	@Override
	protected Object createRecord(ResultSet rs) throws SQLException {
		return new M_Payment(rs);
	}

	@Override
	protected String getDataSQL() {
		String sql = ""
				+ "(SELECT DISTINCT p.C_Payment_ID                  , "
				+ "        (CASE WHEN tendertype = 'A' THEN 'Transferencia' ELSE 'Tarjeta' END)::CHARACTER VARYING AS PaymentType, "
				+ "        "
				+ getOrdenPagoDataSource().getCashNameDescription()
				+ "     AS CashName   , "
				+ // redefinir
				"        (SELECT ba.cc "
				+ "                || ' - ' "
				+ "                || b.Name "
				+ "        FROM    C_Bank b "
				+ "                INNER JOIN C_BankAccount ba "
				+ "                ON (b.C_Bank_ID     = ba.C_Bank_ID) "
				+ "        WHERE   ba.C_BankAccount_ID = p.C_BankAccount_ID "
				+ "        ) AS BankAccount          , "
				+ "        (CASE WHEN tendertype = 'A' THEN p.checkno ELSE p.creditcardnumber END)  as RoutingNo , "
				+ "        p.DateAcct AS TransferDate, "
				+ "        sum (currencyconvert(al.amount, ah.c_currency_id, p.c_currency_id, p.DateAcct::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id )) as Amount,"
				+
				// "        sum(al.amount) AS Amount,   " +
				"        p.payamt AS PayAmt, "
				+ "        cu.iso_code as Currency "
				+
				/*
				 * "        currencyconvert(al.amount, p.c_currency_id, ah.c_currency_id, ah.datetrx::TIMESTAMP "
				 * + "WITH TIME zone         , " +
				 * "        NULL::INTEGER  , " + "        ah.ad_client_id, "
				 * + "        ah.ad_org_id) AS Amount " +
				 */
				"FROM    c_allocationhdr ah "
				+ "        JOIN c_allocationline al "
				+ "        ON      ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "        JOIN c_payment p "
				+ "        ON      al.c_payment_id = p.c_payment_id "
				+ "  		 JOIN c_currency cu ON p.C_Currency_ID = cu.C_Currency_ID "
				+ "WHERE   p.tenderType IN ('A','C') "
				+ "    AND ah.C_AllocationHdr_ID   = ? "
				+ "GROUP BY p.C_Payment_ID, tendertype, CashName, BankAccount, p.checkno, p.creditcardnumber, p.dateAcct, PayAmt, Currency "
				+
				// "ORDER BY PaymentType, " +
				// "        BankAccount " +
				") "
				+ " "
				+ "UNION ALL "
				+ "        (SELECT DISTINCT ll.C_CashLine_ID                , "
				+ "                'Efectivo'::CHARACTER VARYING AS PaymentType, "
				+ "                cb.name "
				+ "                || ' - ' "
				+ "                || c.name               AS CashName    , "
				+ "                NULL::CHARACTER VARYING AS BankAccount , "
				+ "                NULL::CHARACTER VARYING AS RoutingNo   , "
				+ "                c.DateAcct              AS TransferDate, "
				+
				/*
				 * "                currencyconvert(ABS(ll.amount), cb.c_currency_id, ah.c_currency_id, ah.datetrx::TIMESTAMP "
				 * + "WITH TIME zone         , " +
				 * "        NULL::INTEGER  , " + "        ah.ad_client_id, "
				 * + "        ah.ad_org_id) AS Amount " +
				 */
				"        sum (currencyconvert(al.amount, ah.c_currency_id, ll.c_currency_id, c.DateAcct::timestamp with time zone, NULL::integer, ah.ad_client_id, ah.ad_org_id )) as Amount,"
				+
				// "                SUM(al.amount) AS Amount, " +
				"                ABS(ll.amount) AS PayAmt,  "
				+ "                cu.iso_code AS Currency  "
				+ "        FROM    c_allocationhdr ah "
				+ "                JOIN c_allocationline al "
				+ "                ON      ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "                JOIN c_cashline ll "
				+ "                ON      al.c_cashline_id = ll.c_cashline_id "
				+ "                JOIN c_cash c "
				+ "                ON      c.C_Cash_ID = ll.C_Cash_ID "
				+ "                JOIN C_CashBook cb "
				+ "                ON      cb.C_CashBook_ID = c.C_CashBook_ID "
				+ "		  		 JOIN c_currency cu ON cb.C_Currency_ID = cu.C_Currency_ID "
				+ "        WHERE   true "
				+ "            AND ah.C_AllocationHdr_ID = ? "
				+ "        GROUP BY ll.C_CashLine_ID, PaymentType, CashName, BankAccount, RoutingNo, TransferDate, PayAmt, Currency "
				+ "        ORDER BY PaymentType, "
				+ "                CashName "
				+ "        ) "
				+ "UNION ALL "
				+ getRetentionQuery();
		return sql;
	}
	
	/**
	 * Query para retornar la nómina de invoices usadas como crédito
	 * Utilizada también en el Launch para obtener el total de NC del Allocation! 
	 */
	public String getRetentionQuery() {
		return 
				" (SELECT DISTINCT i.C_Invoice_ID, "
				+ "		 'Retención'::CHARACTER VARYING AS PaymentType, "
				+ "  CASE "
				+ "		WHEN ri.C_RetencionSchema_ID IS NOT NULL THEN rs.name "
				+ "		ELSE (dt.PrintName)::CHARACTER VARYING "
				+ "	END AS Description2, "
				+ "		 NULL::CHARACTER VARYING AS BankAccount, "
				+ "		 i.DocumentNo::CHARACTER VARYING AS RoutingNo, "
				+ "		 i.DateAcct AS TransferDate, "
				+ "		 sum(currencyconvert(al.amount, ah.c_currency_id, i.c_currency_id, i.DateAcct::TIMESTAMP WITH TIME zone, NULL::INTEGER, ah.ad_client_id, ah.ad_org_id)) AS Amount, "
				+
				"        i.grandtotal AS PayAmt, "
				+ "        cu.iso_code AS Currency  "
				+ " FROM    c_allocationhdr ah "
				+ "	 INNER JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "	 INNER JOIN c_invoice i ON al.c_invoice_credit_id = i.c_invoice_id "
				+ "  	 INNER JOIN c_currency cu ON i.C_Currency_ID = cu.C_Currency_ID "
				+ " 	 INNER JOIN c_doctype dt ON dt.c_doctype_id = i.c_doctype_id "
				+ "    LEFT JOIN M_Retencion_Invoice ri ON i.c_invoice_id = ri.c_invoice_id "
				+ "    LEFT JOIN C_RetencionSchema rs ON ri.c_retencionschema_ID = rs.c_retencionschema_id "
				+ " WHERE  ah.C_AllocationHdr_ID   = ? AND dt.doctypekey IN ('RTI', 'RTR', 'RCR', 'RCI')"
				+
				" GROUP BY i.C_Invoice_ID, PaymentType, Description2, BankAccount, RoutingNo, TransferDate, PayAmt, Currency "
				+ " ORDER BY Description2, i.DateAcct) ";
	}
	
	@Override
	protected void setQueryParameters(PreparedStatement pstmt)
			throws SQLException {
		int i = 1;
		pstmt.setInt(i++, getOrdenPagoDataSource().p_C_AllocationHdr_ID);
		pstmt.setInt(i++, getOrdenPagoDataSource().p_C_AllocationHdr_ID);
		pstmt.setInt(i++, getOrdenPagoDataSource().p_C_AllocationHdr_ID);
	}

	/**
	 * POJO de Otros Pagos.
	 */
	private class M_Payment {
		protected String paymentType;
		protected String cashName;
		protected String bankAccount;
		protected String routingNo;
		protected Timestamp transferDate;
		protected BigDecimal amount;
		protected String currency;

		public M_Payment(String paymentType, String cashName,
				String bankAccount, String routingNo,
				Timestamp transferDate, BigDecimal amount, String currency) {
			super();
			this.paymentType = paymentType;
			this.cashName = cashName;
			this.bankAccount = bankAccount;
			this.routingNo = routingNo;
			this.transferDate = transferDate;
			this.amount = amount;
			this.currency = currency;
		}

		public M_Payment(ResultSet rs) throws SQLException {
			this(rs.getString("PaymentType"), rs.getString("CashName"), rs
					.getString("BankAccount"), rs.getString("RoutingNo"),
					rs.getTimestamp("TransferDate"), (getOrdenPagoDataSource().getPaymentOrder()
							.isAdvanced() ? rs.getBigDecimal("PayAmt") : rs
							.getBigDecimal("Amount")), rs
							.getString("Currency"));
		}
	}
}