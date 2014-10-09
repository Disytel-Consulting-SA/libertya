package org.openXpertya.cc;

import java.sql.Timestamp;
import java.util.Properties;

import org.openXpertya.util.Env;

/**
 * Clase generadora de consulta de cuenta corriente. Se centraliza para tener
 * todo en una clase y cualquier cambio se aplica aquí dentro. Por lo pronto
 * esta clase y sus querys se utilizan en las clases {@link #org.openXpertya.process.CurrentAccountReport} y {@link #org.openXpertya.cc.OnCreditCurrentAccountBalanceData}
 * .<br>
 * Las consultas obtienen el detalle de todos los documentos de la EC, aplicando
 * ademas los filtros de Tipo de Documento y Organización asignados como
 * parámetro del reporte. Realiza la conversión de montos a la moneda del
 * Contexto dado que los pagos pueden estar expresados en otra moneda. NO aplica
 * el filtro de fechas para que sea reutilizable la consulta.
 * <ul>
 * <li>El saldo de las Invoices se calcula haciendo: La sumatoria de lo
 * facturado (amount + writeoffamt + discountamt de C_AllocationLine) + La
 * sumatoria de lo pendiente por las Facturas (invoiceOpen) convertido a tasa
 * actual.</li>
 * <li>El saldo de los Payments se calcula haciendo: La sumatoria de lo cobrado
 * (amount de C_AllocationLine) + La sumatoria de lo pendiente por las pagos
 * (paymentavailable) convertido a tasa actual.</li>
 * <li>El saldo de las CashLine se calcula haciendo: La sumatoria de lo cobrado
 * (amount de C_AllocationLine) + La sumatoria de lo pendiente por las pagos
 * (cashlineavailable) convertido a tasa actual.</li>
 * </ul>
 */

public class CurrentAccountQuery {

	/** Organización */
	private Integer orgID;

	/** Tipo de Documento */
	private Integer docTypeID;

	/** Detalle de Cobros */
	private Boolean detailReceiptsPayments;

	/** Fecha desde */
	private Timestamp dateFrom;

	/** Fecha hasta */
	private Timestamp dateTo;

	/** Contexto */
	private Properties ctx;

	/** Moneda de la compañía */
	private Integer currencyID;

	public CurrentAccountQuery(Properties ctx, Integer orgID,
			Integer docTypeID, Boolean detailReceiptsPayments,
			Timestamp dateFrom, Timestamp dateTo) {
		setCtx(ctx);
		setOrgID(orgID);
		setDocTypeID(docTypeID);
		setDetailReceiptsPayments(detailReceiptsPayments);
		setDateFrom(dateFrom);
		setDateTo(dateTo);
		setCurrencyID(Env.getContextAsInt(getCtx(), "$C_Currency_ID"));
	}

