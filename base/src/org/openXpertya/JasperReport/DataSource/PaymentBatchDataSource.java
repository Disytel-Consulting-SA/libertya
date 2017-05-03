package org.openXpertya.JasperReport.DataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openXpertya.model.MReference;
import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

/**
 * Data Source que carga los datos del reporte de Informe
 * de Lotes de Pago, a partir de una consulta SQL.
 * @author Kevin Feuerschvenger - SurSoftware
 * @date 31/08/2016
 */
public class PaymentBatchDataSource extends QueryDataSource {

	/** ID de ad_reference de reglas de fechas de pago. */
	private Integer paymentDateRuleRefListID;

	/** ID de ad_reference de estados de un documento. */
	private Integer docStatusRefListID;

	/** ID de lote de pago del cual se quiere obtener el reporte. Opcional */
	private Integer paymentBatchpoID;

	/** Contexto */
	private Properties ctx;

	/**
	 * Constructor de la clase.
	 * @param trxName Transacción de BD a utilizar para efectuar la consulta.
	 */
	public PaymentBatchDataSource(String trxName) {
		super(trxName);
		findIDs();
	}

	/**
	 * Constructor de la clase.
	 * @param ctx Contexto de la aplicación.
	 * @param trxName Transacción de BD a utilizar para efectuar la consulta.
	 */
	public PaymentBatchDataSource(Properties ctx, String trxName) {
		super(trxName);
		setCtx(ctx);
		findIDs();
	}

