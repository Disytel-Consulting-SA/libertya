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
 * contexto usando fecha/tasa del documento o fecha de corte según
 * {@link #isDocumentConvertRate()}. NO aplica el filtro de fechas para que sea
 * reutilizable la consulta.
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

	public static final String PARAM_IS_DOCUMENT_CONVERT_RATE = "IsDocumentConvertRate";
	public static final String PARAM_CONVERSION_RATE_DATE = "ConversionRateDate";
	public static final String CONVERSION_RATE_DATE_CUTOFF = "C";
	public static final String CONVERSION_RATE_DATE_DOCUMENT = "D";

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

	/** Usar fecha/tasa propia del documento para convertir importes */
	private boolean documentConvertRate = true;
	
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
		sqlDoc.append(getDebitExpression() + " AS debit, ");
		sqlDoc.append(getCreditExpression() + " AS credit, ");
		sqlDoc.append(" d.tipo_doc, ");
		sqlDoc.append(" d.documentno, ");
		sqlDoc.append(" d.datetrx, ");
		sqlDoc.append(" d.dateacct, ");
		sqlDoc.append(" d.c_doctype_id, ");
		sqlDoc.append(" d.documenttable, ");
		sqlDoc.append(" d.document_id, ");
		sqlDoc.append(getOpenAmountExpression() + " AS openamt, ");
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
		sqlDoc.append(" LEFT JOIN c_cash source_cash ON source_cash.c_cash_id = source_cashline.c_cash_id ");
		sqlDoc.append(" LEFT JOIN c_allocationhdr source_allocationhdr ON source_allocationhdr.c_allocationhdr_id = d.document_id ");
		sqlDoc.append(" AND d.documenttable = 'C_AllocationHdr' ");
		sqlDoc.append(getDocumentAvailableJoin());
		sqlDoc.append(getDocumentAllocatedJoin());
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

	protected String getOpenAmountExpression() {
		String nativeOpenAmount = getCurrencyID() == null
				? getNativeOpenAmountExpression()
				: getNativeReportOpenAmountExpression();
		return getConvertedAmountExpression(nativeOpenAmount);
	}

	protected String getDebitExpression() {
		return getBalanceDocumentAmountExpression("debit", getFullDebitCondition());
	}

	protected String getCreditExpression() {
		return getBalanceDocumentAmountExpression("credit", getFullCreditCondition());
	}

	protected String getBalanceDocumentAmountExpression(String columnName, String fullAmountCondition) {
		if (getCurrencyID() == null) {
			return getNativeAmountExpression(columnName, fullAmountCondition);
		}

		String standardAmount = getConvertedAmountExpression("d." + columnName);
		String balanceFallbackAmount = getConvertedAmountExpression(getNativeAmountExpression(columnName, fullAmountCondition));
		String openAmount = getConvertedAmountExpression(getNativeReconstructedOpenAmountExpression());
		String nonInvoiceAllocated = getConvertedAmountExpression(getNativeNonInvoiceAllocatedAmountExpression());
		String invoiceAllocated = "COALESCE(document_allocated.allocatedamt, 0::numeric)";
		String convertedInvoiceAllocationCondition = getConvertedInvoiceAllocationCondition();
		String reconstructedFullAmountCondition = getReconstructedFullAmountCondition(columnName, fullAmountCondition);
		String fallbackAmount = "CASE WHEN " + getBalanceDocumentCondition() + " THEN " + balanceFallbackAmount
				+ " ELSE " + standardAmount + " END";

		return "CASE WHEN " + getReconstructedDocumentCondition() + " "
				+ "AND COALESCE(d." + columnName + ", 0::numeric) <> 0::numeric "
				+ "THEN CASE WHEN " + convertedInvoiceAllocationCondition + " "
				+ "THEN " + invoiceAllocated + " + " + nonInvoiceAllocated + " "
				+ "+ CASE WHEN " + reconstructedFullAmountCondition + " THEN " + openAmount + " ELSE 0::numeric END "
				+ "ELSE " + fallbackAmount + " END "
				+ "ELSE " + fallbackAmount + " END";
	}

	protected String getConvertedInvoiceAllocationCondition() {
		String completeInvoiceAllocation = "(document_allocated.linecount > 0 "
				+ "AND document_allocated.linecount = document_allocated.nativecount "
				+ "AND document_allocated.linecount = document_allocated.allocatedcount)";
		return "(" + completeInvoiceAllocation + " OR " + getAdvanceWithoutAllocatedLinesCondition() + ") "
				+ "AND COALESCE(document_allocated.nativeamt, 0::numeric) >= 0::numeric "
				+ "AND COALESCE(document_allocated.nativeamt, 0::numeric) <= ABS(d.amount) - " + getNativeReconstructedOpenAmountExpression() + " + 0.01::numeric";
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

	protected String getNativeReportOpenAmountExpression() {
		return "CASE WHEN " + getAdvanceAllocationCondition() + " "
				+ "THEN " + getNativeReconstructedOpenAmountExpression() + " "
				+ "ELSE " + getNativeOpenAmountExpression() + " END";
	}

	protected String getNativeAllocatedAmountExpression() {
		return "CASE WHEN " + getBalanceDocumentCondition() + " "
				+ "THEN ABS(d.amount) - ABS(document_available.openamt) ELSE 0::numeric END";
	}

	protected String getNativeNonInvoiceAllocatedAmountExpression() {
		return "CASE WHEN " + getReconstructedDocumentCondition() + " "
				+ "THEN GREATEST(ABS(d.amount) - " + getNativeReconstructedOpenAmountExpression() + " - COALESCE(document_allocated.nativeamt, 0::numeric), 0::numeric) "
				+ "ELSE 0::numeric END";
	}

	protected String getNativeReconstructedOpenAmountExpression() {
		return "CASE "
				+ "WHEN " + getBalanceDocumentCondition() + " THEN ABS(document_available.openamt) "
				+ "WHEN " + getAdvanceAllocationCondition() + " THEN GREATEST(ABS(d.amount) - COALESCE(document_allocated.nativeamt, 0::numeric), 0::numeric) "
				+ "ELSE ABS(d.openamt) END";
	}

	protected String getAdvanceWithoutAllocatedLinesCondition() {
		return "(" + getAdvanceAllocationCondition() + " AND document_allocated.linecount = 0)";
	}

	protected String getAdvanceAllocationCondition() {
		return "(d.documenttable = 'C_AllocationHdr' AND source_allocationhdr.allocationtype IN ('RCA', 'OPA'))";
	}

	protected String getReconstructedFullAmountCondition(String columnName, String fullAmountCondition) {
		return "(" + fullAmountCondition + " OR " + getAllocationFullAmountCondition(columnName) + ")";
	}

	protected String getAllocationFullAmountCondition(String columnName) {
		if ("debit".equals(columnName)) {
			return "(d.documenttable = 'C_AllocationHdr' AND d.issotrx = 'N')";
		}
		return "(d.documenttable = 'C_AllocationHdr' AND d.issotrx = 'Y')";
	}

	protected String getDocumentAvailableJoin() {
		return " LEFT JOIN LATERAL (SELECT CASE "
				+ "WHEN d.documenttable = 'C_Payment' AND COALESCE(source_payment.c_charge_id, 0) = 0 THEN COALESCE(paymentavailable(d.document_id, " + getDateToInlineQuery() + "), 0::numeric) "
				+ "WHEN d.documenttable = 'C_CashLine' AND COALESCE(source_cashline.c_charge_id, 0) = 0 THEN COALESCE(cashlineavailable(d.document_id, " + getDateToInlineQuery() + "), 0::numeric) "
				+ "ELSE 0::numeric END AS openamt) document_available "
				+ "ON " + getReconstructedDocumentCondition() + " ";
	}

	protected String getDocumentAllocatedJoin() {
		if (getCurrencyID() == null) {
			return "";
		}

		String nativeAmount = getInvoiceAllocatedNetDocumentCurrencyAmountExpression();
		String convertedAmount = getInvoiceAllocatedNetConvertedAmountExpression();
		return " LEFT JOIN LATERAL (SELECT "
				+ "SUM(" + nativeAmount + ") AS nativeamt, "
				+ "SUM(" + convertedAmount + ") AS allocatedamt, "
				+ "COUNT(*) AS linecount, "
				+ "COUNT(" + nativeAmount + ") AS nativecount, "
				+ "COUNT(" + convertedAmount + ") AS allocatedcount "
				+ "FROM c_allocationline allocation_line "
				+ "JOIN c_allocationhdr allocation_hdr ON allocation_hdr.c_allocationhdr_id = allocation_line.c_allocationhdr_id "
				+ "LEFT JOIN c_invoice allocated_invoice ON allocated_invoice.c_invoice_id = allocation_line.c_invoice_id "
				+ "LEFT JOIN c_invoice allocated_credit_invoice ON allocated_credit_invoice.c_invoice_id = allocation_line.c_invoice_credit_id "
				+ "WHERE (" + getDirectAllocatedLineCondition() + " OR " + getAdvanceAppliedLineCondition() + ") "
				+ "AND (allocation_line.c_invoice_id IS NOT NULL OR allocation_line.c_invoice_credit_id IS NOT NULL) "
				+ "AND allocation_line.isactive = 'Y' "
				+ "AND allocation_hdr.isactive = 'Y' "
				+ "AND allocation_hdr.processed = 'Y' "
				+ "AND allocation_hdr.docstatus IN ('CO', 'CL') "
				+ "AND allocation_hdr.dateacct::date <= (" + getDateToInlineQuery() + ")::date) document_allocated "
				+ "ON " + getReconstructedDocumentCondition() + " ";
	}

	protected String getDirectAllocatedLineCondition() {
		return "(((d.documenttable = 'C_Payment' AND allocation_line.c_payment_id = d.document_id) "
				+ "OR (d.documenttable = 'C_CashLine' AND allocation_line.c_cashline_id = d.document_id) "
				+ "OR (d.documenttable = 'C_AllocationHdr' AND allocation_line.c_allocationhdr_id = d.document_id)) "
				+ "AND (d.documenttable <> 'C_AllocationHdr' OR COALESCE(allocation_hdr.allocationtype, '') NOT IN ('RCA', 'OPA')))";
	}

	protected String getAdvanceAppliedLineCondition() {
		return "(d.documenttable = 'C_AllocationHdr' AND EXISTS ("
				+ "SELECT 1 "
				+ "FROM c_allocationline advance_allocation_line "
				+ "JOIN c_allocationhdr advance_allocation_hdr ON advance_allocation_hdr.c_allocationhdr_id = advance_allocation_line.c_allocationhdr_id "
				+ "WHERE advance_allocation_line.c_allocationhdr_id = d.document_id "
				+ "AND advance_allocation_hdr.allocationtype IN ('RCA', 'OPA') "
				+ "AND advance_allocation_hdr.isactive = 'Y' "
				+ "AND advance_allocation_hdr.processed = 'Y' "
				+ "AND advance_allocation_hdr.docstatus IN ('CO', 'CL') "
				+ "AND advance_allocation_line.isactive = 'Y' "
				+ "AND allocation_line.c_allocationline_id <> advance_allocation_line.c_allocationline_id "
				+ "AND ((advance_allocation_line.c_payment_id IS NOT NULL AND allocation_line.c_payment_id = advance_allocation_line.c_payment_id) "
				+ "OR (advance_allocation_line.c_cashline_id IS NOT NULL AND allocation_line.c_cashline_id = advance_allocation_line.c_cashline_id) "
				+ "OR (advance_allocation_line.c_invoice_credit_id IS NOT NULL AND allocation_line.c_invoice_credit_id = advance_allocation_line.c_invoice_credit_id))))";
	}

	protected String getInvoiceAllocatedNetDocumentCurrencyAmountExpression() {
		String invoiceAmount = getInvoiceAllocatedDocumentCurrencyAmountExpression("allocated_invoice", "allocation_line.c_invoice_id");
		String creditInvoiceAmount = getInvoiceAllocatedDocumentCurrencyAmountExpression("allocated_credit_invoice", "allocation_line.c_invoice_credit_id");
		return "CASE WHEN " + getAdvanceCreditAppliedLineCondition() + " THEN " + invoiceAmount
				+ " ELSE " + invoiceAmount + " - " + creditInvoiceAmount + " END";
	}

	protected String getInvoiceAllocatedDocumentCurrencyAmountExpression(String invoiceAlias, String invoiceIDColumn) {
		return "CASE WHEN " + invoiceIDColumn + " IS NULL THEN 0::numeric "
				+ "WHEN " + invoiceAlias + ".c_invoice_id IS NULL THEN NULL "
				+ "WHEN allocation_hdr.c_currency_id = d.c_currency_id "
				+ "THEN ABS(allocation_line.amount) "
				+ "ELSE currencyconvert(ABS(allocation_line.amount), allocation_hdr.c_currency_id, d.c_currency_id, "
				+ getDocumentAllocationConversionDateExpression() + ", "
				+ getDocumentAllocationConversionTypeExpression() + ", "
				+ "allocation_hdr.ad_client_id, allocation_hdr.ad_org_id) END";
	}

	protected String getDocumentAllocationConversionDateExpression() {
		return "CASE WHEN d.documenttable = 'C_Payment' THEN source_payment.dateacct "
				+ "WHEN d.documenttable = 'C_CashLine' THEN source_cash.dateacct ELSE d.dateacct END";
	}

	protected String getDocumentAllocationConversionTypeExpression() {
		return "CASE WHEN d.documenttable = 'C_Payment' THEN COALESCE(source_payment.c_conversiontype_id, 0) ELSE 0 END";
	}

	protected String getInvoiceAllocatedNetConvertedAmountExpression() {
		String invoiceAmount = getInvoiceAllocatedConvertedAmountExpression("allocated_invoice", "allocation_line.c_invoice_id");
		String creditInvoiceAmount = getInvoiceAllocatedConvertedAmountExpression("allocated_credit_invoice", "allocation_line.c_invoice_credit_id");
		return "CASE WHEN " + getAdvanceCreditAppliedLineCondition() + " THEN " + invoiceAmount
				+ " ELSE " + invoiceAmount + " - " + creditInvoiceAmount + " END";
	}

	protected String getAdvanceCreditAppliedLineCondition() {
		return "(d.documenttable = 'C_AllocationHdr' AND EXISTS ("
				+ "SELECT 1 "
				+ "FROM c_allocationline advance_credit_allocation_line "
				+ "JOIN c_allocationhdr advance_credit_allocation_hdr ON advance_credit_allocation_hdr.c_allocationhdr_id = advance_credit_allocation_line.c_allocationhdr_id "
				+ "WHERE advance_credit_allocation_line.c_allocationhdr_id = d.document_id "
				+ "AND advance_credit_allocation_hdr.allocationtype IN ('RCA', 'OPA') "
				+ "AND advance_credit_allocation_hdr.isactive = 'Y' "
				+ "AND advance_credit_allocation_hdr.processed = 'Y' "
				+ "AND advance_credit_allocation_hdr.docstatus IN ('CO', 'CL') "
				+ "AND advance_credit_allocation_line.isactive = 'Y' "
				+ "AND advance_credit_allocation_line.c_invoice_credit_id IS NOT NULL "
				+ "AND allocation_line.c_invoice_credit_id = advance_credit_allocation_line.c_invoice_credit_id "
				+ "AND allocation_line.c_allocationline_id <> advance_credit_allocation_line.c_allocationline_id))";
	}

	protected String getInvoiceAllocatedConvertedAmountExpression(String invoiceAlias, String invoiceIDColumn) {
		String invoiceAmount = getInvoiceAllocatedNativeAmountExpression(invoiceAlias);
		int accountingCurrencyID = Env.getC_Currency_ID(getCtx());
		String invoiceToReportConversion = "currencyconvert(" + invoiceAmount + ", " + invoiceAlias + ".c_currency_id, "
				+ getCurrencyID() + ", " + getInvoiceAllocationReportConversionDateExpression(invoiceAlias) + ", "
				+ "COALESCE(" + invoiceAlias + ".c_conversiontype_id, 0), allocation_hdr.ad_client_id, allocation_hdr.ad_org_id)";

		if (isDocumentConvertRate() && getCurrencyID().intValue() == accountingCurrencyID) {
			invoiceToReportConversion = "CASE WHEN " + invoiceAlias + ".c_currency_id <> " + getCurrencyID() + " "
					+ "AND COALESCE(" + invoiceAlias + ".cintolo_exchange_rate, 0) > 0 "
					+ "THEN currencyround((" + invoiceAmount + ") * " + invoiceAlias + ".cintolo_exchange_rate, "
					+ getCurrencyID() + ", NULL) ELSE " + invoiceToReportConversion + " END";
		}

		return "CASE WHEN " + invoiceIDColumn + " IS NULL THEN 0::numeric "
				+ "WHEN " + invoiceAlias + ".c_invoice_id IS NULL THEN NULL "
				+ "WHEN " + invoiceAlias + ".c_currency_id = " + getCurrencyID() + " "
				+ "THEN " + invoiceAmount + " ELSE " + invoiceToReportConversion + " END";
	}

	protected String getInvoiceAllocationReportConversionDateExpression(String invoiceAlias) {
		return isDocumentConvertRate()
				? invoiceAlias + ".dateinvoiced::timestamp with time zone"
				: getCutoffConversionDateExpression();
	}

	protected String getInvoiceAllocatedNativeAmountExpression(String invoiceAlias) {
		int accountingCurrencyID = Env.getC_Currency_ID(getCtx());
		return "CASE "
				+ "WHEN allocation_hdr.c_currency_id = " + invoiceAlias + ".c_currency_id "
				+ "THEN currencyround(ABS(allocation_line.amount), " + invoiceAlias + ".c_currency_id, NULL) "
				+ "WHEN allocation_hdr.c_currency_id = " + accountingCurrencyID + " "
				+ "AND COALESCE(" + invoiceAlias + ".cintolo_exchange_rate, 0) > 0 "
				+ "THEN currencyround(ABS(allocation_line.amount)/" + invoiceAlias + ".cintolo_exchange_rate, " + invoiceAlias + ".c_currency_id, NULL) "
				+ "ELSE currencyconvert(ABS(allocation_line.amount), allocation_hdr.c_currency_id, " + invoiceAlias + ".c_currency_id, "
				+ invoiceAlias + ".dateinvoiced::timestamp with time zone, COALESCE(" + invoiceAlias + ".c_conversiontype_id, 0), "
				+ "allocation_hdr.ad_client_id, allocation_hdr.ad_org_id) END";
	}

	protected String getBalanceDocumentCondition() {
		return "((d.documenttable = 'C_Payment' AND COALESCE(source_payment.c_charge_id, 0) = 0) "
				+ "OR (d.documenttable = 'C_CashLine' AND COALESCE(source_cashline.c_charge_id, 0) = 0))";
	}

	protected String getReconstructedDocumentCondition() {
		return "(" + getBalanceDocumentCondition() + " OR d.documenttable = 'C_AllocationHdr')";
	}

	/**
	 * Para facturas se prioriza la tasa guardada en el comprobante al convertir a
	 * moneda contable. Para el resto se utiliza la fecha propia del documento.
	 */
	protected String getConvertedAmountExpression(String amountExpression) {
		if (getCurrencyID() == null) {
			return amountExpression;
		}

		if (!isDocumentConvertRate()) {
			return getStandardConvertedAmountExpression(amountExpression, getCutoffConversionDateExpression());
		}

		return getDocumentConvertedAmountExpression(amountExpression);
	}

	protected String getDocumentConvertedAmountExpression(String amountExpression) {
		String standardConversion = getStandardConvertedAmountExpression(amountExpression, getDocumentConversionDateExpression());

		int accountingCurrencyID = Env.getC_Currency_ID(getCtx());
		if (getCurrencyID().intValue() != accountingCurrencyID) {
			return standardConversion;
		}

		return "CASE WHEN d.documenttable = 'C_Invoice' "
				+ "AND d.c_currency_id <> " + getCurrencyID() + " "
				+ "AND COALESCE(source_invoice.cintolo_exchange_rate, 0) > 0 "
				+ "THEN currencyround((" + amountExpression + ") * source_invoice.cintolo_exchange_rate, "
				+ getCurrencyID() + ", NULL) ELSE " + standardConversion + " END";
	}

	protected String getStandardConvertedAmountExpression(String amountExpression, String conversionDateExpression) {
		return "currencyconvert(" + amountExpression + ", d.c_currency_id, "
				+ getCurrencyID() + ", " + conversionDateExpression + ", "
				+ getConversionTypeExpression() + ", "
				+ "d.ad_client_id, d.ad_org_id)";
	}

	protected String getDocumentConversionDateExpression() {
		return "CASE WHEN d.documenttable = 'C_Invoice' THEN source_invoice.dateinvoiced ELSE d.dateacct END";
	}

	protected String getCutoffConversionDateExpression() {
		return getDateToInlineQuery();
	}

	protected String getConversionTypeExpression() {
		return "CASE WHEN d.documenttable = 'C_Invoice' THEN COALESCE(source_invoice.c_conversiontype_id, 0) ELSE NULL END";
	}

	public static boolean isConversionRateDateParameter(String name) {
		return PARAM_IS_DOCUMENT_CONVERT_RATE.equalsIgnoreCase(name)
				|| PARAM_CONVERSION_RATE_DATE.equalsIgnoreCase(name);
	}

	public static boolean getDocumentConvertRate(Object value, boolean defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		if (value instanceof Boolean) {
			return ((Boolean) value).booleanValue();
		}
		String stringValue = value.toString();
		if ("Y".equalsIgnoreCase(stringValue) || "true".equalsIgnoreCase(stringValue)
				|| CONVERSION_RATE_DATE_DOCUMENT.equalsIgnoreCase(stringValue)) {
			return true;
		}
		if ("N".equalsIgnoreCase(stringValue) || "false".equalsIgnoreCase(stringValue)
				|| CONVERSION_RATE_DATE_CUTOFF.equalsIgnoreCase(stringValue)) {
			return false;
		}
		return defaultValue;
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

	public boolean isDocumentConvertRate() {
		return documentConvertRate;
	}

	public void setDocumentConvertRate(boolean documentConvertRate) {
		this.documentConvertRate = documentConvertRate;
	}
}