	/**
	 * @return Query de cuenta corriente de todos los documentos, sin filtros de
	 *         fechas
	 */
	public String getAllDocumentsQuery() {
		StringBuffer sqlDoc = new StringBuffer();
		if (detailReceiptsPayments) {
			sqlDoc.append(" SELECT ");
			sqlDoc.append(" 	d.Dateacct::date as DateTrx, ");
			sqlDoc.append(" 	d.C_DocType_ID, ");
			sqlDoc.append(" 	d.DocumentNo, ");
			sqlDoc.append(" 	d.duedate, ");
			sqlDoc.append("     ABS(CASE WHEN d.signo_issotrx = ? THEN ");
			sqlDoc.append("     (SELECT CASE ");
			// sqlDoc.append("     WHEN d.documenttable = 'C_Invoice' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount + (CASE WHEN al.c_invoice_credit_id IS NULL THEN 0.0 ELSE (al.writeoffamt + al.discountamt) END )) END) FROM C_AllocationLine al WHERE ((al.c_invoice_id = d.document_id) OR (al.c_invoice_credit_id = d.document_id)) AND (al.isactive = 'Y')) ");
			sqlDoc.append("		WHEN d.documenttable = 'C_Invoice' THEN (sign(d.amount) * ( abs(currencyConvert(d.amount, d.c_currency_id, "
					+ getCurrencyID()
					+ ", ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), d.ad_client_id, d.ad_org_id)) - abs(invoiceOpen(d.document_id, d.c_invoicepayschedule_id, "
					+ getCurrencyID() + " , 0)) ) )");
			sqlDoc.append("     WHEN d.documenttable = 'C_CashLine' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_cashline_id = d.document_id) AND (al.isactive = 'Y')) ");
			sqlDoc.append("     ELSE (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_payment_id = d.document_id) AND (al.isactive = 'Y')) END) ");
			// sqlDoc.append("		          currencyconvert(d.amount, d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), d.ad_client_id, d.ad_org_id) ");
			sqlDoc.append("     + ");
			sqlDoc.append("     abs((SELECT currencyconvert ( CASE WHEN d.documenttable = 'C_Invoice' THEN ");
			sqlDoc.append("     invoiceOpen(d.document_id, coalesce(d.c_invoicepayschedule_id,0)) ");
			sqlDoc.append("     WHEN d.documenttable = 'C_CashLine' THEN ");
			sqlDoc.append("     cashlineavailable(d.document_id) ");
			sqlDoc.append("     ELSE paymentavailable(d.document_id) END, d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ))) ");
			sqlDoc.append("	         ELSE 0.0 END) * SIGN(d.amount)::numeric AS Debit, ");
			sqlDoc.append("     ABS(CASE WHEN d.signo_issotrx = ? THEN ");
			sqlDoc.append("     (SELECT CASE ");
			// sqlDoc.append("     WHEN d.documenttable = 'C_Invoice' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount + (CASE WHEN al.c_invoice_credit_id IS NULL THEN 0.0 ELSE (al.writeoffamt + al.discountamt) END )) END) FROM C_AllocationLine al WHERE ((al.c_invoice_id = d.document_id) OR (al.c_invoice_credit_id = d.document_id)) AND (al.isactive = 'Y')) ");
			sqlDoc.append("		WHEN d.documenttable = 'C_Invoice' THEN (sign(d.amount) * ( abs(currencyConvert(d.amount, d.c_currency_id, "
					+ getCurrencyID()
					+ ", ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), d.ad_client_id, d.ad_org_id)) - abs(invoiceOpen(d.document_id, d.c_invoicepayschedule_id, "
					+ getCurrencyID() + " , 0)) ) )");
			sqlDoc.append("     WHEN d.documenttable = 'C_CashLine' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_cashline_id = d.document_id) AND (al.isactive = 'Y')) ");
			sqlDoc.append("     ELSE (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_payment_id = d.document_id) AND (al.isactive = 'Y')) END) ");
			// sqlDoc.append("		          currencyconvert(d.amount, d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), d.ad_client_id, d.ad_org_id) ");
			sqlDoc.append("     + ");
			sqlDoc.append("     abs(SELECT currencyconvert ( CASE WHEN d.documenttable = 'C_Invoice' THEN ");
			sqlDoc.append("     invoiceOpen(d.document_id, coalesce(d.c_invoicepayschedule_id,0)) ");
			sqlDoc.append("     WHEN d.documenttable = 'C_CashLine' THEN ");
			sqlDoc.append("     cashlineavailable(d.document_id) ");
			sqlDoc.append("     ELSE paymentavailable(d.document_id) END, d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ))) ");
			sqlDoc.append("	         ELSE 0.0 END) * SIGN(d.amount)::numeric AS Credit, ");
			sqlDoc.append("  	d.Created, ");
			sqlDoc.append("  	d.C_Currency_ID, ");
			sqlDoc.append("  	d.amount, ");
			sqlDoc.append("  	d.documenttable, ");
			sqlDoc.append("  	d.document_id, ");
			sqlDoc.append(" 	d.c_invoicepayschedule_id ");
			sqlDoc.append(" FROM V_Documents_Org d ");
			sqlDoc.append(" WHERE d.DocStatus IN ('CO','CL', 'RE', 'VO') ");
			sqlDoc.append("   AND d.AD_Client_ID = ? ");
			sqlDoc.append("   AND d.C_Bpartner_ID = ? ");
			sqlAppend("   AND d.AD_Org_ID = ? ", orgID, sqlDoc);
			sqlAppend("   AND d.C_DocType_ID = ? ", docTypeID, sqlDoc);
		} else {
			sqlDoc.append("     SELECT ");
			sqlDoc.append("     	date_trunc('day',d.Dateacct) as DateTrx, ");
			sqlDoc.append("     	d.C_DocType_ID, ");
			sqlDoc.append("     	COALESCE((SELECT a.documentno FROM C_AllocationHdr a WHERE (a.C_AllocationHdr_ID = (SELECT al.C_AllocationHdr_ID FROM C_AllocationLine al WHERE ( ((d.documenttable = 'C_Payment') AND (al.C_Payment_ID = d.document_id)) OR ((d.documenttable = 'C_Invoice') AND (al.C_Invoice_Credit_ID = d.document_id)) OR ((d.documenttable = 'C_CashLine') AND (al.C_CashLine_ID = d.document_id)) ) LIMIT 1))),DocumentNo) AS DocumentNo, ");
			sqlDoc.append("     	ABS((CASE WHEN d.signo_issotrx = ? THEN ");
			sqlDoc.append(" 		(SELECT CASE ");
			// sqlDoc.append(" 		WHEN d.documenttable = 'C_Invoice' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount + (CASE WHEN al.c_invoice_credit_id IS NULL THEN 0.0 ELSE (al.writeoffamt + al.discountamt) END )) END) FROM C_AllocationLine al WHERE ((al.c_invoice_id = d.document_id) OR (al.c_invoice_credit_id = d.document_id)) AND (al.isactive = 'Y')) ");
			sqlDoc.append("			WHEN d.documenttable = 'C_Invoice' THEN (sign(d.amount) * ( abs(currencyConvert(d.amount, d.c_currency_id, "
					+ getCurrencyID()
					+ ", ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), d.ad_client_id, d.ad_org_id)) - abs(invoiceOpen(d.document_id, d.c_invoicepayschedule_id, "
					+ getCurrencyID() + " , 0)) ) )");
			sqlDoc.append(" 		WHEN d.documenttable = 'C_CashLine' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_cashline_id = d.document_id) AND (al.isactive = 'Y')) ");
			sqlDoc.append(" 		ELSE (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_payment_id = d.document_id) AND (al.isactive = 'Y')) END) ");
			sqlDoc.append(" 		+ ");
			sqlDoc.append(" 		abs((SELECT currencyconvert ( CASE WHEN d.documenttable = 'C_Invoice' THEN ");
			sqlDoc.append(" 		invoiceOpen(d.document_id, coalesce(d.c_invoicepayschedule_id,0)) ");
			sqlDoc.append(" 		WHEN d.documenttable = 'C_CashLine' THEN cashlineavailable(d.document_id) ");
			sqlDoc.append(" 		ELSE paymentavailable(d.document_id) END, d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ))) ELSE 0.0 END)) * SIGN(d.amount)::numeric AS Debit, ");
			sqlDoc.append(" 		ABS((CASE WHEN d.signo_issotrx = ? THEN ");
			sqlDoc.append(" 		(SELECT CASE ");
			// sqlDoc.append(" 		WHEN d.documenttable = 'C_Invoice' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount + (CASE WHEN al.c_invoice_credit_id IS NULL THEN 0.0 ELSE (al.writeoffamt + al.discountamt) END )) END) FROM C_AllocationLine al WHERE ((al.c_invoice_id = d.document_id) OR (al.c_invoice_credit_id = d.document_id)) AND (al.isactive = 'Y')) ");
			sqlDoc.append("			WHEN d.documenttable = 'C_Invoice' THEN (sign(d.amount) * ( abs(currencyConvert(d.amount, d.c_currency_id, "
					+ getCurrencyID()
					+ ", ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), d.ad_client_id, d.ad_org_id)) - abs(invoiceOpen(d.document_id, d.c_invoicepayschedule_id, "
					+ getCurrencyID() + " , 0)) ) )");
			sqlDoc.append(" 		WHEN d.documenttable = 'C_CashLine' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_cashline_id = d.document_id) AND (al.isactive = 'Y')) ");
			sqlDoc.append(" 		ELSE (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_payment_id = d.document_id) AND (al.isactive = 'Y')) END) ");
			sqlDoc.append(" 		+ ");
			sqlDoc.append(" 		abs((SELECT currencyconvert ( CASE WHEN d.documenttable = 'C_Invoice' THEN ");
			sqlDoc.append(" 		invoiceOpen(d.document_id, coalesce(d.c_invoicepayschedule_id,0)) ");
			sqlDoc.append(" 		WHEN d.documenttable = 'C_CashLine' THEN ");
			sqlDoc.append(" 		cashlineavailable(d.document_id) ");
			sqlDoc.append(" 		ELSE paymentavailable(d.document_id) END, d.c_currency_id, ?, ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ))) ELSE 0.0 END)) * SIGN(d.amount)::numeric AS Credit, ");
			sqlDoc.append(" 		date_trunc('day',d.Created) AS Created, ");
			sqlDoc.append(" 		(CASE WHEN ((SELECT al.C_AllocationHdr_ID FROM C_AllocationLine al WHERE ( ((d.documenttable = 'C_Payment') AND (al.C_Payment_ID = d.document_id)) OR ((d.documenttable = 'C_Invoice') AND (al.C_Invoice_Credit_ID = d.document_id)) OR ((d.documenttable = 'C_CashLine') AND (al.C_CashLine_ID = d.document_id)) ) LIMIT 1) IS NOT NULL) THEN '118' ELSE d.C_Currency_ID END) AS C_Currency_ID, ");
			sqlDoc.append(" 		(CASE WHEN ((SELECT al.C_AllocationHdr_ID FROM C_AllocationLine al WHERE ( ((d.documenttable = 'C_Payment') AND (al.C_Payment_ID = d.document_id) AND (al.isActive = 'Y')) OR ((d.documenttable = 'C_Invoice') AND (al.C_Invoice_Credit_ID = d.document_id) AND (al.isActive = 'Y')) OR ((d.documenttable = 'C_CashLine') AND (al.C_CashLine_ID = d.document_id) AND (al.isActive = 'Y')) ) LIMIT 1) IS NOT NULL) THEN (CASE WHEN d.signo_issotrx = '1' THEN (SELECT CASE WHEN d.documenttable = 'C_Invoice' THEN ( sign(d.amount) * ( abs(currencyConvert(d.amount, d.c_currency_id, "
					+ getCurrencyID()
					+ ", ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), d.ad_client_id, d.ad_org_id)) - abs(invoiceOpen(d.document_id, d.c_invoicepayschedule_id, "
					+ getCurrencyID()
					+ " , 0)) )  ) WHEN d.documenttable = 'C_CashLine' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_cashline_id = d.document_id) AND (al.isActive = 'Y')) ELSE (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_payment_id = d.document_id) AND (al.isActive = 'Y')) END) + abs((SELECT currencyconvert ( CASE WHEN d.documenttable = 'C_Invoice' THEN invoiceOpen(d.document_id, coalesce(d.c_invoicepayschedule_id,0)) WHEN d.documenttable = 'C_CashLine' THEN cashlineavailable(d.document_id) ELSE paymentavailable(d.document_id) END, d.c_currency_id, '118', ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ))) ELSE 0.0 END) + (CASE WHEN d.signo_issotrx = '-1' THEN (SELECT CASE WHEN d.documenttable = 'C_Invoice' THEN ( sign(d.amount) * ( abs(currencyConvert(d.amount, d.c_currency_id, "
					+ getCurrencyID()
					+ ", ('now'::text)::timestamp(6) with time zone, COALESCE(c_conversiontype_id,0), d.ad_client_id, d.ad_org_id)) - abs(invoiceOpen(d.document_id, d.c_invoicepayschedule_id, "
					+ getCurrencyID()
					+ " , 0)) )  ) WHEN d.documenttable = 'C_CashLine' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_cashline_id = d.document_id) AND (al.isActive = 'Y')) ELSE (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al WHERE (al.c_payment_id = d.document_id) AND (al.isActive = 'Y')) END) + abs((SELECT currencyconvert ( CASE WHEN d.documenttable = 'C_Invoice' THEN invoiceOpen(d.document_id, coalesce(d.c_invoicepayschedule_id,0)) WHEN d.documenttable = 'C_CashLine' THEN cashlineavailable(d.document_id) ELSE paymentavailable(d.document_id) END, d.c_currency_id, '118', ('now'::text)::timestamp(6) with time zone, COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ))) ELSE 0.0 END) ELSE d.amount END) AS amount, ");
			sqlDoc.append(" 		(CASE WHEN ((SELECT al.C_AllocationHdr_ID FROM C_AllocationLine al WHERE ( ((d.documenttable = 'C_Payment') AND (al.C_Payment_ID = d.document_id)) OR ((d.documenttable = 'C_Invoice') AND (al.C_Invoice_Credit_ID = d.document_id)) OR ((d.documenttable = 'C_CashLine') AND (al.C_CashLine_ID = d.document_id)) ) LIMIT 1) IS NOT NULL) THEN 'C_AllocationHdr' ELSE d.documenttable END) AS documenttable, ");
			sqlDoc.append(" 		(CASE WHEN ((SELECT al.C_AllocationHdr_ID FROM C_AllocationLine al WHERE ( ((d.documenttable = 'C_Payment') AND (al.C_Payment_ID = d.document_id)) OR ((d.documenttable = 'C_Invoice') AND (al.C_Invoice_Credit_ID = d.document_id)) OR ((d.documenttable = 'C_CashLine') AND (al.C_CashLine_ID = d.document_id)) ) LIMIT 1) IS NOT NULL) THEN (SELECT al.C_AllocationHdr_ID FROM C_AllocationLine al WHERE ( ((d.documenttable = 'C_Payment') AND (al.C_Payment_ID = d.document_id)) OR ((d.documenttable = 'C_Invoice') AND (al.C_Invoice_Credit_ID = d.document_id)) OR ((d.documenttable = 'C_CashLine') AND (al.C_CashLine_ID = d.document_id)) ) LIMIT 1) ELSE d.document_id END) AS document_id, ");
			sqlDoc.append(" 	d.c_invoicepayschedule_id ");
			sqlDoc.append(" 	FROM V_Documents_org d ");
			sqlDoc.append(" 	WHERE d.DocStatus IN ('CO','CL', 'RE', 'VO') ");
			sqlDoc.append("     AND d.AD_Client_ID = ? ");
			sqlDoc.append("   AND d.C_Bpartner_ID = ? ");
			sqlAppend("   AND d.AD_Org_ID = ? ", orgID, sqlDoc);
			sqlAppend("   AND d.C_DocType_ID = ? ", docTypeID, sqlDoc);
		}
		return sqlDoc.toString();
	}

