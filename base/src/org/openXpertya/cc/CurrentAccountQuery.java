package org.openXpertya.cc;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

import org.openXpertya.model.MAllocationHdr;
import org.openXpertya.model.MRole;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/**
 * Clase generadora de consulta de cuenta corriente. Se centraliza para tener
 * todo en una clase y cualquier cambio se aplica aquí dentro. Por lo pronto
 * esta clase y sus querys se utilizan en las clases {@link #org.openXpertya.process.CurrentAccountReport} y {@link #org.openXpertya.cc.OnCreditCurrentAccountBalanceData}
 * .<br>
 * Las consultas obtienen el detalle de todos los documentos de la EC, aplicando
 * ademas los filtros de Tipo de Documento y Organización asignados como
 * parámetro del reporte. Realiza la conversión de montos a la moneda del
 * contexto respetando la fecha y tasa persistida de cada documento. NO aplica
 * el filtro de fechas para que sea reutilizable la consulta.
 * <ul>
 * <li>El saldo de las Invoices se calcula haciendo: La sumatoria de lo
 * facturado (amount + writeoffamt + discountamt de C_AllocationLine) + La
 * sumatoria de lo pendiente por las Facturas (invoiceOpen) convertido a la
 * tasa de la factura.</li>
 * <li>El saldo de los Payments se calcula haciendo: La sumatoria de lo cobrado
 * (amount de C_AllocationLine) + La sumatoria de lo pendiente por las pagos
 * (paymentavailable) convertido a la fecha del pago.</li>
 * <li>El saldo de las CashLine se calcula haciendo: La sumatoria de lo cobrado
 * (amount de C_AllocationLine) + La sumatoria de lo pendiente por las pagos
 * (cashlineavailable) convertido a la fecha de la línea de caja.</li>
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
	
	/** Entidad Comercial */
	private Integer bPartnerID;
	
	/** Condición de Comprobantes: Efectivo, Cta Cte, Todos */
	private String condition;
	
	/** Tipo de Cuenta: C = Cliente, V = Proveedor, B = Ambos */
	private String accountType;
	
	/** Agregar Validaciones de Seguridad de Organizaciones */
	private boolean addSecurityValidation = false;
	
	public CurrentAccountQuery(Properties ctx, Integer orgID,
			Integer docTypeID, Boolean detailReceiptsPayments,
			Timestamp dateFrom, Timestamp dateTo, String condition, 
			Integer bPartnerID, String accountType) {
		setCtx(ctx);
		setOrgID(orgID);
		setDocTypeID(docTypeID);
		setDetailReceiptsPayments(detailReceiptsPayments);
		setDateFrom(dateFrom);
		setDateTo(dateTo != null ? dateTo : new Timestamp(System.currentTimeMillis()));
		setCurrencyID(Env.getContextAsInt(getCtx(), "$C_Currency_ID"));
		setCondition(condition);
		setbPartnerID(bPartnerID);
		setAccountType(Util.isEmpty(accountType, true)?"B":accountType);
	}
	
	public CurrentAccountQuery(Properties ctx, Integer orgID,
			Integer docTypeID, Boolean detailReceiptsPayments,
			Timestamp dateFrom, Timestamp dateTo, String condition, 
			Integer bPartnerID, String accountType, 
			boolean addSecurityValidation) {
		this(ctx, orgID, docTypeID, detailReceiptsPayments, dateFrom, dateTo, condition, bPartnerID, accountType);
		setAddSecurityValidation(addSecurityValidation);
	}

	/**
	 * @return Query de cuenta corriente de todos los documentos, sin filtros de
	 *         fechas
	 */
	public String getAllDocumentsQuery() {
		return getAllDocumentsQuery(null);
	}
	
	/**
	 * @param whereClause cláusula where adicional
	 * @return Query de cuenta corriente de todos los documentos, sin filtros de
	 *         fechas
	 */
	public String getAllDocumentsQuery(String whereClause) {
		whereClause = Util.isEmpty(whereClause, true) ? "" : whereClause;
		whereClause = "WHERE (1 = 1) " + whereClause;
		StringBuffer sqlSummarySubQueryEnd = new StringBuffer();
		StringBuffer sqlDoc = new StringBuffer();
		sqlDoc.append(" select * ");
		sqlDoc.append(" from (select ");
		sqlDoc.append(" d.c_currency_id, ");
		sqlDoc.append(" d.amount, ");
		sqlDoc.append(getConvertedAmountExpression(getNativeDebitExpression()) + " AS debit, ");
		sqlDoc.append(getConvertedAmountExpression(getNativeCreditExpression()) + " AS credit, ");
		sqlDoc.append(" d.tipo_doc, ");
		sqlDoc.append(" d.documentno, ");
		sqlDoc.append(" d.datetrx, ");
		sqlDoc.append(" d.dateacct, ");
		sqlDoc.append(" d.c_doctype_id, ");
		sqlDoc.append(" d.documenttable, ");
		sqlDoc.append(" d.document_id, ");
		sqlDoc.append(getConvertedAmountExpression(getNativeOpenAmountExpression()) + " AS openamt, ");
		sqlDoc.append(" d.created, ");
		sqlDoc.append(" d.c_bpartner_id, ");
		sqlDoc.append(" d.ad_org_id, ");
		sqlDoc.append(" d.ad_client_id, ");
		sqlDoc.append(" d.issotrx, ");
		sqlDoc.append(" d.c_invoicepayschedule_id, ");
		sqlDoc.append(" d.duedate ");
		sqlDoc.append(" FROM ( ");
		sqlDoc.append("SELECT * FROM c_alldocumentscc_v ");
		sqlDoc.append("UNION ALL ");
		sqlDoc.append(getSalesTransactionDocumentsQuery());
		sqlDoc.append(" ) d ");
		sqlDoc.append(" LEFT JOIN c_invoice source_invoice ON source_invoice.c_invoice_id = d.document_id ");
		sqlDoc.append(" AND d.documenttable = 'C_Invoice' ");
		sqlDoc.append(" LEFT JOIN c_payment source_payment ON source_payment.c_payment_id = d.document_id ");
		sqlDoc.append(" AND d.documenttable = 'C_Payment' ");
		sqlDoc.append(" LEFT JOIN c_doctype source_payment_doctype ON source_payment_doctype.c_doctype_id = source_payment.c_doctype_id ");
		sqlDoc.append(" LEFT JOIN c_cashline source_cashline ON source_cashline.c_cashline_id = d.document_id ");
		sqlDoc.append(" AND d.documenttable = 'C_CashLine' ");
		sqlDoc.append(getDocumentAvailableJoin());
		sqlAppend(" WHERE d.AD_Client_ID = ? ", Env.getAD_Client_ID(getCtx()), sqlDoc);
		if (getbPartnerID() != null)
			sqlAppend("   AND d.C_Bpartner_ID = ? ", getbPartnerID(), sqlDoc);
		if (getOrgID() != null && getOrgID() != 0)
			sqlAppend("   AND d.AD_Org_ID = ? ", getOrgID(), sqlDoc);
		if (getDocTypeID() != null)
			sqlAppend("   AND d.C_DocType_ID = ? ", getDocTypeID(), sqlDoc);
		/*if (getCurrencyID() != null)
			sqlAppend("   AND d.C_Currency_ID = ? ", getCurrencyID(), sqlDoc);*/
		if (!getAccountType().equals("B"))
			sqlAppend("   AND d.issotrx = ? ", getAccountType().equals("C") ? "'Y'" : "'N'", sqlDoc);    /** Tipo de Cuenta: C = Cliente, V = Proveedor, B = Ambos */
		sqlDoc.append(getSecurityValidation());
//		sqlDoc.append(" ) as t ");
//		
		sqlDoc.append(sqlSummarySubQueryEnd);
		sqlDoc.append(" ) as d ");
		sqlDoc.append(whereClause);
		return sqlDoc.toString();
	}

	/**
	 * Los importes de pagos y líneas de caja de c_alldocumentscc_v pueden venir
	 * expresados en la moneda de la factura aunque la fila declare la moneda del
	 * medio de pago. Se reconstruye el imputado en la moneda nativa del documento.
	 */
	protected String getNativeDebitExpression() {
		return getNativeAmountExpression("debit", getFullDebitCondition());
	}

	protected String getNativeCreditExpression() {
		return getNativeAmountExpression("credit", getFullCreditCondition());
	}

	protected String getNativeAmountExpression(String columnName, String fullAmountCondition) {
		String allocatedAmt = getNativeAllocatedAmountExpression();
		return "CASE WHEN " + getBalanceDocumentCondition() + " "
				+ "THEN CASE WHEN " + fullAmountCondition + " THEN ABS(d.amount)"
				+ " ELSE " + allocatedAmt + " END ELSE d." + columnName + " END";
	}

	protected String getFullDebitCondition() {
		return "((d.documenttable = 'C_Payment' AND "
				+ "((d.issotrx = 'Y' AND source_payment_doctype.doctypekey = 'CRR') "
				+ "OR (d.issotrx = 'N' AND source_payment_doctype.doctypekey <> 'VPR'))) "
				+ "OR (d.documenttable = 'C_CashLine' AND d.issotrx = 'N'))";
	}

	protected String getFullCreditCondition() {
		return "((d.documenttable = 'C_Payment' AND "
				+ "((d.issotrx = 'Y' AND source_payment_doctype.doctypekey <> 'CRR') "
				+ "OR (d.issotrx = 'N' AND source_payment_doctype.doctypekey = 'VPR'))) "
				+ "OR (d.documenttable = 'C_CashLine' AND d.issotrx = 'Y'))";
	}

	protected String getNativeOpenAmountExpression() {
		return "CASE "
				+ "WHEN " + getBalanceDocumentCondition() + " THEN ABS(document_available.openamt) "
				+ "ELSE d.openamt END";
	}

	protected String getNativeAllocatedAmountExpression() {
		return "CASE WHEN " + getBalanceDocumentCondition() + " "
				+ "THEN ABS(d.amount) - ABS(document_available.openamt) ELSE 0::numeric END";
	}

	protected String getDocumentAvailableJoin() {
		return " LEFT JOIN LATERAL (SELECT CASE "
				+ "WHEN d.documenttable = 'C_Payment' AND COALESCE(source_payment.c_charge_id, 0) = 0 THEN COALESCE(paymentavailable(d.document_id, " + getDateToInlineQuery() + "), 0::numeric) "
				+ "WHEN d.documenttable = 'C_CashLine' AND COALESCE(source_cashline.c_charge_id, 0) = 0 THEN COALESCE(cashlineavailable(d.document_id, " + getDateToInlineQuery() + "), 0::numeric) "
				+ "ELSE 0::numeric END AS openamt) document_available "
				+ "ON " + getBalanceDocumentCondition() + " ";
	}

	protected String getBalanceDocumentCondition() {
		return "((d.documenttable = 'C_Payment' AND COALESCE(source_payment.c_charge_id, 0) = 0) "
				+ "OR (d.documenttable = 'C_CashLine' AND COALESCE(source_cashline.c_charge_id, 0) = 0))";
	}

	/**
	 * Para facturas se prioriza la tasa guardada en el comprobante al convertir a
	 * moneda contable. Para el resto se utiliza la fecha propia del documento.
	 */
	protected String getConvertedAmountExpression(String amountExpression) {
		if (getCurrencyID() == null) {
			return amountExpression;
		}

		int accountingCurrencyID = Env.getC_Currency_ID(getCtx());
		String standardConversion = "currencyconvert(" + amountExpression + ", d.c_currency_id, "
				+ getCurrencyID() + ", CASE WHEN d.documenttable = 'C_Invoice' THEN source_invoice.dateinvoiced ELSE d.dateacct END, "
				+ "CASE WHEN d.documenttable = 'C_Invoice' THEN COALESCE(source_invoice.c_conversiontype_id, 0) ELSE NULL END, "
				+ "d.ad_client_id, d.ad_org_id)";

		if (getCurrencyID().intValue() != accountingCurrencyID) {
			return standardConversion;
		}

		return "CASE WHEN d.documenttable = 'C_Invoice' "
				+ "AND d.c_currency_id <> " + getCurrencyID() + " "
				+ "AND COALESCE(source_invoice.cintolo_exchange_rate, 0) > 0 "
				+ "THEN currencyround((" + amountExpression + ") * source_invoice.cintolo_exchange_rate, "
				+ getCurrencyID() + ", NULL) ELSE " + standardConversion + " END";
	}

	/**
	 * Las asignaciones de TPV (STX) no forman parte de c_alldocumentscc_v en
	 * algunas bases históricas. Eso deja las facturas cobradas con pendiente en
	 * cero, pero sin la contrapartida de cobranza en el saldo. Esta subquery las
	 * recompone para todos los consumidores de CurrentAccountQuery.
	 */
	protected String getSalesTransactionDocumentsQuery() {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ");
		sql.append(" ah.c_currency_id, ");
		sql.append(" stx.allocatedamt AS amount, ");
		sql.append(" 0::numeric AS debit, ");
		sql.append(" stx.allocatedamt AS credit, ");
		sql.append(" COALESCE(dt.name, 'Cobro TPV') AS tipo_doc, ");
		sql.append(" ah.documentno, ");
		sql.append(" ah.datetrx, ");
		sql.append(" ah.dateacct, ");
		sql.append(" ah.c_doctype_id, ");
		sql.append(" 'C_AllocationHdr'::text AS documenttable, ");
		sql.append(" ah.c_allocationhdr_id AS document_id, ");
		sql.append(" 0::numeric AS openamt, ");
		sql.append(" ah.created, ");
		sql.append(" ah.c_bpartner_id, ");
		sql.append(" ah.ad_org_id, ");
		sql.append(" ah.ad_client_id, ");
		sql.append(" 'Y'::bpchar AS issotrx, ");
		sql.append(" NULL::integer AS c_invoicepayschedule_id, ");
		sql.append(" NULL::timestamp without time zone AS duedate ");
		sql.append(" FROM c_allocationhdr ah ");
		sql.append(" LEFT JOIN c_doctype dt ON ah.c_doctype_id = dt.c_doctype_id ");
		sql.append(" JOIN ( ");
		sql.append(" 	SELECT al2.c_allocationhdr_id, ");
		sql.append(" 		   SUM(al2.amount + al2.discountamt + al2.writeoffamt) AS allocatedamt ");
		sql.append(" 	  FROM c_allocationline al2 ");
		sql.append(" 	 WHERE al2.c_invoice_id IS NOT NULL ");
		sql.append(" 	   AND al2.c_invoice_credit_id IS NULL ");
		sql.append(" 	   AND (al2.c_payment_id IS NOT NULL OR al2.c_cashline_id IS NOT NULL) ");
		sql.append(" 	 GROUP BY al2.c_allocationhdr_id ");
		sql.append(" ) stx ON stx.c_allocationhdr_id = ah.c_allocationhdr_id ");
		sql.append(" WHERE ah.allocationtype = '");
		sql.append(MAllocationHdr.ALLOCATIONTYPE_SalesTransaction);
		sql.append("' ");
		sql.append("   AND stx.allocatedamt > 0::numeric ");
		sql.append("   AND ah.processed = 'Y'::bpchar ");
		sql.append("   AND ah.docstatus IN ('CO', 'CL') ");
		sql.append("   AND NOT EXISTS ( ");
		sql.append(" 		SELECT 1 ");
		sql.append(" 		  FROM c_alldocumentscc_v existing ");
		sql.append(" 		 WHERE existing.documenttable = 'C_AllocationHdr' ");
		sql.append(" 		   AND existing.document_id = ah.c_allocationhdr_id ");
		sql.append("   ) ");
		return sql.toString();
	}

	/**
	 * @return Query de cuenta corriente con todos los filtros
	 */
	public String getQuery() {
		String whereClause = getSqlAppend("   AND ?::date <= d.DateAcct::date ", getDateFrom()) 
							+ getSqlAppend("   AND d.DateAcct::date <= ?::date ", getDateTo());
		String sqlDoc = getAllDocumentsQuery(whereClause);
		StringBuffer sql = new StringBuffer();

		sql.append(sqlDoc); // Consulta de todos los comprobantes
		sql.append(" ORDER BY d.DateAcct::date, d.Created");

		if (!detailReceiptsPayments) {
			StringBuffer sqlGroupBy = new StringBuffer();
			sqlGroupBy
					.append(" SELECT DateTrx, DateAcct, C_DocType_ID, DocumentNo, SUM(Debit) AS Debit, SUM(Credit) AS Credit, Created, C_Currency_ID, SUM(amount) AS Amount, documenttable, document_id, c_invoicepayschedule_id, sum(openamt) as openamt, tipo_doc ");
			sqlGroupBy.append(" FROM( ");
			sqlGroupBy.append(sql);
			sqlGroupBy.append(" ) AS aux ");
			sqlGroupBy
					.append(" GROUP BY DateTrx, DateAcct, C_DocType_ID, DocumentNo, Created, C_Currency_ID, documenttable, document_id, c_invoicepayschedule_id, tipo_doc ");
			sqlGroupBy.append(" ORDER BY DateAcct, Created ");
			sql = sqlGroupBy;
		}
		return sql.toString();
	}

	/**
	 * @return Query con el saldo acumulado a la fecha desde
	 */
	public String getAcumBalanceQuery() {
		Timestamp prevDateTo = getDateTo();
		Calendar dateToOpenAmt = Calendar.getInstance();
		dateToOpenAmt.setTimeInMillis(getDateFrom().getTime());
		dateToOpenAmt.add(Calendar.DATE, -1);
		setDateTo(new Timestamp(dateToOpenAmt.getTimeInMillis()));
		String sqlDoc = getAllDocumentsQuery(" AND d.DateAcct::date < ?::date ");
		StringBuffer sqlBalance = new StringBuffer();
		sqlBalance
				.append(" SELECT COALESCE(SUM(t.Credit),0.0) AS Credit, COALESCE(SUM(t.Debit),0.0) AS Debit, COALESCE(SUM(t.openamt),0.0) AS openamt ");
		sqlBalance.append(" FROM ( ");
		sqlBalance.append(sqlDoc);
		sqlBalance.append(" ) t");
		setDateTo(prevDateTo);
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
	
	/**
	 * @return cláusula where para el estado de los documentos
	 */
	protected String getDocStatusWhereClause(){
		// Para documentos de compras, no se deben mostrar los anulados 
		String whereClause = " (CASE WHEN d.documenttable = 'C_Invoice' AND d.issotrx = 'N' "
							+ " THEN d.DocStatus IN ('CO', 'CL', 'RE', 'WC') "
							+ " ELSE d.DocStatus IN ('CO','CL', 'RE', 'VO', 'WC') END) ";
		return whereClause;
	}

	private void sqlAppend(String clause, Object value, StringBuffer sql) {
		if (value != null) {
			sql.append(clause.replace("?", value.toString()));
		} else {
			sql.append(clause);
		}
	}
	
	private String getSqlAppend(String clause, Object value) {
		String append = "";
		if (value != null)
			append = clause;
		return append;
	}
	
	protected String getSecurityValidation() {
		String secVal = "";
		if(isAddSecurityValidation()) {
			// dREHER 5.0
			String orgAccess = MRole.get(getCtx(), Env.getAD_Role_ID(getCtx())).getOrgWhere(MRole.SQL_RO);
			if(!Util.isEmpty(orgAccess, true))
			secVal = " AND d."+ MRole.get(getCtx(), Env.getAD_Role_ID(getCtx())).getOrgWhere(MRole.SQL_RO);
		}
		return secVal;
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

	public Integer getbPartnerID() {
		return bPartnerID;
	}

	public void setbPartnerID(Integer bPartnerID) {
		this.bPartnerID = bPartnerID;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public String getDateToInlineQuery() {
		return getDateTo() != null
				? "'" + Env.getDateFormatted(getDateTo()) + "'::timestamp without time zone"
				: "now()::timestamp without time zone";
	}
	
	protected String getInvoiceOrgIDAllocatedQueryCondition(){
		return Util.isEmpty(getOrgID(), true)?"":" AND (i.c_invoice_id IS NULL OR i.ad_org_id = "+getOrgID()+" ) ";
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	protected boolean isAddSecurityValidation() {
		return addSecurityValidation;
	}

	protected void setAddSecurityValidation(boolean addSecurityValidation) {
		this.addSecurityValidation = addSecurityValidation;
	}
}
