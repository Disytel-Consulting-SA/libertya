package org.openXpertya.JasperReport.DataSource;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.openXpertya.util.Env;

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
		int accountingCurrencyID = Env.getC_Currency_ID(Env.getCtx());
		String sql = ""
				+ "SELECT "
				+ "	 CASE "
				+ "		WHEN dt.DocBaseType = 'API' THEN 'FAC ' || i.DocumentNo "
				+ "     ELSE i.DocumentNo "
				+ "	 END AS DocumentNo, "
				+ "	 cu.iso_code as Currency, "
				+ "	 i.grandtotal AS GrandTotalAmt, "
				// +"	 SUM(al.amount + al.discountamt + al.writeoffamt) AS AllocatedAmt, "
				// OP#8516 La conversion usa la fecha de aplicacion de la factura, igual que el saldo (getAllocatedAmt);
				// con la fecha de emision el importe imputado no coincidia con el saldo cuando ambas fechas difieren
				+ "	 SUM(CASE "
				+ "		 WHEN ah.c_currency_id = i.c_currency_id THEN al.amount + al.discountamt + al.writeoffamt "
				// OP#8516 Pago en la moneda de la factura con tasa de cambio propia (anticipo): se usa esa tasa
				+ "		 WHEN al.c_payment_id IS NOT NULL AND payment_exchange_rate(al.c_payment_id) IS NOT NULL "
				+ "			 AND (SELECT p.c_currency_id FROM c_payment p WHERE p.c_payment_id = al.c_payment_id) = i.c_currency_id "
				+ "			 THEN currencyRound((al.amount + al.discountamt + al.writeoffamt)/payment_exchange_rate(al.c_payment_id), i.c_currency_id, NULL) "
				+ "		 WHEN ah.c_currency_id = " + accountingCurrencyID + " AND COALESCE(i.cintolo_exchange_rate, 0) > 0 "
				+ "			 THEN currencyRound((al.amount + al.discountamt + al.writeoffamt)/i.cintolo_exchange_rate, i.c_currency_id, NULL) "
				+ "		 ELSE currencyconvert(al.amount + al.discountamt + al.writeoffamt, ah.c_currency_id, i.c_currency_id, i.dateacct::timestamp with time zone, COALESCE(i.c_conversiontype_id, 0), ah.ad_client_id, ah.ad_org_id) "
				+ "	 END) AS AllocatedAmt, "
				+ "	 invoiceopen(i.C_Invoice_ID,0) AS OpenAmt "
				+ "FROM c_allocationhdr ah "
				+ "  JOIN c_allocationline al ON ah.c_allocationhdr_id = al.c_allocationhdr_id "
				+ "  JOIN c_invoice i ON i.C_Invoice_ID = al.C_Invoice_ID "
				+ "  JOIN c_currency cu ON i.C_Currency_ID = cu.C_Currency_ID "
				+ "  JOIN C_DocType dt ON dt.C_DocType_ID = i.C_DocTypetarget_ID "
				+ "WHERE ah.C_AllocationHdr_ID = ? "
				+ "GROUP BY al.C_Invoice_ID, i.DocumentNo, dt.DocBaseType, GrandTotalAmt, OpenAmt, Currency "
				// OP#8516 Notas de credito/debito de diferencia de cambio del comprobante que quedaron en borrador:
				// no son lineas de la asignacion, pero cubren parte de la factura. Se informan convertidas a la
				// moneda de la factura original (credito suma, debito resta) para que los importes cierren
				+ "UNION ALL "
				+ "SELECT dn.DocumentNo || ' (dif. de cambio)' AS DocumentNo, "
				+ "	 cu.iso_code AS Currency, "
				+ "	 (CASE WHEN dtd.DocBaseType IN ('ARC', 'APC') THEN 1 ELSE -1 END) * (CASE "
				+ "		 WHEN oi.c_invoice_id IS NULL OR oi.c_currency_id = dn.c_currency_id THEN dn.grandtotal "
				+ "		 WHEN dn.c_currency_id = " + accountingCurrencyID + " AND COALESCE(oi.cintolo_exchange_rate, 0) > 0 "
				+ "			 THEN currencyRound(dn.grandtotal/oi.cintolo_exchange_rate, oi.c_currency_id, NULL) "
				+ "		 ELSE currencyconvert(dn.grandtotal, dn.c_currency_id, oi.c_currency_id, oi.dateacct::timestamp with time zone, COALESCE(oi.c_conversiontype_id, 0), dn.ad_client_id, dn.ad_org_id) "
				+ "	 END)::numeric AS GrandTotalAmt, "
				+ "	 (CASE WHEN dtd.DocBaseType IN ('ARC', 'APC') THEN 1 ELSE -1 END) * (CASE "
				+ "		 WHEN oi.c_invoice_id IS NULL OR oi.c_currency_id = dn.c_currency_id THEN dn.grandtotal "
				+ "		 WHEN dn.c_currency_id = " + accountingCurrencyID + " AND COALESCE(oi.cintolo_exchange_rate, 0) > 0 "
				+ "			 THEN currencyRound(dn.grandtotal/oi.cintolo_exchange_rate, oi.c_currency_id, NULL) "
				+ "		 ELSE currencyconvert(dn.grandtotal, dn.c_currency_id, oi.c_currency_id, oi.dateacct::timestamp with time zone, COALESCE(oi.c_conversiontype_id, 0), dn.ad_client_id, dn.ad_org_id) "
				+ "	 END)::numeric AS AllocatedAmt, "
				+ "	 0::numeric AS OpenAmt "
				+ "FROM c_invoice dn "
				+ "  JOIN C_DocType dtd ON dtd.C_DocType_ID = dn.C_DocTypetarget_ID "
				+ "  LEFT JOIN c_invoice oi ON oi.C_Invoice_ID = dn.C_Invoice_Orig_ID "
				+ "  JOIN c_currency cu ON cu.C_Currency_ID = COALESCE(oi.C_Currency_ID, dn.C_Currency_ID) "
				+ "WHERE dn.Cintolo_Exchange_Dif_Receipt = ? "
				+ "  AND dn.DocStatus IN ('DR', 'IP') "
				+ "  AND NOT EXISTS (SELECT 1 FROM c_allocationline x WHERE x.C_AllocationHdr_ID = dn.Cintolo_Exchange_Dif_Receipt AND x.C_Invoice_ID = dn.C_Invoice_ID)";
		return sql;
	}

	@Override
	protected void setQueryParameters(PreparedStatement pstmt)
			throws SQLException {
		pstmt.setInt(1, getOrdenPagoDataSource().getP_C_AllocationHdr_ID());
		pstmt.setInt(2, getOrdenPagoDataSource().getP_C_AllocationHdr_ID());
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