	/**
	 * @return Query de cuenta corriente con todos los filtros
	 */
	public String getQuery() {
		String sqlDoc = getAllDocumentsQuery();
		StringBuffer sql = new StringBuffer();

		sql.append(sqlDoc); // Consulta de todos los comprobantes
		sqlAppend("   AND ?::date <= d.Dateacct::date ", getDateFrom(), sql);
		sqlAppend("   AND d.Dateacct::date <= ?::date ", getDateTo(), sql);
		sql.append(" ORDER BY d.Dateacct, d.Created");

		if (!detailReceiptsPayments) {
			StringBuffer sqlGroupBy = new StringBuffer();
			sqlGroupBy
					.append(" SELECT DateTrx, C_DocType_ID, DocumentNo, SUM(Debit) AS Debit, SUM(Credit) AS Credit, Created, C_Currency_ID, SUM(amount) AS Amount, documenttable, document_id, c_invoicepayschedule_id");
			sqlGroupBy.append(" FROM( ");
			sqlGroupBy.append(sql);
			sqlGroupBy.append(" ) AS aux ");
			sqlGroupBy
					.append(" GROUP BY DateTrx, C_DocType_ID, DocumentNo, Created, C_Currency_ID, documenttable, document_id, c_invoicepayschedule_id ");
			sqlGroupBy.append(" ORDER BY DateTrx, Created ");
			sql = sqlGroupBy;
		}
		return sql.toString();
	}

