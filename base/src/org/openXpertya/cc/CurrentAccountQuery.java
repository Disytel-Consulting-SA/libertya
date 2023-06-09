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
		setDateTo(dateTo);
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
		sqlDoc.append(" from ( ");
		if(!detailReceiptsPayments){
			sqlDoc.append(" select  datetrx, "
							+ "createdghost, "
							+ "c_doctype_id, "
							+ "documentno, "
							+ "duedate, "
							+ "created, "
							+ "c_currency_id, "
							+ "documenttable, "
							+ "document_id, "
							+ "c_invoicepayschedule_id, "
							+ "amount, "
							+ "sum(openamt) as openamt, "
							+ "sum(debit) as debit, "
							+ "sum(credit) as credit ");
			sqlDoc.append(" from ( ");
			sqlDoc.append(" select (CASE WHEN ah.c_allocationhdr_id IS NULL THEN rv.datetrx ELSE ah.dateacct::date END) as datetrx, ");
			sqlDoc.append(" createdghost, ");
			sqlDoc.append(" (CASE WHEN ah.c_allocationhdr_id IS NULL THEN rv.C_DocType_ID ELSE coalesce(dt.c_doctype_id, dd.c_doctype_id) END) as c_doctype_id, ");
			sqlDoc.append(" (CASE WHEN ah.c_allocationhdr_id IS NULL THEN rv.documentno ELSE ah.documentno END) as documentno, ");
			sqlDoc.append(" (CASE WHEN ah.c_allocationhdr_id IS NULL THEN rv.duedate::date ELSE null::date END) as duedate, ");
			sqlDoc.append(" (CASE WHEN ah.c_allocationhdr_id IS NULL THEN rv.created ELSE ah.created END)::date as created, ");
			sqlDoc.append(" (CASE WHEN ah.c_allocationhdr_id IS NULL THEN rv.c_currency_id ELSE ah.c_currency_id END) as c_currency_id, ");
			sqlDoc.append(" (CASE WHEN ah.c_allocationhdr_id IS NULL THEN rv.documenttable ELSE 'C_AllocationHdr' END) as documenttable, ");
			sqlDoc.append(" (CASE WHEN ah.c_allocationhdr_id IS NULL THEN rv.document_id ELSE ah.c_allocationhdr_id END) as document_id, ");
			sqlDoc.append(" (CASE WHEN ah.c_allocationhdr_id IS NULL THEN rv.c_invoicepayschedule_id ELSE null::integer END) as c_invoicepayschedule_id, ");
			sqlDoc.append(" debit, ");
			sqlDoc.append(" credit, ");		
			sqlDoc.append(" rv.amount, ");
			sqlDoc.append(" openamt ");
			sqlDoc.append(" from ( ");
			
			// Final de la query
			sqlSummarySubQueryEnd.append(" ) rv ");
			sqlSummarySubQueryEnd.append(" LEFT JOIN c_allocationhdr ah on ah.c_allocationhdr_id = rv.c_allocationhdr_id ");
			sqlSummarySubQueryEnd.append(" LEFT JOIN C_Doctype dt ON dt.c_doctype_id = ah.c_doctype_id ");
			sqlSummarySubQueryEnd.append(" LEFT JOIN (select ad_client_id, c_doctype_id, name, printname, signo_issotrx, issotrx, doctypekey FROM c_doctype WHERE doctypekey in ('POSEC01','CRSEC01','POS','PAL')) dd "
											+ " on (dd.ad_client_id = ah.ad_client_id and (case when ah.allocationtype = 'OP' then dd.doctypekey = 'POSEC01' "
											+ "													when ah.allocationtype = 'RC' then dd.doctypekey = 'CRSEC01' "
											+ "													WHEN ah.allocationtype = 'STX' THEN dd.doctypekey = 'POS' "
											+ "													ELSE dd.doctypekey = 'PAL' end) ) ");
			sqlSummarySubQueryEnd.append(" ) as ad ");
			sqlSummarySubQueryEnd.append(" GROUP BY datetrx, createdghost, c_doctype_id, documentno, duedate, created, c_currency_id, documenttable, document_id, c_invoicepayschedule_id, amount ");
		}
		sqlDoc.append(" SELECT distinct ");
		sqlDoc.append(" 	DateTrx, ");
		sqlDoc.append(" 	createdghost, ");
		sqlDoc.append(" 	C_DocType_ID, ");
		sqlDoc.append(" 	C_BPartner_ID, ");
		sqlDoc.append(" 	DocumentNo, ");
		sqlDoc.append(" 	duedate, ");
		sqlDoc.append("     ABS(CASE WHEN signo_issotrx = ? THEN allocatedamt + openamt ELSE 0.0 END) * SIGN(amount)::numeric AS Debit, ");
		sqlDoc.append("     ABS(CASE WHEN signo_issotrx = ? THEN allocatedamt + openamt ELSE 0.0 END) * SIGN(amount)::numeric AS Credit, ");
		sqlDoc.append("  	Created, ");
		sqlDoc.append("  	C_Currency_ID, ");
		sqlDoc.append("  	amount, ");
		sqlDoc.append("  	documenttable, ");
		sqlDoc.append("  	document_id, ");
		sqlDoc.append(" 	c_invoicepayschedule_id, ");
		sqlDoc.append(" 	c_allocationhdr_id, ");
		sqlDoc.append("  	openamt ");
		
		sqlDoc.append(" from ( ");
		sqlDoc.append(" SELECT distinct ");
		sqlDoc.append(" 	d.Dateacct::date as DateTrx, ");
		sqlDoc.append(" 	d.Created as createdghost, ");
		sqlDoc.append(" 	d.C_DocType_ID, ");
		sqlDoc.append(" 	d.C_BPartner_ID, ");
		sqlDoc.append(" 	d.DocumentNo, ");
		sqlDoc.append(" 	d.duedate, ");

		// Importe imputado
		sqlDoc.append("     abs((SELECT CASE ");
		sqlDoc.append("		WHEN d.documenttable = 'C_Invoice' THEN getallocatedamt(d.document_id, " + getCurrencyID() + ", COALESCE(c_conversiontype_id,0), 1, "+ getDateToInlineQuery() +", coalesce(d.c_invoicepayschedule_id,0)) ");
		sqlDoc.append("     WHEN d.documenttable = 'C_CashLine' THEN (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al INNER JOIN C_AllocationHdr ah ON ah.c_allocationhdr_id = al.c_allocationhdr_id LEFT JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id WHERE (al.c_cashline_id = d.document_id) AND (al.isactive = 'Y') AND (ah.dateacct::date <= " + getDateToInlineQuery() + ") "+getInvoiceOrgIDAllocatedQueryCondition()+" ) ");
		sqlDoc.append("     ELSE (select (CASE WHEN SUM(al.amount) IS NULL THEN 0.0 ELSE SUM(al.amount) END) FROM C_AllocationLine al INNER JOIN C_AllocationHdr ah ON ah.c_allocationhdr_id = al.c_allocationhdr_id LEFT JOIN c_invoice i ON i.c_invoice_id = al.c_invoice_id WHERE (al.c_payment_id = d.document_id) AND (al.isactive = 'Y') AND (ah.dateacct::date <= " + getDateToInlineQuery() + ") "+getInvoiceOrgIDAllocatedQueryCondition()+" ) END)) as allocatedamt, ");
		
		// Importe pendiente
		sqlDoc.append("     abs((SELECT currencyconvert ( CASE WHEN d.documenttable = 'C_Invoice' THEN ");
		sqlDoc.append("     invoiceOpen(d.document_id, coalesce(d.c_invoicepayschedule_id,0), "+ getDateToInlineQuery() +") ");
		sqlDoc.append("     WHEN d.documenttable = 'C_CashLine' THEN ");
		sqlDoc.append("     cashlineavailable(d.document_id, "+ getDateToInlineQuery() +") ");
		sqlDoc.append("     ELSE paymentavailable(d.document_id, "+ getDateToInlineQuery() +") END, d.c_currency_id, ?, "+ getDateToInlineQuery() +", COALESCE(d.c_conversiontype_id,0), d.ad_client_id, d.ad_org_id ))) as openamt, ");

		sqlDoc.append("  	d.Created, ");
		sqlDoc.append("  	d.C_Currency_ID, ");
		sqlDoc.append("  	d.amount, ");
		sqlDoc.append("  	d.documenttable, ");
		sqlDoc.append("  	d.document_id, ");
		sqlDoc.append(" 	d.c_invoicepayschedule_id, ");
		sqlDoc.append(" 	d.c_allocationhdr_id, ");
		sqlDoc.append(" 	d.signo_issotrx ");
		sqlDoc.append(" FROM V_Documents_Org_Filtered (" + (bPartnerID != null ? bPartnerID : -1) + ", " + !detailReceiptsPayments + ", '"+getCondition()+"', " + getDateToInlineQuery() + ", "+(orgID == null?0:orgID)+", '"+getAccountType()+"', true, true)  d ");
		sqlDoc.append(" WHERE d.AD_Client_ID = ? ");
		sqlDoc.append(" AND "+getDocStatusWhereClause());
		sqlAppend("   AND d.C_Bpartner_ID = ? ", bPartnerID, sqlDoc);
		sqlAppend("   AND d.AD_Org_ID = ? ", orgID, sqlDoc);
		sqlAppend("   AND d.C_DocType_ID = ? ", docTypeID, sqlDoc);
		sqlDoc.append(getSecurityValidation());
		sqlDoc.append(" ) as t ");
		
		sqlDoc.append(sqlSummarySubQueryEnd);
		sqlDoc.append(" ) as d ");
		sqlDoc.append(whereClause);
		return sqlDoc.toString();
	}

	/**
	 * @return Query de cuenta corriente con todos los filtros
	 */
	public String getQuery() {
		String whereClause = getSqlAppend("   AND ?::date <= d.DateTrx::date ", getDateFrom()) 
							+ getSqlAppend("   AND d.DateTrx::date <= ?::date ", getDateTo());
		String sqlDoc = getAllDocumentsQuery(whereClause);
		StringBuffer sql = new StringBuffer();

		sql.append(sqlDoc); // Consulta de todos los comprobantes
		sql.append(" ORDER BY d.DateTrx::date, d.Created");

		if (!detailReceiptsPayments) {
			StringBuffer sqlGroupBy = new StringBuffer();
			sqlGroupBy
					.append(" SELECT DateTrx, C_DocType_ID, DocumentNo, SUM(Debit) AS Debit, SUM(Credit) AS Credit, Created, C_Currency_ID, SUM(amount) AS Amount, documenttable, document_id, c_invoicepayschedule_id, sum(openamt) as openamt ");
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
		Timestamp prevDateTo = getDateTo();
		Calendar dateToOpenAmt = Calendar.getInstance();
		dateToOpenAmt.setTimeInMillis(getDateFrom().getTime());
		dateToOpenAmt.add(Calendar.DATE, -1);
		setDateTo(new Timestamp(dateToOpenAmt.getTimeInMillis()));
		String sqlDoc = getAllDocumentsQuery(" AND d.DateTrx::date < ?::date ");
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
		if (value != null)
			sql.append(clause);
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
			secVal = " AND "+MRole.get(getCtx(), Env.getAD_Role_ID(getCtx())).getOrgWhere(MRole.SQL_RO);
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