	/**
	 * Método que recupera los ad_reference_id
	 * utilizados en la query del reporte.
	 */
	private void findIDs() {
		String adReferenceTableName = MReference.Table_Name;

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	a.ad_reference_id AS docstatusreflistid, ");
		sql.append("	b.ad_reference_id AS paymentdaterulereflistid ");
		sql.append("FROM ");
		sql.append("( ");
		sql.append("	SELECT ");
		sql.append("		ad_reference_id ");
		sql.append("	FROM ");
		sql.append("		" + adReferenceTableName + " ");
		sql.append("	WHERE ");
		sql.append("		UPPER(name) = ? ");
		sql.append(") AS a, ");
		sql.append("( ");
		sql.append("	SELECT ");
		sql.append("		ad_reference_id ");
		sql.append("	FROM ");
		sql.append("		" + adReferenceTableName + " ");
		sql.append("	WHERE ");
		sql.append("		UPPER(name) = ? ");
		sql.append(") AS b ");

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			// Preparo la Query
			pstmt = DB.prepareStatement(sql.toString());

			pstmt.setString(1, "_DOCUMENT STATUS");
			pstmt.setString(2, "PAYMENT DATE RULE");

			// Ejecuto la consulta
			rs = pstmt.executeQuery();

			// Recorro los resultados.
			if (rs.next()) {
				docStatusRefListID = rs.getInt("docstatusreflistid");
				paymentDateRuleRefListID = rs.getInt("paymentdaterulereflistid");
			}
			// Libera el ResultSet inmediatamente en lugar de esperar a que lo cierre automaticamente.
			rs.close();
			// Libera el PreparedStatement inmediatamente en lugar de esperar a que lo cierre automaticamente.
			pstmt.close();
			pstmt = null;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
			}
			pstmt = null;
		}
	}

	@Override
	protected String getQuery() {
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ");
		sql.append("	* ");
		sql.append("FROM ( ");
		sql.append("	SELECT ");
		sql.append("		pb.c_paymentbatchpo_id, ");
		sql.append("		o.name AS org_name, ");
		sql.append("		pb.documentno AS document_number, ");
		sql.append("		pb.batchdate::date AS batch_date, ");
		sql.append("		dt.name AS document_type, ");
		sql.append("		trl1.name AS payment_date_rule, ");
		sql.append("		pb.grandtotal AS grand_total, ");
		sql.append("		trl2.name AS document_status, ");
		sql.append("		COALESCE(pb.description, '-') AS description ");
		sql.append("	FROM ");
		sql.append("		c_paymentbatchpo pb ");
		sql.append("		INNER JOIN ad_org o ON o.ad_org_id = pb.ad_org_id ");
		sql.append("		INNER JOIN c_doctype dt ON dt.c_doctype_id = pb.c_doctype_id ");
		sql.append("		INNER JOIN ad_ref_list rl1 ON UPPER(rl1.value) = UPPER(pb.paymentdaterule) ");
		sql.append("		INNER JOIN ad_ref_list_trl trl1 ON rl1.ad_ref_list_id = trl1.ad_ref_list_id ");
		sql.append("		INNER JOIN ad_ref_list rl2 ON UPPER(rl2.value) = UPPER(pb.docstatus) ");
		sql.append("		INNER JOIN ad_ref_list_trl trl2 ON rl2.ad_ref_list_id = trl2.ad_ref_list_id ");
		sql.append("	WHERE ");
		sql.append("		rl1.ad_reference_id = ? ");
		sql.append("		AND rl2.ad_reference_id = ? ");
		sql.append("		AND trl1.ad_language = ? ");
		sql.append("		AND trl2.ad_language = ? ");

		if (!Util.isEmpty(getPaymentBatchpoID(), true)) {
			sql.append("	AND pb.c_paymentbatchpo_id = ? ");
		}

		sql.append(") AS a INNER JOIN ");
		sql.append("( ");
		sql.append("	SELECT ");
		sql.append("		pbd.c_paymentbatchpo_id, ");
		sql.append("		bp.value AS provider_value, ");
		sql.append("		bp.name AS provider_name, ");
		sql.append("		pbd.paymentdate::date AS payment_date, ");
		sql.append("		pbd.paymentamount AS payment_amount, ");
		sql.append("		pbd.firstduedate::date AS first_due_date, ");
		sql.append("		pbd.lastduedate::date AS last_due_date, ");
		sql.append("		EXTRACT(day FROM (pbd.lastduedate - pbd.firstduedate)) AS diff, ");
		sql.append("		coalesce(ba.description, bap.description) AS bank_account_name, ");
		sql.append("		ah.documentno AS allocation_documentno ");
		sql.append("	FROM ");
		sql.append("		c_paymentbatchpodetail pbd ");
		sql.append("		INNER JOIN c_bpartner bp ON bp.c_bpartner_id = pbd.c_bpartner_id ");
		sql.append("		LEFT JOIN c_bankaccount ba ON ba.c_bankaccount_id = pbd.c_bankaccount_id ");
		sql.append("		LEFT JOIN c_bankaccount bap ON bap.c_bankaccount_id = bp.c_bankaccount_id ");
		sql.append("		LEFT JOIN c_allocationhdr ah ON ah.c_allocationhdr_id = pbd.c_allocationhdr_id ");
		sql.append("	ORDER BY ");
		sql.append("		pbd.paymentdate ASC ");
		sql.append(") AS b ");
		sql.append("ON a.c_paymentbatchpo_id = b.c_paymentbatchpo_id");

		return sql.toString();
	}

	@Override
	protected Object[] getParameters() {
		List<Object> params = new ArrayList<Object>();

		params.add(getPaymentDateRuleRefListID());
		params.add(getDocStatusRefListID());
		params.add(ctx.getProperty("#AD_Language"));
		params.add(ctx.getProperty("#AD_Language"));

		if (!Util.isEmpty(getPaymentBatchpoID(), true)) {
			params.add(getPaymentBatchpoID());
		}

		return params.toArray();
	}

	// Getters & Setters

	public Integer getPaymentDateRuleRefListID() {
		return paymentDateRuleRefListID;
	}

	public void setPaymentDateRuleRefListID(Integer paymentDateRuleRefListID) {
		this.paymentDateRuleRefListID = paymentDateRuleRefListID;
	}

	public Integer getDocStatusRefListID() {
		return docStatusRefListID;
	}

	public void setDocStatusRefListID(Integer docStatusRefListID) {
		this.docStatusRefListID = docStatusRefListID;
	}

	public Integer getPaymentBatchpoID() {
		return paymentBatchpoID;
	}

	public void setPaymentBatchpoID(Integer paymentBatchpoID) {
		this.paymentBatchpoID = paymentBatchpoID;
	}

	public Properties getCtx() {
		return ctx;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

}