	/**
	 * @return Query con el saldo acumulado a la fecha desde
	 */
	public String getAcumBalanceQuery() {
		String sqlDoc = getAllDocumentsQuery();
		StringBuffer sqlBalance = new StringBuffer();
		sqlBalance
				.append(" SELECT COALESCE(SUM(t.Credit),0.0) AS Credit, COALESCE(SUM(t.Debit),0.0) AS Debit ");
		sqlBalance.append(" FROM ( ");
		sqlBalance.append(sqlDoc).append(" AND d.DateTrx < ? ");
		sqlBalance.append(" ) t");
		return sqlBalance.toString();
	}

	/**
	 * @return Query con el saldo acumulado total con todos los filtros
	 */
	public String getBalanceQuery() {
		String sql = getAllDocumentsQuery();
		StringBuffer sqlBalance = new StringBuffer();
		sqlBalance
				.append("SELECT COALESCE(SUM(t.Debit - t.Credit),0.0) as Balance ");
		sqlBalance.append(" FROM ( ").append(sql);
		sqlBalance.append(" ) as t ");
		return sqlBalance.toString();
	}

	private void sqlAppend(String clause, Object value, StringBuffer sql) {
		if (value != null)
			sql.append(clause);
	}

	public Integer getOrgID() {
		return orgID;
	}

	public void setOrgID(Integer orgID) {
		this.orgID = orgID;
	}

	public Integer getDocTypeID() {
		return docTypeID;
	}

	public void setDocTypeID(Integer docTypeID) {
		this.docTypeID = docTypeID;
	}

	public Boolean getDetailReceiptsPayments() {
		return detailReceiptsPayments;
	}

	public void setDetailReceiptsPayments(Boolean detailReceiptsPayments) {
		this.detailReceiptsPayments = detailReceiptsPayments;
	}

	public Timestamp getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Timestamp dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Timestamp getDateTo() {
		return dateTo;
	}

	public void setDateTo(Timestamp dateTo) {
		this.dateTo = dateTo;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Integer getCurrencyID() {
		return currencyID;
	}

	public void setCurrencyID(Integer currencyID) {
		this.currencyID = currencyID;
	}

}
