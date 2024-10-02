package org.openXpertya.cc;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

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
		sqlDoc.append(" c_currency_id, ");
		sqlDoc.append(" amount, ");
		sqlDoc.append(getCurrencyID() != null ? " currencyconvert(debit, c_currency_id, " + getCurrencyID() + ", '" + Env.getDateFormatted(dateTo) + "', NULL, " + Env.getAD_Client_ID(getCtx()) + ", " + getOrgID() + ") AS debit, " : " debit, ");
		sqlDoc.append(getCurrencyID() != null ? " currencyconvert(credit, c_currency_id, " + getCurrencyID() + ", '" + Env.getDateFormatted(dateTo) + "', NULL, " + Env.getAD_Client_ID(getCtx()) + ", " + getOrgID() + ") AS credit, " : " credit, ");
		sqlDoc.append(" tipo_doc, ");
		sqlDoc.append(" documentno, ");
		sqlDoc.append(" datetrx, ");
		sqlDoc.append(" dateacct, ");
		sqlDoc.append(" c_doctype_id, ");
		sqlDoc.append(" documenttable, ");
		sqlDoc.append(" document_id, ");
		sqlDoc.append(getCurrencyID() != null ? " currencyconvert(openamt, c_currency_id, " + getCurrencyID() + ", '" + Env.getDateFormatted(dateTo) + "', NULL, " + Env.getAD_Client_ID(getCtx()) + ", " + getOrgID() + ") AS openamt, " : " openamt, ");
		sqlDoc.append(" created, ");
		sqlDoc.append(" c_bpartner_id, ");
		sqlDoc.append(" ad_org_id, ");
		sqlDoc.append(" ad_client_id, ");
		sqlDoc.append(" issotrx, ");
		sqlDoc.append(" c_invoicepayschedule_id, ");
		sqlDoc.append(" duedate ");
		sqlDoc.append(" FROM c_alldocumentscc_v d ");
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
					.append(" SELECT DateTrx, DateAcct, C_DocType_ID, DocumentNo, SUM(Debit) AS Debit, SUM(Credit) AS Credit, Created, C_Currency_ID, SUM(amount) AS Amount, documenttable, document_id, c_invoicepayschedule_id, sum(openamt) as openamt ");
			sqlGroupBy.append(" FROM( ");
			sqlGroupBy.append(sql);
			sqlGroupBy.append(" ) AS aux ");
			sqlGroupBy
					.append(" GROUP BY DateTrx, DateAcct, C_DocType_ID, DocumentNo, Created, C_Currency_ID, documenttable, document_id, c_invoicepayschedule_id ");
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
	
	public String getDateToInlineQuery(){
		return " ('"+ ((getDateTo() != null) ? getDateTo() + "'" : "now'::text") + ")::timestamp(6) without time zone ";
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
